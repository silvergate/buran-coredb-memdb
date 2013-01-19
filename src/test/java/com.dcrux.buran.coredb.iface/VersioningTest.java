package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VersioningTest extends TestsBase {

    private ClassId classId;

    private void assureNodeDeclared() throws PermissionDeniedException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    @Before
    public void setup() throws PermissionDeniedException {
        assureNodeDeclared();
    }

    @Test
    public void createAndUpdateNode()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            NodeNotUpdatable, HistoryHintNotFulfillable, NodeNotFoundException,
            InformationUnavailableException {
        IApi api = getBuran();

        /* Create a node in incubation */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNidOne = createInfo.getIncNid();

        /* Populate the node in incubation with data */

        /* Integer */
        int intValue = 3320033;
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

        int intValueV1 = 555543;
        api.setData(getReceiver(), getSender(), iNidOneV2, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValueV1));

        /* Commit node version 1 */

        CommitResult commitResultTwo = api.commit(getReceiver(), getSender(), iNidOneV2);
        NidVer nidVer1 = commitResultTwo.getNid(iNidOneV2);
        Assert.assertEquals("Should be second version", NidVer.FIRST_VERSION + 1,
                nidVer1.getVersion());
        Assert.assertEquals("Node-ID from first and second version must be equal", nidVer0.getNid(),
                nidVer1.getNid());

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
            int rV0Int = (int) api
                    .getData(getReceiver(), getSender(), nidVer0, NodeClassSimple.PROPERTY_INT,
                            PrimGet.SINGLETON);
            Assert.assertEquals(rV0Int, intValue);
            String rV0Str = (String) api
                    .getData(getReceiver(), getSender(), nidVer0, NodeClassSimple.PROPERTY_STRING,
                            PrimGet.SINGLETON);
            Assert.assertEquals(rV0Str, strValue);
        }

        /* Check properties of node v1 */
        int rV1Int = (int) api
                .getData(getReceiver(), getSender(), nidVer1, NodeClassSimple.PROPERTY_INT,
                        PrimGet.SINGLETON);
        Assert.assertEquals(rV1Int, intValueV1);
    }

    @Test
    public void optimisticLockingExceptionTest()
            throws PermissionDeniedException, OptimisticLockingException, IncubationNodeNotFound,
            NodeNotUpdatable, HistoryHintNotFulfillable {
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

}