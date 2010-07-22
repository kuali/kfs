/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class TransactionArchiveTest extends KualiTestBase {

    private BusinessObjectService businessObjectService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }
    
    @ConfigureContext(shouldCommitTransactions = false)
    public void testOJBConfiguration() throws Exception {

        TransactionArchiveSecurity transactionArchiveSecurity = new TransactionArchiveSecurity();
        transactionArchiveSecurity.setDocumentNumber("321543");
        transactionArchiveSecurity.setLineNumber(Integer.valueOf("1"));
        transactionArchiveSecurity.setLineTypeCode("Z");
        transactionArchiveSecurity.setSecurityId("004764106");
        transactionArchiveSecurity.setRegistrationCode("01P");
        transactionArchiveSecurity.setEtranCode("00100");
        
        businessObjectService.save(transactionArchiveSecurity);
        
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(TransactionArchiveSecurity.DOCUMENT_NUMBER, transactionArchiveSecurity.getDocumentNumber());
        primaryKeys.put(TransactionArchiveSecurity.LINE_NUMBER, transactionArchiveSecurity.getLineNumber());
        primaryKeys.put(TransactionArchiveSecurity.LINE_TYPE_CODE, transactionArchiveSecurity.getLineTypeCode());
        
        TransactionArchiveSecurity newTransactionArchiveSecurity = 
            (TransactionArchiveSecurity) businessObjectService.findByPrimaryKey(TransactionArchiveSecurity.class, primaryKeys);
        
        assertTrue(newTransactionArchiveSecurity.getDocumentNumber().equals(transactionArchiveSecurity.getDocumentNumber()));
        assertTrue(newTransactionArchiveSecurity.getLineTypeCode().equals(transactionArchiveSecurity.getLineTypeCode()));
        assertTrue(newTransactionArchiveSecurity.getLineNumber().equals(transactionArchiveSecurity.getLineNumber()));
    }
}
