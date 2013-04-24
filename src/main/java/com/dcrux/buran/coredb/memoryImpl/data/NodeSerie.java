package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.edge.EdgeIndex;
import com.dcrux.buran.coredb.iface.edge.EdgeLabel;
import com.dcrux.buran.coredb.iface.node.NidVer;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caelis
 */
public class NodeSerie implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7443880879598278485L;
    private final long oid;
    private final long classId;
    private final long receiverId;
    private int currentVersion = Integer.MIN_VALUE;

    public static final int FIRST_VERSION = NidVer.FIRST_VERSION;

    private boolean hasBeenDeleted;

    public NodeSerie(long oid, long classId, long receiverId) {
        this.oid = oid;
        this.classId = classId;
        this.receiverId = receiverId;
    }

    private final Map<Integer, NodeImpl> versionToNode = new HashMap<>();
    private final Map<NodeImpl, Integer> nodeToVersion = new HashMap<>();

    private final Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> inEdges = new HashMap<>();

    public Map<EdgeLabel, Multimap<EdgeIndex, EdgeImpl>> getInEdges() {
        return inEdges;
    }

    void addInEdge(EdgeIndex index, EdgeImpl edgeImpl) {
        Multimap<EdgeIndex, EdgeImpl> edges = this.inEdges.get(edgeImpl.getLabel());
        if (edges == null) {
            edges = HashMultimap.create();
            this.inEdges.put(edgeImpl.getLabel(), edges);
        }
        edges.put(index, edgeImpl);
    }

    void addNewVersion(NodeImpl node) {
        if (hasBeenDeleted()) {
            throw new IllegalStateException("Cannot update serie, has been marked as deleted.");
        }

    /* Get node to replace - if any */
        final NodeImpl nodeToReplace;
        if (!hasNoVersion()) {
            nodeToReplace = getNode(getCurrentVersion());
        } else {
            nodeToReplace = null;
        }

        int newVersion;
        if (hasNoVersion()) {
            newVersion = FIRST_VERSION;
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

    void markAsDeleted(long currentTime) {
        if (hasBeenDeleted()) {
            throw new IllegalStateException("Cannot update serie, has been marked as deleted.");
        }
        if (hasNoVersion()) {
            throw new IllegalStateException("Has no version - cannot mark as deleted");
        }
        final NodeImpl nodeToReplace = getNode(getCurrentVersion());
        nodeToReplace.setValidTo(currentTime);
        this.hasBeenDeleted = true;
    }

    public NodeImpl getNode(int version) {
        final NodeImpl node = this.versionToNode.get(version);
        if (node == null) {
            throw new IllegalStateException("Node with given version not found");
        }
        return node;
    }

    public boolean hasVersion(int version) {
        return this.versionToNode.containsKey(version);
    }

    public boolean hasNoVersion() {
        return (this.currentVersion == Integer.MIN_VALUE);
    }

    public boolean hasBeenDeleted() {
        return this.hasBeenDeleted;
    }

    /**
     * Liefert die momentane version, falls eine version vorhanden ist (commited und nicht
     * gelöscht). Liefert <code>null</code> in allen anderen fällen.
     *
     * @return
     */
    public Integer tryGetCurrentVersion() {
        if (hasNoVersion()) {
            return null;
        }
        if (hasBeenDeleted()) {
            return null;
        }
        return this.currentVersion;
    }

    public int getCurrentVersion() {
        if (hasNoVersion()) {
            throw new IllegalStateException("Has no version");
        }
        if (hasBeenDeleted()) {
            throw new IllegalStateException("Has been deleted - has no current version");
        }
        return this.currentVersion;
    }

    public int getLatestVersionBeforeDeletion() {
        if (hasNoVersion()) {
            throw new IllegalStateException("Has no version");
        }
        if (!hasBeenDeleted()) {
            throw new IllegalStateException("Has not yet been deleted");
        }
        return this.currentVersion;
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
