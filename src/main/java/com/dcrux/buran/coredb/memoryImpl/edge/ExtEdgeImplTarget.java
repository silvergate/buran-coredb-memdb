package com.dcrux.buran.coredb.memoryImpl.edge;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 27.12.12
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */
public class ExtEdgeImplTarget implements IEdgeImplTarget {
  private final long userId;
  private final long oid;
  @Nullable
  private final Integer version;

  public ExtEdgeImplTarget(long userId, long oid, @Nullable Integer version) {
    this.userId = userId;
    this.oid = oid;
    this.version = version;
  }

  public long getUserId() {
    return userId;
  }

  public long getOid() {
    return oid;
  }

  @Nullable
  public Integer getVersion() {
    return version;
  }
}
