/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.bo;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.test.ConfigureContext;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverFormHelper.
 */
@ConfigureContext
public class BudgetPeriodTest extends KualiTestBase {

    public void testBudgetPeriod() {
        assertTrue(true);
    }

    public static List createBudgetPeriods(int numberOfPeriods) {
        List budgetPeriods = new ArrayList();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        for (int i = 0; i < numberOfPeriods; i++) {
            BudgetPeriod budgetPeriod = new BudgetPeriod();
            budgetPeriod.setBudgetPeriodSequenceNumber(new Integer(i));
            try {
                budgetPeriod.setBudgetPeriodBeginDate(new Date(sdf.parse("07/01/" + 2000 + i).getTime()));
                budgetPeriod.setBudgetPeriodEndDate(new Date(sdf.parse("06/30/" + 2001 + i).getTime()));
            }
            catch (ParseException e) {
                throw new NullPointerException();
            }
            budgetPeriods.add(budgetPeriod);
        }

        return budgetPeriods;
    }
}
