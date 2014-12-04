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
package org.kuali.kfs.module.ar.batch.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

@ConfigureContext
public class CustomerInvoiceWriteoffBatchServiceTest extends KualiTestBase {

    private DateTimeService dateTimeService;
    private PersonService personService;
    private CustomerInvoiceWriteoffBatchService batchService;
    
    public void setUp() throws Exception {
        super.setUp();
        personService = SpringContext.getBean(PersonService.class);
        batchService = SpringContext.getBean(CustomerInvoiceWriteoffBatchService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }
    
    public void testFilesDropCorrectly() {
        Person person = personService.getPersonByPrincipalName(KFSConstants.SYSTEM_USER); 
        CustomerInvoiceWriteoffBatchVO batchVO = createBatchVO(person);
        
        batchVO.setSubmittedOn(dateTimeService.getCurrentTimestamp().toString());
        
        batchVO.setNote("This is the user note added with the batch documents.");
        
        batchVO.addInvoiceNumber("1111111");
        batchVO.addInvoiceNumber("2222222");
        batchVO.addInvoiceNumber("3333333");
        
        String batchXmlFileName = batchService.createBatchDrop(person, batchVO);
        
        assertTrue("Returned xml file name should not be blank.", StringUtils.isNotBlank(batchXmlFileName));
        
        File batchXML = new File(batchXmlFileName);
        
        assertTrue("Batch XML file should be present in the file system.", batchXML.exists());
        assertTrue("Batch XML file should not be zero bytes.", (batchXML.length() > 0));
        
    }
    
    private CustomerInvoiceWriteoffBatchVO createBatchVO(Person person) {
        CustomerInvoiceWriteoffBatchVO batchVO = new CustomerInvoiceWriteoffBatchVO(person.getPrincipalName());
        return batchVO;
    }
}
