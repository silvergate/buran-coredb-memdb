package com.dcrux.buran.coredb.iface.query.propertyCondition;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 25.12.12
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class PcIntersection implements IPropertyCondition {
  private final IPropertyCondition val1;
  private final IPropertyCondition val2;

  public PcIntersection(IPropertyCondition val1, IPropertyCondition val2) {
    this.val1 = val1;
    this.val2 = val2;
  }

  public IPropertyCondition getVal1() {
    return val1;
  }

  public IPropertyCondition getVal2() {
    return val2;
  }
}
