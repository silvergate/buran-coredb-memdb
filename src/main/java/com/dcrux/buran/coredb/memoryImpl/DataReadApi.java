package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.api.apiData.HistoryState;
import com.dcrux.buran.coredb.iface.api.exceptions.ExpectableException;
import com.dcrux.buran.coredb.iface.api.exceptions.NodeNotFoundException;
import com.dcrux.buran.coredb.iface.api.exceptions.VersionNotFoundException;
import com.dcrux.buran.coredb.iface.edge.*;
import com.dcrux.buran.coredb.iface.edgeClass.EdgeClass;
import com.dcrux.buran.coredb.iface.node.NidVer;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeUtil;
import com.dcrux.buran.coredb.memoryImpl.edge.VersionedEdgeImplTarget;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caelis
 */
public class DataReadApi {
    private final Nodes nodes;
    private final NodeClassesApi ncApi;
    private final EdgeUtil edgeUtil = new EdgeUtil();

    public DataReadApi(Nodes nodes, NodeClassesApi ncApi) {
        this.nodes = nodes;
        this.ncApi = ncApi;
    }

    @Nullable
    NodeImpl getNodeFromCurrent(long receiverId, NidVer oid) throws NodeNotFoundException {
        return this.nodes.getByUserId(receiverId)
                .getNodeThhrowIfNodeSerieNotFound(oid.getNid(), oid.getVersion(), true);
    }

    @Nullable
    NodeImpl getNodeFromCurrentOrHistorized(long receiverId, NidVer oid)
            throws NodeNotFoundException {
        return this.nodes.getByUserId(receiverId)
                .getNodeThhrowIfNodeSerieNotFound(oid.getNid(), oid.getVersion(), false);
    }

    public Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> getOutEdgesImpl(long receiverId, long senderId,
            NidVer oid, EnumSet<EdgeType> types, boolean queryableOnly)
            throws NodeNotFoundException {
        if (types.isEmpty()) {
            throw new ExpectableException("Types cannot be empty");
        }
        final NodeImpl node = getNodeFromCurrentOrHistorized(receiverId, oid);
        if (node == null) {
            throw new NodeNotFoundException("Node not found");
        }
        final long nodeClassId = node.getNodeSerie().getClassId();
        final NodeClass nodeClass = this.ncApi.getClassById(nodeClassId);
        final Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> privateEdges = new HashMap<>();
        for (Map.Entry<EdgeLabel, Map<EdgeIndex, EdgeImpl>> edgesEntry : node.getOutEdges()
                .entrySet()) {

      /* Is queryable? */
            final boolean isQueryable;
            if (edgesEntry.getKey().isPublic()) {
                isQueryable = edgesEntry.getKey().isPublicQueryable();
            } else {
                EdgeClass pec =
                        nodeClass.getEdgeClasses().get(edgesEntry.getKey().getPrivateEdgeIndex());
                isQueryable = pec.isQueryable();
            }

            if (queryableOnly && (!isQueryable)) {
                continue;
            }
            final boolean isPublic = edgesEntry.getKey().isPublic();
            final boolean add = (isPublic && types.contains(EdgeType.publicMod)) ||
                    (!isPublic && types.contains(EdgeType.privateMod));
            if (add) {
                privateEdges.put(edgesEntry.getKey(), edgesEntry.getValue());
            }
        }
        return privateEdges;
    }

