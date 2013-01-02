package com.dcrux.buran.coredb.iface.api;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
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

  /* Data manipulation */

  IncOid createNew(UserId receiver, UserId sender, ClassId classId);

  IncOid createNewUpdate(UserId receiver, UserId sender, OidVersion nodeToUpdate) throws NodeNotUpdatable,
          PermissionDeniedException;

  void setData(UserId receiver, UserId sender, IncOid incOid, short typeIndex, IDataSetter dataSetter) throws
          IncubationNodeNotFound;

  void setEdge(UserId receiver, UserId sender, IncOid incOid, EdgeIndex index, EdgeLabel label,
               IIncEdgeTarget target) throws EdgeIndexAlreadySet, IncubationNodeNotFound;

  void setEdgeReplace(UserId receiver, UserId sender, IncOid incOid, EdgeIndex index, EdgeLabel label,
                      IIncEdgeTarget target) throws IncubationNodeNotFound;

  void removeEdge(UserId receiver, UserId sender, IncOid incOid, EdgeIndex index) throws IncubationNodeNotFound;

  void removeEdgeStrict(UserId receiver, UserId sender, IncOid incOid, EdgeIndex index) throws EdgeIndexNotSet,
          IncubationNodeNotFound;

  void removeEdges(UserId receiver, UserId sender, IncOid incOid, EdgeLabel label) throws IncubationNodeNotFound;

  /* Data read api */

  @Nullable
  public Object getData(UserId receiver, UserId sender, OidVersion nidVersion, short typeIndex,
                        IDataGetter dataGetter) throws InformationUnavailableException, PermissionDeniedException,
          NodeNotFoundException;

  Map<EdgeLabel, Map<EdgeIndex, Edge>> getOutEdges(UserId receiver, UserId sender, OidVersion oid,
                                                   EnumSet<EdgeType> types, Optional<EdgeLabel> label) throws
          NodeNotFoundException, InformationUnavailableException, PermissionDeniedException;

  Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> getInEdges(UserId receiver, UserId sender, OidVersion oid,
                                                                 EnumSet<EdgeType> types,
                                                                 Optional<EdgeLabel> label) throws
          NodeNotFoundException, InformationUnavailableException, PermissionDeniedException;

}