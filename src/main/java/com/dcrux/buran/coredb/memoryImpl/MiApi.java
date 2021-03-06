package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.NidVer;
import com.dcrux.buran.coredb.iface.NodeState;
import com.dcrux.buran.coredb.iface.api.exceptions.DomainNotFoundException;
import com.dcrux.buran.coredb.iface.api.exceptions.IncubationNodeNotFound;
import com.dcrux.buran.coredb.iface.api.exceptions.NodeNotFoundException;
import com.dcrux.buran.coredb.iface.domains.DomainId;
import com.dcrux.buran.coredb.memoryImpl.data.IncNode;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author caelis
 */
public class MiApi {

    private final DataReadApi dataReadApi;
    private final DmApi dmApi;
    private final DomApi domApi;

    public MiApi(DataReadApi dataReadApi, DmApi dmApi, DomApi domApi) {
        this.dataReadApi = dataReadApi;
        this.dmApi = dmApi;
        this.domApi = domApi;
    }

    @Nullable
    public NodeState getState(long receiverId, long senderId, NidVer nidVer) {
        final NodeImpl inCurrent = this.dataReadApi.getNodeFromCurrent(receiverId, nidVer);
        if (inCurrent != null) {
            return NodeState.available;
        }
        final NodeImpl histOrCur =
                this.dataReadApi.getNodeFromCurrentOrHistorized(receiverId, nidVer);
        if (histOrCur != null) {
            return NodeState.historizedAvailable;
        }
        return null;
    }

    public long getClassId(long receiverId, long senderId, NidVer nidVer)
            throws NodeNotFoundException {
        final NodeImpl inCurrent = this.dataReadApi.getNodeFromCurrent(receiverId, nidVer);
        if (inCurrent == null) {
            throw new NodeNotFoundException("Node not found");
        }
        return inCurrent.getNodeSerie().getClassId();
    }

    private void assureDomainExists(long receiverId, DomainId domainId)
            throws DomainNotFoundException {
        final boolean hasDomain = this.domApi.hasDomain(receiverId, domainId);
        if (!hasDomain) throw new DomainNotFoundException(
                MessageFormat.format("User {0} has no domain {1}.", receiverId, domainId));
    }

    public void addNodeDomain(long receiverId, long senderId, IncNid incNid, DomainId domain)
            throws IncubationNodeNotFound, DomainNotFoundException {
        assureDomainExists(receiverId, domain);
        final IncNode incNode = this.dmApi.getIncNode(receiverId, senderId, incNid);
        if (incNode == null) throw new IncubationNodeNotFound("Node " + incNid + " not found.");
        incNode.getNode().getDomainIds().add(domain.getId());
    }

    public boolean removeNodeDomain(long receiverId, long senderId, IncNid incNid, DomainId domain)
            throws IncubationNodeNotFound, DomainNotFoundException {
        assureDomainExists(receiverId, domain);
        final IncNode incNode = this.dmApi.getIncNode(receiverId, senderId, incNid);
        if (incNode == null) throw new IncubationNodeNotFound("Node " + incNid + " not found.");
        return incNode.getNode().getDomainIds().remove(domain.getId());
    }

    public int removeAllNodeDomains(long receiverId, long senderId, IncNid incNid)
            throws IncubationNodeNotFound {
        final IncNode incNode = this.dmApi.getIncNode(receiverId, senderId, incNid);
        if (incNode == null) throw new IncubationNodeNotFound("Node " + incNid + " not found.");
        int numOfDomains = incNode.getNode().getDomainIds().size();
        incNode.getNode().getDomainIds().clear();
        return numOfDomains;
    }

    public Set<DomainId> getNodeDomains(long receiverId, long senderId, NidVer nidVer)
            throws NodeNotFoundException {
        final NodeImpl node = this.dataReadApi.getNodeFromCurrentOrHistorized(receiverId, nidVer);
        if (node == null) throw new NodeNotFoundException("Node " + nidVer + " not found.");
        final Set<DomainId> domainIds = new HashSet<DomainId>();
        for (final Long domainId : node.getDomainIds()) {
            domainIds.add(new DomainId(domainId));
        }
        return domainIds;
    }
}
