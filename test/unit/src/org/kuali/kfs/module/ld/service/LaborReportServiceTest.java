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
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.Message;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.util.LaborTestDataPreparator;
import org.kuali.module.labor.util.ReportRegistry;
import org.kuali.test.ConfigureContext;
import org.kuali.test.util.TestDataPreparator;

/**
 * This class...
 */
@ConfigureContext
public class LaborReportServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private OriginEntryGroup group1, group2, invalidGroup;
    private Map fieldValues;
    private String reportsDirectory;
    private Date today;

    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private LaborReportService laborReportService;
    private BusinessObjectService businessObjectService;
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

        laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);
        originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        laborReportService = SpringContext.getBean(LaborReportService.class);
        laborPosterTransactionValidator = SpringContext.getBeansOfType(VerifyTransaction.class).get("laborPosterTransactionValidator");
        reportsDirectory = ReportRegistry.getReportsDirectory();

        today = (SpringContext.getBean(DateTimeService.class)).getCurrentSqlDate();
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
        return LaborTestDataPreparator.getLaborOriginEntryList(properties, propertyKeyPrefix, numberOfInputData, group);
    }
}
