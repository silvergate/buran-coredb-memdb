package com.dcrux.buran.coredb.iface.query.nodeMeta;

/**
 * @author caelis
 */
public class McInverse implements INodeMetaCondition {

  private final INodeMetaCondition val;

  public McInverse(INodeMetaCondition val) {
    this.val = val;
  }

  public INodeMetaCondition getVal() {
    return val;
  }

  @Override
  public boolean matches(IMetaInfoForQuery metaInfoForQuery) {
    return !this.val.matches(metaInfoForQuery);
  }
}
