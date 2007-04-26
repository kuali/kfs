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
package org.kuali.module.labor.service;

import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_MAIN_POSTER_ERROR;
import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_MAIN_POSTER_VALID;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.PayrollAccrualSummaryTable;
import org.kuali.module.labor.util.ReportRegistry;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

/**
 * This class...
 */
@WithTestSpringContext
public class LaborReportServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private OriginEntryGroup group1, group2, invalidGroup;
    private Map fieldValues;
    private String reportsDirectory;
    private Date today;

    private BeanFactory beanFactory;
    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private LaborReportService laborReportService;
    private BusinessObjectService businessObjectService;
    private KualiConfigurationService kualiConfigurationService;
    private VerifyTransaction laborPosterTransactionValidator;
    private PersistenceService persistenceService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborReportService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        beanFactory = SpringServiceLocator.getBeanFactory();
        laborOriginEntryService = (LaborOriginEntryService) beanFactory.getBean("laborOriginEntryService");
        originEntryGroupService = (OriginEntryGroupService) beanFactory.getBean("glOriginEntryGroupService");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        persistenceService = (PersistenceService) beanFactory.getBean("persistenceService");

        laborReportService = (LaborReportService) beanFactory.getBean("laborReportService");

        kualiConfigurationService = (KualiConfigurationService) beanFactory.getBean("kualiConfigurationService");
        laborPosterTransactionValidator = (VerifyTransaction) beanFactory.getBean("laborPosterTransactionValidator");

        reportsDirectory = ReportRegistry.getReportsDirectory();

        today = ((DateTimeService) beanFactory.getBean("dateTimeService")).getCurrentSqlDate();
        group1 = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_VALID, true, true, false);
        group2 = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_VALID, true, true, false);
        invalidGroup = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_ERROR, false, true, false);

        LaborOriginEntry cleanup = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
    }

    public void testGenerateInputSummaryReport() throws Exception {
        String testTarget = "generateInputSummaryReport.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();

        laborReportService.generateInputSummaryReport(groups, ReportRegistry.LABOR_POSTER_INPUT, reportsDirectory, today);

        groups.add(group1);
        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, group1));
        groups.add(group2);
        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, group2));
        laborReportService.generateInputSummaryReport(groups, ReportRegistry.LABOR_POSTER_INPUT, reportsDirectory, today);
    }

    public void testGenerateErrorTransactionListing() throws Exception {
        String testTarget = "generateErrorTransactionListing.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));

        laborReportService.generateErrorTransactionListing(invalidGroup, ReportRegistry.LABOR_POSTER_ERROR, reportsDirectory, today);

        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, invalidGroup));
        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, invalidGroup));
        laborReportService.generateErrorTransactionListing(invalidGroup, ReportRegistry.LABOR_POSTER_ERROR, reportsDirectory, today);
    }

    public void testGenerateOutputSummaryReportByGroups() throws Exception {
        String testTarget = "generateOutputSummaryReport.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();

        laborReportService.generateOutputSummaryReport(groups, ReportRegistry.LABOR_POSTER_OUTPUT, reportsDirectory, today);

        groups.add(group1);
        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, group1));
        groups.add(group2);
        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, group2));
        laborReportService.generateOutputSummaryReport(groups, ReportRegistry.LABOR_POSTER_OUTPUT, reportsDirectory, today);
    }

    public void testGenerateOutputSummaryReportBySingleGroup() throws Exception {
        String testTarget = "generateOutputSummaryReport.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));

        laborReportService.generateOutputSummaryReport(group1, ReportRegistry.LABOR_POSTER_OUTPUT_BY_SINGLE_GROUP, reportsDirectory, today);

        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, group1));
        laborReportService.generateOutputSummaryReport(group1, ReportRegistry.LABOR_POSTER_OUTPUT_BY_SINGLE_GROUP, reportsDirectory, today);
    }

    public void testGenerateStatisticsReport() throws Exception {
        String testTarget = "generateStatisticsReport.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();

        List<Summary> reportSummary = this.getReportSummary();
        laborReportService.generateStatisticsReport(reportSummary, null, ReportRegistry.LABOR_POSTER_STATISTICS, reportsDirectory, today);

        groups.add(group1);
        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, group1));
        groups.add(group2);
        businessObjectService.save(getInputDataList(testTarget + "testData", numberOfTestData, group2));

        persistenceService.clearCache();

        Map<Transaction, List<Message>> errorMap = this.getErrorMap(groups);
        laborReportService.generateStatisticsReport(reportSummary, errorMap, ReportRegistry.LABOR_POSTER_STATISTICS, reportsDirectory, today);
    }

    public void testGenerateStatisticsReportFromStringList() throws Exception {

        PayrollAccrualSummaryTable accrualSummaryTable = new PayrollAccrualSummaryTable();
        List<String> reportSummary = PayrollAccrualSummaryTable.buildReportSummary(accrualSummaryTable);

        reportSummary.add(0, PayrollAccrualSummaryTable.buildReportSummaryLine("Base Accrual Percent", 0.50));
        laborReportService.generateStatisticsReport(reportSummary, ReportRegistry.PAYROLL_ACCRUAL_STATISTICS, reportsDirectory, today);
    }

    private List<Summary> getReportSummary() {
        List<Summary> reportSummary = new ArrayList<Summary>();
        for (int i = 0; i < 10; i++) {
            String destination = "Table " + i;
            reportSummary.add(new Summary(reportSummary.size() + 2, destination, 100 * i));
            reportSummary.add(new Summary(reportSummary.size() + 2, "", 0));
        }
        return reportSummary;
    }

    private Map<Transaction, List<Message>> getErrorMap(List<OriginEntryGroup> groups) {
        Map<Transaction, List<Message>> errorMap = new HashMap<Transaction, List<Message>>();
        for (Iterator<LaborOriginEntry> entry = laborOriginEntryService.getEntriesByGroups(groups); entry.hasNext();) {
            LaborOriginEntry originEntry = entry.next();

            List<Message> errors = laborPosterTransactionValidator.verifyTransaction(originEntry);
            if (!errors.isEmpty()) {
                errorMap.put(originEntry, errors);
            }
        }
        return errorMap;
    }

    private List getInputDataList(String propertyKeyPrefix, int numberOfInputData, OriginEntryGroup group) {
        return TestDataPreparator.getLaborOriginEntryList(properties, propertyKeyPrefix, numberOfInputData, group);
    }
}
