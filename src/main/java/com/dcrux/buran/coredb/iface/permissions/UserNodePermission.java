package com.dcrux.buran.coredb.iface.permissions;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 26.12.12
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
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
