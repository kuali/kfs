/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.rice.krad.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Builds a <code>{@link String}</code> instance using a pattern similar to the varargs printf() variety.
 * 
 * 
 */
public class PatternedStringBuilder {
    private String _pattern;

    /**
     * Constructor that takes a pattern
     * 
     * @param pattern
     */
    public PatternedStringBuilder(String pattern) {
        setPattern(pattern);
    }

    /**
     * Write accessor method for pattern
     * 
     * @param pattern
     */
    public void setPattern(String pattern) {
        _pattern = pattern;
    }

    /**
     * Read accessor method for pattern
     * 
     * @return String
     */
    public String getPattern() {
        return _pattern;
    }

    /**
     * Takes an ellipses of <code>{@link String}</code> parameters and builds a <code>{@link String}</code> instance from them
     * and the pattern given earlier.
     * 
     * @param args
     * @return String
     */
    public String sprintf(Object... args) {
        ByteArrayOutputStream retval = new ByteArrayOutputStream();

        new PrintStream(retval).printf(getPattern(), args);

        return retval.toString();
    }
}
