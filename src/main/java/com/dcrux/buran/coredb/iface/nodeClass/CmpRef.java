package com.dcrux.buran.coredb.iface.nodeClass;

/**
 * @author caelis
 */
public class CmpRef {
  private final short id;

  public CmpRef(short id) {
    this.id = id;
  }

  public short getId() {
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

    CmpRef that = (CmpRef) o;

    if (id != that.id) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (int) id;
  }
}
