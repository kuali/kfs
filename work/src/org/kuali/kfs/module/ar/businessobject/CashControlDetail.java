package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.ar.document.PaymentApplicationDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashControlDetail extends PersistableBusinessObjectBase {

	private String documentNumber;
	private String referenceFinancialDocumentNumber;
	private String customerPaymentMediumIdentifier;
	private KualiDecimal financialDocumentLineAmount;
	private String customerPaymentDescription;
	private String customerNumber;
	private Date customerPaymentDate;

    private PaymentApplicationDocument referenceFinancialDocument;
	private Customer customer;
    private NonAppliedHolding nonAppliedHolding;
    
	/**
	 * Default constructor.
	 */
	public CashControlDetail() {

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
	 * Gets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @return Returns the referenceFinancialDocumentNumber
	 * 
	 */
	public String getReferenceFinancialDocumentNumber() { 
		return referenceFinancialDocumentNumber;
	}

	/**
	 * Sets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
	 * 
	 */
	public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
		this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
	}


	/**
	 * Gets the customerPaymentMediumIdentifier attribute.
	 * 
	 * @return Returns the customerPaymentMediumIdentifier
	 * 
	 */
	public String getCustomerPaymentMediumIdentifier() { 
		return customerPaymentMediumIdentifier;
	}

	/**
	 * Sets the customerPaymentMediumIdentifier attribute.
	 * 
	 * @param customerPaymentMediumIdentifier The customerPaymentMediumIdentifier to set.
	 * 
	 */
	public void setCustomerPaymentMediumIdentifier(String customerPaymentMediumIdentifier) {
		this.customerPaymentMediumIdentifier = customerPaymentMediumIdentifier;
	}


	/**
	 * Gets the financialDocumentLineAmount attribute.
	 * 
	 * @return Returns the financialDocumentLineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentLineAmount() { 
		return financialDocumentLineAmount;
	}

	/**
	 * Sets the financialDocumentLineAmount attribute.
	 * 
	 * @param financialDocumentLineAmount The financialDocumentLineAmount to set.
	 * 
	 */
	public void setFinancialDocumentLineAmount(KualiDecimal financialDocumentLineAmount) {
		this.financialDocumentLineAmount = financialDocumentLineAmount;
	}


	/**
	 * Gets the customerPaymentDescription attribute.
	 * 
	 * @return Returns the customerPaymentDescription
	 * 
	 */
	public String getCustomerPaymentDescription() { 
		return customerPaymentDescription;
	}

	/**
	 * Sets the customerPaymentDescription attribute.
	 * 
	 * @param customerPaymentDescription The customerPaymentDescription to set.
	 * 
	 */
	public void setCustomerPaymentDescription(String customerPaymentDescription) {
		this.customerPaymentDescription = customerPaymentDescription;
	}


	/**
	 * Gets the customerNumber attribute.
	 * 
	 * @return Returns the customerNumber
	 * 
	 */
	public String getCustomerNumber() { 
		return customerNumber;
	}

	/**
	 * Sets the customerNumber attribute.
	 * 
	 * @param customerNumber The customerNumber to set.
	 * 
	 */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}


	/**
	 * Gets the customerPaymentDate attribute.
	 * 
	 * @return Returns the customerPaymentDate
	 * 
	 */
	public Date getCustomerPaymentDate() { 
		return customerPaymentDate;
	}

	/**
	 * Sets the customerPaymentDate attribute.
	 * 
	 * @param customerPaymentDate The customerPaymentDate to set.
	 * 
	 */
	public void setCustomerPaymentDate(Date customerPaymentDate) {
		this.customerPaymentDate = customerPaymentDate;
	}


	/**
	 * Gets the referenceFinancialDocument attribute.
	 * 
	 * @return Returns the referenceFinancialDocument
	 * 
	 */
	public PaymentApplicationDocument getReferenceFinancialDocument() { 
		return referenceFinancialDocument;
	}

	/**
	 * Sets the referenceFinancialDocument attribute.
	 * 
	 * @param referenceFinancialDocument The referenceFinancialDocument to set.
	 * @deprecated
	 */
	public void setReferenceFinancialDocument(PaymentApplicationDocument referenceFinancialDocument) {
		this.referenceFinancialDocument = referenceFinancialDocument;
	}

	/**
	 * Gets the customer attribute.
	 * 
	 * @return Returns the customer
	 * 
	 */
	public Customer getCustomer() { 
		return customer;
	}

	/**
	 * Sets the customer attribute.
	 * 
	 * @param customer The customer to set.
	 * @deprecated
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
     * Gets the nonAppliedHolding attribute. 
     * @return Returns the nonAppliedHolding.
     */
    public NonAppliedHolding getNonAppliedHolding() {
        return nonAppliedHolding;
    }

    /**
     * Sets the nonAppliedHolding attribute value.
     * @param nonAppliedHolding The nonAppliedHolding to set.
     * @deprecated
     */
    public void setNonAppliedHolding(NonAppliedHolding nonAppliedHolding) {
        this.nonAppliedHolding = nonAppliedHolding;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }
}
