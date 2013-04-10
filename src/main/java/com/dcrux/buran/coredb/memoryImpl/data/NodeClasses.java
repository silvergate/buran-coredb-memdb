package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author caelis
 */
public class NodeClasses implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7202873807884721763L;
	private Map<Long, NodeClass> classes = new HashMap<>();
    private Map<String, NodeClass> hashesToClasses = new HashMap<>();
    private Map<String, Long> hashesToClassIds = new HashMap<>();

    @Nullable
    public NodeClass getClassById(long id) {
        return this.classes.get(id);
    }

    @Nullable
    public Long getIdByHash(NodeClassHash nch) {
        return this.hashesToClassIds.get(nch.getHash());
    }

    public long storeClass(NodeClass nodeClass, NodeClassHash hash) {
        /* Don't store twice if already stored */
        final Long existingClassId = this.hashesToClassIds.get(hash.getHash());
        if (existingClassId != null) return existingClassId;

        long idCandidate;
        synchronized (this.classes) {
            Random random = new Random();
            do {
                idCandidate = random.nextLong();
            } while (this.classes.containsKey(idCandidate));
            this.classes.put(idCandidate, nodeClass);
        }
        this.hashesToClassIds.put(hash.getHash(), idCandidate);
        this.hashesToClasses.put(hash.getHash(), nodeClass);
        System.out.println("Stored as (classId): " + idCandidate);
        return idCandidate;
    }
}
