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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.gl.CamsGlPosterBase;
import org.kuali.module.cams.service.AssetObjectCodeService;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetRetirementService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetRetirementServiceImpl implements AssetRetirementService {

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

        AssetPayment offsetPayment = null;
        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                offsetPayment = (AssetPayment) ObjectUtils.deepCopy(sourcePayment);
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
        AssetPayment newPayment = null;
        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                newPayment = (AssetPayment) ObjectUtils.deepCopy(sourcePayment);
                newPayment.setCapitalAssetNumber(targetAsset.getCapitalAssetNumber());
                newPayment.setFinancialDocumentTypeCode(AssetRetirementGlobal.ASSET_RETIREMENT_DOCTYPE_CD);
                newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                newPayment.setDocumentNumber(currentDocumentNumber);
                resetPeriodDepreciationAmount(newPayment);
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
     * Clear setting for periodic depreciation expenses.
     * 
     * @param newPayment
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void resetPeriodDepreciationAmount(AssetPayment newPayment) throws IllegalAccessException, InvocationTargetException {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(AssetPayment.class);

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method writeMethod = propertyDescriptor.getWriteMethod();

            if (writeMethod != null && Pattern.matches(CamsConstants.SET_PERIOD_DEPRECIATION_AMOUNT_REGEX, writeMethod.getName().toLowerCase())) {
                Object[] nullVals = new Object[] { null };
                writeMethod.invoke(newPayment, nullVals);
            }
        }
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
    public boolean checkRetireMultipleAssets(String retirementReasonCode, List<AssetRetirementGlobalDetail> assetRetirementDetails, Integer maxNumber, boolean addErrorPath) {
        boolean success = true;
        String errorPath = RiceConstants.MAINTENANCE_NEW_MAINTAINABLE + KFSConstants.MAINTENANCE_ADD_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS;

        if (addErrorPath) {
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
        }
        if (isRetirementReasonCodeInGroup(CamsConstants.AssetRetirementReasonCodeGroup.SINGLE_RETIRED_ASSET, retirementReasonCode) && assetRetirementDetails.size() > maxNumber) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_MULTIPLE_ASSET_RETIRED);
            success = false;
        }
        if (addErrorPath) {
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
        }

        return success;
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#createGLPostables(org.kuali.module.cams.bo.AssetRetirementGlobal,
     *      org.kuali.module.cams.gl.CamsGlPosterBase)
     */
    public void createGLPostables(AssetRetirementGlobal assetRetirementGlobal, CamsGlPosterBase assetRetirementGlPoster) {

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
    private List<GeneralLedgerPendingEntrySourceDetail> generateGlPostablesForOnePayment(String documentNumber, CamsGlPosterBase assetRetirementGlPoster, Asset asset, AssetPayment assetPayment) {
        List<GeneralLedgerPendingEntrySourceDetail> postables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        Account plantAccount = getPlantFundAccount(asset, assetPayment);

        if (ObjectUtils.isNotNull(plantAccount)) {
            AssetObjectCode assetObjectCode = assetObjectCodeService.findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment);

            KualiDecimal accountChargeAmount = assetPayment.getAccountChargeAmount();
            KualiDecimal accumlatedDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();

            if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();
                postable.setFinancialDocumentLineDescription("Plant Fund for FMS");
                postable.setAmount(accountChargeAmount);
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
                postable.setCapitalization(true);

                setCommonGlPostableAttributes(documentNumber, asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment, plantAccount, postable);
                postables.add(postable);
            }
            if (accumlatedDepreciationAmount != null && !accumlatedDepreciationAmount.isZero()) {
                AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();
                postable.setFinancialDocumentLineDescription("Accumulated Depreciation");
                postable.setAmount(accumlatedDepreciationAmount);
                postable.setFinancialObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObject());
                postable.setAccumulatedDepreciation(true);

                setCommonGlPostableAttributes(documentNumber, asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment, plantAccount, postable);
                postables.add(postable);
            }
            if (accountChargeAmount != null && accumlatedDepreciationAmount != null && !accountChargeAmount.subtract(accumlatedDepreciationAmount).isZero()) {
                AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();

                postable.setFinancialDocumentLineDescription("Gain/Loss Disposition of Assets");
                postable.setAmount(accountChargeAmount.subtract(accumlatedDepreciationAmount));
                postable.setFinancialObjectCode(DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
                postable.setObjectCode(getOffsetFinancialObject(asset));
                postable.setOffset(true);
                setCommonGlPostableAttributes(documentNumber, asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment, plantAccount, postable);
                postables.add(postable);
            }
        }
        return postables;
    }


    /**
     * Get the offset Object Code.
     * 
     * @param asset
     * @return
     */
    private ObjectCode getOffsetFinancialObject(Asset asset) {
        Map pkMap = new HashMap();
        pkMap.put("universityFiscalYear", universityDateService.getCurrentFiscalYear());
        pkMap.put("chartOfAccountsCode", asset.getOrganizationOwnerChartOfAccountsCode());
        pkMap.put("financialObjectCode", DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
        ObjectCode offsetFinancialObject = (ObjectCode) businessObjectService.findByPrimaryKey(ObjectCode.class, pkMap);

        if (ObjectUtils.isNull(offsetFinancialObject)) {
            throw new ReferentialIntegrityException("Object code is not defined for this universityFiscalYear=" + universityDateService.getCurrentFiscalYear() + ", chartOfAccountsCode=" + asset.getOrganizationOwnerChartOfAccountsCode() + ", financialObjectCode=" + DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
        }

        return offsetFinancialObject;
    }

    /**
     * Set Postable attributes which are common among Capitalized, Accumulated Depreciation and gain/loss disposition .
     * 
     * @param documentNumber
     * @param orgOwnerChartOfAccountsCode
     * @param assetPayment
     * @param plantAccount
     * @param postable
     */
    private void setCommonGlPostableAttributes(String documentNumber, String orgOwnerChartOfAccountsCode, AssetPayment assetPayment, Account plantAccount, AssetGlpeSourceDetail postable) {
        postable.setDocumentNumber(documentNumber);
        postable.setAccount(plantAccount);
        postable.setAccountNumber(plantAccount.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.GL_BALANCE_TYPE_CDE_AC);
        postable.setChartOfAccountsCode(orgOwnerChartOfAccountsCode);

        postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        postable.setPostingYear(universityDateService.getCurrentFiscalYear());
        // Fields copied from payment
        postable.setProjectCode(assetPayment.getProjectCode());
        postable.setSubAccountNumber(assetPayment.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());
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
