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

import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;


public class AssetPaymentDocumentRule extends AccountingDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentDocumentRule.class);
    
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        LOG.info("*** AssetPaymentRule - processCustomSaveDocumentBusinessRules(Document document)");
        boolean valid = super.processCustomSaveDocumentBusinessRules(document);

        if (valid)
            valid&=this.validateAssetPayment(document);
        

        return valid;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.info("*** AssetPaymentRule - processCustomRouteDocumentBusinessRules(Document document)");

        boolean valid = super.processCustomRouteDocumentBusinessRules(document);

        AssetPaymentDocument assetPaymentDocument = new AssetPaymentDocument();
        AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail();
        
        return valid;
        
    }

    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean valid=false;
        
        return valid;
    }

    
    /* TODO validate the following fields:
23. The SubAcct (sub-account) is not required and is validated. 
24. The SubObj (sub-object code) is not required and is validated. The SubObj is upper cased. 
25. The Project code is not required and is validated. The Project code is upper cased. 
*/    
    private boolean validateAssetPayment(Document document) {
        boolean valid=true;
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument)document;
        List<AssetPaymentDetail> assetPaymentDetails = assetPaymentDocument.getAssetPaymentDetailLines(); 
        
        
        return valid;
    }
    
    
}
