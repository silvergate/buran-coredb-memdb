package com.dcrux.buran.coredb.iface.query;

/**
 * @author caelis
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
