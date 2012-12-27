package com.dcrux.buran.coredb.iface.query.propertyCondition;

import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 25.12.12
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class PcInverse implements IPropertyCondition {
  private final IPropertyCondition val;

  public PcInverse(IPropertyCondition val) {
    this.val = val;
  }

  public IPropertyCondition getVal() {
    return val;
  }

  @Override
  public boolean matches(Object[] data, NodeClass nodeClass) {
    final boolean matches = this.val.matches(data, nodeClass);
    return !matches;
  }
}
