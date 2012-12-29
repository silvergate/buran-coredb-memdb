package com.dcrux.buran.coredb.iface.query.nodeMeta;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.EdgeWithSource;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.permissions.UserNodePermission;
import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.Set;

/**
 * @author caelis
 */
public interface IMetaInfoForQuery {

  public enum EdgeType {
    out,
    in
  }

  long getClassId();

  int getVersion();

  long getValidFrom();

  long getValidTo();

  long getReceiver();

  long getSender();

  UserNodePermission getOwnPermissions();

  Map<Long, UserNodePermission> getOtherPermissions();

  Set<Long> getDomainIds();

  NodeClass getNodeClass();

  /* Edges */
  Map<EdgeIndex, EdgeWithSource> getQueryableOutEdges(EdgeLabel label);

  Multimap<EdgeIndex, EdgeWithSource> getQueryableInEdges(EdgeLabel label);

  INodeMatcher getNodeMatcher();
}
