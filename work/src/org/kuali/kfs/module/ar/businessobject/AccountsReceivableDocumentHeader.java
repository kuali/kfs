package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AccountsReceivableDocumentHeader extends PersistableBusinessObjectBase {

	private String documentNumber;
	private String customerNumber;
	private String processingChartOfAccountCode;
	private String processingOrganizationCode;
	private Date entryDate;
	private String financialDocumentExplanationText;

    private CashControl cashControl;
	private Customer customer;
	private Chart processingChartOfAccount;
	private Org processingOrganization;
    private DocumentHeader documentHeader;
    
	/**
	 * Default constructor.
	 */
	public AccountsReceivableDocumentHeader() {

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
	 * Gets the processingChartOfAccountCode attribute.
	 * 
	 * @return Returns the processingChartOfAccountCode
	 * 
	 */
	public String getProcessingChartOfAccountCode() { 
		return processingChartOfAccountCode;
	}

	/**
	 * Sets the processingChartOfAccountCode attribute.
	 * 
	 * @param processingChartOfAccountCode The processingChartOfAccountCode to set.
	 * 
	 */
	public void setProcessingChartOfAccountCode(String processingChartOfAccountCode) {
		this.processingChartOfAccountCode = processingChartOfAccountCode;
	}


	/**
	 * Gets the processingOrganizationCode attribute.
	 * 
	 * @return Returns the processingOrganizationCode
	 * 
	 */
	public String getProcessingOrganizationCode() { 
		return processingOrganizationCode;
	}

	/**
	 * Sets the processingOrganizationCode attribute.
	 * 
	 * @param processingOrganizationCode The processingOrganizationCode to set.
	 * 
	 */
	public void setProcessingOrganizationCode(String processingOrganizationCode) {
		this.processingOrganizationCode = processingOrganizationCode;
	}


	/**
	 * Gets the entryDate attribute.
	 * 
	 * @return Returns the entryDate
	 * 
	 */
	public Date getEntryDate() { 
		return entryDate;
	}

	/**
	 * Sets the entryDate attribute.
	 * 
	 * @param entryDate The entryDate to set.
	 * 
	 */
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}


	/**
	 * Gets the financialDocumentExplanationText attribute.
	 * 
	 * @return Returns the financialDocumentExplanationText
	 * 
	 */
	public String getFinancialDocumentExplanationText() { 
		return financialDocumentExplanationText;
	}

	/**
	 * Sets the financialDocumentExplanationText attribute.
	 * 
	 * @param financialDocumentExplanationText The financialDocumentExplanationText to set.
	 * 
	 */
	public void setFinancialDocumentExplanationText(String financialDocumentExplanationText) {
		this.financialDocumentExplanationText = financialDocumentExplanationText;
	}


	/**
	 * Gets the cashControl attribute.
	 * 
	 * @return Returns the cashControl
	 * 
	 */
	public CashControl getCashControl() { 
		return cashControl;
	}

	/**
	 * Sets the cashControl attribute.
	 * 
	 * @param cashControl The cashControl to set.
	 * @deprecated
	 */
	public void setCashControl(CashControl cashControl) {
		this.cashControl = cashControl;
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
	 * Gets the processingChartOfAccount attribute.
	 * 
	 * @return Returns the processingChartOfAccount
	 * 
	 */
	public Chart getProcessingChartOfAccount() { 
		return processingChartOfAccount;
	}

	/**
	 * Sets the processingChartOfAccount attribute.
	 * 
	 * @param processingChartOfAccount The processingChartOfAccount to set.
	 * @deprecated
	 */
	public void setProcessingChartOfAccount(Chart processingChartOfAccount) {
		this.processingChartOfAccount = processingChartOfAccount;
	}

	/**
	 * Gets the processingOrganization attribute.
	 * 
	 * @return Returns the processingOrganization
	 * 
	 */
	public Org getProcessingOrganization() { 
		return processingOrganization;
	}

	/**
	 * Sets the processingOrganization attribute.
	 * 
	 * @param processingOrganization The processingOrganization to set.
	 * @deprecated
	 */
	public void setProcessingOrganization(Org processingOrganization) {
		this.processingOrganization = processingOrganization;
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
        m.put("documentNumber", this.documentNumber);
	    return m;
    }

}
