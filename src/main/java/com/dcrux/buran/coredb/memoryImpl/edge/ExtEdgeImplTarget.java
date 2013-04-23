package com.dcrux.buran.coredb.memoryImpl.edge;

import javax.annotation.Nullable;

/**
 * @author caelis
 */
public class ExtEdgeImplTarget implements IEdgeImplTarget {
    /**
     *
     */
    private static final long serialVersionUID = -3286036567180943701L;
    private final long userId;
    private final long oid;
    @Nullable
    private final Integer version;

    public ExtEdgeImplTarget(long userId, long oid, @Nullable Integer version) {
        this.userId = userId;
        this.oid = oid;
        this.version = version;
    }

    public long getUserId() {
        return userId;
    }

    public long getOid() {
        return oid;
    }

    @Nullable
    public Integer getVersion() {
        return version;
    }
}
