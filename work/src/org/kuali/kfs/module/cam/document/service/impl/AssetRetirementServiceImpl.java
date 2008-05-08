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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetRetirementService;

public class AssetRetirementServiceImpl implements AssetRetirementService {

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
        AssetPaymentService assetPaymentService = SpringContext.getBean(AssetPaymentService.class);

        List<AssetPayment> offsetPayments = new TypedArrayList(AssetPayment.class);
        Integer maxSequenceNo = assetPaymentService.getMaxSequenceNumber(sourceAsset.getCapitalAssetNumber());

        AssetPayment offsetPayment = null;
        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                if (!CamsConstants.TRANSFER_PAYMENT_CODE_Y.equalsIgnoreCase(sourcePayment.getTransferPaymentCode())) {
                    offsetPayment = (AssetPayment) ObjectUtils.deepCopy(sourcePayment);
                    offsetPayment.setFinancialDocumentTypeCode(AssetRetirementGlobal.ASSET_RETIREMENT_DOCTYPE_CD);
                    offsetPayment.setDocumentNumber(currentDocumentNumber);
                    offsetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    assetPaymentService.adjustPaymentAmounts(offsetPayment, true, false);
                    offsetPayments.add(offsetPayment);
                }
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
                if (!CamsConstants.TRANSFER_PAYMENT_CODE_Y.equalsIgnoreCase(sourcePayment.getTransferPaymentCode())) {
                    newPayment = (AssetPayment) ObjectUtils.deepCopy(sourcePayment);
                    newPayment.setCapitalAssetNumber(targetAsset.getCapitalAssetNumber());
                    newPayment.setFinancialDocumentTypeCode(AssetRetirementGlobal.ASSET_RETIREMENT_DOCTYPE_CD);
                    newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    newPayment.setDocumentNumber(currentDocumentNumber);
                    resetPeriodDepreciationAmount(newPayment);
                    newPayments.add(newPayment);
                }
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
}
