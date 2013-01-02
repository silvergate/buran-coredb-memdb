package com.gmail.at.silvergate.buran.coredb.memoryImpl.test;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.IncOid;
import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeClass;
import com.dcrux.buran.coredb.iface.edgeTargets.IncVersionedEdTarget;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string.StringEq;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string.StringType;
import com.dcrux.buran.coredb.iface.query.QCdNode;
import com.dcrux.buran.coredb.iface.query.edgeCondition.OutEdgeCondition;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.IPropertyCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PropCondition;
import com.dcrux.buran.coredb.memoryImpl.ApiIface;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import com.google.common.base.Optional;

import java.util.Set;

/**
 * @author caelis
 */
public class Main {
  public static void main(String[] args) throws OptimisticLockingException, IncubationNodeNotFound, EdgeIndexAlreadySet,
          NodeNotFoundException {

    ApiIface api = new ApiIface();

    EdgeLabel halloEdge = EdgeLabel.privateEdge("hallo");

    /* Declare class */
    final NodeClass nodeClass = NodeClass.builder().add("daName", false, new StringType(true, true, true))
            .addEdgeClass(PrivateEdgeClass.cQueryable(halloEdge)).get();
    final NodeClassHash ncHash = api.getNodeClassesApi().declareClass(nodeClass);
    final Long classId = api.getNodeClassesApi().getClassIdByHash(ncHash);

    final long receiverId = 0L;
    final long senderId = 100L;

    /* Erstellen von 2 nodes in der incubation */

    IncOid nodeOneInc = api.getDmApi().createNew(receiverId, senderId, classId, null);
    IncOid nodeTwoInc = api.getDmApi().createNew(receiverId, senderId, classId, null);

    /* NodeImpl 1 mit daten & edges befüllen: Die edges von node 1 zeigen auf node 2 */

    api.getDmApi().setEdge(receiverId, senderId, nodeOneInc, EdgeIndex.c(0), halloEdge,
            new IncVersionedEdTarget(nodeTwoInc.getId()), false);
    api.getDmApi().setEdge(receiverId, senderId, nodeOneInc, EdgeIndex.c(1), halloEdge,
            new IncVersionedEdTarget(nodeTwoInc.getId()), false);
    api.getDmApi().setData(receiverId, senderId, nodeOneInc, (short) 0, PrimSet.string("Ich bin eine Welt"));

    /* NodeImpl 2 mit daten befüllen */

    api.getDmApi().setData(receiverId, senderId, nodeTwoInc, (short) 0, PrimSet.string("Text an NodeImpl 2"));

    /* Beide nodes Committen */

    final CommitResult cr = api.getCommitApi().commit(receiverId, senderId, nodeOneInc, nodeTwoInc);
    System.out.println("OID (node 1) = " + cr.getOidVers(nodeOneInc));
    System.out.println("OID (node 2) = " + cr.getOidVers(nodeTwoInc));

    /* Von der commiteten node 1 daten lesen */

    final Object value =
            api.getDrApi().getData(receiverId, senderId, cr.getOidVers(nodeOneInc), (short) 0, PrimGet.SINGLETON);
    System.out.println("Value (NodeImpl 1) = " + value);

        /* Von der commiteten node 2 daten lesen */

    final Object value2 =
            api.getDrApi().getData(receiverId, senderId, cr.getOidVers(nodeTwoInc), (short) 0, PrimGet.SINGLETON);
    System.out.println("Value (NodeImpl 2) = " + value2);

    /* Query: Bedingung: muss eine node mit "Ich bin eine Welt" und einer "hallo"-edge im index 0 sein.
    Am ende der Edge muss eine NodeImpl vorhanden sein, mit dem text "Text an NodeImpl 2" */

    PropCondition pcNode2 = new PropCondition((short) 0, new StringEq("Text an NodeImpl 2"));

    PropCondition pc = new PropCondition((short) 0, new StringEq("Ich bin eine Welt"));
    INodeMetaCondition nmc = OutEdgeCondition.hasEdge(halloEdge, EdgeIndex.c(0),
            new QCdNode(Optional.<INodeMetaCondition>absent(), classId, Optional.<IPropertyCondition>of(pcNode2)));
    QCdNode query = new QCdNode(Optional.<INodeMetaCondition>of(nmc), classId, Optional.<IPropertyCondition>of(pc));
    final Set<NodeImpl> result = api.getQueryApi().query(receiverId, senderId, query);
    System.out.println("Query Result: " + result);

    return;
  }
}
