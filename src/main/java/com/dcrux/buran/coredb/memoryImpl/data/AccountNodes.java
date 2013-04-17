package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.NidVer;
import com.dcrux.buran.coredb.iface.api.CommitResult;
import com.dcrux.buran.coredb.iface.api.exceptions.ExpectableException;
import com.dcrux.buran.coredb.iface.api.exceptions.OptimisticLockingException;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.subscription.SubscriptionEventType;
import com.dcrux.buran.coredb.memoryImpl.DataReadApi;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;
import com.dcrux.buran.coredb.memoryImpl.PreparedComitInfo;
import com.dcrux.buran.coredb.memoryImpl.SubscriptionApi;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author caelis
 */
public class AccountNodes implements Serializable {

    private final long receiverId;

    private Map<Long, NodeSerie> oidToAliveSeries = new HashMap<>();
    private Multimap<Long, NodeSerie> classIdToAliveSeries = HashMultimap.create();
    private Map<Long, NodeSerie> oidToRemovedEmptyAndAliveSeries = new HashMap<>();

    private Map<Long, IncNode> incOidToIncNodes = new HashMap<>();

    private transient CommitUtil commitUtil;

    public AccountNodes(long receiverId) {
        this.receiverId = receiverId;
    }

    private CommitUtil getCommitUtil() {
        if (this.commitUtil == null) {
            this.commitUtil = new CommitUtil();
        }
        return this.commitUtil;
    }

    public Map<Long, NodeSerie> getOidToAliveSeries() {
        return oidToAliveSeries;
    }

    public Map<Long, NodeSerie> getOidToRemovedEmptyAndAliveSeries() {
        return oidToRemovedEmptyAndAliveSeries;
    }

    public Multimap<Long, NodeSerie> getClassIdToAliveSeries() {
        return classIdToAliveSeries;
    }

    public IncNid createNew(long senderId, long classId, @Nullable NidVer toUpdate,
            NodeClassesApi ncApi) {
        final long incOid = getIncOidCounter().incrementAndGet();
        final NodeClass nc = ncApi.getClassById(classId);
        if (nc == null) {
            throw new IllegalStateException("NodeClass not found");
        }

        final int version;
        if (toUpdate == null) {
            version = NodeSerie.FIRST_VERSION;
        } else {
            version = toUpdate.getVersion() + 1;
        }

        final NodeImpl newNode = new NodeImpl(version, senderId, receiverId, 0L, 0L,
                new Object[nc.getNumberOfTypes()]);
        final IncNode incNode = new IncNode(toUpdate, newNode, receiverId, classId);

    /* Add data */
        getIncOidToIncNodes().put(incOid, incNode);
        final IncNid incNidNew = new IncNid(incOid);
        return incNidNew;
    }

    @Nullable
    public IncNode getIncNode(long incOid) {
        return getIncOidToIncNodes().get(incOid);
    }

    private void markNodeAsDeleted(final NodeImpl node, long currentTime) {
        final NodeSerie ns = node.getNodeSerie();
        if (ns.hasBeenDeleted()) {
            throw new IllegalStateException("Node serie is already marked to delete");
        }
        if (ns.hasNoVersion()) {
            throw new IllegalStateException("Cannot delete a node without version");
        }
        ns.markAsDeleted(currentTime);
        this.oidToAliveSeries.remove(ns.getOid());
        this.classIdToAliveSeries.remove(ns.getClassId(), ns);
    }

    private void addNode(final NodeImpl node) {
        final NodeSerie ns = node.getNodeSerie();
        assert (ns != null);
        if (ns.hasBeenDeleted()) {
            throw new ExpectableException(
                    "NodeImpl serie is marked as deleted. Cannot add a new node.");
        }

        boolean nsWasEmptyBefore = ns.hasNoVersion();

        if (nsWasEmptyBefore) {
            addNewNodeSerie(ns);
        }

        ns.addNewVersion(node);

        if (nsWasEmptyBefore) {
            this.oidToAliveSeries.put(ns.getOid(), ns);
            this.classIdToAliveSeries.put(ns.getClassId(), ns);
        }
    }

    private void addNewNodeSerie(NodeSerie nodeSerie) {
        if (!nodeSerie.hasNoVersion()) {
            throw new ExpectableException("NodeImpl serie already contains nodes.");
        }
        this.oidToRemovedEmptyAndAliveSeries.put(nodeSerie.getOid(), nodeSerie);
    }

    @Nullable
    public NodeSerie getNodeSerieByOid(long oid, boolean aliveOnly) {
        if (!aliveOnly) {
            return this.oidToRemovedEmptyAndAliveSeries.get(oid);
        } else {
            return this.oidToAliveSeries.get(oid);
        }
    }

    @Nullable
    public NodeImpl getNode(long oid, int version, boolean currentOnly) {
        final NodeSerie ns = getNodeSerieByOid(oid, false);
        if (ns == null) {
            return null;
        }
        if (currentOnly) {
            /* Check whether it's the current version */
            if (ns.hasBeenDeleted()) return null;
            if (ns.hasNoVersion()) return null;
            final Integer curVer = ns.getCurrentVersion();
            if (curVer != version) return null;
        }

        if (!ns.hasVersion(version)) return null;
        return ns.getNode(version);
    }

