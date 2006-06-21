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
package org.kuali.module.kra.bo;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.KualiInteger;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverFormHelper.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetNonpersonnelTest extends KualiTestBaseWithSpring {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testBudgetNonpersonnel() {
        assertTrue(true);

        /**
         * @todo stubbed this for creation of static method below. Probably should write some BudgetPeriod test cases at some point
         *       ...
         */
    }

    public static List createBudgetNonpersonnel(String[] nonpersonnelCategories, String[] subCategories, String[] subcontractorNumber) {
        List budgetNonpersonnelList = new ArrayList();

        if (nonpersonnelCategories.length != subCategories.length) {
            throw new IllegalArgumentException("nonpersonnelCategories and subCategories must equal in length");
        }

        for (int i = 0; i < nonpersonnelCategories.length; i++) {
            BudgetNonpersonnel budgetNonpersonnel = new BudgetNonpersonnel();
            budgetNonpersonnel.setBudgetNonpersonnelSequenceNumber(new Integer(i));
            budgetNonpersonnel.setBudgetPeriodSequenceNumber(new Integer(0));
            budgetNonpersonnel.setBudgetTaskSequenceNumber(new Integer(0));

            budgetNonpersonnel.setSubcontractorNumber(subcontractorNumber[i]);
            
            budgetNonpersonnel.setBudgetNonpersonnelCategoryCode(nonpersonnelCategories[i]);
            budgetNonpersonnel.setBudgetNonpersonnelSubCategoryCode(subCategories[i]);
            budgetNonpersonnel.setNonpersonnelObjectCode(new NonpersonnelObjectCode(nonpersonnelCategories[i], subCategories[i]));

            budgetNonpersonnel.setCopyToFuturePeriods(false);
            budgetNonpersonnel.setAgencyCopyIndicator(false);
            budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
            budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);

            budgetNonpersonnel.setAgencyRequestAmount(new KualiInteger(1000));
            budgetNonpersonnel.setBudgetUniversityCostShareAmount(new KualiInteger(2000));
            budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(new KualiInteger(3000));

            budgetNonpersonnel.setBudgetOriginSequenceNumber(null);

            budgetNonpersonnelList.add(budgetNonpersonnel);
        }

        return budgetNonpersonnelList;
    }
}
