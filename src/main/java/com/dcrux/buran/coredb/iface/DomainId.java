package com.dcrux.buran.coredb.iface;

/**
 *
 * @author caelis
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
