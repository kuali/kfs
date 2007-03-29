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

import java.math.BigDecimal;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.text.ParseException;

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
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.bo.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.module.budget.bo.BudgetConstructionAccountReports;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTrackerOverride;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
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
        Boolean Result;
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
        Result = (Boolean) ((Object[]) Results.next()) [0];
        return Result.booleanValue();
            
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
        //  the order is important because of referenctial integrity in the database
        clearBothYearsBCSF(BaseYear);
        clearBothYearsPendingApptFunding(BaseYear);
        clearBothYearsBCPosition(BaseYear);
        //  the calling order is important because of referential integrity in the 
        //  database
        clearBothYearsPBGL(BaseYear);
        clearBothYearsHeaders(BaseYear);
    }

    private void clearBaseYearBCSF(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }

    private void clearBothYearsBCSF(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearBCSF()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }

    private void clearBaseYearBCPosition(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionPosition.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }

    private void clearBothYearsBCPosition(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionPosition.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearBCPosition()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionPosition.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearRequestYearBCPosition(Integer BaseYear)
    {
        Integer RequestYear = BaseYear +1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionPosition.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearRequestYearBCSF(Integer BaseYear)
    {
        Integer RequestYear = BaseYear +1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
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
        // the order here is mandated by referential integrity
        // remove rows from the base year from budget construction months
        Criteria mnCriteriaID = new Criteria();
        mnCriteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria mnQueryID =
            new QueryByCriteria(BudgetConstructionMonthly.class,mnCriteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(mnQueryID);
        // remove rows from the basse year from budget construction general ledger
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
        // the order here is mandated by referential integrity
        QueryByCriteria mnQueryId =
            new QueryByCriteria(BudgetConstructionMonthly.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(mnQueryId);
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearRequestYearPBGL(Integer BaseYear)
    {
        Integer RequestYear = BaseYear + 1;
        // the order here is mandated by referential integrity
        // remove rows from the request year from budget construction months
        Criteria mnCriteriaID = new Criteria();
        mnCriteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria mnQueryID =
            new QueryByCriteria(BudgetConstructionMonthly.class,mnCriteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(mnQueryID);
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

    private void clearBaseYearPendingApptFunding(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }

    private void clearBothYearsPendingApptFunding(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearPendingApptFunding()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
    }
    
    private void clearRequestYearPendingApptFunding(Integer BaseYear)
    {
        Integer RequestYear = BaseYear +1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
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
    private HashMap<String,String[]> CSFTrackerKeys = new HashMap<String,String[]>();
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
                                               boolean GLUpdatesAllowed,
                                               boolean CSFUpdatesAllowed)
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
        //@@TODO:  added this in hopes of solving the memory problem
        //         (we probably can't clear the cache if we have an iterator in a loop)
        // (02/23/2007)  this failed--the workflow thread is way behind ours, and we
        //               killed it after it had only processed 16 of 73 documents.
        // getPersistenceBrokerTemplate().clearCache();
        }
        //  we also have to read CSF for any accounts with no base budget in GL BALANCE
        //  but which pay people in budgeted positions
        if (CSFUpdatesAllowed)
        {
          getCSFCandidateDocumentKeys(BaseYear);
          getCSFOverrideDeletedKeys(BaseYear);
          getCSFOverrideCandidateDocumentKeys(BaseYear);
        getAndStoreCurrentCSFBCHeaderCandidates(BaseYear);
        //@@TODO:  added this in hopes of solving the memory problem
        //         (we probably can't clear the cache if we have an iterator in a loop)
        // getPersistenceBrokerTemplate().clearCache();
    }
        //  now we have to read the newly created documents (after workflow is
        //  finished with them, and change the status flag to correspond to the
        //  budget construction "untouched" status
        //@@TODO:  we need a delay here--we cannot do this until workflow is finished
        // storeBudgetConstructionDocumentsInitialStatus();
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
        if (! acctRptsToMap.isEmpty())
        {
            acctRptsToMap.clear();
        }
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
        
        /********************************************************************************************
         *  (8)  the budget construction position table is dependent on each institution's payroll
         *       system, and thus cannot be coded effectively in Kuali.   However, RI in the database
         *       requires that it be populated.  This is a stub routine to do so.
         ********************************************************************************************
         */
             private HashMap<String,BudgetConstructionPosition> currentBCPosition =
                 new HashMap(BudgetConstructionConstants.ESTIMATED_NUMBER_OF_BUDGETED_POSITIONS);
             private HashSet<String> deletedCSFOverridePositions =
                 new HashSet();
             private String newLockMarker = new String("saveThis");

             private Integer returnedPositionNumber         = new Integer(0);
             private Integer returnedEmplid                 = new Integer(1);
             private Integer returnedChartOfAccountsCode    = new Integer(2);
             private Integer returnedAccountNumber          = new Integer(3);
             private Integer returnedSubAccountNumber       = new Integer(4);
             private Integer returnedFinancialObjectCode    = new Integer(5);
             private Integer returnedFinancialSubObjectCode = new Integer(6);
             private Integer returnedCSFFundingStatusCode   = new Integer(7);
             
             public void createNewBCPosition(Integer BaseYear,
                     boolean PosSyncAllowed,
                     boolean CSFUpdatesAllowed)
             {
               
                // do nothing if batch position updates are turned off  
                if (! PosSyncAllowed)
                {
                    return;
                }
                // read the current positions first
                 readCurrentPositions(BaseYear,CSFUpdatesAllowed);
                //
                // the positions for the base year are those in ACTIVE lines
                // in CSF and CSF override
                // there are two elements of stub code in this
                // (1) it will be necessary to read active, budgeted positions for the
                //     current fiscal year (by effective date at IU) into a hash map
                //     and look for those in the CSF hash map, and fill in the position
                //     attributes using the former.  
                // (2) the next fiscal year positions come ONLY from the HR budgeted
                //     positions (again by effective date at IU).  it does not matter
                //     whether they are in CSF.  in the code below, we spoof the coming
                //     year fetch by using CSF. 
                // initialize the data needed for the spoof routines
                initializeStubValues(BaseYear); 
                // (we will eliminate positions from lines which are not active,
                //  because the positions in inactive lines might no longer be
                //  budgeted, and/or the accounts might no longer be active)
                 readCSFOverridePositions(BaseYear,CSFUpdatesAllowed);
                 readCSFPositions(BaseYear,CSFUpdatesAllowed);
                //  finally, we have to read the position map, and store all the
                //  positions that do not have a default lock ID, changing the lock
                //  ID first
                storeNewPositions(); 
                getPersistenceBrokerTemplate().clearCache();
             }
             
             private String buildDeletedKey(Object[] returnedRow)
             {
                 return ((String) returnedRow[returnedPositionNumber])+
                         vacantEmplid((String) returnedRow[returnedCSFFundingStatusCode],
                                      (String) returnedRow[returnedEmplid])+
                        ((String) returnedRow[returnedChartOfAccountsCode])+
                        ((String) returnedRow[returnedAccountNumber])+
                        ((String) returnedRow[returnedSubAccountNumber])+
                        ((String) returnedRow[returnedFinancialObjectCode])+
                        ((String) returnedRow[returnedFinancialSubObjectCode]);

             }
             
             private void buildDeletedList(Integer BaseYear)
             {
                 // start fresh
                 deletedCSFOverridePositions.clear();
                 String yearString = BaseYear.toString();
                 // get the accounting strings for the lines marked deleted in CSF
                 Criteria criteriaID = new Criteria();
                 criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                                             BaseYear);
                 criteriaID.addNotEqualTo(PropertyConstants.CSF_DELETE_CODE,
                                          BudgetConstructionConstants.ACTIVE_CSF_LINE);
                 String[] selectList = buildCSFPositionSelectList();
                 ReportQueryByCriteria queryID = 
                     new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                               selectList,criteriaID);
                 Iterator resultSet = 
                     getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                 while (resultSet.hasNext())
                 {
                     Object[] returnedRow = (Object[]) resultSet.next();
                     String deletedKey = buildDeletedKey(returnedRow);
                     if (! deletedCSFOverridePositions.contains(deletedKey))
                     {
                         deletedCSFOverridePositions.add(deletedKey);
                     }
                 }
                 LOG.info(String.format("\ndeleted CSF Override rows %d",
                                    deletedCSFOverridePositions.size()));
             }
             
             private String[] buildCSFPositionSelectList()
             {
                 String[] returnArray =
                    {PropertyConstants.POSITION_NUMBER,
                     PropertyConstants.EMPLID,
                     PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                     PropertyConstants.ACCOUNT_NUMBER,
                     PropertyConstants.SUB_ACCOUNT_NUMBER,
                     PropertyConstants.FINANCIAL_OBJECT_CODE,
                     PropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                     PropertyConstants.CSF_FUNDING_STATUS_CODE};
                 return returnArray;
             }
             
             private void buildNewPositionFromCSF(Integer FiscalYear, Object[] rowReturned)
             {
                // we will save new positions with a lock ID that is NOT the default 
                // we will then automatically store them when we run the code to clear
                // locks 
                BudgetConstructionPosition newBCPosition = new BudgetConstructionPosition();
                newBCPosition.setUniversityFiscalYear(FiscalYear);
                newBCPosition.setPositionLockUserIdentifier(newLockMarker);
                newBCPosition.setPositionNumber((String) rowReturned[returnedPositionNumber]);
                // this is spoof code
                fillPositionAttributesSpoof(newBCPosition, rowReturned, FiscalYear);
                // add the new position to the position hash
                currentBCPosition.put(positionKey(FiscalYear,
                                                  (String) rowReturned[returnedPositionNumber]),
                                                  newBCPosition);
             }
             
             private String positionKey (Integer FiscalYear, String PositionNumber)
             {
                 return PositionNumber+FiscalYear.toString();
             }
             
             private void readCSFOverridePositions(Integer BaseYear,
                                                   boolean CSFUpdatesAllowed)
             {
                 Integer overridesRead     = new Integer(0);
                 Integer overridePositions = new Integer(0);
                 Integer overrideRequest   = new Integer(0);
             //  first get the positions in deleted lines
             //  any key marked deleted in CSF override will NOT contribute
             //  to the position table
                 buildDeletedList(BaseYear);
             //  now, read the active CSF override lines    
                 String yearString = BaseYear.toString();
                 // get the accounting strings for the lines marked deleted in CSF
                 Criteria criteriaID = new Criteria();
                 criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                                             BaseYear);
                 criteriaID.addEqualTo(PropertyConstants.CSF_DELETE_CODE,
                                          BudgetConstructionConstants.ACTIVE_CSF_LINE);
                 String[] selectList = buildCSFPositionSelectList(); 
                 ReportQueryByCriteria queryID = 
                     new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                               selectList,criteriaID);
                 Iterator resultSet = 
                     getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                 while (resultSet.hasNext())
                 {
                     overridesRead = overridesRead+1;
                     Object[] rowReturned = (Object[]) resultSet.next();
                     // we know the active rows are not deleted in CSF Override
                     // so, we just go ahead and build a new position, but only if
                     // CSF updates are active
                     if (! CSFUpdatesAllowed)
                     {    
                       String testBaseKey = 
                           positionKey(BaseYear,
                                      (String) rowReturned[returnedPositionNumber]);
                       if (! currentBCPosition.containsKey(testBaseKey))
                       {
                           buildNewPositionFromCSF(BaseYear,rowReturned);
                           overridePositions = overridePositions+1;
                       }
                     }
                     buildRequestYearSpoof(BaseYear+1,rowReturned);
                     overrideRequest = overrideRequest+1;
                 }
                 LOG.info(String.format("\noverride rows (1) read = %d\n"+
                                           "             (2) base year positions = %d\n"+
                                           "             (3) request year positions = %d",
                                           overridesRead, overridePositions, overrideRequest));
             }
             
             private void readCSFPositions(Integer BaseYear, boolean CSFUpdatesAllowed)
             {
                 Integer csfRead     = new Integer(0);
                 Integer csfPositions = new Integer(0);
                 Integer csfRequest   = new Integer(0);
             //  now, read the active CSF lines    
                 String yearString = BaseYear.toString();
                 // get the accounting strings for the lines marked active in CSF
                 Criteria criteriaID = new Criteria();
                 criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                                             BaseYear);
                 criteriaID.addEqualTo(PropertyConstants.CSF_DELETE_CODE,
                                          BudgetConstructionConstants.ACTIVE_CSF_LINE);
                 String[] selectList = buildCSFPositionSelectList(); 
                 ReportQueryByCriteria queryID = 
                     new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class,
                                               selectList,criteriaID);
                 Iterator resultSet = 
                     getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                 while (resultSet.hasNext())
                 {
                     csfRead = csfRead+1;
                     Object[] rowReturned = (Object[]) resultSet.next();
                     // do not include any CSF rows which are marked deleted in CSF 
                     // override
                     String csfKey = buildDeletedKey(rowReturned);
                     if (deletedCSFOverridePositions.contains(csfKey))
                     {
                         continue;
                     }
                     // if CSF updates are not allowed, we will not include any base year
                     // position rows
                     if (CSFUpdatesAllowed)
                     {
                       String testBaseKey = 
                           positionKey(BaseYear,
                                       (String) rowReturned[returnedPositionNumber]);
                       if (! currentBCPosition.containsKey(testBaseKey))
                       {
                           buildNewPositionFromCSF(BaseYear,rowReturned);
                           csfPositions = csfPositions+1;
                       }
                     }
                     buildRequestYearSpoof(BaseYear+1,rowReturned);
                     csfRequest = csfRequest+1;
                 }
                 LOG.info(String.format("\nCSF rows (1) read = %d\n"+
                                           "        (2) base year positions = %d\n"+
                                           "        (3) request year positions = %d",
                                           csfRead, csfPositions, csfRequest));
             }
             
             private void readCurrentPositions(Integer BaseYear,
                                               boolean CSFUpdatesAllowed)
             {
                 QueryByCriteria queryId;
                 if (CSFUpdatesAllowed) 
                 {
                   // we only update the base year if CSF updates are allowed
                   // we always update the request year
                   // this code gets both years   
                   queryId = 
                       new QueryByCriteria(BudgetConstructionPosition.class,
                                           QueryByCriteria.CRITERIA_SELECT_ALL);
                 }
                 else
                 {
                   // base year budget construction positions are frozen
                   // only update the request year positions  
                   Integer RequestYear = BaseYear+1;  
                   Criteria criteriaID = new Criteria();
                   criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                                         RequestYear);
                   queryId = 
                       new QueryByCriteria(BudgetConstructionPosition.class,criteriaID);
                 }
                 Iterator rowsReturned =
                 getPersistenceBrokerTemplate().getIteratorByQuery(queryId);
                 while (rowsReturned.hasNext())
                 {
                     BudgetConstructionPosition currentPosition = 
                         (BudgetConstructionPosition) rowsReturned.next();
                     String hashKey = 
                         positionKey(currentPosition.getUniversityFiscalYear(),
                                     currentPosition.getPositionNumber());
                     currentBCPosition.put(hashKey,currentPosition);
                 }
                 LOG.info(String.format("\nposition count before updates %d",
                                        currentBCPosition.size()));
             }
             
             private void storeNewPositions()
             {
                 Integer positionsWritten = new Integer(0);
                 for (Map.Entry<String,BudgetConstructionPosition> positionRows : 
           currentBCPosition.entrySet())
                 {
                     BudgetConstructionPosition testPosition =
                         positionRows.getValue();
                     if (!(storeNewPositionsCriteria(testPosition)))
                     {
                         // store everything that has a "lock"
                         // this code will also serve to clear hanging locks, although
                         // we should do that as well in the lock release routine because
                         // this code doesn't run if the flag is not set
                         // this mechanism can also permit us to update the default
                         // object class and set the change flag in the "real" system
                         testPosition.setPositionLockUserIdentifier(
                                 BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
                         getPersistenceBrokerTemplate().store(testPosition);
                         positionsWritten = positionsWritten+1;
                     }
                 }
                 LOG.info(String.format("\n%d of %d budget positions written",
                             positionsWritten,currentBCPosition.size()));
             }
             
             private boolean storeNewPositionsCriteria(BudgetConstructionPosition testPosition)
             {
                 //  if we try to test content of A (null) = content og B (null) we get a null
                 //  pointer exception.  so, we need this convoluted routine because the default
                 //  locks in the DB can be nulls
                 //  (the null pointer exception apparently comes from the use of the null
                 //   constant in the compareTo)
                 //  this routine returns true if the tested lock is equal in value to the
                 //  default lock, false otherwise
                 boolean nullLock = (testPosition.getPositionLockUserIdentifier() == null);
                 boolean nullDefaultLock = 
                     (BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS == null);
                 if  (nullLock && nullDefaultLock)
                 {
                     return true;
                 }
                 if ((nullDefaultLock) || (nullLock))
                 {
                     return false;
                 }
                 else
                 {
                     return (testPosition.getPositionLockUserIdentifier().compareTo(
                             BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS) == 0);
                 }
             }

             private String vacantEmplid(String vacantCSFCode, String emplidCSF)
             {
                return (vacantCSFCode.equals(BudgetConstructionConstants.VACANT_CSF_LINE)?
                        BudgetConstructionConstants.VACANT_EMPLID:
                        emplidCSF);
             }
  
             //
             //  these are some stub routines to build a test position table
             //  the implementing institution will have to replace these with
             //  routines that access the HR system at that institution
             //
             private void buildRequestYearSpoof(Integer RequestYear, Object[] rowReturned)
             {
                 String testRequestKey = 
                     positionKey(RequestYear,
                                 (String) rowReturned[returnedPositionNumber]);
                 if (! currentBCPosition.containsKey(testRequestKey))
                 {
                     buildNewPositionFromCSF(RequestYear,rowReturned);
                 }
             }
             
             private String missingRCCode = new String("NO");
             private String missingOrgCode = new String(" ");
             private String[] defaultValue;
             private String fabricatedPositionTitle = new String("Generated from CSF");
             private HashMap<String,String> orgRCMap;
             private HashMap<String,String[]> acctRCMap;
             
             private String acctRCKey(String ChartCode, String AccountNumber)
             {
                 return AccountNumber+ChartCode;
             }
             
             private java.sql.Date defaultAugustFirst(Integer dateYear)
             {
                 // this routine assumes a specific SQLDATE_FORMAT in the date service
                 try
                 {
                 java.sql.Date augustFirst = 
                         dateTimeService.convertToSqlDate(dateYear.toString()+
                                                          "-08-01");
                 return augustFirst;
                 }
                 catch (ParseException ex)
                 {
                     LOG.warn("\nproblem setting July 1 position date with: ("+
                             dateYear.toString()+"-08-01)\ncurrent date was used\n"
                             +ex.getMessage());
                     return dateTimeService.getCurrentSqlDateMidnight();
                 }
             }
             
             private java.sql.Date defaultJulyFirst(Integer dateYear)
             {
                 // this routine assumes a specific SQLDATE_FORMAT in the date service
                 try
                 {
                 java.sql.Date julyFirst = 
                         dateTimeService.convertToSqlDate(dateYear.toString()+
                                                          "-07-01");
                 return julyFirst;
                 }
                 catch (ParseException ex)
                 {
                     LOG.warn("\nproblem setting July 1 position date with: ("+
                             dateYear.toString()+"-07-01)\ncurrent date was used\n"
                             +ex.getMessage());
                     return dateTimeService.getCurrentSqlDateMidnight();
                 }
             }
 
             private void fillPositionAttributesSpoof(BudgetConstructionPosition newBCPosition,
                                                      Object[] rowReturned, Integer FiscalYear)
             {
                 // default the regular/temporary indicator to regular, and mark as budgeted
                 newBCPosition.setPositionRegularTemporary("R");
                 newBCPosition.setBudgetedPosition(true);
                 newBCPosition.setPositionEffectiveStatus("A");
                 newBCPosition.setPositionStatus("A");
                 newBCPosition.setPositionDescription(fabricatedPositionTitle);
                 // standard hours will be 40
                 newBCPosition.setPositionStandardHoursDefault(new BigDecimal(40));
                 // set the position type to academic, staff monthly, or staff biweekly
                 String posTypeString = 
                     positionTypeSpoof((String) rowReturned[returnedFinancialObjectCode]);
                 newBCPosition.setIuPositionType(posTypeString);
                 // academic are 10-pay, others 12-pay
                 if (posTypeString.equals("AC"))
                 {
                   java.sql.Date augustFirst = defaultAugustFirst(FiscalYear);  
                   newBCPosition.setPositionEffectiveDate(augustFirst);  
                   newBCPosition.setIuNormalWorkMonths(new Integer(10));
                   newBCPosition.setIuPayMonths(new Integer(10));
                 }
                 else
                 {
                   java.sql.Date julyFirst = defaultJulyFirst(FiscalYear); 
                   newBCPosition.setPositionEffectiveDate(julyFirst);  
                   newBCPosition.setIuNormalWorkMonths(new Integer(12));
                   newBCPosition.setIuPayMonths(new Integer(12));
                 }
                 // default object class comes from the CSF line
                 // department ID and RC come from the account, chart comes from the CSF
                 // line.  if the position is split, the first one in wins the prize
                 newBCPosition.setIuDefaultObjectCode((String) rowReturned[returnedFinancialObjectCode]);
                 String acctTestKey = acctRCKey((String) rowReturned[returnedChartOfAccountsCode],
                                                (String) rowReturned[returnedAccountNumber]);
                 //
                 //  the RC code should be derived from the department ID on the position
                 //  in HR.
                 //  a default is used if no RC can be assigned
                 newBCPosition.setResponsibilityCenterCode(missingRCCode);
                 newBCPosition.setPositionDepartmentIdentifier(missingOrgCode);
                 if (acctRCMap.containsKey(acctTestKey))
                 {
                   String[] acctRptsData =
                       acctRCMap.get(acctTestKey);
                   newBCPosition.setPositionDepartmentIdentifier(acctRptsData[0]);
                   newBCPosition.setResponsibilityCenterCode(acctRptsData[1]);
                 }
             }
             
             private void initializeStubValues(Integer BaseYear)
             {
                 readAcctRCMap(BaseYear);
             }
             
             private String positionOrg(String chartCode, String orgCode)
             {
                 return chartCode+"-"+orgCode;
             }
             
             private String positionTypeSpoof (String objectClass)
             {
                 //24xx is monthly, 25xx bi-weekly, otherwise default to academic
                 String empTypeID = objectClass.substring(0,2);
                 return ((empTypeID.equals("24"))?"SM":
                         ((empTypeID.equals("25"))?"SB":"AC"));
             }
             
             private void readAcctRCCSF(Class csfClass, Integer BaseYear)
             {
                 Criteria criteriaID = new Criteria();
                 criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                                       BaseYear);
                 String[] selectList = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                        PropertyConstants.ACCOUNT_NUMBER};
                 ReportQueryByCriteria queryID =
                     new ReportQueryByCriteria(csfClass, selectList, criteriaID, true);
                 Iterator resultSet = 
                     getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                 while (resultSet.hasNext())
                 {
                     Object[] rowReturned = (Object[]) resultSet.next();
                     String acctKey = acctRCKey((String) rowReturned[0],
                                                (String) rowReturned[1]);
                     if (!(acctRCMap.containsKey(acctKey)))
                     {
                         acctRCMap.put(acctKey,defaultValue);
                     }
                 }
             }

             private Integer readAcctRCMapCount(Integer BaseYear)
             {
                Integer acctCount = new Integer(0);
                Criteria criteriaID = new Criteria();
                // we will estimate the size of the map needed based on the
                // number of accounts alone, and not on the full key.  if this is
                // an underestimate, the map can expand on its own
                //
                // CSF override is a method for institutions which do not have a CSF
                // tracker to enter payroll data.  so, we should include it in the count
                criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                                      BaseYear);
                String[] selectList = {"COUNT(DISTINCT "+
                                       PropertyConstants.ACCOUNT_NUMBER+")"}; 
                ReportQueryByCriteria queryID = 
                    new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                              selectList, criteriaID);
                Iterator ovrdIter = 
                    getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                if (ovrdIter.hasNext())
                {
                    BigDecimal resultValue = (BigDecimal) ((Object[]) ovrdIter.next())[0];
                    acctCount = acctCount +
                                (Integer) resultValue.intValue();  
                }
                ReportQueryByCriteria queryCSF =
                    new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class,
                            selectList, criteriaID);
                Iterator csfIter =
                    getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryCSF);
                if (csfIter.hasNext())
                {
                    BigDecimal resultValue = (BigDecimal) ((Object[]) csfIter.next())[0];
                    acctCount = acctCount +
                                (Integer) resultValue.intValue();  
                }
                return acctCount+10;
             }
             
             private void readAcctRCMap(Integer BaseYear)
             {
                 //  we need the set of organizations/RC codes
                 readOrgRCMap();
                 //
                 Integer acctCount = readAcctRCMapCount(BaseYear);
                 acctRCMap = new HashMap<String,String[]>(acctCount);
                 defaultValue = new String[] {missingOrgCode, missingRCCode};
                 // first we build a map with the default RC and organization
                 //    take override accounts first, then CSF accounts
                 readAcctRCCSF(CalculatedSalaryFoundationTrackerOverride.class, BaseYear);
                 readAcctRCCSF(CalculatedSalaryFoundationTracker.class, BaseYear);
                 // next, read the account table to fill in the org codes and the RC's.
                 // every account key in the hash should have a counterpart in the account
                 // table.  as each of those counterparts comes up as we iterate through
                 // all the accounts, we will join to the organization table to get
                 // RC code
                 Criteria criteriaID = new Criteria();
                 criteriaID.addEqualTo(PropertyConstants.ACCOUNT_CLOSED_INDICATOR,
                                       false);
                 // we cannot return a business object with a report query
                 // so, we just return what we need
                 String [] attrbList = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                        PropertyConstants.ACCOUNT_NUMBER,
                                        PropertyConstants.ORGANIZATION_CODE};
                 Integer chartIndex        = new Integer(0);
                 Integer accountIndex      = new Integer(1);
                 Integer organizationIndex = new Integer(2);
                 ReportQueryByCriteria queryID = 
                     new ReportQueryByCriteria(org.kuali.module.chart.bo.Account.class,
                                               attrbList, criteriaID);
                 Iterator accountsReturned = 
                     getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                 while (accountsReturned.hasNext())
                 {
                     Object[] rowReturned = (Object[]) accountsReturned.next();
                     String orgPositionKey = positionOrg((String) rowReturned[chartIndex],
                                                         (String) rowReturned[organizationIndex]);
                     String acctKey = acctRCKey((String) rowReturned[chartIndex],
                                                (String) rowReturned[accountIndex]);
                     if ((acctRCMap.containsKey(acctKey)) && 
                         (orgRCMap.containsKey(orgPositionKey)))
                     {
                         String[] newValue = {orgPositionKey,
                                              orgRCMap.get(orgPositionKey)};
                         // this will replace the default values
                         acctRCMap.put(acctKey,newValue);
                     }
                 }
                 //  at this point, we no longer need the orgRCMap
                 //  we want to minimize memory use
                 orgRCMap.clear();
             }
             
             private Integer readOrgRCCount()
             {
                 String[] selectList = {"COUNT(*)"};
                 ReportQueryByCriteria queryID = 
                     new ReportQueryByCriteria(Org.class, selectList,
                             QueryByCriteria.CRITERIA_SELECT_ALL);
                 Iterator resultRow =
                 getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                 if (!(resultRow.hasNext()))
                 {
                     return new Integer(0);
                 }
                 else
                 {
                     BigDecimal resultValue = (BigDecimal) ((Object[]) resultRow.next())[0];
                     return (Integer) resultValue.intValue();
                 }
             }
             
             private void readOrgRCMap()
             {
                 Integer orgCount = readOrgRCCount();
                 orgRCMap = new HashMap<String,String>(orgCount+3);
                 String[] selectList = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                        PropertyConstants.ORGANIZATION_CODE,
                                        PropertyConstants.RESPONSIBILITY_CENTER_CODE};
                 ReportQueryByCriteria queryID = 
                     new ReportQueryByCriteria(Org.class, selectList,
                                               QueryByCriteria.CRITERIA_SELECT_ALL);
                 Iterator resultSet =
                     getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                 while (resultSet.hasNext())
                 {
                     Object[] rowReturned = (Object[]) resultSet.next();
                     String orgKey = positionOrg ((String) rowReturned[0],
                                                  (String) rowReturned[1]);
                     orgRCMap.put(orgKey,(String) rowReturned[2]);
                 }
             }
             
             
             
             
             
    /****************************************************************************************
    * (9)  this code builds the budget construction CSF tracker and the budget construction
    *      appointment funding
    ****************************************************************************************
    */
    public void buildAppointmentFundingAndBCSF(Integer BaseYear)
    {
       /*********************************************************************
        * RI requirements:
        * this method assumes that ALL budget construction positions for the
        * request year are in the position table, 
        * and
        * that budget construction documents for every accounting key in the
        * CSF tables have been created 
        **********************************************************************/
       // budget construction CSF tracker is always rebuilt from scratch
       // (it doesn't make sense to carry a base salary line that has 
       //  has disappeared from the system--hence the current base--from
       //  one run to the next.)
       clearBCCSF(BaseYear); 
       // build the new BC CSF objects in memory
       setUpCSFOverrideKeys(BaseYear); 
       setUpBCSFMap(BaseYear);
       setUpKeysNeedingRounding(BaseYear);
       readCSFOverride(BaseYear);
       readCSF(BaseYear);
       adjustCSFRounding();
       // at this point we should clear the cache
       // any objects fetched from the database (CSF and CSF Override) will not
       // be written back, and we may need the memory.
       getPersistenceBrokerTemplate().clearCache();
       //  store bCSF rows matching current appointment funding
       readExistingAppointmentFunding(BaseYear);
       //  if all of the bCSF rows have been stored, we can quit here
       if (bCSF.size() == 0)
       {
           return;
       }
       //  what we have left are the bCSF rows that do NOT match appointment funding
       //  -- createNewBCDocumentsFromGLCSF is called in both the genesis and update
       //     steps, before anything else is done.  therefore, we are assured that 
       //     all the documents exist.
       //  -- we need to create new GL (if the accounting key is not yet in GL).
       //     this requires that we have a document number, so we need to read those.
       //     this happens regardless of whether GL updates are allowed, because we
       //     will only be adding GL rows with 0 amounts.  there is only base in CSF,
       //     no request.
       //
       //  >> RI requires that data is stored in the order indicated.
       //  -- we will also have to create new appointment funding rows (again, with
       //     no request.
       //  -- finally, we will have to store the bCSF rows themselves.
       setUpbcHdrDocNumbers(BaseYear);
       setUpCurrentPBGLKeys(BaseYear);
       setUpPositionNormalWorkMonths(BaseYear);
       // we should be able to clear the cache again
       // nothing that has been written needs to persist
       // all that exists at this point is a set of BCSF rows that have never
       // been in the database
       getPersistenceBrokerTemplate().clearCache();
    }
    
    // the set of new BCSF objects to be written
    private HashMap<String,BudgetConstructionCalculatedSalaryFoundationTracker> bCSF;
    // hashmap to hold the document numbers for each accounting key in the header
    private HashMap<String,String> bcHdrDocNumbers;
    // hashset to hold the accounting string for each pending GL entry
    private HashSet<String> currentPBGLKeys;
    // keys for deleted or vacant rows present in the override CSF: none of these keys
    // will load to BCSF from either the override or actual CSF (even if they
    // are active in the actual CSF) 
    private HashSet<String> csfOverrideKeys;
    // EMPLID's in CSF which have more than one active row
    // we budget in whole dollars, while payroll deals in pennies
    // we will use this for our complicated rounding algorithm, to prevent
    // to keep the total budget base salary within a dollar of the payroll salary
    private HashMap<String,roundMechanism> keysNeedingRounding;
    // we need the position normal work months to write a new appointment funding row
    private HashMap<String,Integer> positionNormalWorkMonths;

    // overload the vacant BCSF line object builders
    private void
    addToExistingBCSFVacant(CalculatedSalaryFoundationTrackerOverride csf,
                            String csfKey)
    {
      //
      // this method takes care of a rare occurrence.
      // - more than one person shares a position in the same line.
      // - both people leave on the same day 
      // - since each vacant CSF line carries the EMPLID of the last
      //   incumbent, and EMPLID is part of the key, there are two
      //   separate lines in CSF.
      // - the EMPLID is replaced with the vacant ID in BCSF, so there
      //   will only be one line in BCSF, and we need to aggregate the
      //   amounts and effort  
      BudgetConstructionCalculatedSalaryFoundationTracker nowBCSF =
          bCSF.get(csfKey);
      // first round the amount to whole dollars
      KualiDecimal roundedAmount = csf.getCsfAmount().setScale(0);
      nowBCSF.setCsfAmount(nowBCSF.getCsfAmount().add(roundedAmount));
      // increase the percent time (maximum of 100)
      BigDecimal pctTime = nowBCSF.getCsfTimePercent();
      pctTime.add(csf.getCsfTimePercent());
      if (pctTime.floatValue() > 100.0)
      {
          pctTime = new BigDecimal(100.0);
      }
      nowBCSF.setCsfTimePercent(pctTime);
      // increase the FTE (full-time equivalent) (maximum of 1.0)
      BigDecimal csfFTE = nowBCSF.getCsfFullTimeEmploymentQuantity();
      csfFTE.add(csf.getCsfFullTimeEmploymentQuantity());
      if (csfFTE.floatValue() > 1.0)
      {
          csfFTE = new BigDecimal(1.0);
      }
      nowBCSF.setCsfFullTimeEmploymentQuantity(csfFTE);
    }
    
    private void
    addToExistingBCSFVacant(CalculatedSalaryFoundationTracker csf,
                            String csfKey)
    {
        //
        // this method takes care of a rare occurrence.
        // - more than one person shares a position in the same line.
        // - both people leave on the same day 
        // - since each vacant CSF line carries the EMPLID of the last
        //   incumbent, and EMPLID is part of the key, there are two
        //   separate lines in CSF.
        // - the EMPLID is replaced with the vacant ID in BCSF, so there
        //   will only be one line in BCSF, and we need to aggregate the
        //   amounts and effort  
      BudgetConstructionCalculatedSalaryFoundationTracker nowBCSF =
          bCSF.get(csfKey);
      // first round the amount to whole dollars
      KualiDecimal roundedAmount = csf.getCsfAmount().setScale(0);
      nowBCSF.setCsfAmount(nowBCSF.getCsfAmount().add(roundedAmount));
      // increase the percent time (maximum of 100)
      BigDecimal pctTime = nowBCSF.getCsfTimePercent();
      pctTime.add(csf.getCsfTimePercent());
      if (pctTime.floatValue() > 100.0)
      {
          pctTime = new BigDecimal(100.0);
      }
      nowBCSF.setCsfTimePercent(pctTime);
      // increase the FTE (full-time equivalent) (maximum of 1.0)
      BigDecimal csfFTE = nowBCSF.getCsfFullTimeEmploymentQuantity();
      csfFTE.add(csf.getCsfFullTimeEmploymentQuantity());
      if (csfFTE.floatValue() > 1.0)
      {
          csfFTE = new BigDecimal(1.0);
      }
      nowBCSF.setCsfFullTimeEmploymentQuantity(csfFTE);
    }
    
    // make the rounding adjustments
    private void adjustCSFRounding()
    {
        for (Map.Entry<String,roundMechanism> roundMap: keysNeedingRounding.entrySet())
        {
           roundMechanism rx = roundMap.getValue();
           rx.fixRoundErrors();
        }
        // we can reclaim the storage
        keysNeedingRounding.clear();
    }
    
    // overload the BCSF object builders
    private void 
    buildAndStoreBCSFfromCSF(CalculatedSalaryFoundationTrackerOverride csf,
                             String csfKey)
    {
       boolean vacantLine = isVacantLine(csf);  
       BudgetConstructionCalculatedSalaryFoundationTracker csfBC = new
       BudgetConstructionCalculatedSalaryFoundationTracker();
       // budget construction CSF contains the coming fiscal year
       csfBC.setUniversityFiscalYear(csf.getUniversityFiscalYear()+1);
       csfBC.setChartOfAccountsCode(csf.getChartOfAccountsCode());
       csfBC.setAccountNumber(csf.getAccountNumber());
       csfBC.setSubAccountNumber(csf.getSubAccountNumber());
       csfBC.setFinancialObjectCode(csf.getFinancialObjectCode());
       csfBC.setFinancialSubObjectCode(csf.getFinancialSubObjectCode());
       csfBC.setPositionNumber(csf.getPositionNumber());
       // budget construction CSF always contains the vacant EMPLID, not
       // the EMPLID of the last incumbent
       csfBC.setEmplid((vacantLine?
                       BudgetConstructionConstants.VACANT_EMPLID:
                       csf.getEmplid()));
       csfBC.setCsfFullTimeEmploymentQuantity(csf.getCsfFullTimeEmploymentQuantity());
       csfBC.setCsfTimePercent(csf.getCsfTimePercent());
       csfBC.setCsfFundingStatusCode(csf.getCsfFundingStatusCode());
       // we only worry about rounding errors when the line is not vacant
       // since all vacant lines in CSF have the same (vacant) EMPLID, we
       // would have to round by position. 
       if (!vacantLine)
       {
           csfBC.setCsfAmount(csf.getCsfAmount());
           bCSF.put(csfKey,csfBC);
           // now we have to round and save the rounding error
           roundMechanism rX = keysNeedingRounding.get(csf.getEmplid());
           rX.addNewBCSF(csfBC);
       }
       else
       {
           // for vacant lines, we have to round to whole dollars
           csfBC.setCsfAmount(csf.getCsfAmount().setScale(0));
           bCSF.put(csfKey,csfBC);
       }
    }
    
    private void 
    buildAndStoreBCSFfromCSF(CalculatedSalaryFoundationTracker csf,
                             String csfKey)
    {
       boolean vacantLine = isVacantLine(csf);  
       BudgetConstructionCalculatedSalaryFoundationTracker csfBC = new
       BudgetConstructionCalculatedSalaryFoundationTracker();
       // budget construction CSF contains the coming fiscal year
       csfBC.setUniversityFiscalYear(csf.getUniversityFiscalYear()+1);
       csfBC.setChartOfAccountsCode(csf.getChartOfAccountsCode());
       csfBC.setAccountNumber(csf.getAccountNumber());
       csfBC.setSubAccountNumber(csf.getSubAccountNumber());
       csfBC.setFinancialObjectCode(csf.getFinancialObjectCode());
       csfBC.setFinancialSubObjectCode(csf.getFinancialSubObjectCode());
       csfBC.setPositionNumber(csf.getPositionNumber());
       // budget construction CSF always contains the vacant EMPLID, not
       // the EMPLID of the last incumbent
       csfBC.setEmplid((vacantLine?
                       BudgetConstructionConstants.VACANT_EMPLID:
                       csf.getEmplid()));
       csfBC.setCsfFullTimeEmploymentQuantity(csf.getCsfFullTimeEmploymentQuantity());
       csfBC.setCsfTimePercent(csf.getCsfTimePercent());
       csfBC.setCsfFundingStatusCode(csf.getCsfFundingStatusCode());
       // we only worry about rounding errors when the line is not vacant
       // since all vacant lines in CSF have the same (vacant) EMPLID, we
       // would have to round by position, and positions can be shared. 
       if (!vacantLine)
       {
           csfBC.setCsfAmount(csf.getCsfAmount());
           bCSF.put(csfKey,csfBC);
           // now we have to round and save the rounding error
           roundMechanism rX = keysNeedingRounding.get(csf.getEmplid());
           rX.addNewBCSF(csfBC);
       }
       else
       {
           // for vacant lines, we have to round to whole dollars
           csfBC.setCsfAmount(csf.getCsfAmount().setScale(0));
           bCSF.put(csfKey,csfBC);
       }
    }
    
    private void
    buildAppointemntFundingFromBCSF(BudgetConstructionCalculatedSalaryFoundationTracker bcsf)
    {
        // current referential integrity insists that the position exists
        // if implementers take it out, we hedge our bets below
        String positionNumber = bcsf.getPositionNumber();
        Integer normalWorkMonths =
            (positionNormalWorkMonths.containsKey(positionNumber)?
             positionNormalWorkMonths.get(positionNumber):12);
        // rqstAmount and notOnLeave are used elswhere and defined globally
        KualiDecimal defaultAmount = KualiDecimal.ZERO;
        BigDecimal   defaultFractions = new BigDecimal(0);
        //
        PendingBudgetConstructionAppointmentFunding bcaf =
            new PendingBudgetConstructionAppointmentFunding();
        bcaf.setUniversityFiscalYear(bcsf.getUniversityFiscalYear());
        bcaf.setChartOfAccountsCode(bcsf.getChartOfAccountsCode());
        bcaf.setAccountNumber(bcsf.getAccountNumber());
        bcaf.setSubAccountNumber(bcsf.getSubAccountNumber());
        bcaf.setFinancialObjectCode(bcsf.getFinancialObjectCode());
        bcaf.setFinancialSubObjectCode(bcsf.getFinancialSubObjectCode());
        bcaf.setEmplid(bcsf.getEmplid());
        bcaf.setPositionNumber(positionNumber);
        bcaf.setAppointmentRequestedFteQuantity(
                bcsf.getCsfFullTimeEmploymentQuantity());
        bcaf.setAppointmentRequestedTimePercent(
                bcsf.getCsfTimePercent());
        // set the defaults
        bcaf.setAppointmentFundingDurationCode(notOnLeave);
        bcaf.setAppointmentRequestedCsfAmount(defaultAmount);
        bcaf.setAppointmentRequestedCsfFteQuantity(defaultFractions);
        bcaf.setAppointmentRequestedCsfTimePercent(defaultFractions);
        bcaf.setAppointmentTotalIntendedAmount(defaultAmount);
        bcaf.setAppointmentTotalIntendedFteQuantity(defaultFractions);
        bcaf.setAppointmentRequestedAmount(rqstAmount);
        bcaf.setAppointmentRequestedPayRate(defaultFractions);
        bcaf.setAppointmentFundingMonth(normalWorkMonths);
        // for a new row, these are always false
        bcaf.setAppointmentFundingDeleteIndicator(false);
        bcaf.setPositionObjectChangeIndicator(false);
        bcaf.setPositionSalaryChangeIndicator(false);
        // now store the result
        getPersistenceBrokerTemplate().store(bcaf);
        // store the new BCSF row as well
        getPersistenceBrokerTemplate().store(bcsf);
    }
    
    private String buildAppointmentFundingKey(
                   PendingBudgetConstructionAppointmentFunding bcaf)
    {
        return (bcaf.getEmplid())+
        bcaf.getPositionNumber()+
        bcaf.getAccountNumber()+
        bcaf.getChartOfAccountsCode()+
        bcaf.getSubAccountNumber()+
        bcaf.getFinancialObjectCode()+
        bcaf.getFinancialSubObjectCode();
    }
    
    // overload the CSF key builders 
    private String buildCSFKey(CalculatedSalaryFoundationTrackerOverride csf)
    {
        return (csf.getEmplid())+
                csf.getPositionNumber()+
                csf.getAccountNumber()+
                csf.getChartOfAccountsCode()+
                csf.getSubAccountNumber()+
                csf.getFinancialObjectCode()+
                csf.getFinancialSubObjectCode();
    }

    private String buildCSFKey(CalculatedSalaryFoundationTracker csf)
    {
        return (csf.getEmplid())+
                csf.getPositionNumber()+
                csf.getAccountNumber()+
                csf.getChartOfAccountsCode()+
                csf.getSubAccountNumber()+
                csf.getFinancialObjectCode()+
                csf.getFinancialSubObjectCode();
    }
    
    private void buildPBGLFromBCSF(
            BudgetConstructionCalculatedSalaryFoundationTracker bcsf)
    {
        // first we need to see if a new PBGL row is needed
  //      String testKey = bcsf.get
        PendingBudgetConstructionGeneralLedger pbGL = 
            new PendingBudgetConstructionGeneralLedger();
    }
    
    private String buildVacantCSFKey(CalculatedSalaryFoundationTrackerOverride csf)
    {
        boolean vacantLine = isVacantLine(csf);
        return (vacantLine?BudgetConstructionConstants.VACANT_EMPLID:
                csf.getEmplid())+
                csf.getPositionNumber()+
                csf.getAccountNumber()+
                csf.getChartOfAccountsCode()+
                csf.getSubAccountNumber()+
                csf.getFinancialObjectCode()+
                csf.getFinancialSubObjectCode();
    }

    private String buildVacantCSFKey(CalculatedSalaryFoundationTracker csf)
    {
        boolean vacantLine = isVacantLine(csf);
        return (vacantLine?BudgetConstructionConstants.VACANT_EMPLID:
                csf.getEmplid())+
                csf.getPositionNumber()+
                csf.getAccountNumber()+
                csf.getChartOfAccountsCode()+
                csf.getSubAccountNumber()+
                csf.getFinancialObjectCode()+
                csf.getFinancialSubObjectCode();
    }
    //
    // clean out the existing BCSF data for the key in question.
    private void clearBCCSF(Integer BaseYear)
    {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear);
        QueryByCriteria queryID = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                                criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        // as always, we should clear the cache after a delete,
        // even though in this case we haven't yet fetched much
        getPersistenceBrokerTemplate().clearCache();
    }
    
    
