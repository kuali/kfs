/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
