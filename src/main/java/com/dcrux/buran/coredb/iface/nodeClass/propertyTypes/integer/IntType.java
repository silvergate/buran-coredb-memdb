package com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.integer;

import com.dcrux.buran.coredb.iface.nodeClass.*;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimGet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimSet;

/**
 *
 * @author caelis
 */
public class IntType implements IType {
  public static final TypeRef REF = new TypeRef((short) 22);

  @Override
  public TypeRef getRef() {
    return REF;
  }

  @Override
  public boolean supports(SorterRef sorting) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean supports(CmpRef comparator) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean supports(IDataSetter dataSetter) {
    if (dataSetter.getClass().equals(PrimSet.class)) {
      final PrimSet ps = (PrimSet) dataSetter;
      return ps.getValue() instanceof Integer;
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
