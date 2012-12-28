package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 *
 * @author caelis
 */
public class UnversionedEdTarget implements IIncEdgeTarget, IEdgeTarget {

  private final long oid;

  @Override
  public EdgeTargetType getEdgeTargetType() {
    return EdgeTargetType.unversioned;
  }

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.unversioned;
  }

  public UnversionedEdTarget(long oid) {
    this.oid = oid;
  }

  public long getOid() {
    return oid;
  }
}
