package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.apiData.CommitResult;
import com.dcrux.buran.coredb.iface.api.apiData.CreateInfo;
import com.dcrux.buran.coredb.iface.api.apiData.KeepAliveHint;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.domains.DomainId;
import com.dcrux.buran.coredb.iface.edge.EdgeIndex;
import com.dcrux.buran.coredb.iface.edge.EdgeLabel;
import com.dcrux.buran.coredb.iface.edgeTargets.IncVersionedEdTarget;
import com.dcrux.buran.coredb.iface.node.IncNid;
import com.dcrux.buran.coredb.iface.node.NidVer;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.BlobSet;
import com.dcrux.buran.coredb.iface.propertyTypes.ftsi.FtsiAddText;
import com.dcrux.buran.coredb.iface.propertyTypes.integer.IntEq;
import com.dcrux.buran.coredb.iface.propertyTypes.set.SetAdd;
import com.dcrux.buran.coredb.iface.propertyTypes.string.StringEq;
import com.dcrux.buran.coredb.iface.query.*;
import com.dcrux.buran.coredb.iface.query.edgeCondition.OutEdgeCondition;
import com.dcrux.buran.coredb.iface.query.nodeMeta.*;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PcIntersection;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PcInverse;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PcUnion;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PropCondition;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class QueryTest extends TestsBase {

    private ClassId classId;

    private void assureNodeDeclared() throws PermissionDeniedException, QuotaExceededException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    @Test
    public void queryMainTest()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            InformationUnavailableException, NodeNotFoundException, EdgeIndexAlreadySet,
            DomainNotFoundException, QuotaExceededException, VersionNotFoundException {
        assureNodeDeclared();
        IApi api = getBuran();

        /* Create a node in incubation - don't update an existing node */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNid = createInfo.getIncNid();

        /* Populate the node in incubation with data */

        /* Add two domains to the system and assign to node */
        DomainId domain1 = api.addAnonymousDomain(getReceiver(), getSender());
        DomainId domain2 = api.addAnonymousDomain(getReceiver(), getSender());
        api.addDomainToNode(getReceiver(), getSender(), iNid, domain1);
        api.addDomainToNode(getReceiver(), getSender(), iNid, domain2);

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

        api.setEdge(getReceiver(), getSender(), iNid, EdgeIndex.c(0),
                EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE),
                IncVersionedEdTarget.c(iNid2));
        api.setEdge(getReceiver(), getSender(), iNid2, EdgeIndex.c(0),
                EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE),
                IncVersionedEdTarget.c(iNid));

        /* Commit node (from incubation to live) */

        CommitResult commitResult = api.commit(getReceiver(), getSender(), iNid, iNid2);

        queryForIntegerEq(intValue);
        queryForIntegerEq(intValue2);
        queryForStringEq(stringValue);
        queryForStringEq(stringValue2);
        andOrNotQuery(intValue, intValue2, stringValue, stringValue2);
        queryMetadata(commitResult.getNid(iNid), commitResult.getNid(iNid2), intValue, domain1,
                domain2);
        queryUsingEdges(commitResult.getNid(iNid), commitResult.getNid(iNid2), intValue, intValue2);
    }

    private void queryForIntegerEq(int realValue)
            throws PermissionDeniedException, QuotaExceededException {
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

    private void queryForStringEq(String realValue)
            throws PermissionDeniedException, QuotaExceededException {
        IApi api = getBuran();

        /* String-Equals: We should find exactly one node */
        QueryCdNode query = QueryCdNode.c(CondCdNode.c(this.classId,
                PropCondition.c(NodeClassSimple.PROPERTY_STRING, StringEq.eq(realValue))));
        QueryResult result = api.query(getReceiver(), getSender(), query, true);
        Assert.assertEquals("Should have one result.", 1,
                (long) result.getNumberOfResultsWithoutLimit().get());
    }

    private void andOrNotQuery(int intValue1, int intValue2, String stringValue1,
            String stringValue2) throws PermissionDeniedException, InformationUnavailableException,
            NodeNotFoundException, QuotaExceededException, VersionNotFoundException {
        IApi api = getBuran();

        /* Or-Query: We should find two nodes */
        QueryCdNode query = QueryCdNode.c(CondCdNode.c(this.classId,
                PcUnion.c(PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.eq(intValue1)),
                        PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.eq(intValue2)))));
        QueryResult result = api.query(getReceiver(), getSender(), query, true);
        Assert.assertEquals("Should have two results.", 2,
                (long) result.getNumberOfResultsWithoutLimit().get());

        /* And-Query: We should find no node */
        QueryCdNode queryAnd = QueryCdNode.c(CondCdNode.c(this.classId, PcIntersection
                .c(PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.eq(intValue1)),
                        PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.eq(intValue2)))));
        QueryResult resultAnd = api.query(getReceiver(), getSender(), queryAnd, true);
        Assert.assertEquals("Should have no result.", 0,
                (long) resultAnd.getNumberOfResultsWithoutLimit().get());

        /* Not-query: Find nodes without string 'stringValue1' */
        QueryCdNode queryNot = QueryCdNode.c(CondCdNode.c(this.classId, PcInverse
                .c(PropCondition.c(NodeClassSimple.PROPERTY_STRING, StringEq.eq(stringValue1)))));
        QueryResult resultNot = api.query(getReceiver(), getSender(), queryNot, true);
        /* Node with 'stringValue1' must not be in result set */
        for (NidVer found : resultNot.getNodes()) {
            Object value =
                    api.getData(getReceiver(), getSender(), found, NodeClassSimple.PROPERTY_STRING,
                            PrimGet.STRING);
            if (value != null) {
                final String valueAsString = (String) value;
                Assert.assertTrue("Must not be stringValue1", !stringValue1.equals(valueAsString));
            }
        }
    }

    private void queryMetadata(NidVer node1, NidVer node2, int intValue1, DomainId dom1,
            DomainId dom2) throws PermissionDeniedException, QuotaExceededException {
        IApi api = getBuran();

        /* Query for nodes where sender = getSender() */
        QueryNode query1 = QueryNode.c(CondNode.c(SenderIsIn.c(getSender())));
        /* Query for nodes where sender = getReceiver() */
        QueryNode query2 = QueryNode.c(CondNode.c(SenderIsIn.c(getReceiver())));

        /* Query for nodes where sender = getSender() and is in domains 'dom1' and 'dom2' */
        QueryNode query3 = QueryNode.c(CondNode.c(McIntersection.c(SenderIsIn.c(getSender()),
                McIntersection.c(InDomain.c(dom1), InDomain.c(dom2)))));

        /* Query for nodes where sender = getSender() and is first version */
        QueryNode query4 = QueryNode.c(CondNode
                .c(McIntersection.c(SenderIsIn.c(getSender()), VersionEq.c(NidVer.FIRST_VERSION))));

        /* Query for nodes where first version and PROPERTY_INT = 'intValue1' */
        QueryCdNode query5 = QueryCdNode.c(CondCdNode
                .c(this.classId, VersionEq.c(NidVer.FIRST_VERSION),
                        PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.eq(intValue1))));

        /* Query for nodes that are not in domain 'dom1' */
        QueryNode query6 = QueryNode.c(CondNode.c(McInverse.c(InDomain.c(dom1))));

        /* Perform queries */
        final QueryResult r1 = api.query(getReceiver(), getSender(), query1, true);
        final QueryResult r2 = api.query(getReceiver(), getSender(), query2, true);
        final QueryResult r3 = api.query(getReceiver(), getSender(), query3, true);
        final QueryResult r4 = api.query(getReceiver(), getSender(), query4, true);
        final QueryResult r5 = api.query(getReceiver(), getSender(), query5, true);
        final QueryResult r6 = api.query(getReceiver(), getSender(), query6, true);

        /* Check results */

        /* Query 1: Node 1 and 2 should be in results. */
        Assert.assertTrue(r1.getNodes().contains(node1));
        Assert.assertTrue(r1.getNodes().contains(node2));

        /* Query 2: Node 1 and 2 must not be in results, since sender != receiver */
        Assert.assertTrue(!r2.getNodes().contains(node1));
        Assert.assertTrue(!r2.getNodes().contains(node2));

        /* Query 3: Only node 1 is in both domains */
        Assert.assertTrue(r3.getNodes().contains(node1));
        Assert.assertTrue(!r3.getNodes().contains(node2));

        /* Query 4: Should return both nodes (both are first-version nodes). */
        Assert.assertTrue(r4.getNodes().contains(node1));
        Assert.assertTrue(r4.getNodes().contains(node2));

        /* Query 5: Returns only first node, since second node has a different int-value. */
        Assert.assertTrue(r5.getNodes().contains(node1));
        Assert.assertTrue(!r5.getNodes().contains(node2));

        /* Query 6: Returns only second node, since first node is in domain 'dom1'. */
        Assert.assertTrue(!r6.getNodes().contains(node1));
        Assert.assertTrue(r6.getNodes().contains(node2));
    }

    private void queryUsingEdges(NidVer node1, NidVer node2, int intValue1, int intValue2)
            throws PermissionDeniedException, QuotaExceededException {
        IApi api = getBuran();

        /* Query for a nodes where first version and PROPERTY_INT = 'intValue1' and that has an
        outgoing edge to another node where PROPERTY_INT = 'intValue2' */

        CondCdNode qNode2 = CondCdNode.c(this.classId, SenderIsIn.c(getSender()),
                PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.eq(intValue2)));

        QueryCdNode query = QueryCdNode.c(CondCdNode.c(this.classId, McIntersection
                .c(VersionEq.c(NidVer.FIRST_VERSION), OutEdgeCondition.c(this.classId,
                        EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE),
                        EdgeIndex.c(0), qNode2)),
                PropCondition.c(NodeClassSimple.PROPERTY_INT, IntEq.eq(intValue1))));

        final QueryResult r = api.query(getReceiver(), getSender(), query, true);

        /* We should find node 1 and not node 2 */
        Assert.assertTrue(r.getNodes().contains(node1));
        Assert.assertTrue(!r.getNodes().contains(node2));
    }
}