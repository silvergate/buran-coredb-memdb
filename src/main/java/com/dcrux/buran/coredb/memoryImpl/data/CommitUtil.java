package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.IncOid;
import com.dcrux.buran.coredb.iface.OidVersion;
import com.dcrux.buran.coredb.iface.api.ExpectableException;
import com.dcrux.buran.coredb.iface.api.OptimisticLockingException;
import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeClass;
import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeConstraints;
import com.dcrux.buran.coredb.iface.edgeTargets.*;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.DataReadApi;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;
import com.dcrux.buran.coredb.memoryImpl.PreparedComitInfo;
import com.dcrux.buran.coredb.memoryImpl.edge.*;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author caelis
 */
public class CommitUtil {
  public Set<PreparedComitInfo> generateOidsFromIoids(long senderId, final Set<IncOid> incOids,
                                                      AccountNodes accountNodes) {
    final Set<PreparedComitInfo> toOids = new HashSet<>();
    for (final IncOid incOid : incOids) {
      final IncNode incNode = accountNodes.getIncNode(incOid.getId());
      final long classId = incNode.getClassId();
      assert (incNode != null);
      if (incNode.getToUpdate() != null) {
        /* Update old version */
        final NodeSerie nodeSerie = accountNodes.getNodeSerieByOid(incNode.getToUpdate().getOid(), true);
        // TODO: Unterschiedliche fehlermeldung, bei nicht vorhanden eine expected exception, wenn gelöscht eine optimistic locking exception
        if (nodeSerie == null) {
          throw new ExpectableException("Cannot update given node, node has been deletet or not found");
        }
        final PreparedComitInfo pci = new PreparedComitInfo(
                new OidVersion(incNode.getToUpdate().getOid(), incNode.getToUpdate().getVersion() + 1), incOid, classId,
                true, incNode.getToUpdate(), incNode.getReceiverId(), incNode, nodeSerie);
        incNode.getNode().setNodeSerie(nodeSerie);
        toOids.add(pci);
      } else {
                /* Create new node */
        final long oid = accountNodes.getOidCounter().incrementAndGet();
        final NodeSerie newNodeSerie = new NodeSerie(oid, classId, incNode.getReceiverId());
        final PreparedComitInfo pci =
                new PreparedComitInfo(new OidVersion(oid, NodeSerie.FIRST_VERSION), incOid, classId, false, null,
                        incNode.getReceiverId(), incNode, newNodeSerie);
        incNode.getNode().setNodeSerie(newNodeSerie);
        toOids.add(pci);
      }
    }
    return toOids;
  }

  public void validateEdgeTarget(final Set<PreparedComitInfo> pciEntry, final IncubationEdge incEdge, long receiverId,
                                 DataReadApi drApi) {
    final IIncEdgeTarget target = incEdge.getTarget();
    switch (target.getIncType()) {
      case externalUnversioned:
        System.out.println(
                "TODO: Hier muss überprüft werden, ob die userId bekannt ist! (Der Rest kann nicht überprüft werden)");
        break;
      case externalVersioned:
        System.out.println(
                "TODO: Hier muss überprüft werden, ob die userId bekannt ist! (Der Rest kann nicht überprüft werden)");
        break;
      case unversioned:
        if (!drApi.oidExistsInCurrentOrHistory(receiverId, ((UnversionedEdTarget) target).getOid())) {
          throw new IllegalStateException("Edge target not found");
        }
        break;
      case versioned:
        if (!drApi.oidExistsInCurrentOrHistory(receiverId, ((VersionedEdTarget) target).getOid())) {
          throw new IllegalStateException("Edge target not found");
        }
        break;
      case unversionedInc:
          /* Has to be in incubation */
        OidVersion found = findOidInIncOids(pciEntry, ((IncUnversionedEdTarget) target).getIoid());
        if (found == null) {
          throw new IllegalStateException("A given edge-inc-oid is not found in incubation");
        }
        break;
      case versionedInc:
                   /* Has to be in incubation */
        OidVersion foundVer = findOidInIncOids(pciEntry, ((IncVersionedEdTarget) target).getIoid());
        if (foundVer == null) {
          throw new IllegalStateException("A given edge-inc-oid is not found in incubation");
        }
        break;
    }
  }

