package com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string;

import com.dcrux.buran.coredb.iface.nodeClass.CmpRef;
import com.dcrux.buran.coredb.iface.nodeClass.ICmp;

/**
 * @author caelis
 */
public class StringEq implements ICmp {
  public static final CmpRef REF = new CmpRef((short) 21);

  private final String rhs;

  public StringEq(String rhs) {
    this.rhs = rhs;
  }

  @Override
  public CmpRef getRef() {
    return REF;
  }

  @Override
  public boolean matches(Object lhs) {
    if ((lhs == null) && (this.rhs == null)) {
      return true;
    }
    if (lhs == null) {
      return false;
    }
    return lhs.equals(this.rhs);
  }
}
