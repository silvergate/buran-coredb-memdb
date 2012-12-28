package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 *
 * @author caelis
 */
public class VersionedEdTarget implements IIncEdgeTarget, IEdgeTarget {

  private final long oid;
  private final int version;

  @Override
  public EdgeTargetType getEdgeTargetType() {
    return EdgeTargetType.versioned;
  }

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.versioned;
  }

  public VersionedEdTarget(long oid, int version) {
    this.oid = oid;
    this.version = version;
  }

  public long getOid() {
    return oid;
  }

  public int getVersion() {
    return version;
  }
}
