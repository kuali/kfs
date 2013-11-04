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
