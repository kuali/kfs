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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.PurchaseOrderSplitRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.VendorConstants.VendorTypes;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.kfs.vnd.service.PhoneNumberService;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Business rule(s) applicable to Purchase Order document.
 */
public class PurchaseOrderDocumentRule extends PurchasingDocumentRuleBase implements PurchaseOrderSplitRule, AddVendorToQuoteRule, AssignSensitiveDataRule {

    /**
     * Overrides the method in PurchasingDocumentRuleBase class in order to add validation for the Vendor Stipulation Tab. Tab
     * included on Purchase Order Documents is Vendor Stipulation.
     * 
     * @param purapDocument the purchase order document to be validated
     * @return boolean false when an error is found in any validation.
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processVendorStipulationValidation((PurchaseOrderDocument) purapDocument);

        return valid;
    }
    
    /**
     * @param purapDocument the purchase order document to be validated
     * @return boolean false when an error is found in any validation.
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#processItemValidation(org.kuali.kfs.module.purap.document.PurchasingDocument)
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);

        return valid;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#newIndividualItemValidation(java.lang.String, org.kuali.kfs.module.purap.businessobject.PurApItem, org.kuali.kfs.module.purap.businessobject.RecurringPaymentType)
     */
    @Override
    public boolean newIndividualItemValidation(PurchasingAccountsPayableDocument purapDocument, String documentType, PurApItem item) {
        boolean valid = super.newIndividualItemValidation(purapDocument, documentType, item);
        valid &= validateEmptyItemWithAccounts((PurchaseOrderItem) item, item.getItemIdentifierString());
        
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
        if (item.getItemType().isLineItemIndicator() && item.isItemDetailEmpty() && !item.isAccountListEmpty()) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_NOT_ALLOWED, identifierString);
        }

        return valid;
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
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#processVendorValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
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
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase.AccountingLineAction)
     */
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine) {
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);

        if (((PurchaseOrderDocument) financialDocument).isDocumentStoppedInRouteNode(NodeDetailEnum.INTERNAL_PURCHASING_REVIEW)) {
            // DO NOTHING: do not check that user owns acct lines; at this level, approvers can edit all detail on PO
            return true;
        }
        else {

            return false;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#commodityCodeIsRequired()
     */
    @Override
    protected boolean commodityCodeIsRequired() {
        return SpringContext.getBean(ParameterService.class).getIndicatorParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.validation.PurchaseOrderSplitRule#validateSplit(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    public boolean validateSplit(PurchaseOrderDocument po) {
        boolean valid = true;
        HashMap<String, List<PurchaseOrderItem>> categorizedItems = SpringContext.getBean(PurchaseOrderService.class).categorizeItemsForSplit((List<PurchaseOrderItem>)po.getItems());
        List<PurchaseOrderItem> movingPOItems = categorizedItems.get(PODocumentsStrings.ITEMS_MOVING_TO_SPLIT);
        List<PurchaseOrderItem> remainingPOItems = categorizedItems.get(PODocumentsStrings.ITEMS_REMAINING);
        if (movingPOItems.isEmpty()) {
            GlobalVariables.getErrorMap().putError(PurapConstants.SPLIT_PURCHASE_ORDER_TAB_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_SPLIT_ONE_ITEM_MUST_MOVE);
            valid &= false;
        }
        else if (remainingPOItems.isEmpty()) {
            GlobalVariables.getErrorMap().putError(PurapConstants.SPLIT_PURCHASE_ORDER_TAB_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_SPLIT_ONE_ITEM_MUST_REMAIN);
            valid &= false;
        }
        return valid;
    }
    
    public boolean processAddVendorBusinessRules (PurchaseOrderDocument document, PurchaseOrderVendorQuote vendorQuote) {
        boolean valid = true;
        valid &= isVendorQuoteActiveNotDebarredVendor(vendorQuote.getVendorHeaderGeneratedIdentifier(), vendorQuote.getVendorDetailAssignedIdentifier());
        valid &= vendorQuoteHasRequiredFields(vendorQuote);
        return valid;
    }
    
    private boolean isVendorQuoteActiveNotDebarredVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifer) {
        VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(vendorHeaderGeneratedIdentifier, vendorDetailAssignedIdentifer);    
        if (vendorDetail != null) {
            if (!vendorDetail.isActiveIndicator()) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_NAME, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_INACTIVE_VENDOR);
                return false;
            }
            else if (vendorDetail.isVendorDebarred()) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_NAME, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_DEBARRED_VENDOR);
                return false;
            }
        }
        return true;
    }
    
    private boolean vendorQuoteHasRequiredFields (PurchaseOrderVendorQuote vendorQuote) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        if ( StringUtils.isBlank(vendorQuote.getVendorName()) ) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_NAME, KFSKeyConstants.ERROR_REQUIRED, "Vendor Name");
            valid = false;
        }
        if (StringUtils.isBlank(vendorQuote.getVendorLine1Address())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_LINE_1_ADDR, KFSKeyConstants.ERROR_REQUIRED, "Vendor Line 1 Address");
            valid = false;
        }
        if (StringUtils.isBlank(vendorQuote.getVendorCityName())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_CITY_NAME, KFSKeyConstants.ERROR_REQUIRED, "Vendor City Name");
            valid = false;
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }
    
    /**
     * Check whether the specified sensitive data list is valid for assignment to the specified purchase order.
     * @param document the purchase order to have sensitive data assigned to 
     * @param sensitiveDatas the sensitive data list to be checked for assignment
     * @return true if all sensitive data entry in the list are active and unique; false otherwise
     * @see org.kuali.kfs.module.purap.document.validation.impl.AssignSensitiveDataRule#processAssignSensitiveDataBusinessRules(org.kuali.kfs.module.purap.document.PurchaseOrderDocument, java.util.List)
     */
    public boolean processAssignSensitiveDataBusinessRules (PurchaseOrderDocument document, List sensitiveDatas) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();        
        HashSet sdset = new HashSet();
        
        for (Object sdobj : sensitiveDatas) {
            SensitiveData sd = (SensitiveData)sdobj;
            if (!sd.isActive()) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS, PurapKeyConstants.ERROR_ASSIGN_INACTIVE_SENSITIVE_DATA, sd.getSensitiveDataDescription());
                valid = false;                
            }
            else if (!sdset.add(sd.getSensitiveDataCode())) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS, PurapKeyConstants.ERROR_ASSIGN_REDUNDANT_SENSITIVE_DATA, sd.getSensitiveDataDescription());
                valid = false;                                    
            }            
        }

        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }
    
}
