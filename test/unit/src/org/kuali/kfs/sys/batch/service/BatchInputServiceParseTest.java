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
package org.kuali.kfs.sys.batch.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.fp.batch.ProcurementCardInputFileType;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransaction;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.CollectorXmlInputFileType;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;

/**
 * Tests for the service parse method.
 */
@ConfigureContext
public class BatchInputServiceParseTest extends KualiTestBase {
    private static final String TEST_BATCH_XML_DIRECTORY = "org/kuali/kfs/sys/batch/fixture/";

    private BatchInputFileService batchInputFileService;

    private byte[] validPCDOFileContents;
    private byte[] validCollectorFileContents;

    private BatchInputFileType pcdoBatchInputFileType;
    private BatchInputFileType collectorBatchInputFileType;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        pcdoBatchInputFileType = SpringContext.getBean(ProcurementCardInputFileType.class);
        collectorBatchInputFileType = SpringContext.getBean(CollectorXmlInputFileType.class);

        InputStream pcdoValidFileStream = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "BatchInputValidPCDO.xml");
        validPCDOFileContents = IOUtils.toByteArray(pcdoValidFileStream);
        InputStream collectorValidFileStream = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "BatchInputValidCollector.xml");
        validCollectorFileContents = IOUtils.toByteArray(collectorValidFileStream);
    }
    
    public final void testDummy() {
        
    }

    /**
     * Verifies the correct object graph is being built from the pcdo file contents in the service parse method. The PCDO
     * unmarshalling rules specify the result should be a ArrayList of ProcurementCardTransaction objects.
     * 
     * @see org.kuali.kfs.fp.businessobject.ProcurementCardTransaction
     */
    public final void DISABLED_testParse_pcdoValidContents() throws Exception {
        Object parsedObject = batchInputFileService.parse(pcdoBatchInputFileType, validPCDOFileContents);

        assertNotNull("parsed object was null", parsedObject);
        assertEquals("incorrect object type constructured from parse", java.util.ArrayList.class, parsedObject.getClass());

        List parsedList = (ArrayList) parsedObject;
        assertEquals("parsed object size was incorrect", 3, parsedList.size());

        for (int i = 0; i < parsedList.size(); i++) {
            Object parsedElement = parsedList.get(i);
            assertEquals("list object type was incorrect on index " + i, ProcurementCardTransaction.class, parsedElement.getClass());
        }
    }

    /**
     * Verifies the correct object graph is being built from the collector file contents in the service parse method. The Collector
     * unmarshalling rules specify the result should be a populated CollectorBatch object.
     * 
     * @see org.kuali.module.gl.collector.xml.CollectorBatch
     */
    public final void DISABLED_testParse_collectorValidContents() throws Exception {
        Object parsedObject = batchInputFileService.parse(collectorBatchInputFileType, validCollectorFileContents);

        assertNotNull("parsed object was null", parsedObject);
        assertEquals("incorrect object type constructured from parse", CollectorBatch.class, parsedObject.getClass());

        CollectorBatch collectorBatch = (CollectorBatch) parsedObject;

        // verify origin entries were populated
        assertEquals("parsed object has incorrect number of origin entries", 2, collectorBatch.getOriginEntries().size());

        for (int i = 0; i < collectorBatch.getOriginEntries().size(); i++) {
            Object originEntry = collectorBatch.getOriginEntries().get(i);

            assertNotNull("orgin entry record is null on index " + i, originEntry);
            assertEquals("object type was incorrect on index " + i, OriginEntryFull.class, originEntry.getClass());
        }

        // verify id billing entries were populated
        assertEquals("parsed object has incorrect number of id billing entries", 2, collectorBatch.getCollectorDetails().size());

        for (int i = 0; i < collectorBatch.getCollectorDetails().size(); i++) {
            Object idBilling = collectorBatch.getCollectorDetails().get(i);

            assertNotNull("ID Billing record is null on index " + i, idBilling);
            assertEquals("object type was incorrect on index " + i, CollectorDetail.class, idBilling.getClass());
        }

        assertEquals("number of batch records does not match header count", collectorBatch.getTotalRecords().intValue(), collectorBatch.getOriginEntries().size() + collectorBatch.getCollectorDetails().size());
    }


    /**
     * Verifies exception is thrown on parse method if file contents are empty.
     */
    public final void DISABLED_testParse_emptyFileContents() throws Exception {
        byte[] emptyContents = null;

        boolean failedAsExpected = false;
        try {
            batchInputFileService.parse(pcdoBatchInputFileType, emptyContents);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        assertTrue("exception not thrown for null empty pcdo file contents", failedAsExpected);

        failedAsExpected = false;
        try {
            batchInputFileService.parse(collectorBatchInputFileType, emptyContents);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        assertTrue("exception not thrown for null empty collector file contents", failedAsExpected);
    }

    /**
     * Verifies error message occurs on parse when an invalid xml format is given.
     */
    public final void DISABLED_testParse_invalidTagOrder() throws Exception {
        InputStream fileContents = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "BatchInputInvalidTagOrderPCDO.xml");
        byte[] invalidTagOrderPCDOFileContents = IOUtils.toByteArray(fileContents);

        boolean failedAsExpected = false;
        try {
            batchInputFileService.parse(pcdoBatchInputFileType, invalidTagOrderPCDOFileContents);
        }
        catch (ParseException e) {
            failedAsExpected = true;
        }

        assertTrue("parse exception not thrown for xml with invalid tag order", failedAsExpected);
    }

    /**
     * Verifies error message occurs on parse when an invalid xml format is given.
     */
    public final void DISABLED_testParse_missingRequiredField() throws Exception {
        InputStream fileContents = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "BatchInputMissingTagPCDO.xml");
        byte[] missingTagPCDOFileContents = IOUtils.toByteArray(fileContents);

        boolean failedAsExpected = false;
        try {
            batchInputFileService.parse(pcdoBatchInputFileType, missingTagPCDOFileContents);
        }
        catch (ParseException e) {
            failedAsExpected = true;
        }

        assertTrue("parse exception not thrown for xml missing a required field", failedAsExpected);
    }

    /**
     * Verifies error occurs when an invalid tag is given.
     */
    public final void DISABLED_testParse_invalidTag() throws Exception {
        InputStream fileContents = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "BatchInputInvalidTagCollector.xml");
        byte[] invalidTagCollectorContents = IOUtils.toByteArray(fileContents);

        boolean failedAsExpected = false;
        try {
            batchInputFileService.parse(collectorBatchInputFileType, invalidTagCollectorContents);
        }
        catch (ParseException e) {
            failedAsExpected = true;
        }

        assertTrue("parse exception not thrown for invalid tag", failedAsExpected);
    }
}
