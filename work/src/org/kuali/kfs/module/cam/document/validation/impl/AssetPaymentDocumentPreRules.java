/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.validation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This prerule is ex..
 */
public class AssetPaymentDocumentPreRules extends PromptBeforeValidationBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentDocumentPreRules.class);

    @Override
    public boolean doPrompts(Document document) {
        if (hasDifferentObjectSubTypes((AssetPaymentDocument) document)) {
            if (!isOkHavingDifferentObjectSubTypes()) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }
        return true;
    }

    /**
     * This method determines whether or not an asset has different object sub type codes in its documents.
     * 
     * @return true when the asset has payments with object codes that point to different object sub type codes
     */
    public boolean hasDifferentObjectSubTypes(AssetPaymentDocument document) {
        //This method will only execute if the document is being submitted.
     if (!(document.getDocumentHeader().getWorkflowDocument().isSaved() || document.getDocumentHeader().getWorkflowDocument().isInitiated())) {
        return false;
        }

        List<String> subTypes = new ArrayList<String>();
        subTypes = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(Asset.class, CamsConstants.Parameters.OBJECT_SUB_TYPE_GROUPS) );

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

        if (!getAssetService().isObjectSubTypeCompatible(objectSubTypeList)) {
            return true;
        }

        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = document.getAssetPaymentAssetDetail();
        for (AssetPaymentAssetDetail assetPaymentAssetDetail : assetPaymentAssetDetails) {
            assetPaymentAssetDetail.getAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
            List<AssetPayment> assetPayments = assetPaymentAssetDetail.getAsset().getAssetPayments();

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
        }
        // If none object sub types are different...
        return false;
    }

    protected boolean isOkHavingDifferentObjectSubTypes() {
        String parameterDetail = "(module:" + KRADServiceLocatorWeb.getKualiModuleService().getNamespaceCode(AssetGlobal.class) + "/component:" + AssetGlobal.class.getSimpleName() + ")";
        
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
        
        String continueQuestion = kualiConfiguration.getPropertyValueAsString(CamsKeyConstants.CONTINUE_QUESTION);
        String warningMessage = kualiConfiguration.getPropertyValueAsString(CamsKeyConstants.Payment.WARNING_NOT_SAME_OBJECT_SUB_TYPES) + " " + CamsConstants.Parameters.OBJECT_SUB_TYPE_GROUPS + " " + parameterDetail + ". " + continueQuestion;
        return super.askOrAnalyzeYesNoQuestion(CamsConstants.AssetPayment.ASSET_PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION, warningMessage);
    }

    protected AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }
}
