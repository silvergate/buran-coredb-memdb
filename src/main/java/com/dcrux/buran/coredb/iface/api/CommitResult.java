package com.dcrux.buran.coredb.iface.api;

import com.dcrux.buran.coredb.iface.IncOid;
import com.dcrux.buran.coredb.iface.OidVersion;

import java.util.Map;

/**
 * @author caelis
 */
public class CommitResult {
  public CommitResult(Map<IncOid, OidVersion> incOidToOidVer) {
    this.incOidToOidVer = incOidToOidVer;
  }

  private final Map<IncOid, OidVersion> incOidToOidVer;

  public OidVersion getOidVers(IncOid incoid) {
    final OidVersion oidVer = this.incOidToOidVer.get(incoid);
    if (oidVer == null) {
      throw new IllegalArgumentException("Given incOid not found.");
    }
    return oidVer;
  }
}
