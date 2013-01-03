package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.NidVer;
import com.dcrux.buran.coredb.memoryImpl.data.IncNode;
import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;

import javax.annotation.Nullable;

/**
 * @author caelis
 */
public class PreparedComitInfo {
  private final NidVer oidToGet;
  private final IncNid ioid;
  private long classId;
  private final boolean isUpdate;
  private final NidVer oidToUpdate;
  private final long receiverId;
  private final IncNode incNode;
  private final NodeSerie nodeSerie;

  public PreparedComitInfo(NidVer oidToGet, IncNid ioid, long classId, boolean update, @Nullable NidVer oidToUpdate,
                           long receiverId, IncNode incNode, NodeSerie nodeSerie) {
    this.oidToGet = oidToGet;
    this.ioid = ioid;
    this.classId = classId;
    this.isUpdate = update;
    this.oidToUpdate = oidToUpdate;
    this.receiverId = receiverId;
    this.incNode = incNode;
    this.nodeSerie = nodeSerie;
  }

  public NidVer getOidToGet() {
    return oidToGet;
  }

  public IncNid getIoid() {
    return ioid;
  }

  public long getClassId() {
    return classId;
  }

  public boolean isUpdate() {
    return isUpdate;
  }

  public NidVer getOidToUpdate() {
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
