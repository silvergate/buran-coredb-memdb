package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.subscription.Subscription;
import com.dcrux.buran.coredb.iface.subscription.SubscriptionEventType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Buran.
 *
 * @author: ${USER} Date: 13.01.13 Time: 14:37
 */
public class AccountSubscriptions implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3778490215059336614L;
    private final long receiverId;

    public AccountSubscriptions(long receiverId) {
        this.receiverId = receiverId;
    }

    private final AtomicInteger subCounter = new AtomicInteger(0);

    public AtomicInteger getSubCounter() {
        return subCounter;
    }

    private Map<Integer, Subscription> subIdsToSubscriptions = new HashMap<>();
    private Multimap<SubscriptionEventType, Subscription> eventTypeToSubscriptions =
            HashMultimap.create();
    private Map<Subscription, Integer> subToId = new HashMap<>();

    public Map<Subscription, Integer> getSubToId() {
        return subToId;
    }

    public Multimap<SubscriptionEventType, Subscription> getEventTypeToSubscriptions() {
        return eventTypeToSubscriptions;
    }

    public Map<Integer, Subscription> getSubscriptionSet() {
        return subIdsToSubscriptions;
    }
}
