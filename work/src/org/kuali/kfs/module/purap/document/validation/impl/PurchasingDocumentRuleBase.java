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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.LookupService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.UnitOfMeasure;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.PurapConstants.ItemFields;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.CapitalAssetTransactionType;
import org.kuali.module.purap.bo.CapitalAssetTransactionTypeRule;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchasingItemBase;
import org.kuali.module.purap.bo.PurchasingItemCapitalAsset;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.rule.ValidateCapitalAssetsForAutomaticPurchaseOrderRule;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.bo.CommodityCode;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorHeader;
import org.kuali.module.vendor.service.VendorService;

/**
 * Business rule(s) applicable to Purchasing document.
 */
public class PurchasingDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase implements ValidateCapitalAssetsForAutomaticPurchaseOrderRule<PurchasingAccountsPayableDocument> {

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add validations for Payment Info and Delivery tabs.
     * 
     * @param purapDocument the purchasing document to be validated
     * @return boolean false if there is any validation that fails.
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processPaymentInfoValidation((PurchasingDocument) purapDocument);
        valid &= processDeliveryValidation((PurchasingDocument) purapDocument);

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add the validations for the unit price, unit of measure,
     * item quantity (for above the line items), the validateBelowTheLineItemNoUnitcost, validateTotalCost and
     * validateContainsAtLeastOneItem.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if there is any validation that fails.
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processItemValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);
        
        //This is needed so that we don't have to create system parameters for each of the subclasses of PurchaseOrderDocument.
        Class purapDocumentClass = null;
        if (purapDocument instanceof RequisitionDocument) {
            purapDocumentClass = purapDocument.getClass();
        }
        else {
            purapDocumentClass = PurchaseOrderDocument.class;
        }
        String commodityCodeIsRequired = SpringContext.getBean(ParameterService.class).getParameterValue(purapDocumentClass, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
        boolean commodityCodeRequired = false;
        
        List<PurApItem> itemList = purapDocument.getItems();
        int i = 0;
        boolean isNonQuantityItemFound = false;
        
        for (PurApItem item : itemList) {
            // Refresh the item type for validation.
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            GlobalVariables.getErrorMap().addToErrorPath("document.item[" + i + "]");
            String identifierString = item.getItemIdentifierString();
            valid &= validateItemUnitPrice(item);
            
            // This validation is applicable to the above the line items only.
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                valid &= validateItemQuantity(item);                
                if (commodityCodeIsRequired.equalsIgnoreCase("Y")) {
                    commodityCodeRequired = true;   
                }
                valid &= validateCommodityCodes(item, commodityCodeRequired);
            }
            else {
                // If the item is below the line, no accounts can be entered on below the line items
                // that have no unit cost
                valid &= validateBelowTheLineItemNoUnitCost(item);
            }
            GlobalVariables.getErrorMap().removeFromErrorPath("document.item[" + i + "]");
            i++;
            
            /**
             * Receiving required can not be set in on a req/po with all non-qty based items
             */
            if (!isNonQuantityItemFound){
                if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                    if (((PurchasingDocument)purapDocument).isReceivingDocumentRequiredIndicator()){
                        if (!item.getItemType().isQuantityBasedGeneralLedgerIndicator()){
                            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_REQUIRED);
                            valid &= false;
                            isNonQuantityItemFound = true; 
                        }
                    }
                }    
            }
        }
        
        valid &= validateTotalCost((PurchasingDocument) purapDocument);
        valid &= validateContainsAtLeastOneItem((PurchasingDocument) purapDocument);

        return valid;
    }

    /**
     * Overrides to do the validations for commodity codes to prevent 
     * the users to try to save invalid commodity codes to the database
     * which could potentially throw SQL exception when the integrity
     * constraints are violated.
     * 
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean valid = true;
        
        int i = 0;
        for (PurApItem item : ((PurchasingDocument) document).getItems()) {
            GlobalVariables.getErrorMap().clearErrorPath();
            GlobalVariables.getErrorMap().addToErrorPath("document.item[" + i + "]");
            // This validation is applicable to the above the line items only.
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
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
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processAccountValidation(org.kuali.kfs.document.AccountingDocument,
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
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#newIndividualItemValidation(boolean, java.lang.String, org.kuali.module.purap.bo.PurApItem)
     */
    @Override
    public boolean newIndividualItemValidation(PurchasingAccountsPayableDocument purapDocument, String documentType, PurApItem item) {
        boolean valid = true;
        valid &= super.newIndividualItemValidation(purapDocument, documentType, item);
        valid &= validateItemCapitalAssetWithErrors(purapDocument, item, false);
        valid &= validateUnitOfMeasure(item);       
        valid &= validateItemUnitPrice(item);      
        
        if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
            valid &= validateItemDescription(item);
            valid &= validateItemQuantity(item);
            valid &= validateCommodityCodes(item, commodityCodeIsRequired());           
        }
        else {
            // No accounts can be entered on below-the-line items that have no unit cost.
            valid &= validateBelowTheLineItemNoUnitCost(item); 
        }
        
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

    /**
     * Validates that the document contains at least one item.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the document does not contain at least one item.
     */
    public boolean validateContainsAtLeastOneItem(PurchasingDocument purDocument) {
        boolean valid = false;
        for (PurApItem item : purDocument.getItems()) {
            if (!((PurchasingItemBase) item).isEmpty() && item.getItemType().isItemTypeAboveTheLineIndicator()) {

                return true;
            }
        }
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(purDocument.getDocumentHeader().getWorkflowDocument().getDocumentType()).getLabel();

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
        if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
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
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processAddItemBusinessRules(org.kuali.kfs.document.AccountingDocument,org.kuali.module.purap.bo.PurApItem)
     */
    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {
        boolean valid = super.processAddItemBusinessRules(financialDocument, item);
        GlobalVariables.getErrorMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);
        if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
            valid &= validateItemDescription(item);
        }
        valid &= validateItemUnitPrice(item);
        GlobalVariables.getErrorMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add invocation to validateItemUnitPrice.
     * 
     * @param financialDocument the purchasing document to be validated
     * @param item the item to be validated
     * @return boolean false if there is any fail validation
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processImportItemBusinessRules(org.kuali.kfs.document.AccountingDocument,org.kuali.module.purap.bo.PurApItem)
     */
    public boolean processImportItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {
        boolean valid = super.processImportItemBusinessRules(financialDocument, item);
        GlobalVariables.getErrorMap().addToErrorPath(PurapConstants.ITEM_TAB_ERROR_PROPERTY);
        if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
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
            else if (!purItem.getCommodityCode().isActive()) {
                //This is the case where the commodity code on the item is not active.
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in " + identifierString);
            }
        }
        
        return valid;
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
        GlobalVariables.getErrorMap().removeFromErrorPath(PurapConstants.PAYMENT_INFO_ERRORS);

        return valid;
    }

    /**
     * Performs any validation for the Delivery tab.
     * If the delivery required date is not null, then it must be equal to
     * or after current date.
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

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to do all of the vendor validations. The method in
     * PurchasingAccountsPayableDocumentBase currently does not do any validation (it only returns true all the time).
     * 
     * @param purapDocument the purchasing document to be validated
     * @return boolean false if there's any validation that fails.
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processVendorValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
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
        String allowedVendorType = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapRuleConstants.PURAP_VENDOR_TYPE_ALLOWED_ON_REQ_AND_PO);
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
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        if (!super.processCustomUpdateAccountingLineBusinessRules(accountingDocument, originalAccountingLine, updatedAccountingLine)) {

            return false;
        }

        return verifyAccountingLinePercent((PurApAccountingLine) updatedAccountingLine);
    }


    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to also invoke the validateAccountNotExpired and
     * verifyAccountingLinePercent.
     * 
     * @param financialDocument the purchasing document to be validated
     * @param accountingLine the accounting line to be validated before being added
     * @return boolean false if there's any validation that fails.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean processAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean valid = validateAccountNotExpired(accountingLine);
        if (!valid) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, KFSConstants.EMPTY_STRING, accountingLine.getAccountNumber());
        }
        valid &= super.processAddAccountingLineBusinessRules(financialDocument, accountingLine);
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
        accountingLine.refreshNonUpdateableReferences();
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
     * @see org.kuali.module.purap.rule.ValidateCapitalAssestsForAutomaticPurchaseOrderRule#processCapitalAssestsForAutomaticPurchaseOrderRule(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public boolean processCapitalAssetsForAutomaticPurchaseOrderRule(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        List<PurApItem> itemList = purapDocument.getItems();
        for (PurApItem item : itemList) {
            valid &= validateItemCapitalAssetWithErrors(purapDocument, item, true);
        }
        // We don't actually need the error messages for the purposes of the APO.
        GlobalVariables.getErrorMap().clear();
        return valid;
    }
    
    /**
     * Wrapper to do Capital Asset validations, generating errors instead of warnings. Makes sure that 
     * the given item's data relevant to its later possible classification as a Capital Asset is internally consistent, 
     * by marshaling and calling the methods marked as Capital Asset validations. This implementation assumes that 
     * all object codes are valid (real) object codes.
     * 
     * @param item                      A PurchasingItemBase object
     * @param recurringPaymentType      The item's document's RecurringPaymentType
     * @param itemIdentifier            The item number (String)
     * @param apoCheck                  True if this check is for APO purposes
     * @return True if the item passes all Capital Asset validations
     */
    public boolean validateItemCapitalAssetWithErrors(PurchasingAccountsPayableDocument purapDocument, PurApItem item, boolean apoCheck) {
        PurchasingDocument purDocument = (PurchasingDocument)purapDocument;
        PurchasingItemBase purchasingItem = (PurchasingItemBase)item;
        //FIXME CAMS - hiding this validation for now until the CAMS tab is created (hjs)
//        return validateItemCapitalAsset(purDocument, purchasingItem, false);
        return true;
    }
    
    /**
     * Wrapper to do Capital Asset validations, generating warnings instead of errors. Makes sure that 
     * the given item's data relevant to its later possible classification as a Capital Asset is internally consistent, 
     * by marshaling and calling the methods marked as Capital Asset validations. This implementation assumes that 
     * all object codes are valid (real) object codes.
     * 
     * @param item                      A PurchasingItemBase object
     * @param recurringPaymentType      The item's document's RecurringPaymentType
     * @param itemIdentifier            The item number (String)
     * @return True if the item passes all Capital Asset validations
     */
    public boolean validateItemCapitalAssetWithWarnings(PurchasingAccountsPayableDocument purapDocument, PurApItem item) {
        PurchasingDocument purDocument = (PurchasingDocument)purapDocument;
        PurchasingItemBase purchasingItem = (PurchasingItemBase)item;
        //FIXME CAMS - hiding this validation for now until the CAMS tab is created (hjs)
//        return validateItemCapitalAsset(purDocument, purchasingItem, true);
        return true;
    }
    
    /**
     * Makes sure that the given item's data relevant to its later possible classification as a Capital Asset is 
     * internally consistent, by marshaling and calling the methods marked as Capital Asset validations.
     * This implementation assumes that all object codes are valid (real) object codes.
     * 
     * @param item                      A PurchasingItemBase object
     * @param recurringPaymentType      The item's document's RecurringPaymentType
     * @param warn                      A boolean which should be set to true if warnings are to be set on the calling document 
     *                                      for most of the validations, rather than errors.
     * @param itemIdentifier            The item number (String)
     * @return  True if the item passes all Capital Asset validations
     */
    protected boolean validateItemCapitalAsset(PurchasingDocument purDocument, PurchasingItemBase item, boolean warn) {
        boolean valid = true;
        String itemIdentifier = item.getItemIdentifierString();
        KualiDecimal itemQuantity = item.getItemQuantity();       
        HashSet<String> capitalOrExpenseSet = new HashSet<String>(); // For the first validation on every accounting line.
        
        String capitalAssetTransactionTypeCode = item.getCapitalAssetTransactionTypeCode();
        CapitalAssetTransactionType capitalAssetTransactionType = null;
        if (!StringUtils.isEmpty(capitalAssetTransactionTypeCode)) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE);
            capitalAssetTransactionType = item.getCapitalAssetTransactionType();
        }
        // Do the checks that depend on Accounting Line information.
        for( PurApAccountingLine accountingLine : item.getSourceAccountingLines() ) {
            // Because of ObjectCodeCurrent, we had to refresh this.
            accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            ObjectCode objectCode = accountingLine.getObjectCode();
            String capitalOrExpense = objectCodeCapitalOrExpense(objectCode);
            capitalOrExpenseSet.add(capitalOrExpense); // HashSets accumulate distinct values (and nulls) only.
            
            valid &= validateAccountingLinesNotCapitalAndExpense(capitalOrExpenseSet, warn, itemIdentifier, objectCode);
            if (warn) {
                valid &= validateLevelCapitalAssetIndication(itemQuantity, item.getExtendedPrice(), objectCode, itemIdentifier);
            } 
            else {
                validateLevelCapitalAssetIndication(itemQuantity, item.getExtendedPrice(), objectCode, itemIdentifier);
            }
            
            // Do the checks involving capital asset transaction type.
            if (StringUtils.isEmpty(capitalAssetTransactionTypeCode)) {
                valid &= validateCapitalAssetTransactionTypeIsRequired(objectCode, warn, itemIdentifier);
            }
            else {
                valid &= validateObjectCodeVersusTransactionType(objectCode, capitalAssetTransactionType, warn, itemIdentifier);
                valid &= validateQuantityVersusObjectCode(capitalAssetTransactionType, itemQuantity, objectCode, warn, itemIdentifier);
            }
        }
        // These checks do not depend on Accounting Line information, but do depend on transaction type.
        if (!StringUtils.isEmpty(capitalAssetTransactionTypeCode)) {
                purDocument.refreshReferenceObject(PurapPropertyConstants.RECURRING_PAYMENT_TYPE);
                RecurringPaymentType recurringPaymentType = purDocument.getRecurringPaymentType(); 
                valid &= validateCapitalAssetTransactionTypeVersusRecurrence(capitalAssetTransactionType, recurringPaymentType, warn, itemIdentifier);                
                valid &= validateCapitalAssetNumberRequirements(capitalAssetTransactionType, item.getPurchasingItemCapitalAssets(), warn, itemIdentifier);
            }
        return valid;
    }
    
    /**
     * Capital Asset validation: An item cannot have among its associated accounting lines both object codes 
     * that indicate it is a Capital Asset, and object codes that indicate that the item is not a Capital Asset.  
     * Whether an object code indicates that the item is a Capital Asset is determined by whether its level is 
     * among a specific set of levels that are deemed acceptable for such items.
     * 
     * @param capitalOrExpenseSet   A HashSet containing the distinct values of either "Capital" or "Expense"
     *                              that have been added to it.
     * @param warn                  A boolean which should be set to true if warnings are to be set on the calling document
     * @param itemIdentifier        A String identifying the item for error display
     * @param objectCode            An ObjectCode, for error display
     * @return  True if the given HashSet contains at most one of either "Capital" or "Expense"
     */
    public boolean validateAccountingLinesNotCapitalAndExpense(HashSet<String> capitalOrExpenseSet, boolean warn, String itemIdentifier, ObjectCode objectCode) {
        boolean valid = true;
        // If the set contains more than one distinct string, fail.
        if ( capitalOrExpenseSet.size() > 1 ) {
            if (warn) {
                String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.ERROR_ITEM_CAPITAL_AND_EXPENSE);
                warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                warning = StringUtils.replace(warning,"{1}",objectCode.getFinancialObjectCodeName());
                GlobalVariables.getMessageList().add(warning);
            } 
            else { 
                GlobalVariables.getErrorMap().putError(KFSConstants.FINANCIAL_OBJECT_LEVEL_CODE_PROPERTY_NAME, 
                        PurapKeyConstants.ERROR_ITEM_CAPITAL_AND_EXPENSE,itemIdentifier,objectCode.getFinancialObjectCodeName());              
            }
            valid &= false;
        }
        return valid;
    }
    

 
    /**
     * Capital Asset validation: If the item has a quantity, and has an extended price greater than or equal to 
     * the threshold for becoming a Capital Asset, and the  object code has one of a list of levels related to capital
     * assets, and indicating the possibility of the item being a capital asset, rather than an actual Capital Asset level, 
     * a warning should be given that a Capital Asset level should be used. Failure of this validation gives a warning 
     * both at the Requisition stage and at the Purchase Order stage.
     *  
     * @param itemQuantity          The quantity as a KualiDecimal
     * @param extendedPrice         The extended price as a KualiDecimal
     * @param objectCode            The ObjectCode
     * @param itemIdentifier        A String identifying the item
     * @return  False if the validation fails.
     */
    public boolean validateLevelCapitalAssetIndication(KualiDecimal itemQuantity, KualiDecimal extendedPrice, ObjectCode objectCode, String itemIdentifier) {
        boolean valid = true;
        if ((itemQuantity != null) && 
            (itemQuantity.isGreaterThan(KualiDecimal.ZERO))) {
            String capitalAssetPriceThreshold = SpringContext.getBean(ParameterService.class).getParameterValue(
                    ParameterConstants.PURCHASING_DOCUMENT.class, 
                    PurapParameterConstants.CapitalAsset.CAPITAL_ASSET_PRICE_THRESHOLD);
            if ((extendedPrice != null) &&
                (StringUtils.isNotEmpty(capitalAssetPriceThreshold)) &&
                (extendedPrice.isGreaterEqual(new KualiDecimal(capitalAssetPriceThreshold)))) {
                
                String possiblyCapitalAssetObjectCodeLevels = "";
                try {
                    possiblyCapitalAssetObjectCodeLevels = SpringContext.getBean(ParameterService.class).getParameterValue(
                            ParameterConstants.PURCHASING_DOCUMENT.class, 
                            PurapParameterConstants.CapitalAsset.POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS);
                    if (StringUtils.contains(possiblyCapitalAssetObjectCodeLevels,objectCode.getFinancialObjectLevel().getFinancialObjectLevelCode())) {
                        valid &= false;
                    }
                }
                catch (NullPointerException npe) {
                    valid &= false;
                }
                if (!valid) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(
                            PurapKeyConstants.WARNING_ABOVE_THRESHOLD_SUGESTS_CAPITAL_ASSET_LEVEL);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",capitalAssetPriceThreshold);                   
                    GlobalVariables.getMessageList().add(warning);
                }
            }
        }
        
        return valid;
    }
    
    /**
     * Capital Asset validation: If the item has no transaction type code, check whether any of the object codes 
     * on the item's associated accounting lines are of a sub-type which indicates that a transaction type code 
     * should be present.
     * 
     * @param objectCode        An ObjectCode
     * @param warn              A boolean which should be set to true if warnings are to be set on the calling document
     * @param itemIdentifier    The item number (String)
     * @return      False if a transaction type code should be present, according to the given object code's subtype.
     */
    public boolean validateCapitalAssetTransactionTypeIsRequired(ObjectCode objectCode, boolean warn, String itemIdentifier) {
        boolean valid = true;
        
        // For each object code sub-type in the AssetTransactionTypeRule table ...
        for (String subTypeCode : getAssetTransactionTypeDistinctObjectCodeSubtypes()) {
            // If the current object code sub-type code is the same as the one in our object code ...
            if (StringUtils.equals(subTypeCode,objectCode.getFinancialObjectSubTypeCode())) {
                // This validation fails.
                if (warn) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(
                            PurapKeyConstants.ERROR_ITEM_OBJECT_CODE_SUBTYPE_REQUIRES_TRAN_TYPE);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",objectCode.getFinancialObjectCodeName());
                    warning = StringUtils.replace(warning,"{2}",objectCode.getFinancialObjectSubType().getFinancialObjectSubTypeName());                    
                    GlobalVariables.getMessageList().add(warning);
                }
                else {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                            PurapKeyConstants.ERROR_ITEM_OBJECT_CODE_SUBTYPE_REQUIRES_TRAN_TYPE,
                            itemIdentifier,
                            objectCode.getFinancialObjectCodeName(),
                            objectCode.getFinancialObjectSubType().getFinancialObjectSubTypeName());                   
                }
                valid &= false;
            }
        }
        
        return valid;
    }
    
    /**
     * Capital Asset validation: If the item has a transaction type, check that the transaction type is acceptable 
     * for the object code sub-types of all the object codes on the associated accounting lines.
     * 
     * @param objectCode
     * @param capitalAssetTransactionType
     * @param warn                          A boolean which should be set to true if warnings are to be set on the calling document
     * @param itemIdentifier
     * @return
     */
    public boolean validateObjectCodeVersusTransactionType(ObjectCode objectCode, CapitalAssetTransactionType capitalAssetTransactionType, boolean warn, String itemIdentifier) {
        boolean valid = true;
        HashMap<String,String> tranTypeMap = new HashMap<String,String>();
        tranTypeMap.put(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE_CODE,capitalAssetTransactionType.getCapitalAssetTransactionTypeCode());
        List<CapitalAssetTransactionTypeRule> relevantRelations = (List<CapitalAssetTransactionTypeRule>)SpringContext.getBean(LookupService.class).findCollectionBySearch(CapitalAssetTransactionTypeRule.class, tranTypeMap);
        
        boolean found = false;
        for( CapitalAssetTransactionTypeRule relation : relevantRelations ) {
            if( StringUtils.equals(relation.getFinancialObjectSubTypeCode(),objectCode.getFinancialObjectSubTypeCode())) {
                found = true;
                break;
            }
        }
        if(!found) {
            if (warn) {
                String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.ERROR_ITEM_TRAN_TYPE_OBJECT_CODE_SUBTYPE);
                warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                warning = StringUtils.replace(warning,"{1}",capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription());
                warning = StringUtils.replace(warning,"{2}",objectCode.getFinancialObjectCodeName());
                GlobalVariables.getMessageList().add(warning);
            }
            else {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                        PurapKeyConstants.ERROR_ITEM_TRAN_TYPE_OBJECT_CODE_SUBTYPE,
                        itemIdentifier,
                        capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(),
                        objectCode.getFinancialObjectCodeName(),
                        objectCode.getFinancialObjectSubType().getFinancialObjectSubTypeName());              
            }
            valid &= false;
        }
        return valid;
    }
    
    /**
     * Capital Asset validation: If there is a capital asset transaction type, and the transaction does not indicate 
     * association with a service item, and if the object code sub-types of the object codes on any of the item's associated 
     * accounting lines are in a specific set of object codes for quantity items, the item must have a quantity.
     *
     * @param capitalAssetTransactionType
     * @param itemQuantity
     * @param objectCode
     * @param warn                          A boolean which should be set to true if warnings are to be set on the calling document
     * @param itemIdentifier
     * @return
     */
    public boolean validateQuantityVersusObjectCode(CapitalAssetTransactionType capitalAssetTransactionType, KualiDecimal itemQuantity, ObjectCode objectCode, boolean warn, String itemIdentifier) {
        boolean valid = true;
        if (((itemQuantity == null) || (itemQuantity.isLessEqual(KualiDecimal.ZERO))) &&
            (capitalAssetTransactionType != null) &&
            (!capitalAssetTransactionType.getCapitalAssetTransactionTypeServiceIndicator())) {
                String quantityObjectCodeSubtypes = SpringContext.getBean(ParameterService.class).getParameterValue(
                        ParameterConstants.PURCHASING_DOCUMENT.class, 
                        PurapParameterConstants.CapitalAsset.QUANTITY_OBJECT_CODE_SUBTYPES);
            if (StringUtils.contains(quantityObjectCodeSubtypes,objectCode.getFinancialObjectSubTypeCode())) {
                if (warn) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.ERROR_ITEM_QUANTITY_OBJECT_CODE_SUBTYPE);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",objectCode.getFinancialObjectCodeName());
                    warning = StringUtils.replace(warning,"{2}",objectCode.getFinancialObjectSubType().getFinancialObjectSubTypeName());                    
                    GlobalVariables.getMessageList().add(warning);
                }
                else {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUANTITY, 
                            PurapKeyConstants.ERROR_ITEM_QUANTITY_OBJECT_CODE_SUBTYPE,
                            itemIdentifier,
                            objectCode.getFinancialObjectCodeName(),
                            objectCode.getFinancialObjectSubType().getFinancialObjectSubTypeName());                
                }
                valid &= false;
            }
        }
        
        return valid;
    }
        
    /**
     * Capital Asset validation: If the item has a transaction type, check that if the document specifies that recurring 
     * payments are to be made, that the transaction type is one that is appropriate for this situation, and that if the 
     * document does not specify that recurring payments are to be made, that the transaction type is one that is 
     * appropriate for that situation.
     * 
     * @param capitalAssetTransactionType
     * @param recurringPaymentType
     * @param warn                          A boolean which should be set to true if warnings are to be set on the calling document
     * @param itemIdentifier
     * @return
     */
    public boolean validateCapitalAssetTransactionTypeVersusRecurrence(CapitalAssetTransactionType capitalAssetTransactionType, RecurringPaymentType recurringPaymentType, boolean warn, String itemIdentifier) {
        boolean valid = true;      
        
        // If there is a tran type ...
        if ((capitalAssetTransactionType != null) &&
            (capitalAssetTransactionType.getCapitalAssetTransactionTypeCode() != null)) {
            String recurringTransactionTypeCodes = SpringContext.getBean(ParameterService.class).getParameterValue(
                    ParameterConstants.PURCHASING_DOCUMENT.class, 
                    PurapParameterConstants.CapitalAsset.RECURRING_CAMS_TRAN_TYPES);
            
            
            if (recurringPaymentType != null) { // If there is a recurring payment type ...                
                if (!StringUtils.contains(recurringTransactionTypeCodes,capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                    // There should be a recurring tran type code.
                    if (warn) {
                        String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE);
                        warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                        warning = StringUtils.replace(warning,"{1}",capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription());
                        warning = StringUtils.replace(warning,"{2}",PurapConstants.CAMSValidationStrings.RECURRING);                    
                        GlobalVariables.getMessageList().add(warning);
                    }
                    else {
                        GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                                PurapKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE,
                                itemIdentifier,
                                capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(),
                                PurapConstants.CAMSValidationStrings.RECURRING);
                    }
                    valid &= false;
                }
            }
            else { //If the payment type is not recurring ...
                // There should not be a recurring transaction type code.
                if (StringUtils.contains(recurringTransactionTypeCodes,capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                    if (warn) {
                        String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE);
                        warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                        warning = StringUtils.replace(warning,"{1}",capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription());
                        warning = StringUtils.replace(warning,"{2}",PurapConstants.CAMSValidationStrings.NON_RECURRING);                    
                        GlobalVariables.getMessageList().add(warning);
                    }
                    else {
                        GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                                PurapKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE,
                                itemIdentifier,
                                capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(),
                                PurapConstants.CAMSValidationStrings.NON_RECURRING);
                    }
                    valid &= false;
                }
            }
        }
        else {  // If there is no transaction type ...
            if (recurringPaymentType != null) { // If there is a recurring payment type ...
                if (warn) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.ERROR_ITEM_NO_TRAN_TYPE);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",PurapConstants.CAMSValidationStrings.RECURRING);                    
                    GlobalVariables.getMessageList().add(warning);
                }
                else {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                            PurapKeyConstants.ERROR_ITEM_NO_TRAN_TYPE,
                            itemIdentifier,
                            PurapConstants.CAMSValidationStrings.RECURRING);                 
                }
                valid &= false;
            }
            else { //If the payment type is not recurring ...
                if (warn) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.ERROR_ITEM_NO_TRAN_TYPE);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",PurapConstants.CAMSValidationStrings.NON_RECURRING);                    
                    GlobalVariables.getMessageList().add(warning);
                }
                else {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                            PurapKeyConstants.ERROR_ITEM_NO_TRAN_TYPE,
                            itemIdentifier,
                            PurapConstants.CAMSValidationStrings.NON_RECURRING);                 
                }
                valid &= false;
            }   
        }

        return valid;
    }
    
    /**
     * Capital Asset validation: If the item has a transaction type and the transaction type is one of a specific set 
     * of transaction types, the item should have an asset number.
     * 
     * @param capitalAssetTransactionType
     * @param warn
     * @param itemIdentifier
     * @return
     */
    public boolean validateCapitalAssetNumberRequirements(CapitalAssetTransactionType capitalAssetTransactionType, List<PurchasingItemCapitalAsset> assets, boolean warn, String itemIdentifier) {
        boolean valid = true;
        if ((capitalAssetTransactionType != null) &&
            (capitalAssetTransactionType.getCapitalAssetTransactionTypeCode() != null) &&
            ((assets == null) || (assets.isEmpty())) ) {
            String assetNumberCapitalAssetTransactionTypes = SpringContext.getBean(ParameterService.class).getParameterValue(
                    ParameterConstants.PURCHASING_DOCUMENT.class, 
                    PurapParameterConstants.CapitalAsset.ASSET_NUMBER_CAMS_TRAN_TYPES);
            // If the transaction type code is among those listed in the parameter as needing an asset number...
            if(StringUtils.contains(assetNumberCapitalAssetTransactionTypes,capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                if (warn) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.ERROR_ITEM_TRAN_TYPE_REQUIRES_ASSET_NUMBER);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription());                    
                    GlobalVariables.getMessageList().add(warning);
                }
                else {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                            PurapKeyConstants.ERROR_ITEM_TRAN_TYPE_REQUIRES_ASSET_NUMBER,
                            itemIdentifier,
                            capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription());
                }
                valid &= false;
            }
        }
        return valid;
    }
    
    /**
     * Utility wrapping isCapitalAssetObjectCode for the use of processItemCapitalAssetValidation.
     * 
     * @param oc    An ObjectCode
     * @return      A String indicating that the given object code is either Capital or Expense
     */
    private String objectCodeCapitalOrExpense(ObjectCode oc) {
        String capital = PurapConstants.CAMSValidationStrings.CAPITAL;
        String expense = PurapConstants.CAMSValidationStrings.EXPENSE;
        return ( isCapitalAssetObjectCode(oc) ? capital : expense );
    }
    
    /**
     * Predicate to determine whether the given object code is of a specifically capital asset level.
     * 
     * @param oc    An ObjectCode
     * @return      True if the ObjectCode's level is the one designated as specifically for capital assets.
     */
    public boolean isCapitalAssetObjectCode(ObjectCode oc) {
        String capitalAssetObjectCodeLevels = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, 
                PurapParameterConstants.CapitalAsset.CAPITAL_ASSET_OBJECT_LEVELS);
        return ( StringUtils.containsIgnoreCase( capitalAssetObjectCodeLevels, oc.getFinancialObjectLevelCode() ) ? true : false );
    }
    
    /**
     * Gets the distinct object code sub-types that exist in the AssetTransactionTypeRule table.
     * 
     * @return  A HashSet containing the distinct Object Code Sub-types
     */
    private HashSet<String> getAssetTransactionTypeDistinctObjectCodeSubtypes() {
        HashSet<String> objectCodeSubtypesInTable = new HashSet<String>();
        HashMap<String,String> dummyMap = new HashMap<String,String>();
        List<CapitalAssetTransactionTypeRule> allRelations = (List<CapitalAssetTransactionTypeRule>)SpringContext.getBean(LookupService.class).findCollectionBySearch(CapitalAssetTransactionTypeRule.class, dummyMap);
        for ( CapitalAssetTransactionTypeRule relation : allRelations ) {
            // Add sub-type codes if not already there.
            objectCodeSubtypesInTable.add(relation.getFinancialObjectSubTypeCode());
        }
        
        return objectCodeSubtypesInTable;
    }

}
