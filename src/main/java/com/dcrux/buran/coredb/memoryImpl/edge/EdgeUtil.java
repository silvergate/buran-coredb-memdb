package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.iface.edge.Edge;
import com.dcrux.buran.coredb.iface.edgeTargets.*;

/**
 * @author caelis
 */
public class EdgeUtil {
    public IEdgeTarget toEdgeTarget(IEdgeImplTarget implTarget) {
        if (implTarget instanceof VersionedEdgeImplTarget) {
            VersionedEdgeImplTarget veit = (VersionedEdgeImplTarget) implTarget;
            return new VersionedEdTarget(veit.getTarget().getNodeSerie().getOid(),
                    veit.getTarget().getVersion());
        } else if (implTarget instanceof UnversionedEdgeImplTarget) {
            UnversionedEdgeImplTarget unversionedEdgeImplTarget =
                    (UnversionedEdgeImplTarget) implTarget;
            return new UnversionedEdTarget(unversionedEdgeImplTarget.getTarget().getOid());
        } else if (implTarget instanceof ExtEdgeImplTarget) {
            ExtEdgeImplTarget extEdgeImplTarget = (ExtEdgeImplTarget) implTarget;
            if (extEdgeImplTarget.getVersion() == null) {
                return new ExtUnversionedEdTarget(extEdgeImplTarget.getUserId(),
                        extEdgeImplTarget.getOid());
            } else {
                return new ExtVersionedEdTarget(extEdgeImplTarget.getUserId(),
                        extEdgeImplTarget.getOid(), extEdgeImplTarget.getVersion());
            }
        } else {
            throw new IllegalArgumentException("Unknown target type");
        }
    }

    public Edge toEdge(EdgeImpl edgeImpl) {
        final IEdgeTarget target = toEdgeTarget(edgeImpl.getTarget());
        return (new Edge(target, edgeImpl.getLabel()));
    }
}