    @Nullable
    public NodeImpl getCurrentNode(long oid) {
        NodeSerie ns = getNodeSerieByOid(oid, true);
        if (ns == null) {
            return null;
        }
        if (ns.hasBeenDeleted()) {
            return null;
        }
        if (ns.hasNoVersion()) {
            return null;
        }
        return ns.getNode(ns.getCurrentVersion());
    }

    public Map<Long, IncNode> getIncOidToIncNodes() {
        return incOidToIncNodes;
    }

    private final AtomicLong incOidCounter = new AtomicLong(0L);
    private final AtomicLong oidCounter = new AtomicLong(0L);

    public AtomicLong getIncOidCounter() {
        return incOidCounter;
    }

    public AtomicLong getOidCounter() {
        return oidCounter;
    }

    public static class SubscriptionTask {
        public SubscriptionTask(NodeImpl nodeImpl, SubscriptionEventType subscriptionEventType) {
            this.nodeImpl = nodeImpl;
            this.subscriptionEventType = subscriptionEventType;
        }

        private final NodeImpl nodeImpl;
        private SubscriptionEventType subscriptionEventType;

        public NodeImpl getNodeImpl() {
            return nodeImpl;
        }

        public SubscriptionEventType getSubscriptionEventType() {
            return subscriptionEventType;
        }
    }

    public CommitResult commit(long senderId, Set<IncNid> incNids, DataReadApi drApi,
            NodeClassesApi ncApi, SubscriptionApi subscriptionApi)
            throws OptimisticLockingException {
        // TODO: Missing write lock
      /* OIDs von allen extrahieren */
        final Set<PreparedComitInfo> prepComInfo =
                getCommitUtil().generateOidsFromIoids(senderId, incNids, this);
      /* Validate */
        getCommitUtil().validate(prepComInfo, drApi, ncApi);

        /* Subscription tasks */
        final Set<SubscriptionTask> subscriptionTasks = new HashSet<>();

        /* Commit node */
        for (final PreparedComitInfo pciEntry : prepComInfo) {
            commitNodeAndEdges(prepComInfo, senderId, pciEntry, subscriptionTasks);
        }

        final Map<IncNid, NidVer> incOidToOidVer = new HashMap<>();
        for (final PreparedComitInfo pciEntry : prepComInfo) {
            incOidToOidVer.put(pciEntry.getIoid(), pciEntry.getOidToGet());
        }

        /* Invoke subscription tasks */
        for (SubscriptionTask subscriptionTask : subscriptionTasks) {
            subscriptionApi.invoke(subscriptionTask.getNodeImpl(),
                    subscriptionTask.getSubscriptionEventType());
        }

        return new CommitResult(incOidToOidVer);
    }

    private void commitNodeAndEdges(Set<PreparedComitInfo> pci, final long senderId,
            PreparedComitInfo pciEntry, final Set<SubscriptionTask> subscriptionTasks) {
        final IncNid incNid = pciEntry.getIoid();
        final long classId = pciEntry.getClassId();

        final IncNode incNode = pciEntry.getIncNode();

        /* Remove Incubation Data */
        getIncOidToIncNodes().remove(incNid.getId());

        /* Set data */
        final AccountNodes accountNodes = this;
        final long oid = pciEntry.getOidToGet().getNid();
        final int version = pciEntry.getOidToGet().getVersion();
        final NidVer nidVer = new NidVer(oid, version);

        /* Set valid from time */
        long currentTime = System.currentTimeMillis();
        incNode.getNode().setValidFrom(currentTime);

        /* Add edge from inc to node */
        for (Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> incEdgeEntry : incNode
                .getIncubationEdges().entrySet()) {
            getCommitUtil().addEdge(pci, incNode.getNode(), incEdgeEntry.getValue(),
                    incEdgeEntry.getKey().getIndex(), this);
        }

        /* Add node */
        if (!incNode.isMarkedToDelete()) {
            addNode(incNode.getNode());
        } else {
            markNodeAsDeleted(incNode.getNode(), currentTime);
        }

        /* Add subscription tasks */

        /* New node */
        if (!incNode.isMarkedToDelete()) {
            if (incNode.getNode().getVersion() == NodeSerie.FIRST_VERSION) {
                /* Adding a new node, not updating */
                subscriptionTasks.add(new SubscriptionTask(incNode.getNode(),
                        SubscriptionEventType.newNode));
            } else {
                /* Adding a new node, updating */
                subscriptionTasks.add(new SubscriptionTask(incNode.getNode(),
                        SubscriptionEventType.nodeUpdatingOther));
            }
        }
        /* Old node */
        if (incNode.getNode().getVersion() != NodeSerie.FIRST_VERSION) {
            final NodeImpl oldNode =
                    pciEntry.getNodeSerie().getNode(incNode.getNode().getVersion() - 1);
            if (incNode.isMarkedToDelete()) {
                /* Old node is removed without update */
                subscriptionTasks
                        .add(new SubscriptionTask(oldNode, SubscriptionEventType.nodeHistorized));
            } else {
                                /* Old node is replaced by other node */
                subscriptionTasks.add(new SubscriptionTask(oldNode,
                        SubscriptionEventType.nodeHistorizedReplaced));
            }
        }
    }
}
