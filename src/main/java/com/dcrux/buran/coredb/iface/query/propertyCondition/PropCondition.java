package com.dcrux.buran.coredb.iface.query.propertyCondition;

import com.dcrux.buran.coredb.iface.nodeClass.ICmp;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 21:08
 * To change this template use File | Settings | File Templates.
 */
public class PropCondition implements IPropertyCondition {
  private final short typeIndex;
  private final ICmp comparator;

  public PropCondition(short typeIndex, ICmp comparator) {
    this.typeIndex = typeIndex;
    this.comparator = comparator;
  }

  public short getTypeIndex() {
    return typeIndex;
  }

  public ICmp getComparator() {
    return comparator;
  }
}
