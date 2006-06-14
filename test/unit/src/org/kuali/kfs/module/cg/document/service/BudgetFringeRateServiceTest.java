/*
 * Created on Jul 15, 2005
 *
 */
package org.kuali.module.kra.service;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.bo.BudgetFringeRate;
import org.kuali.test.KualiTestBaseWithSession;

/**
 * 
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetFringeRateServiceTest extends KualiTestBaseWithSession {

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


    private BudgetFringeRateService budgetFringeRateService;

    protected void setUp() throws Exception {
        super.setUp();
        super.changeCurrentUser(KNOWN_USERNAME);
        this.budgetFringeRateService = SpringServiceLocator.getBudgetFringeRateService();
    }


    public void testValidContractsAndGrantsFringeRate() throws Exception {
        BudgetFringeRate budgetFringeRate = new BudgetFringeRate();

        budgetFringeRate.setContractsAndGrantsFringeRateAmount(GOOD_FRINGE_RATE);

        assertTrue(budgetFringeRateService.isValidFringeRate(budgetFringeRate.getContractsAndGrantsFringeRateAmount()));
    }

    public void testValidContractsAndGrantsCostShare() throws Exception {
        BudgetFringeRate budgetFringeRate = new BudgetFringeRate();

        budgetFringeRate.setUniversityCostShareFringeRateAmount(GOOD_COST_SHARE);
        assertTrue(budgetFringeRateService.isValidCostShare(budgetFringeRate.getUniversityCostShareFringeRateAmount()));
    }

    public void testInvalidContractsAndGrantsFringeRate() throws Exception {
        BudgetFringeRate budgetFringeRate = new BudgetFringeRate();

        budgetFringeRate.setContractsAndGrantsFringeRateAmount(BAD_FRINGE_RATE);
        assertTrue(budgetFringeRateService.isValidFringeRate(budgetFringeRate.getContractsAndGrantsFringeRateAmount()));
    }

    public void testInvalidContractsAndGrantsCostShare() throws Exception {
        BudgetFringeRate budgetFringeRate = new BudgetFringeRate();

        budgetFringeRate.setUniversityCostShareFringeRateAmount(BAD_COST_SHARE);
        assertTrue(budgetFringeRateService.isValidCostShare(budgetFringeRate.getUniversityCostShareFringeRateAmount()));
    }
}
