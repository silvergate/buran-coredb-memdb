package com.dcrux.buran.coredb.iface.nodeClass;

/**
 *
 * @author caelis
 */
public interface ISorter {
  ISorter getSingleton();

  SorterRef getRef();

  int compare(Object o1, Object o2);
}
