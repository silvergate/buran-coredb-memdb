package com.dcrux.buran.coredb.iface.query.nodeMeta;

import com.dcrux.buran.coredb.iface.NidVer;
import com.dcrux.buran.coredb.iface.query.IQNode;

/**
 * @author caelis
 */
public interface INodeMatcher {
  boolean matchesVersion(NidVer nidVer, IQNode qNode);

  boolean matches(long oid, IQNode qNode);
}
