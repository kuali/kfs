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
package org.kuali.module.budget.rules;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.rules.MaintenancePreRulesBase;

/**
 * This class...
 * 
 * 
 */
public class BudgetConstructionPositionPreRules extends MaintenancePreRulesBase {


    private BudgetConstructionPosition newBudgetConstructionPosition;
    private BudgetConstructionPosition copyBudgetConstructionPosition;


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
        if ((ObjectUtils.isNotNull(newPositionStandardHoursDefault)) && 
                (ObjectUtils.isNotNull(newPositionStandardHoursDefault)) && 
                (ObjectUtils.isNotNull(newPositionStandardHoursDefault))) {

            result = BudgetConstructionPosition.getCalculatedBCPositionFTE(newPositionStandardHoursDefault, newIuNormalWorkMonths, newIuPayMonths);
            newBudgetConstructionPosition.setPositionFullTimeEquivalency(result);
        } else{
            result = new BigDecimal(0);
        }
    }
       
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newBudgetConstructionPosition = (BudgetConstructionPosition) document.getNewMaintainableObject().getBusinessObject();
        copyBudgetConstructionPosition = (BudgetConstructionPosition) ObjectUtils.deepCopy(newBudgetConstructionPosition);
        copyBudgetConstructionPosition.refresh();
    }
}
