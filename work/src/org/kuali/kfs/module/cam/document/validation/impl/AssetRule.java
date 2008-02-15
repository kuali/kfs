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

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRule;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.module.cams.CamsConstants;

/**
 * AssetRule delegates responsibility to specific action (document type) implementations since there are
 * so many for Asset Maintenance Document.
 */
public class AssetRule extends MaintenanceDocumentRuleBase implements MaintenanceDocumentRule {
    private static final Map<String,Class> FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION = new HashMap();
    static {
        FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_TAG, AssetTagRule.class);
        FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_SEPERATE, AssetSeperateRule.class);
        FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_PAYMENT, AssetPaymentRule.class);
        FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_EDIT, AssetEditRule.class);
        FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_RETIREMENT, AssetRetireRule.class);
        FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_TRANSFER, AssetTransferRule.class);
        FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_LOAN, AssetLoanRule.class);
        FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_MERGE, AssetMergeRule.class);
    }
    private MaintenanceDocumentRule realRule;

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#setupBaseConvenienceObjects(org.kuali.core.document.MaintenanceDocument)
     */
    public void setupBaseConvenienceObjects(MaintenanceDocument document) {
        try {
            realRule = (MaintenanceDocumentRule)FINANCIAL_DOCUMENT_TYPE_TO_RULE_IMPLEMENTATION.get(document.getDocumentHeader().getFinancialDocumentTypeCode()).newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Caught exception while trying to initialize realMaintainable based on financialDocumentTypeCode: " + document.getDocumentHeader().getFinancialDocumentTypeCode(), e);
        }
    }
    
    
    // EVERYTHING BELOW IS SIMPLY DEFERRINGTO THE REAL MAINTENANCE DOCUMENT RULE
    
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#processApproveDocument(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {
        return realRule.processApproveDocument(approveEvent);
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#processRouteDocument(org.kuali.core.document.Document)
     */
    public boolean processRouteDocument(Document document) {
        return realRule.processRouteDocument(document);
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#processSaveDocument(org.kuali.core.document.Document)
     */
    public boolean processSaveDocument(Document document) {
        return realRule.processSaveDocument(document);
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#setupConvenienceObjects()
     */
    public void setupConvenienceObjects() {
        realRule.setupConvenienceObjects();
    }
}
