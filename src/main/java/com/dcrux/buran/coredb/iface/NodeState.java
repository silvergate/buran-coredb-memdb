package com.dcrux.buran.coredb.iface;

/**
 *
 * @author caelis
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
