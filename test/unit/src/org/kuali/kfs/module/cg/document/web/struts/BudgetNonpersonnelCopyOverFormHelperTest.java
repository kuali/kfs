/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.web.struts.form;

import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.bo.BudgetNonpersonnel;
import org.kuali.module.kra.bo.BudgetNonpersonnelTest;
import org.kuali.module.kra.bo.BudgetPeriodTest;
import org.kuali.module.kra.service.BudgetNonpersonnelService;
import org.kuali.module.kra.web.struts.form.BudgetNonpersonnelCopyOverFormHelper.NonpersonnelCopyOverCategoryHelper;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverFormHelper.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetNonpersonnelCopyOverFormHelperTest extends KualiTestBaseWithSpring {

    private BudgetNonpersonnelService budgetNonpersonnelService;

    private BudgetForm budgetForm;

    protected void setUp() throws Exception {
        super.setUp();
        budgetForm = new BudgetForm();

        budgetNonpersonnelService = SpringServiceLocator.getBudgetNonpersonnelService();
        budgetForm.setNonpersonnelCategories(budgetNonpersonnelService.getAllNonpersonnelCategories());

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

        /** @todo Could test values next for a more exhaustive test case ... */
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

        assertEquality(new KualiDecimal(4000), nonpersonnelCopyOverCategoryHelper.getBudgetUniversityCostShareAmountTotal().get(0));
        assertEquality(new KualiDecimal(2200), nonpersonnelCopyOverCategoryHelper.getBudgetUniversityCostShareAmountTotal().get(1));
        assertEquality(new KualiDecimal(2420), nonpersonnelCopyOverCategoryHelper.getBudgetUniversityCostShareAmountTotal().get(2));
        assertEquality(new KualiDecimal(2662), nonpersonnelCopyOverCategoryHelper.getBudgetUniversityCostShareAmountTotal().get(3));
        assertEquality(new KualiDecimal(2928), nonpersonnelCopyOverCategoryHelper.getBudgetUniversityCostShareAmountTotal().get(4));

        assertEquality(new KualiDecimal(6000), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(0));
        assertEquality(new KualiDecimal(3300), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(1));
        assertEquality(new KualiDecimal(3630), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(2));
        assertEquality(new KualiDecimal(3993), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(3));
        assertEquality(new KualiDecimal(4392), nonpersonnelCopyOverCategoryHelper.getBudgetThirdPartyCostShareAmountTotal().get(4));
    }
}
