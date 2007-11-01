/*
 * Copyright 2007 The Kuali Foundation.
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.module.budget.dao.FiscalYearInitiatorDao;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PositionObjectBenefit;


public class FiscalYearIntiatorDaoOjb extends PlatformAwareDaoBaseOjb implements FiscalYearInitiatorDao {

    /*
     * These routines are designed to create rows for the next fiscal year for reference tables, based on the rows in those tables
     * for the current fiscal year. The idea is to relieve people of the responsibility for typing in hundreds of new rows in a
     * maintenance document, and to preclude having to auto-generate reference rows for x years in the future, maintaining them as
     * things change. There are two modes used by routines in this module. (1) slash-and-burn: if any rows for the target year
     * exist, they are deleted, and replaced with copies of the current year's rows (2) warm-and-fuzzy: any rows for the new year
     * that already exist are left in place, and only those rows whose keys are missing in the new fiscal year are copied from the
     * current year There are two versions of each method (using overloading). To get the slash-and_burn version, one uses the
     * method where there is a second parameter, and passes its value as the static variable "replaceMode".
     */


    /* turn on the logger for the persistence broker */
    private static Logger LOG = org.apache.log4j.Logger.getLogger(FiscalYearIntiatorDaoOjb.class);


    private DateTimeService dateTimeService;
    private PersistenceStructureService persistenceStructureService;

    private UniversityDate universityDate;

    public static final boolean replaceMode = true;

    /*******************************************************************************************************************************
     * AccountingPeriod *
     ******************************************************************************************************************************/
    public void makeAccountingPeriod(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<AccountingPeriod> makersMethods = new MakersMethods<AccountingPeriod>();
        // since we change fields other than the fiscal year, we have to pass in
        // a field change method with the standard signature
        Method fieldMethod = specialFieldsMethod("accountingPeriodSpecialFields", AccountingPeriod.class);
        makersMethods.genericWarmAndFuzzy(AccountingPeriod.class, currentFiscalYear, fieldMethod);
    }

    public void makeAccountingPeriod(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<AccountingPeriod> makersMethods = new MakersMethods<AccountingPeriod>();
        // since we change fields other than the fiscal year, we have to pass in
        // a field change method with the standard signature
        Method fieldMethod = specialFieldsMethod("accountingPeriodSpecialFields", AccountingPeriod.class);
        makersMethods.genericSlashAndBurn(AccountingPeriod.class, currentFiscalYear, fieldMethod);
    }

    private void accountingPeriodSpecialFields(Integer currentFiscalYear, Integer newFiscalYear, AccountingPeriod candidateRow) {
        // there is a four-character year in periods 01 through 12, and a two-character
        // year in period 13. we need to update these for the new year. instead of
        // hard-wiring in the periods, we just try to make both changes
        Integer startThisYear = currentFiscalYear - 1;
        String startThisYearString = startThisYear.toString();
        String currentFiscalYearString = currentFiscalYear.toString();
        String newFiscalYearString = newFiscalYear.toString();
        String nameString = candidateRow.getUniversityFiscalPeriodName();
        candidateRow.setUniversityFiscalPeriodName(updateStringField(newFiscalYearString, currentFiscalYearString, candidateRow.getUniversityFiscalPeriodName()));
        candidateRow.setUniversityFiscalPeriodName(updateStringField(currentFiscalYearString, startThisYearString, candidateRow.getUniversityFiscalPeriodName()));
        candidateRow.setUniversityFiscalPeriodName(updateTwoDigitYear(newFiscalYearString.substring(2, 4), currentFiscalYearString.substring(2, 4), candidateRow.getUniversityFiscalPeriodName()));
        candidateRow.setUniversityFiscalPeriodName(updateTwoDigitYear(currentFiscalYearString.substring(2, 4), startThisYearString.substring(2, 4), candidateRow.getUniversityFiscalPeriodName()));
        // we have to update the ending date, increasing it by one year
        candidateRow.setUniversityFiscalPeriodEndDate(addYearToDate(candidateRow.getUniversityFiscalPeriodEndDate()));
        // we set all of the fiscal period status codes to "closed" before the
        // start of the coming year
        candidateRow.setUniversityFiscalPeriodStatusCode(KFSConstants.ACCOUNTING_PERIOD_STATUS_OPEN);
    }

    /*******************************************************************************************************************************
     * BenefitsCalculation *
     ******************************************************************************************************************************/
    public void makeBenefitsCalculation(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<BenefitsCalculation> makersMethods = new MakersMethods<BenefitsCalculation>();
        makersMethods.genericWarmAndFuzzy(BenefitsCalculation.class, currentFiscalYear);
    }

    public void makeBenefitsCalculation(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<BenefitsCalculation> makersMethods = new MakersMethods<BenefitsCalculation>();
        makersMethods.genericSlashAndBurn(BenefitsCalculation.class, currentFiscalYear);
    }

    /*******************************************************************************************************************************
     * LaborObject *
     ******************************************************************************************************************************/
    public void makeLaborObject(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<LaborObject> makersMethods = new MakersMethods<LaborObject>();
        makersMethods.genericWarmAndFuzzy(LaborObject.class, currentFiscalYear);
    }

    public void makeLaborObject(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<LaborObject> makersMethods = new MakersMethods<LaborObject>();
        makersMethods.genericSlashAndBurn(LaborObject.class, currentFiscalYear);
    }

    /*******************************************************************************************************************************
     * ObjectCode *
     ******************************************************************************************************************************/
    public void makeObjectCode(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<ObjectCode> makersMethods = new MakersMethods<ObjectCode>();
        makersMethods.genericWarmAndFuzzy(ObjectCode.class, currentFiscalYear, objectCodeAdditionalCriteria());
    }

    public void makeObjectCode(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<ObjectCode> makersMethods = new MakersMethods<ObjectCode>();
        makersMethods.genericSlashAndBurn(ObjectCode.class, currentFiscalYear, objectCodeAdditionalCriteria());
    }

    private Criteria objectCodeAdditionalCriteria() {
        // this method allows us to add any filters needed on the current
        // year rows--for example, we might not want any marked deleted.
        // for ObjectCode, we don't want any invalid objects--UNLESS they
        // are the dummy object used in budget construction
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_ACTIVE_CODE, true);
        Criteria criteriaBdg = new Criteria();
        criteriaBdg.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BudgetConstructionConstants.OBJECT_CODE_2PLG);
        criteriaID.addOrCriteria(criteriaBdg);
        return criteriaID;
    }

    /*******************************************************************************************************************************
     * Offset Definition *
     ******************************************************************************************************************************/
    public void makeOffsetDefinition(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<OffsetDefinition> makersMethods = new MakersMethods<OffsetDefinition>();
        makersMethods.genericWarmAndFuzzy(OffsetDefinition.class, currentFiscalYear);
    }

    public void makeOffsetDefinition(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<OffsetDefinition> makersMethods = new MakersMethods<OffsetDefinition>();
        makersMethods.genericSlashAndBurn(OffsetDefinition.class, currentFiscalYear);
    }

    /*******************************************************************************************************************************
     * Options *
     ******************************************************************************************************************************/
    public void makeOptions(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<Options> makersMethods = new MakersMethods<Options>();
        // since we change fields other than the fiscal year, we have to pass in
        // a field change method with the standard signature
        Method fieldMethod = specialFieldsMethod("optionsSpecialFields", Options.class);
        makersMethods.genericWarmAndFuzzy(Options.class, currentFiscalYear, fieldMethod);
    }

    public void makeOptions(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<Options> makersMethods = new MakersMethods<Options>();
        // since we change fields other than the fiscal year, we have to pass in
        // a field change method with the standard signature
        Method fieldMethod = specialFieldsMethod("optionsSpecialFields", Options.class);
        makersMethods.genericSlashAndBurn(Options.class, currentFiscalYear, fieldMethod);
    }

    private void optionsSpecialFields(Integer currentFiscalYear, Integer newFiscalYear, Options candidateRow) {
        // some ineffeciency in set up is traded for easier maintenance
        Integer currentYearStart = currentFiscalYear - 1;
        String endNextYearString = newFiscalYear.toString();
        String endCurrentYearString = currentFiscalYear.toString();
        String startCurrentYearString = currentYearStart.toString();
        candidateRow.setUniversityFiscalYearStartYr(currentFiscalYear);
        // here we allow for a substring of XXXX-YYYY as well as XXXX and YYYY
        String holdIt = updateStringField(endNextYearString, endCurrentYearString, candidateRow.getUniversityFiscalYearName());
        candidateRow.setUniversityFiscalYearName(updateStringField(endCurrentYearString, startCurrentYearString, holdIt));
    }

    /*******************************************************************************************************************************
     * Organization Reversion *
     ******************************************************************************************************************************/
    public void makeOrganizationReversion(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // this table is always one fiscal year behind
        Integer baseYear = currentFiscalYear - 1;
        MakersMethods<OrganizationReversion> makersMethods = new MakersMethods<OrganizationReversion>();
        makersMethods.genericWarmAndFuzzy(OrganizationReversion.class, baseYear);
    }

    public void makeOrganizationReversion(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // this table is always one fiscal year behind
        Integer baseYear = currentFiscalYear - 1;
        MakersMethods<OrganizationReversion> makersMethods = new MakersMethods<OrganizationReversion>();
        makersMethods.genericSlashAndBurn(OrganizationReversion.class, baseYear);
    }

    /*******************************************************************************************************************************
     * Position Object Benefit *
     ******************************************************************************************************************************/
    public void makePositionObjectBenefit(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<PositionObjectBenefit> makersMethods = new MakersMethods<PositionObjectBenefit>();
        makersMethods.genericWarmAndFuzzy(PositionObjectBenefit.class, currentFiscalYear);
    }

    public void makePositionObjectBenefit(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<PositionObjectBenefit> makersMethods = new MakersMethods<PositionObjectBenefit>();
        makersMethods.genericSlashAndBurn(PositionObjectBenefit.class, currentFiscalYear);
    }

    /*******************************************************************************************************************************
     * SubObjCd *
     ******************************************************************************************************************************/
    public void makeSubObjCd(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<SubObjCd> makersMethods = new MakersMethods<SubObjCd>();
        makersMethods.genericWarmAndFuzzy(SubObjCd.class, currentFiscalYear);
    }

    public void makeSubObjCd(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MakersMethods<SubObjCd> makersMethods = new MakersMethods<SubObjCd>();
        makersMethods.genericSlashAndBurn(SubObjCd.class, currentFiscalYear);
    }

    /*******************************************************************************************************************************
     * University Date *
     ******************************************************************************************************************************/
    // this is the only routine that simply replaces what is there, if anything
    // but, we have to do a delete--otherwise, we can get an optimistic locking
    // exception when we try to store a new row on top of something already in
    // the database. we will delete by fiscal year.
    // the accounting period is assumed to correspond to the month, with the
    // month of the start date being the first period and the month of the last
    // day of the fiscal year being the twelfth.
    // the fiscal year tag is always the year of the ending date of the fiscal year
    public void makeUniversityDate(GregorianCalendar FiscalYearStartDate) {
        // loop through a year's worth of dates for the new year
        GregorianCalendar shunivdate = new GregorianCalendar(FiscalYearStartDate.get(Calendar.YEAR), FiscalYearStartDate.get(Calendar.MONTH), FiscalYearStartDate.get(Calendar.DAY_OF_MONTH));
        // set up the end date
        GregorianCalendar enddate = new GregorianCalendar(FiscalYearStartDate.get(Calendar.YEAR), FiscalYearStartDate.get(Calendar.MONTH), FiscalYearStartDate.get(Calendar.DAY_OF_MONTH));
        enddate.add(Calendar.MONTH, 12);
        enddate.add(Calendar.DAY_OF_MONTH, -1);
        // the fiscal year is always the year of the ending date of the fiscal year
        Integer nextFiscalYear = (Integer) enddate.get(Calendar.YEAR);
        // get rid of anything already there
        deleteNewYearRows(nextFiscalYear, UniversityDate.class);
        // initialize the period variables
        int period = 1;
        String periodString = String.format("%02d", period);
        int compareMonth = shunivdate.get(Calendar.MONTH);
        int currentMonth = shunivdate.get(Calendar.MONTH);
        // loop through the dates until we hit the last one
        while (!(shunivdate.equals(enddate))) {
            // TODO: temporary debugging code
            LOG.debug(String.format("\n%s %s %tD:%tT", nextFiscalYear, periodString, shunivdate, shunivdate));
            // store these values--we will update whatever is there
            UniversityDate universityDate = new UniversityDate();
            universityDate.setUniversityFiscalYear(nextFiscalYear);
            universityDate.setUniversityDate(new Date(shunivdate.getTimeInMillis()));
            universityDate.setUniversityFiscalAccountingPeriod(periodString);
            getPersistenceBrokerTemplate().store(universityDate);
            // next day
            shunivdate.add(Calendar.DAY_OF_MONTH, 1);
            // does this kick us into a new month and therefore a new accounting period?
            compareMonth = shunivdate.get(Calendar.MONTH);
            if (currentMonth != compareMonth) {
                period = period + 1;
                periodString = String.format("%02d", period);
                currentMonth = compareMonth;
                // TODO: debugging code
                if (period == 13) {
                    LOG.warn("the date comparison is not working properly");
                    break;
                }
            }
        }
        // store the end date
        UniversityDate universityDate = new UniversityDate();
        universityDate.setUniversityFiscalYear(nextFiscalYear);
        universityDate.setUniversityDate(new Date(shunivdate.getTimeInMillis()));
        universityDate.setUniversityFiscalAccountingPeriod(periodString);
        getPersistenceBrokerTemplate().store(universityDate);
        // TODO: temporary debugging code
        LOG.debug(String.format("\n%s %s %tD:%tT\n", nextFiscalYear, periodString, shunivdate, shunivdate));
    }

    // these are private utility methods

    private java.sql.Date addYearToDate(Date inDate) {
        // OK. Apparently the JDK is trying to offer a generic calendar to all
        // users. java.sql.Date (which extends java.util.Date) is trying to create
        // a DB independent date value. both have settled on milliseconds since
        // midnight, January 1, 1970, Greenwich Mean Time. This value is then
        // "normalized" to the local time zone when it is converted to a date. But,
        // the constructors are based on the original millisecond value, and this
        // value is recoverable via the "time" methods in the classes.
        GregorianCalendar currentCalendarDate = new GregorianCalendar();
        // create a calendar object with no values set
        currentCalendarDate.clear();
        // set the calendar values using the "standard" millisecond value
        // this should represent the java.sql.Date value in the local time zone
        currentCalendarDate.setTimeInMillis(inDate.getTime());
        // add a year to the SQL date
        currentCalendarDate.add(GregorianCalendar.YEAR, 1);
        // return the "standardized" value of the orginal date + 1 year
        return (new Date(currentCalendarDate.getTimeInMillis()));
    }

    private HashSet<String> buildMapOfExistingKeys(Integer RequestYear, Class businessObject) {
        // this code builds and returns a hash set containing the composite
        // key string of rows that already exist in the relevant table for the
        // new fiscal year (we assume all the members of the composite key are
        // strings except the fiscal year.
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        // get space for the map
        HashSet<String> returnHash = new HashSet<String>(hashObjectSize(businessObject, criteriaID));
        // set up to query for the key fields
        String[] attrib = { "" }; // we'll reorient this pointer when we know the size
        attrib = (String[]) persistenceStructureService.getPrimaryKeys(businessObject).toArray(attrib);
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(businessObject, attrib, criteriaID);
        Iterator keyValues = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (keyValues.hasNext()) {
            Object[] keyObject = (Object[]) keyValues.next();
            // we assume the fiscal year is an integer, and the other keys are strings
            // OJB always returns BigDecimal for a number (including fiscal year),
            // but we apply toString directly to the object, so we should be OK.
            StringBuffer concatKey = new StringBuffer(keyObject[0].toString());
            for (int i = 1; i < keyObject.length; i++) {
                concatKey = concatKey.append(keyObject[i]);
            }
            returnHash.add(concatKey.toString());
        }
        return returnHash;
    }

    private void deleteNewYearRows(Integer RequestYear, Class businessObject) {
        // this gets rid of all the rows in the new fiscal year
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(businessObject, criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        getPersistenceBrokerTemplate().clearCache();
    }

    public Integer fiscalYearFromToday() {
        // we look up the fiscal year for today's date, and return it
        // we return 0 if nothing is found
        Integer currentFiscalYear = new Integer(0);
        Date lookUpDate = dateTimeService.getCurrentSqlDateMidnight();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_DATE, lookUpDate);
        String[] attrb = { KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(UniversityDate.class, attrb, criteriaID);
        Iterator resultRow = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (resultRow.hasNext()) {
            currentFiscalYear = (Integer) ((BigDecimal) ((Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(resultRow))[0]).intValue();
        }
        // TODO:
        LOG.debug(String.format("\nreturned from fiscalYearFromToday: %d", currentFiscalYear));
        // TODO:
        return currentFiscalYear;
    }

    // @@TODO:
    // this code is duplicated from GenesisDaoOjb. we don't need to overload the
    // hashObjectSize method here
    // if this thing catches on, maybe we should make the hashObjectSize method
    // a public method in a service
    //
    private Integer hashCapacity(Integer hashSize) {
        // this corresponds to a little more than the default load factor of .75
        // a rehash supposedly occurs when the actual number of elements exceeds
        // (load factor)*capacity
        // N rows < .75 capacity ==> capacity > 4N/3 or 1.3333N. We add a little slop.
        Double tempValue = hashSize.floatValue() * (1.45);
        return (Integer) tempValue.intValue();
    }

    private Integer hashObjectSize(Class classID, Criteria criteriaID) {
        // this counts all rows
        String[] selectList = new String[] { "COUNT(*)" };
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(classID, selectList, criteriaID);
        Iterator resultRows = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (resultRows.hasNext()) {
            return (hashCapacity(((BigDecimal) ((Object[]) resultRows.next())[0]).intValue()));
        }
        return (new Integer(1));
    }

    private Method specialFieldsMethod(String methodName, Class businessObject) throws NoSuchMethodException {
        // this routine returns a pointer to the field change method of the
        // business object. the parameter signature must be Integer, Integer,
        // business object
        Class[] parmArray = { Integer.class, Integer.class, businessObject };

        Method sfMethod = this.getClass().getDeclaredMethod(methodName, parmArray);
        sfMethod.setAccessible(true);
        return sfMethod;
    }

    private String updateStringField(String newYearString, String oldYearString, String currentField) {
        /*
         * this routine is reminiscent of computing in 1970, when disk space was scarce and every byte was fraught with meaning.
         * some fields are captions and titles, and they contain things like the fiscal year. for the new year, we have to update
         * these substrings in place, so they don't have to be updated by hand to display correct information in the application. we
         * use the regular expression utilities in java
         */
        Pattern pattern = Pattern.compile(oldYearString);
        Matcher matcher = pattern.matcher(currentField);
        return matcher.replaceAll(newYearString);
    }

    private String updateTwoDigitYear(String newYear, String oldYear, String currentString) {
        /*
         * this routine is provided to update string fields which contain two-digit years that need to be updated for display. it is
         * very specific, but it's necessary. "two-digit year" means the two numeric characters preceded by a non-numeric character.
         */
        // group 1 is the bounded by the outermost set of parentheses
        // group 2 is the first inner set
        // group 3 is the second inner set--a two-digit year at the beginning of the line
        String regExpString = "(([^0-9]{1}" + oldYear + ")|^(" + oldYear + "))";
        Pattern pattern = Pattern.compile(regExpString);
        Matcher matcher = pattern.matcher(currentString);
        // start looking for a match
        boolean matched = matcher.find();
        if (!matched) {
            // just return if nothing is found
            return currentString;
        }
        // we found something
        // we have to process it
        String returnString = currentString;
        StringBuffer outString = new StringBuffer();
        // is there a match at the beginning of the line (a match with group 3)?
        if (matcher.group(3) != null) {
            // there is a two-digit-year string at the beginning of the line
            // we want to replace it
            matcher.appendReplacement(outString, newYear);
            // find the next match if there is one
            matched = matcher.find();
        }
        while (matched) {
            // the new string will no longer match with group 3
            // if there is still a match, it will be with group 2
            // now we have to prefix the new year string with the same
            // non-numeric character as the next match (hyphen, space, whatever)
            String newYearString = matcher.group(2).substring(0, 1) + newYear;
            matcher.appendReplacement(outString, newYearString);
            matched = matcher.find();
        }
        // dump whatever detritus is left into the new string
        matcher.appendTail(outString);
        return outString.toString();
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    // generic class to pass in types to the generic routines
    private class MakersMethods<T> {

        // this method implements warm and fuzzy mode for an business object
        // which requires no special processing
        private void genericWarmAndFuzzy(Class ojbMappedClass, Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method genericMethod = null;
            Criteria additionalCriterion = null;
            genericWarmAndFuzzy(ojbMappedClass, currentFiscalYear, genericMethod, additionalCriterion);
        }

        // this method implements warm and fuzzy mode for an business object
        // which requires a custom routine to update some of its fields
        private void genericWarmAndFuzzy(Class ojbMappedClass, Integer currentFiscalYear, Method fieldMethod) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Criteria additionalCriterion = null;
            genericWarmAndFuzzy(ojbMappedClass, currentFiscalYear, fieldMethod, additionalCriterion);
        }

        // this method implements warm and fuzzy mode for an business object
        // which filters the rows to be copied from the previous fiscal year
        // based on an additional criterion
        private void genericWarmAndFuzzy(Class ojbMappedClass, Integer currentFiscalYear, Criteria additionalCriterion) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method genericMethod = null;
            genericWarmAndFuzzy(ojbMappedClass, currentFiscalYear, genericMethod, additionalCriterion);
        }

        private void genericWarmAndFuzzy(Class ojbMappedClass, Integer currentFiscalYear, Method fieldMethod, Criteria additionalCriterion) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
            Integer rowsRead = new Integer(0);
            Integer rowsWritten = new Integer(0);
            Integer rowsFailingRI = new Integer(0);
            Integer newFiscalYear = currentFiscalYear + 1;
            String requestYearString = newFiscalYear.toString();
            // build the list of parent keys already copied to the new year (if any)
            // the appropriate child foreign keys must exist in each parent
            // if they do not, this means that the parent row in the current fiscal
            // year was filtered out in the copy to the new fiscal year, and therefore
            // the corresponding child rows should not be copied either
            /*
             * TODO: the bean is not there yet ParentKeyChecker<T> parentKeyChecker = new ParentKeyChecker<T>(newFiscalYear);
             */
            // get the hash set of keys of objects which already exist for the new
            // year and will not be replaced
            HashSet existingKeys = buildMapOfExistingKeys(newFiscalYear, ojbMappedClass);
            //
            String[] keyFields = { "" }; // reorient this pointer when we know the size
            keyFields = (String[]) persistenceStructureService.getPrimaryKeys(ojbMappedClass).toArray(keyFields);
            // get the rows from the previous year
            Criteria criteriaID = new Criteria();
            criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentFiscalYear);
            if (additionalCriterion != null) {
                criteriaID.addAndCriteria(additionalCriterion);
            }
            QueryByCriteria queryID = new QueryByCriteria(ojbMappedClass, criteriaID);
            Iterator<T> oldYearObjects = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
            while (oldYearObjects.hasNext()) {
                rowsRead = rowsRead + 1;
                StringBuffer hashChecker = new StringBuffer(requestYearString);
                T ourBO = oldYearObjects.next();
                for (int i = 1; i < keyFields.length; i++) {
                    Field fld = ourBO.getClass().getDeclaredField(keyFields[i]);
                    fld.setAccessible(true);
                    hashChecker.append((String) fld.get(ourBO));
                }
                // TODO:
                if (rowsRead % 1007 == 1) {
                    LOG.warn(String.format("\n%s: row %d hash key = %s\n", ojbMappedClass.getName(), rowsRead, hashChecker.toString()));
                }
                // TODO:
                if (existingKeys.contains(hashChecker.toString())) {
                    continue;
                }
                // check to see if the row exists in all the parents
                /*
                 * TODO: the bean is not there yet if (!parentKeyChecker.childRowSatisfiesRI(ourBO) { rowsFailingRI =
                 * rowsFailingRI+1; continue; }
                 */
                // we have to set the fiscal year and the version number
                setCommonFields(ourBO, newFiscalYear);
                if (!(fieldMethod == null)) {
                    Object[] args = { currentFiscalYear, newFiscalYear, ourBO };
                    fieldMethod.invoke(FiscalYearIntiatorDaoOjb.this, args);
                }
                // store the result
                getPersistenceBrokerTemplate().store(ourBO);
                rowsWritten = rowsWritten + 1;
            }
            LOG.warn(String.format("\n%s:\n%d read = %d\n%d written = %d", ojbMappedClass.getName(), currentFiscalYear, rowsRead, newFiscalYear, rowsWritten));
            if (fieldMethod != null) {
                // reset the method to private, just to be safe
                fieldMethod.setAccessible(false);
            }
            getPersistenceBrokerTemplate().clearCache();
        }

        // this method implements slash and burn mode for an business object
        // which requires no special processing
        private void genericSlashAndBurn(Class ojbMappedClass, Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method genericMethod = null;
            Criteria additionalCriterion = null;
            genericSlashAndBurn(ojbMappedClass, currentFiscalYear, genericMethod, additionalCriterion);
        }


        // this method implements slash and burn mode for an business object
        // which requires a routine to update some of its fields
        private void genericSlashAndBurn(Class ojbMappedClass, Integer currentFiscalYear, Method fieldMethod) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Criteria additionalCriterion = null;
            genericSlashAndBurn(ojbMappedClass, currentFiscalYear, fieldMethod, additionalCriterion);
        }

        // this method implements slash and burn mode for an business object
        // which requires an additional filtering criterion to select the rows
        // to be copied from the previous year
        private void genericSlashAndBurn(Class ojbMappedClass, Integer currentFiscalYear, Criteria additionalCriterion) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method genericMethod = null;
            genericSlashAndBurn(ojbMappedClass, currentFiscalYear, genericMethod, additionalCriterion);
        }

        private void genericSlashAndBurn(Class ojbMappedClass, Integer currentFiscalYear, Method fieldMethod, Criteria additionalCriterion) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
            Integer rowsRead = new Integer(0);
            Integer rowsWritten = new Integer(0);
            Integer newFiscalYear = currentFiscalYear + 1;
            String requestYearString = newFiscalYear.toString();
            // delete all the rows for the new year
            deleteNewYearRows(newFiscalYear, ojbMappedClass);
            // get the rows from the previous year
            Criteria criteriaID = new Criteria();
            criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentFiscalYear);
            if (additionalCriterion != null) {
                criteriaID.addAndCriteria(additionalCriterion);
            }
            QueryByCriteria queryID = new QueryByCriteria(ojbMappedClass, criteriaID);
            Iterator<T> oldYearObjects = getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
            while (oldYearObjects.hasNext()) {
                rowsRead = rowsRead + 1;
                T ourBO = oldYearObjects.next();
                // we have to set the fiscal year and the version number
                setCommonFields(ourBO, newFiscalYear);
                if (!(fieldMethod == null)) {
                    Object[] args = { currentFiscalYear, newFiscalYear, ourBO };
                    fieldMethod.invoke(FiscalYearIntiatorDaoOjb.this, args);
                    // fieldMethod.invoke(this, args);
                }
                // store the result
                getPersistenceBrokerTemplate().store(ourBO);
                rowsWritten = rowsWritten + 1;
            }
            LOG.warn(String.format("\n%s:\n%d read = %d\n%d written = %d", ojbMappedClass.getName(), currentFiscalYear, rowsRead, newFiscalYear, rowsWritten));
            if (fieldMethod != null) {
                // reset the method to private, just to be safe
                fieldMethod.setAccessible(false);
            }
            getPersistenceBrokerTemplate().clearCache();
        }

        private void setCommonFields(T ourBO, Integer newFiscalYear) throws NoSuchFieldException, IllegalAccessException {
            // the compiler doesn't know the class of the object at this point,
            // so we can't use the standard methods
            //
            // set the fiscal year
            Field fld = ourBO.getClass().getDeclaredField(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            fld.setAccessible(true);
            fld.set(ourBO, (Object) newFiscalYear);
            // set the version number (to avoid running up the meter as the
            // years fly by)
            // the version number is a field of the base class common to all
            // persistable business objects
            fld = ourBO.getClass().getSuperclass().getDeclaredField(KFSPropertyConstants.VERSION_NUMBER);
            fld.setAccessible(true);
            fld.set(ourBO, (Object) (new Long(0)));
        }

    };

    // @@TODO: remove this test routine
    public void testUpdateTwoDigitYear() {
        String oldYear = new String("08");
        String newYear = new String("11");
        String testString = new String("08-09 x 08 07-08 08-09 09 08 08");
        String newString = updateTwoDigitYear(newYear, oldYear, testString);
        LOG.warn(String.format("\n test of updateTwoDigitYear:\n input = %s\n output = %s\n from: %s, to:%s  ", testString, newString, oldYear, newYear));
        testString = new String("x08-09 x 08 07-08 08-09 09 tail");
        newString = updateTwoDigitYear(newYear, oldYear, testString);
        LOG.warn(String.format("\n test of updateTwoDigitYear:\n input = %s\n output = %s\n from: %s, to:%s  ", testString, newString, oldYear, newYear));
        testString = new String(" nada ");
        newString = updateTwoDigitYear(newYear, oldYear, testString);
        LOG.warn(String.format("\n test of updateTwoDigitYear:\n input = %s\n output = %s\n from: %s, to:%s  ", testString, newString, oldYear, newYear));
    }
}
