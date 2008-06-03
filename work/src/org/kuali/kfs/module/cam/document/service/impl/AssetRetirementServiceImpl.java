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
package org.kuali.module.cams.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.module.cams.service.AssetObjectCodeService;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetRetirementService;
import org.kuali.module.cams.util.ObjectValueUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetRetirementServiceImpl implements AssetRetirementService {

    private enum AmountCategory {
        CAPITALIZATION {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setCapitalization(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_PLANT_FUND_FOR_FMS);
                postable.setAmount(assetPayment.getAccountChargeAmount());
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            }
        },
        ACCUMMULATE_DEPRECIATION {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setAccumulatedDepreciation(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_ACCUMULATED_DEPRECIATION);
                postable.setAmount(assetPayment.getAccumulatedPrimaryDepreciationAmount());
                postable.setFinancialObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObject());
            }
        },
        OFFSET_AMOUNT {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_GAIN_LOSS_DISPOSITION);
                postable.setAmount(assetPayment.getAccountChargeAmount().subtract(assetPayment.getAccumulatedPrimaryDepreciationAmount()));
                postable.setFinancialObjectCode(DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
                postable.setObjectCode(getOffsetFinancialObject(assetPayment.getAsset()));
            }
        };

        abstract void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode);
    }

    // TODO: replaced by system parameters
    public static final String MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES = "CM;CF;C1;C2;UC;UF;BR;BY";
    public static final String NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES = "AM;BD;BF;BI;CP;ES;IF;LA;LE;LI;LF;LR";
    public static final String DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE = "4998";

    UniversityDateService universityDateService;
    AssetObjectCodeService assetObjectCodeService;
    BusinessObjectService businessObjectService;
    AssetPaymentService assetPaymentService;

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public AssetObjectCodeService getAssetObjectCodeService() {
        return assetObjectCodeService;
    }

    public void setAssetObjectCodeService(AssetObjectCodeService assetObjectCodeService) {
        this.assetObjectCodeService = assetObjectCodeService;
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }

    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#isAssetRetiredBySoldOrAuction(org.kuali.module.cams.bo.AssetRetirementGlobal)
     */
    public boolean isAssetRetiredBySoldOrAuction(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.AUCTION.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.SOLD.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#isAssetRetiredByExternalTransferOrGift(org.kuali.module.cams.bo.AssetRetirementGlobal)
     */
    public boolean isAssetRetiredByExternalTransferOrGift(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.GIFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#isAssetRetiredByMerged(org.kuali.module.cams.bo.AssetRetirementGlobal)
     */
    public boolean isAssetRetiredByMerged(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.MERGED.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#isAssetRetiredByTheft(org.kuali.module.cams.bo.AssetRetirementGlobal)
     */
    public boolean isAssetRetiredByTheft(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.THEFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#getAssetRetirementReasonName(org.kuali.module.cams.bo.AssetRetirementGlobal)
     */
    public String getAssetRetirementReasonName(AssetRetirementGlobal assetRetirementGlobal) {
        return assetRetirementGlobal.getRetirementReason() == null ? new String() : assetRetirementGlobal.getRetirementReason().getRetirementReasonName();
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#generateOffsetPaymentsForEachSource(org.kuali.module.cams.bo.Asset,
     *      java.util.List, java.lang.String)
     */
    public void generateOffsetPaymentsForEachSource(Asset sourceAsset, List<PersistableBusinessObject> persistables, String currentDocumentNumber) {
        List<AssetPayment> offsetPayments = new TypedArrayList(AssetPayment.class);
        Integer maxSequenceNo = assetPaymentService.getMaxSequenceNumber(sourceAsset.getCapitalAssetNumber());

        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                AssetPayment offsetPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(sourcePayment, offsetPayment);
                offsetPayment.setFinancialDocumentTypeCode(AssetRetirementGlobal.ASSET_RETIREMENT_DOCTYPE_CD);
                offsetPayment.setDocumentNumber(currentDocumentNumber);
                offsetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                assetPaymentService.adjustPaymentAmounts(offsetPayment, true, false);
                offsetPayments.add(offsetPayment);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error occured while creating offset payment in retirement", e);
        }
        persistables.addAll(offsetPayments);
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#generateNewPaymentForTarget(org.kuali.module.cams.bo.Asset,
     *      org.kuali.module.cams.bo.Asset, java.util.List, java.lang.Integer, java.lang.String)
     */
    public Integer generateNewPaymentForTarget(Asset targetAsset, Asset sourceAsset, List<PersistableBusinessObject> persistables, Integer maxSequenceNo, String currentDocumentNumber) {
        List<AssetPayment> newPayments = new TypedArrayList(AssetPayment.class);
        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                AssetPayment newPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(sourcePayment, newPayment);
                newPayment.setCapitalAssetNumber(targetAsset.getCapitalAssetNumber());
                newPayment.setFinancialDocumentTypeCode(AssetRetirementGlobal.ASSET_RETIREMENT_DOCTYPE_CD);
                newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                newPayment.setDocumentNumber(currentDocumentNumber);
                assetPaymentService.adjustPaymentAmounts(newPayment, false, true);
                newPayments.add(newPayment);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error occured while creating new payment in retirement", e);
        }
        persistables.addAll(newPayments);
        return maxSequenceNo;
    }


    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#isRetirementReasonCodeInGroup(java.lang.String, java.lang.String)
     */
    public boolean isRetirementReasonCodeInGroup(String reasonCodeGroup, String reasonCode) {
        if (StringUtils.isBlank(reasonCodeGroup) || StringUtils.isBlank(reasonCode)) {
            return false;
        }
        return Arrays.asList(reasonCodeGroup.split(";")).contains(reasonCode);
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#checkRetireMultipleAssets(java.lang.String, java.util.List,
     *      java.lang.Integer)
     */
    public boolean isAllowedRetireMultipleAssets(String retirementReasonCode) {
        // TODO: Replaced by system parameters
        return !isRetirementReasonCodeInGroup(CamsConstants.AssetRetirementReasonCodeGroup.SINGLE_RETIRED_ASSET, retirementReasonCode);
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#createGLPostables(org.kuali.module.cams.bo.AssetRetirementGlobal,
     *      org.kuali.module.cams.gl.CamsGlPosterBase)
     */
    public void createGLPostables(AssetRetirementGlobal assetRetirementGlobal, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster) {

        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            Asset asset = assetRetirementGlobalDetail.getAsset();

            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                List<GeneralLedgerPendingEntrySourceDetail> postables = generateGlPostablesForOnePayment(assetRetirementGlobal.getDocumentNumber(), assetRetirementGlPoster, asset, assetPayment);
                assetRetirementGlPoster.getPostables().addAll(postables);
            }

        }

    }

    /**
     * 
     * Generate a collection of Postables for each payment.
     * 
     * @param documentNumber
     * @param assetRetirementGlPoster
     * @param asset
     * @param assetPayment
     * @return
     */
    private List<GeneralLedgerPendingEntrySourceDetail> generateGlPostablesForOnePayment(String documentNumber, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster, Asset asset, AssetPayment assetPayment) {
        List<GeneralLedgerPendingEntrySourceDetail> postables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        Account plantAccount = getPlantFundAccount(asset, assetPayment);

        if (ObjectUtils.isNotNull(plantAccount)) {
            AssetGlpeSourceDetail postable = null;
            KualiDecimal accountChargeAmount = assetPayment.getAccountChargeAmount();
            KualiDecimal accumlatedDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();

            if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                postables.add(createNewPostable(AmountCategory.CAPITALIZATION, asset, assetPayment, documentNumber, plantAccount));
            }

            if (accumlatedDepreciationAmount != null && !accumlatedDepreciationAmount.isZero()) {
                postables.add(createNewPostable(AmountCategory.ACCUMMULATE_DEPRECIATION, asset, assetPayment, documentNumber, plantAccount));
            }

            if (accountChargeAmount != null && accumlatedDepreciationAmount != null && !accountChargeAmount.subtract(accumlatedDepreciationAmount).isZero()) {
                postables.add(createNewPostable(AmountCategory.OFFSET_AMOUNT, asset, assetPayment, documentNumber, plantAccount));
            }
        }
        return postables;
    }

    /**
     * 
     * This method creates one postable and sets the values.
     * 
     * @param category
     * @param asset
     * @param assetPayment
     * @param documentNumber
     * @param plantAccount
     * @return
     */
    private AssetGlpeSourceDetail createNewPostable(AmountCategory category, Asset asset, AssetPayment assetPayment, String documentNumber, Account plantAccount) {
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();

        AssetObjectCode assetObjectCode = assetObjectCodeService.findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment);
        category.setParams(postable, assetPayment, assetObjectCode);

        // Set Postable attributes which are common among Capitalized, Accumulated Depreciation and gain/loss disposition .
        postable.setDocumentNumber(documentNumber);
        postable.setAccount(plantAccount);
        postable.setAccountNumber(plantAccount.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.GL_BALANCE_TYPE_CDE_AC);
        postable.setChartOfAccountsCode(asset.getOrganizationOwnerChartOfAccountsCode());

        postable.setPostingYear(universityDateService.getCurrentFiscalYear());
        // Fields copied from payment
        postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        postable.setProjectCode(assetPayment.getProjectCode());
        postable.setSubAccountNumber(assetPayment.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());

        return postable;
    }


    /**
     * Get the offset Object Code.
     * 
     * @param asset
     * @return
     */
    static private ObjectCode getOffsetFinancialObject(Asset asset) {
        Map pkMap = new HashMap();
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        pkMap.put("universityFiscalYear", universityDateService.getCurrentFiscalYear());
        pkMap.put("chartOfAccountsCode", asset.getOrganizationOwnerChartOfAccountsCode());
        pkMap.put("financialObjectCode", DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
        ObjectCode offsetFinancialObject = (ObjectCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, pkMap);

        if (ObjectUtils.isNull(offsetFinancialObject)) {
            throw new ReferentialIntegrityException("Object code is not defined for this universityFiscalYear=" + universityDateService.getCurrentFiscalYear() + ", chartOfAccountsCode=" + asset.getOrganizationOwnerChartOfAccountsCode() + ", financialObjectCode=" + DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
        }

        return offsetFinancialObject;
    }


    /**
     * 
     * Get the corresponding Plant Fund Account object based on the payment's financialObjectSubTypeCode.
     * 
     * @param asset
     * @param payment
     * @return
     */
    private Account getPlantFundAccount(Asset asset, AssetPayment payment) {
        Account plantFundAccount = null;

        payment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);

        if (ObjectUtils.isNotNull(payment.getFinancialObject()) && ObjectUtils.isNotNull(asset.getOrganizationOwnerAccount())) {
            String financialObjectSubTypeCode = payment.getFinancialObject().getFinancialObjectSubTypeCode();

            if (StringUtils.isNotBlank(financialObjectSubTypeCode)) {
                if (Arrays.asList(MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES.split(";")).contains(financialObjectSubTypeCode)) {
                    plantFundAccount = asset.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
                }
                else if (Arrays.asList(NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES.split(";")).contains(financialObjectSubTypeCode)) {
                    plantFundAccount = asset.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
                }
            }
        }

        return plantFundAccount;
    }
}
