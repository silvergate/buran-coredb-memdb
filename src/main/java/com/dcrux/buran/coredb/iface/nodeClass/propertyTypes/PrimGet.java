package com.dcrux.buran.coredb.iface.nodeClass.propertyTypes;

import com.dcrux.buran.coredb.iface.nodeClass.IDataGetter;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 24.12.12
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class PrimGet implements IDataGetter {
  public static final IDataGetter SINGLETON = new PrimGet();
}
