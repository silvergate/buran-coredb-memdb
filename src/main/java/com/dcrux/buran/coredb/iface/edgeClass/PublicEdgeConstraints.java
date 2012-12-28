package com.dcrux.buran.coredb.iface.edgeClass;

/**
 *
 * @author caelis
 */
public enum PublicEdgeConstraints {
  /**
   * Keiner ist ok, genau einer ist auch ok.
   */
  maxOne,
  /**
   * Keiner ist ok, einer ist ok, 2 und mehr sind auch ok.
   */
  many
}
