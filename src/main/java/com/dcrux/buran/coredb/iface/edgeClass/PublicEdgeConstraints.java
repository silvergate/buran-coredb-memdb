package com.dcrux.buran.coredb.iface.edgeClass;

/**
 * @author caelis
 */
public enum PublicEdgeConstraints {
  /**
   * Keiner ist ok, genau einer ist auch ok.
   */
  maxOne('O'),
  /**
   * Keiner ist ok, einer ist ok, 2 und mehr sind auch ok.
   */
  many('M');

  private char value;

  private PublicEdgeConstraints(char value) {
    this.value = value;
  }

  public char getValue() {
    return value;
  }

  public static PublicEdgeConstraints fromValue(char value) {
    if (value == maxOne.getValue()) {
      return maxOne;
    }
    if (value == many.getValue()) {
      return many;
    }
    throw new IllegalArgumentException("Unknown value");
  }
}
