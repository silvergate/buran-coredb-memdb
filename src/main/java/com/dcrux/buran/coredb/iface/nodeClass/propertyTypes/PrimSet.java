package com.dcrux.buran.coredb.iface.nodeClass.propertyTypes;

import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 24.12.12
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public final class PrimSet implements IDataSetter {
  private final Object value;

  private PrimSet(Object value) {
    this.value = value;
  }

  public static PrimSet string(final String value) {
    assert (value != null);
    return new PrimSet(value);
  }

  public static PrimSet integer(final int value) {
    return new PrimSet(value);
  }

  public Object getValue() {
    return value;
  }
}
