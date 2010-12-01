package org.kuali.kfs.sys.batch;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Tests the updated logic for the addition of the RUN_DATE parameter used by the Job class.
 * 
 * Tests 2a, 2b, 5a, 5b, 9, and 'multipleDatesNoMatch' should result in a skipped step. The others should not skip the step.
 */
@ConfigureContext
public class JobTest extends KualiTestBase {
    
    private static final String PARAMETER_CACHE_PREFIX = "Parameter:"; //ParameterServiceBase.PARAMETER_CACHE_PREFIX
    private static final String PARAMETER_CACHE_GROUP_NAME = "SystemParameter"; //ParameterServiceBase.PARAMETER_CACHE_GROUP_NAME
    private static final String PARAMETER_VALUE_SEPARATOR = ";";
    
    private static final String STEP_RUN_PARM_NM = Job.STEP_RUN_PARM_NM;
    private static final String STEP_RUN_ON_DATE_PARM_NM = Job.STEP_RUN_ON_DATE_PARM_NM;
    private static final String PARM_TYP_CD = "CONFG";
    private static final String PARM_CONSTR_CD = "A";

    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    private KualiConfigurationService kualiConfigurationService;
    
    private Step step;
    private Date jobRunDate;
    
    private Class stepClass;
    private String dateFormat;
    private String appCode;
    
    /**
     * The step used for testing (iu.testFailureStep) is unimportant (arbitrary). 
     * We're testing the value returned by Job.skipStep() when the various combinations of values for the parameters RUN_IND and RUN_DATE are set.
     * If Job.skipStep() returns true then Job.runStep() will not execute the Step; if it returns false the Job.runStep will execute the Step. 
     * 
     * The value for jobRunDate will always be the current date and the values for RUN_DATE will be relative to the current date.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        parameterService = SpringContext.getBean(ParameterService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        
        step = BatchSpringContext.getStep("iu.testFailureStep");    
        if (step == null) {
            throw new Exception("Unable to find bean for step: iu.testFailureStep");
        }
        Step unProxiedStep = (Step) ProxyUtils.getTargetIfProxied(step);
        stepClass = unProxiedStep.getClass();        
        
        jobRunDate = dateTimeService.getCurrentDate();
        dateFormat = parameterService.getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KNSConstants.SystemGroupParameterNames.DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE);
        appCode =    kualiConfigurationService.getPropertyString(KNSConstants.APPLICATION_CODE);
    }
    
    /**
     * The method we're testing.
     * 
     * @return true if the step should be skipped based on RUN_IND and RUN_DATE values, false if the step should run.
     */
    private boolean skipStep() {
        return Job.skipStep(parameterService, step, jobRunDate);
    }
    
    /**
     * This method sets the values for the RUN_IND and RUN_DATE in the parameter table
     * 
     * @param runIndExists does RUN_IND exist?
     * @param runIndValue RUN_IND value
     * @param runDateExists does RUN_DATE exist?
     * @param runDateValue RUN_DATE value
     */
    private void setParameters(boolean runIndExists, String runIndValue, boolean runDateExists, Date runDateValue) {                
        Date[] runDateValues = {runDateValue};
        setParameters(runIndExists, runIndValue, runDateExists, runDateValues);
    }
    
    private void setParameters(boolean runIndExists, String runIndValue, boolean runDateExists, Date[] runDateValues) {
        if (runIndExists) {
            setRunInd(runIndValue);
        }
        else {
            removeRunInd();
        }
        
        if (runDateExists) {
            setRunDates(runDateValues);
        }
        else {
            removeRunDate();
        }        
    }
    
    private void setRunInd(String runInd) {
        setParameter(STEP_RUN_PARM_NM, runInd);
    }
    
