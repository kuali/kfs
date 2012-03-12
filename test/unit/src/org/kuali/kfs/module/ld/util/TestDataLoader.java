/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.util;

import static org.kuali.kfs.gl.businessobject.OriginEntrySource.LABOR_BACKUP;
import static org.kuali.kfs.gl.businessobject.OriginEntrySource.LABOR_SCRUBBER_VALID;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.Log4jConfigurer;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.SpringContextForBatchRunner;
import org.kuali.rice.krad.service.BusinessObjectService;

public class TestDataLoader {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestDataLoader.class);

    private Properties properties;
    private String fieldNames;
    private String fieldLength;
    private String deliminator;

    private List<String> keyFieldList;
    private List<String> fieldLengthList;

    private BusinessObjectService businessObjectService;
    private LaborOriginEntryService laborOriginEntryService;

    public TestDataLoader() {
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborTransaction.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);

        fieldNames = properties.getProperty("fieldNames");
        fieldLength = properties.getProperty("fieldLength");
        deliminator = properties.getProperty("deliminator");

        Log4jConfigurer.configureLogging(false);

        SpringContextForBatchRunner.initializeKfs();
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));
        fieldLengthList = Arrays.asList(StringUtils.split(fieldLength, deliminator));
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);
    }

    public int loadTransactionIntoPendingEntryTable() {
        int numberOfInputData = Integer.valueOf(properties.getProperty("numOfData"));
        int[] fieldLength = this.getFieldLength(fieldLengthList);
        return this.loadInputData("data", numberOfInputData, keyFieldList, fieldLength);
    }

    public int loadTransactionIntoOriginEntryTable(OriginEntryGroup group) {
        int numberOfInputData = Integer.valueOf(properties.getProperty("numOfData"));
        businessObjectService.save(group);

        int[] fieldLength = this.getFieldLength(fieldLengthList);
        List<LaborOriginEntry> originEntries = this.loadInputData(LaborOriginEntry.class, "data", numberOfInputData, keyFieldList, fieldLength);
        for (LaborOriginEntry entry : originEntries) {
            entry.setEntryGroupId(group.getId());
        }

        businessObjectService.save(originEntries);
        return originEntries.size();
    }

    public int loadTransactionIntoGLEntryTable() {
        int numberOfInputData = Integer.valueOf(properties.getProperty("numOfData"));
        int[] fieldLength = this.getFieldLength(fieldLengthList);

        List<LaborGeneralLedgerEntry> laborGLEntries = this.loadInputData(LaborGeneralLedgerEntry.class, "data", numberOfInputData, keyFieldList, fieldLength);
        businessObjectService.save(laborGLEntries);

        return laborGLEntries.size();
    }

    private int loadInputData(String propertyKeyPrefix, int numberOfInputData, List<String> keyFieldList, int[] fieldLength) {
        int count = 0;
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            PendingLedgerEntryForTesting inputData = new PendingLedgerEntryForTesting();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldLength, keyFieldList);

            if (businessObjectService.countMatching(LaborLedgerPendingEntry.class, inputData.getPrimaryKeyMap()) <= 0) {
                inputData.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
                businessObjectService.save(inputData);
                count++;
            }
        }
        return count;
    }

    private List loadInputData(Class clazz, String propertyKeyPrefix, int numberOfInputData, List<String> keyFieldList, int[] fieldLength) {
        List inputDataList = new ArrayList();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            try {
                Object inputData = clazz.newInstance();
                ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldLength, keyFieldList);

                inputDataList.add(inputData);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inputDataList;
    }

    private int[] getFieldLength(List<String> fieldLengthList) {
        int[] fieldLengthArray = new int[fieldLengthList.size()];
        for (int i = 0; i < fieldLengthArray.length; i++) {
            fieldLengthArray[i] = Integer.valueOf(fieldLengthList.get(i).trim());
        }
        return fieldLengthArray;
    }

    public static void main(String[] args) {
        TestDataLoader testDataLoader = new TestDataLoader();
        Date groupCreationDate = new Date(0);

        if (ArrayUtils.isEmpty(args) || args.length < 2) {
            System.out.println("The program requires at least two arguments.");
            return;
        }

        if (!StringUtils.isAlphanumeric(args[0])) {
            System.out.println("The first argument should be a number.");
            return;
        }

        for (int numOfRound = Integer.parseInt(args[0]); numOfRound > 0; numOfRound--) {
            if (ArrayUtils.contains(args, "poster")) {
                OriginEntryGroup group = new OriginEntryGroup();
                group.setSourceCode(LABOR_SCRUBBER_VALID);
                group.setValid(true);
                group.setScrub(false);
                group.setProcess(true);
                group.setDate(groupCreationDate);
                int numOfData = testDataLoader.loadTransactionIntoOriginEntryTable(group);
                System.out.println("Number of Origin Entries for Poster = " + numOfData);
            }

            if (ArrayUtils.contains(args, "scrubber")) {
                OriginEntryGroup group = new OriginEntryGroup();
                group.setSourceCode(LABOR_BACKUP);
                group.setValid(true);
                group.setScrub(true);
                group.setProcess(true);
                group.setDate(groupCreationDate);
                int numOfData = testDataLoader.loadTransactionIntoOriginEntryTable(group);
                System.out.println("Number of Origin Entries for Scrubber = " + numOfData);
            }

            if (ArrayUtils.contains(args, "pending")) {
                int numOfData = testDataLoader.loadTransactionIntoPendingEntryTable();
                System.out.println("Number of Pending Entries = " + numOfData);
            }

            if (ArrayUtils.contains(args, "glentry")) {
                int numOfData = testDataLoader.loadTransactionIntoGLEntryTable();
                System.out.println("Number of Labor GL Entries = " + numOfData);
            }
        }
        System.exit(0);
    }
}
