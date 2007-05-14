package org.kuali.module.purap.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchaseOrderQuoteLanguage extends PersistableBusinessObjectBase {

	private Integer purchaseOrderQuoteLanguageIdentifier;
	private String purchaseOrderQuoteLanguageDescription;
	private Date purchaseOrderQuoteLanguageCreateDate;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderQuoteLanguage() {

	}

	/**
	 * Gets the purchaseOrderQuoteLanguageIdentifier attribute.
	 * 
	 * @return Returns the purchaseOrderQuoteLanguageIdentifier
	 * 
	 */
	public Integer getPurchaseOrderQuoteLanguageIdentifier() { 
		return purchaseOrderQuoteLanguageIdentifier;
	}

	/**
	 * Sets the purchaseOrderQuoteLanguageIdentifier attribute.
	 * 
	 * @param purchaseOrderQuoteLanguageIdentifier The purchaseOrderQuoteLanguageIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderQuoteLanguageIdentifier(Integer purchaseOrderQuoteLanguageIdentifier) {
		this.purchaseOrderQuoteLanguageIdentifier = purchaseOrderQuoteLanguageIdentifier;
	}


	/**
	 * Gets the purchaseOrderQuoteLanguageDescription attribute.
	 * 
	 * @return Returns the purchaseOrderQuoteLanguageDescription
	 * 
	 */
	public String getPurchaseOrderQuoteLanguageDescription() { 
		return purchaseOrderQuoteLanguageDescription;
	}

	/**
	 * Sets the purchaseOrderQuoteLanguageDescription attribute.
	 * 
	 * @param purchaseOrderQuoteLanguageDescription The purchaseOrderQuoteLanguageDescription to set.
	 * 
	 */
	public void setPurchaseOrderQuoteLanguageDescription(String purchaseOrderQuoteLanguageDescription) {
		this.purchaseOrderQuoteLanguageDescription = purchaseOrderQuoteLanguageDescription;
	}


	/**
	 * Gets the purchaseOrderQuoteLanguageCreateDate attribute.
	 * 
	 * @return Returns the purchaseOrderQuoteLanguageCreateDate
	 * 
	 */
	public Date getPurchaseOrderQuoteLanguageCreateDate() { 
		return purchaseOrderQuoteLanguageCreateDate;
	}

	/**
	 * Sets the purchaseOrderQuoteLanguageCreateDate attribute.
	 * 
	 * @param purchaseOrderQuoteLanguageCreateDate The purchaseOrderQuoteLanguageCreateDate to set.
	 * 
	 */
	public void setPurchaseOrderQuoteLanguageCreateDate(Date purchaseOrderQuoteLanguageCreateDate) {
		this.purchaseOrderQuoteLanguageCreateDate = purchaseOrderQuoteLanguageCreateDate;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.purchaseOrderQuoteLanguageIdentifier != null) {
            m.put("purchaseOrderQuoteLanguageIdentifier", this.purchaseOrderQuoteLanguageIdentifier.toString());
        }
	    return m;
    }
}
