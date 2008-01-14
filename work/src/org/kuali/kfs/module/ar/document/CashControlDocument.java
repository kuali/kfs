package org.kuali.module.ar.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashControlDocument extends TransactionalDocumentBase {

	private String documentNumber;
	private String referenceFinancialDocumentNumber;
	private Integer universityFiscalYear;
	private String universityFiscalPeriodCode;
	private String customerPaymentMediumCode;
	private KualiDecimal cashControlTotalAmount;

	private AccountingPeriod universityFiscalPeriod;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;

    private List<CashControlDetail> cashControlDetails;
    
	/**
	 * Default constructor.
	 */
	public CashControlDocument() {
        cashControlDetails = new ArrayList<CashControlDetail>();
        
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
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the universityFiscalPeriodCode attribute.
	 * 
	 * @return Returns the universityFiscalPeriodCode
	 * 
	 */
	public String getUniversityFiscalPeriodCode() { 
		return universityFiscalPeriodCode;
	}

	/**
	 * Sets the universityFiscalPeriodCode attribute.
	 * 
	 * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
	 * 
	 */
	public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
		this.universityFiscalPeriodCode = universityFiscalPeriodCode;
	}


	/**
	 * Gets the customerPaymentMediumCode attribute.
	 * 
	 * @return Returns the customerPaymentMediumCode
	 * 
	 */
	public String getCustomerPaymentMediumCode() { 
		return customerPaymentMediumCode;
	}

	/**
	 * Sets the customerPaymentMediumCode attribute.
	 * 
	 * @param customerPaymentMediumCode The customerPaymentMediumCode to set.
	 * 
	 */
	public void setCustomerPaymentMediumCode(String customerPaymentMediumCode) {
		this.customerPaymentMediumCode = customerPaymentMediumCode;
	}


	/**
	 * Gets the cashControlTotalAmount attribute.
	 * 
	 * @return Returns the cashControlTotalAmount
	 * 
	 */
	public KualiDecimal getCashControlTotalAmount() { 
		return cashControlTotalAmount;
	}

	/**
	 * Sets the cashControlTotalAmount attribute.
	 * 
	 * @param cashControlTotalAmount The cashControlTotalAmount to set.
	 * 
	 */
	public void setCashControlTotalAmount(KualiDecimal cashControlTotalAmount) {
		this.cashControlTotalAmount = cashControlTotalAmount;
	}

	/**
	 * Gets the universityFiscalPeriod attribute.
	 * 
	 * @return Returns the universityFiscalPeriod
	 * 
	 */
	public AccountingPeriod getUniversityFiscalPeriod() { 
		return universityFiscalPeriod;
	}

	/**
	 * Sets the universityFiscalPeriod attribute.
	 * 
	 * @param universityFiscalPeriod The universityFiscalPeriod to set.
	 * @deprecated
	 */
	public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
		this.universityFiscalPeriod = universityFiscalPeriod;
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
     * Gets the cashControlDetails attribute. 
     * @return Returns the cashControlDetails.
     */
    public List<CashControlDetail> getCashControlDetails() {
        return cashControlDetails;
    }

    /**
     * Sets the cashControlDetails attribute value.
     * @param cashControlDetails The cashControlDetails to set.
     */
    public void setCashControlDetails(List<CashControlDetail> cashControlDetails) {
        this.cashControlDetails = cashControlDetails;
    }
  
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
	    return m;
    }

}
