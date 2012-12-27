package com.dcrux.buran.coredb.iface.query.nodeMeta;

import com.dcrux.buran.coredb.iface.OidVersion;
import com.dcrux.buran.coredb.iface.query.IQNode;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 26.12.12
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
public interface INodeMatcher {
  boolean matchesVersion(OidVersion oidVersion, IQNode qNode);

  boolean matches(long oid, IQNode qNode);
}
