package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.IncOid;
import com.dcrux.buran.coredb.iface.api.CommitResult;
import com.dcrux.buran.coredb.iface.api.OptimisticLockingException;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author caelis
 */
public class CommitApi {
  private final Nodes nodes;
  private final DataReadApi drApi;
  private final NodeClassesApi ncApi;

  public CommitApi(Nodes nodes, DataReadApi drApi, NodeClassesApi ncApi) {
    this.nodes = nodes;
    this.drApi = drApi;
    this.ncApi = ncApi;
  }

  public CommitResult commit(long receiverId, long senderId, IncOid... incOid) throws OptimisticLockingException {
    Set<IncOid> incOids = new HashSet<>();
    incOids.addAll(Arrays.asList(incOid));
    return this.nodes.getByUserId(receiverId).commit(senderId, incOids, this.drApi, this.ncApi);
  }

  public CommitResult commit(long receiverId, long senderId, Set<IncOid> incOids) throws OptimisticLockingException {
    return this.nodes.getByUserId(receiverId).commit(senderId, incOids, this.drApi, this.ncApi);
  }
}