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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.PurapConstants.ItemFields;
import org.kuali.kfs.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.document.validation.AddPurchasingCapitalAssetLocationRule;
import org.kuali.kfs.module.purap.document.validation.AddPurchasingItemCapitalAssetRule;
import org.kuali.kfs.module.purap.document.validation.ChangeSystemPurapRule;
import org.kuali.kfs.module.purap.document.validation.SelectSystemPurapRule;
import org.kuali.kfs.module.purap.document.validation.UpdateCamsViewPurapRule;
import org.kuali.kfs.module.purap.document.validation.ValidateCapitalAssetsForAutomaticPurchaseOrderRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business rule(s) applicable to Purchasing document.
 */
public class PurchasingDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase implements ValidateCapitalAssetsForAutomaticPurchaseOrderRule, ChangeSystemPurapRule, SelectSystemPurapRule, UpdateCamsViewPurapRule, AddPurchasingItemCapitalAssetRule, AddPurchasingCapitalAssetLocationRule {

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add validations for Payment Info and Delivery tabs.
     * 
     * @param purapDocument the purchasing document to be validated
     * @return boolean false if there is any validation that fails.
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processPaymentInfoValidation((PurchasingDocument) purapDocument);
        valid &= processDeliveryValidation((PurchasingDocument) purapDocument);
        valid &= processCapitalAssetValidation((PurchasingDocument) purapDocument);
        valid &= processUpdateCamsViewPurapBusinessRules(purapDocument);
        
