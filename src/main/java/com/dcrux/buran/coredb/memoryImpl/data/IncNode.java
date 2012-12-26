package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.OidVersion;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 01:26
 * To change this template use File | Settings | File Templates.
 */
public class IncNode {

  // TODO: Gehört nicht ins interface!

  @Nullable
  private final OidVersion toUpdate;
  private final Node node;
  private final long receiverId;
  private final Map<EdgeIndexLabel, IncubationEdge> incubationEdges = new HashMap<>();

  public IncNode(@Nullable OidVersion toUpdate, Node node, long receiverId) {
    this.toUpdate = toUpdate;
    this.node = node;
    this.receiverId = receiverId;
  }

  public long getReceiverId() {
    return receiverId;
  }

  @Nullable
  public OidVersion getToUpdate() {
    return toUpdate;
  }

  public Node getNode() {
    return node;
  }

  public Map<EdgeIndexLabel, IncubationEdge> getIncubationEdges() {
    return incubationEdges;
  }

  public static class EdgeIndexLabel {
    private final EdgeLabel label;
    private final EdgeIndex index;

    public EdgeLabel getLabel() {
      return label;
    }

    public EdgeIndex getIndex() {
      return index;
    }

    public EdgeIndexLabel(EdgeLabel label, EdgeIndex index) {
      this.label = label;
      this.index = index;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      EdgeIndexLabel that = (EdgeIndexLabel) o;

      if (!index.equals(that.index)) {
        return false;
      }
      if (!label.equals(that.label)) {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int result = label.hashCode();
      result = 31 * result + index.hashCode();
      return result;
    }
  }
}
