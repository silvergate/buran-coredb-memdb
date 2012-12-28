package com.dcrux.buran.coredb.iface.edgeClass;

import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.google.common.base.Optional;

/**
 *
 * @author caelis
 */
public class PrivateEdgeClass extends EdgeClass {

  public PrivateEdgeClass(EdgeLabel label, boolean queryable, Optional<ClassId> inEdgeClass,
                          PrivateEdgeConstraints outNodeConstraints) {
    super(label, queryable, inEdgeClass);
    assert (!label.isPublic());
    this.outNodeConstraints = outNodeConstraints;
  }

  public static PrivateEdgeClass cQueryable(EdgeLabel label) {
    return new PrivateEdgeClass(label, true, Optional.<ClassId>absent(), PrivateEdgeConstraints.many);
  }

  private final PrivateEdgeConstraints outNodeConstraints;

  public PrivateEdgeConstraints getOutNodeConstraints() {
    return outNodeConstraints;
  }
}
