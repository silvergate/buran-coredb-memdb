package com.dcrux.buran.coredb.iface.query.nodeMeta;

import com.dcrux.buran.coredb.iface.DomainId;

/**
 * @author caelis
 */
public class InDomain implements INodeMetaCondition {
  private DomainId domain;

  public InDomain(DomainId domain) {
    this.domain = domain;
  }

  public DomainId getDomain() {
    return domain;
  }

  @Override
  public boolean matches(IMetaInfoForQuery metaInfoForQuery) {
    return metaInfoForQuery.getDomainIds().contains(this.domain.getId());
  }
}
