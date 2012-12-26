package com.dcrux.buran.coredb.iface.edgeClass;

import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 19.12.12
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
public class PrivateEdgeClass extends EdgeClass {

  public PrivateEdgeClass(String label, boolean queryable, Optional<ClassId> outEdgeClass,
                          PrivateEdgeConstraints outNodeConstraints) {
    super(label, queryable, outEdgeClass);
    this.outNodeConstraints = outNodeConstraints;
  }

  private final PrivateEdgeConstraints outNodeConstraints;

  public PrivateEdgeConstraints getOutNodeConstraints() {
    return outNodeConstraints;
  }
}