    public Map<EdgeLabel, Map<EdgeIndex, Edge>> getOutEdges(long receiverId, long senderId,
            NidVer oid, EnumSet<EdgeType> types, boolean queryableOnly,
            Optional<EdgeLabel> labelFilter, boolean checkExistenceOfPrivateLabelFilter)
            throws NodeNotFoundException {
          /* Check modifier */
        if (labelFilter.isPresent()) {
            if (labelFilter.get().isPublic() && (!types.contains(EdgeType.publicMod))) {
                throw new IllegalArgumentException(
                        "Public label is given and EdgeType.publicMod " + "is missing");
            }
            if ((!labelFilter.get().isPublic()) && (!types.contains(EdgeType.privateMod))) {
                throw new IllegalArgumentException(
                        "Private label is given and EdgeType.privateMod" + "is missing");
            }
        }

        /* Check label filter */
        if (checkExistenceOfPrivateLabelFilter && (labelFilter.isPresent()) &&
                (!labelFilter.get().isPublic())) {
            final NodeImpl node = getNodeFromCurrentOrHistorized(receiverId, oid);
            if (node == null) {
                throw new NodeNotFoundException("Node not found");
            }
            final long nodeClassId = node.getNodeSerie().getClassId();
            final NodeClass nodeClass = this.ncApi.getClassById(nodeClassId);
            if (!nodeClass.getEdgeClasses().containsKey(labelFilter.get().getPrivateEdgeIndex()))
                throw new IllegalArgumentException(
                        "Given (private) label filter is not declared " + "in class.");
        }

        Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> outEdgesImpl =
                getOutEdgesImpl(receiverId, senderId, oid, types, queryableOnly);
        Map<EdgeLabel, Map<EdgeIndex, Edge>> outEdges = new HashMap<>();

        for (final Map.Entry<EdgeLabel, Map<EdgeIndex, EdgeImpl>> outEdgesImplEntry : outEdgesImpl
                .entrySet()) {
            final Map<EdgeIndex, Edge> singleEntry = new HashMap<>();
            final EdgeLabel edgeLabel = outEdgesImplEntry.getKey();
            if (labelFilter.isPresent() && (!labelFilter.get().equals(edgeLabel))) {
                /* Wrong label */
                continue;
            }
            outEdges.put(outEdgesImplEntry.getKey(), singleEntry);
            for (final Map.Entry<EdgeIndex, EdgeImpl> entry : outEdgesImplEntry.getValue()
                    .entrySet()) {
                final Edge edge = this.edgeUtil.toEdge(entry.getValue());
                singleEntry.put(entry.getKey(), edge);
            }
        }

        return outEdges;
    }

    @Nullable
    public Integer getCurrentNodeVersion(long receiverId, long senderId, long nidWithoutVersion)
            throws NodeNotFoundException {
        final NodeSerie nodeSerie =
                this.nodes.getByUserId(receiverId).getOidToRemovedEmptyAndAliveSeries()
                        .get(nidWithoutVersion);
        if (nodeSerie == null) {
            throw new NodeNotFoundException("Node not found");
        }
        if (nodeSerie.hasBeenDeleted() || nodeSerie.hasNoVersion()) {
            return null;
        }
        return nodeSerie.getCurrentVersion();
    }

    @Nullable
    public NidVer getLatestVersionBeforeDeletion(long receiverId, long senderId,
            long nidWithoutVersion) throws NodeNotFoundException {
        final NodeSerie nodeSerie =
                this.nodes.getByUserId(receiverId).getOidToRemovedEmptyAndAliveSeries()
                        .get(nidWithoutVersion);
        if (nodeSerie == null) {
            throw new NodeNotFoundException("Node not found");
        }
        if (nodeSerie.hasNoVersion()) {
            throw new ExpectableException("This shoud never happen");
        }
        if (!nodeSerie.hasBeenDeleted()) {
            return null;
        }
        return new NidVer(nidWithoutVersion, nodeSerie.getLatestVersionBeforeDeletion());
    }

    private void addToEdges(final Map<EdgeLabel, Multimap<EdgeIndex, NidVer>> combination,
            Optional<EdgeLabel> label, Optional<EdgeIndexRange> indexRange, boolean queryableOnly,
            NodeClass nodeClass, Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> edgeImpls,
            EnumSet<HistoryState> sourceHistoryStates, EnumSet<EdgeType> types,
            long requiredSourceUserId) {
        for (Map.Entry<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> verInEdgesByLabel : edgeImpls
                .entrySet()) {
            if ((label.isPresent()) && (!label.get().equals(verInEdgesByLabel.getKey()))) {
                continue;
            }

            /* Right type? */
            boolean typeCorrect = ((types.contains(EdgeType.privateMod) &&
                    verInEdgesByLabel.getKey().isPrivate()) ||
                    (types.contains(EdgeType.publicMod) && verInEdgesByLabel.getKey().isPublic()));
            if (!typeCorrect) {
                continue;
            }

      /* Is queryable? */
            final boolean isQueryable;
            if (verInEdgesByLabel.getKey().isPublic()) {
                isQueryable = verInEdgesByLabel.getKey().isPublicQueryable();
            } else {
                /*
                PrivateEdgeClass pec = nodeClass.getEdgeClasses().get(verInEdgesByLabel.getKey());
                if (pec==null)
                    throw new ExpectableException(
                            MessageFormat
                                    .format("Label {0} not defined for class.", verInEdgesByLabel));
                isQueryable = pec.isQueryable();      */
                //TODO: Da muss die klasse von der source verwendet werden und nicht nodeClass
                // (denn das ist das target).
                isQueryable = true;
            }

            if ((!isQueryable) && (queryableOnly)) {
                continue;
            }
            for (Map.Entry<EdgeIndex, EdgeImpl> verInEdgeEntry : verInEdgesByLabel.getValue()
                    .entries()) {
                /* Is in range?*/
                if (indexRange.isPresent()) {
                    if (!indexRange.get().contains(verInEdgeEntry.getKey())) {
                        /* Skip */
                        continue;
                    }
                }

                Multimap<EdgeIndex, NidVer> edgeImplsByLabel =
                        combination.get(verInEdgesByLabel.getKey());
                if (edgeImplsByLabel == null) {
                    edgeImplsByLabel = HashMultimap.create();
                    combination.put(verInEdgesByLabel.getKey(), edgeImplsByLabel);
                }
                /* Die Source-Node ist immer vom typ VersionedEdgeImplTarget */
                final VersionedEdgeImplTarget sourceNode =
                        (VersionedEdgeImplTarget) verInEdgeEntry.getValue().getSource();

                Integer currentVersion =
                        sourceNode.getTarget().getNodeSerie().tryGetCurrentVersion();
                boolean correctHistoryState = (currentVersion != null &&
                        currentVersion == sourceNode.getTarget().getVersion() &&
                        sourceHistoryStates.contains(HistoryState.active)) ||
                        (sourceHistoryStates.contains(HistoryState.historized));

                if ((sourceNode.getTarget().getNodeSerie().getReceiverId() ==
                        requiredSourceUserId) && (correctHistoryState)) {
                    /* Source-ID is correct */
                    /* History state is correct */
                    edgeImplsByLabel
                            .put(verInEdgeEntry.getKey(), sourceNode.getTarget().createNidVer());
                }
            }
        }
    }

