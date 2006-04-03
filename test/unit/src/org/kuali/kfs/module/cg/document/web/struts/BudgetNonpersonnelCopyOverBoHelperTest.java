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

import org.kuali.module.kra.bo.BudgetNonpersonnel;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverBoHelper.
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
        
        // BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel budgetNonpersonnel)
        budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();
        budgetNonpersonnel.setCopyToFuturePeriods(false);
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        BudgetNonpersonnelCopyOverBoHelper newBudgetNonpersonnel = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel);
        assertFalse(newBudgetNonpersonnel.getAgencyCopyIndicator());
        assertFalse(newBudgetNonpersonnel.getBudgetUniversityCostShareCopyIndicator());
        assertFalse(newBudgetNonpersonnel.getBudgetThirdPartyCostShareCopyIndicator());
        
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        newBudgetNonpersonnel = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel);
        assertTrue(newBudgetNonpersonnel.getAgencyCopyIndicator());
        assertTrue(newBudgetNonpersonnel.getBudgetUniversityCostShareCopyIndicator());
        assertTrue(newBudgetNonpersonnel.getBudgetThirdPartyCostShareCopyIndicator());

        assertEquality(newBudgetNonpersonnel.getBudgetInflatedAgencyAmount(), new Long(1000));
        assertEquality(newBudgetNonpersonnel.getBudgetInflatedUniversityCostShareAmount(), new Long(2000));
        assertEquality(newBudgetNonpersonnel.getBudgetInflatedThirdPartyCostShareAmount(), new Long(3000));
        
        // BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel budgetNonpersonnel, int inflationLength, KualiDecimal budgetNonpersonnelInflationRate)
        // todo
        
        // BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel originBudgetNonpersonnel, Integer budgetPeriodSequenceNumberOverride, int inflationLength, KualiDecimal budgetNonpersonnelInflationRate)
        // todo
    }
    
    public void testGetBudgetNonpersonnel() {
        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnel = createBudgetNonpersonnelCopyOverBoHelper();
        
        // agencyCopyIndicator = false
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        BudgetNonpersonnel newBudgetNonpersonnel1 = budgetNonpersonnel.getBudgetNonpersonnel();
        assertEquality(newBudgetNonpersonnel1.getAgencyRequestAmount(), new Long(1000));
        assertEquality(newBudgetNonpersonnel1.getBudgetUniversityCostShareAmount(), new Long(2000));
        assertEquality(newBudgetNonpersonnel1.getBudgetThirdPartyCostShareAmount(), new Long(3000));
        assertFalse(newBudgetNonpersonnel1.getCopyToFuturePeriods()); // should always be false per interface requirement
        
        // agencyCopyIndicator = true
        budgetNonpersonnel.setAgencyCopyIndicator(true);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(true);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(true);
        budgetNonpersonnel.setCopyToFuturePeriods(false);
        BudgetNonpersonnel newBudgetNonpersonnel2 = budgetNonpersonnel.getBudgetNonpersonnel();
        assertEquality(newBudgetNonpersonnel1.getAgencyRequestAmount(), new Long(4000));
        assertEquality(newBudgetNonpersonnel1.getBudgetUniversityCostShareAmount(), new Long(5000));
        assertEquality(newBudgetNonpersonnel1.getBudgetThirdPartyCostShareAmount(), new Long(6000));
        assertFalse(newBudgetNonpersonnel2.getCopyToFuturePeriods()); // should always be false per interface requirement
    }
    
    private BudgetNonpersonnelCopyOverBoHelper createBudgetNonpersonnelCopyOverBoHelper() {
        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnel = new BudgetNonpersonnelCopyOverBoHelper();
        
        budgetNonpersonnel.setAgencyRequestAmount(new Long(1000));
        budgetNonpersonnel.setBudgetUniversityCostShareAmount(new Long(2000));
        budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(new Long(3000));
        budgetNonpersonnel.setBudgetInflatedAgencyAmount(new Long(4000));
        budgetNonpersonnel.setBudgetInflatedUniversityCostShareAmount(new Long(5000));
        budgetNonpersonnel.setBudgetInflatedThirdPartyCostShareAmount(new Long(6000));
        
        budgetNonpersonnel.setAgencyCopyIndicator(false);
        budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
        budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
        
        budgetNonpersonnel.setCopyToFuturePeriods(true);
        
        return budgetNonpersonnel;
    }
}
