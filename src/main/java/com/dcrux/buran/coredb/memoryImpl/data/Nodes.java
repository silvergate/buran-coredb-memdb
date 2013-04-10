package com.dcrux.buran.coredb.memoryImpl.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caelis
 */
public class Nodes implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2012814362373736392L;
	private Map<Long, AccountNodes> receiverIdToNodes = new HashMap<>();

    public AccountNodes getByUserId(final long userId) {
        AccountNodes an = this.receiverIdToNodes.get(userId);
        if (an == null) {
            an = new AccountNodes(userId);
            this.receiverIdToNodes.put(userId, an);
        }
        return an;
    }
}
