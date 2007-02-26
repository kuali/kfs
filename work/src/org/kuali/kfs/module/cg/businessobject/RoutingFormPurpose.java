

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormPurpose extends PersistableBusinessObjectBase {

	private String documentNumber;
	private String purposeCode;

	/**
	 * Default constructor.
	 */
	public RoutingFormPurpose() {

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
	 * Gets the purposeCode attribute.
	 * 
	 * @return Returns the purposeCode
	 * 
	 */
	public String getPurposeCode() { 
		return purposeCode;
	}

	/**
	 * Sets the purposeCode attribute.
	 * 
	 * @param purposeCode The purposeCode to set.
	 * 
	 */
	public void setPurposeCode(String purposeCode) {
		this.purposeCode = purposeCode;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        m.put("purposeCode", this.purposeCode);
	    return m;
    }
}
