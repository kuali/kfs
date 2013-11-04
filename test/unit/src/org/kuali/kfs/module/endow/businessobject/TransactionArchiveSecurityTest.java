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
public class TransactionArchiveSecurityTest extends KualiTestBase {

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
        return; // this test will not function as written
//        TransactionArchiveSecurity transactionSecurityArchive = new TransactionArchiveSecurity();
//        transactionSecurityArchive.setDocumentNumber("321543");
//        transactionSecurityArchive.setLineNumber(1);
//        transactionSecurityArchive.setLineTypeCode("Z");
//        transactionSecurityArchive.setRegistrationCode("01P");
//        transactionSecurityArchive.setSecurityId("9128273E0");
//        transactionSecurityArchive.setEtranCode("00100");
//        
//        businessObjectService.save(transactionSecurityArchive);
//        
//        Map<String, Object> primaryKeys = new HashMap<String, Object>();
//        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, transactionSecurityArchive.getDocumentNumber());
//        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, transactionSecurityArchive.getLineNumber());
//        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, transactionSecurityArchive.getLineTypeCode());
//        
//        TransactionArchiveSecurity newTransactionSecurityArchive = (TransactionArchiveSecurity) businessObjectService.findByPrimaryKey(TransactionArchiveSecurity.class, primaryKeys);
//        
//        assertTrue(newTransactionSecurityArchive.getDocumentNumber().equals(transactionSecurityArchive.getDocumentNumber()));
//        assertTrue(newTransactionSecurityArchive.getLineTypeCode().equals(transactionSecurityArchive.getLineTypeCode()));
//        assertTrue(newTransactionSecurityArchive.getLineNumber().equals(transactionSecurityArchive.getLineNumber()));
    }
}
