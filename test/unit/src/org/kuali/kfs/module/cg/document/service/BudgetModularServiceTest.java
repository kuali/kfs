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
package org.kuali.module.kra.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.bo.Budget;
import org.kuali.module.kra.bo.BudgetNonpersonnel;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests service methods in BudgetModularService.
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetModularServiceTest extends KualiTestBaseWithSpring {
    
    private BudgetModularService budgetModularService;
    private BudgetNonpersonnelService budgetNonpersonnelService;
    
    private Budget budget;
    private List nonpersonnelCategories;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        budgetModularService = SpringServiceLocator.getBudgetModularService();
        budgetNonpersonnelService = SpringServiceLocator.getBudgetNonpersonnelService();
        budget = setupBudget();
        nonpersonnelCategories = budgetNonpersonnelService.getAllNonpersonnelCategories();
    }
    
    protected Budget setupBudget() {
        Budget budget = new Budget();
        return budget;
    }
    
    
    public void testGenerateModularBudget() {
        List nonpersonnelList = new ArrayList();
        BudgetNonpersonnel budgetNonpersonnel = new BudgetNonpersonnel();
        budgetNonpersonnel.setAgencyRequestAmount(new Long(35000));
        //budgetModularService.generateModularBudget(budget, nonpersonnelCategories);
    }
    
    
//    public void testGenerateAgencyModularIncrements() {
//        BudgetModular modularBudget = new BudgetModular();
//        modularBudget.setTotalActualDirectCostAmount(new KualiDecimal(120000));
//        modularBudget.setBudgetPeriodMaximumAmount(new Integer(250000));
//        modularBudget.setBudgetModularIncrementAmount(new Integer(25000));
//        List result = budgetModularService.generateAgencyModularIncrements(modularBudget);
//        assertTrue(result.size() == 10);
//        assertTrue(((String) result.get(9)).equals("25000"));
//        assertTrue(((String) result.get(0)).equals("250000"));
//        assertTrue(((String) result.get(5)).equals("125000"));
//    }
//    
//    public void testDetermineModularDirectCost() {
//        BudgetModular modularBudget = new BudgetModular();
//        modularBudget.setTotalActualDirectCostAmount(new KualiDecimal(120000));
//        modularBudget.setBudgetPeriodMaximumAmount(new Integer(250000));
//        modularBudget.setBudgetModularIncrementAmount(new Integer(25000));
//        KualiDecimal result = budgetModularService.determineModularDirectCost(2, modularBudget);
//        assertTrue(result.equals(new KualiDecimal(75000)));
//    }
//    
//    public void testCalculateTotalModularDirectCostAmount() {
//        List modularPeriodList = new ArrayList();
//        
//        KualiDecimal result = budgetModularService.calculateTotalModularDirectCostAmount(new KualiDecimal(120000), modularPeriodList);
//        assertTrue(result.equals(new KualiDecimal(0)));
//        
//        BudgetModularPeriod modularPeriod1 = new BudgetModularPeriod();
//        modularPeriodList.add(modularPeriod1);
//        
//        BudgetModularPeriod modularPeriod2 = new BudgetModularPeriod();
//        modularPeriodList.add(modularPeriod2);
//        
//        result = budgetModularService.calculateTotalModularDirectCostAmount(new KualiDecimal(120000), modularPeriodList);
//        assertTrue(result.equals(new KualiDecimal(240000)));
//    }
//    
//    public void testCalculateTotalAdjustedModularDirectCostAmount() {
//        List modularPeriodList = new ArrayList();
//        
//        KualiDecimal result = budgetModularService.calculateTotalAdjustedModularDirectCostAmount(modularPeriodList);
//        assertTrue(result.equals(new KualiDecimal(0)));
//        
//        BudgetModularPeriod modularPeriod1 = new BudgetModularPeriod();
//        modularPeriod1.setBudgetAdjustedModularDirectCostAmount(new Integer(30000));
//        modularPeriodList.add(modularPeriod1);
//        
//        BudgetModularPeriod modularPeriod2 = new BudgetModularPeriod();
//        modularPeriod2.setBudgetAdjustedModularDirectCostAmount(new Integer(20000));
//        modularPeriodList.add(modularPeriod2);
//        
//        result = budgetModularService.calculateTotalAdjustedModularDirectCostAmount(modularPeriodList);
//        assertTrue(result.equals(new KualiDecimal(50000)));
//    }
//    
//    public void testSumKualiDecimalAmountAcrossPeriods() {
//        List modularPeriodList = new ArrayList();
//        
//        KualiDecimal result = budgetModularService.sumKualiDecimalAmountAcrossPeriods(modularPeriodList, "actualDirectCostAmount");
//        assertTrue(result.equals(new KualiDecimal(0)));
//        
//        BudgetModularPeriod modularPeriod1 = new BudgetModularPeriod();
//        modularPeriod1.setActualDirectCostAmount(new KualiDecimal(30000));
//        modularPeriodList.add(modularPeriod1);
//        
//        BudgetModularPeriod modularPeriod2 = new BudgetModularPeriod();
//        modularPeriod2.setActualDirectCostAmount(new KualiDecimal(20000));
//        modularPeriodList.add(modularPeriod2);
//        
//        result = budgetModularService.sumKualiDecimalAmountAcrossPeriods(modularPeriodList, "actualDirectCostAmount");
//        assertTrue(result.equals(new KualiDecimal(50000)));
//    }
}
