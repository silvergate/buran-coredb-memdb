package com.dcrux.buran.coredb.memoryImpl.data;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 25.12.12
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class NodeWithClassId {
  private final Node node;
  private final long classId;

  public Node getNode() {
    return node;
  }

  public long getClassId() {
    return classId;
  }

  public NodeWithClassId(Node node, long classId) {
    this.node = node;
    this.classId = classId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NodeWithClassId that = (NodeWithClassId) o;

    if (classId != that.classId) {
      return false;
    }
    if (!node.equals(that.node)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = node.hashCode();
    result = 31 * result + (int) (classId ^ (classId >>> 32));
    return result;
  }
}
