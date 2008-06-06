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
package org.kuali.module.cams.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.cams.service.AssetService;

/**
 * 
 * This prerule is ex..
 */
public class AssetPaymentDocumentPreRules extends PreRulesContinuationBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentDocumentPreRules.class);
    private static AssetService assetService = SpringContext.getBean(AssetService.class);

    public boolean doRules(Document document) {
        if (hasDifferentObjectSubTypes((AssetPaymentDocument) document)) {
            if (!isOkHavingDifferentObjectSubTypes()) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * This method determines whether or not an asset has different object sub type codes in its documents.
     * 
     * @return true when the asset has payments with object codes that point to different object sub type codes
     */
    public boolean hasDifferentObjectSubTypes(AssetPaymentDocument document) {
        // This method will only execute if the document is being submitted.
        if (!document.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            return false;
        }

        document.getAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);

        List<String> subTypes = new ArrayList<String>();
        subTypes = SpringContext.getBean(ParameterService.class).getParameterValues(Asset.class, CamsConstants.Parameters.OBJECT_SUB_TYPE_GROUPS);

        List<AssetPayment> assetPayments = document.getAsset().getAssetPayments();
        List<AssetPaymentDetail> assetPaymentDetails = document.getSourceAccountingLines();
        List<String> validObjectSubTypes = new ArrayList<String>();

        String objectSubTypeCode = null;

        /*
         * Expected system parameter elements (object sub types). [BD,BF] [CM,CF,CO] [UC,UF,UO] [LI,LF]
         */

        // Getting the list of object sub type codes from the asset payments on the jsp.
        List<String> objectSubTypeList = new ArrayList<String>();
        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);            
            if (ObjectUtils.isNull(assetPaymentDetail.getObjectCode())) {
                return false;
            }            
            objectSubTypeList.add(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());
        }

        if (!assetService.isObjectSubTypeCompatible(objectSubTypeList)) {
            return true;
        }

        // Comparing against the already approved asset payments
        if (!assetPayments.isEmpty()) {
            for (AssetPayment assetPayment : assetPayments) {
                String paymentSubObjectType = assetPayment.getFinancialObject().getFinancialObjectSubTypeCode();

                validObjectSubTypes = new ArrayList<String>();
                for (String subType : subTypes) {
                    validObjectSubTypes = Arrays.asList(StringUtils.split(subType, ","));
                    if (validObjectSubTypes.contains(paymentSubObjectType)) {
                        break;
                    }
                    validObjectSubTypes = new ArrayList<String>();
                }
                if (validObjectSubTypes.isEmpty())
                    validObjectSubTypes.add(paymentSubObjectType);

                // Comparing the same asset payment document
                for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
                    if (!validObjectSubTypes.contains(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode())) {
                        // Differences where found.
                        return true;
                    }
                }
            }
        }
        // If none object sub types are different...
        return false;
    }


    private boolean isOkHavingDifferentObjectSubTypes() {
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);
        String warningMessage = kualiConfiguration.getPropertyString(CamsKeyConstants.Payment.WARNING_NOT_SAME_OBJECT_SUB_TYPES);
        return super.askOrAnalyzeYesNoQuestion(CamsConstants.ASSET_PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION, warningMessage);
    }
}
