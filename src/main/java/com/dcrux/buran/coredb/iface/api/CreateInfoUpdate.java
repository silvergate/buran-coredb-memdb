package com.dcrux.buran.coredb.iface.api;

import com.dcrux.buran.coredb.iface.IncNid;

/**
 * Buran.
 *
 * @author: ${USER}
 * Date: 03.01.13
 * Time: 01:05
 */
public class CreateInfoUpdate {
  private final CreateInfo createInfo;
  private final HistoryInformation historyInformation;

  public IncNid getIncNid() {
    return this.createInfo.getIncNid();
  }

  public CreateInfo getCreateInfo() {
    return createInfo;
  }

  public HistoryInformation getHistoryInformation() {
    return historyInformation;
  }

  public CreateInfoUpdate(CreateInfo createInfo, HistoryInformation historyInformation) {
    this.createInfo = createInfo;
    this.historyInformation = historyInformation;
  }
}
