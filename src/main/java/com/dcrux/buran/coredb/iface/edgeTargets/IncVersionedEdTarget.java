package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * @author caelis
 */
public class IncVersionedEdTarget implements IIncEdgeTarget {

  //TODO: Die Incs-targets kommen vermutlich in die implementierung

  private final long inid;

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.versionedInc;
  }

  public IncVersionedEdTarget(long inid) {
    this.inid = inid;
  }

  public long getInid() {
    return inid;
  }
}
