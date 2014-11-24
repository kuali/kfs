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
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderContractLanguage;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Business rule(s) applicable to Purchase Order Contract Language maintenance document.
 */
public class PurchaseOrderContractLanguageRule extends MaintenanceDocumentRuleBase {

    protected PurchaseOrderContractLanguage newContractLanguage;
    protected PurchaseOrderContractLanguage oldContractLanguage;
    protected BusinessObjectService boService;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        oldContractLanguage = (PurchaseOrderContractLanguage) super.getOldBo();
        // setup newDelegateChange convenience objects, make sure all possible sub-objects are populated
        newContractLanguage = (PurchaseOrderContractLanguage) super.getNewBo();
        boService = (BusinessObjectService) super.getBoService();
        super.setupConvenienceObjects();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }

    /**
     * Check to see if data duplicates existing data
     * 
     * @return boolean indicating if validation succeeded
     */
    protected boolean checkForDuplicate() {
        LOG.info("checkForDuplicate called");
        boolean success = true;
        Map fieldValues = new HashMap();
        
        if (oldContractLanguage.getPurchaseOrderContractLanguageIdentifier() != null &&
            newContractLanguage.getPurchaseOrderContractLanguageIdentifier() != null &&
            StringUtils.equalsIgnoreCase(newContractLanguage.getCampusCode(),oldContractLanguage.getCampusCode()) &&
            newContractLanguage.getPurchaseOrderContractLanguageIdentifier().equals(oldContractLanguage.getPurchaseOrderContractLanguageIdentifier()) &&
            StringUtils.equalsIgnoreCase(newContractLanguage.getPurchaseOrderContractLanguageDescription(),oldContractLanguage.getPurchaseOrderContractLanguageDescription()) &&
            newContractLanguage.getContractLanguageCreateDate().equals(oldContractLanguage.getContractLanguageCreateDate())){
            success = true;
        }else{
            fieldValues.put("campusCode", newContractLanguage.getCampusCode());
            fieldValues.put("purchaseOrderContractLanguageDescription", newContractLanguage.getPurchaseOrderContractLanguageDescription());
            fieldValues.put("contractLanguageCreateDate", newContractLanguage.getContractLanguageCreateDate());
            if (boService.countMatching(newContractLanguage.getClass(), fieldValues) != 0) {
                success &= false;
                putGlobalError(PurapKeyConstants.PURAP_GENERAL_POTENTIAL_DUPLICATE);
            }
        }
        return success;
    }
}
