package com.dcrux.buran.coredb.iface.query.nodeMeta;

import com.dcrux.buran.coredb.iface.OidVersion;
import com.dcrux.buran.coredb.iface.query.IQNode;

/**
 * @author caelis
 */
public interface INodeMatcher {
  boolean matchesVersion(OidVersion oidVersion, IQNode qNode);

  boolean matches(long oid, IQNode qNode);
}
