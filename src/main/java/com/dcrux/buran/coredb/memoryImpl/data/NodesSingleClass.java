package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.OidVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 01:13
 * To change this template use File | Settings | File Templates.
 */
public class NodesSingleClass {
  private final Map<OidVersion, Node> current = new HashMap<>();
  private final Map<OidVersion, Node> currentAndHistorized = new HashMap<>();
  private final Map<Long, IncNode> incOidToIncubationNode = new HashMap<>();

  public Map<OidVersion, Node> getCurrent() {
    return current;
  }

  public Map<Long, IncNode> getIncOidToIncubationNode() {
    return incOidToIncubationNode;
  }

  public Map<OidVersion, Node> getCurrentAndHistorized() {
    return currentAndHistorized;
  }
}