//  we will overload both of these checks as well             
    private boolean isVacantLine(CalculatedSalaryFoundationTracker csf)
    {
       return  ((csf.getCsfFundingStatusCode().equals(
                 BudgetConstructionConstants.VACANT_CSF_LINE))||
                (csf.getCsfFundingStatusCode().equals(
                 BudgetConstructionConstants.UNFUNDED_CSF_LINE)));
    }
    
    private boolean isVacantLine(CalculatedSalaryFoundationTrackerOverride csf)
    {
       return  ((csf.getCsfFundingStatusCode().equals(
                 BudgetConstructionConstants.VACANT_CSF_LINE))||
                (csf.getCsfFundingStatusCode().equals(
                 BudgetConstructionConstants.UNFUNDED_CSF_LINE)));
    }

    // here are the routines to build BCSF
    
    private void readCSF(Integer BaseYear)
    {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(PropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        QueryByCriteria queryID = 
            new QueryByCriteria(CalculatedSalaryFoundationTracker.class,criteriaID);
        Iterator csfResultSet =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (csfResultSet.hasNext())
        {
            CalculatedSalaryFoundationTracker csfRow = 
                (CalculatedSalaryFoundationTracker) csfResultSet.next();
            // has this been overridden?  if so, don't store it
            String testKey = buildCSFKey(csfRow);
            if (csfOverrideKeys.contains(testKey))
            {
                break;
            }
            // is the line vacant
            testKey = buildVacantCSFKey(csfRow);
            if (isVacantLine(csfRow)&&(bCSF.containsKey(testKey)))
            {
                //the line is vacant and it is already in CSF
                addToExistingBCSFVacant(csfRow,testKey);
            }
            else
            {
                buildAndStoreBCSFfromCSF(csfRow,testKey);
            }
        }
        // we no longer need the list of csf override keys--recycle
        csfOverrideKeys.clear();
    }
    
    private void readCSFOverride(Integer BaseYear)
    {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(PropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        QueryByCriteria queryID = 
            new QueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,criteriaID);
        Iterator csfResultSet =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (csfResultSet.hasNext())
        {
            CalculatedSalaryFoundationTrackerOverride csfRow = 
                (CalculatedSalaryFoundationTrackerOverride) csfResultSet.next();
            // has this been overridden?  if so, don't store it
            // is the line vacant
            String testKey = buildVacantCSFKey(csfRow);
            if (isVacantLine(csfRow)&&(bCSF.containsKey(testKey)))
            {
                //the line is vacant and it is already in CSF
                addToExistingBCSFVacant(csfRow,testKey);
            }
            else
            {
                buildAndStoreBCSFfromCSF(csfRow,testKey);
            }
        }
    }

    private void readExistingAppointmentFunding (Integer BaseYear)
    {
        // we will read all existing appointment funding
        // -- if an AF object matches with a BCSF row, we will store and
        //    remove the BCSF row, and ignore the AF object
        // -- if an AF object does NOT match with a BCSF row, we will
        //    check to see if it has been altered by a user.  if not, we
        //    will mark it deleted and store it.
        //
        Integer RequestYear = BaseYear+1;
        Criteria criteriaID = new Criteria();
        // we add this criterion so that it is possible to have more than
        // one year at a time in budget construction
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              RequestYear);
        QueryByCriteria queryID = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                                criteriaID);
        Iterator bcafResults =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (bcafResults.hasNext())
        {
            PendingBudgetConstructionAppointmentFunding bcaf =
                (PendingBudgetConstructionAppointmentFunding) bcafResults.next();
            String testKey = buildAppointmentFundingKey(bcaf);
            if (bCSF.containsKey(testKey))
            {
                // the new BCSF row is already in appointment funding
                // we store the BCSF row, delete it from the hash map, and go on
                BudgetConstructionCalculatedSalaryFoundationTracker bCSFRow =
                    bCSF.get(testKey);
                getPersistenceBrokerTemplate().store(bCSFRow);
                bCSF.remove(testKey);
            }
            else
            {
                // the current funding row is NOT in the new base set
                // we will mark it deleted if it came in from CSF and has not
                // been altered by a user
                untouchedAppointmentFunding(bcaf);
            }
        }
    }
    
    // set up the hash objects   
    private void setUpBCSFMap (Integer BaseYear)
    {
        // we'll just overestimate, making the size equal to active override 
        // rows and active CSF rows, even though the former might replace some
        // of the latter
        Integer bCSFSize = new Integer(0);
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(PropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        String[] selectList = {"COUNT(*)"};
        ReportQueryByCriteria queryID =
            new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                      selectList,criteriaID);
        Iterator ovrdCntr =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (ovrdCntr.hasNext())
        {
            bCSFSize = ((BigDecimal) ((Object[]) ovrdCntr.next())[0]).intValue();
        }
        queryID =
            new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class,
                                      selectList,criteriaID);
        Iterator csfCntr =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (csfCntr.hasNext())
        {
            bCSFSize = bCSFSize +
                       ((BigDecimal) ((Object[]) ovrdCntr.next())[0]).intValue();
        }
        bCSF = new HashMap<String,
                          BudgetConstructionCalculatedSalaryFoundationTracker>(bCSFSize);
    }
    
    private void setUpbcHdrDocNumbers(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        String[] selectList = {"COUNT(*)"};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(BudgetConstructionHeader.class,selectList,
                                      criteriaID);
        Iterator counterRow = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (counterRow.hasNext())
        {
            Integer headCount =
                ((BigDecimal)((Object[]) counterRow.next())[0]).intValue();
            bcHdrDocNumbers = new HashMap<String,String>(headCount);
        }
        //  now we have to get the actual data
        String[] headerList = {PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                               PropertyConstants.ACCOUNT_NUMBER,
                               PropertyConstants.SUB_ACCOUNT_NUMBER,
                               PropertyConstants.DOCUMENT_NUMBER};
        queryID =
            new ReportQueryByCriteria(BudgetConstructionHeader.class,
                                      headerList,criteriaID);
        Iterator headerRows =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (headerRows.hasNext())
        {
            Object[] headerRow = (Object[]) headerRows.next();
            String testKey = ((String) headerRow[0])+
                             ((String) headerRow[1])+
                             ((String) headerRow[2]);
            bcHdrDocNumbers.put(testKey,((String) headerRow[3]));
        }
    }
    
    private void setUpCSFOverrideKeys(Integer BaseYear)
    {
    //  these are rows in CSF Override--they should take precedence
    //  over what is in CSF
    //  the idea is this:
    //  (1) we build BCSF from CSF Override first.  so, when we read CSF, 
    //      we will not create a new BCSF entry if the override already has
    //      created one.
    //  (2) the override will create an entry with the same key as CSF unless
    //      (a) the override has a deleted row or (b) the override has a 
    //      vacant row so that the EMPLID is changed to the vacant EMPLID
    //      in BCSF.
    //   So, we create a list of override keys possibly missing in BCSF
    //   which can be used to eliminate CSF candidates for BCSF.    
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear);
        Criteria deleteCriteria = new Criteria();
        deleteCriteria.addNotEqualTo(PropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        Criteria vacantCriteria = new Criteria();
        vacantCriteria.addEqualTo(PropertyConstants.CSF_FUNDING_STATUS_CODE,
                                  BudgetConstructionConstants.VACANT_CSF_LINE);
        deleteCriteria.addOrCriteria(vacantCriteria);
        criteriaID.addAndCriteria(deleteCriteria);
        String[] selectList = {"COUNT(*)"};
        ReportQueryByCriteria queryID =
            new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                      selectList,criteriaID);
        Iterator csfOverride =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        Integer overrideSize = new Integer(0);
        while (csfOverride.hasNext())
        {
            overrideSize =
                ((BigDecimal)((Object[]) csfOverride.next())[0]).intValue();
        }
        if (overrideSize > 0)
        {
           csfOverrideKeys = new HashSet<String>(overrideSize); 
        }
        else
        {
           csfOverrideKeys = new HashSet<String>(); 
        }
    // now we want to build the hash set
    QueryByCriteria qry = 
       new QueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                           criteriaID);
    Iterator csfOvrd = getPersistenceBrokerTemplate().getIteratorByQuery(qry);
    while (csfOvrd.hasNext())
    {
        csfOverrideKeys.add(buildCSFKey(
                       (CalculatedSalaryFoundationTrackerOverride) csfOvrd.next()));
    }
    // the hashset of override keys must exist before BCSF can be built
    // so, we should be able to clear the cache to save on memory
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void setUpCurrentPBGLKeys(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              RequestYear);
        String[] selectCount = {"COUNT(*)"};
        ReportQueryByCriteria queryID =
            new ReportQueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
                                      selectCount,criteriaID);
        Iterator rowCounter =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (rowCounter.hasNext())
        {
            Integer rowCount = 
                ((BigDecimal)((Object[]) rowCounter.next())[0]).intValue();
            currentPBGLKeys = new HashSet<String>(rowCount);
        }
        // the PBGL already exists
        // we will get business objects so we can use an overloaded method that
        // will be easy to change in order to extract the key
        // the objects are of no further use, and will disappear when we clear the cache
    }
    
    private void setUpKeysNeedingRounding(Integer BaseYear)
    {
      Integer emplidCSFOvrdCount = new Integer(0);
      Integer emplidCSFCount     = new Integer(0);
      Criteria criteriaID = new Criteria();
      criteriaID.addEqualTo(PropertyConstants.CSF_DELETE_CODE,
                        BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
      criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                            BaseYear);
      String[] selectList = {"COUNT(DISTINCT "+PropertyConstants.EMPLID+")"};
      ReportQueryByCriteria queryID = 
        new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                  selectList,criteriaID);
