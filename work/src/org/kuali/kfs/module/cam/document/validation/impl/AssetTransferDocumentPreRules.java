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
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.rice.KNSServiceLocator;

public class AssetTransferDocumentPreRules extends PreRulesContinuationBase {

    @Override
    public boolean doRules(Document document) {
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        Asset asset = assetTransferDocument.getAsset();
        boolean confirmProceed = true;
        // If asset is loaned, ask a confirmation question
        if (asset.getExpectedReturnDate() != null && asset.getLoanReturnDate() == null) {
            KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();
            confirmProceed = super.askOrAnalyzeYesNoQuestion(CamsConstants.ASSET_LOAN_CONFIRM_QN_ID, kualiConfiguration.getPropertyString(CamsKeyConstants.Transfer.WARN_TRFR_AST_LOAN_ACTIVE));
            if (!confirmProceed) {
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
            }
        }
        return confirmProceed;
    }
}
