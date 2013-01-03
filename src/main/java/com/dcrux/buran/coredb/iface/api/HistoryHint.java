package com.dcrux.buran.coredb.iface.api;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 03.01.13
 * Time: 00:48
 */
public class HistoryHint {
  private final HistoryFunction function;
  private final boolean allowKeepMore;
  private final boolean allowKeepLess;

  public HistoryHint(HistoryFunction function, boolean allowKeepMore, boolean allowKeepLess) {
    this.function = function;
    this.allowKeepMore = allowKeepMore;
    this.allowKeepLess = allowKeepLess;
  }

  public HistoryFunction getFunction() {
    return function;
  }

  public boolean isAllowKeepMore() {
    return allowKeepMore;
  }

  public boolean isAllowKeepLess() {
    return allowKeepLess;
  }
}
