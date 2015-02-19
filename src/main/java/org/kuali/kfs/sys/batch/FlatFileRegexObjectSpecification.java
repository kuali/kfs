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

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * FlatFileObjectSpecification for files which match lines to objects via regular expression patterns
 */
public class FlatFileRegexObjectSpecification extends AbstractFlatFileObjectSpecification {
    protected String regexPattern;
    protected Pattern pattern;
    
    /**
     * @return the regular expression pattern to match lines with
     */
    public String getRegexPattern() {
        return regexPattern;
    }
    /**
     * Sets a regex pattern to match lines with
     * @param regexPattern the regular expression pattern to match lines with
     */
    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }
    /**
     * @return the Pattern to match lines for this object specification
     */
    public Pattern getPattern() {
        if (pattern == null && !StringUtils.isBlank(regexPattern)) {
            pattern = Pattern.compile(regexPattern);
        }
        return pattern;
    }
    
    
}
