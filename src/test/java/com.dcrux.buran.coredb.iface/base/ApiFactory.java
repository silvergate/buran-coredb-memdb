package com.dcrux.buran.coredb.iface.base;

import com.dcrux.buran.coredb.iface.api.IApi;
import com.dcrux.buran.coredb.memoryImpl.ApiIface;

/**
 * Buran.
 *
 * @author: ${USER} Date: 19.01.13 Time: 09:22
 */
public class ApiFactory {
    public IApi createBuran() {
        ApiIface apiImpl = new ApiIface();
        return apiImpl;
    }
}
