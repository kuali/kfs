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

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.module.kra.bo.BudgetNonpersonnel;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverBoHelper.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetNonpersonnelCopyOverBoHelperTest extends KualiTestBaseWithSpring {

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testBudgetNonpersonnelCopyOverBoHelper() {
        // BudgetNonpersonnelCopyOverBoHelper()
        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnel = new BudgetNonpersonnelCopyOverBoHelper();
        assertEquality(budgetNonpersonnel.getBudgetNonpersonnelSequenceNumber(), new Integer(-1));


        // //** BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel budgetNonpersonnel)
        budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();

        // indicators not changed when copyToFuturePeriods false
        budgetNonpersonnel.setCopyToFuturePeriods(false);
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        BudgetNonpersonnelCopyOverBoHelper newBudgetNonpersonnel1 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel);
        assertFalse(newBudgetNonpersonnel1.getAgencyCopyIndicator());
        assertFalse(newBudgetNonpersonnel1.getBudgetUniversityCostShareCopyIndicator());
        assertFalse(newBudgetNonpersonnel1.getBudgetThirdPartyCostShareCopyIndicator());

        // indicators changed when copyToFuturePeriods true
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        newBudgetNonpersonnel1 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel);
        assertTrue(newBudgetNonpersonnel1.getAgencyCopyIndicator());
        assertTrue(newBudgetNonpersonnel1.getBudgetUniversityCostShareCopyIndicator());
        assertTrue(newBudgetNonpersonnel1.getBudgetThirdPartyCostShareCopyIndicator());

        // inflation values equal to non inflation values
        assertEquality(newBudgetNonpersonnel1.getBudgetInflatedAgencyAmount(), new KualiInteger(1000));
        assertEquality(newBudgetNonpersonnel1.getBudgetInflatedUniversityCostShareAmount(), new KualiInteger(2000));
        assertEquality(newBudgetNonpersonnel1.getBudgetInflatedThirdPartyCostShareAmount(), new KualiInteger(3000));


        // //** BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel budgetNonpersonnel, int inflationLength, KualiDecimal
        // budgetNonpersonnelInflationRate)
        budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();

        // indicators false, don't modify amounts
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        BudgetNonpersonnelCopyOverBoHelper newBudgetNonpersonnel2 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, 5, new KualiDecimal(10.0));
        assertEquality(newBudgetNonpersonnel2.getAgencyRequestAmount(), new KualiInteger(1000));
        assertEquality(newBudgetNonpersonnel2.getBudgetUniversityCostShareAmount(), new KualiInteger(2000));
        assertEquality(newBudgetNonpersonnel2.getBudgetThirdPartyCostShareAmount(), new KualiInteger(3000));

        // indicators true, set amounts to 0
        budgetNonpersonnel.setAgencyCopyIndicator(true);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(true);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(true);
        newBudgetNonpersonnel2 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, 5, new KualiDecimal(10.0));
        assertEquality(newBudgetNonpersonnel2.getAgencyRequestAmount(), new KualiInteger(0));
        assertEquality(newBudgetNonpersonnel2.getBudgetUniversityCostShareAmount(), new KualiInteger(0));
        assertEquality(newBudgetNonpersonnel2.getBudgetThirdPartyCostShareAmount(), new KualiInteger(0));

        // inflation over 5 periods, 10%
        assertEquality(newBudgetNonpersonnel2.getBudgetInflatedAgencyAmount(), new KualiInteger(11274));
        assertEquality(newBudgetNonpersonnel2.getBudgetInflatedUniversityCostShareAmount(), new KualiInteger(12884));
        assertEquality(newBudgetNonpersonnel2.getBudgetInflatedThirdPartyCostShareAmount(), new KualiInteger(14495));


        // //** BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel originBudgetNonpersonnel, Integer
        // budgetPeriodSequenceNumberOverride, int inflationLength, KualiDecimal budgetNonpersonnelInflationRate)
        budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();

        // indicators not changed when copyToFuturePeriods false
        budgetNonpersonnel.setCopyToFuturePeriods(false);
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        BudgetNonpersonnelCopyOverBoHelper newBudgetNonpersonnel3 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, new Integer(11), 5, new KualiDecimal(10.0));
        assertFalse(newBudgetNonpersonnel3.getAgencyCopyIndicator());
        assertFalse(newBudgetNonpersonnel3.getBudgetUniversityCostShareCopyIndicator());
        assertFalse(newBudgetNonpersonnel3.getBudgetThirdPartyCostShareCopyIndicator());

        // indicators changed when copyToFuturePeriods true
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        newBudgetNonpersonnel3 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, new Integer(11), 5, new KualiDecimal(10.0));
        assertTrue(newBudgetNonpersonnel3.getAgencyCopyIndicator());
        assertTrue(newBudgetNonpersonnel3.getBudgetUniversityCostShareCopyIndicator());
        assertTrue(newBudgetNonpersonnel3.getBudgetThirdPartyCostShareCopyIndicator());

        // budgetPeriodSequenceNumber = budgetPeriodSequenceNumberOverride
        assertEquality(newBudgetNonpersonnel3.getBudgetPeriodSequenceNumber(), new Integer(11));

        // amounts always set to 0
        assertEquality(newBudgetNonpersonnel3.getAgencyRequestAmount(), new KualiInteger(0));
        assertEquality(newBudgetNonpersonnel3.getBudgetUniversityCostShareAmount(), new KualiInteger(0));
        assertEquality(newBudgetNonpersonnel3.getBudgetThirdPartyCostShareAmount(), new KualiInteger(0));

        // should be equal to the budgetNonpersonnelSequenceNumber
        assertEquality(newBudgetNonpersonnel3.getBudgetOriginSequenceNumber(), new Integer(173));

        // origin amounts should be equal to the amounts passed in
        assertEquality(newBudgetNonpersonnel3.getBudgetOriginAgencyAmount(), new KualiInteger(1000));
        assertEquality(newBudgetNonpersonnel3.getBudgetOriginUniversityCostShareAmount(), new KualiInteger(2000));
        assertEquality(newBudgetNonpersonnel3.getBudgetOriginThirdPartyCostShareAmount(), new KualiInteger(3000));

        // budgetNonpersonnelSequenceNumber always null
        assertNull(newBudgetNonpersonnel3.getBudgetNonpersonnelSequenceNumber());

        // inflation over 5 periods, 10%
        assertEquality(newBudgetNonpersonnel3.getBudgetInflatedAgencyAmount(), new KualiInteger(1611));
        assertEquality(newBudgetNonpersonnel3.getBudgetInflatedUniversityCostShareAmount(), new KualiInteger(3221));
        assertEquality(newBudgetNonpersonnel3.getBudgetInflatedThirdPartyCostShareAmount(), new KualiInteger(4832));
    }

    public void testGetBudgetNonpersonnel() {
        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();

        // agencyCopyIndicator = false
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        BudgetNonpersonnel newBudgetNonpersonnel1 = budgetNonpersonnel.getBudgetNonpersonnel();
        assertEquality(newBudgetNonpersonnel1.getAgencyRequestAmount(), new KualiInteger(1000));
        assertEquality(newBudgetNonpersonnel1.getBudgetUniversityCostShareAmount(), new KualiInteger(2000));
        assertEquality(newBudgetNonpersonnel1.getBudgetThirdPartyCostShareAmount(), new KualiInteger(3000));
        assertFalse(newBudgetNonpersonnel1.getCopyToFuturePeriods()); // should always be false per interface requirement

        // agencyCopyIndicator = true
        budgetNonpersonnel.setAgencyCopyIndicator(true);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(true);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(true);
        budgetNonpersonnel.setCopyToFuturePeriods(false);
        BudgetNonpersonnel newBudgetNonpersonnel2 = budgetNonpersonnel.getBudgetNonpersonnel();
        assertEquality(newBudgetNonpersonnel2.getAgencyRequestAmount(), new KualiInteger(4000));
        assertEquality(newBudgetNonpersonnel2.getBudgetUniversityCostShareAmount(), new KualiInteger(5000));
        assertEquality(newBudgetNonpersonnel2.getBudgetThirdPartyCostShareAmount(), new KualiInteger(6000));
        assertFalse(newBudgetNonpersonnel2.getCopyToFuturePeriods()); // should always be false per interface requirement

        // KULERA-491 bug: getBudgetNonpersonnel doesn't return object of type gBudgetNonpersonnel which confused OJB.
        boolean exceptionThrown = false;
        try {
            BudgetNonpersonnelCopyOverBoHelper newBudgetNonpersonnel3 = (BudgetNonpersonnelCopyOverBoHelper) budgetNonpersonnel.getBudgetNonpersonnel();
        }
        catch (ClassCastException e) {
            exceptionThrown = true;
        }
        assertTrue("BudgetNonpersonnel.getBudgetNonpersonnel should not return an object of type BudgetNonpersonnelCopyOverBoHelper because " + "that confuses OJB. It can only store BudgetNonpersonnel.", exceptionThrown);
    }

    /**
     * Helper to give a representation of a re-occuring BudgetNonpersonnelCopyOverBoHelper object. Note this may not be a valid
     * representation of how the object looks like in production. The reason for that is that it's easier to test if I fudge some of
     * the data (ie. inflation amounts are not actually the inflated amounts, if they are it's harder for me to tell if they are
     * indeed properly inflated).
     * 
     * @return
     */
    private BudgetNonpersonnelCopyOverBoHelper createBudgetNonpersonnelCopyOverBoHelper() {
        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnel = new BudgetNonpersonnelCopyOverBoHelper();

        budgetNonpersonnel.setBudgetNonpersonnelSequenceNumber(new Integer(173));

        budgetNonpersonnel.setAgencyRequestAmount(new KualiInteger(1000));
        budgetNonpersonnel.setBudgetUniversityCostShareAmount(new KualiInteger(2000));
        budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(new KualiInteger(3000));

        budgetNonpersonnel.setBudgetInflatedAgencyAmount(new KualiInteger(4000));
        budgetNonpersonnel.setBudgetInflatedUniversityCostShareAmount(new KualiInteger(5000));
        budgetNonpersonnel.setBudgetInflatedThirdPartyCostShareAmount(new KualiInteger(6000));

        budgetNonpersonnel.setBudgetOriginAgencyAmount(new KualiInteger(7000));
        budgetNonpersonnel.setBudgetOriginUniversityCostShareAmount(new KualiInteger(8000));
        budgetNonpersonnel.setBudgetOriginThirdPartyCostShareAmount(new KualiInteger(9000));

        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);

        budgetNonpersonnel.setCopyToFuturePeriods(true);

        return budgetNonpersonnel;
    }
}
