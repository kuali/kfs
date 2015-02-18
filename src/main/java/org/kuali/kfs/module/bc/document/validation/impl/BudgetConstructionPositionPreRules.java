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
package org.kuali.kfs.module.bc.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public class BudgetConstructionPositionPreRules extends MaintenancePreRulesBase {


    protected BudgetConstructionPosition newBudgetConstructionPosition;
    protected BudgetConstructionPosition copyBudgetConstructionPosition;


    public BudgetConstructionPositionPreRules() {

    }

    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);

        computeFTE();

        return true;
    }


    protected void computeFTE() {
        BigDecimal newPositionStandardHoursDefault = newBudgetConstructionPosition.getPositionStandardHoursDefault();
        Integer newIuNormalWorkMonths = newBudgetConstructionPosition.getIuNormalWorkMonths();
        Integer newIuPayMonths = newBudgetConstructionPosition.getIuPayMonths();
        BigDecimal result;
        if ((ObjectUtils.isNotNull(newPositionStandardHoursDefault)) && (ObjectUtils.isNotNull(newPositionStandardHoursDefault)) && (ObjectUtils.isNotNull(newPositionStandardHoursDefault))) {

            result = BudgetConstructionPosition.getCalculatedBCPositionFTE(newPositionStandardHoursDefault, newIuNormalWorkMonths, newIuPayMonths);
            newBudgetConstructionPosition.setPositionFullTimeEquivalency(result);
        }
        else {
            result = new BigDecimal(0);
        }
    }

    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newBudgetConstructionPosition = (BudgetConstructionPosition) document.getNewMaintainableObject().getBusinessObject();
        copyBudgetConstructionPosition = (BudgetConstructionPosition) ObjectUtils.deepCopy(newBudgetConstructionPosition);
        //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
        //BudgetConstructionPosition does not have any updatable references
        copyBudgetConstructionPosition.refreshNonUpdateableReferences();
    }
}
