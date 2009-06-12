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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CreditCardReceiptDocument;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.fp.document.InternalBillingDocument;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.fp.document.ServiceBillingDocument;
import org.kuali.kfs.fp.document.YearEndDistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.fp.document.YearEndGeneralErrorCorrectionDocument;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ExternalPurApItem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabKeyConstants;
import org.kuali.kfs.module.cab.CabParameterConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.AssetTransactionType;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.AvailabilityMatrix;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemCapitalAssetBase;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CapitalAssetBuilderModuleServiceImpl implements CapitalAssetBuilderModuleService {
    private static Logger LOG = Logger.getLogger(CapitalAssetBuilderModuleService.class);

    private static enum AccountCapitalObjectCode {
        BOTH_NONCAP {
            boolean validateAssetInfoAllowed(AccountingDocument accountingDocument, boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
                boolean valid = true;
                // non capital object code, disallow the capital information entered.
                if (!isNewAssetBlank || !isUpdateAssetBlank) {
                    // give error if data was entered
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_DO_NOT_ENTER_ANY_DATA);
                    valid = false;
                }

                return valid;
            }

            boolean isDocumentTypeRestricted(AccountingDocument accountingDocument) {
                return false;
            }
        },
        FROM_CAPITAL_TO_NONCAP {
            boolean validateAssetInfoAllowed(AccountingDocument accountingDocument, boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
                boolean valid = validateAssetInfoEntered(isNewAssetBlank, isUpdateAssetBlank);
                if (valid) {
                    if (isDocumentTypeRestricted(accountingDocument)) {
                        valid &= validateOnlyOneAssetInfoEntered(isNewAssetBlank, isUpdateAssetBlank);
                    }
                    // Capital on the FROM side and non-capital on the TO side, we only allow modify an asset.
                    else if (!isNewAssetBlank || isUpdateAssetBlank) {
                        GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_UPDATE_ALLOW_ONLY);
                        valid = false;
                    }
                }
                return valid;
            }

            /**
             * For Internal Billing, when credit capital on Income line, allow modify asset or create new asset.
             * 
             * @see org.kuali.kfs.module.cab.service.impl.CapitalAssetBuilderModuleServiceImpl.AccountCapitalObjectCode#isDocumentTypeRestricted(org.kuali.kfs.sys.document.AccountingDocument)
             */
            boolean isDocumentTypeRestricted(AccountingDocument accountingDocument) {
                if (accountingDocument instanceof InternalBillingDocument) {
                    return true;
                }
                return false;
            }
        },
        FROM_NONCAP_TO_CAPITAL {
            boolean validateAssetInfoAllowed(AccountingDocument accountingDocument, boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
                boolean valid = validateAssetInfoEntered(isNewAssetBlank, isUpdateAssetBlank);
                if (valid) {
                    if (isDocumentTypeRestricted(accountingDocument)) {
                        valid &= validateOnlyOneAssetInfoEntered(isNewAssetBlank, isUpdateAssetBlank);
                    }
                    // When Non-capital asset object on the FROM side & Capital asset on the TO side, we only allow create a new
                    // asset.
                    else if (!isUpdateAssetBlank || isNewAssetBlank) {
                        GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_QUANTITY, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_CREATE_NEW_ALLOW_ONLY);
                        valid = false;
                    }
                }
                return valid;
            }

            /**
             * The document is restricted if it's NOT any of the doc type: General Error Correction, Year End General Error
             * Correction, Distribution Of Income And Expense, Year End Distribution Of Income And Expense, Service Billing,
             * Internal Billing
             * 
             * @see org.kuali.kfs.module.cab.service.impl.CapitalAssetBuilderModuleServiceImpl.AccountCapitalObjectCode#isDocumentTypeRestricted(org.kuali.kfs.sys.document.AccountingDocument)
             */
            boolean isDocumentTypeRestricted(AccountingDocument accountingDocument) {
                if (accountingDocument instanceof GeneralErrorCorrectionDocument || accountingDocument instanceof DistributionOfIncomeAndExpenseDocument || accountingDocument instanceof ServiceBillingDocument || accountingDocument instanceof InternalBillingDocument) {
                    return false;
                }
                else {
                    return true;
                }
            }
        },
        BOTH_CAPITAL {
            boolean validateAssetInfoAllowed(AccountingDocument accountingDocument, boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
                return validateAssetInfoEntered(isNewAssetBlank, isUpdateAssetBlank) && validateOnlyOneAssetInfoEntered(isNewAssetBlank, isUpdateAssetBlank);
            }

            boolean isDocumentTypeRestricted(AccountingDocument accountingDocument) {
                return false;
            }
        };

        /**
         * Validate Asset Information is not blank.
         * 
         * @param isNewAssetBlank
         * @param isUpdateAssetBlank
         * @return
         */
        private static boolean validateAssetInfoEntered(boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
            boolean valid = true;
            // can modify existing or create new. Required to enter one of each type.
            if (isNewAssetBlank && isUpdateAssetBlank) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_REQUIRE_DATA_ENTRY);
                valid = false;
            }
            return valid;
        }

        /**
         * Validate only either one asset information entered.
         * 
         * @param isNewAssetBlank
         * @param isUpdateAssetBlank
         * @return
         */
        private static boolean validateOnlyOneAssetInfoEntered(boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
            boolean valid = true;
            if (!isNewAssetBlank && !isUpdateAssetBlank) {
                // Data exists on both crate new asset and update asset, give error
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_NEW_OR_UPDATE_ONLY);
                valid = false;
            }
            return valid;
        }

        abstract boolean validateAssetInfoAllowed(AccountingDocument accoutingDocument, boolean isNewAssetBlank, boolean isUpdateAssetBlank);

        abstract boolean isDocumentTypeRestricted(AccountingDocument accountingDocument);
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#getAllAssetTransactionTypes()
     */
    public List<CapitalAssetBuilderAssetTransactionType> getAllAssetTransactionTypes() {
        Class<? extends CapitalAssetBuilderAssetTransactionType> assetTransactionTypeClass = this.getKualiModuleService().getResponsibleModuleService(CapitalAssetBuilderAssetTransactionType.class).getExternalizableBusinessObjectImplementation(CapitalAssetBuilderAssetTransactionType.class);
        return (List<CapitalAssetBuilderAssetTransactionType>) this.getBusinessObjectService().findAll(assetTransactionTypeClass);
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validatePurchasingAccountsPayableData(org.kuali.kfs.sys.document.AccountingDocument)
     */
    public boolean validatePurchasingData(AccountingDocument accountingDocument) {
        PurchasingDocument purchasingDocument = (PurchasingDocument) accountingDocument;
        String documentType = (purchasingDocument instanceof RequisitionDocument) ? "REQUISITION" : "PURCHASE_ORDER";
        if (PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL.equals(purchasingDocument.getCapitalAssetSystemTypeCode())) {
            return validateIndividualCapitalAssetSystemFromPurchasing(purchasingDocument.getCapitalAssetSystemStateCode(), purchasingDocument.getPurchasingCapitalAssetItems(), purchasingDocument.getChartOfAccountsCode(), documentType);
        }
        else if (PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM.equals(purchasingDocument.getCapitalAssetSystemTypeCode())) {
            return validateOneSystemCapitalAssetSystemFromPurchasing(purchasingDocument.getCapitalAssetSystemStateCode(), purchasingDocument.getPurchasingCapitalAssetSystems(), purchasingDocument.getPurchasingCapitalAssetItems(), purchasingDocument.getChartOfAccountsCode(), documentType);
        }
        else if (PurapConstants.CapitalAssetSystemTypes.MULTIPLE.equals(purchasingDocument.getCapitalAssetSystemTypeCode())) {
            return validateMultipleSystemsCapitalAssetSystemFromPurchasing(purchasingDocument.getCapitalAssetSystemStateCode(), purchasingDocument.getPurchasingCapitalAssetSystems(), purchasingDocument.getPurchasingCapitalAssetItems(), purchasingDocument.getChartOfAccountsCode(), documentType);
        }
        return false;
    }

    public boolean validateAccountsPayableData(AccountingDocument accountingDocument) {
        AccountsPayableDocument apDocument = (AccountsPayableDocument) accountingDocument;
        boolean valid = true;
        for (PurApItem purApItem : apDocument.getItems()) {
            AccountsPayableItem accountsPayableItem = (AccountsPayableItem) purApItem;
            // only run on ap items that were line items (not additional charge items) and were cams items
            if ((!accountsPayableItem.getItemType().isAdditionalChargeIndicator()) && StringUtils.isNotEmpty(accountsPayableItem.getCapitalAssetTransactionTypeCode())) {
                valid &= validateAccountsPayableItem(accountsPayableItem);
            }
        }
        return valid;
    }

    /**
     * Perform the item level capital asset validation to determine if the given document is not allowed to become an Automatic
     * Purchase Order (APO). The APO is not allowed if any accounting strings on the document are using an object level indicated as
     * capital via a parameter setting.
     */
    public boolean doesAccountingLineFailAutomaticPurchaseOrderRules(AccountingLine accountingLine) {
        PurApAccountingLine purapAccountingLine = (PurApAccountingLine) accountingLine;
        purapAccountingLine.refreshNonUpdateableReferences();
        return getParameterService().getParameterEvaluator(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.CAPITAL_ASSET_OBJECT_LEVELS, purapAccountingLine.getObjectCode().getFinancialObjectLevelCode()).evaluationSucceeds();
    }

    /**
     * Perform the document level capital asset validation to determine if the given document is not allowed to become an Automatic
     * Purchase Order (APO). The APO is not allowed if any capital asset items exist on the document.
     */
    public boolean doesDocumentFailAutomaticPurchaseOrderRules(AccountingDocument accountingDocument) {
        PurchasingDocument purchasingDocument = (PurchasingDocument) accountingDocument;
        return ObjectUtils.isNotNull(purchasingDocument.getPurchasingCapitalAssetItems()) && !purchasingDocument.getPurchasingCapitalAssetItems().isEmpty();
    }

    public boolean validateAutomaticPurchaseOrderRule(AccountingDocument accountingDocument) {
        PurchasingDocument purchasingDocument = (PurchasingDocument) accountingDocument;
        for (PurApItem item : purchasingDocument.getItems()) {
            if (doesItemNeedCapitalAsset(item.getItemTypeCode(), item.getSourceAccountingLines())) {
                // if the item needs capital asset, we cannot have an APO, so return false.
                return false;
            }
        }
        return true;
    }

    public boolean doesItemNeedCapitalAsset(String itemTypeCode, List accountingLines) {
        if (PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE.equals(itemTypeCode)) {
            // FIXME: Chris - this should be true but need to look to see where itemline number is referenced first
            // return true;
            return false;
        }// else
        for (Iterator iterator = accountingLines.iterator(); iterator.hasNext();) {
            PurApAccountingLine accountingLine = (PurApAccountingLine) iterator.next();
            accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            if (ObjectUtils.isNotNull(accountingLine.getObjectCode()) && isCapitalAssetObjectCode(accountingLine.getObjectCode())) {
                return true;
            }
        }

        return false;
    }

    public boolean validateUpdateCAMSView(AccountingDocument accountingDocument) {
        PurchasingDocument purchasingDocument = (PurchasingDocument) accountingDocument;
        boolean valid = true;
        for (PurApItem purapItem : purchasingDocument.getItems()) {
            if (purapItem.getItemType().isLineItemIndicator()) {
                if (!doesItemNeedCapitalAsset(purapItem.getItemTypeCode(), purapItem.getSourceAccountingLines())) {
                    PurchasingCapitalAssetItem camsItem = ((PurchasingItem) purapItem).getPurchasingCapitalAssetItem();
                    if (camsItem != null && !camsItem.isEmpty()) {
                        valid = false;
                        GlobalVariables.getErrorMap().putError("newPurchasingItemCapitalAssetLine", PurapKeyConstants.ERROR_CAPITAL_ASSET_ITEM_NOT_CAMS_ELIGIBLE, "in line item # " + purapItem.getItemLineNumber());
                    }
                }
            }
        }
        return valid;
    }

    public boolean validateAddItemCapitalAssetBusinessRules(ItemCapitalAsset asset) {
        boolean valid = true;
        if (asset.getCapitalAssetNumber() == null) {
            valid = false;
        }
        else {
            valid = SpringContext.getBean(DictionaryValidationService.class).isBusinessObjectValid((PurchasingItemCapitalAssetBase) asset);
        }
        if (!valid) {
            String propertyName = "newPurchasingItemCapitalAssetLine." + PurapPropertyConstants.CAPITAL_ASSET_NUMBER;
            String errorKey = PurapKeyConstants.ERROR_CAPITAL_ASSET_ASSET_NUMBER_MUST_BE_LONG_NOT_NULL;
            GlobalVariables.getErrorMap().putError(propertyName, errorKey);
        }

        return valid;
    }

    public boolean warningObjectLevelCapital(AccountingDocument accountingDocument) {
        org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument purapDocument = (org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument) accountingDocument;
        for (PurApItem item : purapDocument.getItems()) {
            if (item.getItemType().isLineItemIndicator() && item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                List<PurApAccountingLine> accounts = item.getSourceAccountingLines();
                BigDecimal unitPrice = item.getItemUnitPrice();
                String itemIdentifier = item.getItemIdentifierString();
                for (PurApAccountingLine account : accounts) {
                    ObjectCode objectCode = account.getObjectCode();
                    if (!validateLevelCapitalAssetIndication(unitPrice, objectCode, itemIdentifier)) {
                        // found an error
                        return false;
                    }
                }
            }
        }
        // no need for error
        return true;
    }


    /**
     * Validates the capital asset field requirements based on system parameter and chart for individual system type. This also
     * calls validations for quantity on locations equal quantity on line items, validates that the transaction type allows asset
     * number and validates the non quantity driven allowed indicator.
     * 
     * @param systemState
     * @param capitalAssetItems
     * @param chartCode
     * @param documentType
     * @return
     */
    protected boolean validateIndividualCapitalAssetSystemFromPurchasing(String systemState, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType) {
        // For Individual Asset system type, the List of CapitalAssetSystems in the input parameter for
        // validateAllFieldRequirementsByChart
        // should be null. So we'll pass in a null here.
        boolean valid = validateAllFieldRequirementsByChart(systemState, null, capitalAssetItems, chartCode, documentType, PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL);
        valid &= validateQuantityOnLocationsEqualsQuantityOnItem(capitalAssetItems, PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, systemState);
        valid &= validateIndividualSystemPurchasingTransactionTypesAllowingAssetNumbers(capitalAssetItems);
        valid &= validateNonQuantityDrivenAllowedIndicatorAndTradeIn(capitalAssetItems);
        return valid;
    }

    /**
     * Validates the capital asset field requirements based on system parameter and chart for one system type. This also calls
     * validations that the transaction type allows asset number and validates the non quantity driven allowed indicator.
     * 
     * @param systemState
     * @param capitalAssetSystems
     * @param capitalAssetItems
     * @param chartCode
     * @param documentType
     * @return
     */
    protected boolean validateOneSystemCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType) {
        boolean valid = validateAllFieldRequirementsByChart(systemState, capitalAssetSystems, capitalAssetItems, chartCode, documentType, PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM);
        String capitalAssetTransactionType = capitalAssetItems.get(0).getCapitalAssetTransactionTypeCode();
        String prefix = "document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_SYSTEMS + "[0].";
        valid &= validatePurchasingTransactionTypesAllowingAssetNumbers(capitalAssetSystems.get(0), capitalAssetTransactionType, prefix);
        valid &= validateNonQuantityDrivenAllowedIndicatorAndTradeIn(capitalAssetItems);
        return valid;
    }

    /**
     * Validates the capital asset field requirements based on system parameter and chart for multiple system type. This also calls
     * validations that the transaction type allows asset number and validates the non quantity driven allowed indicator.
     * 
     * @param systemState
     * @param capitalAssetSystems
     * @param capitalAssetItems
     * @param chartCode
     * @param documentType
     * @return
     */
    protected boolean validateMultipleSystemsCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType) {
        boolean valid = validateAllFieldRequirementsByChart(systemState, capitalAssetSystems, capitalAssetItems, chartCode, documentType, PurapConstants.CapitalAssetSystemTypes.MULTIPLE);
        String capitalAssetTransactionType = capitalAssetItems.get(0).getCapitalAssetTransactionTypeCode();
        String prefix = "document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_SYSTEMS + "[0].";
        valid &= validatePurchasingTransactionTypesAllowingAssetNumbers(capitalAssetSystems.get(0), capitalAssetTransactionType, prefix);
        valid &= validateNonQuantityDrivenAllowedIndicatorAndTradeIn(capitalAssetItems);
        return valid;
    }

    /**
     * Validates all the field requirements by chart. It obtains a List of parameters where the parameter names are like
     * "CHARTS_REQUIRING%" then loop through these parameters. If the system type is individual then invoke the
     * validateFieldRequirementByChartForIndividualSystemType for further validation, otherwise invoke the
     * validateFieldRequirementByChartForOneOrMultipleSystemType for further validation.
     * 
     * @param systemState
     * @param capitalAssetSystems
     * @param capitalAssetItems
     * @param chartCode
     * @param documentType
     * @param systemType
     * @return
     */
    protected boolean validateAllFieldRequirementsByChart(String systemState, List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType, String systemType) {
        boolean valid = true;
        List<Parameter> results = new ArrayList<Parameter>();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE);
        criteria.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_DOCUMENT);
        criteria.put(CabPropertyConstants.Parameter.PARAMETER_NAME, "CHARTS_REQUIRING%" + documentType);
        results.addAll(SpringContext.getBean(ParameterService.class).retrieveParametersGivenLookupCriteria(criteria));
        for (Parameter parameter : results) {
            if (ObjectUtils.isNotNull(parameter)) {
                if (systemType.equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                    valid &= validateFieldRequirementByChartForIndividualSystemType(systemState, capitalAssetItems, chartCode, parameter.getParameterName(), parameter.getParameterValue());
                }
                else {
                    valid &= validateFieldRequirementByChartForOneOrMultipleSystemType(systemType, systemState, capitalAssetSystems, capitalAssetItems, chartCode, parameter.getParameterName(), parameter.getParameterValue());
                }
            }
        }
        return valid;
    }

    /**
     * Validates all the field requirements by chart. It obtains a List of parameters where the parameter names are like
     * "CHARTS_REQUIRING%" then loop through these parameters. If any of the parameter's values is null, then return false
     * 
     * @param accountingDocument
     * @return
     */
    public boolean validateAllFieldRequirementsByChart(AccountingDocument accountingDocument) {
        PurchasingDocument purchasingDocument = (PurchasingDocument) accountingDocument;
        String documentType = (purchasingDocument instanceof RequisitionDocument) ? "REQUISITION" : "PURCHASE_ORDER";
        boolean valid = true;
        List<Parameter> results = new ArrayList<Parameter>();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE);
        criteria.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_DOCUMENT);
        criteria.put(CabPropertyConstants.Parameter.PARAMETER_NAME, "CHARTS_REQUIRING%" + documentType);
        results.addAll(SpringContext.getBean(ParameterService.class).retrieveParametersGivenLookupCriteria(criteria));
        for (Parameter parameter : results) {
            if (ObjectUtils.isNotNull(parameter)) {
                if (parameter.getParameterValue() != null) {
                    return false;
                }
            }
        }
        return valid;
    }

    /**
     * Validates for PURCHASING_OBJECT_SUB_TYPES parameter. If at least one object code of any accounting line entered is of this
     * type, then return false.
     * 
     * @param accountingDocument
     * @return
     */
    public boolean validatePurchasingObjectSubType(AccountingDocument accountingDocument) {
        boolean valid = true;
        PurchasingDocument purchasingDocument = (PurchasingDocument) accountingDocument;
        for (PurApItem item : purchasingDocument.getItems()) {
            String itemTypeCode = item.getItemTypeCode();
            List accountingLines = item.getSourceAccountingLines();
            for (Iterator iterator = accountingLines.iterator(); iterator.hasNext();) {
                PurApAccountingLine accountingLine = (PurApAccountingLine) iterator.next();
                accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
                if (ObjectUtils.isNotNull(accountingLine.getObjectCode()) && isCapitalAssetObjectCode(accountingLine.getObjectCode())) {
                    return false;
                }
            }
        }
        return valid;
    }

    /**
     * Validates field requirement by chart for one or multiple system types.
     * 
     * @param systemType
     * @param systemState
     * @param capitalAssetSystems
     * @param capitalAssetItems
     * @param chartCode
     * @param parameterName
     * @param parameterValueString
     * @return
     */
    protected boolean validateFieldRequirementByChartForOneOrMultipleSystemType(String systemType, String systemState, List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String parameterName, String parameterValueString) {
        boolean valid = true;
        boolean needValidation = (this.getParameterService().getParameterEvaluator(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, parameterName, chartCode).evaluationSucceeds());

        if (needValidation) {
            if (parameterName.startsWith("CHARTS_REQUIRING_LOCATIONS_ADDRESS")) {
                return validateCapitalAssetLocationAddressFieldsOneOrMultipleSystemType(capitalAssetSystems);
            }
            String mappedName = PurapConstants.CAMS_REQUIREDNESS_FIELDS.REQUIREDNESS_FIELDS_BY_PARAMETER_NAMES.get(parameterName);
            if (mappedName != null) {
                // Check the availability matrix here, if this field doesn't exist according to the avail. matrix, then no need
                // to validate any further.
                String availableValue = getValueFromAvailabilityMatrix(mappedName, systemType, systemState);
                if (availableValue.equals(PurapConstants.CapitalAssetAvailability.NONE)) {
                    return true;
                }

                // capitalAssetTransactionType field is off the item
                if (mappedName.equals(PurapPropertyConstants.CAPITAL_ASSET_TRANSACTION_TYPE_CODE)) {
                    String[] mappedNames = { PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS, mappedName };

                    for (PurchasingCapitalAssetItem item : capitalAssetItems) {
                        StringBuffer keyBuffer = new StringBuffer("document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS + "[" + new Integer(item.getPurchasingItem().getItemLineNumber().intValue() - 1) + "].");
                        valid &= validateFieldRequirementByChartHelper(item, ArrayUtils.subarray(mappedNames, 1, mappedNames.length), keyBuffer, item.getPurchasingItem().getItemLineNumber());
                    }
                }
                // all the other fields are off the system.
                else {
                    List<String> mappedNamesList = new ArrayList<String>();
                    mappedNamesList.add(PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_SYSTEMS);
                    if (mappedName.indexOf(".") < 0) {
                        mappedNamesList.add(mappedName);
                    }
                    else {
                        mappedNamesList.addAll(mappedNameSplitter(mappedName));
                    }
                    // For One system type, we would only have 1 CapitalAssetSystem, however, for multiple we may have more than
                    // one systems in the future. Either way, this for loop should allow both the one system and multiple system
                    // types to work fine.
                    int count = 0;
                    for (CapitalAssetSystem system : capitalAssetSystems) {
                        StringBuffer keyBuffer = new StringBuffer("document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_SYSTEMS + "[" + new Integer(count) + "].");
                        valid &= validateFieldRequirementByChartHelper(system, ArrayUtils.subarray(mappedNamesList.toArray(), 1, mappedNamesList.size()), keyBuffer, null);
                        count++;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Validates the field requirement by chart for individual system type.
     * 
     * @param systemState
     * @param capitalAssetItems
     * @param chartCode
     * @param parameterName
     * @param parameterValueString
     * @return
     */
    protected boolean validateFieldRequirementByChartForIndividualSystemType(String systemState, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String parameterName, String parameterValueString) {
        boolean valid = true;
        boolean needValidation = (this.getParameterService().getParameterEvaluator(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, parameterName, chartCode).evaluationSucceeds());

        if (needValidation) {
            if (parameterName.startsWith("CHARTS_REQUIRING_LOCATIONS_ADDRESS")) {
                return validateCapitalAssetLocationAddressFieldsForIndividualSystemType(capitalAssetItems);
            }
            String mappedName = PurapConstants.CAMS_REQUIREDNESS_FIELDS.REQUIREDNESS_FIELDS_BY_PARAMETER_NAMES.get(parameterName);
            if (mappedName != null) {
                // Check the availability matrix here, if this field doesn't exist according to the avail. matrix, then no need
                // to validate any further.
                String availableValue = getValueFromAvailabilityMatrix(mappedName, PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL, systemState);
                if (availableValue.equals(PurapConstants.CapitalAssetAvailability.NONE)) {
                    return true;
                }

                // capitalAssetTransactionType field is off the item
                List<String> mappedNamesList = new ArrayList<String>();

                if (mappedName.equals(PurapPropertyConstants.CAPITAL_ASSET_TRANSACTION_TYPE_CODE)) {
                    mappedNamesList.add(PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS);
                    mappedNamesList.add(mappedName);

                }
                // all the other fields are off the system which is off the item
                else {
                    mappedNamesList.add(PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS);
                    mappedNamesList.add(PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_SYSTEM);
                    if (mappedName.indexOf(".") < 0) {
                        mappedNamesList.add(mappedName);
                    }
                    else {
                        mappedNamesList.addAll(mappedNameSplitter(mappedName));
                    }
                }
                // For Individual system type, we'll always iterate through the item, then if the field is off the system, we'll get
                // it through
                // the purchasingCapitalAssetSystem of the item.
                for (PurchasingCapitalAssetItem item : capitalAssetItems) {
                    StringBuffer keyBuffer = new StringBuffer("document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS + "[" + new Integer(item.getPurchasingItem().getItemLineNumber().intValue() - 1) + "].");
                    valid &= validateFieldRequirementByChartHelper(item, ArrayUtils.subarray(mappedNamesList.toArray(), 1, mappedNamesList.size()), keyBuffer, item.getPurchasingItem().getItemLineNumber());
                }
            }
        }
        return valid;
    }

    /**
     * Utility method to split a long String using the "." as the delimiter then add each of the array element into a List of String
     * and return the List of String.
     * 
     * @param mappedName The String to be splitted.
     * @return The List of String after being splitted, with the "." as delimiter.
     */
    protected List<String> mappedNameSplitter(String mappedName) {
        List<String> result = new ArrayList<String>();
        String[] mappedNamesArray = mappedName.split("\\.");
        for (int i = 0; i < mappedNamesArray.length; i++) {
            result.add(mappedNamesArray[i]);
        }
        return result;
    }

    /**
     * Validates the field requirement by chart recursively and give error messages when it returns false.
     * 
     * @param bean The object to be used to obtain the property value
     * @param mappedNames The array of Strings which when combined together, they form the field property
     * @param errorKey The error key to be used for adding error messages to the error map.
     * @param itemNumber The Integer that represents the item number that we're currently iterating.
     * @return true if it passes the validation.
     */
    protected boolean validateFieldRequirementByChartHelper(Object bean, Object[] mappedNames, StringBuffer errorKey, Integer itemNumber) {
        boolean valid = true;
        Object value = ObjectUtils.getPropertyValue(bean, (String) mappedNames[0]);
        if (ObjectUtils.isNull(value)) {
            errorKey.append(mappedNames[0]);
            String fieldName = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(bean.getClass(), (String) mappedNames[0]);
            if (itemNumber != null) {
                fieldName = fieldName + " in Item " + itemNumber;
            }
            GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, fieldName);
            return false;
        }
        else if (value instanceof Collection) {
            if (((Collection) value).isEmpty()) {
                // if this collection doesn't contain anything, when it's supposed to contain some strings with values, return
                // false.
                errorKey.append(mappedNames[0]);
                String mappedNameStr = (String) mappedNames[0];
                String methodToInvoke = "get" + (mappedNameStr.substring(0, 1)).toUpperCase() + mappedNameStr.substring(1, mappedNameStr.length() - 1) + "Class";
                Class offendingClass;
                try {
                    offendingClass = (Class) bean.getClass().getMethod(methodToInvoke, null).invoke(bean, null);
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                BusinessObjectEntry boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(offendingClass.getSimpleName());
                List<AttributeDefinition> offendingAttributes = boe.getAttributes();
                AttributeDefinition offendingAttribute = offendingAttributes.get(0);
                String fieldName = SpringContext.getBean(DataDictionaryService.class).getAttributeShortLabel(offendingClass, offendingAttribute.getName());
                GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, fieldName);
                return false;
            }
            int count = 0;
            for (Iterator iter = ((Collection) value).iterator(); iter.hasNext();) {
                errorKey.append(mappedNames[0] + "[" + count + "].");
                count++;
                valid &= validateFieldRequirementByChartHelper(iter.next(), ArrayUtils.subarray(mappedNames, 1, mappedNames.length), errorKey, itemNumber);
            }
            return valid;
        }
        else if (StringUtils.isBlank(value.toString())) {
            errorKey.append(mappedNames[0]);
            GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, (String) mappedNames[0]);
            return false;
        }
        else if (mappedNames.length > 1) {
            // this means we have not reached the end of a single field to be traversed yet, so continue with the recursion.
            errorKey.append(mappedNames[0]).append(".");
            valid &= validateFieldRequirementByChartHelper(value, ArrayUtils.subarray(mappedNames, 1, mappedNames.length), errorKey, itemNumber);
            return valid;
        }
        else {
            return true;
        }
    }

    protected String getValueFromAvailabilityMatrix(String fieldName, String systemType, String systemState) {
        for (AvailabilityMatrix am : PurapConstants.CAMS_AVAILABILITY_MATRIX.MATRIX_LIST) {
            if (am.fieldName.equals(fieldName) && am.systemState.equals(systemState) && am.systemType.equals(systemType)) {
                return am.availableValue;
            }
        }
        // if we can't find any matching from availability matrix, return null for now.
        return null;
    }

    /**
     * Validates that the total quantity on all locations equals to the quantity on the line item. This is only used for IND system
     * type.
     * 
     * @param capitalAssetItems
     * @return true if the total quantity on all locations equals to the quantity on the line item.
     */
    protected boolean validateQuantityOnLocationsEqualsQuantityOnItem(List<PurchasingCapitalAssetItem> capitalAssetItems, String systemType, String systemState) {
        boolean valid = true;
        String availableValue = getValueFromAvailabilityMatrix(PurapPropertyConstants.CAPITAL_ASSET_LOCATIONS + "." + PurapPropertyConstants.QUANTITY, systemType, systemState);
        if (availableValue.equals(PurapConstants.CapitalAssetAvailability.NONE)) {
            // If the location quantity isn't available on the document given the system type and system state, we don't need to
            // validate this, just return true.
            return true;
        }
        int count = 0;
        for (PurchasingCapitalAssetItem item : capitalAssetItems) {
            if (item.getPurchasingItem() != null && item.getPurchasingItem().getItemType().isQuantityBasedGeneralLedgerIndicator() && !item.getPurchasingCapitalAssetSystem().getCapitalAssetLocations().isEmpty()) {
                KualiDecimal total = new KualiDecimal(0);
                for (CapitalAssetLocation location : item.getPurchasingCapitalAssetSystem().getCapitalAssetLocations()) {
                    if (ObjectUtils.isNotNull(location.getItemQuantity())) {
                        total = total.add(location.getItemQuantity());
                    }
                }
                if (!item.getPurchasingItem().getItemQuantity().equals(total)) {
                    valid = false;
                    String errorKey = PurapKeyConstants.ERROR_CAPITAL_ASSET_LOCATIONS_QUANTITY_MUST_EQUAL_ITEM_QUANTITY;
                    String propertyName = "document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS + "[" + count + "]." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_SYSTEM + ".newPurchasingCapitalAssetLocationLine." + PurapPropertyConstants.QUANTITY;
                    GlobalVariables.getErrorMap().putError(propertyName, errorKey, Integer.toString(count + 1));
                }
            }
            count++;
        }

        return valid;
    }

    /**
     * Validates for the individual system type that for each of the items, the capitalAssetTransactionTypeCode matches the system
     * parameter PURCHASING_ASSET_TRANSACTION_TYPES_ALLOWING_ASSET_NUMBERS, the method will return true but if it doesn't match the
     * system parameter, then loop through each of the itemCapitalAssets. If there is any non-null capitalAssetNumber then return
     * false.
     * 
     * @param capitalAssetItems the List of PurchasingCapitalAssetItems on the document to be validated
     * @return false if the capital asset transaction type does not match the system parameter that allows asset numbers but the
     *         itemCapitalAsset contains at least one asset numbers.
     */
    protected boolean validateIndividualSystemPurchasingTransactionTypesAllowingAssetNumbers(List<PurchasingCapitalAssetItem> capitalAssetItems) {
        boolean valid = true;
        int count = 0;
        for (PurchasingCapitalAssetItem capitalAssetItem : capitalAssetItems) {
            String prefix = "document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS + "[" + count + "].";
            valid &= validatePurchasingTransactionTypesAllowingAssetNumbers(capitalAssetItem.getPurchasingCapitalAssetSystem(), capitalAssetItem.getCapitalAssetTransactionTypeCode(), prefix);
            count++;
        }
        return valid;
    }

    /**
     * Generic validation that if the capitalAssetTransactionTypeCode does not match the system parameter
     * PURCHASING_ASSET_TRANSACTION_TYPES_ALLOWING_ASSET_NUMBERS and at least one of the itemCapitalAssets contain a non-null
     * capitalAssetNumber then return false. This method is used by one system and multiple system types as well as being used as a
     * helper method for validateIndividualSystemPurchasingTransactionTypesAllowingAssetNumbers method.
     * 
     * @param capitalAssetSystem the capitalAssetSystem containing a List of itemCapitalAssets to be validated.
     * @param capitalAssetTransactionType the capitalAssetTransactionTypeCode containing asset numbers to be validated.
     * @return false if the capital asset transaction type does not match the system parameter that allows asset numbers but the
     *         itemCapitalAsset contains at least one asset numbers.
     */
    protected boolean validatePurchasingTransactionTypesAllowingAssetNumbers(CapitalAssetSystem capitalAssetSystem, String capitalAssetTransactionType, String prefix) {
        boolean allowedAssetNumbers = (this.getParameterService().getParameterEvaluator(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.PURCHASING_ASSET_TRANSACTION_TYPES_ALLOWING_ASSET_NUMBERS, capitalAssetTransactionType).evaluationSucceeds());
        if (allowedAssetNumbers) {
            // If this is a transaction type that allows asset numbers, we don't need to validate anymore, just return true here.
            return true;
        }
        else {
            for (ItemCapitalAsset asset : capitalAssetSystem.getItemCapitalAssets()) {
                if (asset.getCapitalAssetNumber() != null) {
                    String propertyName = prefix + PurapPropertyConstants.CAPITAL_ASSET_TRANSACTION_TYPE_CODE;
                    GlobalVariables.getErrorMap().putError(propertyName, PurapKeyConstants.ERROR_CAPITAL_ASSET_ASSET_NUMBERS_NOT_ALLOWED_TRANS_TYPE, capitalAssetTransactionType);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * TODO: rename this method removing trade in reference? Validates that if the non quantity drive allowed indicator on the
     * capital asset transaction type is false and the item is of non quantity type
     * 
     * @param capitalAssetItems The List of PurchasingCapitalAssetItem to be validated.
     * @return false if the indicator is false and there is at least one non quantity items on the list.
     */
    protected boolean validateNonQuantityDrivenAllowedIndicatorAndTradeIn(List<PurchasingCapitalAssetItem> capitalAssetItems) {
        boolean valid = true;
        int count = 0;
        for (PurchasingCapitalAssetItem capitalAssetItem : capitalAssetItems) {
            String prefix = "document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS + "[" + count + "].";
            if (StringUtils.isNotBlank(capitalAssetItem.getCapitalAssetTransactionTypeCode())) {
                // ((PurchasingCapitalAssetItemBase)
                // capitalAssetItem).refreshReferenceObject(PurapPropertyConstants.CAPITAL_ASSET_TRANSACTION_TYPE);
                if (!capitalAssetItem.getCapitalAssetTransactionType().getCapitalAssetNonquantityDrivenAllowIndicator()) {
                    if (!capitalAssetItem.getPurchasingItem().getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                        String propertyName = prefix + PurapPropertyConstants.CAPITAL_ASSET_TRANSACTION_TYPE_CODE;
                        GlobalVariables.getErrorMap().putError(propertyName, PurapKeyConstants.ERROR_CAPITAL_ASSET_TRANS_TYPE_NOT_ALLOWING_NON_QUANTITY_ITEMS, capitalAssetItem.getCapitalAssetTransactionTypeCode());
                        valid &= false;
                    }
                }
            }
            count++;
        }
        return valid;
    }

    /**
     * Wrapper to do Capital Asset validations, generating errors instead of warnings. Makes sure that the given item's data
     * relevant to its later possible classification as a Capital Asset is internally consistent, by marshaling and calling the
     * methods marked as Capital Asset validations. This implementation assumes that all object codes are valid (real) object codes.
     * 
     * @param recurringPaymentType The item's document's RecurringPaymentType
     * @param item A PurchasingItemBase object
     * @param apoCheck True if this check is for APO purposes
     * @return True if the item passes all Capital Asset validations
     */
    public boolean validateItemCapitalAssetWithErrors(String recurringPaymentTypeCode, ExternalPurApItem item, boolean apoCheck) {
        PurchasingItemBase purchasingItem = (PurchasingItemBase) item;
        List<String> previousErrorPath = GlobalVariables.getErrorMap().getErrorPath();
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(PurapConstants.CAPITAL_ASSET_TAB_ERRORS);
        boolean result = validatePurchasingItemCapitalAsset(recurringPaymentTypeCode, purchasingItem);
        GlobalVariables.getErrorMap().clearErrorPath();
        return result;
    }

    /**
     * Makes sure that the given item's data relevant to its later possible classification as a Capital Asset is internally
     * consistent, by marshaling and calling the methods marked as Capital Asset validations. This implementation assumes that all
     * object codes are valid (real) object codes.
     * 
     * @param recurringPaymentType The item's document's RecurringPaymentType
     * @param item A PurchasingItemBase object
     * @param warn A boolean which should be set to true if warnings are to be set on the calling document for most of the
     *        validations, rather than errors.
     * @return True if the item passes all Capital Asset validations
     */
    protected boolean validatePurchasingItemCapitalAsset(String recurringPaymentTypeCode, PurchasingItem item) {
        boolean valid = true;
        String capitalAssetTransactionTypeCode = "";
        AssetTransactionType capitalAssetTransactionType = null;
        String itemIdentifier = item.getItemIdentifierString();

        if (item.getPurchasingCapitalAssetItem() != null) {
            capitalAssetTransactionTypeCode = item.getPurchasingCapitalAssetItem().getCapitalAssetTransactionTypeCode();
            // ((PurchasingCapitalAssetItemBase)
            // item.getPurchasingCapitalAssetItem()).refreshReferenceObject(PurapPropertyConstants.CAPITAL_ASSET_TRANSACTION_TYPE);
            capitalAssetTransactionType = (AssetTransactionType) item.getPurchasingCapitalAssetItem().getCapitalAssetTransactionType();
        }

        // These checks do not depend on Accounting Line information, but do depend on transaction type.
        if (!StringUtils.isEmpty(capitalAssetTransactionTypeCode)) {
            valid &= validateCapitalAssetTransactionTypeVersusRecurrence(capitalAssetTransactionType, recurringPaymentTypeCode, itemIdentifier);
        }
        return valid &= validatePurapItemCapitalAsset(item, capitalAssetTransactionType);
    }

    /**
     * This method validated purapItem giving a transtype
     * 
     * @param recurringPaymentType
     * @param item
     * @param warn
     * @return
     */
    protected boolean validatePurapItemCapitalAsset(PurApItem item, AssetTransactionType capitalAssetTransactionType) {
        boolean valid = true;
        String itemIdentifier = item.getItemIdentifierString();
        boolean quantityBased = item.getItemType().isQuantityBasedGeneralLedgerIndicator();
        BigDecimal itemUnitPrice = item.getItemUnitPrice();
        HashSet<String> capitalOrExpenseSet = new HashSet<String>(); // For the first validation on every accounting line.


        // Do the checks that depend on Accounting Line information.
        for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
            // Because of ObjectCodeCurrent, we had to refresh this.
            accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            ObjectCode objectCode = accountingLine.getObjectCode();

            if (ObjectUtils.isNotNull(objectCode)) {
                String capitalOrExpense = objectCodeCapitalOrExpense(objectCode);
                capitalOrExpenseSet.add(capitalOrExpense); // HashSets accumulate distinct values (and nulls) only.

                valid &= validateAccountingLinesNotCapitalAndExpense(capitalOrExpenseSet, itemIdentifier, objectCode);


                // Do the checks involving capital asset transaction type.
                if (capitalAssetTransactionType != null) {
                    valid &= validateObjectCodeVersusTransactionType(objectCode, capitalAssetTransactionType, itemIdentifier, quantityBased);
                }
            }
        }
        return valid;
    }

    /**
     * Capital Asset validation: An item cannot have among its associated accounting lines both object codes that indicate it is a
     * Capital Asset, and object codes that indicate that the item is not a Capital Asset. Whether an object code indicates that the
     * item is a Capital Asset is determined by whether its level is among a specific set of levels that are deemed acceptable for
     * such items.
     * 
     * @param capitalOrExpenseSet A HashSet containing the distinct values of either "Capital" or "Expense" that have been added to
     *        it.
     * @param warn A boolean which should be set to true if warnings are to be set on the calling document
     * @param itemIdentifier A String identifying the item for error display
     * @param objectCode An ObjectCode, for error display
     * @return True if the given HashSet contains at most one of either "Capital" or "Expense"
     */
    protected boolean validateAccountingLinesNotCapitalAndExpense(HashSet<String> capitalOrExpenseSet, String itemIdentifier, ObjectCode objectCode) {
        boolean valid = true;
        // If the set contains more than one distinct string, fail.
        if (capitalOrExpenseSet.size() > 1) {
            GlobalVariables.getErrorMap().putError(KFSConstants.FINANCIAL_OBJECT_LEVEL_CODE_PROPERTY_NAME, CabKeyConstants.ERROR_ITEM_CAPITAL_AND_EXPENSE, itemIdentifier, objectCode.getFinancialObjectCodeName());
            valid &= false;
        }
        return valid;
    }

    protected boolean validateLevelCapitalAssetIndication(BigDecimal unitPrice, ObjectCode objectCode, String itemIdentifier) {

        String capitalAssetPriceThresholdParam = this.getParameterService().getParameterValue(AssetGlobal.class, CabParameterConstants.CapitalAsset.CAPITALIZATION_LIMIT_AMOUNT);
        BigDecimal priceThreshold = null;
        try {
            priceThreshold = new BigDecimal(capitalAssetPriceThresholdParam);
        }
        catch (NumberFormatException nfe) {
            throw new RuntimeException("the parameter for CAPITAL_ASSET_OBJECT_LEVELS came was not able to be converted to a number.", nfe);
        }
        if (unitPrice.compareTo(priceThreshold) >= 0) {
            List<String> possibleCAMSObjectLevels = this.getParameterService().getParameterValues(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS);
            if (possibleCAMSObjectLevels.contains(objectCode.getFinancialObjectLevelCode())) {

                String warning = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CabKeyConstants.WARNING_ABOVE_THRESHOLD_SUGESTS_CAPITAL_ASSET_LEVEL);
                warning = StringUtils.replace(warning, "{0}", itemIdentifier);
                warning = StringUtils.replace(warning, "{1}", priceThreshold.toString());
                GlobalVariables.getMessageList().add(warning);

                return false;
            }
        }
        return true;
    }

    /**
     * Capital Asset validation: If the item has a transaction type, check that the transaction type is acceptable for the object
     * code sub-types of all the object codes on the associated accounting lines.
     * 
     * @param objectCode
     * @param capitalAssetTransactionType
     * @param warn A boolean which should be set to true if warnings are to be set on the calling document
     * @param itemIdentifier
     * @return
     */
    protected boolean validateObjectCodeVersusTransactionType(ObjectCode objectCode, CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType, String itemIdentifier, boolean quantityBasedItem) {
        boolean valid = true;
        String[] objectCodeSubTypes = {};


        if (quantityBasedItem) {
            String capitalAssetQuantitySubtypeRequiredText = capitalAssetTransactionType.getCapitalAssetQuantitySubtypeRequiredText();
            if (capitalAssetQuantitySubtypeRequiredText != null) {
                objectCodeSubTypes = StringUtils.split(capitalAssetQuantitySubtypeRequiredText, ";");
            }
        }
        else {
            String capitalAssetNonquantitySubtypeRequiredText = capitalAssetTransactionType.getCapitalAssetNonquantitySubtypeRequiredText();
            if (capitalAssetNonquantitySubtypeRequiredText != null) {
                objectCodeSubTypes = StringUtils.split(capitalAssetNonquantitySubtypeRequiredText, ";");
            }
        }

        boolean found = false;
        for (String subType : objectCodeSubTypes) {
            if (StringUtils.equals(subType, objectCode.getFinancialObjectSubTypeCode())) {
                found = true;
                break;
            }
        }

        if (!found) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_TRAN_TYPE_OBJECT_CODE_SUBTYPE, itemIdentifier, capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(), objectCode.getFinancialObjectCodeName(), objectCode.getFinancialObjectSubType().getFinancialObjectSubTypeName());

            valid &= false;
        }
        return valid;
    }

    /**
     * Capital Asset validation: If the item has a transaction type, check that if the document specifies that recurring payments
     * are to be made, that the transaction type is one that is appropriate for this situation, and that if the document does not
     * specify that recurring payments are to be made, that the transaction type is one that is appropriate for that situation.
     * 
     * @param capitalAssetTransactionType
     * @param recurringPaymentType
     * @param warn A boolean which should be set to true if warnings are to be set on the calling document
     * @param itemIdentifier
     * @return
     */
    protected boolean validateCapitalAssetTransactionTypeVersusRecurrence(CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType, String recurringPaymentTypeCode, String itemIdentifier) {
        boolean valid = true;

        // If there is a tran type ...
        if ((capitalAssetTransactionType != null) && (capitalAssetTransactionType.getCapitalAssetTransactionTypeCode() != null)) {
            String recurringTransactionTypeCodes = this.getParameterService().getParameterValue(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.RECURRING_CAMS_TRAN_TYPES);


            if (StringUtils.isNotEmpty(recurringPaymentTypeCode)) { // If there is a recurring payment type ...
                if (!StringUtils.contains(recurringTransactionTypeCodes, capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                    // There should be a recurring tran type code.
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE, itemIdentifier, capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(), CabConstants.ValidationStrings.RECURRING);
                    valid &= false;
                }
            }
            else { // If the payment type is not recurring ...
                // There should not be a recurring transaction type code.
                if (StringUtils.contains(recurringTransactionTypeCodes, capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE, itemIdentifier, capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(), CabConstants.ValidationStrings.NON_RECURRING);

                    valid &= false;
                }
            }
        }
        else { // If there is no transaction type ...
            if (StringUtils.isNotEmpty(recurringPaymentTypeCode)) { // If there is a recurring payment type ...
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_NO_TRAN_TYPE, itemIdentifier, CabConstants.ValidationStrings.RECURRING);
                valid &= false;
            }
            else { // If the payment type is not recurring ...
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_NO_TRAN_TYPE, itemIdentifier, CabConstants.ValidationStrings.NON_RECURRING);
                valid &= false;
            }
        }

        return valid;
    }

    /**
     * Utility wrapping isCapitalAssetObjectCode for the use of processItemCapitalAssetValidation.
     * 
     * @param oc An ObjectCode
     * @return A String indicating that the given object code is either Capital or Expense
     */
    protected String objectCodeCapitalOrExpense(ObjectCode oc) {
        String capital = CabConstants.ValidationStrings.CAPITAL;
        String expense = CabConstants.ValidationStrings.EXPENSE;
        return (isCapitalAssetObjectCode(oc) ? capital : expense);
    }

    /**
     * Predicate to determine whether the given object code is of a specified object sub type required for purap cams.
     * 
     * @param oc An ObjectCode
     * @return True if the ObjectSubType is the one designated for capital assets.
     */
    protected boolean isCapitalAssetObjectCode(ObjectCode oc) {
        String capitalAssetObjectSubType = this.getParameterService().getParameterValue(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, PurapParameterConstants.CapitalAsset.PURCHASING_OBJECT_SUB_TYPES);
        return (StringUtils.containsIgnoreCase(capitalAssetObjectSubType, oc.getFinancialObjectSubTypeCode()) ? true : false);
    }

    protected boolean validateCapitalAssetLocationAddressFieldsOneOrMultipleSystemType(List<CapitalAssetSystem> capitalAssetSystems) {
        boolean valid = true;
        int systemCount = 0;
        for (CapitalAssetSystem system : capitalAssetSystems) {
            List<CapitalAssetLocation> capitalAssetLocations = system.getCapitalAssetLocations();
            StringBuffer errorKey = new StringBuffer("document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_SYSTEMS + "[" + new Integer(systemCount++) + "].");
            errorKey.append("capitalAssetLocations");
            int locationCount = 0;
            for (CapitalAssetLocation location : capitalAssetLocations) {
                errorKey.append("[" + locationCount++ + "].");
                valid &= validateCapitalAssetLocationAddressFields(location, errorKey);
            }
        }
        return valid;
    }

    protected boolean validateCapitalAssetLocationAddressFieldsForIndividualSystemType(List<PurchasingCapitalAssetItem> capitalAssetItems) {
        boolean valid = true;
        for (PurchasingCapitalAssetItem item : capitalAssetItems) {
            CapitalAssetSystem system = item.getPurchasingCapitalAssetSystem();
            List<CapitalAssetLocation> capitalAssetLocations = system.getCapitalAssetLocations();
            StringBuffer errorKey = new StringBuffer("document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS + "[" + new Integer(item.getPurchasingItem().getItemLineNumber().intValue() - 1) + "].");
            errorKey.append("purchasingCapitalAssetSystem.capitalAssetLocations");
            int i = 0;
            for (CapitalAssetLocation location : capitalAssetLocations) {
                errorKey.append("[" + i++ + "].");
                valid &= validateCapitalAssetLocationAddressFields(location, errorKey);
            }
        }
        return valid;
    }

    protected boolean validateCapitalAssetLocationAddressFields(CapitalAssetLocation location, StringBuffer errorKey) {
        boolean valid = true;
        if (StringUtils.isBlank(location.getCapitalAssetLine1Address())) {
            GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ADDRESS_LINE1);
            valid &= false;
        }
        if (StringUtils.isBlank(location.getCapitalAssetCityName())) {
            GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_CITY);
            valid &= false;
        }
        if (StringUtils.isBlank(location.getCapitalAssetCountryCode())) {
            GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_COUNTRY);
            valid &= false;
        }
        else if (location.getCapitalAssetCountryCode().equals(KFSConstants.COUNTRY_CODE_UNITED_STATES)) {
            if (StringUtils.isBlank(location.getCapitalAssetStateCode())) {
                GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_STATE);
                valid &= false;
            }
            if (StringUtils.isBlank(location.getCapitalAssetPostalCode())) {
                GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_POSTAL_CODE);
                valid &= false;
            }
        }
        if (!location.isOffCampusIndicator()) {
            if (StringUtils.isBlank(location.getCampusCode())) {
                GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_CAMPUS);
                valid &= false;
            }
            if (StringUtils.isBlank(location.getBuildingCode())) {
                GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_BUILDING);
                valid &= false;
            }
            if (StringUtils.isBlank(location.getBuildingRoomNumber())) {
                GlobalVariables.getErrorMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM);
                valid &= false;
            }
        }
        return valid;
    }

    protected boolean validateAccountsPayableItem(AccountsPayableItem apItem) {
        boolean valid = true;
        valid &= validatePurapItemCapitalAsset(apItem, (AssetTransactionType) apItem.getCapitalAssetTransactionType());
        return valid;
    }

    // end of methods for purap

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validateFinancialProcessingData(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     * @param accountingDocument and capitalAssetInformation
     * @return True if the FinancialProcessingData is valid.
     */
    public boolean validateFinancialProcessingData(AccountingDocument accountingDocument, CapitalAssetInformation capitalAssetInformation) {
        boolean valid = true;

        // Check if we need to collect cams data
        AccountCapitalObjectCode accountCapitalObjectCode = this.getCapitalAssetObjectSubTypeLinesFlag(accountingDocument);
        boolean isNewAssetBlank = isNewAssetBlank(capitalAssetInformation);
        boolean isUpdateAssetBlank = isUpdateAssetBlank(capitalAssetInformation);

        // validate asset information provided by the user are allowed
        valid = accountCapitalObjectCode.validateAssetInfoAllowed(accountingDocument, isNewAssetBlank, isUpdateAssetBlank);

        if (valid) {
            // non-capital object code, no further check needed for asset
            if (AccountCapitalObjectCode.BOTH_NONCAP.equals(accountCapitalObjectCode)) {
                return true;
            }
            else {
                if (!isUpdateAssetBlank) {
                    // Validate update Asset information
                    valid &= validateUpdateCapitalAssetField(capitalAssetInformation, accountingDocument);
                }
                else {
                    // Validate New Asset information
                    valid &= checkNewCapitalAssetFieldsExist(capitalAssetInformation);
                    if (valid) {
                        valid = validateNewCapitalAssetFields(capitalAssetInformation);
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Get the Capital Asset Object Code from the accounting lines.
     * 
     * @param accountingDocument
     * @return getCapitalAssetObjectSubTypeLinesFlag = AccountCapitalObjectCode.NON_CAPITAL ==> no assetObjectSubType code
     *         AccountCapitalObjectCode.FROM_CAPITAL ==> assetObjectSubType code on source lines
     *         AccountCapitalObjectCode.FROM_CAPITAL ==> assetObjectSubType code on target lines
     *         AccountCapitalObjectCode.BOTH_CAPITAL ==> assetObjectSubType code on both source and target lines
     */
    protected AccountCapitalObjectCode getCapitalAssetObjectSubTypeLinesFlag(AccountingDocument accountingDocument) {
        List<String> financialProcessingCapitalObjectSubTypes = this.getParameterService().getParameterValues(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.FINANCIAL_PROCESSING_CAPITAL_OBJECT_SUB_TYPES);
        AccountCapitalObjectCode capitalAssetObjectSubTypeLinesFlag = AccountCapitalObjectCode.BOTH_NONCAP;

        // Check if SourceAccountingLine has objectSub type code
        List<SourceAccountingLine> sAccountingLines = accountingDocument.getSourceAccountingLines();
        for (SourceAccountingLine sourceAccountingLine : sAccountingLines) {
            ObjectCode objectCode = sourceAccountingLine.getObjectCode();

            if (ObjectUtils.isNotNull(objectCode)) {
                String objectSubTypeCode = objectCode.getFinancialObjectSubTypeCode();
                if (financialProcessingCapitalObjectSubTypes.contains(objectSubTypeCode)) {
                    capitalAssetObjectSubTypeLinesFlag = AccountCapitalObjectCode.FROM_CAPITAL_TO_NONCAP;
                    break;
                }
            }
        }

        // Check if TargetAccountingLine has objectSub type code
        List<TargetAccountingLine> tAccountingLines = accountingDocument.getTargetAccountingLines();
        for (TargetAccountingLine targetAccountingLine : tAccountingLines) {
            ObjectCode objectCode = targetAccountingLine.getObjectCode();

            if (ObjectUtils.isNotNull(objectCode)) {
                String objectSubTypeCode = objectCode.getFinancialObjectSubTypeCode();
                if (financialProcessingCapitalObjectSubTypes.contains(objectSubTypeCode)) {
                    capitalAssetObjectSubTypeLinesFlag = capitalAssetObjectSubTypeLinesFlag == AccountCapitalObjectCode.FROM_CAPITAL_TO_NONCAP ? AccountCapitalObjectCode.BOTH_CAPITAL : AccountCapitalObjectCode.FROM_NONCAP_TO_CAPITAL;
                    break;
                }
            }
        }

        return capitalAssetObjectSubTypeLinesFlag;
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#hasCapitalAssetObjectSubType(org.kuali.kfs.sys.document.AccountingDocument)
     */
    public boolean hasCapitalAssetObjectSubType(AccountingDocument accountingDocument) {
        return getCapitalAssetObjectSubTypeLinesFlag(accountingDocument) != null;
    }


    /**
     * To check if data exists on create new asset
     * 
     * @param capitalAssetInformation
     * @return boolean false if the new asset is not blank
     */
    private boolean isNewAssetBlank(CapitalAssetInformation capitalAssetInformation) {
        boolean isBlank = true;

        if (ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetTypeCode()) || ObjectUtils.isNotNull(capitalAssetInformation.getVendorName()) || ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetQuantity()) || ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetManufacturerName()) || ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetManufacturerModelNumber()) || ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetDescription())) {
            isBlank = false;
        }
        return isBlank;
    }

    /**
     * To check if data exists on update asset
     * 
     * @param capitalAssetInformation
     * @return boolean false if the update asset is not blank
     */
    private boolean isUpdateAssetBlank(CapitalAssetInformation capitalAssetInformation) {
        boolean isBlank = true;

        if (ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetNumber())) {
            isBlank = false;
        }
        return isBlank;
    }

    /**
     * To validate if the asset is active
     * 
     * @param capitalAssetManagementAsset the asset to be validated
     * @return boolean false if the asset is not active
     */
    private boolean validateUpdateCapitalAssetField(CapitalAssetInformation capitalAssetInformation, AccountingDocument accountingDocument) {
        boolean valid = true;

        Map<String, String> params = new HashMap<String, String>();
        params.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, capitalAssetInformation.getCapitalAssetNumber().toString());
        Asset asset = (Asset) this.getBusinessObjectService().findByPrimaryKey(Asset.class, params);

        List<Long> assetNumbers = new ArrayList<Long>();
        assetNumbers.add(capitalAssetInformation.getCapitalAssetNumber());

        if (ObjectUtils.isNull(asset)) {
            valid = false;
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_NUMBER);
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
        }
        else if (!(this.getAssetService().isCapitalAsset(asset) && !this.getAssetService().isAssetRetired(asset))) {
            // check asset status must be capital asset active.
            valid = false;
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_ACTIVE_CAPITAL_ASSET_REQUIRED);
        }
        else {
            String documentNumber = accountingDocument.getDocumentNumber();
            String documentType = getDocumentTypeName(accountingDocument);

            if (getCapitalAssetManagementModuleService().isFpDocumentEligibleForAssetLock(accountingDocument, documentType) && getCapitalAssetManagementModuleService().isAssetLocked(assetNumbers, documentType, documentNumber)) {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Check if all required fields exist on new asset
     * 
     * @param capitalAssetInformation the fields of add asset to be checked
     * @return boolean false if a required field is missing
     */
    private boolean checkNewCapitalAssetFieldsExist(CapitalAssetInformation capitalAssetInformation) {
        boolean valid = true;

        if (StringUtils.isBlank(capitalAssetInformation.getCapitalAssetTypeCode())) {
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE);
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        if (capitalAssetInformation.getCapitalAssetQuantity() == null || capitalAssetInformation.getCapitalAssetQuantity() <= 0) {
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_QUANTITY);
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_QUANTITY, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        if (StringUtils.isBlank(capitalAssetInformation.getVendorName())) {
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.VENDOR_NAME);
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.VENDOR_NAME, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        if (StringUtils.isBlank(capitalAssetInformation.getCapitalAssetManufacturerName())) {
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_MANUFACTURE_NAME);
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_MANUFACTURE_NAME, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        if (StringUtils.isBlank(capitalAssetInformation.getCapitalAssetDescription())) {
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, CamsPropertyConstants.Asset.CAPITAL_ASSET_DESCRIPTION);
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Asset.CAPITAL_ASSET_DESCRIPTION, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        int index = 0;
        List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAssetInformation.getCapitalAssetInformationDetails();
        for (CapitalAssetInformationDetail dtl : capitalAssetInformationDetails) {
            String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION_DETAILS;

            if (StringUtils.isBlank(dtl.getCampusCode())) {
                String label = this.getDataDictionaryService().getAttributeLabel(Campus.class, KFSPropertyConstants.CAMPUS_CODE);
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.CAMPUS_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }

            if (StringUtils.isBlank(dtl.getBuildingCode())) {
                String label = this.getDataDictionaryService().getAttributeLabel(Building.class, KFSPropertyConstants.BUILDING_CODE);
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }

            if (StringUtils.isBlank(dtl.getBuildingRoomNumber())) {
                String label = this.getDataDictionaryService().getAttributeLabel(Room.class, KFSPropertyConstants.BUILDING_ROOM_NUMBER);
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_ROOM_NUMBER, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }
            index++;
        }

        return valid;
    }

    /**
     * To validate new asset information
     * 
     * @param capitalAssetInformation the information of add asset to be validated
     * @return boolean false if data is incorrect
     */
    private boolean validateNewCapitalAssetFields(CapitalAssetInformation capitalAssetInformation) {
        boolean valid = true;

        Map<String, String> params = new HashMap<String, String>();
        params.put(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, capitalAssetInformation.getCapitalAssetTypeCode().toString());
        AssetType assetType = (AssetType) this.getBusinessObjectService().findByPrimaryKey(AssetType.class, params);

        if (ObjectUtils.isNull(assetType)) {
            valid = false;
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE);
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
        }

        int index = 0;
        List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAssetInformation.getCapitalAssetInformationDetails();
        for (CapitalAssetInformationDetail dtl : capitalAssetInformationDetails) {
            // We have to explicitly call this DD service to uppercase each field. This may not be the best place and maybe form
            // populate
            // is a better place but we CAMS team don't own FP document. This is the best we can do for now.
            SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(dtl);
            String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION_DETAILS;

            if (StringUtils.isNotBlank(dtl.getCapitalAssetTagNumber()) && !dtl.getCapitalAssetTagNumber().equalsIgnoreCase(CamsConstants.Asset.NON_TAGGABLE_ASSET)) {
                if (isTagNumberDuplicate(capitalAssetInformationDetails, dtl)) {
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.CAPITAL_ASSET_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, dtl.getCapitalAssetTagNumber());
                    valid = false;
                }
            }

            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.CAMPUS_CODE, dtl.getCampusCode());
            Campus campus = (Campus) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Campus.class).getExternalizableBusinessObject(Campus.class, criteria);
            if (ObjectUtils.isNull(campus)) {
                valid = false;
                String label = this.getDataDictionaryService().getAttributeLabel(Campus.class, KFSPropertyConstants.CAMPUS_CODE);
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.CAMPUS_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
            }

            params = new HashMap<String, String>();
            params.put(KFSPropertyConstants.CAMPUS_CODE, dtl.getCampusCode());
            params.put(KFSPropertyConstants.BUILDING_CODE, dtl.getBuildingCode());
            Building building = (Building) this.getBusinessObjectService().findByPrimaryKey(Building.class, params);
            if (ObjectUtils.isNull(building)) {
                valid = false;
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_CODE, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_BUILDING_CODE, dtl.getBuildingCode(), dtl.getCampusCode());
            }

            params = new HashMap<String, String>();
            params.put(KFSPropertyConstants.CAMPUS_CODE, dtl.getCampusCode());
            params.put(KFSPropertyConstants.BUILDING_CODE, dtl.getBuildingCode());
            params.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, dtl.getBuildingRoomNumber());
            Room room = (Room) this.getBusinessObjectService().findByPrimaryKey(Room.class, params);
            if (ObjectUtils.isNull(room)) {
                valid = false;
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_ROOM_NUMBER, dtl.getBuildingRoomNumber(), dtl.getBuildingCode(), dtl.getCampusCode());
            }
            index++;
        }

        return valid;
    }

    /**
     * To check if the tag number is duplicate or in use
     * 
     * @param capitalAssetInformation, capitalAssetInformationDetail
     * @return boolean false if data is duplicate or in use
     */
    private boolean isTagNumberDuplicate(List<CapitalAssetInformationDetail> capitalAssetInformationDetails, CapitalAssetInformationDetail dtl) {
        boolean duplicateTag = false;
        int tagCounter = 0;
        if (!this.getAssetService().findActiveAssetsMatchingTagNumber(dtl.getCapitalAssetTagNumber()).isEmpty()) {
            // Tag number is already in use
            duplicateTag = true;
        }
        else {
            for (CapitalAssetInformationDetail capitalAssetInfoDetl : capitalAssetInformationDetails) {
                if (capitalAssetInfoDetl.getCapitalAssetTagNumber().equalsIgnoreCase(dtl.getCapitalAssetTagNumber().toString())) {
                    tagCounter++;
                }
            }
            if (tagCounter > 1) {
                // Tag number already exists in the collection
                duplicateTag = true;
            }
        }
        return duplicateTag;
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#notifyRouteStatusChange(java.lang.String,
     *      java.lang.String)
     */
    public void notifyRouteStatusChange(DocumentHeader documentHeader) {

        KualiWorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();
        String documentNumber = documentHeader.getDocumentNumber();

        String documentType = workflowDocument.getDocumentType();

        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {
            // release CAB line items
            activateCabPOLines(documentNumber);
            activateCabGlLines(documentNumber);
        }
        if (workflowDocument.stateIsProcessed()) {
            // update CAB GL lines if fully processed
            updatePOLinesStatusAsProcessed(documentNumber);
            updateGlLinesStatusAsProcessed(documentNumber);
            // report asset numbers to PO
            Integer poId = getPurchaseOrderIdentifier(documentNumber);
            if (poId != null) {
                List<Long> assetNumbers = null;
                if (DocumentTypeName.ASSET_ADD_GLOBAL.equalsIgnoreCase(documentType)) {
                    assetNumbers = getAssetNumbersFromAssetGlobal(documentNumber);
                }
                else if (DocumentTypeName.ASSET_PAYMENT.equalsIgnoreCase(documentType)) {
                    assetNumbers = getAssetNumbersFromAssetPayment(documentNumber);
                }

                if (!assetNumbers.isEmpty()) {
                    String noteText = buildNoteTextForPurApDoc(documentType, assetNumbers);
                    SpringContext.getBean(PurchasingAccountsPayableModuleService.class).addAssignedAssetNumbers(poId, workflowDocument.getInitiatorPrincipalId(), noteText);
                }
            }
        }

    }

    /**
     * update cab non-PO lines status code for item/account/glEntry to 'P' as fully processed when possible
     * 
     * @param documentNumber
     */
    protected void updateGlLinesStatusAsProcessed(String documentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAMS_DOCUMENT_NUMBER, documentNumber);
        Collection<GeneralLedgerEntryAsset> matchingGlAssets = this.getBusinessObjectService().findMatching(GeneralLedgerEntryAsset.class, fieldValues);
        if (matchingGlAssets != null && !matchingGlAssets.isEmpty()) {
            for (GeneralLedgerEntryAsset generalLedgerEntryAsset : matchingGlAssets) {
                GeneralLedgerEntry generalLedgerEntry = generalLedgerEntryAsset.getGeneralLedgerEntry();

                // update gl status as processed
                generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS);
                this.getBusinessObjectService().save(generalLedgerEntry);

                // release asset lock
                if (isFpDocumentFullyProcessed(generalLedgerEntry)) {
                    getCapitalAssetManagementModuleService().deleteAssetLocks(generalLedgerEntry.getDocumentNumber(), null);
                }
            }
        }
    }

    /**
     * Check all generalLedgerEntries from the same FP document are fully processed.
     * 
     * @param generalLedgerEntry
     * @return
     */
    protected boolean isFpDocumentFullyProcessed(GeneralLedgerEntry generalLedgerEntry) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, generalLedgerEntry.getDocumentNumber());
        Collection<GeneralLedgerEntry> matchingGlEntries = this.getBusinessObjectService().findMatching(GeneralLedgerEntry.class, fieldValues);

        for (GeneralLedgerEntry glEntry : matchingGlEntries) {
            if (!CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS.equals(glEntry.getActivityStatusCode())) {
                return false;
            }
        }
        return true;
    }

    /**
     * update CAB PO lines status code for item/account/glEntry to 'P' as fully processed when possible
     * 
     * @param documentNumber
     */
    protected void updatePOLinesStatusAsProcessed(String documentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAMS_DOCUMENT_NUMBER, documentNumber);
        Collection<PurchasingAccountsPayableItemAsset> matchingAssets = this.getBusinessObjectService().findMatching(PurchasingAccountsPayableItemAsset.class, fieldValues);
        if (matchingAssets != null && !matchingAssets.isEmpty()) {
            // Map<Long, GeneralLedgerEntry> updateGlLines = new HashMap<Long, GeneralLedgerEntry>();
            // update item and account status code to 'P' as fully processed
            for (PurchasingAccountsPayableItemAsset itemAsset : matchingAssets) {

                itemAsset.setActivityStatusCode(CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS);
                for (PurchasingAccountsPayableLineAssetAccount assetAccount : itemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
                    assetAccount.setActivityStatusCode(CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS);
                    GeneralLedgerEntry generalLedgerEntry = assetAccount.getGeneralLedgerEntry();

                    // updateGlLines.put(generalLedgerEntry.getGeneralLedgerAccountIdentifier(), generalLedgerEntry);

                    if (isGlEntryFullyProcessed(generalLedgerEntry)) {
                        generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS);
                        this.getBusinessObjectService().save(generalLedgerEntry);
                    }
                }

                // update cab document status code to 'P' as all its items fully processed
                PurchasingAccountsPayableDocument purapDocument = itemAsset.getPurchasingAccountsPayableDocument();
                if (isDocumentFullyProcessed(purapDocument)) {
                    purapDocument.setActivityStatusCode(CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS);
                }

                this.getBusinessObjectService().save(purapDocument);

                String lockingInformation = null;
                PurchaseOrderDocument poDocument = getPurApInfoService().getCurrentDocumentForPurchaseOrderIdentifier(purapDocument.getPurchaseOrderIdentifier());
                // Only individual system will lock on item line number. other system will using preq/cm doc nbr as the locking
                // key
                if (PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS.equalsIgnoreCase(poDocument.getCapitalAssetSystemTypeCode())) {
                    lockingInformation = itemAsset.getAccountsPayableLineItemIdentifier().toString();
                }
                // release the asset lock no matter if it's Asset global or Asset Payment since the CAB user can create Asset global
                // doc even if Purap Asset numbers existing.
                if (isAccountsPayableItemLineFullyProcessed(purapDocument, lockingInformation)) {
                    getCapitalAssetManagementModuleService().deleteAssetLocks(purapDocument.getDocumentNumber(), lockingInformation);
                }

            }
        }

    }

    /**
     * If lockingInformation is not empty, check all item lines from the same PurAp item are fully processed. If lockingInformation
     * is empty, we check all items from the same PREQ/CM document processed as fully processed.
     * 
     * @param itemAsset
     * @return
     */
    protected boolean isAccountsPayableItemLineFullyProcessed(PurchasingAccountsPayableDocument purapDocument, String lockingInformation) {
        for (PurchasingAccountsPayableItemAsset item : purapDocument.getPurchasingAccountsPayableItemAssets()) {
            if ((StringUtils.isBlank(lockingInformation) && !CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS.equals(item.getActivityStatusCode())) || (StringUtils.isNotBlank(lockingInformation) && item.getAccountsPayableLineItemIdentifier().equals(lockingInformation) && !CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS.equals(item.getActivityStatusCode()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if Gl Entry related accounts are fully processed
     * 
     * @param glEntry
     * @return
     */
    protected boolean isGlEntryFullyProcessed(GeneralLedgerEntry glEntry) {
        for (PurchasingAccountsPayableLineAssetAccount account : glEntry.getPurApLineAssetAccounts()) {
            if (!CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS.equalsIgnoreCase(account.getActivityStatusCode())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if PurApDocument related items are fully processed.
     * 
     * @param purapDocument
     * @return
     */
    protected boolean isDocumentFullyProcessed(PurchasingAccountsPayableDocument purapDocument) {
        for (PurchasingAccountsPayableItemAsset item : purapDocument.getPurchasingAccountsPayableItemAssets()) {
            if (!CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS.equalsIgnoreCase(item.getActivityStatusCode())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Build the appropriate note text being set to the purchase order document.
     * 
     * @param documentType
     * @param assetNumbers
     * @return
     */
    private String buildNoteTextForPurApDoc(String documentType, List<Long> assetNumbers) {
        StringBuffer noteText = new StringBuffer();

        if (DocumentTypeName.ASSET_ADD_GLOBAL.equalsIgnoreCase(documentType)) {
            noteText.append("Asset Numbers have been created for this document: ");
        }
        else {
            noteText.append("Existing Asset Numbers have been applied for this document: ");
        }

        for (int i = 0; i < assetNumbers.size(); i++) {
            noteText.append(assetNumbers.get(i).toString());
            if (i < assetNumbers.size() - 1) {
                noteText.append(", ");
            }
        }

        return noteText.toString();
    }

    /**
     * Acquire asset numbers from CAMS asset payment document.
     * 
     * @param documentNumber
     * @param assetNumbers
     */
    private List<Long> getAssetNumbersFromAssetGlobal(String documentNumber) {
        List<Long> assetNumbers = new ArrayList<Long>();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CamsPropertyConstants.AssetGlobalDetail.DOCUMENT_NUMBER, documentNumber);
        Collection<AssetGlobalDetail> assetGlobalDetails = this.getBusinessObjectService().findMatching(AssetGlobalDetail.class, fieldValues);
        for (AssetGlobalDetail detail : assetGlobalDetails) {
            assetNumbers.add(detail.getCapitalAssetNumber());
        }
        return assetNumbers;
    }

    /**
     * Acquire asset numbers from CAMS asset global document.
     * 
     * @param documentNumber
     * @param assetNumbers
     */
    private List<Long> getAssetNumbersFromAssetPayment(String documentNumber) {
        List<Long> assetNumbers = new ArrayList<Long>();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CamsPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        Collection<AssetPaymentAssetDetail> paymentAssetDetails = this.getBusinessObjectService().findMatching(AssetPaymentAssetDetail.class, fieldValues);
        for (AssetPaymentAssetDetail detail : paymentAssetDetails) {
            if (ObjectUtils.isNotNull(detail.getAsset())) {
                assetNumbers.add(detail.getCapitalAssetNumber());
            }
        }

        return assetNumbers;
    }

    /**
     * Query PurchasingAccountsPayableItemAsset and return the purchaseOrderIdentifier if the given documentNumber is initiated from
     * the PurAp line.
     * 
     * @param documentNumber
     * @return
     */
    private Integer getPurchaseOrderIdentifier(String camsDocumentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAMS_DOCUMENT_NUMBER, camsDocumentNumber);
        Collection<PurchasingAccountsPayableItemAsset> matchingItems = this.getBusinessObjectService().findMatching(PurchasingAccountsPayableItemAsset.class, fieldValues);

        for (PurchasingAccountsPayableItemAsset item : matchingItems) {
            if (ObjectUtils.isNull(item.getPurchasingAccountsPayableDocument())) {
                item.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableItemAsset.PURCHASING_ACCOUNTS_PAYABLE_DOCUMENT);
            }
            return item.getPurchasingAccountsPayableDocument().getPurchaseOrderIdentifier();
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#getCurrentPurchaseOrderDocumentNumber(java.lang.String)
     */
    public String getCurrentPurchaseOrderDocumentNumber(String camsDocumentNumber) {
        Integer poId = getPurchaseOrderIdentifier(camsDocumentNumber);
        if (poId != null) {
            PurchaseOrderDocument poDocument = getPurApInfoService().getCurrentDocumentForPurchaseOrderIdentifier(poId);
            if (ObjectUtils.isNotNull(poDocument)) {
                return poDocument.getDocumentNumber();
            }
        }
        return null;
    }

    /**
     * Activates CAB GL Lines
     * 
     * @param documentNumber String
     */
    protected void activateCabGlLines(String documentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAMS_DOCUMENT_NUMBER, documentNumber);
        Collection<GeneralLedgerEntryAsset> matchingGlAssets = this.getBusinessObjectService().findMatching(GeneralLedgerEntryAsset.class, fieldValues);
        if (matchingGlAssets != null && !matchingGlAssets.isEmpty()) {
            for (GeneralLedgerEntryAsset generalLedgerEntryAsset : matchingGlAssets) {
                GeneralLedgerEntry generalLedgerEntry = generalLedgerEntryAsset.getGeneralLedgerEntry();
                generalLedgerEntry.setTransactionLedgerSubmitAmount(KualiDecimal.ZERO);
                generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
                this.getBusinessObjectService().save(generalLedgerEntry);
                this.getBusinessObjectService().delete(generalLedgerEntryAsset);
            }
        }
    }

    /**
     * Activates PO Lines
     * 
     * @param documentNumber String
     */
    protected void activateCabPOLines(String documentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAMS_DOCUMENT_NUMBER, documentNumber);
        Collection<PurchasingAccountsPayableItemAsset> matchingPoAssets = this.getBusinessObjectService().findMatching(PurchasingAccountsPayableItemAsset.class, fieldValues);

        if (matchingPoAssets != null && !matchingPoAssets.isEmpty()) {
            for (PurchasingAccountsPayableItemAsset itemAsset : matchingPoAssets) {
                PurchasingAccountsPayableDocument purapDocument = itemAsset.getPurchasingAccountsPayableDocument();
                purapDocument.setActivityStatusCode(CabConstants.ActivityStatusCode.MODIFIED);
                this.getBusinessObjectService().save(purapDocument);
                itemAsset.setActivityStatusCode(CabConstants.ActivityStatusCode.MODIFIED);
                this.getBusinessObjectService().save(itemAsset);
                List<PurchasingAccountsPayableLineAssetAccount> lineAssetAccounts = itemAsset.getPurchasingAccountsPayableLineAssetAccounts();
                for (PurchasingAccountsPayableLineAssetAccount assetAccount : lineAssetAccounts) {
                    assetAccount.setActivityStatusCode(CabConstants.ActivityStatusCode.MODIFIED);
                    this.getBusinessObjectService().save(assetAccount);
                    GeneralLedgerEntry generalLedgerEntry = assetAccount.getGeneralLedgerEntry();
                    KualiDecimal submitAmount = generalLedgerEntry.getTransactionLedgerSubmitAmount();
                    if (submitAmount == null) {
                        submitAmount = KualiDecimal.ZERO;
                    }
                    submitAmount = submitAmount.subtract(assetAccount.getItemAccountTotalAmount());
                    generalLedgerEntry.setTransactionLedgerSubmitAmount(submitAmount);
                    generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.MODIFIED);
                    this.getBusinessObjectService().save(generalLedgerEntry);
                }
            }
        }
    }


    /**
     * gets the document type based on the instance of a class
     * 
     * @param accountingDocument
     * @return
     */
    private String getDocumentTypeName(AccountingDocument accountingDocument) {
        String documentTypeName = null;
        if (accountingDocument instanceof YearEndGeneralErrorCorrectionDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.YEAR_END_GENERAL_ERROR_CORRECTION;
        else if (accountingDocument instanceof YearEndDistributionOfIncomeAndExpenseDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.YEAR_END_DISTRIBUTION_OF_INCOME_AND_EXPENSE;
        else if (accountingDocument instanceof ServiceBillingDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.SERVICE_BILLING;
        else if (accountingDocument instanceof GeneralErrorCorrectionDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.GENERAL_ERROR_CORRECTION;
        else if (accountingDocument instanceof CashReceiptDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.CASH_RECEIPT;
        else if (accountingDocument instanceof AdvanceDepositDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.ADVANCE_DEPOSIT;
        else if (accountingDocument instanceof CreditCardReceiptDocument) 
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.CREDIT_CARD_RECEIPT;
        else if (accountingDocument instanceof DistributionOfIncomeAndExpenseDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE;
        else if (accountingDocument instanceof InternalBillingDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.INTERNAL_BILLING;
        else if (accountingDocument instanceof ProcurementCardDocument)
            documentTypeName = KFSConstants.FinancialDocumentTypeCodes.PROCUREMENT_CARD;
        else
            throw new RuntimeException("Invalid FP document type.");

        return documentTypeName;
    }

    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    public DataDictionaryService getDataDictionaryService() {
        return SpringContext.getBean(DataDictionaryService.class);
    }

    public AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    public KualiModuleService getKualiModuleService() {
        return SpringContext.getBean(KualiModuleService.class);
    }

    public CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }

    private PurApInfoService getPurApInfoService() {
        return SpringContext.getBean(PurApInfoService.class);
    }

}
