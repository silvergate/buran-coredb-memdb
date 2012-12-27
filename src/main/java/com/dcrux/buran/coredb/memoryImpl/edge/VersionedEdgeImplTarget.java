package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.memoryImpl.data.Node;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 27.12.12
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
public class VersionedEdgeImplTarget implements IEdgeImplTarget {
  private final Node target;

  public VersionedEdgeImplTarget(Node target) {
    this.target = target;
  }

  public Node getTarget() {
    return target;
  }
}
