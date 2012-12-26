package com.dcrux.buran.coredb.memoryImpl.data;

import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 19.12.12
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public class NodeClasses {
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
