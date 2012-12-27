package com.gmail.at.silvergate.buran.coredb.memoryImpl.test;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.IncOid;
import com.dcrux.buran.coredb.iface.api.CommitResult;
import com.dcrux.buran.coredb.iface.api.OptimisticLockingException;
import com.dcrux.buran.coredb.iface.edgeTargets.IncVersionedEdTarget;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string.StringEq;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string.StringType;
import com.dcrux.buran.coredb.iface.query.QCdNode;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.IPropertyCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PropCondition;
import com.dcrux.buran.coredb.memoryImpl.ApiIface;
import com.dcrux.buran.coredb.memoryImpl.data.Node;
import com.google.common.base.Optional;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 09.12.12
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public class Main {
  public static void main(String[] args) throws OptimisticLockingException {

    ApiIface api = new ApiIface();

    /* Declare class */
    final NodeClass nodeClass = NodeClass.builder().add("daName", false, new StringType(true, true, true)).get();
    final NodeClassHash ncHash = api.getNodeClassesApi().declareClass(nodeClass);
    final Long classId = api.getNodeClassesApi().getClassIdByHash(ncHash);

    final long receiverId = 0L;
    final long senderId = 100L;

    IncOid ioid = api.getDmApi().createNew(receiverId, senderId, classId, null);
    api.getDmApi().setEdge(receiverId, senderId, ioid, new EdgeIndex(0), EdgeLabel.privateEdge("hallo"),
            new IncVersionedEdTarget(ioid.getId()), false);
    api.getDmApi().setEdge(receiverId, senderId, ioid, new EdgeIndex(1), EdgeLabel.privateEdge("hallo"),
            new IncVersionedEdTarget(ioid.getId()), false);

    api.getDmApi().setData(receiverId, senderId, ioid, (short) 0, PrimSet.string("Ich bin eine Welt"));

    final CommitResult cr = api.getCommitApi().commit(receiverId, senderId, ioid);
    System.out.println("OID = " + cr.getOidVers(ioid));

    final Object value =
            api.getDrApi().getData(receiverId, senderId, cr.getOidVers(ioid), (short) 0, PrimGet.SINGLETON);
    System.out.println("Value = " + value);

    /* Query */

    PropCondition pc = new PropCondition((short) 0, new StringEq("Ich bin eine Welt"));
    QCdNode query = new QCdNode(Optional.<INodeMetaCondition>absent(), classId, Optional.<IPropertyCondition>of(pc));
    final Set<Node> result = api.getQueryApi().query(receiverId, senderId, query);
    System.out.println("Query Result: " + result);

    return;
  }
}
