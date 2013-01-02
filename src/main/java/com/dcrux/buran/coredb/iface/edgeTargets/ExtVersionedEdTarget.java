package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * @author caelis
 */
public class ExtVersionedEdTarget implements IIncEdgeTarget, IEdgeTarget {

  private final long oid;
  private final int version;
  private final long userId;

  @Override
  public EdgeTargetType getEdgeTargetType() {
    return EdgeTargetType.externalVersioned;
  }

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.externalUnversioned;
  }

  public ExtVersionedEdTarget(long userId, long oid, int version) {
    this.oid = oid;
    this.version = version;
    this.userId = userId;
  }

  public long getOid() {
    return oid;
  }

  public int getVersion() {
    return version;
  }

  public long getUserId() {
    return userId;
  }
}
