package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.OidVersion;
import com.dcrux.buran.coredb.iface.api.ExpectableException;
import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.data.Node;
import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.ITypeImpl;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public class DataReadApi {
  private final Nodes nodes;
  private final NodeClassesApi ncApi;
  private final TypesRegistry typesRegistry;

  public DataReadApi(Nodes nodes, NodeClassesApi ncApi, TypesRegistry typesRegistry) {
    this.nodes = nodes;
    this.ncApi = ncApi;
    this.typesRegistry = typesRegistry;
  }

  @Nullable
  Node getNodeFromCurrent(long receiverId, OidVersion oid) {
    return this.nodes.getByUserId(receiverId).getNode(oid.getOid(), oid.getVersion(), true);
  }

  @Nullable
  Node getNodeFromCurrentOrHistorized(long receiverId, OidVersion oid) {
    return this.nodes.getByUserId(receiverId).getNode(oid.getOid(), oid.getVersion(), false);
  }

  @Nullable
  public Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> getOutEdges(long receiverId, long senderId, OidVersion oid,
                                                              boolean isPublic) {
    final Node node = getNodeFromCurrentOrHistorized(receiverId, oid);
    if (node == null) {
      return null;
    }
    final Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> privateEdges = new HashMap<>();
    for (Map.Entry<EdgeLabel, Map<EdgeIndex, EdgeImpl>> edgesEntry : node.getOutEdges().entrySet()) {
      if (isPublic == edgesEntry.getKey().isPublic()) {
        privateEdges.put(edgesEntry.getKey(), edgesEntry.getValue());
      }
    }
    return privateEdges;
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
                        IDataGetter dataGetter) {
    final Node node = this.nodes.getByUserId(receiverId).getNode(oidVersion.getOid(), oidVersion.getVersion(), false);
    if (node == null) {
      throw new ExpectableException("Node not found");
    }
    final long classId = node.getNodeSerie().getClassId();

    final NodeClass nc = this.ncApi.getClassById(classId);
    if (nc == null) {
      throw new IllegalStateException("NodeClass not found");
    }
    final IType type = nc.getType(typeIndex);
    final boolean supports = type.supports(dataGetter);
    if (!supports) {
      throw new IllegalArgumentException("The given data getter is no supported on this type.");
    }
    ITypeImpl ti = this.typesRegistry.get(type.getRef());
    final Object value = node.getData()[typeIndex];
    return ti.getData(dataGetter, value);
  }

}