    private void setRunDates(Date[] runDates) {
        if (runDates == null) {
            setParameter(STEP_RUN_ON_DATE_PARM_NM, "");
        }
        else if (runDates.length == 1 ) {
            setParameter(STEP_RUN_ON_DATE_PARM_NM, (runDates[0] == null ? "" : dateTimeService.toString(runDates[0], dateFormat)));
        }
        else {
            String runDateStr = "";
            for(Date date : runDates) {
                runDateStr += (date == null ? "" : dateTimeService.toString(date, dateFormat)+ PARAMETER_VALUE_SEPARATOR);
            }
            setParameter(STEP_RUN_ON_DATE_PARM_NM, runDateStr);
        }
    }
    
    private void removeRunInd() {
        removeParameter(STEP_RUN_PARM_NM);
    }
    
    private void removeRunDate() {
        removeParameter(STEP_RUN_ON_DATE_PARM_NM);
    }
    
    /**
     * Adds the parameter to the parameter table if one doesn't exist, updates the existing value if it does
     * 
     * @param parameterName the name of the parameter to be added or updated
     * @param parameterText the value of the parameter
     */
    private void setParameter(String parameterName, String parameterText) {
        Parameter parameter = getParameter(parameterName);
        parameter.setParameterValue(parameterText);
        businessObjectService.save(parameter);
        insertIntoCache(parameter);
    } 
    
    /**
     * Removes the parameter from the parameter table if it exists.
     * 
     * @param parameterName the name of the parameter to be removed
     */
    private void removeParameter(String parameterName) {
        if (parameterService.parameterExists(stepClass, parameterName)) {
            Parameter parameter = getParameter(parameterName);
            businessObjectService.delete(parameter);
            flushParameterFromCache(parameterName);
        }
    } 
    
    /**
     * Returns the parameter if it exists, returns a new instance if it doesn't.
     * 
     * @param parameterName the name of the parameter to retrieve
     * @return the parameter
     */
    private Parameter getParameter(String parameterName) {
        String namespaceCode = parameterService.getNamespace(stepClass);
        String detailTypeCode = parameterService.getDetailType(stepClass);
        
        if (parameterService.parameterExists(stepClass, parameterName)) {
            return parameterService.retrieveParameter(namespaceCode, detailTypeCode, parameterName);
        }
        else {
            Parameter parameter = new Parameter();
            parameter.setParameterNamespaceCode(namespaceCode);
            parameter.setParameterDetailTypeCode(detailTypeCode);
            parameter.setParameterName(parameterName);
            parameter.setParameterTypeCode(PARM_TYP_CD);
            parameter.setParameterConstraintCode(PARM_CONSTR_CD);
            parameter.setParameterApplicationNamespaceCode(appCode);
            
            return parameter;
        }
    }
    
    /**
     * Inserts the given Parameter into the cache.  If the Parameter is already in the cache,
     * these entries should  be overwritten.
     */
    private void insertIntoCache(Parameter parameter) {
        if (parameter == null) {
            return;
        }
        KEWServiceLocator.getCacheAdministrator().putInCache(getParameterCacheKey(parameter.getParameterNamespaceCode(), parameter.getParameterDetailTypeCode(), parameter.getParameterName()), parameter, PARAMETER_CACHE_GROUP_NAME);
    }
    
    /**
     * Removes the parameter from the cache
     */
    private void flushParameterFromCache(String parameterName) {        
        KEWServiceLocator.getCacheAdministrator().flushEntry(getParameterCacheKey(parameterName));
    }
    
    /**
     * Returns the cache key for the given parameter name
     */
    private String getParameterCacheKey(String parameterName) {
        String namespaceCode = parameterService.getNamespace(stepClass);
        String detailTypeCode = parameterService.getDetailType(stepClass);
        
        return getParameterCacheKey(namespaceCode, detailTypeCode, parameterName);
    }
    
    private String getParameterCacheKey(String namespaceCode, String detailTypeCode, String parameterName) {
        return PARAMETER_CACHE_PREFIX + namespaceCode + "-" + detailTypeCode + "-" + parameterName;
    }
    
