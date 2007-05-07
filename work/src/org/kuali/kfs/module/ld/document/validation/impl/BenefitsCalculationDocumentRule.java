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
package org.kuali.module.labor.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.labor.bo.BenefitsCalculation;

public class BenefitsCalculationDocumentRule extends MaintenanceDocumentRuleBase{
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BenefitsCalculationDocumentRule.class);

    private BenefitsCalculation oldBenefitsCalculation;
    private BenefitsCalculation newBenefitsCalculation;

    public BenefitsCalculationDocumentRule(){
        super();
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");

        // process rules
        checkRules(document);

        return true;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomRouteDocumentBusinessRules()");

        // process rules
        success &= checkRules(document);

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomSaveDocumentBusinessRules()");

        // process rules
        success &= checkRules(document);

        return success;
    }
    
    protected boolean checkRules(MaintenanceDocument document) {

        boolean success = true;

        /* The fringe benefit percentage cannot be equal to or over 100% */
        if(ObjectUtils.isNotNull( newBenefitsCalculation.getPositionFringeBenefitPercent()) ) {
            if(newBenefitsCalculation.getPositionFringeBenefitPercent().isGreaterEqual(new KualiDecimal(100))){
                putFieldError("positionFringeBenefitPercent", KFSKeyConstants.Labor.ERROR_FRINGE_BENEFIT_PERCENTAGE_INVALID);
                success = false;
            }
        }        
        
        return success;
    }

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    @Override
    public void setupConvenienceObjects() {

        // setup oldBenefitsCalculation convenience objects, make sure all possible sub-objects are populated
        oldBenefitsCalculation = (BenefitsCalculation) super.getOldBo();

        // setup newBenefitsCalculation convenience objects, make sure all possible sub-objects are populated
        newBenefitsCalculation = (BenefitsCalculation) super.getNewBo();
    }

}
