package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.NidVer;
import com.dcrux.buran.coredb.iface.api.exceptions.EdgeIndexAlreadySet;
import com.dcrux.buran.coredb.iface.api.exceptions.EdgeIndexNotSet;
import com.dcrux.buran.coredb.iface.api.exceptions.ExpectableException;
import com.dcrux.buran.coredb.iface.api.exceptions.IncubationNodeNotFound;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.data.IncNode;
import com.dcrux.buran.coredb.memoryImpl.data.IncubationEdge;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;
import com.google.common.base.Optional;

import javax.annotation.Nullable;
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

  public DmApi(Nodes nodes, NodeClassesApi ncApi, TypesRegistry typesRegistry) {
    this.nodes = nodes;
    this.ncApi = ncApi;
    this.typesRegistry = typesRegistry;
  }

  public IncNid createNew(long receiverId, long senderId, long classId, @Nullable NidVer toUpdate) {
    return this.nodes.getByUserId(receiverId).createNew(senderId, classId, toUpdate, this.ncApi);
  }

  @Nullable
  IncNode getIncNode(long receiverId, long senderId, IncNid incNid) {
    return this.nodes.getByUserId(receiverId).getIncNode(incNid.getId());
  }

  public void setEdge(long receiverId, long senderId, IncNid incNid, EdgeIndex index, EdgeLabel label,
                      IIncEdgeTarget target, boolean allowReplace) throws EdgeIndexAlreadySet, IncubationNodeNotFound {
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

  public void removeEdge(long receiverId, long senderId, IncNid incNid, EdgeLabel label, EdgeIndex index,
                         boolean strict) throws EdgeIndexNotSet, IncubationNodeNotFound {
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

  public void removeEdges(long receiverId, long senderId, IncNid incNid, Optional<EdgeLabel> label) throws
          IncubationNodeNotFound {
    final IncNode incNode = getIncNode(receiverId, senderId, incNid);
    if (incNode == null) {
      throw new IncubationNodeNotFound("Inc Node not found");
    }
    final Set<EdgeIndex> toRemove = new HashSet<>();
    for (final Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> item : incNode.getIncubationEdges().entrySet()) {
      final boolean remove =
              (!label.isPresent()) || (item.getValue().getLabel().getLabel().equals(label.get().getLabel()));
      if (remove) {
        toRemove.add(item.getKey().getIndex());
      }
    }
    incNode.getIncubationEdges().keySet().removeAll(toRemove);
  }

  public void setData(long receiverId, long senderId, IncNid incNid, short typeIndex, IDataSetter dataSetter) throws
          IncubationNodeNotFound {
    final IncNode incNode = this.nodes.getByUserId(receiverId).getIncOidToIncNodes().get(incNid.getId());
    if (incNode == null) {
      throw new IncubationNodeNotFound("NodeImpl in incubation not found");
    }
    if (incNode.getNode().getSenderId() != senderId) {
      throw new ExpectableException("The sender id is not the same as the one given at creation.");
    }
    final long classId = incNode.getClassId();
    final NodeClass nc = this.ncApi.getClassById(classId);
    if (nc == null) {
      throw new IllegalStateException("NodeClass not found");
    }
    final IType type = nc.getType(typeIndex);
    final boolean supports = type.supports(dataSetter);
    if (!supports) {
      throw new IllegalArgumentException("The given data setter is no supported on this type.");
    }
    //ITypeImpl ti = this.typesRegistry.get(type.getRef());
    final Object oldValue = incNode.getNode().getData()[typeIndex];
    final Object newValue = type.setData(dataSetter, oldValue);
    incNode.getNode().getData()[typeIndex] = newValue;
  }

}
