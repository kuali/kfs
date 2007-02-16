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
package org.kuali.module.budget.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.Constants.BudgetConstructionConstants;
import org.kuali.Constants.ParameterValues;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.budget.bo.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.module.budget.bo.BudgetConstructionAccountReports;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTrackerOverride;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.GenesisDao;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.bo.FunctionControlCode;
import org.kuali.module.gl.GLConstants.ColumnNames;
import org.kuali.module.gl.bo.Balance;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.exception.WorkflowException;


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
     *    tags, and changing them would be labor-intensive and error-prone.  But OJB
     *    repositories allow headers, so we could get around this by using an entity to 
     *    describe the A fields.  The entity would be in one place, so changes to the A
     *    fields could also be made in one place.  The foreignkey field-ref tag B fields
     *    are repeated in every description anyway, so things aren't always in one place
     *    to begin with.)
     */

    private FiscalYearFunctionControl fiscalYearFunctionControl;
    private FunctionControlCode functionControlCode;
    
    /*  turn on the logger for the persistence broker */
    private static Logger LOG = org.apache.log4j.Logger.getLogger(GenesisDaoOjb.class);


    // @@TODO maybe it isn't worth moving these home-coming queen values somewhere else
    //        maybe we don't need the second one at all
    public final static Long DEFAULT_VERSION_NUMBER = new Long(1);
    public final static Integer MAXIMUM_ORGANIZATION_TREE_DEPTH = new Integer(1000);

    /*
     *  this is old stuff which we may not use--we'll see
     */
    
    /*  these constants should be in PropertyConstants */
    public final static String BUDGET_FLAG_PROPERTY_NAME = "financialSystemFunctionControlCode";
    public final static String BUDGET_FLAG_VALUE = "financialSystemFunctionActiveIndicator";
    public final static String BUDGET_CZAR_CHART = "UA";
    public final static String FINANCIAL_CHART_PROPERTY = "chartOfAccountsCode"; 
    public final static String BUDGET_CZAR_ORG = "BUDU";
    public final static String ORG_CODE_PROPERTY = "organizationCode";
    public final static String FISCAL_OFFICER_ID_PROPERTY = "accountFiscalOfficerSystemIdentifier";
    public final static String ACCOUNT_CLOSED_INDICATOR_PROPERTY = "accountClosedIndicator";

    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private DateTimeService dateTimeService; 
    private DocumentDao documentDao;
    
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
        return (Result.compareTo(Constants.ParameterValues.YES) == 0);
            
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
     * ******************************************************************************  
     *   (1) these routines are used to create and set the control flags for budget *
     *   construction                                                               *
     * ******************************************************************************  
     */
    
    public void setControlFlagsAtTheStartOfGenesis(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        //
        // first we have to eliminate anything for the new year that's there now
        getPersistenceBrokerTemplate().clearCache();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(FiscalYearFunctionControl.class,
                                      criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        getPersistenceBrokerTemplate().clearCache();
       // 
       //  the default values (except for the BUDGET_CONSTRUCTION_GENESIS_RUNNING flag)
       //  come from the function control code table
       FiscalYearFunctionControl SLF;
       criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
       String[] attrQ = {PropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE,
                         PropertyConstants.FINANCIAL_SYSTEM_FUNCTION_DEFAULT_INDICATOR};
       ReportQueryByCriteria rptQueryID = new ReportQueryByCriteria(FunctionControlCode.class,
                                       attrQ,criteriaID);
       Integer sqlFunctionControlCode     = 0;
       Integer sqlFunctionActiveIndicator = 1;
       // run the query
       Iterator Results = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rptQueryID);
       while (Results.hasNext())
       {
           SLF = new FiscalYearFunctionControl();
           Object[] resultFields = (Object[]) Results.next();
           String flagTag     = (String) resultFields[sqlFunctionControlCode];
 //          String flagDefault = (String) resultFields[sqlFunctionActiveIndicator];
 //  apparently OJB is smart enough to bring this in as a boolean
           boolean flagDefault = (Boolean) resultFields[sqlFunctionActiveIndicator];
           SLF.setUniversityFiscalYear(RequestYear);
           LOG.debug("\nfiscal year has been set");
           SLF.setFinancialSystemFunctionControlCode(flagTag);
           LOG.debug("\nfunction code has been set");
           SLF.setVersionNumber(DEFAULT_VERSION_NUMBER);
           LOG.debug(String.format("\nversion number set to %d",
                                  SLF.getVersionNumber()));
           if (flagTag.equals( 
               BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING))
           {
               SLF.setFinancialSystemFunctionActiveIndicator(true);
           }
           else
           {
//               SLF.setFinancialSystemFunctionActiveIndicator(
//                       ((flagDefault == Constants.ParameterValues.YES)? true : false));
                 SLF.setFinancialSystemFunctionActiveIndicator(flagDefault);
           }
           LOG.debug("\nabout to store the result");
           getPersistenceBrokerTemplate().store(SLF);
       }
    }
    
    public void setControlFlagsAtTheEndOfGenesis(Integer BaseYear)
    {
        Integer RequestYear = BaseYear + 1;
        resetExistingFlags(BaseYear,
                           BudgetConstructionConstants.CURRENT_FSCL_YR_CTRL_FLAGS);
        resetExistingFlags(RequestYear,
                           BudgetConstructionConstants.NEXT_FSCL_YR_CTRL_FLAGS_AFTER_GENESIS);
    }
    
    //  this method just reads the existing flags and changes their values
    //  based on the configuration constants
    public void resetExistingFlags (Integer Year,
                                    HashMap<String,String> configValues)
    {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,Year);
        QueryByCriteria queryID = 
            new QueryByCriteria(FiscalYearFunctionControl.class,criteriaID);
        Iterator Results = 
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (Results.hasNext())
        {
          LOG.debug("\nbefore call to next() and cast");  
          FiscalYearFunctionControl SLF = (FiscalYearFunctionControl) Results.next();
          LOG.debug("\nafter call to next()");
          String mapKey = SLF.getFinancialSystemFunctionControlCode();
          String newValue = configValues.get(mapKey);
          SLF.setFinancialSystemFunctionActiveIndicator(
                  ((newValue.equals(ParameterValues.YES))? true : false));
          LOG.debug("\nabout to store the result");
          getPersistenceBrokerTemplate().store(SLF);
          LOG.debug("\nafter store");
        }
    }
    
    /*
     *  ****************************************************************  
     *  (2) intialization for genesis                                  *
     *  these methods clean out the PBGL and document tables.          *
     *  BC only allows one fiscal year at a time                       *
     *  (this could be modified to clear things out by fiscal year)    *
     *  it should be modified to add more tables                       * 
     *  ****************************************************************
     */
    public void clearDBForGenesis(Integer BaseYear)
    {
        clearBothYearsPBGL(BaseYear);
        clearBothYearsHeaders(BaseYear);
    }
    
    private void clearBaseYearHeaders(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearBothYearsHeaders(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearHeaders()
    {
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class,
                                                     QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
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
        LOG.debug(String.format("delete PBGL started at %tT for %d",dateTimeService.getCurrentDate(),
                BaseYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.debug(String.format("delete PBGL ended at %tT",dateTimeService.getCurrentDate()));
    }
    
    private void clearBothYearsPBGL(Integer BaseYear)
    {
        clearBaseYearPBGL(BaseYear);
        clearRequestYearPBGL(BaseYear);
    }

    private void clearPBGL()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
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
        LOG.debug(String.format("\ndelete PBGL started at %tT for %d",dateTimeService.getCurrentDate(),
                RequestYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.debug(String.format("\ndelete PBGL ended at %tT",dateTimeService.getCurrentDate()));
    }
    
    /* 
     *  ****************************************************************************
     *  (3) BC Document Creation                                                   *
     *  ****************************************************************************
     */
    
    //
    //  these methods are used to create BC documents outside of transactional
    //  control.  we will create all the needed documents based on what needs to be done,
    //  and route them.
    //
    //
    //  this is convoluted, but we are trying to minimize database calls
    //  (1) The GL BALANCE is joined to 8 other tables, so each GL BALANCE row that
    //      we select in a "persistable" query requires 9 data base calls
    //  (2) A report query can return the keys and the amounts we need in a single call
    //  (3) The persistence broker access we are allowed on this project will only allow
    //      a report query to return an iterator.  One cannot return an iterator in a 
    //      non-transactional context.
    //  (4) Because workflow is a remote transaction, and must read what we write, we
    //      cannot call workflow in the middle of a transaction--we must commit first.
    //  (5) Each BC Header row is unique by fiscal year, chart, account, sub account.
    //
    //   So, since we are trying to minimize data base calls, we will do the following
    //   Transactional:
    //      * Read the GL with an iterator and decide which keys require a new header.
    //      * Store the new header with a proxy document number (just the header, not
    //        the Kuali document header that accompanies it).
    //   Non-transactional:
    //      * Read all the headers with proxy document numbers into a collection.
    //      * Delete all the headers with proxy document numbers.
    //      * Create a new Budget Construction Document for each header in the collection,
    //        and store and route it.
    //   Transactional:
    //      * We are now assured that all required documents exist.  We will now do the
    //        actual data processing to create the PBGL rows.
    
    private HashSet<String> currentBCHeaderKeys;
    private HashMap<String,BudgetConstructionHeader> newHeaderFromGL =
        new HashMap<String,BudgetConstructionHeader>(
                Constants.BudgetConstructionConstants.ESTIMATED_BUDGET_CONSTRUCTION_DOCUMENT_COUNT);
    private Collection<BudgetConstructionHeader> newBCDocumentSource;
    // these routines are used to merge CSF and CSF Override
    private HashMap<String,String[]> CSFTrackerKeys;
    // this saves our document numbers so we can update the status codes at the end
    // of the route process--we want this to be FIFO, so workflow flows undisturbed
    // (ulitmately, as will everything else, to the sea)
    private ArrayList<String> newBCDocumentNumbers = 
        new ArrayList<String>(
                Constants.BudgetConstructionConstants.ESTIMATED_BUDGET_CONSTRUCTION_DOCUMENT_COUNT);
    // counters
    Long documentsToCreateinNTS    = new Long(0);
    Long documentsSkippedinNTS     = new Long(0);
    Long documentsCreatedinNTS     = new Long(0);
    Long documentsCSFCreatedinNTS  = new Long(0);
    Long documentsGLCreatedinNTS   = new Long(0);
    
    Long proxyCandidatesReadinTS   = new Long(0);
    Long proxyBCHeadersCreatedinTS = new Long(0);
    
    private String proxyDocumentNumber = new String("-");
 
 //
 // this is the new document creation mechanism that works with embedded workflow
    public void createNewBCDocumentsFromGLCSF (Integer BaseYear,
                                               Boolean GLUpdatesAllowed,
                                               Boolean CSFUpdatesAllowed)
    {
        if ((!GLUpdatesAllowed)&&(!CSFUpdatesAllowed))
        {
            // no new documents need to be created
            return;
        }
        Integer RequestYear = BaseYear+1;
        // fetch the keys currently in budget construction header
        getCurrentBCHeaderKeys(BaseYear);
        //
        //  we have to read the GL BALANCE (which is not proxy=true) to create
        //  new BC header objects.  we use a report query to avoid triggering
        //  nine separate reads for each row, and to avoid returning the entire
        //  field list when we only need a few fields.
        if (GLUpdatesAllowed)
        {
        getAndStoreCurrentGLBCHeaderCandidates(BaseYear);
        }
        //  we also have to read CSF for any accounts with no base budget in GL BALANCE
        //  but which pay people in budgeted positions
        if (CSFUpdatesAllowed)
        {
          getCSFCandidateDocumentKeys(BaseYear);
          getCSFOverrideDeletedKeys(BaseYear);
          getCSFOverrideCandidateDocumentKeys(BaseYear);
        getAndStoreCurrentCSFBCHeaderCandidates(BaseYear);
    }
        //  now we have to read the newly created documents (after workflow is
        //  finished with them, and change the status flag to correspond to the
        //  budget construction "untouched" status
        storeBudgetConstructionDocumentsInitialStatus();
    }

    //  here are the private methods that go with it      
    private void getAndStoreCurrentCSFBCHeaderCandidates(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        for (Map.Entry<String, String[]> newCSFDocs: CSFTrackerKeys.entrySet())
        {
            // all the CSF keys in the map require new documents
            proxyCandidatesReadinTS = proxyCandidatesReadinTS+1;
            String[] Results = newCSFDocs.getValue();
            // set up the Budget Construction Header
            BudgetConstructionDocument newBCHdr;
            try
            {
            newBCHdr = (BudgetConstructionDocument)
            documentService.getNewDocument(
                    BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
            }
            catch (WorkflowException wex)
            {
                LOG.warn(String.format(
                        "\nskipping creation of document for CSF key: %s %s %s \n(%s)\n",
                        Results[0], Results[1], Results[2], wex.getMessage()));
                documentsSkippedinNTS = documentsSkippedinNTS+1;
                continue;
            }
            newBCHdr.setUniversityFiscalYear(RequestYear);
            newBCHdr.setChartOfAccountsCode(Results[0]);
            newBCHdr.setAccountNumber(Results[1]);
            newBCHdr.setSubAccountNumber(Results[2]);
            //  store the document
            try
            {
            storeANewBCDocument(newBCHdr);
            }
            catch (WorkflowException wex)
            {
                LOG.warn(String.format(
                        "\nskipping creation of document for CSF key: %s %s %s \n(%s)\n",
                        newBCHdr.getChartOfAccounts(),
                        newBCHdr.getAccountNumber(),
                        newBCHdr.getSubAccountNumber(),
                        wex.getMessage()));
                documentsSkippedinNTS = documentsSkippedinNTS+1;
                continue;

            }
            documentsCSFCreatedinNTS = documentsCSFCreatedinNTS+1;
            documentsCreatedinNTS = documentsCreatedinNTS+1;
            //  add this header to the current BC Header map
            // String testKey = Results[0]+Results[1]+Results[2];
            // currentBCHeaderKeys.add(testKey);
        }
    }
    
    private void getAndStoreCurrentGLBCHeaderCandidates(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        // first build a document set from GL BALANCE
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addEqualTo(PropertyConstants.BALANCE_TYPE_CODE,
                              Constants.BALANCE_TYPE_BASE_BUDGET);
        String newAttr = ColumnNames.BEGINNING_BALANCE+"-"+
                         ColumnNames.ANNUAL_BALANCE;
        criteriaId.addNotEqualTo(newAttr,0);
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.SUB_ACCOUNT_NUMBER};
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(Balance.class,
                                                                  queryAttr,
                                                                  criteriaId,
                                                                  true);
        Iterator RowsReturned = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        while (RowsReturned.hasNext())
        {
            proxyCandidatesReadinTS = proxyCandidatesReadinTS+1;
            Object[] Results = (Object[]) RowsReturned.next();
            String testKey = ((String) Results[0])+
                             ((String) Results[1])+
                             ((String) Results[2]);
            if (currentBCHeaderKeys.contains(testKey))
            {
                // don't create a new row for anything with a current header
                continue;
            }
            // set up the Budget Construction Header
            BudgetConstructionDocument newBCHdr;
            try
            {
            newBCHdr = (BudgetConstructionDocument)
            documentService.getNewDocument(
                    BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
            }
            catch (WorkflowException wex)
            {
                LOG.warn(String.format(
                        "\nskipping creation of document for GL key: %s %s %s \n(%s)\n",
                        (String) Results[0],
                        (String) Results[1],
                        (String) Results[2],
                        wex.getMessage()));
                documentsSkippedinNTS = documentsSkippedinNTS+1;
                continue;
            }
            newBCHdr.setUniversityFiscalYear(RequestYear);
            newBCHdr.setChartOfAccountsCode((String) Results[0]);
            newBCHdr.setAccountNumber((String) Results[1]);
            newBCHdr.setSubAccountNumber((String) Results[2]);
            //  store the document
            try
            {
            storeANewBCDocument(newBCHdr);
            }
            catch (WorkflowException wex)
            {
                LOG.warn(String.format(
                        "\nskipping creation of document for GL key: %s %s %s \n(%s)\n",
                        newBCHdr.getChartOfAccounts(),
                        newBCHdr.getAccountNumber(),
                        newBCHdr.getSubAccountNumber(),
                        wex.getMessage()));
                documentsSkippedinNTS = documentsSkippedinNTS+1;
                continue;

            }
            documentsGLCreatedinNTS = documentsGLCreatedinNTS+1;
            documentsCreatedinNTS = documentsCreatedinNTS+1;
            //  add this header to the current BC Header map
            currentBCHeaderKeys.add(testKey);
        }
    }
    
    public void getCSFCandidateDocumentKeys(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addEqualTo(PropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.SUB_ACCOUNT_NUMBER};
        ReportQueryByCriteria queryId = 
            new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class,
                                      queryAttr, criteriaId, true);
        Iterator rowsReturned = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        // decide which keys from CSF need to create new documents
        // we have already created new documents for all the GL keys
        while (rowsReturned.hasNext())
        {
            Object [] returnedRow = (Object []) rowsReturned.next();
            String testKey = ((String) returnedRow[0])+
                             ((String) returnedRow[1])+
                             ((String) returnedRow[2]);
            if (currentBCHeaderKeys.contains(testKey))
            {
                //  there is no need to create a row for this key
                continue;
            }
            String[] valueCSF = {(String) returnedRow[0],
                                 (String) returnedRow[1],
                                 (String) returnedRow[2]};
            CSFTrackerKeys.put(testKey, valueCSF);
        }
    }
    
    public void getCSFOverrideCandidateDocumentKeys(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addEqualTo(PropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.SUB_ACCOUNT_NUMBER};
        ReportQueryByCriteria queryId = 
            new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                      queryAttr, criteriaId, true);
        Iterator rowsReturned = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        // decide which keys from CSF override need to create new documents
        // we have already read in the CSF keys--existing keys need not be replaced
        // new active keys from CSF override should be added
        while (rowsReturned.hasNext())
        {
            Object [] returnedRow = (Object []) rowsReturned.next();
            String testKey = ((String) returnedRow[0])+
                             ((String) returnedRow[1])+
                             ((String) returnedRow[2]);
            if (currentBCHeaderKeys.contains(testKey))
            {
                //  there is no need to create a row for this key
                //  it is already in the base budget in the GL
                continue;
            }
            String[] valueCSF = {(String) returnedRow[0],
                                 (String) returnedRow[1],
                                 (String) returnedRow[2]};
            CSFTrackerKeys.put(testKey, valueCSF);
        }
    }
    
    public void getCSFOverrideDeletedKeys(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addNotEqualTo(PropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.SUB_ACCOUNT_NUMBER};
        ReportQueryByCriteria queryId = 
            new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                      queryAttr, criteriaId, true);
        Iterator rowsReturned = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        // decide which keys from CSF override need to create new documents
        // we have already read in the CSF keys--any overrides of existing CSF
        // which carry a delete code should be tentatively removed CSF key table
        while (rowsReturned.hasNext())
        {
            Object [] returnedRow = (Object []) rowsReturned.next();
            String testKey = ((String) returnedRow[0])+
                             ((String) returnedRow[1])+
                             ((String) returnedRow[2]);
            if (currentBCHeaderKeys.contains(testKey))
            {
                //  this key is in the GL base budget
                //  it should create a document whether anyone is paid from it
                //  or not
                continue;
            }
            if (CSFTrackerKeys.containsKey(testKey))
            {
                // an override row deletes a key in CSF
                // we tentatively remove this key from the map
                // if there is an active override row for this key as well, it 
                // will be restored when we read the active override keys
                CSFTrackerKeys.remove(testKey);
            }
        }
    }

    public void storeANewBCDocument(BudgetConstructionDocument newBCHdr)
    throws WorkflowException
    {
        newBCHdr.setOrganizationLevelChartOfAccountsCode(
                BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE);
        newBCHdr.setOrganizationLevelOrganizationCode(
                BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE);
        newBCHdr.setOrganizationLevelCode(
                BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CODE);
        newBCHdr.setBudgetTransactionLockUserIdentifier(
                BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        newBCHdr.setBudgetLockUserIdentifier(
                BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        newBCHdr.setVersionNumber(DEFAULT_VERSION_NUMBER);
        DocumentHeader kualiDocumentHeader = newBCHdr.getDocumentHeader();
        newBCHdr.setDocumentNumber(newBCHdr.getDocumentHeader().getDocumentNumber());
        kualiDocumentHeader.setOrganizationDocumentNumber(
                            newBCHdr.getUniversityFiscalYear().toString());
        kualiDocumentHeader.setFinancialDocumentStatusCode(
                Constants.INITIAL_KUALI_DOCUMENT_STATUS_CD);
        kualiDocumentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        kualiDocumentHeader.setFinancialDocumentDescription(String.format("%s %d %s %s",
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION,
                       newBCHdr.getUniversityFiscalYear(),
                       newBCHdr.getChartOfAccountsCode(),newBCHdr.getAccountNumber()));
        kualiDocumentHeader.setExplanation(
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION);
        getPersistenceBrokerTemplate().store(newBCHdr);
        documentService.prepareWorkflowDocument(newBCHdr);
        workflowDocumentService.route(newBCHdr.getDocumentHeader().getWorkflowDocument(),
                                      "created by Genesis",null);
        // save the document number
        // after all the documents are created, we will read each one in turn
        // (in order of creation) and change the status.
        // there needs to be a lag in doing this, to allow workflow to route the
        // document (which apparently takes three separate read/save sequences).
        newBCDocumentNumbers.add(newBCHdr.getDocumentNumber());
   }
    
 // this is the non-transactional public method
 // called in GenesisRouteService
    
    public void createNewBCDocuments(Integer BaseYear)
    {
        stepToGetProxyDocumentNumberRows(BaseYear);
        stepToStoreNewDocuments(BaseYear);
        LOG.info(String.format("\nstub BC Headers removed: %d",documentsToCreateinNTS));
        LOG.info(String.format("\n  new documents created: %d",documentsCreatedinNTS));
        LOG.info(String.format("\n  new document failures: %d",documentsSkippedinNTS));
    }
 
//  this is the transactional public method
//  called from GenesisService    
    
    public void primeNewBCHeadersDocumentCreation(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        // fetch the keys currently in budget construction header
        getCurrentBCHeaderKeys(BaseYear);
        //
        // we have to read the GL BALANCE (which is NOT proxy=true) to 
        // create new BC header objects
        storeCurrentGLBCHeaderCandidates(BaseYear);
        LOG.info(String.format("\nproxy BC Header candidates read: %d",
                proxyCandidatesReadinTS));
        LOG.info(String.format("\nproxy BC Header rows created: %d",
                proxyBCHeadersCreatedinTS));
    }
    
    private void getCurrentBCHeaderKeys(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        Collection<BudgetConstructionHeader> Results;
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class,
                                                      criteriaId);
        Results = getPersistenceBrokerTemplate().getCollectionByQuery(queryId);
        if (Results.size() > 0) 
        {
            currentBCHeaderKeys = new HashSet<String>(Results.size()); 
        }
        else
        {
            currentBCHeaderKeys = new HashSet<String>();
            return;
        }
        Iterator ReturnedRows = Results.iterator();
        
        while (ReturnedRows.hasNext())
        {
            BudgetConstructionHeader bCHdr = 
                (BudgetConstructionHeader) ReturnedRows.next();
            currentBCHeaderKeys.add(bCHdr.getChartOfAccountsCode()+
                                    bCHdr.getAccountNumber()+
                                    bCHdr.getSubAccountNumber());
        }
    }
    
    private void setUpNewBCDocument(BudgetConstructionDocument toDoc,
                                     BudgetConstructionHeader fromDoc)
    {
        toDoc.setUniversityFiscalYear(fromDoc.getUniversityFiscalYear());
        toDoc.setChartOfAccountsCode(fromDoc.getChartOfAccountsCode());
        toDoc.setAccountNumber(fromDoc.getAccountNumber());
        toDoc.setSubAccountNumber(fromDoc.getSubAccountNumber());
        toDoc.setOrganizationLevelCode(fromDoc.getOrganizationLevelCode());
        toDoc.setOrganizationLevelChartOfAccountsCode(
                fromDoc.getOrganizationLevelChartOfAccountsCode());
        toDoc.setOrganizationLevelOrganizationCode(
                fromDoc.getOrganizationLevelOrganizationCode());
        toDoc.setBudgetLockUserIdentifier(
                fromDoc.getBudgetLockUserIdentifier());
        toDoc.setBudgetTransactionLockUserIdentifier(
                fromDoc.getBudgetTransactionLockUserIdentifier());
        //  now set up the remaining values
        toDoc.setVersionNumber(DEFAULT_VERSION_NUMBER);
        DocumentHeader kualiDocumentHeader = toDoc.getDocumentHeader();
        toDoc.setDocumentNumber(toDoc.getDocumentHeader().getDocumentNumber());
        kualiDocumentHeader.setOrganizationDocumentNumber(
                            toDoc.getUniversityFiscalYear().toString());
        kualiDocumentHeader.setFinancialDocumentStatusCode(
                Constants.INITIAL_KUALI_DOCUMENT_STATUS_CD);
        kualiDocumentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        kualiDocumentHeader.setFinancialDocumentDescription(String.format("%s %d %s %s",
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION,
                       fromDoc.getUniversityFiscalYear(),
                       fromDoc.getChartOfAccountsCode(),fromDoc.getAccountNumber()));
        kualiDocumentHeader.setExplanation(
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION);
    }
    
    private void stepToGetProxyDocumentNumberRows(Integer BaseYear)
    {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        criteriaId.addEqualTo(PropertyConstants.DOCUMENT_NUMBER,proxyDocumentNumber);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class,
                                                      criteriaId);
        newBCDocumentSource = 
            getPersistenceBrokerTemplate().getCollectionByQuery(queryId);
        // now we have to get rid of them
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void stepToStoreNewDocuments(Integer BaseYear) 
    {
        LOG.info("\nInteraction with workflow started: "+String.format("%tT",new GregorianCalendar().getTime()));
        Iterator bCHeaderRows = newBCDocumentSource.iterator();
        documentsToCreateinNTS =  ((Integer) (newBCDocumentSource.size())).longValue();
        while (bCHeaderRows.hasNext())
        {
            BudgetConstructionHeader bCTemplate = 
                (BudgetConstructionHeader) bCHeaderRows.next();
            try
            {
                storeBudgetConstructionDocument(bCTemplate);
            }
            catch (WorkflowException wex)
            {
                LOG.warn(String.format(
                        "\nskipping creation of document for: %s %s %s \n(%s)\n",
                        bCTemplate.getChartOfAccounts(),
                        bCTemplate.getAccountNumber(),
                        bCTemplate.getSubAccountNumber(),
                        wex.getMessage()));
                documentsSkippedinNTS = documentsSkippedinNTS+1;
                continue;
            }
            documentsCreatedinNTS = documentsCreatedinNTS+1;
        }
        LOG.info("\nInteraction with workflow ended: "+String.format("%tT",new GregorianCalendar().getTime()));
        storeBudgetConstructionDocumentsInitialStatus();
        LOG.info("\nStatus changes completed: "+String.format("%tT",new GregorianCalendar().getTime()));

    }
    
    private void storeBudgetConstructionDocument (BudgetConstructionHeader bCHdrTemplate)
    throws WorkflowException
    {
        //  this should only be called from a non-transactional method
        // first we get the new document
        BudgetConstructionDocument bCDocument = (BudgetConstructionDocument) 
            documentService.getNewDocument(
                    BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
        // header and BC document share the same fields--fill them in in the new document
        setUpNewBCDocument(bCDocument, bCHdrTemplate);
        // store the document
        getPersistenceBrokerTemplate().store(bCDocument);
        // route in workflow (See DocumentService.routeNewDocument--we don't need all the
        // checks.  There are no pending GL entry lines involved, for example.)
        documentService.prepareWorkflowDocument(bCDocument);
        workflowDocumentService.route(bCDocument.getDocumentHeader().getWorkflowDocument(),
                     "created by Genesis",null);
        //  we need to reset the status code
        //  the OLTP screens use the status code to keep track of whether the 
        //  document has been edited
        /*
         *  apparently something in workflow route accesses the header two or
         *  three times.  we were getting optimistic locking errors (and having
         *  our status overridden even when we were able to update) because
         *  we were competing with this asynchronous process.  so, we save the 
         *  document numbers here in a FIFO array, so we can update the documents
         *  later after workflow has done its business
         */
        newBCDocumentNumbers.add(bCDocument.getDocumentNumber());
    }
    
    private void storeBudgetConstructionDocumentsInitialStatus ()
    {
        //  since the object ID was created when the document was stored, we need to
        //  retrieve the header from the database instead of just using the header
        //  object in the existing BC document object
        //  @@TODO:
        //  this is not happening under transactional control, so the header object
        //  will not be cached.  there is no update method in PersistenceBrokerTemplate.
        //  so, we will probably do three DB calls: (a) get the document header,
        //  (b) get it again on store to see if ojb should do an update or an insert,
        //  and (c) update. 
        for (int i=0; i < newBCDocumentNumbers.size(); i++)
        {
          String documentNumber = newBCDocumentNumbers.get(i);    
          Criteria criteriaId = new Criteria();
          criteriaId.addEqualTo(PropertyConstants.DOCUMENT_NUMBER,documentNumber);
          QueryByCriteria queryId = 
             new QueryByCriteria(DocumentHeader.class,criteriaId);
          DocumentHeader routedDocument = 
              (DocumentHeader) getPersistenceBrokerTemplate().getObjectByQuery(queryId);
          routedDocument.setFinancialDocumentStatusCode(
                  BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_INITIAL_STATUS);
          getPersistenceBrokerTemplate().store(routedDocument);
        }
    }
    
    private void storeCurrentGLBCHeaderCandidates(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addEqualTo(PropertyConstants.BALANCE_TYPE_CODE,
                              Constants.BALANCE_TYPE_BASE_BUDGET);
        String newAttr = ColumnNames.BEGINNING_BALANCE+"-"+
                         ColumnNames.ANNUAL_BALANCE;
        criteriaId.addNotEqualTo(newAttr,0);
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.SUB_ACCOUNT_NUMBER};
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(Balance.class,
                                                                  queryAttr,
                                                                  criteriaId,
                                                                  true);
        Iterator RowsReturned = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        while (RowsReturned.hasNext())
        {
            proxyCandidatesReadinTS = proxyCandidatesReadinTS+1;
            Object[] Results = (Object[]) RowsReturned.next();
            String testKey = ((String) Results[0])+
                             ((String) Results[1])+
                             ((String) Results[2]);
            if (currentBCHeaderKeys.contains(testKey))
            {
                // don't create a new row for anything with a current header
                continue;
            }
            // set up the Budget Construction Header
            BudgetConstructionHeader newBCHdr = new BudgetConstructionHeader();
            newBCHdr.setDocumentNumber(proxyDocumentNumber);
            newBCHdr.setUniversityFiscalYear(RequestYear);
            newBCHdr.setChartOfAccountsCode((String) Results[0]);
            newBCHdr.setAccountNumber((String) Results[1]);
            newBCHdr.setSubAccountNumber((String) Results[2]);
            newBCHdr.setOrganizationLevelChartOfAccountsCode(
                    BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE);
            newBCHdr.setOrganizationLevelOrganizationCode(
                    BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE);
            newBCHdr.setOrganizationLevelCode(
                    BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CODE);
            newBCHdr.setBudgetTransactionLockUserIdentifier(
                    BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            newBCHdr.setBudgetLockUserIdentifier(
                    BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            newBCHdr.setVersionNumber(DEFAULT_VERSION_NUMBER);
           // store the result
            getPersistenceBrokerTemplate().store(newBCHdr);
            proxyBCHeadersCreatedinTS = proxyBCHeadersCreatedinTS+1;
        }
    }
        
    /*
     *  ****************************************************************************
     *   (4) here are the routines which freeze accounting at the beginning of     *
     *       budget construction (so updates can be done in parallel, or updates   *
     *       for the budget year only can be done without affecting the current    *
     *       chart of accounts).                                                   *
     *       These routines only run once, at genesis.                             *
     *  ****************************************************************************     
     */
    
    //   public routines
    
    public void createChartForNextBudgetCycle()
    {
      // first we have to remove what's there
      // (the documentation says deleteByQuery (1) ignores object references and (2) does
      //  not synchronize the cache.  so, we clear the cache before and after.)
        getPersistenceBrokerTemplate().clearCache();
        Criteria criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        QueryByCriteria killAcctQuery = 
            new QueryByCriteria(BudgetConstructionAccountReports.class);
        killAcctQuery.setCriteria(criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(killAcctQuery);
        QueryByCriteria killOrgQuery =
            new QueryByCriteria(BudgetConstructionOrganizationReports.class);
        killOrgQuery.setCriteria(criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(killOrgQuery);
        getPersistenceBrokerTemplate().clearCache();
      // build the account table
        buildNewAccountReportsTo();
      // build the organization table  
        buildNewOrganizationReportsTo();
    }
    
    //  private working methods for the BC chart update
    
    private void buildNewAccountReportsTo()
    {
        
        //  All active accounts are loaded into the budget accounting table
        
        Integer sqlChartOfAccountsCode = 0;
        Integer sqlAccountNumber = 1;
        Integer sqlReportsToChartofAccountsCode = 0;
        Integer sqlOrganizationCode = 2;
        
        Long accountsAdded = new Long(0);
        
        Criteria criteriaID = new Criteria();
        /*  current IU genesis does NOT check for closed accounts--it loads all accounts
         *  it is possible that an account which has been closed still has base budget 
        criteriaID.addNotEqualTo(PropertyConstants.ACCOUNT_CLOSED_INDICATOR,
                              Constants.ParameterValues.YES);
         */
        criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.ORGANIZATION_CODE};
       ReportQueryByCriteria queryID = 
       new ReportQueryByCriteria(Account.class, queryAttr, criteriaID, true);
       Iterator Results = 
           getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
       while (Results.hasNext())
       {
           Object[] ReturnList = (Object[]) Results.next();
           // just save this stuff, one at a time
           // it isn't needed for anything else
           BudgetConstructionAccountReports acctRpts = 
               new BudgetConstructionAccountReports();
           acctRpts.setChartOfAccountsCode((String) ReturnList[sqlChartOfAccountsCode]);
           acctRpts.setAccountNumber((String) ReturnList[sqlAccountNumber]);
           acctRpts.setReportsToChartOfAccountsCode((String)
                    ReturnList[sqlReportsToChartofAccountsCode]);
           acctRpts.setReportsToOrganizationCode((String)
                    ReturnList[sqlOrganizationCode]);
           acctRpts.setVersionNumber(DEFAULT_VERSION_NUMBER);
           getPersistenceBrokerTemplate().store(acctRpts);
           accountsAdded = accountsAdded + 1;
       }
       LOG.info(String.format("\nAccount reporting lines added to budget construction %d",
                accountsAdded));
    }
    
    private void buildNewOrganizationReportsTo()
    {
      
        //  all active organizations are loaded into the budget construction
        //  organization table
        
        Integer sqlChartOfAccountsCode          = 0;
        Integer sqlOrganizationCode             = 1;
        Integer sqlReportsToChartOfAccountsCode = 2;
        Integer sqlReportsToOrganizationCode    = 3;
        Integer sqlResponsibilityCenterCode     = 4;

        Long organizationsAdded = new Long(0);
        
        Criteria criteriaID = new Criteria();
        /*
         *  IU genesis takes all organizations, not just active ones
         *  the reason is that a closed account which still has a base budget
         *  might report to one of these organizations 
        criteriaID.addEqualTo(PropertyConstants.ORGANIZATION_ACTIVE_INDICATOR,
                              Constants.ParameterValues.YES);
         */
        criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ORGANIZATION_CODE,
                              PropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.REPORTS_TO_ORGANIZATION_CODE,
                              PropertyConstants.RESPONSIBILITY_CENTER_CODE};
       ReportQueryByCriteria queryID = 
       new ReportQueryByCriteria(Org.class, queryAttr, criteriaID, true);
       Iterator Results = 
           getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
       while (Results.hasNext())
       {
           Object[] ReturnList = (Object[]) Results.next();
           // just save this stuff, one at a time
           // it isn't needed for anything else
           BudgetConstructionOrganizationReports orgRpts = 
               new BudgetConstructionOrganizationReports();
           orgRpts.setChartOfAccountsCode((String) ReturnList[sqlChartOfAccountsCode]);
           orgRpts.setOrganizationCode((String) ReturnList[sqlOrganizationCode]);
           orgRpts.setReportsToChartOfAccountsCode((String)
                    ReturnList[sqlReportsToChartOfAccountsCode]);
           orgRpts.setReportsToOrganizationCode((String)
                    ReturnList[sqlReportsToOrganizationCode]);
           orgRpts.setResponsibilityCenterCode((String)
                    ReturnList[sqlResponsibilityCenterCode]);
           orgRpts.setVersionNumber(DEFAULT_VERSION_NUMBER);
           getPersistenceBrokerTemplate().store(orgRpts);
           organizationsAdded = organizationsAdded + 1;
       }
       LOG.info(String.format("\nOrganization reporting lines added to budget construction %d",
                organizationsAdded));
    }
    
    /*
     *  *********************************************************************************
     *  (5) these are the routines that build the security organization hierarchy
     *   -- they run every time the budget construction update process runs
     *   -- they are designed to pick up any changes made to the BC account and BC
     *      organization tables
     *   -- based on changes, they will adjust the security levels of accounts in the BC
     *      header.  for a header at the level of an organization that is no longer valid,
     *      the level will return to the account manager level.  for a header at the level
     *      of an organization that has changed its location in the hierarchy, the new
     *      level will be added to the header
     *   -- this process only affects accounts in the budget construction pending
     *      general ledger, and it is assumed that all updates to the PBGL have been
     *      finished when this process runs.       
     *  *********************************************************************************    
     */

    private HashMap<String,BudgetConstructionAccountReports> acctRptsToMap =
        new HashMap(BudgetConstructionConstants.ESTIMATED_NUMBER_OF_FINANCIAL_ACCOUNTS);
    private HashMap<String,BudgetConstructionOrganizationReports> orgRptsToMap =
        new HashMap(BudgetConstructionConstants.ESTIMATED_NUMBER_OF_ACTIVE_ORGANIZATIONS);
    private HashMap<String,BudgetConstructionAccountOrganizationHierarchy> acctOrgHierMap =
        new HashMap(BudgetConstructionConstants.BUDGETED_ACCOUNTS_TIMES_AVERAGE_REPORTING_TREE_SIZE);
    private BudgetConstructionHeader budgetConstructionHeader; 
    //  these are the values at the root of the organization tree
    //  they report to themselves, and they are at the highest level of every 
    //  organization's reporting chain
    private String rootChart;
    private String rootOrganization;
    
    private Integer nHeadersBackToZero      = 0;
    private Integer nHeadersSwitchingLevels = 0;
        

    // public method
    
    public void rebuildOrganizationHierarchy(Integer BaseYear)
    {
        // ********
        // this routine REQUIRES that pending GL is complete
        // we only build a hierarchy for accounts that exist in the GL
        // ********
        
        Integer RequestYear = BaseYear + 1;
        
        //
        // first we have to clear out what's there for the coming fiscal year
        // again, we clear the cache after doing a deleteByQuery
        getPersistenceBrokerTemplate().clearCache();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        QueryByCriteria killOrgHierQuery = 
            new QueryByCriteria(BudgetConstructionAccountOrganizationHierarchy.class,
                                criteriaID);
        killOrgHierQuery.setCriteria(criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(killOrgHierQuery);
        getPersistenceBrokerTemplate().clearCache();
        //
        // now we fetch the root of the organization tree
        String[] rootNode = 
            SpringServiceLocator.getOrganizationService().getRootOrganizationCode();
        rootChart = rootNode[0];
        rootOrganization = rootNode[1];
        //
        // read the entire account reports to table, and build a hash map for the
        // join with the PBGL accounts
        readAcctReportsTo();
        // read the entire organization reports to table, and build a hash map for
        // getting the organization tree
        readOrgReportsTo();
        //
        //  we query the budget construction header and loop through the results
        //  we build a hierarchy for every account we find
        //  we reset level of any account which no longer exists in the hierarchy
        criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(BudgetConstructionHeader.class,
                                      criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (Results.hasNext())
        {
           BudgetConstructionHeader extantBCHdr = (BudgetConstructionHeader) Results.next();
           buildAcctOrgHierFromAcctRpts(acctRptsToMap.get(
                   getAcctRptsToKeyFromBCHdr(extantBCHdr)), RequestYear);
           updateBudgetConstructionHeaderAsNeeded(extantBCHdr);
        }
    }
    
    //  private utility methods

    private void buildAcctOrgHierFromAcctRpts(BudgetConstructionAccountReports acctRpts,
            Integer RequestYear)
    {
        // part of the key of the budget construction header is a sub account
        // so, our algorithm could visit the same account more than once
        // if the hierarchy for this account is already built, we skip this routine
        String inKey = getOrgHierarchyKeyFromAcctRpts(acctRpts);
        if (acctOrgHierMap.get(inKey) != null)
        {
            return;
        }
        Integer orgLevel = 1;
        // the organization the account directly reports to is at level 1
        // (the account starts out at the account fiscal office level--level 0) 
        BudgetConstructionAccountOrganizationHierarchy acctOrgHier;
        acctOrgHier =
            new BudgetConstructionAccountOrganizationHierarchy();
        acctOrgHier.setUniversityFiscalYear(RequestYear);
        acctOrgHier.setChartOfAccountsCode(acctRpts.getChartOfAccountsCode());
        acctOrgHier.setAccountNumber(acctRpts.getAccountNumber());
        acctOrgHier.setOrganizationLevelCode(orgLevel);
        acctOrgHier.setVersionNumber(DEFAULT_VERSION_NUMBER);
        acctOrgHier.setOrganizationChartOfAccountsCode(acctRpts.getReportsToChartOfAccountsCode());
        acctOrgHier.setOrganizationCode(acctRpts.getReportsToOrganizationCode());
        // save the new row
        getPersistenceBrokerTemplate().store(acctOrgHier);
        // save the new row in a hash map so we can merge with the budget header
        String mapKey = getOrgHierarchyKey(acctOrgHier);
        acctOrgHierMap.put(mapKey,acctOrgHier);
        // now we have to loop to assign the hierarchy
        // (especially before testing, we need to be on the look out for infinite
        //@@TODO:
        //  loops.  assertions are verboten, so we'll just code a high value for
        //  the level limit, instead of using a potentially infinite while loop)
        while (orgLevel < MAXIMUM_ORGANIZATION_TREE_DEPTH)
        {
            // find the current organization in the BC organization reports to table
            String orgKey = getOrgRptsToKeyFromAcctOrgHier(acctOrgHier);
            if (noNewMapEntryNeeded(orgRptsToMap.get(orgKey)))
            {
                // get out if we have found the root of the reporting tree
                break;
            }
            orgLevel = orgLevel+1;
            BudgetConstructionOrganizationReports orgRpts =
                orgRptsToMap.get(orgKey);
            acctOrgHier = 
                new BudgetConstructionAccountOrganizationHierarchy();
            acctOrgHier.setUniversityFiscalYear(RequestYear);
            acctOrgHier.setChartOfAccountsCode(acctRpts.getChartOfAccountsCode());
            acctOrgHier.setAccountNumber(acctRpts.getAccountNumber());
            acctOrgHier.setOrganizationLevelCode(orgLevel);
            acctOrgHier.setVersionNumber(DEFAULT_VERSION_NUMBER);
            acctOrgHier.setOrganizationChartOfAccountsCode(
                        orgRpts.getReportsToChartOfAccountsCode());
            acctOrgHier.setOrganizationCode(orgRpts.getReportsToOrganizationCode());
            // save the new row
            getPersistenceBrokerTemplate().store(acctOrgHier);
            // save the new row in a hash map so we can merge with the budget header
            mapKey = getOrgHierarchyKey(acctOrgHier);
            acctOrgHierMap.put(mapKey,acctOrgHier);
        }
        if (orgLevel >= MAXIMUM_ORGANIZATION_TREE_DEPTH)
        {
            LOG.warn(String.format("\n%s/%s reports to more than %d organizations",
                     acctRpts.getChartOfAccountsCode(),
                     acctRpts.getAccountNumber(),
                     MAXIMUM_ORGANIZATION_TREE_DEPTH));
        }
    }
    
    private String getAcctRptsToKey(
            BudgetConstructionAccountReports acctRpts)
    {
        String TestKey = new String();
        TestKey = acctRpts.getChartOfAccountsCode()+
                  acctRpts.getAccountNumber();
        return TestKey;
    }
    
    private String getAcctRptsToKeyFromBCHdr(
                   BudgetConstructionHeader bCHdr)
    {
        String TestKey = new String();
        TestKey = bCHdr.getChartOfAccountsCode()+
                  bCHdr.getAccountNumber();
        return TestKey;
    }
    
    private String getOrgHierarchyKey(
            BudgetConstructionAccountOrganizationHierarchy orgHier)
    {
        String TestKey = new String();
        TestKey = orgHier.getChartOfAccountsCode()+
                  orgHier.getAccountNumber()+
                  orgHier.getOrganizationChartOfAccountsCode()+
                  orgHier.getOrganizationCode();
        return TestKey;
    }
    
    private String getOrgHierarchyKeyFromAcctRpts(
            BudgetConstructionAccountReports acctRpts)
    {
        String TestKey = new String();
        TestKey = acctRpts.getChartOfAccountsCode()+
                  acctRpts.getAccountNumber()+
                  acctRpts.getReportsToChartOfAccountsCode()+
                  acctRpts.getReportsToOrganizationCode();
        return TestKey;
    }
 
    private String getOrgHierarchyKeyFromBCHeader(
                   BudgetConstructionHeader bCHdr)
    {
        String TestKey = new String();
        TestKey = bCHdr.getChartOfAccountsCode()+
                  bCHdr.getAccountNumber()+
                  bCHdr.getOrganizationLevelChartOfAccountsCode()+
                  bCHdr.getOrganizationLevelOrganizationCode();
        return TestKey;
    }
    
    private String getOrgRptsToKey(
            BudgetConstructionOrganizationReports orgRpts)
    {
        String TestKey = new String();
        TestKey = orgRpts.getChartOfAccountsCode()+
                  orgRpts.getOrganizationCode();
        return TestKey;
    }
    
    private String getOrgRptsToKeyFromAcctOrgHier(
            BudgetConstructionAccountOrganizationHierarchy acctOrgHier)
    {
        String TestKey = new String();
        TestKey = acctOrgHier.getOrganizationChartOfAccountsCode()+
                  acctOrgHier.getOrganizationCode();
        return TestKey;
    }
    
    private boolean noNewMapEntryNeeded(BudgetConstructionOrganizationReports orgRpts)
    {
        // no new entry is needed we are at the root of the organization tree
        String thisChart = orgRpts.getChartOfAccountsCode();
        String thisOrg   = orgRpts.getOrganizationCode();
        if ((thisChart.compareTo(rootChart) == 0)&&
             (thisOrg.compareTo(rootOrganization) == 0))
             {
                 return true;
             }
        // no new entry is needed if either the chart or the organization 
        // which this organization reports to is null
        // or if the organization reports to itself
        String rptsToChart = orgRpts.getReportsToChartOfAccountsCode();
        if (rptsToChart.length() == 0)
        {
            LOG.warn(String.format("\n(%s, %s) reports to a null chart",
                    thisChart, thisOrg));
            return true;
        }
        String rptsToOrg = orgRpts.getReportsToOrganizationCode();
        if (rptsToOrg.length() == 0) 
        {
            LOG.warn(String.format("\n(%s, %s) reports to a null organization",
                    thisChart, thisOrg));
            return true;
        }
        if ((thisChart.compareTo(rptsToChart) == 0)
           &&(thisOrg.compareTo(rptsToOrg) == 0))
        {
            LOG.warn(String.format("\n(%s,%s) reports to itself and is not the root",
                     thisChart, thisOrg));
            return true;
        }
        return false;
    }
    
    private void readAcctReportsTo()
    {
        // we will use a report query, to bypass the "persistence" bureaucracy
        // we will use the OJB class as a convenient container object in the hashmap
        Integer sqlChartOfAccountsCode          = 0;
        Integer sqlAccountNumber                = 1;
        Integer sqlReportsToChartofAccountsCode = 2;
        Integer sqlOrganizationCode             = 3;
        Criteria criteriaID = ReportQueryByCriteria.CRITERIA_SELECT_ALL;
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.REPORTS_TO_ORGANIZATION_CODE};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(BudgetConstructionAccountReports.class,
                                      queryAttr,criteriaID);
        Iterator Results = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (Results.hasNext())
        {
            Object[] ReturnList = (Object[]) Results.next();
            BudgetConstructionAccountReports acctRpts = 
                new BudgetConstructionAccountReports();
            acctRpts.setChartOfAccountsCode((String) ReturnList[sqlChartOfAccountsCode]);
            acctRpts.setAccountNumber((String) ReturnList[sqlAccountNumber]);
            acctRpts.setReportsToChartOfAccountsCode((String)
                     ReturnList[sqlReportsToChartofAccountsCode]);
            acctRpts.setReportsToOrganizationCode((String)
                     ReturnList[sqlOrganizationCode]);
            String TestKey = getAcctRptsToKey(acctRpts);
            acctRptsToMap.put(TestKey,acctRpts);
        }
       LOG.info("\nAccount Reports To for Organization Hierarchy:"); 
       LOG.info(String.format("\nNumber of account-reports-to rows: %d",
                acctRptsToMap.size()));        
   }
    
   private void readOrgReportsTo()
   {
       // we will use a report query, to bypass the "persistence" bureaucracy
       // we will use the OJB class as a convenient container object in the hashmap
       Integer sqlChartOfAccountsCode          = 0;
       Integer sqlOrganizationCode             = 1;
       Integer sqlReportsToChartofAccountsCode = 2;
       Integer sqlReportsToOrganizationCode    = 3;
       Criteria criteriaID = ReportQueryByCriteria.CRITERIA_SELECT_ALL;
       String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                             PropertyConstants.ORGANIZATION_CODE,
                             PropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE,
                             PropertyConstants.REPORTS_TO_ORGANIZATION_CODE};
       ReportQueryByCriteria queryID = 
           new ReportQueryByCriteria(BudgetConstructionOrganizationReports.class,
                                     queryAttr,criteriaID);
       Iterator Results = 
           getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
       while (Results.hasNext())
       {
           Object[] ReturnList = (Object[]) Results.next();
           BudgetConstructionOrganizationReports orgRpts = 
               new BudgetConstructionOrganizationReports();
           orgRpts.setChartOfAccountsCode((String) ReturnList[sqlChartOfAccountsCode]);
           orgRpts.setOrganizationCode((String) ReturnList[sqlOrganizationCode]);
           orgRpts.setReportsToChartOfAccountsCode((String)
                    ReturnList[sqlReportsToChartofAccountsCode]);
           orgRpts.setReportsToOrganizationCode((String)
                    ReturnList[sqlReportsToOrganizationCode]);
           String TestKey = getOrgRptsToKey(orgRpts);
           orgRptsToMap.put(TestKey,orgRpts);
       }
      LOG.info("\nOrganization Reports To for Organization Hierarchy:"); 
      LOG.info(String.format("\nNumber of organization-reports-to rows: %d",
               orgRptsToMap.size()));        
   }
   
   private void updateBudgetConstructionHeaderAsNeeded(
                BudgetConstructionHeader bCHdr)
   {
      // header rows at the lowest (initial) level should be left alone
      if (bCHdr.getOrganizationLevelCode().equals( 
          BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CODE))
      {
          return;
      }
      // we will only update if the level of the organization has changed 
      // or if the organization has disappeared completely 
      String mapKey = getOrgHierarchyKeyFromBCHeader(bCHdr);
      BudgetConstructionAccountOrganizationHierarchy acctOrgHier =
          acctOrgHierMap.get(mapKey);
      if (acctOrgHier == null)
      {   
          // the account no longer reports to this organization
          // we have to return to the lowest level and the default the
          // organization reported to
          nHeadersBackToZero = nHeadersBackToZero+1;
          bCHdr.setOrganizationLevelChartOfAccountsCode(
                  BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE);
          bCHdr.setOrganizationLevelOrganizationCode(
                  BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE);
          bCHdr.setOrganizationLevelCode(
                  BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CODE);
          getPersistenceBrokerTemplate().store(bCHdr);
          return;
      }
     Integer levelFromHierarchy = acctOrgHier.getOrganizationLevelCode();
     Integer levelFromHeader    = bCHdr.getOrganizationLevelCode();
     if (!levelFromHierarchy.equals(levelFromHeader))
     {
         // the organization reported to has changed its location in the hierarchy
         bCHdr.setOrganizationLevelCode(levelFromHierarchy);
         getPersistenceBrokerTemplate().store(bCHdr);
         nHeadersSwitchingLevels = nHeadersSwitchingLevels+1;
     }
   }
   
   
   
    
    /*
     *  **************************************************************************
     *  (6) here are the routines we will use for updating budget construction GL*
     *  **************************************************************************
     */
    // maps (hash maps) to return the results of the GL call
    // --pBGLFromGL contains all the rows returned, stuffed into an object that can be 
    //   saved to the pending budget construction general ledger
    // --bCHdrFromGL contains one entry for each potentially new key for the budget
    //   construction header table.
    private HashMap<String,PendingBudgetConstructionGeneralLedger>  pBGLFromGL =
        new HashMap(BudgetConstructionConstants.ESTIMATED_PENDING_GENERAL_LEDGER_ROWS);
    private HashMap<String,String> documentNumberFromBCHdr =
            new HashMap(BudgetConstructionConstants.ESTIMATED_BUDGET_CONSTRUCTION_DOCUMENT_COUNT);
    private HashMap<String,String> CurrentPBGLDocNumbers = 
            new HashMap(BudgetConstructionConstants.ESTIMATED_BUDGET_CONSTRUCTION_DOCUMENT_COUNT);
    private HashMap<String,Integer> skippedPBGLKeys = new HashMap(); 
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
    private Integer nGLBBRowsSkipped = new Integer(0);
    
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
        Criteria tranLockID = new Criteria();
        //@@TODO:  add these to the PropertyConstants or at least to 
        //         BudgetConstructionConstants?
        if (BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS == null)
        { 
            //  make sure that a NULL test is used in case = NULL is not supported
            //  by the database
            lockID.addNotNull("budgetLockUserIdentifier");
            tranLockID.addNotNull("budgetTransactionLockUserIdentifier");
        }
        else
        {
            lockID.addNotEqualTo("budgetLockUserIdentifier",
                          BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            tranLockID.addNotEqualTo("budgetTransactionLockUserIdentifier",
                    BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        };
        lockID.addOrCriteria(tranLockID);
        criteriaID.addAndCriteria(lockID);
        //
        QueryByCriteria queryID = 
            new QueryByCriteria(BudgetConstructionHeader.class, criteriaID);
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
        // @@TODO: this is just here for testing purposes
        //         it will be handled by the clearDBForGenesis method in production
        clearBothYearsPBGL(BaseYear);
        // we have to clean out account reports to
        // it is not fiscal year-specific
        // this implies that last year's data can't be there, because the
        // organization hierarchy will have changed
        readBCHeaderForDocNumber(BaseYear);
        readGLForPBGL(BaseYear);
        addNewGLRowsToPBGL(BaseYear);
        writeFinalDiagnosticCounts();
    }
    
    public void updateToPBGL(Integer BaseYear)
    {
        readBCHeaderForDocNumber(BaseYear);
        readGLForPBGL(BaseYear);
        updateCurrentPBGL(BaseYear);
        addNewGLRowsToPBGL(BaseYear);
        writeFinalDiagnosticCounts();
    }
    
    //
    //  two test routines to display the field values in the two business objects
    //  produced from the GL read.  these are primarily here for initial testing
    private void info()
    { 
        if (! LOG.isEnabledFor(Level.INFO))
           {
            return;
           };
       //  print one header row   
       for (Map.Entry<String,String> bcHeaderRows : 
           documentNumberFromBCHdr.entrySet())
       {
           String toPrint = bcHeaderRows.getValue();
           LOG.info(String.format("\n\nA sample document number %s\n",toPrint));
           break;
       }
       // print one PBGL row
       for (Map.Entry<String,PendingBudgetConstructionGeneralLedger> pBGLRows : 
           pBGLFromGL.entrySet())
       {
           PendingBudgetConstructionGeneralLedger toPrint = pBGLRows.getValue();
           LOG.info("\n\nA sample PBGL row\n");
           LOG.info(String.format("\nDocument Number = %s",
                    toPrint.getDocumentNumber()));
           LOG.info(String.format("\nUniversity Fiscal Year = %d",
                   toPrint.getUniversityFiscalYear()));
           LOG.info(String.format("\nChart: %s",
                   toPrint.getChartOfAccountsCode()));
           LOG.info(String.format("\nAccount: %s",
                   toPrint.getAccountNumber()));
           LOG.info(String.format("\nSub Account: %s",
                   toPrint.getSubAccountNumber()));
           LOG.info(String.format("\nObject Code: %s",
                   toPrint.getFinancialObjectCode()));
           LOG.info(String.format("\nSubobject Code: %s",
                   toPrint.getFinancialSubObjectCode()));
           LOG.info(String.format("\nBalance Type: %s",
                   toPrint.getFinancialBalanceTypeCode()));
           LOG.info(String.format("\nObject Type: %s",
                   toPrint.getFinancialObjectTypeCode()));
           LOG.info(String.format("\nBase Amount: %s",
                   toPrint.getFinancialBeginningBalanceLineAmount().toString()));
           LOG.info(String.format("\nRequest Amount: %s",
                   toPrint.getAccountLineAnnualBalanceAmount().toString()));
           LOG.info(String.format("\nVersion Number: %d",
                   toPrint.getVersionNumber()));
           break;
       }
     }
    
    private void debug()
    { 
        if (! LOG.isEnabledFor(Level.DEBUG))
           {
            return;
           };
       //  print one header row    
        for (Map.Entry<String,String> bcHeaderRows : 
           documentNumberFromBCHdr.entrySet())
       {
           String toPrint = bcHeaderRows.getValue();
           LOG.debug(String.format("\n\nA sample document number %s\n",toPrint));
           break;
       }
       // print one PBGL row
       for (Map.Entry<String,PendingBudgetConstructionGeneralLedger> pBGLRows : 
           pBGLFromGL.entrySet())
       {
           PendingBudgetConstructionGeneralLedger toPrint = pBGLRows.getValue();
           LOG.debug("\n\nA sample PBGL row\n");
           LOG.debug(String.format("\nDocument Number = %s",
                    toPrint.getDocumentNumber()));
           LOG.debug(String.format("\nUniversity Fiscal Year = %d",
                   toPrint.getUniversityFiscalYear()));
           LOG.debug(String.format("\nChart: %s",
                   toPrint.getChartOfAccountsCode()));
           LOG.debug(String.format("\nAccount: %s",
                   toPrint.getAccountNumber()));
           LOG.debug(String.format("\nSub Account: %s",
                   toPrint.getSubAccountNumber()));
           LOG.debug(String.format("\nObject Code: %s",
                   toPrint.getFinancialObjectCode()));
           LOG.debug(String.format("\nSubobject Code: %s",
                   toPrint.getFinancialSubObjectCode()));
           LOG.debug(String.format("\nBalance Type: %s",
                   toPrint.getFinancialBalanceTypeCode()));
           LOG.debug(String.format("\nObject Type: %s",
                   toPrint.getFinancialObjectTypeCode()));
           LOG.debug(String.format("\nBase Amount: %s",
                     toPrint.getFinancialBeginningBalanceLineAmount().toString()));
           LOG.debug(String.format("\nRequest Amount: %s",
                   toPrint.getAccountLineAnnualBalanceAmount().toString()));
           LOG.debug(String.format("\nVersion Number: %d",
                   toPrint.getVersionNumber()));
           break;
       }
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
             nGLRowsAdded = nGLRowsAdded+1;
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
    
    private void readBCHeaderForDocNumber(Integer BaseYear)
    {
        //  we have to read all the budget construction header objects so that
        //  we can use them to assign document numbers
        //  the header objects have all been created in a previous, non-transactional
        //  step (since the document number comes from workflow, and workflow cannot
        //  be called from within a kuali transaction)
        //
        Integer RequestYear = BaseYear + 1;
        //
        Long documentsRead = new Long(0);
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              PropertyConstants.ACCOUNT_NUMBER,
                              PropertyConstants.SUB_ACCOUNT_NUMBER,
                              PropertyConstants.DOCUMENT_NUMBER};
        ReportQueryByCriteria queryId = 
            new ReportQueryByCriteria(BudgetConstructionHeader.class,queryAttr,criteriaId);
        Iterator Results =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        while (Results.hasNext())
        {
            Object[] rowReturned = (Object[]) Results.next();
            String hashKey = ((String) rowReturned[0])+
                             ((String) rowReturned[1])+
                             ((String) rowReturned[2]);
            documentNumberFromBCHdr.put(hashKey,((String) rowReturned[3]));
            documentsRead = documentsRead+1;
        }
        LOG.info(String.format("\nBC Headers read = %d",documentsRead));
    }
    
    private void readGLForPBGL(Integer BaseYear)
    {
        // we apparently need to configure the log file in order to use it
        // @@TODO: should these be a "weak hash map", to optimize memory use?
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
        LOG.info("\nGL Query started: "+String.format("%tT",dateTimeService.getCurrentDate()));
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        LOG.info("\nGL Query finished: "+String.format("%tT",dateTimeService.getCurrentDate()));
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
            //  we need to add a document number to the PBGL object
            String HeaderTestKey = buildHeaderTestKeyFromSQLResults(ReturnList);
            if (documentNumberFromBCHdr.get(HeaderTestKey) == null)
            {
               recordSkippedKeys(HeaderTestKey);
            }
            else
            { 
               pBGLFromGL.get(GLTestKey).setDocumentNumber(
                       documentNumberFromBCHdr.get(HeaderTestKey)); 
            }
        }
        LOG.info("\nHash maps built: "+
                String.format("%tT",dateTimeService.getCurrentDate()));
        info();
        nGLBBKeysRead = documentNumberFromBCHdr.size();
        nGLBBRowsRead = pBGLFromGL.size()+ nGLBBRowsRead;
    }
    
    private void recordSkippedKeys(String badGLKey)
    {
        nGLBBRowsSkipped = nGLBBRowsSkipped+1;
        if (skippedPBGLKeys.get(badGLKey) == null)
        {
            skippedPBGLKeys.put(badGLKey,new Integer(1));
        }
        else
        {
            Integer rowCount = skippedPBGLKeys.get(badGLKey) + 1;
            skippedPBGLKeys.put(badGLKey,rowCount);
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
       if (baseFromCurrentGL.equals(baseFromPBGL))
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
       
       
       // what we are going to do here is what Oracle calls a hash join
       //
       // we will merge the current PBGL rows with the GL detail, and 
       // replace the amount on each current PBGL row which matches from
       // the GL row, and remove the GL row 
       //
       // we will compare the GL Key row with the the current PBGL row,
       // and if the keys are the same, we will eliminate the GL key row
       //
       //  fetch the current PBGL rows
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
           // update the base amount and store the result if necessary
           updateBaseBudgetAmount(currentPBGLInstance);
       }
    }
    
    private void writeFinalDiagnosticCounts()
    {
        LOG.info(String.format("\n\nRun Statistics\n\n"));
        LOG.info(String.format("\nGeneral Ledger BB Keys read: %d",
                                nGLBBKeysRead));
        LOG.info(String.format("\nGeneral Ledger BB Rows read: %d",
                 nGLBBRowsRead));
        LOG.info(String.format("\nExisting Pending General Ledger rows: %d",
                 nCurrentPBGLRows));
        LOG.info(String.format("\nof these..."));
        LOG.info(String.format("\nnew PBGL rows written: %d",
                 nGLRowsAdded));
        LOG.info(String.format("\ncurrent PBGL amounts updated: %d",
                 nGLRowsUpdated));
        LOG.info(String.format("\nGL rows with zero net amounts (skipped) %d\n",nGLBBRowsZeroNet));
        LOG.info(String.format("\nGL account/subaccount keys skipped: %d",nGLBBRowsSkipped));
        if (!skippedPBGLKeys.isEmpty())
        {
            for (Map.Entry<String,Integer> skippedRows : skippedPBGLKeys.entrySet())
            {
             LOG.info(String.format("\nGL key %s with %d rows skipped--no document header",
                     skippedRows.getKey(),skippedRows.getValue()));
                
            }
        }
    }
 
    /*
     * ******************************************************************************
     * (7)  there could be an object class in the object code table that was marked
     *      as inactive during the current fiscal year.  there could also be GL rows
     *      with base budget which refer to this object code.  the fiscal year makers
     *      routine would NOT copy a deleted object code into the new fiscal year.
     *      to maintain referential integrity, we will copy such an object code (but
     *      mark it as deleted) into the new fiscal year if it will occur in budget
     *      construction.
     */
    
    private HashMap<String,String[]> baseYearInactiveObjects;
    private HashMap<String,String[]> gLBBObjects;
    private Integer nInactiveBBObjectCodes = new Integer(0);
    
    public void ensureObjectClassRIForBudget(Integer BaseYear)
    {
        readBaseYearInactiveObjects(BaseYear);
        if (baseYearInactiveObjects.isEmpty())
        {
            // no problems
            LOG.info(String.format("\nInactive Object Codes in BC GL: %d",
                     nInactiveBBObjectCodes));
            return;
        }
        readAndFilterGLBBObjects(BaseYear);
        if (gLBBObjects.isEmpty())
        {
            // no problems
            LOG.info(String.format("\nInactive Object Codes in BC GL: %d",
                    nInactiveBBObjectCodes));
            return;
        }
        // we have to create an object row for the request year
        addRIObjectClassesForBB(BaseYear);
        LOG.info(String.format("\nInactive Object Codes in BC GL: %d",
                nInactiveBBObjectCodes));
    }
    
    public void addRIObjectClassesForBB(Integer BaseYear)
    {
        //  we will read the object table for the request year first
        //  if the row is there (someone could have added it, or updated it),
        //  we will not change it at all.
        //  this is an extra read, but overall looking just for problems
        //  will require many fewer reads than comparing all object codes in the
        //  request year to all object codes in the GL BB base.
        Integer RequestYear = BaseYear + 1;
        for(Map.Entry<String,String[]> problemObjectCodes: gLBBObjects.entrySet())
        {
          String problemChart =  problemObjectCodes.getValue()[0];
          String problemObject =  problemObjectCodes.getValue()[1];   
          if (isObjectInRequestYear(BaseYear,problemChart,problemObject))
          {
              // everything is fine
              continue;
          }
          //  now we have to add the object to the request year as an inactive object
          Criteria criteriaID = new Criteria();
          criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
          criteriaID.addColumnEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                      problemChart);
          criteriaID.addEqualTo(PropertyConstants.OBJECT_CODE,problemObject);
          ReportQueryByCriteria queryID = 
              new ReportQueryByCriteria(ObjectCode.class,criteriaID);
          Iterator Results = 
              getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
          if (!Results.hasNext())
          {
              // this should never happen
              // if it does, it will cause an RI exception in the GL load to BC
              // at least this message will give some clue
              LOG.warn(String.format("could not find BB object (%s, %s) in %d",
                       problemChart,problemObject,BaseYear));
              continue;
          }
          ObjectCode baseYearObject = (ObjectCode) Results.next();
          baseYearObject.setUniversityFiscalYear(RequestYear);
          baseYearObject.setActive(false);
          getPersistenceBrokerTemplate().store(baseYearObject);
        }
    }

    private boolean isObjectInRequestYear(Integer BaseYear,
                                          String Chart, String ObjectCode)
    {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        criteriaID.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE,Chart);
        criteriaID.addEqualTo(PropertyConstants.ACCOUNT_NUMBER,ObjectCode);
        QueryByCriteria queryID = 
            new QueryByCriteria(ObjectCode.class,criteriaID);
        Integer Result = 
            getPersistenceBrokerTemplate().getCount(queryID);
        return (!Result.equals(0));
    }
    
    public void readBaseYearInactiveObjects(Integer BaseYear)
    {
        baseYearInactiveObjects = new HashMap<String,String[]>();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_ACTIVE_CODE,
                              Constants.ParameterValues.NO);
        String[] queryAttr  = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                               PropertyConstants.OBJECT_CODE};
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(ObjectCode.class,
                                                                  queryAttr,
                                                                  criteriaID);
        Iterator Result = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (Result.hasNext())
        {
          Object[] resultRow = (Object[]) Result.next();
          String[] hashMapValue = new String[2];
          hashMapValue[0] = (String) resultRow[0];
          hashMapValue[1] = (String) resultRow[1];
          String hashMapKey = hashMapValue[0]+hashMapValue[1];
          baseYearInactiveObjects.put(hashMapKey,hashMapValue);
        }
    }
        
        private void readAndFilterGLBBObjects(Integer BaseYear)
        {
            // this must be done before we read GL for PBGL
            // otherwise, we will get an RI violation when we try to add a PBGL
            // row with an object inactive in the current year
            gLBBObjects = new HashMap<String,String[]>();
            Criteria criteriaID = new Criteria();
            criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
            criteriaID.addEqualTo(PropertyConstants.BALANCE_TYPE_CODE,
                                  Constants.BALANCE_TYPE_BASE_BUDGET);
            String[] queryAttr = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,           
                                  PropertyConstants.OBJECT_CODE};
            ReportQueryByCriteria queryID = 
                new ReportQueryByCriteria(Balance.class,
                                          queryAttr,criteriaID,true);
            Iterator Result = 
                getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
            while (Result.hasNext())
            {
              Object[] resultRow = (Object[]) Result.next();
              String[] hashMapValue = new String[2];
              hashMapValue[0] = (String) resultRow[0];
              hashMapValue[1] = (String) resultRow[1];
              String hashMapKey = hashMapValue[0]+hashMapValue[1];
              if (baseYearInactiveObjects.get(hashMapKey)!= null)
              {
                  gLBBObjects.put(hashMapKey,hashMapValue);
                  nInactiveBBObjectCodes = nInactiveBBObjectCodes + 1;
              }
            }
        }

    public void setDocumentService(DocumentService documentService)
    {
        this.documentService = documentService;
    }
     public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
     public void setWorkflowDocumentService (WorkflowDocumentService workflowDocumentService)
     {
         this.workflowDocumentService = workflowDocumentService;
     }
}