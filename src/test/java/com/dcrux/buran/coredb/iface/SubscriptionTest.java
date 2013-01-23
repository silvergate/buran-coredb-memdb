package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.propertyTypes.integer.IntEq;
import com.dcrux.buran.coredb.iface.propertyTypes.string.StringEq;
import com.dcrux.buran.coredb.iface.query.CondCdNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.SenderIsIn;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PropCondition;
import com.dcrux.buran.coredb.iface.subscription.ISubscriptionEventHandler;
import com.dcrux.buran.coredb.iface.subscription.Subscription;
import com.dcrux.buran.coredb.iface.subscription.SubscriptionEventType;
import com.dcrux.buran.coredb.iface.subscription.SubscriptionId;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SubscriptionTest extends TestsBase {

    private ClassId classId;

    private void assureNodeDeclared() throws PermissionDeniedException, QuotaExceededException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    @Test
    public void subscriptionTests()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            InformationUnavailableException, NodeNotFoundException, EdgeIndexAlreadySet,
            DomainNotFoundException, NodeNotUpdatable, HistoryHintNotFulfillable,
            NotUpdatingException, QuotaExceededException {
        assureNodeDeclared();
        IApi api = getBuran();

        /* Information about raised events */
        final List<RaiseInfo> raisedEvents = new ArrayList<RaiseInfo>();

        /* Create subscription 1 */
        final String sub1String = "This is a string to query in subscription 1";
        /* Subscription condition */
        final CondCdNode sub1Cond = CondCdNode.c(this.classId, SenderIsIn.c(getSender()),
                PropCondition.c(NodeClassSimple.PROPERTY_STRING, StringEq.eq(sub1String)));
        final ISubscriptionEventHandler sub1Handler = new ISubscriptionEventHandler() {
            @Override
            public void handle(NidVer node, SubscriptionEventType eventType, SubscriptionId id) {
                /* Event occurred: Something happened in commit with a node where sender =
                getSender() and PROPERTY_STRING = sub1String */
                raisedEvents.add(new RaiseInfo(node, eventType, 1));
            }
        };
        final Subscription subscription1 = new Subscription(getReceiver(), getSender(),
                EnumSet.allOf(SubscriptionEventType.class), sub1Cond, sub1Handler);

        final SubscriptionId sub2Id = api.addSubscription(subscription1);

        /* Create subscription 2 */
        final int intGtValue = 4453;
        /* Subscription condition */
        final CondCdNode sub2Cond = CondCdNode.c(this.classId, SenderIsIn.c(getSender()),
                PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.gt(intGtValue)));
        final ISubscriptionEventHandler sub2Handler = new ISubscriptionEventHandler() {
            @Override
            public void handle(NidVer node, SubscriptionEventType eventType, SubscriptionId id) {
                /* Event occurred: Something happened in commit with a node where sender =
                getSender() and PROPERTY_INT > intGtValue */
                raisedEvents.add(new RaiseInfo(node, eventType, 2));
            }
        };
        final Subscription subscription2 = new Subscription(getReceiver(), getSender(),
                EnumSet.allOf(SubscriptionEventType.class), sub2Cond, sub2Handler);

        final SubscriptionId sub1Id = api.addSubscription(subscription2);

        /* Do some modifications and check result */

        /* Add a node, this should raise subscription-event 2, but not event 1. */
        NidVer nidVer1 = addNodeWithIntValue(api, intGtValue + 10);
        Assert.assertEquals(1, raisedEvents.size());
        Assert.assertTrue(
                raisedEvents.contains(new RaiseInfo(nidVer1, SubscriptionEventType.newNode, 2)));
        raisedEvents.clear();

        /* Update the previously created node, this should raise two events: One for the updated
        node (historized after the call) and one for the updating node. */
        NidVer nidVer2 = updateNodeWithIntValue(api, nidVer1, intGtValue + 10);
        Assert.assertEquals(2, raisedEvents.size());
        Assert.assertTrue(raisedEvents
                .contains(new RaiseInfo(nidVer1, SubscriptionEventType.nodeHistorizedReplaced, 2)));
        Assert.assertTrue(raisedEvents
                .contains(new RaiseInfo(nidVer2, SubscriptionEventType.nodeUpdatingOther, 2)));
        raisedEvents.clear();

        /* Update node 2, this should raise subscription 1 and subscription 2. */
        NidVer nidVer3 = updateNodeWithStringValue(api, nidVer2, sub1String);
        Assert.assertEquals(2, raisedEvents.size());
        Assert.assertTrue(raisedEvents
                .contains(new RaiseInfo(nidVer2, SubscriptionEventType.nodeHistorizedReplaced, 2)));
        Assert.assertTrue(raisedEvents
                .contains(new RaiseInfo(nidVer3, SubscriptionEventType.nodeUpdatingOther, 1)));
        raisedEvents.clear();

        /* Update node 3 with node 4 with a string that does not match subscription 1. But
        updating node 3 will raise subscription 1. */
        NidVer nidVer4 = updateNodeWithStringValue(api, nidVer3, "SOME TEXT 323423423423");
        Assert.assertEquals(1, raisedEvents.size());
        Assert.assertTrue(raisedEvents
                .contains(new RaiseInfo(nidVer3, SubscriptionEventType.nodeHistorizedReplaced, 1)));
        raisedEvents.clear();

        /* Add a new node 5 */
        NidVer nidVer5 = addNodeWithIntValue(api, intGtValue + 10);
        /* Remove node 5 */
        deleteNode(api, nidVer5);
        /* Adding and removing should raise 2 subscription events: One for adding node 5
        (EventType = 'newNode') and one for removing node 5 (EventType = 'nodeHistorized'). */
        Assert.assertEquals(2, raisedEvents.size());
        Assert.assertTrue(raisedEvents
                .contains(new RaiseInfo(nidVer5, SubscriptionEventType.nodeHistorized, 2)));
        Assert.assertTrue(
                raisedEvents.contains(new RaiseInfo(nidVer5, SubscriptionEventType.newNode, 2)));
        raisedEvents.clear();

        /* Remove subscriptions */
        api.removeSubscription(getReceiver(), getSender(), sub1Id);
        api.removeSubscription(getReceiver(), getSender(), sub2Id);

        /* Add a new node 6 */
        NidVer nidVer6 = addNodeWithIntValue(api, intGtValue + 10);
        /* No subscription event should be raised, since we've removed the subscriptions. */
        Assert.assertEquals(0, raisedEvents.size());
    }

    private NidVer addNodeWithIntValue(IApi api, int intValue)
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            QuotaExceededException {
        /* Create a node in incubation - don't update an existing node */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNid = createInfo.getIncNid();
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValue));
        CommitResult commitResult = api.commit(getReceiver(), getSender(), iNid);
        return commitResult.getNid(iNid);
    }

    private NidVer deleteNode(IApi api, NidVer toUpdate)
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            NodeNotUpdatable, HistoryHintNotFulfillable, NotUpdatingException,
            QuotaExceededException {
        /* Create a node in incubation - don't update an existing node */
        CreateInfoUpdate createInfo =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        toUpdate, Optional.<HistoryHint>absent());
        IncNid iNid = createInfo.getIncNid();
        api.markNodeAsDeleted(getReceiver(), getSender(), iNid);
        CommitResult commitResult = api.commit(getReceiver(), getSender(), iNid);
        return commitResult.getNid(iNid);
    }

    private NidVer updateNodeWithIntValue(IApi api, NidVer toUpdate, int intValue)
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            NodeNotUpdatable, HistoryHintNotFulfillable, QuotaExceededException {
        /* Create a node in incubation - don't update an existing node */
        CreateInfoUpdate createInfo =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        toUpdate, Optional.<HistoryHint>absent());
        IncNid iNid = createInfo.getIncNid();
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValue));
        CommitResult commitResult = api.commit(getReceiver(), getSender(), iNid);
        return commitResult.getNid(iNid);
    }

    private NidVer updateNodeWithStringValue(IApi api, NidVer toUpdate, String strValue)
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            NodeNotUpdatable, HistoryHintNotFulfillable, QuotaExceededException {
        /* Create a node in incubation - don't update an existing node */
        CreateInfoUpdate createInfo =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        toUpdate, Optional.<HistoryHint>absent());
        IncNid iNid = createInfo.getIncNid();
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_STRING,
                PrimSet.string(strValue));
        CommitResult commitResult = api.commit(getReceiver(), getSender(), iNid);
        return commitResult.getNid(iNid);
    }

    private class RaiseInfo {
        private final NidVer nidVer;
        private final SubscriptionEventType eventType;
        private final int handlerId;

        private RaiseInfo(NidVer nidVer, SubscriptionEventType eventType, int handlerId) {
            this.nidVer = nidVer;
            this.eventType = eventType;
            this.handlerId = handlerId;
        }

        public NidVer getNidVer() {
            return nidVer;
        }

        public SubscriptionEventType getEventType() {
            return eventType;
        }

        public int getHandlerId() {
            return handlerId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RaiseInfo raiseInfo = (RaiseInfo) o;

            if (handlerId != raiseInfo.handlerId) return false;
            if (eventType != raiseInfo.eventType) return false;
            if (!nidVer.equals(raiseInfo.nidVer)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = nidVer.hashCode();
            result = 31 * result + eventType.hashCode();
            result = 31 * result + handlerId;
            return result;
        }
    }
}