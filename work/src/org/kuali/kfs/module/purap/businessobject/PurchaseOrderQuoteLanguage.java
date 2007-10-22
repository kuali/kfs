package org.kuali.module.purap.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Quote Language Business Object.
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

	public Integer getPurchaseOrderQuoteLanguageIdentifier() { 
		return purchaseOrderQuoteLanguageIdentifier;
	}

	public void setPurchaseOrderQuoteLanguageIdentifier(Integer purchaseOrderQuoteLanguageIdentifier) {
		this.purchaseOrderQuoteLanguageIdentifier = purchaseOrderQuoteLanguageIdentifier;
	}

	public String getPurchaseOrderQuoteLanguageDescription() { 
		return purchaseOrderQuoteLanguageDescription;
	}

	public void setPurchaseOrderQuoteLanguageDescription(String purchaseOrderQuoteLanguageDescription) {
		this.purchaseOrderQuoteLanguageDescription = purchaseOrderQuoteLanguageDescription;
	}

	public Date getPurchaseOrderQuoteLanguageCreateDate() { 
		return purchaseOrderQuoteLanguageCreateDate;
	}

	public void setPurchaseOrderQuoteLanguageCreateDate(Date purchaseOrderQuoteLanguageCreateDate) {
		this.purchaseOrderQuoteLanguageCreateDate = purchaseOrderQuoteLanguageCreateDate;
	}

	public boolean isActive() { 
		return active;
	}

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
