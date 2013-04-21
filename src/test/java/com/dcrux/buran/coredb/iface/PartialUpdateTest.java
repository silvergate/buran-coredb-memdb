package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.domains.DomainId;
import com.dcrux.buran.coredb.iface.edge.EdgeIndex;
import com.dcrux.buran.coredb.iface.edge.EdgeLabel;
import com.dcrux.buran.coredb.iface.edge.EdgeType;
import com.dcrux.buran.coredb.iface.edgeTargets.IEdgeTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.IncVersionedEdTarget;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.BlobSet;
import com.dcrux.buran.coredb.iface.propertyTypes.set.SetAdd;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PartialUpdateTest extends TestsBase {

    private ClassId classId;

    @Before
    public void assureNodeDeclared() throws PermissionDeniedException, QuotaExceededException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    private final int intValue = 33454;
    private final String stringValue = "I'm a string stored in the node";
    private final String binaryString = "This string will be converted to binary";
    private final String binaryString2 = "Data to append";
    private DomainId domain1;
    private DomainId domain2;


    private NidVer createNodeAndPopulateWithTestData()
            throws IncubationNodeNotFound, OptimisticLockingException, PermissionDeniedException,
            EdgeIndexAlreadySet, DomainNotFoundException, QuotaExceededException {
        IApi api = getBuran();

         /* Create a node in incubation - don't update an existing node */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNid = createInfo.getIncNid();

        /* Populate the node in incubation with data */

        /* Domains */
        this.domain1 = api.addAnonymousDomain(getReceiver(), getSender());
        this.domain2 = api.addAnonymousDomain(getReceiver(), getSender());
        api.addDomainToNode(getReceiver(), getSender(), iNid, this.domain1);
        api.addDomainToNode(getReceiver(), getSender(), iNid, this.domain2);

        /* Integer */
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(this.intValue));

        /* A String */
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_STRING,
                PrimSet.string(this.stringValue));

        /* The set */
        String setEntry1 = "Set entry one";
        String setEntry2 = "Second set entry";
        Set<ByteContainer> byteContainerSet = new HashSet<ByteContainer>();
        byteContainerSet.add(new ByteContainer(setEntry1.getBytes()));
        byteContainerSet.add(new ByteContainer(setEntry2.getBytes()));
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_SET,
                PrimSet.set(byteContainerSet));
        /* Add one more entry */
        String setEntry3 = "Another entry";
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_SET,
                SetAdd.c(setEntry3.getBytes()));

        /* Add binary data */
        final byte[] binaryData = this.binaryString.getBytes();
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_BLOB,
                BlobSet.c(0, binaryData));
        /* Append some more data */
        final byte[] binaryData2 = this.binaryString2.getBytes();
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_BLOB,
                BlobSet.cAppendOnly(binaryData.length, binaryData2));

        /* Add another node for linking by edge */
        CreateInfo createInfo2 = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNid2 = createInfo.getIncNid();

        /* Link the two nodes by an edge */

        api.setEdge(getReceiver(), getSender(), iNid, EdgeIndex.c(0),
                EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE),
                IncVersionedEdTarget.c(iNid2));

        /* Commit node (from incubation to live) */

        CommitResult commitResult = api.commit(getReceiver(), getSender(), iNid);
        return commitResult.getNid(iNid);
    }

    @Test
    public void partialUpdate()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            InformationUnavailableException, NodeNotFoundException, EdgeIndexAlreadySet,
            NodeNotUpdatable, HistoryHintNotFulfillable, IncompatibleClassException,
            DomainNotFoundException, QuotaExceededException {
        IApi api = getBuran();

        final NidVer originalNode1 = createNodeAndPopulateWithTestData();

        /* Update node and change only the int-value to '16' */

        final int newIntValue1 = 16;
        /* Create an update node */
        final CreateInfoUpdate ciu1 =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        originalNode1, Optional.<HistoryHint>absent());
        /* Transfer data from 'old' node to new node. Transfer everything but the int-value. */
        api.transferData(getReceiver(), getSender(), ciu1.getIncNid(), originalNode1,
                TransferExclusion.c().ex(NodeClassSimple.PROPERTY_INT));
        /* Set the new int-value. */
        api.setData(getReceiver(), getSender(), ciu1.getIncNid(), NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(newIntValue1));
        /* Commit new node */
        final CommitResult cr1 = api.commit(getReceiver(), getSender(), ciu1.getIncNid());

        /* Do some checks */

        final NidVer newNode1 = cr1.getNid(ciu1.getIncNid());

        /* The int-value should now be 16 */
        Assert.assertEquals(newIntValue1, (int) api
                .getData(getReceiver(), getSender(), newNode1, NodeClassSimple.PROPERTY_INT,
                        PrimGet.SINGLETON));
        /* The string-value should still be the same as in the old node. */
        Assert.assertEquals(this.stringValue, (String) api
                .getData(getReceiver(), getSender(), newNode1, NodeClassSimple.PROPERTY_STRING,
                        PrimGet.SINGLETON));
        /* The edge should still be there */
        final Map<EdgeLabel, Map<EdgeIndex, IEdgeTarget>> edges =
                api.getOutEdges(getReceiver(), getSender(), newNode1, EnumSet.allOf(EdgeType.class),
                        Optional.of(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE)));
        Assert.assertEquals("Should have one entry.", 1, edges.size());
        Assert.assertTrue("The edge with label EDGE_ONE at index 0 must be available.",
                edges.get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE))
                        .containsKey(EdgeIndex.c(0)));
        /* The domains should still be there */
        final Set<DomainId> domains = api.getDomains(getReceiver(), getSender(), newNode1);
        Assert.assertEquals("Should be in two domains.", 2, domains.size());
        Assert.assertTrue(domains.contains(this.domain1));
        Assert.assertTrue(domains.contains(this.domain2));
    }
}