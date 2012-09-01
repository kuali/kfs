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

import static org.kuali.kfs.module.tem.TemPropertyConstants.TravelAgencyAuditReportFields.*;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.document.service.TravelAgencyAuditValidationHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * Business rules validation for the Travel Agency Audit and Correction using the IU method of
 * importing by trip
 *  
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class TravelAgencyAuditValidationByTrip implements TravelAgencyAuditValidationHelper {
    
    protected ExpenseImportByTripService expenseImportByTripService;
    
    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(final MaintenanceDocument document) {
        boolean result = true;
        AgencyStagingData data = (AgencyStagingData)getNewBo();
        if(this.getExpenseImportByTripService().isAccountingInfoMissing(data)) {
            putFieldError(ACCOUNTING_INFO, TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCTG_INFO);
            result &= false;
        }
        if (this.getExpenseImportByTripService().isTripDataMissing(data)) {
            putFieldError(LODGING_NUMBER, TemKeyConstants.MESSAGE_AGENCY_DATA_MISSING_TRIP_DATA);
            result &= false;
        }
        
        if (this.getExpenseImportByTripService().isDuplicate(data)) {
            putFieldError(TRIP_ID, TemKeyConstants.MESSAGE_AGENCY_DATA_DUPLICATE_RECORD);
            result &= false;
        }
        if(this.getExpenseImportByTripService().validateTripId(data) == null) {
            putFieldError(TRIP_ID, TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRIP_ID);
            result &= false;
        }
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(final MaintenanceDocument document) {
        boolean result = true;
        final AgencyStagingData data = (AgencyStagingData) getNewBo();
        if(this.getExpenseImportByTripService().isAccountingInfoMissing(data)) {
            putFieldError(ACCOUNTING_INFO, TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCTG_INFO);
            result &= false;
        }
        if (this.getExpenseImportByTripService().isTripDataMissing(data)) {
            putFieldError(LODGING_NUMBER, TemKeyConstants.MESSAGE_AGENCY_DATA_MISSING_TRIP_DATA);
            result &= false;
        }
        
        if (this.getExpenseImportByTripService().isDuplicate(data)) {
            putFieldError(TRIP_ID, TemKeyConstants.MESSAGE_AGENCY_DATA_DUPLICATE_RECORD);
            result &= false;
        }
        if(this.getExpenseImportByTripService().validateTripId(data) == null) {
            putFieldError(TRIP_ID, TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRIP_ID);
            result &= false;
        }
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomApproveDocumentBusinessRules(final MaintenanceDocument document) {
        return true;
    }

    /**
     * Gets the expenseImportByTripService attribute. 
     * @return Returns the expenseImportByTripService.
     */
    public ExpenseImportByTripService getExpenseImportByTripService() {
        return expenseImportByTripService;
    }

    /**
     * Sets the expenseImportByTripService attribute value.
     * @param expenseImportByTripService The expenseImportByTripService to set.
     */
    public void setExpenseImportByTripService(final ExpenseImportByTripService expenseImportByTripService) {
        this.expenseImportByTripService = expenseImportByTripService;
    }
    
}
