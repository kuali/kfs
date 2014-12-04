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
