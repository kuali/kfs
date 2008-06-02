/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.PurchaseOrderQuoteList;

/**
 * Business rule(s) applicable to Purchase Order Contract Language maintenance document.
 */
public class PurchaseOrderQuoteListRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.validateVendor();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.validateVendor();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.validateVendor();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }

    /**
     * Check to see if data duplicates existing data
     * 
     * @return boolean indicating if validation succeeded
     */
    protected boolean validateVendor() {
        LOG.info("validateVendor called");
        boolean success = true;
        
        PurchaseOrderQuoteList newBo = (PurchaseOrderQuoteList)super.getNewBo();
        if (newBo.getQuoteListVendors() == null || newBo.getQuoteListVendors().size() == 0) {
            success = false;
            putGlobalError(PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_LIST_NO_VENDOR);
        }
        return success;
    }
}
