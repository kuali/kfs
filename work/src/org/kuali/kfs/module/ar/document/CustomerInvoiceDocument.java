package org.kuali.module.ar.document;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CustomerAddress;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.CustomerProcessingType;
import org.kuali.module.ar.bo.ReceivableCustomerInvoiceDetail;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;
import org.kuali.module.ar.service.InvoicePaidAppliedService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerInvoiceDocument extends AccountingDocumentBase implements Copyable, Correctable {

    protected Integer nextInvoiceItemNumber;
	private String invoiceHeaderText;
	private String invoiceAttentionLineText;
	private Date invoiceDueDate;
	private Date billingDate;
	private Date billingDateForDisplay;
	private String invoiceTermsText;
	private String organizationInvoiceNumber;
	private boolean writeoffIndicator;
	private String customerPurchaseOrderNumber;
	private String printInvoiceIndicator;
	private KualiDecimal invoiceWriteoffAmount;
	private String invoiceDescription;
	private Date customerPurchaseOrderDate;
	private String billByChartOfAccountCode;
	private String billedByOrganizationCode;
	private Integer customerShipToAddressIdentifier;
	private Integer customerBillToAddressIdentifier;
	private String customerSpecialProcessingCode;
	private boolean customerRecordAttachmentIndicator;
	private boolean openInvoiceIndicator;
    private String paymentChartOfAccountsCode;
    private String paymentAccountNumber;
    private String paymentSubAccountNumber;
    private String paymentFinancialObjectCode;
    private String paymentFinancialSubObjectCode;
    private String paymentProjectCode;
    private String paymentOrganizationReferenceIdentifier;
    private Date printDate;
    private KualiDecimal stateTaxPercent;
    private KualiDecimal localTaxPercent;
    
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
	private Chart billByChartOfAccount;
	private Org billedByOrganization;
	private CustomerProcessingType customerSpecialProcessing;
    private Account paymentAccount;
    private Chart paymentChartOfAccounts;
    private SubAccount paymentSubAccount;
    private ObjectCode paymentFinancialObject;
    private SubObjCd paymentFinancialSubObject;
    private ProjectCode paymentProject;   
    
    private CustomerAddress customerShipToAddress;
    private CustomerAddress customerBillToAddress;
    
    
	/**
	 * Default constructor.
	 */
	public CustomerInvoiceDocument() {
	    super();
	    this.nextInvoiceItemNumber = new Integer(1);
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
	 * Gets the invoiceHeaderText attribute.
	 * 
	 * @return Returns the invoiceHeaderText
	 * 
	 */
	public String getInvoiceHeaderText() { 
		return invoiceHeaderText;
	}

	/**
	 * Sets the invoiceHeaderText attribute.
	 * 
	 * @param invoiceHeaderText The invoiceHeaderText to set.
	 * 
	 */
	public void setInvoiceHeaderText(String invoiceHeaderText) {
		this.invoiceHeaderText = invoiceHeaderText;
	}


	/**
	 * Gets the invoiceAttentionLineText attribute.
	 * 
	 * @return Returns the invoiceAttentionLineText
	 * 
	 */
	public String getInvoiceAttentionLineText() { 
		return invoiceAttentionLineText;
	}

	/**
	 * Sets the invoiceAttentionLineText attribute.
	 * 
	 * @param invoiceAttentionLineText The invoiceAttentionLineText to set.
	 * 
	 */
	public void setInvoiceAttentionLineText(String invoiceAttentionLineText) {
		this.invoiceAttentionLineText = invoiceAttentionLineText;
	}


	/**
	 * Gets the invoiceDueDate attribute.
	 * 
	 * @return Returns the invoiceDueDate
	 * 
	 */
	public Date getInvoiceDueDate() { 
		return invoiceDueDate;
	}

	/**
	 * Sets the invoiceDueDate attribute.
	 * 
	 * @param invoiceDueDate The invoiceDueDate to set.
	 * 
	 */
	public void setInvoiceDueDate(Date invoiceDueDate) {
		this.invoiceDueDate = invoiceDueDate;
	}


	/**
	 * Gets the billingDate attribute.
	 * 
	 * @return Returns the billingDate
	 * 
	 */
	public Date getBillingDate() { 
		return billingDate;
	}

	/**
	 * Sets the billingDate attribute.
	 * 
	 * @param billingDate The billingDate to set.
	 * 
	 */
	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}


	/**
	 * Gets the invoiceTermsText attribute.
	 * 
	 * @return Returns the invoiceTermsText
	 * 
	 */
	public String getInvoiceTermsText() { 
		return invoiceTermsText;
	}

	/**
	 * Sets the invoiceTermsText attribute.
	 * 
	 * @param invoiceTermsText The invoiceTermsText to set.
	 * 
	 */
	public void setInvoiceTermsText(String invoiceTermsText) {
		this.invoiceTermsText = invoiceTermsText;
	}


	/**
	 * Gets the organizationInvoiceNumber attribute.
	 * 
	 * @return Returns the organizationInvoiceNumber
	 * 
	 */
	public String getOrganizationInvoiceNumber() { 
		return organizationInvoiceNumber;
	}

	/**
	 * Sets the organizationInvoiceNumber attribute.
	 * 
	 * @param organizationInvoiceNumber The organizationInvoiceNumber to set.
	 * 
	 */
	public void setOrganizationInvoiceNumber(String organizationInvoiceNumber) {
		this.organizationInvoiceNumber = organizationInvoiceNumber;
	}


	/**
	 * Gets the writeoffIndicator attribute.
	 * 
	 * @return Returns the writeoffIndicator
	 * 
	 */
	public boolean isWriteoffIndicator() { 
		return writeoffIndicator;
	}

	/**
	 * Sets the writeoffIndicator attribute.
	 * 
	 * @param writeoffIndicator The writeoffIndicator to set.
	 * 
	 */
	public void setWriteoffIndicator(boolean writeoffIndicator) {
		this.writeoffIndicator = writeoffIndicator;
	}


	/**
	 * Gets the customerPurchaseOrderNumber attribute.
	 * 
	 * @return Returns the customerPurchaseOrderNumber
	 * 
	 */
	public String getCustomerPurchaseOrderNumber() { 
		return customerPurchaseOrderNumber;
	}

	/**
	 * Sets the customerPurchaseOrderNumber attribute.
	 * 
	 * @param customerPurchaseOrderNumber The customerPurchaseOrderNumber to set.
	 * 
	 */
	public void setCustomerPurchaseOrderNumber(String customerPurchaseOrderNumber) {
		this.customerPurchaseOrderNumber = customerPurchaseOrderNumber;
	}

	/**
     * Gets the printInvoiceIndicator attribute. 
     * @return Returns the printInvoiceIndicator.
     */
    public String getPrintInvoiceIndicator() {
        return printInvoiceIndicator;
    }

    /**
     * Sets the printInvoiceIndicator attribute value.
     * @param printInvoiceIndicator The printInvoiceIndicator to set.
     */
    public void setPrintInvoiceIndicator(String printInvoiceIndicator) {
        this.printInvoiceIndicator = printInvoiceIndicator;
    }

    /**
	 * Gets the invoiceWriteoffAmount attribute.
	 * 
	 * @return Returns the invoiceWriteoffAmount
	 * 
	 */
	public KualiDecimal getInvoiceWriteoffAmount() { 
		return invoiceWriteoffAmount;
	}

	/**
	 * Sets the invoiceWriteoffAmount attribute.
	 * 
	 * @param invoiceWriteoffAmount The invoiceWriteoffAmount to set.
	 * 
	 */
	public void setInvoiceWriteoffAmount(KualiDecimal invoiceWriteoffAmount) {
		this.invoiceWriteoffAmount = invoiceWriteoffAmount;
	}


	/**
	 * Gets the invoiceDescription attribute.
	 * 
	 * @return Returns the invoiceDescription
	 * 
	 */
	public String getInvoiceDescription() { 
		return invoiceDescription;
	}

	/**
	 * Sets the invoiceDescription attribute.
	 * 
	 * @param invoiceDescription The invoiceDescription to set.
	 * 
	 */
	public void setInvoiceDescription(String invoiceDescription) {
		this.invoiceDescription = invoiceDescription;
	}


	/**
	 * Gets the customerPurchaseOrderDate attribute.
	 * 
	 * @return Returns the customerPurchaseOrderDate
	 * 
	 */
	public Date getCustomerPurchaseOrderDate() { 
		return customerPurchaseOrderDate;
	}

	/**
	 * Sets the customerPurchaseOrderDate attribute.
	 * 
	 * @param customerPurchaseOrderDate The customerPurchaseOrderDate to set.
	 * 
	 */
	public void setCustomerPurchaseOrderDate(Date customerPurchaseOrderDate) {
		this.customerPurchaseOrderDate = customerPurchaseOrderDate;
	}


	/**
	 * Gets the billByChartOfAccountCode attribute.
	 * 
	 * @return Returns the billByChartOfAccountCode
	 * 
	 */
	public String getBillByChartOfAccountCode() { 
		return billByChartOfAccountCode;
	}

	/**
	 * Sets the billByChartOfAccountCode attribute.
	 * 
	 * @param billByChartOfAccountCode The billByChartOfAccountCode to set.
	 * 
	 */
	public void setBillByChartOfAccountCode(String billByChartOfAccountCode) {
		this.billByChartOfAccountCode = billByChartOfAccountCode;
	}


	/**
	 * Gets the billedByOrganizationCode attribute.
	 * 
	 * @return Returns the billedByOrganizationCode
	 * 
	 */
	public String getBilledByOrganizationCode() { 
		return billedByOrganizationCode;
	}

	/**
	 * Sets the billedByOrganizationCode attribute.
	 * 
	 * @param billedByOrganizationCode The billedByOrganizationCode to set.
	 * 
	 */
	public void setBilledByOrganizationCode(String billedByOrganizationCode) {
		this.billedByOrganizationCode = billedByOrganizationCode;
	}


	/**
	 * Gets the customerShipToAddressIdentifier attribute.
	 * 
	 * @return Returns the customerShipToAddressIdentifier
	 * 
	 */
	public Integer getCustomerShipToAddressIdentifier() { 
		return customerShipToAddressIdentifier;
	}

	/**
	 * Sets the customerShipToAddressIdentifier attribute.
	 * 
	 * @param customerShipToAddressIdentifier The customerShipToAddressIdentifier to set.
	 * 
	 */
	public void setCustomerShipToAddressIdentifier(Integer customerShipToAddressIdentifier) {
		this.customerShipToAddressIdentifier = customerShipToAddressIdentifier;
	}


	/**
	 * Gets the customerBillToAddressIdentifier attribute.
	 * 
	 * @return Returns the customerBillToAddressIdentifier
	 * 
	 */
	public Integer getCustomerBillToAddressIdentifier() { 
		return customerBillToAddressIdentifier;
	}

	/**
	 * Sets the customerBillToAddressIdentifier attribute.
	 * 
	 * @param customerBillToAddressIdentifier The customerBillToAddressIdentifier to set.
	 * 
	 */
	public void setCustomerBillToAddressIdentifier(Integer customerBillToAddressIdentifier) {
		this.customerBillToAddressIdentifier = customerBillToAddressIdentifier;
	}


	/**
	 * Gets the customerSpecialProcessingCode attribute.
	 * 
	 * @return Returns the customerSpecialProcessingCode
	 * 
	 */
	public String getCustomerSpecialProcessingCode() { 
		return customerSpecialProcessingCode;
	}

	/**
	 * Sets the customerSpecialProcessingCode attribute.
	 * 
	 * @param customerSpecialProcessingCode The customerSpecialProcessingCode to set.
	 * 
	 */
	public void setCustomerSpecialProcessingCode(String customerSpecialProcessingCode) {
		this.customerSpecialProcessingCode = customerSpecialProcessingCode;
	}


	/**
	 * Gets the customerRecordAttachmentIndicator attribute.
	 * 
	 * @return Returns the customerRecordAttachmentIndicator
	 * 
	 */
	public boolean isCustomerRecordAttachmentIndicator() { 
		return customerRecordAttachmentIndicator;
	}

	/**
	 * Sets the customerRecordAttachmentIndicator attribute.
	 * 
	 * @param customerRecordAttachmentIndicator The customerRecordAttachmentIndicator to set.
	 * 
	 */
	public void setCustomerRecordAttachmentIndicator(boolean customerRecordAttachmentIndicator) {
		this.customerRecordAttachmentIndicator = customerRecordAttachmentIndicator;
	}


	/**
	 * Gets the openInvoiceIndicator attribute.
	 * 
	 * @return Returns the openInvoiceIndicator
	 * 
	 */
	public boolean isOpenInvoiceIndicator() { 
		return openInvoiceIndicator;
	}

	/**
	 * Sets the openInvoiceIndicator attribute.
	 * 
	 * @param openInvoiceIndicator The openInvoiceIndicator to set.
	 * 
	 */
	public void setOpenInvoiceIndicator(boolean openInvoiceIndicator) {
		this.openInvoiceIndicator = openInvoiceIndicator;
	}

	/**
     * Gets the paymentAccountNumber attribute. 
     * @return Returns the paymentAccountNumber.
     */
    public String getPaymentAccountNumber() {
        return paymentAccountNumber;
    }

    /**
     * Sets the paymentAccountNumber attribute value.
     * @param paymentAccountNumber The paymentAccountNumber to set.
     */
    public void setPaymentAccountNumber(String paymentAccountNumber) {
        this.paymentAccountNumber = paymentAccountNumber;
    }

    /**
     * Gets the paymentChartOfAccountsCode attribute. 
     * @return Returns the paymentChartOfAccountsCode.
     */
    public String getPaymentChartOfAccountsCode() {
        return paymentChartOfAccountsCode;
    }

    /**
     * Sets the paymentChartOfAccountsCode attribute value.
     * @param paymentChartOfAccountsCode The paymentChartOfAccountsCode to set.
     */
    public void setPaymentChartOfAccountsCode(String paymentChartOfAccountsCode) {
        this.paymentChartOfAccountsCode = paymentChartOfAccountsCode;
    }

    /**
     * Gets the paymentFinancialObjectCode attribute. 
     * @return Returns the paymentFinancialObjectCode.
     */
    public String getPaymentFinancialObjectCode() {
        return paymentFinancialObjectCode;
    }

    /**
     * Sets the paymentFinancialObjectCode attribute value.
     * @param paymentFinancialObjectCode The paymentFinancialObjectCode to set.
     */
    public void setPaymentFinancialObjectCode(String paymentFinancialObjectCode) {
        this.paymentFinancialObjectCode = paymentFinancialObjectCode;
    }

    /**
     * Gets the paymentFinancialSubObjectCode attribute. 
     * @return Returns the paymentFinancialSubObjectCode.
     */
    public String getPaymentFinancialSubObjectCode() {
        return paymentFinancialSubObjectCode;
    }

    /**
     * Sets the paymentFinancialSubObjectCode attribute value.
     * @param paymentFinancialSubObjectCode The paymentFinancialSubObjectCode to set.
     */
    public void setPaymentFinancialSubObjectCode(String paymentFinancialSubObjectCode) {
        this.paymentFinancialSubObjectCode = paymentFinancialSubObjectCode;
    }

    /**
     * Gets the paymentOrganizationReferenceIdentifier attribute. 
     * @return Returns the paymentOrganizationReferenceIdentifier.
     */
    public String getPaymentOrganizationReferenceIdentifier() {
        return paymentOrganizationReferenceIdentifier;
    }

    /**
     * Sets the paymentOrganizationReferenceIdentifier attribute value.
     * @param paymentOrganizationReferenceIdentifier The paymentOrganizationReferenceIdentifier to set.
     */
    public void setPaymentOrganizationReferenceIdentifier(String paymentOrganizationReferenceIdentifier) {
        this.paymentOrganizationReferenceIdentifier = paymentOrganizationReferenceIdentifier;
    }

    /**
     * Gets the paymentProjectCode attribute. 
     * @return Returns the paymentProjectCode.
     */
    public String getPaymentProjectCode() {
        return paymentProjectCode;
    }

    /**
     * Sets the paymentProjectCode attribute value.
     * @param paymentProjectCode The paymentProjectCode to set.
     */
    public void setPaymentProjectCode(String paymentProjectCode) {
        this.paymentProjectCode = paymentProjectCode;
    }

    /**
     * Gets the paymentSubAccountNumber attribute. 
     * @return Returns the paymentSubAccountNumber.
     */
    public String getPaymentSubAccountNumber() {
        return paymentSubAccountNumber;
    }

    /**
     * Sets the paymentSubAccountNumber attribute value.
     * @param paymentSubAccountNumber The paymentSubAccountNumber to set.
     */
    public void setPaymentSubAccountNumber(String paymentSubAccountNumber) {
        this.paymentSubAccountNumber = paymentSubAccountNumber;
    }

    /**
     * Gets the printDate attribute.
     * 
     * @return Returns the printDate
     * 
     */
    public Date getPrintDate() { 
        return printDate;
    }

    /**
     * Sets the printDate attribute.
     * 
     * @param printDate The printDate to set.
     * 
     */
    public void setPrintDate(Date printDate) {
        this.printDate = printDate;
    }    
    
    /**
     * Gets the localTaxPercent attribute. 
     * @return Returns the localTaxPercent.
     */
    public KualiDecimal getLocalTaxPercent() {
        return localTaxPercent;
    }

    /**
     * Sets the localTaxPercent attribute value.
     * @param localTaxPercent The localTaxPercent to set.
     */
    public void setLocalTaxPercent(KualiDecimal localTaxPercent) {
        this.localTaxPercent = localTaxPercent;
    }

    /**
     * Gets the stateTaxPercent attribute. 
     * @return Returns the stateTaxPercent.
     */
    public KualiDecimal getStateTaxPercent() {
        return stateTaxPercent;
    }

    /**
     * Sets the stateTaxPercent attribute value.
     * @param stateTaxPercent The stateTaxPercent to set.
     */
    public void setStateTaxPercent(KualiDecimal stateTaxPercent) {
        this.stateTaxPercent = stateTaxPercent;
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
	 */
	public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
		this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
	}

	/**
	 * Gets the billByChartOfAccount attribute.
	 * 
	 * @return Returns the billByChartOfAccount
	 * 
	 */
	public Chart getBillByChartOfAccount() { 
		return billByChartOfAccount;
	}

	/**
	 * Sets the billByChartOfAccount attribute.
	 * 
	 * @param billByChartOfAccount The billByChartOfAccount to set.
	 * @deprecated
	 */
	public void setBillByChartOfAccount(Chart billByChartOfAccount) {
		this.billByChartOfAccount = billByChartOfAccount;
	}

	/**
	 * Gets the billedByOrganization attribute.
	 * 
	 * @return Returns the billedByOrganization
	 * 
	 */
	public Org getBilledByOrganization() { 
		return billedByOrganization;
	}

	/**
	 * Sets the billedByOrganization attribute.
	 * 
	 * @param billedByOrganization The billedByOrganization to set.
	 * @deprecated
	 */
	public void setBilledByOrganization(Org billedByOrganization) {
		this.billedByOrganization = billedByOrganization;
	}

	/**
	 * Gets the customerSpecialProcessing attribute.
	 * 
	 * @return Returns the customerSpecialProcessing
	 * 
	 */
	public CustomerProcessingType getCustomerSpecialProcessing() { 
		return customerSpecialProcessing;
	}

	/**
	 * Sets the customerSpecialProcessing attribute.
	 * 
	 * @param customerSpecialProcessing The customerSpecialProcessing to set.
	 * @deprecated
	 */
	public void setCustomerSpecialProcessing(CustomerProcessingType customerSpecialProcessing) {
		this.customerSpecialProcessing = customerSpecialProcessing;
	}
    
	/**
     * Gets the paymentAccount attribute. 
     * @return Returns the paymentAccount.
     */
    public Account getPaymentAccount() {
        return paymentAccount;
    }

    /**
     * Sets the paymentAccount attribute value.
     * @param paymentAccount The paymentAccount to set.
     * @deprecated
     */
    public void setPaymentAccount(Account paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    /**
     * Gets the paymentChartOfAccounts attribute. 
     * @return Returns the paymentChartOfAccounts.
     */
    public Chart getPaymentChartOfAccounts() {
        return paymentChartOfAccounts;
    }

    /**
     * Sets the paymentChartOfAccounts attribute value.
     * @param paymentChartOfAccounts The paymentChartOfAccounts to set.
     * @deprecated
     */
    public void setPaymentChartOfAccounts(Chart paymentChartOfAccounts) {
        this.paymentChartOfAccounts = paymentChartOfAccounts;
    }

    /**
     * Gets the paymentFinancialObject attribute. 
     * @return Returns the paymentFinancialObject.
     */
    public ObjectCode getPaymentFinancialObject() {
        return paymentFinancialObject;
    }

    /**
     * Sets the paymentFinancialObject attribute value.
     * @param paymentFinancialObject The paymentFinancialObject to set.
     * @deprecated
     */
    public void setPaymentFinancialObject(ObjectCode paymentFinancialObject) {
        this.paymentFinancialObject = paymentFinancialObject;
    }

    /**
     * Gets the paymentFinancialSubObject attribute. 
     * @return Returns the paymentFinancialSubObject.
     */
    public SubObjCd getPaymentFinancialSubObject() {
        return paymentFinancialSubObject;
    }

    /**
     * Sets the paymentFinancialSubObject attribute value.
     * @param paymentFinancialSubObject The paymentFinancialSubObject to set.
     * @deprecated
     */
    public void setPaymentFinancialSubObject(SubObjCd paymentFinancialSubObject) {
        this.paymentFinancialSubObject = paymentFinancialSubObject;
    }

    /**
     * Gets the paymentProject attribute. 
     * @return Returns the paymentProject.
     */
    public ProjectCode getPaymentProject() {
        return paymentProject;
    }

    /**
     * Sets the paymentProject attribute value.
     * @param paymentProject The paymentProject to set.
     * @deprecated
     */
    public void setPaymentProject(ProjectCode paymentProject) {
        this.paymentProject = paymentProject;
    }

    /**
     * Gets the paymentSubAccount attribute. 
     * @return Returns the paymentSubAccount.
     */
    public SubAccount getPaymentSubAccount() {
        return paymentSubAccount;
    }

    /**
     * Sets the paymentSubAccount attribute value.
     * @param paymentSubAccount The paymentSubAccount to set.
     * @deprecated
     */
    public void setPaymentSubAccount(SubAccount paymentSubAccount) {
        this.paymentSubAccount = paymentSubAccount;
    }
    
    /**
     * This method returns the billing date for display.  If billing date hasn't been set yet, just display current date
     * 
     * @return
     */
    public Date getBillingDateForDisplay() {
        if( ObjectUtils.isNotNull( getBillingDate() ) ){
            return getBillingDate();
        } else {
            return SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        }
    }  
    
    /**
     * This method...
     * @param date
     */
    public void setBillingDateForDisplay(Date date){
        //do nothing
    }    

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
	    return m;
    }

    public Integer getNextInvoiceItemNumber() {
        return nextInvoiceItemNumber;
    }

    public void setNextInvoiceItemNumber(Integer nextInvoiceItemNumber) {
        this.nextInvoiceItemNumber = nextInvoiceItemNumber;
    }
    
    
    /**
     * This method returns true if this document is a reversal for another document
     * @return
     */
    public boolean isInvoiceReversal(){
        return ObjectUtils.isNotNull(documentHeader.getFinancialDocumentInErrorNumber());
    }
    
    /**
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return ((CustomerInvoiceDetail)postable).isDebit();
    }
    
    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return CustomerInvoiceDetail.class;
    }
    
    /**
     * Ensures that all the accounts receivable object codes are correctly updated
     */
    public void updateAccountReceivableObjectCodes(){
        for( Iterator e = getSourceAccountingLines().iterator(); e.hasNext(); ){
            SpringContext.getBean(CustomerInvoiceDetailService.class).updateAccountsReceivableObjectCode(((CustomerInvoiceDetail)e.next()));
        }
    }
    
    /**
     * This method creates the following GLPE's for the invoice
     * 
     * 1. Debit to receivable for total line amount ( including sales tax if it exists ).
     * 2. Credit to income based on item price * quantity.
     * 3. Credit to state sales tax account/object code if state sales tax exists.
     * 4. Credit to district sales tax account/object code if district sales tax exists. 
     * 
     * @see org.kuali.kfs.service.impl.GenericGeneralLedgerPendingEntryGenerationProcessImpl#processGenerateGeneralLedgerPendingEntries(org.kuali.kfs.document.GeneralLedgerPendingEntrySource, org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        
        // 1. Debit to receivable
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        processReceivableDebitGeneralLedgerPendingEntry(sequenceHelper, glpeSourceDetail, explicitEntry);

        // increment the sequence counter
        sequenceHelper.increment();
        
        // 2. Credit to the income
        explicitEntry = new GeneralLedgerPendingEntry();
        processIncomeCreditGeneralLedgerPendingEntry(sequenceHelper, glpeSourceDetail, explicitEntry);
        
        // increment the sequence counter
        //sequenceHelper.increment();

        // 3. If State Sales Tax exists, credit to sales tax account       
        // TODO Sales Tax Service should create the state sales tax GLPEs
        /*
        // increment the sequence counter
        sequenceHelper.increment();
        */
        
        // 4. If District Sales Tax exists, credit to sales tax account
        // TODO District Tax Service should create the state sales tax GLPEs

        return true;
    }
    
    
    /**
     * This method creates the receivable GLPE for each invoice detail line.
     * 
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void processReceivableDebitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry) {

        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail)glpeSourceDetail;
        
        ReceivableCustomerInvoiceDetail receivable = new ReceivableCustomerInvoiceDetail( customerInvoiceDetail, this);
        
        //receivable line is debit if (normal invoice AND not a discount line) OR (reversal AND a discount line)
        boolean isDebit = (!isInvoiceReversal() && !customerInvoiceDetail.isDiscountLine()) 
                            || (isInvoiceReversal() && customerInvoiceDetail.isDiscountLine());
        receivable.setDebit(isDebit);
        
        processExplicitGeneralLedgerPendingEntry(sequenceHelper, receivable, explicitEntry);
    }
    
    /**
     * This method adds pending entry with transaction ledger entry amount set to item price * quantity
     * 
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void processIncomeCreditGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry) {

        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail)glpeSourceDetail;

        //income line is debit if (normal invoice AND is a discount line) OR (reversal AND not a discount line)
        boolean isDebit = (!isInvoiceReversal() && customerInvoiceDetail.isDiscountLine()) 
        || (isInvoiceReversal() && !customerInvoiceDetail.isDiscountLine());
        customerInvoiceDetail.setDebit(isDebit);        
        
        // populate the explicit entry
        getGeneralLedgerPendingEntryService().populateExplicitGeneralLedgerPendingEntry(this, glpeSourceDetail, sequenceHelper, explicitEntry);
        
        //modify amount (since income should exclude state and district tax amounts);
        KualiDecimal subTotalAmount = customerInvoiceDetail.getInvoiceItemUnitPrice().multiply(new KualiDecimal(customerInvoiceDetail.getInvoiceItemQuantity()));
        explicitEntry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForDetail(customerInvoiceDetail));
        
        //add pending entry
        addPendingEntry(explicitEntry);
    }
    
    /**
     * Returns an implementation of the GeneralLedgerPendingEntryService
     * @return an implementation of the GeneralLedgerPendingEntryService
     */
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return SpringContext.getBean(GeneralLedgerPendingEntryService.class);
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map
     */
    public Map getValuesMap() {
        Map valuesMap = new HashMap();

        valuesMap.put("postingYear", getPostingYear());
        valuesMap.put("paymentChartOfAccountsCode", getPaymentChartOfAccountsCode());
        valuesMap.put("paymentAccountNumber", getPaymentAccountNumber());
        valuesMap.put("paymentFinancialObjectCode", getPaymentFinancialObjectCode());
        valuesMap.put("paymentSubAccountNumber", getPaymentSubAccountNumber());
        valuesMap.put("paymentFinancialSubObjectCode", getPaymentFinancialSubObjectCode());
        valuesMap.put("paymentProjectCode", getPaymentProjectCode());
        return valuesMap;
    }
    
    
    /**
     * When document is processed, set the billingDate to today's date.
     * 
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange(){
        super.handleRouteStatusChange();
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            setBillingDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight());
            
            //setup discount line references
            updateDiscountAndParentLineReferences();
            
            //save invoice paid applied lines
            //TODO maybe this should be done somewhere else? should there be a list of discount lines??
            SpringContext.getBean(InvoicePaidAppliedService.class).saveInvoicePaidAppliedForDiscounts(getSourceAccountingLines(), this);
        } 
    }
    
    
    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        SpringContext.getBean(CustomerInvoiceDocumentService.class).setupDefaultValuesForCopiedCustomerInvoiceDocument(this);
    }
    
    /**
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        negateCustomerInvoiceDetailUnitPrices();
    }
    
    
    /**
     * This method...
     */
    public void negateCustomerInvoiceDetailUnitPrices(){
        CustomerInvoiceDetail customerInvoiceDetail;
        for( Iterator i = getSourceAccountingLines().iterator(); i.hasNext();  ){
            customerInvoiceDetail = (CustomerInvoiceDetail)i.next();
            customerInvoiceDetail.setInvoiceItemUnitPrice(customerInvoiceDetail.getInvoiceItemUnitPrice().negated());
        }
        
    }
    
    /**
     * This method returns true if invoice document has at least one discount line
     * @return
     */
    public boolean hasAtLeastOneDiscount(){
        
        CustomerInvoiceDetail customerInvoiceDetail;
        for( Iterator i = getSourceAccountingLines().iterator(); i.hasNext();  ){
            customerInvoiceDetail = (CustomerInvoiceDetail)i.next();
            if (customerInvoiceDetail.isDiscountLineParent()){
                return true;
            }
        }        
        return false;
    }
    
    /**
     * This method returns true if line number is discount line number based on sequence number
     * @param sequenceNumber
     * @return
     */
    public boolean isDiscountLineBasedOnSequenceNumber(Integer sequenceNumber){
        if( ObjectUtils.isNull(sequenceNumber)){
            return false;
        }
        
        CustomerInvoiceDetail customerInvoiceDetail;
        for( Iterator i = getSourceAccountingLines().iterator(); i.hasNext();  ){
            customerInvoiceDetail = (CustomerInvoiceDetail)i.next();
            Integer discLineNum = customerInvoiceDetail.getInvoiceItemDiscountLineNumber();
            
            //check if sequence number is referenced as a discount line for another customer invoice detail (i.e. the parent line)
            if( ObjectUtils.isNotNull(discLineNum) && sequenceNumber.equals( customerInvoiceDetail.getInvoiceItemDiscountLineNumber() ) ){
                return true;
            }
        }
        return false;
    }  
    
    /**
     * This method returns parent customer invoice detail based on child discount sequence number
     * 
     * @param sequenceNumber
     * @return
     */
    public CustomerInvoiceDetail getParentLineBasedOnDiscountSequenceNumber(Integer discountSequenceNumber){
        
        if( ObjectUtils.isNull(discountSequenceNumber)){
            return null;
        }
        
        CustomerInvoiceDetail customerInvoiceDetail;
        for( Iterator i = getSourceAccountingLines().iterator(); i.hasNext();  ){
            customerInvoiceDetail = (CustomerInvoiceDetail)i.next();
            Integer discLineNum = customerInvoiceDetail.getInvoiceItemDiscountLineNumber();
            if( ObjectUtils.isNotNull(discLineNum) && discountSequenceNumber.equals( customerInvoiceDetail.getInvoiceItemDiscountLineNumber() ) ){
                return customerInvoiceDetail;
            }
        }
        return null;
    }        
    
    
    /**
     * This method is called on CustomerInvoiceDocumentAction.execute() to set isDiscount to true if it truly is a discount line
     */
    public void updateDiscountAndParentLineReferences(){
        
        CustomerInvoiceDetail discount;
        for( Iterator i = getSourceAccountingLines().iterator(); i.hasNext();  ){
            discount = (CustomerInvoiceDetail)i.next();
            
            //get sequence number and check if theres a corresponding parent line for that discount line
            CustomerInvoiceDetail parent = getParentLineBasedOnDiscountSequenceNumber(discount.getSequenceNumber() );
            if( ObjectUtils.isNotNull(parent) ){
                discount.setParentDiscountCustomerInvoiceDetail(parent);
                parent.setDiscountCustomerInvoiceDetail(discount);
            } else {
                discount.setParentDiscountCustomerInvoiceDetail(null);
            }
        }
    }

    /**
     * This method removes the corresponding discount line based on the index of the parent line index.
     * This assumes that the discount line is ALWAYS after the index of the parent line.
     * 
     * @param deleteIndex
     */
    public void removeDiscountLineBasedOnParentLineIndex(int parentLineIndex) {
        //get parent line line
        CustomerInvoiceDetail parentLine = (CustomerInvoiceDetail)getSourceAccountingLines().get(parentLineIndex);
        
        //get index for discount line
        int discountLineIndex = -1; //this should ALWAYS get set
        for( int i = 0; i < getSourceAccountingLines().size(); i++ ){
            if( parentLine.getInvoiceItemDiscountLineNumber().equals(((CustomerInvoiceDetail)getSourceAccountingLines().get(i)).getSequenceNumber())){
                discountLineIndex = i;
            }
        }
        //remove discount line
        getSourceAccountingLines().remove(discountLineIndex);
    }

    public CustomerAddress getCustomerShipToAddress() {
        return customerShipToAddress;
    }

    public void setCustomerShipToAddress(CustomerAddress customerShipToAddress) {
        this.customerShipToAddress = customerShipToAddress;
    }

    public CustomerAddress getCustomerBillToAddress() {
        return customerBillToAddress;
    }

    public void setCustomerBillToAddress(CustomerAddress customerBillToAddress) {
        this.customerBillToAddress = customerBillToAddress;
    }
}
