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
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string.StringType;
import com.dcrux.buran.coredb.memoryImpl.ApiIface;

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
    final long senderId = 0L;

    IncOid ioid = api.getDmApi().createNew(0L, 0L, classId, null);
    api.getDmApi().setEdge(0L, 0L, ioid, new EdgeIndex(0), EdgeLabel.privateEdge("hallo"),
            new IncVersionedEdTarget(ioid.getId()), false);
    api.getDmApi().setEdge(0L, 0L, ioid, new EdgeIndex(1), EdgeLabel.privateEdge("hallo"),
            new IncVersionedEdTarget(ioid.getId()), false);

    api.getDmApi().setData(receiverId, senderId, ioid, (short) 0, PrimSet.string("Ich bin eine welt"));

    final CommitResult cr = api.getCommitApi().commit(senderId, ioid);
    System.out.println("OID = " + cr.getOidVers(ioid));

    final Object value =
            api.getDrApi().getData(receiverId, senderId, cr.getOidVers(ioid), (short) 0, PrimGet.SINGLETON);
    System.out.println("Value = " + value);

    //OidVersion ov = api.getCommitApi().commit(0L, ioid);

    //System.out.println(api.getDrApi().getPrivateOutEdges(0L, 0L, ov));

    return;
  }
}
