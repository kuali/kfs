/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.fixture.EndowmentRecurringCashTransferFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentRecurringCashTransferKemidTargetFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = kfs)
public class RecurringCashTransferTransactionsBatchTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RecurringCashTransferTransactionsBatchTest.class);

    private BusinessObjectService businessObjectService;
    private CreateRecurringCashTransferTransactionsServiceImpl createRecurringCashTransferTransactionsService;  
    private KEMService kemService;
    private KEMID kemid;
    private UnitTestSqlDao unitTestSqlDao;
    private Collection<EndowmentRecurringCashTransfer> recurringCashTransfers;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        createRecurringCashTransferTransactionsService = (CreateRecurringCashTransferTransactionsServiceImpl) TestUtils.getUnproxiedService("mockCreateRecurringCashTransferTransactionsService");
        kemService = SpringContext.getBean(KEMService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class); 
        recurringCashTransfers = new ArrayList();
        
    }
    
    public final void testTransactionAmount() {
        LOG.info("testTransactionAmount() method entered.");

        EndowmentRecurringCashTransfer endowmentRecurringCashTransfer;
        KualiDecimal cashEquivalents;
        
        // test case 1
        endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) EndowmentRecurringCashTransferFixture.VALID_ECT_SOURCE_1.createEndowmentRecurringCashTransfer();
        EndowmentRecurringCashTransferKEMIDTarget kemidTarget = EndowmentRecurringCashTransferKemidTargetFixture.VALID_KEMID_TARGET_1.createEndowmentRecurringCashTransferKEMIDTarget();
        endowmentRecurringCashTransfer.getKemidTarget().add(kemidTarget);
       
        cashEquivalents = createRecurringCashTransferTransactionsService.calculateTotalCashEquivalents(endowmentRecurringCashTransfer);
        
        KualiDecimal transactionAmount = createRecurringCashTransferTransactionsService.calculateCashTransferTransactionAmount(kemidTarget, cashEquivalents, endowmentRecurringCashTransfer.getSourceKemid(), endowmentRecurringCashTransfer.getLastProcessDate());
        
        assertTrue("total transaction amount is "+transactionAmount.toString(), transactionAmount.equals(new KualiDecimal(10.00)));
        

        // test case 2
        endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) EndowmentRecurringCashTransferFixture.VALID_ECT_SOURCE_2.createEndowmentRecurringCashTransfer();
        EndowmentRecurringCashTransferKEMIDTarget kemidTarget1 = EndowmentRecurringCashTransferKemidTargetFixture.VALID_KEMID_TARGET_2.createEndowmentRecurringCashTransferKEMIDTarget();
        EndowmentRecurringCashTransferKEMIDTarget kemidTarget2 = EndowmentRecurringCashTransferKemidTargetFixture.VALID_KEMID_TARGET_3.createEndowmentRecurringCashTransferKEMIDTarget();
        endowmentRecurringCashTransfer.getKemidTarget().add(kemidTarget1);
        endowmentRecurringCashTransfer.getKemidTarget().add(kemidTarget2);
        
        cashEquivalents = createRecurringCashTransferTransactionsService.calculateTotalCashEquivalents(endowmentRecurringCashTransfer);
       
        // total transaction amount should be 146.42  
        KualiDecimal transactionAmount1 = createRecurringCashTransferTransactionsService.calculateCashTransferTransactionAmount(kemidTarget1, cashEquivalents, endowmentRecurringCashTransfer.getSourceKemid(), endowmentRecurringCashTransfer.getLastProcessDate());
        KualiDecimal transactionAmount2 = createRecurringCashTransferTransactionsService.calculateCashTransferTransactionAmount(kemidTarget2, cashEquivalents, endowmentRecurringCashTransfer.getSourceKemid(), endowmentRecurringCashTransfer.getLastProcessDate());
        
        
        //boolean success  = createRecurringCashTransferTransactionsService.createRecurringCashTransferTransactions(recurringCashTransfers);
        
        assertTrue("total transaction amount is "+transactionAmount1.add(transactionAmount2).toString(), transactionAmount1.add(transactionAmount2).equals(new KualiDecimal(146.42)));

        LOG.info("testTransactionAmount() method finished.");        
    }
    
}
