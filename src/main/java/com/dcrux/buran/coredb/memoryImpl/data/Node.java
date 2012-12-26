package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.Edge;
import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;

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

  private final Map<EdgeLabel, Map<EdgeIndex, Edge>> privateEdges = new HashMap<>();
  private final Map<EdgeLabel, Map<EdgeIndex, Edge>> publicEdges = new HashMap<>();

  public Node(long senderId, long validFrom, long validTo, Object[] data) {
    this.senderId = senderId;
    this.validFrom = validFrom;
    this.validTo = validTo;
    this.data = data;
  }

  public Set<Long> getDomainIds() {
    return domainIds;
  }

  private void addEdge(EdgeWithIndex edgeWithIndex, Map<EdgeLabel, Map<EdgeIndex, Edge>> edges) {
    Map<EdgeIndex, Edge> element = edges.get(edgeWithIndex.getEdge().getLabel());
    if (element == null) {
      element = new HashMap<>();
      edges.put(edgeWithIndex.getEdge().getLabel(), element);
    }
    element.put(edgeWithIndex.getIndex(), edgeWithIndex.getEdge());
  }

  public void addEdge(EdgeWithIndex edgeWithIndex) {
    if (edgeWithIndex.getEdge().getLabel().isPublic()) {
      addEdge(edgeWithIndex, this.publicEdges);
    } else {
      addEdge(edgeWithIndex, this.privateEdges);
    }
  }

  public void setValidFrom(long validFrom) {
    this.validFrom = validFrom;
  }

  public void setValidTo(long validTo) {
    this.validTo = validTo;
  }

  public Map<EdgeLabel, Map<EdgeIndex, Edge>> getPrivateEdges() {
    return this.privateEdges;
  }

  public Map<EdgeLabel, Map<EdgeIndex, Edge>> getPublicEdges() {
    return this.publicEdges;
  }
}
