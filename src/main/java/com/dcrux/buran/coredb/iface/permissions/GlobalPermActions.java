package com.dcrux.buran.coredb.iface.permissions;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 25.12.12
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public enum GlobalPermActions {
  /* Query */
  queryMeta(0),
  queryEdges(1),
  queryPayload(2),

  /* Read */
  readMeta(10),
  readEdges(11),
  readPayload(12),
  /* Hinweis für die beiden folgendend: Transferieren sollte immer möglich sein */
  readPermissionsFromOtherUsers(13),
  readDomains(14),

  /* Permissions */
  allowMore(20),
  allowLess(21),
  denyMore(22),
  denyLess(23),

  /* Update a node */
  update(30),
  addToDomain(31),
  removeFromDomain(32),

  /* Create node */
  create(40),

  /* Remove a node in this domain */
  historize(50),

  /* Domain permission */
  createDomain(60),

  deactivateDomain(70);

  private byte value;

  GlobalPermActions(int value) {
    assert (value >= 0);
    assert (value <= 255);
    this.value = (byte) (value + Byte.MIN_VALUE);
  }
}
