package com.dcrux.buran.coredb.iface.nodeClass;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 19.12.12
 * Time: 21:07
 * To change this template use File | Settings | File Templates.
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
