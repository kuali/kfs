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
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rules.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetTransferDocumentRule extends GeneralLedgerPostingDocumentRuleBase {

    public static final String DOCUMENT_NUMBER_PATH = "documentNumber";
    public static final String DOCUMENT_PATH = "document";
    public static final String DOC_HEADER_PATH = DOCUMENT_PATH + "." + DOCUMENT_NUMBER_PATH;
    private UniversityDateService universityDateService;
    private AssetPaymentService assetPaymentService;
    private AssetService assetService;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        applyRules(document);
        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        //TODO - Active loan and pending document edit
        
        return applyRules(document);
    }

    private boolean applyRules(Document document) {
        boolean valid = true;
        // check if selected account has plant fund accounts
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        valid &= validateOwnerAccount(assetTransferDocument);
        // validate if location info is available, campus or off-campus
        valid &= validateLocation(assetTransferDocument);
        return valid;
    }


    private boolean validateLocation(AssetTransferDocument assetTransferDocument) {
        boolean valid = true;
        if (!StringUtils.isBlank(assetTransferDocument.getCampusCode())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.CAMPUS);
            if (ObjectUtils.isNull(assetTransferDocument.getCampus())) {
                putError(CamsPropertyConstants.AssetTransferDocument.CAMPUS_CODE, CamsKeyConstants.Transfer.ERROR_INVALID_CAMPUS_CODE);
                valid &= false;
            }
        }
        if (!StringUtils.isBlank(assetTransferDocument.getBuildingCode())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.BUILDING);
            if (ObjectUtils.isNull(assetTransferDocument.getBuilding())) {
                putError(CamsPropertyConstants.AssetTransferDocument.BUILDING_CODE, CamsKeyConstants.Transfer.ERROR_INVALID_BUILDING_CODE);
                valid &= false;
            }
        }
        if (!StringUtils.isBlank(assetTransferDocument.getBuildingRoomNumber())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.BUILDING_ROOM);
            if (ObjectUtils.isNull(assetTransferDocument.getBuildingRoom())) {
                putError(CamsPropertyConstants.AssetTransferDocument.BUILDING_ROOM_NUMBER, CamsKeyConstants.Transfer.ERROR_INVALID_ROOM_NUMBER);
                valid &= false;
            }
        }
        if (!StringUtils.isBlank(assetTransferDocument.getOffCampusStateCode())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_STATE);
            if (ObjectUtils.isNull(assetTransferDocument.getOffCampusState())) {
                putError(CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_STATE_CODE, CamsKeyConstants.Transfer.ERROR_INVALID_OFF_CAMPUS_STATE);
                valid &= false;
            }
        }
        return valid;
    }


    private boolean validateOwnerAccount(AssetTransferDocument assetTransferDocument) {
        boolean valid = true;
        assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
        // check if account is active
        Account organizationOwnerAccount = assetTransferDocument.getOrganizationOwnerAccount();
        if (organizationOwnerAccount == null || organizationOwnerAccount.isExpired() || organizationOwnerAccount.isAccountClosedIndicator()) {
            // show error if account is not active
            putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_OWNER_ACCT_NOT_ACTIVE);
            valid &= false;
        }
        else {
            Org ownerOrg = organizationOwnerAccount.getOrganization();
            Account campusPlantAccount = ownerOrg.getCampusPlantAccount();
            Account organizationPlantAccount = ownerOrg.getOrganizationPlantAccount();

            // if asset is movable, use campus plant account number, alert user if acct not found
            if (campusPlantAccount == null) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_CAMPUS_PLANT_FUND_UNKNOWN);
                valid &= false;
            }
            // if asset is immovable use org plant account number, alert user if acct not found
            if (organizationPlantAccount == null) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_ORG_PLANT_FUND_UNKNOWN);
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * Convenience method to append the path prefix
     */
    public TypedArrayList putError(String propertyName, String errorKey, String... errorParameters) {
        return GlobalVariables.getErrorMap().putError(DOCUMENT_PATH + "." + propertyName, errorKey, errorParameters);
    }

    public UniversityDateService getUniversityDateService() {
        if (this.universityDateService == null) {
            this.universityDateService = SpringContext.getBean(UniversityDateService.class);

        }
        return universityDateService;
    }


    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


    public AssetPaymentService getAssetPaymentService() {
        if (this.assetPaymentService == null) {
            this.assetPaymentService = SpringContext.getBean(AssetPaymentService.class);

        }
        return assetPaymentService;
    }


    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }


    public AssetService getAssetService() {
        if (this.assetService == null) {
            this.assetService = SpringContext.getBean(AssetService.class);
        }
        return assetService;
    }


    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
}
