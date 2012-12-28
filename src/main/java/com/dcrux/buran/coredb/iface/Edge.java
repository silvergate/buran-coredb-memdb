package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.edgeTargets.IEdgeTarget;

/**
 *
 * @author caelis
 */
public class Edge {
  private IEdgeTarget target;
  private final EdgeLabel label;

  public Edge(IEdgeTarget target, EdgeLabel label) {
    this.target = target;
    this.label = label;
  }

  public IEdgeTarget getTarget() {
    return target;
  }

  public EdgeLabel getLabel() {
    return label;
  }

  public void setTarget(IEdgeTarget target) {
    this.target = target;
  }
}
