package com.dcrux.buran.coredb.memoryImpl.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caelis
 */
public class Domains implements Serializable {
    public Map<Long, AccountDomains> accountDomains = new HashMap<>();

    public AccountDomains getByUserId(long userId) {
        AccountDomains accDomains = this.accountDomains.get(userId);
        if (accDomains == null) {
            accDomains = new AccountDomains();
            this.accountDomains.put(userId, accDomains);
        }
        return accDomains;
    }
}
