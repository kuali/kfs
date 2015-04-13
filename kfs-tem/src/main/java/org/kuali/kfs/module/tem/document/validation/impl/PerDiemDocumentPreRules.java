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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.document.Document;

/**
 * Business Prerules applicable to Per Diem documents. These PreRules checks that the Per Diem totals match.
 */
public class PerDiemDocumentPreRules extends MaintenancePreRulesBase {


    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PerDiemDocumentPreRules.class);

    protected PerDiem newPerDiem;

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

        KualiDecimal total = newPerDiem.getBreakfast();
        total = total.add(newPerDiem.getLunch());
        total = total.add(newPerDiem.getDinner());
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
