package com.dcrux.buran.coredb.iface.nodeClass;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 19.12.12
 * Time: 00:49
 * To change this template use File | Settings | File Templates.
 */
public interface ICmp {
  CmpRef getRef();

  boolean matches(Object lhs);
}
