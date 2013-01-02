package com.dcrux.buran.coredb.iface.query;

import com.dcrux.buran.coredb.iface.query.edgeCondition.OutEdgeCondition;
import com.google.common.base.Optional;

/**
 * @author caelis
 */
public class QEdge {
  public QEdge(OutEdgeCondition condition, Optional<IQNode> source) {
    this.condition = condition;
    this.source = source;
  }

  private final OutEdgeCondition condition;
  private final Optional<IQNode> source;

  public OutEdgeCondition getCondition() {
    return condition;
  }

  public Optional<IQNode> getSource() {
    return source;
  }
}
