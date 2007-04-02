package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PaymentRequestView extends PersistableBusinessObjectBase {

	private Integer accountsPayablePurchasingDocumentLinkIdentifier;
	private Integer paymentRequestIdentifier;
	private String documentNumber;

	/**
	 * Default constructor.
	 */
	public PaymentRequestView() {

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
	 * Gets the paymentRequestIdentifier attribute.
	 *
	 * @return Returns the paymentRequestIdentifier
	 *
	 */
	public Integer getPaymentRequestIdentifier() {
		return paymentRequestIdentifier;
	}

	/**
	 * Sets the paymentRequestIdentifier attribute.
	 *
	 * @param paymentRequestIdentifier The paymentRequestIdentifier to set.
	 *
	 */
	public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
		this.paymentRequestIdentifier = paymentRequestIdentifier;
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
