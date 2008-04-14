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

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverBoHelper;
import org.kuali.test.ConfigureContext;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverBoHelper.
 */
@ConfigureContext
public class BudgetNonpersonnelCopyOverBoHelperTest extends KualiTestBase {

    public void testBudgetNonpersonnelCopyOverBoHelper() {
        // BudgetNonpersonnelCopyOverBoHelper()
        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnel = new BudgetNonpersonnelCopyOverBoHelper();
        assertEquality(budgetNonpersonnel.getBudgetNonpersonnelSequenceNumber(), new Integer(-1));


        // //** BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel budgetNonpersonnel)
        budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();

        // indicators not changed when copyToFuturePeriods false
        budgetNonpersonnel.setCopyToFuturePeriods(false);
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetInstitutionCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        BudgetNonpersonnelCopyOverBoHelper newBudgetNonpersonnel1 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel);
        assertFalse(newBudgetNonpersonnel1.getAgencyCopyIndicator());
        assertFalse(newBudgetNonpersonnel1.getBudgetInstitutionCostShareCopyIndicator());
        assertFalse(newBudgetNonpersonnel1.getBudgetThirdPartyCostShareCopyIndicator());

        // indicators changed when copyToFuturePeriods true
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        newBudgetNonpersonnel1 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel);
        assertTrue(newBudgetNonpersonnel1.getAgencyCopyIndicator());
        assertTrue(newBudgetNonpersonnel1.getBudgetInstitutionCostShareCopyIndicator());
        assertTrue(newBudgetNonpersonnel1.getBudgetThirdPartyCostShareCopyIndicator());

        // inflation values equal to non inflation values
        assertEquality(newBudgetNonpersonnel1.getBudgetInflatedAgencyAmount(), new KualiInteger(1000));
        assertEquality(newBudgetNonpersonnel1.getBudgetInflatedInstitutionCostShareAmount(), new KualiInteger(2000));
        assertEquality(newBudgetNonpersonnel1.getBudgetInflatedThirdPartyCostShareAmount(), new KualiInteger(3000));


        // //** BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel budgetNonpersonnel, int inflationLength, KualiDecimal
        // budgetNonpersonnelInflationRate)
        budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();

        // indicators false, don't modify amounts
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetInstitutionCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        BudgetNonpersonnelCopyOverBoHelper newBudgetNonpersonnel2 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, 5, new KualiDecimal(10.0));
        assertEquality(newBudgetNonpersonnel2.getAgencyRequestAmount(), new KualiInteger(1000));
        assertEquality(newBudgetNonpersonnel2.getBudgetInstitutionCostShareAmount(), new KualiInteger(2000));
        assertEquality(newBudgetNonpersonnel2.getBudgetThirdPartyCostShareAmount(), new KualiInteger(3000));

        // indicators true, set amounts to 0
        budgetNonpersonnel.setAgencyCopyIndicator(true);
        budgetNonpersonnel.setBudgetInstitutionCostShareCopyIndicator(true);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(true);
        newBudgetNonpersonnel2 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, 5, new KualiDecimal(10.0));
        assertEquality(newBudgetNonpersonnel2.getAgencyRequestAmount(), KualiInteger.ZERO);
        assertEquality(newBudgetNonpersonnel2.getBudgetInstitutionCostShareAmount(), KualiInteger.ZERO);
        assertEquality(newBudgetNonpersonnel2.getBudgetThirdPartyCostShareAmount(), KualiInteger.ZERO);

        // inflation over 5 periods, 10%
        assertEquality(newBudgetNonpersonnel2.getBudgetInflatedAgencyAmount(), new KualiInteger(11274));
        assertEquality(newBudgetNonpersonnel2.getBudgetInflatedInstitutionCostShareAmount(), new KualiInteger(12884));
        assertEquality(newBudgetNonpersonnel2.getBudgetInflatedThirdPartyCostShareAmount(), new KualiInteger(14495));


        // //** BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel originBudgetNonpersonnel, Integer
        // budgetPeriodSequenceNumberOverride, int inflationLength, KualiDecimal budgetNonpersonnelInflationRate)
        budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();

        // indicators not changed when copyToFuturePeriods false
        budgetNonpersonnel.setCopyToFuturePeriods(false);
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetInstitutionCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        BudgetNonpersonnelCopyOverBoHelper newBudgetNonpersonnel3 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, new Integer(11), 5, new KualiDecimal(10.0));
        assertFalse(newBudgetNonpersonnel3.getAgencyCopyIndicator());
        assertFalse(newBudgetNonpersonnel3.getBudgetInstitutionCostShareCopyIndicator());
        assertFalse(newBudgetNonpersonnel3.getBudgetThirdPartyCostShareCopyIndicator());

        // indicators changed when copyToFuturePeriods true
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        newBudgetNonpersonnel3 = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, new Integer(11), 5, new KualiDecimal(10.0));
        assertTrue(newBudgetNonpersonnel3.getAgencyCopyIndicator());
        assertTrue(newBudgetNonpersonnel3.getBudgetInstitutionCostShareCopyIndicator());
        assertTrue(newBudgetNonpersonnel3.getBudgetThirdPartyCostShareCopyIndicator());

        // budgetPeriodSequenceNumber = budgetPeriodSequenceNumberOverride
        assertEquality(newBudgetNonpersonnel3.getBudgetPeriodSequenceNumber(), new Integer(11));

        // amounts always set to 0
        assertEquality(newBudgetNonpersonnel3.getAgencyRequestAmount(), KualiInteger.ZERO);
        assertEquality(newBudgetNonpersonnel3.getBudgetInstitutionCostShareAmount(), KualiInteger.ZERO);
        assertEquality(newBudgetNonpersonnel3.getBudgetThirdPartyCostShareAmount(), KualiInteger.ZERO);

        // should be equal to the budgetNonpersonnelSequenceNumber
        assertEquality(newBudgetNonpersonnel3.getBudgetOriginSequenceNumber(), new Integer(173));

        // origin amounts should be equal to the amounts passed in
        assertEquality(newBudgetNonpersonnel3.getBudgetOriginAgencyAmount(), new KualiInteger(1000));
        assertEquality(newBudgetNonpersonnel3.getBudgetOriginInstitutionCostShareAmount(), new KualiInteger(2000));
        assertEquality(newBudgetNonpersonnel3.getBudgetOriginThirdPartyCostShareAmount(), new KualiInteger(3000));

        // budgetNonpersonnelSequenceNumber always null
        assertNull(newBudgetNonpersonnel3.getBudgetNonpersonnelSequenceNumber());

        // inflation over 5 periods, 10%
        assertEquality(newBudgetNonpersonnel3.getBudgetInflatedAgencyAmount(), new KualiInteger(1611));
        assertEquality(newBudgetNonpersonnel3.getBudgetInflatedInstitutionCostShareAmount(), new KualiInteger(3221));
        assertEquality(newBudgetNonpersonnel3.getBudgetInflatedThirdPartyCostShareAmount(), new KualiInteger(4832));
    }

    public void testGetBudgetNonpersonnel() {
        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();

        // agencyCopyIndicator = false
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetInstitutionCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        BudgetNonpersonnel newBudgetNonpersonnel1 = budgetNonpersonnel.getBudgetNonpersonnel();
        assertEquality(newBudgetNonpersonnel1.getAgencyRequestAmount(), new KualiInteger(1000));
        assertEquality(newBudgetNonpersonnel1.getBudgetInstitutionCostShareAmount(), new KualiInteger(2000));
        assertEquality(newBudgetNonpersonnel1.getBudgetThirdPartyCostShareAmount(), new KualiInteger(3000));
        assertFalse(newBudgetNonpersonnel1.getCopyToFuturePeriods()); // should always be false per interface requirement

        // agencyCopyIndicator = true
        budgetNonpersonnel.setAgencyCopyIndicator(true);
        budgetNonpersonnel.setBudgetInstitutionCostShareCopyIndicator(true);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(true);
        budgetNonpersonnel.setCopyToFuturePeriods(false);
        BudgetNonpersonnel newBudgetNonpersonnel2 = budgetNonpersonnel.getBudgetNonpersonnel();
        assertEquality(newBudgetNonpersonnel2.getAgencyRequestAmount(), new KualiInteger(4000));
        assertEquality(newBudgetNonpersonnel2.getBudgetInstitutionCostShareAmount(), new KualiInteger(5000));
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
        budgetNonpersonnel.setBudgetInstitutionCostShareAmount(new KualiInteger(2000));
        budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(new KualiInteger(3000));

        budgetNonpersonnel.setBudgetInflatedAgencyAmount(new KualiInteger(4000));
        budgetNonpersonnel.setBudgetInflatedInstitutionCostShareAmount(new KualiInteger(5000));
        budgetNonpersonnel.setBudgetInflatedThirdPartyCostShareAmount(new KualiInteger(6000));

        budgetNonpersonnel.setBudgetOriginAgencyAmount(new KualiInteger(7000));
        budgetNonpersonnel.setBudgetOriginInstitutionCostShareAmount(new KualiInteger(8000));
        budgetNonpersonnel.setBudgetOriginThirdPartyCostShareAmount(new KualiInteger(9000));

        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetInstitutionCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);

        budgetNonpersonnel.setCopyToFuturePeriods(true);

        return budgetNonpersonnel;
    }
}
