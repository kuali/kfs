/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.PurchaseOrderQuoteLanguage;

/* 
 * THIS CODE IS NOT USED IN RELEASE 2 BUT THE CODE WAS LEFT IN TO
 * FACILITATE TURNING IT BACK ON EARLY IN THE DEVELOPMENT CYCLE OF RELEASE 3.
*/
public class PurchaseOrderQuoteLanguageRule extends MaintenanceDocumentRuleBase {

    private PurchaseOrderQuoteLanguage newQuoteLanguage;
    private BusinessObjectService boService;

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // setup newDelegateChange convenience objects, make sure all possible sub-objects are populated
        newQuoteLanguage = (PurchaseOrderQuoteLanguage) super.getNewBo();
        boService = (BusinessObjectService) super.getBoService();
        super.setupConvenienceObjects();
    }

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }

    protected boolean checkForDuplicate() {
        LOG.info("checkForDuplicate called");
        boolean success = true;
        Map fieldValues = new HashMap();
        fieldValues.put("purchaseOrderQuoteLanguageDescription", newQuoteLanguage.getPurchaseOrderQuoteLanguageDescription());
        fieldValues.put("purchaseOrderQuoteLanguageCreateDate", newQuoteLanguage.getPurchaseOrderQuoteLanguageCreateDate());
        if (boService.countMatching(newQuoteLanguage.getClass(), fieldValues) != 0) {
            success &= false;
            putGlobalError(PurapKeyConstants.PURAP_GENERAL_POTENTIAL_DUPLICATE);
        }
        return success;
    }
}
