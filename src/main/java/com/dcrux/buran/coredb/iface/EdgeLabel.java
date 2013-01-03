package com.dcrux.buran.coredb.iface;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author caelis
 */
public class EdgeLabel implements Serializable {
  private final String label;

  private EdgeLabel(String label) {
    this.label = label;
    if (isPublic()) {
      if (this.label.length() < 4) {
        throw new IllegalArgumentException("Label has to be at least 4 characters");
      }
      if (this.label.length() > 76) {
        throw new IllegalArgumentException("Label has to be at max 76 characters");
      }
    } else {
      if (this.label.length() < 4) {
        throw new IllegalArgumentException("Label has to be at least 4 (plus one for ':') characters");
      }
      if (this.label.length() > 31) {
        throw new IllegalArgumentException("Label has to be at max 31 (minus one for ':') characters");
      }
    }
  }

  public String getLabel() {
    return label;
  }

  public static EdgeLabel publicEdge(String data) {
    return new EdgeLabel(MessageFormat.format("pub:{0}", data));
  }

  public static EdgeLabel privateEdge(String name) {
    return new EdgeLabel(MessageFormat.format(":{0}", name));
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
