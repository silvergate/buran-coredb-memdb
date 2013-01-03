package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * @author caelis
 */
public class IncUnversionedEdTarget implements IIncEdgeTarget {

  //TODO: Die Incs-targets kommen vermutlich in die implementierung

  private final long inid;

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.unversionedInc;
  }

  public IncUnversionedEdTarget(long inid, int version) {
    this.inid = inid;
  }

  public long getInid() {
    return inid;
  }
}
