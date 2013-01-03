package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * @author caelis
 */
public class UnversionedEdTarget implements IIncEdgeTarget, IEdgeTarget {

  private final long nid;

  @Override
  public EdgeTargetType getEdgeTargetType() {
    return EdgeTargetType.unversioned;
  }

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.unversioned;
  }

  public UnversionedEdTarget(long nid) {
    this.nid = nid;
  }

  public long getNid() {
    return nid;
  }
}
