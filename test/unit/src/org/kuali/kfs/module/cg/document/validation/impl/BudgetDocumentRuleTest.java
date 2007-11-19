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
package org.kuali.module.kra.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.rules.budget.BudgetDocumentRule;
import org.kuali.test.ConfigureContext;


/**
 * Test basic rule methods of <code>{@link BudgetDocumentRuleBase}</code> convenience class.
 */
@ConfigureContext
public class BudgetDocumentRuleTest extends KualiTestBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetDocumentRuleTest.class);

    private BudgetDocumentRule budgetDocumentRule;
    private String MINIMUM_NUMBER_OF_TASKS;
    private String MAXIMUM_NUMBER_OF_TASKS;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        budgetDocumentRule = new BudgetDocumentRule();
        MAXIMUM_NUMBER_OF_TASKS = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, KraConstants.MAXIMUM_NUMBER_OF_TASKS);
        MINIMUM_NUMBER_OF_TASKS = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, KraConstants.MINIMUM_NUMBER_OF_TASKS);
    }

    public void testValidPeriods() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2006-01-01"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2006-12-31"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1), true);

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2006-07-01"));
        period2.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2007-06-30"));

        budgetDocumentRule.isPeriodValid(period2, "period 2", new Integer(1), true);

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testValidPeriodLeapYear() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-01-01"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-12-31"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1), true);

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-02-28"));
        period2.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2009-02-27"));

        budgetDocumentRule.isPeriodValid(period2, "period 2", new Integer(1), true);

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2007-03-31"));
        period3.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-02-29"));

        budgetDocumentRule.isPeriodValid(period3, "period 3", new Integer(1), true);

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testInvalidPeriodStartAfterEnd() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2009-01-01"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-12-31"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1), true);

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.size() == 1);
    }

    public void testInvalidPeriodLength() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2007-01-01"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-12-31"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1), true);

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.size() == 1);
    }

    public void testValidConsecutivePeriodList() throws Exception {
        List periodList = new ArrayList();

        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-01-01"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-12-31"));

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2009-01-01"));
        period2.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2009-12-31"));

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2010-01-01"));
        period3.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2010-12-31"));

        BudgetPeriod period4 = new BudgetPeriod();
        period4.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2011-01-01"));
        period4.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2011-12-31"));

        BudgetPeriod period5 = new BudgetPeriod();
        period5.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2012-01-01"));
        period5.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2012-11-26"));

        periodList.add(period1);
        periodList.add(period2);
        periodList.add(period3);
        periodList.add(period4);
        periodList.add(period5);

        budgetDocumentRule.isPeriodListValid(periodList, false, true);

        Map errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testInvalidConsecutivePeriodList() throws Exception {
        List periodList = new ArrayList();

        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodSequenceNumber(new Integer(1));
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-01-01"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2008-12-31"));

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodSequenceNumber(new Integer(2));
        period2.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2009-01-02"));
        period2.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2010-01-01"));

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodSequenceNumber(new Integer(3));
        period3.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2010-02-06"));
        period3.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2010-12-31"));

        BudgetPeriod period4 = new BudgetPeriod();
        period4.setBudgetPeriodSequenceNumber(new Integer(4));
        period4.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2010-12-01"));
        period4.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2011-11-30"));

        BudgetPeriod period5 = new BudgetPeriod();
        period5.setBudgetPeriodSequenceNumber(new Integer(5));
        period5.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2012-01-01"));
        period5.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("2012-11-26"));

        periodList.add(period1);
        periodList.add(period2);
        periodList.add(period3);
        periodList.add(period4);
        periodList.add(period5);

        budgetDocumentRule.isPeriodListValid(periodList, false, true);

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
        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MAXIMUM_NUMBER_OF_TASKS) + 1));
        Map errorMap1 = GlobalVariables.getErrorMap();
        assertTrue(errorMap1.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MAXIMUM_NUMBER_OF_TASKS) + 1));
        Map errorMap2 = GlobalVariables.getErrorMap();
        assertTrue(errorMap2.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MAXIMUM_NUMBER_OF_TASKS) + 1));
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
