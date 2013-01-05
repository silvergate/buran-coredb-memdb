package com.dcrux.buran.coredb.memoryImpl.query;

import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.query.propertyCondition.IPropertyCondition;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;

/**
 * @author caelis
 */
public class DataMatacher {
    private final NodeClassesApi nodeClassesApi;
    private final long classId;
    private NodeClass nodeClass;

    public DataMatacher(NodeClassesApi nodeClassesApi, long classId) {
        this.nodeClassesApi = nodeClassesApi;
        this.classId = classId;
    }

    public boolean matches(Object[] data, IPropertyCondition propCondition) {
        if (this.nodeClass == null) {
            this.nodeClass = this.nodeClassesApi.getClassById(this.classId);
            assert (this.nodeClass != null);
        }
        return matchesInt(data, propCondition);
    }

    private boolean matchesInt(Object[] data, IPropertyCondition propCondition) {
        if (this.nodeClass == null) {
            this.nodeClass = this.nodeClassesApi.getClassById(this.classId);
            assert (this.nodeClass != null);
        }

        return propCondition.matches(data, this.nodeClass);
    }
}