  @Nullable
  private PreparedComitInfo findPrepComInfoByIncOid(Set<PreparedComitInfo> pci, long incOid) {
    for (final PreparedComitInfo pciEntryEntry : pci) {
      if (pciEntryEntry.getIoid().getId() == incOid) {
        return pciEntryEntry;
      }
    }
    return null;
  }

  @Nullable
  private OidVersion findOidInIncOids(Set<PreparedComitInfo> pci, long incOid) {
    PreparedComitInfo prepComInfo = findPrepComInfoByIncOid(pci, incOid);
    if (prepComInfo != null) {
      return prepComInfo.getOidToGet();
    }
    return null;
  }

  private void validateSingleNodeConstraint(PreparedComitInfo pci, NodeClassesApi ncApi) {
    final long classId = pci.getClassId();
    final NodeClass nc = ncApi.getClassById(classId);
    if (nc == null) {
      throw new IllegalStateException("Class not declared");
    }

    /* Check types */
    for (short typeIndex = 0; typeIndex < nc.getNumberOfTypes(); typeIndex++) {
      final boolean required = nc.isRequired(typeIndex);
      final Object value = pci.getIncNode().getNode().getData()[typeIndex];
      if (required && (value == null)) {
        throw new IllegalStateException("A required property is not available");
      }
    }

    /* Check edges */
    for (Map.Entry<EdgeLabel, PrivateEdgeClass> edgeEntry : nc.getEdgeClasses().entrySet()) {
      final PrivateEdgeConstraints constraints = edgeEntry.getValue().getOutNodeConstraints();
      int numberOfEdges = 0;
      boolean zeroIsSet = false;
      for (Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> edgeInstance : pci.getIncNode().getIncubationEdges()
              .entrySet()) {
        if (edgeInstance.getKey().getLabel().equals(edgeEntry.getKey())) {
           /* Label is correct */
          if (edgeInstance.getKey().getIndex().getId() == 0) {
            zeroIsSet = true;
          }
          numberOfEdges++;
        }
      }
      switch (constraints) {
        case exactOne:
          if ((!zeroIsSet) || (numberOfEdges == 1)) {
            throw new IllegalStateException("Need exact one edge");
          }
          break;
        case many:
          break;
        case manyAtLeastOne:
          if ((!zeroIsSet)) {
            throw new IllegalStateException("Need at least one edge");
          }
          break;
        case oneOrNone:
          if (!((zeroIsSet && (numberOfEdges == 1)) || (numberOfEdges != 0))) {
            throw new IllegalStateException("One edge or none");
          }
          break;
      }
      //TODO: Check target node class
    }

    /* Private edges have to be declared */
    for (final Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> incEdge : pci.getIncNode().getIncubationEdges()
            .entrySet()) {
      final EdgeLabel label = incEdge.getKey().getLabel();
      if (!label.isPublic()) {
        /* Label has to be declared */
        final boolean declared = nc.getEdgeClasses().containsKey(label);
        if (!declared) {
          throw new ExpectableException(MessageFormat.format("Private label {0} is not declared in class", label));
        }
      }
    }

  }

  public void validate(Set<PreparedComitInfo> prepComInfo, DataReadApi drApi, NodeClassesApi ncApi) throws
          OptimisticLockingException {
    /* No duplicate target oids*/
    final Set<Long> givenOids = new HashSet<>();
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      if (givenOids.contains(pciEntry.getOidToGet().getOid())) {
        throw new IllegalStateException("Diesebe OID befindet sich mehrfach im commit");
      }
      givenOids.add(pciEntry.getOidToGet().getOid());
    }

