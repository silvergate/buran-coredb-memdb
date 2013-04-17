package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.NidVer;
import com.dcrux.buran.coredb.iface.api.TransferExclusion;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.edge.EdgeIndex;
import com.dcrux.buran.coredb.iface.edge.EdgeLabel;
import com.dcrux.buran.coredb.iface.edgeTargets.IEdgeTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.data.IncNode;
import com.dcrux.buran.coredb.memoryImpl.data.IncubationEdge;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeUtil;
import com.dcrux.buran.coredb.memoryImpl.edge.IEdgeImplTarget;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;
import com.google.common.base.Optional;
import org.apache.commons.lang.SerializationUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author caelis
 */
public class DmApi {
    private final Nodes nodes;
    private final NodeClassesApi ncApi;
    private final TypesRegistry typesRegistry;
    private final EdgeUtil edgeUtil = new EdgeUtil();

    public DmApi(Nodes nodes, NodeClassesApi ncApi, TypesRegistry typesRegistry) {
        this.nodes = nodes;
        this.ncApi = ncApi;
        this.typesRegistry = typesRegistry;
    }

    public IncNid createNew(long receiverId, long senderId, long classId,
            @Nullable NidVer toUpdate) {
        return this.nodes.getByUserId(receiverId)
                .createNew(senderId, classId, toUpdate, this.ncApi);
    }

    @Nullable
    IncNode getIncNode(long receiverId, long senderId, IncNid incNid) {
        return this.nodes.getByUserId(receiverId).getIncNode(incNid.getId());
    }

    public void setEdge(long receiverId, long senderId, IncNid incNid, EdgeIndex index,
            EdgeLabel label, IIncEdgeTarget target, boolean allowReplace)
            throws EdgeIndexAlreadySet, IncubationNodeNotFound {
        final IncNode incNode = getIncNode(receiverId, senderId, incNid);
        if (incNode == null) {
            throw new IncubationNodeNotFound("Inc Node not found");
        }
        final IncNode.EdgeIndexLabel eil = new IncNode.EdgeIndexLabel(label, index);
        if (!allowReplace) {
            if (incNode.getIncubationEdges().containsKey(eil)) {
                throw new EdgeIndexAlreadySet("Index already taken");
            }
        }
        incNode.getIncubationEdges().put(eil, new IncubationEdge(target, label));
    }

    public void removeEdge(long receiverId, long senderId, IncNid incNid, EdgeLabel label,
            EdgeIndex index, boolean strict) throws EdgeIndexNotSet, IncubationNodeNotFound {
        final IncNode incNode = getIncNode(receiverId, senderId, incNid);
        if (incNode == null) {
            throw new IncubationNodeNotFound("Inc Node not found");
        }
        final IncNode.EdgeIndexLabel eil = new IncNode.EdgeIndexLabel(label, index);
        if (strict) {
            if (!incNode.getIncubationEdges().containsKey(eil)) {
                throw new EdgeIndexNotSet("Index not found");
            }
        }
        incNode.getIncubationEdges().remove(eil);
    }

    public void removeEdges(long receiverId, long senderId, IncNid incNid,
            Optional<EdgeLabel> label) throws IncubationNodeNotFound {
        final IncNode incNode = getIncNode(receiverId, senderId, incNid);
        if (incNode == null) {
            throw new IncubationNodeNotFound("Inc Node not found");
        }
        final Set<IncNode.EdgeIndexLabel> toRemove = new HashSet<>();
        for (final Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> item : incNode
                .getIncubationEdges().entrySet()) {
            final boolean remove =
                    (!label.isPresent()) || (item.getValue().getLabel().equals(label.get()));
            if (remove) {
                toRemove.add(item.getKey());
            }
        }
        incNode.getIncubationEdges().keySet().removeAll(toRemove);
    }

    public void setData(long receiverId, long senderId, IncNid incNid, short typeIndex,
            IDataSetter dataSetter) throws IncubationNodeNotFound {
        final IncNode incNode =
                this.nodes.getByUserId(receiverId).getIncOidToIncNodes().get(incNid.getId());
        if (incNode == null) {
            throw new IncubationNodeNotFound("NodeImpl in incubation not found");
        }
        if (incNode.getNode().getSenderId() != senderId) {
            throw new ExpectableException(
                    "The sender id is not the same as the one given at creation.");
        }
        final long classId = incNode.getClassId();
        final NodeClass nc = this.ncApi.getClassById(classId);
        if (nc == null) {
            throw new IllegalStateException("NodeClass not found");
        }
        final IType type = nc.getType(typeIndex);
        final boolean supports = type.supports(dataSetter);
        if (!supports) {
            throw new IllegalArgumentException(
                    "The given data setter is no supported on this type.");
        }
        //ITypeImpl ti = this.typesRegistry.get(type.getRef());
        final Object oldValue = incNode.getNode().getData()[typeIndex];
        final Object newValue = type.setData(dataSetter, oldValue);
        incNode.getNode().getData()[typeIndex] = newValue;
    }

