package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.edge.EdgeIndex;
import com.dcrux.buran.coredb.iface.edge.EdgeLabel;
import com.dcrux.buran.coredb.iface.node.NidVer;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author caelis
 */
public class NodeImpl implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4936515447721013089L;

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
    private final Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> versionedInEdgeds = new HashMap<>();

    public NodeImpl(int version, long senderId, long receiverId, long validFrom, long validTo,
            Object[] data) {
        this.senderId = senderId;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.data = data;
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public NidVer createNidVer() {
        return new NidVer(this.nodeSerie.getOid(), this.version);
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
        Multimap<EdgeIndex, EdgeImpl> element = this.versionedInEdgeds.get(edgeImpl.getLabel());
        if (element == null) {
            element = HashMultimap.create();
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

    public Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> getVersionedInEdgeds() {
        return versionedInEdgeds;
    }

    @Override
    public String toString() {
        return "NodeImpl{" +
                "validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", senderId=" + senderId +
                ", version=" + version +
                '}';
    }
}
