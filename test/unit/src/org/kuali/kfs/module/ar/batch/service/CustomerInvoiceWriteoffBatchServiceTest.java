/*
 * Copyright 2008 The Kuali Foundation
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
