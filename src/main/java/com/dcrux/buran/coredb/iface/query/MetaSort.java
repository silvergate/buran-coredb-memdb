package com.dcrux.buran.coredb.iface.query;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 17.12.12
 * Time: 21:15
 * To change this template use File | Settings | File Templates.
 */
public class MetaSort implements ISorting {
  public static enum MetaField {
    firstCommitTime,
    commitTime
  }

  private final MetaField field;
  private final SortDirection direction;

  public MetaSort(MetaField field, SortDirection direction) {
    this.field = field;
    this.direction = direction;
  }

  public MetaField getField() {
    return field;
  }

  public SortDirection getDirection() {
    return direction;
  }
}
