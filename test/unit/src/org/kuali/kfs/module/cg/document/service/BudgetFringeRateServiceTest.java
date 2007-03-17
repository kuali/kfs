/*
 * Copyright 2005-2007 The Kuali Foundation.
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
/*
 * Created on Jul 15, 2005
 *
 */
package org.kuali.module.kra.service;

import static org.kuali.kfs.util.SpringServiceLocator.getBudgetFringeRateService;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * 
 * This class...
 * 
 * 
 */
@WithTestSpringContext
public class BudgetFringeRateServiceTest extends KualiTestBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetFringeRateServiceTest.class);

    private static final String UNKNOWN_USERNAME = "foo";
    private static final String KNOWN_USERNAME = "KHUNTLEY";
    private static final String UNKNOWN_DOCUMENT_TYPENAME = "bar";
    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiBudgetDocument";
    private static final Long UNKNOWN_DOCHEADERID = new Long(-1);

    private static final KualiDecimal BAD_FRINGE_RATE = new KualiDecimal("20.50");
    private static final KualiDecimal GOOD_FRINGE_RATE = new KualiDecimal("0.50");
    private static final KualiDecimal BAD_COST_SHARE = new KualiDecimal("20.50");
    private static final KualiDecimal GOOD_COST_SHARE = new KualiDecimal("0.50");



    public void testValidContractsAndGrantsFringeRate() throws Exception {
        BudgetFringeRate budgetFringeRate = new BudgetFringeRate();

        budgetFringeRate.setContractsAndGrantsFringeRateAmount(GOOD_FRINGE_RATE);

        assertTrue(getBudgetFringeRateService().isValidFringeRate(budgetFringeRate.getContractsAndGrantsFringeRateAmount()));
    }

    public void testValidContractsAndGrantsCostShare() throws Exception {
        BudgetFringeRate budgetFringeRate = new BudgetFringeRate();

        budgetFringeRate.setInstitutionCostShareFringeRateAmount(GOOD_COST_SHARE);
        assertTrue(getBudgetFringeRateService().isValidCostShare(budgetFringeRate.getInstitutionCostShareFringeRateAmount()));
    }

    public void testInvalidContractsAndGrantsFringeRate() throws Exception {
        BudgetFringeRate budgetFringeRate = new BudgetFringeRate();

        budgetFringeRate.setContractsAndGrantsFringeRateAmount(BAD_FRINGE_RATE);
        assertTrue(getBudgetFringeRateService().isValidFringeRate(budgetFringeRate.getContractsAndGrantsFringeRateAmount()));
    }

    public void testInvalidContractsAndGrantsCostShare() throws Exception {
        BudgetFringeRate budgetFringeRate = new BudgetFringeRate();

        budgetFringeRate.setInstitutionCostShareFringeRateAmount(BAD_COST_SHARE);
        assertTrue(getBudgetFringeRateService().isValidCostShare(budgetFringeRate.getInstitutionCostShareFringeRateAmount()));
    }
}
