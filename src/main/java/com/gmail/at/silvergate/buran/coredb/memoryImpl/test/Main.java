package com.gmail.at.silvergate.buran.coredb.memoryImpl.test;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.UserId;
import com.dcrux.buran.coredb.iface.api.CommitResult;
import com.dcrux.buran.coredb.iface.api.CreateInfo;
import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.KeepAliveHint;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.domains.DomainHashCreator;
import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeClass;
import com.dcrux.buran.coredb.iface.edgeClass.PublicEdgeClass;
import com.dcrux.buran.coredb.iface.edgeClass.PublicEdgeConstraints;
import com.dcrux.buran.coredb.iface.edgeTargets.IncVersionedEdTarget;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.propertyTypes.string.StringEq;
import com.dcrux.buran.coredb.iface.propertyTypes.string.StringType;
import com.dcrux.buran.coredb.iface.query.QCdNode;
import com.dcrux.buran.coredb.iface.query.edgeCondition.OutEdgeCondition;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.IPropertyCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PropCondition;
import com.dcrux.buran.coredb.memoryImpl.ApiIface;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import com.google.common.base.Optional;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * @author caelis
 */
public class Main {
  public static void main(String[] args) throws OptimisticLockingException, IncubationNodeNotFound, EdgeIndexAlreadySet,
          NodeNotFoundException, PermissionDeniedException, InformationUnavailableException {

    ApiIface apiImpl = new ApiIface();
    IApi api = apiImpl;

    EdgeLabel halloEdge = EdgeLabel.privateEdge("hallo");

    /* Declare class */
    final NodeClass nodeClass = NodeClass.builder().add("daName", false, new StringType(true, true, true))
            .addEdgeClass(PrivateEdgeClass.cQueryable(halloEdge)).get();
    final NodeClassHash ncHash = api.declareClass(nodeClass);
    final ClassId classId = api.getClassIdByHash(ncHash);

    /* Public edge label test */
    final ClassId cls2 = new ClassId(332332l);
    PublicEdgeClass pec = new PublicEdgeClass(UUID.randomUUID(), true, Optional.of(cls2), PublicEdgeConstraints.many,
            Optional.of(classId));
    final EdgeLabel label = pec.createLabel();
    System.out.println(label.getLabel());
    PublicEdgeClass pecParsed = PublicEdgeClass.parse(label);
    System.out.println("Equals: " + pecParsed.createLabel().equals(label));

    /* Domain hash */
    DomainHashCreator dhc = new DomainHashCreator(UUID.randomUUID(), "sdjkvnjskdfnnvknsdjkfnv", "rvmsdkrfvksdmfkmvksd",
            "skdjfvskdfkvjsdfsvkdmfj");
    System.out.println("DomainHash:" + Arrays.toString(dhc.createHash().getHash()));

    final UserId receiver = UserId.c(0L);
    final UserId sender = UserId.c(100L);

    /* Erstellen von 2 nodes in der incubation */

    CreateInfo nodeOneCreateInfo = api.createNew(receiver, sender, classId, Optional.<KeepAliveHint>absent());
    IncNid nodeOneInc = nodeOneCreateInfo.getIncNid();
    CreateInfo nodeTwoCreateInfo = api.createNew(receiver, sender, classId, Optional.<KeepAliveHint>absent());
    IncNid nodeTwoInc = nodeTwoCreateInfo.getIncNid();

    /* NodeImpl 1 mit daten & edges befüllen: Die edges von node 1 zeigen auf node 2 */

    api.setEdge(receiver, sender, nodeOneInc, EdgeIndex.c(0), halloEdge, new IncVersionedEdTarget(nodeTwoInc.getId()));
    api.setEdge(receiver, sender, nodeOneInc, EdgeIndex.c(1), halloEdge, new IncVersionedEdTarget(nodeTwoInc.getId()));
    api.setData(receiver, sender, nodeOneInc, (short) 0, PrimSet.string("Ich bin eine Welt"));

    /* NodeImpl 2 mit daten befüllen */

    api.setData(receiver, sender, nodeTwoInc, (short) 0, PrimSet.string("Text an NodeImpl 2"));

    /* Beide nodes Committen */

    final CommitResult cr = api.commit(receiver, sender, nodeOneInc, nodeTwoInc);
    System.out.println("OID (node 1) = " + cr.getNid(nodeOneInc));
    System.out.println("OID (node 2) = " + cr.getNid(nodeTwoInc));

    /* Von der commiteten node 1 daten lesen */

    final Object value = api.getData(receiver, sender, cr.getNid(nodeOneInc), (short) 0, PrimGet.SINGLETON);
    System.out.println("Value (NodeImpl 1) = " + value);

        /* Von der commiteten node 2 daten lesen */

    final Object value2 = api.getData(receiver, sender, cr.getNid(nodeTwoInc), (short) 0, PrimGet.SINGLETON);
    System.out.println("Value (NodeImpl 2) = " + value2);

    /* Query: Bedingung: muss eine node mit "Ich bin eine Welt" und einer "hallo"-edge im index 0 sein.
    Am ende der Edge muss eine NodeImpl vorhanden sein, mit dem text "Text an NodeImpl 2" */

    PropCondition pcNode2 = new PropCondition((short) 0, new StringEq("Text an NodeImpl 2"));

    PropCondition pc = new PropCondition((short) 0, new StringEq("Ich bin eine Welt"));
    INodeMetaCondition nmc = OutEdgeCondition.hasEdge(halloEdge, EdgeIndex.c(0),
            new QCdNode(Optional.<INodeMetaCondition>absent(), classId.getId(),
                    Optional.<IPropertyCondition>of(pcNode2)));
    QCdNode query =
            new QCdNode(Optional.<INodeMetaCondition>of(nmc), classId.getId(), Optional.<IPropertyCondition>of(pc));
    final Set<NodeImpl> result = apiImpl.getQueryApi().query(receiver.getId(), sender.getId(), query);
    System.out.println("Query Result: " + result);

    return;
  }
}
