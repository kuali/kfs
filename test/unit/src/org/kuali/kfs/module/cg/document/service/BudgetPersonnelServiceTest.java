/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.kra.service;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.test.RequiresSpringContext;

/**
 * This class...
 * 
 * 
 */
@RequiresSpringContext
public class BudgetPersonnelServiceTest extends KualiTestBase {

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public void testPeriodFullFiscalYear() throws Exception {
        KualiDecimal periodSalary;
        BudgetUser dummyUser = new BudgetUser();
        // dummyUser.setBaseSalary(new Long(100000));

        BudgetPeriod dummyPeriod = new BudgetPeriod();
        dummyPeriod.setBudgetPeriodBeginDate(new Date(sdf.parse("07/01/2006").getTime()));
        dummyPeriod.setBudgetPeriodEndDate(new Date(sdf.parse("06/30/2007").getTime()));

        // Fiscal year is the same as the period dates
        // periodSalary =
        // budgetPersonnelService.calculatePeriodSalary(dummyUser, dummyPeriod, new KualiDecimal(3));
        // assertEquals(dummyUser.getBaseSalary(), periodSalary);

        // Fiscal year is in the future, relative to the period dates
        // periodSalary =
        // budgetPersonnelService.calculatePeriodSalary(dummyUser, dummyPeriod, new KualiDecimal(3));
        // assertEquals(new KualiDecimal(103000), periodSalary);

        // Fiscal year is in the pase, relative to the period dates
        // periodSalary = budgetPersonnelService.calculatePeriodSalary(dummyUser, dummyPeriod, new KualiDecimal(3));
        // assertEquals(new KualiDecimal(97087.38), periodSalary);
    }
}
