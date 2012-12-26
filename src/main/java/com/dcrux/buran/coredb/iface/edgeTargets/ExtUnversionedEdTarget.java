package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 21.12.12
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class ExtUnversionedEdTarget implements IIncEdgeTarget, IEdgeTarget {

  private final long oid;
  private final long userId;

  @Override
  public EdgeTargetType getEdgeTargetType() {
    return EdgeTargetType.externalVersioned;
  }

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.externalUnversioned;
  }

  public ExtUnversionedEdTarget(long userId, long oid) {
    this.oid = oid;
    this.userId = userId;
  }

  public long getOid() {
    return oid;
  }

  public long getUserId() {
    return userId;
  }
}
