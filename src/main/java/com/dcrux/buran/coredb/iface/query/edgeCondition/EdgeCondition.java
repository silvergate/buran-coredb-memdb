package com.dcrux.buran.coredb.iface.query.edgeCondition;

import com.dcrux.buran.coredb.iface.Edge;
import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.OidVersion;
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
public class EdgeCondition implements INodeMetaCondition {
  private final EdgeDirection direction;
  private final EdgeLabel label;
  private final Optional<EdgeIndex> index;
  private final Optional<IQNode> target;
  private final boolean matchAll;

  public EdgeCondition(EdgeDirection direction, EdgeLabel label, Optional<EdgeIndex> index, Optional<IQNode> target,
                       boolean matchAll) {
    this.direction = direction;
    this.label = label;
    this.index = index;
    this.target = target;
    this.matchAll = matchAll;
    if (target.isPresent() && matchAll) {
      throw new ExpectableException("If target is present, you cannot set matchAll to true.");
    }
  }

  public EdgeDirection getDirection() {
    return direction;
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
    /* Can query edge? */
    final boolean queryable = metaInfoForQuery.isQueryable(this.label);
    if (!queryable) {
      throw new ExpectableException("Edge with this label ist not queryable");
    }

    /* Get edges */
    final boolean isPublic = this.label.isPublic();
    final Map<EdgeLabel, Map<EdgeIndex, Edge>> edges;
    switch (this.direction) {
      case in:
        if (isPublic) {
          edges = metaInfoForQuery.getPublicInEdges(this.label);
        } else {
          edges = metaInfoForQuery.getPrivateInEdges(this.label);
        }
        break;
      case out:
        if (isPublic) {
          edges = metaInfoForQuery.getPublicOutEdges(this.label);
        } else {
          edges = metaInfoForQuery.getPrivateOutEdges(this.label);
        }
        break;
      default:
        throw new ExpectableException("Unknown inout type");
    }

    /* Edge with this label available? */
    if (!edges.containsKey(this.label)) {
      return false;
    }

    final Map<EdgeIndex, Edge> singleLabelEdge = edges.get(this.label);

    if (this.index.isPresent()) {
      /* Edge index available? */
      if (!singleLabelEdge.containsKey(this.index.get())) {
        return false;
      }
    }

    if (!this.target.isPresent()) {
      return true;
    }

    /* Correct target ? */

    final Map<EdgeIndex, Edge> edgesToQuery;
    if (this.index.isPresent()) {
      edgesToQuery = new HashMap<>();
      edgesToQuery.put(this.index.get(), singleLabelEdge.get(this.index.get()));
    } else {
      edgesToQuery = singleLabelEdge;
    }

    for (final Map.Entry<EdgeIndex, Edge> entry : edgesToQuery.entrySet()) {
      final Edge edge = entry.getValue();
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

      if (isMatchAll() && (!found)) {
        return false;
      }
      if (!isMatchAll() && (found)) {
        return true;
      }
    }

    if (isMatchAll()) {
      return true;
    } else {
      return false;
    }
  }
}
