/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents a customer invoice detail on the customer invoice document. This class extends SourceAccountingLine since
 * each customer invoice detail has associated accounting line information.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerInvoiceDetail extends SourceAccountingLine implements AppliedPayment {
    private static Logger LOG = Logger.getLogger(CustomerInvoiceDetail.class);
    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "CustomerInvoiceDetail";

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

    private transient CustomerInvoiceDocument customerInvoiceDocument;
    private transient CustomerInvoiceDetail parentDiscountCustomerInvoiceDetail;
    private transient CustomerInvoiceDetail discountCustomerInvoiceDetail;

    // fields used for CustomerInvoiceWriteoffDocument
    private KualiDecimal writeoffAmount;
    private String customerInvoiceWriteoffDocumentNumber;

    /**
     * Default constructor.
     */
    public CustomerInvoiceDetail() {
        super();
        invoiceItemTaxAmount = KualiDecimal.ZERO;
    }
    
    // ---- BEGIN OPEN AMOUNTS
    
    public KualiDecimal getAmountOpen() { 
        
        //  if the parent isnt saved, or if its saved but not approved, we 
        // need to include the discounts.  If its both saved AND approved, 
        // we do not include the discounts.
        boolean includeDiscounts = !(isParentSaved() && isParentApproved());
        
        KualiDecimal amount = getAmount();
        KualiDecimal applied = getAmountApplied();
        KualiDecimal a = amount.subtract(applied);
        
        if (includeDiscounts) {
            CustomerInvoiceDetail discount = getDiscountCustomerInvoiceDetail();
            if (ObjectUtils.isNotNull(discount)) {
                a = a.add(discount.getAmount());
            }
        }
        return a;
    }
    
    private boolean isParentSaved() {
        return getCustomerInvoiceDocument() != null;
    }
    
    private boolean isParentApproved() {
        if (getCustomerInvoiceDocument() == null) {
            return false;
        }
        return KFSConstants.DocumentStatusCodes.APPROVED.equalsIgnoreCase(getCustomerInvoiceDocument().getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
    }
    
    //TODO Andrew
//    @Deprecated
//    private KualiDecimal getAmountOpenFromDatabaseNoDiscounts() {
//        KualiDecimal amount = getAmount();
//        KualiDecimal applied = getAmountAppliedFromDatabase();
//        KualiDecimal a = amount.subtract(applied);
//        return a;
//    }
//
//    @Deprecated
//    private KualiDecimal getAmountOpenFromDatabaseDiscounted() {
//        KualiDecimal amount = getAmount();
//        KualiDecimal applied = getAmountAppliedFromDatabase();
//        KualiDecimal a = amount.subtract(applied);
//        CustomerInvoiceDetail discount = getDiscountCustomerInvoiceDetail();
//        if (ObjectUtils.isNotNull(discount)) {
//            a = a.add(discount.getAmount());
//        }
//        return a;
//    }

    //TODO Andrew
    //public KualiDecimal getAmountOpenExcludingAnyAmountFromCurrentPaymentApplicationDocument() {
    //    return getAmountOpenExcludingAnyAmountFrom(getCurrentPaymentApplicationDocument());
    //}
    
    /**
     * 
     * Retrieves the discounted amount.  This is the amount minues any 
     * discounts that might exist.  If no discount exists, then it 
     * just returns the amount.
     * 
     * NOTE this does not subtract PaidApplieds, only discounts.
     * 
     * @return
     */
    //PAYAPP
    public KualiDecimal getAmountDiscounted() {
        KualiDecimal a = getAmount();
        CustomerInvoiceDetail discount = getDiscountCustomerInvoiceDetail();
        if(ObjectUtils.isNotNull(discount)) {
            KualiDecimal d = discount.getAmount();
            a = a.add(d);
        }
        return a;
    }
    
    //TODO Andrew
//    public KualiDecimal getAmountOpenExcludingAnyAmountFrom(PaymentApplicationDocument paymentApplicationDocument) {
//        return getAmountDiscounted().subtract(getAmountAppliedExcludingAnyAmountAppliedBy(paymentApplicationDocument));
//    }
//    
//    public KualiDecimal getAmountOpenPerCurrentPaymentApplicationDocument() {
//        return getAmountDiscounted().subtract(getAmountAppliedByCurrentPaymentApplicationDocument());
//    }
    
    /**
     * This method returns the amount that remained unapplied on a given date.
     * 
     * @param date
     * @return
     */
    public KualiDecimal getAmountOpenByDateFromDatabase(java.sql.Date date) {
        return getAmountOpen();
        //TODO Andrew - need to fix this to actually respect the dates
        //return getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(date, null);
    }
    
    public KualiDecimal getAmountOpenByDateFromDatabase(java.util.Date date) {
        return getAmountOpen();
        //TODO Andrew - need to fix this to actually respect the dates
        //return getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(new java.sql.Date(date.getTime()),null);
    }
    
    //TODO Andrew
//    public KualiDecimal getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(java.sql.Date date, PaymentApplicationDocument paymentApplicationDocument) {
//        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
//        
//        // Lookup all applied payments as of the given date
//        Map<String,Object> criteria = new HashMap<String,Object>();
//        criteria.put("invoiceItemNumber", getSequenceNumber());
//        criteria.put("financialDocumentReferenceInvoiceNumber", getDocumentNumber());
//        criteria.put("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
//        Collection<InvoicePaidApplied> invoicePaidAppliedsAsOfDate = businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
//        
//        KualiDecimal totalAppliedAmount = new KualiDecimal(0);
//        KualiDecimal appliedAmount = new KualiDecimal(0);
//        for (InvoicePaidApplied invoicePaidApplied : invoicePaidAppliedsAsOfDate) {
//            appliedAmount = invoicePaidApplied.getInvoiceItemAppliedAmount();
//            Date invoicePaidDate = invoicePaidApplied.getDocumentHeader().getDocumentFinalDate();
//            // get the paid date and use that to limit the adds from below
//            if (ObjectUtils.isNotNull(appliedAmount)&&!invoicePaidDate.after(date)) {
//                if(null != paymentApplicationDocument) {
//                    if(!invoicePaidApplied.getDocumentNumber().equals(paymentApplicationDocument.getDocumentNumber())) {
//                        totalAppliedAmount = totalAppliedAmount.add(appliedAmount);
//                    }
//                } else {
//                    totalAppliedAmount = totalAppliedAmount.add(appliedAmount);
//                }
//            }
//        }
//        
//        return getAmount().subtract(totalAppliedAmount);
//    }
//    
//    public KualiDecimal getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(java.util.Date date, PaymentApplicationDocument paymentApplicationDocument) {
//        return getAmountOpenByDateFromDatabaseExcludingAnyAmountAppliedByPaymentApplicationDocument(new java.sql.Date(date.getTime()),paymentApplicationDocument);
//    }
    
    // ---- END OPEN AMOUNTS
    
    // ---- BEGIN APPLIED AMOUNTS
    public KualiDecimal getAmountApplied() { 
        List<InvoicePaidApplied> invoicePaidApplieds = null;
        invoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingAnyDocumentFromDatabase();
        KualiDecimal appliedAmount = new KualiDecimal(0);
        for(InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        return appliedAmount;
    }
    
    //TODO Andrew
//    /**
//     * @return the applied amount by getting it from the matching invoice paid applied
//     */
//    public KualiDecimal getAmountAppliedFromDatabase() {
//        return getAmountAppliedBy(null);
//    }
//    
//  /**
//  * This method is a convenience method used from the Struts form on the payment application document screen.
//  * @return
//  */
// public KualiDecimal getAmountAppliedByCurrentPaymentApplicationDocument() {
//     return getAmountAppliedBy(getCurrentPaymentApplicationDocument());
// }
// 
    /**
     * @param paymentApplicationDocument
     * @return 
     */
    public KualiDecimal getAmountAppliedBy(String documentNumber) {
        List<InvoicePaidApplied> invoicePaidApplieds = null;
        if (StringUtils.isBlank(documentNumber)) {
            invoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingAnyDocumentFromDatabase();
        } else {
            invoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingDocument(documentNumber);
        }
        KualiDecimal appliedAmount = new KualiDecimal(0);
        for(InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        return appliedAmount;
    }
    
    /**
     * @param paymentApplicationDocument
     * @return the sum of applied amounts according to the database, excluding any amounts applied by paymentApplicationDocument
     */
    public KualiDecimal getAmountAppliedExcludingAnyAmountAppliedBy(String documentNumber) {
        List<InvoicePaidApplied> invoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingAnyDocumentFromDatabase();
        KualiDecimal appliedAmount = new KualiDecimal(0);
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            // Exclude any amounts applied by paymentApplicationDocument
            if (StringUtils.isNotBlank(documentNumber)) {
            }
            if (StringUtils.isBlank(documentNumber) || !documentNumber.equalsIgnoreCase(invoicePaidApplied.getDocumentNumber())) {
                appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
            }
        }
        return appliedAmount;
    }
    
    // ---- END APPLIED AMOUNTS
    
    /**
     * This method returns the writeoff amount. If writeoff document hasn't been approved yet, display the open amount. Else display
     * the amount applied from the specific approved writeoff document.
     * 
     * @param customerInvoiceWriteoffDocumentNumber
     * @return
     */
    public KualiDecimal getWriteoffAmount() {
        if (SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class).isCustomerInvoiceWriteoffDocumentApproved(customerInvoiceWriteoffDocumentNumber)) {
            //TODO this probably isnt right ... in the case of discounts and/or credit 
            //     memos, the getAmount() isnt the amount that the writeoff document will have 
            //     written off
            return super.getAmount(); // using the accounting line amount ... see comments at top of class
        }
        else {
            return getAmountOpen();
        }
    }
    
    /**
     * This method returns the invoice pre tax amount
     * 
     * @return
     */
    public KualiDecimal getInvoiceItemPreTaxAmount() {
        if (ObjectUtils.isNotNull(invoiceItemUnitPrice) && ObjectUtils.isNotNull(invoiceItemQuantity)) {
            BigDecimal bd = invoiceItemUnitPrice.multiply(invoiceItemQuantity);
            bd = bd.setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR);
            return new KualiDecimal(bd);
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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
        //TODO Andrew
        //return getMatchingInvoicePaidApplieds(null);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // assuming here that you never have a PaidApplied against a Discount line
        Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("invoiceItemNumber", getInvoiceItemNumber());
        criteria.put("financialDocumentReferenceInvoiceNumber", getDocumentNumber());
        criteria.put("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);

        List<InvoicePaidApplied> invoicePaidApplieds = (List<InvoicePaidApplied>) businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
        if(ObjectUtils.isNull(invoicePaidApplieds)) {
            invoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
        }
        return invoicePaidApplieds;
    }
    
    /**
     * @param paymentApplicationDocumentNumber
     * @return the List of matching InvoicePaidApplieds.
     * If paymentApplicationDocumentNumber is null invoicePaidApplieds matching any PaymentApplicationDocument will be returned.
     * If paymentApplicationDocumentNumber is not null only the invoicePaidApplieds that match on that PaymentApplicationDocument will be returned.
     */
    private List<InvoicePaidApplied> getMatchingInvoicePaidAppliedsMatchingDocument(String documentNumber) {
        if (StringUtils.isBlank(documentNumber)) {
            return getMatchingInvoicePaidAppliedsMatchingAnyDocumentFromDatabase();
        }
        
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("documentNumber", documentNumber);
        criteria.put("invoiceItemNumber", getSequenceNumber());
        criteria.put("financialDocumentReferenceInvoiceNumber", getDocumentNumber());

        List<InvoicePaidApplied> invoicePaidApplieds = (List<InvoicePaidApplied>) businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
        if(ObjectUtils.isNull(invoicePaidApplieds)) {
            invoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
        }
        return invoicePaidApplieds;
    }

    //TODO Andrew
//    /**
//     * @param paymentApplicationDocumentNumber
//     * @return matching InvoicePaidApplieds from the database matching the specific PaymentApplicationDocument if they exist
//     */
//    public List<InvoicePaidApplied> getMatchingInvoicePaidAppliedsMatchingASpecificPaymentApplicationDocumentFromDatabase(String paymentApplicationDocumentNumber) {
//        return getMatchingInvoicePaidApplieds(paymentApplicationDocumentNumber);
//    }
//
//    /**
//     * @param paymentApplicationDocumentNumber
//     * @return
//     */
//    public InvoicePaidApplied getSingleMatchingInvoicePaidAppliedMatchingASpecificPaymentApplicationDocumentFromDatabase(String paymentApplicationDocumentNumber) {
//        List<InvoicePaidApplied> matchingInvoicePaidApplieds = getMatchingInvoicePaidApplieds(paymentApplicationDocumentNumber);
//        return matchingInvoicePaidApplieds.iterator().next();
//    }
//    
//    
//    /**
//     * Get InvoicePaidApplieds related to this CustomerInvoiceDetail if they 
//     * exist in a PaymentApplicationDocument and do it just by looking at the
//     * PaymentApplicationDocument as it is in memory. Don't get anything from
//     * the database.
//     * 
//     * @param paymentApplicationDocument
//     * @return
//     */
//    public List<InvoicePaidApplied> getMatchingInvoicePaidAppliedsMatchingPaymentApplicationDocumentNoDatabase(PaymentApplicationDocument paymentApplicationDocument) {
//        List<InvoicePaidApplied> invoicePaidApplieds = paymentApplicationDocument.getInvoicePaidApplieds();
//        List<InvoicePaidApplied> selectedInvoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
//        // The paymentApplicationDocumentService isn't used to pull anything from the database.
//        // It's just used to try to pair CustomerInvoiceDetails and InvoicePaidApplieds
//        PaymentApplicationDocumentService paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
//        for(InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
//            if(paymentApplicationDocumentService.customerInvoiceDetailPairsWithInvoicePaidApplied(this, invoicePaidApplied)) {
//                selectedInvoicePaidApplieds.add(invoicePaidApplied);
//            }
//        }
//        return selectedInvoicePaidApplieds;
//    }
//    
//    /**
//     * This method returns the results of @link getMatchingInvoicePaidAppliedsMatchingPaymentApplicationDocumentNoDatabase(PaymentApplicationDocument)
//     * as a single InvoicePaidApplied. This is OK because there's only one
//     * InvoicePaidApplied for a CustomerInvoiceDetail on a given PaymentApplicationDocument.
//     * 
//     * @param paymentApplicationDocument
//     * @return
//     */
//    public InvoicePaidApplied getSingleMatchingInvoicepaidAppliedMatchingPaymentApplicationDocumentNoDatabase(PaymentApplicationDocument paymentApplicationDocument) {
//        List<InvoicePaidApplied> matchingInvoicePaidApplieds = getMatchingInvoicePaidAppliedsMatchingPaymentApplicationDocumentNoDatabase(paymentApplicationDocument);
//        return matchingInvoicePaidApplieds.iterator().next();
//    }

    // ---- Simple getters/setters
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        if (customerInvoiceDocument == null) {
            DocumentService documentService = (DocumentService) SpringContext.getBean(DocumentService.class);
            try {
                customerInvoiceDocument = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(getDocumentNumber());
            }
            catch (WorkflowException e) {
                throw new RuntimeException("A WorkflowException was thrown when trying to open the details parent document.  This should never happen.", e);
            }
        }
        return customerInvoiceDocument;
    }
    
    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
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

    //TODO Andrew
//    public boolean isFullApply() {
//        return fullApply;
//    }
//
//    public void setFullApply(boolean fullApply) {
//        this.fullApply = fullApply;
//    }

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
        return getDocumentNumber();
    }
    
    /**
     * 
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#refresh()
     */
    public void refresh() {
        super.refresh();
        this.updateAmountBasedOnQuantityAndUnitPrice();
    }
    
}
