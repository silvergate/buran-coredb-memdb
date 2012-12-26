package com.dcrux.buran.coredb.memoryImpl.typeImpls;

import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;
import com.dcrux.buran.coredb.iface.nodeClass.IDataSetter;
import com.dcrux.buran.coredb.iface.nodeClass.TypeRef;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 24.12.12
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public interface ITypeImpl {
  TypeRef getRef();

  @Nullable
  Object setData(IDataSetter dataSetter, @Nullable Object currentValue);

  @Nullable
  Object getData(IDataGetter dataGetter, @Nullable Object value);
}
