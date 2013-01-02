package com.dcrux.buran.coredb.iface.api;

/**
 * @author caelis
 */
public class InformationUnavailableException extends UnexpectableException {
  private final UnavailabilityReason tempUnavailable;

  public InformationUnavailableException(UnavailabilityReason tempUnavailable) {
    this.tempUnavailable = tempUnavailable;
  }

  public UnavailabilityReason isTempUnavailable() {
    return tempUnavailable;
  }
}
