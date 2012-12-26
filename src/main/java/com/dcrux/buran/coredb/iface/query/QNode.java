package com.dcrux.buran.coredb.iface.query;

import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 21:56
 * To change this template use File | Settings | File Templates.
 */
public class QNode {
  private final INodeMetaCondition metaCondition;

  public QNode(INodeMetaCondition metaCondition) {
    this.metaCondition = metaCondition;
  }

  public INodeMetaCondition getMetaCondition() {
    return metaCondition;
  }
}
