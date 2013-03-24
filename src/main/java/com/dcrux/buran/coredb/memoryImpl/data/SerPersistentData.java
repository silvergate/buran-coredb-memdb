package com.dcrux.buran.coredb.memoryImpl.data;

import java.io.Serializable;

/**
 * Buran.
 *
 * @author: ${USER} Date: 23.03.13 Time: 16:06
 */
public class SerPersistentData implements Serializable {

    private Nodes nodes;
    private NodeClasses nodeClasses;
    private Domains domains;
    private Subscriptions subscriptions;

    public Nodes getNodes() {
        if (this.nodes == null) {
            this.nodes = new Nodes();
        }
        return nodes;
    }


    public NodeClasses getNodeClasses() {
        if (this.nodeClasses == null) {
            this.nodeClasses = new NodeClasses();
        }
        return nodeClasses;
    }


    public Domains getDomains() {
        if (this.domains == null) {
            this.domains = new Domains();
        }
        return domains;
    }

    public Subscriptions getSubscriptions() {
        if (this.subscriptions == null) {
            this.subscriptions = new Subscriptions();
        }
        return subscriptions;
    }

}
