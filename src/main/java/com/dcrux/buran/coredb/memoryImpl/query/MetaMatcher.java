package com.dcrux.buran.coredb.memoryImpl.query;

import com.dcrux.buran.coredb.iface.api.apiData.HistoryState;
import com.dcrux.buran.coredb.iface.api.exceptions.ExpectableException;
import com.dcrux.buran.coredb.iface.api.exceptions.NodeNotFoundException;
import com.dcrux.buran.coredb.iface.edge.Edge;
import com.dcrux.buran.coredb.iface.edge.EdgeIndex;
import com.dcrux.buran.coredb.iface.edge.EdgeIndexRange;
import com.dcrux.buran.coredb.iface.edge.EdgeLabel;
import com.dcrux.buran.coredb.iface.node.NidVer;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.permissions.UserNodePermission;
import com.dcrux.buran.coredb.iface.query.ICondNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.IMetaInfoForQuery;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMatcher;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.memoryImpl.DataReadApi;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;
import com.dcrux.buran.coredb.memoryImpl.data.AccountNodes;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeUtil;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.NotImplementedException;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * @author caelis
 */
public class MetaMatcher {

    private NodeImpl node;
    private long classId;
    private final DataReadApi drApi;
    private final EdgeUtil edgeUtil = new EdgeUtil();
    private final AccountNodes accountNodes;
    private final NodeClassesApi ncApi;

    public MetaMatcher(DataReadApi drApi, AccountNodes accountNodes, NodeClassesApi ncApi) {
        this.drApi = drApi;
        this.accountNodes = accountNodes;
        this.ncApi = ncApi;
    }

    private final IMetaInfoForQuery metaInfoForQuery = new IMetaInfoForQuery() {
        @Override
        public long getClassId() {
            return classId;
        }

        @Override
        public int getVersion() {
            return node.getVersion();
        }

        @Override
        public long getValidFrom() {
            return node.getValidFrom();
        }

        @Override
        public long getValidTo() {
            return node.getValidTo();
        }

        @Override
        public long getReceiver() {
            return node.getNodeSerie().getReceiverId();
        }

        @Override
        public long getSender() {
            return node.getSenderId();
        }

        @Override
        public UserNodePermission getOwnPermissions() {
            throw new NotImplementedException();
        }

        @Override
        public Map<Long, UserNodePermission> getOtherPermissions() {
            throw new NotImplementedException();
        }

        @Override
        public Set<Long> getDomainIds() {
            return node.getDomainIds();
        }

        @Override
        public NodeClass getNodeClass() {
            return null;  //To change body of implemented methods use File | Settings | File
            // Templates.
        }

        @Override
        public Map<EdgeIndex, Edge> getQueryableOutEdges(EdgeLabel label) {
            try {
                final Map<EdgeLabel, Map<EdgeIndex, Edge>> edges =
                        drApi.getOutEdges(getReceiver(), getSender(),
                                new NidVer(node.getNodeSerie().getOid(), node.getVersion()),
                                EnumSet.of(com.dcrux.buran.coredb.iface.edge.EdgeType.privateMod,
                                        com.dcrux.buran.coredb.iface.edge.EdgeType.publicMod), true,
                                Optional.<EdgeLabel>absent(), false);
                if (!edges.containsKey(label)) {
                    return Collections.emptyMap();
                } else {
                    return edges.get(label);
                }
            } catch (NodeNotFoundException e) {
                throw new ExpectableException("Node not found, this should not happen.");
            }
        }

        @Override
        public Multimap<EdgeIndex, NidVer> getQueryableInEdges(EdgeLabel label) {
            try {
                final Map<EdgeLabel, Multimap<EdgeIndex, NidVer>> inEdges =
                        drApi.getInEdges(getReceiver(), getSender(),
                                new NidVer(node.getNodeSerie().getOid(), node.getVersion()),
                                EnumSet.of(HistoryState.active), Optional.<ClassId>absent(),
                                EnumSet.of(com.dcrux.buran.coredb.iface.edge.EdgeType.privateMod,
                                        com.dcrux.buran.coredb.iface.edge.EdgeType.publicMod),
                                Optional.<EdgeIndexRange>absent(), Optional.<EdgeLabel>of(label),
                                true);
                if (!inEdges.containsKey(label)) {
                    return HashMultimap.create();
                }
                return inEdges.get(label);
            } catch (NodeNotFoundException e) {
                throw new ExpectableException("Node not found, this should not happen.");
            }
        }

        @Override
        public INodeMatcher getNodeMatcher() {
            return nodeMatcher;
        }
    };

    private final INodeMatcher nodeMatcher = new INodeMatcher() {
        @Override
        public boolean matchesVersion(NidVer nidVer, ICondNode qNode) {
            final NodeImpl node = accountNodes.getNode(nidVer.getNid(), nidVer.getVersion(), true);
            if (node == null) {
                return false;
            }
            DataAndMetaMatcher dataAndMetaMatcher = new DataAndMetaMatcher();
            return dataAndMetaMatcher.matches(qNode, drApi, node, ncApi, accountNodes);
        }

        @Override
        public boolean matches(long oid, ICondNode qNode) {
            final NodeImpl node = accountNodes.getCurrentNode(oid);
            if (node == null) {
                return false;
            }
            DataAndMetaMatcher dataAndMetaMatcher = new DataAndMetaMatcher();
            return dataAndMetaMatcher.matches(qNode, drApi, node, ncApi, accountNodes);
        }

        @Override
        public boolean matches(long oid, ClassId classId) {
            final NodeImpl node = accountNodes.getCurrentNode(oid);
            if (node == null) {
                return false;
            }
            return classId.getId() == node.getNodeSerie().getClassId();
        }
    };

    public boolean matches(NodeImpl node, INodeMetaCondition nodeMetaCondition) {
        this.node = node;
        this.classId = node.getNodeSerie().getClassId();
        return nodeMetaCondition.matches(this.metaInfoForQuery);
    }
}
