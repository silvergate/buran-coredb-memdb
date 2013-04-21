package com.gmail.at.silvergate.buran.coredb.memoryImpl.test;

import com.dcrux.buran.coredb.iface.IncNid;
import com.dcrux.buran.coredb.iface.UserId;
import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.domains.DomainHashCreator;
import com.dcrux.buran.coredb.iface.edge.EdgeIndex;
import com.dcrux.buran.coredb.iface.edge.EdgeLabel;
import com.dcrux.buran.coredb.iface.edge.EdgeLabelIndex;
import com.dcrux.buran.coredb.iface.edgeClass.EdgeClass;
import com.dcrux.buran.coredb.iface.edgeTargets.IncVersionedEdTarget;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.*;
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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author caelis
 */
public class Main {

    public static void fts(Fuzziness fuzziness, String text, IApi apiImpl, ClassId classId,
            UserId receiver, UserId sender)
            throws PermissionDeniedException, QuotaExceededException {
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
            UserId sender) throws PermissionDeniedException, QuotaExceededException {
        fts(Fuzziness.high, text, apiImpl, classId, receiver, sender);
        fts(Fuzziness.medium, text, apiImpl, classId, receiver, sender);
        fts(Fuzziness.low, text, apiImpl, classId, receiver, sender);
    }

    private static void testBinary() {
        BinaryBlocks bb = new BinaryBlocks();
        final String eingabeString = "Es handelt sich um die eingabe";
        byte[] bin = eingabeString.getBytes();
        boolean written = bb.setData(0, bin, false);
        if (!written) throw new IllegalStateException("written is false");
        byte[] binClone = bb.read(0, bin.length - 1);

        if (!Arrays.equals(bin, binClone)) System.out.println("Die beiden sind nicht equal!");
    }

    private static File getPersistenceFile() throws IOException {
        String userHome = System.getProperty("user.home");
        File serFile = new File(new File(userHome), "buran.1.ser");
        if (!serFile.exists()) {
            serFile.createNewFile();
        }
        return serFile;
    }

    public static void main(String[] args)
            throws OptimisticLockingException, IncubationNodeNotFound, EdgeIndexAlreadySet,
            NodeNotFoundException, PermissionDeniedException, InformationUnavailableException,
            QuotaExceededException, IOException {
        testBinary();

        ApiIface apiImpl = new ApiIface(getPersistenceFile());
        IApi api = apiImpl;

        String halloEdgeLabel = "hallo";

    /* Declare class */
        final NodeClass nodeClass = NodeClass.builder()
                .add("daName", false, StringType.indexed(StringType.MAX_LEN_INDEXED))
                .add("fulltext", false, new FtsiType())
                .add("binary", false, BlobType.indexed(BlobType.MAX_LENGTH))
                .addEdgeClass(EdgeClass.cQueryableMany(EdgeLabelIndex.fromString(halloEdgeLabel)))
                .get();
        final NodeClassHash ncHash = api.declareClass(nodeClass);
        final ClassId classId = api.getClassIdByHash(ncHash);
        EdgeLabel halloEdge = EdgeLabel.privateEdge(classId, "hallo");

    /* Public edge label test */
        final ClassId cls2 = ClassId.c(332332l);
        final EdgeLabel label = EdgeLabel.createPublic(true);

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

    /* NodeImpl 1 mit daten & edge befüllen: Die edge von node 1 zeigen auf node 2 */

        final String blobString = "Dies ist die quelle für Binary";
        final byte[] blobBytes = blobString.getBytes();
        api.setEdge(receiver, sender, nodeOneInc, EdgeIndex.c(0), halloEdge,
                new IncVersionedEdTarget(nodeTwoInc.getId()));
        api.setEdge(receiver, sender, nodeOneInc, EdgeIndex.c(1), halloEdge,
                new IncVersionedEdTarget(nodeTwoInc.getId()));
        api.setData(receiver, sender, nodeOneInc, (short) 0, PrimSet.string("Ich bin eine Welt"));
        api.setData(receiver, sender, nodeOneInc, (short) 1, FtsiAddText.c("Es handelt sich " +
                "hierbei um einen Text, wobei Apple Inc. das iPhone mit iOS " +
                "herstellt, und Microsoft das Windows 8."));
        api.setData(receiver, sender, nodeOneInc, (short) 2, BlobSet.c(0, blobBytes));

    /* NodeImpl 2 mit daten befüllen */

        api.setData(receiver, sender, nodeTwoInc, (short) 0, PrimSet.string("Text an NodeImpl 2"));

    /* Beide nodes Committen */

        final CommitResult cr = api.commit(receiver, sender, nodeOneInc, nodeTwoInc);
        System.out.println("OID (node 1) = " + cr.getNid(nodeOneInc));
        System.out.println("OID (node 2) = " + cr.getNid(nodeTwoInc));

    /* Von der commiteten node 1 daten lesen */

        final Object value =
                api.getData(receiver, sender, cr.getNid(nodeOneInc), (short) 0, PrimGet.STRING);
        System.out.println("Value (NodeImpl 1) = " + value);
        /* Lesen der anzahl bytes im blob */
        final Long blobLength = (Long) api
                .getData(receiver, sender, cr.getNid(nodeOneInc), (short) 2, LengthGet.SINGLETON);
        System.out.println("Value (NodeImpl 1): Länge des blobs: = " + blobLength);
        /* Lesen des blobs und daraus wieder einen string konstruieren */
        final byte[] blobData = (byte[]) api
                .getData(receiver, sender, cr.getNid(nodeOneInc), (short) 2, BlobGet.c(blobLength));
        final String reconstructedString = new String(blobData);
        /* Ausgeben des rekonstruierten strings, sollte wieder dem String von oben 'blobString'
        entsprechen */
        System.out.println(
                "Value (NodeImpl 1): Inhalt des blobs als String: = '" + reconstructedString + "'");

        /* Von der commiteten node 2 daten lesen */

        final Object value2 =
                api.getData(receiver, sender, cr.getNid(nodeTwoInc), (short) 0, PrimGet.STRING);
        System.out.println("Value (NodeImpl 2) = " + value2);

    /* Query: Bedingung: muss eine node mit "Ich bin eine Welt" und einer "hallo"-edge im index 0
     sein.
    Am ende der Edge muss eine NodeImpl vorhanden sein, mit dem text "Text an NodeImpl 2" */

        PropCondition pcNode2 = new PropCondition((short) 0, StringEq.eq("Text an NodeImpl 2"));

        PropCondition pc = new PropCondition((short) 0, StringEq.eq("Ich bin eine Welt"));
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
