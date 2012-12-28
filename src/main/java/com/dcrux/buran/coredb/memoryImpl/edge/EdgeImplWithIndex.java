package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.iface.EdgeIndex;

/**
 *
 * @author caelis
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
