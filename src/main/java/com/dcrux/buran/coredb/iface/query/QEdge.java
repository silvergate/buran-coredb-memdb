package com.dcrux.buran.coredb.iface.query;

import com.dcrux.buran.coredb.iface.query.edgeCondition.OutEdgeCondition;
import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 17.12.12
 * Time: 00:58
 * To change this template use File | Settings | File Templates.
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
