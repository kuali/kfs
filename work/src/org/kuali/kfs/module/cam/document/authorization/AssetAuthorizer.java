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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KimAttributes;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    /**
     * Returns the set of authorization restrictions (if any) that apply to this Asset in this context.
     * 
     * @param document
     * @param user
     * @return a new set of {@link MaintenanceDocumentAuthorizations} that marks certain fields as necessary
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, Person user) {
        MaintenanceDocumentAuthorizations auths = super.getFieldAuthorizations(document, user);
        Asset newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        Asset oldAsset = (Asset) document.getOldMaintainableObject().getBusinessObject();
        if (document.isNew()) {
            // fabrication request asset creation. Hide fields that are only applicable to asset fabrication. For
            // sections that are to be hidden on asset fabrication see AssetMaintainableImpl.getCoreSections
            hideFields(auths, CamsConstants.Asset.EDIT_DETAIL_INFORMATION_FIELDS);
            hideFields(auths, CamsConstants.Asset.EDIT_ORGANIZATION_INFORMATION_FIELDS);
            auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS);
            auths.addReadonlyAuthField(CamsPropertyConstants.Asset.VENDOR_NAME);
            auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE);
        }
        else {
            // acquisition type code is read-only during edit
            auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE);
            // fabrication fields are read-only
            makeReadOnlyFields(auths, Arrays.asList(CamsConstants.Asset.FABRICATION_INFORMATION_FIELDS));
        }

        hidePaymentSequence(auths, newAsset);
        if (!CamsConstants.RETIREMENT_REASON_CODE_M.equals(newAsset.getRetirementReasonCode())) {
            // hide merge target capital asset number
            auths.addHiddenAuthField(CamsPropertyConstants.Asset.RETIREMENT_INFO_MERGED_TARGET);
        }

        setConditionalReadOnlyFields(auths, newAsset, user);
        // read-only acquisition code
        return auths;
    }

    /**
     * Check and set view only for campusTagNumber,assetTypeCode and assetDescription
     * 
     * @param auths
     * @param asset
     */
    private void setConditionalReadOnlyFields(MaintenanceDocumentAuthorizations auths, Asset asset, Person user) {
        // Apply the rule, when tag number exists and user is not a member of WORKGROUP_CM_SUPER_USERS &
        // WORKGROUP_CM_ASSET_MERGE_SEPARATE_USERS
        if (asset.isTagged() & !KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS) && !KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, CamsConstants.Workgroups.WORKGROUP_CM_ASSET_MERGE_SEPARATE_USERS)) {
            // if tag was created in a prior fiscal year, set tag number, asset type code and description as view only
            if (getAssetService().isAssetTaggedInPriorFiscalYear(asset)) {
                makeReadOnlyFields(auths, getParameterService().getParameterValues(Asset.class, CamsConstants.Parameters.EDITABLE_FIELDS_WHEN_TAGGED_PRIOR_FISCAL_YEAR));
            }
            else {
                // Set the Tag Number as view only
                auths.addReadonlyAuthField(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER);
            }
        }
    }


    private void hidePaymentSequence(MaintenanceDocumentAuthorizations auths, Asset asset) {
        int size = asset.getAssetPayments().size();
        for (int i = 0; i < size; i++) {
            auths.addHiddenAuthField(CamsPropertyConstants.Asset.ASSET_PAYMENTS + "[" + i + "]." + CamsPropertyConstants.AssetPayment.PAYMENT_SEQ_NUMBER);
        }
    }


    private void hideFields(MaintenanceDocumentAuthorizations auths, List<String> hiddenFields) {
        for (String field : hiddenFields) {
            auths.addHiddenAuthField(field);
        }
    }

    private void makeReadOnlyFields(MaintenanceDocumentAuthorizations auths, List<String> readOnlyFields) {
        for (String field : readOnlyFields) {
            auths.addReadonlyAuthField(field);
        }
    }

    private void hideFields(MaintenanceDocumentAuthorizations auths, String[] hiddenFields) {
        for (String field : hiddenFields) {
            auths.addHiddenAuthField(field);
        }
    }


//    @Override
//    public FinancialSystemDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemDocumentActionFlags actionFlags = super.getDocumentActionFlags(document, user);
//
//        // If capital asset is retired then deny "Save", "Submit" and "Approve"
//        Asset asset = (Asset) document.getDocumentBusinessObject();
//        if (getAssetService().isAssetRetired(asset) && getAssetService().isCapitalAsset(asset)) {
//            GlobalVariables.getErrorMap().putError(MAINTAINABLE_ERROR_PREFIX + ASSET_INVENTORY_STATUS, CamsKeyConstants.ERROR_ASSET_RETIRED_NOEDIT, new String[] {});
//            actionFlags.setCanAdHocRoute(false);
//            actionFlags.setCanApprove(false);
//            actionFlags.setCanBlanketApprove(false);
//            actionFlags.setCanRoute(false);
//            actionFlags.setCanSave(false);
//        }
//        return actionFlags;
//    }

    
    private ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    private AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase#populateRoleQualification(org.kuali.rice.kns.document.Document, java.util.Map)
     */
    @Override
    protected void populateRoleQualification(Document document, Map<String, String> attributes) {
        super.populateRoleQualification(document, attributes);
        
        Asset asset = (Asset) ((MaintenanceDocument) document).getNewMaintainableObject().getBusinessObject();
        
        String chart = asset.getOrganizationOwnerChartOfAccountsCode();
        String org = asset.getOrganizationOwnerAccount().getOrganizationCode();
        
        attributes.put(KimAttributes.CHART_OF_ACCOUNTS_CODE, chart);
        attributes.put(KimAttributes.ORGANIZATION_CODE, org);        
    }
}
