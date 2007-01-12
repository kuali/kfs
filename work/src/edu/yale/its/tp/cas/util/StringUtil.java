package edu.yale.its.tp.cas.util;

/**
 * <p>A class housing some utility functions related to String manipulation.</p>
 * <p>Copyright 2000, Shawn Bayern.</p>
 */
public class StringUtil {

  /**
   * Replaces all occurrences of an old String with a new String in
   * the given String.
   */
  public static String substituteAll(String s, String o, String n) {
    if (s == null)
      return null;
    while (s.indexOf(o) != -1)
      s = substituteOne(s, o, n);
    return s;
  }

  /**
   * Replaces one occurrence of an old String with a new String in
   * the given String.
   */
  public static String substituteOne(String s, String o, String n) {
    if (s == null)
      return null;
    int begin = s.indexOf(o);
    if (begin == -1)
      return s;
    int end = begin + o.length();
    return (new StringBuffer(s)).replace(begin, end, n).toString();
  }
}
