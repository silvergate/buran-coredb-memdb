package com.dcrux.buran.coredb.memoryImpl.typeImpls;

import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.iface.nodeClass.TypeRef;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.integer.IntType;

import javax.annotation.Nullable;

/**
 * @author caelis
 */
public class IntImpl implements ITypeImpl {
  @Override
  public TypeRef getRef() {
    return IntType.REF;
  }

  @Override
  public Object setData(IDataSetter dataSetter, Object currentValue) {
    PrimSet ds = (PrimSet) dataSetter;
    return ds.getValue();
  }

  @Nullable
  @Override
  public Object getData(IDataGetter dataGetter, @Nullable Object value) {
    return (Integer) value;
  }
}
