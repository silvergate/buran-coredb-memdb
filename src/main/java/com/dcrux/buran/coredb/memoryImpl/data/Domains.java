package com.dcrux.buran.coredb.memoryImpl.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 25.12.12
 * Time: 01:16
 * To change this template use File | Settings | File Templates.
 */
public class Domains {
  public Map<Long, AccountDomains> accountDomains = new HashMap<>();

  public AccountDomains getByUserId(long userId) {
    AccountDomains accDomains = this.accountDomains.get(userId);
    if (accDomains == null) {
      accDomains = new AccountDomains();
      this.accountDomains.put(userId, accDomains);
    }
    return accDomains;
  }
}
