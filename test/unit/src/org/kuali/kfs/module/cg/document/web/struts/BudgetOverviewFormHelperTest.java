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

import org.kuali.core.util.KualiInteger;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests methods in BudgetOverviewFormHelper.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetOverviewFormHelperTest extends KualiTestBaseWithSpring {

    protected void setUp() throws Exception {
        super.setUp();
    }

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

        budgetOverviewFormHelper.setPersonnelSalaryAgencyRequest(new KualiInteger(0));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsAgencyRequest(new KualiInteger(0));
        assertEquals("0 + 0 = 0", budgetOverviewFormHelper.getTotalPersonnelAgencyRequest(), new KualiInteger(0));

        budgetOverviewFormHelper.setPersonnelSalaryAgencyRequest(new KualiInteger(5));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsAgencyRequest(new KualiInteger(-15));
        assertEquals("5 - 15 = -10", budgetOverviewFormHelper.getTotalPersonnelAgencyRequest(), new KualiInteger(-10));

        budgetOverviewFormHelper.setPersonnelSalaryAgencyRequest(new KualiInteger(50000));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsAgencyRequest(new KualiInteger(25000));
        assertEquals("50000 + 50000 = 75000", budgetOverviewFormHelper.getTotalPersonnelAgencyRequest(), new KualiInteger(75000));
    }

    public void testGetTotalPersonnelInstitutionCostShare() {
        BudgetOverviewFormHelper budgetOverviewFormHelper = new BudgetOverviewFormHelper();

        budgetOverviewFormHelper.setPersonnelSalaryInstitutionCostShare(new KualiInteger(0));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsInstitutionCostShare(new KualiInteger(0));
        assertEquals("0 + 0 = 0", budgetOverviewFormHelper.getTotalPersonnelInstitutionCostShare(), new KualiInteger(0));

        budgetOverviewFormHelper.setPersonnelSalaryInstitutionCostShare(new KualiInteger(5));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsInstitutionCostShare(new KualiInteger(-15));
        assertEquals("5 - 15 = -10", budgetOverviewFormHelper.getTotalPersonnelInstitutionCostShare(), new KualiInteger(-10));

        budgetOverviewFormHelper.setPersonnelSalaryInstitutionCostShare(new KualiInteger(50000));
        budgetOverviewFormHelper.setPersonnelFringeBenefitsInstitutionCostShare(new KualiInteger(25000));
        assertEquals("50000 + 50000 = 75000", budgetOverviewFormHelper.getTotalPersonnelInstitutionCostShare(), new KualiInteger(75000));
    }
}
