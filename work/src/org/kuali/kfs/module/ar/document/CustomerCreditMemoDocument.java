package org.kuali.kfs.module.ar.document;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.BlanketApproveDocumentEvent;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.ReceivableCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerCreditMemoDocument extends FinancialSystemTransactionalDocumentBase implements GeneralLedgerPendingEntrySource {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoDocument.class);

    private String statusCode;
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
        this.postingYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
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
     * @return a list of glpes
     */
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }

    /**
     * This method sets the glpes
     * @return a list of glpes
     */
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.generalLedgerPendingEntries = generalLedgerPendingEntries;
    }

    public KualiDecimal getTaxRate() {
        KualiDecimal stateTaxRate = getStateTaxPercent();
        KualiDecimal localTaxRate = getDistrictTaxPercent();
        KualiDecimal taxRate = stateTaxRate.add(localTaxRate);

        return taxRate;
    }
    
    public KualiDecimal getStateTaxPercent() {
        KualiDecimal stateTaxRate = invoice.getStateTaxPercent();
        
        if (ObjectUtils.isNull(stateTaxRate))
            stateTaxRate = KualiDecimal.ZERO;
        
        return stateTaxRate;
    }
    
    public KualiDecimal getDistrictTaxPercent() {
        KualiDecimal localTaxRate = invoice.getLocalTaxPercent();
        
        if (ObjectUtils.isNull(localTaxRate))
            localTaxRate = KualiDecimal.ZERO;
        
        return localTaxRate;
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

    public void resetTotals() {
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
        KualiDecimal invItemTaxAmount, invoiceUnitPrice, openInvoiceQuantity, openInvoiceAmount;

        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        setStatusCode(ArConstants.CustomerCreditMemoStatuses.IN_PROCESS);

        List<CustomerInvoiceDetail> customerInvoiceDetails = invoice.getCustomerInvoiceDetailsWithoutDiscounts();
        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            customerCreditMemoDetail = new CustomerCreditMemoDetail();

            if(ObjectUtils.isNull(customerInvoiceDetail.getInvoiceItemTaxAmount())){
                customerInvoiceDetail.setInvoiceItemTaxAmount(KualiDecimal.ZERO);
            }
            customerCreditMemoDetail.setInvoiceLineTotalAmount(customerInvoiceDetail.getInvoiceItemTaxAmount(), customerInvoiceDetail.getAmount());
            customerCreditMemoDetail.setReferenceInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
            openInvoiceAmount = customerInvoiceDetailService.getOpenAmount(customerInvoiceDetail.getSequenceNumber(), customerInvoiceDetail);

            /*
             * invoiceUnitPrice = ((CustomerInvoiceDetail) invoiceDetail).getInvoiceItemUnitPrice(); openInvoiceQuantity =
             * openInvoiceAmount.divide(invoiceUnitPrice); customerCreditMemoDetail.setInvoiceOpenItemQuantity(openInvoiceQuantity);
             */

            customerCreditMemoDetail.setInvoiceOpenItemAmount(openInvoiceAmount);
            customerCreditMemoDetail.setInvoiceOpenItemQuantity(getInvoiceOpenItemQuantity(customerCreditMemoDetail, customerInvoiceDetail));
            customerCreditMemoDetail.setDocumentNumber(this.documentNumber);
            customerCreditMemoDetail.setFinancialDocumentReferenceInvoiceNumber(this.financialDocumentReferenceInvoiceNumber);

            creditMemoDetails.add(customerCreditMemoDetail);
        }

    }
    
    /**
     * This method populates credit memo details that aren't saved in database
     */
    public void populateCustomerCreditMemoDetailsAfterLoad() {

        KualiDecimal invoiceUnitPrice, openInvoiceQuantity, openInvoiceAmount, invItemTaxAmount, creditMemoItemAmount, creditMemoTaxAmount, taxRate;
        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);

        taxRate = getTaxRate();
        List<CustomerInvoiceDetail> customerInvoiceDetails = invoice.getCustomerInvoiceDetailsWithoutDiscounts();
        for (CustomerCreditMemoDetail creditMemoDetail : creditMemoDetails) {

            creditMemoDetail.setFinancialDocumentReferenceInvoiceNumber(this.financialDocumentReferenceInvoiceNumber);
            CustomerInvoiceDetail customerInvoiceDetail = creditMemoDetail.getCustomerInvoiceDetail(); 
            openInvoiceAmount = customerInvoiceDetailService.getOpenAmount(customerInvoiceDetail.getSequenceNumber(), customerInvoiceDetail);
            creditMemoDetail.setInvoiceOpenItemAmount(openInvoiceAmount);

            /*
             * invoiceUnitPrice = ((CustomerInvoiceDetail) invoiceDetail).getInvoiceItemUnitPrice(); openInvoiceQuantity =
             * openInvoiceAmount.divide(invoiceUnitPrice); creditMemoDetail.setInvoiceOpenItemQuantity(openInvoiceQuantity);
             */
            creditMemoDetail.setInvoiceOpenItemQuantity(getInvoiceOpenItemQuantity(creditMemoDetail, customerInvoiceDetail));

            if(ObjectUtils.isNull(customerInvoiceDetail.getInvoiceItemTaxAmount())){
                customerInvoiceDetail.setInvoiceItemTaxAmount(KualiDecimal.ZERO);
            }
            creditMemoDetail.setInvoiceLineTotalAmount(customerInvoiceDetail.getInvoiceItemTaxAmount(), customerInvoiceDetail.getAmount());

            creditMemoItemAmount = creditMemoDetail.getCreditMemoItemTotalAmount();
            creditMemoDetail.setDuplicateCreditMemoItemTotalAmount(creditMemoItemAmount);
            if (ObjectUtils.isNotNull(creditMemoItemAmount)) {
                creditMemoTaxAmount = creditMemoItemAmount.multiply(taxRate);
                creditMemoDetail.setCreditMemoItemTaxAmount(creditMemoTaxAmount);
                creditMemoDetail.setCreditMemoLineTotalAmount(creditMemoItemAmount.add(creditMemoTaxAmount));

                crmTotalItemAmount = crmTotalItemAmount.add(creditMemoItemAmount);
                crmTotalTaxAmount = crmTotalTaxAmount.add(creditMemoTaxAmount);
                crmTotalAmount = crmTotalAmount.add(creditMemoItemAmount.add(creditMemoTaxAmount));
            }
        }
    }
    
    public KualiDecimal getInvoiceOpenItemQuantity(CustomerCreditMemoDetail customerCreditMemoDetail,CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal invoiceOpenItemQuantity;
        KualiDecimal invoiceItemUnitPrice = customerInvoiceDetail.getInvoiceItemUnitPrice();
        if (ObjectUtils.isNull(invoiceItemUnitPrice) || invoiceItemUnitPrice.equals(KualiDecimal.ZERO))
            invoiceOpenItemQuantity = KualiDecimal.ZERO;
        else {
            KualiDecimal invoiceOpenItemAmount = customerCreditMemoDetail.getInvoiceOpenItemAmount();
            invoiceOpenItemQuantity = invoiceOpenItemAmount.divide(invoiceItemUnitPrice);
        }
        return invoiceOpenItemQuantity;
    }
    
    /**
     * do all the calculations before the document gets saved
     * gets called for 'Submit', 'Save', and 'Blanket Approved'
     * @see org.kuali.core.document.Document#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    public void prepareForSave(KualiDocumentEvent event) {
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)event.getDocument();
        CustomerCreditMemoDocumentService customerCreditMemoDocumentService = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
        if (event instanceof BlanketApproveDocumentEvent)
            customerCreditMemoDocumentService.recalculateCustomerCreditMemoDocument(customerCreditMemoDocument,true);
        else
            customerCreditMemoDocumentService.recalculateCustomerCreditMemoDocument(customerCreditMemoDocument,false);
        
        // generate GLPEs
        if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("general ledger GLPE generation failed");
        }
        super.prepareForSave(event);  
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        return success;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#clearAnyGeneralLedgerPendingEntries()
     */
    public void clearAnyGeneralLedgerPendingEntries() {
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPostables()
     */
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        List<GeneralLedgerPendingEntrySourceDetail> generalLedgerPendingEntrySourceDetails = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        if (creditMemoDetails != null) {
            Iterator iter = creditMemoDetails.iterator();
            CustomerCreditMemoDetail customerCreditMemoDetail;
            KualiDecimal amount; 
            while (iter.hasNext()) {
                customerCreditMemoDetail = (CustomerCreditMemoDetail)iter.next();
                amount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
                
                // get only non empty credit memo details to generate GLPEs
                if (ObjectUtils.isNotNull(amount) && amount.isGreaterThan(KualiDecimal.ZERO))
                    generalLedgerPendingEntrySourceDetails.add((GeneralLedgerPendingEntrySourceDetail)customerCreditMemoDetail);
            }
        }
        return generalLedgerPendingEntrySourceDetails;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#addPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    public void addPendingEntry(GeneralLedgerPendingEntry entry) {
        generalLedgerPendingEntries.add(entry);
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount();
    }

    /**
     * Returns the financial document type code for the given document, using the DocumentTypeService
     * @return the financial document type code for the given document
     */
    public String getFinancialDocumentTypeCode() {
        return SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(this.getClass());
    }

    /**
     * This method creates the following GLPE's for the customer credit memo
     *
     * 1. Credit to receivable object for total line amount (excluding sales tax)
     * 2. Debit to income object for total line amount (excluding sales tax)
     * 3. Credit to receivable object in sales tax account(if sales tax exists)
     * 4. Debit to liability object in sales tax account(if sales tax exists)
     * 5. Credit to receivable object in district tax account(if district tax exists)
     * 6. Debit to liability object code in district tax account(if district tax exists)
     *
     * @see org.kuali.kfs.service.impl.GenericGeneralLedgerPendingEntryGenerationProcessImpl#processGenerateGeneralLedgerPendingEntries(org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */

    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean hasClaimOnCashOffset = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        
        boolean hasStateSalesTax = false;
        boolean hasDistrictSalesTax = false;
        
        addReceivableGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset);
        sequenceHelper.increment();
        addIncomeGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset);
        
        if( hasStateSalesTax ){
            sequenceHelper.increment();
            addStateSalesTaxGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset);
        }
        
        if( hasDistrictSalesTax ){
            sequenceHelper.increment();
            addDistrictSalesTaxGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset);
        }

        return true;
    }
    
    /**
     * This method creates the receivable GLPEs for credit memo detail lines.
     *
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void addReceivableGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset) {

        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;
        CustomerInvoiceDetail customerInvoiceDetail = customerCreditMemoDetail.getCustomerInvoiceDetail();
        ReceivableCustomerInvoiceDetail receivableCustomerInvoiceDetail = new ReceivableCustomerInvoiceDetail(customerInvoiceDetail, this.getInvoice());
        boolean isDebit = false;       
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createAndAddGenericInvoiceRelatedGLPEs(this, receivableCustomerInvoiceDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, customerCreditMemoDetail.getCreditMemoItemTotalAmount());
    }
    
    /**
     * This method adds pending entry with transaction ledger entry amount set to item price * quantity
     *
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void addIncomeGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset) {

        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;        
        boolean isDebit = true;
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createAndAddGenericInvoiceRelatedGLPEs(this, customerCreditMemoDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, customerCreditMemoDetail.getCreditMemoItemTotalAmount());
    }
    
    protected void addStateSalesTaxGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset){
        /*
        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;   
        boolean isDebit = false;
        KualiDecimal creditMemoDetailStateTaxAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount().multiply(getStateTaxPercent());
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createStateSalesTaxGLPEs(this, customerCreditMemoDetail, sequenceHelper, isDebit, hasOffset, creditMemoDetailStateTaxAmount);
        //Add state sales tax receivable too
        */
    }
    
    protected void addDistrictSalesTaxGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset){
        /*
        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;        
        boolean isDebit = true;
        KualiDecimal creditMemoDetailDistrictTaxAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount().multiply(getDistrictTaxPercent());
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createDistrictSalesTaxGLPEs(this, customerCreditMemoDetail, sequenceHelper, isDebit, hasOffset, creditMemoDetailDistrictTaxAmount);
        //Add district sales tax receivable too
        */
    } 

}
