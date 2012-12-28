package com.dcrux.buran.coredb.memoryImpl.query;

import com.dcrux.buran.coredb.iface.query.IQNode;
import com.dcrux.buran.coredb.iface.query.QCdNode;
import com.dcrux.buran.coredb.iface.query.QNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.IPropertyCondition;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;
import com.dcrux.buran.coredb.memoryImpl.data.AccountNodes;
import com.dcrux.buran.coredb.memoryImpl.data.Node;

/**
 *
 * @author caelis
 */
public class DataAndMetaMatcher {

  public boolean matches(IQNode qNode, final Node node, NodeClassesApi ncApi, AccountNodes accountNodes) {
    final DataMatacher dataMatcher = new DataMatacher(ncApi, node.getNodeSerie().getClassId());
    final MetaMatcher metaMatcher = new MetaMatcher(accountNodes, ncApi);
    INodeMetaCondition metaCondition = null;
    IPropertyCondition propCondition = null;
    Long classId = null;
    if (qNode instanceof QNode) {
      if (((QNode) qNode).getMetaCondition().isPresent()) {
        metaCondition = ((QNode) qNode).getMetaCondition().get();
      }
    }
    if (qNode instanceof QCdNode) {
      if (((QCdNode) qNode).getPropertyCondition().isPresent()) {
        propCondition = ((QCdNode) qNode).getPropertyCondition().get();
        classId = ((QCdNode) qNode).getClassId();
      }
    }

    /* Match */
    if ((classId != null) && (node.getNodeSerie().getClassId() != classId)) {
      return false;
    }

    if (metaCondition != null) {
      if (!metaMatcher.matches(node, metaCondition)) {
        return false;
      }
    }

    if (propCondition != null) {
      if (!dataMatcher.matches(node.getData(), propCondition)) {
        return false;
      }
    }

    return true;
  }
}
