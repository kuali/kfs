/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.ItemFields;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAmountPositiveValidation;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineDataDictionaryValidation;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineValuesAllowedValidationHutch;
import org.kuali.kfs.sys.document.validation.impl.BusinessObjectDataDictionaryValidation;
import org.kuali.kfs.sys.document.validation.impl.CompositeValidation;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestProcessItemValidation extends GenericValidation {

    private PurapService purapService;
    private PurApItem itemForValidation;
    private AttributedDocumentEvent event;
    private CompositeValidation reviewAccountingLineValidation;
    private PaymentRequestDocument preqDocument;
    private PurApAccountingLine preqAccountingLine;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        this.event = event;
        
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument)event.getDocument();
        PaymentRequestItem preqItem = (PaymentRequestItem) itemForValidation;
        
        valid &= validateEachItem(paymentRequestDocument, preqItem);
        
        return valid;

    }

    /**
     * Calls another validate item method and passes an identifier string from the item.
     * 
     * @param paymentRequestDocument - payment request document.
     * @param item
     * @return
     */
    protected boolean validateEachItem(PaymentRequestDocument paymentRequestDocument, PaymentRequestItem item) {
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
        if (!purapService.isFullDocumentEntryCompleted(paymentRequestDocument)) {
            if (item.getItemType().isLineItemIndicator()) {
                valid &= validateAboveTheLineItems(item, identifierString,paymentRequestDocument.isReceivingDocumentRequiredIndicator());
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
    protected boolean validateAboveTheLineItems(PaymentRequestItem item, String identifierString, boolean isReceivingDocumentRequiredIndicator) {
        boolean valid = true;
        // Currently Quantity is allowed to be NULL on screen;
        // must be either a positive number or NULL for DB
        MessageMap errorMap = GlobalVariables.getMessageMap();
        errorMap.clearErrorPath();
        
        if (ObjectUtils.isNotNull(item.getItemQuantity())) {
            if (item.getItemQuantity().isNegative()) {
                // if quantity is negative give an error
                valid = false;
                errorMap.putError(PurapConstants.ITEM_TAB_ERRORS, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.INVOICE_QUANTITY, identifierString);
            }
            if (!isReceivingDocumentRequiredIndicator){
                if (item.getPoOutstandingQuantity().isLessThan(item.getItemQuantity())) {
                    valid = false;
                   errorMap.putError(PurapConstants.ITEM_TAB_ERRORS, PurapKeyConstants.ERROR_ITEM_QUANTITY_TOO_MANY, ItemFields.INVOICE_QUANTITY, identifierString, ItemFields.OPEN_QUANTITY);
                }
            }
        }
        if (ObjectUtils.isNotNull(item.getExtendedPrice()) && item.getExtendedPrice().isPositive() && ObjectUtils.isNotNull(item.getPoOutstandingQuantity()) && item.getPoOutstandingQuantity().isPositive()) {

            // here we must require the user to enter some value for quantity if they want a credit amount associated
            if (ObjectUtils.isNull(item.getItemQuantity()) || item.getItemQuantity().isZero()) {
                // here we have a user not entering a quantity with an extended amount but the PO has a quantity...require user to
                // enter a quantity
                valid = false;
                errorMap.putError(PurapConstants.ITEM_TAB_ERRORS, PurapKeyConstants.ERROR_ITEM_QUANTITY_REQUIRED, ItemFields.INVOICE_QUANTITY, identifierString, ItemFields.OPEN_QUANTITY);
            }
        }

        // check that non-quantity based items are not trying to pay on a zero encumbrance amount (check only prior to ap approval)
        if ((ObjectUtils.isNull(item.getPaymentRequest().getPurapDocumentIdentifier())) || (PurapConstants.PaymentRequestStatuses.APPDOC_IN_PROCESS.equals(item.getPaymentRequest().getApplicationDocumentStatus()))) {
// RICE20 : needed? :  !purapService.isFullDocumentEntryCompleted(item.getPaymentRequest())) {
            if ((item.getItemType().isAmountBasedGeneralLedgerIndicator()) && ((item.getExtendedPrice() != null) && item.getExtendedPrice().isNonZero())) {
                if (item.getPoOutstandingAmount() == null || item.getPoOutstandingAmount().isZero()) {
                    valid = false;
                    errorMap.putError(PurapConstants.ITEM_TAB_ERRORS, PurapKeyConstants.ERROR_ITEM_AMOUNT_ALREADY_PAID, identifierString);
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
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_INCOMPLETE, identifierString);
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
            if (accountingLine.getAmount().isZero()) {
                if (!canApproveAccountingLinesWithZeroAmount()) {
                    GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_AMOUNT_INVALID, itemForValidation.getItemIdentifierString());
                    valid &= false;
                }
            }
            valid &= reviewAccountingLineValidation(paymentRequestDocument, accountingLine);            
            accountTotal = accountTotal.add(accountingLine.getAmount());
        }
        if (purapService.isFullDocumentEntryCompleted((PaymentRequestDocument) paymentRequestDocument)) {
            // check amounts not percent after full entry
            if (accountTotal.compareTo(itemTotal) != 0) {
                valid = false;
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_AMOUNT_TOTAL, identifierString);
            }
        }
        return valid;
    }

    public CompositeValidation getReviewAccountingLineValidation() {
        return reviewAccountingLineValidation;
    }

    public void setReviewAccountingLineValidation(CompositeValidation reviewAccountingLineValidation) {
        this.reviewAccountingLineValidation = reviewAccountingLineValidation;
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

    protected boolean reviewAccountingLineValidation(PaymentRequestDocument document, PurApAccountingLine accountingLine){
        boolean valid = true;
        List<Validation> gauntlet = new ArrayList<Validation>();
        this.preqDocument = document;
        this.preqAccountingLine = accountingLine;
        
        createGauntlet(reviewAccountingLineValidation);
        
        for (Validation validation : gauntlet) {
            valid &= validation.validate(event);
        }

        return valid;
    }
    
    protected void createGauntlet(CompositeValidation validation) {
        for (Validation val : validation.getValidations()) {
            if (val instanceof CompositeValidation) {
                createGauntlet((CompositeValidation)val);
            } else if (val instanceof BusinessObjectDataDictionaryValidation) {
                addParametersToValidation((BusinessObjectDataDictionaryValidation)val);
            } else if (val instanceof AccountingLineAmountPositiveValidation) {
                addParametersToValidation((AccountingLineAmountPositiveValidation)val);
            } else if (val instanceof AccountingLineDataDictionaryValidation) {
                addParametersToValidation((AccountingLineDataDictionaryValidation)val);
            } else if (val instanceof AccountingLineValuesAllowedValidationHutch) {
                addParametersToValidation((AccountingLineValuesAllowedValidationHutch)val);
            } else {
                throw new IllegalStateException("Validations in the PaymentRequestProcessItemValidation must contain specific instances of validation");
            }
        }
    }

    /**
     * checks if an accounting line with zero dollar amount can be approved.  This will check
     * the system parameter APPROVE_ACCOUNTING_LINES_WITH_ZERO_DOLLAR_AMOUNT_IND and determines if the
     * line can be approved or not.
     * 
     * @return true if the system parameter value is Y else returns N.
     */
    public boolean canApproveAccountingLinesWithZeroAmount() {
        boolean canApproveLine = false;
        
        // get parameter to see if accounting line with zero dollar amount can be approved.
        String approveZeroAmountLine = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.APPROVE_ACCOUNTING_LINES_WITH_ZERO_DOLLAR_AMOUNT_IND);
        
        if ("Y".equalsIgnoreCase(approveZeroAmountLine)) {
            return true;
        }
        
        return canApproveLine;
    }
    
    protected void addParametersToValidation(BusinessObjectDataDictionaryValidation validation) {
        validation.setBusinessObjectForValidation(this.preqAccountingLine);
    }

    protected void addParametersToValidation(AccountingLineAmountPositiveValidation validation) {
        validation.setAccountingDocumentForValidation(this.preqDocument);
        validation.setAccountingLineForValidation(this.preqAccountingLine);
    }

    protected void addParametersToValidation(AccountingLineDataDictionaryValidation validation) {
        validation.setAccountingLineForValidation(this.preqAccountingLine);
    }

    protected void addParametersToValidation(AccountingLineValuesAllowedValidationHutch validation) {        
        validation.setAccountingDocumentForValidation(this.preqDocument);
        validation.setAccountingLineForValidation(this.preqAccountingLine);
    }

    /**
     * Gets the event attribute. 
     * @return Returns the event.
     */
    protected AttributedDocumentEvent getEvent() {
        return event;
    }

    /**
     * Sets the event attribute value.
     * @param event The event to set.
     */
    protected void setEvent(AttributedDocumentEvent event) {
        this.event = event;
    }

    /**
     * Gets the preqDocument attribute. 
     * @return Returns the preqDocument.
     */
    protected PaymentRequestDocument getPreqDocument() {
        return preqDocument;
    }

    /**
     * Sets the preqDocument attribute value.
     * @param preqDocument The preqDocument to set.
     */
    protected void setPreqDocument(PaymentRequestDocument preqDocument) {
        this.preqDocument = preqDocument;
    }

    /**
     * Gets the preqAccountingLine attribute. 
     * @return Returns the preqAccountingLine.
     */
    protected PurApAccountingLine getPreqAccountingLine() {
        return preqAccountingLine;
    }

    /**
     * Sets the preqAccountingLine attribute value.
     * @param preqAccountingLine The preqAccountingLine to set.
     */
    protected void setPreqAccountingLine(PurApAccountingLine preqAccountingLine) {
        this.preqAccountingLine = preqAccountingLine;
    }

}
