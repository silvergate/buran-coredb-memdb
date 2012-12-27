package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.*;
import com.dcrux.buran.coredb.memoryImpl.data.IncNode;
import com.dcrux.buran.coredb.memoryImpl.data.Node;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 25.12.12
 * Time: 00:27
 * To change this template use File | Settings | File Templates.
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
    final Node inCurrent = this.dataReadApi.getNodeFromCurrent(receiverId, oidVersion);
    if (inCurrent != null) {
      return NodeState.state(NodeState.NodeStateReason.available, NodeState.State.available);
    }
    final Node histOrCur = this.dataReadApi.getNodeFromCurrentOrHistorized(receiverId, oidVersion);
    if (histOrCur != null) {
      return NodeState.state(NodeState.NodeStateReason.historized, NodeState.State.available);
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
    final Node node = this.dataReadApi.getNodeFromCurrentOrHistorized(receiverId, oidVersion);
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
