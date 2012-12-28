package com.dcrux.buran.coredb.memoryImpl.query;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.EdgeWithSource;
import com.dcrux.buran.coredb.iface.OidVersion;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.permissions.UserNodePermission;
import com.dcrux.buran.coredb.iface.query.IQNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.IMetaInfoForQuery;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMatcher;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;
import com.dcrux.buran.coredb.memoryImpl.data.AccountNodes;
import com.dcrux.buran.coredb.memoryImpl.data.Node;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeImpl;
import com.dcrux.buran.coredb.memoryImpl.edge.EdgeUtil;
import org.apache.commons.lang.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author caelis
 */
public class MetaMatcher {

  private Node node;
  private long classId;
  private final EdgeUtil edgeUtil = new EdgeUtil();
  private final AccountNodes accountNodes;
  private final NodeClassesApi ncApi;

  public MetaMatcher(AccountNodes accountNodes, NodeClassesApi ncApi) {
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
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<EdgeIndex, EdgeWithSource> getQueryableOutEdges(EdgeLabel label) {
      Map<EdgeLabel, Map<EdgeIndex, EdgeImpl>> outEdges = node.getOutEdges();
      final Map<EdgeIndex, EdgeWithSource> result = new HashMap<>();
      if (outEdges.containsKey(label)) {
        for (final Map.Entry<EdgeIndex, EdgeImpl> edgesInLabel : outEdges.get(label).entrySet()) {
          result.put(edgesInLabel.getKey(), edgeUtil.toEdgeWithSource(edgesInLabel.getValue()));
        }
      }
      return result;
    }

    @Override
    public INodeMatcher getNodeMatcher() {
      return nodeMatcher;
    }
  };

  private final INodeMatcher nodeMatcher = new INodeMatcher() {
    @Override
    public boolean matchesVersion(OidVersion oidVersion, IQNode qNode) {
      final Node node = accountNodes.getNode(oidVersion.getOid(), oidVersion.getVersion(), true);
      if (node == null) {
        return false;
      }
      DataAndMetaMatcher dataAndMetaMatcher = new DataAndMetaMatcher();
      return dataAndMetaMatcher.matches(qNode, node, ncApi, accountNodes);
    }

    @Override
    public boolean matches(long oid, IQNode qNode) {
      final Node node = accountNodes.getCurrentNode(oid);
      if (node == null) {
        return false;
      }
      DataAndMetaMatcher dataAndMetaMatcher = new DataAndMetaMatcher();
      return dataAndMetaMatcher.matches(qNode, node, ncApi, accountNodes);
    }
  };

  public boolean matches(Node node, INodeMetaCondition nodeMetaCondition) {
    this.node = node;
    this.classId = node.getNodeSerie().getClassId();
    return nodeMetaCondition.matches(this.metaInfoForQuery);
  }
}
