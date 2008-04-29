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

import java.util.List;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetRetirementService;

public class AssetRetirementServiceImpl implements AssetRetirementService {

    public boolean isAssetRetiredBySoldOrAuction(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.AUCTION.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.SOLD.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    public boolean isAssetRetiredByExternalTransferOrGift(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.GIFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    public boolean isAssetRetiredByMerged(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.MERGED.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    public boolean isAssetRetiredByTheft(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.THEFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    public String getAssetRetirementReasonName(AssetRetirementGlobal assetRetirementGlobal) {
        return assetRetirementGlobal.getRetirementReason() == null ? new String() : assetRetirementGlobal.getRetirementReason().getRetirementReasonName();
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#generateOffsetPaymentsForEachSource(org.kuali.module.cams.bo.Asset, java.util.List, java.lang.String)
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
                    offsetPayment.setFinancialDocumentTypeCode("AMRG");
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
     * @see org.kuali.module.cams.service.AssetRetirementService#generateNewPaymentForTarget(org.kuali.module.cams.bo.Asset, org.kuali.module.cams.bo.Asset, java.util.List, java.lang.Integer, java.lang.String)
     */
    public Integer generateNewPaymentForTarget(Asset targetAsset, Asset sourceAsset, List<PersistableBusinessObject> persistables, Integer maxSequenceNo, String currentDocumentNumber) {
        List<AssetPayment> newPayments = new TypedArrayList(AssetPayment.class);
        AssetPayment newPayment = null;
        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                if (!CamsConstants.TRANSFER_PAYMENT_CODE_Y.equalsIgnoreCase(sourcePayment.getTransferPaymentCode())) {
                    newPayment = (AssetPayment) ObjectUtils.deepCopy(sourcePayment);
                    newPayment.setCapitalAssetNumber(targetAsset.getCapitalAssetNumber());
                    newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    newPayment.setDocumentNumber(currentDocumentNumber);
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


}
