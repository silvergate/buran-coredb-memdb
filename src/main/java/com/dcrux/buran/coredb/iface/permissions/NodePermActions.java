package com.dcrux.buran.coredb.iface.permissions;

/**
 *
 * @author caelis
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
