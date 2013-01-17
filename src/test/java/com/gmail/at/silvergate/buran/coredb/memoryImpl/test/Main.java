package com.gmail.at.silvergate.buran.coredb.memoryImpl.test;

import com.dcrux.buran.coredb.iface.EdgeIndex;
import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.UserId;
import com.dcrux.buran.coredb.iface.api.*;
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
import com.dcrux.buran.coredb.iface.propertyTypes.ftsi.FtsiAddText;
import com.dcrux.buran.coredb.iface.propertyTypes.ftsi.FtsiMatch;
import com.dcrux.buran.coredb.iface.propertyTypes.ftsi.FtsiType;
import com.dcrux.buran.coredb.iface.propertyTypes.ftsi.Fuzziness;
import com.dcrux.buran.coredb.iface.propertyTypes.string.StringEq;
import com.dcrux.buran.coredb.iface.propertyTypes.string.StringType;
import com.dcrux.buran.coredb.iface.query.CondCdNode;
import com.dcrux.buran.coredb.iface.query.QueryCdNode;
import com.dcrux.buran.coredb.iface.query.edgeCondition.OutEdgeCondition;
import com.dcrux.buran.coredb.iface.query.nodeMeta.INodeMetaCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.IPropertyCondition;
import com.dcrux.buran.coredb.iface.query.propertyCondition.PropCondition;
import com.dcrux.buran.coredb.memoryImpl.ApiIface;
import com.google.common.base.Optional;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author caelis
 */
public class Main {

    public static void fts(Fuzziness fuzziness, String text, IApi apiImpl, ClassId classId,
            UserId receiver, UserId sender) throws PermissionDeniedException {
        PropCondition ftsOne = new PropCondition((short) 1, FtsiMatch.c(fuzziness, text));
        CondCdNode query = new CondCdNode(Optional.<INodeMetaCondition>absent(), classId.getId(),
                Optional.<IPropertyCondition>of(ftsOne));
        final QueryResult queryResult = apiImpl.query(receiver, sender, QueryCdNode.c(query), true);
        boolean found = !queryResult.getNodes().isEmpty();
        System.out.println(MessageFormat
                .format("* FTS: ''{0}'' : fuzziness: {1}, " + "found: {2} ", text, fuzziness,
                        found));
    }

    public static void fts(String text, IApi apiImpl, ClassId classId, UserId receiver,
            UserId sender) throws PermissionDeniedException {
        fts(Fuzziness.high, text, apiImpl, classId, receiver, sender);
        fts(Fuzziness.medium, text, apiImpl, classId, receiver, sender);
        fts(Fuzziness.low, text, apiImpl, classId, receiver, sender);
    }

