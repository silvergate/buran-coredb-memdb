package com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string;

import com.dcrux.buran.coredb.iface.nodeClass.*;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimSet;

/**
 *
 * @author caelis
 */
public class StringType implements IType {

  public static final TypeRef REF = new TypeRef((short) 1);

  @Override
  public TypeRef getRef() {
    return REF;
  }

  private final boolean unicodeSorting;
  private final boolean unicodeRangeQuery;
  private final boolean equalsQuery;

  public boolean isUnicodeSorting() {
    return unicodeSorting;
  }

  public boolean isEqualsQuery() {
    return equalsQuery;
  }

  /* Fulltext search */

  // Falls true, werden stopwords entfernt. Wird nach einem stopword gesucht
  /*private final boolean ftsRemoveStopWords = true;
  private final boolean ftsPhoeneticQuery = true;
  private final boolean ftsExactQuery = true;
  private final boolean ftsExactICaseQuery = true;
  private final boolean ftsBeginsWith = true;  */

  public StringType(boolean unicodeSorting, boolean unicodeRangeQuery, boolean equalsQuery) {
    this.unicodeSorting = unicodeSorting;
    this.unicodeRangeQuery = unicodeRangeQuery;
    this.equalsQuery = equalsQuery;
  }

  @Override
  public boolean supports(SorterRef sorting) {
    return false;
  }

  @Override
  public boolean supports(CmpRef comparator) {
    if (this.equalsQuery && (comparator.equals(StringEq.REF))) {
      return true;
    }
    return false;
  }

  @Override
  public boolean supports(IDataSetter dataSetter) {
    if (dataSetter.getClass().equals(PrimSet.class)) {
      final PrimSet ps = (PrimSet) dataSetter;
      return ps.getValue() instanceof String;
    }
    return false;
  }

  @Override
  public boolean supports(IDataGetter dataGetter) {
    if (dataGetter.getClass().equals(PrimGet.class)) {
      return true;
    }
    return false;
  }
}
