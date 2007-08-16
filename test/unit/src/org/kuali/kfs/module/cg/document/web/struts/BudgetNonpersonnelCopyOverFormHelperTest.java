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

import static org.kuali.test.util.KualiTestAssertionUtils.assertEquality;

import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.kra.bo.BudgetNonpersonnelTest;
import org.kuali.module.kra.bo.BudgetPeriodTest;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.service.BudgetNonpersonnelService;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverFormHelper.NonpersonnelCopyOverCategoryHelper;
import org.kuali.test.ConfigureContext;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverFormHelper.
 * 
 * 
 */
@ConfigureContext
public class BudgetNonpersonnelCopyOverFormHelperTest extends KualiTestBase {


    private BudgetForm budgetForm;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        budgetForm = new BudgetForm();

        budgetForm.setNonpersonnelCategories(SpringContext.getBean(BudgetNonpersonnelService.class).getAllNonpersonnelCategories());

        String[] categories = { "CO", "CO", "FL" };
        String[] subCategories = { "C1", "C1", "F5" };
        String[] subcontractorNumber = { "", "", "" };
        budgetForm.getBudgetDocument().getBudget().setNonpersonnelItems(BudgetNonpersonnelTest.createBudgetNonpersonnel(categories, subCategories, subcontractorNumber));
        budgetForm.setCurrentTaskNumber(new Integer(0));
        budgetForm.getBudgetDocument().getBudget().setPeriods(BudgetPeriodTest.createBudgetPeriods(5));
        budgetForm.getBudgetDocument().getBudget().setBudgetNonpersonnelInflationRate(new KualiDecimal(10));
    }

    public void testBudgetNonpersonnelCopyOverFormHelper() {
        BudgetNonpersonnelCopyOverFormHelper budgetNonpersonnelCopyOverFormHelper1 = new BudgetNonpersonnelCopyOverFormHelper();
        assertTrue("should have empty nonpersonnelCopyOverCategoryHelpers so that hidden variables can populate it", budgetNonpersonnelCopyOverFormHelper1.getNonpersonnelCopyOverCategoryHelpers().isEmpty());

        BudgetNonpersonnelCopyOverFormHelper budgetNonpersonnelCopyOverFormHelper2 = new BudgetNonpersonnelCopyOverFormHelper(budgetForm);

        assertNotNull("Should not be null after construction.", budgetNonpersonnelCopyOverFormHelper2);
        assertNotNull("Should not be null after construction.", budgetNonpersonnelCopyOverFormHelper2.getNonpersonnelCopyOverCategoryHelpers());
        // Further tests are easier done after decontruction. The important part here is that it doesn't crash.
    }

    public void testDeconstruct() {
        BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) budgetForm.getBudgetDocument().getBudget().getNonpersonnelItem(0);
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        budgetNonpersonnel = (BudgetNonpersonnel) budgetForm.getBudgetDocument().getBudget().getNonpersonnelItem(2);
        budgetNonpersonnel.setCopyToFuturePeriods(true);

        BudgetNonpersonnelCopyOverFormHelper budgetNonpersonnelCopyOverFormHelper1 = new BudgetNonpersonnelCopyOverFormHelper(budgetForm);
        budgetNonpersonnelCopyOverFormHelper1.deconstruct(budgetForm);

        List budgetNonpersonnelList = budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems();
        assertTrue("Incorrect number of items found after copy over.", budgetNonpersonnelList.size() == 11);

        // Could test values next for a more exhaustive test case, but this covers the basics for now.
    }

    public void testRefresh() {
        this.testDeconstruct(); // this will leave budgetForm with interesting data to refresh.

        BudgetNonpersonnelCopyOverFormHelper budgetNonpersonnelCopyOverFormHelper1 = new BudgetNonpersonnelCopyOverFormHelper(budgetForm);

        // note that BudgetNonpersonnelCopyOverFormHelper constructor already calls refresh, so calling it again is kind of mute,
        // but it shouldn't hurt either. For any chance: We're interested in testing the totals.
        budgetNonpersonnelCopyOverFormHelper1.refresh(budgetForm.getBudgetDocument().getBudget().getPeriods().size());

        NonpersonnelCopyOverCategoryHelper nonpersonnelCopyOverCategoryHelper = (NonpersonnelCopyOverCategoryHelper) budgetNonpersonnelCopyOverFormHelper1.getNonpersonnelCopyOverCategoryHelpers().get("CO");

        assertEquality(new KualiDecimal(2000), nonpersonnelCopyOverCategoryHelper.getAgencyRequestAmountTotal().get(0));
        assertEquality(new KualiDecimal(1100), nonpersonnelCopyOverCategoryHelper.getAgencyRequestAmountTotal().get(1));
        assertEquality(new KualiDecimal(1210), nonpersonnelCopyOverCategoryHelper.getAgencyRequestAmountTotal().get(2));
        assertEquality(new KualiDecimal(1331), nonpersonnelCopyOverCategoryHelper.getAgencyRequestAmountTotal().get(3));
        assertEquality(new KualiDecimal(1464), nonpersonnelCopyOverCategoryHelper.getAgencyRequestAmountTotal().get(4));

        assertEquality(new KualiDecimal(4000), nonpersonnelCopyOverCategoryHelper.getBudgetInstitutionCostShareAmountTotal().get(0));
        assertEquality(new KualiDecimal(2200), nonpersonnelCopyOverCategoryHelper.getBudgetInstitutionCostShareAmountTotal().get(1));
        assertEquality(new KualiDecimal(2420), nonpersonnelCopyOverCategoryHelper.getBudgetInstitutionCostShareAmountTotal().get(2));
        assertEquality(new KualiDecimal(2662), nonpersonnelCopyOverCategoryHelper.getBudgetInstitutionCostShareAmountTotal().get(3));
        assertEquality(new KualiDecimal(2928), nonpersonnelCopyOverCategoryHelper.getBudgetInstitutionCostShareAmountTotal().get(4));

        assertEquality(new KualiDecimal(6000), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(0));
        assertEquality(new KualiDecimal(3300), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(1));
        assertEquality(new KualiDecimal(3630), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(2));
        assertEquality(new KualiDecimal(3993), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(3));
        assertEquality(new KualiDecimal(4392), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(4));
    }
}