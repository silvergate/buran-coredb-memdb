package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.api.apiData.CommitResult;
import com.dcrux.buran.coredb.iface.api.exceptions.OptimisticLockingException;
import com.dcrux.buran.coredb.iface.node.IncNid;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author caelis
 */
public class CommitApi {
    private final Nodes nodes;
    private final DataReadApi drApi;
    private final NodeClassesApi ncApi;
    private final SubscriptionApi subscriptionApi;

    public CommitApi(Nodes nodes, DataReadApi drApi, NodeClassesApi ncApi,
            SubscriptionApi subscriptionApi) {
        this.nodes = nodes;
        this.drApi = drApi;
        this.ncApi = ncApi;
        this.subscriptionApi = subscriptionApi;
    }

    public CommitResult commit(long receiverId, long senderId, IncNid... incNid)
            throws OptimisticLockingException {
        Set<IncNid> incNids = new HashSet<>();
        incNids.addAll(Arrays.asList(incNid));
        return this.nodes.getByUserId(receiverId)
                .commit(senderId, incNids, this.drApi, this.ncApi, this.subscriptionApi);
    }

    public CommitResult commit(long receiverId, long senderId, Set<IncNid> incNids)
            throws OptimisticLockingException {
        return this.nodes.getByUserId(receiverId)
                .commit(senderId, incNids, this.drApi, this.ncApi, this.subscriptionApi);
    }
}