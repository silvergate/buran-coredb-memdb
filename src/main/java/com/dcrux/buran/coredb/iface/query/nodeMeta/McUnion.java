package com.dcrux.buran.coredb.iface.query.nodeMeta;

/**
 *
 * @author caelis
 */
public class McUnion implements INodeMetaCondition {

  private final INodeMetaCondition val1;
  private final INodeMetaCondition val2;

  public McUnion(INodeMetaCondition val1, INodeMetaCondition val2) {
    this.val1 = val1;
    this.val2 = val2;
  }

  public INodeMetaCondition getVal1() {
    return val1;
  }

  public INodeMetaCondition getVal2() {
    return val2;
  }

  @Override
  public boolean matches(IMetaInfoForQuery metaInfoForQuery) {
    final boolean c1 = this.val1.matches(metaInfoForQuery);
    if (c1) {
      return true;
    }
    final boolean c2 = this.val2.matches(metaInfoForQuery);
    if (c2) {
      return true;
    }
    return false;
  }
}
