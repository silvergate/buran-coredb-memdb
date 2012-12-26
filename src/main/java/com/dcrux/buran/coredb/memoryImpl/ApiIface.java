package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.memoryImpl.data.Edges;
import com.dcrux.buran.coredb.memoryImpl.data.NodeClasses;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class ApiIface {
  private final CommitApi commitApi;
  private final DmApi dataManipulationApi;
  private final DataReadApi dataReadApi;
  private final NodeClassesApi nodeClassesApi;
  private final TypesRegistry typesRegistry;
  private final MiApi metaApi;

  public ApiIface() {
    this.typesRegistry = new TypesRegistry();
    Edges edges = new Edges();
    Nodes nodes = new Nodes();
    NodeClasses ncs = new NodeClasses();
    this.nodeClassesApi = new NodeClassesApi(ncs);
    this.dataManipulationApi = new DmApi(edges, nodes, this.nodeClassesApi, typesRegistry);
    this.dataReadApi = new DataReadApi(edges, nodes, this.nodeClassesApi, typesRegistry);
    this.commitApi = new CommitApi(edges, nodes, this.dataReadApi, this.nodeClassesApi);
    this.metaApi = new MiApi(this.dataReadApi, this.dataManipulationApi);
  }

  public CommitApi getCommitApi() {
    return commitApi;
  }

  public DmApi getDmApi() {
    return dataManipulationApi;
  }

  public DataReadApi getDrApi() {
    return dataReadApi;
  }

  public NodeClassesApi getNodeClassesApi() {
    return nodeClassesApi;
  }

  public MiApi getMetaApi() {
    return metaApi;
  }
}
