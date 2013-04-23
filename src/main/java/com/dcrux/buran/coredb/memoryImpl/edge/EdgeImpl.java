package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.iface.edge.EdgeLabel;

import java.io.Serializable;

/**
 * @author caelis
 */
public class EdgeImpl implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8167724352831721242L;
    private final EdgeLabel label;
    private final IEdgeImplTarget source;
    private final IEdgeImplTarget target;

    public EdgeImpl(EdgeLabel label, IEdgeImplTarget source, IEdgeImplTarget target) {
        this.label = label;
        this.source = source;
        this.target = target;
    }

    public EdgeLabel getLabel() {
        return label;
    }

    public IEdgeImplTarget getSource() {
        return source;
    }

    public IEdgeImplTarget getTarget() {
        return target;
    }
}
