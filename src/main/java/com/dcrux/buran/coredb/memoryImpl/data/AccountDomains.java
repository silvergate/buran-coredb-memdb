package com.dcrux.buran.coredb.memoryImpl.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author caelis
 */
public class AccountDomains {
    private final Map<Long, DomainImpl> domainIdToDomain = new HashMap<>();

    public Map<Long, DomainImpl> getDomainIdToDomain() {
        return domainIdToDomain;
    }
}
