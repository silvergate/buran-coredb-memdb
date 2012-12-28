package com.dcrux.buran.coredb.iface.query.nodeMeta;

/**
 *
 * @author caelis
 */
public interface INodeMetaCondition {
  boolean matches(IMetaInfoForQuery metaInfoForQuery);
}
