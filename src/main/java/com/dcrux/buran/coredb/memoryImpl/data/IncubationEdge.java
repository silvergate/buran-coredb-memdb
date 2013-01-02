package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;

/**
 * @author caelis
 */
public class IncubationEdge {

  /* Target */
  private final IIncEdgeTarget target;

  private final EdgeLabel label;

  public IncubationEdge(IIncEdgeTarget target, EdgeLabel label) {
    this.target = target;
    this.label = label;
  }

  public IIncEdgeTarget getTarget() {
    return target;
  }

  public EdgeLabel getLabel() {
    return label;
  }
}
