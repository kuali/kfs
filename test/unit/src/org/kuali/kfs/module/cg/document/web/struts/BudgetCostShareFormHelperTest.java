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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.kra.bo.BudgetNonpersonnelTest;
import org.kuali.module.kra.bo.BudgetPeriodTest;
import org.kuali.module.kra.budget.bo.BudgetInstitutionCostShare;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetPeriodInstitutionCostShare;
import org.kuali.module.kra.budget.bo.BudgetPeriodThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetTaskPeriodIndirectCost;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.InstitutionCostSharePersonnel;
import org.kuali.module.kra.budget.bo.UserAppointmentTask;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.web.struts.form.BudgetCostShareFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetIndirectCostFormHelper;
import org.kuali.test.ConfigureContext;

/**
 * This class tests methods in BudgetOverviewFormHelper.
 */
@ConfigureContext
public class BudgetCostShareFormHelperTest extends KualiTestBase {

    List<BudgetPeriod> periods;
    List<BudgetUser> personnel;
    List<BudgetNonpersonnel> budgetNonpersonnelItems;
    List<InstitutionCostSharePersonnel> institutionCostSharePersonnel;
    List<BudgetInstitutionCostShare> budgetInstitutionCostShare;
    List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare;
    BudgetIndirectCostFormHelper budgetIndirectCostFormHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Periods
        periods = BudgetPeriodTest.createBudgetPeriods(2);

        // Personnel
        personnel = new ArrayList();
        BudgetUser budgetUser1 = new BudgetUser();
        budgetUser1.setFiscalCampusCode("BL");
        budgetUser1.setPrimaryDepartmentCode("CHEM");
        UserAppointmentTask userAppointmentTask = new UserAppointmentTask();
        UserAppointmentTaskPeriod userAppointmentTaskPeriod1 = new UserAppointmentTaskPeriod();
        userAppointmentTaskPeriod1.setInstitutionCostShareFringeBenefitTotalAmount(new KualiInteger(2500));
        userAppointmentTaskPeriod1.setInstitutionCostShareRequestTotalAmount(new KualiInteger(1500));
        userAppointmentTaskPeriod1.setBudgetPeriodSequenceNumber(new Integer(0));
        UserAppointmentTaskPeriod userAppointmentTaskPeriod2 = new UserAppointmentTaskPeriod();
        userAppointmentTaskPeriod2.setInstitutionCostShareFringeBenefitTotalAmount(new KualiInteger(3500));
        userAppointmentTaskPeriod2.setInstitutionCostShareRequestTotalAmount(new KualiInteger(4500));
        userAppointmentTaskPeriod2.setBudgetPeriodSequenceNumber(new Integer(1));
        List userAppointmentTaskPeriod = new ArrayList();
        userAppointmentTaskPeriod.add(userAppointmentTaskPeriod1);
        userAppointmentTaskPeriod.add(userAppointmentTaskPeriod2);
        userAppointmentTask.setUserAppointmentTaskPeriods(userAppointmentTaskPeriod);
        List userAppointmentTasks = new ArrayList();
        userAppointmentTasks.add(userAppointmentTask);
        budgetUser1.setUserAppointmentTasks(userAppointmentTasks);
        personnel.add(budgetUser1);

        // University Cost Share Personnel
        institutionCostSharePersonnel = new ArrayList();
        InstitutionCostSharePersonnel universityCostSharePersonnel1 = new InstitutionCostSharePersonnel();
        universityCostSharePersonnel1.setChartOfAccountsCode("BL");
        universityCostSharePersonnel1.setOrganizationCode("CHEM");
        institutionCostSharePersonnel.add(universityCostSharePersonnel1);

        // Nonpersonnel
        String[] categories = { "CO", "CO", "SC", "SC" };
        String[] subCategories = { "C1", "C1", "R3", "R1" };
        String[] subcontractorNumber = { "", "", "1", "2" };
        budgetNonpersonnelItems = BudgetNonpersonnelTest.createBudgetNonpersonnel(categories, subCategories, subcontractorNumber);

        // Budget University Cost Share
        budgetInstitutionCostShare = new ArrayList();
        BudgetInstitutionCostShare budgetUniversityCostShare1 = new BudgetInstitutionCostShare();
        List budgetPeriodUniversityCostShare = new ArrayList();
        BudgetPeriodInstitutionCostShare budgetPeriodUniversityCostShare1 = new BudgetPeriodInstitutionCostShare();
        budgetPeriodUniversityCostShare1.setBudgetCostShareAmount(new KualiInteger(1000));
        budgetPeriodUniversityCostShare.add(budgetPeriodUniversityCostShare1);
        BudgetPeriodInstitutionCostShare budgetPeriodUniversityCostShare2 = new BudgetPeriodInstitutionCostShare();
        budgetPeriodUniversityCostShare2.setBudgetCostShareAmount(new KualiInteger(2000));
        budgetPeriodUniversityCostShare.add(budgetPeriodUniversityCostShare2);
        budgetUniversityCostShare1.setBudgetPeriodCostShare(budgetPeriodUniversityCostShare);
        budgetInstitutionCostShare.add(budgetUniversityCostShare1);

