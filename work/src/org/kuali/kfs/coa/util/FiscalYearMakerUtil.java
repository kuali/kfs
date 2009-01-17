/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Utility methods for the fiscal year maker process
 */
public class FiscalYearMakerUtil {
    
    /**
     * this routine is reminiscent of computing in 1970, when disk space was scarce and every byte was fraught with meaning. some
     * fields are captions and titles, and they contain things like the fiscal year. for the new year, we have to update these
     * substrings in place, so they don't have to be updated by hand to display correct information in the application. we use the
     * regular expression utilities in java
     * 
     * @param newYearString
     * @param oldYearString
     * @param currentField
     * @return the updated string
     */
    private String updateStringField(String newYearString, String oldYearString, String currentField) {
        Pattern pattern = Pattern.compile(oldYearString);
        Matcher matcher = pattern.matcher(currentField);
        return matcher.replaceAll(newYearString);
    }

    /**
     * this routine is provided to update string fields which contain two-digit years that need to be updated for display. it is
     * very specific, but it's necessary. "two-digit year" means the two numeric characters preceded by a non-numeric character.
     * 
     * @param newYear
     * @param oldYear
     * @param currentString
     * @return the updated string for a two digit year
     */
    private String updateTwoDigitYear(String newYear, String oldYear, String currentString) {
        
        
  
        // group 1 is the bounded by the outermost set of parentheses
        // group 2 is the first inner set
        // group 3 is the second inner set--a two-digit year at the beginning of the line
        String regExpString = "(([^0-9]{1}" + oldYear + ")|^(" + oldYear + "))";
        Pattern pattern = Pattern.compile(regExpString);
        Matcher matcher = pattern.matcher(currentString);
        // start looking for a match
        boolean matched = matcher.find();
        if (!matched) {
            // just return if nothing is found
            return currentString;
        }
        // we found something
        // we have to process it
        String returnString = currentString;
        StringBuffer outString = new StringBuffer();
        // is there a match at the beginning of the line (a match with group 3)?
        if (matcher.group(3) != null) {
            // there is a two-digit-year string at the beginning of the line
            // we want to replace it
            matcher.appendReplacement(outString, newYear);
            // find the next match if there is one
            matched = matcher.find();
        }
        while (matched) {
            // the new string will no longer match with group 3
            // if there is still a match, it will be with group 2
            // now we have to prefix the new year string with the same
            // non-numeric character as the next match (hyphen, space, whatever)
            String newYearString = matcher.group(2).substring(0, 1) + newYear;
            matcher.appendReplacement(outString, newYearString);
            matched = matcher.find();
        }
        // dump whatever detritus is left into the new string
        matcher.appendTail(outString);
        return outString.toString();
    }
}
