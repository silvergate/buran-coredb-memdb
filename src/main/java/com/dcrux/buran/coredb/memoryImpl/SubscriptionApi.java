package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.api.exceptions.PermissionDeniedException;
import com.dcrux.buran.coredb.iface.node.NidVer;
import com.dcrux.buran.coredb.iface.query.ICondNode;
import com.dcrux.buran.coredb.iface.subscription.Subscription;
import com.dcrux.buran.coredb.iface.subscription.SubscriptionEventType;
import com.dcrux.buran.coredb.iface.subscription.SubscriptionId;
import com.dcrux.buran.coredb.memoryImpl.data.*;
import com.dcrux.buran.coredb.memoryImpl.query.DataAndMetaMatcher;

import java.util.Collection;

/**
 * Buran.
 *
 * @author: ${USER} Date: 13.01.13 Time: 14:33
 */
public class SubscriptionApi {

    private final Subscriptions subscriptions;
    private final Nodes nodes;
    private final NodeClassesApi ncApi;
    private final DataReadApi drApi;

    public SubscriptionApi(Subscriptions subscriptions, Nodes nodes, NodeClassesApi ncApi,
            DataReadApi drApi) {
        this.subscriptions = subscriptions;
        this.nodes = nodes;
        this.ncApi = ncApi;
        this.drApi = drApi;
    }

    public SubscriptionId addSubscription(final Subscription subscription)
            throws PermissionDeniedException {
        final AccountSubscriptions accountSubscriptions =
                this.subscriptions.getByUserId(subscription.getReceiver().getId());
        int subId = accountSubscriptions.getSubCounter().getAndIncrement();
        accountSubscriptions.getSubscriptionSet().put(subId, subscription);
        for (SubscriptionEventType eventType : subscription.getEventTypes()) {
            accountSubscriptions.getEventTypeToSubscriptions().put(eventType, subscription);
        }
        accountSubscriptions.getSubToId().put(subscription, subId);
        return new SubscriptionId(subId);
    }

    public boolean removeSubscription(long receiverId, long senderId,
            final SubscriptionId subscriptionId) throws PermissionDeniedException {
        final AccountSubscriptions accountSubscriptions =
                this.subscriptions.getByUserId(receiverId);
        final Subscription subscription =
                accountSubscriptions.getSubscriptionSet().get(subscriptionId.getId());
        if (subscription == null) return false;
        accountSubscriptions.getSubscriptionSet().remove(subscriptionId.getId());
        accountSubscriptions.getSubToId().remove(subscription);
        for (SubscriptionEventType eventType : subscription.getEventTypes()) {
            accountSubscriptions.getEventTypeToSubscriptions().remove(eventType, subscription);
        }

        return true;
    }

    public void invoke(NodeImpl nodeImpl, SubscriptionEventType eventType) {
        final long receiverId = nodeImpl.getNodeSerie().getReceiverId();
        final AccountSubscriptions accountSubscriptions =
                this.subscriptions.getByUserId(receiverId);
        final Collection<Subscription> subscriptions =
                accountSubscriptions.getEventTypeToSubscriptions().get(eventType);
        if (subscriptions == null) return;
        final DataAndMetaMatcher dataAndMetaMatcher = new DataAndMetaMatcher();
        final AccountNodes acNodes = nodes.getByUserId(receiverId);
        for (Subscription subscription : subscriptions) {
            if (subscription.getReceiver().getId() != receiverId)
                throw new IllegalStateException("Subscription is in wrong account");
            final ICondNode condition = subscription.getQuery();
            final boolean matches = dataAndMetaMatcher
                    .matches(condition, this.drApi, nodeImpl, this.ncApi, acNodes);
            if (matches) {
                /* Subscription matches */
                NidVer nidVer = new NidVer(nodeImpl.getNodeSerie().getOid(), nodeImpl.getVersion());
                final int subscriptionId = accountSubscriptions.getSubToId().get(subscription);
                try {
                    subscription.getHandler()
                            .handle(nidVer, eventType, new SubscriptionId(subscriptionId));
                } catch (Exception e) {
                    /* Fehler im listener werden abgefangen, damit die weiteren listener auf
                    jeden fall aufgerufen werden. */
                    e.printStackTrace();
                }
            }
        }
    }
}
