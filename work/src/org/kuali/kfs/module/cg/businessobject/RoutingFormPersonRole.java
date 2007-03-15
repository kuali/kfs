package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormPersonRole extends PersistableBusinessObjectBase {

	private String documentNumber;
	private String personRoleCode;

	/**
	 * Default constructor.
	 */
	public RoutingFormPersonRole() {

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
	 * Gets the personRoleCode attribute.
	 * 
	 * @return Returns the personRoleCode
	 * 
	 */
	public String getPersonRoleCode() { 
		return personRoleCode;
	}

	/**
	 * Sets the personRoleCode attribute.
	 * 
	 * @param personRoleCode The personRoleCode to set.
	 * 
	 */
	public void setPersonRoleCode(String personRoleCode) {
		this.personRoleCode = personRoleCode;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        m.put("personRoleCode", this.personRoleCode);
	    return m;
    }
}
