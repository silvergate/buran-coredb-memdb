package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.memoryImpl.data.NodeClasses;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;

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

  public ApiIface() {
    this.typesRegistry = new TypesRegistry();
    Nodes nodes = new Nodes();
    NodeClasses ncs = new NodeClasses();
    this.nodeClassesApi = new NodeClassesApi(ncs);
    this.dataManipulationApi = new DmApi(nodes, this.nodeClassesApi, typesRegistry);
    this.dataReadApi = new DataReadApi(nodes, this.nodeClassesApi, typesRegistry);
    this.commitApi = new CommitApi(nodes, this.dataReadApi, this.nodeClassesApi);
    this.metaApi = new MiApi(this.dataReadApi, this.dataManipulationApi);
    this.queryApi = new QueryApi(nodes, getNodeClassesApi(), this.dataReadApi);
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

  @Override
  public IncOid createNew(UserId receiver, UserId sender, ClassId classId) {
    return getDmApi().createNew(receiver.getId(), sender.getId(), classId.getId(), null);
  }

  @Override
  public IncOid createNewUpdate(UserId receiver, UserId sender, OidVersion nodeToUpdate) throws NodeNotUpdatable,
          PermissionDeniedException {
    final NodeImpl nodeImpl = getDrApi().getNodeFromCurrent(receiver.getId(), nodeToUpdate);
    if (nodeImpl == null) {
      throw new NodeNotUpdatable();
    }
    return getDmApi().createNew(receiver.getId(), sender.getId(), nodeImpl.getNodeSerie().getClassId(), nodeToUpdate);
  }

  @Override
  public void setData(UserId receiver, UserId sender, IncOid incOid, short typeIndex, IDataSetter dataSetter) throws
          IncubationNodeNotFound {
    getDmApi().setData(receiver.getId(), sender.getId(), incOid, typeIndex, dataSetter);
  }

  @Override
  public void setEdge(UserId receiver, UserId sender, IncOid incOid, EdgeIndex index, EdgeLabel label,
                      IIncEdgeTarget target) throws EdgeIndexAlreadySet, IncubationNodeNotFound {
    getDmApi().setEdge(receiver.getId(), sender.getId(), incOid, index, label, target, false);
  }

  @Override
  public void setEdgeReplace(UserId receiver, UserId sender, IncOid incOid, EdgeIndex index, EdgeLabel label,
                             IIncEdgeTarget target) throws IncubationNodeNotFound {
    try {
      getDmApi().setEdge(receiver.getId(), sender.getId(), incOid, index, label, target, true);
    } catch (EdgeIndexAlreadySet edgeIndexAlreadySet) {
      throw new ExpectableException("This should never happen");
    }
  }

  @Override
  public void removeEdge(UserId receiver, UserId sender, IncOid incOid, EdgeIndex index) throws IncubationNodeNotFound {
    try {
      getDmApi().removeEdge(receiver.getId(), sender.getId(), incOid, index, false);
    } catch (EdgeIndexNotSet edgeIndexNotSet) {
      throw new ExpectableException("This should never happen");
    }
  }

  @Override
  public void removeEdgeStrict(UserId receiver, UserId sender, IncOid incOid, EdgeIndex index) throws EdgeIndexNotSet,
          IncubationNodeNotFound {
    getDmApi().removeEdge(receiver.getId(), sender.getId(), incOid, index, true);
  }

  @Override
  public void removeEdges(UserId receiver, UserId sender, IncOid incOid, EdgeLabel label) throws
          IncubationNodeNotFound {
    getDmApi().removeEdges(receiver.getId(), sender.getId(), incOid, label);
  }

  @Nullable
  @Override
  public Object getData(UserId receiver, UserId sender, OidVersion nidVersion, short typeIndex,
                        IDataGetter dataGetter) throws InformationUnavailableException, PermissionDeniedException,
          NodeNotFoundException {
    return getDrApi().getData(receiver.getId(), sender.getId(), nidVersion, typeIndex, dataGetter);
  }

  @Override
  public Map<EdgeLabel, Map<EdgeIndex, Edge>> getOutEdges(UserId receiver, UserId sender, OidVersion oid,
                                                          EnumSet<EdgeType> types, Optional<EdgeLabel> label) throws
          NodeNotFoundException, InformationUnavailableException, PermissionDeniedException {
    return getDrApi().getOutEdges(receiver.getId(), sender.getId(), oid, types, false);
  }

  @Override
  public Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> getInEdges(UserId receiver, UserId sender, OidVersion oid,
                                                                        EnumSet<EdgeType> types,
                                                                        Optional<EdgeLabel> label) throws
          NodeNotFoundException, InformationUnavailableException, PermissionDeniedException {
    return getDrApi().getInEdges(receiver.getId(), sender.getId(), oid, types, label, false);
  }
}
