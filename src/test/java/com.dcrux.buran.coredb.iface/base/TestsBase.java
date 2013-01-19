package com.dcrux.buran.coredb.iface.base;

import com.dcrux.buran.coredb.iface.UserId;
import com.dcrux.buran.coredb.iface.api.IApi;

import java.util.Random;

/**
 * Buran.
 *
 * @author: ${USER} Date: 19.01.13 Time: 09:23
 */
public class TestsBase {

    private final ApiFactory factory = new ApiFactory();
    private IApi buran;
    private final Random random = new Random();
    private UserId sender;
    private UserId receiver;

    public TestsBase() {
    }

    public IApi createBuran() {
        return factory.createBuran();
    }

    public IApi getBuran() {
        if (this.buran == null) {
            this.buran = createBuran();
        }
        return this.buran;
    }

    protected UserId getReceiver() {
        if (this.receiver == null) this.receiver = UserId.c(this.random.nextLong());
        return this.receiver;
    }

    protected UserId getSender() {
        if (this.sender == null) this.sender = UserId.c(this.random.nextLong());
        return this.sender;
    }


}
