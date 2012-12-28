package com.dcrux.buran.coredb.iface.nodeClass;

/**
 *
 * @author caelis
 */
public class NodeClassHash {
  private final String hash;

  public NodeClassHash(String hash) {
    this.hash = hash;
  }

  public String getHash() {
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NodeClassHash that = (NodeClassHash) o;

    if (!hash.equals(that.hash)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }
}
