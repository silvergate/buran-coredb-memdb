package com.dcrux.buran.coredb.iface;

/**
 *
 * @author caelis
 */
public class IncOid {
  private final long id;

  public IncOid(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    IncOid incOid = (IncOid) o;

    if (id != incOid.id) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
