package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * @author caelis
 */
public class ExtUnversionedEdTarget implements IIncEdgeTarget, IEdgeTarget {

  private final long nid;
  private final long userId;

  @Override
  public EdgeTargetType getEdgeTargetType() {
    return EdgeTargetType.externalVersioned;
  }

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.externalUnversioned;
  }

  public ExtUnversionedEdTarget(long userId, long nid) {
    this.nid = nid;
    this.userId = userId;
  }

  public long getNid() {
    return nid;
  }

  public long getUserId() {
    return userId;
  }
}
