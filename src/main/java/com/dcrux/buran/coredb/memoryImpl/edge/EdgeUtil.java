package com.dcrux.buran.coredb.memoryImpl.edge;

import com.dcrux.buran.coredb.iface.Edge;
import com.dcrux.buran.coredb.iface.EdgeWithSource;
import com.dcrux.buran.coredb.iface.edgeTargets.*;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 27.12.12
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public class EdgeUtil {
  public IEdgeTarget toEdgeTarget(IEdgeImplTarget implTarget) {
    if (implTarget instanceof VersionedEdgeImplTarget) {
      VersionedEdgeImplTarget veit = (VersionedEdgeImplTarget) implTarget;
      return new VersionedEdTarget(veit.getTarget().getNodeSerie().getOid(), veit.getTarget().getVersion());
    } else if (implTarget instanceof UnversionedEdgeImplTarget) {
      UnversionedEdgeImplTarget unversionedEdgeImplTarget = (UnversionedEdgeImplTarget) implTarget;
      return new UnversionedEdTarget(unversionedEdgeImplTarget.getTarget().getOid());
    } else if (implTarget instanceof ExtEdgeImplTarget) {
      ExtEdgeImplTarget extEdgeImplTarget = (ExtEdgeImplTarget) implTarget;
      if (extEdgeImplTarget.getVersion() == null) {
        return new ExtUnversionedEdTarget(extEdgeImplTarget.getUserId(), extEdgeImplTarget.getOid());
      } else {
        return new ExtVersionedEdTarget(extEdgeImplTarget.getUserId(), extEdgeImplTarget.getOid(),
                extEdgeImplTarget.getVersion());
      }
    } else {
      throw new IllegalArgumentException("Unknown target type");
    }
  }

  public EdgeWithSource toEdgeWithSource(EdgeImpl edgeImpl) {
    final IEdgeTarget source = toEdgeTarget(edgeImpl.getSource());
    final IEdgeTarget target = toEdgeTarget(edgeImpl.getTarget());
    return new EdgeWithSource(new Edge(target, edgeImpl.getLabel()), source);
  }
}
