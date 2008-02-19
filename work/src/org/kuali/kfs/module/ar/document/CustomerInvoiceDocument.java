package org.kuali.module.ar.document;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPostable;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.CustomerProcessingType;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerInvoiceDocument extends AccountingDocumentBase {

    protected Integer nextInvoiceItemNumber;
    
	private Integer universityFiscalYear;
	private String universityFiscalPeriodCode;
	private String invoiceHeaderText;
	private String invoiceAttentionLineText;
	private Date invoiceDueDate;
	private Date billingDate;
	private String invoiceTermsText;
	private String organizationInvoiceNumber;
	private boolean writeoffIndicator;
	private String customerPurchaseOrderNumber;
	private boolean printInvoiceIndicator;
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

    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
	private AccountingPeriod universityFiscalPeriod;
	private Chart billByChartOfAccount;
	private Org billedByOrganization;
	private CustomerProcessingType customerSpecialProcessing;

    private List<CustomerInvoiceDetail> customerInvoiceDetails;
    
	/**
	 * Default constructor.
	 */
	public CustomerInvoiceDocument() {
	    super();
	    this.nextInvoiceItemNumber = new Integer(1);
        customerInvoiceDetails = new ArrayList<CustomerInvoiceDetail>();
        
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
	 * 
	 * @return Returns the printInvoiceIndicator
	 * 
	 */
	public boolean isPrintInvoiceIndicator() { 
		return printInvoiceIndicator;
	}

	/**
	 * Sets the printInvoiceIndicator attribute.
	 * 
	 * @param printInvoiceIndicator The printInvoiceIndicator to set.
	 * 
	 */
	public void setPrintInvoiceIndicator(boolean printInvoiceIndicator) {
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
     * Gets the customerInvoiceDetails attribute. 
     * @return Returns the customerInvoiceDetails.
     */
    public List<CustomerInvoiceDetail> getCustomerInvoiceDetails() {
        return customerInvoiceDetails;
    }

    /**
     * Sets the customerInvoiceDetails attribute value.
     * @param customerInvoiceDetails The customerInvoiceDetails to set.
     */
    public void setCustomerInvoiceDetails(List<CustomerInvoiceDetail> customerInvoiceDetails) {
        this.customerInvoiceDetails = customerInvoiceDetails;
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
     * Retrieves a specific customer invoice detail from the list, by array index
     * 
     * @param index the index of the checks array to retrieve the check from
     * @return a Check
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetail(int index) {
        if (index >= customerInvoiceDetails.size()) {
            for (int i = customerInvoiceDetails.size(); i <= index; i++) {
                customerInvoiceDetails.add(new CustomerInvoiceDetail());
            }
        }
        return customerInvoiceDetails.get(index);
    }	
	
	/**
	 * This method adds the passed in customer invoice detail to the collection of customer invoice details
	 * @param customerInvoiceDetail
	 */
	public void addCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail){
	    customerInvoiceDetail.setInvoiceItemNumber(getNextInvoiceItemNumber());
	    customerInvoiceDetails.add(customerInvoiceDetail);
        this.nextInvoiceItemNumber = new Integer(this.getNextInvoiceItemNumber().intValue() + 1);	    
	}
	
	
	/**
	 * This method removes the customer invoice detail and the specified index
	 * @param index
	 */
	public void deleteCustomerInvoiceDetail(int index){
	    customerInvoiceDetails.remove(index);
	}
	
	/**
	 * This method sets the default values for an invoice
	 */
	public void setupDefaultValues(){
	    ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        if(currentUser != null) {
            setDefaultBillByChartOfAccountsCode(currentUser);
            setDefaultBilledByOrganizationCode(currentUser);
        }
        
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        setDefaultInvoiceDueDate(dateTimeService);
        setDefaultBillingDate(dateTimeService);
        
        //Write-off Indicator = 'Y'
        setWriteoffIndicator(true);
        
        //Print Invoice Indicator = "Y"
        setPrintInvoiceIndicator(true);
        
        //Processing Chart = Processing Chart retrieved from Billing Org options
        //convert this into some kind of service maybe?
        //this.getAccountsReceivablethisHeader().setProcessingChartOfAccountCode(processingChartOfAccountCode);
        
        //Processing Org = Processing Org retrieved from Billing Org Options
        //this.getAccountsReceivablethisHeader().setProcessingOrganizationCode(processingOrganizationCode);
        
        //Print Invoice Detail = Print Invoice Detail retrieved from Billing Org Options
        //can't find this one
        
        //Payment Terms Text = Payment Terms Text retrieved from Billing Org Options
        //this.setInvoiceTermsText(invoiceTermsText);
        
        //Set AR this header
        this.setAccountsReceivableDocumentHeader(new AccountsReceivableDocumentHeader());
        this.getAccountsReceivableDocumentHeader().setDocumentNumber(this.getDocumentNumber());	    
	}
	
	/**
	 * This method sets billing date equal to todays date by default
	 * @param dateTimeService
	 */
	public void setDefaultBillingDate(DateTimeService dateTimeService){
	    Date today = dateTimeService.getCurrentSqlDate();
        this.setBillingDate(today);
	}
	
    /**
     * This method sets due date equal to todays date +30 days by default
     * @param dateTimeService
     */
    public void setDefaultInvoiceDueDate(DateTimeService dateTimeService){
        //Invoice due date = current date + 30 days
        Calendar cal = dateTimeService.getCurrentCalendar();
        cal.add(Calendar.DATE, 30);
        Date sqlDueDate = null;
        try {
           sqlDueDate =  dateTimeService.convertToSqlDate(new Timestamp(cal.getTime().getTime()));
        } catch (ParseException e) {
            //TODO: throw an error here, but don't die
        }
        if(sqlDueDate != null) {
            this.setInvoiceDueDate(sqlDueDate);
        }
    }	
	
	/**
	 * Sets documents default chart of accounts code based on current user
	 * @param currentUser
	 */
	public void setDefaultBillByChartOfAccountsCode(ChartUser currentUser){
	    this.setBillByChartOfAccountCode(currentUser.getChartOfAccountsCode());
	}
	
	/**
	 * Sets documents default organization code based on current user
	 * @param currentUser
	 */
	public void setDefaultBilledByOrganizationCode(ChartUser currentUser){
	    this.setBilledByOrganizationCode(currentUser.getOrganizationCode());
	}

    public Integer getNextInvoiceItemNumber() {
        return nextInvoiceItemNumber;
    }

    public void setNextInvoiceItemNumber(Integer nextInvoiceItemNumber) {
        this.nextInvoiceItemNumber = nextInvoiceItemNumber;
    }
    
    /**
     * Returns the total amount of all customer invoice detail amounts.
     * @return
     */
    public KualiDecimal getInvoiceTotalAmount(){
        KualiDecimal invoiceTotalAmount = new KualiDecimal(0);
        for ( CustomerInvoiceDetail customerInvoiceDetail : getCustomerInvoiceDetails() ){
            invoiceTotalAmount = invoiceTotalAmount.add( customerInvoiceDetail.getAmount() );
        }
        return invoiceTotalAmount;
    }
    
    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPostable) is a credit or a debit, in terms of GLPE generation
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    @Override
    public boolean isDebit(GeneralLedgerPostable postable) {
        // TODO Auto-generated method stub
        return false;
    }
}
