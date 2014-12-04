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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.io.IOUtils;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.exception.XmlErrorHandler;

public class CustomerLoadDigesterTest extends TestCase {

    private static final String XML_SAMPLE_DIRECTORY = "org/kuali/kfs/module/ar/batch/sample/";
    private static final String XML_SAMPLE_FILE = "CustomerLoad-Sample.xml";
    private static final String SCHEMA_DIRECTORY = "work/web-root/static/xsd/ar/";
    private static final String SCHEMA_FILE = "customer.xsd";
    private static final String DIGESTER_RULE_DIRECTORY = "org/kuali/kfs/module/ar/batch/digester/";
    private static final String DIGESTER_RULE_FILE = "customerLoadDigesterRules.xml";
    
    private List<CustomerDigesterVO> parsedCustomerList;
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testNothing() {
        
    }
    
    public void NORUN_testCustomerLoadDigesterRules() throws Exception {
        
        Digester digester = buildDigester(SCHEMA_DIRECTORY + SCHEMA_FILE, DIGESTER_RULE_DIRECTORY + DIGESTER_RULE_FILE);
        
        //  get the right kind of input stream expected for the sample batch file
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_SAMPLE_DIRECTORY + XML_SAMPLE_FILE);
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        ByteArrayInputStream sampleCustomerBatchFile = new ByteArrayInputStream(byteArray);

        Object parsedCustomers = null;
        try {
            parsedCustomers = digester.parse(sampleCustomerBatchFile);
        }
        catch (Exception e) {
            throw new ParseException("Error parsing xml contents: " + e.getMessage(), e);
        }
        
        assertNotNull("Parsed object should not be null.", parsedCustomers);
        assertTrue("Parsed object class [" + parsedCustomers.getClass().toString() + "] should be assignable to a List.", 
                parsedCustomers instanceof List);
        
        List parsedObjects = (List) parsedCustomers;
        
        assertTrue("Parsed object List should contain at least one item.", (parsedObjects.size() >= 1));
        
        parsedCustomerList = new ArrayList<CustomerDigesterVO>();
        
        //  look at every item, make sure its of type Customer 
        Integer i = 0;
        for (Iterator iterator = parsedObjects.iterator(); iterator.hasNext();) {
            Object item = (Object) iterator.next();
            
            assertNotNull("List item [" + i.toString() + "] should not be null.", item);
            assertTrue("List item [" + i.toString() + "] class [" + item.getClass().toString() + "] should be a Customer object.", 
                    item.getClass().equals(CustomerDigesterVO.class));
            
            parsedCustomerList.add((CustomerDigesterVO) item);
            
            i++;
        }
        
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
