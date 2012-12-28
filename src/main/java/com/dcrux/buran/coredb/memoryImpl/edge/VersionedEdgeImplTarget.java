package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.memoryImpl.data.Node;

/**
 *
 * @author caelis
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
