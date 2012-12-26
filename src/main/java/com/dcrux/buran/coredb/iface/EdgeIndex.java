package com.dcrux.buran.coredb.iface;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 08.12.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class EdgeIndex {

  public static final EdgeIndex BASIS = new EdgeIndex(0);

  private final int id;

  public EdgeIndex(int id) {
    this.id = id;
  }

  public int getId() {
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

    EdgeIndex edgeIndex = (EdgeIndex) o;

    if (id != edgeIndex.id) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
