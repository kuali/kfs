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
package org.kuali.kfs.sys.batch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.exception.XmlErrorHandler;
import org.springframework.core.io.UrlResource;
import org.xml.sax.SAXException;



/**
 * Base class for BatchInputFileType implementations that validate using an XSD schema and parse using a digester file
 */
public abstract class XmlBatchInputFileTypeBase extends BatchInputFileTypeBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(XmlBatchInputFileTypeBase.class);

    protected String digestorRulesFileName;
    protected String schemaLocation;

    /**
     * Constructs a BatchInputFileTypeBase.java.
     */
    public XmlBatchInputFileTypeBase() {
        super();
    }

    /**
     * Gets the digestorRulesFileName attribute.
     */
    public String getDigestorRulesFileName() {
        return digestorRulesFileName;
    }

    /**
     * Sets the digestorRulesFileName attribute value.
     */
    public void setDigestorRulesFileName(String digestorRulesFileName) {
        this.digestorRulesFileName = digestorRulesFileName;
    }

    /**
     * Gets the schemaLocation attribute.
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * Sets the schemaLocation attribute value.
     */
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(java.lang.String, java.lang.Object)
     */
    public void process(String fileName, Object parsedFileContents) {
        // default impl does nothing
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#parse(byte[])
     */
    public Object parse(byte[] fileByteContent) throws ParseException {
        if (fileByteContent == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        // handle zero byte contents, xml parsers don't deal with them well
        if (fileByteContent.length == 0) {
            LOG.error("an invalid argument was given, empty input stream");
            throw new IllegalArgumentException("an invalid argument was given, empty input stream");
        }

        // validate contents against schema
        ByteArrayInputStream validateFileContents = new ByteArrayInputStream(fileByteContent);
        validateContentsAgainstSchema(getSchemaLocation(), validateFileContents);

        // setup digester for parsing the xml file
        Digester digester = buildDigester(getSchemaLocation(), getDigestorRulesFileName());

        Object parsedContents = null;
        try {
            ByteArrayInputStream parseFileContents = new ByteArrayInputStream(fileByteContent);
            parsedContents = digester.parse(parseFileContents);
        }
        catch (Exception e) {
            LOG.error("Error parsing xml contents", e);
            throw new ParseException("Error parsing xml contents: " + e.getMessage(), e);
        }

        return parsedContents;    
    }
    
    /**
     * Validates the xml contents against the batch input type schema using the java 1.5 validation package.
     * 
     * @param schemaLocation - location of the schema file
     * @param fileContents - xml contents to validate against the schema
     */
    protected void validateContentsAgainstSchema(String schemaLocation, InputStream fileContents) throws ParseException {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // get schemaFile
        UrlResource schemaResource = null;
        try {
            schemaResource = new UrlResource(schemaLocation);
        }
        catch (MalformedURLException e2) {
            LOG.error("error getting schema url: " + e2.getMessage());
            throw new RuntimeException("error getting schema url:  " + e2.getMessage(), e2);
        }

        // load a WXS schema, represented by a Schema instance
        Source schemaSource = null;
        try {
            schemaSource = new StreamSource(schemaResource.getInputStream());
        }
        catch (IOException e2) {
            LOG.error("error getting schema stream from url: " + e2.getMessage());
            throw new RuntimeException("error getting schema stream from url:   " + e2.getMessage(), e2);
        }
        
        Schema schema = null;
        try {
            schema = factory.newSchema(schemaSource);
        }
        catch (SAXException e) {
            LOG.error("error occured while setting schema file: " + e.getMessage());
            throw new RuntimeException("error occured while setting schema file: " + e.getMessage(), e);
        }    

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XmlErrorHandler());

        // validate
        try {
            validator.validate(new StreamSource(fileContents));
        }
        catch (SAXException e) {
            LOG.error("error encountered while parsing xml " + e.getMessage());
            throw new ParseException("Schema validation error occured while processing file: " + e.getMessage(), e);
        }
        catch (IOException e1) {
            LOG.error("error occured while validating file contents: " + e1.getMessage());
            throw new RuntimeException("error occured while validating file contents: " + e1.getMessage(), e1);
        }
    }
    

    /**
     * @return fully-initialized Digester used to process entry XML files
     */
    protected Digester buildDigester(String schemaLocation, String digestorRulesFileName) {
        Digester digester = new Digester();
        digester.setNamespaceAware(false);
        digester.setValidating(true);
        digester.setErrorHandler(new XmlErrorHandler());
        digester.setSchema(schemaLocation);

        Rules rules = loadRules(digestorRulesFileName);

        digester.setRules(rules);

        return digester;
    }

    /**
     * @return Rules loaded from the appropriate XML file
     */
    protected Rules loadRules(String digestorRulesFileName) {
        // locate Digester rules
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL rulesUrl = classLoader.getResource(digestorRulesFileName);
        if (rulesUrl == null) {
            throw new RuntimeException("unable to locate digester rules file " + digestorRulesFileName);
        }

        // create and init digester
        Digester digester = DigesterLoader.createDigester(rulesUrl);

        return digester.getRules();
    }
}
