/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.sql.Date;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao;
import org.kuali.kfs.module.bc.document.dataaccess.impl.BudgetConstructionDaoJdbcBase;



public class BudgetConstructionHumanResourcesPayrollInterfaceDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionHumanResourcesPayrollInterfaceDao {
    /**
     * 
     * @see org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao#buildBudgetConstructionAdministrativePosts(java.lang.Integer)
     */
    public void buildBudgetConstructionAdministrativePosts() {
      /**
       * this unrealistic implementation will simply clean out what is already there
       */
       String sqlString = new String("DELETE FROM LD_BCN_ADM_POST_T\n");
       getSimpleJdbcTemplate().update(sqlString);
    }

    /**
     * 
     * @see org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao#buildBudgetConstructionAppointmentFundingReasons(java.lang.Integer)
     */
    public void buildBudgetConstructionAppointmentFundingReasons(Integer requestFiscalYear) {
        /**
         * this unrealistic implementation will simply clean out what is already there
         */
         String sqlString = new String("DELETE FROM LD_BCN_AF_REASON_T WHERE (UNIV_FISCAL_YR = ?)\n");
         getSimpleJdbcTemplate().update(sqlString,requestFiscalYear);
    }

    /**
     * 
     * @see org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao#buildBudgetConstructionIntendedIncumbent(java.lang.Integer)
     */
    public void buildBudgetConstructionIntendedIncumbent(Integer requestFiscalYear) {
        /**
         * this unrealistic implementation will refresh all incumbents who presently exist in the CSF tracker, but
         * leave any who no longer do in place.
         */
        Integer baseFiscalYear = requestFiscalYear - 1;
         StringBuilder sqlBuilder = new StringBuilder(1500);
         sqlBuilder.append("DELETE FROM LD_BCN_INTINCBNT_T\n");
         sqlBuilder.append("WHERE (EXISTS (SELECT 1\n");
         sqlBuilder.append("               FROM LD_CSF_TRACKER_T\n");
         sqlBuilder.append("               WHERE (LD_CSF_TRACKER_T.UNIV_FISCAL_YR = ?)\n");
         sqlBuilder.append("                 AND (LD_CSF_TRACKER_T.EMPLID = LD_BCN_INTINCBNT_T.EMPLID)\n");
         sqlBuilder.append("                 AND (LD_CSF_TRACKER_T.POS_CSF_DELETE_CD = ?)))\n");
         String sqlString = sqlBuilder.toString();
         getSimpleJdbcTemplate().update(sqlString,baseFiscalYear,BCConstants.ACTIVE_CSF_DELETE_CODE);

         sqlBuilder.delete(0, sqlBuilder.length());
         /**
          *  constants for intended incumbent
          *  the "classification ID" is an IU-specific field that refers to faculty titles.  we default it below.
          *  positions allowed in budget construction are those that are active in the current fiscal year, those that start
          *  July 1 of the coming fiscal year, or, if the person is a 10-month appointee, those that start on August 1 of the
          *  coming fiscal year.
          */
         String defaultClassificationId = new String("TL");
         GregorianCalendar calendarJuly1 = new GregorianCalendar(baseFiscalYear, Calendar.JULY, 1);
         GregorianCalendar calendarAugust1 = new GregorianCalendar(baseFiscalYear, Calendar.AUGUST, 1);
         Date julyFirst = new Date(calendarJuly1.getTimeInMillis());
         Date augustFirst = new Date(calendarAugust1.getTimeInMillis());
         /**
          *  this SQL is unrealistic, but tries to provide decent test data that will cover most cases.
          *  the "in-line view" is required because of the OBJ_ID, which frustrates using a DISTINCT directly.
          *  intended incumbent has only one row per person in real life.  the position is the "principal job" in
          *  PeopleSoft, where people can have secondary appointments in other positions.  the fields to implement
          *  this are not included in Kuali--hence our need to arbitrarily choose the highest position in sort order.
          *  the DISTINCT is necessary, because CSF can have more than one accounting line per person with the same
          *  position.  that, unlike secondary jobs, is a common occurrence. 
          *  in addition, the check for an "August 1" fiscal year is only done at IU for academic-year (10-pay) appointments 
          *  the alias "makeUnique" for the in-line view is required by MySQL (but not by Oracle).
          */
         sqlBuilder.append("INSERT INTO LD_BCN_INTINCBNT_T\n");
         sqlBuilder.append("(EMPLID, PERSON_NM, SETID_SALARY, SAL_ADMIN_PLAN, GRADE, IU_CLASSIF_LEVEL, ACTV_IND)\n");
         sqlBuilder.append("(SELECT EMPLID, PERSON_NM, BUSINESS_UNIT, POS_SAL_PLAN_DFLT, POS_GRADE_DFLT, ?, 'Y'\n"); 
         sqlBuilder.append("FROM\n");
         sqlBuilder.append("(SELECT DISTINCT csf.EMPLID,\n");
         sqlBuilder.append("        CONCAT(CONCAT(csf.EMPLID,' LastNm HR'),CONCAT(', ',CONCAT(csf.EMPLID,' 1stNm HR'))) AS PERSON_NM,\n");
         sqlBuilder.append("        pos.BUSINESS_UNIT,\n");
         sqlBuilder.append("        pos.POS_SAL_PLAN_DFLT,\n");
         sqlBuilder.append("        pos.POS_GRADE_DFLT\n");
         sqlBuilder.append(" FROM LD_CSF_TRACKER_T csf,\n");
         sqlBuilder.append(" PS_POSITION_DATA pos\n");
         sqlBuilder.append(" WHERE (csf.UNIV_FISCAL_YR = ?)\n");
         sqlBuilder.append("   AND (csf.POS_CSF_DELETE_CD = ?)\n");
         sqlBuilder.append("   AND (csf.POSITION_NBR = pos.POSITION_NBR)\n");
         sqlBuilder.append("   AND  ((pos.EFFDT <= ?) OR (pos.EFFDT = ?))\n");
         sqlBuilder.append("   AND (NOT EXISTS (SELECT 1\n");
         sqlBuilder.append("                    FROM PS_POSITION_DATA pox\n");
         sqlBuilder.append("                    WHERE (pos.POSITION_NBR = pox.POSITION_NBR)\n");
         sqlBuilder.append("                      AND (pos.EFFDT < pox.EFFDT)\n");
         sqlBuilder.append("                      AND ((pox.EFFDT <= ?) OR (pox.EFFDT = ?))))\n");
         sqlBuilder.append("   AND (NOT EXISTS (SELECT 1\n");
         sqlBuilder.append("                    FROM LD_CSF_TRACKER_T cfx\n");
         sqlBuilder.append("                    WHERE (csf.UNIV_FISCAL_YR = cfx.UNIV_FISCAL_YR)\n");
         sqlBuilder.append("                      AND (csf.EMPLID = cfx.EMPLID)\n");
         sqlBuilder.append("                      AND (cfx.POS_CSF_DELETE_CD = ?)\n");
         sqlBuilder.append("                      AND (csf.POSITION_NBR < cfx.POSITION_NBR)))) makeUnique)\n");
         
         sqlString = sqlBuilder.toString();
         Object[] sqlArgumentList = {defaultClassificationId,baseFiscalYear,BCConstants.ACTIVE_CSF_DELETE_CODE,julyFirst,augustFirst,julyFirst,augustFirst,BCConstants.ACTIVE_CSF_DELETE_CODE};
         int[] sqlArgumentTypes = {Types.VARCHAR,Types.INTEGER,Types.VARCHAR,Types.DATE,Types.DATE,Types.DATE,Types.DATE,Types.VARCHAR};
         getSimpleJdbcTemplate().update(sqlString,sqlArgumentList);
//         getSimpleJdbcTemplate().getJdbcOperations().update(sqlString,sqlArgumentList,sqlArgumentTypes);
    }
/**
 * 
 * @see org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao#buildBudgetConstructionIntendedIncumbentWithFacultyAttributes(java.lang.Integer)
 */    
    public void buildBudgetConstructionIntendedIncumbentWithFacultyAttributes (Integer requestFiscalYear)
    {
         // this method is the same as buildBudgetConstructionIntendedIncumbent in the default interface.
         // to update faculty ranks, one would modify buildBudgetConstructionIntendedIncumbent so the defaultClassifictaionId for faculty incumbents corresponded to the appropriate faculty level.
         this.buildBudgetConstructionIntendedIncumbent(requestFiscalYear);
    }

