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
package org.kuali.module.kra.web.struts.form;

import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;
import org.kuali.test.ConfigureContext;

/**
 * This class tests methods in BudgetOverviewFormHelper.
 */
@ConfigureContext
public class BudgetOverviewFormHelperTest extends KualiTestBase {

    public void testBudgetOverviewFormHelper() {
        BudgetOverviewFormHelper budgetOverviewFormHelper1 = new BudgetOverviewFormHelper();
        assertNotNull("Shouldn't be null.", budgetOverviewFormHelper1);

        /**
         * @todo KULERA-428: Better to use database values instead of mock here? Mock objects is turning really ugly at this
         *       point...
         */
    }

    public void testGetTotalPersonnelAgencyRequest() {
        BudgetOverviewFormHelper budgetOverviewFormHelper = new BudgetOverviewFormHelper();

        budgetOverviewFormHelper.setPersonnelSalaryAgencyRequest(KualiInteger.ZERO);
        budgetOverviewFormHelper.setPersonnelFringeBenefitsAgencyRequest(KualiInteger.ZERO);
        assertEquals("0 + 0 = 0", budgetOverviewFormHelper.getTotalPersonnelAgencyRequest(), KualiInteger.ZERO);

        budgetOverviewFormHelper.setPersonnelSalaryAgencyRequest(new KualiInteger(5));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsAgencyRequest(new KualiInteger(-15));
        assertEquals("5 - 15 = -10", budgetOverviewFormHelper.getTotalPersonnelAgencyRequest(), new KualiInteger(-10));

        budgetOverviewFormHelper.setPersonnelSalaryAgencyRequest(new KualiInteger(50000));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsAgencyRequest(new KualiInteger(25000));
        assertEquals("50000 + 50000 = 75000", budgetOverviewFormHelper.getTotalPersonnelAgencyRequest(), new KualiInteger(75000));
    }

    public void testGetTotalPersonnelInstitutionCostShare() {
        BudgetOverviewFormHelper budgetOverviewFormHelper = new BudgetOverviewFormHelper();

        budgetOverviewFormHelper.setPersonnelSalaryInstitutionCostShare(KualiInteger.ZERO);
        budgetOverviewFormHelper.setPersonnelFringeBenefitsInstitutionCostShare(KualiInteger.ZERO);
        assertEquals("0 + 0 = 0", budgetOverviewFormHelper.getTotalPersonnelInstitutionCostShare(), KualiInteger.ZERO);

        budgetOverviewFormHelper.setPersonnelSalaryInstitutionCostShare(new KualiInteger(5));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsInstitutionCostShare(new KualiInteger(-15));
        assertEquals("5 - 15 = -10", budgetOverviewFormHelper.getTotalPersonnelInstitutionCostShare(), new KualiInteger(-10));

        budgetOverviewFormHelper.setPersonnelSalaryInstitutionCostShare(new KualiInteger(50000));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsInstitutionCostShare(new KualiInteger(25000));
        assertEquals("50000 + 50000 = 75000", budgetOverviewFormHelper.getTotalPersonnelInstitutionCostShare(), new KualiInteger(75000));
    }
}
