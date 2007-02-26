

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormResearchTypeCode extends PersistableBusinessObjectBase {

	private String documentNumber;
	private String researchTypeCode;

	/**
	 * Default constructor.
	 */
	public RoutingFormResearchTypeCode() {

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
	 * Gets the researchTypeCode attribute.
	 * 
	 * @return Returns the researchTypeCode
	 * 
	 */
	public String getResearchTypeCode() { 
		return researchTypeCode;
	}

	/**
	 * Sets the researchTypeCode attribute.
	 * 
	 * @param researchTypeCode The researchTypeCode to set.
	 * 
	 */
	public void setResearchTypeCode(String researchTypeCode) {
		this.researchTypeCode = researchTypeCode;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        m.put("researchTypeCode", this.researchTypeCode);
	    return m;
    }
}
