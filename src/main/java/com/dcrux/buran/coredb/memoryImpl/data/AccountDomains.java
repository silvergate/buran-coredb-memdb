package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.domains.DomainHash;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author caelis
 */
public class AccountDomains {
    private final Map<Long, DomainImpl> domainIdToDomain = new HashMap<>();
    private final Map<DomainHash, DomainImpl> domainHashToDomain = new HashMap<>();

    public Map<Long, DomainImpl> getDomainIdToDomain() {
        return domainIdToDomain;
    }

    public Map<DomainHash, DomainImpl> getDomainHashToDomain() {
        return domainHashToDomain;
    }

    private final AtomicLong domIdCounter = new AtomicLong(0L);

    public AtomicLong getDomIdCounter() {
        return domIdCounter;
    }
}