//     EMPLID count for CSF Override
      Iterator emplidCSFOvrd =
           getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
      if (emplidCSFOvrd.hasNext())
      {
        emplidCSFOvrdCount = 
         ((BigDecimal)((Object[]) emplidCSFOvrd.next())[0]).intValue();
      }
//     EMPLID count for CSF
      queryID =
        new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class,
                                  selectList,criteriaID);
      Iterator emplidCSF =
           getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
      if (emplidCSF.hasNext())
      {
        emplidCSFCount = 
             ((BigDecimal)((Object[]) emplidCSF.next())[0]).intValue();
      }
      emplidCSFCount = emplidCSFCount+emplidCSFOvrdCount;
      if (emplidCSFCount > 0)
      {
        keysNeedingRounding = 
          new HashMap<String,roundMechanism>(emplidCSFCount);
      }
      else
      {
        keysNeedingRounding =
             new HashMap<String,roundMechanism>();
      }
      LOG.info(String.format("\nEmplid count %d",
                         emplidCSFCount));
//     now fill the hashmap
//     there will be one rounding bucket for each EMPLID
      String[] columnList = {PropertyConstants.EMPLID};
//     first use CSF Override
      queryID = 
        new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,
                                  columnList,criteriaID,true);
       Iterator emplidOvrd = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
      while (emplidOvrd.hasNext())
      {
        String newKey = (String) ((Object[]) emplidOvrd.next())[0];
                                 keysNeedingRounding.put(newKey, new roundMechanism());
      }
      LOG.info(String.format("\nEMPLID's from CSF override: %d",
                         keysNeedingRounding.size()));
