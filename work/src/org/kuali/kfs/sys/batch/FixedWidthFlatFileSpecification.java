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
 * Concrete extension of AbstractFlatFilePrefixSpecificationBase which can handle the parsing of lines where substrings
 * are parsed by knowing the beginning and ending position of the substring
 */
public class FixedWidthFlatFileSpecification extends AbstractFlatFilePrefixSpecificationBase {
    /**
     * Parses a line by pulling out substrings given by the FlatFilePropertySpecification configuration objects passed in
     * @see org.kuali.kfs.sys.batch.FlatFileSpecification#parseLineIntoObject(FlatFilePrefixObjectSpecification, String, Object)
     */
    @Override
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
