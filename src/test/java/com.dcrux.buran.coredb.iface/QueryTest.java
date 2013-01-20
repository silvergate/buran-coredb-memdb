package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.edgeTargets.IncVersionedEdTarget;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.BlobSet;
import com.dcrux.buran.coredb.iface.propertyTypes.ftsi.FtsiAddText;
import com.dcrux.buran.coredb.iface.propertyTypes.integer.IntEq;
import com.dcrux.buran.coredb.iface.propertyTypes.set.SetAdd;
import com.dcrux.buran.coredb.iface.query.CondCdNode;
import com.dcrux.buran.coredb.iface.query.QueryCdNode;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PropCondition;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class QueryTest extends TestsBase {

    private ClassId classId;

    private void assureNodeDeclared() throws PermissionDeniedException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    /**
     * Try to declare a node-class.
     *
     * @throws com.dcrux.buran.coredb.iface.api.exceptions.PermissionDeniedException
     *
     */
    @Test
    public void declareNode() throws PermissionDeniedException {
        assureNodeDeclared();
    }

    @Test
    public void queryMainTest()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            InformationUnavailableException, NodeNotFoundException, EdgeIndexAlreadySet {
        assureNodeDeclared();
        IApi api = getBuran();

        /* Create a node in incubation - don't update an existing node */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNid = createInfo.getIncNid();

        /* Populate the node in incubation with data */

        /* Integer */
        int intValue = 32323;
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValue));

        /* A String */
        String stringValue = "DAA_STRING";
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_STRING,
                PrimSet.string(stringValue));

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
        final String binaryString = "This string will be converted to binary";
        final byte[] binaryData = binaryString.getBytes();
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_BLOB,
                BlobSet.c(0, binaryData));
        /* Append some more data */
        final String binaryString2 = "Data to append";
        final byte[] binaryData2 = binaryString2.getBytes();
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_BLOB,
                BlobSet.cAppendOnly(binaryData.length, binaryData2));

        /* Append ftsi data */
        final String ftsiText =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis ac nulla non " +
                        "sapien fermentum venenatis.";
        final String moreFtsiText =
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac " +
                        "turpis egestas.";
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_FTSI,
                FtsiAddText.c(ftsiText));
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_FTSI,
                FtsiAddText.c(moreFtsiText));

        /* Create a second node */

        CreateInfo createInfo2 = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNid2 = createInfo2.getIncNid();

        /* Populate the second node in incubation with data */

        /* Integer */
        int intValue2 = 44443;
        api.setData(getReceiver(), getSender(), iNid2, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValue2));

        /* A String */
        String stringValue2 = "DOOOD";
        api.setData(getReceiver(), getSender(), iNid2, NodeClassSimple.PROPERTY_STRING,
                PrimSet.string(stringValue2));

        /* Mutually link the two nodes */

        api.setEdge(getReceiver(), getSender(), iNid, EdgeIndex.c(0), NodeClassSimple.EDGE_ONE,
                IncVersionedEdTarget.c(iNid2));
        api.setEdge(getReceiver(), getSender(), iNid2, EdgeIndex.c(0), NodeClassSimple.EDGE_ONE,
                IncVersionedEdTarget.c(iNid));

        /* Commit node (from incubation to live) */

        CommitResult commitResult = api.commit(getReceiver(), getSender(), iNid, iNid2);

        queryForIntegerEq(intValue);
        queryForIntegerEq(intValue2);

    }

    private void queryForIntegerEq(int realValue) throws PermissionDeniedException {
        IApi api = getBuran();

        /* Integer-Equals: We should find exactly one node */
        QueryCdNode query = QueryCdNode.c(CondCdNode.c(this.classId,
                PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.eq(realValue))));
        QueryResult result = api.query(getReceiver(), getSender(), query, true);
        Assert.assertEquals("Should have one result.", 1,
                (long) result.getNumberOfResultsWithoutLimit().get());
        NidVer foundNode = result.getNodes().get(0);

        /* Integer > (greater): The node previously found must not be in the result-set */
        QueryCdNode query2 = QueryCdNode.c(CondCdNode.c(this.classId,
                PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.gt(realValue))));
        QueryResult result2 = api.query(getReceiver(), getSender(), query2, true);
        Assert.assertTrue(!result2.getNodes().contains(foundNode));

                /* Integer < (lesser): The node previously found must not be in the result-set */
        QueryCdNode query3 = QueryCdNode.c(CondCdNode.c(this.classId,
                PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.le(realValue))));
        QueryResult result3 = api.query(getReceiver(), getSender(), query3, true);
        Assert.assertTrue(!result3.getNodes().contains(foundNode));
    }
}