    public Map<EdgeLabel, Multimap<EdgeIndex, NidVer>> getInEdges(long receiverId, long senderId,
            NidVer oid, EnumSet<HistoryState> sourceHistoryStates, Optional<ClassId> sourceClassId,
            EnumSet<EdgeType> types, Optional<EdgeIndexRange> indexRange, Optional<EdgeLabel> label,
            boolean queryablesOnly) throws NodeNotFoundException {
        if (types.isEmpty()) {
            throw new ExpectableException("types.isEmpty()");
        }
        final NodeImpl node = getNodeFromCurrentOrHistorized(receiverId, oid);
        if (node == null) {
            throw new NodeNotFoundException("Node not found");
        }

        final NodeClass nodeClass = this.ncApi.getClassById(node.getNodeSerie().getClassId());

        final Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> verInEdges =
                node.getVersionedInEdgeds();
        final Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> unverInEdges =
                node.getNodeSerie().getInEdges();

        final Map<EdgeLabel, Multimap<EdgeIndex, NidVer>> combination = new HashMap<>();

          /* Add versioned edge */
        addToEdges(combination, label, indexRange, queryablesOnly, nodeClass, verInEdges,
                sourceHistoryStates, types, receiverId);

          /* Add unversioned edge */
        addToEdges(combination, label, indexRange, queryablesOnly, nodeClass, unverInEdges,
                sourceHistoryStates, types, receiverId);

        return combination;
    }

    /**
     * Liefet <code>true</code>, falls die OID irgendwo vorhanden ist (current oder historisiert).
     *
     * @param receiverId
     * @param oid
     * @return
     */
    public boolean oidExistsInCurrentOrHistory(long receiverId, long oid) {
        final NodeSerie ns = this.nodes.getByUserId(receiverId).getNodeSerieByOid(oid, false);
        return ns != null;
    }

    public boolean existsInCurrent(long receiverId, NidVer nidVer) throws NodeNotFoundException {
        // TODO: Das muss nicht ins public api, da gibts schon das getState
        return getNodeFromCurrent(receiverId, nidVer) != null;
    }

    @Nullable
    public Object getData(long receiverId, long senderId, NidVer nidVer, short typeIndex,
            IDataGetter dataGetter) throws NodeNotFoundException, VersionNotFoundException {
        final NodeImpl node = this.nodes.getByUserId(receiverId)
                .getNode(nidVer.getNid(), nidVer.getVersion(), false);
        if (node == null) {
            throw new VersionNotFoundException(MessageFormat.format("Node found but given version" +
                    " {0} does not exists" +
                    ". Node deleted?", nidVer.getVersion()));
        }
        final long classId = node.getNodeSerie().getClassId();

        final NodeClass nc = this.ncApi.getClassById(classId);
        if (nc == null) {
            throw new IllegalStateException("NodeClass not found");
        }
        final IType type = nc.getType(typeIndex);
        final boolean supports = type.supports(dataGetter);
        if (!supports) {
            throw new ExpectableException("The given data getter is no supported on this type.");
        }
        //ITypeImpl ti = this.typesRegistry.get(type.getRef());
        final Object value = node.getData()[typeIndex];
        return type.getData(dataGetter, value);
    }

}
