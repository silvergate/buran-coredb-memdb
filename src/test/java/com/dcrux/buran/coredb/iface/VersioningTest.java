package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.apiData.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.node.IncNid;
import com.dcrux.buran.coredb.iface.node.NidVer;
import com.dcrux.buran.coredb.iface.node.NodeState;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VersioningTest extends TestsBase {

    private ClassId classId;
    private NidVer nodeOneId;

    private void assureNodeDeclared() throws PermissionDeniedException, QuotaExceededException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    @Before
    public void setup() throws PermissionDeniedException, QuotaExceededException {
        assureNodeDeclared();
    }

    @Test
    public void createAndUpdateNode()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            NodeNotUpdatable, HistoryHintNotFulfillable, NodeNotFoundException,
            InformationUnavailableException, QuotaExceededException {
        IApi api = getBuran();

        /* Create a node in incubation */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNidOne = createInfo.getIncNid();

        /* Populate the node in incubation with data */

        /* Integer */
        Integer intValue = 3320033;
        api.setData(getReceiver(), getSender(), iNidOne, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValue));
        String strValue = "I'm a text...";
        api.setData(getReceiver(), getSender(), iNidOne, NodeClassSimple.PROPERTY_STRING,
                PrimSet.string(strValue));

        /* Commit node */

        CommitResult commitResultOne = api.commit(getReceiver(), getSender(), iNidOne);
        NidVer nidVer0 = commitResultOne.getNid(iNidOne);
        Assert.assertEquals("Should be first version", NidVer.FIRST_VERSION, nidVer0.getVersion());

        /* Create a new node in incubation and update 'nidVer0' node */

        final CreateInfoUpdate createInfoUpdate =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        nidVer0, Optional.<HistoryHint>absent());
        final IncNid iNidOneV2 = createInfoUpdate.getIncNid();

        /* Populate node version 1 with some data */

        Integer intValueV1 = 555543;
        api.setData(getReceiver(), getSender(), iNidOneV2, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValueV1));

        /* Commit node version 1 */

        CommitResult commitResultTwo = api.commit(getReceiver(), getSender(), iNidOneV2);
        NidVer nidVer1 = commitResultTwo.getNid(iNidOneV2);
        Assert.assertEquals("Should be second version", NidVer.FIRST_VERSION + 1,
                nidVer1.getVersion());
        Assert.assertEquals("Node-ID from first and second version must be equal", nidVer0.getNid(),
                nidVer1.getNid());
        this.nodeOneId = nidVer1;

        /* Check node states */
        final NodeState v0State = api.getNodeState(getReceiver(), getSender(), nidVer0);
        final NodeState v1State = api.getNodeState(getReceiver(), getSender(), nidVer1);
        Assert.assertTrue("Version 0 Node must be in state 'historized'",
                NodeState.isHistorized(v0State));
        Assert.assertEquals("Version 1 Node must be in state 'active'", NodeState.available,
                v1State);

        /* Check properties of node v0 */
        /* Checking is only possible if history information is still available */
        if (v0State == NodeState.historizedAvailable) {
            Integer rV0Int =
                    api.getData(getReceiver(), getSender(), nidVer0, NodeClassSimple.PROPERTY_INT,
                            PrimGet.INTEGER);
            Assert.assertEquals(rV0Int, intValue);
            String rV0Str = (String) api
                    .getData(getReceiver(), getSender(), nidVer0, NodeClassSimple.PROPERTY_STRING,
                            PrimGet.STRING);
            Assert.assertEquals(rV0Str, strValue);
        }

        /* Check properties of node v1 */
        Integer rV1Int =
                api.getData(getReceiver(), getSender(), nidVer1, NodeClassSimple.PROPERTY_INT,
                        PrimGet.INTEGER);
        Assert.assertEquals(rV1Int, intValueV1);
    }

    @Test
    public void optimisticLockingExceptionTest()
            throws PermissionDeniedException, OptimisticLockingException, IncubationNodeNotFound,
            NodeNotUpdatable, HistoryHintNotFulfillable, QuotaExceededException {
        IApi api = getBuran();

        /* Create a node in incubation */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNidOne = createInfo.getIncNid();

        /* Commit node */

        CommitResult commitResultOne = api.commit(getReceiver(), getSender(), iNidOne);
        NidVer nidVer0 = commitResultOne.getNid(iNidOne);
        Assert.assertEquals("Should be first version", NidVer.FIRST_VERSION, nidVer0.getVersion());

        /* Try to update node v0 twice. This should work until we try to commit. */
        final CreateInfoUpdate createInfoUpdate11 =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        nidVer0, Optional.<HistoryHint>absent());
        final CreateInfoUpdate createInfoUpdate12 =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        nidVer0, Optional.<HistoryHint>absent());
        final IncNid iNidOneV11 = createInfoUpdate11.getIncNid();
        final IncNid iNidOneV12 = createInfoUpdate12.getIncNid();

        /* Commit first update node - this should work */
        CommitResult commitResultTwo1 = api.commit(getReceiver(), getSender(), iNidOneV11);

        boolean exceptionRaised = false;
        try {
        /* Commit second update node - this should raise an OptimisticLockingException */
            CommitResult commitResultTwo2 = api.commit(getReceiver(), getSender(), iNidOneV12);
        } catch (OptimisticLockingException ole) {
            exceptionRaised = true;
        }
        Assert.assertTrue("The second commit must raise an OptimisticLockingException.",
                exceptionRaised);
    }

    @Test
    public void deleteTest() throws PermissionDeniedException, InformationUnavailableException,
            NodeNotFoundException, EdgeIndexAlreadySet, OptimisticLockingException,
            IncubationNodeNotFound, NodeNotUpdatable, HistoryHintNotFulfillable,
            NotUpdatingException, QuotaExceededException {
        IApi api = getBuran();

        if (this.nodeOneId == null) createAndUpdateNode();

        final CreateInfoUpdate createInfoUpdate =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        this.nodeOneId, Optional.<HistoryHint>absent());

        /* Mark node in incubation as deleted */
        api.markNodeAsDeleted(getReceiver(), getSender(), createInfoUpdate.getIncNid());
        /* Commit */
        final CommitResult commitResult =
                api.commit(getReceiver(), getSender(), createInfoUpdate.getIncNid());
        NidVer nidVer = commitResult.getNid(createInfoUpdate.getIncNid());

        Assert.assertEquals("Deleted node should have a new version", nidVer.getVersion() - 1,
                this.nodeOneId.getVersion());
        Assert.assertEquals("Deleted node should have the same node-id", nidVer.getNid(),
                this.nodeOneId.getNid());

        /* If we mark a node as deleted it will be inexistent. */
        boolean nodeNotFoundExceptionThrown = false;
        try {
            final NodeState state = api.getNodeState(getReceiver(), getSender(), nidVer);
        } catch (NodeNotFoundException nnfe) {
            nodeNotFoundExceptionThrown = true;
        }
        Assert.assertTrue("A node marked as deleted should be inexistent.",
                nodeNotFoundExceptionThrown);

        /* The previous version is historized */
        final NodeState state2 = api.getNodeState(getReceiver(), getSender(), this.nodeOneId);
        Assert.assertTrue("The previous version should be historized.",
                NodeState.isHistorized(state2));

        NidVer currentVersion =
                api.getCurrentNodeVersion(getReceiver(), getSender(), this.nodeOneId);
        Assert.assertNull("There should be no current version since the node has been deleted.",
                currentVersion);
        NidVer currentVersionOk =
                api.getLatestVersionBeforeDeletion(getReceiver(), getSender(), this.nodeOneId);
        Assert.assertEquals("The latest version should be the version marked as deleted.",
                this.nodeOneId, currentVersionOk);
    }
}