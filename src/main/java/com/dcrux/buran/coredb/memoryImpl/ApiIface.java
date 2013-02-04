package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.domains.DomainHash;
import com.dcrux.buran.coredb.iface.domains.DomainHashCreator;
import com.dcrux.buran.coredb.iface.domains.DomainId;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;
import com.dcrux.buran.coredb.iface.nodeClass.*;
import com.dcrux.buran.coredb.iface.query.IQuery;
import com.dcrux.buran.coredb.iface.subscription.Subscription;
import com.dcrux.buran.coredb.iface.subscription.SubscriptionId;
import com.dcrux.buran.coredb.memoryImpl.data.*;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author caelis
 */
public class ApiIface implements IApi {
    private final CommitApi commitApi;
    private final DmApi dataManipulationApi;
    private final DataReadApi dataReadApi;
    private final NodeClassesApi nodeClassesApi;
    private final TypesRegistry typesRegistry;
    private final MiApi metaApi;
    private final QueryApi queryApi;
    private final DomApi domainApi;
    private final SubscriptionApi subscriptionApi;

    public ApiIface() {
        this.typesRegistry = new TypesRegistry();
        Nodes nodes = new Nodes();
        NodeClasses ncs = new NodeClasses();
        Domains doms = new Domains();
        Subscriptions subscriptions = new Subscriptions();

        this.nodeClassesApi = new NodeClassesApi(ncs);
        this.dataManipulationApi = new DmApi(nodes, this.nodeClassesApi, typesRegistry);
        this.dataReadApi = new DataReadApi(nodes, this.nodeClassesApi, typesRegistry);
        this.subscriptionApi =
                new SubscriptionApi(subscriptions, nodes, this.nodeClassesApi, this.dataReadApi);
        this.commitApi =
                new CommitApi(nodes, this.dataReadApi, this.nodeClassesApi, this.subscriptionApi);
        this.domainApi = new DomApi(doms);
        this.metaApi = new MiApi(this.dataReadApi, this.dataManipulationApi, this.domainApi);
        this.queryApi = new QueryApi(nodes, getNodeClassesApi(), this.dataReadApi, typesRegistry);
    }

    public CommitApi getCommitApi() {
        return commitApi;
    }

    public DmApi getDmApi() {
        return dataManipulationApi;
    }

    public DataReadApi getDrApi() {
        return dataReadApi;
    }

    public NodeClassesApi getNodeClassesApi() {
        return nodeClassesApi;
    }

    public MiApi getMetaApi() {
        return metaApi;
    }

    public QueryApi getQueryApi() {
        return queryApi;
    }

    public SubscriptionApi getSubscriptionApi() {
        return subscriptionApi;
    }

    private short keepAliveNumSeconds() {
        return 100;
    }

    @Override
    public CreateInfo createNew(UserId receiver, UserId sender, ClassId classId,
            Optional<KeepAliveHint> keepAliveHint) {
        if (receiver == null) throw new IllegalArgumentException("receiver==null");
        if (sender == null) throw new IllegalArgumentException("sender==null");
        IncNid incNid =
                getDmApi().createNew(receiver.getId(), sender.getId(), classId.getId(), null);
        return new CreateInfo(incNid, new KeepAliveInfo(keepAliveNumSeconds(),
                System.currentTimeMillis() + keepAliveNumSeconds() * 1000));
    }

    @Override
    public CreateInfoUpdate createNewUpdate(UserId receiver, UserId sender,
            Optional<KeepAliveHint> keepAliveHint, NidVer nodeToUpdate,
            Optional<HistoryHint> historyHint)
            throws NodeNotUpdatable, PermissionDeniedException, HistoryHintNotFulfillable {
        final NodeImpl nodeImpl = getDrApi().getNodeFromCurrent(receiver.getId(), nodeToUpdate);
        if (nodeImpl == null) {
            throw new NodeNotUpdatable();
        }
        if (historyHint.isPresent()) {
            if (!historyHint.get().isAllowKeepMore() &&
                    (historyHint.get().getFunction() == HistoryFunction.keepButProperties ||
                            historyHint.get().getFunction() == HistoryFunction.keepNothing)) {
                throw new HistoryHintNotFulfillable("Only supports keeping all");
            }
        }
        IncNid incNid = getDmApi()
                .createNew(receiver.getId(), sender.getId(), nodeImpl.getNodeSerie().getClassId(),
                        nodeToUpdate);
        final CreateInfo crInfo = new CreateInfo(incNid, new KeepAliveInfo(keepAliveNumSeconds(),
                System.currentTimeMillis() + keepAliveNumSeconds() * 1000));
        final CreateInfoUpdate criUpdate =
                new CreateInfoUpdate(crInfo, new HistoryInformation(HistoryFunction.keepAll));
        return criUpdate;
    }

