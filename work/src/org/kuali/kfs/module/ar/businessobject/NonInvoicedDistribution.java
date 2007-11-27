package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonInvoicedDistribution extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentLineNumber;
	private String referenceFinancialDocumentNumber;
	private KualiDecimal financialDocumentLineAmount;

    private NonAppliedHolding nonAppliedHolding;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    
	/**
	 * Default constructor.
	 */
	public NonInvoicedDistribution() {

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
	 * Gets the financialDocumentLineNumber attribute.
	 * 
	 * @return Returns the financialDocumentLineNumber
	 * 
	 */
	public Integer getFinancialDocumentLineNumber() { 
		return financialDocumentLineNumber;
	}

	/**
	 * Sets the financialDocumentLineNumber attribute.
	 * 
	 * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
	 * 
	 */
	public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
		this.financialDocumentLineNumber = financialDocumentLineNumber;
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
	 * Gets the nonAppliedHolding attribute.
	 * 
	 * @return Returns the nonAppliedHolding
	 * 
	 */
	public NonAppliedHolding getNonAppliedHolding() { 
		return nonAppliedHolding;
	}

	/**
	 * Sets the nonAppliedHolding attribute.
	 * 
	 * @param nonAppliedHolding The nonAppliedHolding to set.
	 * @deprecated
	 */
	public void setNonAppliedHolding(NonAppliedHolding nonAppliedHolding) {
		this.nonAppliedHolding = nonAppliedHolding;
	}

    /**
     * Gets the accountsReceivableDocumentHeader attribute. 
     * @return Returns the accountsReceivableDocumentHeader.
     */
    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    /**
     * Sets the accountsReceivableDocumentHeader attribute value.
     * @param accountsReceivableDocumentHeader The accountsReceivableDocumentHeader to set.
     * @deprecated
     */
    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }    
    
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }

}
