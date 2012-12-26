package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.iface.api.CommitResult;
import com.dcrux.buran.coredb.iface.api.OptimisticLockingException;
import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeClass;
import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeConstraints;
import com.dcrux.buran.coredb.iface.edgeTargets.*;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.data.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class CommitApi {
  private final Edges edges;
  private final Nodes nodes;
  private final DataReadApi drApi;
  private final NodeClassesApi ncApi;

  public CommitApi(Edges edges, Nodes nodes, DataReadApi drApi, NodeClassesApi ncApi) {
    this.edges = edges;
    this.nodes = nodes;
    this.drApi = drApi;
    this.ncApi = ncApi;
  }

  public CommitResult commit(long senderId, IncOid... incOid) throws OptimisticLockingException {
    Set<IncOid> incOids = new HashSet<>();
    incOids.addAll(Arrays.asList(incOid));
    return commit(senderId, incOids);
  }

  public CommitResult commit(long senderId, Set<IncOid> incOids) throws OptimisticLockingException {
    // TODO: Missing write lock
      /* OIDs von allen extrahieren */
    final Set<PreparedComitInfo> prepComInfo = generateOidsFromIoids(senderId, incOids);
      /* Validate */
    validate(senderId, prepComInfo);

        /* Commit node */
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      commitNodeAndEdges(prepComInfo, senderId, pciEntry);
    }

    final Map<IncOid, OidVersion> incOidToOidVer = new HashMap<>();
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      incOidToOidVer.put(pciEntry.getIoid(), pciEntry.getOidToGet());
    }
    return new CommitResult(incOidToOidVer);
  }

  private void validate(long senderId, Set<PreparedComitInfo> prepComInfo) throws OptimisticLockingException {
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
        final boolean exists = this.drApi.existsInCurrent(pciEntry.getReceiverId(), pciEntry.getOidToUpdate());
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
        validateEdgeTarget(prepComInfo, incEdge, pciEntry.getReceiverId());
      }
    }

    /* Check constraints */
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      validateSingleNodeConstraint(pciEntry);
    }
  }

  private void validateSingleNodeConstraint(PreparedComitInfo pci) {
    final long classId = pci.getClassId();
    final NodeClass nc = this.ncApi.getClassById(classId);
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
    /* Check properties */
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
    }
  }

  private void validateEdgeTarget(final Set<PreparedComitInfo> pciEntry, final IncubationEdge incEdge,
                                  long receiverId) {
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
        if (!this.drApi.oidExistsInCurrentOrHistory(receiverId, ((UnversionedEdTarget) target).getOid())) {
          throw new IllegalStateException("Edge target not found");
        }
        break;
      case versioned:
        if (!this.drApi.oidExistsInCurrentOrHistory(receiverId, ((VersionedEdTarget) target).getOid())) {
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
  private OidVersion findOidInIncOids(Set<PreparedComitInfo> pci, long incOid) {
    boolean foundVer = false;
    for (final PreparedComitInfo pciEntryEntry : pci) {
      if (pciEntryEntry.getIoid().getId() == incOid) {
        return pciEntryEntry.getOidToGet();
      }
    }
    return null;
  }

  private Set<PreparedComitInfo> generateOidsFromIoids(long senderId, final Set<IncOid> incOids) {
    final Set<PreparedComitInfo> toOids = new HashSet<>();
    for (final IncOid incOid : incOids) {
      final long classId = this.nodes.getByUserId(senderId).getIncOidToClassId().get(incOid.getId());
      final NodesSingleClass incClassNodes = this.nodes.getByUserId(senderId).getByClassId(classId);
      assert (incClassNodes != null);
      final IncNode incNode = incClassNodes.getIncOidToIncubationNode().get(incOid.getId());
      assert (incNode != null);
      if (incNode.getToUpdate() != null) {
        /* Update old version */
        final PreparedComitInfo pci = new PreparedComitInfo(
                new OidVersion(incNode.getToUpdate().getOid(), incNode.getToUpdate().getVersion() + 1), incOid, classId,
                true, incNode.getToUpdate(), incNode.getReceiverId(), incNode);
        toOids.add(pci);
      } else {
                /* Create new node */
        final AccountNodes accountNodes = this.nodes.getByUserId(incNode.getReceiverId());
        final long oid = accountNodes.getOidCounter().incrementAndGet();
        final PreparedComitInfo pci =
                new PreparedComitInfo(new OidVersion(oid, 0), incOid, classId, false, null, incNode.getReceiverId(),
                        incNode);
        toOids.add(pci);
      }
    }
    return toOids;
  }

  private void commitNodeAndEdges(Set<PreparedComitInfo> pci, final long senderId, PreparedComitInfo pciEntry) {
    final IncOid incOid = pciEntry.getIoid();
    final long classId = pciEntry.getClassId();
    final NodesSingleClass incClassNodes = this.nodes.getByUserId(senderId).getByClassId(classId);
    final IncNode incNode = incClassNodes.getIncOidToIncubationNode().get(incOid.getId());

    /* Remove Data */
    removeFromIncubation(this.nodes.getByUserId(senderId), incClassNodes, incOid.getId());

    /* Set data */
    final AccountEdges accountEdges = this.edges.getAccountEdges(incNode.getReceiverId());
    final AccountNodes accountNodes = this.nodes.getByUserId(incNode.getReceiverId());
    final NodesSingleClass classNodes = accountNodes.getByClassId(classId);
    final long oid = pciEntry.getOidToGet().getOid();
    final int version = pciEntry.getOidToGet().getVersion();
    final OidVersion oidVersion = new OidVersion(oid, version);
    if (incNode.getToUpdate() == null) {
      System.out.println("Adding OID " + oid + " for class " + classId);
      accountNodes.getOidToClassId().put(oid, classId);
    } else {
      /* Historize old node */
      historizeNode(incNode.getReceiverId(), incNode.getToUpdate().getOid(), incNode.getToUpdate().getVersion(),
              classId);
    }

    /* Add new node to current and historized */
    classNodes.getCurrent().put(oidVersion, incNode.getNode());
    accountNodes.getOidToNodeWithClass().put(oidVersion.getOid(), new NodeWithClassId(incNode.getNode(), classId));
    classNodes.getCurrentAndHistorized().put(oidVersion, incNode.getNode());

    /* Set valid from time */
    long currentTime = System.currentTimeMillis();
    incNode.getNode().setValidFrom(currentTime);

    /* Set public Edges */
    commitEdges(pci, oid, incNode.getNode(), incNode, accountEdges);
  }

  private void historizeNode(long receiverId, long oid, int version, @Nullable Long classId) {
    final AccountNodes accountNodes = this.nodes.getByUserId(receiverId);
    /* Get class ID */
    long realClassId;
    if (classId == null) {
      realClassId = accountNodes.getOidToClassId().get(oid);
    } else {
      realClassId = classId;
    }

    /* Remove from current */
    final NodesSingleClass singleClassNodes = accountNodes.getByClassId(realClassId);
    final OidVersion oidVersion = new OidVersion(oid, version);
    final Node node = singleClassNodes.getCurrent().get(oidVersion);
    singleClassNodes.getCurrent().remove(oidVersion);

    accountNodes.getOidToNodeWithClass().remove(oidVersion.getOid());

    /* Remove edges */
    final AccountEdges accountEdges = this.edges.getAccountEdges(receiverId);
    accountEdges.getCurrentOutEdges().removeAll(oid);
    for (Map.Entry<EdgeLabel, Map<EdgeIndex, Edge>> publicEdgeEntry : node.getPublicEdges().entrySet()) {
      for (Map.Entry<EdgeIndex, Edge> singleEntry : publicEdgeEntry.getValue().entrySet()) {
        final EdgeWithIndex ewi = new EdgeWithIndex(singleEntry.getKey(), singleEntry.getValue());
        final EdgeLabel label = publicEdgeEntry.getKey();
        accountEdges.getLabelToEdges().remove(label, ewi);
      }
    }

    /* Valid to */
    long currentTime = System.currentTimeMillis();
    node.setValidTo(currentTime);
  }

  private void commitEdges(Set<PreparedComitInfo> pci, final long newOid, final Node newNode, IncNode incNode,
                           final AccountEdges accountEdges) {
    for (final Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> incEdge : incNode.getIncubationEdges().entrySet()) {
      commitEdge(pci, newOid, newNode, accountEdges, incEdge.getKey().getIndex(), incEdge.getValue());
    }
  }

  private void commitEdge(Set<PreparedComitInfo> pci, final long oid, final Node newNode,
                          final AccountEdges accountEdges, final EdgeIndex edgeIndex, final IncubationEdge incEdge) {
    IIncEdgeTarget target = incEdge.getTarget();
    if (!(target instanceof IEdgeTarget)) {
      switch (target.getIncType()) {
        case versionedInc:
          OidVersion foundVersioned = findOidInIncOids(pci, ((IncVersionedEdTarget) target).getIoid());
          target = new VersionedEdTarget(foundVersioned.getOid(), foundVersioned.getVersion());
          break;
        case unversionedInc:
          OidVersion foundUnversioned = findOidInIncOids(pci, ((IncUnversionedEdTarget) target).getIoid());
          target = new UnversionedEdTarget(foundUnversioned.getOid());
          break;
        default:
          throw new IllegalArgumentException("Unknown node");
      }
    }
    final Edge edge = new Edge((IEdgeTarget) target, incEdge.getLabel());
    final EdgeWithIndex edgeWithIndex = new EdgeWithIndex(edgeIndex, edge);

    /* Add to node */
    newNode.addEdge(edgeWithIndex);

    if (edge.getLabel().isPublic()) {
      /* Add to global */
      accountEdges.getCurrentOutEdges().put(oid, edgeWithIndex);
      accountEdges.getLabelToEdges().put(edge.getLabel(), edgeWithIndex);
    }
  }

  private void removeFromIncubation(AccountNodes accountNodes, NodesSingleClass nsc, long incOid) {
    nsc.getIncOidToIncubationNode().remove(incOid);
    accountNodes.getIncOidToClassId().remove(incOid);
  }
}
