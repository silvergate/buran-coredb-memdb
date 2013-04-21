package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.CreateInfo;
import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.iface.api.KeepAliveHint;
import com.dcrux.buran.coredb.iface.api.QueryResult;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.iface.nodeClass.SorterRef;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.propertyTypes.SorterRefs;
import com.dcrux.buran.coredb.iface.query.CondCdNode;
import com.dcrux.buran.coredb.iface.query.PropertySort;
import com.dcrux.buran.coredb.iface.query.QueryCdNode;
import com.dcrux.buran.coredb.iface.query.SortDirection;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SortTest extends TestsBase {

    private static class SetValueList {
        private final Map<Integer, Map<Short, IDataSetter>> values = new HashMap<>();

        public void setValue(int row, short typeIndex, IDataSetter dataSetter) {
            Map<Short, IDataSetter> rowValues = this.values.get(row);
            if (rowValues == null) {
                rowValues = new HashMap<>();
                this.values.put(row, rowValues);
            }
            rowValues.put(typeIndex, dataSetter);
        }

        public Map<Integer, Map<Short, IDataSetter>> getValues() {
            return values;
        }
    }

    private ClassId classId;
    private SorterRef sortRef;

    @Before
    public void assureNodeDeclared() throws PermissionDeniedException, QuotaExceededException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    private void addNodesWithData(UserId receiver, UserId sender, ClassId classId,
            SetValueList setValueList)
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            QuotaExceededException {
        IApi api = getBuran();
        for (final Map<Short, IDataSetter> valueRow : setValueList.getValues().values()) {
            final CreateInfo createResult =
                    api.createNew(receiver, sender, classId, Optional.<KeepAliveHint>absent());
            final IncNid iNid = createResult.getIncNid();
            for (final Map.Entry<Short, IDataSetter> singleValue : valueRow.entrySet()) {
                api.setData(receiver, sender, iNid, singleValue.getKey(), singleValue.getValue());
            }
            api.commit(receiver, sender, iNid);
        }
    }

    @Test
    public void sortingMainTest()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            InformationUnavailableException, NodeNotFoundException, EdgeIndexAlreadySet,
            DomainNotFoundException, QuotaExceededException {
        IApi api = getBuran();

        /* Create data */
        SetValueList svl = new SetValueList();

        svl.setValue(0, NodeClassSimple.PROPERTY_INT, PrimSet.integer(34));
        svl.setValue(1, NodeClassSimple.PROPERTY_INT, PrimSet.extinct());
        svl.setValue(2, NodeClassSimple.PROPERTY_INT, PrimSet.integer(2222));
        svl.setValue(3, NodeClassSimple.PROPERTY_INT, PrimSet.integer(1));

        svl.setValue(0, NodeClassSimple.PROPERTY_STRING, PrimSet.string("house"));
        svl.setValue(1, NodeClassSimple.PROPERTY_STRING, PrimSet.extinct());
        svl.setValue(2, NodeClassSimple.PROPERTY_STRING, PrimSet.string("Bicycle"));
        svl.setValue(3, NodeClassSimple.PROPERTY_STRING, PrimSet.string("zulu"));

        svl.setValue(0, NodeClassSimple.PROPERTY_BINARY, PrimSet.extinct());
        svl.setValue(1, NodeClassSimple.PROPERTY_BINARY, PrimSet.binary("car".getBytes()));
        svl.setValue(2, NodeClassSimple.PROPERTY_BINARY, PrimSet.binary("Bicycle".getBytes()));
        svl.setValue(3, NodeClassSimple.PROPERTY_BINARY, PrimSet.binary("zulu".getBytes()));

        svl.setValue(0, NodeClassSimple.PROPERTY_LONGINT, PrimSet.longInt(33));
        svl.setValue(1, NodeClassSimple.PROPERTY_LONGINT, PrimSet.extinct());
        svl.setValue(2, NodeClassSimple.PROPERTY_LONGINT, PrimSet.longInt(4444443));
        svl.setValue(3, NodeClassSimple.PROPERTY_LONGINT, PrimSet.longInt(0));

        svl.setValue(0, NodeClassSimple.PROPERTY_BOOLEAN, PrimSet.bool(false));
        svl.setValue(1, NodeClassSimple.PROPERTY_BOOLEAN, PrimSet.bool(false));
        svl.setValue(2, NodeClassSimple.PROPERTY_BOOLEAN, PrimSet.extinct());
        svl.setValue(3, NodeClassSimple.PROPERTY_BOOLEAN, PrimSet.bool(true));

        svl.setValue(0, NodeClassSimple.PROPERTY_LONGFLOAT, PrimSet.longFloat(100.00d));
        svl.setValue(1, NodeClassSimple.PROPERTY_LONGFLOAT, PrimSet.longFloat(323233339.00d));
        svl.setValue(2, NodeClassSimple.PROPERTY_LONGFLOAT, PrimSet.extinct());
        svl.setValue(3, NodeClassSimple.PROPERTY_LONGFLOAT, PrimSet.longFloat(0.001d));

        addNodesWithData(getReceiver(), getSender(), this.classId, svl);

        /* Check sorting */

        /* NATURAL_NL is this default natural sort where missing values have the lowest value */
        this.sortRef = SorterRefs.NATURAL_NL;

        int i1 = (int) querySortGetAtIndex(NodeClassSimple.PROPERTY_INT, SortDirection.desc, 0);
        int i2 = (int) querySortGetAtIndex(NodeClassSimple.PROPERTY_INT, SortDirection.asc, 1);
        Object i3 = querySortGetAtIndex(NodeClassSimple.PROPERTY_INT, SortDirection.asc, 0);
        Assert.assertEquals(2222, i1);
        Assert.assertEquals(1, i2);
        Assert.assertEquals(null, i3);

        String s1 =
                (String) querySortGetAtIndex(NodeClassSimple.PROPERTY_STRING, SortDirection.desc,
                        0);
        String s2 =
                (String) querySortGetAtIndex(NodeClassSimple.PROPERTY_STRING, SortDirection.asc, 1);
        Object s3 = querySortGetAtIndex(NodeClassSimple.PROPERTY_STRING, SortDirection.asc, 0);
        Assert.assertEquals("zulu", s1);
        Assert.assertEquals("Bicycle", s2);
        Assert.assertEquals(null, s3);

        long l1 =
                (long) querySortGetAtIndex(NodeClassSimple.PROPERTY_LONGINT, SortDirection.desc, 0);
        long l2 =
                (long) querySortGetAtIndex(NodeClassSimple.PROPERTY_LONGINT, SortDirection.asc, 1);
        Object l3 = querySortGetAtIndex(NodeClassSimple.PROPERTY_LONGINT, SortDirection.asc, 0);
        Assert.assertEquals(4444443, l1);
        Assert.assertEquals(0, l2);
        Assert.assertEquals(null, l3);

        boolean b1 =
                (boolean) querySortGetAtIndex(NodeClassSimple.PROPERTY_BOOLEAN, SortDirection.desc,
                        0);
        boolean b2 =
                (boolean) querySortGetAtIndex(NodeClassSimple.PROPERTY_BOOLEAN, SortDirection.asc,
                        1);
        Object b3 = querySortGetAtIndex(NodeClassSimple.PROPERTY_BOOLEAN, SortDirection.asc, 0);
        Assert.assertEquals(true, b1);
        Assert.assertEquals(false, b2);
        Assert.assertEquals(null, b3);

        double d1 =
                (double) querySortGetAtIndex(NodeClassSimple.PROPERTY_LONGFLOAT, SortDirection.desc,
                        0);
        double d2 =
                (double) querySortGetAtIndex(NodeClassSimple.PROPERTY_LONGFLOAT, SortDirection.asc,
                        1);
        Object d3 = querySortGetAtIndex(NodeClassSimple.PROPERTY_LONGFLOAT, SortDirection.asc, 0);
        Assert.assertEquals(323233339.00d, d1, 1.0d);
        Assert.assertEquals(0.001d, d2, 1.0d);
        Assert.assertEquals(null, d3);

        /* Switch to NATURAL_NH sorting: missing values have the highest value */
        this.sortRef = SorterRefs.NATURAL_NH;

        final Object null1 =
                querySortGetAtIndex(NodeClassSimple.PROPERTY_INT, SortDirection.desc, 0);
        final Object null2 =
                querySortGetAtIndex(NodeClassSimple.PROPERTY_STRING, SortDirection.desc, 0);
        final Object null3 =
                querySortGetAtIndex(NodeClassSimple.PROPERTY_LONGINT, SortDirection.desc, 0);
        final Object null4 =
                querySortGetAtIndex(NodeClassSimple.PROPERTY_BOOLEAN, SortDirection.desc, 0);
        final Object null5 =
                querySortGetAtIndex(NodeClassSimple.PROPERTY_LONGFLOAT, SortDirection.desc, 0);

        Assert.assertNull(null1);
        Assert.assertNull(null2);
        Assert.assertNull(null3);
        Assert.assertNull(null4);
        Assert.assertNull(null5);
    }

    private Object querySortGetAtIndex(short typeIndex, SortDirection sortDir, int index)
            throws PermissionDeniedException, InformationUnavailableException,
            NodeNotFoundException, QuotaExceededException {
        IApi api = getBuran();
        QueryCdNode query = QueryCdNode.cSorted(CondCdNode.c(this.classId),
                PropertySort.c(typeIndex, this.sortRef, sortDir));
        final QueryResult result = api.query(getReceiver(), getSender(), query, true);
        NidVer nidVer = result.getNodes().get(index);
        return api.getData(getReceiver(), getSender(), nidVer, typeIndex, PrimGet.ANY);
    }
}