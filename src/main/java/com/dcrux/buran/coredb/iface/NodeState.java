package com.dcrux.buran.coredb.iface;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 15.12.12
 * Time: 12:19
 * To change this template use File | Settings | File Templates.
 */
public class NodeState {

  public static enum NodeStateReason {
    available,
    historized,
    tempUnavailable
  }

  public static enum State {
    available,
    propertiesMissing,
    propertiesAndMetadataButClassMissing
  }

  private final byte state;

  public NodeState(byte state) {
    this.state = state;
  }

  public static NodeState state(NodeStateReason state, State reason) {
    return new NodeState((byte) 0);
  }

  public State getState() {
    return null;
  }

  public NodeStateReason getReason() {
    return null;
  }

}
