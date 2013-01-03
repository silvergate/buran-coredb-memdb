package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.NidVer;
import com.dcrux.buran.coredb.iface.api.CommitResult;
import com.dcrux.buran.coredb.iface.api.exceptions.ExpectableException;
import com.dcrux.buran.coredb.iface.api.exceptions.OptimisticLockingException;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.memoryImpl.DataReadApi;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;
import com.dcrux.buran.coredb.memoryImpl.PreparedComitInfo;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author caelis
 */
public class AccountNodes {

  private final long receiverId;

  private Map<Long, NodeSerie> oidToAliveSeries = new HashMap<>();
  private Multimap<Long, NodeSerie> classIdToAliveSeries = HashMultimap.create();
  private Map<Long, NodeSerie> oidToRemovedEmptyAndAliveSeries = new HashMap<>();

  private Map<Long, IncNode> incOidToIncNodes = new HashMap<>();

  private final CommitUtil commitUtil = new CommitUtil();

  public AccountNodes(long receiverId) {
    this.receiverId = receiverId;
  }

  public Map<Long, NodeSerie> getOidToAliveSeries() {
    return oidToAliveSeries;
  }

  public Multimap<Long, NodeSerie> getClassIdToAliveSeries() {
    return classIdToAliveSeries;
  }

  public IncNid createNew(long senderId, long classId, @Nullable NidVer toUpdate, NodeClassesApi ncApi) {
    final long incOid = getIncOidCounter().incrementAndGet();
    final NodeClass nc = ncApi.getClassById(classId);
    if (nc == null) {
      throw new IllegalStateException("NodeClass not found");
    }

    final int version;
    if (toUpdate == null) {
      version = NodeSerie.FIRST_VERSION;
    } else {
      version = toUpdate.getVersion() + 1;
    }

    final NodeImpl newNode = new NodeImpl(version, senderId, receiverId, 0L, 0L, new Object[nc.getNumberOfTypes()]);
    final IncNode incNode = new IncNode(toUpdate, newNode, receiverId, classId);

    /* Add data */
    getIncOidToIncNodes().put(incOid, incNode);
    final IncNid incNidNew = new IncNid(incOid);
    return incNidNew;
  }

  @Nullable
  public IncNode getIncNode(long incOid) {
    return getIncOidToIncNodes().get(incOid);
  }

  private void addNode(final NodeImpl node) {
    final NodeSerie ns = node.getNodeSerie();
    assert (ns != null);
    if (ns.isMarkedAsDeleted()) {
      throw new ExpectableException("NodeImpl serie is marked as deleted. Cannot add a new node.");
    }

    boolean nsWasEmptyBefore = ns.getCurrentVersion() == null;

    if (nsWasEmptyBefore) {
      addNewNodeSerie(ns);
    }

    ns.addNewVersion(node);

    if (nsWasEmptyBefore) {
      this.oidToAliveSeries.put(ns.getOid(), ns);
      this.classIdToAliveSeries.put(ns.getClassId(), ns);
    }
  }

  public void markAsDeleted(long oid, long currentTime) {
    if (true) {
      throw new NotImplementedException("SCHEISS IMPLEMENTATION, NEU MACHEN!");
    }
    final NodeSerie ns = this.oidToAliveSeries.get(oid);
    assert (ns != null);
    ns.markAsDeleted(currentTime);
    this.oidToAliveSeries.remove(oid);
    this.classIdToAliveSeries.remove(ns.getClassId(), ns);
  }

  private void addNewNodeSerie(NodeSerie nodeSerie) {
    if (nodeSerie.getCurrentVersion() != null) {
      throw new ExpectableException("NodeImpl serie already contains nodes.");
    }
    this.oidToRemovedEmptyAndAliveSeries.put(nodeSerie.getOid(), nodeSerie);
  }

  @Nullable
  public NodeSerie getNodeSerieByOid(long oid, boolean aliveOnly) {
    if (!aliveOnly) {
      return this.oidToRemovedEmptyAndAliveSeries.get(oid);
    } else {
      return this.oidToAliveSeries.get(oid);
    }
  }

  @Nullable
  public NodeImpl getNode(long oid, int version, boolean currentOnly) {
    final NodeSerie ns = getNodeSerieByOid(oid, true);
    if (ns == null) {
      return null;
    }
    final Integer curVer = ns.getCurrentVersion();
    if (curVer == null) {
      return null;
    }
    if ((curVer != version) && (currentOnly)) {
      return null;
    }
    return ns.getNode(version);
  }

  @Nullable
  public NodeImpl getCurrentNode(long oid) {
    NodeSerie ns = getNodeSerieByOid(oid, true);
    if (ns == null) {
      return null;
    }
    if (ns.getCurrentVersion() == null) {
      return null;
    }
    return ns.getNode(ns.getCurrentVersion());
  }

  public Map<Long, IncNode> getIncOidToIncNodes() {
    return incOidToIncNodes;
  }

  private transient AtomicLong incOidCounter = new AtomicLong(0L);
  private transient AtomicLong oidCounter = new AtomicLong(0L);

  public AtomicLong getIncOidCounter() {
    return incOidCounter;
  }

  public AtomicLong getOidCounter() {
    return oidCounter;
  }

  public CommitResult commit(long senderId, Set<IncNid> incNids, DataReadApi drApi, NodeClassesApi ncApi) throws
          OptimisticLockingException {
    // TODO: Missing write lock
      /* OIDs von allen extrahieren */
    final Set<PreparedComitInfo> prepComInfo = this.commitUtil.generateOidsFromIoids(senderId, incNids, this);
      /* Validate */
    this.commitUtil.validate(prepComInfo, drApi, ncApi);

        /* Commit node */
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      commitNodeAndEdges(prepComInfo, senderId, pciEntry);
    }

    final Map<IncNid, NidVer> incOidToOidVer = new HashMap<>();
    for (final PreparedComitInfo pciEntry : prepComInfo) {
      incOidToOidVer.put(pciEntry.getIoid(), pciEntry.getOidToGet());
    }
    return new CommitResult(incOidToOidVer);
  }

  private void commitNodeAndEdges(Set<PreparedComitInfo> pci, final long senderId, PreparedComitInfo pciEntry) {
    final IncNid incNid = pciEntry.getIoid();
    final long classId = pciEntry.getClassId();

    final IncNode incNode = pciEntry.getIncNode();

    /* Remove Incubation Data */
    getIncOidToIncNodes().remove(incNid.getId());

    /* Set data */
    final AccountNodes accountNodes = this;
    final long oid = pciEntry.getOidToGet().getOid();
    final int version = pciEntry.getOidToGet().getVersion();
    final NidVer nidVer = new NidVer(oid, version);

    /* Set valid from time */
    long currentTime = System.currentTimeMillis();
    incNode.getNode().setValidFrom(currentTime);

     /* Add edges from inc to node */
    for (Map.Entry<IncNode.EdgeIndexLabel, IncubationEdge> incEdgeEntry : incNode.getIncubationEdges().entrySet()) {
      this.commitUtil.addEdge(pci, incNode.getNode(), incEdgeEntry.getValue(), incEdgeEntry.getKey().getIndex(), this);
    }

    /* Add node */
    addNode(incNode.getNode());
  }
}
