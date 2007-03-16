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

import org.kuali.Constants;
import org.kuali.Constants.*;
import org.kuali.module.budget.dao.GenesisDao;
import org.kuali.module.budget.service.GenesisService;
import org.springframework.transaction.annotation.Transactional;

 
@Transactional
public class GenesisServiceImpl implements GenesisService {
    
    /*  settings for common fields for all document headers for budget construction */
    
      private GenesisDao genesisDao;

      public final void testStep (Integer BaseYear)
      {
         genesisDao.updateToPBGL(BaseYear);  
      }
      
      public final void testSLFStep (Integer BaseYear)
      {
          genesisDao.setControlFlagsAtTheStartOfGenesis(BaseYear);
      }
      
      public final void testSLFAfterStep (Integer BaseYear)
      {
          genesisDao.setControlFlagsAtTheEndOfGenesis(BaseYear);
      }
      
      public final void testLockClearance(Integer currentFiscalYear)
      {
          genesisDao.clearHangingBCLocks(currentFiscalYear);
      }

      public void testPositionBuild(Integer currentFiscalYear)
      {
          boolean CSFOK     = CSFUpdatesAllowed(currentFiscalYear+1);
          boolean PSSynchOK = BatchPositionSynchAllowed(currentFiscalYear+1);
          genesisDao.createNewBCPosition(currentFiscalYear,
                                         PSSynchOK,
                                         CSFOK);
      }
      
      /*
       *   here are some flag value routines
       */
      
      public boolean BatchPositionSynchAllowed(Integer BaseYear)
      {
          Integer RequestYear = BaseYear + 1;
          boolean ReturnValue =
          (genesisDao.getBudgetConstructionControlFlag(RequestYear,
                       BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING))||
          ((genesisDao.getBudgetConstructionControlFlag(RequestYear,
                       BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE))&&
           (genesisDao.getBudgetConstructionControlFlag(RequestYear,
                       BudgetConstructionConstants.BUDGET_BATCH_SYNCHRONIZATION_OK)));            
          return ReturnValue;
      }
    
      public boolean CSFUpdatesAllowed(Integer BaseYear)
      {
          Integer RequestYear = BaseYear + 1;
          boolean ReturnValue =
          (genesisDao.getBudgetConstructionControlFlag(RequestYear,
                       BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING))||
          ((genesisDao.getBudgetConstructionControlFlag(RequestYear,
                       BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE))&&
           (genesisDao.getBudgetConstructionControlFlag(RequestYear,
                       BudgetConstructionConstants.CSF_UPDATES_OK)));            
          return ReturnValue;
      }
      
      public boolean GLUpdatesAllowed(Integer BaseYear)
      {
          Integer RequestYear = BaseYear + 1;
          boolean ReturnValue =
          (genesisDao.getBudgetConstructionControlFlag(RequestYear,
                       BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING))||
          ((genesisDao.getBudgetConstructionControlFlag(RequestYear,
                       BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE))&&
           (genesisDao.getBudgetConstructionControlFlag(BaseYear,
                       BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK)));            
          return ReturnValue;
      }
      
