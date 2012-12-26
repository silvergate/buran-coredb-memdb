package com.dcrux.buran.coredb.memoryImpl.typeImpls;

import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.iface.nodeClass.TypeRef;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.PrimSet;
import com.dcrux.buran.coredb.iface.nodeClass.propertyTypes.string.StringType;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 24.12.12
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
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
