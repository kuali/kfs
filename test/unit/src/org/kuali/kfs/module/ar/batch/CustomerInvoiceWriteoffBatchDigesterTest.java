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
package org.kuali.kfs.module.ar.batch;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO;
import org.kuali.kfs.sys.exception.XmlErrorHandler;

public class CustomerInvoiceWriteoffBatchDigesterTest extends TestCase {

    private static final String XML_SAMPLE_DIRECTORY = "org/kuali/kfs/module/ar/batch/sample/";
    private static final String XML_SAMPLE_FILE = "CustomerInvoiceWriteoffBatch-Sample-Good.xml";
    private static final String SCHEMA_DIRECTORY = "work/web-root/static/xsd/ar/";
    private static final String SCHEMA_FILE = "customerInvoiceWriteoffBatch.xsd";
    private static final String DIGESTER_RULE_DIRECTORY = "org/kuali/kfs/module/ar/batch/digester/";
    private static final String DIGESTER_RULE_FILE = "customerInvoiceWriteoffBatchDigesterRules.xml";
    
    private CustomerInvoiceWriteoffBatchVO parsedBatchVO;
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testNothing() {
        
    }
    
    public void NORUN_testCustomerInvoiceWriteoffBatchDigesterRules() throws Exception {
        
        Digester digester = buildDigester(SCHEMA_DIRECTORY + SCHEMA_FILE, DIGESTER_RULE_DIRECTORY + DIGESTER_RULE_FILE);
        
        //  get the right kind of input stream expected for the sample batch file
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_SAMPLE_DIRECTORY + XML_SAMPLE_FILE);
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        ByteArrayInputStream sampleCustomerBatchFile = new ByteArrayInputStream(byteArray);

        Object parsedObject = null;
        try {
            parsedObject = digester.parse(sampleCustomerBatchFile);
        }
        catch (Exception e) {
            throw new RuntimeException("Error parsing xml contents: " + e.getMessage(), e);
        }
        
        assertNotNull("Parsed object should not be null.", parsedObject);
        assertTrue("Parsed object class [" + parsedObject.getClass().toString() + "] should be assignable to a List.", 
                parsedObject instanceof CustomerInvoiceWriteoffBatchVO);
        
        parsedBatchVO = (CustomerInvoiceWriteoffBatchVO) parsedObject;
        
        assertTrue("SubmittedBy PersonUserID should not be blank.", StringUtils.isNotBlank(parsedBatchVO.getSubmittedByPrincipalName()));
        assertTrue("SubmittedOn should not be blank.", StringUtils.isNotBlank(parsedBatchVO.getSubmittedOn()));
        assertNotNull("InvoiceNumbers should not be null.", parsedBatchVO.getInvoiceNumbers());
        assertFalse("InvoiceNumbers should not be empty.", parsedBatchVO.getInvoiceNumbers().isEmpty());
        assertTrue("InvoiceNumbers should have 3 elements in the set.", (parsedBatchVO.getInvoiceNumbers().size() == 3));
        
        return;
    }
    
    /**
     * @return fully-initialized Digester used to process entry XML files
     */
    private Digester buildDigester(String schemaLocation, String digestorRulesFileName) {
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
    private Rules loadRules(String digestorRulesFileName) {
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
