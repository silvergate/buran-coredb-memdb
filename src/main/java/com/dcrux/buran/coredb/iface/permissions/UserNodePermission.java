package com.dcrux.buran.coredb.iface.permissions;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author caelis
 */
public class UserNodePermission {

  public void add(NodePermActions nodePermActions, AllowOrDeny allowOrDeny) {
    this.map.put(nodePermActions, allowOrDeny);
  }

  private final Map<NodePermActions, AllowOrDeny> map = new HashMap<>();

  @Nullable
  public AllowOrDeny getAllowOrDeny(final NodePermActions npa) {
    return this.map.get(npa);
  }
}
