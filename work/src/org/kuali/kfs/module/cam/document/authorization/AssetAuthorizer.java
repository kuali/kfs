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
import static org.kuali.module.cams.CamsPropertyConstants.Asset.AGENCY_NUMBER;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.GOVERNMENT_TAG_NUMBER;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.NATIONAL_STOCK_NUMBER;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.OLD_TAG_NUMBER;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.VENDOR_NAME;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetAuthorizer extends MaintenanceDocumentAuthorizerBase {
    private static final String[] RETIRED_INV_CODES = new String[] { "O", "R", "E" };
    private static String[] DEFAULT_READONLY_FIELDS = new String[] { ORGANIZATION_OWNER_ACCOUNT_NUMBER, ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE, AGENCY_NUMBER, VENDOR_NAME, OLD_TAG_NUMBER, GOVERNMENT_TAG_NUMBER, NATIONAL_STOCK_NUMBER, ASSET_DATE_OF_SERVICE };
    private static final String[] CM_ASSET_MERGE_SEPARATE_USERS_READONLY_FIELDS = new String[] { ORGANIZATION_OWNER_ACCOUNT_NUMBER, VENDOR_NAME, ASSET_DATE_OF_SERVICE };
    private static String[] CM_SUPER_USERS_READONLY_FIELDS = new String[] {};


    /**
     * Returns the set of authorization restrictions (if any) that apply to this Asset in this context.
     * 
     * @param document
     * @param user
     * @return a new set of {@link MaintenanceDocumentAuthorizations} that marks certain fields as necessary
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations auths = super.getFieldAuthorizations(document, user);
        Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        if (user.isMember(CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS)) {
            setReadonlyAuthorizations(auths, CM_SUPER_USERS_READONLY_FIELDS);
        }
        else if (user.isMember(CamsConstants.Workgroups.WORKGROUP_CM_ASSET_MERGE_SEPARATE_USERS)) {
            setReadonlyAuthorizations(auths, CM_ASSET_MERGE_SEPARATE_USERS_READONLY_FIELDS);
        }
        else {
            setReadonlyAuthorizations(auths, DEFAULT_READONLY_FIELDS);
        }
        return auths;
    }

    private void setReadonlyAuthorizations(MaintenanceDocumentAuthorizations auths, String[] readOnlyFields) {
        for (String field : readOnlyFields) {
            auths.addHiddenAuthField(field);
        }
    }

    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags actionFlags = super.getDocumentActionFlags(document, user);

        // If asset is retired then deny "Save", "Submit" and "Approve"
        Asset asset = (Asset) document.getDocumentBusinessObject();
        if (ArrayUtils.contains(RETIRED_INV_CODES, asset.getInventoryStatusCode())) {
            actionFlags.setCanAdHocRoute(false);
            actionFlags.setCanApprove(false);
            actionFlags.setCanBlanketApprove(false);
            actionFlags.setCanRoute(false);
            actionFlags.setCanSave(false);
            GlobalVariables.getErrorMap().putError(MAINTAINABLE_ERROR_PREFIX + ASSET_INVENTORY_STATUS, CamsKeyConstants.ERROR_ASSET_RETIRED_NOEDIT, new String[] {});
        }
        return actionFlags;
    }

}
