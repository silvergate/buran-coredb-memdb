package com.dcrux.buran.coredb.iface.edgeClass;

/**
 *
 * @author caelis
 */
public enum PrivateEdgeConstraints {
  /**
   * Erlaubt ist nur eine (und die ist erforderlich).
   */
  exactOne,
  /**
   * Erlaubt ist eine oder kein.
   */
  oneOrNone,
  /**
   * Keine ist ok, eine ist ok, mehrere sind auch ok.
   */
  many,
  /**
   * Einer ist ok, 2 oder mehr sind auch ok (keiner hingegen nicht).
   */
  manyAtLeastOne
}
