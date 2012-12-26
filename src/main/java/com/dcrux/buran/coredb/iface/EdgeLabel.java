package com.dcrux.buran.coredb.iface;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 08.12.12
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
public class EdgeLabel {
  private final String label;

  public EdgeLabel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static EdgeLabel publicEdge(long id) {
    return new EdgeLabel("reg:" + Long.toHexString(id));
  }

  public static EdgeLabel privateEdge(String name) {
    return new EdgeLabel("ur:" + name);
  }

  public boolean isPublic() {
    return this.label.startsWith("reg:");
  }
}
