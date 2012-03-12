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
import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class validates object sub type code for the financial object for which payment is being made
 */
public class AssetPaymentObjectCodeValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentObjectCodeValidation.class);

    private AssetService assetService;
    private ParameterService parameterService;

    private AccountingLine accountingLineForValidation;

    /**
     * Validate financial object sub type code validation, it should be of type capital asset.
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // skip check if accounting line is from CAB
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return true;
        }
        
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) getAccountingLineForValidation();
        boolean result = true;
        
        List<String> validSubtypeCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(AssetGlobal.class, CamsConstants.Parameters.CAPITAL_OBJECT_SUB_TYPES) );
        
        String parameterDetail = "(module:" + KRADServiceLocatorWeb.getKualiModuleService().getNamespaceCode(AssetGlobal.class) + "/component:" + AssetGlobal.class.getSimpleName() + ")";
        boolean capitalAssetFound = false;
        
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = assetPaymentDocument.getAssetPaymentAssetDetail();
        for(AssetPaymentAssetDetail assetPaymentAssetDetail:assetPaymentAssetDetails) {
            if (assetService.isCapitalAsset(assetPaymentAssetDetail.getAsset())) {
                if (!validSubtypeCodes.contains(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode())) {            
                    GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE, CamsKeyConstants.Payment.ERROR_INVALID_OBJECT_SUBTYPE, new String[] { assetPaymentDetail.getFinancialObjectCode(), assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode(), CamsConstants.Parameters.CAPITAL_OBJECT_SUB_TYPES +" "+parameterDetail,validSubtypeCodes.toString() });
                    result = false;
                    break;
                }
            }
            
        }                
        return result;
    }

    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    public void setAccountingLineForValidation(AccountingLine accountingLine) {
        this.accountingLineForValidation = accountingLine;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


}
