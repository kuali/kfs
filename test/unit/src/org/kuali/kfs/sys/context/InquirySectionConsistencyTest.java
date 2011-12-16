/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.context;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.InquiryDefinition;
import org.kuali.rice.kns.datadictionary.InquirySectionDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.DataDictionary;

@ConfigureContext
public class InquirySectionConsistencyTest extends KualiTestBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquirySectionConsistencyTest.class);
    
    private DataDictionary dataDictionary;
    
    protected void setUp() throws Exception {
        super.setUp();
        dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
    }

    public void testInquirySectionsMatchDataFile() throws Exception {
        Properties expectedSections = new Properties();
        File f = new File( new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI() ), getClass().getPackage().getName().replace(".", File.separator) + File.separator + "boInquirySections.properties" );
        expectedSections.load( new FileReader( f ) );

        StringBuilder testFailures = new StringBuilder();
        
        for(org.kuali.rice.krad.datadictionary.BusinessObjectEntry kradBusinessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry) kradBusinessObjectEntry;
            if ( expectedSections.containsKey(businessObjectEntry.getBusinessObjectClass().getName()) ) {
                if ( businessObjectEntry.getInquiryDefinition() != null ) {
                    //LOG.info("Processing inquiry section for " + businessObjectEntry.getBusinessObjectClass().getName());
                    InquiryDefinition inqDef = businessObjectEntry.getInquiryDefinition();
                    Iterator<InquirySectionDefinition> i = inqDef.getInquirySections().iterator();
                    StringBuilder sb = new StringBuilder();
                    while ( i.hasNext() ) {
                        String title = i.next().getTitle();
                        if ( title == null ) {
                            title = "(null)";
                        } else if ( StringUtils.isBlank(title) ) {
                            title = "(blank)";
                        }
                        sb.append( title );
                        if ( i.hasNext() ) {
                            sb.append( ',' );
                        }
                    }
                    if ( !sb.toString().equals(expectedSections.get(businessObjectEntry.getBusinessObjectClass().getName()) ) ) {
                        testFailures.append( "MISMATCH: " + businessObjectEntry.getBusinessObjectClass().getName() + " -- expected " + expectedSections.get(businessObjectEntry.getBusinessObjectClass().getName()) + " -- but was -- " + sb.toString() + "\n" );
                    }
                }
            }
        }
        assertTrue( "TEST FAILURES:\n" + testFailures.toString(), testFailures.length() == 0);
    }
}
