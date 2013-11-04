/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.document.validation.impl;

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class ACIModelPreRule extends MaintenancePreRulesBase{
    private AutomatedCashInvestmentModel newACIModel;
    
    /**
     * Set value for newACIModel.
     * 
     * @param document the newACIModel Maintenance document
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newACIModel convenience objects, make sure all possible sub-objects are populated
        newACIModel = (AutomatedCashInvestmentModel) document.getNewMaintainableObject().getBusinessObject();
        newACIModel.refreshNonUpdateableReferences();
    }
    
    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {

        setupConvenienceObjects(maintenanceDocument);
        
        // Set the date of last ACI model change -- the date is the time when the maintenance doc is submitted, not
        // the time when that maintenance doc gets approved.
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        newACIModel.setDateOfLastACIModelChange(dateTimeService.getCurrentSqlDate());

        return true;
    }

}
