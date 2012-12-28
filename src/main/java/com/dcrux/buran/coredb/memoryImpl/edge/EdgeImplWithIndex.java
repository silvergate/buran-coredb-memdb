package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.iface.EdgeIndex;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 28.12.12
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class EdgeImplWithIndex {
  private final EdgeImpl edge;
  private final EdgeIndex index;

  public EdgeImplWithIndex(EdgeImpl edge, EdgeIndex index) {
    this.edge = edge;
    this.index = index;
  }

  public EdgeImpl getEdge() {
    return edge;
  }

  public EdgeIndex getIndex() {
    return index;
  }
}
