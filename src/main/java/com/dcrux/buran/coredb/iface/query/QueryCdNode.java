package com.dcrux.buran.coredb.iface.query;

import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 16.12.12
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
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
