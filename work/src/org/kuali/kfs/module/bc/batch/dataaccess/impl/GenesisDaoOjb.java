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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.KFSConstants.ParameterValues;
import org.kuali.kfs.context.SpringContext;
import org.springframework.dao.DataAccessException;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.module.budget.bo.BudgetConstructionAccountReports;
import org.kuali.module.budget.bo.BudgetConstructionAdministrativePost;
import org.kuali.module.budget.bo.BudgetConstructionAppointmentFundingReason;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbent;
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
import org.kuali.module.chart.service.OrganizationService;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.bo.FunctionControlCode;
import org.kuali.module.gl.GLConstants.ColumnNames;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.integration.service.LaborModuleService;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
// October, 2007 additions
import edu.iu.uis.eden.actions.CompleteAction;
import edu.iu.uis.eden.actions.ActionTakenEvent;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.server.BeanConverter;


public class GenesisDaoOjb extends BudgetConstructionBatchHelperDaoOjb 
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
    
    /*  these constants should be in KFSPropertyConstants */
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
    private RouteHeaderService routeHeaderService = null;
    private LaborModuleService laborModuleService;
    
    
    public final Map<String,String> getBudgetConstructionControlFlags (Integer universityFiscalYear)
    {
        /*  return the flag names and the values for all the BC flags for the fiscal year */
        
        /*  the key to the map returned will be the name of the flag
         *  the entry will be the flag's value 
         */
        Map<String, String> controlFlags = new HashMap();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME,
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
        criteriaID.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME,
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
    

  /*
   * ********************************************************************************
   *   This routine returns the fiscal year as of the run date.
   *   Normally, genesis runs in the current fiscal year to build budget construction
   *   for the coming fiscal year.
   */   
    public Integer fiscalYearFromToday()
    {
        //  we look up the fiscal year for today's date, and return it
        //  we return 0 if nothing is found
        Integer currentFiscalYear = new Integer(0);
        Date lookUpDate =
            dateTimeService.getCurrentSqlDateMidnight();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_DATE,
                              lookUpDate);
        String[] attrb = {KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR};
        ReportQueryByCriteria queryID =
            new ReportQueryByCriteria(UniversityDate.class, attrb, criteriaID);
        Iterator resultRow =
        getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        // there will only be one row.
        // Oracle will not close the cursor, however, until the iterator has been exhausted
        if (resultRow.hasNext())
        {
            currentFiscalYear = (Integer) ((Number)
                        ((Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(resultRow))[0]).intValue();
        }
        //TODO:
        LOG.debug(String.format("\nreturned from fiscalYearFromToday: %d",
                currentFiscalYear));
        //TODO:
        return currentFiscalYear;
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(FiscalYearFunctionControl.class,
                                      criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        getPersistenceBrokerTemplate().clearCache();
       // 
       //  the default values (except for the BUDGET_CONSTRUCTION_GENESIS_RUNNING flag)
       //  come from the function control code table
       FiscalYearFunctionControl SLF;
       criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
       String[] attrQ = {KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE,
                         KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_DEFAULT_INDICATOR};
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
//                       ((flagDefault == KFSConstants.ParameterValues.YES)? true : false));
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,Year);
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
     *                                                                 *
     *  NOTE (IMPORTANT):                                              *
     *  In order to do a bulk delete, we MUST use deleteByQuery.  The  *
     *  only alternative is to fetch each existing row and delete it,  *
     *  one at a time.  This is unrealistic in a batch process dealing *
     *  with many rows. deleteByQuery does NOT "synchronize" the cache:*
     *  the deleted rows remain in the cache and will be fetched on a  *
     *  subsequent database call if they fit the criteria.  The OJB    *
     *  documentation explicitly states this.  So, after a             *
     *  deleteByQuery we need to call clearCache.  This will not remove*
     *  instantiated objects but will remove the database rows that    *
     *  were used to build them.  One consequence is that every store  *
     *  of an object that was instantiated before the cache was cleared*
     *  will generate a select on the first key field (to test whether *
     *  the object exists) and then an INSERT if it does not or an     *
     *  UPDATE if it does.  This same process happens if on changes the*
     *  key (for example, the Fiscal Year) of an instantiated object.  *  
     *  ****************************************************************
     */
    public void clearDBForGenesis(Integer BaseYear)
    {
        clearBudgetConstructionAdministrativePost();
        clearBudgetConstructionIntendedIncumbent();
        //  the order is important because of referential integrity in the database
        clearBothYearsBCSF(BaseYear);
        clearBothYearsBudgetConstructionAppointmentFundingReason(BaseYear);
        clearBothYearsPendingApptFunding(BaseYear);
        clearBothYearsBCPosition(BaseYear);
        //  the calling order is important because of referential integrity in the 
        //  database
        clearBothYearsPBGL(BaseYear);
        clearBothYearsHeaders(BaseYear);
    }
    
    private void clearBudgetConstructionAdministrativePost()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionAdministrativePost.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    private void clearBaseYearBudgetConstructionAppointmentFundingReason(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionAppointmentFundingReason.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    private void clearBothYearsBudgetConstructionAppointmentFundingReason(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionAppointmentFundingReason.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearBudgetConstructionAppointmentFundingReason()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionAppointmentFundingReason.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearRequestYearBudgetConstructionAppointmentFundingReason(Integer BaseYear)
    {
        Integer RequestYear = BaseYear +1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionAppointmentFundingReason.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearBaseYearBCSF(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    private void clearBothYearsBCSF(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearBCSF()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearBudgetConstructionIntendedIncumbent()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionIntendedIncumbent.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    private void clearBaseYearBCPosition(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionPosition.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    private void clearBothYearsBCPosition(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionPosition.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearBCPosition()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionPosition.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearRequestYearBCPosition(Integer BaseYear)
    {
        Integer RequestYear = BaseYear +1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionPosition.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearRequestYearBCSF(Integer BaseYear)
    {
        Integer RequestYear = BaseYear +1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearBaseYearHeaders(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearBothYearsHeaders(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearHeaders()
    {
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class,
                                                     QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    private void clearBaseYearPBGL(Integer BaseYear)
    {
        // the order here is mandated by referential integrity
        // remove rows from the base year from budget construction months
        Criteria mnCriteriaID = new Criteria();
        mnCriteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria mnQueryID =
            new QueryByCriteria(BudgetConstructionMonthly.class,mnCriteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(mnQueryID);
        // remove rows from the basse year from budget construction general ledger
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryID = 
            new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
                    criteriaID);
        LOG.debug(String.format("delete PBGL started at %tT for %d",dateTimeService.getCurrentDate(),
                BaseYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.debug(String.format("delete PBGL ended at %tT",dateTimeService.getCurrentDate()));
        getPersistenceBrokerTemplate().clearCache();
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
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearRequestYearPBGL(Integer BaseYear)
    {
        Integer RequestYear = BaseYear + 1;
        // the order here is mandated by referential integrity
        // remove rows from the request year from budget construction months
        Criteria mnCriteriaID = new Criteria();
        mnCriteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria mnQueryID =
            new QueryByCriteria(BudgetConstructionMonthly.class,mnCriteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(mnQueryID);
        // remove rows from the request year
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryID = 
            new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
                    criteriaID);
        LOG.debug(String.format("\ndelete PBGL started at %tT for %d",dateTimeService.getCurrentDate(),
                RequestYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.debug(String.format("\ndelete PBGL ended at %tT",dateTimeService.getCurrentDate()));
        getPersistenceBrokerTemplate().clearCache();
    }

    private void clearBaseYearPendingApptFunding(Integer BaseYear)
    {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    private void clearBothYearsPendingApptFunding(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear,RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                                                      criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearPendingApptFunding()
    {
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                                QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void clearRequestYearPendingApptFunding(Integer BaseYear)
    {
        Integer RequestYear = BaseYear +1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        QueryByCriteria queryId = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                    criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
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
    
    private HashSet<String> currentBCHeaderKeys = new HashSet<String>(1);
    // these routines are used to merge CSF and CSF Override
    private HashMap<String,String[]> CSFTrackerKeys =
            new HashMap<String,String[]>(1);
    private void createNewDocumentsCleanUp()
    {
        currentBCHeaderKeys.clear();
        CSFTrackerKeys.clear();
    }
            
    // counters
    Long documentsToCreateinNTS    = new Long(0);
    Long documentsSkippedinNTS     = new Long(0);
    Long documentsCreatedinNTS     = new Long(0);
    Long documentsCSFCreatedinNTS  = new Long(0);
    Long documentsGLCreatedinNTS   = new Long(0);
    
    Long proxyCandidatesReadinTS   = new Long(0);
    Long proxyBCHeadersCreatedinTS = new Long(0);
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
        // get the workflow document service
        setUpRouteHeaderService();
        // take the count of header keys from the GL
        setUpCurrentBCHeaderKeys(BaseYear);
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
          setUpCSFHashStructures(BaseYear);  
          getCSFCandidateDocumentKeys(BaseYear);
          getCSFOverrideDeletedKeys(BaseYear);
          getCSFOverrideCandidateDocumentKeys(BaseYear);
          getAndStoreCurrentCSFBCHeaderCandidates(BaseYear);
        }
        createNewDocumentsCleanUp();
    }
 /*  @@TODO:  this is prior to October, 2007, when calling complete stopped running in finite time   
    private void finalizeBCDocument(BudgetConstructionDocument bcDoc) throws WorkflowException
    {
        // this routine reads the actual workflow tables to set the status dates and the final status code
        // going through the routing procedure took far too much time per document
        DocumentRouteHeaderValue routeHeader =
            routeHeaderService.getRouteHeader(bcDoc.getDocumentHeader().getWorkflowDocument().getRouteHeaderId());
        routeHeader.markDocumentEnroute();
        routeHeader.markDocumentApproved();
        routeHeader.markDocumentProcessed();
        routeHeader.markDocumentFinalized();
        routeHeaderService.saveRouteHeader(routeHeader);
    }
*/    

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
                wex.printStackTrace();
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
                wex.printStackTrace();
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
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE,
                              KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        String newAttr = ColumnNames.BEGINNING_BALANCE+"-"+
                         ColumnNames.ANNUAL_BALANCE;
        criteriaId.addNotEqualTo(newAttr,0);
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ACCOUNT_NUMBER,
                              KFSPropertyConstants.SUB_ACCOUNT_NUMBER};
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
                wex.printStackTrace();
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
                wex.printStackTrace();
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
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ACCOUNT_NUMBER,
                              KFSPropertyConstants.SUB_ACCOUNT_NUMBER};
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
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ACCOUNT_NUMBER,
                              KFSPropertyConstants.SUB_ACCOUNT_NUMBER};
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
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addNotEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ACCOUNT_NUMBER,
                              KFSPropertyConstants.SUB_ACCOUNT_NUMBER};
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
    
    private void getCurrentBCHeaderKeys(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaId = new Criteria();
        Iterator<Object[]> Results;
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              RequestYear);
        String[] selectList = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER};
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(BudgetConstructionHeader.class, selectList, criteriaId);
        Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
       
        while (Results.hasNext())
        {
            Object[] returnedRow = Results.next();
            currentBCHeaderKeys.add(((String) returnedRow[0])+((String) returnedRow[1])+((String) returnedRow[2]));
        }
    }
    
    //@@TODO:  this method was added in October, 2007 in an attempt to speed things up
    private void saveBCDocumentInWorkflow(BudgetConstructionDocument bcDoc) throws WorkflowException
    {
       // first, fetch the DB version of the workflow document saved when Kuali checked for the correct document type 
        DocumentRouteHeaderValue ourWorkflowDoc = 
         routeHeaderService.getRouteHeader(bcDoc.getDocumentHeader().getWorkflowDocument().getRouteHeaderId());
       // next, call the complete method in the document service
       // WorkflowDocumentService interface has a complete method, but its Kuali counterpart does not
       // so, we mimic the workflow call here.  complete should be added to Kuali 
       CompleteAction action = new CompleteAction(ourWorkflowDoc, ourWorkflowDoc.getInitiatorUser(), "created by Genesis");
       action.recordAction();
       // there was no need to queue.  we want to mark the document final and save it 
       ourWorkflowDoc.markDocumentEnroute();
       ourWorkflowDoc.markDocumentApproved();
       ourWorkflowDoc.markDocumentProcessed();
       ourWorkflowDoc.markDocumentFinalized();
       routeHeaderService.saveRouteHeader(ourWorkflowDoc);
    }

    
    public void setUpCSFHashStructures(Integer BaseYear)
    {
       // these are the potential document keys in the CSF tracker
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaId.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        String[] propertyString = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                   KFSPropertyConstants.ACCOUNT_NUMBER,
                                   KFSPropertyConstants.SUB_ACCOUNT_NUMBER};
        CSFTrackerKeys = 
            new HashMap<String,String[]>(hashObjectSize(CalculatedSalaryFoundationTracker.class,
                                         criteriaId,propertyString));
    }

    public void setUpCurrentBCHeaderKeys(Integer BaseYear)
    {
        // the BC header keys should be roughly the same as the GL balance BB keys
        // if any new keys are introduced from CSF, it means that there is money
        // in the payroll that has NOT been budgeted.  this should be a rare 
        // occurrence.  
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE,
                              KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        String[] propertyString = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                   KFSPropertyConstants.ACCOUNT_NUMBER,
                                   KFSPropertyConstants.SUB_ACCOUNT_NUMBER};
        currentBCHeaderKeys = 
            new HashSet<String>(hashObjectSize(Balance.class,criteriaID,
                                propertyString));
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
                KFSConstants.INITIAL_KUALI_DOCUMENT_STATUS_CD);
        kualiDocumentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        kualiDocumentHeader.setFinancialDocumentDescription(String.format("%s %d %s %s",
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION,
                       newBCHdr.getUniversityFiscalYear(),
                       newBCHdr.getChartOfAccountsCode(),newBCHdr.getAccountNumber()));
        kualiDocumentHeader.setExplanation(
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION);
        getPersistenceBrokerTemplate().store(newBCHdr);
        documentService.prepareWorkflowDocument(newBCHdr);
        // this is more efficient than route and does what we want
        // it calls a document method, not a service
        // newBCHdr.getDocumentHeader().getWorkflowDocument().complete("created by Genesis");
        // October, 2007:  refactoring has made BC document creation unacceptably slow
        // we are going to attempt to streamline the process by by-passing the workflowDocumentActionWebService
        // (which is called by complete above) and simply saving our document and its action log message directly
        // using workflowDocumentService routines
        saveBCDocumentInWorkflow(newBCHdr);
        //@@TODO: end October 2007 additions
        //@@TODO: October 2007 deletion
        // finalizeBCDocument(newBCHdr);
//        workflowDocumentService.route(newBCHdr.getDocumentHeader().getWorkflowDocument(),
//                                      "created by Genesis",null);
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
      // this has moved temporarily until the data model is fixed (8/8/2007)  
        buildNewOrganizationReportsTo();
      // build the account table
        buildNewAccountReportsTo();
      // build the organization table  
      // buildNewOrganizationReportsTo();
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
        criteriaID.addNotEqualTo(KFSPropertyConstants.ACCOUNT_CLOSED_INDICATOR,
                              KFSConstants.ParameterValues.YES);
         */
        criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ACCOUNT_NUMBER,
                              KFSPropertyConstants.ORGANIZATION_CODE};
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
        criteriaID.addEqualTo(KFSPropertyConstants.ORGANIZATION_ACTIVE_INDICATOR,
                              KFSConstants.ParameterValues.YES);
         */
        criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ORGANIZATION_CODE,
                              KFSPropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.REPORTS_TO_ORGANIZATION_CODE,
                              KFSPropertyConstants.RESPONSIBILITY_CENTER_CODE};
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
            new HashMap<String,BudgetConstructionAccountReports>(1);
    private HashMap<String,BudgetConstructionOrganizationReports> orgRptsToMap =
            new HashMap<String,BudgetConstructionOrganizationReports>(1);
    private HashMap<String,BudgetConstructionAccountOrganizationHierarchy> acctOrgHierMap =
            new HashMap<String,BudgetConstructionAccountOrganizationHierarchy>(1);
    private void organizationHierarchyCleanUp()
    {
        acctRptsToMap.clear();
        orgRptsToMap.clear();
        acctOrgHierMap.clear();
    }
    
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        QueryByCriteria killOrgHierQuery = 
            new QueryByCriteria(BudgetConstructionAccountOrganizationHierarchy.class,
                                criteriaID);
        killOrgHierQuery.setCriteria(criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(killOrgHierQuery);
        getPersistenceBrokerTemplate().clearCache();
        //
        // now we fetch the root of the organization tree
        String[] rootNode = 
            SpringContext.getBean(OrganizationService.class).getRootOrganizationCode();
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        acctOrgHierMap = 
            new HashMap<String,BudgetConstructionAccountOrganizationHierarchy>(
                    hashObjectSize(BudgetConstructionAccountOrganizationHierarchy.class,
                          criteriaID)*
                          BudgetConstructionConstants.AVERAGE_REPORTING_TREE_SIZE);
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
        organizationHierarchyCleanUp();
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
        // we always get a new copy of the map
        acctRptsToMap = 
            new HashMap<String,BudgetConstructionAccountReports>(
                    hashObjectSize(BudgetConstructionAccountReports.class,criteriaID));
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ACCOUNT_NUMBER,
                              KFSPropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.REPORTS_TO_ORGANIZATION_CODE};
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
       // build a new map
       orgRptsToMap = new HashMap<String,BudgetConstructionOrganizationReports>(
                      hashObjectSize(BudgetConstructionOrganizationReports.class,
                      criteriaID));
       String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                             KFSPropertyConstants.ORGANIZATION_CODE,
                             KFSPropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE,
                             KFSPropertyConstants.REPORTS_TO_ORGANIZATION_CODE};
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
            new HashMap<String,PendingBudgetConstructionGeneralLedger>(1);
    private HashMap<String,String> documentNumberFromBCHdr =
            new HashMap<String,String>(1);
    private HashMap<String,Integer> skippedPBGLKeys = new HashMap(); 
    private void pBGLCleanUp()
    {
        pBGLFromGL.clear();
        documentNumberFromBCHdr.clear();
    }
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                RequestYear);
        Criteria lockID = new Criteria();
        Criteria tranLockID = new Criteria();
        //@@TODO:  add these to the KFSPropertyConstants or at least to 
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
        // clearBothYearsPBGL(BaseYear);
        // we have to clean out account reports to
        // it is not fiscal year-specific
        // this implies that last year's data can't be there, because the
        // organization hierarchy will have changed
        readBCHeaderForDocNumber(BaseYear);
        readGLForPBGL(BaseYear);
        addNewGLRowsToPBGL(BaseYear);
        writeFinalDiagnosticCounts();
        pBGLCleanUp();
    }
    
    public void updateToPBGL(Integer BaseYear)
    {
        readBCHeaderForDocNumber(BaseYear);
        readGLForPBGL(BaseYear);
        updateCurrentPBGL(BaseYear);
        addNewGLRowsToPBGL(BaseYear);
        writeFinalDiagnosticCounts();
        pBGLCleanUp();
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
       KualiInteger DollarBaseAmount = 
           new KualiInteger(BaseAmount.bigDecimalValue());
       PBGLObj.setFinancialBeginningBalanceLineAmount(DollarBaseAmount);
       PBGLObj.setAccountLineAnnualBalanceAmount(KualiInteger.ZERO);
       //  ObjectID is set in the BusinessObjectBase on insert and update
       //  but, we must set the version number
       PBGLObj.setVersionNumber(DEFAULT_VERSION_NUMBER);
       return PBGLObj;
    }
    
    private void readBCHeaderForDocNumber(Integer BaseYear)
    {
        //  we have to read all the budget construction header objects so that
        //  we can use them to assign document numbers
        //
        Integer RequestYear = BaseYear + 1;
        //
        Long documentsRead = new Long(0);
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        documentNumberFromBCHdr =
            new HashMap<String,String>(hashObjectSize(
                    BudgetConstructionHeader.class,criteriaId));
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ACCOUNT_NUMBER,
                              KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                              KFSPropertyConstants.DOCUMENT_NUMBER};
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE,
                              KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        //  we'll estimate the size of the PBGL map from the number of
        //  base budget rows in the GL.  this should be close
        pBGLFromGL =
            new HashMap<String,PendingBudgetConstructionGeneralLedger>(
                    hashObjectSize(Balance.class,criteriaID));
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                              KFSPropertyConstants.ACCOUNT_NUMBER,
                              KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                              KFSPropertyConstants.OBJECT_CODE,
                              KFSPropertyConstants.SUB_OBJECT_CODE,
                              KFSPropertyConstants.BALANCE_TYPE_CODE,
                              KFSPropertyConstants.OBJECT_TYPE_CODE,
                              KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT,
                              KFSPropertyConstants.BEGINNING_BALANCE_LINE_AMOUNT};
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
       KualiInteger baseFromCurrentGL = 
           matchFromGL.getFinancialBeginningBalanceLineAmount();
       KualiInteger baseFromPBGL = 
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
       criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
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
    
    private HashMap<String,String[]> baseYearInactiveObjects =
            new HashMap<String,String[]>(1);
    private HashMap<String,String[]> gLBBObjects =
            new HashMap<String,String[]>(1);
    private Integer nInactiveBBObjectCodes = new Integer(0);
    private void objectClassRICleanUp()
    {
        baseYearInactiveObjects.clear();
        gLBBObjects.clear();
    }
    
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
        objectClassRICleanUp();
    }
    
    private void addRIObjectClassesForBB(Integer BaseYear)
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
          criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
          criteriaID.addColumnEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                      problemChart);
          criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE,problemObject);
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
          ObjectCode baseYearObject = (ObjectCode) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(Results);
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        criteriaID.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,Chart);
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE,ObjectCode);
        QueryByCriteria queryID = 
            new QueryByCriteria(ObjectCode.class,criteriaID);
        Integer Result = 
            getPersistenceBrokerTemplate().getCount(queryID);
        return (!Result.equals(0));
    }
    
    private void readBaseYearInactiveObjects(Integer BaseYear)
    {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_ACTIVE_CODE,false);
        baseYearInactiveObjects = 
            new HashMap<String,String[]>(hashObjectSize(ObjectCode.class,criteriaID));
        String[] queryAttr  = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                               KFSPropertyConstants.FINANCIAL_OBJECT_CODE};
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
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE,
                              KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        gLBBObjects = 
            new HashMap<String,String[]>(hashObjectSize(Balance.class,criteriaID));
        String[] queryAttr = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,           
                              KFSPropertyConstants.OBJECT_CODE};
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
    
    /****************************************************************************************
    * (9)  this code builds the budget construction CSF tracker and the budget construction
    *      appointment funding
    ****************************************************************************************
    */
             
    // the set of new BCSF objects to be written
    private HashMap<String,BudgetConstructionCalculatedSalaryFoundationTracker> bCSF =
            new HashMap<String,BudgetConstructionCalculatedSalaryFoundationTracker>(1);
    // hashmap to hold the document numbers for each accounting key in the header
    private HashMap<String,String> bcHdrDocNumbers =
            new HashMap<String,String>(1);
    // hashset to hold the accounting string for each pending GL entry
    private HashSet<String> currentPBGLKeys = new HashSet<String>(1);
    // hashMap for finding the object type of "detailed position" object codes
    private HashMap<String,String> detailedPositionObjectTypes =
            new HashMap<String,String>(1);
    // keys for deleted or vacant rows present in the override CSF: none of these keys
    // will load to BCSF from either the override or actual CSF (even if they
    // are active in the actual CSF) 
    private HashSet<String> csfOverrideKeys = new HashSet<String>(1);;
    // EMPLID's in CSF which have more than one active row
    // we budget in whole dollars, while payroll deals in pennies
    // we will use this for our complicated rounding algorithm, to prevent
    // to keep the total budget base salary within a dollar of the payroll salary
    private HashMap<String,roundMechanism> keysNeedingRounding =
            new HashMap<String,roundMechanism>(1);
    // we need the position normal work months to write a new appointment funding row
    private HashMap<String,Integer> positionNormalWorkMonths =
            new HashMap<String,Integer>(1);
    private void buildAppointmentFundingCleanUp()
    {
        bCSF.clear();
        bcHdrDocNumbers.clear();
        currentPBGLKeys.clear();
        detailedPositionObjectTypes.clear();
        csfOverrideKeys.clear();
        keysNeedingRounding.clear();
        positionNormalWorkMonths.clear();
    }
    //
    // counters
    //
    Integer CSFRowsRead              = new Integer(0);
    Integer CSFRowsVacant            = new Integer(0);
    Integer CSFVacantsConsolidated   = new Integer(0);
    Integer CSFOverrideDeletesRead   = new Integer(0);
    Integer CSFOverrideRead          = new Integer(0);
    Integer CSFOverrideVacant        = new Integer(0);
    Integer CSFForBCSF               = new Integer(0);
    Integer CSFCurrentGLRows         = new Integer(0);
    Integer CSFCurrentBCAFRows       = new Integer(0);
    Integer CSFBCSFRowsMatchingGL    = new Integer(0);
    Integer CSFBCSFRowsMatchingBCAF  = new Integer(0);
    Integer CSFNewGLRows             = new Integer(0);
    Integer CSFNewBCAFRows           = new Integer(0);
    Integer CSFBCAFRowsMarkedDeleted = new Integer(0);
    Integer CSFBCAFRowsMissing       = new Integer(0);
    Integer CSFBadObjectsSkipped     = new Integer(0);
             
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
       clearBCCSF(BaseYear+1);
       // build the new BC CSF objects in memory
       setUpCSFOverrideKeys(BaseYear); 
       setUpBCSFMap(BaseYear);
       setUpKeysNeedingRounding(BaseYear);
       readCSFOverride(BaseYear);
       readCSF(BaseYear);
       CSFForBCSF = bCSF.size(); 
       adjustCSFRounding();
       // at this point we should clear the cache
       // any objects fetched from the database (CSF and CSF Override) will not
       // be written back, and we may need the memory.
       //getPersistenceBrokerTemplate().clearCache();
       //  store bCSF rows matching current appointment funding
       readExistingAppointmentFunding(BaseYear);
       //  if all of the bCSF rows have been stored (they all already eixst in
       //  PBGL), we can quit here
       if (bCSF.size() == 0)
       {
           buildAppointmentFundingCleanUp();
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
       //getPersistenceBrokerTemplate().clearCache();
       readAndWriteBCSFAndNewAppointmentFundingAndNewPBGL(BaseYear);
       CSFDiagnostics();
       buildAppointmentFundingCleanUp();
    }
    
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
      // KualiDecimal roundedAmount = csf.getCsfAmount().setScale(0);
      // this is after the change to KualiInteger from KualiDecimal in the BCSF table
      // (June 1, 2007)
      KualiInteger roundedAmount = 
            new KualiInteger(csf.getCsfAmount(),
                             RoundingMode.valueOf(KualiInteger.ROUND_BEHAVIOR));
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
      CSFVacantsConsolidated = CSFVacantsConsolidated+1;
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
      // KualiDecimal roundedAmount = csf.getCsfAmount().setScale(0);
      // this is after the change to KualiInteger from KualiDecimal in the BCSF table
      // (June 1, 2007)
      KualiInteger roundedAmount = 
            new KualiInteger(csf.getCsfAmount(),
                             RoundingMode.valueOf(KualiInteger.ROUND_BEHAVIOR));
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
      CSFVacantsConsolidated = CSFVacantsConsolidated+1;
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
                       BCConstants.VACANT_EMPLID:
                       csf.getEmplid()));
       csfBC.setCsfFullTimeEmploymentQuantity(csf.getCsfFullTimeEmploymentQuantity());
       csfBC.setCsfTimePercent(csf.getCsfTimePercent());
       csfBC.setCsfFundingStatusCode(csf.getCsfFundingStatusCode());
       // we only worry about rounding errors when the line is not vacant
       // since all vacant lines in CSF have the same (vacant) EMPLID, we
       // would have to round by position. 
       if (!vacantLine)
       {
          // changed BO type from KualiDecimal to KualiInteger (June 1, 2007) 
          // csfBC.setCsfAmount(csf.getCsfAmount());
           bCSF.put(csfKey,csfBC);
           // now we have to round and save the rounding error
           roundMechanism rX = keysNeedingRounding.get(csf.getEmplid());
           rX.addNewBCSF(csfBC,csf.getCsfAmount());
       }
       else
       {
           // for vacant lines, we have to round to whole dollars
           csfBC.setCsfAmount(new KualiInteger(csf.getCsfAmount(),
                                  RoundingMode.valueOf(KualiInteger.ROUND_BEHAVIOR)));
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
                       BCConstants.VACANT_EMPLID:
                       csf.getEmplid()));
       csfBC.setCsfFullTimeEmploymentQuantity(csf.getCsfFullTimeEmploymentQuantity());
       csfBC.setCsfTimePercent(csf.getCsfTimePercent());
       csfBC.setCsfFundingStatusCode(csf.getCsfFundingStatusCode());
       // we only worry about rounding errors when the line is not vacant
       // since all vacant lines in CSF have the same (vacant) EMPLID, we
       // would have to round by position, and positions can be shared. 
       if (!vacantLine)
       {
           bCSF.put(csfKey,csfBC);
           // now we have to round and save the rounding error
           roundMechanism rX = keysNeedingRounding.get(csf.getEmplid());
           rX.addNewBCSF(csfBC,csf.getCsfAmount());
       }
       else
       {
           // for vacant lines, we have to round to whole dollars
           csfBC.setCsfAmount(new KualiInteger(csf.getCsfAmount(),
                   RoundingMode.valueOf(KualiInteger.ROUND_BEHAVIOR)));
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
        KualiInteger defaultAmount = KualiInteger.ZERO;
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
    
    private String buildDocKeyFromBCSF(
                   BudgetConstructionCalculatedSalaryFoundationTracker bcsf)
    {
        // see setUpbcHdrDocNumbers for the correct key elements
        // the order here must match the order there
        return bcsf.getChartOfAccountsCode()+
               bcsf.getAccountNumber()+
               bcsf.getSubAccountNumber();
    }
    
    private boolean buildPBGLFromBCSFAndStore(
            BudgetConstructionCalculatedSalaryFoundationTracker bcsf)
    {
        // first we need to see if a new PBGL row is needed
        String testKey = buildPBGLKey(bcsf);
        if (currentPBGLKeys.contains(testKey))
        {
            return true;
        }
        // Budget construction cannot show detailed salary lines unless the object code
        // support "detailed positions".   But, the CSF is a plug-in, so Kuali cannot
        // assume that the CSF enforces this rule.  Here, we test whether the object
        // class is valid.  if it is not, we write a message and skip the row
        // we do this before the GL check, so we can be sure we write all the rows
        // that have problems
        String objectType =
            detailedPositionObjectTypes.get(
                    bcsf.getChartOfAccountsCode()+bcsf.getFinancialObjectCode());
        if (objectType == null)
        {
          LOG.warn(String.format("\nthis row has an object class which does not support"+
                                 " detailed positions (skipped):\n"+
                                 "position: %s, EMPLID: %s, accounting string ="+
                                 "(%s,%s,%s,%s,%s",
                                 bcsf.getPositionNumber(),
                                 bcsf.getEmplid(),
                                 bcsf.getChartOfAccountsCode(),
                                 bcsf.getAccountNumber(),
                                 bcsf.getSubAccountNumber(),
                                 bcsf.getFinancialObjectCode(),
                                 bcsf.getFinancialSubObjectCode()));
          CSFBadObjectsSkipped = CSFBadObjectsSkipped+1;
          return false;
        }
        // we need a new row
        // store the key so we won't try to add another row from a different
        // person's bcsf which has the same key
        currentPBGLKeys.add(testKey);
        String docKey = buildDocKeyFromBCSF(bcsf);
        // we never have to build a new document header
        // createNewBCDocumentsFromGLCSF is always called earlier in the step
        // containing this routine
        // fill in the fields
        PendingBudgetConstructionGeneralLedger pbGL = 
            new PendingBudgetConstructionGeneralLedger();
        pbGL.setDocumentNumber(bcHdrDocNumbers.get(docKey));
        pbGL.setUniversityFiscalYear(bcsf.getUniversityFiscalYear());
        pbGL.setChartOfAccountsCode(bcsf.getChartOfAccountsCode());
        pbGL.setAccountNumber(bcsf.getAccountNumber());
        pbGL.setSubAccountNumber(bcsf.getSubAccountNumber());
        pbGL.setFinancialObjectCode(bcsf.getFinancialObjectCode());
        pbGL.setFinancialSubObjectCode(bcsf.getFinancialSubObjectCode());
        pbGL.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        pbGL.setFinancialObjectTypeCode(objectType);
        pbGL.setAccountLineAnnualBalanceAmount(KualiInteger.ZERO);
        pbGL.setFinancialBeginningBalanceLineAmount(KualiInteger.ZERO);
        // store the new PBGL row
        getPersistenceBrokerTemplate().store(pbGL);
        CSFNewGLRows = CSFNewGLRows+1;
        return true;
    }
    
    // these two rows are overloaded so we have a standardized key
    private String buildPBGLKey(BudgetConstructionCalculatedSalaryFoundationTracker bcsf)
    {
       return bcsf.getAccountNumber()+
              bcsf.getFinancialObjectCode()+
              bcsf.getChartOfAccountsCode()+
              bcsf.getSubAccountNumber()+
              bcsf.getFinancialSubObjectCode();
    }
    
    private String buildPBGLKey(PendingBudgetConstructionGeneralLedger pbgl)
    {
       return pbgl.getAccountNumber()+
              pbgl.getFinancialObjectCode()+
              pbgl.getChartOfAccountsCode()+
              pbgl.getSubAccountNumber()+
              pbgl.getFinancialSubObjectCode();
    }
    
    private String buildVacantCSFKey(CalculatedSalaryFoundationTrackerOverride csf)
    {
        boolean vacantLine = isVacantLine(csf);
        return (vacantLine?BCConstants.VACANT_EMPLID:
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
        return (vacantLine?BCConstants.VACANT_EMPLID:
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
    private void clearBCCSF(Integer FiscalYear)
    {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              FiscalYear);
        QueryByCriteria queryID = 
            new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class,
                                criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        // as always, we should clear the cache after a delete,
        // even though in this case we haven't yet fetched much
        getPersistenceBrokerTemplate().clearCache();
    }
    
    private void CSFDiagnostics()
    {
        LOG.info(String.format("\n\nResults of building BC CSF"));
        LOG.info(String.format("\nCSF override rows =      %d",CSFOverrideRead));
        LOG.info(String.format("\nCSF rows read            %d",CSFRowsRead));
        LOG.info(String.format("\nCSF override deletes     %d",
                 CSFOverrideDeletesRead));
        LOG.info(String.format("\n\nCSF overrides vacant    %d",CSFOverrideVacant));
        LOG.info(String.format("\nCSF vacant               %d",CSFRowsVacant));
        LOG.info(String.format("\nCSF vacants consolidated %d",
                 CSFVacantsConsolidated));
        LOG.info(String.format("\n\nBudgetConstruction CSF rows %d",
                 CSFForBCSF));
        LOG.info(String.format("\n\nCurrent PBGL rows           %d",
                 CSFCurrentGLRows));
        LOG.info(String.format("\nCurrent appt funding rows     %d",
                 CSFCurrentBCAFRows));
        LOG.info(String.format("\n\nAppt funding rows not in BCSF   %d",
                 CSFBCAFRowsMissing));
        LOG.info(String.format("\nAppt funding rows marked deleted %d",
                 CSFBCAFRowsMarkedDeleted));
    }
    
    private ArrayList<String> findPositionRequiredObjectCodes (Integer BaseYear)
    {
        // we want to build an SQL IN criteria to filter a return set
        // we will find distinct objects only, regardless of chart
        // this will not be a concern--it will make the return set bigger,but
        // include every case we want
        // the result will be used to build a list to check for missing PBGL rows,
        // so having more PBGL rows than we need will not cause us to miss any
        Integer RequestYear = BaseYear+1;
        ArrayList<String> objectCodesWithIndividualPositions = 
                          new ArrayList<String>(10);
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        criteriaID.addEqualTo(KFSPropertyConstants.DETAIL_POSITION_REQUIRED_INDICATOR,
                                  true);
        String[] selectList = {KFSPropertyConstants.FINANCIAL_OBJECT_CODE};
        Class laborObjectClass = laborModuleService.getLaborLedgerObjectClass();
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(laborObjectClass,selectList,criteriaID,true);
        Iterator objectCodesReturned =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (objectCodesReturned.hasNext())
        {
          objectCodesWithIndividualPositions.add((String)((Object[]) objectCodesReturned.next())[0]);
        }
        return objectCodesWithIndividualPositions;
    }
    
//  we will overload both of these checks as well             
    private boolean isVacantLine(CalculatedSalaryFoundationTracker csf)
    {
       return  ((csf.getCsfFundingStatusCode().equals(
                 BCConstants.csfFundingStatusFlag.VACANT.getFlagValue()))||
                (csf.getCsfFundingStatusCode().equals(
                 BCConstants.csfFundingStatusFlag.UNFUNDED.getFlagValue())));
    }
    
    private boolean isVacantLine(CalculatedSalaryFoundationTrackerOverride csf)
    {
       return  ((csf.getCsfFundingStatusCode().equals(
               BCConstants.csfFundingStatusFlag.VACANT.getFlagValue()))||
                (csf.getCsfFundingStatusCode().equals(
                        BCConstants.csfFundingStatusFlag.UNFUNDED.getFlagValue())));
    }

    // here are the routines to build BCSF
    
    private void readAndWriteBCSFAndNewAppointmentFundingAndNewPBGL(Integer BaseYear)
    {
      // read through the remaining BCSF objects (those that do not presently exist
      // in appointment funding (BCAF)
      // we will check whether they exist in Pending GL (PBGL), and, if not, write
      // a new GL line.  Then, we will write a PBGL row and a BCSF row (in that order,
      // because of referential integrity.  The PBGL and BCAF rows will have 0 amounts.
      // people will have to fill in the budgets (or mark the funding deleted) to cover
      // people in the payroll in budgeted positions, but not funding in the base 
      // budget.
      CSFNewBCAFRows = bCSF.size();  
      for (Map.Entry<String,BudgetConstructionCalculatedSalaryFoundationTracker>
           orphanBCSF: bCSF.entrySet())
      {
         BudgetConstructionCalculatedSalaryFoundationTracker bcsf = 
             orphanBCSF.getValue(); 
         if (!buildPBGLFromBCSFAndStore(bcsf))
         { 
             continue;
         }
           buildAppointemntFundingFromBCSF(bcsf);
      }
    }
    
    private void readCSF(Integer BaseYear)
    {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        QueryByCriteria queryID = 
            new QueryByCriteria(CalculatedSalaryFoundationTracker.class,criteriaID);
        Iterator csfResultSet =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (csfResultSet.hasNext())
        {
            CalculatedSalaryFoundationTracker csfRow = 
                (CalculatedSalaryFoundationTracker) csfResultSet.next();
            CSFRowsRead = CSFRowsRead+1;
            CSFRowsVacant = CSFRowsVacant+(isVacantLine(csfRow)?1:0);
            // has this been overridden?  if so, don't store it
            String testKey = buildCSFKey(csfRow);
            if (csfOverrideKeys.contains(testKey))
            {
                continue;
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        QueryByCriteria queryID = 
            new QueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class,criteriaID);
        Iterator csfResultSet =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (csfResultSet.hasNext())
        {
            CalculatedSalaryFoundationTrackerOverride csfRow = 
                (CalculatedSalaryFoundationTrackerOverride) csfResultSet.next();
            CSFOverrideRead   = CSFOverrideRead+1;
            CSFOverrideVacant = CSFOverrideVacant+(isVacantLine(csfRow)?1:0);
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              RequestYear);
        QueryByCriteria queryID = 
            new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class,
                                criteriaID);
        Iterator bcafResults =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (bcafResults.hasNext())
        {
            CSFCurrentBCAFRows = CSFCurrentBCAFRows+1;
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        bCSFSize = hashObjectSize(CalculatedSalaryFoundationTrackerOverride.class,
                                  criteriaID)+
                   hashObjectSize(CalculatedSalaryFoundationTracker.class,criteriaID);
        bCSF = new HashMap<String,
               BudgetConstructionCalculatedSalaryFoundationTracker>(bCSFSize);
    }
    
    private void setUpbcHdrDocNumbers(Integer BaseYear)
    {
        Integer RequestYear = BaseYear+1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        bcHdrDocNumbers = 
            new HashMap<String,String>(hashObjectSize(BudgetConstructionHeader.class,
                                       criteriaID));
        //  now we have to get the actual data
        String[] headerList = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                               KFSPropertyConstants.ACCOUNT_NUMBER,
                               KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                               KFSPropertyConstants.DOCUMENT_NUMBER};
        ReportQueryByCriteria queryID =
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear);
        Criteria deleteCriteria = new Criteria();
        deleteCriteria.addNotEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                              BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
        Criteria vacantCriteria = new Criteria();
        vacantCriteria.addEqualTo(KFSPropertyConstants.CSF_FUNDING_STATUS_CODE,
                BCConstants.csfFundingStatusFlag.VACANT.getFlagValue());
        deleteCriteria.addOrCriteria(vacantCriteria);
        criteriaID.addAndCriteria(deleteCriteria);
        csfOverrideKeys = 
            new HashSet<String>(hashObjectSize(
                                CalculatedSalaryFoundationTrackerOverride.class,
                                criteriaID)); 
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
    CSFOverrideDeletesRead = csfOverrideKeys.size();
    // the hashset of override keys must exist before BCSF can be built
    // so, we should be able to clear the cache to save on memory
    //    getPersistenceBrokerTemplate().clearCache();
    }
    
    private void setUpCurrentPBGLKeys(Integer BaseYear)
    {
        // this will actually set up two maps
        // both will be used in the same routine to build the PBGL for BCSF 
        // keys not in the base budget (someone is being paid from an account,
        // but no one has yet bothered to move base budget funding into the 
        // account to cover the expense).
        Integer RequestYear = BaseYear+1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              RequestYear);
        criteriaID.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
                this.findPositionRequiredObjectCodes(BaseYear));
        currentPBGLKeys = new HashSet<String>(hashObjectSize(
                PendingBudgetConstructionGeneralLedger.class,criteriaID));
        // now do the same for the detailed position object code--> object type
        // map (object codes that are allowed to fund individual HR positions)
        detailedPositionObjectTypes = 
            new HashMap<String,String>(hashObjectSize(ObjectCode.class,criteriaID));
        // the PBGL has already been built
        // we will get business objects so we can use an overloaded method that
        // will be easy to change in order to extract the key
        // the objects are of no further use, and will disappear when we clear the cache
        QueryByCriteria pbGLQuery = 
            new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class,criteriaID);
        Iterator pbGLObjects =
            getPersistenceBrokerTemplate().getIteratorByQuery(pbGLQuery);
        while (pbGLObjects.hasNext())
        {
            PendingBudgetConstructionGeneralLedger pbGLRow =
                (PendingBudgetConstructionGeneralLedger) pbGLObjects.next();
            String testKey = this.buildPBGLKey(pbGLRow);
            currentPBGLKeys.add(testKey);
        }
        CSFCurrentGLRows = currentPBGLKeys.size();
        //
        // now we have to set up the query to read the object types
        String[] objectTypeSelectList = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                         KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
                                         KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(ObjectCode.class,objectTypeSelectList,criteriaID);
        Iterator objectTypeRowReturned =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (objectTypeRowReturned.hasNext())
        {
            Object[] objectRow = (Object[]) objectTypeRowReturned.next();
            String keyString = ((String) objectRow[0])+((String) objectRow[1]);
            String valueString = (String) objectRow[2];
            detailedPositionObjectTypes.put(keyString,valueString);
        }
    }
    
    private void setUpKeysNeedingRounding(Integer BaseYear)
    {
      Integer emplidCSFOvrdCount = new Integer(0);
      Integer emplidCSFCount     = new Integer(0);
      Criteria criteriaID = new Criteria();
      criteriaID.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE,
                        BudgetConstructionConstants.ACTIVE_CSF_DELETE_CODE);
      criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                            BaseYear);
      keysNeedingRounding = 
          new HashMap<String,roundMechanism>(
                  hashObjectSize(CalculatedSalaryFoundationTrackerOverride.class,
                          criteriaID,KFSPropertyConstants.EMPLID)+
                          hashObjectSize(CalculatedSalaryFoundationTracker.class,
                                  criteriaID,KFSPropertyConstants.EMPLID));
