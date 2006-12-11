

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PersonRole extends BusinessObjectBase {

	private String personRoleCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private String personRoleDescription;

	/**
	 * Default constructor.
	 */
	public PersonRole() {

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
     * Gets the dataObjectMaintenanceCodeActiveIndicator attribute. 
     * @return Returns the dataObjectMaintenanceCodeActiveIndicator.
     */
    public boolean isDataObjectMaintenanceCodeActiveIndicator() {
        return dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Sets the dataObjectMaintenanceCodeActiveIndicator attribute value.
     * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
     */
    public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
    }

	/**
	 * Gets the personRoleDescription attribute.
	 * 
	 * @return Returns the personRoleDescription
	 * 
	 */
	public String getPersonRoleDescription() { 
		return personRoleDescription;
	}

	/**
	 * Sets the personRoleDescription attribute.
	 * 
	 * @param personRoleDescription The personRoleDescription to set.
	 * 
	 */
	public void setPersonRoleDescription(String personRoleDescription) {
		this.personRoleDescription = personRoleDescription;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("personRoleCode", this.personRoleCode);
	    return m;
    }

}