//     now add the EMPLID's from CSF itself
      queryID =
      new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class,
                                columnList,criteriaID,true);    
      Iterator emplidIter = 
          getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
      while (emplidIter.hasNext())
      {
        String newKey = (String) ((Object[]) emplidIter.next())[0];
     // insert what is not already there from CSF override
        if (! keysNeedingRounding.containsKey(newKey))
        {
           keysNeedingRounding.put(newKey, new roundMechanism());
        }
      }
      LOG.info(String.format("\nEMPLID total for BCSF: %d",
               keysNeedingRounding.size()));
    }
    
// read the position table so we can attach normal work months to new bcaf rows
    private void setUpPositionNormalWorkMonths(Integer BaseYear)
    {
      Integer RequestYear = BaseYear+1;
      Criteria criteriaID = new Criteria();
      criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
      String[] selectList = {"COUNT(*)"};
      ReportQueryByCriteria queryID =
          new ReportQueryByCriteria(BudgetConstructionPosition.class,
                                    selectList,criteriaID);
      Iterator positionCountRow =
          getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
      while (positionCountRow.hasNext())
      {
        Integer positionCount = 
            ((BigDecimal)((Object[]) positionCountRow.next())[0]).intValue();
        positionNormalWorkMonths = new HashMap<String,Integer>(positionCount);
      }
      String[] fieldList = {PropertyConstants.POSITION_NUMBER,
                            PropertyConstants.IU_NORMAL_WORK_MONTHS};
      queryID = new ReportQueryByCriteria(BudgetConstructionPosition.class,
                                          fieldList,criteriaID);
      Iterator positionRows =
          getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
      while (positionRows.hasNext())
      {
          Object[] positionRow = (Object[]) positionRows.next();
          positionNormalWorkMonths.put((String) positionRow[0],
                                       (Integer) positionRow[1]);
      }
    }
        
