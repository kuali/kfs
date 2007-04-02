package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchaseOrderView extends PersistableBusinessObjectBase {

	private Integer accountsPayablePurchasingDocumentLinkIdentifier;
	private Integer purchaseOrderIdentifier;
	private String purchaseOrderCurrentIndicator;
	private String documentNumber;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderView() {

	}

	/**
	 * Gets the accountsPayablePurchasingDocumentLinkIdentifier attribute.
	 *
	 * @return Returns the accountsPayablePurchasingDocumentLinkIdentifier
	 *
	 */
	public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
		return accountsPayablePurchasingDocumentLinkIdentifier;
	}

	/**
	 * Sets the accountsPayablePurchasingDocumentLinkIdentifier attribute.
	 *
	 * @param accountsPayablePurchasingDocumentLinkIdentifier The accountsPayablePurchasingDocumentLinkIdentifier to set.
	 *
	 */
	public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
		this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
	}


	/**
	 * Gets the purchaseOrderIdentifier attribute.
	 *
	 * @return Returns the purchaseOrderIdentifier
	 *
	 */
	public Integer getPurchaseOrderIdentifier() {
		return purchaseOrderIdentifier;
	}

	/**
	 * Sets the purchaseOrderIdentifier attribute.
	 *
	 * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
	 *
	 */
	public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
		this.purchaseOrderIdentifier = purchaseOrderIdentifier;
	}


	/**
	 * Gets the purchaseOrderCurrentIndicator attribute.
	 *
	 * @return Returns the purchaseOrderCurrentIndicator
	 *
	 */
	public String getPurchaseOrderCurrentIndicator() {
		return purchaseOrderCurrentIndicator;
	}

	/**
	 * Sets the purchaseOrderCurrentIndicator attribute.
	 *
	 * @param purchaseOrderCurrentIndicator The purchaseOrderCurrentIndicator to set.
	 *
	 */
	public void setPurchaseOrderCurrentIndicator(String purchaseOrderCurrentIndicator) {
		this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
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
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();
        if (this.accountsPayablePurchasingDocumentLinkIdentifier != null) {
            m.put("accountsPayablePurchasingDocumentLinkIdentifier", this.accountsPayablePurchasingDocumentLinkIdentifier.toString());
        }
        return m;
    }
}
