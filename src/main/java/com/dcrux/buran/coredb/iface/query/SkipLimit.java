package com.dcrux.buran.coredb.iface.query;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 17.12.12
 * Time: 00:30
 * To change this template use File | Settings | File Templates.
 */
public class SkipLimit {
  private final int skip;
  private final int limit;

  public SkipLimit(int skip, int limit) {
    this.skip = skip;
    this.limit = limit;
  }

  public int getSkip() {
    return skip;
  }

  public int getLimit() {
    return limit;
  }
}
