package org.kuali.kfs.module.ar.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class represents a customer invoice detail on the customer invoice document. This class extends SourceAccountingLine since
 * each customer invoice detail has associated accounting line information.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerInvoiceDetail extends SourceAccountingLine implements AppliedPayment {

    // private Integer invoiceItemNumber; using SourceAccountingLine.sequenceNumber
    private BigDecimal invoiceItemQuantity;
    private BigDecimal invoiceItemUnitPrice;
    // private KualiDecimal invoiceItemTotalAmount; using SourceAccountingLine.amount for now
    private Date invoiceItemServiceDate;
    private String invoiceItemCode;
    private String invoiceItemDescription;
    private String accountsReceivableObjectCode;
    private String accountsReceivableSubObjectCode;
    private KualiDecimal invoiceItemTaxAmount;
    private boolean taxableIndicator;
    private boolean isDebit;
    private Integer invoiceItemDiscountLineNumber;

    private String invoiceItemUnitOfMeasureCode;
    private UnitOfMeasure unitOfMeasure;

    private SubObjectCode accountsReceivableSubObject;
    private ObjectCode accountsReceivableObject;

    private CustomerInvoiceDocument customerInvoiceDocument;
    private CustomerInvoiceDetail parentDiscountCustomerInvoiceDetail;
    private CustomerInvoiceDetail discountCustomerInvoiceDetail;

    // fields used for CustomerInvoiceWriteoffDocument
    private KualiDecimal writeoffAmount;
    private String customerInvoiceWriteoffDocumentNumber;

    // field for check box control
    private boolean fullApply;

    // This bit with the special applied amount and setAppliedAmount/getSpecialAppliedAmount
    // is a bit of a hack. It let's the form send information back to the action
    // and get handled properly. It can properly take the payment application document into
    // context and figure out amounts properly.
    // {
    transient private PaymentApplicationDocument currentPaymentApplicationDocument;
    public void setCurrentPaymentApplicationDocument(PaymentApplicationDocument paymentApplicationDocument) { currentPaymentApplicationDocument = paymentApplicationDocument; }
    public PaymentApplicationDocument getCurrentPaymentApplicationDocument() { return currentPaymentApplicationDocument; }
    transient private KualiDecimal specialAppliedAmount;
    public void setAmountApplied(KualiDecimal a) { specialAppliedAmount = a; }
    /**
     * @deprecated
     * @param amount
     */
    public void setAmountToApply(KualiDecimal amount) { }
    /**
     * @see org.kuali.kfs.module.ar.businessobject.AppliedPayment#getAmountToApply()
     */
    public KualiDecimal getAmountToApply() { return getAmountAppliedBy(currentPaymentApplicationDocument); }
    public KualiDecimal getSpecialAppliedAmount() { return specialAppliedAmount; }
    
    // }
    
    /**
     * Default constructor.
     */
    public CustomerInvoiceDetail() {
        super();
        invoiceItemTaxAmount = KualiDecimal.ZERO;
    }
    
    // ---- BEGIN OPEN AMOUNTS
    
    public KualiDecimal getAmountOpen() { 
        return getAmountOpenFromDatabase();
    }
    
    /**
     * This method returns an open amount for a specific detail. If a customer 
     * invoice detail is a discount line, return null, because discount lines 
     * should NEVER have an open amount.
     * 
     * @return
     */
    public KualiDecimal getAmountOpenFromDatabase() {
        KualiDecimal amount = getAmount();
        KualiDecimal applied = getAmountAppliedFromDatabase();
        KualiDecimal a = amount.subtract(applied);
        CustomerInvoiceDetail discount = getDiscountCustomerInvoiceDetail();
        return a;
    }

    public KualiDecimal getAmountOpenExcludingAnyAmountFromCurrentPaymentApplicationDocument() {
        return getAmountOpenExcludingAnyAmountFrom(getCurrentPaymentApplicationDocument());
    }
    
    public KualiDecimal getAmountOpenExcludingAnyAmountFrom(PaymentApplicationDocument paymentApplicationDocument) {
        return getAmount().subtract(getAmountAppliedExcludingAnyAmountAppliedBy(paymentApplicationDocument));
    }
    
    public KualiDecimal getAmountOpenPerCurrentPaymentApplicationDocument() {
        return getAmount().subtract(getAmountAppliedByCurrentPaymentApplicationDocument());
    }
    
    /**
     * This method returns the amount that remained unapplied on a given date.
     * 
     * @param date
     * @return
     */
    public KualiDecimal getAmountOpenByDateFromDatabase(java.sql.Date date) {
        return getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(date, null);
    }
    
    public KualiDecimal getAmountOpenByDateFromDatabase(java.util.Date date) {
        return getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(new java.sql.Date(date.getTime()),null);
    }
    
    public KualiDecimal getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(java.sql.Date date, PaymentApplicationDocument paymentApplicationDocument) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        
        // Lookup all applied payments as of the given date
        Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("invoiceItemNumber", getSequenceNumber());
        criteria.put("financialDocumentReferenceInvoiceNumber", getDocumentNumber());
        criteria.put("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        Collection<InvoicePaidApplied> invoicePaidAppliedsAsOfDate = businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
        
        KualiDecimal totalAppliedAmount = new KualiDecimal(0);
        KualiDecimal appliedAmount = new KualiDecimal(0);
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidAppliedsAsOfDate) {
            appliedAmount = invoicePaidApplied.getInvoiceItemAppliedAmount();
            Date invoicePaidDate = invoicePaidApplied.getDocumentHeader().getDocumentFinalDate();
            // get the paid date and use that to limit the adds from below
            if (ObjectUtils.isNotNull(appliedAmount)&&!invoicePaidDate.after(date)) {
                if(null != paymentApplicationDocument) {
                    if(!invoicePaidApplied.getDocumentNumber().equals(paymentApplicationDocument.getDocumentNumber())) {
                        totalAppliedAmount = totalAppliedAmount.add(appliedAmount);
                    }
                } else {
                    totalAppliedAmount = totalAppliedAmount.add(appliedAmount);
                }
            }
        }
        
        return getAmount().subtract(totalAppliedAmount);
    }
    
    public KualiDecimal getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(java.util.Date date, PaymentApplicationDocument paymentApplicationDocument) {
        return getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(new java.sql.Date(date.getTime()),paymentApplicationDocument);
    }
    
    // ---- END OPEN AMOUNTS
    
    // ---- BEGIN APPLIED AMOUNTS
    /**
     * @deprecated
     */
    public KualiDecimal getAmountApplied() { 
        return getAmountAppliedByCurrentPaymentApplicationDocument();
    }
    
    /**
     * @return the applied amount by getting it from the matching invoice paid applied
     */
    public KualiDecimal getAmountAppliedFromDatabase() {
        return getAmountAppliedBy(null);
    }
    
    /**
     * @param paymentApplicationDocument
     * @return 
     */
    public KualiDecimal getAmountAppliedBy(PaymentApplicationDocument paymentApplicationDocument) {
        List<InvoicePaidApplied> invoicePaidApplieds = null;
        if(null == paymentApplicationDocument) {
            invoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingAnyDocumentFromDatabase();
        } else {
            invoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingPaymentApplicationDocumentNoDatabase(paymentApplicationDocument);
        }
        KualiDecimal appliedAmount = new KualiDecimal(0);
        for(InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        return appliedAmount;
    }
    
    /**
     * This method is a convenience method used from the Struts form on the payment application document screen.
     * @return
     */
    public KualiDecimal getAmountAppliedByCurrentPaymentApplicationDocument() {
        return getAmountAppliedBy(getCurrentPaymentApplicationDocument());
    }
    
    /**
     * @param paymentApplicationDocument
     * @return the sum of applied amounts according to the database, excluding any amounts applied by paymentApplicationDocument
     */
    public KualiDecimal getAmountAppliedExcludingAnyAmountAppliedBy(PaymentApplicationDocument paymentApplicationDocument) {
        List<InvoicePaidApplied> invoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingAnyDocumentFromDatabase();
        KualiDecimal appliedAmount = new KualiDecimal(0);
        for(InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            // Exclude any amounts applied by paymentApplicationDocument
            if(null == paymentApplicationDocument || !paymentApplicationDocument.getDocumentNumber().equals(invoicePaidApplied.getDocumentNumber())) {
                appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
            }
        }
        return appliedAmount;
    }
    
    // ---- END APPLIED AMOUNTS
    
    /**
     * If the detail is a discount return the amount negated. Otherwise, return the remaining balance (i.e. for writeoffs)
     * 
     * @see org.kuali.kfs.module.ar.businessobject.AppliedPayment#getAmountToApply()
     */
    public KualiDecimal getAmountToApplyFromDatabase() {
        if (isDiscountLine()) {
            return getAmount().negated();
        } else {
            return getAmountOpenFromDatabase();
        }
    }

    /**
     * This method returns the writeoff amount. If writeoff document hasn't been approved yet, display the open amount. Else display
     * the amount applied from the specific approved writeoff document.
     * 
     * @param customerInvoiceWriteoffDocumentNumber
     * @return
     */
    public KualiDecimal getWriteoffAmount() {
        if (SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class).isCustomerInvoiceWriteoffDocumentApproved(customerInvoiceWriteoffDocumentNumber)) {
            return super.getAmount(); // using the accounting line amount ... see comments at top of class
        }
        else {
            return getAmountOpenFromDatabase();
        }
    }
    
    /**
     * This method returns the invoice pre tax amount
     * 
     * @return
     */
    public KualiDecimal getInvoiceItemPreTaxAmount() {
        if (ObjectUtils.isNotNull(invoiceItemUnitPrice) && ObjectUtils.isNotNull(invoiceItemQuantity)) {
            return new KualiDecimal(invoiceItemUnitPrice.multiply(invoiceItemQuantity));
        } else {
            return KualiDecimal.ZERO;
        }
    }

    /**
     * Gets the accountsReceivableObjectCode attribute.
     * 
     * @return Returns the accountsReceivableObjectCode
     */
    public String getAccountsReceivableObjectCode() {
        return accountsReceivableObjectCode;
    }

    /**
     * Sets the accountsReceivableObjectCode attribute.
     * 
     * @param accountsReceivableObjectCode The accountsReceivableObjectCode to set.
     */
    public void setAccountsReceivableObjectCode(String accountsReceivableObjectCode) {
        this.accountsReceivableObjectCode = accountsReceivableObjectCode;
    }

    /**
     * Gets the accountsReceivableSubObjectCode attribute.
     * 
     * @return Returns the accountsReceivableSubObjectCode
     */
    public String getAccountsReceivableSubObjectCode() {
        return accountsReceivableSubObjectCode;
    }

    /**
     * Sets the accountsReceivableSubObjectCode attribute.
     * 
     * @param accountsReceivableSubObjectCode The accountsReceivableSubObjectCode to set.
     */
    public void setAccountsReceivableSubObjectCode(String accountsReceivableSubObjectCode) {
        this.accountsReceivableSubObjectCode = accountsReceivableSubObjectCode;
    }

    /**
     * Gets the invoiceItemQuantity attribute.
     * 
     * @return Returns the invoiceItemQuantity
     */
    public BigDecimal getInvoiceItemQuantity() {
        return invoiceItemQuantity;
    }

    /**
     * Sets the invoiceItemQuantity attribute.
     * 
     * @param invoiceItemQuantity The invoiceItemQuantity to set.
     */
    public void setInvoiceItemQuantity(BigDecimal invoiceItemQuantity) {
        this.invoiceItemQuantity = invoiceItemQuantity;
    }

    /**
     * Gets the invoiceItemUnitOfMeasureCode attribute.
     * 
     * @return Returns the invoiceItemUnitOfMeasureCode
     */
    public String getInvoiceItemUnitOfMeasureCode() {
        return invoiceItemUnitOfMeasureCode;
    }

    /**
     * Sets the invoiceItemUnitOfMeasureCode attribute.
     * 
     * @param invoiceItemUnitOfMeasureCode The invoiceItemUnitOfMeasureCode to set.
     */
    public void setInvoiceItemUnitOfMeasureCode(String invoiceItemUnitOfMeasureCode) {
        this.invoiceItemUnitOfMeasureCode = invoiceItemUnitOfMeasureCode;
    }

    /**
     * Gets the invoiceItemUnitPrice attribute.
     * 
     * @return Returns the invoiceItemUnitPrice
     */
    public BigDecimal getInvoiceItemUnitPrice() {
        return invoiceItemUnitPrice;
    }

    /**
     * @param invoiceItemUnitPrice
     */
    public void setInvoiceItemUnitPrice(KualiDecimal invoiceItemUnitPrice) {
        if (ObjectUtils.isNotNull(invoiceItemUnitPrice)) {
            this.invoiceItemUnitPrice = invoiceItemUnitPrice.bigDecimalValue();
        }
        else {
            this.invoiceItemUnitPrice = BigDecimal.ZERO;
        }
    }

    /**
     * Sets the invoiceItemUnitPrice attribute.
     * 
     * @param invoiceItemUnitPrice The invoiceItemUnitPrice to set.
     */
    public void setInvoiceItemUnitPrice(BigDecimal invoiceItemUnitPrice) {
        this.invoiceItemUnitPrice = invoiceItemUnitPrice;
    }

    /**
     * Gets the invoiceItemServiceDate attribute.
     * 
     * @return Returns the invoiceItemServiceDate
     */
    public Date getInvoiceItemServiceDate() {
        return invoiceItemServiceDate;
    }

    /**
     * Sets the invoiceItemServiceDate attribute.
     * 
     * @param invoiceItemServiceDate The invoiceItemServiceDate to set.
     */
    public void setInvoiceItemServiceDate(Date invoiceItemServiceDate) {
        this.invoiceItemServiceDate = invoiceItemServiceDate;
    }

    /**
     * Gets the invoiceItemCode attribute.
     * 
     * @return Returns the invoiceItemCode
     */
    public String getInvoiceItemCode() {
        return invoiceItemCode;
    }

    /**
     * Sets the invoiceItemCode attribute.
     * 
     * @param invoiceItemCode The invoiceItemCode to set.
     */
    public void setInvoiceItemCode(String invoiceItemCode) {
        this.invoiceItemCode = invoiceItemCode;
    }

    /**
     * Gets the invoiceItemDescription attribute.
     * 
     * @return Returns the invoiceItemDescription
     */
    public String getInvoiceItemDescription() {
        return invoiceItemDescription;
    }

    /**
     * Sets the invoiceItemDescription attribute.
     * 
     * @param invoiceItemDescription The invoiceItemDescription to set.
     */
    public void setInvoiceItemDescription(String invoiceItemDescription) {
        this.invoiceItemDescription = invoiceItemDescription;
    }

    /**
     * Gets the invoiceItemTaxAmount attribute. TODO Use tax service to get invoice item tax amount
     * 
     * @return Returns the invoiceItemTaxAmount.
     */
    public KualiDecimal getInvoiceItemTaxAmount() {
        return invoiceItemTaxAmount;
    }

    /**
     * Sets the invoiceItemTaxAmount attribute value.
     * 
     * @param invoiceItemTaxAmount The invoiceItemTaxAmount to set.
     */
    public void setInvoiceItemTaxAmount(KualiDecimal invoiceItemTaxAmount) {
        this.invoiceItemTaxAmount = invoiceItemTaxAmount;
    }

    /**
     * Gets the invoiceItemDiscountLineNumber attribute.
     * 
     * @return Returns the invoiceItemDiscountLineNumber.
     */
    public Integer getInvoiceItemDiscountLineNumber() {
        return invoiceItemDiscountLineNumber;
    }

    /**
     * Sets the invoiceItemDiscountLineNumber attribute value.
     * 
     * @param invoiceItemDiscountLineNumber The invoiceItemDiscountLineNumber to set.
     */
    public void setInvoiceItemDiscountLineNumber(Integer invoiceItemDiscountLineNumber) {
        this.invoiceItemDiscountLineNumber = invoiceItemDiscountLineNumber;
    }

    /**
     * Gets the accountsReceivableSubObject attribute.
     * 
     * @return Returns the accountsReceivableSubObject
     */
    public SubObjectCode getAccountsReceivableSubObject() {
        return accountsReceivableSubObject;
    }

    /**
     * Sets the accountsReceivableSubObject attribute.
     * 
     * @param accountsReceivableSubObject The accountsReceivableSubObject to set.
     * @deprecated
     */
    public void setAccountsReceivableSubObject(SubObjectCode accountsReceivableSubObject) {
        this.accountsReceivableSubObject = accountsReceivableSubObject;
    }

    /**
     * Gets the accountsReceivableObject attribute.
     * 
     * @return Returns the accountsReceivableObject
     */
    public ObjectCode getAccountsReceivableObject() {
        return accountsReceivableObject;
    }

    /**
     * Sets the accountsReceivableObject attribute.
     * 
     * @param accountsReceivableObject The accountsReceivableObject to set.
     * @deprecated
     */
    public void setAccountsReceivableObject(ObjectCode accountsReceivableObject) {
        this.accountsReceivableObject = accountsReceivableObject;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", getDocumentNumber());
        if (this.getSequenceNumber() != null) {
            m.put("invoiceItemNumber", this.getSequenceNumber().toString());
        }
        return m;
    }

    /**
     * Update line amount based on quantity and unit price
     */
    public void updateAmountBasedOnQuantityAndUnitPrice() {
        setAmount(getInvoiceItemPreTaxAmount());
    }

    public boolean isTaxableIndicator() {
        return taxableIndicator;
    }

    // yes this is redundant, its required for the JSP on the accounting
    // line checkbox field
    public boolean getTaxableIndicator() {
        return taxableIndicator;
    }

    public void setTaxableIndicator(boolean taxableIndicator) {
        this.taxableIndicator = taxableIndicator;
    }

    public boolean isDebit() {
        return isDebit;
    }

    public void setDebit(boolean isDebit) {
        this.isDebit = isDebit;
    }

    /**
     * This method returns true if customer invoice detail has a corresponding discount line
     * 
     * @return
     */
    public boolean isDiscountLineParent() {
        return ObjectUtils.isNotNull(getInvoiceItemDiscountLineNumber());
    }

    /**
     * This method should only be used to determine if detail is discount line in JSP. If you want to determine if invoice detail is
     * a detail line use CustomerInvoiceDocument.isDiscountLineBasedOnSequenceNumber() instead.
     * 
     * @return
     */
    public boolean isDiscountLine() {
        return ObjectUtils.isNotNull(parentDiscountCustomerInvoiceDetail);
    }

    /**
     * This method sets the amount to negative if it isn't already negative
     * 
     * @return
     */
    public void setInvoiceItemUnitPriceToNegative() {
        // if unit price is positive
        if (invoiceItemUnitPrice.compareTo(BigDecimal.ZERO) == 1) {
            invoiceItemUnitPrice = invoiceItemUnitPrice.negate();
        }
    }

    public CustomerInvoiceDetail getParentDiscountCustomerInvoiceDetail() {
        return parentDiscountCustomerInvoiceDetail;
    }

    public void setParentDiscountCustomerInvoiceDetail(CustomerInvoiceDetail parentDiscountCustomerInvoiceDetail) {
        this.parentDiscountCustomerInvoiceDetail = parentDiscountCustomerInvoiceDetail;
    }

    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetail() {
        return discountCustomerInvoiceDetail;
    }

    public void setDiscountCustomerInvoiceDetail(CustomerInvoiceDetail discountCustomerInvoiceDetail) {
        this.discountCustomerInvoiceDetail = discountCustomerInvoiceDetail;
    }

    // ---- Methods to find matching InvoicePaidApplieds.
    
    /**
     * @return matching InvoicePaidApplieds from the database if they exist
     */
    public List<InvoicePaidApplied> getMatchingInvoicePaidAppliedsMatchingAnyDocumentFromDatabase() {
        return getMatchingInvoicePaidApplieds(null);
    }
    
    /**
     * @param paymentApplicationDocumentNumber
     * @return matching InvoicePaidApplieds from the database matching the specific PaymentApplicationDocument if they exist
     */
    public List<InvoicePaidApplied> getMatchingInvoicePaidAppliedsMatchingASpecificPaymentApplicationDocumentFromDatabase(String paymentApplicationDocumentNumber) {
        return getMatchingInvoicePaidApplieds(paymentApplicationDocumentNumber);
    }

    /**
     * @param paymentApplicationDocumentNumber
     * @return
     */
    public InvoicePaidApplied getSingleMatchingInvoicePaidAppliedMatchingASpecificPaymentApplicationDocumentFromDatabase(String paymentApplicationDocumentNumber) {
        List<InvoicePaidApplied> matchingInvoicePaidApplieds = getMatchingInvoicePaidApplieds(paymentApplicationDocumentNumber);
        return matchingInvoicePaidApplieds.iterator().next();
    }
    
    /**
     * @param paymentApplicationDocumentNumber
     * @return the List of matching InvoicePaidApplieds.
     * If paymentApplicationDocumentNumber is null invoicePaidApplieds matching any PaymentApplicationDocument will be returned.
     * If paymentApplicationDocumentNumber is not null only the invoicePaidApplieds that match on that PaymentApplicationDocument will be returned.
     */
    private List<InvoicePaidApplied> getMatchingInvoicePaidApplieds(String paymentApplicationDocumentNumber) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Map<String,Object> criteria = new HashMap<String,Object>();
        if(null != paymentApplicationDocumentNumber) {
            criteria.put("documentNumber", paymentApplicationDocumentNumber);
            criteria.put("invoiceItemNumber", getSequenceNumber());
            criteria.put("financialDocumentReferenceInvoiceNumber", getDocumentNumber());
        }
        else {
            criteria.put("invoiceItemNumber", getSequenceNumber());
            criteria.put("financialDocumentReferenceInvoiceNumber", getDocumentNumber());
            criteria.put("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        }
        List<InvoicePaidApplied> invoicePaidApplieds = (List<InvoicePaidApplied>) businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
        if(ObjectUtils.isNull(invoicePaidApplieds)) {
            invoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
        }
        return invoicePaidApplieds;
    }
    
    /**
     * Get InvoicePaidApplieds related to this CustomerInvoiceDetail if they 
     * exist in a PaymentApplicationDocument and do it just by looking at the
     * PaymentApplicationDocument as it is in memory. Don't get anything from
     * the database.
     * 
     * @param paymentApplicationDocument
     * @return
     */
    public List<InvoicePaidApplied> getMatchingInvoicePaidAppliedsMatchingPaymentApplicationDocumentNoDatabase(PaymentApplicationDocument paymentApplicationDocument) {
        List<InvoicePaidApplied> invoicePaidApplieds = paymentApplicationDocument.getInvoicePaidApplieds();
        List<InvoicePaidApplied> selectedInvoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
        // The paymentApplicationDocumentService isn't used to pull anything from the database.
        // It's just used to try to pair CustomerInvoiceDetails and InvoicePaidApplieds
        PaymentApplicationDocumentService paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        for(InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            if(paymentApplicationDocumentService.customerInvoiceDetailPairsWithInvoicePaidApplied(this, invoicePaidApplied)) {
                selectedInvoicePaidApplieds.add(invoicePaidApplied);
            }
        }
        return selectedInvoicePaidApplieds;
    }
    
    /**
     * This method returns the results of @link getMatchingInvoicePaidAppliedsMatchingPaymentApplicationDocumentNoDatabase(PaymentApplicationDocument)
     * as a single InvoicePaidApplied. This is OK because there's only one
     * InvoicePaidApplied for a CustomerInvoiceDetail on a given PaymentApplicationDocument.
     * 
     * @param paymentApplicationDocument
     * @return
     */
    public InvoicePaidApplied getSingleMatchingInvoicepaidAppliedMatchingPaymentApplicationDocumentNoDatabase(PaymentApplicationDocument paymentApplicationDocument) {
        List<InvoicePaidApplied> matchingInvoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingPaymentApplicationDocumentNoDatabase(paymentApplicationDocument);
        return matchingInvoicePaidApplieds.iterator().next();
    }

    // ---- Simple getters/setters
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument() throws WorkflowException {
        DocumentService documentService = (DocumentService) SpringContext.getBean(DocumentService.class);
        customerInvoiceDocument = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(getDocumentNumber());
        return customerInvoiceDocument;
    }
    public String getCustomerInvoiceWriteoffDocumentNumber() {
        return customerInvoiceWriteoffDocumentNumber;
    }

    public void setCustomerInvoiceWriteoffDocumentNumber(String customerInvoiceWriteoffDocumentNumber) {
        this.customerInvoiceWriteoffDocumentNumber = customerInvoiceWriteoffDocumentNumber;
    }

    public void setWriteoffAmount(KualiDecimal writeoffAmount) {
        this.writeoffAmount = writeoffAmount;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public boolean isFullApply() {
        return fullApply;
    }

    public void setFullApply(boolean fullApply) {
        this.fullApply = fullApply;
    }

    /**
     * If the detail is a discount customer invoice detail, return the parent customer invoice detail's sequence number instead
     * 
     * @see org.kuali.kfs.module.ar.businessobject.AppliedPayment#getInvoiceItemNumber()
     */
    public Integer getInvoiceItemNumber() {
        if (isDiscountLine()) {
            return parentDiscountCustomerInvoiceDetail.getSequenceNumber();
        } else {
            return this.getSequenceNumber();
        }
    }

    /**
     * If detail is part of an invoice that is a reversal, return the invoice that is being corrected. Else return the customer
     * details document number.
     * 
     * @see org.kuali.kfs.module.ar.businessobject.AppliedPayment#getInvoiceReferenceNumber()
     */
    public String getInvoiceReferenceNumber() {
        try {
            if (getCustomerInvoiceDocument().isInvoiceReversal()) {
                return getCustomerInvoiceDocument().getDocumentHeader().getFinancialDocumentInErrorNumber();
            } else {
                return getDocumentNumber();
            }
        } catch (WorkflowException we) {
            we.printStackTrace();
            return "";
        }
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#refresh()
     */
    public void refresh() {
        super.refresh();
        this.updateAmountBasedOnQuantityAndUnitPrice();
    }
    
}
