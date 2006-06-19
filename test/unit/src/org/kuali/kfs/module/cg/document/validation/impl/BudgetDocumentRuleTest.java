/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.bo.BudgetPeriod;
import org.kuali.module.kra.bo.BudgetTask;
import org.kuali.module.kra.rules.budget.BudgetDocumentRule;
import org.kuali.test.KualiTestBaseWithFixtures;


/**
 * Test basic rule methods of <code>{@link BudgetDocumentRuleBase}</code> convenience class.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetDocumentRuleTest extends KualiTestBaseWithFixtures {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetDocumentRuleTest.class);

    private DateTimeService dateTimeService;
    private BudgetDocumentRule budgetDocumentRule;
    private String MINIMUM_NUMBER_OF_TASKS;

    protected void setUp() throws Exception {
        super.setUp();
        budgetDocumentRule = new BudgetDocumentRule();
        dateTimeService = SpringServiceLocator.getDateTimeService();
        MINIMUM_NUMBER_OF_TASKS = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "minimumNumberOfTasks");
    }

    public void testValidPeriods() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2006-01-01"));
        period1.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2006-12-31"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1));

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2006-07-01"));
        period2.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2007-06-30"));

        budgetDocumentRule.isPeriodValid(period2, "period 2", new Integer(1));

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testValidPeriodLeapYear() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2008-01-01"));
        period1.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2008-12-31"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1));

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2008-02-28"));
        period2.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2009-02-27"));

        budgetDocumentRule.isPeriodValid(period2, "period 2", new Integer(1));

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2007-03-31"));
        period3.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2008-02-29"));

        budgetDocumentRule.isPeriodValid(period3, "period 3", new Integer(1));

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testInvalidPeriodStartAfterEnd() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2009-01-01"));
        period1.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2008-12-31"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1));

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.size() == 1);
    }

    public void testInvalidPeriodLength() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2007-01-01"));
        period1.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2008-12-31"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1));

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.size() == 1);
    }

    public void testValidConsecutivePeriodList() throws Exception {
        List periodList = new ArrayList();

        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2008-01-01"));
        period1.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2008-12-31"));

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2009-01-01"));
        period2.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2009-12-31"));

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2010-01-01"));
        period3.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2010-12-31"));

        BudgetPeriod period4 = new BudgetPeriod();
        period4.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2011-01-01"));
        period4.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2011-12-31"));

        BudgetPeriod period5 = new BudgetPeriod();
        period5.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2012-01-01"));
        period5.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2012-11-26"));

        periodList.add(period1);
        periodList.add(period2);
        periodList.add(period3);
        periodList.add(period4);
        periodList.add(period5);

        budgetDocumentRule.isPeriodListValid(periodList, false);

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testInvalidConsecutivePeriodList() throws Exception {
        List periodList = new ArrayList();

        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodSequenceNumber(new Integer(1));
        period1.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2008-01-01"));
        period1.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2008-12-31"));

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodSequenceNumber(new Integer(2));
        period2.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2009-01-02"));
        period2.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2010-01-01"));

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodSequenceNumber(new Integer(3));
        period3.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2010-02-06"));
        period3.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2010-12-31"));

        BudgetPeriod period4 = new BudgetPeriod();
        period4.setBudgetPeriodSequenceNumber(new Integer(4));
        period4.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2010-12-01"));
        period4.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2011-11-30"));

        BudgetPeriod period5 = new BudgetPeriod();
        period5.setBudgetPeriodSequenceNumber(new Integer(5));
        period5.setBudgetPeriodBeginDate(dateTimeService.convertToSqlDate("2012-01-01"));
        period5.setBudgetPeriodEndDate(dateTimeService.convertToSqlDate("2012-11-26"));

        periodList.add(period1);
        periodList.add(period2);
        periodList.add(period3);
        periodList.add(period4);
        periodList.add(period5);

        budgetDocumentRule.isPeriodListValid(periodList, false);

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue("should be 4, was " + errorMap.size(), errorMap.size() == 4);
    }

    public void testValidTaskList() throws Exception {
        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue()));
        Map errorMap1 = GlobalVariables.getErrorMap();
        assertTrue(errorMap1.isEmpty());

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue()));
        Map errorMap2 = GlobalVariables.getErrorMap();
        assertTrue(errorMap2.isEmpty());

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue()));
        Map errorMap3 = GlobalVariables.getErrorMap();
        assertTrue(errorMap3.isEmpty());
    }

    /**
     * Test for an empty tak list.
     * 
     * @throws Exception
     */
    public void testNotEnoughTasks() throws Exception {
        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue() - 1));
        Map errorMap1 = GlobalVariables.getErrorMap();
        assertTrue(errorMap1.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue() - 1));
        Map errorMap2 = GlobalVariables.getErrorMap();
        assertTrue(errorMap2.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue() - 1));
        Map errorMap3 = GlobalVariables.getErrorMap();
        assertTrue(errorMap3.size() == 1);
    }

    /**
     * Test for a task list that is too big.
     */
    public void testTooManyTasks() throws Exception {
        budgetDocumentRule.isTaskListValid(buildTaskList(KraConstants.maximumNumberOfTasks + 1));
        Map errorMap1 = GlobalVariables.getErrorMap();
        assertTrue(errorMap1.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(KraConstants.maximumNumberOfTasks + 1));
        Map errorMap2 = GlobalVariables.getErrorMap();
        assertTrue(errorMap2.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(KraConstants.maximumNumberOfTasks + 1));
        Map errorMap3 = GlobalVariables.getErrorMap();
        assertTrue(errorMap3.size() == 1);
    }

    private List buildTaskList(int x) {
        List taskList = new ArrayList();
        while (x > 0) {
            BudgetTask task = new BudgetTask();
            taskList.add(task);
            x--;
        }
        return taskList;
    }
}
