package org.kuali.module.ar.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.bo.PaymentMedium;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashControlDocument extends TransactionalDocumentBase implements AmountTotaling {

	private String referenceFinancialDocumentNumber;
	private Integer universityFiscalYear;
	private String universityFiscalPeriodCode;
	private String customerPaymentMediumCode;
	private KualiDecimal cashControlTotalAmount = KualiDecimal.ZERO;

    private PaymentMedium customerPaymentMedium;
	private AccountingPeriod universityFiscalPeriod;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;

    private List<CashControlDetail> cashControlDetails;
    
    private boolean hasNegativeCashControlDetail = false;
    
	public boolean isHasNegativeCashControlDetail() {
        return hasNegativeCashControlDetail;
    }

    public void setHasNegativeCashControlDetail(boolean hasNegativeCashControlDetail) {
        this.hasNegativeCashControlDetail = hasNegativeCashControlDetail;
    }

    /**
	 * Default constructor.
	 */
	public CashControlDocument() {
        super();
        accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        customerPaymentMedium = new PaymentMedium();
        universityFiscalPeriod = new AccountingPeriod();
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
     * This method adds a new cash control detail to the list
     * @param cashControlDetail
     */
    public void addCashControlDetail(CashControlDetail cashControlDetail) {
        prepareCashControlDetail(cashControlDetail);
        this.cashControlTotalAmount = this.cashControlTotalAmount.add(cashControlDetail.getFinancialDocumentLineAmount());
        cashControlDetails.add(cashControlDetail);
    }
    
    /**
     * This method removes a cash control detail from the list
     * @param index
     */
    public void deleteCashControlDetail(int index) {
       cashControlDetails.remove(index);
    }
    
    /**
     * This is a helper method that automatically populates document specfic information into the
     *  cash control detail deposit (CashControlDetail) instance.
     */
    private void prepareCashControlDetail(CashControlDetail cashControlDetail) {
        cashControlDetail.setDocumentNumber(this.getDocumentNumber());
    }
  
	/**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
	    return m;
    }

    /**
     * Gets the customerPaymentMedium attribute.
     * @return Returns the customerPaymentMedium
     */
    public PaymentMedium getCustomerPaymentMedium() {
        return customerPaymentMedium;
    }

    /**
     * Sets the customerPaymentMedium attribute value.
     * @param customerPaymentMedium The customerPaymentMedium to set.
     */
    public void setCustomerPaymentMedium(PaymentMedium customerPaymentMedium) {
        this.customerPaymentMedium = customerPaymentMedium;
    }

    /**
     * @see org.kuali.core.document.AmountTotaling#getTotalDollarAmount()
     */
    public KualiDecimal getTotalDollarAmount() {
        return cashControlTotalAmount;
    }
    
    /**
     * This method returns the advance deposit total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCashControlAmount() {
        return (String) new CurrencyFormatter().format(cashControlTotalAmount);
    }
    
    /**
     * Retrieves a specific cash control detail from the list, by array index
     * 
     * @param index the index of the cash control details to retrieve the cash control detail from
     * @return a CashControlDetail
     */
    public CashControlDetail getCashControlDetail(int index) {
        if (index >= cashControlDetails.size()) {
            for (int i = cashControlDetails.size(); i <= index; i++) {
                cashControlDetails.add(new CashControlDetail());
            }
        }
        return cashControlDetails.get(index);
    }   

}
