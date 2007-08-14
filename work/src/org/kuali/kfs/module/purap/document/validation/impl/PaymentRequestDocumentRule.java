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

import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.ItemFields;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.rule.CalculateAccountsPayableRule;
import org.kuali.module.purap.rule.ContinueAccountsPayableRule;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurchaseOrderService;

public class PaymentRequestDocumentRule extends AccountsPayableDocumentRuleBase implements ContinueAccountsPayableRule, CalculateAccountsPayableRule {

    private static KualiDecimal zero = new KualiDecimal(0);
    private static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    
	/**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#accountIsAccessible(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean accountIsAccessible(AccountingDocument financialDocument, AccountingLine accountingLine) {
        PaymentRequestDocument preq = (PaymentRequestDocument)financialDocument;
        //We are overriding the accessibility of the accounts only in the case where awaiting ap review, that is because the super checks enroute
        //and checks if it is the owner while we allow "full entry" until past this stage
        if(StringUtils.equals(PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW, preq.getStatusCode())) {
            return true;
        } else {
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
        valid &= processInvoiceValidation((PaymentRequestDocument)purapDocument);
        valid &= processPurchaseOrderIDValidation((PaymentRequestDocument)purapDocument);
        valid &= processPaymentRequestDateValidationForContinue((PaymentRequestDocument)purapDocument);
        valid &= validatePaymentRequestDates((PaymentRequestDocument)purapDocument);
        return valid;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        
        //not needed, this is done on calculate if we need to warn them outside of calculate that has to be done elsewhere
        //validateTotals((PaymentRequestDocument)purapDocument);
        isValid &= validateRouteFiscal(paymentRequestDocument);
        isValid &= processValidation(paymentRequestDocument);
        return isValid; 
    }
      
// TODO: Chris - we can probably uncomment this if desired after KULPURAP-1182 however this may not be needed
//    @Override
//    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
//        boolean isValid = true;
//        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
//        //not needed, this is done on calculate.if we need to warn them outside of calculate that has to be done elsewhere
//        //        validateTotals(paymentRequestDocument);
//        //Had to do it this way because the processItemValidation in the superclass contains
//        //some validations that won't be needed for save (e.g. the total must be 100%), so
//        //that I couldn't call the super.processItemValidation within the processItemValidation
//        //in this class.
//        
//        //TODO: Chris - remove the if + contents if we can do it as a result of KULPURAP-1182
//        
//        isValid &= processItemValidationForSave(paymentRequestDocument);
//        isValid &= processItemValidation(paymentRequestDocument);
//        return isValid;
//    }
        
    public boolean processContinueAccountsPayableBusinessRules(AccountsPayableDocument apDocument) {
        boolean valid = true;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument)apDocument;
        valid &= processPurchaseOrderIDValidation(paymentRequestDocument);
        valid &= processInvoiceValidation(paymentRequestDocument);      
        valid &= processPaymentRequestDateValidationForContinue(paymentRequestDocument);
        return valid;
    }

    public boolean processCalculateAccountsPayableBusinessRules(AccountsPayableDocument apDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument)apDocument;
        validateTotals(paymentRequestDocument);
        if (isPayDateInThePast(paymentRequestDocument.getPaymentRequestPayDate())) {
            valid &= false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, PurapKeyConstants.ERROR_INVALID_PAY_DATE);
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * This method performs any validation for the Invoice tab.
     * 
     * @param preqDocument
     * @return
     */
    public boolean processInvoiceValidation(PaymentRequestDocument preqDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
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
    
    boolean processPurchaseOrderIDValidation(PaymentRequestDocument document) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
       
        Integer POID = document.getPurchaseOrderIdentifier();
       
        // I think only the current PO can have the pending action indicator to be "Y". For all the other POs with the same PO
        // number, the pending indicator should be always "N". So, I think we only need to check if for the current PO the 
        // Pending indicator is "Y" and it is not a Retransmit doc, then we don't allow users to create a PREQ. Correct? 
        // Given a PO number, the user enters in the Init screen. For the rule "Error if the PO is not open", we also only 
        // need to check this rule against the current PO, Correct?
        PurchaseOrderDocument purchaseOrderDocument = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(document.getPurchaseOrderIdentifier());
        if (ObjectUtils.isNull(purchaseOrderDocument)) {    
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            valid &= false;
        } 
        else if (!StringUtils.equals(purchaseOrderDocument.getStatusCode(),PurapConstants.PurchaseOrderStatuses.OPEN)){
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_OPEN);
            valid &= false;
            // if the PO is pending and it is not a Retransmit, we cannot generate a Payment Request for it:
            // 2007-04-19 15:50:40,750 [http-8080-Processor23] ERROR
            // org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/kuali-dev].[action] :: Servlet.service() for servlet
            // action threw exception
            // java.lang.RuntimeException: transient FlexDoc is null - this should never happen
            // org.kuali.core.bo.DocumentHeader.getWorkflowDocument(DocumentHeader.java:67)
            // } else if (purchaseOrderDocument.isPendingActionIndicator() &
            // !StringUtils.equals(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument().getDocumentType(),
            // PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)){
            // GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER,
            // PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
        // } 
        // else if (purchaseOrderDocument.isPendingActionIndicator() &
        // !StringUtils.equals(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument().getDocumentType(),
        // PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)){
        // GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER,
        // PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
        //      valid &= false;         
        }
        else {            
            // Verify that there exists at least 1 item left to be invoiced
            valid &= encumberedItemExistsForInvoicing(purchaseOrderDocument);
            //FIXME: This valid = true;  is causing the clear button do not work properly in case of this error. Remvoing it will stop preq generation 
            valid = true;
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }
        
