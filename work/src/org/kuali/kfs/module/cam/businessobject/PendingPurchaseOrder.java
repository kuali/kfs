package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PendingPurchaseOrder extends PersistableBusinessObjectBase {

	private String purchaseOrderNumber;
	private String requiredNumber;
	private String departmentRepresentative;
	private String deliverToName;
	private String deliverToAddress1;
	private String deliverToAddress2;
	private String requestName;
	private String requestAddress1;
	private String requestAddress2;
	private String requestorPersonEmailAddress;
	private String requestorPersonPhoneNumber;

	/**
	 * Default constructor.
	 */
	public PendingPurchaseOrder() {

	}

	/**
	 * Gets the purchaseOrderNumber attribute.
	 * 
	 * @return Returns the purchaseOrderNumber
	 * 
	 */
	public String getPurchaseOrderNumber() { 
		return purchaseOrderNumber;
	}

	/**
	 * Sets the purchaseOrderNumber attribute.
	 * 
	 * @param purchaseOrderNumber The purchaseOrderNumber to set.
	 * 
	 */
	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}


	/**
	 * Gets the requiredNumber attribute.
	 * 
	 * @return Returns the requiredNumber
	 * 
	 */
	public String getRequiredNumber() { 
		return requiredNumber;
	}

	/**
	 * Sets the requiredNumber attribute.
	 * 
	 * @param requiredNumber The requiredNumber to set.
	 * 
	 */
	public void setRequiredNumber(String requiredNumber) {
		this.requiredNumber = requiredNumber;
	}


	/**
	 * Gets the departmentRepresentative attribute.
	 * 
	 * @return Returns the departmentRepresentative
	 * 
	 */
	public String getDepartmentRepresentative() { 
		return departmentRepresentative;
	}

	/**
	 * Sets the departmentRepresentative attribute.
	 * 
	 * @param departmentRepresentative The departmentRepresentative to set.
	 * 
	 */
	public void setDepartmentRepresentative(String departmentRepresentative) {
		this.departmentRepresentative = departmentRepresentative;
	}


	/**
	 * Gets the deliverToName attribute.
	 * 
	 * @return Returns the deliverToName
	 * 
	 */
	public String getDeliverToName() { 
		return deliverToName;
	}

	/**
	 * Sets the deliverToName attribute.
	 * 
	 * @param deliverToName The deliverToName to set.
	 * 
	 */
	public void setDeliverToName(String deliverToName) {
		this.deliverToName = deliverToName;
	}


	/**
	 * Gets the deliverToAddress1 attribute.
	 * 
	 * @return Returns the deliverToAddress1
	 * 
	 */
	public String getDeliverToAddress1() { 
		return deliverToAddress1;
	}

	/**
	 * Sets the deliverToAddress1 attribute.
	 * 
	 * @param deliverToAddress1 The deliverToAddress1 to set.
	 * 
	 */
	public void setDeliverToAddress1(String deliverToAddress1) {
		this.deliverToAddress1 = deliverToAddress1;
	}


	/**
	 * Gets the deliverToAddress2 attribute.
	 * 
	 * @return Returns the deliverToAddress2
	 * 
	 */
	public String getDeliverToAddress2() { 
		return deliverToAddress2;
	}

	/**
	 * Sets the deliverToAddress2 attribute.
	 * 
	 * @param deliverToAddress2 The deliverToAddress2 to set.
	 * 
	 */
	public void setDeliverToAddress2(String deliverToAddress2) {
		this.deliverToAddress2 = deliverToAddress2;
	}


	/**
	 * Gets the requestName attribute.
	 * 
	 * @return Returns the requestName
	 * 
	 */
	public String getRequestName() { 
		return requestName;
	}

	/**
	 * Sets the requestName attribute.
	 * 
	 * @param requestName The requestName to set.
	 * 
	 */
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}


	/**
	 * Gets the requestAddress1 attribute.
	 * 
	 * @return Returns the requestAddress1
	 * 
	 */
	public String getRequestAddress1() { 
		return requestAddress1;
	}

	/**
	 * Sets the requestAddress1 attribute.
	 * 
	 * @param requestAddress1 The requestAddress1 to set.
	 * 
	 */
	public void setRequestAddress1(String requestAddress1) {
		this.requestAddress1 = requestAddress1;
	}


	/**
	 * Gets the requestAddress2 attribute.
	 * 
	 * @return Returns the requestAddress2
	 * 
	 */
	public String getRequestAddress2() { 
		return requestAddress2;
	}

	/**
	 * Sets the requestAddress2 attribute.
	 * 
	 * @param requestAddress2 The requestAddress2 to set.
	 * 
	 */
	public void setRequestAddress2(String requestAddress2) {
		this.requestAddress2 = requestAddress2;
	}


	/**
	 * Gets the requestorPersonEmailAddress attribute.
	 * 
	 * @return Returns the requestorPersonEmailAddress
	 * 
	 */
	public String getRequestorPersonEmailAddress() { 
		return requestorPersonEmailAddress;
	}

	/**
	 * Sets the requestorPersonEmailAddress attribute.
	 * 
	 * @param requestorPersonEmailAddress The requestorPersonEmailAddress to set.
	 * 
	 */
	public void setRequestorPersonEmailAddress(String requestorPersonEmailAddress) {
		this.requestorPersonEmailAddress = requestorPersonEmailAddress;
	}


	/**
	 * Gets the requestorPersonPhoneNumber attribute.
	 * 
	 * @return Returns the requestorPersonPhoneNumber
	 * 
	 */
	public String getRequestorPersonPhoneNumber() { 
		return requestorPersonPhoneNumber;
	}

	/**
	 * Sets the requestorPersonPhoneNumber attribute.
	 * 
	 * @param requestorPersonPhoneNumber The requestorPersonPhoneNumber to set.
	 * 
	 */
	public void setRequestorPersonPhoneNumber(String requestorPersonPhoneNumber) {
		this.requestorPersonPhoneNumber = requestorPersonPhoneNumber;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("purchaseOrderNumber", this.purchaseOrderNumber);
	    return m;
    }
}
