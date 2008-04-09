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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.exceptions.DocumentInitiationAuthorizationException;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.AssetRetirementGlobal;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetRetirementAuthorizer extends MaintenanceDocumentAuthorizerBase {

    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);


    /**
     * Returns the set of authorization restrictions (if any) that apply to this Asset Retirement in this context.
     * 
     * @param document
     * @param user
     * @return a new set of {@link MaintenanceDocumentAuthorizations} that marks certain fields as necessary
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations auths = super.getFieldAuthorizations(document, user);
        AssetRetirementGlobal assetGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        if (!StringUtils.equalsIgnoreCase(CamsConstants.AssetRetirementReasonCode.MERGED, assetGlobal.getRetirementReasonCode())) {
            auths.addHiddenAuthField("mergedTargetCapitalAssetNumber");
        }

        return auths;
    }

    /**
     * Asset Retirement can be initiated for CM_ASSET_MERGE_SEPARATE_USERS & CM_SUPER_USERS
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {
        if (!user.isMember(CamsConstants.Workgroups.WORKGROUP_CM_ASSET_MERGE_SEPARATE_USERS) && !user.isMember(CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS)) {
            throw new DocumentInitiationAuthorizationException(new String[] { CamsConstants.Workgroups.WORKGROUP_CM_ASSET_MERGE_SEPARATE_USERS, CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS, documentTypeName });
        }
    }


}
