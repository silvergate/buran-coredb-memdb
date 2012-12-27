package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.edgeTargets.IEdgeTarget;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 27.12.12
 * Time: 21:52
 * To change this template use File | Settings | File Templates.
 */
public class EdgeWithSource {
  private final Edge edge;
  private IEdgeTarget source;

  public EdgeWithSource(Edge edge, IEdgeTarget source) {
    this.edge = edge;
    this.source = source;
  }

  public Edge getEdge() {
    return edge;
  }

  public IEdgeTarget getSource() {
    return source;
  }
}
