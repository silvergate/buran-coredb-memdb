package com.dcrux.buran.coredb.memoryImpl.typeImpls;

import com.dcrux.buran.coredb.iface.nodeClass.*;

import javax.annotation.Nullable;

/**
 * @author caelis
 */
@Deprecated
public interface ITypeImpl {
    TypeRef getRef();

    @Nullable
    Object setData(IDataSetter dataSetter, @Nullable Object currentValue);

    @Nullable
    Object getData(IDataGetter dataGetter, @Nullable Object value);

    @Nullable
    ISorter getSorter(IType type, SorterRef ref);
}
