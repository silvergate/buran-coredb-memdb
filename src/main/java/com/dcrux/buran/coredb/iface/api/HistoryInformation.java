package com.dcrux.buran.coredb.iface.api;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 03.01.13
 * Time: 00:57
 */
public class HistoryInformation {
  private final HistoryFunction function;

  public HistoryInformation(HistoryFunction function) {
    this.function = function;
  }

  public HistoryFunction getFunction() {
    return function;
  }
}