    @Override
    public KeepAliveInfo keepAlive(UserId receiver, UserId sender, KeepAliveHint keepAliveHint,
            IncNid... incNid) throws IncubationNodeNotFound {
        return new KeepAliveInfo(keepAliveNumSeconds(),
                System.currentTimeMillis() + keepAliveNumSeconds() * 1000);
    }

    @Override
    public void cancelIncubationNode(UserId receiver, UserId sender, IncNid... incNid)
            throws IncubationNodeNotFound {
        //TODO: Not implemented
    }

    @Override
    public void setData(UserId receiver, UserId sender, IncNid incNid, short typeIndex,
            IDataSetter dataSetter) throws IncubationNodeNotFound {
        getDmApi().setData(receiver.getId(), sender.getId(), incNid, typeIndex, dataSetter);
    }

    @Override
    public void transferData(UserId receiver, UserId sender, IncNid target, NidVer src,
            TransferExclusion transferExclusion)
            throws IncubationNodeNotFound, InformationUnavailableException,
            PermissionDeniedException, NodeNotFoundException, IncompatibleClassException {
        this.dataManipulationApi
                .transferData(receiver.getId(), sender.getId(), target, src, transferExclusion);
    }

    @Override
    public void setEdge(UserId receiver, UserId sender, IncNid incNid, EdgeIndex index,
            EdgeLabel label, IIncEdgeTarget target)
            throws EdgeIndexAlreadySet, IncubationNodeNotFound {
        getDmApi().setEdge(receiver.getId(), sender.getId(), incNid, index, label, target, false);
    }

    @Override
    public void setEdgeReplace(UserId receiver, UserId sender, IncNid incNid, EdgeIndex index,
            EdgeLabel label, IIncEdgeTarget target) throws IncubationNodeNotFound {
        try {
            getDmApi()
                    .setEdge(receiver.getId(), sender.getId(), incNid, index, label, target, true);
        } catch (EdgeIndexAlreadySet edgeIndexAlreadySet) {
            throw new ExpectableException("This should never happen");
        }
    }

    @Override
    public void removeEdge(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label,
            EdgeIndex index) throws IncubationNodeNotFound {
        try {
            getDmApi().removeEdge(receiver.getId(), sender.getId(), incNid, label, index, false);
        } catch (EdgeIndexNotSet edgeIndexNotSet) {
            throw new ExpectableException("This should never happen");
        }
    }

    @Override
    public void removeEdgeStrict(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label,
            EdgeIndex index) throws EdgeIndexNotSet, IncubationNodeNotFound {
        getDmApi().removeEdge(receiver.getId(), sender.getId(), incNid, label, index, true);
    }

    @Override
    public void removeEdges(UserId receiver, UserId sender, IncNid incNid,
            Optional<EdgeLabel> label) throws IncubationNodeNotFound {
        getDmApi().removeEdges(receiver.getId(), sender.getId(), incNid, label);
    }

    @Override
    public void markNodeAsDeleted(UserId receiver, UserId sender, IncNid incNid)
            throws IncubationNodeNotFound, NotUpdatingException {
        this.getDmApi().markAsDeleted(receiver.getId(), sender.getId(), incNid);
    }

    @Nullable
    @Override
    public Object getData(UserId receiver, UserId sender, NidVer nidVersion, short typeIndex,
            IDataGetter dataGetter)
            throws InformationUnavailableException, PermissionDeniedException,
            NodeNotFoundException {
        return getDrApi()
                .getData(receiver.getId(), sender.getId(), nidVersion, typeIndex, dataGetter);
    }

    @Override
    public Map<EdgeLabel, Map<EdgeIndex, Edge>> getOutEdges(UserId receiver, UserId sender,
            NidVer nid, EnumSet<EdgeType> types, Optional<EdgeLabel> label)
            throws NodeNotFoundException, InformationUnavailableException,
            PermissionDeniedException {
        return getDrApi().getOutEdges(receiver.getId(), sender.getId(), nid, types, false);
    }

    @Override
    public Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> getInEdges(UserId receiver,
            UserId sender, NidVer nid, EnumSet<EdgeType> types, Optional<EdgeLabel> label)
            throws NodeNotFoundException, InformationUnavailableException,
            PermissionDeniedException {
        return getDrApi().getInEdges(receiver.getId(), sender.getId(), nid, types, label, false);
    }

    @Override
    public NodeClassHash declareClass(NodeClass nodeClass) throws PermissionDeniedException {
        return getNodeClassesApi().declareClass(nodeClass);
    }

