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

import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.rules.AssetTransferDocumentRule;
import org.kuali.module.cams.service.AssetHeaderService;

public class AssetTransferDocumentAuthorizer extends TransactionalDocumentAuthorizerBase {

    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);

    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {
        super.canInitiate(documentTypeName, user);
    }

    /**
     * @see org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser) This method determines if user can continue with transfer action or not, following
     *      conditions are checked to decide
     *      <li>Check if asset is active and not retired</li>
     *      <li>Find all pending documents associated with this asset, if any found disable the transfer action</li>
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags actionFlags = super.getDocumentActionFlags(document, user);
        Asset asset = ((AssetTransferDocument) document).getAsset();
        // check if transfer action is allowed, if not present an error message
        // check if asset is active
        boolean transferable = true;
        if (parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.RETIRED_STATUS_CODES).contains(asset.getInventoryStatusCode())) {
            transferable = false;
            GlobalVariables.getErrorMap().putError(AssetTransferDocumentRule.DOC_HEADER_PATH, CamsKeyConstants.Transfer.ERROR_ASSET_RETIRED_NOTRANSFER, asset.getCapitalAssetNumber().toString(), asset.getRetirementReason().getRetirementReasonName());
        }
        // check if any pending transactions
        if (transferable) {
            Collection<AssetHeader> pendingHeaders = SpringContext.getBean(AssetHeaderService.class).findPendingHeadersByAsset(asset, document);
            if (pendingHeaders != null && !pendingHeaders.isEmpty()) {
                transferable = false;
                String[] headerNos = new String[pendingHeaders.size()];
                int pos = 0;
                for (AssetHeader assetHeader : pendingHeaders) {
                    headerNos[pos] = assetHeader.getDocumentNumber();
                    pos++;
                }
                GlobalVariables.getErrorMap().putError(AssetTransferDocumentRule.DOC_HEADER_PATH, CamsKeyConstants.Transfer.ERROR_ASSET_DOCS_PENDING, headerNos.toString());
            }
        }
        // Disable the buttons, if not transferable
        if (!transferable) {
            actionFlags.setCanAdHocRoute(false);
            actionFlags.setCanApprove(false);
            actionFlags.setCanBlanketApprove(false);
            actionFlags.setCanRoute(false);
            actionFlags.setCanSave(false);
        }
        return actionFlags;
    }
}
