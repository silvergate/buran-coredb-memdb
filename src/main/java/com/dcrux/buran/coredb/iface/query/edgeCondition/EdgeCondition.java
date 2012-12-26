package com.dcrux.buran.coredb.iface.query.edgeCondition;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.query.IQNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class EdgeCondition implements INodeMetaCondition {
  private final EdgeDirection direction;
  private final EdgeLabel label;
  private final Optional<EdgeIndex> index;
  private final Optional<IQNode> target;

  public EdgeCondition(EdgeDirection direction, EdgeLabel label, Optional<EdgeIndex> index, Optional<IQNode> target) {
    this.direction = direction;
    this.label = label;
    this.index = index;
    this.target = target;
  }

  public EdgeDirection getDirection() {
    return direction;
  }

  public EdgeLabel getLabel() {
    return label;
  }

  public Optional<EdgeIndex> getIndex() {
    return index;
  }

  public Optional<IQNode> getTarget() {
    return target;
  }
}
