package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 21.12.12
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class IncUnversionedEdTarget implements IIncEdgeTarget {

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