    /* Objects to update have to exist */
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      if (pciEntry.isUpdate()) {
        final boolean exists = drApi.existsInCurrent(pciEntry.getReceiverId(), pciEntry.getOidToUpdate());
        if (!exists) {
          throw new OptimisticLockingException();
        }
      }
    }

    /* Check node targets */
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      for (final Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> eEntry : pciEntry.getIncNode().getIncubationEdges()
              .entrySet()) {
        final IncubationEdge incEdge = eEntry.getValue();
        validateEdgeTarget(prepComInfo, incEdge, pciEntry.getReceiverId(), drApi);
      }
    }

    /* Check constraints */
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      validateSingleNodeConstraint(pciEntry, ncApi);
    }
  }

  public void addEdge(Set<PreparedComitInfo> prepComInfo, final NodeImpl sourceNode, final IncubationEdge incEdge,
                      EdgeIndex index, AccountNodes accountNodes) {
    NodeSerie inNodeSerie = null;
    NodeImpl inNode = null;

    final IIncEdgeTarget target = incEdge.getTarget();
    final IEdgeImplTarget finalTarget;
    switch (target.getIncType()) {
      case externalUnversioned:
        finalTarget = new ExtEdgeImplTarget(((ExtUnversionedEdTarget) target).getUserId(),
                ((ExtUnversionedEdTarget) target).getOid(), null);
        break;
      case externalVersioned:
        finalTarget = new ExtEdgeImplTarget(((ExtVersionedEdTarget) target).getUserId(),
                ((ExtVersionedEdTarget) target).getOid(), ((ExtVersionedEdTarget) target).getVersion());
        break;
      case unversioned:
        final UnversionedEdTarget unversionedEdTarget = (UnversionedEdTarget) target;
        finalTarget =
                new UnversionedEdgeImplTarget(accountNodes.getNodeSerieByOid(unversionedEdTarget.getOid(), false));
        /* Add to in-node */
        inNodeSerie = accountNodes.getNodeSerieByOid(unversionedEdTarget.getOid(), false);
        break;
      case versioned:
        final VersionedEdTarget versionedEdTarget = (VersionedEdTarget) target;
        finalTarget = new VersionedEdgeImplTarget(
                accountNodes.getNode(versionedEdTarget.getOid(), versionedEdTarget.getVersion(), false));
        /* Add to in node */
        inNode = accountNodes.getNode(versionedEdTarget.getOid(), versionedEdTarget.getVersion(), false);
        break;
      case unversionedInc:
        final IncUnversionedEdTarget incUnversionedEdTarget = (IncUnversionedEdTarget) target;
        final PreparedComitInfo pci = findPrepComInfoByIncOid(prepComInfo, incUnversionedEdTarget.getIoid());
        assert (pci != null);
        finalTarget = new UnversionedEdgeImplTarget(pci.getNodeSerie());
                /* Add to in-node */
        inNodeSerie = pci.getNodeSerie();
        break;
      case versionedInc:
        final IncVersionedEdTarget incVersionedEdTarget = (IncVersionedEdTarget) target;
        final PreparedComitInfo pciTwo = findPrepComInfoByIncOid(prepComInfo, incVersionedEdTarget.getIoid());
        assert (pciTwo != null);
        finalTarget = new VersionedEdgeImplTarget(pciTwo.getIncNode().getNode());
                /* Add to in node */
        inNode = pciTwo.getIncNode().getNode();
        break;
      default:
        throw new IllegalArgumentException();
    }

    VersionedEdgeImplTarget sourceTarget = new VersionedEdgeImplTarget(sourceNode);
    final EdgeImpl edgeImpl = new EdgeImpl(incEdge.getLabel(), sourceTarget, finalTarget);

    /* Add to out node */
    sourceNode.addOutEdge(index, edgeImpl);

    /* Add to in-node */
    if (inNodeSerie != null) {
      inNodeSerie.addInEdge(index, edgeImpl);
    }
    if (inNode != null) {
      inNode.addVersionedInEdge(index, edgeImpl);
    }
  }

}
