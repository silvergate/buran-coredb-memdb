package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClassHash;
import com.dcrux.buran.coredb.memoryImpl.data.NodeClasses;
import org.apache.commons.lang.SerializationUtils;

import javax.annotation.Nullable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: caelis
 * Date: 19.12.12
 * Time: 21:01
 * To change this template use File | Settings | File Templates.
 */
public class NodeClassesApi {

  public NodeClassesApi(NodeClasses classes) {
    this.classes = classes;
  }

  private final NodeClasses classes;

  public NodeClassHash declareClass(NodeClass nodeClass) {
    byte[] ser = SerializationUtils.serialize(nodeClass);
    final MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
      byte[] hash = md.digest(ser);
      final String hashStr = new String(hash);
      final NodeClassHash nch = new NodeClassHash(hashStr);
      this.classes.storeClass(nodeClass, nch);
      return nch;
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  @Nullable
  public Long getClassIdByHash(NodeClassHash hash) {
    return this.classes.getIdByHash(hash);
  }

  @Nullable
  public NodeClass getClassById(long classId) {
    return this.classes.getClassById(classId);
  }

}
