/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabKeyConstants;
import org.kuali.kfs.module.cab.CabParameterConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.AssetTransactionType;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetTransactionTypeRule;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;


public class CapitalAssetBuilderModuleServiceImpl implements CapitalAssetBuilderModuleService {

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private AssetService assetService;
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
    
    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
    
    public boolean validateIndividualCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType) {
        return validateAllFieldRequirementsByChart(capitalAssetSystems, capitalAssetItems, chartCode, documentType);
    }
    
    public boolean validateOneSystemCapitalAssetSystemFromPurchasing(String systemState, CapitalAssetSystem capitalAssetSystem) {
        return true;
    }

    public boolean validateMultipleSystemsCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems) {
        return true;
    }
    
    /**
     * @see org.kuali.kfs.integration.service.CapitalAssetBuilderModuleService#doesDocumentExceedThreshold(org.kuali.rice.kns.util.KualiDecimal)
     */
    public boolean doesDocumentExceedThreshold(KualiDecimal docTotal) {
        String parameterThreshold = parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.CAPITALIZATION_LIMIT_AMOUNT);
        if (docTotal.compareTo(new KualiDecimal(parameterThreshold)) > 0) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.integration.service.CapitalAssetBuilderModuleService#validateAccounts(java.util.List, java.lang.String)
     */
    public boolean validateAccounts(List<SourceAccountingLine> accountingLines, String transactionType) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean validateAllFieldRequirementsByChart(List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType) {
        boolean valid = true;
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, "KFS-CAB");
        fieldValues.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, "Document");
        String name = "CHARTS_REQUIRING%" + documentType;
        fieldValues.put(CabPropertyConstants.Parameter.PARAMETER_NAME, name);
        //TODO: Chris or Heather's input to Ailish's email about changing the parameters.
        List<Parameter> results = SpringContext.getBean(PurapService.class).getParametersGivenLikeCriteria(fieldValues);

        for (Parameter parameter : results) {
            valid &= validateFieldRequirementByChart(capitalAssetSystems, capitalAssetItems, chartCode, parameter.getParameterName(), parameter.getParameterValue());
        }
        return valid;
    }
    
    private boolean validateFieldRequirementByChart(List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String parameterName, String parameterValueString) {
        boolean valid = true;        
        boolean needValidation = (parameterService.getParameterEvaluator(ParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, parameterName, chartCode).evaluationSucceeds());
        
        if (needValidation) {
            String mappedName = PurapConstants.CAMS_REQUIREDNESS_FIELDS.REQUIREDNESS_FIELDS_BY_PARAMETER_NAMES.get(parameterName);
            if (mappedName != null) {
                // If the mappedName contains a "." that means it's a field within a collection entry.
                String[] mappedNames = mappedName.split("\\.");
                if (mappedNames[0].equals("purchasingCapitalAssetItems")) {
                    for (PurchasingCapitalAssetItem item : capitalAssetItems) {
                        valid &= validateFieldRequirementByChartHelper (item, ArrayUtils.subarray(mappedNames, 1, mappedNames.length));
                    }
                }
                else if (mappedNames[0].equals("purchasingCapitalAssetSystems")) {
                    for (CapitalAssetSystem system : capitalAssetSystems) {
                        valid &= validateFieldRequirementByChartHelper (system, ArrayUtils.subarray(mappedNames, 1, mappedNames.length));
                    }
                }
            }
        }
        return valid;
    }

    private boolean validateFieldRequirementByChartHelper(Object bean, Object[] mappedNames) {
        boolean valid = true;
        Object value = ObjectUtils.getPropertyValue(bean, (String)mappedNames[0]);
        if (ObjectUtils.isNull(value)) {
            return false;
        }
        else if (value instanceof Collection) {
            if (((Collection)value).isEmpty()) {
                //if this collection doesn't contain anything, when it's supposed to contain some strings with values, return false.
                return false;
            }
            for (Iterator iter = ((Collection)value).iterator(); iter.hasNext();) {
                valid &= validateFieldRequirementByChartHelper(iter.next(), ArrayUtils.subarray(mappedNames, 1, mappedNames.length));
            }
            return valid;
        }
        else if (StringUtils.isBlank(value.toString())) {
            return false;
        }
        else if (mappedNames.length > 1) {
            //this means we have not reached the end of a single field to be traversed yet, so continue with the recursion.
            valid &= validateFieldRequirementByChartHelper(value, ArrayUtils.subarray(mappedNames, 1, mappedNames.length));
            return valid;
        }
        else {
            return true;
        }
    }
    
    public List<CapitalAssetBuilderAssetTransactionType> getAllAssetTransactionTypes() {
        //TODO: Implement this.
        return new ArrayList<CapitalAssetBuilderAssetTransactionType>();
    }
    
    //-------- KULPURAP 2795 methods start here.

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
    public boolean validateItemCapitalAssetWithErrors(RecurringPaymentType recurringPaymentType, PurApItem item, boolean apoCheck) {
        PurchasingItemBase purchasingItem = (PurchasingItemBase)item;
        return validateItemCapitalAsset(recurringPaymentType, purchasingItem, false);
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
    public boolean validateItemCapitalAssetWithWarnings(RecurringPaymentType recurringPaymentType, PurApItem item) {
        PurchasingItemBase purchasingItem = (PurchasingItemBase)item;
        return validateItemCapitalAsset(recurringPaymentType, purchasingItem, true);
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
    protected boolean validateItemCapitalAsset(RecurringPaymentType recurringPaymentType, PurchasingItemBase item, boolean warn) {
        boolean valid = true;
        String itemIdentifier = item.getItemIdentifierString();
        KualiDecimal itemQuantity = item.getItemQuantity();       
        HashSet<String> capitalOrExpenseSet = new HashSet<String>(); // For the first validation on every accounting line.
        
        String capitalAssetTransactionTypeCode = "";
        AssetTransactionType capitalAssetTransactionType = null;
        if( item.getCapitalAssetItem() != null ) {
            capitalAssetTransactionTypeCode = item.getCapitalAssetItem().getCapitalAssetTransactionTypeCode();
            capitalAssetTransactionType = (AssetTransactionType)item.getCapitalAssetItem().getCapitalAssetTransactionType();
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
            if (!StringUtils.isEmpty(capitalAssetTransactionTypeCode)) {
                valid &= validateObjectCodeVersusTransactionType(objectCode, capitalAssetTransactionType, warn, itemIdentifier);
            }
        }
        // These checks do not depend on Accounting Line information, but do depend on transaction type.
        if (!StringUtils.isEmpty(capitalAssetTransactionTypeCode)) {
                valid &= validateCapitalAssetTransactionTypeVersusRecurrence(capitalAssetTransactionType, recurringPaymentType, warn, itemIdentifier);                
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
                String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CabKeyConstants.ERROR_ITEM_CAPITAL_AND_EXPENSE);
                warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                warning = StringUtils.replace(warning,"{1}",objectCode.getFinancialObjectCodeName());
                GlobalVariables.getMessageList().add(warning);
            } 
            else { 
                GlobalVariables.getErrorMap().putError(KFSConstants.FINANCIAL_OBJECT_LEVEL_CODE_PROPERTY_NAME, 
                        CabKeyConstants.ERROR_ITEM_CAPITAL_AND_EXPENSE,itemIdentifier,objectCode.getFinancialObjectCodeName());              
            }
            valid &= false;
        }
        return valid;
    }
    
    /**
     * Gets the distinct object code sub-types that exist in the AssetTransactionTypeRule table.
     * 
     * @return  A HashSet containing the distinct Object Code Sub-types
     */
    //TODO: - when tran type rules are moved into tran type maint. table 
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
                    AssetGlobal.class, 
                    CabParameterConstants.CapitalAsset.CAPITAL_ASSET_PRICE_THRESHOLD);
            if ((extendedPrice != null) &&
                (StringUtils.isNotEmpty(capitalAssetPriceThreshold)) &&
                (extendedPrice.isGreaterEqual(new KualiDecimal(capitalAssetPriceThreshold)))) {
                
                String possiblyCapitalAssetObjectCodeLevels = "";
                try {
                    possiblyCapitalAssetObjectCodeLevels = SpringContext.getBean(ParameterService.class).getParameterValue(
                            ParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, 
                            CabParameterConstants.CapitalAsset.POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS);
                    if (StringUtils.contains(possiblyCapitalAssetObjectCodeLevels,objectCode.getFinancialObjectLevel().getFinancialObjectLevelCode())) {
                        valid &= false;
                    }
                }
                catch (NullPointerException npe) {
                    valid &= false;
                }
                if (!valid) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(
                            CabKeyConstants.WARNING_ABOVE_THRESHOLD_SUGESTS_CAPITAL_ASSET_LEVEL);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",capitalAssetPriceThreshold);                   
                    GlobalVariables.getMessageList().add(warning);
                }
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
// TODO: - rule lookup class . The CapitalAssetTransactionTypeRule will be moved/removed, will obtain the rule from CapitalAssetTransactionType in the future (Dave's other jira).    
    public boolean validateObjectCodeVersusTransactionType(ObjectCode objectCode, CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType, boolean warn, String itemIdentifier) {
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
                String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CabKeyConstants.ERROR_ITEM_TRAN_TYPE_OBJECT_CODE_SUBTYPE);
                warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                warning = StringUtils.replace(warning,"{1}",capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription());
                warning = StringUtils.replace(warning,"{2}",objectCode.getFinancialObjectCodeName());
                GlobalVariables.getMessageList().add(warning);
            }
            else {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                        CabKeyConstants.ERROR_ITEM_TRAN_TYPE_OBJECT_CODE_SUBTYPE,
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
    public boolean validateCapitalAssetTransactionTypeVersusRecurrence(CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType, RecurringPaymentType recurringPaymentType, boolean warn, String itemIdentifier) {
        boolean valid = true;      
        
        // If there is a tran type ...
        if ((capitalAssetTransactionType != null) &&
            (capitalAssetTransactionType.getCapitalAssetTransactionTypeCode() != null)) {
            String recurringTransactionTypeCodes = SpringContext.getBean(ParameterService.class).getParameterValue(
                    ParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, 
                    CabParameterConstants.CapitalAsset.RECURRING_CAMS_TRAN_TYPES);
            
            
            if (recurringPaymentType != null) { // If there is a recurring payment type ...                
                if (!StringUtils.contains(recurringTransactionTypeCodes,capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                    // There should be a recurring tran type code.
                    if (warn) {
                        String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CabKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE);
                        warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                        warning = StringUtils.replace(warning,"{1}",capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription());
                        warning = StringUtils.replace(warning,"{2}",CabConstants.ValidationStrings.RECURRING);                    
                        GlobalVariables.getMessageList().add(warning);
                    }
                    else {
                        GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                                CabKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE,
                                itemIdentifier,
                                capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(),
                                CabConstants.ValidationStrings.RECURRING);
                    }
                    valid &= false;
                }
            }
            else { //If the payment type is not recurring ...
                // There should not be a recurring transaction type code.
                if (StringUtils.contains(recurringTransactionTypeCodes,capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                    if (warn) {
                        String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CabKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE);
                        warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                        warning = StringUtils.replace(warning,"{1}",capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription());
                        warning = StringUtils.replace(warning,"{2}",CabConstants.ValidationStrings.NON_RECURRING);                    
                        GlobalVariables.getMessageList().add(warning);
                    }
                    else {
                        GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                                CabKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE,
                                itemIdentifier,
                                capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(),
                                CabConstants.ValidationStrings.NON_RECURRING);
                    }
                    valid &= false;
                }
            }
        }
        else {  // If there is no transaction type ...
            if (recurringPaymentType != null) { // If there is a recurring payment type ...
                if (warn) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CabKeyConstants.ERROR_ITEM_NO_TRAN_TYPE);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",CabConstants.ValidationStrings.RECURRING);                    
                    GlobalVariables.getMessageList().add(warning);
                }
                else {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                            CabKeyConstants.ERROR_ITEM_NO_TRAN_TYPE,
                            itemIdentifier,
                            CabConstants.ValidationStrings.RECURRING);                 
                }
                valid &= false;
            }
            else { //If the payment type is not recurring ...
                if (warn) {
                    String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CabKeyConstants.ERROR_ITEM_NO_TRAN_TYPE);
                    warning = StringUtils.replace(warning,"{0}",itemIdentifier);
                    warning = StringUtils.replace(warning,"{1}",CabConstants.ValidationStrings.NON_RECURRING);                    
                    GlobalVariables.getMessageList().add(warning);
                }
                else {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, 
                            CabKeyConstants.ERROR_ITEM_NO_TRAN_TYPE,
                            itemIdentifier,
                            CabConstants.ValidationStrings.NON_RECURRING);                 
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
        String capital = CabConstants.ValidationStrings.CAPITAL;
        String expense = CabConstants.ValidationStrings.EXPENSE;
        return ( isCapitalAssetObjectCode(oc) ? capital : expense );
    }
    
    /**
     * Predicate to determine whether the given object code is of a specifically capital asset level.
     * 
     * @param oc    An ObjectCode
     * @return      True if the ObjectCode's level is the one designated as specifically for capital assets.
     */
    public boolean isCapitalAssetObjectCode(ObjectCode oc) {
        String capitalAssetObjectCodeLevels = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, 
                CabParameterConstants.CapitalAsset.CAPITAL_ASSET_OBJECT_LEVELS);
        return ( StringUtils.containsIgnoreCase( capitalAssetObjectCodeLevels, oc.getFinancialObjectLevelCode() ) ? true : false );
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validateFinancialProcessingData(java.util.List, org.kuali.kfs.integration.cam.CapitalAssetManagementAsset)
     */
    public boolean validateFinancialProcessingData(List<SourceAccountingLine> accountingLines, CapitalAssetManagementAsset capitalAssetManagementAsset) {
        boolean valid = true;

        // Need to collect cams data?
        if (parameterService.parameterExists(Document.class, CabConstants.Parameters.FINANCIAL_PROCESSING_CAPITAL_OBJECT_SUB_TYPES)) {
            boolean found = false;
            
            List<String> financialProcessingCapitalObjectSubTypes = parameterService.getParameterValues(Document.class, CabConstants.Parameters.FINANCIAL_PROCESSING_CAPITAL_OBJECT_SUB_TYPES);
            for (SourceAccountingLine sourceAccountingLine : accountingLines) {
                if (financialProcessingCapitalObjectSubTypes.contains(sourceAccountingLine.getObjectCode().getFinancialObjectSubTypeCode())) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                // No object sub type code that matches criteria, not going to validate rest of data
                return true;
            }
        } else {
            // No parameter, not going to validate rest of data
            return true;
        }
        
        if (ObjectUtils.isNotNull(capitalAssetManagementAsset.getCapitalAssetNumber())) {
            
            // New Asset
            
            if (ObjectUtils.isNotNull(capitalAssetManagementAsset.getCapitalAssetTypeCode()) ||
                    ObjectUtils.isNotNull(capitalAssetManagementAsset.getVendorName()) ||
                    ObjectUtils.isNotNull(capitalAssetManagementAsset.getManufacturerName()) ||
                    ObjectUtils.isNotNull(capitalAssetManagementAsset.getManufacturerModelNumber()) ||
                    ObjectUtils.isNotNull(capitalAssetManagementAsset.getSerialNumber()) ||
                    ObjectUtils.isNotNull(capitalAssetManagementAsset.getCampusCode()) ||
                    ObjectUtils.isNotNull(capitalAssetManagementAsset.getBuildingCode()) ||
                    ObjectUtils.isNotNull(capitalAssetManagementAsset.getBuildingRoomNumber()) ||
                    ObjectUtils.isNotNull(capitalAssetManagementAsset.getBuildingSubRoomNumber())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetManagementAsset.ERROR_ASSET_NEW_OR_UPDATE_ONLY);
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, capitalAssetManagementAsset.getCapitalAssetNumber().toString());
            Asset asset = (Asset) businessObjectService.findByPrimaryKey(Asset.class, params);
            
            if (ObjectUtils.isNull(asset)) {
                valid = false;
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
            } else if (!(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE.equals(asset.getCapitalAssetTypeCode()) ||
                    CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_NON_ACCESSIBLE.equals(asset.getCapitalAssetTypeCode()) ||
                    CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION.equals(asset.getCapitalAssetTypeCode()) ||
                    CamsConstants.InventoryStatusCode.CAPITAL_ASSET_SURPLUS_EQUIPEMENT.equals(asset.getCapitalAssetTypeCode()))) {
                // TODO Put previous blurb in system parameter (doesn't just affect this code but also whoever else uses those constants)
                valid = false;
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetManagementAsset.ERROR_ACTIVE_CAPITAL_ASSET_REQUIRED);
            }
        } else {
            
            // Update Asset

            // TODO
            // Quantity is required.  Value must be >0. 

            if (StringUtils.isBlank(capitalAssetManagementAsset.getVendorName())) {
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.VENDOR_NAME);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.VENDOR_NAME, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }
            
            if (StringUtils.isBlank(capitalAssetManagementAsset.getCapitalAssetTypeCode())) {
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            } else {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CamsPropertyConstants.AssetType.CAPITAL_ASSET_TYPE_CODE, capitalAssetManagementAsset.getCapitalAssetNumber().toString());
                AssetType assetType = (AssetType) businessObjectService.findByPrimaryKey(AssetType.class, params);

                if(ObjectUtils.isNull(assetType)) {
                    valid = false;
                    String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE);
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetType.CAPITAL_ASSET_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                }
            }
            
            if (StringUtils.isBlank(capitalAssetManagementAsset.getManufacturerName())) {
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.MANUFACTURER_NAME);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.MANUFACTURER_NAME, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }
            
            if (StringUtils.isBlank(capitalAssetManagementAsset.getCapitalAssetDescription())) {
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAPITAL_ASSET_DESCRIPTION);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAPITAL_ASSET_DESCRIPTION, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }
            
            if (StringUtils.isBlank(capitalAssetManagementAsset.getCampusTagNumber())) {
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            } else {
                if(!assetService.findActiveAssetsMatchingTagNumber(capitalAssetManagementAsset.getCampusTagNumber()).isEmpty()) {
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, capitalAssetManagementAsset.getCampusTagNumber());
                    valid = false;
                }
            }

            if (StringUtils.isBlank(capitalAssetManagementAsset.getCampusCode())) {
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAMPUS_CODE);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAMPUS_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            } else {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CamsPropertyConstants.Asset.CAMPUS_CODE, capitalAssetManagementAsset.getCampusCode());
                Campus campus = (Campus) businessObjectService.findByPrimaryKey(Campus.class, params);

                if(ObjectUtils.isNull(campus)) {
                    valid = false;
                    String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAMPUS_CODE);
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAMPUS_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                }
            }
            
            if (StringUtils.isBlank(capitalAssetManagementAsset.getBuildingCode())) {
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.BUILDING_CODE);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.BUILDING_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            } else {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CamsPropertyConstants.Asset.CAMPUS_CODE, capitalAssetManagementAsset.getCampusCode());
                params.put(CamsPropertyConstants.Asset.BUILDING_CODE, capitalAssetManagementAsset.getBuildingCode());
                Building building = (Building) businessObjectService.findByPrimaryKey(Building.class, params);

                if(ObjectUtils.isNull(building)) {
                    valid = false;
                    String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.BUILDING_CODE);
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.BUILDING_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                }
            }
            
            if (StringUtils.isBlank(capitalAssetManagementAsset.getBuildingRoomNumber())) {
                String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            } else {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CamsPropertyConstants.Asset.CAMPUS_CODE, capitalAssetManagementAsset.getCampusCode());
                params.put(CamsPropertyConstants.Asset.BUILDING_CODE, capitalAssetManagementAsset.getBuildingCode());
                params.put(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, capitalAssetManagementAsset.getBuildingRoomNumber());
                Room room = (Room) businessObjectService.findByPrimaryKey(Room.class, params);

                if(ObjectUtils.isNull(room)) {
                    valid = false;
                    String label = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER);
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
                }
            }
        }
    
        return valid;
    }
}
