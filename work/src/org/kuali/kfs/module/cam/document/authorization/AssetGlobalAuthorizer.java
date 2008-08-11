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
package org.kuali.kfs.module.cam.document.authorization;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.validation.impl.AssetLocationGlobalRule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;

public class AssetGlobalAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalAuthorizer.class);
    private static AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);

    /**
     * Hide or set specific fields as non-editable.
     *
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer#getFieldAuthorizations(org.kuali.core.document.MaintenanceDocument, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {

        MaintenanceDocumentAuthorizations auths = super.getFieldAuthorizations(document, user);
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        if (assetGlobalService.isAssetSeparateDocument(assetGlobal)) {
            setAssetGlobalDetailsFieldsReadOnlyAccessMode(auths, user);
            setAssetGlobalPaymentsFieldsReadOnlyAccessMode(auths, user, false);
        } else {
            setAssetGlobalPaymentsFieldsReadOnlyAccessMode(auths, user, true);
        }
        return auths;
    }
    
    /**
     * Sets Asset Global Details fields to read only
     * 
     * @param auths
     * @param user
     */
    private void setAssetGlobalDetailsFieldsReadOnlyAccessMode(MaintenanceDocumentAuthorizations auths, UniversalUser user) {
        // TODO set CAPITAL_ASSET_DESCRIPTION and ORGANIZATION_TEXT to read-only. see KFSMI-1208
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.AGENCY_NUMBER); // owner
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.INVENTORY_STATUS_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.CONDITION_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_DESCRIPTION);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.VENDOR_NAME);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.MANUFACTURER_NAME);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.MANUFACTURER_MODEL_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_TEXT);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.REP_USER_AUTH_ID);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.LAST_INVENTORY_DATE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.CREATE_DATE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_DEPRECIATION_DATE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.LAND_COUNTRY_NAME);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.LAND_ACREAGE_SIZE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.LAND_PARCEL_NUMBER);
    }

    /**
     * Sets Asset Payment Details fields to read only
     * 
     * @param auths
     * @param user
     */
    private void setAssetGlobalPaymentsFieldsReadOnlyAccessMode(MaintenanceDocumentAuthorizations auths, UniversalUser user, boolean bool) {
        // do not include add section within the payment details collection
        MaintainableCollectionDefinition maintCollDef = SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getMaintainableCollection("AssetGlobalMaintenanceDocument", "assetPaymentDetails");
        maintCollDef.setIncludeAddLine(bool);
    }
}