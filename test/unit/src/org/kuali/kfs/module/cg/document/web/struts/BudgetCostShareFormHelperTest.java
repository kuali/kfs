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

import org.kuali.core.util.KualiInteger;
import org.kuali.module.kra.bo.BudgetNonpersonnel;
import org.kuali.module.kra.bo.BudgetNonpersonnelTest;
import org.kuali.module.kra.bo.BudgetPeriod;
import org.kuali.module.kra.bo.BudgetPeriodTest;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests methods in BudgetOverviewFormHelper.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetCostShareFormHelperTest extends KualiTestBaseWithSpring {

    BudgetCostShareFormHelper budgetCostShareFormHelper;
    List<BudgetPeriod> periods;
    List<BudgetNonpersonnel> nonpersonnelItems;

    protected void setUp() throws Exception {
        super.setUp();

        budgetCostShareFormHelper = new BudgetCostShareFormHelper();
        periods = BudgetPeriodTest.createBudgetPeriods(2);

        String[] categories = { "CO", "CO", "SC", "SC" };
        String[] subCategories = { "C1", "C1", "R3", "R1" };
        String[] subcontractorNumber = { "", "", "1", "2" };
        nonpersonnelItems = BudgetNonpersonnelTest.createBudgetNonpersonnel(categories, subCategories, subcontractorNumber);
    }

    public void testSetupSubcontractors() {
        budgetCostShareFormHelper.setupSubcontractors(periods, nonpersonnelItems);

        List<BudgetCostShareFormHelper.Subcontractor> subcontractors = budgetCostShareFormHelper.getSubcontractors();

        assertTrue("subcontractors.size() == 2", subcontractors.size() == 2);

        for (BudgetCostShareFormHelper.Subcontractor subcontractor : subcontractors) {
            assertEquals("totalPeriodAmount = 3000", subcontractor.getTotalPeriodAmount(), new KualiInteger(3000));

            KualiInteger[] periodAmounts = subcontractor.getPeriodAmounts();
            assertEquals("periodAmounts[0] = 3000", periodAmounts[0], new KualiInteger(3000));
            assertEquals("periodAmounts[1] = 0", periodAmounts[1], new KualiInteger(0));
        }

        // This test does not do any aggregation (hit Subcontractor.addPeriodAmount). That is not very good but in the
        // interest of time I'm omitting that. BudgetNonpersonnelCopyOverFormHelperTest could possibly be used for that
        // although it wouldn't be trivial...
    }

    public void testSetupTotals() {
        /** @TODO */
        assertTrue("TODO: Write JUnit test case", true);
    }
}
