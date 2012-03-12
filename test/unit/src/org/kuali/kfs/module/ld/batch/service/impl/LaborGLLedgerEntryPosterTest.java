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
package org.kuali.kfs.module.ld.batch.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.service.LaborGeneralLedgerEntryService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext
public class LaborGLLedgerEntryPosterTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;
    private Map fieldValues;
    private OriginEntryGroup group1;
    private Date today;

    private BusinessObjectService businessObjectService;
    private PostTransaction laborGLLedgerEntryPoster;
    private LaborOriginEntryGroupService originEntryGroupService;
    private LaborGeneralLedgerEntryService laborGeneralLedgerEntryService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborGLLedgerEntryPoster.properties";
        
        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));

        laborGLLedgerEntryPoster = SpringContext.getBean(PostTransaction.class,"laborGLLedgerEntryPoster");
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        originEntryGroupService = SpringContext.getBean(LaborOriginEntryGroupService.class);
        laborGeneralLedgerEntryService = SpringContext.getBean(LaborGeneralLedgerEntryService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        // TODO:- commented out
        //group1 = originEntryGroupService.createGroup(dateTimeService.getCurrentSqlDate(), LABOR_MAIN_POSTER_VALID, false, false, false);
        today = dateTimeService.getCurrentDate();

        LaborGeneralLedgerEntry cleanup = new LaborGeneralLedgerEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LaborGeneralLedgerEntry.class, fieldValues);
    }

    public void testPost() throws Exception {
        // TODO:- need to change using file
//        int numberOfTestData = Integer.valueOf(properties.getProperty("post.numOfData"));
//        int expectedMaxSequenceNumber = Integer.valueOf(properties.getProperty("post.expectedMaxSequenceNumber"));
//        int expectedInsertion = Integer.valueOf(properties.getProperty("post.expectedInsertion"));
//
//        List<LaborOriginEntry> transactionList = LaborTestDataPreparator.getLaborOriginEntryList(properties, "post.testData", numberOfTestData, group1);
//        Map<String, Integer> operationType = new HashMap<String, Integer>();
//
//        for (LaborOriginEntry transaction : transactionList) {
//            String operation = laborGLLedgerEntryPoster.post(transaction, 0, today);
//            Integer currentNumber = operationType.get(operation);
//            Integer numberOfOperation = currentNumber != null ? currentNumber + 1 : 1;
//            operationType.put(operation, numberOfOperation);
//        }
//
//        Collection returnValues = businessObjectService.findMatching(LaborGeneralLedgerEntry.class, fieldValues);
//        assertEquals(numberOfTestData, returnValues.size());
//
//        assertEquals(1, operationType.size());
//        assertEquals(expectedInsertion, operationType.get(KFSConstants.OperationType.INSERT).intValue());
//
//        LaborGeneralLedgerEntry expected1 = new LaborGeneralLedgerEntry();
//        ObjectUtil.populateBusinessObject(expected1, properties, "post.expected1", fieldNames, deliminator);
//        assertEquals(expectedMaxSequenceNumber, laborGeneralLedgerEntryService.getMaxSequenceNumber(expected1).intValue());
//
//        LaborGeneralLedgerEntry expected2 = new LaborGeneralLedgerEntry();
//        ObjectUtil.populateBusinessObject(expected2, properties, "post.expected2", fieldNames, deliminator);
//        assertEquals(expectedMaxSequenceNumber, laborGeneralLedgerEntryService.getMaxSequenceNumber(expected2).intValue());
//
//        LaborGeneralLedgerEntry expected3 = new LaborGeneralLedgerEntry();
//        ObjectUtil.populateBusinessObject(expected3, properties, "post.expected3", fieldNames, deliminator);
//        assertEquals(expectedMaxSequenceNumber, laborGeneralLedgerEntryService.getMaxSequenceNumber(expected3).intValue());
    }
}
