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
public class PublicEdgeClass extends EdgeClass {

  // TODO: Hier müssen unbedingt mehr infos rein, sonst kann kein sinnvoller hash berechnet werden.

  public PublicEdgeClass(String label, boolean queryable, Optional<ClassId> outEdgeClass,
                         PublicEdgeConstraints outNodeConstraints, Optional<ClassId> inEdgeClass) {
    super(label, queryable, inEdgeClass);
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
