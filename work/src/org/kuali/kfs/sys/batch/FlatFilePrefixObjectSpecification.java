package org.kuali.kfs.sys.batch;


import org.apache.commons.beanutils.PropertyUtils;

/**
 * The specification for a business object which should be parsed into during the parsing of a flat file
 */
public class FlatFilePrefixObjectSpecification extends AbstractFlatFileObjectSpecification {

	protected String linePrefix;
    /**
     * @return the prefix of the line which determines if the given line should be associated with this object specification
     */
    public String getLinePrefix() {
		return linePrefix;
	}

    /**
     * Sets the prefix that configures which lines this object specification will be associated with
     * @param linePrefix the prefix
     */
	public void setLinePrefix(String linePrefix) {
		this.linePrefix = linePrefix;
	}
}
