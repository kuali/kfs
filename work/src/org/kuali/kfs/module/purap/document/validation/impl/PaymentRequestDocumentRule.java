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
package org.kuali.module.purap.rules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.ItemFields;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.authorization.PaymentRequestDocumentActionAuthorizer;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapService;

/**
 * Business rule(s) applicable to Payment Request documents.
 */
public class PaymentRequestDocumentRule extends AccountsPayableDocumentRuleBase {

    private static KualiDecimal zero = new KualiDecimal(0);
    private static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Returns true if full document entry is complete, bypassing further rules.
     * 
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
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
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#accountIsAccessible(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
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
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
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
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
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
     * Ensures source accounting lines amounts are not negative.
     * 
     * @param paymentRequestDocument - payment request document to validate
     * @return
     */
    private boolean checkNegativeAccounts(PaymentRequestDocument paymentRequestDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);

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
     * @see org.kuali.module.purap.rule.ContinuePurapRule#processContinueAccountsPayableBusinessRules(org.kuali.module.purap.document.AccountsPayableDocument)
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
     * @see org.kuali.module.purap.rule.CalculateAccountsPayableRule#processCalculateAccountsPayableBusinessRules(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public boolean processCalculateAccountsPayableBusinessRules(AccountsPayableDocument apDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) apDocument;
        // Give warnings for the following. The boolean results of the calls are not to be used here.
        boolean totalsMatch = validateTotals(paymentRequestDocument);
        boolean payDateOk = validatePayDateNotOverThresholdDaysAway(paymentRequestDocument);
        // The Grand Total Amount must be greate than zero.
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
     * @see org.kuali.module.purap.rule.PreCalculateAccountsPayableRule#processPreCalculateAccountsPayableBusinessRules(org.kuali.module.purap.document.AccountsPayableDocument)
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
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
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
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
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
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);

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
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) document.getItems()) {
            // Quantity-based items
            if (poi.getItemType().isItemTypeAboveTheLineIndicator() && poi.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                KualiDecimal encumberedQuantity = poi.getItemOutstandingEncumberedQuantity() == null ? zero : poi.getItemOutstandingEncumberedQuantity();
                if (encumberedQuantity.compareTo(zero) == 1) {
                    zeroDollar = false;
                    break;
                }
            }
            // Service Items or Below-the-line Items
            else if (poi.getItemType().isAmountBasedGeneralLedgerIndicator() || !poi.getItemType().isItemTypeAboveTheLineIndicator()) {
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
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
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
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
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
    boolean validatePayDateNotPast(PaymentRequestDocument document) {
        boolean valid = true;
        java.sql.Date paymentRequestPayDate = document.getPaymentRequestPayDate();
        if (ObjectUtils.isNotNull(paymentRequestPayDate) && SpringContext.getBean(PurapService.class).isDateInPast(paymentRequestPayDate)) {
            valid &= false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, PurapKeyConstants.ERROR_INVALID_PAY_DATE);
        }
        return valid;
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
            if (item.getExtendedPrice() != null && item.getExtendedPrice().isNonZero()) {
                boolean skipThisItem = false;
                if (excludedItemTypes.contains(item.getItemTypeCode())) {
                    // this item type is excluded
                    skipThisItem = true;
                    break;
                }
                if (skipThisItem) {
                    continue;
                }
                total = total.add(item.getExtendedPrice());
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
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                valid &= validateAboveTheLineItems(item, identifierString);
            }
            valid &= validateItemWithoutAccounts(item, identifierString);
        }
        // always run account validations
        valid &= validateItemAccounts(paymentRequestDocument, item, identifierString);
        return valid;
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
        KualiDecimal itemTotal = item.getExtendedPrice();
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
            // send ERROR: PREQ is already cancelled
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_CANCEL_CANCELLED);
            return valid;
        }

        if (ObjectUtils.isNotNull(pr.getExtractedDate())) {
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

            if (((item.getExtendedPrice() != null && item.getExtendedPrice().isNonZero()) && item.getItemType().isItemTypeAboveTheLineIndicator() && ((item.getItemType().isAmountBasedGeneralLedgerIndicator() && (item.getPoOutstandingAmount() != null && item.getPoOutstandingAmount().isNonZero())) || (item.getItemType().isQuantityBasedGeneralLedgerIndicator() && (item.getPoOutstandingQuantity() != null && item.getPoOutstandingQuantity().isNonZero())))) || (((item.getExtendedPrice() != null) && (item.getExtendedPrice().isNonZero())) && (!item.getItemType().isItemTypeAboveTheLineIndicator()))) {
                // OK TO VALIDATE because we have extended price and an open encumberance on the PO item
                super.processItemValidation(paymentRequest);
                // This is calling the validations which in EPIC are located in validateFormatters, but in Kuali they should be
                // covered
                // within the processItemValidation of this class.
                validateEachItem(paymentRequest, item);
            }
            else if ((item.getExtendedPrice() != null && item.getExtendedPrice().isNonZero() && item.getItemType().isItemTypeAboveTheLineIndicator() && ((item.getItemType().isAmountBasedGeneralLedgerIndicator() && (item.getPoOutstandingAmount() == null || item.getPoOutstandingAmount().isZero())) || (item.getItemType().isQuantityBasedGeneralLedgerIndicator() && (item.getPoOutstandingQuantity() == null || item.getPoOutstandingQuantity().isZero()))))) {
                // ERROR because we have extended price and no open encumberance on the PO item
                // this error should have been caught at an earlier level
                if (item.getItemType().isAmountBasedGeneralLedgerIndicator()) {
                    String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has extended price '" + item.getExtendedPrice() + "' but outstanding encumbered amount " + item.getPoOutstandingAmount();
                    LOG.error("validatePaymentRequestReview() " + error);
                }
                else {
                    String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has quantity '" + item.getItemQuantity() + "' but outstanding encumbered quantity " + item.getPoOutstandingQuantity();
                    LOG.error("validatePaymentRequestReview() " + error);
                }
            }
            else {
                // not validating but ok
                String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has extended price '" + item.getExtendedPrice() + "'";
                if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
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
     * Returns true if full document entry is completed and bypasses any further validation, otherwise proceeds as normal.
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#verifyAccountPercent(org.kuali.kfs.document.AccountingDocument, java.util.List, java.lang.String)
     */
    @Override
    protected boolean verifyAccountPercent(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((PaymentRequestDocument) accountingDocument)) {
            return true;
        }
        return super.verifyAccountPercent(accountingDocument, purAccounts, itemLineNumber);
    }

    /**
     * @see org.kuali.module.purap.rule.CancelAccountsPayableRule#processCancelAccountsPayableBusinessRules(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public boolean processCancelAccountsPayableBusinessRules(AccountsPayableDocument document) {
        boolean valid = true;
        PaymentRequestDocument preq = (PaymentRequestDocument) document;
        // no errors for now since we are not showing the button if they can't cancel, if that changes we need errors
        // also this is different than CreditMemo even though the rules are almost identical we should merge and have one consistent
        // way to do this
        PaymentRequestDocumentActionAuthorizer preqAuth = new PaymentRequestDocumentActionAuthorizer(preq);
        valid = valid &= preqAuth.canCancel();
        return valid;
    }
}
