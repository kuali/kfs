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
package org.kuali.kfs.module.bc.batch.service.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.bc.batch.dataaccess.GenesisDao;
import org.kuali.kfs.module.bc.batch.service.BudgetConstructionHumanResourcesPayrollInterfaceService;
import org.kuali.kfs.module.bc.batch.service.GenesisService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class GenesisServiceImpl implements GenesisService {

    /* settings for common fields for all document headers for budget construction */

    private GenesisDao genesisDao;
    private BudgetConstructionHumanResourcesPayrollInterfaceService budgetConstructionHumanResourcesPayrollInterfaceService;

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
        genesisDao.clearHangingBCLocks(BaseYear);
        genesisDao.ensureObjectClassRIForBudget(BaseYear);
        genesisDao.createNewBCDocumentsFromGLCSF(BaseYear, GLUpdatesAllowed(BaseYear), CSFUpdatesAllowed(BaseYear));
        if (GLUpdatesAllowed(BaseYear)) {
            genesisDao.updateToPBGL(BaseYear);
        }
        boolean CSFOK = CSFUpdatesAllowed(BaseYear);
        boolean PSSynchOK = BatchPositionSynchAllowed(BaseYear);
        boolean BCUpdatesAllowed = IsBudgetConstructionInUpdateMode(BaseYear);
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionPosition(BaseYear,PSSynchOK,CSFOK);
        LOG.warn(String.format("\n  intended incumbent"));
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionIntendedIncumbent(BaseYear,PSSynchOK,CSFOK,BCUpdatesAllowed);
        if (CSFOK) {
            genesisDao.buildAppointmentFundingAndBCSF(BaseYear);
        }
        genesisDao.rebuildOrganizationHierarchy(BaseYear);
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

    public void setGenesisDao(GenesisDao genesisDao) {
        this.genesisDao = genesisDao;
    }
    
    public void setBudgetConstructionHumanResourcesPayrollInterfaceService(BudgetConstructionHumanResourcesPayrollInterfaceService budgetConstructionHumanResourcesPayrollInterfaceService)
    {
        this.budgetConstructionHumanResourcesPayrollInterfaceService = budgetConstructionHumanResourcesPayrollInterfaceService; 
    }
}
