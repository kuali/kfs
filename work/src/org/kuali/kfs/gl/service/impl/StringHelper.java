/**
 * 
 */
package org.kuali.module.gl.util;

import java.util.regex.Pattern;

/**
 * @author Evans
 *
 */
public class StringHelper extends ObjectHelper {

	static public boolean isEmpty(String s) {
		return !isNull(s) && "".equals(s);
	}
	
	/**
	 * 
	 * @param s
	 * @return true if !isNull(s) && s.toString() is all dashes.
	 */
	static public boolean isDashes(String s) {
		return !isNull(s) && Pattern.matches("^-*$", s);
	}
	
	static public boolean isNullOrEmpty(String s) {
		return isNull(s) || isEmpty(s);
	}
	
	static public boolean isEmptyOrDashes(String s) {
		return isEmpty(s) || isDashes(s);
	}
	
	static public boolean isNullOrEmptyOrDashes(String s) {
		return isNullOrEmpty(s) || isDashes(s);
	}
}
