/*
 * Copyright 2008-2009 The Kuali Foundation
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.exception.XmlErrorHandler;
import org.xml.sax.SAXException;

public class CustomerLoadXMLSchemaTest extends TestCase {

    private static final String XML_SAMPLE_DIRECTORY = "org/kuali/kfs/module/ar/batch/sample/";
    private static final String XML_SAMPLE_FILE = "CustomerLoad-Sample.xml";
    private static final String SCHEMA_DIRECTORY = "work/web-root/static/xsd/ar/";
    private static final String SCHEMA_FILE = "customer.xsd";
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testNothing() {
        
    }

    /**
     * 
     * Tests the AR Customer Load sample XML file against the XSD schema.  
     * 
     * The goal if this is to make sure that the Sample file stays current, even 
     * if someone changes the XSD schema file down the road.  This failing test will 
     * then force the sample file to be updated (hopefully).
     * 
     */
    public void NORUN_testSampleAgainstSchema() throws Exception {
        
        InputStream inputStream;
        byte[] byteArray;
        
        //  XML file stream creation
        inputStream = ClassLoader.getSystemResourceAsStream(XML_SAMPLE_DIRECTORY + XML_SAMPLE_FILE);
        byteArray = IOUtils.toByteArray(inputStream);
        ByteArrayInputStream sampleXmlFile = new ByteArrayInputStream(byteArray);
        
        //  Schema file stream creation
        File schemaFile = new File(SCHEMA_DIRECTORY + SCHEMA_FILE);
        assertTrue("File should exist at the specified path.", schemaFile.exists());
        
        inputStream = new FileInputStream(schemaFile);
        byteArray = IOUtils.toByteArray(inputStream);
        ByteArrayInputStream schemaLocation = new ByteArrayInputStream(byteArray);

        validateContentsAgainstSchema(schemaLocation, sampleXmlFile);
    }
    
    /**
     * Validates the xml contents against the batch input type schema using the java 1.5 validation package.
     * 
     * @param schemaLocation - location of the schema file
     * @param fileContents - xml contents to validate against the schema
     */
    private void validateContentsAgainstSchema(InputStream schemaLocation, InputStream fileContents) 
            throws ParseException, MalformedURLException, IOException, SAXException {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // load a WXS schema, represented by a Schema instance
        Source schemaSource = null;
        schemaSource = new StreamSource(schemaLocation);
        
        Schema schema = null;
        schema = factory.newSchema(schemaSource);

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XmlErrorHandler());

        // validate
        validator.validate(new StreamSource(fileContents));
    }

}
