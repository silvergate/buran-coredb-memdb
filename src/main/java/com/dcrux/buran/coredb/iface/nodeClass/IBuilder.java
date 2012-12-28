package com.dcrux.buran.coredb.iface.nodeClass;

import com.dcrux.buran.coredb.iface.edgeClass.PrivateEdgeClass;

/**
 *
 * @author caelis
 */
public interface IBuilder {
  IBuilder add(String name, boolean required, IType type);

  IBuilder addEdgeClass(PrivateEdgeClass edgeClass);

  NodeClass get();
}
