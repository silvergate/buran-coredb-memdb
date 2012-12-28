package com.dcrux.buran.coredb.iface.query.propertyCondition;

import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;

/**
 *
 * @author caelis
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
