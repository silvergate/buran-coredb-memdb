package com.dcrux.buran.coredb.iface.base;

import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.memoryImpl.ApiIface;

import java.io.File;
import java.io.IOException;

/**
 * Buran.
 *
 * @author: ${USER} Date: 19.01.13 Time: 09:22
 */
public class ApiFactory {

    private static File getPersistenceFile() throws IOException {
        String userHome = System.getProperty("user.home");
        File serFile = new File(new File(userHome), "buran.tests.1.ser");
        if (serFile.exists()) {
            serFile.delete();
        }
        return serFile;
    }

    public IApi createBuran() throws IOException {
        ApiIface apiImpl = new ApiIface(getPersistenceFile());
        return apiImpl;
    }
}
