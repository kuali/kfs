/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.module.ar.batch;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;

@ConfigureContext(session = khuntley)
public class CustomerLoadCSVInputFileTypeTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadCSVInputFileTypeTest.class);

    private static final String CSV_SAMPLE_DIRECTORY = "org/kuali/kfs/module/ar/batch/sample/";
    private static final String CSV_TEST_FILE = "CustomerLoad_Test.csv";
    
    CustomerLoadCSVInputFileType batchInput;
    byte[] byteArraybyteArray = null;
    
    @Override
    public void setUp() {
        batchInput = SpringContext.getBean(CustomerLoadCSVInputFileType.class);
        
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(CSV_SAMPLE_DIRECTORY + CSV_TEST_FILE);
        try {
            byteArraybyteArray = IOUtils.toByteArray(inputStream);
        }
        catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Test content parsing
     */
    public void testContentParsing() {
        Object parsedContent = null;
        try {
            parsedContent =batchInput.parse(byteArraybyteArray); 
        }
        catch (ParseException pe){
            LOG.error(pe.getMessage(), pe);
        }
        assertNotNull(parsedContent);
    }
    
    /**
     * Test object to VO conversion
     * 
     */
    public void testObjectVOConversion() {
        List<CustomerDigesterVO> customerVOs = new ArrayList<CustomerDigesterVO>();
        try{
            customerVOs = (List<CustomerDigesterVO>)batchInput.parse(byteArraybyteArray);
        }
        catch(Exception e){
            LOG.error(e.getMessage(), e);
        }
        final int customerSize = 7;
        assertFalse("Parse customer should  not be empty.", customerVOs.isEmpty());
        assertEquals("The parsed VO size does not match expected", customerVOs.size(), customerSize);
    }
    
    /**
     * Test validation
     */
    public void testValidation() {
        List<CustomerDigesterVO> customerVOs = new ArrayList<CustomerDigesterVO>();
        try{
            customerVOs = (List<CustomerDigesterVO>)batchInput.parse(byteArraybyteArray);
        }
        catch(Exception e){
            LOG.error(e.getMessage(), e);
        }
        
        boolean result = batchInput.validate(customerVOs);
        assertTrue("The Validation should have produced no error messages.", result);
    }
    
}

