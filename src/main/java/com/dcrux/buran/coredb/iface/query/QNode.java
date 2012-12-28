package com.dcrux.buran.coredb.iface.query;

import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.google.common.base.Optional;

/**
 *
 * @author caelis
 */
public class QNode implements IQNode {
  private final Optional<INodeMetaCondition> metaCondition;

  public QNode(Optional<INodeMetaCondition> metaCondition) {
    this.metaCondition = metaCondition;
  }

  public Optional<INodeMetaCondition> getMetaCondition() {
    return metaCondition;
  }
}
