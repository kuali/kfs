package org.kuali.module.financial.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashieringItemInProcess extends PersistableBusinessObjectBase {

	private String workgroupName;
	private Integer itemIdentifier;
	private KualiDecimal itemAmount;
	private KualiDecimal itemReducedAmount;
	private KualiDecimal itemRemainingAmount;
    private KualiDecimal currentPayment;
	private Date itemOpenDate;
	private Date itemClosedDate;
	private String itemDescription;

	/**
	 * Default constructor.
	 */
	public CashieringItemInProcess() {}

	/**
	 * Gets the workgroupName attribute.
	 * 
	 * @return Returns the workgroupName
	 * 
	 */
	public String getWorkgroupName() { 
		return workgroupName;
	}

	/**
	 * Sets the workgroupName attribute.
	 * 
	 * @param workgroupName The workgroupName to set.
	 * 
	 */
	public void setWorkgroupName(String workgroupName) {
		this.workgroupName = workgroupName;
	}


	/**
	 * Gets the itemIdentifier attribute.
	 * 
	 * @return Returns the itemIdentifier
	 * 
	 */
	public Integer getItemIdentifier() { 
		return itemIdentifier;
	}

	/**
	 * Sets the itemIdentifier attribute.
	 * 
	 * @param itemIdentifier The itemIdentifier to set.
	 * 
	 */
	public void setItemIdentifier(Integer itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}


	/**
	 * Gets the itemAmount attribute.
	 * 
	 * @return Returns the itemAmount
	 * 
	 */
	public KualiDecimal getItemAmount() { 
		return itemAmount;
	}

	/**
	 * Sets the itemAmount attribute.
	 * 
	 * @param itemAmount The itemAmount to set.
	 * 
	 */
	public void setItemAmount(KualiDecimal itemAmount) {
		this.itemAmount = itemAmount;
	}


	/**
	 * Gets the itemReducedAmount attribute.
	 * 
	 * @return Returns the itemReducedAmount
	 * 
	 */
	public KualiDecimal getItemReducedAmount() { 
		return itemReducedAmount;
	}

	/**
	 * Sets the itemReducedAmount attribute.
	 * 
	 * @param itemReducedAmount The itemReducedAmount to set.
	 * 
	 */
	public void setItemReducedAmount(KualiDecimal itemReducedAmount) {
		this.itemReducedAmount = itemReducedAmount;
	}


	/**
	 * Gets the itemRemainingAmount attribute.
	 * 
	 * @return Returns the itemRemainingAmount
	 * 
	 */
	public KualiDecimal getItemRemainingAmount() { 
		return itemRemainingAmount;
	}

	/**
	 * Sets the itemRemainingAmount attribute.
	 * 
	 * @param itemRemainingAmount The itemRemainingAmount to set.
	 * 
	 */
	public void setItemRemainingAmount(KualiDecimal itemTotalAmount) {
		this.itemRemainingAmount = itemTotalAmount;
	}


	/**
	 * Gets the itemOpenDate attribute.
	 * 
	 * @return Returns the itemOpenDate
	 * 
	 */
	public Date getItemOpenDate() { 
		return itemOpenDate;
	}

	/**
	 * Sets the itemOpenDate attribute.
	 * 
	 * @param itemOpenDate The itemOpenDate to set.
	 * 
	 */
	public void setItemOpenDate(Date itemOpenDate) {
		this.itemOpenDate = itemOpenDate;
	}


	/**
	 * Gets the itemClosedDate attribute.
	 * 
	 * @return Returns the itemClosedDate
	 * 
	 */
	public Date getItemClosedDate() { 
		return itemClosedDate;
	}

	/**
	 * Sets the itemClosedDate attribute.
	 * 
	 * @param itemClosedDate The itemClosedDate to set.
	 * 
	 */
	public void setItemClosedDate(Date itemClosedDate) {
		this.itemClosedDate = itemClosedDate;
	}


	/**
	 * Gets the itemDescription attribute.
	 * 
	 * @return Returns the itemDescription
	 * 
	 */
	public String getItemDescription() { 
		return itemDescription;
	}

	/**
	 * Sets the itemDescription attribute.
	 * 
	 * @param itemDescription The itemDescription to set.
	 * 
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	/**
     * Gets the currentPayment attribute. 
     * @return Returns the currentPayment.
     */
    public KualiDecimal getCurrentPayment() {
        return currentPayment;
    }

    /**
     * Sets the currentPayment attribute value.
     * @param currentPayment The currentPayment to set.
     */
    public void setCurrentPayment(KualiDecimal currentPayment) {
        this.currentPayment = currentPayment;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("workgroupName", this.workgroupName);
        if (this.itemIdentifier != null) {
            m.put("itemIdentifier", this.itemIdentifier.toString());
        }
	    return m;
    }
    
    /**
     * 
     * This method determines if this cashiering item in process was likely filled in by someone
     * Since workgroupName is likely automatically populated, it doesn't count
     * @return if this item in process is populated 
     */
    public boolean isPopulated() {
        return (this.itemOpenDate != null && itemAmount != null && !itemAmount.equals(KualiDecimal.ZERO));
    }
}
