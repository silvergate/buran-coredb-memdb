package com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string;

import com.dcrux.buran.coredb.iface.nodeClass.ISorter;
import com.dcrux.buran.coredb.iface.nodeClass.SorterRef;

/**
 *
 * @author caelis
 */
public class StringUnicodeSort implements ISorter {

  private static final SorterRef REF = new SorterRef((short) 1);
  private static final StringUnicodeSort SINGLETON = new StringUnicodeSort();

  @Override
  public ISorter getSingleton() {
    return SINGLETON;
  }

  @Override
  public SorterRef getRef() {
    return REF;
  }

  @Override
  public int compare(Object o1, Object o2) {
    return -1;
  }
}
