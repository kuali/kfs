

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DueDateType extends PersistableBusinessObjectBase {

	private String dueDateTypeCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private Integer approvalLeadTime;
	private String dueDateDescription;

	/**
	 * Default constructor.
	 */
	public DueDateType() {

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
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean isDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}


	/**
	 * Gets the approvalLeadTime attribute.
	 * 
	 * @return Returns the approvalLeadTime
	 * 
	 */
	public Integer getApprovalLeadTime() { 
		return approvalLeadTime;
	}

	/**
	 * Sets the approvalLeadTime attribute.
	 * 
	 * @param approvalLeadTime The approvalLeadTime to set.
	 * 
	 */
	public void setApprovalLeadTime(Integer approvalLeadTime) {
		this.approvalLeadTime = approvalLeadTime;
	}


	/**
	 * Gets the dueDateDescription attribute.
	 * 
	 * @return Returns the dueDateDescription
	 * 
	 */
	public String getDueDateDescription() { 
		return dueDateDescription;
	}

	/**
	 * Sets the dueDateDescription attribute.
	 * 
	 * @param dueDateDescription The dueDateDescription to set.
	 * 
	 */
	public void setDueDateDescription(String dueDateDescription) {
		this.dueDateDescription = dueDateDescription;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("dueDateTypeCode", this.dueDateTypeCode);
	    return m;
    }
}