    public void markAsDeleted(long receiverId, long senderId, IncNid incNid)
            throws IncubationNodeNotFound, NotUpdatingException {
        final IncNode incNode =
                this.nodes.getByUserId(receiverId).getIncOidToIncNodes().get(incNid.getId());
        if (incNode == null) {
            throw new IncubationNodeNotFound("NodeImpl in incubation not found");
        }
        if (incNode.getNode().getSenderId() != senderId) {
            throw new ExpectableException(
                    "The sender id is not the same as the one given at creation.");
        }
        if (incNode.getToUpdate() == null)
            throw new NotUpdatingException("This node is not updating another node.");
        incNode.setMarkedToDelete(true);
    }

    public void transferData(long receiverId, long senderId, IncNid target, NidVer src,
            TransferExclusion transferExclusion)
            throws IncubationNodeNotFound, InformationUnavailableException,
            PermissionDeniedException, NodeNotFoundException, IncompatibleClassException {
        /* Read incubation node */
        final IncNode incNode =
                this.nodes.getByUserId(receiverId).getIncOidToIncNodes().get(target.getId());
        if (incNode == null) {
            throw new IncubationNodeNotFound("NodeImpl in incubation not found");
        }
        if (incNode.getNode().getSenderId() != senderId) {
            throw new ExpectableException(
                    "The sender id is not the same as the one given at creation.");
        }

        /* Read source node */

        final NodeImpl node =
                this.nodes.getByUserId(receiverId).getNode(src.getNid(), src.getVersion(), false);
        if (node == null) {
            throw new NodeNotFoundException("Node not found");
        }

        /* Transfer data */

        /* Check class if we transfer properties */
        if ((!transferExclusion.isExcludeAllProperties()) ||
                (!transferExclusion.isExcludeAllEdges())) {
            if (incNode.getClassId() != node.getNodeSerie().getClassId())
                throw new IncompatibleClassException("If we try to transfer properties or edge " +
                        "from one to another node they must both be of the same class.");
        }

        /* Transfer properties */
        if (!transferExclusion.isExcludeAllProperties()) {
            final long classId = node.getNodeSerie().getClassId();
            final NodeClass nc = this.ncApi.getClassById(classId);
            if (nc == null) {
                throw new IllegalStateException("NodeClass not found");
            }
            for (short typeIndex = 0; typeIndex < nc.getNumberOfTypes(); typeIndex++) {
                if (transferExclusion.getExcludedProperties().contains(typeIndex)) {
                  /* Do not transfer this property. */
                    continue;
                }
                final Object srcData = node.getData()[typeIndex];
                if (srcData != null) {
                    incNode.getNode().getData()[typeIndex] =
                            SerializationUtils.clone((Serializable) srcData);
                } else {
                    incNode.getNode().getData()[typeIndex] = null;
                }
            }
        }

        /* Transfer domains */
        if (!transferExclusion.isExcludeDomains()) {
            incNode.getNode().getDomainIds().clear();
            incNode.getNode().getDomainIds().addAll(node.getDomainIds());
        }

        /* Transfer edge */
        if (!transferExclusion.isExcludeAllEdges()) {
            final Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> outEdges = node.getOutEdges();
            for (final Map.Entry<EdgeLabel, Map<EdgeIndex, EdgeImpl>> edgeLabelEntry : outEdges
                    .entrySet()) {
                final EdgeLabel edgeLabelEntryLabel = edgeLabelEntry.getKey();
                if (transferExclusion.getExcludedEdges().contains(edgeLabelEntryLabel)) {
                 /* The given label is excluded from transfer. */
                    continue;
                }
                for (Map.Entry<EdgeIndex, EdgeImpl> edgeIndexEntry : edgeLabelEntry.getValue()
                        .entrySet()) {
                    final EdgeImpl edgeImpl = edgeIndexEntry.getValue();
                    final IEdgeImplTarget edgeImplTarget = edgeImpl.getTarget();
                    final IEdgeTarget edgeTarget = this.edgeUtil.toEdgeTarget(edgeImplTarget);
                    final IncubationEdge incEdge =
                            new IncubationEdge((IIncEdgeTarget) edgeTarget, edgeLabelEntryLabel);
                    incNode.getIncubationEdges().put(new IncNode.EdgeIndexLabel(edgeLabelEntryLabel,
                            edgeIndexEntry.getKey()), incEdge);
                }
            }
        }

        /* Transfer permissions */
        //TODO: Do when permissions are done.
    }

}
