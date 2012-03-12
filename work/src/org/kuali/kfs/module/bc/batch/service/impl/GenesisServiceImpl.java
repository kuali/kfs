/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.batch.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.bc.batch.dataaccess.GenesisDao;
import org.kuali.kfs.module.bc.batch.service.BudgetConstructionHumanResourcesPayrollInterfaceService;
import org.kuali.kfs.module.bc.batch.service.GenesisService;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class GenesisServiceImpl implements GenesisService {

    /* settings for common fields for all document headers for budget construction */

    protected GenesisDao genesisDao;
    protected BudgetConstructionHumanResourcesPayrollInterfaceService budgetConstructionHumanResourcesPayrollInterfaceService;

    private static Logger LOG = org.apache.log4j.Logger.getLogger(GenesisServiceImpl.class);

    /*
     * here are some flag value routines
     */

    public boolean BatchPositionSynchAllowed(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        boolean ReturnValue = (genesisDao.getBudgetConstructionControlFlag(RequestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING)) || ((genesisDao.getBudgetConstructionControlFlag(RequestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE)) && (genesisDao.getBudgetConstructionControlFlag(RequestYear, BudgetConstructionConstants.BUDGET_BATCH_SYNCHRONIZATION_OK)));
        return ReturnValue;
    }
    
    public boolean CSFUpdatesAllowed(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        boolean ReturnValue = (genesisDao.getBudgetConstructionControlFlag(RequestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING)) || ((genesisDao.getBudgetConstructionControlFlag(RequestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE)) && (genesisDao.getBudgetConstructionControlFlag(RequestYear, BudgetConstructionConstants.CSF_UPDATES_OK)));
        return ReturnValue;
    }

    public boolean GLUpdatesAllowed(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        boolean ReturnValue = (genesisDao.getBudgetConstructionControlFlag(RequestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING)) || ((genesisDao.getBudgetConstructionControlFlag(RequestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE)) && (genesisDao.getBudgetConstructionControlFlag(BaseYear, BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK)));
        return ReturnValue;
    }
    
    public boolean IsBudgetConstructionInUpdateMode(Integer baseYear)
    {
        Integer requestYear = baseYear + 1;
        return genesisDao.getBudgetConstructionControlFlag(requestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING) || ((genesisDao.getBudgetConstructionControlFlag(requestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE)) && (genesisDao.getBudgetConstructionControlFlag(requestYear, BudgetConstructionConstants.BUDGET_CONSTRUCTION_UPDATES_OK)));
    }

    public void clearDBForGenesis(Integer BaseYear) {
        genesisDao.clearDBForGenesis(BaseYear);
    }

    // use today's date to return the base fiscal year
    public Integer genesisFiscalYearFromToday() {
        return genesisDao.fiscalYearFromToday();
    }

    // this step updates the budget from the payroll (CSF) and the GL once
    // genesis has run.
    public void bCUpdateStep(Integer BaseYear) {
        /**
         *  the calling order is important
         *  for example, GL cannot be created without a document number, appointment funding needs the normal work months
         *  from the budget construction position table
         *  the calling order is constrained further if an institution implements referential integrity
         */
        LOG.warn(String.format("\n\nStarting BC Update:\n  clear hanging locks\n"));
        genesisDao.clearHangingBCLocks(BaseYear);
        LOG.warn(String.format("\n  validate object classes\n"));
        genesisDao.ensureObjectClassRIForBudget(BaseYear);
        LOG.warn(String.format("\n  create new documents\n"));
        genesisDao.createNewBCDocumentsFromGLCSF(BaseYear, GLUpdatesAllowed(BaseYear), CSFUpdatesAllowed(BaseYear));
        if (GLUpdatesAllowed(BaseYear)) {
            LOG.warn(String.format("\n  add rows to pending general ledger\n"));
            genesisDao.updateToPBGL(BaseYear);
        }
        boolean CSFOK = CSFUpdatesAllowed(BaseYear);
        boolean PSSynchOK = BatchPositionSynchAllowed(BaseYear);
        boolean BCUpdatesAllowed = IsBudgetConstructionInUpdateMode(BaseYear);
        LOG.warn(String.format("\n  update Budget Construction Position\n"));
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionPosition(BaseYear,PSSynchOK,CSFOK);
        LOG.warn(String.format("\n  intended incumbent"));
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionIntendedIncumbent(BaseYear,PSSynchOK,CSFOK,BCUpdatesAllowed);
        if (CSFOK) {
            LOG.warn(String.format("\n  build appointment funding\n"));
            genesisDao.buildAppointmentFundingAndBCSF(BaseYear);
        }
        LOG.warn(String.format("\n  rebuild the organization hierarchy\n"));
        genesisDao.rebuildOrganizationHierarchy(BaseYear);
        // look for accounts coming from payroll or GL that have not been entered into the Budget Construction Accounting table
        Integer requestFiscalYear = BaseYear + 1;
        LOG.warn(String.format("\n  look for accounts missing from Budget Construction Accounting\n"));
        HashMap<String,String[]> missingAccounts = (HashMap<String,String[]>) genesisDao.verifyAccountsAreAccessible(requestFiscalYear);
        for (Map.Entry<String,String[]> missingAccount : missingAccounts.entrySet())
        {
            String[] missingValues = missingAccount.getValue();
            LOG.warn(String.format("    (chart: %s, account: %s) not found in Budget Construction Accounting\n", missingValues[0], missingValues[1]));
        }
    }

    public void genesisStep(Integer BaseYear) {
        /**
         *  the calling order is important
         *  for example, GL cannot be created without a document number, appointment funding needs the normal work months
         *  from the budget construction position table
         *  the calling order is constrained further if an institution implements referential integrity
         */
        LOG.warn(String.format("\nstarting Genesis:\n  flags"));
        genesisDao.setControlFlagsAtTheStartOfGenesis(BaseYear);
        LOG.warn(String.format("\n  clear database"));
        genesisDao.clearDBForGenesis(BaseYear);
        LOG.warn(String.format("\n  chart for budget"));
        genesisDao.createChartForNextBudgetCycle();
        LOG.warn(String.format("\n  referential integrity for object classes"));
        genesisDao.ensureObjectClassRIForBudget(BaseYear);
        LOG.warn(String.format("\n  new BC documents"));
        genesisDao.createNewBCDocumentsFromGLCSF(BaseYear, GLUpdatesAllowed(BaseYear), CSFUpdatesAllowed(BaseYear));
        LOG.warn(String.format("\n  load to PBGL"));
        genesisDao.initialLoadToPBGL(BaseYear);
        LOG.warn(String.format("\n  new positions"));
        boolean CSFOK = CSFUpdatesAllowed(BaseYear);
        boolean PSSynchOK = BatchPositionSynchAllowed(BaseYear);
        boolean BCUpdatesAllowed = IsBudgetConstructionInUpdateMode(BaseYear);
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionPosition(BaseYear,PSSynchOK,CSFOK);
        LOG.warn(String.format("\n  intended incumbent"));
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionIntendedIncumbent(BaseYear,PSSynchOK,CSFOK,BCUpdatesAllowed);
        if (CSFOK) {
            LOG.warn("\n  appointment funding/BCSF");
            genesisDao.buildAppointmentFundingAndBCSF(BaseYear);
        }
        LOG.warn("\n  organization hierarchy");
        genesisDao.rebuildOrganizationHierarchy(BaseYear);
        LOG.warn("\n  reset control flags");
        genesisDao.setControlFlagsAtTheEndOfGenesis(BaseYear);
        LOG.warn("\n  end of genesis");
    }
    
    /*
     *   look of accounts from the payroll (CSF) or GL that came into budget construction but are *not* in the budget construction accounting table.
     *   this can be due to an oversight on the part of the chart manager, or to problems with the current year's budget control.
     *   such accounts will not appear in the pull-up list, since they can't be added to the reporting hierarchy, which is built from budget construction accounting.
     *   this method is provided for use by a report an institution might want to write.  such accounts will always appear in the log from bCUpdateStep above, whether this method is used or not.
     */
    public Map verifyAccountsAreAccessible(Integer requestFiscalYear)
    {
        return genesisDao.verifyAccountsAreAccessible(requestFiscalYear);
    }

    public void setGenesisDao(GenesisDao genesisDao) {
        this.genesisDao = genesisDao;
    }
    
    public void setBudgetConstructionHumanResourcesPayrollInterfaceService(BudgetConstructionHumanResourcesPayrollInterfaceService budgetConstructionHumanResourcesPayrollInterfaceService)
    {
        this.budgetConstructionHumanResourcesPayrollInterfaceService = budgetConstructionHumanResourcesPayrollInterfaceService; 
    }
}
