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
                rowValues = new HashMap<Short, IDataSetter>();
                this.values.put(row, rowValues);
            }
            rowValues.put(typeIndex, dataSetter);
        }

        public Map<Integer, Map<Short, IDataSetter>> getValues() {
            return values;
        }
    }

    private ClassId classId;

    @Before
    public void assureNodeDeclared() throws PermissionDeniedException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    private void addNodesWithData(UserId receiver, UserId sender, ClassId classId,
            SetValueList setValueList)
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException {
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
            DomainNotFoundException {
        IApi api = getBuran();

        /* Create data */
        SetValueList svl = new SetValueList();

        svl.setValue(0, NodeClassSimple.PROPERTY_INT, PrimSet.integer(34));
        svl.setValue(1, NodeClassSimple.PROPERTY_INT, PrimSet.integer(323));
        svl.setValue(2, NodeClassSimple.PROPERTY_INT, PrimSet.integer(2222));
        svl.setValue(3, NodeClassSimple.PROPERTY_INT, PrimSet.integer(1));

        svl.setValue(0, NodeClassSimple.PROPERTY_STRING, PrimSet.string("house"));
        svl.setValue(1, NodeClassSimple.PROPERTY_STRING, PrimSet.string("car"));
        svl.setValue(2, NodeClassSimple.PROPERTY_STRING, PrimSet.string("Bicycle"));
        svl.setValue(3, NodeClassSimple.PROPERTY_STRING, PrimSet.string("zulu"));

        svl.setValue(0, NodeClassSimple.PROPERTY_BINARY, PrimSet.binary("house".getBytes()));
        svl.setValue(1, NodeClassSimple.PROPERTY_BINARY, PrimSet.binary("car".getBytes()));
        svl.setValue(2, NodeClassSimple.PROPERTY_BINARY, PrimSet.binary("Bicycle".getBytes()));
        svl.setValue(3, NodeClassSimple.PROPERTY_BINARY, PrimSet.binary("zulu".getBytes()));

        svl.setValue(0, NodeClassSimple.PROPERTY_LONGINT, PrimSet.longInt(33));
        svl.setValue(1, NodeClassSimple.PROPERTY_LONGINT, PrimSet.longInt(33323));
        svl.setValue(2, NodeClassSimple.PROPERTY_LONGINT, PrimSet.longInt(4444443));
        svl.setValue(3, NodeClassSimple.PROPERTY_LONGINT, PrimSet.longInt(0));

        svl.setValue(0, NodeClassSimple.PROPERTY_BOOLEAN, PrimSet.bool(false));
        svl.setValue(1, NodeClassSimple.PROPERTY_BOOLEAN, PrimSet.bool(false));
        svl.setValue(2, NodeClassSimple.PROPERTY_BOOLEAN, PrimSet.bool(true));
        svl.setValue(3, NodeClassSimple.PROPERTY_BOOLEAN, PrimSet.bool(true));

        addNodesWithData(getReceiver(), getSender(), this.classId, svl);

        /* Check sorting */

        int i1 = (int) querySortGetFirst(NodeClassSimple.PROPERTY_INT, SortDirection.desc);
        int i2 = (int) querySortGetFirst(NodeClassSimple.PROPERTY_INT, SortDirection.asc);
        Assert.assertEquals(2222, i1);
        Assert.assertEquals(1, i2);

        String s1 = (String) querySortGetFirst(NodeClassSimple.PROPERTY_STRING, SortDirection.desc);
        String s2 = (String) querySortGetFirst(NodeClassSimple.PROPERTY_STRING, SortDirection.asc);
        Assert.assertEquals("zulu", s1);
        Assert.assertEquals("Bicycle", s2);

        long l1 = (long) querySortGetFirst(NodeClassSimple.PROPERTY_LONGINT, SortDirection.desc);
        long l2 = (long) querySortGetFirst(NodeClassSimple.PROPERTY_LONGINT, SortDirection.asc);
        Assert.assertEquals(4444443, l1);
        Assert.assertEquals(0, l2);

        boolean b1 =
                (boolean) querySortGetFirst(NodeClassSimple.PROPERTY_BOOLEAN, SortDirection.desc);
        boolean b2 =
                (boolean) querySortGetFirst(NodeClassSimple.PROPERTY_BOOLEAN, SortDirection.asc);
        Assert.assertEquals(true, b1);
        Assert.assertEquals(false, b2);
    }

    private Object querySortGetFirst(short typeIndex, SortDirection sortDir)
            throws PermissionDeniedException, InformationUnavailableException,
            NodeNotFoundException {
        IApi api = getBuran();
        QueryCdNode query = QueryCdNode.cSorted(CondCdNode.c(this.classId),
                PropertySort.c(typeIndex, SorterRefs.NATURAL, sortDir));
        final QueryResult result = api.query(getReceiver(), getSender(), query, true);
        NidVer nidVer = result.getNodes().get(0);
        return api.getData(getReceiver(), getSender(), nidVer, typeIndex, PrimGet.SINGLETON);
    }
}