package com.dcrux.buran.coredb.iface.query.nodeMeta;

import com.dcrux.buran.coredb.iface.DomainId;
import com.dcrux.buran.coredb.iface.Edge;
import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.permissions.UserNodePermission;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 26.12.12
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public interface IMetaInfoForQuery {
  long getClassId();

  int getVersion();

  long getValidFrom();

  long getValidTo();

  long getReceiver();

  long getSender();

  UserNodePermission getOwnPermissions();

  Map<Long, UserNodePermission> getOtherPermissions();

  Set<DomainId> getDomains();

  NodeClass getNodeClass();

  boolean isQueryable(EdgeLabel label);

  /* Edges */
  Map<EdgeLabel, Map<EdgeIndex, Edge>> getPublicOutEdges(@Nullable EdgeLabel label);

  Map<EdgeLabel, Map<EdgeIndex, Edge>> getPrivateOutEdges(@Nullable EdgeLabel label);

  Map<EdgeLabel, Map<EdgeIndex, Edge>> getPublicInEdges(@Nullable EdgeLabel label);

  Map<EdgeLabel, Map<EdgeIndex, Edge>> getPrivateInEdges(@Nullable EdgeLabel label);

  INodeMatcher getNodeMatcher();
}
