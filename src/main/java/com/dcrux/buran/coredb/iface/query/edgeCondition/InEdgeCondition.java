package com.dcrux.buran.coredb.iface.query.edgeCondition;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.api.exceptions.ExpectableException;
import com.dcrux.buran.coredb.iface.edgeTargets.IEdgeTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.UnversionedEdTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.VersionedEdTarget;
import com.dcrux.buran.coredb.iface.query.IQNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.IMetaInfoForQuery;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class InEdgeCondition implements INodeMetaCondition {

  private final EdgeLabel label;
  private final Optional<EdgeIndex> index;
  private final Optional<IQNode> source;

  private InEdgeCondition(EdgeLabel label, Optional<EdgeIndex> index, Optional<IQNode> source) {
    this.label = label;
    this.index = index;
    this.source = source;
  }

  public static InEdgeCondition hasAnyEdge(EdgeLabel label) {
    return new InEdgeCondition(label, Optional.<EdgeIndex>absent(), Optional.<IQNode>absent());
  }

  public static InEdgeCondition hasEdge(EdgeLabel label, EdgeIndex index) {
    return new InEdgeCondition(label, Optional.<EdgeIndex>of(index), Optional.<IQNode>absent());
  }

  public static InEdgeCondition hasAnyEdge(EdgeLabel label, IQNode targetNode) {
    return new InEdgeCondition(label, Optional.<EdgeIndex>absent(), Optional.<IQNode>of(targetNode));
  }

  public static InEdgeCondition hasEdge(EdgeLabel label, EdgeIndex index, IQNode targetNode) {
    return new InEdgeCondition(label, Optional.<EdgeIndex>of(index), Optional.<IQNode>of(targetNode));
  }

  public EdgeLabel getLabel() {
    return label;
  }

  public Optional<EdgeIndex> getIndex() {
    return index;
  }

  public Optional<IQNode> getSource() {
    return this.source;
  }

  @Override
  public boolean matches(IMetaInfoForQuery metaInfoForQuery) {
    final Multimap<EdgeIndex, EdgeWithSource> queryableOutEdges = metaInfoForQuery.getQueryableInEdges(this.label);

        /* Label available? */
    if (queryableOutEdges.isEmpty()) {
      return false;
    }

    final Multimap<EdgeIndex, EdgeWithSource> outEdgesToQuery;
    if (this.index.isPresent()) {
      outEdgesToQuery = HashMultimap.create();
      if (!queryableOutEdges.containsKey(this.index.get())) {
        return false;
      }
      outEdgesToQuery.putAll(this.index.get(), queryableOutEdges.get(this.index.get()));
    } else {
      outEdgesToQuery = queryableOutEdges;
    }

    if (this.source.isPresent()) {
      for (Map.Entry<EdgeIndex, EdgeWithSource> elementToCheck : outEdgesToQuery.entries()) {
        boolean matches = matches(elementToCheck.getValue(), metaInfoForQuery);
        if (matches) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean matches(EdgeWithSource withSource, IMetaInfoForQuery metaInfoForQuery) {
    final Edge edge = withSource.getEdge();
    final IEdgeTarget edgeTarget = withSource.getSource();
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
        oid = ((UnversionedEdTarget) edgeTarget).getNid();
        version = null;
        break;
      case versioned:
        oid = ((VersionedEdTarget) edgeTarget).getNid();
        version = ((VersionedEdTarget) edgeTarget).getVersion();
        break;
      default:
        throw new ExpectableException("Unknown edge target");
    }

    if (oid != null) {
      if (version == null) {
        found = metaInfoForQuery.getNodeMatcher().matches(oid, this.source.get());
      } else {
        found = metaInfoForQuery.getNodeMatcher().matchesVersion(new NidVer(oid, version), this.source.get());
      }
    }
    return found;
  }
}
