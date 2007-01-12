package edu.yale.its.tp.cas.ticket;

import java.security.SecureRandom;

/**
 * Some static utility methods.
 */
public class Util {

  private static int TRANSACTION_ID_LENGTH = 32;

  /** Returns a printable String corresponding to a byte array. */
  public static synchronized String toPrintable(byte[] b) {
    final char[] alphabet = ("abcdefghijklmnopqrstuvwxyz"
      + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      + "1234567890").toCharArray();
    char[] out = new char[b.length];
    for (int i = 0; i < b.length; i++) {
      int index = b[i] % alphabet.length;
        if (index < 0)
          index += alphabet.length;
        out[i] = alphabet[index];
      }
    return new String(out);
  }

  public static String getTransactionId() {
    // produce the random transaction ID
    byte[] b = new byte[TRANSACTION_ID_LENGTH];
    SecureRandom sr = new SecureRandom();
    sr.nextBytes(b);
    return Util.toPrintable(b);
  }
}
