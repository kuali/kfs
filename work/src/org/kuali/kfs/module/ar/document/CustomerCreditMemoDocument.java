package org.kuali.module.ar.document;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerCreditMemoDocument extends AccountingDocumentBase {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoDocument.class);
    
    private String statusCode;
    
    private String documentNumber;
    private Integer financialDocumentPostingYear;// there is an inherited property 'postingYear'
    private String financialDocumentReferenceInvoiceNumber;
    
    private KualiDecimal crmTotalItemAmount = KualiDecimal.ZERO;
    private KualiDecimal crmTotalTaxAmount = KualiDecimal.ZERO;
    private KualiDecimal crmTotalAmount = KualiDecimal.ZERO;
    
    private Integer invOutstandingDays;
    
    private DocumentHeader documentHeader;

    private CustomerInvoiceDocument invoice;
    private List<CustomerCreditMemoDetail> creditMemoDetails;
    
    public CustomerCreditMemoDocument(){
        super();
        creditMemoDetails = new TypedArrayList(CustomerCreditMemoDetail.class);
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
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }


    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }


    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the financialDocumentPostingYear attribute. 
     * @return Returns the financialDocumentPostingYear.
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }


    /**
     * Sets the financialDocumentPostingYear attribute value.
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
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
        if(ObjectUtils.isNull(invoice) && StringUtils.isNotEmpty(financialDocumentReferenceInvoiceNumber) )
            refreshReferenceObject("invoice");
               
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
     * Initializes the values for a new document.
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        setStatusCode(ArConstants.CustomerCreditMemoStatuses.INITIATE);
    }
    
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }
    
    /**
     * Clear out the initially populated fields.
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");

        // Clearing document Init fields
        setFinancialDocumentReferenceInvoiceNumber(null);
    }


    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // TODO Auto-generated method stub
        return false;
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
     * 
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
     * 
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
        double diffInDays = DateUtils.getDifferenceInDays(invBillingDateTimestamp,todayDateTimestamp);
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
    
    public KualiDecimal getTaxRate(){
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
    
    private void prepareTotalsForUpdate(KualiDecimal oldItemAmount){
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

}
