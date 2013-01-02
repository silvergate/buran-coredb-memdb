package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.api.ExpectableException;
import com.dcrux.buran.coredb.iface.api.NodeNotFoundException;
import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeUtil;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.ITypeImpl;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caelis
 */
public class DataReadApi {
  private final Nodes nodes;
  private final NodeClassesApi ncApi;
  private final TypesRegistry typesRegistry;
  private final EdgeUtil edgeUtil = new EdgeUtil();

  public DataReadApi(Nodes nodes, NodeClassesApi ncApi, TypesRegistry typesRegistry) {
    this.nodes = nodes;
    this.ncApi = ncApi;
    this.typesRegistry = typesRegistry;
  }

  @Nullable
  NodeImpl getNodeFromCurrent(long receiverId, OidVersion oid) {
    return this.nodes.getByUserId(receiverId).getNode(oid.getOid(), oid.getVersion(), true);
  }

  @Nullable
  NodeImpl getNodeFromCurrentOrHistorized(long receiverId, OidVersion oid) {
    return this.nodes.getByUserId(receiverId).getNode(oid.getOid(), oid.getVersion(), false);
  }

  public Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> getOutEdgesImpl(long receiverId, long senderId, OidVersion oid,
                                                                  EnumSet<EdgeType> types, boolean queryableOnly) throws
          NodeNotFoundException {
    if (types.isEmpty()) {
      throw new ExpectableException("Types cannot be empty");
    }
    final NodeImpl node = getNodeFromCurrentOrHistorized(receiverId, oid);
    if (node == null) {
      throw new NodeNotFoundException("Node not found");
    }
    final Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> privateEdges = new HashMap<>();
    for (Map.Entry<EdgeLabel, Map<EdgeIndex, EdgeImpl>> edgesEntry : node.getOutEdges().entrySet()) {
      boolean isQueryable = true; //TODO: Implement me
      if (queryableOnly && (!isQueryable)) {
        continue;
      }
      final boolean isPublic = edgesEntry.getKey().isPublic();
      final boolean add =
              (isPublic && types.contains(EdgeType.publicMod)) || (!isPublic && types.contains(EdgeType.privateMod));
      if (add) {
        privateEdges.put(edgesEntry.getKey(), edgesEntry.getValue());
      }
    }
    return privateEdges;
  }

  public Map<EdgeLabel, Map<EdgeIndex, Edge>> getOutEdges(long receiverId, long senderId, OidVersion oid,
                                                          EnumSet<EdgeType> types, boolean queryableOnly) throws
          NodeNotFoundException {
    Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> outEdgesImpl =
            getOutEdgesImpl(receiverId, senderId, oid, types, queryableOnly);
    Map<EdgeLabel, Map<EdgeIndex, Edge>> outEdges = new HashMap<>();

    for (final Map.Entry<EdgeLabel, Map<EdgeIndex, EdgeImpl>> outEdgesImplEntry : outEdgesImpl.entrySet()) {
      final Map<EdgeIndex, Edge> singleEntry = new HashMap<>();
      outEdges.put(outEdgesImplEntry.getKey(), singleEntry);
      for (final Map.Entry<EdgeIndex, EdgeImpl> entry : outEdgesImplEntry.getValue().entrySet()) {
        final Edge edge = this.edgeUtil.toEdgeWithSource(entry.getValue()).getEdge();
        singleEntry.put(entry.getKey(), edge);
      }
    }

    return outEdges;
  }

  private void addToEdges(final Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> combination,
                          Optional<EdgeLabel> label, boolean queryableOnly, NodeClass nodeClass,
                          Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> edgeImpls) {
    for (Map.Entry<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> verInEdgesByLabel : edgeImpls.entrySet()) {
      if ((label.isPresent()) && (!label.get().equals(verInEdgesByLabel.getKey()))) {
        continue;
      }
      boolean isQueryable = true; //TODO: Implement me
      if ((!isQueryable) && (queryableOnly)) {
        continue;
      }
      for (Map.Entry<EdgeIndex, EdgeImpl> verInEdgeEntry : verInEdgesByLabel.getValue().entries()) {
        Multimap<EdgeIndex, EdgeWithSource> edgeImplsByLabel = combination.get(verInEdgesByLabel.getKey());
        if (edgeImplsByLabel == null) {
          edgeImplsByLabel = HashMultimap.create();
          combination.put(verInEdgesByLabel.getKey(), edgeImplsByLabel);
        }
        final EdgeWithSource edgeWithSource = this.edgeUtil.toEdgeWithSource(verInEdgeEntry.getValue());
        edgeImplsByLabel.put(verInEdgeEntry.getKey(), edgeWithSource);
      }
    }
  }

  public Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> getInEdges(long receiverId, long senderId, OidVersion oid,
                                                                        EnumSet<EdgeType> types,
                                                                        Optional<EdgeLabel> label,
                                                                        boolean queryablesOnly) throws
          NodeNotFoundException {
    if (types.isEmpty()) {
      throw new ExpectableException("Types cannot be empty");
    }
    final NodeImpl node = getNodeFromCurrentOrHistorized(receiverId, oid);
    if (node == null) {
      throw new NodeNotFoundException("Node not found");
    }

    final NodeClass nodeClass;
    if (queryablesOnly) {
      nodeClass = this.ncApi.getClassById(node.getNodeSerie().getClassId());
    } else {
      nodeClass = null;
    }

    final Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> verInEdges = node.getVersionedInEdgeds();
    final Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> unverInEdges = node.getNodeSerie().getInEdges();

    final Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> combination = new HashMap<>();

          /* Add versioned edges */
    addToEdges(combination, label, queryablesOnly, nodeClass, verInEdges);

          /* Add unversioned edges */
    addToEdges(combination, label, queryablesOnly, nodeClass, unverInEdges);

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

  public boolean existsInCurrent(long receiverId, OidVersion oidVersion) {
    // TODO: Das muss nicht ins public api, da gibts schon das getState
    return getNodeFromCurrent(receiverId, oidVersion) != null;
  }

  @Nullable
  public Object getData(long receiverId, long senderId, OidVersion oidVersion, short typeIndex,
                        IDataGetter dataGetter) throws NodeNotFoundException {
    final NodeImpl node =
            this.nodes.getByUserId(receiverId).getNode(oidVersion.getOid(), oidVersion.getVersion(), false);
    if (node == null) {
      throw new NodeNotFoundException("NodeImpl not found");
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
    ITypeImpl ti = this.typesRegistry.get(type.getRef());
    final Object value = node.getData()[typeIndex];
    return ti.getData(dataGetter, value);
  }

}
