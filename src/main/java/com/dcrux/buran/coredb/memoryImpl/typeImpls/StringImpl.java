package com.dcrux.buran.coredb.memoryImpl.typeImpls;

import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.iface.nodeClass.TypeRef;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string.StringType;

import javax.annotation.Nullable;

/**
 * @author caelis
 */
public class StringImpl implements ITypeImpl {
  @Override
  public TypeRef getRef() {
    return StringType.REF;
  }

  @Override
  public Object setData(IDataSetter dataSetter, Object currentValue) {
    PrimSet ds = (PrimSet) dataSetter;
    return ds.getValue();
  }

  @Nullable
  @Override
  public Object getData(IDataGetter dataGetter, @Nullable Object value) {
    return (String) value;
  }
}
