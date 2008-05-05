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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.VendorConstants.VendorTypes;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.PhoneNumberService;
import org.kuali.module.vendor.service.VendorService;

/**
 * Business rule(s) applicable to Purchase Order document.
 */
public class PurchaseOrderDocumentRule extends PurchasingDocumentRuleBase {

    /**
     * Overrides the method in PurchasingDocumentRuleBase class in order to add validation for the Vendor Stipulation Tab. Tab
     * included on Purchase Order Documents is Vendor Stipulation.
     * 
     * @param purapDocument the purchase order document to be validated
     * @return boolean false when an error is found in any validation.
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processAdditionalValidation((PurchasingDocument) purapDocument);
        valid &= processVendorStipulationValidation((PurchaseOrderDocument) purapDocument);

        return valid;
    }

    /**
     * Performs any validation for the Additional tab, but currently it only returns true. Someday we might be able to just remove
     * this.
     * 
     * @param purDocument the purchase order document to be validated
     * @return boolean true (always return true for now)
     */
    public boolean processAdditionalValidation(PurchasingDocument purDocument) {
        boolean valid = true;

        return valid;
    }

    /**
     * @param purapDocument the purchase order document to be validated
     * @return boolean false when an error is found in any validation.
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#processItemValidation(org.kuali.module.purap.document.PurchasingDocument)
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);
        valid &= validateTradeInAndDiscountCoexistence((PurchasingDocument) purapDocument);

        return valid;
    }
    
    /**
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#newIndividualItemValidation(java.lang.String, org.kuali.module.purap.bo.PurApItem, org.kuali.module.purap.bo.RecurringPaymentType)
     */
    @Override
    public boolean newIndividualItemValidation(PurchasingAccountsPayableDocument purapDocument, String documentType, PurApItem item) {
        boolean valid = true;
        valid &= validateEmptyItemWithAccounts((PurchaseOrderItem) item, item.getItemIdentifierString());
        if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
            valid &= validateItemForAmendment((PurchaseOrderItem) item, item.getItemIdentifierString());
        }
        
