package com.dcrux.buran.coredb.iface;

/**
 * @author caelis
 */
public class NidVer {
  private final long oid;
  private final int version;

  public long getOid() {
    return oid;
  }

  public int getVersion() {
    return version;
  }

  public NidVer(long oid, int version) {
    this.oid = oid;
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NidVer that = (NidVer) o;

    if (oid != that.oid) {
      return false;
    }
    if (version != that.version) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (oid ^ (oid >>> 32));
    result = 31 * result + version;
    return result;
  }

  @Override
  public String toString() {
    return "NidVer{" +
            "oid=" + oid +
            ", version=" + version +
            '}';
  }
}
