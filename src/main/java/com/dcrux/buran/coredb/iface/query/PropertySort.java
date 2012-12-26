package com.dcrux.buran.coredb.iface.query;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 16.12.12
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public class PropertySort implements ISorting {
  private final short fieldIndex;
  private final SortDirection order;

  public short getFieldIndex() {
    return fieldIndex;
  }

  public SortDirection getOrder() {
    return order;
  }

  public PropertySort(short fieldIndex, SortDirection order) {
    this.fieldIndex = fieldIndex;
    this.order = order;
  }
}
