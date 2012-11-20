/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.document.service.TravelAgencyAuditValidationHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * Business rules validation for the Travel Agency Audit and Correction
 */
public class TravelAgencyAuditValidation extends MaintenanceDocumentRuleBase {
    
    protected TravelAgencyAuditValidationHelper validationByTripHelper;
    protected TravelAgencyAuditValidationHelper validationByTravelerHelper;
    
    public TravelAgencyAuditValidation() {
        setValidationByTripHelper((TravelAgencyAuditValidationHelper) SpringContext.getBean(TravelAgencyAuditValidationByTrip.class));
        setValidationByTravelerHelper((TravelAgencyAuditValidationHelper) SpringContext.getBean(TravelAgencyAuditValidationByTraveler.class));
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomSaveDocumentBusinessRules(document);
        result &= getValidationHelper().processCustomSaveDocumentBusinessRules(document);
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);
        result &= getValidationHelper().processCustomRouteDocumentBusinessRules(document);
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {        
        boolean result = super.processCustomApproveDocumentBusinessRules(document);
        result &= getValidationHelper().processCustomApproveDocumentBusinessRules(document);
        return result;
    }
    
    protected TravelAgencyAuditValidationHelper getValidationHelper() {
        final AgencyStagingData data = (AgencyStagingData) getNewBo();
        if (data.getImportBy().equals(ExpenseImportTypes.IMPORT_BY_TRAVELLER)) {
            return getValidationByTravelerHelper();
        }
        return getValidationByTripHelper();
    }

    /**
     * Gets the validationByTravelerHelper attribute. 
     * @return Returns the validationByTravelerHelper.
     */
    public TravelAgencyAuditValidationHelper getValidationByTravelerHelper() {
        return validationByTravelerHelper;
    }

    /**
     * Sets the validationByTravelerHelper attribute value.
     * @param validationByTravelerHelper The validationByTravelerHelper to set.
     */
    public void setValidationByTravelerHelper(final TravelAgencyAuditValidationHelper validationByTravelerHelper) {
        this.validationByTravelerHelper = validationByTravelerHelper;
    }

    /**
     * Gets the validationByTripHelper attribute. 
     * @return Returns the validationByTripHelper.
     */
    public TravelAgencyAuditValidationHelper getValidationByTripHelper() {
        return validationByTripHelper;
    }

    /**
     * Sets the validationByTripHelper attribute value.
     * @param validationByTripHelper The validationByTripHelper to set.
     */
    public void setValidationByTripHelper(final TravelAgencyAuditValidationHelper validationByTripHelper) {
        this.validationByTripHelper = validationByTripHelper;
    }

}
