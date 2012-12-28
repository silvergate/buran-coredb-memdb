package com.dcrux.buran.coredb.iface.query;

import com.google.common.base.Optional;

/**
 *
 * @author caelis
 */
public class QueryCdNode {
  private final IQCdNode condition;
  private final Optional<ISorting> sorting;
  private final SkipLimit skipLimit;

  public QueryCdNode(IQCdNode condition, Optional<ISorting> sorting, SkipLimit skipLimit) {
    this.condition = condition;
    this.sorting = sorting;
    this.skipLimit = skipLimit;
  }

  public IQCdNode getCondition() {
    return condition;
  }

  public Optional<ISorting> getSorting() {
    return sorting;
  }

  public SkipLimit getSkipLimit() {
    return skipLimit;
  }
}
