package com.dcrux.buran.coredb.iface;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 07.12.12
 * Time: 20:13
 * To change this template use File | Settings | File Templates.
 */
public class OidVersion {
  private final long oid;
  private final int version;

  public long getOid() {
    return oid;
  }

  public int getVersion() {
    return version;
  }

  public OidVersion(long oid, int version) {
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

    OidVersion that = (OidVersion) o;

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
    return "OidVersion{" +
            "oid=" + oid +
            ", version=" + version +
            '}';
  }
}
