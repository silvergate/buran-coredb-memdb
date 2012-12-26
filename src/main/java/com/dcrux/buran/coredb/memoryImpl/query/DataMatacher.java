package com.dcrux.buran.coredb.memoryImpl.query;

import com.dcrux.buran.coredb.iface.api.ExpectableException;
import com.dcrux.buran.coredb.iface.nodeClass.IType;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.query.propertyCondition.*;
import com.dcrux.buran.coredb.memoryImpl.NodeClassesApi;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 26.12.12
 * Time: 01:17
 * To change this template use File | Settings | File Templates.
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

    if (propCondition instanceof PropCondition) {
      final PropCondition pcCast = (PropCondition) propCondition;
      final IType type = this.nodeClass.getType(pcCast.getTypeIndex());
      final boolean supports = type.supports(pcCast.getComparator().getRef());
      if (!supports) {
        throw new ExpectableException("The type does not support the given comparator");
      }
      final Object rhs = data[pcCast.getTypeIndex()];
      final boolean matches = pcCast.getComparator().matches(rhs);
      return matches;
    } else if (propCondition instanceof PcInverse) {
      final PcInverse pcInCast = (PcInverse) propCondition;
      return !(matchesInt(data, pcInCast.getVal()));
    } else if (propCondition instanceof PcUnion) {
      final PcUnion pcuCast = (PcUnion) propCondition;
      final boolean one = matchesInt(data, pcuCast.getVal1());
      final boolean two = matchesInt(data, pcuCast.getVal2());
      return one || two;
    } else if (propCondition instanceof PcIntersection) {
      final PcIntersection pcInt = (PcIntersection) propCondition;
      final boolean one = matchesInt(data, pcInt.getVal1());
      final boolean two = matchesInt(data, pcInt.getVal2());
      return one && two;
    } else {
      throw new ExpectableException("Unknown IPropertyCondition type.");
    }
  }
}
