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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetRetirementGlobal;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetRetirementGlobalRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalRule.class);

    /**
     * Constructs a AssetLocationGlobalRule
     */
    public AssetRetirementGlobalRule() {
    }

    /**
     * Processes rules when saving this global.
     * 
     * @param document MaintenanceDocument type of document to be processed.
     * @return boolean true when success
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        if (StringUtils.equalsIgnoreCase(CamsConstants.AssetRetirementReasonCode.MERGED, assetRetirementGlobal.getRetirementReasonCode()) && assetRetirementGlobal.getMergedTargetCapitalAssetNumber() == null) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_MERGED_TARGET_ASSET_NUMBER_NULL);
        }
        return success;
    }

}
