package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.Edge;
import com.dcrux.buran.coredb.iface.EdgeIndex;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 19:51
 * To change this template use File | Settings | File Templates.
 */
public class EdgeWithIndex {
  private final EdgeIndex index;
  private final Edge edge;

  public EdgeWithIndex(EdgeIndex index, Edge edge) {
    this.index = index;
    this.edge = edge;
  }

  public EdgeIndex getIndex() {
    return index;
  }

  public Edge getEdge() {
    return edge;
  }
}
