package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.IncOid;
import com.dcrux.buran.coredb.iface.OidVersion;
import com.dcrux.buran.coredb.memoryImpl.data.IncNode;
import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;

import javax.annotation.Nullable;

/**
 *
 * @author caelis
 */
public class PreparedComitInfo {
  private final OidVersion oidToGet;
  private final IncOid ioid;
  private long classId;
  private final boolean isUpdate;
  private final OidVersion oidToUpdate;
  private final long receiverId;
  private final IncNode incNode;
  private final NodeSerie nodeSerie;

  public PreparedComitInfo(OidVersion oidToGet, IncOid ioid, long classId, boolean update,
                           @Nullable OidVersion oidToUpdate, long receiverId, IncNode incNode, NodeSerie nodeSerie) {
    this.oidToGet = oidToGet;
    this.ioid = ioid;
    this.classId = classId;
    this.isUpdate = update;
    this.oidToUpdate = oidToUpdate;
    this.receiverId = receiverId;
    this.incNode = incNode;
    this.nodeSerie = nodeSerie;
  }

  public OidVersion getOidToGet() {
    return oidToGet;
  }

  public IncOid getIoid() {
    return ioid;
  }

  public long getClassId() {
    return classId;
  }

  public boolean isUpdate() {
    return isUpdate;
  }

  public OidVersion getOidToUpdate() {
    return oidToUpdate;
  }

  public long getReceiverId() {
    return receiverId;
  }

  public IncNode getIncNode() {
    return incNode;
  }

  public NodeSerie getNodeSerie() {
    return nodeSerie;
  }
}
