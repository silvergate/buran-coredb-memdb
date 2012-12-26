package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.memoryImpl.data.Edges;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 23:17
 * To change this template use File | Settings | File Templates.
 */
public class QueryApi {
  private final Edges edges;
  private final Nodes nodes;

  public QueryApi(Edges edges, Nodes nodes) {
    this.edges = edges;
    this.nodes = nodes;
  }

}
