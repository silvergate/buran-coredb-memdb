package com.dcrux.buran.coredb.iface.query.nodeMeta;

import com.dcrux.buran.coredb.iface.DomainId;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */
public class InDomain implements INodeMetaCondition {
  private DomainId domain;

  public InDomain(DomainId domain) {
    this.domain = domain;
  }

  public DomainId getDomain() {
    return domain;
  }
}
