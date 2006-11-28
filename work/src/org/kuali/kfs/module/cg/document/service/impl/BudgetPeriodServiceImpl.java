/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.kra.budget.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.service.BudgetPeriodService;

/**
 * 
 * This class...
 * 
 * 
 */
public class BudgetPeriodServiceImpl implements BudgetPeriodService {

    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;

    /**
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.service.BudgetPeriodService#getBudgetPeriod(java.lang.Long, java.lang.Integer)
     */
    public BudgetPeriod getBudgetPeriod(String documentNumber, Integer budgetPeriodSequenceNumber) {
        return (BudgetPeriod) businessObjectService.retrieve(new BudgetPeriod(documentNumber, budgetPeriodSequenceNumber));
    }

    public BudgetPeriod getFirstBudgetPeriod(String documentNumber) {
        Map fieldValues = new HashMap();
        fieldValues.put(PropertyConstants.DOCUMENT_NUMBER, documentNumber);
        List budgetPeriodList = new ArrayList(businessObjectService.findMatchingOrderBy(BudgetPeriod.class, fieldValues, PropertyConstants.BUDGET_PERIOD_BEGIN_DATE, true));
        return (BudgetPeriod) budgetPeriodList.get(0);
    }

    public int getPeriodIndex(Integer budgetPeriodSequenceNumber, List budgetPeriodList) {
        int periodIndexNumber = -1;
        Iterator budgetPeriodListIter = budgetPeriodList.iterator();

        for (int i = 0; budgetPeriodListIter.hasNext(); i++) {
            BudgetPeriod budgetPeriod = (BudgetPeriod) budgetPeriodListIter.next();

            if (budgetPeriod.getBudgetPeriodSequenceNumber().equals(budgetPeriodSequenceNumber)) {
                periodIndexNumber = i;
                break;
            }
        }

        return periodIndexNumber;
    }

    public int getPeriodsRange(Integer budgetPeriodSequenceNumberA, Integer budgetPeriodSequenceNumberB, List budgetPeriodList) {
        int periodIndexNumberA = getPeriodIndex(budgetPeriodSequenceNumberA, budgetPeriodList);
        int periodIndexNumberB = getPeriodIndex(budgetPeriodSequenceNumberB, budgetPeriodList);

        int periodsRange = -1;

        if (periodIndexNumberA != -1 || periodIndexNumberB != -1) {
            periodsRange = periodIndexNumberB - periodIndexNumberA;
        }

        return periodsRange;
    }

    public BudgetPeriod getPeriodAfterOffset(Integer budgetPeriodSequenceNumber, int offset, List budgetPeriodList) {
        BudgetPeriod period = (BudgetPeriod) budgetPeriodList.get(getPeriodIndex(budgetPeriodSequenceNumber, budgetPeriodList) + offset);

        return period;
    }
}