    @Nullable
    @Override
    public ClassId getClassIdByHash(NodeClassHash hash) {
        final Long classId = getNodeClassesApi().getClassIdByHash(hash);
        if (classId == null) {
            return null;
        }
        return ClassId.c(classId);
    }

    @Override
    public CommitResult commit(UserId receiver, UserId sender, IncNid... incNid)
            throws OptimisticLockingException, PermissionDeniedException, IncubationNodeNotFound {
        return getCommitApi().commit(receiver.getId(), sender.getId(), incNid);
    }

    @Override
    public NodeState getNodeState(UserId receiver, UserId sender, NidVer nid)
            throws NodeNotFoundException, PermissionDeniedException {
        final NodeState state = this.getMetaApi().getState(receiver.getId(), sender.getId(), nid);
        if (state == null) {
            throw new NodeNotFoundException("Node not found");
        }
        return state;
    }

    @Override
    @Nullable
    public NidVer getCurrentNodeVersion(UserId receiver, UserId sender, Nid nid)
            throws NodeNotFoundException {
        final Integer version = this.getDrApi()
                .getCurrentNodeVersion(receiver.getId(), sender.getId(), nid.getNid());
        if (version == null) {
            return null;
        }
        return new NidVer(nid.getNid(), version);
    }

    @Nullable
    @Override
    public NidVer getLatestVersionBeforeDeletion(UserId receiver, UserId sender, Nid nid)
            throws NodeNotFoundException {
        return this.getDrApi()
                .getLatestVersionBeforeDeletion(receiver.getId(), sender.getId(), nid.getNid());
    }

    @Override
    public ClassId getClassId(UserId receiver, UserId sender, NidVer nid)
            throws NodeNotFoundException, PermissionDeniedException, QuotaExceededException {
        return ClassId.c(this.metaApi.getClassId(receiver.getId(), sender.getId(), nid));
    }

    @Override
    public DomainId addAnonymousDomain(UserId receiver, UserId sender)
            throws PermissionDeniedException {
        return this.domainApi.addAnonymousDomain(receiver.getId(), sender.getId());
    }

    @Override
    public DomainId addOrGetIdentifiedDomain(UserId receiver, UserId sender, DomainHash hash)
            throws PermissionDeniedException {
        return this.domainApi.addOrGetIdentifiedDomain(receiver.getId(), sender.getId(), hash);
    }

    @Override
    public void addDomainToNode(UserId receiver, UserId sender, IncNid incNid, DomainId domainId)
            throws IncubationNodeNotFound, DomainNotFoundException {
        this.metaApi.addNodeDomain(receiver.getId(), sender.getId(), incNid, domainId);
    }

    @Override
    public boolean removeDomainFromNode(UserId receiver, UserId sender, IncNid incNid,
            DomainId domainId) throws IncubationNodeNotFound, DomainNotFoundException {
        return this.metaApi.removeNodeDomain(receiver.getId(), sender.getId(), incNid, domainId);
    }

    @Override
    public int clearDomainsFromNode(UserId receiver, UserId sender, IncNid incNid)
            throws IncubationNodeNotFound {
        return this.metaApi.removeAllNodeDomains(receiver.getId(), sender.getId(), incNid);
    }

    @Override
    public Set<DomainId> getDomains(UserId receiver, UserId sender, NidVer nidVer)
            throws InformationUnavailableException, PermissionDeniedException,
            NodeNotFoundException {
        return this.metaApi.getNodeDomains(receiver.getId(), sender.getId(), nidVer);
    }

    @Override
    public DomainHash createDomainHash(UUID uuid, String creatorName, String creatorEmail,
            String description) throws QuotaExceededException {
        DomainHashCreator dhc = new DomainHashCreator(uuid, creatorName, creatorEmail, description);
        return dhc.createHash();
    }

    @Override
    public SubscriptionId addSubscription(Subscription subscription)
            throws PermissionDeniedException {
        return getSubscriptionApi().addSubscription(subscription);
    }

    @Override
    public boolean removeSubscription(UserId receiver, UserId sender, SubscriptionId subscriptionId)
            throws PermissionDeniedException {
        return getSubscriptionApi()
                .removeSubscription(receiver.getId(), sender.getId(), subscriptionId);
    }

    @Override
    public QueryResult query(UserId receiverId, UserId senderId, IQuery query,
            boolean countNumberOfResultsWithoutLimit) throws PermissionDeniedException {
        return this.queryApi.query(receiverId.getId(), senderId.getId(), query,
                countNumberOfResultsWithoutLimit);
    }
}
