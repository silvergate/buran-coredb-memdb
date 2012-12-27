package com.dcrux.buran.coredb.memoryImpl.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 17:48
 * To change this template use File | Settings | File Templates.
 */
public class Nodes {
  private Map<Long, AccountNodes> receiverIdToNodes = new HashMap<>();

  public AccountNodes getByUserId(final long userId) {
    AccountNodes an = this.receiverIdToNodes.get(userId);
    if (an == null) {
      an = new AccountNodes(userId);
      this.receiverIdToNodes.put(userId, an);
    }
    return an;
  }
}