//     decide whether the current appointment funding row, missing from BCSF, has
//     been entered by a user or is due to a CSF row that has since gone away
    String notOnLeave  = new String(BudgetConstructionConstants.NO_LEAVE_INDICATED);
    KualiDecimal rqstAmount = new KualiDecimal(0);
    BigDecimal pctTime = new BigDecimal(0);
    BigDecimal FTE     = new BigDecimal(0);
    Integer appointmentFundingMarkedDeleted = new Integer(0);
    private void 
    untouchedAppointmentFunding(PendingBudgetConstructionAppointmentFunding bcaf)
    {
//     this checks to see whether the missing row could have come in from CSF 
//     if they did not come in from CSF, then it follows that someone entered them
//     and we should not touch them  
      if ((! bcaf.getAppointmentRequestedAmount().equals(rqstAmount)) ||
        (! bcaf.getAppointmentFundingDurationCode().equals(notOnLeave)) ||
        (bcaf.isAppointmentFundingDeleteIndicator()))
      {    
        return;
      }
//
//     this should happen so rarely that we trade time for space, and do
//     an individual OBJ SQL call to see whether the missing row did in fact 
//     come in from CSF.  anecdotal evidence indicates there are about 25 or so
//     a day.  if this gets to be a major run-time problem, the fix would be to 
//     create another hashMap<String,BigDecimal[]), where the key would be 
//     the accounting key, position, and EMPLID (and if the line were vacant, 
//     another key differing only by the replacement of EMPLID by the VACANT_EMPLID
//     value). The BigDecimal[] would be csfTimePercent and 
//     csfFullTimeEmploymentQuantity.
//
      Criteria criteriaID = new Criteria();
      criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                            bcaf.getUniversityFiscalYear()-1);
      criteriaID.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                            bcaf.getChartOfAccountsCode());
      criteriaID.addEqualTo(PropertyConstants.ACCOUNT_NUMBER,
                            bcaf.getAccountNumber());
      criteriaID.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER,
                            bcaf.getSubAccountNumber());
      criteriaID.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_CODE,
                            bcaf.getFinancialObjectCode());
      criteriaID.addEqualTo(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                            bcaf.getFinancialSubObjectCode());
      criteriaID.addEqualTo(PropertyConstants.POSITION_NUMBER,
                            bcaf.getPositionNumber());
      criteriaID.addEqualTo(PropertyConstants.CSF_FULL_TIME_EMPLOYMENT_QUANTITY,
                            bcaf.getAppointmentRequestedFteQuantity());
      criteriaID.addEqualTo(PropertyConstants.CSF_TIME_PERCENT,
                            bcaf.getAppointmentRequestedTimePercent());
      Criteria vacantCriteria = new Criteria();
      Criteria flagCriteria   = new Criteria();
