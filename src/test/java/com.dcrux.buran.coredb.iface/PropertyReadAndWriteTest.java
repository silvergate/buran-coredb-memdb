package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.CommitResult;
import com.dcrux.buran.coredb.iface.api.CreateInfo;
import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.KeepAliveHint;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.BlobGet;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.BlobSet;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.LengthGet;
import com.dcrux.buran.coredb.iface.propertyTypes.set.SetAdd;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class PropertyReadAndWriteTest extends TestsBase {

    private ClassId classId;

    private void assureNodeDeclared() throws PermissionDeniedException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    /**
     * Declaring the same node-class twice should return the same hash.
     */
    @Test
    public void sameHashTest() throws PermissionDeniedException {
        NodeClassHash hash1 = getBuran().declareClass(NodeClassSimple.create());
        NodeClassHash hash2 = getBuran().declareClass(NodeClassSimple.create());

        Assert.assertEquals(hash1.getHash(), hash2.getHash());
    }

    /**
     * Try to declare a node-class.
     *
     * @throws PermissionDeniedException
     */
    @Test
    public void declareNode() throws PermissionDeniedException {
        assureNodeDeclared();
    }

    @Test
    public void createIncubationNode()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            InformationUnavailableException, NodeNotFoundException {
        assureNodeDeclared();
        IApi api = getBuran();

        /* Create a node in incubation - don't update an existing node */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNid = createInfo.getIncNid();

        /* Populate the node in incubation with data */

        /* Integer */
        int intValue = 33454;
        api.setData(getReceiver(), getSender(), iNid, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValue));

        /* A String */
        String stringValue = "I'm a string stored in the node";
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

        /* Commit node (from incubation to live) */

        CommitResult commitResult = api.commit(getReceiver(), getSender(), iNid);

        /* Read node data */

        NidVer nid = commitResult.getNid(iNid);
        /* First version? */
        Assert.assertEquals(NidVer.FIRST_VERSION, nid.getVersion());

        /* Read the integer */
        int rIntValue = (int) api
                .getData(getReceiver(), getSender(), nid, NodeClassSimple.PROPERTY_INT,
                        PrimGet.SINGLETON);
        Assert.assertEquals(intValue, rIntValue);

        /* Read the string */
        String rStringValue = (String) api
                .getData(getReceiver(), getSender(), nid, NodeClassSimple.PROPERTY_STRING,
                        PrimGet.SINGLETON);
        Assert.assertEquals(stringValue, rStringValue);

        /* Read the set */
        Set<ByteContainer> rSetValue = (Set<ByteContainer>) api
                .getData(getReceiver(), getSender(), nid, NodeClassSimple.PROPERTY_SET,
                        PrimGet.SINGLETON);
        Assert.assertTrue(rSetValue.size() == byteContainerSet.size() + 1);
        Assert.assertTrue(rSetValue.containsAll(byteContainerSet));
        Assert.assertTrue(rSetValue.contains(new ByteContainer(setEntry3.getBytes())));

        /* Read binary length & data */
        int rLength = (int) api
                .getData(getReceiver(), getSender(), nid, NodeClassSimple.PROPERTY_BLOB,
                        LengthGet.SINGLETON);
        int expectedLength = binaryData.length + binaryData2.length;
        Assert.assertEquals(expectedLength, rLength);
        byte[] rData1 = (byte[]) api
                .getData(getReceiver(), getSender(), nid, NodeClassSimple.PROPERTY_BLOB,
                        BlobGet.c(binaryData.length));
        byte[] rData2 = (byte[]) api
                .getData(getReceiver(), getSender(), nid, NodeClassSimple.PROPERTY_BLOB,
                        BlobGet.cSkip(binaryData.length, binaryData2.length));
        final String rData1AsString = new String(rData1);
        final String rData2AsString = new String(rData2);
        Assert.assertEquals(binaryString, rData1AsString);
        Assert.assertEquals(binaryString2, rData2AsString);
    }
}