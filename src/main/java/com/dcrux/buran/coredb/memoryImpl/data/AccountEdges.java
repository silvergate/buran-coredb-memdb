package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class AccountEdges {
  private Multimap<EdgeLabel, EdgeWithIndex> labelToEdges = HashMultimap.create();
  private Multimap<Long, EdgeWithIndex> currentOutEdges = HashMultimap.create();
  private Multimap<Long, EdgeWithIndex> currentInEdges = HashMultimap.create();

  public Multimap<EdgeLabel, EdgeWithIndex> getLabelToEdges() {
    return labelToEdges;
  }

  public Multimap<Long, EdgeWithIndex> getCurrentOutEdges() {
    return currentOutEdges;
  }

  public Multimap<Long, EdgeWithIndex> getCurrentInEdges() {
    return currentInEdges;
  }
}
