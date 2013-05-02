package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.apiData.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.node.IncNid;
import com.dcrux.buran.coredb.iface.node.NidVer;
import com.dcrux.buran.coredb.iface.node.NodeMetadata;
import com.dcrux.buran.coredb.iface.node.NodeState;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MetadataTest extends TestsBase {

    private ClassId classId;
    private NidVer nodeOneId;

    private void assureNodeDeclared() throws PermissionDeniedException, QuotaExceededException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    @Before
    public void setup() throws PermissionDeniedException, QuotaExceededException {
        assureNodeDeclared();
    }

    private void checkOne(NidVer node)
            throws PermissionDeniedException, NodeNotFoundException, QuotaExceededException,
            VersionNotFoundException {
        IApi api = getBuran();
        NodeMetadata metadata = api.getNodeMeta(getReceiver(), getSender(), node);

        Assert.assertEquals("Should be current version", metadata.isCurrent(), true);
        Assert.assertFalse("Schould not have a valid to time",
                metadata.getValidToTime().isPresent());
    }

    private void checkTwo(NidVer node0, NidVer node1)
            throws PermissionDeniedException, NodeNotFoundException, QuotaExceededException,
            VersionNotFoundException {
        IApi api = getBuran();
        NodeMetadata metadata0 = api.getNodeMeta(getReceiver(), getSender(), node0);
        NodeMetadata metadata1 = api.getNodeMeta(getReceiver(), getSender(), node1);

        Assert.assertTrue("Should have a valid-to-time", metadata0.getValidToTime().isPresent());
        Assert.assertFalse("must not be current", metadata0.isCurrent());
        Assert.assertEquals("Wrong latest version", metadata0.getLatestVersion(),
                node1.getVersion());

        Assert.assertEquals("Should be current version", metadata1.isCurrent(), true);
        Assert.assertFalse("Should not have a valid to time",
                metadata1.getValidToTime().isPresent());
    }

    private void checkThree(NidVer versionBeforeDeletion, NidVer deletedNode)
            throws PermissionDeniedException, NodeNotFoundException, QuotaExceededException,
            VersionNotFoundException {
        IApi api = getBuran();

        boolean versionNotFound = false;
        NodeMetadata metadata0 = api.getNodeMeta(getReceiver(), getSender(), versionBeforeDeletion);
        try {
            NodeMetadata metadata1 = api.getNodeMeta(getReceiver(), getSender(), deletedNode);
        } catch (VersionNotFoundException vnfe) {
            versionNotFound = true;
        }

        Assert.assertTrue("Wrong version",
                versionBeforeDeletion.getVersion() + 1 == deletedNode.getVersion());
        Assert.assertTrue("The deleted node must not exist as version.", versionNotFound);
        Assert.assertFalse(metadata0.isCurrent());
        Assert.assertEquals("Deleted node should be latest version.",
                metadata0.getLatestVersionBeforeDeletion(), versionBeforeDeletion.getVersion());
        Assert.assertTrue("Should be marked as deleted.", metadata0.isMarkedAsDeleted());
    }

    @Test
    public void createAndUpdateNode()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            NodeNotUpdatable, HistoryHintNotFulfillable, NodeNotFoundException,
            InformationUnavailableException, QuotaExceededException, VersionNotFoundException {
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

        checkOne(nidVer0);

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

        checkTwo(nidVer0, nidVer1);
    }

    @Test
    public void deleteTest() throws PermissionDeniedException, InformationUnavailableException,
            NodeNotFoundException, EdgeIndexAlreadySet, OptimisticLockingException,
            IncubationNodeNotFound, NodeNotUpdatable, HistoryHintNotFulfillable,
            NotUpdatingException, QuotaExceededException, VersionNotFoundException {
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
        boolean versionNotFoundThrown = false;
        try {
            final NodeState state = api.getNodeState(getReceiver(), getSender(), nidVer);
        } catch (VersionNotFoundException nnfe) {
            versionNotFoundThrown = true;
        }
        Assert.assertTrue("A node marked as deleted should be inexistent.", versionNotFoundThrown);

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

        checkThree(this.nodeOneId, nidVer);
    }
}