/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.service.impl;

import static org.kuali.rice.core.api.criteria.PredicateFactory.and;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

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
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
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
import org.kuali.kfs.module.cab.document.service.GlLineService;
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
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryByCriteria.Builder;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterRepositoryService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;

public class CapitalAssetBuilderModuleServiceImpl implements CapitalAssetBuilderModuleService {
    private static Logger LOG = Logger.getLogger(CapitalAssetBuilderModuleService.class);
    
    private boolean duplicateTagLineChecked = false;
    
    protected static enum AccountCapitalObjectCode {
        BOTH_NONCAP {
            boolean validateAssetInfoAllowed(AccountingDocument accountingDocument, boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
                boolean valid = true;
                // non capital object code, disallow the capital information entered.
                if (!isNewAssetBlank || !isUpdateAssetBlank) {
                    // give error if data was entered
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_DO_NOT_ENTER_ANY_DATA);
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
                        GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_UPDATE_ALLOW_ONLY);
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
                if (valid && isDocumentTypeRestricted(accountingDocument)) {
                    valid &= validateOnlyOneAssetInfoEntered(isNewAssetBlank, isUpdateAssetBlank);
                }
                return valid;
            }

            /**
             * The document is restricted for all FP doc types
             * 
             * @see org.kuali.kfs.module.cab.service.impl.CapitalAssetBuilderModuleServiceImpl.AccountCapitalObjectCode#isDocumentTypeRestricted(org.kuali.kfs.sys.document.AccountingDocument)
             */
            boolean isDocumentTypeRestricted(AccountingDocument accountingDocument) {
                return true;
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
        protected static boolean validateAssetInfoEntered(boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
            boolean valid = true;
            // can modify existing or create new. Required to enter one of each type.
            if (isNewAssetBlank && isUpdateAssetBlank) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_REQUIRE_DATA_ENTRY);
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
        protected static boolean validateOnlyOneAssetInfoEntered(boolean isNewAssetBlank, boolean isUpdateAssetBlank) {
            boolean valid = true;
            if (!isNewAssetBlank && !isUpdateAssetBlank) {
                // Data exists on both crate new asset and update asset, give error
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_NEW_OR_UPDATE_ONLY);
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
        // RICE20: FIX ME!!!!!  Someone did not understand the concept of "Externalizable" business objects...We can not
        // necessarily retrieve them from the business object service
        // Actually - using the EBO system here is not necessary at all
        // Since this *IS* the implementation for the built in CAB module, it can use it's BO classes freely
        Class<? extends CapitalAssetBuilderAssetTransactionType> assetTransactionTypeClass = this.getKualiModuleService().getResponsibleModuleService(CapitalAssetBuilderAssetTransactionType.class).getExternalizableBusinessObjectImplementation(CapitalAssetBuilderAssetTransactionType.class);
        Map<String, Object> searchKeys = new HashMap<String, Object>();
        searchKeys.put("active", "Y");
        return (List<CapitalAssetBuilderAssetTransactionType>) this.getBusinessObjectService().findMatching(assetTransactionTypeClass, searchKeys);
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validatePurchasingAccountsPayableData(org.kuali.kfs.sys.document.AccountingDocument)
     */
    public boolean validatePurchasingData(AccountingDocument accountingDocument) {
        Boolean valid = true;
        PurchasingDocument purchasingDocument = (PurchasingDocument) accountingDocument;
        String systemTypeCode = purchasingDocument.getCapitalAssetSystemTypeCode();
        String capitalAssetSystemStateCode = purchasingDocument.getCapitalAssetSystemStateCode();
        String documentType = (purchasingDocument instanceof RequisitionDocument) ? "REQUISITION" : "PURCHASE_ORDER";
        for (PurApItem item : purchasingDocument.getItems()) {
            List accountingLines = item.getSourceAccountingLines();
            for (Iterator iterator = accountingLines.iterator(); iterator.hasNext();) {
                PurApAccountingLine accountingLine = (PurApAccountingLine) iterator.next();
                String coa = accountingLine.getChartOfAccountsCode();
                 if (PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL.equals(systemTypeCode)) {
                    valid &= validateIndividualCapitalAssetSystemFromPurchasing(capitalAssetSystemStateCode, purchasingDocument.getPurchasingCapitalAssetItems(), coa, documentType);
                }
                else if (PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM.equals(systemTypeCode)) {
                    valid &=  validateOneSystemCapitalAssetSystemFromPurchasing(capitalAssetSystemStateCode, purchasingDocument.getPurchasingCapitalAssetSystems(), purchasingDocument.getPurchasingCapitalAssetItems(), coa, documentType);
                }
                else if (PurapConstants.CapitalAssetSystemTypes.MULTIPLE.equals(systemTypeCode)) {
                    valid &= validateMultipleSystemsCapitalAssetSystemFromPurchasing(capitalAssetSystemStateCode, purchasingDocument.getPurchasingCapitalAssetSystems(), purchasingDocument.getPurchasingCapitalAssetItems(), coa, documentType);
                }
            }
        }
        return valid;
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
        return /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.CAPITAL_ASSET_OBJECT_LEVELS, purapAccountingLine.getObjectCode().getFinancialObjectLevelCode()).evaluationSucceeds();
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

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#doesItemNeedCapitalAsset(java.lang.String, java.util.List)
     */
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

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validateUpdateCAMSView(org.kuali.kfs.sys.document.AccountingDocument)
     */
    public boolean validateUpdateCAMSView(AccountingDocument accountingDocument) {
        PurchasingDocument purchasingDocument = (PurchasingDocument) accountingDocument;
        boolean valid = true;
        for (PurApItem purapItem : purchasingDocument.getItems()) {
            if (purapItem.getItemType().isLineItemIndicator()) {
                if (!doesItemNeedCapitalAsset(purapItem.getItemTypeCode(), purapItem.getSourceAccountingLines())) {
                    PurchasingCapitalAssetItem camsItem = ((PurchasingItem) purapItem).getPurchasingCapitalAssetItem();
                    if (camsItem != null && !camsItem.isEmpty()) {
                        valid = false;
                        GlobalVariables.getMessageMap().putError("newPurchasingItemCapitalAssetLine", PurapKeyConstants.ERROR_CAPITAL_ASSET_ITEM_NOT_CAMS_ELIGIBLE, "in line item # " + purapItem.getItemLineNumber());
                    }
                }
            }
        }
        return valid;
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validateAddItemCapitalAssetBusinessRules(org.kuali.kfs.integration.purap.ItemCapitalAsset)
     */
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
            GlobalVariables.getMessageMap().putError(propertyName, errorKey);
        }else{        
            Map<String, String> params = new HashMap<String, String>();
            params.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
            Asset retrievedAsset = (Asset) this.getBusinessObjectService().findByPrimaryKey(Asset.class, params);

            if (ObjectUtils.isNull(retrievedAsset)) {
                valid = false;
                String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_NUMBER);
                GlobalVariables.getMessageMap().putError("newPurchasingItemCapitalAssetLine." + KFSPropertyConstants.CAPITAL_ASSET_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
            }

        }
        return valid;
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#warningObjectLevelCapital(org.kuali.kfs.sys.document.AccountingDocument)
     */
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
        ParameterRepositoryService paramRepositoryService = SpringContext.getBean(ParameterRepositoryService.class);
        Builder qbc = QueryByCriteria.Builder.create();
        qbc.setPredicates(and(
                equal(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE),
                equal(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_DOCUMENT),
                PredicateFactory.like(CabPropertyConstants.Parameter.PARAMETER_NAME, "CHARTS_REQUIRING%" + documentType)));
  
        List<Parameter> results = paramRepositoryService.findParameters(qbc.build()).getResults();
        for (Parameter parameter : results) {
            if (ObjectUtils.isNotNull(parameter)) {
                if (systemType.equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                    valid &= validateFieldRequirementByChartForIndividualSystemType(systemState, capitalAssetItems, chartCode, parameter.getName(), parameter.getValue());
                }
                else {
                    valid &= validateFieldRequirementByChartForOneOrMultipleSystemType(systemType, systemState, capitalAssetSystems, capitalAssetItems, chartCode, parameter.getName(), parameter.getValue());
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
        
        //moved the springcontext out of for loop....
        ParameterRepositoryService paramRepositoryService = SpringContext.getBean(ParameterRepositoryService.class);

        for (PurApItem item : purchasingDocument.getItems()) {
            String itemTypeCode = item.getItemTypeCode();
            List accountingLines = item.getSourceAccountingLines();
            for (Iterator iterator = accountingLines.iterator(); iterator.hasNext();) {
                PurApAccountingLine accountingLine = (PurApAccountingLine) iterator.next();
                String coa = accountingLine.getChartOfAccountsCode();
                List<Parameter> results = new ArrayList<Parameter>();
                 //rice20  not sure if this will work trying to replace getBean(ParameterService.class).retrieveParametersGivenLookupCriteria(criteria));
                Builder qbc = QueryByCriteria.Builder.create();
                qbc.setPredicates(and(
                            equal(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE),
                            equal(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_DOCUMENT),
                            PredicateFactory.like(CabPropertyConstants.Parameter.PARAMETER_NAME, "CHARTS_REQUIRING%" + documentType),
                            PredicateFactory.like(CabPropertyConstants.Parameter.PARAMETER_VALUE, "%" + coa + "%")));
                
                results = (List<Parameter>) (paramRepositoryService.findParameters(qbc.build())).getResults();

                for (Parameter parameter : results) {
                    if (ObjectUtils.isNotNull(parameter)) {
                        if (parameter.getValue() != null) {
                             return false;
                        }
                    }
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
        boolean needValidation = (/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, parameterName, chartCode).evaluationSucceeds());

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
        boolean needValidation = (/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, parameterName, chartCode).evaluationSucceeds());

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
            GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, fieldName);
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
                    offendingClass = (Class) bean.getClass().getMethod(methodToInvoke, (Class[])null).invoke(bean, (Object[])null);
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                org.kuali.rice.krad.datadictionary.BusinessObjectEntry boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(offendingClass.getSimpleName());
                List<AttributeDefinition> offendingAttributes = boe.getAttributes();
                AttributeDefinition offendingAttribute = offendingAttributes.get(0);
                String fieldName = SpringContext.getBean(DataDictionaryService.class).getAttributeShortLabel(offendingClass, offendingAttribute.getName());
                GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, fieldName);
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
            GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, (String) mappedNames[0]);
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
                    GlobalVariables.getMessageMap().putError(propertyName, errorKey, Integer.toString(count + 1));
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
        boolean allowedAssetNumbers = (/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.PURCHASING_ASSET_TRANSACTION_TYPES_ALLOWING_ASSET_NUMBERS, capitalAssetTransactionType).evaluationSucceeds());
        if (allowedAssetNumbers) {
            // If this is a transaction type that allows asset numbers, we don't need to validate anymore, just return true here.
            return true;
        }
        else {
            for (ItemCapitalAsset asset : capitalAssetSystem.getItemCapitalAssets()) {
                if (asset.getCapitalAssetNumber() != null) {
                    String propertyName = prefix + PurapPropertyConstants.CAPITAL_ASSET_TRANSACTION_TYPE_CODE;
                    GlobalVariables.getMessageMap().putError(propertyName, PurapKeyConstants.ERROR_CAPITAL_ASSET_ASSET_NUMBERS_NOT_ALLOWED_TRANS_TYPE, capitalAssetTransactionType);
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
                        GlobalVariables.getMessageMap().putError(propertyName, PurapKeyConstants.ERROR_CAPITAL_ASSET_TRANS_TYPE_NOT_ALLOWING_NON_QUANTITY_ITEMS, capitalAssetItem.getCapitalAssetTransactionTypeCode());
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
        List<String> previousErrorPath = GlobalVariables.getMessageMap().getErrorPath();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.CAPITAL_ASSET_TAB_ERRORS);
        boolean result = validatePurchasingItemCapitalAsset(recurringPaymentTypeCode, purchasingItem);
        GlobalVariables.getMessageMap().clearErrorPath();
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
    
    /** TODO retest this after merge
     * This method validates purapItem giving a transaction type.
     * 
     * @param recurringPaymentType
     * @param item
     * @param warn
     * @return
     *
    protected boolean validatePurapItemCapitalAsset(PurApItem item, AssetTransactionType capitalAssetTransactionType) {
        boolean valid = true;
        String itemIdentifier = item.getItemIdentifierString();
        boolean quantityBased = item.getItemType().isQuantityBasedGeneralLedgerIndicator();
        BigDecimal itemUnitPrice = item.getItemUnitPrice();
        HashSet<String> capitalOrExpenseSet = new HashSet<String>(); // For the first validation on every accounting line.
        
        boolean performObjectCodeVersusTransactionTypeValidation = true;
        if (item instanceof AccountsPayableItem) {
            performObjectCodeVersusTransactionTypeValidation = false;
        }

        // Do the checks that depend on Accounting Line information.
        for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
            // Because of ObjectCodeCurrent, we had to refresh this.
            accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            ObjectCode objectCode = accountingLine.getObjectCode();

            if (ObjectUtils.isNotNull(objectCode)) {
                String capitalOrExpense = objectCodeCapitalOrExpense(objectCode);
                capitalOrExpenseSet.add(capitalOrExpense); // HashSets accumulate distinct values (and nulls) only.
                valid &= validateAccountingLinesNotCapitalAndExpense(capitalOrExpenseSet, itemIdentifier, objectCode);

                if (performObjectCodeVersusTransactionTypeValidation) {
                    // Do the checks involving capital asset transaction type.
                    if (capitalAssetTransactionType != null) {
                        valid &= validateObjectCodeVersusTransactionType(objectCode, capitalAssetTransactionType, itemIdentifier, quantityBased);
                    }
                }
            }
        }
        return valid;    
    }    
    */
    
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
            GlobalVariables.getMessageMap().putError(KFSConstants.FINANCIAL_OBJECT_LEVEL_CODE_PROPERTY_NAME, CabKeyConstants.ERROR_ITEM_CAPITAL_AND_EXPENSE, itemIdentifier, objectCode.getFinancialObjectCodeName());
            valid &= false;
        }
        return valid;
    }

    protected boolean validateLevelCapitalAssetIndication(BigDecimal unitPrice, ObjectCode objectCode, String itemIdentifier) {

        String capitalAssetPriceThresholdParam = this.getParameterService().getParameterValueAsString(AssetGlobal.class, CabParameterConstants.CapitalAsset.CAPITALIZATION_LIMIT_AMOUNT);
        BigDecimal priceThreshold = null;
        try {
            priceThreshold = new BigDecimal(capitalAssetPriceThresholdParam);
        }
        catch (NumberFormatException nfe) {
            throw new RuntimeException("the parameter for CAPITAL_ASSET_OBJECT_LEVELS came was not able to be converted to a number.", nfe);
        }
        if (unitPrice.compareTo(priceThreshold) >= 0) {
            List<String> possibleCAMSObjectLevels = new ArrayList<String>( this.getParameterService().getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS) );
            if (possibleCAMSObjectLevels.contains(objectCode.getFinancialObjectLevelCode())) {
                String warning = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CabKeyConstants.WARNING_ABOVE_THRESHOLD_SUGESTS_CAPITAL_ASSET_LEVEL);
                warning = StringUtils.replace(warning, "{0}", itemIdentifier);
                warning = StringUtils.replace(warning, "{1}", priceThreshold.toString());
                KNSGlobalVariables.getMessageList().add(warning);

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
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_TRAN_TYPE_OBJECT_CODE_SUBTYPE, itemIdentifier, capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(), objectCode.getFinancialObjectCodeName(), objectCode.getFinancialObjectSubType().getFinancialObjectSubTypeName());

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
            String recurringTransactionTypeCodes = this.getParameterService().getParameterValueAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.RECURRING_CAMS_TRAN_TYPES);


            if (StringUtils.isNotEmpty(recurringPaymentTypeCode)) { // If there is a recurring payment type ...
                if (!StringUtils.contains(recurringTransactionTypeCodes, capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                    // There should be a recurring tran type code.
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE, itemIdentifier, capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(), CabConstants.ValidationStrings.RECURRING);
                    valid &= false;
                }
            }
            else { // If the payment type is not recurring ...
                // There should not be a recurring transaction type code.
                if (StringUtils.contains(recurringTransactionTypeCodes, capitalAssetTransactionType.getCapitalAssetTransactionTypeCode())) {
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_WRONG_TRAN_TYPE, itemIdentifier, capitalAssetTransactionType.getCapitalAssetTransactionTypeDescription(), CabConstants.ValidationStrings.NON_RECURRING);

                    valid &= false;
                }
            }
        }
        else { // If there is no transaction type ...
            if (StringUtils.isNotEmpty(recurringPaymentTypeCode)) { // If there is a recurring payment type ...
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_NO_TRAN_TYPE, itemIdentifier, CabConstants.ValidationStrings.RECURRING);
                valid &= false;
            }
            else { // If the payment type is not recurring ...
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE, CabKeyConstants.ERROR_ITEM_NO_TRAN_TYPE, itemIdentifier, CabConstants.ValidationStrings.NON_RECURRING);
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
        String capitalAssetObjectSubType = this.getParameterService().getParameterValueAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, PurapParameterConstants.CapitalAsset.PURCHASING_OBJECT_SUB_TYPES);
        return (StringUtils.containsIgnoreCase(capitalAssetObjectSubType, oc.getFinancialObjectSubTypeCode()) ? true : false);
    }

    /**
     * Not documented
     * @param capitalAssetSystems
     * @return
     */
    protected boolean validateCapitalAssetLocationAddressFieldsOneOrMultipleSystemType(List<CapitalAssetSystem> capitalAssetSystems) {
        boolean valid = true;
        boolean isMoving = false;
        int systemCount = 0;
        for (CapitalAssetSystem system : capitalAssetSystems) {
            List<CapitalAssetLocation> capitalAssetLocations = system.getCapitalAssetLocations();
            StringBuffer errorKey = new StringBuffer("document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_SYSTEMS + "[" + new Integer(systemCount++) + "].");
            errorKey.append("capitalAssetLocations");
            int locationCount = 0;
            for (CapitalAssetLocation location : capitalAssetLocations) {
                errorKey.append("[" + locationCount++ + "].");
                AssetType assetType = getAssetType(system.getCapitalAssetTypeCode());                
                isMoving = ObjectUtils.isNull(assetType) ? false : assetType.isMovingIndicator();
                boolean ignoreBuilding = ObjectUtils.isNull(assetType) ? false : assetType.isRequiredBuildingIndicator();
                valid &= validateCapitalAssetLocationAddressFields(location, isMoving, ignoreBuilding, errorKey);
            }
        }
        return valid;
    }

    /**
     * Not Documented
     * @param capitalAssetItems
     * @return
     */
    protected boolean validateCapitalAssetLocationAddressFieldsForIndividualSystemType(List<PurchasingCapitalAssetItem> capitalAssetItems) {
        boolean valid = true;
        boolean isMoving = false;
        for (PurchasingCapitalAssetItem item : capitalAssetItems) {
            CapitalAssetSystem system = item.getPurchasingCapitalAssetSystem();
            List<CapitalAssetLocation> capitalAssetLocations = system.getCapitalAssetLocations();
            StringBuffer errorKey = new StringBuffer("document." + PurapPropertyConstants.PURCHASING_CAPITAL_ASSET_ITEMS + "[" + new Integer(item.getPurchasingItem().getItemLineNumber().intValue() - 1) + "].");
            errorKey.append("purchasingCapitalAssetSystem.capitalAssetLocations");
            int i = 0;
            for (CapitalAssetLocation location : capitalAssetLocations) {
                errorKey.append("[" + i++ + "].");
                AssetType assetType = getAssetType(system.getCapitalAssetTypeCode());                
                isMoving = ObjectUtils.isNull(assetType) ? false : assetType.isMovingIndicator();
                boolean isRequiredBuilding = ObjectUtils.isNull(assetType) ? false : assetType.isRequiredBuildingIndicator();
                valid &= validateCapitalAssetLocationAddressFields(location, isMoving, isRequiredBuilding, errorKey);
            }
        }
        return valid;
    }

    /**
     * Not documented
     * @param location
     * @param ignoreRoom Is used to identify if room number should be validated.  If true then room is ignored in this validation.
     * @param errorKey
     * @return
     */
    protected boolean validateCapitalAssetLocationAddressFields(CapitalAssetLocation location, boolean isMoving, boolean isRequiredBuilding, StringBuffer errorKey) {
        boolean valid = true;
        if (!location.isOffCampusIndicator()) {
            if (StringUtils.isBlank(location.getCampusCode())) {
                GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_CAMPUS);
                valid &= false;
            }
        }
        if (isMoving) {
            if (! location.isOffCampusIndicator()) {
                // bulding and room required
                if (StringUtils.isBlank(location.getBuildingRoomNumber())) {
                    GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM);
                    valid &= false;
                }
                if (StringUtils.isBlank(location.getBuildingCode())) {
                    GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_BUILDING);
                    valid &= false;
                }
            }
            valid &= validateCapitalAssetAddressFields(location,errorKey);
        } else {
            // no room allowed
            if (!StringUtils.isBlank(location.getBuildingRoomNumber())) {
                GlobalVariables.getMessageMap().putError(errorKey.toString(), CamsKeyConstants.AssetLocation.ERROR_ASSET_LOCATION_ROOM_NUMBER_NONMOVEABLE, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM);
                valid &= false;
            }
            if (isRequiredBuilding) {
                // building required
                if (! location.isOffCampusIndicator()) {
                    if (StringUtils.isBlank(location.getBuildingCode())) {
                        GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_BUILDING);
                        valid &= false;
                    }
                }
                valid &= validateCapitalAssetAddressFields(location,errorKey);
            } else {
                // no building allowed
                if (!StringUtils.isBlank(location.getBuildingCode())) {
                    GlobalVariables.getMessageMap().putError(errorKey.toString(), CamsKeyConstants.AssetLocation.ERROR_ASSET_LOCATION_BUILDING_NONMOVEABLE, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_BUILDING);
                    valid &= false;
                }
            }
        }
        return valid;
    }

    protected boolean validateCapitalAssetAddressFields( CapitalAssetLocation location, StringBuffer errorKey) {
        boolean valid = true;
        if (StringUtils.isBlank(location.getCapitalAssetLine1Address())) {
            GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ADDRESS_LINE1);
            valid &= false;
        }
        if (StringUtils.isBlank(location.getCapitalAssetCityName())) {
            GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_CITY);
            valid &= false;
        }
        if (StringUtils.isBlank(location.getCapitalAssetCountryCode())) {
            GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_COUNTRY);
            valid &= false;
        }
        else if (location.getCapitalAssetCountryCode().equals(KFSConstants.COUNTRY_CODE_UNITED_STATES)) {
            if (StringUtils.isBlank(location.getCapitalAssetStateCode())) {
                GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_STATE);
                valid &= false;
            }
            if (StringUtils.isBlank(location.getCapitalAssetPostalCode())) {
                GlobalVariables.getMessageMap().putError(errorKey.toString(), KFSKeyConstants.ERROR_REQUIRED, PurapPropertyConstants.CAPITAL_ASSET_LOCATION_POSTAL_CODE);
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
    public boolean validateFinancialProcessingData(AccountingDocument accountingDocument, CapitalAssetInformation capitalAssetInformation, int index) {
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
                    valid &= validateUpdateCapitalAssetField(capitalAssetInformation, accountingDocument, index);
                }
                else {
                    // Validate New Asset information
                    valid &= checkNewCapitalAssetFieldsExist(capitalAssetInformation, accountingDocument);
                    if (valid) {
                        valid = validateNewCapitalAssetFields(capitalAssetInformation, index, accountingDocument);
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
        List<String> financialProcessingCapitalObjectSubTypes = new ArrayList<String>( this.getParameterService().getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.FINANCIAL_PROCESSING_CAPITAL_OBJECT_SUB_TYPES) );
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
        final AccountCapitalObjectCode accountCapitalObjectCode = getCapitalAssetObjectSubTypeLinesFlag(accountingDocument);
        return accountCapitalObjectCode != null && !accountCapitalObjectCode.equals(AccountCapitalObjectCode.BOTH_NONCAP);
    }


    /**
     * To check if data exists on create new asset
     * 
     * @param capitalAssetInformation
     * @return boolean false if the new asset is not blank
     */
    protected boolean isNewAssetBlank(CapitalAssetInformation capitalAssetInformation) {
        boolean isBlank = true;

        boolean createAsset = (capitalAssetInformation.getCapitalAssetActionIndicator().equalsIgnoreCase(KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR) ? true :  false);

        if (createAsset && (ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetTypeCode()) || ObjectUtils.isNotNull(capitalAssetInformation.getVendorName()) || ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetQuantity()) || ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetManufacturerName()) || ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetManufacturerModelNumber()) || ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetDescription()))) {
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
    protected boolean isUpdateAssetBlank(CapitalAssetInformation capitalAssetInformation) {
        boolean isBlank = true;

        boolean updateAsset = (capitalAssetInformation.getCapitalAssetActionIndicator().equalsIgnoreCase(KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR) ? true :  false);
        
        if (updateAsset && ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetNumber())) {
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
    protected boolean validateUpdateCapitalAssetField(CapitalAssetInformation capitalAssetInformation, AccountingDocument accountingDocument, int index) {
        boolean valid = true;

        Map<String, String> params = new HashMap<String, String>();
        params.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, capitalAssetInformation.getCapitalAssetNumber().toString());
        Asset asset = (Asset) this.getBusinessObjectService().findByPrimaryKey(Asset.class, params);

        List<Long> assetNumbers = new ArrayList<Long>();
        assetNumbers.add(capitalAssetInformation.getCapitalAssetNumber());

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.removeFromErrorPath(KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + index + "].");
        errors.removeFromErrorPath(capitalAssetInformation.getCapitalAssetActionIndicator().equalsIgnoreCase(KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR) ? KFSPropertyConstants.CAPITAL_ASSET_INFORMATION :  KFSPropertyConstants.CAPITAL_ASSET_MODIFY_INFORMATION);
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
        
        String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + index + "]." + KFSPropertyConstants.CAPITAL_ASSET_NUMBER;
        if (ObjectUtils.isNull(asset)) {
            valid = false;
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_NUMBER);
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix, KFSKeyConstants.ERROR_EXISTENCE, label);
        }
        else if (!(this.getAssetService().isCapitalAsset(asset) && !this.getAssetService().isAssetRetired(asset))) {
            // check asset status must be capital asset active.
            valid = false;
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_ACTIVE_CAPITAL_ASSET_REQUIRED);
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
    protected boolean checkNewCapitalAssetFieldsExist(CapitalAssetInformation capitalAssetInformation, AccountingDocument accountingDocument) {
        boolean valid = true;

        if (StringUtils.isBlank(capitalAssetInformation.getCapitalAssetTypeCode())) {
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE);
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        if (capitalAssetInformation.getCapitalAssetQuantity() == null || capitalAssetInformation.getCapitalAssetQuantity() <= 0) {
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_QUANTITY);
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_QUANTITY, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        //VENDOR_IS_REQUIRED_FOR_NON_MOVEABLE_ASSET parameter determines if we need to check
        //vendor name entered.
        String vendorNameRequired = getParameterService().getParameterValueAsString(Asset.class, CabParameterConstants.CapitalAsset.VENDOR_REQUIRED_FOR_NON_MOVEABLE_ASSET_IND);
        
        if ("Y".equalsIgnoreCase(vendorNameRequired)) {
        // skip vendor name required validation for procurement card document
            if (!(accountingDocument instanceof ProcurementCardDocument) && StringUtils.isBlank(capitalAssetInformation.getVendorName())) {
                String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.VENDOR_NAME);
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.VENDOR_NAME, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }
        }
        
        //MANUFACTURER_IS_REQUIRED_FOR_NON_MOVEABLE_ASSET parameter determines if we need to check
        //vendor name entered.
        String manufacturerNameRequired = getParameterService().getParameterValueAsString(Asset.class, CabParameterConstants.CapitalAsset.MANUFACTURER_REQUIRED_FOR_NON_MOVEABLE_ASSET_IND);

        if ("Y".equalsIgnoreCase(manufacturerNameRequired)) {
            if (StringUtils.isBlank(capitalAssetInformation.getCapitalAssetManufacturerName())) {
                String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_MANUFACTURE_NAME);
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_MANUFACTURE_NAME, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }
        }

        if (StringUtils.isBlank(capitalAssetInformation.getCapitalAssetDescription())) {
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, CamsPropertyConstants.Asset.CAPITAL_ASSET_DESCRIPTION);
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.Asset.CAPITAL_ASSET_DESCRIPTION, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        int index = 0;
        List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAssetInformation.getCapitalAssetInformationDetails();
        for (CapitalAssetInformationDetail dtl : capitalAssetInformationDetails) {
            String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION_DETAILS;

            if (StringUtils.isBlank(dtl.getCampusCode())) {
                String label = this.getDataDictionaryService().getAttributeLabel(Campus.class, KFSPropertyConstants.CAMPUS_CODE);
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.CAMPUS_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }

            if (StringUtils.isBlank(dtl.getBuildingCode())) {
                String label = this.getDataDictionaryService().getAttributeLabel(Building.class, KFSPropertyConstants.BUILDING_CODE);
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_CODE, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }

            // Room is not required for non-moveable
            AssetType assetType = getAssetType(capitalAssetInformation.getCapitalAssetTypeCode());
            if (ObjectUtils.isNull(assetType) || assetType.isMovingIndicator()) {
                if (StringUtils.isBlank(dtl.getBuildingRoomNumber())) {
                    String label = this.getDataDictionaryService().getAttributeLabel(Room.class, KFSPropertyConstants.BUILDING_ROOM_NUMBER);
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_ROOM_NUMBER, KFSKeyConstants.ERROR_REQUIRED, label);
                    valid = false;
                }
            }
            
            // Room number not allowed for non-moveable assets
            if (ObjectUtils.isNotNull(assetType) && !assetType.isMovingIndicator()) {
                if (StringUtils.isNotBlank(dtl.getBuildingRoomNumber())) {
                    String label = this.getDataDictionaryService().getAttributeLabel(Room.class, KFSPropertyConstants.BUILDING_ROOM_NUMBER);
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_ASSET_LOCATION_ROOM_NUMBER_NONMOVEABLE, label);
                    valid = false;
                }
            }
            
            

            index++;
        }

        return valid;
    }

    /**
     * To validate new asset information
     * 
     * @param capitalAssetInformation the information of add asset to be validated
     * @param index index of the capital asset line
     * @return boolean false if data is incorrect
     */
    protected boolean validateNewCapitalAssetFields(CapitalAssetInformation capitalAssetInformation, int index, AccountingDocument accountingDocument) {
        boolean valid = true;
        
        if (!isAssetTypeExisting(capitalAssetInformation.getCapitalAssetTypeCode().toString())) {
            valid = false;
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE);
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
        }

        valid &= validateTotalNumberOfAssetTagLines(capitalAssetInformation);

        if (valid) {
            valid &= validateAssetTagLocationLines(capitalAssetInformation, index, accountingDocument);
        }
        
        return valid;
    }

    /**
     * validates only the tag numbers because we need to check all the tags within
     * the document for duplicates.
     * 
     * @see 
     * @param accountingDocument
     * @return true if duplicate tags else return false.
     */
    public boolean validateAssetTags(AccountingDocument accountingDocument) {
        boolean valid = true;
        
        List<TagRecord> allTags = new ArrayList<TagRecord>();
        
        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocument;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();

        BusinessObjectDictionaryService businessDictionaryService = SpringContext.getBean(BusinessObjectDictionaryService.class);
        
        int indexCapital = 0;
        
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator())) {
                List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAsset.getCapitalAssetInformationDetails();
                int index = 0;
                
                for (CapitalAssetInformationDetail dtl : capitalAssetInformationDetails) {
                    dtl.refreshReferenceObject("building");
                    
                    //if building is inactiv then validation fails....
                    if (ObjectUtils.isNotNull(dtl.getBuilding()) && !dtl.getBuilding().isActive()) {
                        String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + indexCapital + "]." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION_DETAILS;
                        GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_CODE, KFSKeyConstants.ERROR_DOCUMENT_CAPITAL_ASSET_DETAIL_INACTIVE_BUILDING_NOT_ALLOWED, dtl.getBuildingCode());
                        valid &=  false;                        
                    }
                    
                    dtl.refreshReferenceObject("room");
                    //if room is inactiv then validation fails....
                    if (ObjectUtils.isNotNull(dtl.getRoom()) && !dtl.getRoom().isActive()) {
                        String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + indexCapital + "]." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION_DETAILS;
                        GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_ROOM_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_CAPITAL_ASSET_DETAIL_INACTIVE_ROOM_NOT_ALLOWED, dtl.getBuildingRoomNumber());
                        valid &=  false;
                    }
                    
                    index++;
                    
                    businessDictionaryService.performForceUppercase(dtl);
                    if (StringUtils.isNotBlank(dtl.getCapitalAssetTagNumber()) && !dtl.getCapitalAssetTagNumber().equalsIgnoreCase(CamsConstants.Asset.NON_TAGGABLE_ASSET)) {
                        allTags.add(new TagRecord(dtl.getCapitalAssetTagNumber(), capitalAsset.getCapitalAssetLineNumber(), dtl.getCapitalAssetLineNumber(), dtl.getItemLineNumber()));
                    }
                }
            }
            indexCapital++;
        }
        
        for (TagRecord tagRecord : allTags) {
            if (isTagDuplicated(allTags, tagRecord)) {
                tagRecord.setDuplicate(true);
                valid &=  false;
            }
        }

        for (TagRecord tagRecord : allTags) {
            if (tagRecord.isDuplicate()) {
                int indexAsset = 0;
                for (CapitalAssetInformation capitalAsset : capitalAssets) {
                    if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator())) {
                        List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAsset.getCapitalAssetInformationDetails();
                        int index = 0;
                        for (CapitalAssetInformationDetail dtl : capitalAssetInformationDetails) {
                            if (dtl.getCapitalAssetTagNumber().equals(tagRecord.getTagNumber())) {
                                String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + indexAsset + "]." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION_DETAILS;
                                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.CAPITAL_ASSET_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, tagRecord.getTagNumber());
                            }
                            index++;                        
                        }
                    }
                    
                    indexAsset++;
                }
            }
        }

        return valid;
    }
    
    /**
     * checks the tag records for a given asset number and sets duplicate flag to
     * true if found.
     * @param allTags
     * @param tagRecord
     * @return true if duplicate else false
     */
    protected boolean isTagDuplicated(List<TagRecord> allTags, TagRecord tagRecord) {
        boolean duplicate = false;
        
        if (!this.getAssetService().findActiveAssetsMatchingTagNumber(tagRecord.getTagNumber()).isEmpty()) {
            // Tag number is already in use
            return true;
        }
        
        int duplicateCount = 0;
        
        for (TagRecord checkTagRecord : allTags) {
            if (checkTagRecord.getTagNumber().equals(tagRecord.getTagNumber())) {
                //found duplicate...
                duplicateCount++;
            }
        }
        
        if (duplicateCount > 1) {
            tagRecord.setDuplicate(true);
            return true;            
        }
        
        return duplicate;
    }
    
    /**
     * public class to hold the the records representing the capital asset
     * detail lines containing tag information
     */
    public final class TagRecord {
        protected String tagNumber;
        protected int assetLineNumber;
        protected int assetDetailLineNumber;
        protected int assetItemLineNumber;
        protected boolean duplicate;
        
        public TagRecord(String tagNumber, int assetLineNumber, int assetDetailLineNumber, int itemLineNumber) {
            this.tagNumber = tagNumber;
            this.assetLineNumber = assetLineNumber;
            this.assetDetailLineNumber = assetDetailLineNumber;
            this.assetItemLineNumber = itemLineNumber;
            this.duplicate = false;
        }
        
        /**
         * Gets the tagNumber attribute.
         * 
         * @return Returns the tagNumber
         */
        
        public String getTagNumber() {
            return tagNumber;
        }

        /**
         * Gets the assetLineNumber attribute.
         * 
         * @return Returns the assetLineNumber
         */
        
        public int getAssetLineNumber() {
            return assetLineNumber;
        }

        /**
         * Gets the assetDetailLineNumber attribute.
         * 
         * @return Returns the assetDetailLineNumber
         */
        
        public int getAssetDetailLineNumber() {
            return assetDetailLineNumber;
        }

        /**	
         * Sets the tagNumber attribute.
         * 
         * @param tagNumber The tagNumber to set.
         */
        public void setTagNumber(String tagNumber) {
            this.tagNumber = tagNumber;
        }

        /**	
         * Sets the assetLineNumeber attribute.
         * 
         * @param assetLineNumber The assetLineNumber to set.
         */
        public void setAssetLineNumber(int assetLineNumber) {
            this.assetLineNumber = assetLineNumber;
        }

        /**	
         * Sets the assetDetailLineNumber attribute.
         * 
         * @param assetDetailLineNumber The assetDetailLineNumber to set.
         */
        public void setAssetDetailLineNumber(int assetDetailLineNumber) {
            this.assetDetailLineNumber = assetDetailLineNumber;
        }
        /**
         * Gets the assetItemLineNumber attribute.
         * 
         * @return Returns the assetItemLineNumber
         */
        
        public int getAssetItemLineNumber() {
            return assetItemLineNumber;
        }

        /** 
         * Sets the assetItemLineNumber attribute.
         * 
         * @param assetItemLineNumber The assetItemLineNumber to set.
         */
        public void setAssetItemLineNumber(int assetItemLineNumber) {
            this.assetItemLineNumber = assetItemLineNumber;
        }
        
        /**
         * Gets the duplicate attribute.
         * 
         * @return Returns the duplicate
         */
        
        public boolean isDuplicate() {
            return duplicate;
        }

        /** 
         * Sets the duplicate attribute.
         * 
         * @param duplicate The duplicate to set.
         */
        public void setDuplicate(boolean duplicate) {
            this.duplicate = duplicate;
        }
    }
    
    /**
     * validates asset tag location lines for existence of any duplicate tag/location details.
     * Collects all the detail lines for all the capital assets and then checks if there 
     * are any duplicates.
     * 
     * @param capitalAssetInformation
     * @param capitalAssets
     * @param capitalAssetIndex index of the capital asset line
     * @return true if duplicate else false
     */
    protected boolean validateAssetTagLocationLines(CapitalAssetInformation capitalAssetInformation, int capitalAssetIndex, AccountingDocument accountingDocument) {
        boolean valid = true;
        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocument;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();
        
        List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAssetInformation.getCapitalAssetInformationDetails();
        int index = 0;
        
        for (CapitalAssetInformationDetail dtl : capitalAssetInformationDetails) {
            // We have to explicitly call this DD service to upper case each field. This may not be the best place and maybe form
            // populate is a better place but we CAMS team don't own FP document. This is the best we can do for now.
            SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(dtl);
            String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + capitalAssetIndex + "]." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION_DETAILS;

            Campus campus = SpringContext.getBean(CampusService.class).getCampus(dtl.getCampusCode());
            if (ObjectUtils.isNull(campus)) {
                valid = false;
                String label = this.getDataDictionaryService().getAttributeLabel(Campus.class, KFSPropertyConstants.CAMPUS_CODE);
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.CAMPUS_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
            }
            
            Map<String, String> params;
            params = new HashMap<String, String>();
            params.put(KFSPropertyConstants.CAMPUS_CODE, dtl.getCampusCode());
            params.put(KFSPropertyConstants.BUILDING_CODE, dtl.getBuildingCode());
            Building building = (Building) this.getBusinessObjectService().findByPrimaryKey(Building.class, params);
            if (ObjectUtils.isNull(building)) {
                valid = false;
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_CODE, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_BUILDING_CODE, dtl.getBuildingCode(), dtl.getCampusCode());
            }

            params = new HashMap<String, String>();
            params.put(KFSPropertyConstants.CAMPUS_CODE, dtl.getCampusCode());
            params.put(KFSPropertyConstants.BUILDING_CODE, dtl.getBuildingCode());
            params.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, dtl.getBuildingRoomNumber());
            Room room = (Room) this.getBusinessObjectService().findByPrimaryKey(Room.class, params);
            AssetType assetType = getAssetType(capitalAssetInformation.getCapitalAssetTypeCode());
            if (ObjectUtils.isNull(room) && (ObjectUtils.isNull(assetType) || assetType.isMovingIndicator())) {
                valid = false;
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix + "[" + index + "]" + "." + KFSPropertyConstants.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_ROOM_NUMBER, dtl.getBuildingRoomNumber(), dtl.getBuildingCode(), dtl.getCampusCode());
            }
            index++;
        }
        
        return valid;
    }
    
    /**
     * Check assetTypeCode existence.
     * 
     * @param assetTypeCode
     * @return
     */
    public boolean isAssetTypeExisting(String assetTypeCode) {
        AssetType assetType = getAssetType(assetTypeCode);
        return ObjectUtils.isNotNull(assetType);
    }

    /**
     * Retrieve the Asset type from the provided assetType Code
     * 
     * @param assetTypeCode
     * @return {@link AssetType}
     */
    protected AssetType getAssetType(String assetTypeCode) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, assetTypeCode);
        AssetType assetType = (AssetType) this.getBusinessObjectService().findByPrimaryKey(AssetType.class, params);
        return assetType;
    }

    /**
     * Validate asset quantity the user entered matching the number of asset tag lines.
     * 
     * @param capitalAssetInformation
     * @return
     */
    protected boolean validateTotalNumberOfAssetTagLines(CapitalAssetInformation capitalAssetInformation) {
        boolean valid = true;
        Integer userInputAssetQuantity = capitalAssetInformation.getCapitalAssetQuantity();
        if (userInputAssetQuantity != null && (ObjectUtils.isNull(capitalAssetInformation.getCapitalAssetInformationDetails()) || capitalAssetInformation.getCapitalAssetInformationDetails().isEmpty())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_QUANTITY, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_TAG_LINE_REQUIRED);
            valid = false;
        }
        else if (userInputAssetQuantity != null && userInputAssetQuantity.intValue() != capitalAssetInformation.getCapitalAssetInformationDetails().size()) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CAPITAL_ASSET_QUANTITY, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_QUANTITY_NOT_MATCHING_TAG_LINES);
            valid = false;
        }

        return valid;
    }

    /**
     * To check if the tag number is duplicate or in use
     * 
     * @param capitalAssetInformation, capitalAssetInformationDetail
     * @return boolean false if data is duplicate or in use
     */
    protected boolean isTagNumberDuplicate(List<CapitalAssetInformationDetail> capitalAssetInformationDetails, CapitalAssetInformationDetail dtl) {
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

        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();
        String documentNumber = documentHeader.getDocumentNumber();

        String documentType = workflowDocument.getDocumentTypeName();

        if (workflowDocument.isCanceled() || workflowDocument.isDisapproved()) {
            // release CAB line items
            activateCabPOLines(documentNumber);
            activateCabGlLines(documentNumber);
        }
        if (workflowDocument.isProcessed()) {
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
                if (generalLedgerEntry.getTransactionLedgerEntryAmount().compareTo(generalLedgerEntry.getTransactionLedgerSubmitAmount()) == 0) {
                    generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS);
                    this.getBusinessObjectService().save(generalLedgerEntry);
                }
            //    if (SpringContext.getBean(GlLineService.class).findUnprocessedCapitalAssetInformation(generalLedgerEntry) == 0) {
            //        generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS);
            //        this.getBusinessObjectService().save(generalLedgerEntry);
            //    }

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
    protected String buildNoteTextForPurApDoc(String documentType, List<Long> assetNumbers) {
        StringBuffer noteText = new StringBuffer();

        if (DocumentTypeName.ASSET_ADD_GLOBAL.equalsIgnoreCase(documentType)) {
            noteText.append("Asset Numbers have been created for this document: ");
        }
        else {
            noteText.append("Existing Asset Numbers have been applied for this document: ");
        }
 
        if (assetNumbers != null || assetNumbers.size() > 0) {
            noteText.append(assetNumbers.get(0).toString());
            if (assetNumbers.size() > 1) {
                noteText.append(",....,");
                noteText.append(assetNumbers.get(assetNumbers.size() - 1).toString());
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
    protected List<Long> getAssetNumbersFromAssetGlobal(String documentNumber) {
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
    protected List<Long> getAssetNumbersFromAssetPayment(String documentNumber) {
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
    protected Integer getPurchaseOrderIdentifier(String camsDocumentNumber) {
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
            Integer capitalAssetLineNumber = 0;
            GeneralLedgerEntry generalLedgerEntry = new GeneralLedgerEntry();
            
            //get the capital asset number from these entry asset records...
            for (GeneralLedgerEntryAsset generalLedgerEntryAsset : matchingGlAssets) {
                capitalAssetLineNumber = generalLedgerEntryAsset.getCapitalAssetBuilderLineNumber();
                generalLedgerEntry = generalLedgerEntryAsset.getGeneralLedgerEntry();

                break;
            }
            
            GlLineService glLineService = SpringContext.getBean(GlLineService.class);
            
            //get the capital asset information....
            CapitalAssetInformation capitalAssetInformation = glLineService.findCapitalAssetInformation(generalLedgerEntry, capitalAssetLineNumber);
            
            List<CapitalAssetAccountsGroupDetails> groupAccountingLines = capitalAssetInformation.getCapitalAssetAccountsGroupDetails();
            
            for (CapitalAssetAccountsGroupDetails accountingLine : groupAccountingLines) {
                String debitOrCreditCode = KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode()) ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE;
                
                Collection<GeneralLedgerEntry> matchingGLEntries = glLineService.findMatchingGeneralLedgerEntry(accountingLine.getDocumentNumber(), accountingLine.getChartOfAccountsCode(), accountingLine.getAccountNumber(), accountingLine.getFinancialObjectCode(), debitOrCreditCode);
                for(GeneralLedgerEntry matchingGLEntry : matchingGLEntries) {
                    reduceTransactionSumbitGlEntryAmount(matchingGLEntry, accountingLine.getAmount());
                }
            }
            //mark the capital asseet processed indicator as false so it can be processed again
            capitalAssetInformation.setCapitalAssetProcessedIndicator(false);
            getBusinessObjectService().save(capitalAssetInformation);
        }
    }
    
    /**
     * reduces the submit amount by the amount on the accounting line.  When submit amount equals
     * transaction ledger amount, the activity status code is marked as in route status.
     * @param matchingGLEntry
     * @param accountLineAmount
     */
    protected void reduceTransactionSumbitGlEntryAmount(GeneralLedgerEntry matchingGLEntry, KualiDecimal accountLineAmount) {
        //reduces submitted amount on the gl entry and save the results.
        KualiDecimal submitTotalAmount = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(matchingGLEntry.getTransactionLedgerSubmitAmount())) {
            submitTotalAmount = matchingGLEntry.getTransactionLedgerSubmitAmount();
        }
        
        matchingGLEntry.setTransactionLedgerSubmitAmount(submitTotalAmount.subtract(accountLineAmount.abs()));

        if (matchingGLEntry.getTransactionLedgerSubmitAmount().isLessThan(matchingGLEntry.getTransactionLedgerEntryAmount())) {
            matchingGLEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
        }
        
        //save the updated gl entry in CAB.  Remove general ledger entry asset records
        this.getBusinessObjectService().save(matchingGLEntry);
        this.getBusinessObjectService().delete(matchingGLEntry.getGeneralLedgerEntryAssets());
    }
    
    
    /**
     * Activates CAB GL Lines
     * 
     * @param documentNumber String
     */
 //   protected void activateCabGlLines(String documentNumber) {
//        Map<String, String> fieldValues = new HashMap<String, String>();
//        fieldValues.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAMS_DOCUMENT_NUMBER, documentNumber);
//        Collection<GeneralLedgerEntryAsset> matchingGlAssets = this.getBusinessObjectService().findMatching(GeneralLedgerEntryAsset.class, fieldValues);
//        if (matchingGlAssets != null && !matchingGlAssets.isEmpty()) {
//            for (GeneralLedgerEntryAsset generalLedgerEntryAsset : matchingGlAssets) {
//                GeneralLedgerEntry generalLedgerEntry = generalLedgerEntryAsset.getGeneralLedgerEntry();
//                generalLedgerEntry.setTransactionLedgerSubmitAmount(KualiDecimal.ZERO);
//                generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
//                this.getBusinessObjectService().save(generalLedgerEntry);
//                this.getBusinessObjectService().delete(generalLedgerEntryAsset);
//            }
//        }
//    }

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
    protected String getDocumentTypeName(AccountingDocument accountingDocument) {
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

    protected PurApInfoService getPurApInfoService() {
        return SpringContext.getBean(PurApInfoService.class);
    }

    /**
     * Get the Capital Asset Object Code from the accounting lines.
     * 
     * @param accountingLine
     * @return true if the accounting line has an object code that belongs to
     * OBJECT_SUB_TYPE_GROUPS system paramters list else return false;
     */
    public boolean hasCapitalAssetObjectSubType(AccountingLine accountingLine) {
        List<String> financialProcessingCapitalObjectSubTypes = new ArrayList<String>( this.getParameterService().getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, CabParameterConstants.CapitalAsset.FINANCIAL_PROCESSING_CAPITAL_OBJECT_SUB_TYPES) );
        
        ObjectCode objectCode = accountingLine.getObjectCode();
        if (ObjectUtils.isNotNull(objectCode)) {
            String objectSubTypeCode = objectCode.getFinancialObjectSubTypeCode();
            if (financialProcessingCapitalObjectSubTypes.contains(objectSubTypeCode)) {
                return true;
            }
        }
        
        return false ;
    }
    
    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validateAllCapitalAccountingLinesProcessed(org.kuali.kfs.sys.document.AccountingDocument)
     */
    public boolean validateAllCapitalAccountingLinesProcessed(AccountingDocument accountingDocumentForValidation) {
        LOG.debug("validateCapitalAccountingLines - start");
        boolean isValid = true;
        
        if (accountingDocumentForValidation instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAccountingLinesDocumentBase capitalAccountingLinesDocumentBase = (CapitalAccountingLinesDocumentBase) accountingDocumentForValidation;
        List <CapitalAccountingLines> capitalAccountingLines = capitalAccountingLinesDocumentBase.getCapitalAccountingLines();
        
        isValid = allCapitalAccountingLinesProcessed(capitalAccountingLines);
        
        return isValid;
    }

    /**
     * checks if all the lines have been selected for processing by checking the selectLine property
     * on each capital accounting line.
     * 
     * @param capitalAccountingLines
     * @return true if all lines have been selected else return false
     */
    protected boolean allCapitalAccountingLinesProcessed(List<CapitalAccountingLines> capitalAccountingLines) {
        LOG.debug("allCapitalAccountingLinesProcessed - start");
        
        boolean processed = true;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (!capitalAccountingLine.isSelectLine()) {
                processed = false;
                GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATION_NOT_PROCESSED);
                
                break;
            }
        }
        
        return processed;
    }
    
    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validateTotalAmountMatch(org.kuali.kfs.sys.document.AccountingDocument)
     */
    public boolean validateTotalAmountMatch(AccountingDocument accountingDocumentForValidation) {
        LOG.debug("validateTotalAmountMatch - start");

        boolean isValid = true;
        
        if (accountingDocumentForValidation instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAccountingLinesDocumentBase capitalAccountingLinesDocumentBase = (CapitalAccountingLinesDocumentBase) accountingDocumentForValidation;
        List <CapitalAccountingLines> capitalAccountingLines = capitalAccountingLinesDocumentBase.getCapitalAccountingLines();
        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocumentForValidation;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();

        isValid = totalAmountMatchForCapitalAccountingLinesAndCapitalAssets(capitalAccountingLines, capitalAssets);
        
        return isValid;
    }
    
    /**
     * compares the total amount from capital accounting lines to the
     * capital assets totals amount.
     * @param capitalAccountingLines
     * @param capitalAssets
     * @return true if two amounts are equal else return false
     */
    protected boolean totalAmountMatchForCapitalAccountingLinesAndCapitalAssets(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> capitalAssets) {
        boolean totalAmountMatched = true;
        
        KualiDecimal capitalAccountingLinesTotals = KualiDecimal.ZERO;
        KualiDecimal capitalAAssetTotals = KualiDecimal.ZERO;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            capitalAccountingLinesTotals = capitalAccountingLinesTotals.add(capitalAccountingLine.getAmount());
        }
    
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            capitalAAssetTotals = capitalAAssetTotals.add(capitalAsset.getCapitalAssetLineAmount());
        }
        
        if (capitalAccountingLinesTotals.isGreaterThan(capitalAAssetTotals)) {
            //not all the accounting lines amounts have been distributed to capital assets
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINES_NOT_ALL_TOTALS_DISTRIBUTED_TO_CAPITAL_ASSETS);
            return false;
        }
        
        if (capitalAccountingLinesTotals.isLessThan(capitalAAssetTotals)) {
            //not all the accounting lines amounts have been distributed to capital assets
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINES_MORE_TOTALS_DISTRIBUTED_TO_CAPITAL_ASSETS);
            return false;
        }
        
        return totalAmountMatched;
    }
    
    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validateCapitlAssetsAmountToAccountingLineAmount(org.kuali.kfs.sys.document.AccountingDocument)
     */
    public boolean validateCapitlAssetsAmountToAccountingLineAmount(AccountingDocument accountingDocument) {
        boolean valid = true;
       
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) accountingDocument;
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        

        List<CapitalAssetInformation> capitalAssetInformation = caldb.getCapitalAssetInformation();
        
        for(CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.getAmount().isLessThan(getCapitalAccountingLineAmountAllocated(capitalAssetInformation, capitalAccountingLine))) {
                GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_CAPITAL_ASSETS_AMOUNTS_GREATER_THAN_CAPITAL_ACCOUNTING_LINE, capitalAccountingLine.getSequenceNumber().toString(), capitalAccountingLine.getLineType(), capitalAccountingLine.getChartOfAccountsCode(), capitalAccountingLine.getAccountNumber(), capitalAccountingLine.getFinancialObjectCode());
                valid = false;
            }
        }
        
        return valid;
    }
    
    /**
     * sums the capital assets amount distributed so far for a given capital accounting line
     * 
     * @param currentCapitalAssetInformation
     * @param existingCapitalAsset
     * @return capitalAssetsAmount amount that has been distributed for the specific capital accounting line
     */
    protected KualiDecimal getCapitalAccountingLineAmountAllocated(List<CapitalAssetInformation> currentCapitalAssetInformation, CapitalAccountingLines capitalAccountingLine) {
        //check the capital assets records totals
        KualiDecimal capitalAccountingLineAmount = KualiDecimal.ZERO;

        for (CapitalAssetInformation capitalAsset : currentCapitalAssetInformation) {
            List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
            for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                        groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&                        
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                        groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                    capitalAccountingLineAmount = capitalAccountingLineAmount.add(groupAccountLine.getAmount());                    
                }
            }
        }
        
        return capitalAccountingLineAmount;
    }
    
    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#validateCapitalAccountingLines(org.kuali.kfs.sys.document.AccountingDocument)
     */
    public boolean validateCapitalAccountingLines(AccountingDocument accountingDocumentForValidation) {
        LOG.debug("validateCapitalAccountingLines - start");

        boolean isValid = true;
        
        if (accountingDocumentForValidation instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAccountingLinesDocumentBase capitalAccountingLinesDocumentBase = (CapitalAccountingLinesDocumentBase) accountingDocumentForValidation;
        List <CapitalAccountingLines> capitalAccountingLines = capitalAccountingLinesDocumentBase.getCapitalAccountingLines();
        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocumentForValidation;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();
      
        isValid = capitalAssetExistsForCapitalAccountingLinesProcessed(capitalAccountingLines, capitalAssets);
        
        return isValid;
    }
    
    /**
     * 
     * @param capitalAccountingLines
     * @param capitalAssets
     * @return true capital assets exists for all the capital accounting lines.
     */
    protected boolean capitalAssetExistsForCapitalAccountingLinesProcessed(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> capitalAssets) {
        LOG.debug("capitalAssetExistsForCapitalAccountingLinesProcessed - start");
        
        boolean exists = true;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (!capitalAssetExist(capitalAccountingLine, capitalAssets)) {
                GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATION_HAS_NO_CAPITAL_ASSET, capitalAccountingLine.getSequenceNumber().toString(), capitalAccountingLine.getLineType(), capitalAccountingLine.getChartOfAccountsCode(), capitalAccountingLine.getAccountNumber(), capitalAccountingLine.getFinancialObjectCode());
                return false;
            }
        }
        
        return exists;
    }
    
    /**
     *  checks if capital asset exists for the given capital accounting line.
     * @param capitalAccountingLine
     * @param capitalAssetInformation
     * @return true if capital accounting line has a capital asset information else return false
     */
    protected boolean capitalAssetExist(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> capitalAssetInformation) {
        boolean exists = false;
        
        if (ObjectUtils.isNull(capitalAssetInformation) && capitalAssetInformation.size() <= 0) {
            return exists;
        }
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
            for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                        groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                        groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                     return true;
                }
            }
        }

        return exists;
    }
    
    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#markProcessedGLEntryLine(java.lang.String)
     */
    public boolean markProcessedGLEntryLine(String documentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAMS_DOCUMENT_NUMBER, documentNumber);
        Collection<GeneralLedgerEntryAsset> matchingGlAssets = this.getBusinessObjectService().findMatching(GeneralLedgerEntryAsset.class, fieldValues);
        if (matchingGlAssets != null && !matchingGlAssets.isEmpty()) {
            GlLineService glLineService = SpringContext.getBean(GlLineService.class);
            
            for (GeneralLedgerEntryAsset generalLedgerEntryAsset : matchingGlAssets) {
                GeneralLedgerEntry generalLedgerEntry = generalLedgerEntryAsset.getGeneralLedgerEntry();

                // update gl status as processed
                if (glLineService.findUnprocessedCapitalAssetInformation(generalLedgerEntry) == 0) {
                    generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
                } 
                else {
                    generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
                }
                
                this.getBusinessObjectService().save(generalLedgerEntry);
            }
        }
        
        return true;
    }
}
