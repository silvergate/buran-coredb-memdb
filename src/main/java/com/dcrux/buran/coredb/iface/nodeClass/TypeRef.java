package com.dcrux.buran.coredb.iface.nodeClass;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 19.12.12
 * Time: 00:53
 * To change this template use File | Settings | File Templates.
 */
public class TypeRef {
  private final short id;

  public TypeRef(short id) {
    this.id = id;
  }

  public short getId() {
    return id;
  }
}
