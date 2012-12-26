package com.dcrux.buran.coredb.iface.query;

import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 16.12.12
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public class QueryNode {
  private final IQNode condition;
  private final Optional<MetaSort> sorting;
  private final SkipLimit skipLimit;

  public QueryNode(IQNode condition, Optional<MetaSort> sorting, SkipLimit skipLimit) {
    this.condition = condition;
    this.sorting = sorting;
    this.skipLimit = skipLimit;
  }

  public IQNode getCondition() {
    return condition;
  }

  public Optional<MetaSort> getSorting() {
    return sorting;
  }

  public SkipLimit getSkipLimit() {
    return skipLimit;
  }
}
