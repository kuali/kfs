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
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.VENDOR_NAME;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetRetirementAuthorizer extends MaintenanceDocumentAuthorizerBase {

    private static final String[] CM_ASSET_MERGE_SEPARATE_USERS_DENIED_FIELDS = new String[] { ORGANIZATION_OWNER_ACCOUNT_NUMBER, VENDOR_NAME, ASSET_DATE_OF_SERVICE };

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

        if (StringUtils.equalsIgnoreCase(CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER, assetGlobal.getRetirementReasonCode()) || StringUtils.equalsIgnoreCase(CamsConstants.AssetRetirementReasonCode.AUCTION, assetGlobal.getRetirementReasonCode())) {
            //auths.addEditableAuthField(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.RETIREMENT_CHART_OF_ACCOUNTS_CODE);
            auths.addHiddenAuthField(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.RETIREMENT_CHART_OF_ACCOUNTS_CODE);
        }
        return auths;
    }

}
