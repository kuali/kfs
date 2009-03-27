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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.businessobject.BudgetTask;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;


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
        MAXIMUM_NUMBER_OF_TASKS = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.MAXIMUM_NUMBER_OF_TASKS);
        MINIMUM_NUMBER_OF_TASKS = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.MINIMUM_NUMBER_OF_TASKS);
    }

    public void testValidPeriods() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2006"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2006"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1), true);

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("07-01-2006"));
        period2.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("06-30-2007"));

        budgetDocumentRule.isPeriodValid(period2, "period 2", new Integer(1), true);

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testValidPeriodLeapYear() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2008"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2008"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1), true);

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("02-28-2008"));
        period2.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("02-27-2009"));

        budgetDocumentRule.isPeriodValid(period2, "period 2", new Integer(1), true);

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("03-31-2007"));
        period3.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("02-29-2008"));

        budgetDocumentRule.isPeriodValid(period3, "period 3", new Integer(1), true);

        errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testInvalidPeriodStartAfterEnd() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2009"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2008"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1), true);

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.size() == 1);
    }

    public void testInvalidPeriodLength() throws Exception {
        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2007"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2008"));

        budgetDocumentRule.isPeriodValid(period1, "period 1", new Integer(1), true);

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.size() == 1);
    }

    public void testValidConsecutivePeriodList() throws Exception {
        List periodList = new ArrayList();

        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2008"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2008"));

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2009"));
        period2.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2009"));

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2010"));
        period3.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2010"));

        BudgetPeriod period4 = new BudgetPeriod();
        period4.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2011"));
        period4.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2011"));

        BudgetPeriod period5 = new BudgetPeriod();
        period5.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2012"));
        period5.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("11-26-2012"));

        periodList.add(period1);
        periodList.add(period2);
        periodList.add(period3);
        periodList.add(period4);
        periodList.add(period5);

        budgetDocumentRule.isPeriodListValid(periodList, false, true);

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        assertTrue(errorMap.isEmpty());
    }

    public void testInvalidConsecutivePeriodList() throws Exception {
        List periodList = new ArrayList();

        BudgetPeriod period1 = new BudgetPeriod();
        period1.setBudgetPeriodSequenceNumber(new Integer(1));
        period1.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2008"));
        period1.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2008"));

        BudgetPeriod period2 = new BudgetPeriod();
        period2.setBudgetPeriodSequenceNumber(new Integer(2));
        period2.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-02-2009"));
        period2.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2010"));

        BudgetPeriod period3 = new BudgetPeriod();
        period3.setBudgetPeriodSequenceNumber(new Integer(3));
        period3.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("02-06-2010"));
        period3.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-31-2010"));

        BudgetPeriod period4 = new BudgetPeriod();
        period4.setBudgetPeriodSequenceNumber(new Integer(4));
        period4.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("12-01-2010"));
        period4.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("11-30-2011"));

        BudgetPeriod period5 = new BudgetPeriod();
        period5.setBudgetPeriodSequenceNumber(new Integer(5));
        period5.setBudgetPeriodBeginDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("01-01-2012"));
        period5.setBudgetPeriodEndDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate("11-26-2012"));

        periodList.add(period1);
        periodList.add(period2);
        periodList.add(period3);
        periodList.add(period4);
        periodList.add(period5);

        budgetDocumentRule.isPeriodListValid(periodList, false, true);

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        assertTrue("should be 4, was " + errorMap.size(), errorMap.size() == 4);
    }

    public void testValidTaskList() throws Exception {
        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue()));
        ErrorMap errorMap1 = GlobalVariables.getErrorMap();
        assertTrue(errorMap1.isEmpty());

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue()));
        ErrorMap errorMap2 = GlobalVariables.getErrorMap();
        assertTrue(errorMap2.isEmpty());

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue()));
        ErrorMap errorMap3 = GlobalVariables.getErrorMap();
        assertTrue(errorMap3.isEmpty());
    }

    /**
     * Test for an empty tak list.
     * 
     * @throws Exception
     */
    public void testNotEnoughTasks() throws Exception {
        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue() - 1));
        ErrorMap errorMap1 = GlobalVariables.getErrorMap();
        assertTrue(errorMap1.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue() - 1));
        ErrorMap errorMap2 = GlobalVariables.getErrorMap();
        assertTrue(errorMap2.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MINIMUM_NUMBER_OF_TASKS).intValue() - 1));
        ErrorMap errorMap3 = GlobalVariables.getErrorMap();
        assertTrue(errorMap3.size() == 1);
    }

    /**
     * Test for a task list that is too big.
     */
    public void testTooManyTasks() throws Exception {
        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MAXIMUM_NUMBER_OF_TASKS) + 1));
        ErrorMap errorMap1 = GlobalVariables.getErrorMap();
        assertTrue(errorMap1.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MAXIMUM_NUMBER_OF_TASKS) + 1));
        ErrorMap errorMap2 = GlobalVariables.getErrorMap();
        assertTrue(errorMap2.size() == 1);

        budgetDocumentRule.isTaskListValid(buildTaskList(new Integer(MAXIMUM_NUMBER_OF_TASKS) + 1));
        ErrorMap errorMap3 = GlobalVariables.getErrorMap();
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
