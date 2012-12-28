package com.dcrux.buran.coredb.iface.permissions;

/**
 *
 * @author caelis
 */
public enum DomainPermActions {
  /* Read */
  readMeta,
  readEdges,
  readPayload,
  /* Hinweis für die beiden folgendend: Transferieren sollte immer möglich sein */
  readPermissionsFromOtherUsers,
  readDomains,

  /* Permissions */
  allowMore,
  allowLess,
  denyMore,
  denyLess,

  /* Update a node in this domain */
  removeOtherDomains,
  addOtherDomains,
  removeFromThisDomain,

  /* Update a node not in this domain (same as create) */
  addToThisDomain,

  /* Remove a node in this domain */
  historize,

  /* Change domain users */
  domainAllowMore,
  domainAllowLess,
  domainDenyMore,
  domainDenyLess,

  deactivateDomain
}
