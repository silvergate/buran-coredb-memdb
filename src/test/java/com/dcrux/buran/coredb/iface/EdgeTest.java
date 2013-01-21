package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.CommitResult;
import com.dcrux.buran.coredb.iface.api.CreateInfo;
import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.KeepAliveHint;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.edgeTargets.IncUnversionedEdTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.IncVersionedEdTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.UnversionedEdTarget;
import com.dcrux.buran.coredb.iface.edgeTargets.VersionedEdTarget;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.Map;

public class EdgeTest extends TestsBase {

    private ClassId classId;
    private NidVer nodeOneId;

    private void assureNodeDeclared() throws PermissionDeniedException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    @Before
    public void setup() throws PermissionDeniedException {
        assureNodeDeclared();
    }

    @Test
    public void createIncubationNode()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            InformationUnavailableException, NodeNotFoundException, EdgeIndexAlreadySet {
        IApi api = getBuran();

        /* Create a node ONE in incubation */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNidOne = createInfo.getIncNid();

        /* Populate the node in incubation with data */

        /* Integer */
        int intValue = 232;
        api.setData(getReceiver(), getSender(), iNidOne, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValue));

        /* Commit node ONE */

        CommitResult commitResultOne = api.commit(getReceiver(), getSender(), iNidOne);
        NidVer nidVerOne = commitResultOne.getNid(iNidOne);
        this.nodeOneId = nidVerOne;

        /* Create nodes TWO and THREE in incubation */
        CreateInfo createInfoTwo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNidTwo = createInfoTwo.getIncNid();
        CreateInfo createInfoThree = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNidThree = createInfoThree.getIncNid();

        /* A: Add an edge from node two to node three @index 0, unversioned */
        api.setEdge(getReceiver(), getSender(), iNidTwo, EdgeIndex.c(0), NodeClassSimple.EDGE_ONE,
                IncUnversionedEdTarget.c(iNidThree));

        /* B: Add an edge from node two to node three @index 1, unversioned */
        api.setEdge(getReceiver(), getSender(), iNidTwo, EdgeIndex.c(1), NodeClassSimple.EDGE_ONE,
                IncUnversionedEdTarget.c(iNidThree));

        /* C: Add an edge from node three to node two @index 0, unversioned */
        api.setEdge(getReceiver(), getSender(), iNidThree, EdgeIndex.c(0), NodeClassSimple.EDGE_ONE,
                IncUnversionedEdTarget.c(iNidTwo));

        /* D: Add an edge from node three to node two @index 0, versioned */
        api.setEdge(getReceiver(), getSender(), iNidThree, EdgeIndex.c(1), NodeClassSimple.EDGE_TWO,
                IncVersionedEdTarget.c(iNidTwo));

        /* E: Now create an unversioned edge from node three (in incubation) to committed node
        one */
        api.setEdge(getReceiver(), getSender(), iNidThree, EdgeIndex.c(4), NodeClassSimple.EDGE_ONE,
                UnversionedEdTarget.c(nidVerOne));

        /* F: Now create an versioned edge from node three (in incubation) to committed node one */
        api.setEdge(getReceiver(), getSender(), iNidThree, EdgeIndex.c(5), NodeClassSimple.EDGE_ONE,
                VersionedEdTarget.c(nidVerOne));

        /* Commit nodes two and three */

        CommitResult commitResultTwo = api.commit(getReceiver(), getSender(), iNidTwo, iNidThree);
        NidVer nidVerTwo = commitResultTwo.getNid(iNidTwo);
        NidVer nidVerThree = commitResultTwo.getNid(iNidThree);

        /* Check node ONE */

        final Map<EdgeLabel, Map<EdgeIndex, Edge>> oeNodeOne =
                api.getOutEdges(getReceiver(), getSender(), nidVerOne,
                        EnumSet.of(EdgeType.privateMod), Optional.<EdgeLabel>absent());
        Assert.assertEquals("Node one should not have any out edges.", 0, oeNodeOne.size());

        final Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> ieNodeOne =
                api.getInEdges(getReceiver(), getSender(), nidVerOne,
                        EnumSet.of(EdgeType.privateMod), Optional.<EdgeLabel>absent());
        /* See E and F: Should have two in-edges */
        Multimap<EdgeIndex, EdgeWithSource> ieNodeOneEdgeOne =
                ieNodeOne.get(NodeClassSimple.EDGE_ONE);
        Assert.assertEquals("Should have two in-nodes", 2, ieNodeOneEdgeOne.size());
        Assert.assertNotNull("Should have an edge @index 4, see E.",
                ieNodeOneEdgeOne.get(EdgeIndex.c(4)));
        Assert.assertNotNull("Should have an edge @index 5, see F.",
                ieNodeOneEdgeOne.get(EdgeIndex.c(5)));

