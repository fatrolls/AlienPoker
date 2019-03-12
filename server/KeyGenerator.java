package server;

import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class KeyGenerator
{
  public static String createUniqueKey()
  {
    String key = getUniqueStr();
    key = md5(key);

    return key;
  }

  private static String getUniqueStr()
  {
    double rand = Math.random();
    long time = new Date().getTime();
    return rand + time;
  }

  private static String asHex(byte[] hash)
  {
    StringBuffer buf = new StringBuffer(hash.length * 2);

    for (int i = 0; i < hash.length; i++) {
      if ((hash[i] & 0xFF) < 16)
        buf.append("0");
      buf.append(Long.toString(hash[i] & 0xFF, 16));
    }

    return buf.toString();
  }

  public static String md5(String input)
  {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hash = md.digest(input.getBytes());
      return asHex(hash);
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args)
  {
    System.out.println("1:" + createUniqueKey());
    System.out.println("2:" + createUniqueKey());
    System.out.println("3:" + createUniqueKey());
  }
}