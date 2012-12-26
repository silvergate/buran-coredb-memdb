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
public class AccountDomains {
  private final Map<Long, DomainImpl> domainIdToDomain = new HashMap<>();

  public Map<Long, DomainImpl> getDomainIdToDomain() {
    return domainIdToDomain;
  }
}