        /* Check node TWO */

        final Map<EdgeLabel, Map<EdgeIndex, Edge>> oeNodeTwo =
                api.getOutEdges(getReceiver(), getSender(), nidVerTwo,
                        EnumSet.of(EdgeType.privateMod), Optional.<EdgeLabel>absent());
        Assert.assertEquals("Should have one label.", 1, oeNodeTwo.size());
        Assert.assertNotNull("Should have one label.", oeNodeTwo.get(NodeClassSimple.EDGE_ONE));
        Map<EdgeIndex, Edge> oeNodeTwoLabel = oeNodeTwo.get(NodeClassSimple.EDGE_ONE);
        Assert.assertEquals("Should have two edges, see A and B.", 2, oeNodeTwoLabel.size());

        final Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> ieNodeTwo =
                api.getInEdges(getReceiver(), getSender(), nidVerTwo,
                        EnumSet.of(EdgeType.privateMod), Optional.<EdgeLabel>absent());
        Assert.assertEquals("Should have two labels, see C & D.", 2, ieNodeTwo.keySet().size());
        Multimap<EdgeIndex, EdgeWithSource> ieNodeTwoKeyOne =
                ieNodeTwo.get(NodeClassSimple.EDGE_ONE);
        Assert.assertNotNull("Label missing, see C & D", ieNodeTwoKeyOne);
        Multimap<EdgeIndex, EdgeWithSource> ieNodeTwoKeyTwo =
                ieNodeTwo.get(NodeClassSimple.EDGE_TWO);
        Assert.assertNotNull("Label missing, see C & D", ieNodeTwoKeyTwo);
        Assert.assertEquals("Index missing, see C & D", 1, ieNodeTwoKeyOne.entries().size());
        Assert.assertEquals("Index missing, see C & D", 1, ieNodeTwoKeyTwo.entries().size());
        Assert.assertNotNull("Index missing, see C & D", ieNodeTwoKeyOne.get(EdgeIndex.c(0)));
        Assert.assertNotNull("Index missing, see C & D", ieNodeTwoKeyTwo.get(EdgeIndex.c(1)));

        /* Check node THREE */

        final Map<EdgeLabel, Map<EdgeIndex, Edge>> oeNodeThree =
                api.getOutEdges(getReceiver(), getSender(), nidVerThree,
                        EnumSet.of(EdgeType.privateMod), Optional.<EdgeLabel>absent());
        Assert.assertEquals("Should be 2 Labels, see C, D, E & F.", 2, oeNodeThree.keySet().size());
        final Map<EdgeIndex, Edge> oeNodeThreeL1 = oeNodeThree.get(NodeClassSimple.EDGE_ONE);
        final Map<EdgeIndex, Edge> oeNodeThreeL2 = oeNodeThree.get(NodeClassSimple.EDGE_TWO);
        Assert.assertNotNull("Label should be set, see C, E & F.", oeNodeThreeL1);
        Assert.assertNotNull("Label should be set, see D.", oeNodeThreeL2);
        Assert.assertNotNull("Index 0 missing, see C.", oeNodeThreeL1.get(EdgeIndex.c(0)));
        Assert.assertNotNull("Index 4 missing, see E.", oeNodeThreeL1.get(EdgeIndex.c(4)));
        Assert.assertNotNull("Index 5 missing, see F.", oeNodeThreeL1.get(EdgeIndex.c(5)));

        final Map<EdgeLabel, Multimap<EdgeIndex, EdgeWithSource>> ieNodeThree =
                api.getInEdges(getReceiver(), getSender(), nidVerThree,
                        EnumSet.of(EdgeType.privateMod), Optional.<EdgeLabel>absent());
        Assert.assertEquals("Should have one label, see A & B.", 1, ieNodeThree.keySet().size());
        final Multimap<EdgeIndex, EdgeWithSource> ieNodeThreeL1 =
                ieNodeThree.get(NodeClassSimple.EDGE_ONE);
        Assert.assertNotNull("Need index 0, see A.", ieNodeThreeL1.get(EdgeIndex.c(0)));
        Assert.assertNotNull("Need index 1, see B.", ieNodeThreeL1.get(EdgeIndex.c(1)));
    }
}