package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.Edge;
import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.OidVersion;
import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.data.Edges;
import com.dcrux.buran.coredb.memoryImpl.data.Node;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.data.NodesSingleClass;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.ITypeImpl;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public class DataReadApi {
  private final Edges edges;
  private final Nodes nodes;
  private final NodeClassesApi ncApi;
  private final TypesRegistry typesRegistry;

  public DataReadApi(Edges edges, Nodes nodes, NodeClassesApi ncApi, TypesRegistry typesRegistry) {
    this.edges = edges;
    this.nodes = nodes;
    this.ncApi = ncApi;
    this.typesRegistry = typesRegistry;
  }

  @Nullable
  Node getNodeFromCurrent(long receiverId, OidVersion oid) {
    final Long classId = this.nodes.getByUserId(receiverId).getOidToClassId().get(oid.getOid());
    if (classId == null) {
      return null;
    }
    final NodesSingleClass nsc = this.nodes.getByUserId(receiverId).getByClassId(classId);
    if (nsc == null) {
      return null;
    }
    return nsc.getCurrent().get(oid);
  }

  @Nullable
  Node getNodeFromCurrentOrHistorized(long receiverId, OidVersion oid) {
    final Long classId = this.nodes.getByUserId(receiverId).getOidToClassId().get(oid.getOid());
    if (classId == null) {
      return null;
    }
    final NodesSingleClass nsc = this.nodes.getByUserId(receiverId).getByClassId(classId);
    if (nsc == null) {
      return null;
    }
    return nsc.getCurrentAndHistorized().get(oid);
  }

  @Nullable
  public Map<EdgeLabel, Map<EdgeIndex, Edge>> getPrivateOutEdges(long receiverId, long senderId, OidVersion oid) {
    final Node node = getNodeFromCurrentOrHistorized(receiverId, oid);
    if (node == null) {
      return null;
    }
    return node.getPrivateEdges();
  }

  @Nullable
  public Map<EdgeLabel, Map<EdgeIndex, Edge>> getPublicOutEdges(long receiverId, long senderId, OidVersion oid) {
    final Node node = getNodeFromCurrentOrHistorized(receiverId, oid);
    if (node == null) {
      return null;
    }
    return node.getPublicEdges();
  }

  /**
   * Liefet <code>true</code>, falls die OID irgendwo vorhanden ist (current oder historisiert).
   *
   * @param receiverId
   * @param oid
   * @return
   */
  public boolean oidExistsInCurrentOrHistory(long receiverId, long oid) {
    final Long classId = this.nodes.getByUserId(receiverId).getOidToClassId().get(oid);
    if (classId == null) {
      return false;
    }
    return true;
  }

  public boolean existsInCurrent(long receiverId, OidVersion oidVersion) {
    // TODO: Das muss nicht ins public api, da gibts schon das getState
    return getNodeFromCurrent(receiverId, oidVersion) != null;
  }

  @Nullable
  public Object getData(long receiverId, long senderId, OidVersion oidVersion, short typeIndex,
                        IDataGetter dataGetter) {
    final long classId = this.nodes.getByUserId(receiverId).getOidToClassId().get(oidVersion.getOid());
    final Node node =
            this.nodes.getByUserId(receiverId).getByClassId(classId).getCurrentAndHistorized().get(oidVersion);
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
