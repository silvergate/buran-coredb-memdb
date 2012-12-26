package com.dcrux.buran.coredb.iface.nodeClass;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 03.11.12
 * Time: 00:21
 * To change this template use File | Settings | File Templates.
 */
public interface IType extends Serializable {
  TypeRef getRef();

  boolean supports(SorterRef sorting);

  boolean supports(CmpRef comparator);

  boolean supports(IDataSetter dataSetter);

  boolean supports(IDataGetter dataGetter);

}
