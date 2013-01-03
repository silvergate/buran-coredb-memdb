package com.dcrux.buran.coredb.iface.api;

import com.dcrux.buran.coredb.iface.IncNid;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 03.01.13
 * Time: 01:04
 */
public class CreateInfo {
  private final IncNid incNid;
  private final KeepAliveInfo keepAliveInfo;

  public CreateInfo(IncNid incNid, KeepAliveInfo keepAliveInfo) {
    this.incNid = incNid;
    this.keepAliveInfo = keepAliveInfo;
  }

  public IncNid getIncNid() {
    return incNid;
  }

  public KeepAliveInfo getKeepAliveInfo() {
    return keepAliveInfo;
  }
}
