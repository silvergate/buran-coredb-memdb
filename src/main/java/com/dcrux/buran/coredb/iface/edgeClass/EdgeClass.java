package com.dcrux.buran.coredb.iface.edgeClass;

import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.google.common.base.Optional;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 19.12.12
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class EdgeClass implements Serializable {
  private final String label;
  private final boolean queryable;
  private final Optional<ClassId> inEdgeClass;

  public EdgeClass(String label, boolean queryable, Optional<ClassId> inEdgeClass) {
    this.label = label;
    this.queryable = queryable;
    this.inEdgeClass = inEdgeClass;
    assert (this.label.length() >= 4);
    assert (this.label.length() <= 32);
  }

  public boolean isQueryable() {
    return queryable;
  }

  public String getLabel() {
    return label;
  }

  public Optional<ClassId> getInEdgeClass() {
    return inEdgeClass;
  }
}
