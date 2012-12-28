package com.dcrux.buran.coredb.iface.query.propertyCondition;

import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;

/**
 *
 * @author caelis
 */
public interface IPropertyCondition {
  boolean matches(Object[] data, NodeClass nodeClass);
}
