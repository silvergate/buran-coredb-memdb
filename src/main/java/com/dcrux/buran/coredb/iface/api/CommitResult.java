package com.dcrux.buran.coredb.iface.api;

import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.NidVer;

import java.util.Map;

/**
 * @author caelis
 */
public class CommitResult {
  public CommitResult(Map<IncNid, NidVer> incOidToOidVer) {
    this.incOidToOidVer = incOidToOidVer;
  }

  private final Map<IncNid, NidVer> incOidToOidVer;

  public NidVer getNid(IncNid incoid) {
    final NidVer oidVer = this.incOidToOidVer.get(incoid);
    if (oidVer == null) {
      throw new IllegalArgumentException("Given incOid not found.");
    }
    return oidVer;
  }
}
