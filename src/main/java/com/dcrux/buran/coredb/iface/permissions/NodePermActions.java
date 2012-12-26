package com.dcrux.buran.coredb.iface.permissions;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 25.12.12
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public enum NodePermActions {

  /* Query */
  queryMeta,
  queryEdges,
  queryPayload,

  /* Read */
  readMeta,
  readEdges,
  readPayload,
  /* Hinweis für die beiden folgendend: Transferieren sollte immer möglich sein */
  readPermissionsFromOtherUsers,
  readDomains,

  /* Permissions (update) */
  allowMore,
  allowLess,
  denyMore,
  denyLess,

  /* Domains (update) */
  addToDomain,
  removeFromDomain,

  /* Remove a node */
  historize
}
