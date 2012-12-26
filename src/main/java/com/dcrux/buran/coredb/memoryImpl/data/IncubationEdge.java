package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.edgeTargets.IIncEdgeTarget;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 10.12.12
 * Time: 00:28
 * To change this template use File | Settings | File Templates.
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
