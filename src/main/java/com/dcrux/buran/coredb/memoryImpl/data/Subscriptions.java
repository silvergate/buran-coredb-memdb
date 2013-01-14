package com.dcrux.buran.coredb.memoryImpl.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Buran.
 *
 * @author: ${USER} Date: 13.01.13 Time: 14:36
 */
public class Subscriptions {
    private Map<Long, AccountSubscriptions> receiverIdToSubscriptions = new HashMap<>();

    public AccountSubscriptions getByUserId(final long userId) {
        AccountSubscriptions as = this.receiverIdToSubscriptions.get(userId);
        if (as == null) {
            as = new AccountSubscriptions(userId);
            this.receiverIdToSubscriptions.put(userId, as);
        }
        return as;
    }
}
