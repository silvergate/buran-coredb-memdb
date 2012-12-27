package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 27.12.12
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class UnversionedEdgeImplTarget implements IEdgeImplTarget {
  private final NodeSerie target;

  public UnversionedEdgeImplTarget(NodeSerie target) {
    this.target = target;
  }

  public NodeSerie getTarget() {
    return target;
  }
}