//     now fill the hashmap
//     there will be one rounding bucket for each EMPLID
      String[] columnList = {KFSPropertyConstants.EMPLID};
//     first use CSF Override
      ReportQueryByCriteria queryID = 
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
      criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
      positionNormalWorkMonths = 
          new HashMap<String,Integer>(
                  hashObjectSize(BudgetConstructionPosition.class,criteriaID));
      String[] fieldList = {KFSPropertyConstants.POSITION_NUMBER,
                            KFSPropertyConstants.IU_NORMAL_WORK_MONTHS};
      ReportQueryByCriteria queryID = 
                new ReportQueryByCriteria(BudgetConstructionPosition.class,
                                          fieldList,criteriaID);
      Iterator positionRows =
          getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
      while (positionRows.hasNext())
      {
          // apparently, numbers come back from report queries in DB-specific ways.  use Number to be safe (as OJB itself does)
          // the results do not go through the business object
          Object[] positionRow = (Object[]) positionRows.next();
          positionNormalWorkMonths.put((String) positionRow[0],
                                       (Integer) ((Number) positionRow[1]).intValue());
      }
    }
        
//     decide whether the current appointment funding row, missing from BCSF, has
//     been entered by a user or is due to a CSF row that has since gone away
    String notOnLeave  = new String(BCConstants.APPOINTMENT_FUNDING_DURATION_DEFAULT);
    KualiInteger rqstAmount = new KualiInteger(0);
    BigDecimal pctTime = new BigDecimal(0);
    BigDecimal FTE     = new BigDecimal(0);
    private void 
    untouchedAppointmentFunding(PendingBudgetConstructionAppointmentFunding bcaf)
    {
//     this checks to see whether the missing row could have come in from CSF 
//     if they did not come in from CSF, then it follows that someone entered them
//     and we should not touch them  
      CSFBCAFRowsMissing = CSFBCAFRowsMissing+1;  
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
      criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                            bcaf.getUniversityFiscalYear()-1);
      criteriaID.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                            bcaf.getChartOfAccountsCode());
      criteriaID.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER,
                            bcaf.getAccountNumber());
      criteriaID.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                            bcaf.getSubAccountNumber());
      criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
                            bcaf.getFinancialObjectCode());
      criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                            bcaf.getFinancialSubObjectCode());
      criteriaID.addEqualTo(KFSPropertyConstants.POSITION_NUMBER,
                            bcaf.getPositionNumber());
      criteriaID.addEqualTo(KFSPropertyConstants.CSF_FULL_TIME_EMPLOYMENT_QUANTITY,
                            bcaf.getAppointmentRequestedFteQuantity());
      criteriaID.addEqualTo(KFSPropertyConstants.CSF_TIME_PERCENT,
                            bcaf.getAppointmentRequestedTimePercent());
      Criteria vacantCriteria = new Criteria();
      Criteria flagCriteria   = new Criteria();
