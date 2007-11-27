package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OpenCreditDetail extends PersistableBusinessObjectBase {

	private String documentNumber;
//    private String financialDocumentTypeCode;
    private Integer invoiceItemNumber;
	private String chartOfAccountsCode;
	private String accountNumber;
    private String billByChartOfAccountCode;
    private String billedByOrganizationCode;
//    private Date billingDate;    
    private Date entryDate;    
//    private Date invoiceDueDate;
    private String invoiceDescription;    
    private boolean writeoffIndicator;    
    private String customerNumber;
    private String processingChartOfAccountCode;
    private String processingOrganizationCode;    
    private String customerName;
    private KualiDecimal invoiceItemTotalAmount;    
    private KualiDecimal invoiceItemAppliedAmount;
 
    private Chart chartOfAccounts;
    private Account account;
    private Chart billByChartOfAccount;
    private Chart processingChartOfAccount;
    private Org billedByOrganization;
    private Org processingOrganization;
    private Customer customer;
    private InvoiceDetail invoiceItem;
    
    
    /**
	 * Default constructor.
	 */
	public OpenCreditDetail() {

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
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * Gets the invoiceItemTotalAmount attribute.
	 * 
	 * @return Returns the invoiceItemTotalAmount
	 * 
	 */
	public KualiDecimal getInvoiceItemTotalAmount() { 
		return invoiceItemTotalAmount;
	}

	/**
	 * Sets the invoiceItemTotalAmount attribute.
	 * 
	 * @param invoiceItemTotalAmount The invoiceItemTotalAmount to set.
	 * 
	 */
	public void setInvoiceItemTotalAmount(KualiDecimal invoiceItemTotalAmount) {
		this.invoiceItemTotalAmount = invoiceItemTotalAmount;
	}

    /**
     * Gets the billByChartOfAccountCode attribute. 
     * @return Returns the billByChartOfAccountCode.
     */
    public String getBillByChartOfAccountCode() {
        return billByChartOfAccountCode;
    }

    /**
     * Sets the billByChartOfAccountCode attribute value.
     * @param billByChartOfAccountCode The billByChartOfAccountCode to set.
     */
    public void setBillByChartOfAccountCode(String billByChartOfAccountCode) {
        this.billByChartOfAccountCode = billByChartOfAccountCode;
    }

    /**
     * Gets the billedByOrganizationCode attribute. 
     * @return Returns the billedByOrganizationCode.
     */
    public String getBilledByOrganizationCode() {
        return billedByOrganizationCode;
    }

    /**
     * Sets the billedByOrganizationCode attribute value.
     * @param billedByOrganizationCode The billedByOrganizationCode to set.
     */
    public void setBilledByOrganizationCode(String billedByOrganizationCode) {
        this.billedByOrganizationCode = billedByOrganizationCode;
    }

    /**
     * Gets the billingDate attribute. 
     * @return Returns the billingDate.
     */
//    public Date getBillingDate() {
//        return billingDate;
//    }

    /**
     * Sets the billingDate attribute value.
     * @param billingDate The billingDate to set.
     */
//    public void setBillingDate(Date billingDate) {
//        this.billingDate = billingDate;
//    }

    /**
     * Gets the customerName attribute. 
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute value.
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the customerNumber attribute. 
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the entryDate attribute. 
     * @return Returns the entryDate.
     */
    public Date getEntryDate() {
        return entryDate;
    }

    /**
     * Sets the entryDate attribute value.
     * @param entryDate The entryDate to set.
     */
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    /**
     * Gets the financialDocumentTypeCode attribute. 
     * @return Returns the financialDocumentTypeCode.
     */
//    public String getFinancialDocumentTypeCode() {
//        return financialDocumentTypeCode;
//    }

    /**
     * Sets the financialDocumentTypeCode attribute value.
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
//    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
//        this.financialDocumentTypeCode = financialDocumentTypeCode;
//    }

    /**
     * Gets the invoiceDescription attribute. 
     * @return Returns the invoiceDescription.
     */
    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    /**
     * Sets the invoiceDescription attribute value.
     * @param invoiceDescription The invoiceDescription to set.
     */
    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    /**
     * Gets the invoiceDueDate attribute. 
     * @return Returns the invoiceDueDate.
     */
//    public Date getInvoiceDueDate() {
//        return invoiceDueDate;
//    }

    /**
     * Sets the invoiceDueDate attribute value.
     * @param invoiceDueDate The invoiceDueDate to set.
     */
//    public void setInvoiceDueDate(Date invoiceDueDate) {
//        this.invoiceDueDate = invoiceDueDate;
//    }

    /**
     * Gets the invoiceItemAppliedAmount attribute. 
     * @return Returns the invoiceItemAppliedAmount.
     */
    public KualiDecimal getInvoiceItemAppliedAmount() {
        return invoiceItemAppliedAmount;
    }

    /**
     * Sets the invoiceItemAppliedAmount attribute value.
     * @param invoiceItemAppliedAmount The invoiceItemAppliedAmount to set.
     */
    public void setInvoiceItemAppliedAmount(KualiDecimal invoiceItemAppliedAmount) {
        this.invoiceItemAppliedAmount = invoiceItemAppliedAmount;
    }

    /**
     * Gets the processingChartOfAccountCode attribute. 
     * @return Returns the processingChartOfAccountCode.
     */
    public String getProcessingChartOfAccountCode() {
        return processingChartOfAccountCode;
    }

    /**
     * Sets the processingChartOfAccountCode attribute value.
     * @param processingChartOfAccountCode The processingChartOfAccountCode to set.
     */
    public void setProcessingChartOfAccountCode(String processingChartOfAccountCode) {
        this.processingChartOfAccountCode = processingChartOfAccountCode;
    }

    /**
     * Gets the processingOrganizationCode attribute. 
     * @return Returns the processingOrganizationCode.
     */
    public String getProcessingOrganizationCode() {
        return processingOrganizationCode;
    }

    /**
     * Sets the processingOrganizationCode attribute value.
     * @param processingOrganizationCode The processingOrganizationCode to set.
     */
    public void setProcessingOrganizationCode(String processingOrganizationCode) {
        this.processingOrganizationCode = processingOrganizationCode;
    }

    /**
     * Gets the writeoffIndicator attribute. 
     * @return Returns the writeoffIndicator.
     */
    public boolean isWriteoffIndicator() {
        return writeoffIndicator;
    }

    /**
     * Sets the writeoffIndicator attribute value.
     * @param writeoffIndicator The writeoffIndicator to set.
     */
    public void setWriteoffIndicator(boolean writeoffIndicator) {
        this.writeoffIndicator = writeoffIndicator;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() { 
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     * 
     */
    public Account getAccount() { 
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }    
 
    /**
     * Gets the billByChartOfAccount attribute. 
     * @return Returns the billByChartOfAccount.
     */
    public Chart getBillByChartOfAccount() {
        return billByChartOfAccount;
    }

    /**
     * Sets the billByChartOfAccount attribute value.
     * @param billByChartOfAccount The billByChartOfAccount to set.
     * @deprecated
     */
    public void setBillByChartOfAccount(Chart billByChartOfAccount) {
        this.billByChartOfAccount = billByChartOfAccount;
    }

    /**
     * Gets the billedByOrganization attribute. 
     * @return Returns the billedByOrganization.
     */
    public Org getBilledByOrganization() {
        return billedByOrganization;
    }

    /**
     * Sets the billedByOrganization attribute value.
     * @param billedByOrganization The billedByOrganization to set.
     * @deprecated
     */
    public void setBilledByOrganization(Org billedByOrganization) {
        this.billedByOrganization = billedByOrganization;
    }

    /**
     * Gets the customer attribute. 
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     * @param customer The customer to set.
     * @deprecated
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the invoiceItem attribute. 
     * @return Returns the invoiceItem.
     */
    public InvoiceDetail getInvoiceItem() {
        return invoiceItem;
    }

    /**
     * Sets the invoiceItem attribute value.
     * @param invoiceItem The invoiceItem to set.
     * @deprecated
     */
    public void setInvoiceItem(InvoiceDetail invoiceItem) {
        this.invoiceItem = invoiceItem;
    }

    /**
     * Gets the processingChartOfAccount attribute. 
     * @return Returns the processingChartOfAccount.
     */
    public Chart getProcessingChartOfAccount() {
        return processingChartOfAccount;
    }

    /**
     * Sets the processingChartOfAccount attribute value.
     * @param processingChartOfAccount The processingChartOfAccount to set.
     * @deprecated
     */
    public void setProcessingChartOfAccount(Chart processingChartOfAccount) {
        this.processingChartOfAccount = processingChartOfAccount;
    }

    /**
     * Gets the processingOrganization attribute. 
     * @return Returns the processingOrganization.
     */
    public Org getProcessingOrganization() {
        return processingOrganization;
    }

    /**
     * Sets the processingOrganization attribute value.
     * @param processingOrganization The processingOrganization to set.
     * @deprecated
     */
    public void setProcessingOrganization(Org processingOrganization) {
        this.processingOrganization = processingOrganization;
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
