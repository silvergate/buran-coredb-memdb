package com.dcrux.buran.coredb.iface;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 02.01.13
 * Time: 14:56
 */
public final class UserId {
  private final long id;

  public static UserId c(long id) {
    return new UserId(id);
  }

  private UserId(long id) {
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

    UserId userId = (UserId) o;

    if (id != userId.id) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
