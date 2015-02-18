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
