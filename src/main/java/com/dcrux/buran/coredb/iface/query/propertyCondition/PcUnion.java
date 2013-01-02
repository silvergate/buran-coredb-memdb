package com.dcrux.buran.coredb.iface.query.propertyCondition;

import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;

/**
 * @author caelis
 */
public class PcUnion implements IPropertyCondition {
  private final IPropertyCondition val1;
  private final IPropertyCondition val2;

  public PcUnion(IPropertyCondition val1, IPropertyCondition val2) {
    this.val1 = val1;
    this.val2 = val2;
  }

  public IPropertyCondition getVal1() {
    return val1;
  }

  public IPropertyCondition getVal2() {
    return val2;
  }

  @Override
  public boolean matches(Object[] data, NodeClass nodeClass) {
    final boolean matches1 = this.val1.matches(data, nodeClass);
    if (matches1) {
      return true;
    }
    final boolean matches2 = this.val2.matches(data, nodeClass);
    return matches2;
  }
}