        return valid;
    }

    /**
     * Validates items for amendment.
     * 
     * @param item the item to be validated
     * @param identifierString the identifier string of the item to be validated
     * @return boolean true if it passes the validation and false otherwise.
     */
    private boolean validateItemForAmendment(PurchaseOrderItem item, String identifierString) {
        boolean valid = true;
        if ((item.getItemInvoicedTotalQuantity() != null) && (!(item.getItemInvoicedTotalQuantity()).isZero())) {
            if (item.getItemQuantity() == null) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_NULL, "Item Quantity", identifierString);
            }
            else if (item.getItemQuantity().compareTo(item.getItemInvoicedTotalQuantity()) < 0) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_INVALID, "Item Quantity", identifierString);
            }
        }

        if (item.getItemInvoicedTotalAmount() != null) {
            KualiDecimal total = item.getExtendedPrice();
            if ((total == null) || total.compareTo(item.getItemInvoicedTotalAmount()) < 0) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_INVALID_AMT, "Item Extended Price", identifierString);
            }
        }

        return valid;
    }

    /**
     * Validates that the item detail must not be empty if its account is not empty and its item type is ITEM.
     * 
     * @param item the item to be validated
     * @param identifierString the identifier string of the item to be validated
     * @return boolean false if it is an above the line item and the item detail is empty and the account list is not empty.
     */
    boolean validateEmptyItemWithAccounts(PurchaseOrderItem item, String identifierString) {
        boolean valid = true;
        if (item.getItemType().isItemTypeAboveTheLineIndicator() && item.isItemDetailEmpty() && !item.isAccountListEmpty()) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_NOT_ALLOWED, identifierString);
        }

        return valid;
    }

    /**
     * Validates that the purchase order cannot have both trade in and discount item.
     * 
     * @param purDocument the purchase order document to be validated
     * @return boolean false if trade in and discount both exist.
     */
    boolean validateTradeInAndDiscountCoexistence(PurchasingDocument purDocument) {
        boolean discountExists = false;
        boolean tradeInExists = false;

        for (PurApItem item : purDocument.getItems()) {
            if (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) {
                discountExists = true;
                if (tradeInExists) {
                    GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_TRADEIN_DISCOUNT_COEXISTENCE);

                    return false;
                }
            }
            else if (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)) {
                tradeInExists = true;
                if (discountExists) {
                    GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_TRADEIN_DISCOUNT_COEXISTENCE);

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Validation for the Stipulation tab.
     * 
     * @param poDocument the purchase order document to be validated
     * @return boolean false if the vendor stipulation description is blank.
     */
    public boolean processVendorStipulationValidation(PurchaseOrderDocument poDocument) {
        boolean valid = true;
        List<PurchaseOrderVendorStipulation> stipulations = poDocument.getPurchaseOrderVendorStipulations();
        for (int i = 0; i < stipulations.size(); i++) {
            PurchaseOrderVendorStipulation stipulation = stipulations.get(i);
            if (StringUtils.isBlank(stipulation.getVendorStipulationDescription())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_STIPULATION + "[" + i + "]." + PurapPropertyConstants.VENDOR_STIPULATION_DESCRIPTION, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Overrides the method in PurchasingDocumentRuleBase in order to add validations that are specific for Purchase Orders that
     * aren't required for Requisitions.
     * 
     * @param purapDocument the purchase order document to be validated
     * @return boolean false when there is a failed validation.
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#processVendorValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(PurapConstants.VENDOR_ERRORS);
        boolean valid = super.processVendorValidation(purapDocument);
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) purapDocument;
        // check to see if the vendor exists in the database, i.e. its ID is not null
        Integer vendorHeaderID = poDocument.getVendorHeaderGeneratedIdentifier();
        if (ObjectUtils.isNull(vendorHeaderID)) {
            valid = false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_NONEXIST_VENDOR);
        }
        if (StringUtils.isBlank(poDocument.getVendorCountryCode())) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.VENDOR_COUNTRY_CODE, KFSKeyConstants.ERROR_REQUIRED);
        }
        else if (poDocument.getVendorCountryCode().equals(KFSConstants.COUNTRY_CODE_UNITED_STATES)) {
            if (StringUtils.isBlank(poDocument.getVendorStateCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_STATE_CODE, KFSKeyConstants.ERROR_REQUIRED_FOR_US);
            }
            ZipcodeValidationPattern zipPattern = new ZipcodeValidationPattern();
            if (StringUtils.isBlank(poDocument.getVendorPostalCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, KFSKeyConstants.ERROR_REQUIRED_FOR_US, PODocumentsStrings.POSTAL_CODE);
            }
            else if (!zipPattern.matches(poDocument.getVendorPostalCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, PurapKeyConstants.ERROR_POSTAL_CODE_INVALID);
            }
        }
               
        
        // Do checks for alternate payee vendor.
        Integer alternateVendorHdrGeneratedId = poDocument.getAlternateVendorHeaderGeneratedIdentifier();
        Integer alternateVendorHdrDetailAssignedId = poDocument.getAlternateVendorDetailAssignedIdentifier();
        
        VendorDetail alternateVendor = SpringContext.getBean(VendorService.class).getVendorDetail(alternateVendorHdrGeneratedId,alternateVendorHdrDetailAssignedId);
        
        if (alternateVendor != null) {
            if (alternateVendor.isVendorDebarred()) {
                errorMap.putError(PurapPropertyConstants.ALTERNATE_VENDOR_NAME,PurapKeyConstants.ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_DEBARRED);
                valid &= false;
            }
            if (StringUtils.equals(alternateVendor.getVendorHeader().getVendorTypeCode(), VendorTypes.DISBURSEMENT_VOUCHER)) {
                errorMap.putError(PurapPropertyConstants.ALTERNATE_VENDOR_NAME,PurapKeyConstants.ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_DV_TYPE);
                valid &= false;
            }
            if (!alternateVendor.isActiveIndicator()) {
                errorMap.putError(PurapPropertyConstants.ALTERNATE_VENDOR_NAME,PurapKeyConstants.ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_INACTIVE,PODocumentsStrings.ALTERNATE_PAYEE_VENDOR);
                valid &= false;
            }
        }
        errorMap.clearErrorPath();
        return valid;
    }

    /**
     * Validate that if Vendor Id (VendorHeaderGeneratedId) is not empty, and tranmission method is fax, vendor fax number cannot be
     * empty and must be valid.
     * 
     * @param purDocument the purchase order document to be validated
     * @return boolean false if VendorHeaderGeneratedId is not empty, tranmission method is fax, and VendorFaxNumber is empty or
     *         invalid.
     */
    private boolean validateFaxNumberIfTransmissionTypeIsFax(PurchasingDocument purDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        if (ObjectUtils.isNotNull(purDocument.getVendorHeaderGeneratedIdentifier()) && purDocument.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.FAX)) {
            if (ObjectUtils.isNull(purDocument.getVendorFaxNumber()) || !SpringContext.getBean(PhoneNumberService.class).isValidPhoneNumber(purDocument.getVendorFaxNumber())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_PO_TRANSMISSION_TYPE);
                valid &= false;
            }
        }
        GlobalVariables.getErrorMap().clearErrorPath();

        return valid;
    }

    /**
     * Validate that if the PurchaseOrderTotalLimit is not null then the TotalDollarAmount cannot be greater than the
     * PurchaseOrderTotalLimit.
     * 
     * @param purDocument the purchase order document to be validated
     * @return True if the TotalDollarAmount is less than the PurchaseOrderTotalLimit. False otherwise.
     */
    public boolean validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(PurchasingDocument purDocument) {
        boolean valid = true;
        KualiDecimal totalAmount = ((AmountTotaling) purDocument).getTotalDollarAmount();
        if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderTotalLimit()) && ObjectUtils.isNotNull(totalAmount)) {
            if (totalAmount.isGreaterThan(purDocument.getPurchaseOrderTotalLimit())) {
                valid &= false;
                GlobalVariables.getMessageList().add(PurapKeyConstants.PO_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT);
            }
        }

        return valid;
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase in order to check that if the document will stop in Internal
     * Purchasing Review node, then return true.
     * 
     * @param financialDocument the purchase order document to be validated
     * @param accountingLine the accounting line to be validated
     * @param action the AccountingLineAction enum that indicates what is being done to an accounting line
     * @return boolean true if the document will stop in Internal Purchasing Review node, otherwise return the result of the
     *         checkAccountingLineAccountAccessibility in PurapAccountingDocumentRuleBase.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase.AccountingLineAction)
     */
    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);

        if (((PurchaseOrderDocument) financialDocument).isDocumentStoppedInRouteNode(NodeDetailEnum.INTERNAL_PURCHASING_REVIEW)) {
            // DO NOTHING: do not check that user owns acct lines; at this level, approvers can edit all detail on PO
            return true;
        }
        else {

            return super.checkAccountingLineAccountAccessibility(financialDocument, accountingLine, action);
        }
    }
    
    /**
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#commodityCodeIsRequired()
     */
    @Override
    protected boolean commodityCodeIsRequired() {
        return SpringContext.getBean(ParameterService.class).getIndicatorParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
    }

}
