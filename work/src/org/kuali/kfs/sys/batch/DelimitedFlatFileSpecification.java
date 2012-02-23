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
