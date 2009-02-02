package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonAppliedHolding extends PersistableBusinessObjectBase {

	private String referenceFinancialDocumentNumber;
	private KualiDecimal financialDocumentLineAmount = KualiDecimal.ZERO;
	private String customerNumber;

	private Customer customer;
    // MAYBE TAKE THIS OUT
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;

    private List<NonAppliedDistribution> nonAppliedDistributions;
    
	/**
	 * Default constructor.
	 */
	public NonAppliedHolding() {
        nonAppliedDistributions = new ArrayList<NonAppliedDistribution>();
        
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
     * Gets the nonAppliedDistributions attribute. 
     * @return Returns the nonAppliedDistributions.
     */
    public List<NonAppliedDistribution> getNonAppliedDistributions() {
        return nonAppliedDistributions;
    }

    /**
     * Sets the nonAppliedDistributions attribute value.
     * @param nonAppliedDistributions The nonAppliedDistributions to set.
     */
    public void setNonAppliedDistributions(List<NonAppliedDistribution> nonAppliedDistributions) {
        this.nonAppliedDistributions = nonAppliedDistributions;
    }

    /**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }

}
