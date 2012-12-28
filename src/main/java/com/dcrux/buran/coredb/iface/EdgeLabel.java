package com.dcrux.buran.coredb.iface;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 08.12.12
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
public class EdgeLabel implements Serializable {
  private final String label;

  private EdgeLabel(String label) {
    this.label = label;
    assert (this.label.length() >= 4);
    assert (this.label.length() <= 32);
  }

  public String getLabel() {
    return label;
  }

  public static EdgeLabel publicEdge(long id) {
    return new EdgeLabel("pub:" + Long.toHexString(id));
  }

  public static EdgeLabel privateEdge(String name) {
    return new EdgeLabel(":" + name);
  }

  public boolean isPublic() {
    return this.label.startsWith("pub:");
  }

  @Override
  public String toString() {
    return "EdgeLabel{" +
            "'" + label + "'" +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    EdgeLabel edgeLabel = (EdgeLabel) o;

    if (label != null ? !label.equals(edgeLabel.label) : edgeLabel.label != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return label != null ? label.hashCode() : 0;
  }
}
