package com.dcrux.buran.coredb.iface.nodeClass;

/**
 *
 * @author caelis
 */
public interface ICmp {
  CmpRef getRef();

  boolean matches(Object lhs);
}
