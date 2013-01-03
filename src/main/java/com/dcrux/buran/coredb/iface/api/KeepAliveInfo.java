package com.dcrux.buran.coredb.iface.api;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 03.01.13
 * Time: 01:00
 */
public class KeepAliveInfo {
  private final short minNumberOfSeconds;
  private final long minTimeMs;

  public short getMinNumberOfSeconds() {
    return minNumberOfSeconds;
  }

  public long getMinTimeMs() {
    return minTimeMs;
  }

  public KeepAliveInfo(short minNumberOfSeconds, long minTimeMs) {
    this.minNumberOfSeconds = minNumberOfSeconds;
    this.minTimeMs = minTimeMs;
  }
}
