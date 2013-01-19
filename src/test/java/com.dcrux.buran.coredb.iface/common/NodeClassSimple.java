package com.dcrux.buran.coredb.iface.common;

import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.exceptions.PermissionDeniedException;
import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeClass;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;
import com.dcrux.buran.coredb.iface.propertyTypes.blob.BlobType;
import com.dcrux.buran.coredb.iface.propertyTypes.ftsi.FtsiType;
import com.dcrux.buran.coredb.iface.propertyTypes.integer.IntType;
import com.dcrux.buran.coredb.iface.propertyTypes.set.SetType;
import com.dcrux.buran.coredb.iface.propertyTypes.string.StringType;

/**
 * Buran.
 *
 * @author: ${USER} Date: 19.01.13 Time: 09:26
 */
public class NodeClassSimple {
    public static final short PROPERTY_INT = 0;
    public static final short PROPERTY_STRING = 1;
    public static final short PROPERTY_SET = 2;
    public static final short PROPERTY_BLOB = 3;
    public static final short PROPERTY_FTSI = 4;

    public static final EdgeLabel EDGE_ONE = EdgeLabel.privateEdge("edgeOne");
    public static final EdgeLabel EDGE_TWO = EdgeLabel.privateEdge("edgeTwo");


    public static NodeClass create() {
        final NodeClass nodeClass =
                NodeClass.builder().add("anInteger", false, IntType.cQueryable())
                        .add("aString", false, new StringType(true, true, true))
                        .add("aSetProperty", false, SetType.cMaxQueryable())
                        .add("binaryBloebchen", false, BlobType.cIndexed())
                        .add("daaFulltext", false, FtsiType.c())
                        .addEdgeClass(PrivateEdgeClass.cQueryable(EDGE_ONE))
                        .addEdgeClass(PrivateEdgeClass.cQueryable(EDGE_TWO)).get();
        return nodeClass;
    }

    public static ClassId declare(IApi api) throws PermissionDeniedException {
        final NodeClass nodeClass = create();
        final NodeClassHash ncHash = api.declareClass(nodeClass);
        final ClassId classId = api.getClassIdByHash(ncHash);
        return classId;
    }
}