    /**
     * Returns the day after the date given
     * 
     * @param todaysDate
     * @return the date after the date given
     */
    private Date getTomorrowsDate(Date todaysDate) {
        Calendar todaysCalendar = dateTimeService.getCalendar(todaysDate);
        todaysCalendar.add(Calendar.DAY_OF_YEAR, 1);        
        
        Date tomorrowsDate = todaysCalendar.getTime();
        
        return tomorrowsDate;
    }
    
    /**
     * Returns a day one month prior to the date given
     * 
     * @param todaysDate
     * @return the date one month prior to the date given
     */
    private Date getLastMonthsDate(Date todaysDate) {
        Calendar todaysCalendar = dateTimeService.getCalendar(todaysDate);
        todaysCalendar.add(Calendar.MONTH, -1);        
        
        Date lastMonthsDate = todaysCalendar.getTime();
        
        return lastMonthsDate;
    }
    
    /**
     * Returns a day one month from the date given
     * 
     * @param todaysDate
     * @return the date one month from the date given
     */
    private Date getNextMonthsDate(Date todaysDate) {
        Calendar todaysCalendar = dateTimeService.getCalendar(todaysDate);
        todaysCalendar.add(Calendar.MONTH, 1);        
        
        Date nextMonthsDate = todaysCalendar.getTime();
        
        return nextMonthsDate;
    }
    
    
    /**
     * Returns a day one year from the date given
     * 
     * @param todaysDate
     * @return the date one year from the date given
     */
    private Date getNextYearsDate(Date todaysDate) {
        Calendar todaysCalendar = dateTimeService.getCalendar(todaysDate);
        todaysCalendar.add(Calendar.YEAR, 1);        
        
        Date nextYearsDate = todaysCalendar.getTime();
        
        return nextYearsDate;
    }
    

    /////////////////////TESTS//////////////////////////
    
    /**
     * RUN_IND exists, value equal to 'Y'
     * RUN_DATE does not exist
     * 
     * skipStep? false
     * 
     * When RUN_IND == 'Y' the presence and value of RUN_DATE should be ignored (tests 1a,1b,1c,1d)
     */
    public void testSkipStep_1a() {
        setParameters(true, "Y", false, (Date)null);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }

    /**
     * RUN_IND exists, value equal to 'Y'
     * RUN_DATE exists, value is null
     * 
     * skipStep? false
     */
    public void testSkipStep_1b() {        
        setParameters(true, "Y", true, (Date)null);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }

    /**
     * RUN_IND exists, value equal to 'Y'
     * RUN_DATE exists, value equals today's date
     * 
     * skipStep? false
     */
    public void testSkipStep_1c() {        
        setParameters(true, "Y", true, jobRunDate);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }

    /**
     * RUN_IND exists, value equal to 'Y'
     * RUN_DATE exists, value does not equal today's date
     * 
     * skipStep? false
     */
    public void testSkipStep_1d() {                
        Date tomorrowsDate = getTomorrowsDate(jobRunDate);
        
        setParameters(true, "Y", true, tomorrowsDate);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }
    
    
    /**
     * RUN_IND exists, value not equal to 'Y' (value is null)
     * RUN_DATE does not exist
     * 
     * skipStep? true
     */
    public void testSkipStep_2a() {        
        setParameters(true, null, false, (Date)null);
        
        boolean result = skipStep();
        assertEquals(true, result);
    }
    
    /**
     * RUN_IND exists, value not equal to 'Y' (value equal to 'N')
     * RUN_DATE does not exist
     * 
     * skipStep? true
     */
    public void testSkipStep_2b() {        
        setParameters(true, "N", false, (Date)null);
        
        boolean result = skipStep();
        assertEquals(true, result);        
    }
    
    /**
     * RUN_IND exists, value not equal to 'Y' (value is null)
     * RUN_DATE exists, value is null
     * 
     * skipStep? false
     */
    public void testSkipStep_3a() {        
        setParameters(true, null, true, (Date)null);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }
    
