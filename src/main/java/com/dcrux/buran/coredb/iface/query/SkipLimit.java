package com.dcrux.buran.coredb.iface.query;

/**
 *
 * @author caelis
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
