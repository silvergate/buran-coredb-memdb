package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 27.12.12
 * Time: 03:58
 * To change this template use File | Settings | File Templates.
 */
public class NodeSerie {
  private final long oid;
  private final long classId;
  private final long receiverId;
  private int currentVersion = Integer.MIN_VALUE;

  public static final int FIRST_VERSION = Integer.MIN_VALUE + 1;

  public NodeSerie(long oid, long classId, long receiverId) {
    this.oid = oid;
    this.classId = classId;
    this.receiverId = receiverId;
  }

  private final Map<Integer, Node> versionToNode = new HashMap<>();
  private final Map<Node, Integer> nodeToVersion = new HashMap<>();

  private final Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> inEdges = new HashMap<>();

  public Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> getInEdges() {
    return inEdges;
  }

  void addInEdge(EdgeIndex index, EdgeImpl edgeImpl) {
    Map<EdgeIndex, EdgeImpl> edges = this.inEdges.get(edgeImpl.getLabel());
    if (edges == null) {
      edges = new HashMap<>();
      this.inEdges.put(edgeImpl.getLabel(), edges);
    }
    edges.put(index, edgeImpl);
  }

  void addNewVersion(Node node) {
    /* Get node to replace - if any */
    final Node nodeToReplace;
    if (getCurrentVersion() != null) {
      nodeToReplace = getNode(getCurrentVersion());
    } else {
      nodeToReplace = null;
    }

    int newVersion;
    if (getCurrentVersion() == null) {
      newVersion = getFirstVersion();
    } else {
      newVersion = getCurrentVersion() + 1;
    }

    final long validFrom = node.getValidFrom();
    if (nodeToReplace != null) {
      nodeToReplace.setValidTo(validFrom - 1);
    }

    this.currentVersion = newVersion;
    this.versionToNode.put(newVersion, node);
    this.nodeToVersion.put(node, newVersion);
  }

  public int getFirstVersion() {
    return Integer.MIN_VALUE + 1;
  }

  public Node getNode(int version) {
    final Node node = this.versionToNode.get(version);
    assert (node != null);
    return node;
  }

  @Nullable
  public Integer getCurrentVersion() {
    if (this.currentVersion == Integer.MIN_VALUE) {
      return null;
    }
    return this.currentVersion;
  }

  public boolean isMarkedAsDeleted() {
    return ((this.currentVersion == Integer.MIN_VALUE) && (!this.versionToNode.isEmpty()));
  }

  void markAsDeleted(final long currentTime) {
    assert (!this.versionToNode.isEmpty());
    this.currentVersion = Integer.MIN_VALUE;
    final Node node = getNode(getCurrentVersion());
    node.setValidTo(currentTime);
  }

  public long getOid() {
    return oid;
  }

  public long getClassId() {
    return classId;
  }

  public long getReceiverId() {
    return receiverId;
  }
}
