/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteLanguage;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/* 
 * 
*/
public class PurchaseOrderQuoteLanguageRule extends MaintenanceDocumentRuleBase {

    protected PurchaseOrderQuoteLanguage oldQuoteLanguage;
    protected PurchaseOrderQuoteLanguage newQuoteLanguage;
    protected BusinessObjectService boService;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        oldQuoteLanguage = (PurchaseOrderQuoteLanguage) super.getOldBo();
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
        
        if (oldQuoteLanguage != null && newQuoteLanguage != null &&
            newQuoteLanguage.getPurchaseOrderQuoteLanguageIdentifier() != null && oldQuoteLanguage.getPurchaseOrderQuoteLanguageIdentifier() != null &&
            newQuoteLanguage.getPurchaseOrderQuoteLanguageCreateDate() != null && oldQuoteLanguage.getPurchaseOrderQuoteLanguageCreateDate() != null &&
            StringUtils.equalsIgnoreCase(newQuoteLanguage.getPurchaseOrderQuoteLanguageDescription(),oldQuoteLanguage.getPurchaseOrderQuoteLanguageDescription()) &&
            newQuoteLanguage.getPurchaseOrderQuoteLanguageIdentifier().equals(oldQuoteLanguage.getPurchaseOrderQuoteLanguageIdentifier()) &&
            newQuoteLanguage.getPurchaseOrderQuoteLanguageCreateDate().equals(oldQuoteLanguage.getPurchaseOrderQuoteLanguageCreateDate())){
            success = true;
        }else if (boService.countMatching(newQuoteLanguage.getClass(), fieldValues) != 0) {
            success &= false;
            putGlobalError(PurapKeyConstants.PURAP_GENERAL_POTENTIAL_DUPLICATE);
        }
        return success;
    }
}
