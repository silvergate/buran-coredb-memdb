package com.dcrux.buran.coredb.iface.nodeClass;

import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeClass;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 06.11.12
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public interface IBuilder {
  IBuilder add(String name, boolean required, IType type);

  IBuilder addEdgeClass(PrivateEdgeClass edgeClass);

  NodeClass get();
}
