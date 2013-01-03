package com.dcrux.buran.coredb.iface.api;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 03.01.13
 * Time: 00:38
 */
public class KeepAliveHint {
  private final short minNumberOfSeconds;

  public KeepAliveHint(short minNumberOfSeconds) {
    this.minNumberOfSeconds = minNumberOfSeconds;
  }

  public short getMinNumberOfSeconds() {
    return minNumberOfSeconds;
  }
}
