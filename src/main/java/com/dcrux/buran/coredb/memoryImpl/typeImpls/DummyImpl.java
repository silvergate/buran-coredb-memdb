package com.dcrux.buran.coredb.memoryImpl.typeImpls;

import com.dcrux.buran.coredb.iface.nodeClass.*;

import javax.annotation.Nullable;

/**
 * Buran.
 *
 * @author: ${USER} Date: 13.01.13 Time: 10:59
 */
public class DummyImpl implements ITypeImpl {


    @Override
    public TypeRef getRef() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public Object setData(IDataSetter dataSetter, @Nullable Object currentValue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public Object getData(IDataGetter dataGetter, @Nullable Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public ISorter getSorter(IType type, SorterRef ref) {
        return null;
    }
}
