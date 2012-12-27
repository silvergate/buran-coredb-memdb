package com.dcrux.buran.coredb.iface.query;

import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.IPropertyCondition;
import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 21:56
 * To change this template use File | Settings | File Templates.
 */
public class QCdNode extends QNode {
  private final long classId;
  private final Optional<IPropertyCondition> propertyCondition;

  public QCdNode(Optional<INodeMetaCondition> metaCondition, long classId,
                 Optional<IPropertyCondition> propertyCondition) {
    super(metaCondition);
    this.classId = classId;
    this.propertyCondition = propertyCondition;
  }

  public long getClassId() {
    return classId;
  }

  public Optional<IPropertyCondition> getPropertyCondition() {
    return propertyCondition;
  }
}
