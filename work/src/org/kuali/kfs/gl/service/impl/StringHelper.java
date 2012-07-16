/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.gl.ObjectHelper;

/**
 * Provides helper methods related to evaluating Strings 
 */
public class StringHelper extends ObjectHelper {

    /**
     * This method returns true if String object is empty and NOT null 
     * 
     * @param s evaluated String
     * @return true if String object is empty and NOT null
     */
    static public boolean isEmpty(String s) {
        return (s != null) && "".equals(s);
    }

    /**
     * This method returns true if String object is null or empty
     * @param s evaluated String
     * @return true if String object is null or empty
     */
    static public boolean isNullOrEmpty(String s) {
        return (s == null) || isEmpty(s);
    }
 

    /**
     * Right justify the contents of the string, ensuring that the string ends at the last character. If the supplied string is
     * longer than the desired width, the leading characters are removed so that the last character in the supplied string at the
     * last position. If the supplied string is shorter than the desired width, the padding character is inserted one or more
     * times such that the last character in the supplied string appears as the last character in the resulting string and that
     * the length matches that specified.
     * 
     * @param str the string to be right justified; if null, an empty string is used
     * @param width the desired width of the string; must be positive
     * @param padWithChar the character to use for padding, if needed
     * @return the right justified string
     */
    public static String justifyRight( String str,
                                       final int width,
                                       char padWithChar ) {
        assert width > 0;
        // Trim the leading and trailing whitespace ...
        str = str != null ? str.trim() : "";

        final int length = str.length();
        int addChars = width - length;
        if (addChars < 0) {
            // truncate the first characters, keep the last
            return str.subSequence(length - width, length).toString();
        }
        // Prepend the whitespace ...
        final StringBuilder sb = new StringBuilder();
        while (addChars > 0) {
            sb.append(padWithChar);
            --addChars;
        }

        // Write the content ...
        sb.append(str);
        return sb.toString();
    }
}
