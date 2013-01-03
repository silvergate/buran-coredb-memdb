package com.dcrux.buran.coredb.iface.api;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;
import com.dcrux.buran.coredb.iface.nodeClass.*;
import com.google.common.base.Optional;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 02.01.13
 * Time: 15:26
 */
public interface IApi {

  /* REGION: Classes */

  NodeClassHash declareClass(NodeClass nodeClass) throws PermissionDeniedException;

  @Nullable
  ClassId getClassIdByHash(NodeClassHash hash);

  /* REGION: Create and commit */

  CreateInfo createNew(UserId receiver, UserId sender, ClassId classId, Optional<KeepAliveHint> keepAliveHint);

  CreateInfoUpdate createNewUpdate(UserId receiver, UserId sender, Optional<KeepAliveHint> keepAliveHint,
                                   NidVer nodeToUpdate, Optional<HistoryHint> historyHint) throws NodeNotUpdatable,
          PermissionDeniedException, HistoryHintNotFulfillable;

  CommitResult commit(UserId receiver, UserId sender, IncNid... incNid) throws OptimisticLockingException,
          PermissionDeniedException, IncubationNodeNotFound;

  KeepAliveInfo keepAlive(UserId receiver, UserId sender, IncNid incNid, KeepAliveHint keepAliveHint) throws
          IncubationNodeNotFound;

  void cancelIncubationNode(UserId receiver, UserId sender, IncNid incNid) throws IncubationNodeNotFound;

  /* REGION: Data manipulation */

  void setData(UserId receiver, UserId sender, IncNid incNid, short typeIndex, IDataSetter dataSetter) throws
          IncubationNodeNotFound;

  void setEdge(UserId receiver, UserId sender, IncNid incNid, EdgeIndex index, EdgeLabel label,
               IIncEdgeTarget target) throws EdgeIndexAlreadySet, IncubationNodeNotFound;

  void setEdgeReplace(UserId receiver, UserId sender, IncNid incNid, EdgeIndex index, EdgeLabel label,
                      IIncEdgeTarget target) throws IncubationNodeNotFound;

  void removeEdgeStrict(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label, EdgeIndex index) throws
          EdgeIndexNotSet, IncubationNodeNotFound;

  void removeEdge(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label, EdgeIndex index) throws
          IncubationNodeNotFound;

  void removeEdges(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label) throws IncubationNodeNotFound;

  /* REGION: Data read api */

  @Nullable
  public Object getData(UserId receiver, UserId sender, NidVer nidVersion, short typeIndex,
                        IDataGetter dataGetter) throws InformationUnavailableException, PermissionDeniedException,
          NodeNotFoundException;

  Map<EdgeLabel, Map<EdgeIndex, Edge>> getOutEdges(UserId receiver, UserId sender, NidVer oid, EnumSet<EdgeType> types,
                                                   Optional<EdgeLabel> label) throws NodeNotFoundException,
          InformationUnavailableException, PermissionDeniedException;

  Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> getInEdges(UserId receiver, UserId sender, NidVer oid,
                                                                 EnumSet<EdgeType> types,
                                                                 Optional<EdgeLabel> label) throws
          NodeNotFoundException, InformationUnavailableException, PermissionDeniedException;

}