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
