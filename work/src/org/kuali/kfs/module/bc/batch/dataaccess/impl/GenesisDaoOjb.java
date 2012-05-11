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
package org.kuali.kfs.module.bc.batch.dataaccess.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl;
import org.kuali.kfs.fp.businessobject.FunctionControlCode;
import org.kuali.kfs.gl.GeneralLedgerConstants.ColumnNames;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao;
import org.kuali.kfs.module.bc.batch.dataaccess.GenesisDao;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReason;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.CalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.CalculatedSalaryFoundationTrackerOverride;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.sys.KFSConstants.ParameterValues;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;


public class GenesisDaoOjb extends BudgetConstructionBatchHelperDaoOjb implements GenesisDao {
    /*
     *   December, 2006:
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
     *        INSERT or UPDATE.  (There seems to be no way in OJB to store more than
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
     *    November, 2008:
     *    JOINs (especially those using one-to-many relationships) are problematic in OJB, as the admit in their docmentation.
     *    But, it is possible, using ReportQuery and qualifiers, to tell OJB how to build efficient JOIN SQL correctly.  The code
     *    below, however, seems to be efficient as well.  The only disadvantage it has is that it requires additional memory.  Based
     *    on tests against realistic data sets (over 400,000 rows written), the OJB cache is a far greater memory hog--and itself uses
     *    hashmaps--than these hashmaps.  If you are running batch in your own container with no other threads active, it is easy to
     *    turn off the OJB cache dynamically.  This code doesn't really make use of the OJB cache.  It attempts to avoid reading the
     *    same row repeatedly.  It also uses report queries extensively, and those do not cache results.
     *    
     */

    private FiscalYearFunctionControl fiscalYearFunctionControl;
    private FunctionControlCode functionControlCode;

    /*  turn on the logger for the persistence broker */
    private static Logger LOG = org.apache.log4j.Logger.getLogger(GenesisDaoOjb.class);

    /*
     *   version number for new rows
     */
    public final static Long DEFAULT_VERSION_NUMBER = new Long(1);
    /*
     *   code a high value for the limit of the organization reporting chain.  we limit this to avoid
     *   infinite loops when for some reason there is a circular reporting chain in the DB
     */
    public final static Integer MAXIMUM_ORGANIZATION_TREE_DEPTH = new Integer(1000);

    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private DateTimeService dateTimeService;
    private DocumentDao documentDao;
    private KualiModuleService kualiModuleService;
    private BudgetConstructionHumanResourcesPayrollInterfaceDao budgetConstructionHumanResourcesPayrollInterfaceDao;


    public final Map<String, String> getBudgetConstructionControlFlags(Integer universityFiscalYear) {
        /*  return the flag names and the values for all the BC flags for the fiscal year */

        /*  the key to the map returned will be the name of the flag
         *  the entry will be the flag's value 
         */
        Map<String, String> controlFlags = new HashMap();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        String[] queryAttr = { KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_ACTIVE_INDICATOR };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(FiscalYearFunctionControl.class, queryAttr, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        /* fill in the map */
        while (Results.hasNext()) {
            String[] mapValues = (String[]) ((Object[]) Results.next());
            controlFlags.put(mapValues[0], mapValues[1]);
        }
        ;
        return controlFlags;
    }

    public boolean getBudgetConstructionControlFlag(Integer universityFiscalYear, String FlagID) {
        /*  return true if a flag is on, false if it is not */
        Boolean result;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, FlagID);
        String[] queryAttr = { KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_ACTIVE_INDICATOR };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(FiscalYearFunctionControl.class, queryAttr, criteriaID, true);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        result = (Boolean) ((Object[]) Results.next())[0];
        return result;
    }


