package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;
import com.dcrux.buran.coredb.iface.nodeClass.*;
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

  private short keepAliveNumSeconds() {
    return 100;
  }

  @Override
  public CreateInfo createNew(UserId receiver, UserId sender, ClassId classId, Optional<KeepAliveHint> keepAliveHint) {
    IncNid incNid = getDmApi().createNew(receiver.getId(), sender.getId(), classId.getId(), null);
    return new CreateInfo(incNid,
            new KeepAliveInfo(keepAliveNumSeconds(), System.currentTimeMillis() + keepAliveNumSeconds() * 1000));
  }

  @Override
  public CreateInfoUpdate createNewUpdate(UserId receiver, UserId sender, Optional<KeepAliveHint> keepAliveHint,
                                          NidVer nodeToUpdate, Optional<HistoryHint> historyHint) throws
          NodeNotUpdatable, PermissionDeniedException, HistoryHintNotFulfillable {
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
    IncNid incNid =
            getDmApi().createNew(receiver.getId(), sender.getId(), nodeImpl.getNodeSerie().getClassId(), nodeToUpdate);
    final CreateInfo crInfo = new CreateInfo(incNid,
            new KeepAliveInfo(keepAliveNumSeconds(), System.currentTimeMillis() + keepAliveNumSeconds() * 1000));
    final CreateInfoUpdate criUpdate = new CreateInfoUpdate(crInfo, new HistoryInformation(HistoryFunction.keepAll));
    return criUpdate;
  }

  @Override
  public KeepAliveInfo keepAlive(UserId receiver, UserId sender, KeepAliveHint keepAliveHint, IncNid... incNid) throws
          IncubationNodeNotFound {
    return new KeepAliveInfo(keepAliveNumSeconds(), System.currentTimeMillis() + keepAliveNumSeconds() * 1000);
  }

  @Override
  public void cancelIncubationNode(UserId receiver, UserId sender, IncNid... incNid) throws IncubationNodeNotFound {
    //TODO: Not implemented
  }

  @Override
  public void setData(UserId receiver, UserId sender, IncNid incNid, short typeIndex, IDataSetter dataSetter) throws
          IncubationNodeNotFound {
    getDmApi().setData(receiver.getId(), sender.getId(), incNid, typeIndex, dataSetter);
  }

  @Override
  public void setEdge(UserId receiver, UserId sender, IncNid incNid, EdgeIndex index, EdgeLabel label,
                      IIncEdgeTarget target) throws EdgeIndexAlreadySet, IncubationNodeNotFound {
    getDmApi().setEdge(receiver.getId(), sender.getId(), incNid, index, label, target, false);
  }

  @Override
  public void setEdgeReplace(UserId receiver, UserId sender, IncNid incNid, EdgeIndex index, EdgeLabel label,
                             IIncEdgeTarget target) throws IncubationNodeNotFound {
    try {
      getDmApi().setEdge(receiver.getId(), sender.getId(), incNid, index, label, target, true);
    } catch (EdgeIndexAlreadySet edgeIndexAlreadySet) {
      throw new ExpectableException("This should never happen");
    }
  }

  @Override
  public void removeEdge(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label, EdgeIndex index) throws
          IncubationNodeNotFound {
    try {
      getDmApi().removeEdge(receiver.getId(), sender.getId(), incNid, label, index, false);
    } catch (EdgeIndexNotSet edgeIndexNotSet) {
      throw new ExpectableException("This should never happen");
    }
  }

  @Override
  public void removeEdgeStrict(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label, EdgeIndex index) throws
          EdgeIndexNotSet, IncubationNodeNotFound {
    getDmApi().removeEdge(receiver.getId(), sender.getId(), incNid, label, index, true);
  }

  @Override
  public void removeEdges(UserId receiver, UserId sender, IncNid incNid, Optional<EdgeLabel> label) throws
          IncubationNodeNotFound {
    getDmApi().removeEdges(receiver.getId(), sender.getId(), incNid, label);
  }

  @Nullable
  @Override
  public Object getData(UserId receiver, UserId sender, NidVer nidVersion, short typeIndex,
                        IDataGetter dataGetter) throws InformationUnavailableException, PermissionDeniedException,
          NodeNotFoundException {
    return getDrApi().getData(receiver.getId(), sender.getId(), nidVersion, typeIndex, dataGetter);
  }

  @Override
  public Map<EdgeLabel, Map<EdgeIndex, Edge>> getOutEdges(UserId receiver, UserId sender, NidVer nid,
                                                          EnumSet<EdgeType> types, Optional<EdgeLabel> label) throws
          NodeNotFoundException, InformationUnavailableException, PermissionDeniedException {
    return getDrApi().getOutEdges(receiver.getId(), sender.getId(), nid, types, false);
  }

  @Override
  public Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> getInEdges(UserId receiver, UserId sender, NidVer nid,
                                                                        EnumSet<EdgeType> types,
                                                                        Optional<EdgeLabel> label) throws
          NodeNotFoundException, InformationUnavailableException, PermissionDeniedException {
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
  public CommitResult commit(UserId receiver, UserId sender, IncNid... incNid) throws OptimisticLockingException,
          PermissionDeniedException, IncubationNodeNotFound {
    return getCommitApi().commit(receiver.getId(), sender.getId(), incNid);
  }

  @Override
  public NodeState getNodeState(UserId receiver, UserId sender, NidVer nid) throws NodeNotFoundException,
          PermissionDeniedException {
    final NodeState state = this.getMetaApi().getState(receiver.getId(), sender.getId(), nid);
    if (state == null) {
      throw new NodeNotFoundException("Node not found");
    }
    return state;
  }

  @Override
  @Nullable
  public NidVer getCurrentNodeVersion(UserId receiver, UserId sender, long nid) throws NodeNotFoundException {
    final Integer version = this.getDrApi().getCurrentNodeVersion(receiver.getId(), sender.getId(), nid);
    if (version == null) {
      throw new NodeNotFoundException("Node not found");
    }
    return new NidVer(nid, version);
  }
}
