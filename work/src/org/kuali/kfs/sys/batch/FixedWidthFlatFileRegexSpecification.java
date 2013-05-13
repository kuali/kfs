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

/**
 * A flat file specification which matches lines via regular expressions and parses them via flat files
 */
public class FixedWidthFlatFileRegexSpecification extends AbstractRegexSpecificationBase {

    /**
     * Parses the given line into the given object to parse into
     * @param parseSpecification the specification of the parsing
     * @param lineToParse the line to parse into the object
     * @param parseIntoObject the object to parse into
     */
    public void parseLineIntoObject(FlatFileObjectSpecification parseSpecification, String lineToParse, Object parseIntoObject, int lineNumber) {
        // loop through the properties to format and set the property values
        // from the input line
        for (FlatFilePropertySpecification propertySpecification : parseSpecification.getParseProperties()) {
            int start = ((FixedWidthFlatFilePropertySpecification) propertySpecification).getStart();
            int end = ((FixedWidthFlatFilePropertySpecification) propertySpecification).getEnd();
            // if end is not specified, read to the end of line
            if (end == 0) {
                end = lineToParse.length();
            }
            String subString = lineToParse.substring(start, end);
            propertySpecification.setProperty(subString, parseIntoObject, lineNumber);
        }
    }

}
