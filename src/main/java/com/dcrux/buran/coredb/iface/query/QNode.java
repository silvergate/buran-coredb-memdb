package com.dcrux.buran.coredb.iface.query;

import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 21:56
 * To change this template use File | Settings | File Templates.
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
