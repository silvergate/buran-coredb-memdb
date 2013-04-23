package com.dcrux.buran.coredb.iface.base;

import com.dcrux.buran.coredb.iface.UserId;
import com.dcrux.buran.coredb.iface.api.IApi;

import java.io.IOException;
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
    private UserId receiver2;
    private UserId receiver;

    public TestsBase() {
    }

    public IApi createBuran() throws IOException {
        return factory.createBuran();
    }

    public IApi getBuran() {
        if (this.buran == null) {
            try {
                this.buran = createBuran();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings |
                // File Templates.
            }
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

    protected UserId getReceiver2() {
        if (this.receiver2 == null) this.receiver2 = UserId.c(this.random.nextLong());
        return this.receiver2;
    }

}
