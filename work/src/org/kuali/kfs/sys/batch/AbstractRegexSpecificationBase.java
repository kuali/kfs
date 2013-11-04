/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * The abstract parent of flat file specifications which use regexes to determine what line is parsed into what object
 */
public abstract class AbstractRegexSpecificationBase extends AbstractFlatFileSpecificationBase {
    protected List<String> insignificantRegexPatterns;
    protected List<Pattern> insignificantPatterns;
    protected boolean trimLineBeforeMatch;
    protected boolean fullMatch = true;

    /**
     * Matches the given line with an object to parse into, or null if no object could be found
     * @see org.kuali.kfs.sys.batch.FlatFileSpecification#determineClassForLine(java.lang.String)
     */
    public Class<?> determineClassForLine(String line) {
        final String matchLine = trimLine(line);
        for (FlatFileObjectSpecification objectSpecification : getObjectSpecifications()) {
            final FlatFileRegexObjectSpecification regexObjectSpecification = (FlatFileRegexObjectSpecification)objectSpecification;
            final Pattern pattern = regexObjectSpecification.getPattern();
            if (matches(pattern, matchLine)) {
                return regexObjectSpecification.getBusinessObjectClass();
            }
        }
        for (Pattern insignificantPattern : getInsignificantPatterns()) {
            if (matches(insignificantPattern, matchLine)) return null;
        }
        return defaultBusinessObjectClass;
    }
    
    /**
     * Trims the trailing space only from the given line, though only if trimLineBeforeMatch is true
     * @param line the line to perhaps trim trailing spaces from
     * @return the maybe trimmed line
     */
    protected String trimLine(String line) {
        if (isTrimLineBeforeMatch()) {
            return StringUtils.stripEnd(line, " \t\n\f\r");
        }
        return line;
    }

    /**
     * Sets the insignificant regex patterns
     * @param insignificantRegexPatterns the regex patterns for lines to ignore
     */
    public void setInsignificantRegexPatterns(List<String> insignificantRegexPatterns) {
        this.insignificantRegexPatterns = insignificantRegexPatterns;
        
        this.insignificantPatterns = new ArrayList<Pattern>();
        for (String regexPattern : insignificantRegexPatterns) {
            final Pattern pattern = Pattern.compile(regexPattern);
            insignificantPatterns.add(pattern);
        }
    }
    
    /**
     * Determines if the given line matches the given pattern, following the full match rule
     * @param pattern the pattern to match against
     * @param line the parsed line to match
     * @return true if the line matches and the line should be parsed by this object specification; false if this line should be given to the next object specification
     */
    protected boolean matches(Pattern pattern, String line) {
        Matcher matcher = pattern.matcher(line);
        if (fullMatch) {
            return matcher.matches();
        } else {
            return matcher.find();
        }
    }

    /**
     * @return the List of compiled insignificant patterns
     */
    public List<Pattern> getInsignificantPatterns() {
        return this.insignificantPatterns;
    }

    /**
     * @return whether a parsed line will have trailing spaces removed (and trailing spaces only!)
     */
    public boolean isTrimLineBeforeMatch() {
        return trimLineBeforeMatch;
    }

    /**
     * Sets whether this will strip trailing spaces before parsing the line.  Defaults to false.
     * @param trimLineBeforeMatch true if trailing spaces should be stripped, false otherwise
     */
    public void setTrimLineBeforeMatch(boolean trimLineBeforeMatch) {
        this.trimLineBeforeMatch = trimLineBeforeMatch;
    }

    /**
     * @return whether the regular expression associated with this object specification will attempt to match the full line or (if false) simply find the pattern somewhere within the line
     */
    public boolean isFullMatch() {
        return fullMatch;
    }

    /**
     * Sets whether the regular expression associated with this object specification will attempt to match against the whole line or search for the pattern within the line.  If true, it will match the full line, and this is the default.
     * @param fullMatch true if match against the full line should be carried out, false otherwise
     */
    public void setFullMatch(boolean fullMatch) {
        this.fullMatch = fullMatch;
    }
}
