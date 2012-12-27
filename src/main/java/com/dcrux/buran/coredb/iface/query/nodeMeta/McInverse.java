package com.dcrux.buran.coredb.iface.query.nodeMeta;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 27.12.12
 * Time: 01:07
 * To change this template use File | Settings | File Templates.
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
