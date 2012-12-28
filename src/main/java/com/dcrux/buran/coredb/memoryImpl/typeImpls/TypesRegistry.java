package com.dcrux.buran.coredb.memoryImpl.typeImpls;

import com.dcrux.buran.coredb.iface.nodeClass.TypeRef;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author caelis
 */
public class TypesRegistry {
  private Map<TypeRef, ITypeImpl> typeRef2TypeImpl = new HashMap<>();

  private void register(ITypeImpl timpl) {
    assert (!this.typeRef2TypeImpl.containsKey(timpl.getRef()));
    this.typeRef2TypeImpl.put(timpl.getRef(), timpl);
  }

  public ITypeImpl get(TypeRef ref) {
    ITypeImpl timpl = this.typeRef2TypeImpl.get(ref);
    assert (timpl != null);
    return timpl;
  }

  public TypesRegistry() {
    register(new StringImpl());
    register(new IntImpl());
  }
}
