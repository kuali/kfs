/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.budget.dao.ojb;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import java.util.Date;
import java.math.*;

import org.kuali.module.budget.dao.*;
import org.kuali.module.budget.bo.*;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.bo.FunctionControlCode;
import org.kuali.core.dao.DocumentHeaderDao;
import org.kuali.core.document.DocumentHeader;
import org.kuali.Constants;
import org.kuali.Constants.*;
import org.kuali.PropertyConstants;
import org.kuali.core.util.*;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.chart.bo.*;


import org.apache.ojb.broker.query.*;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;
import org.apache.log4j.*;


public class GenesisDaoOjb extends PersistenceBrokerDaoSupport 
             implements GenesisDao {
    /*
     *   These routines are written to try to mitigate the performance hit that
     *   comes from using OJB as opposed to JDBC (pass-through SQL).  Pass-through
     *   SQL in Kuali could lead to database-dependencies in the code, and tie Kuali
     *   to a specific RDBMS.
     *   OJB is not really suited for batch, where rows are fetched, inserted, and
     *   updated in big bunches as opposed to a few at a time.
     *   (1)  OJB in "lazy evaluation mode" (the Kuali standard for performance 
     *        reasons) will only return the row from the main table regardless of 
     *        how many "reference descriptor" joins and/or "collection descriptor"
     *        joins there may be in the OJB repository file.  So, if I query table A and
     *        reference table B, my query (in batch) might return 10,000 A rows in
     *        a single call.  None of the matching B fields will be filled in in the
     *        DAO.  If I then try to access a B field in a given instance of the DAO,
     *        Spring will do a query to fetch the relevant B row.  In essence, in batch
     *        I would do a single DB call to get the 10,000 rows of A, and 10,000 DB
     *        calls to fill in the fields from B, one for each row of A.
     *   (2)  This routine tries to do joins in java, in memory, by using what Oracle
     *        calls a "hash join".  If we want to join A and B on a key, we will get
     *        the relevant fields from A and B on separate DB calls (one for A and one
     *        for B), and create a hash map on the join key from the results.  We can
     *        then iterate through either A or B and get the relevant fields from the
     *        other table by employing the hash key.  This should be fast, since hash
     *        tables are designed for fast access.
     *   (3)  We will only store when absolutely necessary to minimize data base access.
     *        So where in Oracle we would do an UPDATE A.. WHERE (EXISTS (SELECT 1 FROM B
     *        WHERE A matches B) or an INSERT A (SELECT ... FROM A, B WHERE A = B), we will 
     *        get all the candidate rows from both A and B, and store individually to do
     *        INSERT or UPDATE.  (There seems to be now way in OJB to store more than
     *        one row at a time.)  This may lead to a lot of database calls that operate
     *        on a single row.  We can only try to minimize this problem.  We can't
     *        get around it.  
     *   This is the impression of the coder.  If anyone has other suggestions, please
     *   let us know.
     *   (One alternative might be to have many different class-desriptor tags in the
     *    OJB repository file representing table A, one for each join to table B.  If
     *    we could override lazy evaluation at the class-descriptor level, we could 
     *    code some batch-specific joins that would get everything we need in one call.
     *    The problem with this is that the A/B descriptions would then be in multiple
     *    tags, and changing them would be labor-intensive and error-prone.  If OJB
     *    repositories allow headers, we could get around this by using an entity to 
     *    describe the A fields.  This idea requires further research, on the coder's
     *    part at least.)
     */

    private FiscalYearFunctionControl fiscalYearFunctionControl;
    private FunctionControlCode functionControlCode;
    
    /*  turn on the logger for the persistence broker */
    private static Logger LOG = org.apache.log4j.Logger.getLogger(GenesisDaoOjb.class);

    /*  these constants should be in PropertyConstants */
    public final static String BUDGET_FLAG_PROPERTY_NAME = "financialSystemFunctionControlCode";
    public final static String BUDGET_FLAG_VALUE = "financialSystemFunctionActiveIndicator";
    public final static String BUDGET_CZAR_CHART = "UA";
    public final static String FINANCIAL_CHART_PROPERTY = "chartOfAccountsCode"; 
    public final static String BUDGET_CZAR_ORG = "BUDU";
    public final static String ORG_CODE_PROPERTY = "organizationCode";
    public final static String FISCAL_OFFICER_ID_PROPERTY = "accountFiscalOfficerSystemIdentifier";
    public final static String ACCOUNT_CLOSED_INDICATOR_PROPERTY = "accountClosedIndicator";

    // @@TODO maybe it isn't worth moving this home-coming queen value somewhere else
    public final static Long DEFAULT_VERSION_NUMBER = new Long(0);
    

    public final Map<String,String> getBudgetConstructionControlFlags (Integer universityFiscalYear)
    {
        /*  return the flag names and the values for all the BC flags for the fiscal year */
        
        /*  the key to the map returned will be the name of the flag
         *  the entry will be the flag's value 
         */
        Map<String, String> controlFlags = new HashMap();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME,
                                         universityFiscalYear); 
        String[] queryAttr = {BUDGET_FLAG_PROPERTY_NAME,BUDGET_FLAG_VALUE};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(FiscalYearFunctionControl.class, 
                                        queryAttr, criteriaID);
        Iterator Results = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        /* fill in the map */
        while (Results.hasNext())
        {
          String[] mapValues = (String []) ((Object []) Results.next());
          controlFlags.put(mapValues[0],mapValues[1]);
        };
        return controlFlags;        
    }
    
    public boolean getBudgetConstructionControlFlag(Integer universityFiscalYear, String FlagID)
    {
        /*  return true if a flag is on, false if it is not */
        String Result;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME,
                                         universityFiscalYear);
        criteriaID.addEqualTo(BUDGET_FLAG_PROPERTY_NAME,FlagID);
        String[] queryAttr = {BUDGET_FLAG_VALUE};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(FiscalYearFunctionControl.class, queryAttr, criteriaID, true);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(
                             queryID);
        // TODO@ we need to create an exception, put a try around this block, and log errors
        Result = (String) ((Object[]) Results.next()) [0];
        return (Result == Constants.ParameterValues.YES);
            
    }
    
    public final String getBudgetConstructionInitiatorID()
    {
        //@TODO: The constants and field names below should come from constants files
        //  the chart and department should be budget construction constants
        //  the others should be kuali constants
        final String DEFAULT_ID = "666-666-66";
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(FINANCIAL_CHART_PROPERTY, BUDGET_CZAR_CHART);
        criteriaID.addEqualTo(ORG_CODE_PROPERTY,BUDGET_CZAR_ORG);
        criteriaID.addColumnEqualTo(ACCOUNT_CLOSED_INDICATOR_PROPERTY,
                Constants.ParameterValues.NO);
        String[] queryAttr = {FISCAL_OFFICER_ID_PROPERTY};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(Account.class, queryAttr, criteriaID, true);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (!Results.hasNext())
        {
            return DEFAULT_ID;
        }
        else
        {  
           String retID = (String) ((Object[]) Results.next())[0];  
           return retID;
        }
    }
    
    /*
     *  here are the routines we will use for updating budget construction GL
     *  
     */
    // maps (hash maps) to return the results of the GL call
    // --pBGLFromGL contains all the rows returned, stuffed into an object that can be 
    //   saved to the pending budget construction general ledger
    // --bCHdrFromGL contains one entry for each potentially new key for the budget
    //   construction header table.
    private Map<String,PendingBudgetConstructionGeneralLedger>  pBGLFromGL;
    private Map<String,BudgetConstructionHeader> bCHdrFromGL;
    private Map<String,String> CurrentPBGLDocNumbers;
    private HashMap[] returnPointers;
    // these are the indexes for each of the fields returned in the select list
    // of the SQL statement
    private Integer sqlChartOfAccountsCode = 0;
    private Integer sqlAccountNumber = 1;
    private Integer sqlSubAccountNumber = 2;
    private Integer sqlObjectCode = 3;
    private Integer sqlSubObjectCode = 4;
    private Integer sqlBalanceTypeCode = 5;
    private Integer sqlObjectTypeCode = 6;
    private Integer sqlAccountLineAnnualBalanceAmount = 7;
    private Integer sqlBeginningBalanceLineAmount = 8;
    
    private Integer nGLHeadersAdded  = new Integer(0);
    private Integer nGLRowsAdded     = new Integer(0);
    private Integer nGLRowsUpdated   = new Integer(0);
    private Integer nCurrentPBGLRows = new Integer(0);
    private Integer nGLBBRowsZeroNet = new Integer(0);
    private Integer nGLBBRowsRead    = new Integer(0);
    private Integer nGLBBKeysRead    = new Integer(0);
    
    // public methods
    
    public void clearHangingBCLocks (Integer BaseYear)
    {
        // this routine cleans out any locks that might remain from people leaving
        // the application abnormally (for example, Fire! Fire!).  it assumes that
        // people are shut out of the application during a batch run, and that all
        // work prior to the batch run has either been committed or lost.
        BudgetConstructionHeader lockedDocuments;
        //
        Integer RequestYear = BaseYear+1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        Criteria lockID = new Criteria();
        //@@TODO:  add these to the PropertyConstants or at least to 
        //         BudgetConstructionConstants?    
        lockID.addNotEqualTo("budgetLockUserIdentifier",
                          BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        Criteria tranLockID = new Criteria();
        tranLockID.addNotEqualTo("budgetTransacdtionLockUserIdentifier",
                   BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        lockID.addOrCriteria(tranLockID);
        criteriaID.addAndCriteria(lockID);
        //
        QueryByCriteria queryID = 
            new QueryByCriteria(DocumentHeader.class, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        //  now just loop through and change the locks
        while (Results.hasNext())
        {
            lockedDocuments = (BudgetConstructionHeader) Results.next();
            lockedDocuments.setBudgetLockUserIdentifier(BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            lockedDocuments.setBudgetTransactionLockUserIdentifier(BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            getPersistenceBrokerTemplate().store(lockedDocuments);
        }
        
        
        
        
    }
    public void initialLoadToPBGL(Integer BaseYear)
    {
        // we will probably want several of these
        // or we will want to overload one, so people can leave their current
        // data in BC when they run genesis for the new year 
        clearBothYearsPBGL(BaseYear);
        // we have to clean out account reports to
        // it is not fiscal year-specific
        // this implies that last year's data can't be there, because the
        // organization hierarchy will have changed
        readGLForPBGL(BaseYear);
        createNewBCDocuments(BaseYear);
        addNewGLRowsToPBGL(BaseYear);
    }
    
    public void updateToPBGL(Integer BaseYear)
    {
        readGLForPBGL(BaseYear);
        updateCurrentPBGL(BaseYear);
        createNewBCDocuments(BaseYear);
        addNewGLRowsToPBGL(BaseYear);
    }
    //
    //
    // private working methods

    //
    private void addNewGLRowsToPBGL(Integer BaseYear)
    {
        // this method adds the GL rows not yet in PBGL to PBGL
        for (Map.Entry<String,PendingBudgetConstructionGeneralLedger> newPBGLRows :
             pBGLFromGL.entrySet())
        {
             PendingBudgetConstructionGeneralLedger rowToAdd = newPBGLRows.getValue();
             String headerKey = buildHeaderTestKeyFromPBGL(rowToAdd);
             // the document number should be in either the new document headers just
             // added or in the list of document numbers from the existing PBGL rows
             // if it isn't, we issue a warning and go on, but this is an error
             String docNumber = findRowDocumentNumber(headerKey);
             if (docNumber == null)
             {
                 LOG.warn(String.format("\nno document number found for BB GL key (%s,%s,%s)",
                          rowToAdd.getChartOfAccountsCode(),
                          rowToAdd.getAccountNumber(),
                          rowToAdd.getSubAccountNumber()));
                 continue;
             }
             nGLHeadersAdded = nGLHeadersAdded+1;
             rowToAdd.setDocumentNumber(docNumber);
             getPersistenceBrokerTemplate().store(rowToAdd);
        }
    }
    //
    // these two methods build the GL field string that triggers creation of a new
    // pending budget construction general ledger row
    private String buildGLTestKeyFromPBGL (PendingBudgetConstructionGeneralLedger
            pendingBudgetConstructionGeneralLedger)
    {
       String PBGLTestKey = new String();
       PBGLTestKey = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode()+
                         pendingBudgetConstructionGeneralLedger.getAccountNumber()+
                         pendingBudgetConstructionGeneralLedger.getSubAccountNumber()+
                         pendingBudgetConstructionGeneralLedger.getFinancialObjectCode()+
                         pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode()+
                         pendingBudgetConstructionGeneralLedger.getFinancialBalanceTypeCode()+
                         pendingBudgetConstructionGeneralLedger.getFinancialObjectTypeCode();
       return PBGLTestKey;
    }
    private String buildGLTestKeyFromSQLResults (Object[] sqlResult)
    {
        String GLTestKey = new String();
        GLTestKey = (String) sqlResult[sqlChartOfAccountsCode]+
                    (String) sqlResult[sqlAccountNumber]+
                    (String) sqlResult[sqlSubAccountNumber]+
                    (String) sqlResult[sqlObjectCode]+
                    (String) sqlResult[sqlSubObjectCode]+
                    (String) sqlResult[sqlBalanceTypeCode]+
                    (String) sqlResult[sqlObjectTypeCode];
        return GLTestKey;
    }
    //
    // these two methods build the GL field string that triggers creation of a new
    // budget construction header
    public String buildHeaderTestKeyFromPBGL (PendingBudgetConstructionGeneralLedger
            pendingBudgetConstructionGeneralLedger)
            {
               String headerBCTestKey = new String();
               headerBCTestKey = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode()+
                                 pendingBudgetConstructionGeneralLedger.getAccountNumber()+
                                 pendingBudgetConstructionGeneralLedger.getSubAccountNumber();
               return headerBCTestKey;
            }
    private String buildHeaderTestKeyFromSQLResults (Object[] sqlResult)
    {
        String headerBCTestKey = new String();
        headerBCTestKey = (String) sqlResult[sqlChartOfAccountsCode]+
                          (String) sqlResult[sqlAccountNumber]+
                          (String) sqlResult[sqlSubAccountNumber];
        return headerBCTestKey;
    }
    
    private void clearBaseYearPBGL(Integer BaseYear)
    {
        // remove rows from the base year
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryID = 
            new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
                    criteriaID);
        LOG.info(String.format("delete PBGL started at %tT for %d",new Date(),
                BaseYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.info(String.format("delete PBGL ended at %tT",new Date()));
    }
    
    private void clearBothYearsPBGL(Integer BaseYear)
    {
        clearBaseYearPBGL(BaseYear);
        clearRequestYearPBGL(BaseYear);
    }
    
    private void clearRequestYearPBGL(Integer BaseYear)
    {
        Integer RequestYear = BaseYear + 1;
        // remove rows from the request year
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryID = 
            new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
                    criteriaID);
        LOG.info(String.format("\ndelete PBGL started at %tT for %d",new Date(),
                RequestYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.info(String.format("\ndelete PBGL ended at %tT",new Date()));
    }
    
    private void createNewBCDocuments(Integer BaseYear)
    {
        // this routine reads the list of GL document accounting keys from
        // the GL that are not in the budget, and creates both a workflow
        // document and a budget construction header for them
        for (Map.Entry<String,BudgetConstructionHeader> newBCHdrRows :
            bCHdrFromGL.entrySet())
        {
            
        }
    }
    
    private String findRowDocumentNumber(String headerKey)
    {
        String documentNumber = CurrentPBGLDocNumbers.get(headerKey);
        
        if (documentNumber == null)
        {
            BudgetConstructionHeader headerFromGL = bCHdrFromGL.get(headerKey);
            if (headerFromGL == null)
            {
               return documentNumber;
            }
            // doing it this way will detect a GL row with a document number that
            // is still null
            documentNumber = headerFromGL.getDocumentNumber();
        }
        return documentNumber;
    }
    
    private BudgetConstructionHeader newBCHdrBusinessObject(Integer RequestYear,
            Object[] sqlResult)
    {
       BudgetConstructionHeader hDrBC = new BudgetConstructionHeader();
       // document number will be set in the service--we don't yet know whether this
       // will require a new document
       // @@TODO: get the "org of level" from account reports to
       hDrBC.setUniversityFiscalYear(RequestYear);
       hDrBC.setChartOfAccountsCode((String) sqlResult[sqlChartOfAccountsCode]);
       hDrBC.setAccountNumber((String) sqlResult[sqlAccountNumber]);
       hDrBC.setSubAccountNumber((String) sqlResult[sqlSubAccountNumber]);
       // at the account level, this is the same as the chart
       // all new headers start at the account level, and this object will only be used
       // if a new header is required.
       //  the organization and chart are always NULL at the account level
       hDrBC.setOrganizationLevelChartOfAccountsCode(BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE);
       hDrBC.setOrganizationLevelOrganizationCode(BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE);
       hDrBC.setOrganizationLevelCode(BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CODE);
       hDrBC.setBudgetTransactionLockUserIdentifier(BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
       hDrBC.setBudgetLockUserIdentifier(BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
       hDrBC.setVersionNumber(DEFAULT_VERSION_NUMBER);
   //  we need to initialize the document number to null, and set it later
   //  if it doesn't get set later, the code will detect the failure and issue a warning    
       hDrBC.setDocumentNumber("");
   //  return a new header
       return hDrBC;
    }
    
    private PendingBudgetConstructionGeneralLedger newPBGLBusinessObject(Integer RequestYear,
                                                                         Object[] sqlResult)
    {
       PendingBudgetConstructionGeneralLedger PBGLObj = new PendingBudgetConstructionGeneralLedger();
     /*  
      * the document number will be set later if we have to store this in a new document
      * a new row in an existing document will take it's document number from the existing document
      * otherwise (existing document, existing row), the only field in this that will be
      * the beginning balance amount
     */  
       PBGLObj.setUniversityFiscalYear(RequestYear);
       PBGLObj.setChartOfAccountsCode((String) sqlResult[sqlChartOfAccountsCode]);
       PBGLObj.setAccountNumber((String) sqlResult[sqlAccountNumber]);
       PBGLObj.setSubAccountNumber((String) sqlResult[sqlSubAccountNumber]);
       PBGLObj.setFinancialObjectCode((String) sqlResult[sqlObjectCode]);
       PBGLObj.setFinancialSubObjectCode((String) sqlResult[sqlSubObjectCode]);
       PBGLObj.setFinancialBalanceTypeCode((String) sqlResult[sqlBalanceTypeCode]);
       PBGLObj.setFinancialObjectTypeCode((String) sqlResult[sqlObjectTypeCode]);
       KualiDecimal BaseAmount = 
           (KualiDecimal) sqlResult[sqlBeginningBalanceLineAmount];
       BaseAmount = 
           BaseAmount.add((KualiDecimal) sqlResult[sqlAccountLineAnnualBalanceAmount]);
       PBGLObj.setFinancialBeginningBalanceLineAmount(BaseAmount);
       PBGLObj.setAccountLineAnnualBalanceAmount(KualiDecimal.ZERO);
       //  ObjectID is set in the BusinessObjectBase on insert and update
       //  but, we must set the version number
       PBGLObj.setVersionNumber(DEFAULT_VERSION_NUMBER);
       return PBGLObj;
    }
    
    private void readGLForPBGL(Integer BaseYear)
    {
        // we apparently need to configure the log file in order to use it
        // @@TODO: should these be a "weak hash map", to optimize memory use?
       pBGLFromGL  = new HashMap();
       bCHdrFromGL = new HashMap();
       Integer RequestYear = BaseYear + 1;
        //
        //  set up a report query to fetch all the GL rows we are going to need
        Criteria criteriaID = new Criteria();
        // we only pick up a single balance type
        // we also use an integer fiscal year
        // *** this is a point of change if either of these criteria change ***
        // @@TODO We should regularize the sources for these constants
        // they should probably all come from GL (although UNIV_FISCAL_YR is generic)
        // we should add the two hard-wired strings at the bottom to GLConstants
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        criteriaID.addEqualTo(PropertyConstants.BALANCE_TYPE_CODE,
                              Constants.BALANCE_TYPE_BASE_BUDGET);
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.SUB_ACCOUNT_NUMBER,
                              PropertyConstants.OBJECT_CODE,
                              PropertyConstants.SUB_OBJECT_CODE,
                              PropertyConstants.BALANCE_TYPE_CODE,
                              PropertyConstants.OBJECT_TYPE_CODE,
                              PropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT,
                              PropertyConstants.BEGINNING_BALANCE_LINE_AMOUNT};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(Balance.class, queryAttr, criteriaID, true);
        //
        // set up the hashmaps by iterating through the results
        
        // @@TODO this should be in a try/catch structure.  We should catch a 
        //        SQL error, write it to the log, and raise a more generic error
        //        ("error reading GL Balance Table in BC batch"), and throw that
        LOG.info("\nGL Query started: "+String.format("%tT",new Date()));
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        LOG.info("\nGL Query finished: "+String.format("%tT",new Date()));
        while (Results.hasNext())
        {
            Object[] ReturnList = (Object []) Results.next();
            LOG.debug(String.format("\nfields returned = %d\n",ReturnList.length));
            LOG.debug(String.format("\nvalue in last field = %s\n",
                    ReturnList[sqlBeginningBalanceLineAmount].toString()));
            //
            //  exclude any rows where the amounts add to 0
            //  (we don't do it in the WHERE clause to be certain we are ANSI standard)
            KualiDecimal BaseAmount = 
                (KualiDecimal) ReturnList[sqlBeginningBalanceLineAmount];
            BaseAmount = 
                BaseAmount.add((KualiDecimal) ReturnList[sqlAccountLineAnnualBalanceAmount]);
            if (BaseAmount.isZero())
            {
                nGLBBRowsRead = nGLBBRowsRead+1;
                nGLBBRowsZeroNet = nGLBBRowsZeroNet+1;
                continue;
            }
            //  
            //  we always need to build a new PGBL object
            //  we have selected the entire key from GL_BALANCE_T
            //  @@TODO we should throw an exception if the key already exists
            //  this means the table has changed and this code needs to be re-written
            String GLTestKey = buildGLTestKeyFromSQLResults(ReturnList);
            pBGLFromGL.put(GLTestKey,
                     newPBGLBusinessObject(RequestYear,ReturnList));
            //  we only add a BC Header Object for a unique chart/account/subaccount
            String HeaderTestKey = buildHeaderTestKeyFromSQLResults(ReturnList);
            if (bCHdrFromGL.get(HeaderTestKey) == null)
            {
                // add a BCHeader object
                bCHdrFromGL.put(HeaderTestKey,
                          newBCHdrBusinessObject(RequestYear,ReturnList));
            }
        }
        nGLBBRowsRead = nGLBBRowsRead+pBGLFromGL.size();
        nGLBBKeysRead = bCHdrFromGL.size();
        LOG.info("\nHash maps built: "+String.format("%tT",new Date()));
        LOG.info(String.format("\nGL detail hashmap size = %d",pBGLFromGL.size()));
        LOG.info(String.format("\nGL keys hashmap size = %d",bCHdrFromGL.size()));
    }
    
    private void removeDuplicateHeader(PendingBudgetConstructionGeneralLedger currentPBGLInstance)
    {
        String TestKey = buildHeaderTestKeyFromPBGL(currentPBGLInstance);
        bCHdrFromGL.remove(TestKey);
    }
    
    private void saveCurrentPGBLDocNumber(PendingBudgetConstructionGeneralLedger currentPBGLInstance)
    {
       String testKey = buildHeaderTestKeyFromPBGL(currentPBGLInstance);
       if (CurrentPBGLDocNumbers.get(testKey) == null)
       {
           CurrentPBGLDocNumbers.put(testKey,currentPBGLInstance.getDocumentNumber());
       }
    }    
    
    private void updateBaseBudgetAmount(PendingBudgetConstructionGeneralLedger currentPBGLInstance)
    {
       String TestKey = buildGLTestKeyFromPBGL(currentPBGLInstance);
       if (!pBGLFromGL.containsKey(TestKey))
       {
           return;
       }
       PendingBudgetConstructionGeneralLedger matchFromGL = pBGLFromGL.get(TestKey);
       KualiDecimal baseFromCurrentGL = 
           matchFromGL.getFinancialBeginningBalanceLineAmount();
       KualiDecimal baseFromPBGL = 
           currentPBGLInstance.getFinancialBeginningBalanceLineAmount();
       // remove the candidate GL from the hash list
       // it won't match with anything else
       // it should NOT be inserted into the PBGL table
       pBGLFromGL.remove(TestKey);
       if (baseFromCurrentGL == baseFromPBGL)
       {
           // no need to update--false alarm
           return;
       }
       // update the base amount and store the updated PBGL row
       nGLRowsUpdated =nGLRowsUpdated+1;
       currentPBGLInstance.setFinancialBeginningBalanceLineAmount(baseFromCurrentGL);
       getPersistenceBrokerTemplate().store(currentPBGLInstance);
    }
    
    private void updateCurrentPBGL(Integer BaseYear)
    {
       Integer RequestYear = BaseYear+1;
       
       CurrentPBGLDocNumbers = new HashMap();
       
       // what we are going to do here is what Oracle calls a hash join
       //
       // we will merge the current PBGL rows with the GL detail, and 
       // replace the amount on each current PBGL row which matches from
       // the GL row, and remove the GL row 
       //
       // we will compare the GL Key row with the the current PBLG row,
       // and if the keys are the same, we will eliminate the GL key row
       //
       // we will save the document number of the PBGL row before we store it
       // back, so we can use a current document number for those GL detail rows
       // (basically new object classes) which belong to a current document but
       // are not in PBGL.
       //
       //  fetch the current BC header rows
       Criteria criteriaID = new Criteria();
       criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
       QueryByCriteria queryID = 
           new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
                               criteriaID);
       Iterator Results = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
       //  loop through the results
       while (Results.hasNext())
       {
           nCurrentPBGLRows = nCurrentPBGLRows+1;
           PendingBudgetConstructionGeneralLedger currentPBGLInstance =
               (PendingBudgetConstructionGeneralLedger) Results.next();
           // first save the doc number
           saveCurrentPGBLDocNumber(currentPBGLInstance);
           // remove any new header rows which match this PBGL row on the
           // header accounting key.  A BC header (and a document header) 
           // already exist if such a match occurs.
           removeDuplicateHeader(currentPBGLInstance);
           // update the base amount and store the result if necessary
           updateBaseBudgetAmount(currentPBGLInstance);
       }
    }
}
