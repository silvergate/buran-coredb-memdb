package com.dcrux.buran.coredb.iface.edgeClass;

import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.google.common.base.Optional;

/**
 * @author caelis
 */
public class PublicEdgeClass extends EdgeClass {

  // TODO: Hier müssen unbedingt mehr infos rein, sonst kann kein sinnvoller hash berechnet werden.

  public PublicEdgeClass(EdgeLabel label, boolean queryable, Optional<ClassId> outEdgeClass,
                         PublicEdgeConstraints outNodeConstraints, Optional<ClassId> inEdgeClass) {
    super(label, queryable, inEdgeClass);
    assert (label.isPublic());
    this.outNodeConstraints = outNodeConstraints;
    this.outEdgeClass = outEdgeClass;
  }

  private final PublicEdgeConstraints outNodeConstraints;
  private final Optional<ClassId> outEdgeClass;

  public PublicEdgeConstraints getOutNodeConstraints() {
    return outNodeConstraints;
  }

  public Optional<ClassId> getOutEdgeClass() {
    return outEdgeClass;
  }
}
