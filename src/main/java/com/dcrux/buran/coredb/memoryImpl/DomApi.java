package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.api.exceptions.PermissionDeniedException;
import com.dcrux.buran.coredb.iface.domains.DomainHash;
import com.dcrux.buran.coredb.iface.domains.DomainId;
import com.dcrux.buran.coredb.memoryImpl.data.AccountDomains;
import com.dcrux.buran.coredb.memoryImpl.data.DomainImpl;
import com.dcrux.buran.coredb.memoryImpl.data.Domains;

/**
 * @author caelis
 */
public class DomApi {
    private final Domains domains;

    public DomApi(Domains domains) {
        this.domains = domains;
    }

    public DomainId addAnonymousDomain(long receiverId, long senderId)
            throws PermissionDeniedException {
        final AccountDomains accDomains = this.domains.getByUserId(receiverId);
        final long domainId = accDomains.getDomIdCounter().incrementAndGet();
        DomainImpl di = new DomainImpl(domainId);
        accDomains.getDomainIdToDomain().put(domainId, di);
        return new DomainId(domainId);
    }

    public boolean hasDomain(long receiverId, DomainId domainId) {
        final AccountDomains accDomains = this.domains.getByUserId(receiverId);
        return accDomains.getDomainIdToDomain().containsKey(domainId.getId());
    }

    public DomainId addOrGetIdentifiedDomain(long receiverId, long senderId, DomainHash hash)
            throws PermissionDeniedException {
        final AccountDomains accDomains = this.domains.getByUserId(receiverId);
        final DomainImpl existingDomain = accDomains.getDomainHashToDomain().get(hash);
        if (existingDomain == null) {
            final long domainId = accDomains.getDomIdCounter().incrementAndGet();
            DomainImpl di = new DomainImpl(domainId);
            accDomains.getDomainIdToDomain().put(domainId, di);
            accDomains.getDomainHashToDomain().put(hash, di);
            return new DomainId(domainId);
        } else return new DomainId(existingDomain.getId());
    }
}
