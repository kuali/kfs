package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonAppliedHolding extends PersistableBusinessObjectBase {

	private String referenceFinancialDocumentNumber;
	private KualiDecimal financialDocumentLineAmount;
	private String customerNumber;

    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
	private Customer customer;
    private DocumentHeader documentHeader;
    
	/**
	 * Default constructor.
	 */
	public NonAppliedHolding() {

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
	 * Gets the accountsReceivableDocumentHeader attribute.
	 * 
	 * @return Returns the accountsReceivableDocumentHeader
	 * 
	 */
	public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() { 
		return accountsReceivableDocumentHeader;
	}

	/**
	 * Sets the accountsReceivableDocumentHeader attribute.
	 * 
	 * @param accountsReceivableDocumentHeader The accountsReceivableDocumentHeader to set.
	 * @deprecated
	 */
	public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
		this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
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
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }    
    
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }

}