//     funding status is "vacant" or "unfunded"
      vacantCriteria.addEqualTo(PropertyConstants.CSF_FUNDING_STATUS_CODE,
                                BudgetConstructionConstants.VACANT_CSF_LINE);
      flagCriteria.addEqualTo(PropertyConstants.CSF_FUNDING_STATUS_CODE,
                              BudgetConstructionConstants.UNFUNDED_CSF_LINE);
      flagCriteria.addOrCriteria(vacantCriteria);
//     in addition, EMPLID is vacant
      vacantCriteria = new Criteria();
      vacantCriteria.addEqualTo(PropertyConstants.EMPLID,
                                BudgetConstructionConstants.VACANT_EMPLID);
      vacantCriteria.addAndCriteria(flagCriteria);
//     OR, the EMPLID in CSF is the same as in BCAF
      flagCriteria = new Criteria();
      flagCriteria.addEqualTo(PropertyConstants.EMPLID,
                              bcaf.getEmplid());
      flagCriteria.addOrCriteria(vacantCriteria);
//     now add the whole thing to the criteria list
      criteriaID.addAndCriteria(flagCriteria);
      String[] selectList = {"1"};
      ReportQueryByCriteria queryID = 
        new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class,
                                  selectList,criteriaID);
      Iterator resultSet = 
        getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
      if (! resultSet.hasNext())
      {
        // the line did not come from CSF, so it must have been added by a user
        // therefore, we should not mark it deleted  
        return;
      }
