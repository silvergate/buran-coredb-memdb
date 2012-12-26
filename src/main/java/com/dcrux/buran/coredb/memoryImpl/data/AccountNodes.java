package com.dcrux.buran.coredb.memoryImpl.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 11.12.12
 * Time: 01:13
 * To change this template use File | Settings | File Templates.
 */
public class AccountNodes {
  Map<Long, NodesSingleClass> classIdToSingleClass = new HashMap<>();
  Map<Long, Long> oidToClassId = new HashMap<>();
  Map<Long, Long> incOidToClassId = new HashMap<>();

  private final Map<Long, NodeWithClassId> oidToNodeWithClass = new HashMap<>();

  private transient AtomicLong incOidCounter = new AtomicLong(0L);
  private transient AtomicLong oidCounter = new AtomicLong(0L);

  public Map<Long, NodeWithClassId> getOidToNodeWithClass() {
    return oidToNodeWithClass;
  }

  public AtomicLong getIncOidCounter() {
    return incOidCounter;
  }

  public AtomicLong getOidCounter() {
    return oidCounter;
  }

  public Map<Long, NodesSingleClass> getClassIdToSingleClass() {
    return classIdToSingleClass;
  }

  public Map<Long, Long> getOidToClassId() {
    return oidToClassId;
  }

  public Map<Long, Long> getIncOidToClassId() {
    return incOidToClassId;
  }

  public NodesSingleClass getByClassId(final long classId) {
    NodesSingleClass nsc = this.classIdToSingleClass.get(classId);
    if (nsc == null) {
      nsc = new NodesSingleClass();
      this.classIdToSingleClass.put(classId, nsc);
    }
    return nsc;
  }
}