        return valid;
    }

    public boolean processCapitalAssetValidation(PurchasingDocument purchasingDocument) {
        GlobalVariables.getErrorMap().clearErrorPath();
        boolean valid = true;
        //We only need to do capital asset validations if the capital asset system type
        //code is not blank.
        if (StringUtils.isNotBlank(purchasingDocument.getCapitalAssetSystemTypeCode())) {
            
            valid &= SpringContext.getBean(CapitalAssetBuilderModuleService.class).validatePurchasingData(purchasingDocument);

            // FIXME hjs move this to cab module service
            // validate complete location addresses
            if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                for (CapitalAssetSystem system : purchasingDocument.getPurchasingCapitalAssetSystems()) {
                    for (CapitalAssetLocation location : system.getCapitalAssetLocations()) {
                        valid &= SpringContext.getBean(PurchasingService.class).checkCapitalAssetLocation(location);
                    }
                }
            } else if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM)) {
                CapitalAssetSystem system = purchasingDocument.getPurchasingCapitalAssetSystems().get(0);
                for (CapitalAssetLocation location : system.getCapitalAssetLocations()) {
                        valid &= SpringContext.getBean(PurchasingService.class).checkCapitalAssetLocation(location);
                    }
                }
            
        }
        return valid;
    }

    /**
     * Overrides to do the validations for commodity codes to prevent 
     * the users to try to save invalid commodity codes to the database
     * which could potentially throw SQL exception when the integrity
     * constraints are violated.
     * 
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean valid = true;
        
        int i = 0;
        for (PurApItem item : ((PurchasingDocument) document).getItems()) {
            GlobalVariables.getErrorMap().clearErrorPath();
            GlobalVariables.getErrorMap().addToErrorPath("document.item[" + i + "]");
            // This validation is applicable to the above the line items only.
            if (item.getItemType().isLineItemIndicator()) {
                item.refreshReferenceObject(PurapPropertyConstants.COMMODITY_CODE);
                valid &= validateCommodityCodes(item, commodityCodeIsRequired());
            }
            GlobalVariables.getErrorMap().removeFromErrorPath("document.item[" + i + "]");
            i++;
        }
        return valid;
    }
    
    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to also invoke the validateAccountNotExpired for each of
     * the accounts.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processAccountValidation(org.kuali.kfs.sys.document.AccountingDocument,
     *      java.util.List, java.lang.String)
     */
    @Override
    public boolean processAccountValidation(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        boolean valid = true;
        for (PurApAccountingLine accountingLine : purAccounts) {
            boolean notExpired = this.validateAccountNotExpired(accountingLine);
            if (!notExpired) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, itemLineNumber + " has ", accountingLine.getAccount().getAccountNumber());
            }
        }
        valid &= super.processAccountValidation(accountingDocument, purAccounts, itemLineNumber);

        return valid;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#newIndividualItemValidation(boolean, java.lang.String, org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    @Override
    public boolean newIndividualItemValidation(PurchasingAccountsPayableDocument purapDocument, String documentType, PurApItem item) {
        boolean valid = true;
        valid &= super.newIndividualItemValidation(purapDocument, documentType, item);
        //TODO: See if we really need this next line of refresh, if so then uncomment out the next line.
        //purapDocument.refreshReferenceObject(PurapPropertyConstants.RECURRING_PAYMENT_TYPE);        
        String recurringPaymentTypeCode = ((PurchasingDocument)purapDocument).getRecurringPaymentTypeCode(); 
        valid &= SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateItemCapitalAssetWithErrors(recurringPaymentTypeCode, item, false);
        valid &= validateUnitOfMeasure(item);       
        valid &= validateItemUnitPrice(item);      
        
        if (item.getItemType().isLineItemIndicator()) {
            valid &= validateItemDescription(item);
            valid &= validateItemQuantity(item);
            valid &= validateCommodityCodes(item, commodityCodeIsRequired());      
            if (((PurchasingDocument)purapDocument).isReceivingDocumentRequiredIndicator()){
                if (item.getItemType().isAmountBasedGeneralLedgerIndicator()){
                    GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_REQUIRED);
                    valid &= false;
                }
            }
        }
        else {
            // No accounts can be entered on below-the-line items that have no unit cost.
            valid &= validateBelowTheLineItemNoUnitCost(item); 
        }
        
        return valid;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#newProcessItemValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean newProcessItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.newProcessItemValidation(purapDocument);
        valid &= validateTotalCost((PurchasingDocument) purapDocument);
        valid &= validateContainsAtLeastOneItem((PurchasingDocument) purapDocument);
        valid &= validateTradeIn(purapDocument);
        
        return valid;
    }
    
    /**
     * Predicate to do a parameter lookup and tell us whether a commodity code is required.
     * Override in child classes. 
     * 
     * @return      True if a commodity code is required.
     */
    protected boolean commodityCodeIsRequired() {
        return false;
    }


    /**
     * Validates that if the item unit price is null and the source accounting lines is not empty, add error message and return
     * false.
     * 
     * @param item the item to be validated
     * @return boolean false if the item unit price is null and the source accounting lines is not empty.
     */
    public boolean validateBelowTheLineItemNoUnitCost(PurApItem item) {
        if (ObjectUtils.isNull(item.getItemUnitPrice()) && ObjectUtils.isNotNull(item.getSourceAccountingLines()) && !item.getSourceAccountingLines().isEmpty()) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE_NO_UNIT_COST, item.getItemIdentifierString());

            return false;
        }

        return true;
    }
    
    protected String getDocumentTypeLabel(String documentTypeName) {
        try {
            return KNSServiceLocator.getWorkflowInfoService().getDocType(documentTypeName).getDocTypeLabel();
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Caught Exception trying to get Workflow Document Type", e);
        }
    }

    /**
     * Validates that the document contains at least one item.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the document does not contain at least one item.
     */
    public boolean validateContainsAtLeastOneItem(PurchasingDocument purDocument) {
        boolean valid = false;
        for (PurApItem item : purDocument.getItems()) {
            if (!((PurchasingItemBase) item).isEmpty() && item.getItemType().isLineItemIndicator()) {

                return true;
            }
        }
        String documentType = getDocumentTypeLabel(purDocument.getDocumentHeader().getWorkflowDocument().getDocumentType());

        if (!valid) {
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);
        }

        return valid;
    }
    
    /**
     * Checks that a description was entered for the item.
     * 
     * @param item
     * @return
     */
    public boolean validateItemDescription(PurApItem item) {
        boolean valid = true;      
        if (StringUtils.isEmpty(item.getItemDescription())) {
            valid = false;
            String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                                    getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).
                                    getAttributeDefinition(PurapPropertyConstants.ITEM_DESCRIPTION).getLabel();
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_DESCRIPTION, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + item.getItemIdentifierString());
        }
        return valid;
    }

    /**
     * Validates the unit price for all applicable item types. It validates that the unit price field was
     * entered on the item, and that the price is in the right range for the item type.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if there is any validation that fails.
     */
    public boolean validateItemUnitPrice(PurApItem item) {
        boolean valid = true;
        if (item.getItemType().isLineItemIndicator()) {
            if (ObjectUtils.isNull(item.getItemUnitPrice())) {
                valid = false;
                String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                                        getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).
                                        getAttributeDefinition(PurapPropertyConstants.ITEM_UNIT_PRICE).getLabel();
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + item.getItemIdentifierString());
            }
        }    

        if (ObjectUtils.isNotNull(item.getItemUnitPrice())) {
            if ((BigDecimal.ZERO.compareTo(item.getItemUnitPrice()) > 0) && ((!item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) && (!item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is not full order discount or trade in items, don't allow negative unit price.
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.UNIT_COST, item.getItemIdentifierString());
                valid = false;
            }
            else if ((BigDecimal.ZERO.compareTo(item.getItemUnitPrice()) < 0) && ((item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) || (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is full order discount or trade in items, its unit price must be negative.
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_AMOUNT_NOT_BELOW_ZERO, ItemFields.UNIT_COST, item.getItemIdentifierString());
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add invocation to validateItemUnitPrice.
     * 
     * @param financialDocument the purchasing document to be validated
     * @param item the item to be validated
     * @return boolean false if there is any fail validation
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processAddItemBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {
        boolean valid = super.processAddItemBusinessRules(financialDocument, item);
        GlobalVariables.getErrorMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);
        valid &= validateItemUnitPrice(item);
        valid &= validateUnitOfMeasure(item);
        if (item.getItemType().isLineItemIndicator()) {
            valid &= validateItemDescription(item);
            valid &= validateItemQuantity(item);
            valid &= validateCommodityCodes(item, commodityCodeIsRequired());
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add invocation to validateItemUnitPrice.
     * 
     * @param financialDocument the purchasing document to be validated
     * @param item the item to be validated
     * @return boolean false if there is any fail validation
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processImportItemBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    public boolean processImportItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {
        boolean valid = super.processImportItemBusinessRules(financialDocument, item);
        GlobalVariables.getErrorMap().addToErrorPath(PurapConstants.ITEM_TAB_ERROR_PROPERTY);
        if (item.getItemType().isLineItemIndicator()) {
            valid &= validateItemDescription(item);
        }
        valid &= validateItemUnitPrice(item);
        valid &= validateUnitOfMeasureCodeExists(item);
        
        valid &= validateCommodityCodes(item, commodityCodeIsRequired());
        
        GlobalVariables.getErrorMap().removeFromErrorPath(PurapConstants.ITEM_TAB_ERROR_PROPERTY);

        return valid;
    }
    
    /**
     * Validates that the total cost must be greater or equal to zero.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the total cost is less than zero.
     */
    private boolean validateTotalCost(PurchasingDocument purDocument) {
        boolean valid = true;
        if (purDocument.getTotalDollarAmount().isLessThan(new KualiDecimal(BigDecimal.ZERO))) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_TOTAL_NEGATIVE);
        }

        return valid;
    }

    /**
     * Validates that if the item type is quantity based, the unit of measure is required.
     * 
     * @param item the item to be validated
     * @return boolean false if the item type is quantity based and the unit of measure is empty.
     */
    public boolean validateUnitOfMeasure(PurApItem item) {
        boolean valid = true;
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        // Validations for quantity based item type
        if (purItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
            String uomCode = purItem.getItemUnitOfMeasureCode();
            if (StringUtils.isEmpty(uomCode)) {
                valid = false;
                String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                                        getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).
                                        getAttributeDefinition(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE).
                                        getLabel();
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + item.getItemIdentifierString());
            }
        }

        return valid;
    }
    
    /**
     * Validates that if the item type is quantity based, that the unit of measure code is valid.
     * Looks for the UOM Code in the table. If it is not there, the code is invalid. 
     * This checking is needed only for imported items, since items added from new line could only 
     * choose an existing UOM from the drop-down list.
     * 
     * @param item the item to be validated
     * @return boolean false if the item type is quantity based and the unit of measure code is invalid.
     */
    private boolean validateUnitOfMeasureCodeExists(PurApItem item) {
        boolean valid = true;
        
        if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {            
            String uomCode = item.getItemUnitOfMeasureCode();
            Map<String,String> fieldValues = new HashMap<String,String>();
            fieldValues.put(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, uomCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(UnitOfMeasure.class, fieldValues) != 1) {
                String[] errorParams = { uomCode, "" + item.getItemLineNumber() };
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERRORS, PurapKeyConstants.ERROR_ITEMPARSER_INVALID_UOM_CODE, errorParams);
                valid = false;
            }  
        }

        return valid;
    }

    /**
     * Validates that if the item type is quantity based, the item quantity is required and if the item type is amount based, the
     * quantity is not allowed.
     * 
     * @param item the item to be validated
     * @return boolean false if there's any validation that fails.
     */
    public boolean validateItemQuantity(PurApItem item) {
        boolean valid = true;
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        if (purItem.getItemType().isQuantityBasedGeneralLedgerIndicator() && (ObjectUtils.isNull(purItem.getItemQuantity()))) {
            valid = false;
            String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                                    getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).
                                    getAttributeDefinition(PurapPropertyConstants.ITEM_QUANTITY).getLabel();            
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUANTITY, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + item.getItemIdentifierString());
        }
        else if (purItem.getItemType().isAmountBasedGeneralLedgerIndicator() && ObjectUtils.isNotNull(purItem.getItemQuantity())) {
            valid = false;
            String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                                    getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).
                                    getAttributeDefinition(PurapPropertyConstants.ITEM_QUANTITY).getLabel(); 
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUANTITY, PurapKeyConstants.ERROR_ITEM_QUANTITY_NOT_ALLOWED, attributeLabel + " in " + item.getItemIdentifierString());
        }

        return valid;
    }

    private boolean validateTradeIn(PurchasingAccountsPayableDocument purapDocument) {
        boolean isAssignedToTradeInItemFound = false;
        for (PurApItem item : purapDocument.getItems()) {
            // Refresh the item type for validation.
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (item.getItemType().isLineItemIndicator()) {
                if (item.getItemAssignedToTradeInIndicator()) {
                    isAssignedToTradeInItemFound = true;
                    break;
                }           
            }
        }
        if (!isAssignedToTradeInItemFound) {
            PurApItem tradeInItem = purapDocument.getTradeInItem();
            if (tradeInItem != null && tradeInItem.getItemUnitPrice() != null) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_TRADE_IN_NEEDS_TO_BE_ASSIGNED);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Validates whether the commodity code existed on the item, and if existed, whether the
     * commodity code on the item existed in the database, and if so, whether the commodity 
     * code is active. Display error if any of these 3 conditions are not met.
     * 
     * @param item  The PurApItem containing the commodity code to be validated.
     * @return boolean false if the validation fails and true otherwise.
     */
    protected boolean validateCommodityCodes(PurApItem item, boolean commodityCodeRequired) {
        boolean valid = true;
        String identifierString = item.getItemIdentifierString();
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        
        //This validation is only needed if the commodityCodeRequired system parameter is true
        if (commodityCodeRequired && StringUtils.isBlank(purItem.getPurchasingCommodityCode()) ) {
            //This is the case where the commodity code is required but the item does not currently contain the commodity code.
            valid = false;
            String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                                    getDataDictionary().getBusinessObjectEntry(CommodityCode.class.getName()).
                                    getAttributeDefinition(PurapPropertyConstants.ITEM_COMMODITY_CODE).getLabel();
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + identifierString);
        }
        else if (StringUtils.isNotBlank(purItem.getPurchasingCommodityCode())) {
            //Find out whether the commodity code has existed in the database
            Map<String,String> fieldValues = new HashMap<String, String>();
            fieldValues.put(PurapPropertyConstants.ITEM_COMMODITY_CODE, purItem.getPurchasingCommodityCode());
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(CommodityCode.class, fieldValues) != 1) {
                //This is the case where the commodity code on the item does not exist in the database.
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INVALID,  " in " + identifierString);
            }
            else {
                valid &= validateThatCommodityCodeIsActive(item);
            }
        }
        
        return valid;
    }
    
    protected boolean validateThatCommodityCodeIsActive(PurApItem item) {
        if (!((PurchasingItemBase)item).getCommodityCode().isActive()) {
            //This is the case where the commodity code on the item is not active.
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in " + item.getItemIdentifierString());
            return false;
        }
        return true;
    }
    
    /**
     * Validates the commodity code for the doDistribution to make sure that
     * the commodity code entered existed in the database, and if so, make sure
     * that the commodity code is active. If it either not existed in the
     * database or is not active, then display error.
     * 
     * @param purchasingCommodityCode The string representing the commodity code to be validated.
     * @return boolean false if it fails the validation and true otherwise.
     */
    public boolean validateCommodityCodesForDistribution(String purchasingCommodityCode) {
        boolean valid = true;
        //Find out whether the commodity code has existed in the database
        Map<String,String> fieldValues = new HashMap<String, String>();
        fieldValues.put(PurapPropertyConstants.ITEM_COMMODITY_CODE, purchasingCommodityCode);
        
        Collection<CommodityCode> result = (Collection<CommodityCode>)SpringContext.getBean(BusinessObjectService.class).findMatching(CommodityCode.class, fieldValues);
        if (result != null && result.size() > 0) {
            CommodityCode commodityCode = (CommodityCode)(result.iterator().next());
            if (!commodityCode.isActive()) {
                //This is the case where the commodity code on the item is not active.
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in distribute commodity code" );
            }
        }
        else {
            //This is the case where the commodity code on the item does not exist in the database.
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PUR_COMMODITY_CODE_INVALID,  " in distribute commodity code" );
        }
        return valid;
    }
    
    /**
     * Performs any validation for the Payment Info tab.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if there's any validation that fails.
     */
    public boolean processPaymentInfoValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().addToErrorPath(PurapConstants.PAYMENT_INFO_ERRORS);
        valid &= checkBeginDateBeforeEndDate(purDocument);
        if (valid && (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) || ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate()))) {
            if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNull(purDocument.getPurchaseOrderEndDate())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE);
                valid &= false;
            }
            else {
                if (ObjectUtils.isNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE);
                    valid &= false;
                }
            }
        }
        if (valid && ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
            if (ObjectUtils.isNull(purDocument.getRecurringPaymentTypeCode())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.RECURRING_PAYMENT_TYPE_CODE, PurapKeyConstants.ERROR_RECURRING_DATE_NO_TYPE);

                valid &= false;
            }
        }
        else if (valid && ObjectUtils.isNotNull(purDocument.getRecurringPaymentTypeCode())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_RECURRING_TYPE_NO_DATE);
            valid &= false;
        }

        //checks for when FY is set to next FY
        if (purDocument.isPostingYearNext()) {
            Integer currentFY = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
            Date closingDate = SpringContext.getBean(UniversityDateService.class).getLastDateOfFiscalYear(currentFY);

            //if recurring payment begin dates entered, begin date must be > closing date
            if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) &&
                   (purDocument.getPurchaseOrderBeginDate().before(closingDate) ||
                    purDocument.getPurchaseOrderBeginDate().equals(closingDate))) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_NEXT_FY_BEGIN_DATE_INVALID);
                valid &= false;
            }
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(PurapConstants.PAYMENT_INFO_ERRORS);
        return valid;
    }

    /**
     * Performs any validation for the Delivery tab.
     * If the delivery required date is not null, then it must be equal to
     * or after current date.
     * If the selected delivery campus does not exist in Campus Parameter table,
     * display error that it's an invalid selection.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean true if it passes the validation.
     */
    public boolean processDeliveryValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().addToErrorPath(PurapConstants.DELIVERY_TAB_ERRORS);
        if (ObjectUtils.isNotNull(purDocument.getDeliveryRequiredDate())) {
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            Date today = dateTimeService.getCurrentSqlDateMidnight();

            Date deliveryRequiredDate = purDocument.getDeliveryRequiredDate();

            if (today.after(deliveryRequiredDate)) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.DELIVERY_REQUIRED_DATE, PurapKeyConstants.ERROR_DELIVERY_REQUIRED_DATE_IN_THE_PAST);
            }
        }
        
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("campusCode", purDocument.getDeliveryCampusCode());
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        int match = businessObjectService.countMatching(CampusParameter.class, fieldValues);
        if (match < 1) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.DELIVERY_CAMPUS_CODE, PurapKeyConstants.ERROR_DELIVERY_CAMPUS_INVALID);
        }
        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to do all of the vendor validations. The method in
     * PurchasingAccountsPayableDocumentBase currently does not do any validation (it only returns true all the time).
     * 
     * @param purapDocument the purchasing document to be validated
     * @return boolean false if there's any validation that fails.
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processVendorValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processVendorValidation(purapDocument);
        PurchasingDocument purDocument = (PurchasingDocument) purapDocument;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        if (!purDocument.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B)) {
            
            //If there is a vendor and the transmission method is FAX and the fax number is blank, display
            //error that the fax number is required.
            if (purDocument.getVendorHeaderGeneratedIdentifier() != null && purDocument.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.FAX) && StringUtils.isBlank(purDocument.getVendorFaxNumber())) {
                valid &= false;
                String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                getDataDictionary().getBusinessObjectEntry(VendorAddress.class.getName()).
                getAttributeDefinition(VendorPropertyConstants.VENDOR_FAX_NUMBER).getLabel();
                errorMap.putError(VendorPropertyConstants.VENDOR_FAX_NUMBER, KFSKeyConstants.ERROR_REQUIRED, attributeLabel);
            }
            if (StringUtils.isNotBlank(purDocument.getVendorFaxNumber())) {
                PhoneNumberValidationPattern phonePattern = new PhoneNumberValidationPattern();
                if (!phonePattern.matches(purDocument.getVendorFaxNumber())) {
                    valid &= false;
                    errorMap.putError(VendorPropertyConstants.VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_INVALID);
                }
            }
        }

        VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(purDocument.getVendorHeaderGeneratedIdentifier(), purDocument.getVendorDetailAssignedIdentifier());
        if (ObjectUtils.isNull(vendorDetail))
            return valid;
        VendorHeader vendorHeader = vendorDetail.getVendorHeader();

        // make sure that the vendor is not debarred
        if (vendorDetail.isVendorDebarred()) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_DEBARRED_VENDOR);
        }

        // make sure that the vendor is of allowed type
        String allowedVendorType = SpringContext.getBean(ParameterService.class).getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapRuleConstants.PURAP_VENDOR_TYPE_ALLOWED_ON_REQ_AND_PO);
        if (ObjectUtils.isNotNull(vendorHeader) && ObjectUtils.isNotNull(vendorHeader.getVendorTypeCode()) && !vendorHeader.getVendorTypeCode().equals(allowedVendorType)) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INVALID_VENDOR_TYPE);
        }

        // make sure that the vendor is active
        if (!vendorDetail.isActiveIndicator()) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INACTIVE_VENDOR);
        }

        return valid;
    }


    /**
     * Implementation of the rule that if a document has a recurring payment begin date and end date, the begin date should come
     * before the end date. In EPIC, we needed to play around with this order if the fiscal year is the next fiscal year, since we
     * were dealing just with month and day, but we don't need to do that here; we're dealing with the whole Date object.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the begin date is not before the end date.
     */
    private boolean checkBeginDateBeforeEndDate(PurchasingDocument purDocument) {
        boolean valid = true;
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        Date beginDate = purDocument.getPurchaseOrderBeginDate();
        Date endDate = purDocument.getPurchaseOrderEndDate();
        if (ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if (dateTimeService.dateDiff(beginDate, endDate, false) <= 0) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END);
            }
        }

        return valid;
    }


    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to invoke the verifyAccountingLinePercent.
     * 
     * @param accountingDocument the purchasing document to be validated
     * @param originalAccountingLine the original accounting line
     * @param updatedAccountingLine the updated accounting line
     * @return boolean false if there's any validation that fails.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        if (!super.processCustomUpdateAccountingLineBusinessRules(accountingDocument, originalAccountingLine, updatedAccountingLine)) {

            return false;
        }
        //this is necessary because sometimes this method is called for baseline accounts, should not be needed once baseline is removed
        if(updatedAccountingLine instanceof PurApAccountingLine) {
            return verifyAccountingLinePercent((PurApAccountingLine) updatedAccountingLine);
        }//else
        return true;
    }


    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to also invoke the validateAccountNotExpired and
     * verifyAccountingLinePercent.
     * 
     * @param financialDocument the purchasing document to be validated
     * @param accountingLine the accounting line to be validated before being added
     * @return boolean false if there's any validation that fails.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    public boolean processAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, String collectionName) {
        boolean valid = validateAccountNotExpired(accountingLine);
        if (!valid) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, KFSConstants.EMPTY_STRING, accountingLine.getAccountNumber());
        }
        valid &= super.processAddAccountingLineBusinessRules(financialDocument, accountingLine, collectionName);
        if (!valid) {

            return false;
        }

        return verifyAccountingLinePercent((PurApAccountingLine) accountingLine);
    }

    /**
     * Validates that the account is not expired.
     * 
     * @param accountingLine The account to be validated.
     * @return boolean false if the account is expired.
     */
    private boolean validateAccountNotExpired(AccountingLine accountingLine) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        if (accountingLine.getAccount() != null && accountingLine.getAccount().isExpired()) {

            return false;
        }

        return true;
    }

    /**
     * Verifies that the accounting line percent is a whole number.
     * 
     * @param purapAccountingLine the accounting line to be validated
     * @return boolean false if the accounting line percent is not a whole number.
     */
    private boolean verifyAccountingLinePercent(PurApAccountingLine purapAccountingLine) {
        // make sure it's a whole number
        if (purapAccountingLine.getAccountLinePercent().stripTrailingZeros().scale() > 0) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ACCOUNTS, PurapKeyConstants.ERROR_PURCHASING_PERCENT_NOT_WHOLE, purapAccountingLine.getAccountLinePercent().toPlainString());

            return false;
        }

        return true;
    }
    
    /**
     * @see org.kuali.module.purap.rule.ValidateCapitalAssestsForAutomaticPurchaseOrderRule#processCapitalAssestsForAutomaticPurchaseOrderRule(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public boolean processCapitalAssetsForAutomaticPurchaseOrderRule(PurchasingAccountsPayableDocument purapDocument) {
        
        return SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateAutomaticPurchaseOrderRule(purapDocument);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.ChangeSystemPurapRule#processChangeSystemPurapBusinessRules(org.kuali.rice.kns.document.TransactionalDocument)
     */
    public boolean processChangeSystemPurapBusinessRules(TransactionalDocument document) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.UpdateViewPurapRule#processUpdateViewPurapBusinessRules(org.kuali.rice.kns.document.TransactionalDocument)
     */
    public boolean processUpdateCamsViewPurapBusinessRules(TransactionalDocument document) {
        PurchasingDocument purchasingdocument = (PurchasingDocument)document;
        return SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateUpdateCAMSView(purchasingdocument);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.SelectSystemPurapRule#processSelectSystemPurapBusinessRules(org.kuali.rice.kns.document.TransactionalDocument)
     */
    public boolean processSelectSystemPurapBusinessRules(TransactionalDocument document) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean processAddItemCapitalAssetBusinessRules(PurchasingDocument purchasingDocument, ItemCapitalAsset asset) {
        
        return SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateAddItemCapitalAssetBusinessRules(asset);
    }

    public boolean processAddCapitalAssetLocationBusinessRules(PurchasingDocument purchasingDocument, CapitalAssetLocation location) {
        boolean valid = true;
        // TODO: Move this into CABModuleService?
        // Retrieve and evaluate the parameter which determines whether location's address is required.
        // CHARTS_REQUIRING_LOCATIONS_ADDRESS_ON_(REQUISITION/PURCHASE_ORDER)
        Map<String, String> fieldValues = new HashMap<String, String>();
        List<Parameter> results = SpringContext.getBean(PurapService.class).getParametersGivenLikeCriteria(fieldValues);
        // If the location's address is required, enforce the validation of the individual fields of the address.
        return valid;
    }

}
