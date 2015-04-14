/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.batch.service;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.service.impl.LaborNightlyOutServiceImpl;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.module.ld.util.LaborOriginEntryFileIterator;
import org.kuali.kfs.module.ld.util.LaborOriginEntryForTesting;
import org.kuali.kfs.module.ld.util.PendingLedgerEntryForTesting;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.batch.BatchDirectoryHelper;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext
public class LaborNightlyOutServiceTest extends KualiTestBase {
    private Properties properties;
    private Map<String, Object> fieldValues;

    private BusinessObjectService businessObjectService;
    private LaborNightlyOutService laborNightlyOutService;

    private File nightlyOutputFile = null;
    private File nightlyOutputDoneFile = null;

    private BatchDirectoryHelper batchDirectoryHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborNightlyOutService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);

        String fieldNames = properties.getProperty("fieldNames");
        String documentFieldNames = properties.getProperty("documentFieldNames");
        String deliminator = properties.getProperty("deliminator");

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        laborNightlyOutService = SpringContext.getBean(LaborNightlyOutService.class);

        int numberOfDocuments = Integer.valueOf(properties.getProperty("document.numOfData"));
        List<DocumentHeader> inputDataList = TestDataPreparator.buildTestDataList(DocumentHeader.class, properties, "document.testData", documentFieldNames, deliminator, numberOfDocuments);
        businessObjectService.save(inputDataList);

        LaborLedgerPendingEntry cleanup = new LaborLedgerPendingEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        fieldValues.remove(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE); // this test isn't expecting the document approved code to be set, so let's simply remove it
        businessObjectService.deleteMatching(LaborLedgerPendingEntry.class, fieldValues);

        batchDirectoryHelper = new BatchDirectoryHelper("ld","originEntry");
        batchDirectoryHelper.createBatchDirectory();

        String nightlyOutputFileName = batchDirectoryHelper.getBatchFileDirectoryName() + File.separator + LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String nightlyOutputDoneFileName = batchDirectoryHelper.getBatchFileDirectoryName() + File.separator + LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;

        nightlyOutputFile = new File(nightlyOutputFileName);
        nightlyOutputDoneFile = new File(nightlyOutputDoneFileName);
    }

    public void testCopyApprovedPendingLedgerEntries() throws Exception {
        String testTarget = "copyApprovedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.copyApprovedPendingLedgerEntries();

        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));
        List<LaborOriginEntryForTesting> expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);

        Iterator<LaborOriginEntry> originEntries = new LaborOriginEntryFileIterator(nightlyOutputFile);
        int sizeOfOriginEntries = 0;
        while (originEntries.hasNext()) {
            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
            ObjectUtil.buildObject(originEntryForTesting, originEntries.next());

            sizeOfOriginEntries++;
            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
        }

        assertEquals(expectedNumOfData, sizeOfOriginEntries);
    }

    public void testCopyPendingLedgerEntries() throws Exception {
        String testTarget = "copyPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.copyApprovedPendingLedgerEntries();

        List<LaborOriginEntryForTesting> expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);

        Iterator<LaborOriginEntry> originEntries = new LaborOriginEntryFileIterator(nightlyOutputFile);
        int sizeOfOriginEntries = 0;
        while (originEntries.hasNext()) {
            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
            ObjectUtil.buildObject(originEntryForTesting, originEntries.next());

            sizeOfOriginEntries++;
            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
        }

        assertEquals(expectedNumOfData, sizeOfOriginEntries);
    }

    public void testDeleteCopiedPendingLedgerEntries() throws Exception {
        String testTarget = "deleteCopiedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.deleteCopiedPendingLedgerEntries();

        int sizeOfPendingEntries = businessObjectService.countMatching(LaborLedgerPendingEntry.class, fieldValues);

        assertEquals(expectedNumOfData, sizeOfPendingEntries);
    }

    public void testDeletePendingLedgerEntries() throws Exception {
        String testTarget = "deletePendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.deleteCopiedPendingLedgerEntries();

        List<PendingLedgerEntryForTesting> expectedDataList = TestDataPreparator.buildExpectedValueList(PendingLedgerEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection<LaborLedgerPendingEntry> pendingEntries = businessObjectService.findMatching(LaborLedgerPendingEntry.class, fieldValues);
        for (LaborLedgerPendingEntry pendingEntry : pendingEntries) {
            PendingLedgerEntryForTesting pendingEntryForTesting = new PendingLedgerEntryForTesting();
            ObjectUtil.buildObject(pendingEntryForTesting, pendingEntry);

            assertTrue("Cannot find the expected entry", expectedDataList.contains(pendingEntryForTesting));
        }

        assertEquals(expectedNumOfData, pendingEntries.size());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        if (nightlyOutputFile != null && nightlyOutputFile.exists() && nightlyOutputFile.isFile()) {
            nightlyOutputFile.delete();
        }

        if (nightlyOutputDoneFile != null && nightlyOutputDoneFile.exists() && nightlyOutputDoneFile.isFile()) {
            nightlyOutputDoneFile.delete();
        }

        batchDirectoryHelper.removeBatchDirectory();

        super.tearDown();
    }
}