    public static void main(String[] args)
            throws OptimisticLockingException, IncubationNodeNotFound, EdgeIndexAlreadySet,
            NodeNotFoundException, PermissionDeniedException, InformationUnavailableException {

        ByteArrayOutputStream bb = new ByteArrayOutputStream();
        bb.write((byte) 33);

        System.out.println(bb.toByteArray().length);


        ApiIface apiImpl = new ApiIface();
        IApi api = apiImpl;

        EdgeLabel halloEdge = EdgeLabel.privateEdge("hallo");

    /* Declare class */
        final NodeClass nodeClass =
                NodeClass.builder().add("daName", false, new StringType(true, true, true))
                        .add("fulltext", false, new FtsiType())
                        .addEdgeClass(PrivateEdgeClass.cQueryable(halloEdge)).get();
        final NodeClassHash ncHash = api.declareClass(nodeClass);
        final ClassId classId = api.getClassIdByHash(ncHash);

    /* Public edge label test */
        final ClassId cls2 = ClassId.c(332332l);
        PublicEdgeClass pec = new PublicEdgeClass(UUID.randomUUID(), true, Optional.of(cls2),
                PublicEdgeConstraints.many, Optional.of(classId));
        final EdgeLabel label = pec.createLabel();
        System.out.println(label.getLabel());
        PublicEdgeClass pecParsed = PublicEdgeClass.parse(label);
        System.out.println("Equals: " + pecParsed.createLabel().equals(label));

    /* Domain hash */
        DomainHashCreator dhc = new DomainHashCreator(UUID.randomUUID(), "sdjkvnjskdfnnvknsdjkfnv",
                "rvmsdkrfvksdmfkmvksd", "skdjfvskdfkvjsdfsvkdmfj");
        System.out.println("DomainHash:" + Arrays.toString(dhc.createHash().getHash()));

        final UserId receiver = UserId.c(0L);
        final UserId sender = UserId.c(100L);

    /* Erstellen von 2 nodes in der incubation */

        CreateInfo nodeOneCreateInfo =
                api.createNew(receiver, sender, classId, Optional.<KeepAliveHint>absent());
        IncNid nodeOneInc = nodeOneCreateInfo.getIncNid();
        CreateInfo nodeTwoCreateInfo =
                api.createNew(receiver, sender, classId, Optional.<KeepAliveHint>absent());
        IncNid nodeTwoInc = nodeTwoCreateInfo.getIncNid();

    /* NodeImpl 1 mit daten & edges befüllen: Die edges von node 1 zeigen auf node 2 */

        api.setEdge(receiver, sender, nodeOneInc, EdgeIndex.c(0), halloEdge,
                new IncVersionedEdTarget(nodeTwoInc.getId()));
        api.setEdge(receiver, sender, nodeOneInc, EdgeIndex.c(1), halloEdge,
                new IncVersionedEdTarget(nodeTwoInc.getId()));
        api.setData(receiver, sender, nodeOneInc, (short) 0, PrimSet.string("Ich bin eine Welt"));
        api.setData(receiver, sender, nodeOneInc, (short) 1, FtsiAddText.c("Es handelt sich " +
                "hierbei um einen Text, wobei Apple Inc. das iPhone mit iOS " +
                "herstellt, und Microsoft das Windows 8."));

    /* NodeImpl 2 mit daten befüllen */

        api.setData(receiver, sender, nodeTwoInc, (short) 0, PrimSet.string("Text an NodeImpl 2"));

    /* Beide nodes Committen */

        final CommitResult cr = api.commit(receiver, sender, nodeOneInc, nodeTwoInc);
        System.out.println("OID (node 1) = " + cr.getNid(nodeOneInc));
        System.out.println("OID (node 2) = " + cr.getNid(nodeTwoInc));

    /* Von der commiteten node 1 daten lesen */

        final Object value =
                api.getData(receiver, sender, cr.getNid(nodeOneInc), (short) 0, PrimGet.SINGLETON);
        System.out.println("Value (NodeImpl 1) = " + value);

        /* Von der commiteten node 2 daten lesen */

        final Object value2 =
                api.getData(receiver, sender, cr.getNid(nodeTwoInc), (short) 0, PrimGet.SINGLETON);
        System.out.println("Value (NodeImpl 2) = " + value2);

    /* Query: Bedingung: muss eine node mit "Ich bin eine Welt" und einer "hallo"-edge im index 0
     sein.
    Am ende der Edge muss eine NodeImpl vorhanden sein, mit dem text "Text an NodeImpl 2" */

        PropCondition pcNode2 = new PropCondition((short) 0, new StringEq("Text an NodeImpl 2"));

        PropCondition pc = new PropCondition((short) 0, new StringEq("Ich bin eine Welt"));
        INodeMetaCondition nmc = OutEdgeCondition.hasEdge(halloEdge, EdgeIndex.c(0),
                new CondCdNode(Optional.<INodeMetaCondition>absent(), classId.getId(),
                        Optional.<IPropertyCondition>of(pcNode2)));
        CondCdNode query = new CondCdNode(Optional.<INodeMetaCondition>of(nmc), classId.getId(),
                Optional.<IPropertyCondition>of(pc));
        final QueryResult queryResult = api.query(receiver, sender, QueryCdNode.c(query), true);
        System.out.println("Query Result: " + queryResult.getNodes());

        /* Fulltext-suchen */

        fts("ZZLA OOPL", apiImpl, classId, receiver, sender);
        fts("KAKE zSCHEISSE", apiImpl, classId, receiver, sender);
        fts("wIndows miCROSOFT", apiImpl, classId, receiver, sender);
        fts("Bundeshaushalsüberschuss ét Milch, Windows", apiImpl, classId, receiver, sender);
        fts("Bundeshaushalsüberschuss Apple, Windows, iOS, herstellt, hierbei, microsoft", apiImpl,
                classId, receiver, sender);
        fts("mit Apple, ios, microsoft und Windows", apiImpl, classId, receiver, sender);


        return;
    }
}
