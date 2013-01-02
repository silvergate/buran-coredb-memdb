package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * @author caelis
 */
public class IncVersionedEdTarget implements IIncEdgeTarget {

  //TODO: Die Incs-targets kommen vermutlich in die implementierung

  private final long ioid;

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.versionedInc;
  }

  public IncVersionedEdTarget(long ioid) {
    this.ioid = ioid;
  }

  public long getIoid() {
    return ioid;
  }
}
