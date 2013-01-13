package com.dcrux.buran.coredb.memoryImpl.query;

import com.dcrux.buran.coredb.iface.query.CondCdNode;
import com.dcrux.buran.coredb.iface.query.CondNode;
import com.dcrux.buran.coredb.iface.query.ICondNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.IPropertyCondition;
import com.dcrux.buran.coredb.memoryImpl.DataReadApi;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;
import com.dcrux.buran.coredb.memoryImpl.data.AccountNodes;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;

/**
 * @author caelis
 */
public class DataAndMetaMatcher {

    public boolean matches(ICondNode qNode, final DataReadApi drApi, final NodeImpl node,
            NodeClassesApi ncApi, AccountNodes accountNodes) {
        final DataMatacher dataMatcher = new DataMatacher(ncApi, node.getNodeSerie().getClassId());
        final MetaMatcher metaMatcher = new MetaMatcher(drApi, accountNodes, ncApi);
        INodeMetaCondition metaCondition = null;
        IPropertyCondition propCondition = null;
        Long classId = null;
        if (qNode instanceof CondNode) {
            if (((CondNode) qNode).getMetaCondition().isPresent()) {
                metaCondition = ((CondNode) qNode).getMetaCondition().get();
            }
        }
        if (qNode instanceof CondCdNode) {
            if (((CondCdNode) qNode).getPropertyCondition().isPresent()) {
                propCondition = ((CondCdNode) qNode).getPropertyCondition().get();
                classId = ((CondCdNode) qNode).getClassId();
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
