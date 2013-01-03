package com.dcrux.buran.coredb.iface.edgeClass;

import com.dcrux.buran.coredb.iface.EdgeLabel;
import com.dcrux.buran.coredb.iface.nodeClass.ClassId;
import com.google.common.base.Optional;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * @author caelis
 */
public class PublicEdgeClass implements Serializable {

  private static final String QA_TRUE = "qt";
  private static final String QA_FALSE = "qf";
  private static final String CLASSID_ABSENT = "abs";

  private final UUID uuid;
  private final boolean queryable;
  private final Optional<ClassId> inEdgeClass;
  private final PublicEdgeConstraints outNodeConstraints;
  private final Optional<ClassId> outEdgeClass;

  public PublicEdgeClass(UUID uuid, boolean queryable, Optional<ClassId> inEdgeClass,
                         PublicEdgeConstraints outNodeConstraints, Optional<ClassId> outEdgeClass) {
    this.uuid = uuid;
    this.queryable = queryable;
    this.inEdgeClass = inEdgeClass;
    this.outNodeConstraints = outNodeConstraints;
    this.outEdgeClass = outEdgeClass;
  }

  public EdgeLabel createLabel() {
    final String uuidLow = fromLong(this.uuid.getLeastSignificantBits());
    final String uuidHi = fromLong(this.uuid.getMostSignificantBits());
    final String qaString;
    if (this.queryable) {
      qaString = QA_TRUE;
    } else {
      qaString = QA_FALSE;
    }
    final String constraints = Character.toString(this.outNodeConstraints.getValue());
    final String inEdgeClassStr;
    if (this.inEdgeClass.isPresent()) {
      inEdgeClassStr = fromLong(this.inEdgeClass.get().getId());
    } else {
      inEdgeClassStr = CLASSID_ABSENT;
    }
    final String outEdgeClassStr;
    if (this.outEdgeClass.isPresent()) {
      outEdgeClassStr = fromLong(this.outEdgeClass.get().getId());
    } else {
      outEdgeClassStr = CLASSID_ABSENT;
    }

    final String resultingString = MessageFormat
            .format("{0}:{1}:{2}:{3}:{4}:{5}", uuidLow, uuidHi, qaString, constraints, inEdgeClassStr, outEdgeClassStr);
    return EdgeLabel.publicEdge(resultingString);
  }

  private static long toLong(String hex) {
    try {
      final byte[] byteData = Hex.decodeHex(hex.toCharArray());
      ByteBuffer buf = ByteBuffer.wrap(byteData);
      return buf.getLong();
    } catch (DecoderException e) {
      throw new IllegalArgumentException("Unparsable hex string. Invalid edge label.");
    }
  }

  private static String fromLong(long value) {
    final byte b[] = new byte[8];
    final ByteBuffer buf = ByteBuffer.wrap(b);
    buf.putLong(value);
    final String hexString = new String(Hex.encodeHex(b));
    return hexString;
  }

  public static PublicEdgeClass parse(EdgeLabel label) {
    if (!label.isPublic()) {
      throw new IllegalArgumentException("Can only parse public labels");
    }
    final String[] split = label.getLabel().split(":");
    final String pub = split[0];
    final String uuidLow = split[1];
    final String uuidHi = split[2];
    final String qaString = split[3];
    final String constraintsStr = split[4];
    final String inEdgeClassStr = split[5];
    final String outEdgeClassStr = split[6];

    final UUID uuid = new UUID(toLong(uuidHi), toLong(uuidLow));
    final boolean queryableBoolean = qaString.equals(QA_TRUE);
    final PublicEdgeConstraints constr = PublicEdgeConstraints.fromValue(constraintsStr.charAt(0));

    final Optional<ClassId> inEdgeClassOpt;
    if (inEdgeClassStr.equals(CLASSID_ABSENT)) {
      inEdgeClassOpt = Optional.absent();
    } else {
      inEdgeClassOpt = Optional.of(new ClassId(toLong(inEdgeClassStr)));
    }

    final Optional<ClassId> outEdgeClassOpt;
    if (outEdgeClassStr.equals(CLASSID_ABSENT)) {
      outEdgeClassOpt = Optional.absent();
    } else {
      outEdgeClassOpt = Optional.of(new ClassId(toLong(outEdgeClassStr)));
    }

    return new PublicEdgeClass(uuid, queryableBoolean, inEdgeClassOpt, constr, outEdgeClassOpt);
  }

  public UUID getUuid() {
    return uuid;
  }

  public boolean isQueryable() {
    return queryable;
  }

  public Optional<ClassId> getInEdgeClass() {
    return inEdgeClass;
  }

  public PublicEdgeConstraints getOutNodeConstraints() {
    return outNodeConstraints;
  }

  public Optional<ClassId> getOutEdgeClass() {
    return outEdgeClass;
  }
}
