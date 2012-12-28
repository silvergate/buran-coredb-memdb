package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.query.IQNode;
import com.dcrux.buran.coredb.iface.query.QCdNode;
import com.dcrux.buran.coredb.memoryImpl.data.AccountNodes;
import com.dcrux.buran.coredb.memoryImpl.data.Node;
import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.query.DataAndMetaMatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author caelis
 */
public class QueryApi {
  private final Nodes nodes;
  private final NodeClassesApi ncApi;

  public QueryApi(Nodes nodes, NodeClassesApi ncApi) {
    this.nodes = nodes;
    this.ncApi = ncApi;
  }

  public Set<Node> query(long receiverId, long senderId, IQNode query) {
    final DataAndMetaMatcher dataAndMetaMatcher = new DataAndMetaMatcher();
    final AccountNodes acNodes = nodes.getByUserId(receiverId);

    final Long classId;
    if (query instanceof QCdNode) {
      classId = ((QCdNode) query).getClassId();
    } else {
      classId = null;
    }

    Collection<NodeSerie> nodesToIterate;
    if (classId != null) {
      nodesToIterate = acNodes.getClassIdToAliveSeries().get(classId);
    } else {
      nodesToIterate = acNodes.getOidToAliveSeries().values();
    }

    if (nodesToIterate == null) {
      return Collections.emptySet();
    }

    final Set<Node> result = new HashSet<>();
    for (final NodeSerie nodeSerie : nodesToIterate) {
      final Node currentNode = nodeSerie.getNode(nodeSerie.getCurrentVersion());
      final boolean matches = dataAndMetaMatcher.matches(query, currentNode, this.ncApi, acNodes);
      if (matches) {
        result.add(currentNode);
      }
    }
    return result;
  }

}