    /**
     * RUN_IND exists, value not equal to 'Y' (value equal to 'N')
     * RUN_DATE exists, value is null
     * 
     * skipStep? false
     */
    public void testSkipStep_3b() {        
        setParameters(true, "N", true, (Date)null);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }
    
    /**
     * RUN_IND exists, value not equal to 'Y' (value is null)
     * RUN_DATE exists, value equals today's date
     * 
     * skipStep? false
     */
    public void testSkipStep_4a() {        
        setParameters(true, null, true, jobRunDate);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }
    
    /**
     * RUN_IND exists, value not equal to 'Y' (value equal to 'N')
     * RUN_DATE exists, value equals today's date
     * 
     * skipStep? false
     */
    public void testSkipStep_4b() {        
        setParameters(true, "N", true, jobRunDate);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }
    
    /**
     * RUN_IND exists, value not equal to 'Y' (value is null)
     * RUN_DATE exists, value does not equal today's date
     * 
     * skipStep? true
     */
    public void testSkipStep_5a() {        
        Date tomorrowsDate = getTomorrowsDate(jobRunDate);
        
        setParameters(true, null, true, tomorrowsDate);
        
        boolean result = skipStep();
        assertEquals(true, result);
    }
    
    /**
     * RUN_IND exists, value not equal to 'Y' (value equal to 'N')
     * RUN_DATE exists, value does not equal today's date
     * 
     * skipStep? true
     */
    public void testSkipStep_5b() {        
        Date tomorrowsDate = getTomorrowsDate(jobRunDate);
        
        setParameters(true, "N", true, tomorrowsDate);
        
        boolean result = skipStep();
        assertEquals(true, result);
    }
    
    /**
     * RUN_IND does not exist
     * RUN_DATE does not exist
     * 
     * skipStep? false
     */
    public void testSkipStep_6() {        
        setParameters(false, null, false, (Date)null);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }
    
    /**
     * RUN_IND does not exist
     * RUN_DATE exists, value is null
     * 
     * skipStep? false
     */
    public void testSkipStep_7() {        
        setParameters(false, null, true, (Date)null);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }
    
    /**
     * RUN_IND does not exist
     * RUN_DATE exists, value equals today's date
     * 
     * skipStep? false
     */
    public void testSkipStep_8() {        
        setParameters(false, null, true, jobRunDate);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }
    
    /**
     * RUN_IND does not exist
     * RUN_DATE exists, value does not equal today's date
     * 
     * skipStep? true
     */
    public void testSkipStep_9() {        
        Date tomorrowsDate = getTomorrowsDate(jobRunDate);
        
        setParameters(false, null, true, tomorrowsDate);
        
        boolean result = skipStep();
        assertEquals(true, result);
    }    
    
    /**
     * RUN_IND does not exist
     * RUN_DATE exists, value contains a list of dates, one of which is today's date
     * 
     * skipStep? false
     */
    public void testSkipStep_multipleDatesWithMatch() {
        Date[] listOfDates = { getLastMonthsDate(jobRunDate), jobRunDate, getTomorrowsDate(jobRunDate), getNextMonthsDate(jobRunDate)};
        
        setParameters(false, null, true, listOfDates);
        
        boolean result = skipStep();
        assertEquals(false, result);
    }    
    
    /**
     * RUN_IND does not exist
     * RUN_DATE exists, value contains a list of dates, none of which is today's date
     * 
     * skipStep? true
     */
    public void testSkipStep_multipleDatesNoMatch() {
        Date[] listOfDates = { getLastMonthsDate(jobRunDate), getTomorrowsDate(jobRunDate), getNextMonthsDate(jobRunDate), getNextYearsDate(jobRunDate)};
        
        setParameters(false, null, true, listOfDates);
        
        boolean result = skipStep();
        assertEquals(true, result);
    }
}
