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

import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.rules.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Org;

public class AssetTransferDocumentRule extends GeneralLedgerPostingDocumentRuleBase {

    public static final String DOCUMENT_NUMBER_PATH = "documentNumber";
    public static final String DOCUMENT_PATH = "document";
    public static final String DOC_HEADER_PATH = DOCUMENT_PATH + "." + DOCUMENT_NUMBER_PATH;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        // check if selected account has plant fund accounts
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        validateOwnerAccount(assetTransferDocument);
        // validate if location info is available, campus or off-campus
        return true;
    }


    private void validateOwnerAccount(AssetTransferDocument assetTransferDocument) {
        assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
        // check if account is active
        Account organizationOwnerAccount = assetTransferDocument.getOrganizationOwnerAccount();
        if (organizationOwnerAccount == null || organizationOwnerAccount.isExpired() || organizationOwnerAccount.isAccountClosedIndicator()) {
            // show error if account is not active
        }
        else {
            Org ownerOrg = organizationOwnerAccount.getOrganization();
            Account campusPlantAccount = ownerOrg.getCampusPlantAccount();
            Account organizationPlantAccount = ownerOrg.getOrganizationPlantAccount();

            // if asset is movable, use campus plant account number, alert user if acct not found
            if (campusPlantAccount == null) {
                GlobalVariables.getErrorMap().putError(DOC_HEADER_PATH, CamsKeyConstants.Transfer.ERROR_CAMPUS_PLANT_FUND_UNKNOWN);
            }
            // if asset is immovable use org plant account number, alert user if acct not found
            if (organizationPlantAccount == null) {
                GlobalVariables.getErrorMap().putError(DOC_HEADER_PATH, CamsKeyConstants.Transfer.ERROR_ORG_PLANT_FUND_UNKNOWN);
            }
        }
    }


}
