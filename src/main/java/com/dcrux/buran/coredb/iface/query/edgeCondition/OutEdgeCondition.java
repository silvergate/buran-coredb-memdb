package com.dcrux.buran.coredb.iface.query.edgeCondition;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.api.ExpectableException;
import com.dcrux.buran.coredb.iface.edgeTargets.IEdgeTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.UnversionedEdTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.VersionedEdTarget;
import com.dcrux.buran.coredb.iface.query.IQNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.IMetaInfoForQuery;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class OutEdgeCondition implements INodeMetaCondition {

  // TODO: Taugt nur für die Out-Edges etwas, für die in-edges brauchts was anderes

  private final EdgeLabel label;
  private final Optional<EdgeIndex> index;
  private final Optional<IQNode> target;
  private final boolean matchAll;

  private OutEdgeCondition(EdgeLabel label, Optional<EdgeIndex> index, Optional<IQNode> target, boolean matchAll) {
    this.label = label;
    this.index = index;
    this.target = target;
    this.matchAll = matchAll;
    if (target.isPresent() && matchAll) {
      throw new ExpectableException("If target is present, you cannot set matchAll to true.");
    }
  }

  public static OutEdgeCondition hasEdge(EdgeLabel label) {
    return new OutEdgeCondition(label, Optional.<EdgeIndex>absent(), Optional.<IQNode>absent(), false);
  }

  public static OutEdgeCondition hasEdge(EdgeLabel label, EdgeIndex index) {
    return new OutEdgeCondition(label, Optional.<EdgeIndex>of(index), Optional.<IQNode>absent(), false);
  }

  public static OutEdgeCondition hasEdge(EdgeLabel label, IQNode targetNode) {
    return new OutEdgeCondition(label, Optional.<EdgeIndex>absent(), Optional.<IQNode>of(targetNode), false);
  }

  public static OutEdgeCondition hasEdge(EdgeLabel label, EdgeIndex index, IQNode targetNode) {
    return new OutEdgeCondition(label, Optional.<EdgeIndex>of(index), Optional.<IQNode>of(targetNode), false);
  }

  public static OutEdgeCondition hasEdgeAll(EdgeLabel label, IQNode targetNode) {
    return new OutEdgeCondition(label, Optional.<EdgeIndex>absent(), Optional.<IQNode>of(targetNode), true);
  }

  public EdgeLabel getLabel() {
    return label;
  }

  public Optional<EdgeIndex> getIndex() {
    return index;
  }

  public Optional<IQNode> getTarget() {
    return target;
  }

  public boolean isMatchAll() {
    return matchAll;
  }

  @Override
  public boolean matches(IMetaInfoForQuery metaInfoForQuery) {
    final Map<EdgeIndex, EdgeWithSource> queryableOutEdges = metaInfoForQuery.getQueryableOutEdges(this.label);

        /* Label available? */
    if (queryableOutEdges.isEmpty()) {
      return false;
    }

    final Map<EdgeIndex, EdgeWithSource> outEdgesToQuery;
    if (this.index.isPresent()) {
      outEdgesToQuery = new HashMap<>();
      if (!queryableOutEdges.containsKey(this.index.get())) {
        return false;
      }
      outEdgesToQuery.put(this.index.get(), queryableOutEdges.get(this.index.get()));
    } else {
      outEdgesToQuery = queryableOutEdges;
    }

    if (this.target.isPresent()) {
      for (Map.Entry<EdgeIndex, EdgeWithSource> elementToCheck : outEdgesToQuery.entrySet()) {
        boolean matches = matches(elementToCheck.getValue().getEdge(), metaInfoForQuery);
        if (!matches && isMatchAll()) {
          return false;
        }
        if (matches && (!isMatchAll())) {
          return true;
        }
      }
      return isMatchAll();
    } else {
      return true;
    }
  }

  private boolean matches(Edge edge, IMetaInfoForQuery metaInfoForQuery) {
    final IEdgeTarget edgeTarget = edge.getTarget();
    boolean found = false;
    final Integer version;
    final Long oid;
    switch (edgeTarget.getEdgeTargetType()) {
      case externalUnversioned:
          /* Externe können nie durchsucht werden */
        found = false;
        oid = null;
        version = null;
        break;
      case externalVersioned:
          /* Externe können nie durchsucht werden */
        found = false;
        oid = null;
        version = null;
        break;
      case unversioned:
        oid = ((UnversionedEdTarget) edgeTarget).getOid();
        version = null;
        break;
      case versioned:
        oid = ((VersionedEdTarget) edgeTarget).getOid();
        version = ((VersionedEdTarget) edgeTarget).getVersion();
        break;
      default:
        throw new ExpectableException("Unknown edge target");
    }
    if (oid != null) {
      if (version == null) {
        found = metaInfoForQuery.getNodeMatcher().matches(oid, this.target.get());
      } else {
        found = metaInfoForQuery.getNodeMatcher().matchesVersion(new OidVersion(oid, version), this.target.get());
      }
    }
    return found;
  }
}
