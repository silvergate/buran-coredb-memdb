package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.edgeTargets.IEdgeTarget;

/**
 *
 * @author caelis
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
