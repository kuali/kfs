package org.kuali.kfs.sys.batch;

/**
 * A contract of methods which must be implemented by configuration elements which associate setting a substring
 * of a parsed line as a property on a business object
 */
public interface FlatFilePropertySpecification {
    /**
     * Sets the property on the business object
     * @param value the substring of the parsed line to set
     * @param businessObject the business object to set the parsed line on
     * @param lineNumber the current line number 
     */
	public void setProperty(String value, Object businessObject, int lineNumber);
	
	/**
	 * @return the name of the property that should be set
	 */
	public String getPropertyName();
}