        // Budget Third Party Cost Share
        budgetThirdPartyCostShare = new ArrayList();
        BudgetThirdPartyCostShare budgetThirdPartyCostShare1 = new BudgetThirdPartyCostShare();
        List budgetPeriodThirdPartyCostShare = new ArrayList();
        BudgetPeriodThirdPartyCostShare budgetPeriodThirdPartyCostShare1 = new BudgetPeriodThirdPartyCostShare();
        budgetPeriodThirdPartyCostShare1.setBudgetCostShareAmount(new KualiInteger(3000));
        budgetPeriodThirdPartyCostShare.add(budgetPeriodThirdPartyCostShare1);
        BudgetPeriodThirdPartyCostShare budgetPeriodThirdPartyCostShare2 = new BudgetPeriodThirdPartyCostShare();
        budgetPeriodThirdPartyCostShare2.setBudgetCostShareAmount(new KualiInteger(4000));
        budgetPeriodThirdPartyCostShare.add(budgetPeriodThirdPartyCostShare2);
        budgetThirdPartyCostShare1.setBudgetPeriodCostShare(budgetPeriodThirdPartyCostShare);
        budgetThirdPartyCostShare.add(budgetThirdPartyCostShare1);

        // Budget Indirect Cost
        budgetIndirectCostFormHelper = new BudgetIndirectCostFormHelper();
        List periodTotalsList = new ArrayList();
        BudgetTaskPeriodIndirectCost idcPeriod0Exist = new BudgetTaskPeriodIndirectCost();
        idcPeriod0Exist.setCostShareCalculatedIndirectCost(new KualiInteger(300));
        idcPeriod0Exist.setCostShareUnrecoveredIndirectCost(new KualiInteger(200));
        periodTotalsList.add(idcPeriod0Exist);
        BudgetTaskPeriodIndirectCost idcPeriod1Exist = new BudgetTaskPeriodIndirectCost();
        idcPeriod1Exist.setCostShareCalculatedIndirectCost(new KualiInteger(100));
        idcPeriod1Exist.setCostShareUnrecoveredIndirectCost(new KualiInteger(100));
        periodTotalsList.add(idcPeriod1Exist);
        budgetIndirectCostFormHelper.setPeriodTotals(periodTotalsList);
    }

    public void testBudgetCostShareFormHelper() {
        BudgetCostShareFormHelper budgetCostShareFormHelper = new BudgetCostShareFormHelper(periods, personnel, budgetNonpersonnelItems, institutionCostSharePersonnel, budgetInstitutionCostShare, budgetThirdPartyCostShare, budgetIndirectCostFormHelper);

        testSetupInstitutionDirect(budgetCostShareFormHelper.getInstitutionDirect());

        testSetupThirdPartyDirect(budgetCostShareFormHelper.getThirdPartyDirect());

        testSetupSubcontractors(budgetCostShareFormHelper);

        testSetupTotals(budgetCostShareFormHelper);
    }

    private void testSetupInstitutionDirect(BudgetCostShareFormHelper.Direct institutionDirect) {
        assertEquals("institutionDirect.getTotalBudgeted()[0] = 12000", institutionDirect.getTotalBudgeted()[0], new KualiInteger(12000));
        assertEquals("institutionDirect.getTotalBudgeted()[1] = 8000", institutionDirect.getTotalBudgeted()[1], new KualiInteger(8000));
        assertEquals("institutionDirect.getAmountDistributed()[0] = 5000", institutionDirect.getAmountDistributed()[0], new KualiInteger(5000));
        assertEquals("institutionDirect.getAmountDistributed()[1] = 10000", institutionDirect.getAmountDistributed()[1], new KualiInteger(10000));
        assertEquals("institutionDirect.getBalanceToBeDistributed()[0] = 7000", institutionDirect.getBalanceToBeDistributed()[0], new KualiInteger(7000));
        assertEquals("institutionDirect.getBalanceToBeDistributed()[1] = -2000", institutionDirect.getBalanceToBeDistributed()[1], new KualiInteger(-2000));

        assertEquals("institutionDirect.getTotalTotalBudgeted()[0] = 20000", institutionDirect.getTotalTotalBudgeted(), new KualiInteger(20000));
        assertEquals("institutionDirect.getTotalAmountDistributed() = 15000", institutionDirect.getTotalAmountDistributed(), new KualiInteger(15000));
        assertEquals("institutionDirect.getTotalBalanceToBeDistributed() = 5000", institutionDirect.getTotalBalanceToBeDistributed(), new KualiInteger(5000));

        assertEquals("institutionDirect.getTotalSource()[0] = 3000", institutionDirect.getTotalSource()[0], new KualiInteger(3000));

        assertEquals("institutionDirect.getInstitutionDirectPersonnel()[0][0] = 4000", institutionDirect.getInstitutionDirectPersonnel()[0][0], new KualiInteger(4000));
        assertEquals("institutionDirect.getInstitutionDirectPersonnel()[0][1] = 8000", institutionDirect.getInstitutionDirectPersonnel()[0][1], new KualiInteger(8000));
        assertEquals("institutionDirect.getTotalInstitutionDirectPersonnel()[0] = 12000", institutionDirect.getTotalInstitutionDirectPersonnel()[0], new KualiInteger(12000));
    }

    private void testSetupThirdPartyDirect(BudgetCostShareFormHelper.Direct thirdPartyDirect) {
        assertEquals("thirdPartyDirect.getTotalBudgeted()[0] = 12000", thirdPartyDirect.getTotalBudgeted()[0], new KualiInteger(12000));
        assertEquals("thirdPartyDirect.getAmountDistributed()[0] = 9000", thirdPartyDirect.getAmountDistributed()[0], new KualiInteger(9000));
        assertEquals("thirdPartyDirect.getBalanceToBeDistributed()[0] = 3000", thirdPartyDirect.getBalanceToBeDistributed()[0], new KualiInteger(3000));

        assertEquals("thirdPartyDirect.getTotalTotalBudgeted()[0] = 12000", thirdPartyDirect.getTotalTotalBudgeted(), new KualiInteger(12000));
        assertEquals("thirdPartyDirect.getTotalAmountDistributed()[0] = 13000", thirdPartyDirect.getTotalAmountDistributed(), new KualiInteger(13000));
        assertEquals("thirdPartyDirect.getTotalBalanceToBeDistributed()[0] = -1000", thirdPartyDirect.getTotalBalanceToBeDistributed(), new KualiInteger(-1000));

        assertEquals("thirdPartyDirect.getTotalSource()[0] = 7000", thirdPartyDirect.getTotalSource()[0], new KualiInteger(7000));
    }

    private void testSetupSubcontractors(BudgetCostShareFormHelper budgetCostShareFormHelper) {
        List<BudgetCostShareFormHelper.Subcontractor> subcontractors = budgetCostShareFormHelper.getSubcontractors();

        assertTrue("subcontractors.size() == 2", subcontractors.size() == 2);

        for (BudgetCostShareFormHelper.Subcontractor subcontractor : subcontractors) {
            assertEquals("totalPeriodAmount = 3000", subcontractor.getTotalPeriodAmount(), new KualiInteger(3000));

            KualiInteger[] periodAmounts = subcontractor.getPeriodAmounts();
            assertEquals("periodAmounts[0] = 3000", periodAmounts[0], new KualiInteger(3000));
            assertEquals("periodAmounts[1] = 0", periodAmounts[1], KualiInteger.ZERO);
        }

        // This test does not do any aggregation (hit Subcontractor.addPeriodAmount). That is not very good but in the
        // interest of time I'm omitting that. BudgetNonpersonnelCopyOverFormHelperTest could possibly be used for that
        // although it wouldn't be trivial...
    }

    private void testSetupTotals(BudgetCostShareFormHelper budgetCostShareFormHelper) {
        assertEquals("institutionIndirectCostShare[0] = 500", budgetCostShareFormHelper.getInstitutionIndirectCostShare()[0], new KualiInteger(500));
        assertEquals("institutionIndirectCostShare[1] = 200", budgetCostShareFormHelper.getInstitutionIndirectCostShare()[1], new KualiInteger(200));
        assertEquals("total[0] = 20500", budgetCostShareFormHelper.getTotal()[0], new KualiInteger(20500));
        assertEquals("total[1] = 14200", budgetCostShareFormHelper.getTotal()[1], new KualiInteger(14200));

        assertEquals("totalInstitutionIndirectCostShare = 700", budgetCostShareFormHelper.getTotalInstitutionIndirectCostShare(), new KualiInteger(700));
        assertEquals("totalTotal = 34700", budgetCostShareFormHelper.getTotalTotal(), new KualiInteger(34700));
    }
}