//     we need to mark this bcaf line deleted
      bcaf.setAppointmentRequestedFteQuantity(FTE);
      bcaf.setAppointmentRequestedTimePercent(pctTime);
      bcaf.setAppointmentFundingDeleteIndicator(true);
      getPersistenceBrokerTemplate().store(bcaf);
      appointmentFundingMarkedDeleted = appointmentFundingMarkedDeleted+1;
    }

//     this is an inner class which will store the data we need to perform the rounding,
//     and supply the methods as well         
    private class roundMechanism
    {
//     the idea here is that people split over many lines could lose or gain several
//     dollars if we rounded each salary line individually.  so, before we round the
//     salaries in the CSF lines, we add the differences to a running total which
//     will be added (subtracted) in dollar increments to successive lines until 
//     the running total is exhausted.
    private KualiDecimal diffAmount;
    private ArrayList<BudgetConstructionCalculatedSalaryFoundationTracker> candidateBCSFRows;
    private void roundMechanism()
    {
       this.diffAmount = new KualiDecimal(0);
       this.candidateBCSFRows = 
           new ArrayList<BudgetConstructionCalculatedSalaryFoundationTracker>(10);
    }
    private void addNewDiff(KualiDecimal withPennies,
                          KualiDecimal wholeDollars)
    {
       KualiDecimal roundingError = withPennies.subtract(wholeDollars);
       diffAmount.add(roundingError);
    }

    public void addNewBCSF(BudgetConstructionCalculatedSalaryFoundationTracker bCSF)
    {
       // save a pointer to the new BCSF object and round it
       candidateBCSFRows.add(bCSF);
       KualiDecimal withPenniesCSFAmount = bCSF.getCsfAmount();
       KualiDecimal wholeDollarsCSFAmount = bCSF.getCsfAmount().setScale(0);
       // save the difference
       addNewDiff(withPenniesCSFAmount,wholeDollarsCSFAmount);
       // round the amount to be stored
       bCSF.setCsfAmount(wholeDollarsCSFAmount);
    }

    public void fixRoundErrors()
    {
       // this routine adjusts the BCSF values so that the total for each
       // EMPLID round to the nearest whole dollar amount
       KualiDecimal adjustAmount;
       KualiDecimal negAdjust  = new KualiDecimal(-1);
       negAdjust.setScale(0);
       KualiDecimal posAdjust  = new KualiDecimal(1);
       posAdjust.setScale(0);
       // no rounding is necessary if the difference is less than a buck
       // this will also prevent our accessing an empty array list, or
       // trying to adjust things with only one CSF row
       if ((diffAmount.isGreaterThan(negAdjust))&&
           (diffAmount.isLessThan(posAdjust)))
       {
           return;
       }        
       if (diffAmount.isLessThan(KualiDecimal.ZERO))
       {
          adjustAmount = negAdjust; 
       }
       else
       {
          adjustAmount = posAdjust; 
       }
       for (BudgetConstructionCalculatedSalaryFoundationTracker rCSF: candidateBCSFRows)
       {
           KualiDecimal fixBCSFAmount = rCSF.getCsfAmount();
           fixBCSFAmount.add(adjustAmount);
           rCSF.setCsfAmount(fixBCSFAmount);
           diffAmount.subtract(adjustAmount);   
           if ((diffAmount.isGreaterThan(negAdjust))&&
                   (diffAmount.isLessThan(posAdjust)))
               {
                   break;
               }        
       }
    }
    }
    /**************************************************************************** 
     * (10) this is a unit test routine @@TODO: take this out                 
     ****************************************************************************/
    public void genesisUnitTest(Integer BaseYear)
    {
        /*  check the SQL for appointment funding (worked 03/29/2007
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear+1);
        criteriaID.addEqualTo(PropertyConstants.EMPLID,"0000001321");
        QueryByCriteria queryID = new QueryByCriteria(
                PendingBudgetConstructionAppointmentFunding.class, criteriaID);
        Iterator bcafResults =
        getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (bcafResults.hasNext())
        {
            PendingBudgetConstructionAppointmentFunding bcaf =
                (PendingBudgetConstructionAppointmentFunding) bcafResults.next();
            untouchedAppointmentFunding(bcaf);
        }
        */
    }

    //
    //  here are the routines Spring uses to "wire the beans"
    //
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