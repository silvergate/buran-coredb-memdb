package com.dcrux.buran.coredb.iface.nodeClass;

import java.io.Serializable;

/**
 *
 * @author caelis
 */
public interface IType extends Serializable {
  TypeRef getRef();

  boolean supports(SorterRef sorting);

  boolean supports(CmpRef comparator);

  boolean supports(IDataSetter dataSetter);

  boolean supports(IDataGetter dataGetter);

}