      public final void stepBudgetConstructionGLLoad (Integer universityFiscalYear)
      {

          if (genesisDao.getBudgetConstructionControlFlag(universityFiscalYear,
                  Constants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING))
          {
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
          };
          if (genesisDao.getBudgetConstructionControlFlag(universityFiscalYear,
                     Constants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE) &&
                     genesisDao.getBudgetConstructionControlFlag(universityFiscalYear,
                     Constants.BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK))
          {   
              // this is the more complicated branch that updates the BC GL
              // there should be a private method called here, as in the first branch
              return;
          }
         /*
          * (1)  Get a persistence broker to maintain a single transaction
          * (2)  Get a hash map of BC HEADER account keys (we may have to build it from what
          *      is returned).
          * (3)  Get a list of GL BB rows, sorted on accounting key (which will be
          *      the same as the sort of the BC HEADER 
          * (4)  Get a list of PBGL rows, sorted on account key
          * (5)  Do a merge in software, building a new header AND a new document
          *      header every time one of the ACCOUNT KEYS in (3) does not match
          *      a key in (2).
          *      
          *  Here are the assumptions.
          *  --we can use a single broker for all the queries.
          *  --we can create a new PBGL object, a new header object, and a new doc
          *    header object, and store them when we need to
          *  --we can update and store the PBGL objects using the setters for the amount
          *  --any PBGL objects we do NOT store will not be involved in the save
          *  --nothing is actually sent to the database until a commit (is closing the 
          *    broker sufficient for that?), and presumably the SQL that does that is 
          *    not too clunky.  Should we use beginTransaction anc commitTransaction in
          *    the Spring PersistenceBrokerImpl as well?
          *  --we can use the p6spy log to see what the thing does, and since we won't
          *    have too many test rows it will be reasonable
          *  --we can just create a default package with a main method and run all this shit
          *  
          *  we want to see whether we can use the apache.ojb addColumnEqualToField in the
          *  Criteria class and a report query in a subselect to replace the code above and
          *  have the query return to us the GL BB rows that exist (or don't) in PBGL?
          *  
          *  Can we use deleteByQuery or something similar to replace our TRUNCATEs, so 
          *  a single DELETE query runs on the data base and cleans out all the tables?
          *  
          *  Are the constants going to be updated?  Some things have a "property" constant
          *  for the field name, and various codes for the value, and some do not?  There
          *  also seems to be a little overlap between the GL constants and the constants.
          *  
          *  What is the purpose of all the interfaces followed by implementations?  I assume
          *  it's not just to be cool--it's probably there to allow people to override the
          *  implementations.  If that is so, are there any rules for which methods should
          *  go in the interface (ALL public methods, say), or is it up to the developer
          *  to decide?              
          */ 
      }
      
    public void clearDBForGenesis(Integer BaseYear)
    {
        genesisDao.clearDBForGenesis(BaseYear); 
    }
      
    public void createProxyBCHeadersTransactional(Integer BaseYear)
    {
        genesisDao.primeNewBCHeadersDocumentCreation(BaseYear);
    }
 //  this step must be re-done
 //  we need (1) an intiation step that sets the flags,
 //              builds the chart, and creates the proxy
 //              documents
 //          (2) a non-transactional step (not in this service)
 //              that sets document numbers and routes the
 //              documents.  this should be called directly
 //              from geneisDao.
 //          (3) a step that does the rest (initializes the
 //              document status, builds the org hieratchy and
 //              the GL, and resets the flags
 //           THERE COULD BE A DOCUMENT STEP, AS LONG AS THE
 //           THE TRANSACTIONAL PARTS COME FROM A SERVICE AND 
 //           BUT THE DOCUMENT ROUTE IS FROM genesisDao AND IS
 //           NON_TRANSACTIONAL.   
    public void genesisStep(Integer BaseYear)
    {
        genesisDao.setControlFlagsAtTheStartOfGenesis(BaseYear);
        genesisDao.clearDBForGenesis(BaseYear);
        genesisDao.createNewBCDocumentsFromGLCSF(BaseYear,
                GLUpdatesAllowed(BaseYear), CSFUpdatesAllowed(BaseYear));
        genesisDao.createChartForNextBudgetCycle();
        genesisDao.initialLoadToPBGL(BaseYear);
        genesisDao.rebuildOrganizationHierarchy(BaseYear);
        genesisDao.setControlFlagsAtTheEndOfGenesis(BaseYear);
    }
    
    public void genesisDocumentStep (Integer BaseYear)
    {
        genesisDao.setControlFlagsAtTheStartOfGenesis(BaseYear);
        genesisDao.clearDBForGenesis(BaseYear);
//        genesisDao.primeNewBCHeadersDocumentCreation(BaseYear);
        genesisDao.createNewBCDocumentsFromGLCSF(BaseYear,
                GLUpdatesAllowed(BaseYear), CSFUpdatesAllowed(BaseYear));
    }
    
    public void genesisFinalStep (Integer BaseYear)
    {
        // this assumes that the documents have been built in genesisDocumentStep
        genesisDao.createChartForNextBudgetCycle();
        genesisDao.rebuildOrganizationHierarchy(BaseYear);
        //
        genesisDao.initialLoadToPBGL(BaseYear);
        genesisDao.setControlFlagsAtTheEndOfGenesis(BaseYear);
    }

    public void testChartCreation()
    {
        genesisDao.createChartForNextBudgetCycle();
    }
    
    public void testHierarchyCreation(Integer BaseYear)
    {
        genesisDao.rebuildOrganizationHierarchy(BaseYear);
    }

    public void setGenesisDao(GenesisDao genesisDao)
    {
        this.genesisDao = genesisDao;
    }
}
