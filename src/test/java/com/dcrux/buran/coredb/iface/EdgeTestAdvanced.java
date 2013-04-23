package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.edge.EdgeIndex;
import com.dcrux.buran.coredb.iface.edge.EdgeLabel;
import com.dcrux.buran.coredb.iface.edge.EdgeType;
import com.dcrux.buran.coredb.iface.edgeTargets.*;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.Map;

public class EdgeTestAdvanced extends TestsBase {

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
    public void edgeTestAdvanced()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            InformationUnavailableException, NodeNotFoundException, EdgeIndexAlreadySet,
            QuotaExceededException, NodeNotUpdatable, HistoryHintNotFulfillable,
            NotUpdatingException {
        IApi api = getBuran();

        final EdgeLabel publicEdgeLabel1 = EdgeLabel.createPublic(true);
        final EdgeLabel publicEdgeLabel2 = EdgeLabel.createPublic(false);

        /* Create nodes in incubation */
        CreateInfo ci1 = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        CreateInfo ci2 = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        CreateInfo ci3 = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        CreateInfo ci4 = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        CreateInfo ci5 = api.createNew(getReceiver2(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid in1 = ci1.getIncNid();
        IncNid in2 = ci2.getIncNid();
        IncNid in3 = ci3.getIncNid();
        IncNid in4 = ci4.getIncNid();
        IncNid in5 = ci5.getIncNid();


        for (int i = 0; i <= 99; i++) {
            api.setEdge(getReceiver(), getSender(), in1, EdgeIndex.c(i),
                    EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE),
                    IncUnversionedEdTarget.c(in3));
            api.setEdge(getReceiver(), getSender(), in1, EdgeIndex.c(i),
                    EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_TWO),
                    IncVersionedEdTarget.c(in3));
            api.setEdge(getReceiver(), getSender(), in1, EdgeIndex.c(i), publicEdgeLabel1,
                    IncVersionedEdTarget.c(in3));
            api.setEdge(getReceiver(), getSender(), in1, EdgeIndex.c(i), publicEdgeLabel2,
                    IncVersionedEdTarget.c(in3));
        }

        for (int i = 100; i <= 199; i++) {
            api.setEdge(getReceiver(), getSender(), in2, EdgeIndex.c(i),
                    EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE),
                    IncUnversionedEdTarget.c(in4));
            api.setEdge(getReceiver(), getSender(), in2, EdgeIndex.c(i),
                    EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_TWO),
                    IncVersionedEdTarget.c(in4));
            api.setEdge(getReceiver(), getSender(), in2, EdgeIndex.c(i), publicEdgeLabel1,
                    IncVersionedEdTarget.c(in4));
            api.setEdge(getReceiver(), getSender(), in2, EdgeIndex.c(i), publicEdgeLabel2,
                    IncVersionedEdTarget.c(in4));
        }

        /* Edge from node 5 to node 1 */
        api.setEdge(getReceiver2(), getSender(), in5, EdgeIndex.BASE,
                EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE),
                IncUnversionedEdTarget.c(in1));

        /* Commit */
        CommitResult commitResult = api.commit(getReceiver(), getSender(), in1, in2, in3, in4);
        /* Commit node 5: Is to a different account, so need a separate commit */
        CommitResult commitResult2 = api.commit(getReceiver2(), getSender(), in5);

        NidVer n1 = commitResult.getNid(in1);
        NidVer n2 = commitResult.getNid(in2);
        NidVer n3 = commitResult.getNid(in3);
        NidVer n4 = commitResult.getNid(in4);
        NidVer n5 = commitResult.getNid(in5);

        Map<EdgeLabel, Map<EdgeIndex, IEdgeTarget>> result;
        Map<EdgeLabel, Multimap<EdgeIndex, NidVer>> resultIe;

        /* Should have 100 edges targeting to n3 */
        result = api.getOutEdges(getReceiver(), getSender(), n1, EnumSet.of(EdgeType.privateMod),
                Optional.<EdgeIndexRange>absent(),
                Optional.of(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE)));
        Assert.assertEquals("Must contain one label.", result.keySet().size(), 1);
        Assert.assertEquals("Must contain 100 elements.",
                result.get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE)).size(),
                100);
        for (final Map.Entry<EdgeIndex, IEdgeTarget> entry : result
                .get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE)).entrySet()) {
            Assert.assertTrue("(entry.getKey().getId() <= 99) && (entry.getKey().getId() >= 0)",
                    (entry.getKey().getId() <= 99) && (entry.getKey().getId() >= 0));
            final UnversionedEdTarget target = (UnversionedEdTarget) entry.getValue();
            Assert.assertEquals("Edge should target to node n3", target.getNidx().getNid(),
                    n3.getNid());
        }

        /* Should have 25-45 edges targeting to n3 */
        result = api.getOutEdges(getReceiver(), getSender(), n1, EnumSet.of(EdgeType.privateMod),
                Optional.of(EdgeIndexRange.c(25, 45)),
                Optional.of(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_TWO)));
        Assert.assertEquals("Must contain one label.", result.keySet().size(), 1);
        Assert.assertEquals("Must contain 11 elements.",
                result.get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_TWO)).size(),
                21);
        for (final Map.Entry<EdgeIndex, IEdgeTarget> entry : result
                .get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_TWO)).entrySet()) {
            Assert.assertTrue("(entry.getKey().getId() <= 45) && (entry.getKey().getId() >= 25)",
                    (entry.getKey().getId() <= 45) && (entry.getKey().getId() >= 25));
            final VersionedEdTarget target = (VersionedEdTarget) entry.getValue();
            Assert.assertEquals("Edge should target to node n3", target.getNidVer(), n3);
        }

        /* In-Edges @n4 */
        resultIe = api.getInEdges(getReceiver(), getSender(), n4, EnumSet.of(HistoryState.active),
                Optional.<ClassId>absent(), EnumSet.of(EdgeType.privateMod),
                Optional.of(EdgeIndexRange.c(90, 110)), Optional.<EdgeLabel>absent());
        Assert.assertEquals("Should contain 2 labels (EDGE_ONE & EDGE_TWO)", 2, resultIe.size());
        for (final Map.Entry<EdgeIndex, NidVer> entry : resultIe
                .get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE)).entries()) {
            Assert.assertTrue("(entry.getKey().getId() <= 110) && (entry.getKey().getId() >= 90)",
                    (entry.getKey().getId() <= 110) && (entry.getKey().getId() >= 90));
            final NidVer edgeSource = entry.getValue();
            Assert.assertEquals("Edge should target to node n2", edgeSource, n2);
        }
        for (final Map.Entry<EdgeIndex, NidVer> entry : resultIe
                .get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_TWO)).entries()) {
            Assert.assertTrue("(entry.getKey().getId() <= 110) && (entry.getKey().getId() >= 90)",
                    (entry.getKey().getId() <= 110) && (entry.getKey().getId() >= 90));
            final NidVer edgeSource = entry.getValue();
            Assert.assertEquals("Edge should target to node n2", edgeSource, n2);
        }

        /* Node 1 should not see in-edge from node 5, since they're on a different account */
        resultIe = api.getInEdges(getReceiver(), getSender(), n1, EnumSet.of(HistoryState.active),
                Optional.<ClassId>absent(), EnumSet.of(EdgeType.privateMod),
                Optional.<EdgeIndexRange>absent(), Optional.<EdgeLabel>absent());
        Assert.assertTrue("Node 5 must not be in result set.",
                resultIe.get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE)) ==
                        null);

        /* Remove node 1 */
        final CreateInfoUpdate n1Update =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        n1, Optional.<HistoryHint>absent());
        api.markNodeAsDeleted(getReceiver(), getSender(), n1Update.getIncNid());
        api.commit(getReceiver(), getSender(), n1Update.getIncNid());

        /* In-Edges @Node 3: There should be no node 1 */
        resultIe = api.getInEdges(getReceiver(), getSender(), n3, EnumSet.of(HistoryState.active),
                Optional.<ClassId>absent(), EnumSet.of(EdgeType.privateMod),
                Optional.<EdgeIndexRange>absent(), Optional.<EdgeLabel>absent());
        for (final Map.Entry<EdgeIndex, NidVer> entry : resultIe
                .get(EdgeLabel.privateEdge(this.classId, NodeClassSimple.EDGE_ONE)).entries()) {
            Assert.assertTrue("Node 1 should not be in results, since node 1 has been removed.",
                    entry.getValue().getNid() != n1.getNid());
        }
    }
}