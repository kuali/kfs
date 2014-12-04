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
package org.kuali.kfs.sys.batch;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.fp.batch.ProcurementCardInputFileType;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransaction;
import org.kuali.kfs.gl.batch.CollectorXmlInputFileType;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.businessobject.HeaderLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;

/**
 * Tests the flatFileParser.
 * 
 * @see org.kuali.kfs.sys.batch.BatchInputFileType
 */
@ConfigureContext
public class FlatFileParserTest extends KualiTestBase {
    private static final String TEST_BATCH_XML_DIRECTORY = "org/kuali/kfs/sys/batch/fixture/";
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType sampleBatchInputFileType;
    private byte[] sampleValidFileContents;
    private byte[] sampleInvalidPropertyFileContents;
    private byte[] sampleNoHeaderFileContents;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        sampleBatchInputFileType = SpringContext.getBean(BatchInputFileType.class,"sampleTestFlatFileInputFileType");
        

        InputStream validFileStream = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "SampleValidFlatFile.txt");
        sampleValidFileContents = IOUtils.toByteArray(validFileStream);
        
        InputStream sampleInvalidPropertyFileStream = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "SampleInValidPropertyFlatFile.txt");
        sampleInvalidPropertyFileContents = IOUtils.toByteArray(sampleInvalidPropertyFileStream);
        
        InputStream sampleNoHeaderFileStream = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "SampleNoHeaderFlatFile.txt");
        sampleNoHeaderFileContents = IOUtils.toByteArray(sampleNoHeaderFileStream);
        
    }
    
    
    /**
     * Verifies the correct object graph is being built from the sample valid file contents 
     * 
     */
    public final void testParseSampleValidContents() throws Exception {
        Object parsedObject = batchInputFileService.parse(sampleBatchInputFileType, sampleValidFileContents);

        assertNotNull("parsed object was null", parsedObject);
        assertEquals("incorrect object type constructured from parse", java.util.ArrayList.class, parsedObject.getClass());

        List parsedList = (ArrayList) parsedObject;
        assertEquals("parsed object size was incorrect", 1, parsedList.size());

        for (int i = 0; i < parsedList.size(); i++) {
            Object parsedElement = parsedList.get(i);
            assertEquals("list object type was incorrect on index " + i, HeaderLine.class, parsedElement.getClass());
        }
    }
    
    /**
     * Verifies exception is thrown on parse method if invalid property found in the file contents .
     */
    public final void testParseInvalidPropertyFileContents() throws Exception {
    
        boolean failedAsExpected = false;
        String errorMessage = "";
        try {
            Object parsedObject =  batchInputFileService.parse(sampleBatchInputFileType, sampleInvalidPropertyFileContents);
        }
        catch (ParseException e) {
            errorMessage = e.getStackTrace().toString();
            failedAsExpected = true;
        }
        assertTrue("exception thrown for invalid property " , failedAsExpected);
       
    }
    
    /**
     * Verifies exception is thrown on parse method if invalid property found in the file contents .
     */
    public final void testNoHeaderFileContents() throws Exception {
    
        boolean failedAsExpected = false;
        try {
            batchInputFileService.parse(sampleBatchInputFileType, sampleNoHeaderFileContents);
        }
        catch (ParseException e) {
            failedAsExpected = true;
        }
        assertTrue("exception thrown for the file with no header " , failedAsExpected);
       
    }
    
    
}
