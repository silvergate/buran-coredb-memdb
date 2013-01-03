package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.NidVer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caelis
 */
public class IncNode {

  // TODO: Gehört nicht ins interface!

  @Nullable
  private final NidVer toUpdate;
  private final NodeImpl node;
  private final long receiverId;
  private final long classId;
  private final Map<EdgeIndexLabel, IncubationEdge> incubationEdges = new HashMap<>();

  public IncNode(@Nullable NidVer toUpdate, NodeImpl node, long receiverId, long classId) {
    this.toUpdate = toUpdate;
    this.node = node;
    this.receiverId = receiverId;
    this.classId = classId;
  }

  public long getClassId() {
    return classId;
  }

  public long getReceiverId() {
    return receiverId;
  }

  @Nullable
  public NidVer getToUpdate() {
    return toUpdate;
  }

  public NodeImpl getNode() {
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