    public boolean encumberedItemExistsForInvoicing(PurchaseOrderDocument document) {
        boolean zeroDollar = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        for (Iterator itemIter = document.getItems().iterator(); itemIter.hasNext();) {
            PurchaseOrderItem poi = (PurchaseOrderItem) itemIter.next();
            KualiDecimal encumberedQuantity = poi.getItemOutstandingEncumberedQuantity() == null ? zero : poi.getItemOutstandingEncumberedQuantity();
            if (encumberedQuantity.compareTo(zero) == 1) {
                zeroDollar = false;
                break;
            }
        }
        if (zeroDollar) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_NO_ITEMS_TO_INVOICE);
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return zeroDollar;
    }
    
    boolean processPaymentRequestDateValidationForContinue(PaymentRequestDocument document){       
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        //invoice date validation
        java.sql.Date invoiceDate = document.getInvoiceDate();
        if (ObjectUtils.isNotNull(invoiceDate) && SpringContext.getBean(PaymentRequestService.class).isInvoiceDateAfterToday(invoiceDate)) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.INVOICE_DATE, PurapKeyConstants.ERROR_INVALID_INVOICE_DATE);
            valid &= false;
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }
    
    boolean validatePaymentRequestDates(PaymentRequestDocument document) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        //pay date in the past validation
        if (isPayDateInThePast(document.getPaymentRequestPayDate())) {
            valid &= false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, PurapKeyConstants.ERROR_INVALID_PAY_DATE);
        }
        //pay date more than 60 days warning
        if (isPayDateOver60Days(document)) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_60_DAYS);
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }
    
    private boolean isPayDateInThePast(Date paymentRequestPayDate) {
        boolean valid = true;
        if (isDateInPast(paymentRequestPayDate)) {
            valid &= false;
        }
        return valid;
    }
    /**
     * 
     * This method checks whether the total of the items' extended price, excluding the item types that can be
     * negative match with the vendor invoice amount that the user entered at the beginning of the preq creation,
     * and if they don't match, the app will just print a warning to the page that the amounts don't match.
     * 
     * @param document
     */
    public void validateTotals(PaymentRequestDocument document) {
        String securityGroup = (String)PurapConstants.ITEM_TYPE_SYSTEM_PARAMETERS_SECURITY_MAP.get(PurapConstants.PAYMENT_REQUEST_DOCUMENT_DOC_TYPE);
        KualiParameterRule allowsNegativeRule = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterRule(securityGroup, PurapConstants.ITEM_ALLOWS_NEGATIVE);
        if ((ObjectUtils.isNull(document.getVendorInvoiceAmount())) || 
            (this.getTotalExcludingItemTypes(document.getItems(), allowsNegativeRule.getParameterValueSet()).compareTo(document.getVendorInvoiceAmount()) != 0)) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.MESSAGE_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID);
        }
        flagLineItemTotals(document.getItems());
    }
    
    private KualiDecimal getTotalExcludingItemTypes(List<PurchasingApItem> itemList, Set excludedItemTypes) {
        KualiDecimal total = zero;
        for (PurchasingApItem item : itemList) {
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
    
    private void flagLineItemTotals(List<PurchasingApItem> itemList) {
        for (PurchasingApItem purApItem : itemList) {
            PaymentRequestItem item = (PaymentRequestItem)purApItem;
            if (item.getItemQuantity()!= null) {
                if(item.calculateExtendedPrice().compareTo(item.getExtendedPrice())!=0) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_PAYMENT_REQUEST_ITEM_TOTAL_NOT_EQUAL, item.getItemIdentifierString());
            }
        }
    }
    }
    
    /**
     * This method performs item validations for the rules that are only applicable to Payment Request Document.
     * In EPIC, we are also doing similar steps as in this method within the validateFormatter, which is
     * called upon Save. Therefore now we're also calling the same validations upon Save.
     * 
     * @param purapDocument
     * @return
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument)purapDocument;
        paymentRequestDocument.fixPreqItemReference();
        for (PurchasingApItem item : purapDocument.getItems() ) { 
            PaymentRequestItem preqItem = (PaymentRequestItem)item;
            valid &= validateEachItem(paymentRequestDocument, preqItem);
        }
        return valid;
    }

    private boolean validateEachItem(PaymentRequestDocument paymentRequestDocument, PaymentRequestItem item) {
        boolean valid = true;
            String identifierString = item.getItemIdentifierString();
            valid &= validateItem(paymentRequestDocument, item, identifierString);
        return valid;
    }
    
    public boolean validateItem(PaymentRequestDocument paymentRequestDocument, PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE)) { 
            valid &= validateItemTypeItems(item, identifierString); 
        } 
        else {
            valid &= validateNonItemTypeItems(item, identifierString);
        }
        valid &= validateItemWithoutAccounts(item, identifierString);
        valid &= validateItemAccounts(paymentRequestDocument, item, identifierString);
        return valid;
    }

    private boolean validateItemTypeItems(PaymentRequestItem item, String identifierString) {
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
        if (ObjectUtils.isNotNull(item.getExtendedPrice())&&item.getExtendedPrice().isPositive() && ObjectUtils.isNotNull(item.getPoOutstandingQuantity()) && item.getPoOutstandingQuantity().isPositive()) {

            // here we must require the user to enter some value for quantity if they want a credit amount associated
            if (ObjectUtils.isNull(item.getItemQuantity()) || item.getItemQuantity().isZero()) {
                // here we have a user not entering a quantity with an extended amount but the PO has a quantity... require user to
                // enter a quantity
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_QUANTITY_REQUIRED, ItemFields.INVOICE_QUANTITY, identifierString, ItemFields.OPEN_QUANTITY);
            }
        }

        // TODO: Is the following comment right ? It was copied directly from EPIC, but doesn't look right (service items ???).
        // check that service items (no quantity on PO item) are not trying to pay on a zero encumbrance amount (check only prior to
        // ap approval)
        if ( (ObjectUtils.isNull(item.getPaymentRequest().getPurapDocumentIdentifier())) || 
             (PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(item.getPaymentRequest().getStatusCode())) ) {
            if ( (ObjectUtils.isNull(item.getPoOutstandingQuantity()) || item.getPoOutstandingQuantity().isZero()) && 
                 ((item.getExtendedPrice() != null) && item.getExtendedPrice().isNonZero()) ) {
                if (item.getPoOutstandingAmount() == null || item.getPoOutstandingAmount().isZero()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMOUNT_ALREADY_PAID, identifierString);
                }
            }
        }

        return valid;
    }
    
    private boolean validateNonItemTypeItems(PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        if ( ObjectUtils.isNotNull(item.getItemUnitPrice()) && 
             item.getItemUnitPrice().compareTo(KualiDecimal.ZERO.bigDecimalValue()) != 0 &&
             StringUtils.isBlank(item.getItemDescription()) ) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, KFSKeyConstants.ERROR_REQUIRED, PurapConstants.ItemFields.DESCRIPTION + " of " + identifierString + KFSConstants.BLANK_SPACE);
        }
        return valid;
    }
    
    /**
     * This method validates that the item must contain at least one account
     * 
     * @param item
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
        
    public boolean validateItemAccounts(PaymentRequestDocument paymentRequestDocument, PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        List<PurApAccountingLine> accountingLines = item.getSourceAccountingLines();
        for ( PurApAccountingLine accountingLine :  accountingLines ) {
            valid &= this.processReviewAccountingLineBusinessRules(paymentRequestDocument, accountingLine);
        }
        return valid;
    }
    
    public boolean validateCancel(PurchasingAccountsPayableDocument purapDocument) {
        Collection c = new ArrayList();
        boolean valid = true;
        PaymentRequestDocument pr = (PaymentRequestDocument)purapDocument;
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
    
    public boolean validateRouteFiscal(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        PaymentRequestDocument paymentRequest = (PaymentRequestDocument)purapDocument;
        //TODO: Double check that this rule is correct. During fiscal officer review, the status code is AFOA
        /*if (!StringUtils.equals(paymentRequest.getStatusCode(),PurapConstants.PaymentRequestStatuses.IN_PROCESS)) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_PAYMENT_REQUEST_NOT_IN_PROCESS);
            valid &= false;
        }*/
        valid &= validatePaymentRequestReview(paymentRequest);
        return valid;
    }

    boolean validatePaymentRequestReview(PaymentRequestDocument paymentRequest) {
        boolean valid = true;
        paymentRequest.fixPreqItemReference();
        //TODO: uncomment or replace this with a service invocation when Chris/Dan finished
        //the calculate method.
        //this.calculatePaymentRequest(paymentRequest, false);

        // if FY > current FY, warn user that payment will happen in current year
        //TODO: Is this really how we should get the "fiscal year" ?
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

            if ( ( ( item.getExtendedPrice() != null && item.getExtendedPrice().isNonZero()) && 
                   item.getItemType().isItemTypeAboveTheLineIndicator() && 
                   ( ( !item.getItemType().isQuantityBasedGeneralLedgerIndicator() && (item.getPoOutstandingAmount() != null && item.getPoOutstandingAmount().isNonZero())) || 
                     ( item.getItemType().isQuantityBasedGeneralLedgerIndicator() && (item.getPoOutstandingQuantity() != null && item.getPoOutstandingQuantity().isNonZero())))) || 
                 ( ( ( item.getExtendedPrice() != null) && (item.getExtendedPrice().isNonZero())) && 
                   ( !item.getItemType().isItemTypeAboveTheLineIndicator()))) {
                // OK TO VALIDATE because we have extended price and an open encumberance on the PO item
                super.processItemValidation(paymentRequest);
                //This is calling the validations which in EPIC are located in validateFormatters, but in Kuali they should be covered
                //within the processItemValidation of this class.
                validateEachItem(paymentRequest, item);
            }
            else if ( ( item.getExtendedPrice() != null && 
                      item.getExtendedPrice().isNonZero() && 
                      item.getItemType().isItemTypeAboveTheLineIndicator() && 
                      ( ( !item.getItemType().isQuantityBasedGeneralLedgerIndicator() && 
                          (item.getPoOutstandingAmount() == null || item.getPoOutstandingAmount().isZero())) || 
                        ( item.getItemType().isQuantityBasedGeneralLedgerIndicator() && 
                          ( item.getPoOutstandingQuantity() == null || item.getPoOutstandingQuantity().isZero()))))) {
                // ERROR because we have extended price and no open encumberance on the PO item
                // this error should have been caught at an earlier level
                if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                    String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has extended price '" + item.getExtendedPrice() + "' but outstanding encumbered amount " + item.getPoOutstandingAmount();
                    LOG.error("validatePaymentRequestReview() " + error);
                    //TODO: I think here we should just display error instead of throwing PurError
                    throw new PurError(error);
                }
                else {
                    String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has extended price '" + item.getExtendedPrice() + "' but outstanding encumbered quantity " + item.getPoOutstandingQuantity();
                    LOG.error("validatePaymentRequestReview() " + error);
                    //TODO: I think here we should just display error instead of throwing PurError
                    throw new PurError(error);
                }
            }
            else {
                // not validating but ok
                String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has extended price '" + item.getExtendedPrice() + "'";
                if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                    if (!item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
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
    
    private boolean isDateInPast(Date compareDate) {
        boolean valid = true;
        if (compareDate != null) {
            Calendar c = SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentCalendar();
            c.set(Calendar.HOUR, 12);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.AM_PM, Calendar.AM);
            Timestamp timeNow = new Timestamp(c.getTime().getTime());
            Calendar c2 = SpringContext.getBean(DateTimeService.class, "dateTimeService").getCalendar(compareDate);
            c2.set(Calendar.HOUR, 11);
            c2.set(Calendar.MINUTE, 59);
            c2.set(Calendar.SECOND, 59);
            c2.set(Calendar.MILLISECOND, 59);
            c2.set(Calendar.AM_PM, Calendar.PM);
            Timestamp testTime = new Timestamp(c2.getTime().getTime());
            if (timeNow.compareTo(testTime) > 0) {
                valid = false;
            }
        }
        return valid;
    }
    
    public boolean isPayDateOver60Days(PaymentRequestDocument paymentRequest) {
        if (paymentRequest.getPaymentRequestPayDate() != null) {
            // Calendar c is a holder for today's date + 60 days
            Calendar c = SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentCalendar();
            c.set(Calendar.HOUR, 12);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.AM_PM, Calendar.AM);
            c.add(Calendar.DATE, 60);
            Timestamp testTime = new Timestamp(c.getTime().getTime());
            // Calendar c2 is a holder for the paymentRequestPayDate
            Calendar c2 = SpringContext.getBean(DateTimeService.class, "dateTimeService").getCalendar(paymentRequest.getPaymentRequestPayDate());
            c2.set(Calendar.HOUR, 11);
            c2.set(Calendar.MINUTE, 59);
            c2.set(Calendar.SECOND, 59);
            c2.set(Calendar.MILLISECOND, 59);
            c2.set(Calendar.AM_PM, Calendar.PM);
            Timestamp payDate = new Timestamp(c2.getTime().getTime());
            // return whether paymentRequestPayDate is after today's date
            return (payDate.compareTo(testTime) > 0);
        }
        else {
            return false;
        }
    }

    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry);

        PaymentRequestDocument preq = (PaymentRequestDocument)accountingDocument;
        
        if (preq.isGenerateEncumbranceEntries()) {
            ((PurapGeneralLedgerService)SpringServiceLocator.getService(SpringServiceLocator.PURAP_GENERAL_LEDGER_SERVICE)).customizeGeneralLedgerPendingEntry(preq, 
                    accountingLine, explicitEntry, preq.getPurchaseOrderIdentifier(), GL_CREDIT_CODE, PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT, true);
        }
        else {
            ((PurapGeneralLedgerService)SpringServiceLocator.getService(SpringServiceLocator.PURAP_GENERAL_LEDGER_SERVICE)).customizeGeneralLedgerPendingEntry(preq, 
                    accountingLine, explicitEntry, preq.getPurchaseOrderIdentifier(), GL_DEBIT_CODE, PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT, false);
        }
        
        //PREQs do not wait for document final approval to post GL entries to the real table; here we are forcing them to be APPROVED
        explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);

    }
}
