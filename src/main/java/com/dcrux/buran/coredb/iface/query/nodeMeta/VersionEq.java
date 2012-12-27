package com.dcrux.buran.coredb.iface.query.nodeMeta;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
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
