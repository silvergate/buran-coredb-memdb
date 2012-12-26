package com.dcrux.buran.coredb.memoryImpl.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class Edges {
  private final Map<Long, AccountEdges> receiverIdToEdges = new HashMap<>();

  public AccountEdges getAccountEdges(long receiverId) {
    AccountEdges ae = this.receiverIdToEdges.get(receiverId);
    if (ae == null) {
      ae = new AccountEdges();
      this.receiverIdToEdges.put(receiverId, ae);
    }
    return ae;
  }
}
