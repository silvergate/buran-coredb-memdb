package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 *
 * @author caelis
 */
public class IncUnversionedEdTarget implements IIncEdgeTarget {

  //TODO: Die Incs-targets kommen vermutlich in die implementierung

  private final long ioid;

  @Override
  public IncEdgeTargetType getIncType() {
    return IncEdgeTargetType.unversionedInc;
  }

  public IncUnversionedEdTarget(long ioid, int version) {
    this.ioid = ioid;
  }

  public long getIoid() {
    return ioid;
  }
}
