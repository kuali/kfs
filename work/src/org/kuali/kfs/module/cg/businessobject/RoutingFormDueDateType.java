

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormDueDateType extends PersistableBusinessObjectBase {

	private String documentNumber;
	private String dueDateTypeCode;
	private boolean yesNoIndicator;

	/**
	 * Default constructor.
	 */
	public RoutingFormDueDateType() {

	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the dueDateTypeCode attribute.
	 * 
	 * @return Returns the dueDateTypeCode
	 * 
	 */
	public String getDueDateTypeCode() { 
		return dueDateTypeCode;
	}

	/**
	 * Sets the dueDateTypeCode attribute.
	 * 
	 * @param dueDateTypeCode The dueDateTypeCode to set.
	 * 
	 */
	public void setDueDateTypeCode(String dueDateTypeCode) {
		this.dueDateTypeCode = dueDateTypeCode;
	}


	/**
	 * Gets the yesNoIndicator attribute.
	 * 
	 * @return Returns the yesNoIndicator
	 * 
	 */
	public boolean isYesNoIndicator() { 
		return yesNoIndicator;
	}

	/**
	 * Sets the yesNoIndicator attribute.
	 * 
	 * @param yesNoIndicator The yesNoIndicator to set.
	 * 
	 */
	public void setYesNoIndicator(boolean yesNoIndicator) {
		this.yesNoIndicator = yesNoIndicator;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        m.put("dueDateTypeCode", this.dueDateTypeCode);
	    return m;
    }
}
