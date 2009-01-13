/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.ItemFields;
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.authorization.PaymentRequestDocumentActionAuthorizer;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Business rule(s) applicable to Payment Request documents.
 */
public class PaymentRequestDocumentRule extends AccountsPayableDocumentRuleBase {

    private static KualiDecimal zero = KualiDecimal.ZERO;
    private static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    
    /** Map for allowed federal and state tax rates based on income class. */
    private static HashMap<String, ArrayList<BigDecimal>> federalTaxRates;
    private static HashMap<String, ArrayList<BigDecimal>> stateTaxRates;

    // set up the tax rate maps
    //TODO these rates shall be kept in DB tables or as parameter
    static {
        federalTaxRates = new HashMap<String, ArrayList<BigDecimal>>();
        stateTaxRates = new HashMap<String, ArrayList<BigDecimal>>();
        
        ArrayList<BigDecimal> fedrates = new ArrayList<BigDecimal>();
        fedrates.add(new BigDecimal(30));
        fedrates.add(new BigDecimal(14));
        fedrates.add(new BigDecimal(0));
        federalTaxRates.put("F", fedrates);

        fedrates = new ArrayList<BigDecimal>();
        fedrates.add(new BigDecimal(30));
        fedrates.add(new BigDecimal(15));
        fedrates.add(new BigDecimal(10));
        fedrates.add(new BigDecimal(5));
        fedrates.add(new BigDecimal(0));
        federalTaxRates.put("R", fedrates);

        fedrates = new ArrayList<BigDecimal>();
        fedrates.add(new BigDecimal(30));
        fedrates.add(new BigDecimal(0));
        federalTaxRates.put("I", fedrates);
        federalTaxRates.put("A", fedrates);
        federalTaxRates.put("O", fedrates);

        ArrayList<BigDecimal> strates = new ArrayList<BigDecimal>();
        strates.add(new BigDecimal(3.4));
        strates.add(new BigDecimal(0));
        stateTaxRates.put("F", strates);
        stateTaxRates.put("A", strates);
        stateTaxRates.put("O", strates);

        strates = new ArrayList<BigDecimal>();
        strates.add(new BigDecimal(0));
        stateTaxRates.put("I", strates);
        stateTaxRates.put("R", strates);
    }
    
