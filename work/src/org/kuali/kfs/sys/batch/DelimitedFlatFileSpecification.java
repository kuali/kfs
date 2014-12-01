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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The specification for lines which are split by delimiters before being populated into objects
 */
public class DelimitedFlatFileSpecification extends AbstractFlatFilePrefixSpecificationBase {
    private static final Logger LOG = Logger.getLogger(DelimitedFlatFileSpecification.class);
    private String delimiter;

    /**
     * Splits the line based on the given delimiter and parses into properties
     * @see org.kuali.kfs.sys.batch.FlatFileSpecification#parseLineIntoObject(FlatFilePrefixObjectSpecification, String, Object)
     */
    @Override
    public void parseLineIntoObject(FlatFileObjectSpecification parseSpecification, String lineToParse, Object parseIntoObject, int lineNumber) {
        String[] lineSegments = StringUtils.splitPreserveAllTokens(lineToParse, delimiter);
        for (FlatFilePropertySpecification propertySpecification : parseSpecification.getParseProperties()) {
            try {
                propertySpecification.setProperty(lineSegments[((DelimitedFlatFilePropertySpecification) propertySpecification).getLineSegmentIndex()], parseIntoObject, lineNumber);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                LOG.debug("Unable to set property " + propertySpecification.getPropertyName() + " since lineSegmentIndex does not exist for line");
            }
        }
    }

    /**
     * Sets the delimiter to split on
     * @param delimiter the delimiter to split the substring on
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
