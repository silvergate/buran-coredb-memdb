package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.memoryImpl.data.Node;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 29.12.12
 * Time: 16:46
 */
public class EdgeImplWithSource {
  private final EdgeImpl edgeImpl;
  private final Node source;

  public EdgeImplWithSource(EdgeImpl edgeImpl, Node source) {
    this.edgeImpl = edgeImpl;
    this.source = source;
  }

  public EdgeImpl getEdgeImpl() {
    return edgeImpl;
  }

  public Node getSource() {
    return source;
  }
}
