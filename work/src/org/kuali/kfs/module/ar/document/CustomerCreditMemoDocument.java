package org.kuali.module.ar.document;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.bo.FinancialSystemDocumentHeader;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.document.GeneralLedgerPendingEntrySource;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
// public class CustomerCreditMemoDocument extends AccountingDocumentBase {
public class CustomerCreditMemoDocument extends FinancialSystemTransactionalDocumentBase implements GeneralLedgerPendingEntrySource {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoDocument.class);
    
    private String statusCode;
    
    private String documentNumber;
    private Integer postingYear;
    private String financialDocumentReferenceInvoiceNumber;
    
    private KualiDecimal crmTotalItemAmount = KualiDecimal.ZERO;
    private KualiDecimal crmTotalTaxAmount = KualiDecimal.ZERO;
    private KualiDecimal crmTotalAmount = KualiDecimal.ZERO;
    
    private Integer invOutstandingDays;

    private CustomerInvoiceDocument invoice;
    private List<CustomerCreditMemoDetail> creditMemoDetails;
    
    protected List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;

    public CustomerCreditMemoDocument() {
        super();
        creditMemoDetails = new TypedArrayList(CustomerCreditMemoDetail.class);
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
    }

    /**
     * Gets the creditMemoDetails attribute.
     * @return Returns the creditMemoDetails.
     */
    public List<CustomerCreditMemoDetail> getCreditMemoDetails() {
        return creditMemoDetails;
    }

    /**
     * Sets the creditMemoDetails attribute value.
     * @param creditMemoDetails The creditMemoDetails to set.
     */
    public void setCreditMemoDetails(List<CustomerCreditMemoDetail> creditMemoDetails) {
        this.creditMemoDetails = creditMemoDetails;
    }

    /**
     * Gets the financialDocumentReferenceInvoiceNumber attribute.
     * @return Returns the financialDocumentReferenceInvoiceNumber.
     */
    public String getFinancialDocumentReferenceInvoiceNumber() {
        return financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Sets the financialDocumentReferenceInvoiceNumber attribute value.
     * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
     */
    public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
        if (financialDocumentReferenceInvoiceNumber != null)
            financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber.toUpperCase();

        this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
    }
    
    /**
     * Gets the invoice attribute.
     * @return Returns the invoice.
     */
    public CustomerInvoiceDocument getInvoice() {
        if (ObjectUtils.isNull(invoice) && StringUtils.isNotEmpty(financialDocumentReferenceInvoiceNumber)){
            refreshReferenceObject("invoice");
        }

        return invoice;
    }

    /**
     * Sets the invoice attribute value.
     * @param invoice The invoice to set.
     */
    public void setInvoice(CustomerInvoiceDocument invoice) {
        this.invoice = invoice;
    }
    
    /**
     * Gets the statusCode attribute.
     * @return Returns the statusCode.
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode attribute value.
     * @param statusCode The statusCode to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    /**
     * Gets the postingYear attribute. 
     * @return Returns the postingYear.
     */
    public Integer getPostingYear() {
        return postingYear;
    }

    /**
     * Sets the postingYear attribute value.
     * @param postingYear The postingYear to set.
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }
    
    /**
     * Initializes the values for a new document.
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        setStatusCode(ArConstants.CustomerCreditMemoStatuses.INITIATE);
    }

    /**
     * Clear out the initially populated fields.
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");

        // Clearing document Init fields
        setFinancialDocumentReferenceInvoiceNumber(null);
    }

    /**
     * Gets the crmTotalAmount attribute.
     * @return Returns the crmTotalAmount.
     */
    public KualiDecimal getCrmTotalAmount() {
        return crmTotalAmount;
    }
    
    /**
     * This method returns the crmTotalAmount as a currency formatted string.
     * @return String
     */
    public String getCurrencyFormattedCrmTotalAmount() {
        return (String) new CurrencyFormatter().format(crmTotalAmount);
    }

    /**
     * Sets the crmTotalAmount attribute value.
     * @param crmTotalAmount The crmTotalAmount to set.
     */
    public void setCrmTotalAmount(KualiDecimal crmTotalAmount) {
        this.crmTotalAmount = crmTotalAmount;
    }

    /**
     * Gets the crmTotalItemAmount attribute. 
     * @return Returns the crmTotalItemAmount.
     */
    public KualiDecimal getCrmTotalItemAmount() {
        return crmTotalItemAmount;
    }
    
    /**
     * This method returns the crmTotalItemAmount as a currency formatted string.
     * @return String
     */
    public String getCurrencyFormattedCrmTotalItemAmount() {
        return (String) new CurrencyFormatter().format(crmTotalItemAmount);
    }

    /**
     * Sets the crmTotalItemAmount attribute value. 
     * @param crmTotalItemAmount The crmTotalItemAmount to set.
     */
    public void setCrmTotalItemAmount(KualiDecimal crmTotalItemAmount) {
        this.crmTotalItemAmount = crmTotalItemAmount;
    }

    /**
     * Gets the crmTotalTaxAmount attribute.
     * @return Returns the crmTotalTaxAmount.
     */
    public KualiDecimal getCrmTotalTaxAmount() {
        return crmTotalTaxAmount;
    }
    
    /**
     * This method returns the crmTotalTaxAmount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedCrmTotalTaxAmount() {
        return (String) new CurrencyFormatter().format(crmTotalTaxAmount);
    }

    /**
     * Sets the crmTotalTaxAmount attribute value.
     * @param crmTotalTaxAmount The crmTotalTaxAmount to set.
     */
    public void setCrmTotalTaxAmount(KualiDecimal crmTotalTaxAmount) {
        this.crmTotalTaxAmount = crmTotalTaxAmount;
    }

    /**
     * Gets the invOutstandingDays attribute.
     * @return Returns the invOutstandingDays.
     */
    public Integer getInvOutstandingDays() {
        Timestamp invBillingDateTimestamp = new Timestamp(invoice.getBillingDate().getTime());
        Timestamp todayDateTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime());
        double diffInDays = DateUtils.getDifferenceInDays(invBillingDateTimestamp, todayDateTimestamp);
        invOutstandingDays = new Integer(new KualiDecimal(diffInDays).intValue());

        return invOutstandingDays;
    }

    /**
     * Sets the invOutstandingDays attribute value.
     * @param invOutstandingDays The invOutstandingDays to set.
     */
    public void setInvOutstandingDays(Integer invOutstandingDays) {
        this.invOutstandingDays = invOutstandingDays;
    }
    
    /**
     * This method gets the glpes
     * 
     * @return a list of glpes
     */
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }

    /**
     * This method sets the glpes
     * 
     * @return a list of glpes
     */
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.generalLedgerPendingEntries = generalLedgerPendingEntries;
    }

    public KualiDecimal getTaxRate() {
        KualiDecimal stateTaxRate = invoice.getStateTaxPercent();
        KualiDecimal localTaxRate = invoice.getLocalTaxPercent();
        KualiDecimal taxRate;
        
        if (ObjectUtils.isNull(stateTaxRate))
            stateTaxRate = KualiDecimal.ZERO;
        
        if (ObjectUtils.isNull(localTaxRate))
            localTaxRate = KualiDecimal.ZERO;
        
        taxRate = stateTaxRate.add(localTaxRate);
        
        return taxRate;
    }
    
    public void recalculateTotalsBasedOnChangedItemAmount(CustomerCreditMemoDetail customerCreditMemoDetail) {
        KualiDecimal duplicateCreditMemoItemTotalAmount = customerCreditMemoDetail.getDuplicateCreditMemoItemTotalAmount();
        KualiDecimal creditMemoItemTotalAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        
        // substract the 'old' item amount, tax amount, and total amount accordingly from totals
        if (ObjectUtils.isNotNull(duplicateCreditMemoItemTotalAmount))
            prepareTotalsForUpdate(duplicateCreditMemoItemTotalAmount);

        recalculateTotals(creditMemoItemTotalAmount);
        
        // update duplicate credit memo item amount with 'new' value
        customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(creditMemoItemTotalAmount);
    }
    
    public void recalculateTotals(CustomerCreditMemoDetail customerCreditMemoDetail) {
        KualiDecimal duplicateCreditMemoItemTotalAmount = customerCreditMemoDetail.getDuplicateCreditMemoItemTotalAmount();
        
        // substract the 'old' item amount, tax amount, and total amount accordingly from totals
        if (ObjectUtils.isNotNull(duplicateCreditMemoItemTotalAmount)) {
            prepareTotalsForUpdate(duplicateCreditMemoItemTotalAmount);
            customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(null);
        }
    }

    private void prepareTotalsForUpdate(KualiDecimal oldItemAmount) {
        KualiDecimal taxRate = getTaxRate();
        KualiDecimal oldItemTaxAmount = oldItemAmount.multiply(taxRate);
        
        crmTotalItemAmount = crmTotalItemAmount.subtract(oldItemAmount);
        crmTotalTaxAmount = crmTotalTaxAmount.subtract(oldItemTaxAmount);
        crmTotalAmount = crmTotalAmount.subtract(oldItemAmount.add(oldItemTaxAmount));
    }
    
    public void resetTotals(){
        crmTotalItemAmount = KualiDecimal.ZERO;
        crmTotalTaxAmount = KualiDecimal.ZERO;
        crmTotalAmount = KualiDecimal.ZERO;
    }
    
    public void recalculateTotals(KualiDecimal itemAmount) {
        KualiDecimal taxRate = getTaxRate();
        
        crmTotalItemAmount = crmTotalItemAmount.add(itemAmount);
        crmTotalTaxAmount = crmTotalTaxAmount.add(itemAmount.multiply(taxRate));
        crmTotalAmount = crmTotalItemAmount.add(crmTotalTaxAmount);
    }
    
    /*
     * populate customer credit memo details based on the invoice info
     */
    public void populateCustomerCreditMemoDetails() {
        CustomerCreditMemoDetail customerCreditMemoDetail;
        KualiDecimal invItemTaxAmount, openInvoiceAmount;
        Integer itemLineNumber;
        int ind = 0;

        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        setStatusCode(ArConstants.CustomerCreditMemoStatuses.IN_PROCESS);

        //update discount references
        invoice.updateDiscountAndParentLineReferences();

        List<SourceAccountingLine> invoiceDetails = invoice.getSourceAccountingLines();
        for (SourceAccountingLine invoiceDetail : invoiceDetails) {
            if (!((CustomerInvoiceDetail) invoiceDetail).isDiscountLine()) {
                customerCreditMemoDetail = new CustomerCreditMemoDetail();
                
                customerCreditMemoDetail.setAccountingLineIndexForCorrespondingInvoiceDetail(ind);

                // populate invoice item 'Total Amount'
                invItemTaxAmount = ((CustomerInvoiceDetail) invoiceDetail).getInvoiceItemTaxAmount();
                if (invItemTaxAmount == null) {
                    invItemTaxAmount = KualiDecimal.ZERO;
                    ((CustomerInvoiceDetail) invoiceDetail).setInvoiceItemTaxAmount(invItemTaxAmount);
                }
                customerCreditMemoDetail.setInvoiceLineTotalAmount(invItemTaxAmount, invoiceDetail.getAmount());

                itemLineNumber = ((CustomerInvoiceDetail) invoiceDetail).getSequenceNumber();
                customerCreditMemoDetail.setReferenceInvoiceItemNumber(itemLineNumber);

                openInvoiceAmount = customerInvoiceDetailService.getOpenAmount(itemLineNumber, (CustomerInvoiceDetail) invoiceDetail);
                customerCreditMemoDetail.setInvoiceOpenItemAmount(openInvoiceAmount);

                creditMemoDetails.add(customerCreditMemoDetail);
            }
            ind++;
        }

    }
    
    /**
     * This method populate credit memo details that aren't saved in database
     */
    public void populateCustomerCreditMemoDetailsAfterLoad(){
        
        KualiDecimal openInvoiceAmount;
        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);

        List<SourceAccountingLine> invoiceDetails = invoice.getSourceAccountingLines();
        int index = 0;
        for (SourceAccountingLine invoiceDetail : invoiceDetails) {
           for( CustomerCreditMemoDetail creditMemoDetail : creditMemoDetails ){
               if( invoiceDetail.getSequenceNumber().equals( creditMemoDetail.getReferenceInvoiceItemNumber() ) ){
                   creditMemoDetail.setAccountingLineIndexForCorrespondingInvoiceDetail(index);
                   openInvoiceAmount = customerInvoiceDetailService.getOpenAmount(invoiceDetail.getSequenceNumber(), (CustomerInvoiceDetail) invoiceDetail);
                   creditMemoDetail.setInvoiceOpenItemAmount(openInvoiceAmount);                   
               }
               
           }
           index++;
        }      
    }

    // TODO
    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#generateDocumentGeneralLedgerPendingEntries(org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        // TODO
        return success;
    }
    
    // TODO
    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#isDebit(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        //TODO
        return false;
    }
     
    /**
     * Returns a document header associated with this general ledger posting helper 
     * @return a document header
     */
    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }
        
    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#clearAnyGeneralLedgerPendingEntries()
     */
    public void clearAnyGeneralLedgerPendingEntries() {
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();

    }
    
    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPostables()
     */
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        return new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
    }
            
    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#addPendingEntry(org.kuali.kfs.bo.GeneralLedgerPendingEntry)
     */
    public void addPendingEntry(GeneralLedgerPendingEntry entry) {
        generalLedgerPendingEntries.add(entry);

    }
        
    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount().abs();
    }
    
    // TODO
    public String getFinancialDocumentTypeCode() {
        // TODO Auto-generated method stub
        return null;
    }
     
    // TODO 
    /**
     * Perform business rules common to all transactional documents when generating general ledger pending entries.
     * @see org.kuali.core.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        // TODO
        return true;
    }

}
