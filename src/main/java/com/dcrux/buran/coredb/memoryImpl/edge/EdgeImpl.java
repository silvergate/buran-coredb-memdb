package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.iface.EdgeLabel;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 27.12.12
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
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
