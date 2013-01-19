package com.dcrux.buran.coredb.iface;

import com.dcrux.buran.coredb.iface.api.*;
import com.dcrux.buran.coredb.iface.api.exceptions.*;
import com.dcrux.buran.coredb.iface.base.TestsBase;
import com.dcrux.buran.coredb.iface.common.NodeClassSimple;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.dcrux.buran.coredb.iface.propertyTypes.PrimSet;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

public class VersioningTest extends TestsBase {

    private ClassId classId;

    private void assureNodeDeclared() throws PermissionDeniedException {
        if (this.classId == null) this.classId = NodeClassSimple.declare(getBuran());
    }

    @Before
    public void setup() throws PermissionDeniedException {
        assureNodeDeclared();
    }

    @Test
    public void createAndUpdateNode()
            throws PermissionDeniedException, IncubationNodeNotFound, OptimisticLockingException,
            NodeNotUpdatable, HistoryHintNotFulfillable {
        IApi api = getBuran();

        /* Create a node in incubation */
        CreateInfo createInfo = api.createNew(getReceiver(), getSender(), this.classId,
                Optional.<KeepAliveHint>absent());
        IncNid iNidOne = createInfo.getIncNid();

        /* Populate the node in incubation with data */

        /* Integer */
        int intValue = 3320033;
        api.setData(getReceiver(), getSender(), iNidOne, NodeClassSimple.PROPERTY_INT,
                PrimSet.integer(intValue));
        String strValue = "I'm a text...";
        api.setData(getReceiver(), getSender(), iNidOne, NodeClassSimple.PROPERTY_STRING,
                PrimSet.string(strValue));

        /* Commit node */

        CommitResult commitResultOne = api.commit(getReceiver(), getSender(), iNidOne);
        NidVer nidVer0 = commitResultOne.getNid(iNidOne);

        /* Create a new node in incubation and update 'nidVer0' node */

        final CreateInfoUpdate createInfoUpdate =
                api.createNewUpdate(getReceiver(), getSender(), Optional.<KeepAliveHint>absent(),
                        nidVer0, Optional.<HistoryHint>absent());
        final IncNid iNidOneV2 = createInfoUpdate.getIncNid();

        CommitResult commitResultTwo = api.commit(getReceiver(), getSender(), iNidOneV2);
    }
}