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
package org.kuali.kfs.sys.context;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.InquiryDefinition;
import org.kuali.rice.kns.datadictionary.InquirySectionDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.DataDictionary;

@ConfigureContext
public class GenerateDataDictionaryInquirySectionFile extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(GenerateDataDictionaryInquirySectionFile.class);
    private DataDictionary dataDictionary;

    protected void setUp() throws Exception {
        super.setUp();
        dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
    } 

    public void testGenerateInquirySections() throws Exception {
        TreeMap<String,List<InquirySectionDefinition>> boInquirySections = new TreeMap<String, List<InquirySectionDefinition>>();
        for(org.kuali.rice.krad.datadictionary.BusinessObjectEntry kradBusinessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry) kradBusinessObjectEntry;
            if ( businessObjectEntry.getInquiryDefinition() != null ) {
                //LOG.info("Processing inquiry section for " + businessObjectEntry.getBusinessObjectClass().getName());
                InquiryDefinition inqDef = businessObjectEntry.getInquiryDefinition();
                boInquirySections.put(businessObjectEntry.getBusinessObjectClass().getName(), inqDef.getInquirySections() );
            }
        }
        LOG.info( "Class URI: " + getClass().getProtectionDomain().getCodeSource().getLocation().toURI() );
        File f = new File( new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI() ), getClass().getPackage().getName().replace(".", File.separator) + File.separator + "boInquirySections.properties" );
        f = new File( f.getAbsolutePath().replace( "/classes/", "/unit/src/" ) );
        LOG.info( "File path:" + f.getAbsolutePath() );
        FileWriter fw = new FileWriter( f );
        for ( String className : boInquirySections.keySet() ) {
            fw.write(className);
            fw.write('=');
            Iterator<InquirySectionDefinition> i = boInquirySections.get(className).iterator();
            while ( i.hasNext() ) {
                String title = i.next().getTitle();
                if ( title == null ) {
                    title = "(null)";
                } else if ( StringUtils.isBlank(title) ) {
                    title = "(blank)";
                }
                fw.write( title );
                if ( i.hasNext() ) {
                    fw.write( ',' );
                }
            }
            fw.write( '\n' );
        }
        fw.flush();
        fw.close();
    }
}
