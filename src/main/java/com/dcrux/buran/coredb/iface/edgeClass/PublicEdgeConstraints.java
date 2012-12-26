package com.dcrux.buran.coredb.iface.edgeClass;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 20.12.12
 * Time: 20:36
 * To change this template use File | Settings | File Templates.
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
