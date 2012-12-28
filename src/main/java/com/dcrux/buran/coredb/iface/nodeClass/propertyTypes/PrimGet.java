package com.dcrux.buran.coredb.iface.nodeClass.propertyTypes;

import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;

/**
 *
 * @author caelis
 */
public class PrimGet implements IDataGetter {
  public static final IDataGetter SINGLETON = new PrimGet();
}
