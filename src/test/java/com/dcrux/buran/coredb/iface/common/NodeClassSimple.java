package com.dcrux.buran.coredb.iface.common;

import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.exceptions.PermissionDeniedException;
import com.dcrux.buran.coredb.iface.api.exceptions.QuotaExceededException;
import com.dcrux.buran.coredb.iface.edge.EdgeLabelIndex;
import com.dcrux.buran.coredb.iface.edgeClass.EdgeClass;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.FieldIndex;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;
import com.dcrux.buran.coredb.iface.propertyTypes.binary.BinaryType;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.BlobType;
import com.dcrux.buran.coredb.iface.propertyTypes.bool.BoolType;
import com.dcrux.buran.coredb.iface.propertyTypes.ftsi.FtsiType;
import com.dcrux.buran.coredb.iface.propertyTypes.integer.IntType;
import com.dcrux.buran.coredb.iface.propertyTypes.longFloat.LongFloatType;
import com.dcrux.buran.coredb.iface.propertyTypes.longInt.LongType;
import com.dcrux.buran.coredb.iface.propertyTypes.set.SetType;
import com.dcrux.buran.coredb.iface.propertyTypes.string.StringType;

/**
 * Buran.
 *
 * @author: ${USER} Date: 19.01.13 Time: 09:26
 */
public class NodeClassSimple {
    public static final FieldIndex PROPERTY_INT = FieldIndex.c(0);
    public static final FieldIndex PROPERTY_STRING = FieldIndex.c(1);
    public static final FieldIndex PROPERTY_SET = FieldIndex.c(2);
    public static final FieldIndex PROPERTY_BLOB = FieldIndex.c(3);
    public static final FieldIndex PROPERTY_FTSI = FieldIndex.c(4);

    public static final FieldIndex PROPERTY_BINARY = FieldIndex.c(5);
    public static final FieldIndex PROPERTY_LONGINT = FieldIndex.c(6);
    public static final FieldIndex PROPERTY_BOOLEAN = FieldIndex.c(7);
    public static final FieldIndex PROPERTY_LONGFLOAT = FieldIndex.c(8);

    public static final EdgeLabelIndex EDGE_ONE = EdgeLabelIndex.fromString("edgeOne");
    public static final EdgeLabelIndex EDGE_TWO = EdgeLabelIndex.fromString("edgeTwo");

    public static NodeClass create() {
        final NodeClass nodeClass = NodeClass.builder().add("anInteger", false, IntType.indexed())
                .add("aString", false, StringType.indexed(StringType.MAX_LEN_INDEXED))
                .add("aSetProperty", false,
                        SetType.indexed(SetType.MAX_NUM_OF_ELEMENTS, SetType.MAX_LEN_BYTES))
                .add("binaryBloebchen", false, BlobType.indexed(BlobType.MAX_LENGTH))
                .add("daaFulltext", false, FtsiType.c())
                .add("daBinary", false, BinaryType.indexed(BinaryType.MAX_LEN))
                .add("daLongInt", false, LongType.indexed())
                .add("daBoolean", false, BoolType.indexed())
                .add("longFloat", false, LongFloatType.indexed())
                .addEdgeClass(EdgeClass.cQueryableMany(EDGE_ONE))
                .addEdgeClass(EdgeClass.cQueryableMany(EDGE_TWO)).get();
        return nodeClass;
    }

    public static ClassId declare(IApi api)
            throws PermissionDeniedException, QuotaExceededException {
        final NodeClass nodeClass = create();
        final NodeClassHash ncHash = api.declareClass(nodeClass);
        final ClassId classId = api.getClassIdByHash(ncHash);
        return classId;
    }
}
