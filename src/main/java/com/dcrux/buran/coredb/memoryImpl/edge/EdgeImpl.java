package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.iface.EdgeLabel;

/**
 * @author caelis
 */
public class EdgeImpl {
  private final EdgeLabel label;
  private final IEdgeImplTarget source;
  private final IEdgeImplTarget target;

  public EdgeImpl(EdgeLabel label, IEdgeImplTarget source, IEdgeImplTarget target) {
    this.label = label;
    this.source = source;
    this.target = target;
  }

  public EdgeLabel getLabel() {
    return label;
  }

  public IEdgeImplTarget getSource() {
    return source;
  }

  public IEdgeImplTarget getTarget() {
    return target;
  }
}
