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
package org.kuali.module.cams.document.authorization;

import static org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase.MAINTAINABLE_ERROR_PREFIX;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.authorization.FinancialSystemDocumentActionFlags;
import org.kuali.kfs.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementReason;
import org.kuali.module.cams.service.AssetRetirementService;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetRetirementAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private static AssetRetirementService assetRetirementService = SpringContext.getBean(AssetRetirementService.class);
    private static BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);


    /**
     * Returns the set of authorization restrictions (if any) that apply to this Asset Retirement in this context.
     * 
     * @param document
     * @param user
     * @return a new set of {@link MaintenanceDocumentAuthorizations} that marks certain fields as necessary
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations auths = super.getFieldAuthorizations(document, user);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        if (!assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            auths.addHiddenAuthField(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER);
        }

        return auths;
    }

    /**
     * 
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public FinancialSystemDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        FinancialSystemDocumentActionFlags actionFlags = super.getDocumentActionFlags(document, user);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();
        String reasonCode = assetRetirementGlobal.getRetirementReasonCode();
        Map<String, Object> pkMap = new HashMap<String, Object>();

        pkMap.put(CamsPropertyConstants.AssetRetirementReason.RETIREMENT_REASON_CODE, reasonCode);

        AssetRetirementReason assetRetirementReason = (AssetRetirementReason) businessObjectService.findByPrimaryKey(AssetRetirementReason.class, pkMap);
        if (assetRetirementReason != null && assetRetirementReason.isRetirementReasonRestrictionIndicator()) {
            hideActions(actionFlags);
            GlobalVariables.getErrorMap().putError(MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE, CamsKeyConstants.Retirement.ERROR_DISALLOWED_RETIREMENT_REASON_CODE);
        }
        else if (Arrays.asList(parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.MERGE_SEPARATE_RETIREMENT_REASONS).split(";")).contains(reasonCode) && !user.isMember(CamsConstants.Workgroups.WORKGROUP_MERGE_SEPARATE_WORKGROUP)) {
            // Retirement Reason Code is restricted for use depending on the user group
            hideActions(actionFlags);
            GlobalVariables.getErrorMap().putError(MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE, CamsKeyConstants.Retirement.ERROR_DISALLOWED_RETIREMENT_REASON_CODE);
        }
        else if (Arrays.asList(parameterService.getParameterValue(AssetRetirementGlobal.class, CamsConstants.Parameters.RAZE_RETIREMENT_REASONS).split(";")).contains(reasonCode) && !user.isMember(CamsConstants.Workgroups.WORKGROUP_RAZE_WORKGROUP)) {
            hideActions(actionFlags);
            GlobalVariables.getErrorMap().putError(MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE, CamsKeyConstants.Retirement.ERROR_DISALLOWED_RETIREMENT_REASON_CODE);
        }
        return actionFlags;
    }

    /**
     * 
     * Hide action buttons in the screen when the user not allowed to proceed.
     * 
     * @param actionFlags
     */
    private void hideActions(DocumentActionFlags actionFlags) {
        actionFlags.setCanAdHocRoute(false);
        actionFlags.setCanApprove(false);
        actionFlags.setCanBlanketApprove(false);
        actionFlags.setCanRoute(false);
        actionFlags.setCanSave(false);
    }


}
