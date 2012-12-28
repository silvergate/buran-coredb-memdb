package com.dcrux.buran.coredb.iface.nodeClass;

/**
 *
 * @author caelis
 */
public class ClassId {
  private final long id;

  public ClassId(long id) {
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

    ClassId classId = (ClassId) o;

    if (id != classId.id) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
