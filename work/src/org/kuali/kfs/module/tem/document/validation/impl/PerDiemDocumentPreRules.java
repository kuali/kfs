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

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Business Prerules applicable to Per Diem documents. These PreRules checks that the Per Diem totals match.
 */
public class PerDiemDocumentPreRules extends MaintenancePreRulesBase {


    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PerDiemDocumentPreRules.class);

    protected PerDiem newPerDiem;

    public PerDiemDocumentPreRules() {
    }

    /**
     * Sets up a convenience object and few other vendor attributes
     * 
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkTotals(document);
        return true;
    }

    /**
     * Sets the convenience objects like newPerDiem and oldPerDiem, so you have short and easy handles to the new and old
     * objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load all
     * sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    protected void setupConvenienceObjects(MaintenanceDocument document) {
        // setup newPerDiem convenience objects, make sure all possible sub-objects are populated
        newPerDiem = (PerDiem) document.getNewMaintainableObject().getBusinessObject();
    }

    /**
     * Checks if the B+L+D+IE total does not match Meal+Incidentals total
     * 
     * @param document - per diem document
     */
    public void checkTotals(Document document) {
        boolean proceed = true;
        
        KualiDecimal total = new KualiDecimal(newPerDiem.getBreakfast());
        total = total.add(new KualiDecimal(newPerDiem.getLunch()));
        total = total.add(new KualiDecimal(newPerDiem.getDinner()));
        total = total.add(newPerDiem.getIncidentals());
        
        if(!total.equals(newPerDiem.getMealsAndIncidentals()))
        {
            proceed = askOrAnalyzeYesNoQuestion("mealsAndIncidentals", "Warning: Breakfast, Lunch, Dinner, and Incidentals total do not match Meals and Incidentals.  Would you like to proceed anyway?");
            if (!proceed) {
                abortRulesCheck();
            }
        }
    }
}