    /*
     * ********************************************************************************
     *   This routine returns the fiscal year as of the run date.
     *   Normally, genesis runs in the current fiscal year to build budget construction
     *   for the coming fiscal year.
     */
    public Integer fiscalYearFromToday() {
        //  we look up the fiscal year for today's date, and return it
        //  we return 0 if nothing is found
        Integer currentFiscalYear = new Integer(0);
        Date lookUpDate = dateTimeService.getCurrentSqlDateMidnight();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_DATE, lookUpDate);
        String[] attrb = { KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(UniversityDate.class, attrb, criteriaID);
        Iterator resultRow = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        // there will only be one row.
        // Oracle will not close the cursor, however, until the iterator has been exhausted
        if (resultRow.hasNext()) {
            currentFiscalYear = (Integer) ((Number) ((Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(resultRow))[0]).intValue();
        }
        LOG.debug(String.format("\nreturned from fiscalYearFromToday: %d", currentFiscalYear));
        return currentFiscalYear;
    }


    /*
     * ******************************************************************************  
     *   (1) these routines are used to create and set the control flags for budget *
     *   construction.  Genesis sets flags for both the current fiscal year and the *
     *   fiscal year to be budgeted to a fixed set of initial values.  The flags    *
     *   are changed after that using maintenance screens.                          *
     * ******************************************************************************  
     */

    public void setControlFlagsAtTheStartOfGenesis(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        //
        // first we have to eliminate anything for the new year that's there now
        getPersistenceBrokerTemplate().clearCache();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(FiscalYearFunctionControl.class, criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        getPersistenceBrokerTemplate().clearCache();
        // 
        //  the default values (except for the BUDGET_CONSTRUCTION_GENESIS_RUNNING flag)
        //  come from the function control code table
        FiscalYearFunctionControl SLF;
        criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        String[] attrQ = { KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_DEFAULT_INDICATOR };
        ReportQueryByCriteria rptQueryID = new ReportQueryByCriteria(FunctionControlCode.class, attrQ, criteriaID);
        Integer sqlFunctionControlCode = 0;
        Integer sqlFunctionActiveIndicator = 1;
        // run the query
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rptQueryID);
        while (Results.hasNext()) {
            SLF = new FiscalYearFunctionControl();
            Object[] resultFields = (Object[]) Results.next();
            String flagTag = (String) resultFields[sqlFunctionControlCode];
            //          String flagDefault = (String) resultFields[sqlFunctionActiveIndicator];
            //  apparently OJB is smart enough to bring this in as a boolean
            boolean flagDefault = (Boolean) resultFields[sqlFunctionActiveIndicator];
            SLF.setUniversityFiscalYear(RequestYear);
            LOG.debug("\nfiscal year has been set");
            SLF.setFinancialSystemFunctionControlCode(flagTag);
            LOG.debug("\nfunction code has been set");
            SLF.setVersionNumber(DEFAULT_VERSION_NUMBER);
            LOG.debug(String.format("\nversion number set to %d", SLF.getVersionNumber()));
            if (flagTag.equals(BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING)) {
                SLF.setFinancialSystemFunctionActiveIndicator(true);
            }
            else {
                //               SLF.setFinancialSystemFunctionActiveIndicator(
                //                       ((flagDefault == KFSConstants.ParameterValues.YES)? true : false));
                SLF.setFinancialSystemFunctionActiveIndicator(flagDefault);
            }
            LOG.debug("\nabout to store the result");
            getPersistenceBrokerTemplate().store(SLF);
        }
    }

    public void setControlFlagsAtTheEndOfGenesis(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        resetExistingFlags(BaseYear, BCConstants.CURRENT_FSCL_YR_CTRL_FLAGS);
        resetExistingFlags(RequestYear, BCConstants.NEXT_FSCL_YR_CTRL_FLAGS_AFTER_GENESIS);
    }

    //  this method just reads the existing flags and changes their values
    //  based on the configuration constants
    public void resetExistingFlags(Integer Year, HashMap<String, String> configValues) {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, Year);
        QueryByCriteria queryID = new QueryByCriteria(FiscalYearFunctionControl.class, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (Results.hasNext()) {
            LOG.debug("\nbefore call to next() and cast");
            FiscalYearFunctionControl SLF = (FiscalYearFunctionControl) Results.next();
            LOG.debug("\nafter call to next()");
            String mapKey = SLF.getFinancialSystemFunctionControlCode();
            String newValue = configValues.get(mapKey);
            SLF.setFinancialSystemFunctionActiveIndicator(((newValue.equals(ParameterValues.YES)) ? true : false));
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
    public void clearDBForGenesis(Integer BaseYear) {
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

    protected void clearBudgetConstructionAdministrativePost() {
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionAdministrativePost.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBaseYearBudgetConstructionAppointmentFundingReason(Integer BaseYear) {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionAppointmentFundingReason.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBothYearsBudgetConstructionAppointmentFundingReason(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionAppointmentFundingReason.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBudgetConstructionAppointmentFundingReason() {
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionAppointmentFundingReason.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearRequestYearBudgetConstructionAppointmentFundingReason(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionAppointmentFundingReason.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBaseYearBCSF(Integer BaseYear) {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBothYearsBCSF(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBCSF() {
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBudgetConstructionIntendedIncumbent() {
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionIntendedIncumbent.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBaseYearBCPosition(Integer BaseYear) {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionPosition.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBothYearsBCPosition(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionPosition.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBCPosition() {
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionPosition.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearRequestYearBCPosition(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionPosition.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearRequestYearBCSF(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBaseYearHeaders(Integer BaseYear) {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBothYearsHeaders(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearHeaders() {
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionHeader.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBaseYearPBGL(Integer BaseYear) {
        // the order here is mandated by referential integrity
        // remove rows from the base year from budget construction months
        Criteria mnCriteriaID = new Criteria();
        mnCriteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        QueryByCriteria mnQueryID = new QueryByCriteria(BudgetConstructionMonthly.class, mnCriteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(mnQueryID);
        // remove rows from the basse year from budget construction general ledger
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        QueryByCriteria queryID = new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class, criteriaID);
        LOG.debug(String.format("delete PBGL started at %tT for %d", dateTimeService.getCurrentDate(), BaseYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.debug(String.format("delete PBGL ended at %tT", dateTimeService.getCurrentDate()));
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBothYearsPBGL(Integer BaseYear) {
        clearBaseYearPBGL(BaseYear);
        clearRequestYearPBGL(BaseYear);
    }

    protected void clearPBGL() {
        // the order here is mandated by referential integrity
        QueryByCriteria mnQueryId = new QueryByCriteria(BudgetConstructionMonthly.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(mnQueryId);
        QueryByCriteria queryId = new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearRequestYearPBGL(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        // the order here is mandated by referential integrity
        // remove rows from the request year from budget construction months
        Criteria mnCriteriaID = new Criteria();
        mnCriteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria mnQueryID = new QueryByCriteria(BudgetConstructionMonthly.class, mnCriteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(mnQueryID);
        // remove rows from the request year
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class, criteriaID);
        LOG.debug(String.format("\ndelete PBGL started at %tT for %d", dateTimeService.getCurrentDate(), RequestYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.debug(String.format("\ndelete PBGL ended at %tT", dateTimeService.getCurrentDate()));
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBaseYearPendingApptFunding(Integer BaseYear) {
        Criteria criteriaId = new Criteria();
        criteriaId.addColumnEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        QueryByCriteria queryId = new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearBothYearsPendingApptFunding(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addBetween(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearPendingApptFunding() {
        QueryByCriteria queryId = new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    protected void clearRequestYearPendingApptFunding(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class, criteriaId);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    /* 
     *  ****************************************************************************
     *  (3) BC Document Creation                                                   *
     *  ****************************************************************************
     */
    /*
     *  A document number is created for each account/sub-account involved in budgdet construction.  
     *  These "documents" are not routed.  They are created and finalized in workflow in a single step. 
     *  They serve two purposes.  First, the budget construction tool can use the workflow action tables to 
     *  track the people who have edited each account/sub-account, and use the workflow notes tables to allow 
     *  these editors to leave whatever comments they may wish to make.  Second, the general ledger entry 
     *  table requires that every entry have a document number attached.  Assigning a document number to 
     *  each account/sub-account in the budget allows us to load the budget to the general ledger with 
     *  a document number that can be tracked in workflow as outlined above.
     *  
     *  An account/subaccount can get into budget construction in three ways.
     *  (1)  Someone can create a budget for it using the budget construction on-line tool.  Neither genesis nor 
     *  the batch update process cares about this case, so it is irrelevant here.
     *  (2)  There is an existing base budget in the current fiscal year for the account/subaccount.  In this case,
     *  genesis and the batch update process will create a document for it in budget construction if one does not already 
     *  exist (much of the code below involves looking at the GL and checking for existing documents for account/subaccounts with 
     *  base budget in the GL.
     *  (3) There is no base budget in the GL, but for some reason there is a person in a budgeted position drawing pay in 
     *  payroll.  In this case, if no document number exists, we will create one.  It will point to budget construction CSF tracker 
     *  (i.e., payroll) base, but it will not have any base budget amounts in the budget construction general ledger.
     *
     *  NOTE: Kuali workflow went through a lot of iterations since this code was written.  I have tried to remove any comments 
     *  that pertain to iterations other than the stable release 3 configuration, but I may have missed something.  Earlier iterations
     *  (a) used a lot of memory and (b) were at one point in a separate transaction.
     *  Because this code will have to deal with a remote workflow server in real life, it will be the slowest part of genesis.  We did 
     *  not have a good test bed for thoroughly researching this issue, but based on extrapolation I believe the solution we came up with 
     *  (going to "complete" status immediately in order to by-pass routing, then using workflow routines to read the actual workflow
     *  document tables and set all the dates to final and save the initial "action taken") will give acceptable performance.  
     */

    protected HashSet<String> currentBCHeaderKeys = new HashSet<String>(1);
    // these routines are used to merge CSF and CSF Override
    protected HashMap<String, String[]> CSFTrackerKeys = new HashMap<String, String[]>(1);

    protected void createNewDocumentsCleanUp() {
        currentBCHeaderKeys.clear();
        CSFTrackerKeys.clear();
    }

    // counters
    protected Long documentsToCreateinNTS = new Long(0);
    protected Long documentsSkippedinNTS = new Long(0);
    protected Long documentsCreatedinNTS = new Long(0);
    protected Long documentsCSFCreatedinNTS = new Long(0);
    protected Long documentsGLCreatedinNTS = new Long(0);

    protected Long proxyCandidatesReadinTS = new Long(0);
    protected Long proxyBCHeadersCreatedinTS = new Long(0);

    //
    // this is the new document creation mechanism that works with embedded workflow
    public void createNewBCDocumentsFromGLCSF(Integer BaseYear, boolean GLUpdatesAllowed, boolean CSFUpdatesAllowed) {
        if ((!GLUpdatesAllowed) && (!CSFUpdatesAllowed)) {
            // no new documents need to be created
            return;
        }
        // take the count of header keys from the GL
        setUpCurrentBCHeaderKeys(BaseYear);
        Integer RequestYear = BaseYear + 1;
        // fetch the keys currently in budget construction header
        getCurrentBCHeaderKeys(BaseYear);
        //
        //  we have to read the GL BALANCE (which is not proxy=true) to create
        //  new BC header objects.  we use a report query to avoid triggering
        //  nine separate reads for each row, and to avoid returning the entire
        //  field list when we only need a few fields.
        if (GLUpdatesAllowed) {
            getAndStoreCurrentGLBCHeaderCandidates(BaseYear);
        }
        //  we also have to read CSF for any accounts with no base budget in GL BALANCE
        //  but which pay people in budgeted positions
        if (CSFUpdatesAllowed) {
            setUpCSFHashStructures(BaseYear);
            getCSFCandidateDocumentKeys(BaseYear);
            getCSFOverrideDeletedKeys(BaseYear);
            getCSFOverrideCandidateDocumentKeys(BaseYear);
            getAndStoreCurrentCSFBCHeaderCandidates(BaseYear);
        }
        createNewDocumentsCleanUp();
    }

    //  here are the private methods that go with it      
    protected void getAndStoreCurrentCSFBCHeaderCandidates(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        for (Map.Entry<String, String[]> newCSFDocs : CSFTrackerKeys.entrySet()) {
            // all the CSF keys in the map require new documents
            proxyCandidatesReadinTS = proxyCandidatesReadinTS + 1;
            String[] Results = newCSFDocs.getValue();
            // set up the Budget Construction Header
            BudgetConstructionDocument newBCHdr;
            try {
                newBCHdr = (BudgetConstructionDocument) documentService.getNewDocument(BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
            }
            catch (WorkflowException wex) {
                LOG.warn(String.format("\nskipping creation of document for CSF key: %s %s %s \n(%s)\n", Results[0], Results[1], Results[2], wex.getMessage()));
                wex.printStackTrace();
                documentsSkippedinNTS = documentsSkippedinNTS + 1;
                continue;
            }
            newBCHdr.setUniversityFiscalYear(RequestYear);
            newBCHdr.setChartOfAccountsCode(Results[0]);
            newBCHdr.setAccountNumber(Results[1]);
            newBCHdr.setSubAccountNumber(Results[2]);
            //  store the document
            try {
                storeANewBCDocument(newBCHdr);
            }
            catch (WorkflowException wex) {
                LOG.warn(String.format("\nskipping creation of document for CSF key: %s %s %s \n(%s)\n", newBCHdr.getChartOfAccounts(), newBCHdr.getAccountNumber(), newBCHdr.getSubAccountNumber(), wex.getMessage()));
                wex.printStackTrace();
                documentsSkippedinNTS = documentsSkippedinNTS + 1;
                continue;

            }
            documentsCSFCreatedinNTS = documentsCSFCreatedinNTS + 1;
            documentsCreatedinNTS = documentsCreatedinNTS + 1;
        }
    }

    protected void getAndStoreCurrentGLBCHeaderCandidates(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        // first build a document set from GL BALANCE
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaId.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        String newAttr = ColumnNames.BEGINNING_BALANCE + "+" + ColumnNames.ANNUAL_BALANCE;
        criteriaId.addNotEqualTo(newAttr, 0);
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER };
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(Balance.class, queryAttr, criteriaId, true);
        Iterator RowsReturned = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        while (RowsReturned.hasNext()) {
            proxyCandidatesReadinTS = proxyCandidatesReadinTS + 1;
            Object[] Results = (Object[]) RowsReturned.next();
            String testKey = ((String) Results[0]) + ((String) Results[1]) + ((String) Results[2]);
            if (currentBCHeaderKeys.contains(testKey)) {
                // don't create a new row for anything with a current header
                continue;
            }
            // set up the Budget Construction Header
            BudgetConstructionDocument newBCHdr;
            try {
                newBCHdr = (BudgetConstructionDocument) documentService.getNewDocument(BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
            }
            catch (WorkflowException wex) {
                LOG.warn(String.format("\nskipping creation of document for GL key: %s %s %s \n(%s)\n", (String) Results[0], (String) Results[1], (String) Results[2], wex.getMessage()));
                wex.printStackTrace();
                documentsSkippedinNTS = documentsSkippedinNTS + 1;
                continue;
            }
            newBCHdr.setUniversityFiscalYear(RequestYear);
            newBCHdr.setChartOfAccountsCode((String) Results[0]);
            newBCHdr.setAccountNumber((String) Results[1]);
            newBCHdr.setSubAccountNumber((String) Results[2]);
            //  store the document
            try {
                storeANewBCDocument(newBCHdr);
            }
            catch (WorkflowException wex) {
                LOG.warn(String.format("\nskipping creation of document for GL key: %s %s %s \n(%s)\n", newBCHdr.getChartOfAccounts(), newBCHdr.getAccountNumber(), newBCHdr.getSubAccountNumber(), wex.getMessage()));
                wex.printStackTrace();
                documentsSkippedinNTS = documentsSkippedinNTS + 1;
                continue;

            }
            documentsGLCreatedinNTS = documentsGLCreatedinNTS + 1;
            documentsCreatedinNTS = documentsCreatedinNTS + 1;
            //  add this header to the current BC Header map
            currentBCHeaderKeys.add(testKey);
        }
    }

    public void getCSFCandidateDocumentKeys(Integer BaseYear) {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaId.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER };
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class, queryAttr, criteriaId, true);
        Iterator rowsReturned = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        // decide which keys from CSF need to create new documents
        // we have already created new documents for all the GL keys
        while (rowsReturned.hasNext()) {
            Object[] returnedRow = (Object[]) rowsReturned.next();
            String testKey = ((String) returnedRow[0]) + ((String) returnedRow[1]) + ((String) returnedRow[2]);
            if (currentBCHeaderKeys.contains(testKey)) {
                //  there is no need to create a row for this key
                continue;
            }
            String[] valueCSF = { (String) returnedRow[0], (String) returnedRow[1], (String) returnedRow[2] };
            CSFTrackerKeys.put(testKey, valueCSF);
        }
    }

    public void getCSFOverrideCandidateDocumentKeys(Integer BaseYear) {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaId.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        criteriaId.addEqualTo(KFSPropertyConstants.ACTIVE, true);
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER };
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class, queryAttr, criteriaId, true);
        Iterator rowsReturned = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        // decide which keys from CSF override need to create new documents
        // we have already read in the CSF keys--existing keys need not be replaced
        // new active keys from CSF override should be added
        while (rowsReturned.hasNext()) {
            Object[] returnedRow = (Object[]) rowsReturned.next();
            String testKey = ((String) returnedRow[0]) + ((String) returnedRow[1]) + ((String) returnedRow[2]);
            if (currentBCHeaderKeys.contains(testKey)) {
                //  there is no need to create a row for this key
                //  it is already in the base budget in the GL
                continue;
            }
            String[] valueCSF = { (String) returnedRow[0], (String) returnedRow[1], (String) returnedRow[2] };
            CSFTrackerKeys.put(testKey, valueCSF);
        }
    }

    public void getCSFOverrideDeletedKeys(Integer BaseYear) {
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaId.addNotEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        criteriaId.addEqualTo(KFSPropertyConstants.ACTIVE, true);
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER };
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class, queryAttr, criteriaId, true);
        Iterator rowsReturned = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        // decide which keys from CSF override need to create new documents
        // we have already read in the CSF keys--any overrides of existing CSF
        // which carry a delete code should be tentatively removed CSF key table
        while (rowsReturned.hasNext()) {
            Object[] returnedRow = (Object[]) rowsReturned.next();
            String testKey = ((String) returnedRow[0]) + ((String) returnedRow[1]) + ((String) returnedRow[2]);
            if (currentBCHeaderKeys.contains(testKey)) {
                //  this key is in the GL base budget
                //  it should create a document whether anyone is paid from it
                //  or not
                continue;
            }
            if (CSFTrackerKeys.containsKey(testKey)) {
                // an override row deletes a key in CSF
                // we tentatively remove this key from the map
                // if there is an active override row for this key as well, it 
                // will be restored when we read the active override keys
                CSFTrackerKeys.remove(testKey);
            }
        }
    }

    protected void getCurrentBCHeaderKeys(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaId = new Criteria();
        Iterator<Object[]> Results;
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        String[] selectList = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER };
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(BudgetConstructionHeader.class, selectList, criteriaId);
        Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);

        while (Results.hasNext()) {
            Object[] returnedRow = Results.next();
            currentBCHeaderKeys.add(((String) returnedRow[0]) + ((String) returnedRow[1]) + ((String) returnedRow[2]));
        }
    }

    public void setUpCSFHashStructures(Integer BaseYear) {
        // these are the potential document keys in the CSF tracker
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaId.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        String[] propertyString = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER };
        CSFTrackerKeys = new HashMap<String, String[]>(hashObjectSize(CalculatedSalaryFoundationTracker.class, criteriaId, propertyString));
    }

    public void setUpCurrentBCHeaderKeys(Integer BaseYear) {
        // the BC header keys should be roughly the same as the GL balance BB keys
        // if any new keys are introduced from CSF, it means that there is money
        // in the payroll that has NOT been budgeted.  this should be a rare 
        // occurrence.  
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        String[] propertyString = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER };
        currentBCHeaderKeys = new HashSet<String>(hashObjectSize(Balance.class, criteriaID, propertyString));
    }

    public void storeANewBCDocument(BudgetConstructionDocument newBCHdr) throws WorkflowException {
        newBCHdr.setOrganizationLevelChartOfAccountsCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE);
        newBCHdr.setOrganizationLevelOrganizationCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE);
        newBCHdr.setOrganizationLevelCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_CODE);
        newBCHdr.setBudgetTransactionLockUserIdentifier(BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        newBCHdr.setBudgetLockUserIdentifier(BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        newBCHdr.setVersionNumber(DEFAULT_VERSION_NUMBER);
        FinancialSystemDocumentHeader kualiDocumentHeader = newBCHdr.getFinancialSystemDocumentHeader();
        newBCHdr.setDocumentNumber(newBCHdr.getDocumentHeader().getDocumentNumber());
        kualiDocumentHeader.setOrganizationDocumentNumber(newBCHdr.getUniversityFiscalYear().toString());
        kualiDocumentHeader.setFinancialDocumentStatusCode(KFSConstants.INITIAL_KUALI_DOCUMENT_STATUS_CD);
        kualiDocumentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        kualiDocumentHeader.setDocumentDescription(String.format("%s %d %s %s", BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION, newBCHdr.getUniversityFiscalYear(), newBCHdr.getChartOfAccountsCode(), newBCHdr.getAccountNumber()));
        kualiDocumentHeader.setExplanation(BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION);
        // September 2, 2009: since this document is not routed, calling this method should set it to final
        documentService.routeDocument(newBCHdr, "created by Genesis", new ArrayList());
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
    public void createChartForNextBudgetCycle() {
        // first we have to remove what's there
        // (the documentation says deleteByQuery (1) ignores object references and (2) does
        //  not synchronize the cache.  so, we clear the cache before and after.)
        getPersistenceBrokerTemplate().clearCache();
        Criteria criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        QueryByCriteria killAcctQuery = new QueryByCriteria(BudgetConstructionAccountReports.class);
        killAcctQuery.setCriteria(criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(killAcctQuery);
        QueryByCriteria killOrgQuery = new QueryByCriteria(BudgetConstructionOrganizationReports.class);
        killOrgQuery.setCriteria(criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(killOrgQuery);
        getPersistenceBrokerTemplate().clearCache();
        // build the organization table  
        buildNewOrganizationReportsTo();
        // build the account table
        buildNewAccountReportsTo();
    }

    //  private working methods for the BC chart update

    protected void buildNewAccountReportsTo() {

        //  All active accounts are loaded into the budget accounting table

        Integer sqlChartOfAccountsCode = 0;
        Integer sqlAccountNumber = 1;
        Integer sqlReportsToChartofAccountsCode = 0;
        Integer sqlOrganizationCode = 2;

        Long accountsAdded = new Long(0);

        Criteria criteriaID = new Criteria();
        /*  current IU genesis does NOT check for closed accounts--it loads all accounts
         *  it is possible that an account which has been closed still has base budget 
         */
        criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.ORGANIZATION_CODE };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(Account.class, queryAttr, criteriaID, true);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (Results.hasNext()) {
            Object[] ReturnList = (Object[]) Results.next();
            // just save this stuff, one at a time
            // it isn't needed for anything else, so we don't need to store it in memory in a map
            BudgetConstructionAccountReports acctRpts = new BudgetConstructionAccountReports();
            acctRpts.setChartOfAccountsCode((String) ReturnList[sqlChartOfAccountsCode]);
            acctRpts.setAccountNumber((String) ReturnList[sqlAccountNumber]);
            acctRpts.setReportsToChartOfAccountsCode((String) ReturnList[sqlReportsToChartofAccountsCode]);
            acctRpts.setReportsToOrganizationCode((String) ReturnList[sqlOrganizationCode]);
            acctRpts.setVersionNumber(DEFAULT_VERSION_NUMBER);
            getPersistenceBrokerTemplate().store(acctRpts);
            accountsAdded = accountsAdded + 1;
        }
        LOG.info(String.format("\nAccount reporting lines added to budget construction %d", accountsAdded));
    }

    protected void buildNewOrganizationReportsTo() {

        //  all active organizations are loaded into the budget construction
        //  organization table

        Integer sqlChartOfAccountsCode = 0;
        Integer sqlOrganizationCode = 1;
        Integer sqlReportsToChartOfAccountsCode = 2;
        Integer sqlReportsToOrganizationCode = 3;
        Integer sqlResponsibilityCenterCode = 4;

        Long organizationsAdded = new Long(0);

        Criteria criteriaID = new Criteria();
        /*
         *  IU genesis takes all organizations, not just active ones
         *  the reason is that a closed account which still has a base budget
         *  might report to one of these organizations 
         */
        criteriaID = QueryByCriteria.CRITERIA_SELECT_ALL;
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ORGANIZATION_CODE, KFSPropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.REPORTS_TO_ORGANIZATION_CODE, KFSPropertyConstants.RESPONSIBILITY_CENTER_CODE };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(Organization.class, queryAttr, criteriaID, true);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (Results.hasNext()) {
            Object[] ReturnList = (Object[]) Results.next();
            // just save this stuff, one at a time
            // it isn't needed for anything else
            BudgetConstructionOrganizationReports orgRpts = new BudgetConstructionOrganizationReports();
            orgRpts.setChartOfAccountsCode((String) ReturnList[sqlChartOfAccountsCode]);
            orgRpts.setOrganizationCode((String) ReturnList[sqlOrganizationCode]);
            orgRpts.setReportsToChartOfAccountsCode((String) ReturnList[sqlReportsToChartOfAccountsCode]);
            orgRpts.setReportsToOrganizationCode((String) ReturnList[sqlReportsToOrganizationCode]);
            orgRpts.setResponsibilityCenterCode((String) ReturnList[sqlResponsibilityCenterCode]);
            orgRpts.setVersionNumber(DEFAULT_VERSION_NUMBER);
            getPersistenceBrokerTemplate().store(orgRpts);
            organizationsAdded = organizationsAdded + 1;
        }
        LOG.info(String.format("\nOrganization reporting lines added to budget construction %d", organizationsAdded));
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
     *      level will be replace the former one in the header
     *   -- this process only affects accounts in the budget construction pending
     *      general ledger, and it is assumed that all updates to the PBGL have been
     *      finished when this process runs.       
     *  *********************************************************************************    
     */

    protected HashMap<String, BudgetConstructionAccountReports> acctRptsToMap = new HashMap<String, BudgetConstructionAccountReports>(1);
    protected HashMap<String, BudgetConstructionOrganizationReports> orgRptsToMap = new HashMap<String, BudgetConstructionOrganizationReports>(1);
    protected HashMap<String, BudgetConstructionAccountOrganizationHierarchy> acctOrgHierMap = new HashMap<String, BudgetConstructionAccountOrganizationHierarchy>(1);

    protected void organizationHierarchyCleanUp() {
        acctRptsToMap.clear();
        orgRptsToMap.clear();
        acctOrgHierMap.clear();
    }

    private BudgetConstructionHeader budgetConstructionHeader;
    //  these are the values at the root of the organization tree
    //  they report to themselves, and they are at the highest level of every 
    //  organization's reporting chain
    protected String rootChart;
    protected String rootOrganization;

    protected Integer nHeadersBackToZero = 0;
    protected Integer nHeadersSwitchingLevels = 0;


    /*
     *  rebuild the organization hierarchy based on changes to BC accounting and BC organization tables
     */

    public void rebuildOrganizationHierarchy(Integer BaseYear) {
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria killOrgHierQuery = new QueryByCriteria(BudgetConstructionAccountOrganizationHierarchy.class, criteriaID);
        killOrgHierQuery.setCriteria(criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(killOrgHierQuery);
        getPersistenceBrokerTemplate().clearCache();
        //
        // now we fetch the root of the organization tree
        String[] rootNode = SpringContext.getBean(OrganizationService.class).getRootOrganizationCode();
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        acctOrgHierMap = new HashMap<String, BudgetConstructionAccountOrganizationHierarchy>(hashObjectSize(BudgetConstructionAccountOrganizationHierarchy.class, criteriaID) * BCConstants.AVERAGE_REPORTING_TREE_SIZE);
        QueryByCriteria queryID = new QueryByCriteria(BudgetConstructionHeader.class, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (Results.hasNext()) {
            BudgetConstructionHeader extantBCHdr = (BudgetConstructionHeader) Results.next();
            buildAcctOrgHierFromAcctRpts(acctRptsToMap.get(getAcctRptsToKeyFromBCHdr(extantBCHdr)), RequestYear);
            updateBudgetConstructionHeaderAsNeeded(extantBCHdr);
        }
        organizationHierarchyCleanUp();
    }
    
    /*
     *  verify that all the accounts in the budget construction header table are in the budget construction accounting table as well.
     *
     *  genesis initially inserts all accounts in the chart of accounts table into the budget construction accounting table.
     *  after genesis, the budget construction accounting and organization tables must be maintained separately, to allow changes for the coming fiscal year to be made without changing the current account/organization structure.
     *  this means that accounts can come into budget construction via the general ledger or via payroll (CSF) which were created after genesis but for some reason not entered into the budget construction accounting tables.
     *  there is no good way for a program to decide why this happened and what to do about it.  (For instance, it could be that the payroll account is not supposed to exist in the coming year, and the account should not be
     *  in budget construction.  It could also be that some base budget GL was not transferred out of an account which is going away in the new fiscal year.  or, it could be that the chart manager missed something and the account
     *  should be in the budget construction accounting table.  A real live person needs to decide what happened and what to do.) so, check for this situation and print a log message if it occurs. 
     */
    
    public Map verifyAccountsAreAccessible(Integer requestFiscalYear)
    {
        HashMap<String,String[]> returnMap = new HashMap<String,String[]>();
        
        Criteria criteriaId = new Criteria();
        // allow more than one year in BC
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, requestFiscalYear);
        // query for the chart, account, and a field unique to the joined BC Account table
        String[] selectList = {KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                               KFSPropertyConstants.ACCOUNT_NUMBER,
                               BCPropertyConstants.BUDGET_CONSTRUCTION_ACCOUNT_REPORTS+"."+KFSPropertyConstants.REPORTS_TO_ORGANIZATION_CODE};
        ReportQueryByCriteria missingBCAccounts = new ReportQueryByCriteria(BudgetConstructionHeader.class,criteriaId);
        missingBCAccounts.setAttributes(selectList);
        // set up an outer join path to the BC Account table
        missingBCAccounts.setPathOuterJoin(BCPropertyConstants.BUDGET_CONSTRUCTION_ACCOUNT_REPORTS);
        //
        // look for a null value of the organization code to identify accounts not in Budget Construction Accounting
        Iterator <Object[]> returnedRows = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(missingBCAccounts);
        while (returnedRows.hasNext())
        {
            Object[] returnedFields = returnedRows.next();
            if (returnedFields[2] == null)
            {
                // store the missing row in the map
                String chart = (String) returnedFields[0];
                String account = (String) returnedFields[1];
                String[] chartAccount = {chart, account};
                returnMap.put(chart+account,chartAccount);
            }
        }
        
        return returnMap;
    }

    //  private utility methods

    protected void buildAcctOrgHierFromAcctRpts(BudgetConstructionAccountReports acctRpts, Integer RequestYear) {
        // it is possible that a header row could have an account which exists in the chart but not in account reports to, the parallel chart for budget construction.  these two charts must be maintained in parallel.  if that is the case, a verification routine will print a list of problems at the end.  here we just skip building the hierarchy.
        if (acctRpts == null)
        {
            return;
        }
        // part of the key of the budget construction header is a sub account
        // so, our algorithm could visit the same account more than once if the account has more than one subaccount.
        // if the hierarchy for this account is already built, we skip this routine.
        String inKey = getOrgHierarchyKeyFromAcctRpts(acctRpts);
        if (acctOrgHierMap.get(inKey) != null) {
            return;
        }
        Integer orgLevel = 1;
        // the organization the account directly reports to is at level 1
        // (the account starts out at the account fiscal office level--level 0) 
        BudgetConstructionAccountOrganizationHierarchy acctOrgHier;
        acctOrgHier = new BudgetConstructionAccountOrganizationHierarchy();
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
        acctOrgHierMap.put(mapKey, acctOrgHier);
        // now we have to loop to assign the hierarchy
        // (especially before testing, we need to be on the look out for infinite
        //  loops.  assertions are verboten, so we'll just code a high value for
        //  the level limit, instead of using a potentially infinite while loop.  we have no control over the DB, and there could be a circular reporting loop in the DB.)
        while (orgLevel < MAXIMUM_ORGANIZATION_TREE_DEPTH) {
            // find the current organization in the BC organization reports to table
            String orgKey = getOrgRptsToKeyFromAcctOrgHier(acctOrgHier);
            if (noNewMapEntryNeeded(orgRptsToMap.get(orgKey))) {
                // get out if we have found the root of the reporting tree
                break;
            }
            orgLevel = orgLevel + 1;
            BudgetConstructionOrganizationReports orgRpts = orgRptsToMap.get(orgKey);
            acctOrgHier = new BudgetConstructionAccountOrganizationHierarchy();
            acctOrgHier.setUniversityFiscalYear(RequestYear);
            acctOrgHier.setChartOfAccountsCode(acctRpts.getChartOfAccountsCode());
            acctOrgHier.setAccountNumber(acctRpts.getAccountNumber());
            acctOrgHier.setOrganizationLevelCode(orgLevel);
            acctOrgHier.setVersionNumber(DEFAULT_VERSION_NUMBER);
            acctOrgHier.setOrganizationChartOfAccountsCode(orgRpts.getReportsToChartOfAccountsCode());
            acctOrgHier.setOrganizationCode(orgRpts.getReportsToOrganizationCode());
            // save the new row
            getPersistenceBrokerTemplate().store(acctOrgHier);
            // save the new row in a hash map so we can merge with the budget header
            mapKey = getOrgHierarchyKey(acctOrgHier);
            acctOrgHierMap.put(mapKey, acctOrgHier);
        }
        if (orgLevel >= MAXIMUM_ORGANIZATION_TREE_DEPTH) {
            LOG.warn(String.format("\n%s/%s reports to more than %d organizations", acctRpts.getChartOfAccountsCode(), acctRpts.getAccountNumber(), MAXIMUM_ORGANIZATION_TREE_DEPTH));
        }
    }

    protected String getAcctRptsToKey(BudgetConstructionAccountReports acctRpts) {
        String TestKey = new String();
        TestKey = acctRpts.getChartOfAccountsCode() + acctRpts.getAccountNumber();
        return TestKey;
    }

    protected String getAcctRptsToKeyFromBCHdr(BudgetConstructionHeader bCHdr) {
        String TestKey = new String();
        TestKey = bCHdr.getChartOfAccountsCode() + bCHdr.getAccountNumber();
        return TestKey;
    }

    protected String getOrgHierarchyKey(BudgetConstructionAccountOrganizationHierarchy orgHier) {
        String TestKey = new String();
        TestKey = orgHier.getChartOfAccountsCode() + orgHier.getAccountNumber() + orgHier.getOrganizationChartOfAccountsCode() + orgHier.getOrganizationCode();
        return TestKey;
    }

    protected String getOrgHierarchyKeyFromAcctRpts(BudgetConstructionAccountReports acctRpts) {
        String TestKey = new String();
        TestKey = acctRpts.getChartOfAccountsCode() + acctRpts.getAccountNumber() + acctRpts.getReportsToChartOfAccountsCode() + acctRpts.getReportsToOrganizationCode();
        return TestKey;
    }

    protected String getOrgHierarchyKeyFromBCHeader(BudgetConstructionHeader bCHdr) {
        String TestKey = new String();
        TestKey = bCHdr.getChartOfAccountsCode() + bCHdr.getAccountNumber() + bCHdr.getOrganizationLevelChartOfAccountsCode() + bCHdr.getOrganizationLevelOrganizationCode();
        return TestKey;
    }

    protected String getOrgRptsToKey(BudgetConstructionOrganizationReports orgRpts) {
        String TestKey = new String();
        TestKey = orgRpts.getChartOfAccountsCode() + orgRpts.getOrganizationCode();
        return TestKey;
    }

    protected String getOrgRptsToKeyFromAcctOrgHier(BudgetConstructionAccountOrganizationHierarchy acctOrgHier) {
        String TestKey = new String();
        TestKey = acctOrgHier.getOrganizationChartOfAccountsCode() + acctOrgHier.getOrganizationCode();
        return TestKey;
    }

    protected boolean noNewMapEntryNeeded(BudgetConstructionOrganizationReports orgRpts) {
        // no new entry is needed we are at the root of the organization tree
        String thisChart = orgRpts.getChartOfAccountsCode();
        String thisOrg = orgRpts.getOrganizationCode();
        if ((thisChart.compareTo(rootChart) == 0) && (thisOrg.compareTo(rootOrganization) == 0)) {
            return true;
        }
        // no new entry is needed if either the chart or the organization 
        // which this organization reports to is null
        // or if the organization reports to itself
        // this check is here in case the chart/org reporting hierarchy is not set up properly in the DB, and some organizations do not 
        // ultimately report to a single root of the organization tree.
        String rptsToChart = orgRpts.getReportsToChartOfAccountsCode();
        if (rptsToChart.length() == 0) {
            LOG.warn(String.format("\n(%s, %s) reports to a null chart", thisChart, thisOrg));
            return true;
        }
        String rptsToOrg = orgRpts.getReportsToOrganizationCode();
        if (rptsToOrg.length() == 0) {
            LOG.warn(String.format("\n(%s, %s) reports to a null organization", thisChart, thisOrg));
            return true;
        }
        if ((thisChart.compareTo(rptsToChart) == 0) && (thisOrg.compareTo(rptsToOrg) == 0)) {
            LOG.warn(String.format("\n(%s,%s) reports to itself and is not the root", thisChart, thisOrg));
            return true;
        }
        return false;
    }

    protected void readAcctReportsTo() {
        // we will use a report query, to bypass the "persistence" bureaucracy and its cache, saving memory and time
        // we will use the OJB class as a convenient container object in the hashmap
        Integer sqlChartOfAccountsCode = 0;
        Integer sqlAccountNumber = 1;
        Integer sqlReportsToChartofAccountsCode = 2;
        Integer sqlOrganizationCode = 3;
        Criteria criteriaID = ReportQueryByCriteria.CRITERIA_SELECT_ALL;
        // we always get a new copy of the map
        acctRptsToMap = new HashMap<String, BudgetConstructionAccountReports>(hashObjectSize(BudgetConstructionAccountReports.class, criteriaID));
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.REPORTS_TO_ORGANIZATION_CODE };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(BudgetConstructionAccountReports.class, queryAttr, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (Results.hasNext()) {
            Object[] ReturnList = (Object[]) Results.next();
            BudgetConstructionAccountReports acctRpts = new BudgetConstructionAccountReports();
            acctRpts.setChartOfAccountsCode((String) ReturnList[sqlChartOfAccountsCode]);
            acctRpts.setAccountNumber((String) ReturnList[sqlAccountNumber]);
            acctRpts.setReportsToChartOfAccountsCode((String) ReturnList[sqlReportsToChartofAccountsCode]);
            acctRpts.setReportsToOrganizationCode((String) ReturnList[sqlOrganizationCode]);
            String TestKey = getAcctRptsToKey(acctRpts);
            acctRptsToMap.put(TestKey, acctRpts);
        }
        LOG.info("\nAccount Reports To for Organization Hierarchy:");
        LOG.info(String.format("\nNumber of account-reports-to rows: %d", acctRptsToMap.size()));
    }

    protected void readOrgReportsTo() {
        // we will use a report query, to bypass the "persistence" bureaucracy and its cache, saving memory and time
        // we will use the OJB class as a convenient container object in the hashmap
        Integer sqlChartOfAccountsCode = 0;
        Integer sqlOrganizationCode = 1;
        Integer sqlReportsToChartofAccountsCode = 2;
        Integer sqlReportsToOrganizationCode = 3;
        Criteria criteriaID = ReportQueryByCriteria.CRITERIA_SELECT_ALL;
        // build a new map
        orgRptsToMap = new HashMap<String, BudgetConstructionOrganizationReports>(hashObjectSize(BudgetConstructionOrganizationReports.class, criteriaID));
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ORGANIZATION_CODE, KFSPropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.REPORTS_TO_ORGANIZATION_CODE };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(BudgetConstructionOrganizationReports.class, queryAttr, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (Results.hasNext()) {
            Object[] ReturnList = (Object[]) Results.next();
            BudgetConstructionOrganizationReports orgRpts = new BudgetConstructionOrganizationReports();
            orgRpts.setChartOfAccountsCode((String) ReturnList[sqlChartOfAccountsCode]);
            orgRpts.setOrganizationCode((String) ReturnList[sqlOrganizationCode]);
            orgRpts.setReportsToChartOfAccountsCode((String) ReturnList[sqlReportsToChartofAccountsCode]);
            orgRpts.setReportsToOrganizationCode((String) ReturnList[sqlReportsToOrganizationCode]);
            String TestKey = getOrgRptsToKey(orgRpts);
            orgRptsToMap.put(TestKey, orgRpts);
        }
        LOG.info("\nOrganization Reports To for Organization Hierarchy:");
        LOG.info(String.format("\nNumber of organization-reports-to rows: %d", orgRptsToMap.size()));
    }

    protected void updateBudgetConstructionHeaderAsNeeded(BudgetConstructionHeader bCHdr) {
        // header rows at the lowest (initial) level should be left alone
        if (bCHdr.getOrganizationLevelCode().equals(BCConstants.INITIAL_ORGANIZATION_LEVEL_CODE)) {
            return;
        }
        // we will only update if the level of the organization has changed 
        // or if the organization has disappeared completely 
        String mapKey = getOrgHierarchyKeyFromBCHeader(bCHdr);
        BudgetConstructionAccountOrganizationHierarchy acctOrgHier = acctOrgHierMap.get(mapKey);
        if (acctOrgHier == null) {
            // the account no longer reports to this organization
            // we have to return to the lowest level and the default the
            // organization reported to
            nHeadersBackToZero = nHeadersBackToZero + 1;
            bCHdr.setOrganizationLevelChartOfAccountsCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE);
            bCHdr.setOrganizationLevelOrganizationCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE);
            bCHdr.setOrganizationLevelCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_CODE);
            getPersistenceBrokerTemplate().store(bCHdr);
            return;
        }
        Integer levelFromHierarchy = acctOrgHier.getOrganizationLevelCode();
        Integer levelFromHeader = bCHdr.getOrganizationLevelCode();
        if (!levelFromHierarchy.equals(levelFromHeader)) {
            // the organization reported to has changed its location in the hierarchy
            bCHdr.setOrganizationLevelCode(levelFromHierarchy);
            getPersistenceBrokerTemplate().store(bCHdr);
            nHeadersSwitchingLevels = nHeadersSwitchingLevels + 1;
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
    protected HashMap<String, PendingBudgetConstructionGeneralLedger> pBGLFromGL = new HashMap<String, PendingBudgetConstructionGeneralLedger>(1);
    protected HashMap<String, String> documentNumberFromBCHdr = new HashMap<String, String>(1);
    protected HashMap<String, Integer> skippedPBGLKeys = new HashMap();

    protected void pBGLCleanUp() {
        pBGLFromGL.clear();
        documentNumberFromBCHdr.clear();
    }

    // these are the indexes for each of the fields returned in the select list
    // of the SQL statement
    protected Integer sqlChartOfAccountsCode = 0;
    protected Integer sqlAccountNumber = 1;
    protected Integer sqlSubAccountNumber = 2;
    protected Integer sqlObjectCode = 3;
    protected Integer sqlSubObjectCode = 4;
    protected Integer sqlBalanceTypeCode = 5;
    protected Integer sqlObjectTypeCode = 6;
    protected Integer sqlAccountLineAnnualBalanceAmount = 7;
    protected Integer sqlBeginningBalanceLineAmount = 8;

    protected Integer nGLHeadersAdded = new Integer(0);
    protected Integer nGLRowsAdded = new Integer(0);
    protected Integer nGLRowsUpdated = new Integer(0);
    protected Integer nCurrentPBGLRows = new Integer(0);
    protected Integer nGLBBRowsZeroNet = new Integer(0);
    protected Integer nGLBBRowsRead = new Integer(0);
    protected Integer nGLRowsMatchingPBGL = new Integer(0);
    protected Integer nGLBBKeysRead = new Integer(0);
    protected Integer nGLBBRowsSkipped = new Integer(0);

    // public methods

    public void clearHangingBCLocks(Integer BaseYear) {
        // this routine cleans out any locks that might remain from people leaving
        // the application abnormally (for example, Fire! Fire! Leave your computer and get out now!).  it assumes that
        // people are shut out of the application during a batch run, and that all
        // work prior to the batch run has either been committed, or lost because the connection was broken before a save.
        BudgetConstructionHeader lockedDocuments;
        //
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        Criteria lockID = new Criteria();
        Criteria tranLockID = new Criteria();
        if (BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS == null) {
            //  make sure that a NULL test is used in case = NULL is not supported
            //  by the database
            lockID.addNotNull(BCPropertyConstants.BUDGET_LOCK_USER_IDENTIFIER);
            tranLockID.addNotNull(BCPropertyConstants.BUDGET_TRANSACTION_LOCK_USER_IDENTIFIER);
        }
        else {
            lockID.addNotEqualTo(BCPropertyConstants.BUDGET_LOCK_USER_IDENTIFIER, BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            tranLockID.addNotEqualTo(BCPropertyConstants.BUDGET_TRANSACTION_LOCK_USER_IDENTIFIER, BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        }
        ;
        lockID.addOrCriteria(tranLockID);
        criteriaID.addAndCriteria(lockID);
        //
        QueryByCriteria queryID = new QueryByCriteria(BudgetConstructionHeader.class, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        //  now just loop through and change the locks
        while (Results.hasNext()) {
            lockedDocuments = (BudgetConstructionHeader) Results.next();
            lockedDocuments.setBudgetLockUserIdentifier(BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            lockedDocuments.setBudgetTransactionLockUserIdentifier(BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            getPersistenceBrokerTemplate().store(lockedDocuments);
        }
        //   we need to clear the position and funding locks as well
        clearHangingPositionLocks(RequestYear);
        QueryByCriteria queryId = new QueryByCriteria(BudgetConstructionFundingLock.class, QueryByCriteria.CRITERIA_SELECT_ALL);
        getPersistenceBrokerTemplate().deleteByQuery(queryId);
        getPersistenceBrokerTemplate().clearCache();
    }

    public void initialLoadToPBGL(Integer BaseYear) {
        readBCHeaderForDocNumber(BaseYear);
        // exclude rows with a new GL balance of 0 from the initial pending GL
        readGLForPBGL(BaseYear,true);
        addNewGLRowsToPBGL(BaseYear);
        writeFinalDiagnosticCounts();
        pBGLCleanUp();
    }

    public void updateToPBGL(Integer BaseYear) {
        readBCHeaderForDocNumber(BaseYear);
        // allow rows with a net GL balance of 0 to update the pending GL
        readGLForPBGL(BaseYear,false);
        updateCurrentPBGL(BaseYear);
        addNewGLRowsToPBGL(BaseYear);
        writeFinalDiagnosticCounts();
        pBGLCleanUp();
    }
    
    //
    //  clear any hanging position locks
    protected void clearHangingPositionLocks(Integer RequestYear)
    {
        BudgetConstructionPosition lockedPositions;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        Criteria lockID = new Criteria();
        if (BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS == null) {
            //  make sure that a NULL test is used in case = NULL is not supported
            //  by the database
            lockID.addNotNull(BCPropertyConstants.POSITION_LOCK_USER_IDENTIFIER);
        }
        else {
            lockID.addNotEqualTo(BCPropertyConstants.POSITION_LOCK_USER_IDENTIFIER, BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        }
        ;
        criteriaID.addAndCriteria(lockID);
        //
        QueryByCriteria queryID = new QueryByCriteria(BudgetConstructionPosition.class, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        //  now just loop through and change the locks
        while (Results.hasNext()) {
            lockedPositions = (BudgetConstructionPosition) Results.next();
            lockedPositions.setPositionLockUserIdentifier(BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
            getPersistenceBrokerTemplate().store(lockedPositions);
        }
 }

    //
    //  two test routines to display the field values in the two business objects
    //  produced from the GL read.  these are primarily here for initial testing
    protected void info() {
        if (!LOG.isEnabledFor(Level.INFO)) {
            return;
        }
        ;
        //  print one header row   
        for (Map.Entry<String, String> bcHeaderRows : documentNumberFromBCHdr.entrySet()) {
            String toPrint = bcHeaderRows.getValue();
            LOG.info(String.format("\n\nA sample document number %s\n", toPrint));
            break;
        }
        // print one PBGL row
        for (Map.Entry<String, PendingBudgetConstructionGeneralLedger> pBGLRows : pBGLFromGL.entrySet()) {
            PendingBudgetConstructionGeneralLedger toPrint = pBGLRows.getValue();
            LOG.info("\n\nA sample PBGL row\n");
            LOG.info(String.format("\nDocument Number = %s", toPrint.getDocumentNumber()));
            LOG.info(String.format("\nUniversity Fiscal Year = %d", toPrint.getUniversityFiscalYear()));
            LOG.info(String.format("\nChart: %s", toPrint.getChartOfAccountsCode()));
            LOG.info(String.format("\nAccount: %s", toPrint.getAccountNumber()));
            LOG.info(String.format("\nSub Account: %s", toPrint.getSubAccountNumber()));
            LOG.info(String.format("\nObject Code: %s", toPrint.getFinancialObjectCode()));
            LOG.info(String.format("\nSubobject Code: %s", toPrint.getFinancialSubObjectCode()));
            LOG.info(String.format("\nBalance Type: %s", toPrint.getFinancialBalanceTypeCode()));
            LOG.info(String.format("\nObject Type: %s", toPrint.getFinancialObjectTypeCode()));
            LOG.info(String.format("\nBase Amount: %s", toPrint.getFinancialBeginningBalanceLineAmount().toString()));
            LOG.info(String.format("\nRequest Amount: %s", toPrint.getAccountLineAnnualBalanceAmount().toString()));
            LOG.info(String.format("\nVersion Number: %d", toPrint.getVersionNumber()));
            break;
        }
    }

    protected void debug() {
        if (!LOG.isEnabledFor(Level.DEBUG)) {
            return;
        }
        ;
        //  print one header row    
        for (Map.Entry<String, String> bcHeaderRows : documentNumberFromBCHdr.entrySet()) {
            String toPrint = bcHeaderRows.getValue();
            LOG.debug(String.format("\n\nA sample document number %s\n", toPrint));
            break;
        }
        // print one PBGL row
        for (Map.Entry<String, PendingBudgetConstructionGeneralLedger> pBGLRows : pBGLFromGL.entrySet()) {
            PendingBudgetConstructionGeneralLedger toPrint = pBGLRows.getValue();
            LOG.debug("\n\nA sample PBGL row\n");
            LOG.debug(String.format("\nDocument Number = %s", toPrint.getDocumentNumber()));
            LOG.debug(String.format("\nUniversity Fiscal Year = %d", toPrint.getUniversityFiscalYear()));
            LOG.debug(String.format("\nChart: %s", toPrint.getChartOfAccountsCode()));
            LOG.debug(String.format("\nAccount: %s", toPrint.getAccountNumber()));
            LOG.debug(String.format("\nSub Account: %s", toPrint.getSubAccountNumber()));
            LOG.debug(String.format("\nObject Code: %s", toPrint.getFinancialObjectCode()));
            LOG.debug(String.format("\nSubobject Code: %s", toPrint.getFinancialSubObjectCode()));
            LOG.debug(String.format("\nBalance Type: %s", toPrint.getFinancialBalanceTypeCode()));
            LOG.debug(String.format("\nObject Type: %s", toPrint.getFinancialObjectTypeCode()));
            LOG.debug(String.format("\nBase Amount: %s", toPrint.getFinancialBeginningBalanceLineAmount().toString()));
            LOG.debug(String.format("\nRequest Amount: %s", toPrint.getAccountLineAnnualBalanceAmount().toString()));
            LOG.debug(String.format("\nVersion Number: %d", toPrint.getVersionNumber()));
            break;
        }
    }

    //
    //
    // private working methods

    //
    protected void addNewGLRowsToPBGL(Integer BaseYear) {
        // this method adds the GL rows not yet in PBGL to PBGL
        for (Map.Entry<String, PendingBudgetConstructionGeneralLedger> newPBGLRows : pBGLFromGL.entrySet()) {
            PendingBudgetConstructionGeneralLedger rowToAdd = newPBGLRows.getValue();
            // no rows with zero base are added to the budget construction pending general ledger
            if (rowToAdd.getFinancialBeginningBalanceLineAmount().isZero())
            {
              nGLBBRowsZeroNet = nGLBBRowsZeroNet + 1;
            }
            else
            {
              nGLRowsAdded = nGLRowsAdded + 1;
              getPersistenceBrokerTemplate().store(rowToAdd);
            }
        }
    }

    //
    // these two methods build the GL field string that triggers creation of a new
    // pending budget construction general ledger row
    protected String buildGLTestKeyFromPBGL(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        String PBGLTestKey = new String();
        PBGLTestKey = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode() + pendingBudgetConstructionGeneralLedger.getAccountNumber() + pendingBudgetConstructionGeneralLedger.getSubAccountNumber() + pendingBudgetConstructionGeneralLedger.getFinancialObjectCode() + pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode() + pendingBudgetConstructionGeneralLedger.getFinancialBalanceTypeCode() + pendingBudgetConstructionGeneralLedger.getFinancialObjectTypeCode();
        return PBGLTestKey;
    }

    protected String buildGLTestKeyFromSQLResults(Object[] sqlResult) {
        String GLTestKey = new String();
        GLTestKey = (String) sqlResult[sqlChartOfAccountsCode] + (String) sqlResult[sqlAccountNumber] + (String) sqlResult[sqlSubAccountNumber] + (String) sqlResult[sqlObjectCode] + (String) sqlResult[sqlSubObjectCode] + (String) sqlResult[sqlBalanceTypeCode] + (String) sqlResult[sqlObjectTypeCode];
        return GLTestKey;
    }

    //
    // these two methods build the GL field string that triggers creation of a new
    // budget construction header
    public String buildHeaderTestKeyFromPBGL(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        String headerBCTestKey = new String();
        headerBCTestKey = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode() + pendingBudgetConstructionGeneralLedger.getAccountNumber() + pendingBudgetConstructionGeneralLedger.getSubAccountNumber();
        return headerBCTestKey;
    }

    protected String buildHeaderTestKeyFromSQLResults(Object[] sqlResult) {
        String headerBCTestKey = new String();
        headerBCTestKey = (String) sqlResult[sqlChartOfAccountsCode] + (String) sqlResult[sqlAccountNumber] + (String) sqlResult[sqlSubAccountNumber];
        return headerBCTestKey;
    }

    protected PendingBudgetConstructionGeneralLedger newPBGLBusinessObject(Integer RequestYear, Object[] sqlResult) {
        PendingBudgetConstructionGeneralLedger PBGLObj = new PendingBudgetConstructionGeneralLedger();
        /*  
         * the document number will be set later if we have to store this in a new document
         * a new row in an existing document will take it's document number from the existing document
         * otherwise (existing document, existing row), the only field in this that will be different from
         * the existing row is the beginning balance amount
         */
        PBGLObj.setUniversityFiscalYear(RequestYear);
        PBGLObj.setChartOfAccountsCode((String) sqlResult[sqlChartOfAccountsCode]);
        PBGLObj.setAccountNumber((String) sqlResult[sqlAccountNumber]);
        PBGLObj.setSubAccountNumber((String) sqlResult[sqlSubAccountNumber]);
        PBGLObj.setFinancialObjectCode((String) sqlResult[sqlObjectCode]);
        PBGLObj.setFinancialSubObjectCode((String) sqlResult[sqlSubObjectCode]);
        PBGLObj.setFinancialBalanceTypeCode((String) sqlResult[sqlBalanceTypeCode]);
        PBGLObj.setFinancialObjectTypeCode((String) sqlResult[sqlObjectTypeCode]);
        KualiDecimal BaseAmount = (KualiDecimal) sqlResult[sqlBeginningBalanceLineAmount];
        BaseAmount = BaseAmount.add((KualiDecimal) sqlResult[sqlAccountLineAnnualBalanceAmount]);
        KualiInteger DollarBaseAmount = new KualiInteger(BaseAmount.bigDecimalValue());
        PBGLObj.setFinancialBeginningBalanceLineAmount(DollarBaseAmount);
        PBGLObj.setAccountLineAnnualBalanceAmount(KualiInteger.ZERO);
        //  ObjectID is set in the BusinessObjectBase on insert and update
        //  but, we must set the version number
        PBGLObj.setVersionNumber(DEFAULT_VERSION_NUMBER);
        return PBGLObj;
    }

    protected void readBCHeaderForDocNumber(Integer BaseYear) {
        //  we have to read all the budget construction header objects so that
        //  we can use them to assign document numbers
        //
        Integer RequestYear = BaseYear + 1;
        //
        Long documentsRead = new Long(0);
        Criteria criteriaId = new Criteria();
        criteriaId.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        documentNumberFromBCHdr = new HashMap<String, String>(hashObjectSize(BudgetConstructionHeader.class, criteriaId));
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, KFSPropertyConstants.DOCUMENT_NUMBER };
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(BudgetConstructionHeader.class, queryAttr, criteriaId);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryId);
        while (Results.hasNext()) {
            Object[] rowReturned = (Object[]) Results.next();
            String hashKey = ((String) rowReturned[0]) + ((String) rowReturned[1]) + ((String) rowReturned[2]);
            documentNumberFromBCHdr.put(hashKey, ((String) rowReturned[3]));
            documentsRead = documentsRead + 1;
        }
        LOG.info(String.format("\nBC Headers read = %d", documentsRead));
    }

    protected void readGLForPBGL(Integer BaseYear, boolean excludeZeroNetAmounts) {
        Integer RequestYear = BaseYear + 1;
        //
        //  set up a report query to fetch all the GL rows we are going to need
        Criteria criteriaID = new Criteria();
        // we only pick up a single balance type
        // we also use an integer fiscal year
        // *** this is a point of change if either of these criteria change ***
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        //  we'll estimate the size of the PBGL map from the number of
        //  base budget rows in the GL.  this should be close
        pBGLFromGL = new HashMap<String, PendingBudgetConstructionGeneralLedger>(hashObjectSize(Balance.class, criteriaID));
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, KFSPropertyConstants.OBJECT_CODE, KFSPropertyConstants.SUB_OBJECT_CODE, KFSPropertyConstants.BALANCE_TYPE_CODE, KFSPropertyConstants.OBJECT_TYPE_CODE, KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, KFSPropertyConstants.BEGINNING_BALANCE_LINE_AMOUNT };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(Balance.class, queryAttr, criteriaID, true);
        //
        // set up the hashmaps by iterating through the results
        if (LOG.isDebugEnabled()) {
            LOG.debug("\nGL Query started: " + String.format("%tT", dateTimeService.getCurrentDate()));
        }
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (LOG.isDebugEnabled()) {
            LOG.debug("\nGL Query finished: " + String.format("%tT", dateTimeService.getCurrentDate()));
        }
        while (Results.hasNext()) {
            Object[] ReturnList = (Object[]) Results.next();
            LOG.debug(String.format("\nfields returned = %d\n", ReturnList.length));
            LOG.debug(String.format("\nvalue in last field = %s\n", ReturnList[sqlBeginningBalanceLineAmount].toString()));
            //
            // we never load a new pending budget construction row from a general ledger row which has a net zero balance
            // but we allow for the fact that the general ledger counterpart of a pending row already loaded could be netted to zero by a budget transfer. in this case, we need to update the pending amount.
            KualiDecimal BaseAmount = (KualiDecimal) ReturnList[sqlBeginningBalanceLineAmount];
            BaseAmount = BaseAmount.add((KualiDecimal) ReturnList[sqlAccountLineAnnualBalanceAmount]);
            // even when we are updating, it could be that the entire key is not in the pending general ledger because the amounts in all GL rows for that key have always netted to zero.
            // in this case, there will be no document number, and we should skip the row.
            String HeaderTestKey = buildHeaderTestKeyFromSQLResults(ReturnList);
            String documentNumberForKey = documentNumberFromBCHdr.get(HeaderTestKey);
            if (documentNumberForKey == null)
            {
                if (BaseAmount.isZero())
                {
                  // keys in which all rows have base amounts of zero need not have a key   
                  nGLBBRowsRead = nGLBBRowsRead + 1;
                  nGLBBRowsZeroNet = nGLBBRowsZeroNet + 1;
                }
                else
                {
                    // the amounts are *not* zero--the row should *not* be skipped, and we need to log an error
                    recordSkippedKeys(HeaderTestKey);
                }
                continue;
            }
            if ((excludeZeroNetAmounts) && BaseAmount.isZero())
            {    
              //  exclude any rows where the amounts add to 0
              //  (we don't do it in the WHERE clause to be certain we are ANSI standard)
                  nGLBBRowsRead = nGLBBRowsRead + 1;
                  nGLBBRowsZeroNet = nGLBBRowsZeroNet + 1;
                  continue;
            }
            //  
            //  we always need to build a new PGBL object
            //  we have selected the entire key from GL_BALANCE_T
            String GLTestKey = buildGLTestKeyFromSQLResults(ReturnList);
            pBGLFromGL.put(GLTestKey, newPBGLBusinessObject(RequestYear, ReturnList));
            //  we need to add a document number to the PBGL object
            pBGLFromGL.get(GLTestKey).setDocumentNumber(documentNumberForKey);
        }
        LOG.info("\nHash maps built: " + String.format("%tT", dateTimeService.getCurrentDate()));
        info();
        nGLBBKeysRead = documentNumberFromBCHdr.size();
        nGLBBRowsRead = pBGLFromGL.size() + nGLBBRowsRead;
    }

    protected void recordSkippedKeys(String badGLKey) {
        nGLBBRowsSkipped = nGLBBRowsSkipped + 1;
        if (skippedPBGLKeys.get(badGLKey) == null) {
            skippedPBGLKeys.put(badGLKey, new Integer(1));
        }
        else {
            Integer rowCount = skippedPBGLKeys.get(badGLKey) + 1;
            skippedPBGLKeys.put(badGLKey, rowCount);
        }
    }

    protected void updateBaseBudgetAmount(PendingBudgetConstructionGeneralLedger currentPBGLInstance) {
        String TestKey = buildGLTestKeyFromPBGL(currentPBGLInstance);
        if (!pBGLFromGL.containsKey(TestKey)) {
            return;
        }
        PendingBudgetConstructionGeneralLedger matchFromGL = pBGLFromGL.get(TestKey);
        KualiInteger baseFromCurrentGL = matchFromGL.getFinancialBeginningBalanceLineAmount();
        KualiInteger baseFromPBGL = currentPBGLInstance.getFinancialBeginningBalanceLineAmount();
        // remove the candidate GL from the hash list
        // it won't match with anything else
        // it should NOT be inserted into the PBGL table
        pBGLFromGL.remove(TestKey);
        if (baseFromCurrentGL.equals(baseFromPBGL)) {
            // no need to update--false alarm
            nGLRowsMatchingPBGL = nGLRowsMatchingPBGL+1;
            return;
        }
        // update the base amount and store the updated PBGL row
        nGLRowsUpdated = nGLRowsUpdated + 1;
        currentPBGLInstance.setFinancialBeginningBalanceLineAmount(baseFromCurrentGL);
        getPersistenceBrokerTemplate().store(currentPBGLInstance);
    }

    protected void updateCurrentPBGL(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;


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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class, criteriaID);
        Iterator Results = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        //  loop through the results
        while (Results.hasNext()) {
            nCurrentPBGLRows = nCurrentPBGLRows + 1;
            PendingBudgetConstructionGeneralLedger currentPBGLInstance = (PendingBudgetConstructionGeneralLedger) Results.next();
            // update the base amount and store the result if necessary
            updateBaseBudgetAmount(currentPBGLInstance);
        }
    }

    protected void writeFinalDiagnosticCounts() {
        LOG.info(String.format("\n\nGeneral Ledger Run Statistics\n\n"));
        LOG.info(String.format("\nGeneral Ledger BB Keys read: %d", nGLBBKeysRead));
        LOG.info(String.format("\nGeneral Ledger BB Rows read: %d", nGLBBRowsRead));
        LOG.info(String.format("\nExisting Pending General Ledger rows: %d", nCurrentPBGLRows));
        LOG.info(String.format("\nof these..."));
        LOG.info(String.format("\nnew PBGL rows written: %d", nGLRowsAdded));
        LOG.info(String.format("\ncurrent PBGL amounts updated: %d", nGLRowsUpdated));
        LOG.info(String.format("\ncurrent PBGL rows already matching a GL row: %d",nGLRowsMatchingPBGL));
        LOG.info(String.format("\nGL rows with zero net amounts (skipped) %d\n", nGLBBRowsZeroNet));
        LOG.info(String.format("\nGL account/subaccount keys skipped: %d", nGLBBRowsSkipped));
        if (!skippedPBGLKeys.isEmpty()) {
            for (Map.Entry<String, Integer> skippedRows : skippedPBGLKeys.entrySet()) {
                LOG.info(String.format("\nGL key %s with %d rows skipped--no document header", skippedRows.getKey(), skippedRows.getValue()));

            }
        }
        LOG.info(String.format("\n\nend of General Ledger run statics"));
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

    protected HashMap<String, String[]> baseYearInactiveObjects = new HashMap<String, String[]>(1);
    protected HashMap<String, String[]> gLBBObjects = new HashMap<String, String[]>(1);
    protected Integer nInactiveBBObjectCodes = new Integer(0);

    protected void objectClassRICleanUp() {
        baseYearInactiveObjects.clear();
        gLBBObjects.clear();
    }

    public void ensureObjectClassRIForBudget(Integer BaseYear) {
        readBaseYearInactiveObjects(BaseYear);
        if (baseYearInactiveObjects.isEmpty()) {
            // no problems
            LOG.info(String.format("\nInactive Object Codes in BC GL: %d", nInactiveBBObjectCodes));
            return;
        }
        readAndFilterGLBBObjects(BaseYear);
        if (gLBBObjects.isEmpty()) {
            // no problems
            LOG.info(String.format("\nInactive Object Codes in BC GL: %d", nInactiveBBObjectCodes));
            return;
        }
        // we have to create an object row for the request year
        addRIObjectClassesForBB(BaseYear);
        LOG.info(String.format("\nInactive Object Codes in BC GL: %d", nInactiveBBObjectCodes));
        objectClassRICleanUp();
    }

    protected void addRIObjectClassesForBB(Integer BaseYear) {
        //  we will read the object table for the request year first
        //  if the row is there (someone could have added it, or updated it),
        //  we will not change it at all.
        //  this is an extra read, but overall looking just for problems
        //  will require many fewer reads than comparing all object codes in the
        //  request year to all object codes in the GL BB base.
        Integer RequestYear = BaseYear + 1;
        for (Map.Entry<String, String[]> problemObjectCodes : gLBBObjects.entrySet()) {
            String problemChart = problemObjectCodes.getValue()[0];
            String problemObject = problemObjectCodes.getValue()[1];
            if (isObjectInRequestYear(BaseYear, problemChart, problemObject)) {
                // everything is fine
                continue;
            }
            //  now we have to add the object to the request year as an inactive object
            Criteria criteriaID = new Criteria();
            criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
            criteriaID.addColumnEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, problemChart);
            criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, problemObject);
            ReportQueryByCriteria queryID = new ReportQueryByCriteria(ObjectCode.class, criteriaID);
            Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
            if (!Results.hasNext()) {
                // this should never happen
                // if it does, it will cause an RI exception in the GL load to BC
                // at least this message will give some clue
                LOG.warn(String.format("could not find BB object (%s, %s) in %d", problemChart, problemObject, BaseYear));
                continue;
            }
            ObjectCode baseYearObject = (ObjectCode) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(Results);
            baseYearObject.setUniversityFiscalYear(RequestYear);
            baseYearObject.setActive(false);
            getPersistenceBrokerTemplate().store(baseYearObject);
        }
    }

    protected boolean isObjectInRequestYear(Integer BaseYear, String Chart, String ObjectCode) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        criteriaID.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, Chart);
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, ObjectCode);
        QueryByCriteria queryID = new QueryByCriteria(ObjectCode.class, criteriaID);
        Integer result = getPersistenceBrokerTemplate().getCount(queryID);
        return (!result.equals(0));
    }

    protected void readBaseYearInactiveObjects(Integer BaseYear) {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.ACTIVE, false);
        baseYearInactiveObjects = new HashMap<String, String[]>(hashObjectSize(ObjectCode.class, criteriaID));
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_CODE };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(ObjectCode.class, queryAttr, criteriaID);
        Iterator result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (result.hasNext()) {
            Object[] resultRow = (Object[]) result.next();
            String[] hashMapValue = new String[2];
            hashMapValue[0] = (String) resultRow[0];
            hashMapValue[1] = (String) resultRow[1];
            String hashMapKey = hashMapValue[0] + hashMapValue[1];
            baseYearInactiveObjects.put(hashMapKey, hashMapValue);
        }
    }

    protected void readAndFilterGLBBObjects(Integer BaseYear) {
        // this must be done before we read GL for PBGL
        // otherwise, we will get an RI violation when we try to add a PBGL
        // row with an object inactive in the current year
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        gLBBObjects = new HashMap<String, String[]>(hashObjectSize(Balance.class, criteriaID));
        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.OBJECT_CODE };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(Balance.class, queryAttr, criteriaID, true);
        Iterator result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (result.hasNext()) {
            Object[] resultRow = (Object[]) result.next();
            String[] hashMapValue = new String[2];
            hashMapValue[0] = (String) resultRow[0];
            hashMapValue[1] = (String) resultRow[1];
            String hashMapKey = hashMapValue[0] + hashMapValue[1];
            if (baseYearInactiveObjects.get(hashMapKey) != null) {
                gLBBObjects.put(hashMapKey, hashMapValue);
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
    protected HashMap<String, BudgetConstructionCalculatedSalaryFoundationTracker> bCSF = new HashMap<String, BudgetConstructionCalculatedSalaryFoundationTracker>(1);
    // hashmap to hold the document numbers for each accounting key in the header
    protected HashMap<String, String> bcHdrDocNumbers = new HashMap<String, String>(1);
    // hashset to hold the accounting string for each pending GL entry
    protected HashSet<String> currentPBGLKeys = new HashSet<String>(1);
    // hashMap for finding the object type of "detailed position" object codes
    protected HashMap<String, String> detailedPositionObjectTypes = new HashMap<String, String>(1);
    // keys for deleted or vacant rows present in the override CSF: none of these keys
    // will load to BCSF from either the override or actual CSF (even if they
    // are active in the actual CSF) 
    protected HashSet<String> csfOverrideKeys = new HashSet<String>(1);;
    // EMPLID's in CSF which have more than one active row
    // we budget in whole dollars, while payroll deals in pennies
    // we will use this for our complicated rounding algorithm, to keep the total budget base salary within a dollar of the payroll salary
    protected HashMap<String, roundMechanism> keysNeedingRounding = new HashMap<String, roundMechanism>(1);
    // we need the position normal work months to write a new appointment funding row: the normal work months is the "months appointment"
    protected HashMap<String, Integer> positionNormalWorkMonths = new HashMap<String, Integer>(1);

    protected void buildAppointmentFundingCleanUp() {
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
    protected Integer CSFRowsRead = new Integer(0);
    protected Integer CSFRowsVacant = new Integer(0);
    protected Integer CSFVacantsConsolidated = new Integer(0);
    protected Integer CSFOverrideDeletesRead = new Integer(0);
    protected Integer CSFOverrideRead = new Integer(0);
    protected Integer CSFOverrideVacant = new Integer(0);
    protected Integer CSFForBCSF = new Integer(0);
    protected Integer CSFCurrentGLRows = new Integer(0);
    protected Integer CSFCurrentBCAFRows = new Integer(0);
    protected Integer CSFBCSFRowsMatchingGL = new Integer(0);
    protected Integer CSFBCSFRowsMatchingBCAF = new Integer(0);
    protected Integer CSFNewGLRows = new Integer(0);
    protected Integer CSFNewBCAFRows = new Integer(0);
    protected Integer CSFBCAFRowsMarkedDeleted = new Integer(0);
    protected Integer CSFBCAFRowsMissing = new Integer(0);
    protected Integer CSFBadObjectsSkipped = new Integer(0);

    public void buildAppointmentFundingAndBCSF(Integer BaseYear) {
        /*********************************************************************
         * RI requirements:
         * this method assumes that ALL budget construction positions for the
         * request year are in the position table, 
         * and
         * that budget construction documents for every accounting key in the
         * CSF tables have been created 
         **********************************************************************/
        // budget construction CSF tracker is always rebuilt from scratch from the CSF Tracker
        // (it doesn't make sense to carry a base salary line that has 
        //  has disappeared from the CSF Tracker--hence from the payroll system, hence the current base--from
        //  one run of the batch update process to the next.)
        clearBCCSF(BaseYear);
        clearBCCSF(BaseYear + 1);
        // build the new BC CSF objects in memory
        setUpCSFOverrideKeys(BaseYear);
        setUpBCSFMap(BaseYear);
        setUpKeysNeedingRounding(BaseYear);
        readCSFOverride(BaseYear);
        readCSF(BaseYear);
        CSFForBCSF = bCSF.size();
        adjustCSFRounding();
        //  store bCSF rows matching current appointment funding
        readExistingAppointmentFunding(BaseYear);
        //  if all of the bCSF rows have been stored (they all already exist in PBGL, so we don't have to worry about PBLG), we can quit here
        if (bCSF.size() == 0) {
            CSFDiagnostics();
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
        //     will only be adding GL rows with 0 amounts when an account not yet in budget construction comes in from payroll.
        //     there is only base in CSF, no request, and obviously no base budget in the GL exists for an account that isn't in GL.
        //
        //  >> RI requires that data is stored in the order indicated.
        //  -- we will also have to create new appointment funding rows (again, with
        //     no request.
        //  -- finally, we will have to store the bCSF rows themselves.
        setUpbcHdrDocNumbers(BaseYear);
        setUpCurrentPBGLKeys(BaseYear);
        setUpPositionNormalWorkMonths(BaseYear);
        readAndWriteBCSFAndNewAppointmentFundingAndNewPBGL(BaseYear);
        CSFDiagnostics();
        buildAppointmentFundingCleanUp();
    }

    // overload the vacant BCSF line object builders
    protected void addToExistingBCSFVacant(CalculatedSalaryFoundationTrackerOverride csf, String csfKey) {
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
        BudgetConstructionCalculatedSalaryFoundationTracker nowBCSF = bCSF.get(csfKey);
        // first round the amount to whole dollars
        KualiInteger roundedAmount = new KualiInteger(csf.getCsfAmount(), RoundingMode.valueOf(KualiInteger.ROUND_BEHAVIOR));
        nowBCSF.setCsfAmount(nowBCSF.getCsfAmount().add(roundedAmount));
        // increase the percent time (maximum of 100)
        BigDecimal pctTime = nowBCSF.getCsfTimePercent();
        pctTime = pctTime.add(csf.getCsfTimePercent());
        if (pctTime.floatValue() > 100.0) {
            pctTime = new BigDecimal(100.00);
        }
        nowBCSF.setCsfTimePercent(pctTime);
        // increase the FTE (full-time equivalent) (maximum of 1.0)
        BigDecimal csfFTE = nowBCSF.getCsfFullTimeEmploymentQuantity();
        csfFTE = csfFTE.add(csf.getCsfFullTimeEmploymentQuantity());
        if (csfFTE.floatValue() > 1.0) {
            csfFTE = new BigDecimal(1.00);
        }
        nowBCSF.setCsfFullTimeEmploymentQuantity(csfFTE);
        CSFVacantsConsolidated = CSFVacantsConsolidated + 1;
    }

    protected void addToExistingBCSFVacant(CalculatedSalaryFoundationTracker csf, String csfKey) {
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
        BudgetConstructionCalculatedSalaryFoundationTracker nowBCSF = bCSF.get(csfKey);
        // first round the amount to whole dollars
        KualiInteger roundedAmount = new KualiInteger(csf.getCsfAmount(), RoundingMode.valueOf(KualiInteger.ROUND_BEHAVIOR));
        nowBCSF.setCsfAmount(nowBCSF.getCsfAmount().add(roundedAmount));
        // increase the percent time (maximum of 100)
        BigDecimal pctTime = nowBCSF.getCsfTimePercent();
        pctTime = pctTime.add(csf.getCsfTimePercent());
        if (pctTime.floatValue() > 100.0) {
            pctTime = new BigDecimal(100.00);
        }
        nowBCSF.setCsfTimePercent(pctTime);
        // increase the FTE (full-time equivalent) (maximum of 1.0)
        BigDecimal csfFTE = nowBCSF.getCsfFullTimeEmploymentQuantity();
        csfFTE = csfFTE.add(csf.getCsfFullTimeEmploymentQuantity());
        if (csfFTE.floatValue() > 1.0) {
            csfFTE = new BigDecimal(1.00);
        }
        nowBCSF.setCsfFullTimeEmploymentQuantity(csfFTE);
        CSFVacantsConsolidated = CSFVacantsConsolidated + 1;
    }

    // make the rounding adjustments
    protected void adjustCSFRounding() {
        for (Map.Entry<String, roundMechanism> roundMap : keysNeedingRounding.entrySet()) {
            roundMechanism rx = roundMap.getValue();
            rx.fixRoundErrors();
        }
        // we can reclaim the storage
        keysNeedingRounding.clear();
    }

    // overload the BCSF object builders
    protected void buildAndStoreBCSFfromCSF(CalculatedSalaryFoundationTrackerOverride csf, String csfKey) {
        boolean vacantLine = isVacantLine(csf);
        BudgetConstructionCalculatedSalaryFoundationTracker csfBC = new BudgetConstructionCalculatedSalaryFoundationTracker();
        // budget construction CSF contains the coming fiscal year
        csfBC.setUniversityFiscalYear(csf.getUniversityFiscalYear() + 1);
        csfBC.setChartOfAccountsCode(csf.getChartOfAccountsCode());
        csfBC.setAccountNumber(csf.getAccountNumber());
        csfBC.setSubAccountNumber(csf.getSubAccountNumber());
        csfBC.setFinancialObjectCode(csf.getFinancialObjectCode());
        csfBC.setFinancialSubObjectCode(csf.getFinancialSubObjectCode());
        csfBC.setPositionNumber(csf.getPositionNumber());
        // budget construction CSF always contains the vacant EMPLID, not
        // the EMPLID of the last incumbent
        csfBC.setEmplid((vacantLine ? BCConstants.VACANT_EMPLID : csf.getEmplid()));
        csfBC.setCsfFullTimeEmploymentQuantity(csf.getCsfFullTimeEmploymentQuantity());
        csfBC.setCsfTimePercent(csf.getCsfTimePercent());
        csfBC.setCsfFundingStatusCode(csf.getCsfFundingStatusCode());
        // we only worry about rounding errors when the line is not vacant
        // since all vacant lines in CSF have the same (vacant) EMPLID, we
        // would have to round by position. 
        if (!vacantLine) {
            // changed BO type from KualiDecimal to KualiInteger (June 1, 2007) 
            // csfBC.setCsfAmount(csf.getCsfAmount());
            bCSF.put(csfKey, csfBC);
            // now we have to round and save the rounding error
            roundMechanism rX = keysNeedingRounding.get(csf.getEmplid());
            rX.addNewBCSF(csfBC, csf.getCsfAmount());
        }
        else {
            // for vacant lines, we have to round to whole dollars
            csfBC.setCsfAmount(new KualiInteger(csf.getCsfAmount(), RoundingMode.valueOf(KualiInteger.ROUND_BEHAVIOR)));
            bCSF.put(csfKey, csfBC);
        }
    }

    protected void buildAndStoreBCSFfromCSF(CalculatedSalaryFoundationTracker csf, String csfKey) {
        boolean vacantLine = isVacantLine(csf);
        BudgetConstructionCalculatedSalaryFoundationTracker csfBC = new BudgetConstructionCalculatedSalaryFoundationTracker();
        // budget construction CSF contains the coming fiscal year
        csfBC.setUniversityFiscalYear(csf.getUniversityFiscalYear() + 1);
        csfBC.setChartOfAccountsCode(csf.getChartOfAccountsCode());
        csfBC.setAccountNumber(csf.getAccountNumber());
        csfBC.setSubAccountNumber(csf.getSubAccountNumber());
        csfBC.setFinancialObjectCode(csf.getFinancialObjectCode());
        csfBC.setFinancialSubObjectCode(csf.getFinancialSubObjectCode());
        csfBC.setPositionNumber(csf.getPositionNumber());
        // budget construction CSF always contains the vacant EMPLID, not
        // the EMPLID of the last incumbent
        csfBC.setEmplid((vacantLine ? BCConstants.VACANT_EMPLID : csf.getEmplid()));
        csfBC.setCsfFullTimeEmploymentQuantity(csf.getCsfFullTimeEmploymentQuantity());
        csfBC.setCsfTimePercent(csf.getCsfTimePercent());
        csfBC.setCsfFundingStatusCode(csf.getCsfFundingStatusCode());
        // we only worry about rounding errors when the line is not vacant
        // since all vacant lines in CSF have the same (vacant) EMPLID, we
        // would have to round by position, and positions can be shared. 
        if (!vacantLine) {
            bCSF.put(csfKey, csfBC);
            // now we have to round and save the rounding error
            roundMechanism rX = keysNeedingRounding.get(csf.getEmplid());
            rX.addNewBCSF(csfBC, csf.getCsfAmount());
        }
        else {
            // for vacant lines, we have to round to whole dollars
            csfBC.setCsfAmount(new KualiInteger(csf.getCsfAmount(), RoundingMode.valueOf(KualiInteger.ROUND_BEHAVIOR)));
            bCSF.put(csfKey, csfBC);
        }
    }

    protected void buildAppointemntFundingFromBCSF(BudgetConstructionCalculatedSalaryFoundationTracker bcsf) {
        // current referential integrity insists that the position exists
        // if implementers take it out, we hedge our bets below
        String positionNumber = bcsf.getPositionNumber();
        Integer normalWorkMonths = (positionNormalWorkMonths.containsKey(positionNumber) ? positionNormalWorkMonths.get(positionNumber) : 12);
        // rqstAmount and notOnLeave are used elsewhere and defined globally
        KualiInteger defaultAmount = KualiInteger.ZERO;
        BigDecimal defaultFractions = new BigDecimal(0);
        //
        PendingBudgetConstructionAppointmentFunding bcaf = new PendingBudgetConstructionAppointmentFunding();
        bcaf.setUniversityFiscalYear(bcsf.getUniversityFiscalYear());
        bcaf.setChartOfAccountsCode(bcsf.getChartOfAccountsCode());
        bcaf.setAccountNumber(bcsf.getAccountNumber());
        bcaf.setSubAccountNumber(bcsf.getSubAccountNumber());
        bcaf.setFinancialObjectCode(bcsf.getFinancialObjectCode());
        bcaf.setFinancialSubObjectCode(bcsf.getFinancialSubObjectCode());
        bcaf.setEmplid(bcsf.getEmplid());
        bcaf.setPositionNumber(positionNumber);
        bcaf.setAppointmentRequestedFteQuantity(bcsf.getCsfFullTimeEmploymentQuantity());
        bcaf.setAppointmentRequestedTimePercent(bcsf.getCsfTimePercent());
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

    protected String buildAppointmentFundingKey(PendingBudgetConstructionAppointmentFunding bcaf) {
        return (bcaf.getEmplid()) + bcaf.getPositionNumber() + bcaf.getAccountNumber() + bcaf.getChartOfAccountsCode() + bcaf.getSubAccountNumber() + bcaf.getFinancialObjectCode() + bcaf.getFinancialSubObjectCode();
    }

    // overload the CSF key builders 
    protected String buildCSFKey(CalculatedSalaryFoundationTrackerOverride csf) {
        return (csf.getEmplid()) + csf.getPositionNumber() + csf.getAccountNumber() + csf.getChartOfAccountsCode() + csf.getSubAccountNumber() + csf.getFinancialObjectCode() + csf.getFinancialSubObjectCode();
    }

    protected String buildCSFKey(CalculatedSalaryFoundationTracker csf) {
        return (csf.getEmplid()) + csf.getPositionNumber() + csf.getAccountNumber() + csf.getChartOfAccountsCode() + csf.getSubAccountNumber() + csf.getFinancialObjectCode() + csf.getFinancialSubObjectCode();
    }

    protected String buildDocKeyFromBCSF(BudgetConstructionCalculatedSalaryFoundationTracker bcsf) {
        // see setUpbcHdrDocNumbers for the correct key elements
        // the order here must match the order there
        return bcsf.getChartOfAccountsCode() + bcsf.getAccountNumber() + bcsf.getSubAccountNumber();
    }

    protected boolean buildPBGLFromBCSFAndStore(BudgetConstructionCalculatedSalaryFoundationTracker bcsf) {
        // first we need to see if a new PBGL row is needed
        String testKey = buildPBGLKey(bcsf);
        if (currentPBGLKeys.contains(testKey)) {
            return true;
        }
        // Budget construction cannot show detailed salary lines unless the object code supports "detailed positions".   But, the CSF is a plug-in, so Kuali cannot assume that the CSF enforces this rule.  
        // Here, we test whether the object class is valid.  if it is not, we write a message and skip the row.
        // we do this before the GL check, so we can be sure we log all the rows that have problems instead of requiring multiple runs to find them all.
        String objectType = detailedPositionObjectTypes.get(bcsf.getChartOfAccountsCode() + bcsf.getFinancialObjectCode());
        if (objectType == null) {
            LOG.warn(String.format("\nthis row has an object class which does not support" + " detailed positions (skipped):\n" + "position: %s, EMPLID: %s, accounting string =" + "(%s,%s,%s,%s,%s", bcsf.getPositionNumber(), bcsf.getEmplid(), bcsf.getChartOfAccountsCode(), bcsf.getAccountNumber(), bcsf.getSubAccountNumber(), bcsf.getFinancialObjectCode(), bcsf.getFinancialSubObjectCode()));
            CSFBadObjectsSkipped = CSFBadObjectsSkipped + 1;
            return false;
        }
        // we need a new row.  
        // store the key so we won't try to add another row from a different person's bcsf which has the same key.
        currentPBGLKeys.add(testKey);
        String docKey = buildDocKeyFromBCSF(bcsf);
        // we never have to build a new document header because createNewBCDocumentsFromGLCSF is always called earlier in the step containing this routine. 
        // fill in the fields.
        PendingBudgetConstructionGeneralLedger pbGL = new PendingBudgetConstructionGeneralLedger();
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
        CSFNewGLRows = CSFNewGLRows + 1;
        return true;
    }

    // these two rows are overloaded so we have a standardized key
    protected String buildPBGLKey(BudgetConstructionCalculatedSalaryFoundationTracker bcsf) {
        return bcsf.getAccountNumber() + bcsf.getFinancialObjectCode() + bcsf.getChartOfAccountsCode() + bcsf.getSubAccountNumber() + bcsf.getFinancialSubObjectCode();
    }

    protected String buildPBGLKey(PendingBudgetConstructionGeneralLedger pbgl) {
        return pbgl.getAccountNumber() + pbgl.getFinancialObjectCode() + pbgl.getChartOfAccountsCode() + pbgl.getSubAccountNumber() + pbgl.getFinancialSubObjectCode();
    }

    protected String buildVacantCSFKey(CalculatedSalaryFoundationTrackerOverride csf) {
        boolean vacantLine = isVacantLine(csf);
        return (vacantLine ? BCConstants.VACANT_EMPLID : csf.getEmplid()) + csf.getPositionNumber() + csf.getAccountNumber() + csf.getChartOfAccountsCode() + csf.getSubAccountNumber() + csf.getFinancialObjectCode() + csf.getFinancialSubObjectCode();
    }

    protected String buildVacantCSFKey(CalculatedSalaryFoundationTracker csf) {
        boolean vacantLine = isVacantLine(csf);
        return (vacantLine ? BCConstants.VACANT_EMPLID : csf.getEmplid()) + csf.getPositionNumber() + csf.getAccountNumber() + csf.getChartOfAccountsCode() + csf.getSubAccountNumber() + csf.getFinancialObjectCode() + csf.getFinancialSubObjectCode();
    }

    //
    // clean out the existing BCSF data for the key in question.
    protected void clearBCCSF(Integer FiscalYear) {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, FiscalYear);
        QueryByCriteria queryID = new QueryByCriteria(BudgetConstructionCalculatedSalaryFoundationTracker.class, criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        // as always, we should clear the cache after a bulk delete, even though in this case we haven't yet fetched much
        getPersistenceBrokerTemplate().clearCache();
    }


    protected void CSFDiagnostics() {
        LOG.info(String.format("\n\nResults of building BC CSF"));
        LOG.info(String.format("\nCSF override active rows %d", CSFOverrideRead));
        LOG.info(String.format("\nCSF override deletes     %d", CSFOverrideDeletesRead));
        LOG.info(String.format("\nCSF rows read            %d", CSFRowsRead));
        LOG.info(String.format("\n\nCSF overrides vacant    %d", CSFOverrideVacant));
        LOG.info(String.format("\nCSF vacant               %d", CSFRowsVacant));
        LOG.info(String.format("\nCSF vacants consolidated %d", CSFVacantsConsolidated));
        LOG.info(String.format("\n\nBudgetConstruction CSF rows %d", CSFForBCSF));
        LOG.info(String.format("\n\nCurrent PBGL rows with position object classes %d", CSFCurrentGLRows));
        LOG.info(String.format("\nNew PBGL rows created from CSF %d", CSFNewGLRows));
        LOG.info(String.format("\nCSF rows skipped: bad obj code %d",CSFBadObjectsSkipped));
        LOG.info(String.format("\n\nCurrent appt funding rows      %d", CSFCurrentBCAFRows));
        LOG.info(String.format("\nNew appt funding rows from CSF   %d",CSFNewBCAFRows));
        LOG.info(String.format("\nAppt funding rows not in BCSF    %d", CSFBCAFRowsMissing));
        LOG.info(String.format("\nAppt funding rows marked deleted %d", CSFBCAFRowsMarkedDeleted));
        LOG.info(String.format("\n\nend of BC CSF build statistics"));
    }

    protected ArrayList<String> findPositionRequiredObjectCodes(Integer BaseYear) {
        // we want to build an SQL IN criteria to filter a return set. 
        // we will find distinct objects only, regardless of chart, so OJB can build the SQL instead of our having to concatenate the chart and object code fields.  
        // this will not be a concern--it will make the return set bigger,but include every case we want. 
        // the result will be used to build a list to check for missing PBGL rows, so having more PBGL rows than we need will not cause us to miss any.
        Integer RequestYear = BaseYear + 1;
        ArrayList<String> objectCodesWithIndividualPositions = new ArrayList<String>(10);
        
        Map<String, Object> laborObjectCodeMap = new HashMap<String, Object>();
        laborObjectCodeMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        laborObjectCodeMap.put(KFSPropertyConstants.DETAIL_POSITION_REQUIRED_INDICATOR, true);    
        laborObjectCodeMap.put(KFSPropertyConstants.ACTIVE,true);
        List<LaborLedgerObject> laborLedgerObjects = kualiModuleService.getResponsibleModuleService(LaborLedgerObject.class).getExternalizableBusinessObjectsList(LaborLedgerObject.class, laborObjectCodeMap);
        
        for(LaborLedgerObject laborObject: laborLedgerObjects) {
            objectCodesWithIndividualPositions.add(laborObject.getFinancialObjectCode());
        }
        
        return objectCodesWithIndividualPositions;
    }

    //  we will overload both of these checks as well             
    protected boolean isVacantLine(CalculatedSalaryFoundationTracker csf) {
        return ((csf.getCsfFundingStatusCode().equals(BCConstants.csfFundingStatusFlag.VACANT.getFlagValue())) || (csf.getCsfFundingStatusCode().equals(BCConstants.csfFundingStatusFlag.UNFUNDED.getFlagValue())));
    }

    protected boolean isVacantLine(CalculatedSalaryFoundationTrackerOverride csf) {
        return ((csf.getCsfFundingStatusCode().equals(BCConstants.csfFundingStatusFlag.VACANT.getFlagValue())) || (csf.getCsfFundingStatusCode().equals(BCConstants.csfFundingStatusFlag.UNFUNDED.getFlagValue())));
    }

    // here are the routines to build BCSF

    protected void readAndWriteBCSFAndNewAppointmentFundingAndNewPBGL(Integer BaseYear) {
        // read through the remaining BCSF objects (those that do not presently exist in appointment funding (BCAF).
        // we will check whether they exist in Pending GL (PBGL), and, if not, write a new GL line.  
        // Then, we will write a PBGL row and a BCSF row (in that order, because of referential integrity.  
        // The PBGL and BCAF rows will have 0 amounts.
        // People will have to fill in the budgets (or mark the funding deleted) to cover people in the payroll in budgeted positions, but not funding in the base budget in GL.
        CSFNewBCAFRows = bCSF.size();
        for (Map.Entry<String, BudgetConstructionCalculatedSalaryFoundationTracker> orphanBCSF : bCSF.entrySet()) {
            BudgetConstructionCalculatedSalaryFoundationTracker bcsf = orphanBCSF.getValue();
            if (!buildPBGLFromBCSFAndStore(bcsf)) {
                continue;
            }
            buildAppointemntFundingFromBCSF(bcsf);
        }
    }

    protected void readCSF(Integer BaseYear) {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        QueryByCriteria queryID = new QueryByCriteria(CalculatedSalaryFoundationTracker.class, criteriaID);
        Iterator csfResultSet = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (csfResultSet.hasNext()) {
            CalculatedSalaryFoundationTracker csfRow = (CalculatedSalaryFoundationTracker) csfResultSet.next();
            CSFRowsRead = CSFRowsRead + 1;
            CSFRowsVacant = CSFRowsVacant + (isVacantLine(csfRow) ? 1 : 0);
            // has this been overridden?  if so, don't store it
            String testKey = buildCSFKey(csfRow);
            if (csfOverrideKeys.contains(testKey)) {
                continue;
            }
            // is the line vacant
            testKey = buildVacantCSFKey(csfRow);
            if (isVacantLine(csfRow) && (bCSF.containsKey(testKey))) {
                //the line is vacant and it is already in CSF
                addToExistingBCSFVacant(csfRow, testKey);
            }
            else {
                buildAndStoreBCSFfromCSF(csfRow, testKey);
            }
        }
        // we no longer need the list of csf override keys--recycle
        csfOverrideKeys.clear();
    }

    protected void readCSFOverride(Integer BaseYear) {
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        criteriaID.addEqualTo(KFSPropertyConstants.ACTIVE, true);
        QueryByCriteria queryID = new QueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class, criteriaID);
        Iterator csfResultSet = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (csfResultSet.hasNext()) {
            CalculatedSalaryFoundationTrackerOverride csfRow = (CalculatedSalaryFoundationTrackerOverride) csfResultSet.next();
            CSFOverrideRead = CSFOverrideRead + 1;
            CSFOverrideVacant = CSFOverrideVacant + (isVacantLine(csfRow) ? 1 : 0);
            // is the line vacant
            String testKey = buildVacantCSFKey(csfRow);
            if (isVacantLine(csfRow) && (bCSF.containsKey(testKey))) {
                //the line is vacant and it is already in CSF
                addToExistingBCSFVacant(csfRow, testKey);
            }
            else {
                buildAndStoreBCSFfromCSF(csfRow, testKey);
            }
        }
    }

    protected void readExistingAppointmentFunding(Integer BaseYear) {
        // we will read all existing appointment funding (BCAF).
        // -- if a BCAF object matches with a BCSF row, we will store and remove the BCSF row, and ignore the AF object
        // -- if a BCAF object does NOT match with a BCSF row, we will check to see if it has been altered by a user.  if not, we will mark it deleted and store it as such.
        //
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaID = new Criteria();
        // we add this criterion so that it is possible to have more than one year at a time in budget construction if an institution wants to do that.
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(PendingBudgetConstructionAppointmentFunding.class, criteriaID);
        Iterator bcafResults = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (bcafResults.hasNext()) {
            CSFCurrentBCAFRows = CSFCurrentBCAFRows + 1;
            PendingBudgetConstructionAppointmentFunding bcaf = (PendingBudgetConstructionAppointmentFunding) bcafResults.next();
            String testKey = buildAppointmentFundingKey(bcaf);
            if (bCSF.containsKey(testKey)) {
                // the new BCSF row is already in appointment funding. 
                // we store the BCSF row, delete it from the hash map, and go on.
                BudgetConstructionCalculatedSalaryFoundationTracker bCSFRow = bCSF.get(testKey);
                getPersistenceBrokerTemplate().store(bCSFRow);
                bCSF.remove(testKey);
            }
            else {
                // the current funding row is NOT in the new base set. 
                // we will mark it deleted if it came in from CSF and has not been altered by a user.
                // we never remove any existing rows from BCAF in batch.
                untouchedAppointmentFunding(bcaf);
            }
        }
    }

    // set up the hash objects   
    protected void setUpBCSFMap(Integer BaseYear) {
        // we'll just overestimate, making the size equal to active override rows and active CSF rows, even though the former might replace some of the latter
        Integer bCSFSize = new Integer(0);
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        criteriaID.addEqualTo(KFSPropertyConstants.ACTIVE,true);
        Criteria criteriaIDCSF = new Criteria();
        criteriaIDCSF.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaIDCSF.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        bCSFSize = hashObjectSize(CalculatedSalaryFoundationTrackerOverride.class, criteriaID) + hashObjectSize(CalculatedSalaryFoundationTracker.class, criteriaIDCSF);
        bCSF = new HashMap<String, BudgetConstructionCalculatedSalaryFoundationTracker>(bCSFSize);
    }

    protected void setUpbcHdrDocNumbers(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        bcHdrDocNumbers = new HashMap<String, String>(hashObjectSize(BudgetConstructionHeader.class, criteriaID));
        //  now we have to get the actual data
        String[] headerList = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, KFSPropertyConstants.DOCUMENT_NUMBER };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(BudgetConstructionHeader.class, headerList, criteriaID);
        Iterator headerRows = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (headerRows.hasNext()) {
            Object[] headerRow = (Object[]) headerRows.next();
            String testKey = ((String) headerRow[0]) + ((String) headerRow[1]) + ((String) headerRow[2]);
            bcHdrDocNumbers.put(testKey, ((String) headerRow[3]));
        }
    }

    protected void setUpCSFOverrideKeys(Integer BaseYear) {
        //  these are rows in CSF Override--they should take precedence over what is in CSF
        //  the idea is this:
        //  (1) we build BCSF from CSF Override first.  so, when we read CSF, we will not create a new BCSF entry if the override already has created one.
        //  (2) the override will create an entry with the same key as CSF unless (a) the override has a deleted row or (b) the override has a vacant row so that the EMPLID is changed to the vacant EMPLID in BCSF.
        //   So, we create a list of override keys possibly missing in BCSF which can be used to eliminate CSF candidates for BCSF.    
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.ACTIVE, true);
        Criteria deleteCriteria = new Criteria();
        deleteCriteria.addNotEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        Criteria vacantCriteria = new Criteria();
        vacantCriteria.addEqualTo(KFSPropertyConstants.CSF_FUNDING_STATUS_CODE, BCConstants.csfFundingStatusFlag.VACANT.getFlagValue());
        deleteCriteria.addOrCriteria(vacantCriteria);
        criteriaID.addAndCriteria(deleteCriteria);
        csfOverrideKeys = new HashSet<String>(hashObjectSize(CalculatedSalaryFoundationTrackerOverride.class, criteriaID));
        // now we want to build the hash set
        QueryByCriteria qry = new QueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class, criteriaID);
        Iterator<CalculatedSalaryFoundationTrackerOverride> csfOvrd = getPersistenceBrokerTemplate().getIteratorByQuery(qry);
        while (csfOvrd.hasNext()) {
            CalculatedSalaryFoundationTrackerOverride csfOvrdRow = csfOvrd.next();
            csfOverrideKeys.add(buildCSFKey(csfOvrdRow));
            CSFOverrideDeletesRead = CSFOverrideDeletesRead + ((csfOvrdRow.getCsfDeleteCode().equals(BCConstants.ACTIVE_CSF_DELETE_CODE)? 0 : 1));
        }
    }

    protected void setUpCurrentPBGLKeys(Integer BaseYear) {
        // this will actually set up two maps. 
        // both will be used in the same routine to build the PBGL for BCSF.  
        // keys not in the base budget (someone is being paid from an account, but no one has yet bothered to move base budget funding into the account to cover the expense).
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        criteriaID.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.findPositionRequiredObjectCodes(BaseYear));
        currentPBGLKeys = new HashSet<String>(hashObjectSize(PendingBudgetConstructionGeneralLedger.class, criteriaID));
        // now do the same for the detailed position object code--> object type map (object codes that are allowed to fund individual HR positions)
        detailedPositionObjectTypes = new HashMap<String, String>(hashObjectSize(ObjectCode.class, criteriaID));
        // the PBGL has already been built.
        // we will get business objects so we can use an overloaded method that will be easy to change in order to extract the key.
        // the objects are of no further use, and will disappear when we clear the cache
        int counter = 0;
        QueryByCriteria pbGLQuery = new QueryByCriteria(PendingBudgetConstructionGeneralLedger.class, criteriaID);
        Iterator pbGLObjects = getPersistenceBrokerTemplate().getIteratorByQuery(pbGLQuery);
        while (pbGLObjects.hasNext()) {
            PendingBudgetConstructionGeneralLedger pbGLRow = (PendingBudgetConstructionGeneralLedger) pbGLObjects.next();
            String testKey = this.buildPBGLKey(pbGLRow);
            currentPBGLKeys.add(testKey);
            counter = counter + 1;
        }
        CSFCurrentGLRows = new Integer(counter);
        //
        // now we have to set up the query to read the object types
        String[] objectTypeSelectList = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(ObjectCode.class, objectTypeSelectList, criteriaID);
        Iterator objectTypeRowReturned = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (objectTypeRowReturned.hasNext()) {
            Object[] objectRow = (Object[]) objectTypeRowReturned.next();
            String keyString = ((String) objectRow[0]) + ((String) objectRow[1]);
            String valueString = (String) objectRow[2];
            detailedPositionObjectTypes.put(keyString, valueString);
        }
    }

    protected void setUpKeysNeedingRounding(Integer BaseYear) {
        Integer emplidCSFOvrdCount = new Integer(0);
        Integer emplidCSFCount = new Integer(0);
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        criteriaID.addEqualTo(KFSPropertyConstants.ACTIVE, true);
        Criteria criteriaIDCSF = new Criteria();
        criteriaIDCSF.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, BCConstants.ACTIVE_CSF_DELETE_CODE);
        criteriaIDCSF.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, BaseYear);
        keysNeedingRounding = new HashMap<String, roundMechanism>(hashObjectSize(CalculatedSalaryFoundationTrackerOverride.class, criteriaID, KFSPropertyConstants.EMPLID) + hashObjectSize(CalculatedSalaryFoundationTracker.class, criteriaIDCSF, KFSPropertyConstants.EMPLID));
        //     now fill the hashmap: there will be one rounding bucket for each EMPLID
        String[] columnList = { KFSPropertyConstants.EMPLID };
        //     first use CSF Override
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(CalculatedSalaryFoundationTrackerOverride.class, columnList, criteriaID, true);
        Iterator emplidOvrd = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (emplidOvrd.hasNext()) {
            String newKey = (String) ((Object[]) emplidOvrd.next())[0];
            keysNeedingRounding.put(newKey, new roundMechanism());
        }
        LOG.info(String.format("\nEMPLID's from CSF override: %d", keysNeedingRounding.size()));
        //     now add the EMPLID's from CSF itself (the criterion differs because of the active code on CSF override--CSF override *must* precede CSF here)
        queryID = new ReportQueryByCriteria(CalculatedSalaryFoundationTracker.class, columnList, criteriaIDCSF, true);
        Iterator emplidIter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (emplidIter.hasNext()) {
            String newKey = (String) ((Object[]) emplidIter.next())[0];
            // insert what is not already there from CSF override
            if (!keysNeedingRounding.containsKey(newKey)) {
                keysNeedingRounding.put(newKey, new roundMechanism());
            }
        }
        LOG.info(String.format("\nEMPLID total for BCSF: %d", keysNeedingRounding.size()));
    }

    // read the position table so we can attach normal work months to new bcaf rows
    protected void setUpPositionNormalWorkMonths(Integer BaseYear) {
        Integer RequestYear = BaseYear + 1;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        positionNormalWorkMonths = new HashMap<String, Integer>(hashObjectSize(BudgetConstructionPosition.class, criteriaID));
        String[] fieldList = { KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.IU_NORMAL_WORK_MONTHS };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(BudgetConstructionPosition.class, fieldList, criteriaID);
        Iterator positionRows = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (positionRows.hasNext()) {
            // apparently, numbers come back from report queries in DB-specific ways.  use Number to be safe (as OJB itself does) since the results do not go through the business object
            Object[] positionRow = (Object[]) positionRows.next();
            positionNormalWorkMonths.put((String) positionRow[0], (Integer) ((Number) positionRow[1]).intValue());
        }
    }

    //     these are the four values used to decide whether the current appointment funding row, missing from BCSF, has been entered by a user or is due to a CSF row that has since gone away
    protected String notOnLeave = new String(BCConstants.AppointmentFundingDurationCodes.NONE.durationCode);
    protected KualiInteger rqstAmount = new KualiInteger(0);
    protected BigDecimal pctTime = new BigDecimal(0);
    protected BigDecimal FTE = new BigDecimal(0);

    protected void untouchedAppointmentFunding(PendingBudgetConstructionAppointmentFunding bcaf) {
        //     this checks to see whether the missing row could have come in from CSF earlier, but the CSF row which created it is not inactive.  
        //     if they it not come in from CSF, then it follows that someone entered it and we should not touch it.  
        CSFBCAFRowsMissing = CSFBCAFRowsMissing + 1;
        if ((bcaf.getAppointmentRequestedAmount().compareTo(rqstAmount) != 0) || (bcaf.getAppointmentFundingDurationCode().compareTo(notOnLeave) != 0) || (bcaf.isAppointmentFundingDeleteIndicator())) {
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
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, bcaf.getUniversityFiscalYear() - 1);
        criteriaID.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bcaf.getChartOfAccountsCode());
        criteriaID.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, bcaf.getAccountNumber());
        criteriaID.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, bcaf.getSubAccountNumber());
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, bcaf.getFinancialObjectCode());
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, bcaf.getFinancialSubObjectCode());
        criteriaID.addEqualTo(KFSPropertyConstants.POSITION_NUMBER, bcaf.getPositionNumber());
        // if the budget construction appointment funding (BCAF) row has a vacant ID, we look for vacant flags in CSF.
        if (bcaf.getEmplid().equals(BCConstants.VACANT_EMPLID))
        {
            // the EMPLID in CSF will not match with BCAF: we want to see if a row matching on the other criteria is vacant
            //     funding status is "vacant" or "unfunded"
            Criteria flagCriteria = new Criteria();
            flagCriteria.addEqualTo(KFSPropertyConstants.CSF_FUNDING_STATUS_CODE, BCConstants.csfFundingStatusFlag.VACANT.getFlagValue());
            Criteria vacantCriteria = new Criteria();
            vacantCriteria.addEqualTo(KFSPropertyConstants.CSF_FUNDING_STATUS_CODE, BCConstants.csfFundingStatusFlag.UNFUNDED.getFlagValue());
            flagCriteria.addOrCriteria(vacantCriteria);
            criteriaID.addAndCriteria(flagCriteria);
        }
        else
        {
            // since the BCAF row is not vacant, we require that it match CSF on EMPLID
            criteriaID.addEqualTo(KFSPropertyConstants.EMPLID, bcaf.getEmplid());
        }
        // we are going to compare two numbers (an FTE and a Percent Time), which both originated from CSF.  We have to go through the Kuali filters to make sure the values are strictly comparable.  We can't do a report query.
        QueryByCriteria queryID = new QueryByCriteria(CalculatedSalaryFoundationTracker.class, criteriaID);
        Iterator<CalculatedSalaryFoundationTracker> resultSet = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);        
        if (!resultSet.hasNext()) {
            // the line did not come from CSF, so it must have been added by a user. 
            // therefore, we should *not* mark it deleted  
            return;
        }
        // we have to check whether the CFTE and the percent time are the same. 
        // rounding is required because these can be stored as large numbers in the DB.
        CalculatedSalaryFoundationTracker resultCSF = resultSet.next();
        if (untouchedFTEPercentTimeCheck(bcaf, resultCSF))
        {
          //     we need to mark this bcaf line deleted
          bcaf.setAppointmentRequestedFteQuantity(FTE);
          bcaf.setAppointmentRequestedTimePercent(pctTime);
          bcaf.setAppointmentFundingDeleteIndicator(true);
          getPersistenceBrokerTemplate().store(bcaf);
          CSFBCAFRowsMarkedDeleted = CSFBCAFRowsMarkedDeleted + 1;
        }
        // we also need to exhaust the iterator, so OJB will close the cursor (Oracle)
        TransactionalServiceUtils.exhaustIterator(resultSet);
    }
    
    protected static final MathContext compareContext = new MathContext(2,RoundingMode.HALF_UP);

    protected boolean untouchedFTEPercentTimeCheck(PendingBudgetConstructionAppointmentFunding bcaf, CalculatedSalaryFoundationTracker resultCSF)
    {
        // we need to check whether the current appointment funding row not in BCSF should be marked deleted.
        // it should be if it has not been "touched" by a user.  
        // the criteria for "not touched by a user" are (a) it got into appointment funding via CSF, so it matched with a CSF row, (b) the request amount is 0, and (c) the request FTE and percent time match those of the CSF row.
        // (b) was checked earlier, since it can be checked without a CSF look-up and if the amount is not 0 we can avoid an expensive DB call.
        // (a) is a given, because the resultCSF comes from the matching CSF row.  So, all that is left is (c).  we do this in a separate method because it is convoluted and doing so isolates the code.
        // (c) must be done with rounded values, to avoid letting differences in high-order decimal places in DB storage make equivalent values technically unequal. 
        //
        // we are making the assumption here, based on the business object, that these values are BigDecimal
        // for BigDecimal, compareTo ignores the scale if it differs between the two comparands and only looks at the values (2 = 2.00), while equals will return false for the same value but different scales
        BigDecimal CSFFTE  = resultCSF.getCsfFullTimeEmploymentQuantity().round(compareContext);
        BigDecimal BCAFFTE = bcaf.getAppointmentRequestedFteQuantity().round(compareContext);
        boolean FTEOK = (CSFFTE.compareTo(BCAFFTE) == 0);
        BigDecimal CSFPctTime  = resultCSF.getCsfTimePercent().round(compareContext);
        BigDecimal BCAFPctTime = bcaf.getAppointmentRequestedTimePercent().round(compareContext);
        boolean PctTimeOK = (CSFPctTime.compareTo(BCAFPctTime) == 0);
        String bcafPosition = bcaf.getPositionNumber();
        return (FTEOK && PctTimeOK);
    }

    //     this is an inner class which will store the data we need to perform the rounding, and supply the methods as well    
    protected KualiDecimal shavePennies = new KualiDecimal(100);

    protected class roundMechanism {
        //     the idea here is that people split over many lines could lose or gain several dollars if we rounded each salary line individually.  so, we do the following.
        //     (1) assume that all the amounts are positive
        //     (2) truncate the actual amount to the next lowest integer (round floor)
        //     (3) accumulate the difference in a running total to two decimal places
        //     (4) when all the lines for a person have been encountered, we round the
        //         difference to the next whole integer.
        //     (5) add the difference in dollar increments to each of the lines until the
        //         difference amount is exhausted  
        //     In other words, we only use "bankers rounding" at the end.  We truncate by converting to an int, which calls BigDecimal.intvalue.        
        private KualiDecimal diffAmount = new KualiDecimal(0);
        private ArrayList<BudgetConstructionCalculatedSalaryFoundationTracker> candidateBCSFRows = new ArrayList<BudgetConstructionCalculatedSalaryFoundationTracker>(10);

        public void addNewBCSF(BudgetConstructionCalculatedSalaryFoundationTracker bCSF, KualiDecimal amountFromCSFToSet) {
            // lop off the pennies--go down to the nearest whole dollar 
            KualiInteger wholeDollarsCSFAmount = new KualiInteger(amountFromCSFToSet, RoundingMode.FLOOR);
            // store the whole dollar amount in the budget construction CSF object
            bCSF.setCsfAmount(wholeDollarsCSFAmount);
            // find the pennies that were shaved off
            KualiDecimal penniesFromCSFAmount = amountFromCSFToSet;
            // BigDecimal values are immutable.  So, we have to reset the pointer  after the subtract
            penniesFromCSFAmount = penniesFromCSFAmount.subtract(wholeDollarsCSFAmount.kualiDecimalValue());
            //  just round negative amounts and return.
            //  this is only a safety measure.  negative salaries are illegal in budget construction. 
            if (wholeDollarsCSFAmount.isNegative()) {
                return;
            }
            // save the difference. (KualiDecimal values are immutable, so we need to redirect the diffAmount pointer to a new one.)
            diffAmount = diffAmount.add(penniesFromCSFAmount);
            // store the budget construction CSF row with the truncated amount for possible adjustment later
            candidateBCSFRows.add(bCSF);
        }

        public void fixRoundErrors() {
            // this routine adjusts the BCSF values so that the total for each EMPLID round to the nearest whole dollar amount
            if (!diffAmount.isGreaterThan(KualiDecimal.ZERO)) {
                return;
            }
            KualiDecimal adjustAmount = new KualiDecimal(1);
            // no rounding is necessary if the difference is less than a half a buck. 
            // this will also prevent our accessing an empty array list. 
            // we should adjust things with only one row if the pennies were >= .5, though. 
            //
            // now we use "banker's rounding" on the adjustment amount
            if (diffAmount.multiply(shavePennies).mod(shavePennies).isGreaterEqual(new KualiDecimal(50))) {
                diffAmount = diffAmount.add(adjustAmount);
            }
            if (diffAmount.isLessThan(adjustAmount)) {
                return;
            }
            for (BudgetConstructionCalculatedSalaryFoundationTracker rCSF : candidateBCSFRows) {
                KualiInteger fixBCSFAmount = rCSF.getCsfAmount();
                rCSF.setCsfAmount(fixBCSFAmount.add(new KualiInteger(adjustAmount.intValue())));
                diffAmount = diffAmount.subtract(adjustAmount);
                if (diffAmount.isLessThan(adjustAmount)) {
                    break;
                }
            }
        }
    }

    //
    //  here are the routines Spring uses to "wire the beans"
    //
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    public void setBudgetConstructionHumanResourcesPayrollInterfaceDao(BudgetConstructionHumanResourcesPayrollInterfaceDao budgetConstructionHumanResourcesPayrollInterfaceDao) {
        // at IU, some of the tables in Genesis take data from base tables (not budget tables) in the Human Resources/payroll system.
        // (specifically, the months appointment in appointment funding is set from the position table, and some budget construction CSF tracker rows are set to vacant based on attributes of the appointment--the incumbent's position does not match the appointment's "grandfathered" attribute.)  
        // these cases are particular to IU, so are not included in Kuali.  but, the idea that budget tables built with this Dao may need to interact with HR/payroll is accommodated with the injection of this service.
        this.budgetConstructionHumanResourcesPayrollInterfaceDao = budgetConstructionHumanResourcesPayrollInterfaceDao;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }


}
