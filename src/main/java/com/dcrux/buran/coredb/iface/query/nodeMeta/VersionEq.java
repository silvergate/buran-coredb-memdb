package com.dcrux.buran.coredb.iface.query.nodeMeta;

/**
 * @author caelis
 */
public class VersionEq implements INodeMetaCondition {
  private final int version;

  public VersionEq(int version) {
    this.version = version;
  }

  public int getVersion() {
    return version;
  }

  @Override
  public boolean matches(IMetaInfoForQuery metaInfoForQuery) {
    return metaInfoForQuery.getVersion() == this.version;
  }
}
