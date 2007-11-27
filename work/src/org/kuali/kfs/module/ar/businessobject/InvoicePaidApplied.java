package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class InvoicePaidApplied extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer paidAppliedItemNumber;
	private String financialDocumentReferenceInvoiceNumber;
	private Integer invoiceItemNumber;
	private Integer universityFiscalYear;
	private String universityFiscalPeriodCode;
	private KualiDecimal invoiceItemAppliedAmount;

    private InvoiceDetail invoiceItem;
	private AccountingPeriod universityFiscalPeriod;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    
	/**
	 * Default constructor.
	 */
	public InvoicePaidApplied() {

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
	 * Gets the paidAppliedItemNumber attribute.
	 * 
	 * @return Returns the paidAppliedItemNumber
	 * 
	 */
	public Integer getPaidAppliedItemNumber() { 
		return paidAppliedItemNumber;
	}

	/**
	 * Sets the paidAppliedItemNumber attribute.
	 * 
	 * @param paidAppliedItemNumber The paidAppliedItemNumber to set.
	 * 
	 */
	public void setPaidAppliedItemNumber(Integer paidAppliedItemNumber) {
		this.paidAppliedItemNumber = paidAppliedItemNumber;
	}


	/**
	 * Gets the financialDocumentReferenceInvoiceNumber attribute.
	 * 
	 * @return Returns the financialDocumentReferenceInvoiceNumber
	 * 
	 */
	public String getFinancialDocumentReferenceInvoiceNumber() { 
		return financialDocumentReferenceInvoiceNumber;
	}

	/**
	 * Sets the financialDocumentReferenceInvoiceNumber attribute.
	 * 
	 * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
	 * 
	 */
	public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
		this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
	}


	/**
	 * Gets the invoiceItemNumber attribute.
	 * 
	 * @return Returns the invoiceItemNumber
	 * 
	 */
	public Integer getInvoiceItemNumber() { 
		return invoiceItemNumber;
	}

	/**
	 * Sets the invoiceItemNumber attribute.
	 * 
	 * @param invoiceItemNumber The invoiceItemNumber to set.
	 * 
	 */
	public void setInvoiceItemNumber(Integer invoiceItemNumber) {
		this.invoiceItemNumber = invoiceItemNumber;
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
	 * Gets the invoiceItemAppliedAmount attribute.
	 * 
	 * @return Returns the invoiceItemAppliedAmount
	 * 
	 */
	public KualiDecimal getInvoiceItemAppliedAmount() { 
		return invoiceItemAppliedAmount;
	}

	/**
	 * Sets the invoiceItemAppliedAmount attribute.
	 * 
	 * @param invoiceItemAppliedAmount The invoiceItemAppliedAmount to set.
	 * 
	 */
	public void setInvoiceItemAppliedAmount(KualiDecimal invoiceItemAppliedAmount) {
		this.invoiceItemAppliedAmount = invoiceItemAppliedAmount;
	}


	/**
	 * Gets the invoiceItem attribute.
	 * 
	 * @return Returns the invoiceItem
	 * 
	 */
	public InvoiceDetail getInvoiceItem() { 
		return invoiceItem;
	}

	/**
	 * Sets the invoiceItem attribute.
	 * 
	 * @param invoiceItem The invoiceItem to set.
	 * @deprecated
	 */
	public void setInvoiceItem(InvoiceDetail invoiceItem) {
		this.invoiceItem = invoiceItem;
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
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.paidAppliedItemNumber != null) {
            m.put("paidAppliedItemNumber", this.paidAppliedItemNumber.toString());
        }
	    return m;
    }

}
