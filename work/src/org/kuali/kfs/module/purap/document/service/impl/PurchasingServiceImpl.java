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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.service.impl.PersistenceServiceStructureImplBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurchasingServiceImpl extends PersistenceServiceStructureImplBase implements PurchasingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingServiceImpl.class);

    private ParameterService parameterService;
    private SequenceAccessorService sequenceAccessorService;
    private PurapAccountingService purapAccountingService;
    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    public void setCapitalAssetBuilderModuleService(CapitalAssetBuilderModuleService capitalAssetBuilderModuleService) {
        this.capitalAssetBuilderModuleService = capitalAssetBuilderModuleService;
    }

    public void setupCapitalAssetItems(PurchasingDocument purDoc) {

        List<PurchasingCapitalAssetItem> camsItemsList = purDoc.getPurchasingCapitalAssetItems();
        List<PurchasingCapitalAssetItem> newCamsItemsList = new TypedArrayList(purDoc.getPurchasingCapitalAssetItemClass());

        for (PurApItem purapItem : purDoc.getItems()) {
            if (purapItem.getItemType().isLineItemIndicator() || PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE.equals(purapItem.getItemTypeCode())) {
                if (capitalAssetBuilderModuleService.doesItemNeedCapitalAsset(purapItem.getItemTypeCode(), purapItem.getSourceAccountingLines())) {
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    // If either the camsItem is null or if its system is null and the document's system type is IND (this is
                    // the case when the user tries to switch from ONE system type to IND system type), we'll have to create
                    // the camsItem again along with its system to prevent the No collection found error.
                    if (ObjectUtils.isNull(camsItem) || (purDoc.getCapitalAssetSystemTypeCode() != null && purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL) && ObjectUtils.isNull(camsItem.getPurchasingCapitalAssetSystem()))) {
                        PurchasingCapitalAssetItem newCamsItem = createCamsItem(purDoc, purapItem);
                        newCamsItemsList.add(newCamsItem);
                    }
                    else {
                        // We need this next line so that the line item info is refreshed
                        // within the Capital Asset tab each time the document content has
                        // changed during editing to prevent KULPURAP-2926.
                        camsItem.setPurchasingDocument(purDoc);
                        newCamsItemsList.add(camsItem);
                    }
                }
                else {
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    if (camsItem != null && camsItem.isEmpty()) {
                        camsItemsList.remove(camsItem);
                    }
                }
            }
        }

        purDoc.setPurchasingCapitalAssetItems(newCamsItemsList);
        
        if (purDoc.getPurchasingCapitalAssetItems().isEmpty()) {
            purDoc.setCapitalAssetSystemStateCode(null);
            purDoc.setCapitalAssetSystemTypeCode(null);
        }
    }

    private PurchasingCapitalAssetItem createCamsItem(PurchasingDocument purDoc, PurApItem purapItem) {
        PurchasingDocumentSpecificService purchasingDocumentSpecificService = purDoc.getDocumentSpecificService();
        if (purapItem.getItemIdentifier() == null) {
            ClassDescriptor cd = this.getClassDescriptor(purapItem.getClass());
            String sequenceName = cd.getFieldDescriptorByName(PurapPropertyConstants.ITEM_IDENTIFIER).getSequenceName();
            Integer itemIdentifier = new Integer(sequenceAccessorService.getNextAvailableSequenceNumber(sequenceName).toString());
            purapItem.setItemIdentifier(itemIdentifier);
        }
        PurchasingCapitalAssetItem camsItem = purchasingDocumentSpecificService.createCamsItem(purDoc, purapItem);
        return camsItem;
    }


    private PurchasingCapitalAssetItem getItemIfAlreadyInCamsItemsList(PurApItem item, List<PurchasingCapitalAssetItem> camsItemsList) {
        for (PurchasingCapitalAssetItem camsItem : camsItemsList) {
            if (camsItem.getItemIdentifier() != null && camsItem.getItemIdentifier().equals(item.getItemIdentifier())) {
                return camsItem;
            }
        }

        return null;
    }


    public void deleteCapitalAssetItems(PurchasingDocument purDoc, Integer itemIdentifier) {
        // delete the corresponding CAMS items.
        int index = 0;
        for (PurchasingCapitalAssetItem camsItem : purDoc.getPurchasingCapitalAssetItems()) {
            if (camsItem.getItemIdentifier().equals(itemIdentifier)) {
                break;
            }
            index++;
        }
        purDoc.getPurchasingCapitalAssetItems().remove(index);
    }

    public void setupCapitalAssetSystem(PurchasingDocument purDoc) {
        CapitalAssetSystem resultSystem = purDoc.getDocumentSpecificService().createCapitalAssetSystem();
        if (purDoc.getCapitalAssetSystemTypeCode() != null && (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM) || purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS))) {
            if (purDoc.getPurchasingCapitalAssetSystems().size() == 0) {
                purDoc.getPurchasingCapitalAssetSystems().add(resultSystem);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchasingService#prorateForTradeInAndFullOrderDiscount(org.kuali.kfs.module.purap.document.PurchasingDocument)
     */
    public void prorateForTradeInAndFullOrderDiscount(PurchasingDocument purDoc) {
        PurApItem fullOrderDiscount = null;
        PurApItem tradeIn = null;
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        List<PurApAccountingLine> distributedAccounts = null;
        List<SourceAccountingLine> summaryAccounts = null;

        // iterate through below the line and grab FoD and TrdIn.
        for (PurApItem item : purDoc.getItems()) {
            if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) {
                fullOrderDiscount = item;
            }
            else if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)) {
                tradeIn = item;
            }
        }
        // If Discount is not null or zero get proration list for all non misc items and set (if not empty?)
        if (fullOrderDiscount != null && fullOrderDiscount.getExtendedPrice().isNonZero()) {
            // TODO: Chris check if we should update maybe use doc specific service since req is always update and po is only if
            // empty
            fullOrderDiscount.getSourceAccountingLines().clear();
            // FIXME: is this really correct? including below the line?
            totalAmount = purDoc.getTotalDollarAmountAllItems(new String[] { PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE });

            summaryAccounts = purapAccountingService.generateSummary(purDoc.getItems());

            distributedAccounts = purapAccountingService.generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, fullOrderDiscount.getAccountingLineClass());
            fullOrderDiscount.setSourceAccountingLines(distributedAccounts);
        }
        // TODO: Should we also check at least one trade in selected?
        // If Discount is not null or zero get proration list for all non misc items and set (if not empty?)
        if (tradeIn != null && tradeIn.getExtendedPrice().isNonZero()) {
            // TODO: Chris check if we should update maybe use doc specific service since req is always update and po is only if
            // empty
            tradeIn.getSourceAccountingLines().clear();

            totalAmount = purDoc.getTotalDollarAmountForTradeIn();

            //Before we generate account summary, we should update the account amounts first.
            purapAccountingService.updateAccountAmounts(purDoc);
            summaryAccounts = purapAccountingService.generateSummary(purDoc.getTradeInItems());
            if (summaryAccounts.size() == 0) {
                if (purDoc.shouldGiveErrorForEmptyAccountsProration()) {
                    GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_SUMMARY_ACCOUNTS_LIST_EMPTY);    
                }
            }
            else {
                distributedAccounts = purapAccountingService.generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, tradeIn.getAccountingLineClass());
                for (PurApAccountingLine distributedAccount : distributedAccounts) {
                    BigDecimal percent = distributedAccount.getAccountLinePercent();
                    BigDecimal roundedPercent = new BigDecimal(Math.round(percent.doubleValue()));
                    distributedAccount.setAccountLinePercent(roundedPercent);
                }
                tradeIn.setSourceAccountingLines(distributedAccounts);
            }
        }
    }

    public boolean getDefaultUseTaxIndicatorValue(PurchasingDocument purDoc) {

        VendorDetail vendor = purDoc.getVendorDetail();
        if (vendor != null) {
            String vendorStateCode = vendor.getDefaultAddressStateCode();
            String billingStateCode = purDoc.getBillingStateCode();
            if (StringUtils.equals(vendorStateCode, billingStateCode) || (vendor.isTaxableIndicator())) {
                return false;
            }
        }
        else {
            // don't set use tax if no vendor on req
            return true;
        }

        return true;
    }

    public String getDefaultAssetTypeCodeNotThisFiscalYear() {
        //FIXME (hjs) is this breaking modularization??
        return parameterService.getParameterValue(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, PurapParameterConstants.CapitalAsset.PURCHASING_DEFAULT_ASSET_TYPE_WHEN_NOT_THIS_FISCAL_YEAR);
    }

    public void clearAllTaxes(PurchasingDocument purDoc) {
        if (!purDoc.isUseTaxIndicator() && purDoc.getItems() != null) {
            for (int i = 0; i < purDoc.getItems().size(); i++) {
                PurApItem item = purDoc.getItems().get(i);
                if (item.getItemType().isLineItemIndicator()) {
                    item.setItemTaxAmount(null);
                }
            }
        }
    }

    public boolean checkCapitalAssetLocation(CapitalAssetLocation location) {
        // if any of the date fields have a value AND one of them does not have a value...
        if (ObjectUtils.isNotNull(location) &&
                (StringUtils.isEmpty(location.getCapitalAssetLine1Address()) ||
                StringUtils.isEmpty(location.getCapitalAssetCityName()) ||
                StringUtils.isEmpty(location.getCapitalAssetStateCode()) ||
                StringUtils.isEmpty(location.getCapitalAssetPostalCode()) ||
                StringUtils.isEmpty(location.getCapitalAssetCountryCode()) )) { 
            String missingFields = "";
            if (StringUtils.isEmpty(location.getCapitalAssetLine1Address())) {
                missingFields += "Address";
            }
            if (StringUtils.isEmpty(location.getCapitalAssetCityName())) {
                if (!StringUtils.isEmpty(missingFields)) {
                    missingFields += ", ";
                }
                missingFields += "City";
            }
            if (StringUtils.isEmpty(location.getCapitalAssetStateCode())) {
                if (!StringUtils.isEmpty(missingFields)) {
                    missingFields += ", ";
                }
                missingFields += "State";
            }
            if (StringUtils.isEmpty(location.getCapitalAssetPostalCode())) {
                if (!StringUtils.isEmpty(missingFields)) {
                    missingFields += ", ";
                }
                missingFields += "Postal Code";
            }
            if (StringUtils.isEmpty(location.getCapitalAssetCountryCode())) {
                if (!StringUtils.isEmpty(missingFields)) {
                    missingFields += ", ";
                }
                missingFields += "Country";
            }
            // add error!
            GlobalVariables.getErrorMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, PurapKeyConstants.ERROR_CAPITAL_ASSET_INCOMPLETE_ADDRESS, missingFields);
            return false;
        }
        return true;
    }
}