//     funding status is "vacant" or "unfunded"
      vacantCriteria.addEqualTo(KFSPropertyConstants.CSF_FUNDING_STATUS_CODE,
                                BCConstants.csfFundingStatusFlag.VACANT.getFlagValue());
      flagCriteria.addEqualTo(KFSPropertyConstants.CSF_FUNDING_STATUS_CODE,
                              BCConstants.csfFundingStatusFlag.UNFUNDED.getFlagValue());
      flagCriteria.addOrCriteria(vacantCriteria);
//     in addition, EMPLID is vacant
      vacantCriteria = new Criteria();
      vacantCriteria.addEqualTo(KFSPropertyConstants.EMPLID,
                                BCConstants.VACANT_EMPLID);
      vacantCriteria.addAndCriteria(flagCriteria);
//     OR, the EMPLID in CSF is the same as in BCAF
      flagCriteria = new Criteria();
      flagCriteria.addEqualTo(KFSPropertyConstants.EMPLID,
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
      CSFBCAFRowsMarkedDeleted = CSFBCAFRowsMarkedDeleted+1;
      // we also need to exhaust the iterator, so OJB will close the cursor (Oracle)
      TransactionalServiceUtils.exhaustIterator(resultSet);
    }

//     this is an inner class which will store the data we need to perform the rounding,
//     and supply the methods as well    
    KualiDecimal shavePennies = new KualiDecimal(100);
    private class roundMechanism
    {
//     the idea here is that people split over many lines could lose or gain several
//     dollars if we rounded each salary line individually.  so, we do the following.
//     (1) assume that all the amounts are positive
//     (2) truncate the actual amount to the next lowest integer (round floor)
//     (3) accumulate the difference in a running total to two decimal places
//     (4) when all the lines for a person have been encountered, we round the
//         difference to the next whole integer.
//     (5) add the difference in dollar increments to each of the lines until the
//         difference amount is exhausted  
//     In otherwords, we only use "bankers rounding" at the end.  We truncate by 
//     converting to an int, which calls BigDecimal.intvalue.        
    private KualiDecimal diffAmount = new KualiDecimal(0);
    private ArrayList<BudgetConstructionCalculatedSalaryFoundationTracker> 
     candidateBCSFRows =
     new ArrayList<BudgetConstructionCalculatedSalaryFoundationTracker>(10);

    public void addNewBCSF(BudgetConstructionCalculatedSalaryFoundationTracker bCSF,
                           KualiDecimal amountFromCSFToSet)
    {
       // lop off the pennies--go down to the nearest whole dollar 
       KualiInteger wholeDollarsCSFAmount =
           new KualiInteger(amountFromCSFToSet,
                            RoundingMode.FLOOR);
       // store the whole dollar amount in the budget construction CSF object
       bCSF.setCsfAmount(wholeDollarsCSFAmount);
       // find the pennies that were shaved off
       KualiDecimal penniesFromCSFAmount = amountFromCSFToSet;
       // BigDecimal values are immutable.  So, we have to reset the pointer 
       // after the subtract
       penniesFromCSFAmount =
       penniesFromCSFAmount.subtract(wholeDollarsCSFAmount.kualiDecimalValue());
       //  just round negative amounts and return
       //  this is only a safety measure.  negative salaries are illegal in 
       //  budget construction 
       if (wholeDollarsCSFAmount.isNegative())
       {
           return;
       }
       // save the difference
       // (KualiDecimal values are immutable, so we need to redirect the diffAmount
       //  pointer to a new one.)
       diffAmount = diffAmount.add(penniesFromCSFAmount);
       // store the budget construction CSF row with the truncated amount for
       // possible adjustment later
       candidateBCSFRows.add(bCSF);
    }

    public void fixRoundErrors()
    {
       // this routine adjusts the BCSF values so that the total for each
       // EMPLID round to the nearest whole dollar amount
       if (! diffAmount.isGreaterThan(KualiDecimal.ZERO))
       {
           return;
       }
       KualiDecimal adjustAmount  = new KualiDecimal(1);
       // @@TODO: test code
//       if (candidateBCSFRows.size() > 1)
//       {
//           LOG.info(String.format("\n\nrounding amount = %f for %d rows for %s",
//                    diffAmount.floatValue(),candidateBCSFRows.size(),
//                    candidateBCSFRows.get(0).getEmplid()));
//       }
       // @@TODO: end test code 
       // no rounding is necessary if the difference is less than a half a buck
       // this will also prevent our accessing an empty array list
       // we should adjust things with only one row if the pennies were >= .5, though
       //
       // now we use "banker's rounding" on the adjustment amount
       if (diffAmount.multiply(shavePennies).mod(shavePennies).isGreaterEqual(
               new KualiDecimal(50)))
       {
           diffAmount = diffAmount.add(adjustAmount);
       }
       if (diffAmount.isLessThan(adjustAmount))
       {
           return;
       }        
       for (BudgetConstructionCalculatedSalaryFoundationTracker rCSF: candidateBCSFRows)
       {
           // @@TODO: test code
          //LOG.info(String.format("\n%s %s (%s,%s,%s,%s,%s)",
          //          rCSF.getEmplid(), 
          //          rCSF.getPositionNumber(),
          //          rCSF.getChartOfAccountsCode(),
          //          rCSF.getAccountNumber(),
          //          rCSF.getSubAccountNumber(),
          //          rCSF.getFinancialObjectCode(),
          //          rCSF.getFinancialSubObjectCode()));
          // LOG.info(String.format("\n       before %f",
          //         rCSF.getCsfAmount().floatValue()));
           // @@TODO: end test code 
           KualiInteger fixBCSFAmount = rCSF.getCsfAmount();
           rCSF.setCsfAmount(fixBCSFAmount.add(new KualiInteger(adjustAmount.intValue())));
           diffAmount = diffAmount.subtract(adjustAmount);   
           // @@TODO: test code
           //LOG.info(String.format("\n       after %f",
           //        rCSF.getCsfAmount().floatValue()));
           // @@TODO: end test code 
           if (diffAmount.isLessThan(adjustAmount))
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
        /* (1) check the SQL for appointment funding (worked 03/29/2007) p6spy=yes
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              BaseYear+1);
        criteriaID.addEqualTo(KFSPropertyConstants.EMPLID,"0000001321");
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
        /* (2) test the addIn criterion  (worked 04/02/2007) p6spy=yes */
//        Integer RequestYear = BaseYear+1;
//        Criteria criteriaID = new Criteria();
//        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
//                              RequestYear);
//        criteriaID.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
//                this.findPositionRequiredObjectCodes(BaseYear));
//        String[] selectCount = {"COUNT(*)"};
//        ReportQueryByCriteria queryID =
//            new ReportQueryByCriteria(PendingBudgetConstructionGeneralLedger.class,
//                                      selectCount,criteriaID);
//        Iterator rowCounter =
//            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
//        while (rowCounter.hasNext())
//        {
//            Integer rowCount = 
//                ((Number)((Object[]) rowCounter.next())[0]).intValue();
//            LOG.info(String.format("\nPBGL rows with detailed position objects: %d",
//                     rowCount));
//        }
        // here is an add on to verify that OJB instantiates all joined rows
        // one row at a time
//        criteriaID.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,"BL");
//        QueryByCriteria testQry = 
//            new QueryByCriteria(LaborObject.class,criteriaID);
//        Iterator uncleGuido =
//            getPersistenceBrokerTemplate().getIteratorByQuery(testQry);
//        Integer numerateri = new Integer(0);
//        while (uncleGuido.hasNext())
//        {
//            numerateri = numerateri+1;
//            LOG.warn(String.format("\ninstantiating Object %d",numerateri));
//            LaborObject labObj = (LaborObject) uncleGuido.next();
//        }
        /* (3) attempt to test the rounding mechanism (worked 4/4/2007)
        // build the new BC CSF objects in memory
        setUpCSFOverrideKeys(BaseYear); 
        setUpBCSFMap(BaseYear);
        setUpKeysNeedingRounding(BaseYear);
        readCSFOverride(BaseYear);
        readCSF(BaseYear);
        CSFForBCSF = bCSF.size(); 
        adjustCSFRounding();
        CSFDiagnostics();
        */
        /* (4) attempt to test appointment funding (worked 04/09/2007)
        createNewBCDocumentsFromGLCSF(2007,true,true);
        buildAppointmentFundingAndBCSF(2007);
        */
        /*
         * (5)  attempting to speed up document creation
         */
        try
        {
            // testANewBCDocument();
            logAnExistingBCDocument();
        }
        catch (WorkflowException wex)
        {
            LOG.warn(String.format("\nproblem creating document %s",
                                   wex.getMessage()));
            wex.printStackTrace();
        }
        /*
         *  (6) write out a bean's properties
         */
//        try
//        {
//            testBeanProperties();
//        }
//        catch (IllegalAccessException iaex)
//        {
//            LOG.warn(String.format("\n %s",iaex.getMessage()));
//            iaex.printStackTrace();
//        }
//        catch (NoSuchMethodException nsex)
//        {
//            LOG.warn(String.format("\n %s",nsex.getMessage()));
//            nsex.printStackTrace();
//        }
//        catch (InvocationTargetException itex)
//        {
//            LOG.warn(String.format("\n %s",itex.getMessage()));
//            itex.printStackTrace();
//        }
   }
    
    public void testNullForeignKeys()
    {
        // this verifies that some of the code we used in fiscalYearMakers works
        //
        // build a bogus row with some null fields
        Integer oldYear = new Integer(1997);
        CalculatedSalaryFoundationTracker testCSFRow = new CalculatedSalaryFoundationTracker();
        testCSFRow.setUniversityFiscalYear(oldYear);
        testCSFRow.setChartOfAccountsCode("BL");
        testCSFRow.setAccountNumber("1031400");
        testCSFRow.setSubAccountNumber("-----");
        testCSFRow.setFinancialObjectCode("3000");
        testCSFRow.setFinancialSubObjectCode("---");
        testCSFRow.setPositionNumber("00000000");
        testCSFRow.setEmplid("0000000000");
        testCSFRow.setCsfCreateTimestamp(dateTimeService.getCurrentTimestamp());
        // save the row to force nulls in uniitialized fields
        getPersistenceBrokerTemplate().store(testCSFRow);
        getPersistenceBrokerTemplate().clearCache();
        // get the row
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, oldYear);  
        QueryByCriteria queryID = new QueryByCriteria(CalculatedSalaryFoundationTracker.class,criteriaID);
        testCSFRow = (CalculatedSalaryFoundationTracker) getPersistenceBrokerTemplate().getObjectByQuery(queryID);
        // now we want to do exactly what we are doing in fiscal year makers
        // on a string--
        String earnCode = new String("");
        String accountNumber = null;
        try 
        {
            Object returnValue = (String) PropertyUtils.getProperty(testCSFRow,"earnCode");
            if (returnValue != null)
            {
                earnCode = returnValue.toString();
            }
            returnValue = PropertyUtils.getProperty(testCSFRow,"accountNumber");
            // make sure one we know is there translates correctly from Object to string
            accountNumber = returnValue.toString();
        }
        catch (InvocationTargetException ex)
        {
            LOG.error(String.format("\n  InvocationTargetException getting earnCode from fake CSF row\n%s\n",ex.getMessage()));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.error(String.format("\n  NoSuchMethodException getting earnCode from fake CSF row\n%s\n",ex.getMessage()));
        }
        catch (IllegalAccessException ex)
        {
            LOG.error(String.format("\n  IllegalAccessException getting earnCode from fake CSF row\n%s\n",ex.getMessage()));
        }
        LOG.warn(String.format("\n>>> earnCode string length %d, earnCode = %s",earnCode.length(),earnCode));
        LOG.warn(String.format("\n   string from Object accountNumber (String) %s",accountNumber));
        // on a number--
        String employeeRecord = new String("");
        String universityFiscalYear = new String("");
        try 
        {
            Object returnValue = (String) PropertyUtils.getProperty(testCSFRow,"employeeRecord");
            if (returnValue != null)
            {
                employeeRecord = returnValue.toString();
            }
            // make sure one we know is there translates correctly from Object to string
            returnValue = PropertyUtils.getProperty(testCSFRow,"universityFiscalYear");
            universityFiscalYear = returnValue.toString();
        }
        catch (InvocationTargetException ex)
        {
            LOG.error(String.format("\n  InvocationTargetException getting employeeRecord from fake CSF row\n%s\n",ex.getMessage()));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.error(String.format("\n  NoSuchMethodException getting employeeRecord from fake CSF row\n%s\n",ex.getMessage()));
        }
        catch (IllegalAccessException ex)
        {
            LOG.error(String.format("\n  IllegalAccessException getting employeeRecord from fake CSF row\n%s\n",ex.getMessage()));
        }
        LOG.warn(String.format("\n>>> employeeRecord string length %d, employeeRecord = %s",employeeRecord.length(),employeeRecord));
        LOG.warn(String.format("\n   string from Object universityFiscalYear (Integer) %s",universityFiscalYear));
        // on a date--
        String effectiveDate = new String("");
        String csfCreateTimestamp = new String("");
        try 
        {
            Object returnValue = (String) PropertyUtils.getProperty(testCSFRow,"effectiveDate");
            if (returnValue != null)
            {
                effectiveDate = returnValue.toString();
            }
            returnValue = PropertyUtils.getProperty(testCSFRow,"csfCreateTimestamp");
            csfCreateTimestamp = returnValue.toString();
        }
        catch (InvocationTargetException ex)
        {
            LOG.error(String.format("\n  InvocationTargetException getting effectiveDate from fake CSF row\n%s\n",ex.getMessage()));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.error(String.format("\n  NoSuchMethodException getting effectiveDate from fake CSF row\n%s\n",ex.getMessage()));
        }
        catch (IllegalAccessException ex)
        {
            LOG.error(String.format("\n  IllegalAccessException getting effectiveDate from fake CSF row\n%s\n",ex.getMessage()));
        }
        LOG.warn(String.format("\n>>> effectiveDate string length %d, effectiveDate = %s",effectiveDate.length(),effectiveDate));
        LOG.warn(String.format("\n   string from Object csfCreateTimestamp (time stamp) %s",csfCreateTimestamp));
        // now test the string builder
        StringBuilder testBuilder = new StringBuilder(500);
        LOG.warn(String.format("\n\nString builder length = %d and value = %s when unitialized",testBuilder.toString().length(),testBuilder.toString()));
        testBuilder.delete(0,testBuilder.length());
        String emptyString = testBuilder.toString();
        LOG.warn(String.format("\n\nString builder length = %d and value = %s after delete",testBuilder.toString().length(),testBuilder.toString()));
        testBuilder.append(testCSFRow.getAccountNumber());
        testBuilder.delete(0,testBuilder.length());
        emptyString = testBuilder.toString();
        LOG.warn(String.format("\n\nString builder length = %d and value = %s after fill, then delete",testBuilder.toString().length(),testBuilder.toString()));
    }
    
    /*
     *   @@TODO:  take this out
     */
    // playing around to see if we can create a new document and have workflow
    // do fewer steps in the route
    
    
    private void testBeanProperties()
    throws IllegalAccessException, InvocationTargetException,
           NoSuchMethodException
    {
        HashMap<String,Object> propertyMap;
        propertyMap = 
            (HashMap<String,Object>) PropertyUtils.describe(workflowDocumentService);
        LOG.warn(String.format("\n\nworkflowDocumentService bean properties"));
        for (Map.Entry<String,Object> printPair: propertyMap.entrySet())
        {
          LOG.warn(String.format("\n %s  %s",
                          printPair.getKey(),
                          printPair.getValue().toString())); 
        }
        
    }
    
    private void logAnExistingBCDocument()
    throws WorkflowException
    {
        Long testDocumentID = new Long(306358);
        String note1 = new String("this here doc was scoped out by Ernie '07");
        String note2 = new String("Ernie cameback--left some goobers the first time");
        String note3 = new String("Ernie bought another ticket--whoowee!");
        KualiWorkflowDocument workflowDocument = 
                              workflowDocumentService.createWorkflowDocument(testDocumentID,
                                                      GlobalVariables.getUserSession().getUniversalUser());
        // try to write to the log
        workflowDocument.logDocumentAction(note1);
        // try writing to the log again
        workflowDocument.logDocumentAction(note2);
        // get a fresh "flex doc" for the same ID and try writing a third message
        workflowDocument =
            workflowDocumentService.createWorkflowDocument(testDocumentID,
                    GlobalVariables.getUserSession().getUniversalUser());
        workflowDocument.logDocumentAction(note3);
    }
    
    private void testANewBCDocument()
    throws WorkflowException
    {
        // first, get rid of anything that's in the header
        Criteria delCriteria = new Criteria();
        delCriteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,2012);
        QueryByCriteria queryID = new QueryByCriteria(BudgetConstructionDocument.class,
                                                      delCriteria);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        getPersistenceBrokerTemplate().clearCache();
        // try to get the workflow service that deals with the actual document header
        setUpRouteHeaderService();
        // set up the Budget Construction Header
        BudgetConstructionDocument newBCHdr;
        newBCHdr = (BudgetConstructionDocument)
        documentService.getNewDocument(
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
        newBCHdr.setUniversityFiscalYear(2012);
        newBCHdr.setChartOfAccountsCode("BL");
        newBCHdr.setAccountNumber("1031400");
        newBCHdr.setSubAccountNumber("-----");
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
//   this settting apparently has not effect on workflow
//        kualiDocumentHeader.setOrganizationDocumentNumber(
//                            newBCHdr.getUniversityFiscalYear().toString());
        kualiDocumentHeader.setFinancialDocumentStatusCode(
                KFSConstants.INITIAL_KUALI_DOCUMENT_STATUS_CD);
        kualiDocumentHeader.setFinancialDocumentStatusCode(
                EdenConstants.ROUTE_HEADER_FINAL_CD);
        kualiDocumentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        kualiDocumentHeader.setFinancialDocumentDescription(String.format("%s %d %s %s",
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION,
                       newBCHdr.getUniversityFiscalYear(),
                       newBCHdr.getChartOfAccountsCode(),newBCHdr.getAccountNumber()));
        kualiDocumentHeader.setExplanation(String.format("%s: %s",
                BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION,
                newBCHdr.getDocumentHeader().getWorkflowDocument().toString()));
//      do this for a timing check
        LOG.info(String.format("\nstore %s",
                 newBCHdr.getDocumentHeader().getWorkflowDocument().getRouteHeaderId()));    
        getPersistenceBrokerTemplate().store(newBCHdr);
        documentService.prepareWorkflowDocument(newBCHdr);
        StringBuffer annotateDoc = new StringBuffer("created by Genesis--test");
        annotateDoc.append(newBCHdr.getDocumentNumber());
//  the service does not have a complete method, but the document does        
//        workflowDocumentService.route(newBCHdr.getDocumentHeader().getWorkflowDocument(),
//                                      annotateDoc.toString(),null);
//        newBCHdr.getDocumentHeader().getWorkflowDocument().saveDocument(annotateDoc.toString());
//  this is Eric Westfall's suggestion of 01/18/2007
//        newBCHdr.getDocumentHeader().getWorkflowDocument().logDocumentAction(annotateDoc.toString());
//        newBCHdr.getDocumentHeader().getWorkflowDocument().routeDocument(annotateDoc.append(": passed to route").toString());
//  end of Eric's suggestion
//  this complete works, but does not fill in some of the dates 
/*  removed October 2007, when things stopped working in finite time        
        newBCHdr.getDocumentHeader().getWorkflowDocument().complete(annotateDoc.toString());
//  OK.  This should have saved the document.  Now, we are going to try to set all the dates and codes
        finalizeBCDocument(newBCHdr);
*/
    saveBCDocumentInWorkflow(newBCHdr);    
//  do this for a timing check
        LOG.info(String.format("\nfinished processing %s",
                 newBCHdr.getDocumentHeader().getWorkflowDocument().getRouteHeaderId()));  
        Long ourDocID = newBCHdr.getDocumentHeader().getWorkflowDocument().getRouteHeaderId();
        
//
//      try to change the route header itself, so it will have the correct dates and status codes
        
//
//        
//        KHUNTLEY does not have superuser privileges
//        the procurement card does a superUserApprove, and its test uses KULUSER 
//        KULUSER works for us as well, but for our document the superUser method
//        apparently goes through the same stages as the route method        
//        workflowDocumentService.superUserApprove(newBCHdr.getDocumentHeader().getWorkflowDocument(),
//                "created by Genesis");
   }
 
    public Object returnWkflwDocHeader()
    {   
        DocumentRouteHeaderValue newDoc = new DocumentRouteHeaderValue();
        Criteria docCriteria = new Criteria();
        docCriteria.addEqualTo("routeHeaderId",286968);
        QueryByCriteria queryID = 
            new QueryByCriteria(DocumentRouteHeaderValue.class,docCriteria);
        Iterator<DocumentRouteHeaderValue> itr =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        if (itr.hasNext())
        {
           newDoc = TransactionalServiceUtils.retrieveFirstAndExhaustIterator(itr);  
        }
        return newDoc;
    }

    public String testFindBCDocumentNumber (Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber) {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        criteriaId.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccounts);
        criteriaId.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        criteriaId.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class, criteriaId);
        BudgetConstructionHeader newBCHeader = (BudgetConstructionHeader) getPersistenceBrokerTemplate().getObjectByQuery(queryId);
        return(newBCHeader.getDocumentNumber());
    }
    
    public void testObjectID ()
    {
        FiscalYearFunctionControl fyrCtrl;
        FiscalYearFunctionControl fyrCtrl1;
        FiscalYearFunctionControl fyrCtrl2;
        FiscalYearFunctionControl fyrCtrl3;
        Integer fiscalYear;
        String  financialSystemFunctionControlCode; 
        // get the intial row
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,2008);
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE,"CSFUPD");
        QueryByCriteria queryID = new QueryByCriteria(FiscalYearFunctionControl.class,criteriaID);
        fyrCtrl = (FiscalYearFunctionControl) getPersistenceBrokerTemplate().getObjectByQuery(queryID);
        // now get a new object--see if it uses the cache
        fyrCtrl1 = (FiscalYearFunctionControl) getPersistenceBrokerTemplate().getObjectByQuery(queryID);
        LOG.warn(String.format("\ntwo queries by criteria for same row into different object pointers (constant WHERE clauses):\n same Object? %b",
                 fyrCtrl.equals(fyrCtrl1)));
        // now get a new object with a different query--see if it uses the cache
        fiscalYear = fyrCtrl.getUniversityFiscalYear();
        financialSystemFunctionControlCode = fyrCtrl1.getFinancialSystemFunctionControlCode();
        Criteria anotherCriteriaID = new Criteria();
        anotherCriteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,fiscalYear);
        anotherCriteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE,financialSystemFunctionControlCode);
        QueryByCriteria newQueryID = new QueryByCriteria(FiscalYearFunctionControl.class,anotherCriteriaID);
        fyrCtrl2 = (FiscalYearFunctionControl) getPersistenceBrokerTemplate().getObjectByQuery(newQueryID);
        LOG.warn(String.format("\ntwo queries by criteria for same row into different object pointers (constant WHERE clause,"+
                 "variable WHERE clause):\n same Object? %b",
                fyrCtrl.equals(fyrCtrl2)));
        // finally, try getting it using getObjectByID
        // we will take a flying leap and guess that we should pass in the object--that's what the code looks like it wants
        // recall that the PersistenceBrokerTemplate converts PersistenceBrokerException into a DataAccess Exception
        try
        {
        fyrCtrl3 = (FiscalYearFunctionControl) getPersistenceBrokerTemplate().getObjectById(FiscalYearFunctionControl.class,
                                                                              fyrCtrl2);
        LOG.warn(String.format("\ngetObjectById: same Object? %b",
                fyrCtrl.equals(fyrCtrl3)));
        }
        catch (DataAccessException e)
        {
            LOG.warn(String.format("\nPersistenceBrokerException when trying to read Identity for pointer fyrCtrl2:\n%s",e));
        }
        try
        {
        fyrCtrl3 = (FiscalYearFunctionControl) getPersistenceBrokerTemplate().getObjectById(FiscalYearFunctionControl.class,
                                                                              fyrCtrl1);
        LOG.warn(String.format("\ngetObjectById: same Object? %b",
                fyrCtrl.equals(fyrCtrl3)));
        }
        catch (DataAccessException e)
        {
            LOG.warn(String.format("\nPersistenceBrokerException when trying to read Identity for pointer fyrCtrl1:\n%s",e));
        }
        try
        {
        fyrCtrl3 = (FiscalYearFunctionControl) getPersistenceBrokerTemplate().getObjectById(FiscalYearFunctionControl.class,
                                                                              fyrCtrl);
        LOG.warn(String.format("\ngetObjectById: same Object? %b",
                fyrCtrl.equals(fyrCtrl3)));
        }
        catch (DataAccessException e)
        {
            LOG.warn(String.format("\nPersistenceBrokerException when trying to read Identity for pointer fyrCtrl:\n%s",e));
        }
    }
    
    public void testLaborInterface()
    {
        LOG.warn("\n\ntest the new labor interface\n");
        ArrayList<String> laborObjects = findPositionRequiredObjectCodes(2007);
        for (String posObject : laborObjects)
        {
            LOG.warn(String.format("  %s ",posObject));
        }
        LOG.warn("\nend of labor interface test\n");
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
    public void setLaborModuleService(LaborModuleService laborModuleService)
    {
        this.laborModuleService = laborModuleService;
    }
     public void setWorkflowDocumentService (WorkflowDocumentService workflowDocumentService)
     {
         this.workflowDocumentService = workflowDocumentService;
     }
     private void setUpRouteHeaderService ()
     {
        if (this.routeHeaderService == null)
        {
          this.routeHeaderService = 
              (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
        }
     }
     
     
}