package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 21.12.12
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
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
