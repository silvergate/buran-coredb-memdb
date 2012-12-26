package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.IncOid;
import com.dcrux.buran.coredb.iface.OidVersion;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.data.*;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.ITypeImpl;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class DmApi {
  private final Edges edges;
  private final Nodes nodes;
  private final NodeClassesApi ncApi;
  private final TypesRegistry typesRegistry;

  public DmApi(Edges edges, Nodes nodes, NodeClassesApi ncApi, TypesRegistry typesRegistry) {
    this.edges = edges;
    this.nodes = nodes;
    this.ncApi = ncApi;
    this.typesRegistry = typesRegistry;
  }

  public IncOid createNew(long receiverId, long senderId, long classId, @Nullable OidVersion toUpdate) {
    final long incOid = this.nodes.getByUserId(senderId).getIncOidCounter().incrementAndGet();
    final NodeClass nc = this.ncApi.getClassById(classId);
    if (nc == null) {
      throw new IllegalStateException("NodeClass not found");
    }
    final Node newNode = new Node(senderId, 0L, 0L, new Object[nc.getNumberOfTypes()]);
    final IncNode incNode = new IncNode(toUpdate, newNode, receiverId);

    /* Add data */
    this.nodes.getByUserId(senderId).getIncOidToClassId().put(incOid, classId);
    NodesSingleClass nsc = this.nodes.getByUserId(senderId).getByClassId(classId);
    final IncOid incOidNew = new IncOid(incOid);
    nsc.getIncOidToIncubationNode().put(incOidNew.getId(), incNode);
    return incOidNew;
  }

  @Nullable
  IncNode getIncNode(long receiverId, long senderId, IncOid incOid) {
    final long classId = this.nodes.getByUserId(senderId).getIncOidToClassId().get(incOid.getId());
    final IncNode incNode =
            this.nodes.getByUserId(senderId).getByClassId(classId).getIncOidToIncubationNode().get(incOid.getId());
    return incNode;
  }

  public void setEdge(long receiverId, long senderId, IncOid incOid, EdgeIndex index, EdgeLabel label,
                      IIncEdgeTarget target, boolean allowReplace) {
    final IncNode incNode = getIncNode(receiverId, senderId, incOid);
    if (!allowReplace) {
      if (incNode.getIncubationEdges().containsKey(index)) {
        throw new IllegalArgumentException("Index already taken");
      }
    }
    incNode.getIncubationEdges().put(new IncNode.EdgeIndexLabel(label, index), new IncubationEdge(target, label));
  }

  public void removeEdge(long receiverId, long senderId, IncOid incOid, EdgeIndex index, boolean strict) {
    final IncNode incNode = getIncNode(receiverId, senderId, incOid);
    if (strict) {
      if (!incNode.getIncubationEdges().containsKey(index)) {
        throw new IllegalArgumentException("Index not found");
      }
    }
    incNode.getIncubationEdges().remove(index);
  }

  public void removeEdges(long receiverId, long senderId, IncOid incOid, EdgeLabel label) {
    final IncNode incNode = getIncNode(receiverId, senderId, incOid);
    final Set<EdgeIndex> toRemove = new HashSet<>();
    for (final Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> item : incNode.getIncubationEdges().entrySet()) {
      if (item.getValue().getLabel().getLabel().equals(label.getLabel())) {
        toRemove.add(item.getKey().getIndex());
      }
    }
    incNode.getIncubationEdges().keySet().removeAll(toRemove);
  }

  public void setData(long receiverId, long senderId, IncOid incOid, short typeIndex, IDataSetter dataSetter) {
    final long classId = this.nodes.getByUserId(senderId).getIncOidToClassId().get(incOid.getId());
    final IncNode incNode =
            this.nodes.getByUserId(senderId).getByClassId(classId).getIncOidToIncubationNode().get(incOid.getId());
    final NodeClass nc = this.ncApi.getClassById(classId);
    if (nc == null) {
      throw new IllegalStateException("NodeClass not found");
    }
    final IType type = nc.getType(typeIndex);
    final boolean supports = type.supports(dataSetter);
    if (!supports) {
      throw new IllegalArgumentException("The given data setter is no supported on this type.");
    }
    ITypeImpl ti = this.typesRegistry.get(type.getRef());
    final Object oldValue = incNode.getNode().getData()[typeIndex];
    final Object newValue = ti.setData(dataSetter, oldValue);
    incNode.getNode().getData()[typeIndex] = newValue;
  }

}
