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

  /**
   * Declares a class and returns its hash. The hash can the be converted to a class-id. The class-hash is globally valid.
   *
   * @param nodeClass
   * @return
   * @throws PermissionDeniedException
   */
  NodeClassHash declareClass(NodeClass nodeClass) throws PermissionDeniedException;

  /**
   * Gets the class-id given a class-hash. The class-id is specific to a buran server instance.
   *
   * @param hash Globally valid class-hash.
   * @return Returns the class-id or <code>null</code> if the class has not yet been defined. Call {@link #declareClass(com.dcrux.buran.coredb.iface.nodeClass.NodeClass)} first.
   */
  @Nullable
  ClassId getClassIdByHash(NodeClassHash hash);

  /* REGION: Create and commit */

  /**
   * Create a new node in incubation without updating.
   *
   * @param receiver
   * @param sender
   * @param classId
   * @param keepAliveHint
   * @return
   */
  CreateInfo createNew(UserId receiver, UserId sender, ClassId classId, Optional<KeepAliveHint> keepAliveHint);

  /**
   * Creates a new node in incubation. The created node will update the existing node given by @param nodeToUpdate after calling commit.
   *
   * @param receiver
   * @param sender
   * @param keepAliveHint
   * @param nodeToUpdate
   * @param historyHint
   * @return
   * @throws NodeNotUpdatable          Is thrown if the node to update is not currently the current version or if the node has been marked as deleted.
   * @throws PermissionDeniedException
   * @throws HistoryHintNotFulfillable
   */
  CreateInfoUpdate createNewUpdate(UserId receiver, UserId sender, Optional<KeepAliveHint> keepAliveHint,
                                   NidVer nodeToUpdate, Optional<HistoryHint> historyHint) throws NodeNotUpdatable,
          PermissionDeniedException, HistoryHintNotFulfillable;

  /**
   * Commits one or more nodes from incubation. This method works atomic.
   *
   * @param receiver
   * @param sender
   * @param incNid   One or more nodes from incubation.
   * @return
   * @throws OptimisticLockingException
   * @throws PermissionDeniedException
   * @throws IncubationNodeNotFound
   */
  CommitResult commit(UserId receiver, UserId sender, IncNid... incNid) throws OptimisticLockingException,
          PermissionDeniedException, IncubationNodeNotFound;

  /**
   * Extends the keep alive time of a node in incubation.
   *
   * @param receiver
   * @param sender
   * @param keepAliveHint
   * @param incNid        One or more nodes in incubation.
   * @return Keep-alive-information.
   * @throws IncubationNodeNotFound Is thrown if at least one node cannot be found.
   */
  KeepAliveInfo keepAlive(UserId receiver, UserId sender, KeepAliveHint keepAliveHint, IncNid... incNid) throws
          IncubationNodeNotFound;

  /**
   * Removed one or more nodes from incubation without committing. Continues with execution if removal of a node fails.
   *
   * @param receiver
   * @param sender
   * @param incNid
   * @throws IncubationNodeNotFound Is thrown if at least one given node is not found (continues with removal of other nodes).
   */
  void cancelIncubationNode(UserId receiver, UserId sender, IncNid... incNid) throws IncubationNodeNotFound;

  /* REGION: Data manipulation */

  /**
   * Sets data to the property specified by typeIndex.
   *
   * @param receiver
   * @param sender
   * @param incNid
   * @param typeIndex  Type index. Has to be defined in the class.
   * @param dataSetter An implementation of {@link IDataSetter}. Has to be supported by the type defined in the class.
   * @throws IncubationNodeNotFound
   */
  void setData(UserId receiver, UserId sender, IncNid incNid, short typeIndex, IDataSetter dataSetter) throws
          IncubationNodeNotFound;

  /**
   * Sets an edge specified by the given label and index to the given target. Fails if an edge specified by label and index already exists.
   *
   * @param receiver
   * @param sender
   * @param incNid
   * @param index
   * @param label
   * @param target
   * @throws EdgeIndexAlreadySet    The specified (by label and index) edge already exists.
   * @throws IncubationNodeNotFound
   * @see #setEdgeReplace(com.dcrux.buran.coredb.iface.UserId, com.dcrux.buran.coredb.iface.UserId, com.dcrux.buran.coredb.iface.IncNid, com.dcrux.buran.coredb.iface.EdgeIndex, com.dcrux.buran.coredb.iface.EdgeLabel, com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget)
   */
  void setEdge(UserId receiver, UserId sender, IncNid incNid, EdgeIndex index, EdgeLabel label,
               IIncEdgeTarget target) throws EdgeIndexAlreadySet, IncubationNodeNotFound;

  /**
   * Sets an edge specified by the given label and index to the given target. Will replace an existing edge.
   *
   * @param receiver
   * @param sender
   * @param incNid
   * @param index
   * @param label
   * @param target
   * @throws IncubationNodeNotFound
   * @see #setEdge(com.dcrux.buran.coredb.iface.UserId, com.dcrux.buran.coredb.iface.UserId, com.dcrux.buran.coredb.iface.IncNid, com.dcrux.buran.coredb.iface.EdgeIndex, com.dcrux.buran.coredb.iface.EdgeLabel, com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget)
   */
  void setEdgeReplace(UserId receiver, UserId sender, IncNid incNid, EdgeIndex index, EdgeLabel label,
                      IIncEdgeTarget target) throws IncubationNodeNotFound;

  /**
   * Removes a single edge by given label an index. Throws {@link EdgeIndexNotSet} if the specified edge does not exist.
   *
   * @param receiver
   * @param sender
   * @param incNid
   * @param label
   * @param index
   * @throws EdgeIndexNotSet        Is thrown if the edge specified by label and index does not exists.
   * @throws IncubationNodeNotFound
   * @see #removeEdge(com.dcrux.buran.coredb.iface.UserId, com.dcrux.buran.coredb.iface.UserId, com.dcrux.buran.coredb.iface.IncNid, com.dcrux.buran.coredb.iface.EdgeLabel, com.dcrux.buran.coredb.iface.EdgeIndex)
   */
  void removeEdgeStrict(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label, EdgeIndex index) throws
          EdgeIndexNotSet, IncubationNodeNotFound;

  /**
   * Removes a single edge by given label and index. Does nothing if the specified edge does not exist.
   *
   * @param receiver
   * @param sender
   * @param incNid
   * @param label
   * @param index
   * @throws IncubationNodeNotFound
   * @see #removeEdgeStrict(com.dcrux.buran.coredb.iface.UserId, com.dcrux.buran.coredb.iface.UserId, com.dcrux.buran.coredb.iface.IncNid, com.dcrux.buran.coredb.iface.EdgeLabel, com.dcrux.buran.coredb.iface.EdgeIndex)
   */
  void removeEdge(UserId receiver, UserId sender, IncNid incNid, EdgeLabel label, EdgeIndex index) throws
          IncubationNodeNotFound;

  /**
   * Removes all out edges with the specified label (if a label is given) or all edges (if no label is given).
   *
   * @param receiver
   * @param sender
   * @param incNid
   * @param label
   * @throws IncubationNodeNotFound
   */
  void removeEdges(UserId receiver, UserId sender, IncNid incNid, Optional<EdgeLabel> label) throws
          IncubationNodeNotFound;

  /* REGION: Data read api */

  /**
   * Gets data from a property of a node.
   *
   * @param receiver
   * @param sender
   * @param nidVersion
   * @param typeIndex  Type index, is defined in the node class.
   * @param dataGetter Implementation of {@link IDataGetter}. Has to be supported by the type defined in the class.
   * @return Data. Type depends on the implementation of the chosen {@link IDataGetter}.
   * @throws InformationUnavailableException
   *
   * @throws PermissionDeniedException
   * @throws NodeNotFoundException
   */
  @Nullable
  Object getData(UserId receiver, UserId sender, NidVer nidVersion, short typeIndex, IDataGetter dataGetter) throws
          InformationUnavailableException, PermissionDeniedException, NodeNotFoundException;

  /**
   * Gets out-edges from a node. Can optionally be filtered by label and public and private edges.
   *
   * @param receiver
   * @param sender
   * @param nid
   * @param types    Optional: Filter by modifier (public or private). Must not be empty.
   * @param label    Optional: Filter by label.
   * @return
   * @throws NodeNotFoundException
   * @throws InformationUnavailableException
   *
   * @throws PermissionDeniedException
   */
  Map<EdgeLabel, Map<EdgeIndex, Edge>> getOutEdges(UserId receiver, UserId sender, NidVer nid, EnumSet<EdgeType> types,
                                                   Optional<EdgeLabel> label) throws NodeNotFoundException,
          InformationUnavailableException, PermissionDeniedException;

  /**
   * Gets in-edges from a node. Can optionally be filtered by label and public and private edges.
   *
   * @param receiver
   * @param sender
   * @param nid
   * @param types    Optional: Filter by modifier (public or private). Must not be empty.
   * @param label    Optional: Filter by label.
   * @return
   * @throws NodeNotFoundException
   * @throws InformationUnavailableException
   *
   * @throws PermissionDeniedException
   */
  Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> getInEdges(UserId receiver, UserId sender, NidVer nid,
                                                                 EnumSet<EdgeType> types,
                                                                 Optional<EdgeLabel> label) throws
          NodeNotFoundException, InformationUnavailableException, PermissionDeniedException;

  /* REGION: Meta-Data read api */

  /**
   * Returns the state of the given node.
   *
   * @param receiver
   * @param sender
   * @param nid
   * @return
   * @throws NodeNotFoundException     Is thrown if a node with the given node-id does not exists (now and in the past).
   * @throws PermissionDeniedException
   */
  NodeState getNodeState(UserId receiver, UserId sender, NidVer nid) throws NodeNotFoundException,
          PermissionDeniedException;

  /**
   * Returns the current version of the node specified by its id.
   *
   * @param receiver
   * @param sender
   * @param nid
   * @return
   * @throws NodeNotFoundException The node given by its id was not found.
   */
  NidVer getCurrentNodeVersion(UserId receiver, UserId sender, long nid) throws NodeNotFoundException;
}