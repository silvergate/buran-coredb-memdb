package com.dcrux.buran.coredb.iface.query.propertyCondition;

import com.dcrux.buran.coredb.iface.api.ExpectableException;
import com.dcrux.buran.coredb.iface.nodeClass.ICmp;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;

/**
 *
 * @author caelis
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

  @Override
  public boolean matches(Object[] data, NodeClass nodeClass) {
    final IType type = nodeClass.getType(this.typeIndex);
    final boolean supports = type.supports(this.comparator.getRef());
    if (!supports) {
      throw new ExpectableException("The type does not support the given comparator");
    }
    final Object rhs = data[this.typeIndex];
    final boolean matches = this.comparator.matches(rhs);
    return matches;
  }
}
