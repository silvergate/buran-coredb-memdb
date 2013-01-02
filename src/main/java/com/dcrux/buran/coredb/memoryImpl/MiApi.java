package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.memoryImpl.data.IncNode;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author caelis
 */
public class MiApi {

  private final DataReadApi dataReadApi;
  private final DmApi dmApi;

  public MiApi(DataReadApi dataReadApi, DmApi dmApi) {
    this.dataReadApi = dataReadApi;
    this.dmApi = dmApi;
  }

  @Nullable
  public NodeState getState(long receiverId, long senderId, OidVersion oidVersion) {
    final NodeImpl inCurrent = this.dataReadApi.getNodeFromCurrent(receiverId, oidVersion);
    if (inCurrent != null) {
      return NodeState.available;
    }
    final NodeImpl histOrCur = this.dataReadApi.getNodeFromCurrentOrHistorized(receiverId, oidVersion);
    if (histOrCur != null) {
      return NodeState.historizedAvailable;
    }
    return null;
  }

  public void addNodeDomain(long receiverId, long senderId, IncOid incOid, DomainId domain) {
    final IncNode incNode = this.dmApi.getIncNode(receiverId, senderId, incOid);
    incNode.getNode().getDomainIds().add(domain.getId());
  }

  public void removeNodeDomain(long receiverId, long senderId, IncOid incOid, DomainId domain) {
    final IncNode incNode = this.dmApi.getIncNode(receiverId, senderId, incOid);
    incNode.getNode().getDomainIds().remove(domain.getId());
  }

  public void removeAllNodeDomains(long receiverId, long senderId, IncOid incOid) {
    final IncNode incNode = this.dmApi.getIncNode(receiverId, senderId, incOid);
    incNode.getNode().getDomainIds().clear();
  }

  public Set<DomainId> getNodeDomains(long receiverId, long senderId, OidVersion oidVersion) {
    final NodeImpl node = this.dataReadApi.getNodeFromCurrentOrHistorized(receiverId, oidVersion);
    final Set<DomainId> domainIds = new HashSet<DomainId>();
    for (final Long domainId : node.getDomainIds()) {
      domainIds.add(new DomainId(domainId));
    }
    return domainIds;
  }

  public Map<EdgeLabel, Map<EdgeIndex, Edge>> getInEdges(OidVersion oidVersion) {
    throw new NotImplementedException("");
  }
}
