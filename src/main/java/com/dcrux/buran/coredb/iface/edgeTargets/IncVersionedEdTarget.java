package com.dcrux.buran.coredb.iface.edgeTargets;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 21.12.12
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class IncVersionedEdTarget implements IIncEdgeTarget {

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
