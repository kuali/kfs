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
package org.kuali.module.kra.budget.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.KraPropertyConstants;
import org.kuali.module.kra.budget.bo.AppointmentType;


/**
 * This class...
 */
public class AppointmentTypeMaintenanceDocumentRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostLookupRule.class);

    private AppointmentType appointmentType;
    
    /**
     * This method performs any necessary custom business rules on the document.
     * @param document An instance of the maintenance document that is being processed.
     * @return True if all the business rule checks succeed, false otherwise.

     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        
        setupConvenienceObjects();
        
        success &= super.processCustomSaveDocumentBusinessRules(document);
        success &= validateAppointmentTypes(appointmentType);
        
        return true;
    }
    
    private boolean validateAppointmentTypes(AppointmentType appointmentType) {
        boolean success = true;

        if (ObjectUtils.isNotNull(appointmentType.getRelatedAppointmentTypeCode()) &&  appointmentType.getRelatedAppointmentTypeCode().equals(appointmentType.getAppointmentTypeCode())) {
            String propertyName = KraPropertyConstants.DOCUMENT + "." + KraPropertyConstants.NEW_MAINTAINABLE_OBJECT + "." + "relatedAppointmentTypeCode";
            GlobalVariables.getErrorMap().putError(propertyName, KraKeyConstants.ERROR_APPOINTMENT_TYPE_RELATED_TYPE_CODE);
            success = false;
        }
        return success;
    }

    /**
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        appointmentType = (AppointmentType)super.getNewBo();
    }
    
}
