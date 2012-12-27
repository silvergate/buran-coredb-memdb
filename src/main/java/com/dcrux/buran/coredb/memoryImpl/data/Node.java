package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 01:08
 * To change this template use File | Settings | File Templates.
 */
//TODO: Rename to NodeImpl
public class Node {

  public long getSenderId() {
    return senderId;
  }

  public long getValidFrom() {
    return validFrom;
  }

  public long getValidTo() {
    return validTo;
  }

  public Object[] getData() {
    return data;
  }

  private final long senderId;
  private long validFrom;
  private long validTo;
  private final Object[] data;
  private final Set<Long> domainIds = new HashSet<>();
  private NodeSerie nodeSerie;
  private final int version;

  private final Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> outEdges = new HashMap<>();
  private final Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> versionedInEdgeds = new HashMap<>();

  public Node(int version, long senderId, long receiverId, long validFrom, long validTo, Object[] data) {
    this.senderId = senderId;
    this.validFrom = validFrom;
    this.validTo = validTo;
    this.data = data;
    this.version = version;
  }

  public int getVersion() {
    return version;
  }

  public void setNodeSerie(NodeSerie nodeSerie) {
    this.nodeSerie = nodeSerie;
  }

  public NodeSerie getNodeSerie() {
    return nodeSerie;
  }

  public Set<Long> getDomainIds() {
    return domainIds;
  }

  void addOutEdge(EdgeIndex index, EdgeImpl edgeImpl) {
    Map<EdgeIndex, EdgeImpl> element = this.outEdges.get(edgeImpl.getLabel());
    if (element == null) {
      element = new HashMap<>();
      this.outEdges.put(edgeImpl.getLabel(), element);
    }
    element.put(index, edgeImpl);
  }

  void addVersionedInEdge(EdgeIndex index, EdgeImpl edgeImpl) {
    Map<EdgeIndex, EdgeImpl> element = this.versionedInEdgeds.get(edgeImpl.getLabel());
    if (element == null) {
      element = new HashMap<>();
      this.versionedInEdgeds.put(edgeImpl.getLabel(), element);
    }
    element.put(index, edgeImpl);
  }

  public void setValidFrom(long validFrom) {
    this.validFrom = validFrom;
  }

  public void setValidTo(long validTo) {
    this.validTo = validTo;
  }

  public Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> getOutEdges() {
    return outEdges;
  }

  public Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> getVersionedInEdgeds() {
    return versionedInEdgeds;
  }
}
