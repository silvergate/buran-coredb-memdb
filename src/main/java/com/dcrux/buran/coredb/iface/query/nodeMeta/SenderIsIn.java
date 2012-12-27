package com.dcrux.buran.coredb.iface.query.nodeMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 13.12.12
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class SenderIsIn implements INodeMetaCondition {
  private final Set<Long> userIds;

  public SenderIsIn(Long... userId) {
    this.userIds = new HashSet<>();
    this.userIds.addAll(Arrays.asList(userId));
  }

  public Set<Long> getUserIds() {
    return Collections.unmodifiableSet(this.userIds);
  }

  @Override
  public boolean matches(IMetaInfoForQuery metaInfoForQuery) {
    final long sender = metaInfoForQuery.getSender();
    return this.userIds.contains(sender);
  }
}
