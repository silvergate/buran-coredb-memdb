package com.dcrux.buran.coredb.iface;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */
public class DomainId {
  private final long id;

  public DomainId(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }
}
