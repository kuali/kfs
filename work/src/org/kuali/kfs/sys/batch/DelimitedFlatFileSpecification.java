/*
 * Copyright 2012 The Kuali Foundation.
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
