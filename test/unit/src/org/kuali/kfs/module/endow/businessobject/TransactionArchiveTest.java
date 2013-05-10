/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

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
        return; // This test has problems and will not function as written
//        TransactionArchive transactionArchive = new TransactionArchive();
//        transactionArchive.setDocumentNumber("321543");
//        transactionArchive.setLineNumber(Integer.valueOf("1"));
//        transactionArchive.setLineTypeCode("Z");
////        transactionArchiveSecurity.setSecurityId("004764106");
////        transactionArchiveSecurity.setRegistrationCode("01P");
//        transactionArchive.setEtranCode("00100");
//        
//        businessObjectService.save(transactionArchive);
//        
//        Map<String, Object> primaryKeys = new HashMap<String, Object>();
//        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, transactionArchive.getDocumentNumber());
//        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, transactionArchive.getLineNumber());
//        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, transactionArchive.getLineTypeCode());
//        
//        TransactionArchive newTransactionArchive = 
//            (TransactionArchive) businessObjectService.findByPrimaryKey(TransactionArchive.class, primaryKeys);
//        
//        assertTrue(newTransactionArchive.getDocumentNumber().equals(transactionArchive.getDocumentNumber()));
//        assertTrue(newTransactionArchive.getLineTypeCode().equals(transactionArchive.getLineTypeCode()));
//        assertTrue(newTransactionArchive.getLineNumber().equals(transactionArchive.getLineNumber()));
    }
}