    /**
     * 
     * @see org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao#buildBudgetConstructionPositionBaseYear(java.lang.Integer)
     */
    public void buildBudgetConstructionPositionBaseYear(Integer baseFiscalYear) {
        StringBuilder sqlBuilder = new StringBuilder(2000);
        String defaultRCCd = new String("--");
        /**
         *  we have to do this because imbedding a constant string in SQL assumes a string delimiter--that can vary with the DBMS 
         */
        String orgSeparator = new String("-");
        GregorianCalendar calendarJuly1 = new GregorianCalendar(baseFiscalYear, Calendar.JULY, 1);
        Date julyFirst = new Date(calendarJuly1.getTimeInMillis());
        /**
         * first, delete everything for the base year--we will refresh it in case the position has changed
         */
        sqlBuilder.append("DELETE FROM LD_BCN_POS_T\n");
        sqlBuilder.append("WHERE (UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (EXISTS (SELECT 1\n"); 
        sqlBuilder.append("               FROM LD_CSF_TRACKER_T\n"); 
        sqlBuilder.append("               WHERE (LD_CSF_TRACKER_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("                 AND (LD_CSF_TRACKER_T.POSITION_NBR = LD_BCN_POS_T.POSITION_NBR)\n");
        sqlBuilder.append("                 AND (LD_CSF_TRACKER_T.POS_CSF_DELETE_CD = ?)))\n");
        String sqlString = sqlBuilder.toString();
        getSimpleJdbcTemplate().update(sqlString,baseFiscalYear,baseFiscalYear,BCConstants.ACTIVE_CSF_DELETE_CODE);
        sqlBuilder.delete(0, sqlBuilder.length());
        /**
         * re-create the base year position data
         * we take the latest position that is active BEFORE the coming fiscal year
         */
        sqlBuilder.append("INSERT INTO LD_BCN_POS_T\n");
        sqlBuilder.append("(POSITION_NBR, UNIV_FISCAL_YR, POS_EFFDT, POS_EFF_STATUS, POSN_STATUS,\n");
        sqlBuilder.append(" BUDGETED_POSN, CONFIDENTIAL_POSN, POS_STD_HRS_DFLT, POS_REG_TEMP, POS_FTE, POS_DESCR, SETID_DEPT, POS_DEPTID,\n"); 
        sqlBuilder.append(" RC_CD, POS_SAL_PLAN_DFLT, POS_GRADE_DFLT, SETID_JOBCODE, JOBCODE, SETID_SALARY,\n");
        sqlBuilder.append(" POS_LOCK_USR_ID)\n");
        sqlBuilder.append("(SELECT px.POSITION_NBR,\n");
        sqlBuilder.append("        ?, px.EFFDT, px.POS_EFF_STATUS,\n");
        sqlBuilder.append("        px.POSN_STATUS, px.BUDGETED_POSN, 'N',\n");
        sqlBuilder.append("        px.STD_HRS_DEFAULT, px.POS_REG_TEMP, px.POS_FTE, px.DESCR, px.BUSINESS_UNIT,\n");
        sqlBuilder.append("        px.DEPTID, COALESCE(org.RC_CD,?),\n");
        sqlBuilder.append("        px.POS_SAL_PLAN_DFLT, px.POS_GRADE_DFLT, px.BUSINESS_UNIT, px.JOBCODE,\n");
        sqlBuilder.append("        px.BUSINESS_UNIT, ?\n");
        sqlBuilder.append(" FROM PS_POSITION_DATA px LEFT OUTER JOIN LD_BCN_ORG_RPTS_T org\n"); 
        sqlBuilder.append("      ON (CONCAT(CONCAT(org.FIN_COA_CD,?),org.ORG_CD) = px.DEPTID)\n");
        sqlBuilder.append(" WHERE (px.EFFDT < ?)\n");
        sqlBuilder.append("   AND (NOT EXISTS (SELECT 1\n");
        sqlBuilder.append("                    FROM LD_BCN_POS_T\n");
        sqlBuilder.append("                    WHERE (LD_BCN_POS_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("                      AND (px.POSITION_NBR = LD_BCN_POS_T.POSITION_NBR)))\n");
        sqlBuilder.append("   AND (NOT EXISTS (SELECT 1\n");
        sqlBuilder.append("                    FROM PS_POSITION_DATA py\n");
        sqlBuilder.append("                    WHERE (px.POSITION_NBR = py.POSITION_NBR)\n");
        sqlBuilder.append("                      AND (py.EFFDT < ?)\n");
        sqlBuilder.append("                      AND (px.EFFDT < py.EFFDT)))\n");
        sqlBuilder.append("   AND (EXISTS (SELECT 1\n");
        sqlBuilder.append("                FROM LD_CSF_TRACKER_T csf\n");
        sqlBuilder.append("                WHERE (csf.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("                  AND (csf.POS_CSF_DELETE_CD = ?)\n");
        sqlBuilder.append("                  AND (csf.POSITION_NBR = px.POSITION_NBR))))\n");
        sqlString = sqlBuilder.toString();
        getSimpleJdbcTemplate().update(sqlString,baseFiscalYear,defaultRCCd,BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS,orgSeparator,julyFirst,baseFiscalYear,julyFirst,baseFiscalYear,BCConstants.ACTIVE_CSF_DELETE_CODE);
        // set the things that we'll need for testing but which we don't have enough information to set from the Kuali DB
        // this code is obviously somewhat arbitrary--it should be replaced with institution-specific algorithms
        setAcademicDefaultObjectClass(baseFiscalYear);
        setMonthlyStaffOvertimeEligibleDefaultObjectClass(baseFiscalYear);
        setMonthlyStaffOvertimeExemptDefaultObjectClass(baseFiscalYear);
        setBiweeklyStaffDefaultObjectClass(baseFiscalYear);
    }

    /**
     * 
     * @see org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao#buildBudgetConstructionPositonRequestYear(java.lang.Integer)
     */
    public void buildBudgetConstructionPositonRequestYear(Integer requestFiscalYear) {
        StringBuilder sqlBuilder = new StringBuilder(2500);
        // we build constants for DB independence.  we let the library decide how they should be represented in what is passed to the DB server
        String defaultRCCd = new String("--");
        String orgSeparator = new String("-");
        Integer baseFiscalYear = requestFiscalYear-1;
        GregorianCalendar calendarJuly1 = new GregorianCalendar(baseFiscalYear, Calendar.JULY, 1);
        GregorianCalendar calendarAugust1 = new GregorianCalendar(baseFiscalYear, Calendar.AUGUST, 1);
        Date julyFirst = new Date(calendarJuly1.getTimeInMillis());
        Date augustFirst = new Date(calendarAugust1.getTimeInMillis());
        String academicPositionType = new String("AC");
        String academicTenureTrackSalaryPlan = new String("AC1");   
        
        sqlBuilder.append("INSERT INTO LD_BCN_POS_T\n");
        sqlBuilder.append("(POSITION_NBR, UNIV_FISCAL_YR, POS_EFFDT, POS_EFF_STATUS, POSN_STATUS,\n");
        sqlBuilder.append(" BUDGETED_POSN, CONFIDENTIAL_POSN, POS_STD_HRS_DFLT, POS_REG_TEMP, POS_FTE, POS_DESCR, SETID_DEPT, POS_DEPTID,\n"); 
        sqlBuilder.append(" RC_CD, POS_SAL_PLAN_DFLT, POS_GRADE_DFLT, SETID_JOBCODE, JOBCODE, SETID_SALARY,\n");
        sqlBuilder.append(" POS_LOCK_USR_ID)\n");
        sqlBuilder.append("(SELECT px.POSITION_NBR,\n");
        sqlBuilder.append("        ?, px.EFFDT, px.POS_EFF_STATUS,\n");
        sqlBuilder.append("        px.POSN_STATUS, px.BUDGETED_POSN, 'N',\n");
        sqlBuilder.append("        px.STD_HRS_DEFAULT, px.POS_REG_TEMP, px.POS_FTE, px.DESCR, px.BUSINESS_UNIT,\n");
        sqlBuilder.append("        px.DEPTID, COALESCE(org.RC_CD,?),\n");
        sqlBuilder.append("        px.POS_SAL_PLAN_DFLT, px.POS_GRADE_DFLT, px.BUSINESS_UNIT, px.JOBCODE,\n");
        sqlBuilder.append("        px.BUSINESS_UNIT, ?\n");
        sqlBuilder.append(" FROM PS_POSITION_DATA px LEFT OUTER JOIN LD_BCN_ORG_RPTS_T org\n"); 
        sqlBuilder.append("      ON (CONCAT(CONCAT(org.FIN_COA_CD,?),org.ORG_CD) = px.DEPTID)\n");
        sqlBuilder.append(" WHERE ((px.EFFDT <= ?) OR ((px.EFFDT = ?) AND (px.POS_SAL_PLAN_DFLT = ?)))\n");
        sqlBuilder.append("   AND (NOT EXISTS (SELECT 1\n");
        sqlBuilder.append("                    FROM LD_BCN_POS_T\n");
        sqlBuilder.append("                    WHERE (LD_BCN_POS_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("                      AND (px.POSITION_NBR = LD_BCN_POS_T.POSITION_NBR)))\n");
        sqlBuilder.append("   AND (NOT EXISTS (SELECT 1\n");
        sqlBuilder.append("                    FROM PS_POSITION_DATA py\n");
        sqlBuilder.append("                    WHERE (px.POSITION_NBR = py.POSITION_NBR)\n");
        sqlBuilder.append("                      AND ((py.EFFDT <= ?) OR ((py.EFFDT = ?) AND (px.POS_SAL_PLAN_DFLT = ?)))\n");
        sqlBuilder.append("                      AND (px.EFFDT < py.EFFDT)))\n");
        sqlBuilder.append("   AND (EXISTS (SELECT 1\n");
        sqlBuilder.append("                FROM LD_CSF_TRACKER_T csf\n");
        sqlBuilder.append("                WHERE (csf.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("                  AND (csf.POS_CSF_DELETE_CD = ?)\n");
        sqlBuilder.append("                  AND (csf.POSITION_NBR = px.POSITION_NBR))))\n");
        String sqlString = sqlBuilder.toString();
        getSimpleJdbcTemplate().update(sqlString,requestFiscalYear,defaultRCCd,BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS,orgSeparator,julyFirst,augustFirst,academicTenureTrackSalaryPlan,requestFiscalYear,julyFirst,augustFirst,academicTenureTrackSalaryPlan,baseFiscalYear,BCConstants.ACTIVE_CSF_DELETE_CODE);
        // set the things that we'll need for testing but which we don't have enough information to set from the Kuali DB
        // this code is obviously somewhat arbitrary--it should be replaced with institution-specific algorithms
        setAcademicDefaultObjectClass(requestFiscalYear);
        setMonthlyStaffOvertimeEligibleDefaultObjectClass(requestFiscalYear);
        setMonthlyStaffOvertimeExemptDefaultObjectClass(requestFiscalYear);
        setBiweeklyStaffDefaultObjectClass(requestFiscalYear);
    }
    
    /**
     *   At IU, there is a concept of normal work months and pay months.  For example, one can theoretically be in a 12-month position
     *   but only work during a 10-month academic year for some reason.  This situation would make the "full time equivalent" for that
     *   person (assuming she works a 40-hour week during the 10 months) 10/12 or .893333....
     *   Each position is supposed to have an object class.  No one should be able to budget a given position in a different object
     *   class, because that would break the "object level" reporting in accounting that gives totals for "academic salaries", etc.
     *   In this placeholder code, we set these based on the salary plan and the position type.  At IU, there is a table containing salary plan and 
     *   grade that is shared by payroll and the budget to mandate the object class used for salary funding.
     */
    protected void setAcademicDefaultObjectClass(Integer fiscalYear)
    {
        // build constants for DB independence
        Integer monthConstant = new Integer(10);
        String positionType  = new String("AC");
        String defaultObject = new String("2000");
        String salaryPlan    = new String("AC1");
        StringBuilder sqlBuilder = new StringBuilder(500);
        sqlBuilder.append("UPDATE LD_BCN_POS_T\n");
        sqlBuilder.append("SET IU_NORM_WORK_MONTHS = ?,\n");
        sqlBuilder.append("    IU_PAY_MONTHS = ?,\n");
        sqlBuilder.append("    IU_POSITION_TYPE = ?,\n");
        sqlBuilder.append("    IU_DFLT_OBJ_CD = ?\n");
        sqlBuilder.append("WHERE (UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (POS_SAL_PLAN_DFLT = ?)");
        String sqlString = sqlBuilder.toString();
        getSimpleJdbcTemplate().update(sqlString,monthConstant,monthConstant,positionType,defaultObject,fiscalYear,salaryPlan);
    }
    protected void setMonthlyStaffOvertimeEligibleDefaultObjectClass(Integer fiscalYear)
    {
        // build constants for DB independence
        Integer monthConstant = new Integer(12);
        String positionType  = new String("SM");
        String defaultObject = new String("2480");
        String[] salaryPlan = {new String("PAO"), new String("PAU")};
        StringBuilder sqlBuilder = new StringBuilder(500);
        sqlBuilder.append("UPDATE LD_BCN_POS_T\n");
        sqlBuilder.append("SET IU_NORM_WORK_MONTHS = ?,\n");
        sqlBuilder.append("    IU_PAY_MONTHS = ?,\n");
        sqlBuilder.append("    IU_POSITION_TYPE = ?,\n");
        sqlBuilder.append("    IU_DFLT_OBJ_CD = ?\n");
        sqlBuilder.append("WHERE (UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (POS_SAL_PLAN_DFLT IN (?,?))\n");
        String sqlString = sqlBuilder.toString();
        getSimpleJdbcTemplate().update(sqlString,monthConstant,monthConstant,positionType,defaultObject,fiscalYear,salaryPlan[0],salaryPlan[1]);
    }

    protected void setMonthlyStaffOvertimeExemptDefaultObjectClass(Integer fiscalYear)
    {
        // build constants for DB independence
        // (note that this uses a pattern, and therefore assumes that any specific position types beginning with 'P' that go to
        //  a different default object class have already been assigned)
        Integer monthConstant = new Integer(12);
        String  positionType  = new String("SM");
        String  defaultObject = new String("2400");
        String  salaryPlan    = new String("P%");
        StringBuilder sqlBuilder = new StringBuilder(500);
        sqlBuilder.append("UPDATE LD_BCN_POS_T\n");
        sqlBuilder.append("SET IU_NORM_WORK_MONTHS = ?,\n");
        sqlBuilder.append("    IU_PAY_MONTHS = ?,\n");
        sqlBuilder.append("    IU_POSITION_TYPE = ?,\n");
        sqlBuilder.append("    IU_DFLT_OBJ_CD = ?\n");
        sqlBuilder.append("WHERE (UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (POS_SAL_PLAN_DFLT LIKE ?)\n");
        sqlBuilder.append("  AND (IU_DFLT_OBJ_CD IS NULL)\n");
        String sqlString = sqlBuilder.toString();
        getSimpleJdbcTemplate().update(sqlString,monthConstant,monthConstant,positionType,defaultObject,fiscalYear,salaryPlan);
    }
    
    protected void setBiweeklyStaffDefaultObjectClass(Integer fiscalYear)
    {
        // build constants for DB independence
        // (note that we are only assigning default object codes to positions not yet assigned a default.  so, this method must
        //  be called last.  In particular, there is no check on salary plan.)
        Integer monthConstant    = new Integer(12);
        String  positionType     = new String("SB");
        String  defaultObject    = new String("2500");
        String  defaultUnionCode = new String("B1");
        StringBuilder sqlBuilder = new StringBuilder(500);
        sqlBuilder.append("UPDATE LD_BCN_POS_T\n");
        sqlBuilder.append("SET IU_NORM_WORK_MONTHS = ?,\n");
        sqlBuilder.append("    IU_PAY_MONTHS = ?,\n");
        sqlBuilder.append("    IU_POSITION_TYPE = ?,\n");
        sqlBuilder.append("    POS_UNION_CD = ?,\n");
        sqlBuilder.append("    IU_DFLT_OBJ_CD = ?\n");
        sqlBuilder.append("WHERE (UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (IU_DFLT_OBJ_CD IS NULL)\n");
        String sqlString = sqlBuilder.toString();
        getSimpleJdbcTemplate().update(sqlString,monthConstant,monthConstant,positionType,defaultUnionCode,defaultObject,fiscalYear);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao#updateNamesInBudgetConstructionIntendedIncumbent()
     */
    public void updateNamesInBudgetConstructionIntendedIncumbent()
    {
        // do nothing in the default: the names are added in the build routines
    }
}
