package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;

/**
 * @author caelis
 */
public class UnversionedEdgeImplTarget implements IEdgeImplTarget {
    /**
     *
     */
    private static final long serialVersionUID = -2270776881792143403L;
    private final NodeSerie target;

    public UnversionedEdgeImplTarget(NodeSerie target) {
        this.target = target;
    }

    public NodeSerie getTarget() {
        return target;
    }
}
