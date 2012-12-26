package com.dcrux.buran.coredb.iface.query.propertyCondition;

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
}
