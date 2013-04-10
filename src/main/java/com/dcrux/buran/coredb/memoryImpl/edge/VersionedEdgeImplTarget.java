package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;

/**
 * @author caelis
 */
public class VersionedEdgeImplTarget implements IEdgeImplTarget {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4279910518575111038L;
	private final NodeImpl target;

    public VersionedEdgeImplTarget(NodeImpl target) {
        this.target = target;
    }

    public NodeImpl getTarget() {
        return target;
    }
}