    /**
     * Returns true if full document entry is complete, bypassing further rules.
     * 
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        PaymentRequestDocument preq = (PaymentRequestDocument) financialDocument;
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(preq)) {
            return true;
        }
        return super.processCustomAddAccountingLineBusinessRules(financialDocument, accountingLine);
    }

    /**
     * Overriding the accessibility of the accounts only in the case where awaiting ap review, that is because the super
     * checks enroute and checks if it is the owner while we allow "full entry" until past this stage
     *
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#accountIsAccessible(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected boolean accountIsAccessible(AccountingDocument financialDocument, AccountingLine accountingLine) {
        PaymentRequestDocument preq = (PaymentRequestDocument) financialDocument;
        // We are overriding the accessibility of the accounts only in the case where awaiting ap review, that is because the super
        // checks enroute
        // and checks if it is the owner while we allow "full entry" until past this stage
        if (StringUtils.equals(PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW, preq.getStatusCode())) {
            return true;
        }
        else {
            return super.accountIsAccessible(financialDocument, accountingLine);
        }
    }

    /**
     * Tabs included on Payment Request Documents are: Invoice
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        PaymentRequestDocument preqDocument = (PaymentRequestDocument) purapDocument;
        valid &= processInvoiceValidation(preqDocument);
        if (!SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(purapDocument)) {
            valid &= processPurchaseOrderIDValidation(preqDocument);
        }
        valid &= processPaymentRequestDateValidationForContinue(preqDocument);
        valid &= processVendorValidation(preqDocument);
        valid &= validatePaymentRequestDates(preqDocument);
        return valid;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        isValid &= validateRouteFiscal(paymentRequestDocument);
        isValid &= processValidation(paymentRequestDocument);
        isValid &= checkNegativeAccounts(paymentRequestDocument);
        return isValid;
    }
    
    /**
     * This is for EInvoice
     */
    public boolean processRouteDocumentBusinessRules(Document document) {
        return processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * Ensures source accounting lines amounts are not negative.
     * 
     * @param paymentRequestDocument - payment request document to validate
     * @return
     */
    private boolean checkNegativeAccounts(PaymentRequestDocument paymentRequestDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        // if this was set somewhere on the doc(for later use) in prepare for save we could avoid this call
        List<SourceAccountingLine> sourceLines = SpringContext.getBean(PurapAccountingService.class).generateSummary(paymentRequestDocument.getItems());

        for (SourceAccountingLine sourceAccountingLine : sourceLines) {
            if (sourceAccountingLine.getAmount().isNegative()) {
                String accountString = sourceAccountingLine.getChartOfAccountsCode() + " - " + sourceAccountingLine.getAccountNumber() + " - " + sourceAccountingLine.getFinancialObjectCode();
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ACCOUNT_AMOUNT_TOTAL, accountString, sourceAccountingLine.getAmount() + "");
                valid &= false;
            }
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.ContinuePurapRule#processContinueAccountsPayableBusinessRules(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public boolean processContinuePurapBusinessRules(TransactionalDocument apDocument) {
        boolean valid = true;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) apDocument;
        valid &= processPurchaseOrderIDValidation(paymentRequestDocument);
        valid &= processInvoiceValidation(paymentRequestDocument);
        valid &= processPaymentRequestDateValidationForContinue(paymentRequestDocument);
        return valid;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.CalculateAccountsPayableRule#processCalculateAccountsPayableBusinessRules(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public boolean processCalculateAccountsPayableBusinessRules(AccountsPayableDocument apDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) apDocument;
        // Give warnings for the following. The boolean results of the calls are not to be used here.
        boolean totalsMatch = validateTotals(paymentRequestDocument);
        boolean payDateOk = validatePayDateNotOverThresholdDaysAway(paymentRequestDocument);
        // The Grand Total Amount must be greater than zero.
        if (paymentRequestDocument.getGrandTotal().compareTo(KualiDecimal.ZERO) <= 0) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.GRAND_TOTAL, PurapKeyConstants.ERROR_PAYMENT_REQUEST_GRAND_TOTAL_NOT_POSITIVE);
            valid &= false;
        }
        // The Payment Request Pay Date must not be in the past.
        valid &= validatePayDateNotPast(paymentRequestDocument);
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * Validates item fields are valid for the calculation process.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.PreCalculateAccountsPayableRule#processPreCalculateAccountsPayableBusinessRules(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public boolean processPreCalculateAccountsPayableBusinessRules(AccountsPayableDocument document) {
        boolean valid = true;
        PaymentRequestDocument preqDocument = (PaymentRequestDocument) document;

        return valid;
    }

    /**
     * Performs any validation for the Invoice tab.
     * 
     * @param preqDocument - payment request document
     * @return
     */
    public boolean processInvoiceValidation(PaymentRequestDocument preqDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        if (ObjectUtils.isNull(preqDocument.getPurchaseOrderIdentifier())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.PURCHASE_ORDER_ID);
            valid &= false;
        }
        if (ObjectUtils.isNull(preqDocument.getInvoiceDate())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.INVOICE_DATE, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.INVOICE_DATE);
            valid &= false;
        }
        if (StringUtils.isBlank(preqDocument.getInvoiceNumber())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.INVOICE_NUMBER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.INVOICE_NUMBER);
            valid &= false;
        }
        if (ObjectUtils.isNull(preqDocument.getVendorInvoiceAmount())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_INVOICE_AMOUNT, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.VENDOR_INVOICE_AMOUNT);
            valid &= false;
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * Performs validation on the vendor on the payment request document.
     * 
     * @param preqDocument - payment request document.
     * @return
     */
    public boolean processVendorValidation(PaymentRequestDocument preqDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        if (StringUtils.equals(preqDocument.getVendorCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES)) {
            if (StringUtils.isBlank(preqDocument.getVendorStateCode())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_STATE_CODE, KFSKeyConstants.ERROR_REQUIRED_FOR_US, PREQDocumentsStrings.VENDOR_STATE);
                valid &= false;
            }
            if (StringUtils.isBlank(preqDocument.getVendorPostalCode())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, KFSKeyConstants.ERROR_REQUIRED_FOR_US, PREQDocumentsStrings.VENDOR_POSTAL_CODE);
                valid &= false;
            }

        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * Performs various validation on the purchase order.
     * 
     * @param document
     * @return
     */
    protected boolean processPurchaseOrderIDValidation(PaymentRequestDocument document) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        Integer POID = document.getPurchaseOrderIdentifier();

        PurchaseOrderDocument purchaseOrderDocument = document.getPurchaseOrderDocument();
        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            valid &= false;
        }
        else if (purchaseOrderDocument.isPendingActionIndicator()) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_PENDING_ACTION);
            valid &= false;
        }
        else if (!StringUtils.equals(purchaseOrderDocument.getStatusCode(), PurapConstants.PurchaseOrderStatuses.OPEN)) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_OPEN);
            valid &= false;
            // if the PO is pending and it is not a Retransmit, we cannot generate a Payment Request for it
        }
        else {
            // Verify that there exists at least 1 item left to be invoiced
            valid &= encumberedItemExistsForInvoicing(purchaseOrderDocument);
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * Determines if there are items with encumbrances to be invoiced on passed in
     * purchase order document.
     * 
     * @param document - purchase order document
     * @return
     */
    public boolean encumberedItemExistsForInvoicing(PurchaseOrderDocument document) {
        boolean zeroDollar = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) document.getItems()) {
            // Quantity-based items
            if (poi.getItemType().isLineItemIndicator() && poi.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                KualiDecimal encumberedQuantity = poi.getItemOutstandingEncumberedQuantity() == null ? zero : poi.getItemOutstandingEncumberedQuantity();
                if (encumberedQuantity.compareTo(zero) == 1) {
                    zeroDollar = false;
                    break;
                }
            }
            // Service Items or Below-the-line Items
            else if (poi.getItemType().isAmountBasedGeneralLedgerIndicator() || poi.getItemType().isAdditionalChargeIndicator()) {
                KualiDecimal encumberedAmount = poi.getItemOutstandingEncumberedAmount() == null ? zero : poi.getItemOutstandingEncumberedAmount();
                if (encumberedAmount.compareTo(zero) == 1) {
                    zeroDollar = false;
                    break;
                }
            }
        }
        if (zeroDollar) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_NO_ITEMS_TO_INVOICE);
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return !zeroDollar;
    }

    /**
     * Ensures invoice date does not occur in the future.
     * 
     * @param document
     * @return
     */
    protected boolean processPaymentRequestDateValidationForContinue(PaymentRequestDocument document) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        // invoice date validation
        java.sql.Date invoiceDate = document.getInvoiceDate();
        if (ObjectUtils.isNotNull(invoiceDate) && SpringContext.getBean(PaymentRequestService.class).isInvoiceDateAfterToday(invoiceDate)) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.INVOICE_DATE, PurapKeyConstants.ERROR_INVALID_INVOICE_DATE);
            valid &= false;
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * Validates payment request dates.
     * 
     * @param document - payment request document
     * @return
     */
    protected boolean validatePaymentRequestDates(PaymentRequestDocument document) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        // Pay date in the past validation.
        valid &= validatePayDateNotPast(document);
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * Validates that the payment request date does not occur in the past.
     * 
     * @param document - payment request document
     * @return
     */
    protected boolean validatePayDateNotPast(PaymentRequestDocument document) {
        boolean valid = true;
        java.sql.Date paymentRequestPayDate = document.getPaymentRequestPayDate();
        if (ObjectUtils.isNotNull(paymentRequestPayDate) && SpringContext.getBean(PurapService.class).isDateInPast(paymentRequestPayDate)) {
            // the pay date is in the past, now we need to check whether given the state of the document to determine whether a past pay date is allowed
            KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument(); 
            if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
                // past pay dates are not allowed if the document has never been routed (i.e. in saved or initiated state)
                // (note that this block will be run when a document is being routed, or re-saved after being routed
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, PurapKeyConstants.ERROR_INVALID_PAY_DATE);
            } else {
                // otherwise, this document has already been routed
                // it's an error if the pay date has been changed from the pay date in the database and the new pay date is in the past
                // retrieve doc from DB, and compare the dates
                PaymentRequestDocument paymentRequestDocumentFromDatabase = retrievePaymentRequestDocumentFromDatabase(document);
                
                if (ObjectUtils.isNull(paymentRequestDocumentFromDatabase)) {
                    // this definitely should not happen
                    throw new NullPointerException("Unable to find payment request document " + document.getDocumentNumber() + " from database");
                }
                
                java.sql.Date paymentRequestPayDateFromDatabase = paymentRequestDocumentFromDatabase.getPaymentRequestPayDate();
                if (ObjectUtils.isNull(paymentRequestPayDateFromDatabase) || !paymentRequestPayDateFromDatabase.equals(paymentRequestPayDate)) {
                    valid &= false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, PurapKeyConstants.ERROR_INVALID_PAY_DATE);
                }
            }
        }
        return valid;
    }

    /**
     * Retrieves the payment request document from the database.  Note that the instance returned 
     * @param document the document to look in the database for
     * @return an instance representing what's stored in the database for this instance
     */
    protected PaymentRequestDocument retrievePaymentRequestDocumentFromDatabase(PaymentRequestDocument document) {
        Map primaryKeyValues = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(document);
        return (PaymentRequestDocument) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(document.getClass(), primaryKeyValues);
    }

    /**
     * This method side-effects a warning, and consequently should not be used in such a way as to cause validation to fail. Returns
     * a boolean for ease of testing. If the threshold days value is positive, the method will test future dates accurately. If the
     * the threshold days value is negative, the method will test past dates.
     * 
     * @param document The PaymentRequestDocument
     * @return True if the PREQ's pay date is not over the threshold number of days away.
     */
    public boolean validatePayDateNotOverThresholdDaysAway(PaymentRequestDocument document) {
        boolean valid = true;
        int thresholdDays = PurapConstants.PREQ_PAY_DATE_DAYS_BEFORE_WARNING;
        if ((document.getPaymentRequestPayDate() != null) && SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(document.getPaymentRequestPayDate(), thresholdDays)) {
            if (ObjectUtils.isNull(GlobalVariables.getMessageList())) {
                GlobalVariables.setMessageList(new ArrayList());
            }
            if (!GlobalVariables.getMessageList().contains(PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS)) {
                GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS);
            }
            valid &= false;
        }
        return valid;
    }

    /**
     * Validates whether the total of the items' extended price, excluding the item types that can be negative, match with
     * the vendor invoice amount that the user entered at the beginning of the preq creation, and if they don't match, the app will
     * just print a warning to the page that the amounts don't match.
     * 
     * @param document - payment request document
     */
    public boolean validateTotals(PaymentRequestDocument document) {
        boolean valid = true;
        List excludeDiscount = new ArrayList();
        excludeDiscount.add(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE);
        if ((ObjectUtils.isNull(document.getVendorInvoiceAmount())) || (this.getTotalExcludingItemTypes(document.getItems(), excludeDiscount).compareTo(document.getVendorInvoiceAmount()) != 0 && !document.isUnmatchedOverride())) {
            if (!GlobalVariables.getMessageList().contains(PurapKeyConstants.WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID)) {
                GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID);
                valid = false;
            }
        }
        flagLineItemTotals(document.getItems());
        return valid;
    }

    /**
     * Calculates a total but excludes passed in item types from the totalling.
     * 
     * @param itemList - list of purap items
     * @param excludedItemTypes - list of item types to exclude from totalling
     * @return
     */
    private KualiDecimal getTotalExcludingItemTypes(List<PurApItem> itemList, List<String> excludedItemTypes) {
        KualiDecimal total = zero;
        for (PurApItem item : itemList) {
            if (item.getTotalAmount() != null && item.getTotalAmount().isNonZero()) {
                boolean skipThisItem = false;
                if (excludedItemTypes.contains(item.getItemTypeCode())) {
                    // this item type is excluded
                    skipThisItem = true;
                    break;
                }
                if (skipThisItem) {
                    continue;
                }
                total = total.add(item.getTotalAmount());
            }
        }
        return total;
    }

    /**
     * Flags with an erorr the item totals whos calculated extended price does not equal its extended price.
     * 
     * @param itemList - list of purap items
     */
    private void flagLineItemTotals(List<PurApItem> itemList) {
        for (PurApItem purApItem : itemList) {
            PaymentRequestItem item = (PaymentRequestItem) purApItem;
            if (item.getItemQuantity() != null && item.getExtendedPrice() !=null) {
                if (item.calculateExtendedPrice().compareTo(item.getExtendedPrice()) != 0) {
                    GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_PAYMENT_REQUEST_ITEM_TOTAL_NOT_EQUAL, item.getItemIdentifierString());
                }
            }
        }
    }

    /**
     * Performs item validations for the rules that are only applicable to Payment Request Document. In EPIC, we are
     * also doing similar steps as in this method within the validateFormatter, which is called upon Save. Therefore now we're also
     * calling the same validations upon Save.
     * 
     * @param purapDocument - purchasing accounts payable document
     * @return
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) purapDocument;
        for (PurApItem item : purapDocument.getItems()) {
            PaymentRequestItem preqItem = (PaymentRequestItem) item;
            valid &= validateEachItem(paymentRequestDocument, preqItem);
        }
        return valid;
    }

    /**
     * Calls another validate item method and passes an identifier string from the item.
     * 
     * @param paymentRequestDocument - payment request document.
     * @param item
     * @return
     */
    private boolean validateEachItem(PaymentRequestDocument paymentRequestDocument, PaymentRequestItem item) {
        boolean valid = true;
        String identifierString = item.getItemIdentifierString();
        valid &= validateItem(paymentRequestDocument, item, identifierString);
        return valid;
    }

    /**
     * Performs validation if full document entry not completed and peforms varying item validation.
     * Such as, above the line, items without accounts, items with accounts.
     * 
     * @param paymentRequestDocument - payment request document
     * @param item - payment request item
     * @param identifierString - identifier string used to mark in an error map
     * @return
     */
    public boolean validateItem(PaymentRequestDocument paymentRequestDocument, PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        // only run item validations if before full entry
        if (!SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument)) {
            if (item.getItemType().isLineItemIndicator()) {
                valid &= validateAboveTheLineItems(item, identifierString);
            }
            valid &= validateItemWithoutAccounts(item, identifierString);
        }
        // always run account validations
        valid &= validateItemAccounts(paymentRequestDocument, item, identifierString);
        return valid;
    }

    /**
     * Checks whether there exists trade in item with outstanding amount less than 0,
     * if there is a line item that is assigned to trade in. Only checks this if
     * the fullDocumentEntry is not yet completed.
     * 
     * @param document
     * @return
     */
    public boolean validateWarningTradeIn(PaymentRequestDocument document) {
        if (!SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(document)) {
            for (PurApItem item : (List<PurApItem>) document.getItems()) {
                if (item.getItemType().isLineItemIndicator() && item.getItemAssignedToTradeInIndicator()) {
                    PaymentRequestItem tradeInItem = (PaymentRequestItem) document.getTradeInItem();
                    if (ObjectUtils.isNotNull(tradeInItem)) {
                        if (ObjectUtils.isNull(tradeInItem.getItemUnitPrice()) && tradeInItem.getPoOutstandingAmount().isLessThan(new KualiDecimal(0))) {
                            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_ITEM_TRADE_IN_AMOUNT_UNUSED);
                            return false;
                        }
                    }
                }
            }
        }
        return true;        
    }
    
    /**
     * This is invoked during pre rules.
     * If it's a non C & G account and the account has expired, or if it's a C&G account and 
     * the account has expired for less than 90 days, give warning to the fiscal officer that
     * the account has expired and allow the fiscal officer to return to the tabbed page if
     * they wish to fix the account after the question framework, or proceed to routing if 
     * they choose to ignore the warning.
     * 
     * @param document
     * @return
     */
    public boolean validateWarningExpiredAccount(PaymentRequestDocument document) {
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(document)) {
            for (PurApItem item : (List<PurApItem>) document.getItems()) {
                List<PurApAccountingLine> accountingLines = item.getSourceAccountingLines();
                for (PurApAccountingLine accountingLine : accountingLines) {
                    if (accountingLine.getAccount().isExpired()) {
                        Date current = SpringContext.getBean(DateTimeService.class).getCurrentDate();
                        Date accountExpirationDate = accountingLine.getAccount().getAccountExpirationDate();
                        if (!accountingLine.getAccount().isForContractsAndGrants() ||
                             SpringContext.getBean(DateTimeService.class).dateDiff(accountExpirationDate, current, false) < 90) {
                            GlobalVariables.getMessageList().add(KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
                            return false;
                        }
                    }
                }
            }
        }
        return true;        
    }

    /**
     * Validates above the line items.
     * 
     * @param item - payment request item
     * @param identifierString - identifier string used to mark in an error map
     * @return
     */
    private boolean validateAboveTheLineItems(PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        // Currently Quantity is allowed to be NULL on screen;
        // must be either a positive number or NULL for DB
        if (ObjectUtils.isNotNull(item.getItemQuantity())) {
            if (item.getItemQuantity().isNegative()) {
                // if quantity is negative give an error
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.INVOICE_QUANTITY, identifierString);
            }
            if (item.getPoOutstandingQuantity().isLessThan(item.getItemQuantity())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_QUANTITY_TOO_MANY, ItemFields.INVOICE_QUANTITY, identifierString, ItemFields.OPEN_QUANTITY);
            }
        }
        if (ObjectUtils.isNotNull(item.getExtendedPrice()) && item.getExtendedPrice().isPositive() && ObjectUtils.isNotNull(item.getPoOutstandingQuantity()) && item.getPoOutstandingQuantity().isPositive()) {

            // here we must require the user to enter some value for quantity if they want a credit amount associated
            if (ObjectUtils.isNull(item.getItemQuantity()) || item.getItemQuantity().isZero()) {
                // here we have a user not entering a quantity with an extended amount but the PO has a quantity...require user to
                // enter a quantity
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_QUANTITY_REQUIRED, ItemFields.INVOICE_QUANTITY, identifierString, ItemFields.OPEN_QUANTITY);
            }
        }

        // check that non-quantity based items are not trying to pay on a zero encumbrance amount (check only prior to ap approval)
        if ((ObjectUtils.isNull(item.getPaymentRequest().getPurapDocumentIdentifier())) || (PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(item.getPaymentRequest().getStatusCode()))) {
            if ((item.getItemType().isAmountBasedGeneralLedgerIndicator()) && ((item.getExtendedPrice() != null) && item.getExtendedPrice().isNonZero())) {
                if (item.getPoOutstandingAmount() == null || item.getPoOutstandingAmount().isZero()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMOUNT_ALREADY_PAID, identifierString);
                }
            }
        }

        return valid;
    }

    /**
     * Validates that the item must contain at least one account
     * 
     * @param item - payment request item
     * @return
     */
    public boolean validateItemWithoutAccounts(PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNonZero() && item.isAccountListEmpty()) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_INCOMPLETE, identifierString);
        }
        return valid;
    }

    /**
     * Validates the totals for the item by account, that the total by each accounting line for the item, matches
     * the extended price on the item.
     * 
     * @param paymentRequestDocument - payment request document
     * @param item - payment request item to validate
     * @param identifierString - identifier string used to mark in an error map
     * @return
     */
    public boolean validateItemAccounts(PaymentRequestDocument paymentRequestDocument, PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        List<PurApAccountingLine> accountingLines = item.getSourceAccountingLines();
        KualiDecimal itemTotal = item.getTotalAmount();
        KualiDecimal accountTotal = KualiDecimal.ZERO;
        for (PurApAccountingLine accountingLine : accountingLines) {
            valid &= this.processReviewAccountingLineBusinessRules(paymentRequestDocument, accountingLine);
            accountTotal = accountTotal.add(accountingLine.getAmount());
        }
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((PaymentRequestDocument) paymentRequestDocument)) {
            // check amounts not percent after full entry
            if (accountTotal.compareTo(itemTotal) != 0) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_AMOUNT_TOTAL, identifierString);
            }
        }
        return valid;
    }

    /**
     * Validates that a cancel can occur given the current state of the document.
     * 
     * @param purapDocument - purchasing accounts payable document
     * @return
     */
    public boolean validateCancel(PurchasingAccountsPayableDocument purapDocument) {
        Collection c = new ArrayList();
        boolean valid = true;
        PaymentRequestDocument pr = (PaymentRequestDocument) purapDocument;
        if (PurapConstants.PaymentRequestStatuses.CANCELLED_STATUSES.contains(pr.getStatusCode())) {
            // send ERROR: PREQ is already canceled
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_CANCEL_CANCELLED);
            return valid;
        }

        if (ObjectUtils.isNotNull(pr.getExtractedTimestamp())) {
            // send ERROR: PREQ has been extracted to Disbursement Engine
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_CANCEL_EXTRACTED);
            return valid;
        }
        if ((PurapConstants.PurchaseOrderStatuses.CLOSED.equals(pr.getPurchaseOrderDocument().getStatusCode())) && (!(PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(pr.getStatusCode())))) {
            // send WARNING: PREQ Can re open PO EpicConstants.PREQ_ACTION_MSSG_WARN_PROP
            valid = true;
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_CANCEL_REOPEN_PO);
            return valid;
        }
        return valid;
    }

    /**
     * 
     * 
     * @param purapDocument - purchasing accounts payable document
     * @return
     */
    public boolean validateRouteFiscal(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        PaymentRequestDocument paymentRequest = (PaymentRequestDocument) purapDocument;
        valid &= validatePaymentRequestReview(paymentRequest);
        return valid;
    }

    /**
     * 
     * 
     * @param paymentRequest - payment request document
     * @return
     */
    protected boolean validatePaymentRequestReview(PaymentRequestDocument paymentRequest) {
        boolean valid = true;

        // if FY > current FY, warn user that payment will happen in current year
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        Integer fiscalYear = universityDateService.getCurrentFiscalYear();
        Date closingDate = universityDateService.getLastDateOfFiscalYear(fiscalYear);

        if (paymentRequest.getPurchaseOrderDocument().getPostingYear().intValue() > fiscalYear) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_ENCUMBER_NEXT_FY);
        }

        for (Iterator itemIter = paymentRequest.getItems().iterator(); itemIter.hasNext();) {
            PaymentRequestItem item = (PaymentRequestItem) itemIter.next();

            boolean containsAccounts = false;
            int accountLineNbr = 0;

            String identifier = item.getItemIdentifierString();
            BigDecimal total = BigDecimal.ZERO;
            LOG.debug("validatePaymentRequestReview() The " + identifier + " is getting the total percent field set to " + BigDecimal.ZERO);

            if (((item.getTotalAmount() != null && item.getTotalAmount().isNonZero()) && item.getItemType().isLineItemIndicator() && ((item.getItemType().isAmountBasedGeneralLedgerIndicator() && (item.getPoOutstandingAmount() != null && item.getPoOutstandingAmount().isNonZero())) || (item.getItemType().isQuantityBasedGeneralLedgerIndicator() && (item.getPoOutstandingQuantity() != null && item.getPoOutstandingQuantity().isNonZero())))) || (((item.getTotalAmount() != null) && (item.getTotalAmount().isNonZero())) && (item.getItemType().isAdditionalChargeIndicator()))) {
                // OK TO VALIDATE because we have total amount and an open encumberance on the PO item
                super.processItemValidation(paymentRequest);
                // This is calling the validations which in EPIC are located in validateFormatters, but in Kuali they should be
                // covered
                // within the processItemValidation of this class.
                validateEachItem(paymentRequest, item);
            }
            else if ((item.getTotalAmount() != null && item.getTotalAmount().isNonZero() && item.getItemType().isLineItemIndicator() && ((item.getItemType().isAmountBasedGeneralLedgerIndicator() && (item.getPoOutstandingAmount() == null || item.getPoOutstandingAmount().isZero())) || (item.getItemType().isQuantityBasedGeneralLedgerIndicator() && (item.getPoOutstandingQuantity() == null || item.getPoOutstandingQuantity().isZero()))))) {
                // ERROR because we have total amount and no open encumberance on the PO item
                // this error should have been caught at an earlier level
                if (item.getItemType().isAmountBasedGeneralLedgerIndicator()) {
                    String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has total amount '" + item.getTotalAmount() + "' but outstanding encumbered amount " + item.getPoOutstandingAmount();
                    LOG.error("validatePaymentRequestReview() " + error);
                }
                else {
                    String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has quantity '" + item.getItemQuantity() + "' but outstanding encumbered quantity " + item.getPoOutstandingQuantity();
                    LOG.error("validatePaymentRequestReview() " + error);
                }
            }
            else {
                // not validating but ok
                String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has total amount '" + item.getTotalAmount() + "'";
                if (item.getItemType().isLineItemIndicator()) {
                    if (item.getItemType().isAmountBasedGeneralLedgerIndicator()) {
                        error = error + " with outstanding encumbered amount " + item.getPoOutstandingAmount();
                    }
                    else {
                        error = error + " with outstanding encumbered quantity " + item.getPoOutstandingQuantity();
                    }
                }
                LOG.info("validatePaymentRequestReview() " + error);
            }

        }
        return valid;
    }

    /**
     * Validates tax income class: when Non-Reportable income class is chosen, all other fields shall be left blank; 
     * otherwise tax rates and country are required;
     * @param paymentRequest - payment request document
     * @return true if this validation passes; false otherwise.
     */
    protected boolean validateTaxIncomeClass(PaymentRequestDocument preq) {
       boolean valid = true;
       ErrorMap errorMap = GlobalVariables.getErrorMap();

       // TaxClassificationCode is required field
       if (StringUtils.isEmpty(preq.getTaxClassificationCode())) {
           valid = false;
           errorMap.putError(PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED, PurapPropertyConstants.TAX_CLASSIFICATION_CODE);           
       }
       // If TaxClassificationCode is N (Non_Reportable, then other fields shall be blank.
       else if (StringUtils.equalsIgnoreCase(preq.getTaxClassificationCode(), "N")) {
           if (preq.getFederalTaxPercent() != null && preq.getFederalTaxPercent().compareTo(new BigDecimal(0)) != 0) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.FEDERAL_TAX_PERCENT);
           }
           if (preq.getStateTaxPercent() != null && preq.getStateTaxPercent().compareTo(new BigDecimal(0)) != 0) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.STATE_TAX_PERCENT);
           }
           if (!StringUtils.isEmpty(preq.getTaxCountryCode())) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_COUNTRY_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_COUNTRY_CODE);
           }
           if (!StringUtils.isEmpty(preq.getTaxNQIId())) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_NQI_ID, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_NQI_ID);           
           }
           if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new BigDecimal(0)) != 0) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getTaxExemptTreatyIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
           }                         
           if (ObjectUtils.nullSafeEquals(preq.getGrossUpIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.GROSS_UP_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getForeignSourceIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getTaxUSAIDPerDiemIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getOtherTaxExemptIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR);
           }               
       }
       else {
           // If TaxClassificationCode is not N (Non_Reportable, then the federal/state tax percent and country are required.
           if (preq.getFederalTaxPercent() == null) {       
               valid = false;
               errorMap.putError(PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.FEDERAL_TAX_PERCENT);           
           }
           if (preq.getStateTaxPercent() == null) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.STATE_TAX_PERCENT);           
           }
           if (StringUtils.isEmpty(preq.getTaxCountryCode())) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_COUNTRY_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_COUNTRY_CODE);           
           }
       }
       
       return valid;
    }
    
    /**
     * Validates federal and state tax rates based on each other and the income class.
     * Validation will be bypassed if income class is empty or N, or tax rates are null.
     * @param paymentRequest - payment request document
     * @return true if this validation passes; false otherwise.
     */
    protected boolean validateTaxRates(PaymentRequestDocument preq) {
       boolean valid = true;
       String code = preq.getTaxClassificationCode();
       BigDecimal fedrate = preq.getFederalTaxPercent();
       BigDecimal strate = preq.getStateTaxPercent();
       ErrorMap errorMap = GlobalVariables.getErrorMap();

       // only test the cases when income class and tax rates aren't empty/N
       if (StringUtils.isEmpty(code) || StringUtils.equalsIgnoreCase(code, "N") || fedrate == null || strate == null)
           return true;
              
       // validate that the federal and state tax rates are among the allowed set
       ArrayList<BigDecimal> fedrates = (ArrayList<BigDecimal>) federalTaxRates.get(code);
       if (!listContainsValue(fedrates, fedrate)) {
           valid = false;
           errorMap.putError(PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.FEDERAL_TAX_PERCENT);                      
       }
       ArrayList<BigDecimal> strates = (ArrayList<BigDecimal>) stateTaxRates.get(code);
       if (!listContainsValue(strates, strate)) {
           valid = false;
           errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.STATE_TAX_PERCENT);                      
       }
       
       // validate that the federal and state tax rate abide to certain relationship
       if (fedrate.compareTo(new BigDecimal(0)) == 0 && strate.compareTo(new BigDecimal(0)) != 0) {
           valid = false;
           errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapPropertyConstants.STATE_TAX_PERCENT);                      
       } 
       boolean hasstrate = code.equalsIgnoreCase("F") || code.equalsIgnoreCase("A") || code.equalsIgnoreCase("O");
       if (fedrate.compareTo(new BigDecimal(0)) > 0 && strate.compareTo(new BigDecimal(0)) <= 0 && hasstrate) {
           valid = false;
           errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_NOT_ZERO_IF, PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapPropertyConstants.STATE_TAX_PERCENT);                      
       } 
       
       return valid;
    }
    
    /**
     * Validates rules among tax treaty, gross up, foreign source, USAID, other exempt, and Special W-4.
     * @param paymentRequest - payment request document
     * @return true if this validation passes; false otherwise.
     */
    protected boolean validateTaxIndicators(PaymentRequestDocument preq) {
       boolean valid = true;     
       ErrorMap errorMap = GlobalVariables.getErrorMap();
           
       // if choose tax treaty, cannot choose any of the other above 
       if (ObjectUtils.nullSafeEquals(preq.getTaxExemptTreatyIndicator(), true)) {
           if (ObjectUtils.nullSafeEquals(preq.getGrossUpIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.GROSS_UP_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getForeignSourceIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getTaxUSAIDPerDiemIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getOtherTaxExemptIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR);
           }               
           if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
           }               
       }
           
       // if choose gross up, cannot choose any other above, and fed tax rate cannot be zero
       if (ObjectUtils.nullSafeEquals(preq.getGrossUpIndicator(), true)) {
           if (ObjectUtils.nullSafeEquals(preq.getTaxExemptTreatyIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getForeignSourceIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.GROSS_UP_INDICATOR, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getTaxUSAIDPerDiemIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getOtherTaxExemptIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.GROSS_UP_INDICATOR, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR);
           }               
           if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
           }               
           if (preq.getFederalTaxPercent() == null || preq.getFederalTaxPercent().compareTo(new BigDecimal(0)) == 0 ) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_NOT_ZERO_IF, PurapPropertyConstants.GROSS_UP_INDICATOR, PurapPropertyConstants.FEDERAL_TAX_PERCENT);
           }
       }
       
       // if choose foreign source, cannot choose any other above, and tax rates shall be zero
       if (ObjectUtils.nullSafeEquals(preq.getForeignSourceIndicator(), true)) {
           if (ObjectUtils.nullSafeEquals(preq.getTaxExemptTreatyIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getGrossUpIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.GROSS_UP_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getTaxUSAIDPerDiemIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getOtherTaxExemptIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR);
           }               
           if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
           }               
           if (preq.getFederalTaxPercent() != null && preq.getFederalTaxPercent().compareTo(new BigDecimal(0)) != 0) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.FEDERAL_TAX_PERCENT);
           }
           if (preq.getStateTaxPercent() != null && preq.getStateTaxPercent().compareTo(new BigDecimal(0)) != 0) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.STATE_TAX_PERCENT);
           }
       }
       
       // if choose USAID per diem, cannot choose any other above except other exempt code, which must be checked; income class shall be fellowship with tax rates 0
       if (ObjectUtils.nullSafeEquals(preq.getTaxUSAIDPerDiemIndicator(), true)) {
           if (ObjectUtils.nullSafeEquals(preq.getTaxExemptTreatyIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getGrossUpIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.GROSS_UP_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getForeignSourceIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR);
           }               
           if (!ObjectUtils.nullSafeEquals(preq.getOtherTaxExemptIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR);
           }               
           if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
           }               
           if (StringUtils.isEmpty(preq.getTaxClassificationCode()) || !StringUtils.equalsIgnoreCase(preq.getTaxClassificationCode(), "F")) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_CLASSIFICATION_CODE);
           }
           if (preq.getFederalTaxPercent() != null && preq.getFederalTaxPercent().compareTo(new BigDecimal(0)) != 0 ) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.FEDERAL_TAX_PERCENT);
           }
           if (preq.getStateTaxPercent() != null && preq.getStateTaxPercent().compareTo(new BigDecimal(0)) != 0 ) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.STATE_TAX_PERCENT);
           }
       }
       
       // if choose exempt under other code, cannot choose any other above except USAID, and tax rates shall be zero
       if (ObjectUtils.nullSafeEquals(preq.getOtherTaxExemptIndicator(), true)) {
           if (ObjectUtils.nullSafeEquals(preq.getTaxExemptTreatyIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getGrossUpIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapPropertyConstants.GROSS_UP_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getForeignSourceIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR);
           }               
           if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
           }               
           if (preq.getFederalTaxPercent() != null && preq.getFederalTaxPercent().compareTo(new BigDecimal(0)) != 0 ) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapPropertyConstants.FEDERAL_TAX_PERCENT);
           }
           if (preq.getStateTaxPercent() != null && preq.getStateTaxPercent().compareTo(new BigDecimal(0)) != 0 ) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.OTHER_TAX_EXEMPT_INDICATOR, PurapPropertyConstants.STATE_TAX_PERCENT);
           }
       }
              
       // if choose Special W-4, cannot choose tax treaty, gross up, and foreign source; income class shall be fellowship with tax rates 0
       if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0 ) {
           if (ObjectUtils.nullSafeEquals(preq.getTaxExemptTreatyIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getGrossUpIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.GROSS_UP_INDICATOR);
           }               
           if (ObjectUtils.nullSafeEquals(preq.getForeignSourceIndicator(), true)) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.FOREIGN_SOURCE_INDICATOR);
           }               
           if (preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) < 0) { 
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_MUST_NOT_NEGATIVE, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
           }                          
           if (StringUtils.isEmpty(preq.getTaxClassificationCode()) || !StringUtils.equalsIgnoreCase(preq.getTaxClassificationCode(), "F")) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_CLASSIFICATION_CODE);
           }
           if (preq.getFederalTaxPercent() != null && preq.getFederalTaxPercent().compareTo(new BigDecimal(0)) != 0 ) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.FEDERAL_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.FEDERAL_TAX_PERCENT);
           }
           if (preq.getStateTaxPercent() != null && preq.getStateTaxPercent().compareTo(new BigDecimal(0)) != 0 ) {
               valid = false;
               errorMap.putError(PurapPropertyConstants.STATE_TAX_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.STATE_TAX_PERCENT);
           }
       }

       return valid;
    }

    /**
     * Returns true if the specified ArrayList contains the specified BigDecimal value.
     * @param list the specified ArrayList
     * @param value the specified BigDecimal
     */
    private boolean listContainsValue(ArrayList<BigDecimal> list, BigDecimal value) {
        if (list == null || value == null)
            return false;
        for (BigDecimal val : list) {
            if (val.compareTo(value) == 0)
                return true;
        }
        return false;     
    }
            
    /**
     * Process business rules applicable to tax area data before calculating the withholding tax on payment request.
     * @param paymentRequest - payment request document
     * @return true if all business rules applicable passes; false otherwise.
     */
    public boolean ProcessPreCalculateTaxAreaBusinessRules(PaymentRequestDocument preq) {
        boolean valid = true;        
        ErrorMap errorMap = GlobalVariables.getErrorMap();        
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(KFSPropertyConstants.DOCUMENT);

        valid &= validateTaxIncomeClass(preq);        
        valid &= validateTaxRates(preq);    
        valid &= validateTaxIndicators(preq);

        errorMap.clearErrorPath();
        return valid;
    }
    
   /**
     * Returns true if full document entry is completed and bypasses any further validation, otherwise proceeds as normal.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#verifyAccountPercent(org.kuali.kfs.sys.document.AccountingDocument, java.util.List, java.lang.String)
     */
    @Override
    protected boolean verifyAccountPercent(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((PaymentRequestDocument) accountingDocument)) {
            return true;
        }
        return super.verifyAccountPercent(accountingDocument, purAccounts, itemLineNumber);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.CancelAccountsPayableRule#processCancelAccountsPayableBusinessRules(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public boolean processCancelAccountsPayableBusinessRules(AccountsPayableDocument document) {
        boolean valid = true;
        PaymentRequestDocument preq = (PaymentRequestDocument) document;
        // no errors for now since we are not showing the button if they can't cancel, if that changes we need errors
        // also this is different than CreditMemo even though the rules are almost identical we should merge and have one consistent
        // way to do this
        PaymentRequestDocumentActionAuthorizer preqAuth = new PaymentRequestDocumentActionAuthorizer(preq, null);
        valid = valid &= preqAuth.canCancel();
        return valid;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean valid = super.processCustomSaveDocumentBusinessRules(document);
        
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        // Pay date in the past validation.
        valid &= validatePayDateNotPast(paymentRequestDocument);
        GlobalVariables.getErrorMap().clearErrorPath();
        
        
        return valid;
    }
}
