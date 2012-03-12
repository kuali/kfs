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

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.ObjectUtils;

public class AssetGlobalPreRules extends MaintenancePreRulesBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalPreRules.class);

    /**
     * Sets up a convenience object and few other Asset attributes
     * 
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        if (hasDifferentObjectSubTypes(document)) {
            if (!isOkHavingDifferentObjectSubTypes()) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }
        return true;
    }


    /**
     * Validate all object sub type codes are from the same group.
     * 
     * @param assetGlobal
     * @param newLine
     * @return
     */
    public boolean hasDifferentObjectSubTypes(MaintenanceDocument document) {
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();

        boolean invalid = false;
        List<String> objectSubTypeList = new ArrayList<String>();

        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
            if (ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode())) {
                objectSubTypeList.add(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());
            }
        }
        if (!getAssetService().isObjectSubTypeCompatible(objectSubTypeList)) {
            invalid = true;
        }
        return invalid;
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
