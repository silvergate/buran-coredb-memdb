package com.dcrux.buran.coredb.iface.edgeClass;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 20.12.12
 * Time: 20:36
 * To change this template use File | Settings | File Templates.
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
