package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * @author caelis
 */
public class VersionedEdTarget implements IIncEdgeTarget, IEdgeTarget {

  private final long nid;
  private final int version;

  @Override
  public EdgeTargetType getEdgeTargetType() {
    return EdgeTargetType.versioned;
  }

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.versioned;
  }

  public VersionedEdTarget(long nid, int version) {
    this.nid = nid;
    this.version = version;
  }

  public long getNid() {
    return nid;
  }

  public int getVersion() {
    return version;
  }
}
