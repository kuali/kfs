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
package org.kuali.module.budget.service.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSConstants.BudgetConstructionConstants;
import org.kuali.module.budget.dao.GenesisDao;
import org.kuali.module.budget.service.GenesisService;
import org.kuali.module.budget.service.BudgetConstructionHumanResourcesPayrollInterfaceService;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class GenesisServiceImpl implements GenesisService {

    /* settings for common fields for all document headers for budget construction */

    private GenesisDao genesisDao;
    private BudgetConstructionHumanResourcesPayrollInterfaceService budgetConstructionHumanResourcesPayrollInterfaceService;

    private static Logger LOG = org.apache.log4j.Logger.getLogger(GenesisServiceImpl.class);

    public final void testStep(Integer BaseYear) {
        genesisDao.updateToPBGL(BaseYear);
    }

    public final void testSLFStep(Integer BaseYear) {
        genesisDao.setControlFlagsAtTheStartOfGenesis(BaseYear);
    }

    public final void testSLFAfterStep(Integer BaseYear) {
        genesisDao.setControlFlagsAtTheEndOfGenesis(BaseYear);
    }

    public final void testLockClearance(Integer currentFiscalYear) {
        genesisDao.clearHangingBCLocks(currentFiscalYear);
    }

    public Object testDocumentHeader() {
        return genesisDao.returnWkflwDocHeader();
    }

    public void testPositionBuild(Integer currentFiscalYear) {
        // boolean CSFOK = CSFUpdatesAllowed(currentFiscalYear);
        // boolean PSSynchOK = BatchPositionSynchAllowed(currentFiscalYear);
        // genesisDao.createNewBCPosition(currentFiscalYear,
        // PSSynchOK,
        // CSFOK);
        // 09/17/2008 genesisDao.genesisUnitTest(currentFiscalYear);
        // 09/17/2008 test of OJB
        // 01/10/08 genesisDao.testObjectID();
        genesisDao.testNullForeignKeys();
    }

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

    public final void stepBudgetConstructionGLLoad(Integer universityFiscalYear) {

        if (genesisDao.getBudgetConstructionControlFlag(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING)) {
            // wipe out BC HEADER with deleteByQuery
            // wipe out appointment funding GL
            // get a list of all GL BB keys for universityFiscalYear
            // get a list of BB rows, and curse(sic) through the list,
            // getting a new appointment funding GL object at each step and
            // inserting it
            // use the common method to create BC headers and Doc headers for
            // all the GL BB keys
            // we want this to be a single transaction, so we should get
            // a persistence broker and do a start and end
            return;
        }
        ;
        if (genesisDao.getBudgetConstructionControlFlag(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE) && genesisDao.getBudgetConstructionControlFlag(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK)) {
            // this is the more complicated branch that updates the BC GL
            // there should be a private method called here, as in the first branch
            return;
        }
        /*
         * (1) Get a persistence broker to maintain a single transaction (2) Get a hash map of BC HEADER account keys (we may have
         * to build it from what is returned). (3) Get a list of GL BB rows, sorted on accounting key (which will be the same as the
         * sort of the BC HEADER (4) Get a list of PBGL rows, sorted on account key (5) Do a merge in software, building a new
         * header AND a new document header every time one of the ACCOUNT KEYS in (3) does not match a key in (2). Here are the
         * assumptions. --we can use a single broker for all the queries. --we can create a new PBGL object, a new header object,
         * and a new doc header object, and store them when we need to --we can update and store the PBGL objects using the setters
         * for the amount --any PBGL objects we do NOT store will not be involved in the save --nothing is actually sent to the
         * database until a commit (is closing the broker sufficient for that?), and presumably the SQL that does that is not too
         * clunky. Should we use beginTransaction anc commitTransaction in the Spring PersistenceBrokerImpl as well? --we can use
         * the p6spy log to see what the thing does, and since we won't have too many test rows it will be reasonable --we can just
         * create a default package with a main method and run all this shit we want to see whether we can use the apache.ojb
         * addColumnEqualToField in the Criteria class and a report query in a subselect to replace the code above and have the
         * query return to us the GL BB rows that exist (or don't) in PBGL? Can we use deleteByQuery or something similar to replace
         * our TRUNCATEs, so a single DELETE query runs on the data base and cleans out all the tables? Are the constants going to
         * be updated? Some things have a "property" constant for the field name, and various codes for the value, and some do not?
         * There also seems to be a little overlap between the GL constants and the constants. What is the purpose of all the
         * interfaces followed by implementations? I assume it's not just to be cool--it's probably there to allow people to
         * override the implementations. If that is so, are there any rules for which methods should go in the interface (ALL public
         * methods, say), or is it up to the developer to decide?
         */
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
        genesisDao.initialLoadToPBGL(BaseYear);
        boolean CSFOK = CSFUpdatesAllowed(BaseYear);
        boolean PSSynchOK = BatchPositionSynchAllowed(BaseYear);
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionPosition(BaseYear,PSSynchOK,CSFOK);
        LOG.warn(String.format("\n  intended incumbent"));
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionIntendedIncumbent(BaseYear,PSSynchOK,CSFOK);
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
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionPosition(BaseYear,PSSynchOK,CSFOK);
        LOG.warn(String.format("\n  intended incumbent"));
        budgetConstructionHumanResourcesPayrollInterfaceService.refreshBudgetConstructionIntendedIncumbent(BaseYear,PSSynchOK,CSFOK);
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

    // these steps are no longer needed now that workflow runs locally in the same
    // transaction as genesis
    public void genesisDocumentStep(Integer BaseYear) {
        genesisDao.setControlFlagsAtTheStartOfGenesis(BaseYear);
        genesisDao.clearDBForGenesis(BaseYear);
        genesisDao.createNewBCDocumentsFromGLCSF(BaseYear, GLUpdatesAllowed(BaseYear), CSFUpdatesAllowed(BaseYear));
    }

    public void genesisFinalStep(Integer BaseYear) {
        // this assumes that the documents have been built in genesisDocumentStep
        genesisDao.createChartForNextBudgetCycle();
        genesisDao.rebuildOrganizationHierarchy(BaseYear);
        //
        genesisDao.initialLoadToPBGL(BaseYear);
        genesisDao.setControlFlagsAtTheEndOfGenesis(BaseYear);
    }

    public void testChartCreation() {
        genesisDao.createChartForNextBudgetCycle();
    }

    public void testHierarchyCreation(Integer BaseYear) {
        genesisDao.rebuildOrganizationHierarchy(BaseYear);
    }

    public String testFindBCDocumentNumber (Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber) {
      return(genesisDao.testFindBCDocumentNumber(fiscalYear, chartOfAccounts, accountNumber, subAccountNumber));
    }

    public void setGenesisDao(GenesisDao genesisDao) {
        this.genesisDao = genesisDao;
    }
    
    public void setBudgetConstructionHumanResourcesPayrollInterfaceService(BudgetConstructionHumanResourcesPayrollInterfaceService budgetConstructionHumanResourcesPayrollInterfaceService)
    {
        this.budgetConstructionHumanResourcesPayrollInterfaceService = budgetConstructionHumanResourcesPayrollInterfaceService; 
    }
}
