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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = kfs)
public class EndowmenteDocPostingServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EndowmenteDocPostingServiceTest.class);

    private BusinessObjectService businessObjectService;
    private EndowmenteDocPostingServiceImpl endowmenteDocPostingServiceImpl;  
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        endowmenteDocPostingServiceImpl = (EndowmenteDocPostingServiceImpl) TestUtils.getUnproxiedService("mockEndowmenteDocPostingService");
        
    }
    
    public final void testECIDoc() {
        LOG.info("testECIDoc() method entered.");
        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_ECI.createEndowmentTransactionLine(false);
        // check step 3
        // See documentation for Batch Process eDoc Posting, section 11.1.1 
        KemidCurrentCash kemidCurrentCash = (KemidCurrentCash) businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, targetTransactionLine.getKemid());
        KualiDecimal transactionAmount = kemidCurrentCash.getCurrentPrincipalCash().add(targetTransactionLine.getTransactionAmount());
        kemidCurrentCash = endowmenteDocPostingServiceImpl.checkAndCalculateKemidCurrentCash(kemidCurrentCash, targetTransactionLine, "ECI", "P");
        assertTrue("total transaction amount is " + transactionAmount.toString(), kemidCurrentCash.getCurrentPrincipalCash().equals(transactionAmount));

        LOG.info("testECIDoc() method finished.");        
    }
    
    public final void testECDDDoc() {
        LOG.info("testECDDDoc() method entered.");

        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_ECDD.createEndowmentTransactionLine(false);
        // check step 3
        // See documentation for Batch Process eDoc Posting, section 11.1.1 
        KemidCurrentCash kemidCurrentCash = (KemidCurrentCash) businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, targetTransactionLine.getKemid());
        KualiDecimal transactionAmount = kemidCurrentCash.getCurrentIncomeCash().add(targetTransactionLine.getTransactionAmount().negated());
        kemidCurrentCash = endowmenteDocPostingServiceImpl.checkAndCalculateKemidCurrentCash(kemidCurrentCash, targetTransactionLine, "ECDD", "I");
        assertTrue("total transaction amount is " + transactionAmount.toString(), kemidCurrentCash.getCurrentIncomeCash().equals(transactionAmount));

        LOG.info("testECDDDoc() method finished.");        
    }
    
    public final void testEAICashDoc() {
        LOG.info("testEAICashDoc() method entered.");

        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_EAI_CASH.createEndowmentTransactionLine(false);
        // check step 3
        // See documentation for Batch Process eDoc Posting, section 11.1.1 
        KemidCurrentCash kemidCurrentCash = (KemidCurrentCash) businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, targetTransactionLine.getKemid());
        KualiDecimal transactionAmount = kemidCurrentCash.getCurrentPrincipalCash().add(targetTransactionLine.getTransactionAmount().negated());
        kemidCurrentCash = endowmenteDocPostingServiceImpl.checkAndCalculateKemidCurrentCash(kemidCurrentCash, targetTransactionLine, "EAI", "P");
        assertTrue("total transaction amount is " + transactionAmount.toString(), kemidCurrentCash.getCurrentPrincipalCash().equals(transactionAmount));

        LOG.info("testEAICashDoc() method finished.");        
    }
    
    public final void testEAINonCashDoc() {
        LOG.info("testEAINonCashDoc() method entered.");

        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_EAI_NON_CASH.createEndowmentTransactionLine(false);
        EndowmentTransactionSecurity transactionSecurity = (EndowmentTransactionSecurity) EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_TARGET_FOR_POSTING_EDOC_EAI_NON_CASH.createEndowmentTransactionSecurity(false);
        // check step 2
        // See documentation for Batch Process eDoc Posting, section 11.1.1
        // Otherwise, enter zero
        TransactionArchive transactionArchive = endowmenteDocPostingServiceImpl.createTranArchive(targetTransactionLine, "EAI", EndowConstants.TransactionSubTypeCode.NON_CASH);
        TransactionArchiveSecurity tranSecurity = endowmenteDocPostingServiceImpl.createTranArchiveSecurity(transactionSecurity, targetTransactionLine, "EAI");    
        KualiDecimal transactionAmount = targetTransactionLine.getTransactionAmount(); 
        assertTrue("transaction amount is " + transactionAmount.toString(), (KualiDecimal.ZERO).equals(new KualiDecimal(transactionArchive.getPrincipalCashAmount())));
        // Do NOT need to check step 3, because it is non cash
        LOG.info("testEAINonCashDoc() method finished.");        
    }

    public final void testEADCashDoc() {
        LOG.info("testEADCashDoc() method entered.");
        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_EAD_CASH.createEndowmentTransactionLine(false);
        
        // check step 3
        // See documentation for Batch Process eDoc Posting, section 11.1.1 
        // IF the Document Type is EAI, ELD, or ECDD, then the value of TRAN_AMT is multiplied by negative 1.
        KemidCurrentCash kemidCurrentCash = (KemidCurrentCash) businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, targetTransactionLine.getKemid());
        KualiDecimal transactionAmount = kemidCurrentCash.getCurrentPrincipalCash().add(targetTransactionLine.getTransactionAmount());
        kemidCurrentCash = endowmenteDocPostingServiceImpl.checkAndCalculateKemidCurrentCash(kemidCurrentCash, targetTransactionLine, "EAD", "P");
        assertTrue("total transaction amount is " + transactionAmount.toString(), kemidCurrentCash.getCurrentPrincipalCash().equals(transactionAmount));
        
        LOG.info("testEADCashDoc() method finished.");
    }
    
    public final void testEADNonCashDoc() {
        LOG.info("testEADNonCashDoc() method entered.");

        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_EAD_NON_CASH.createEndowmentTransactionLine(false);
        EndowmentTransactionSecurity transactionSecurity = (EndowmentTransactionSecurity) EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_TARGET_FOR_POSTING_EDOC_EAD_NON_CASH.createEndowmentTransactionSecurity(false);
        // check step 2
        // See documentation for Batch Process eDoc Posting, section 11.1.1
        // Otherwise, enter zero
        TransactionArchive transactionArchive = endowmenteDocPostingServiceImpl.createTranArchive(targetTransactionLine, "EAD", EndowConstants.TransactionSubTypeCode.NON_CASH);
        TransactionArchiveSecurity tranSecurity = endowmenteDocPostingServiceImpl.createTranArchiveSecurity(transactionSecurity, targetTransactionLine, "EAD");    
        KualiDecimal transactionAmount = targetTransactionLine.getTransactionAmount(); 
        assertTrue("transaction amount is " + transactionAmount.toString(), (KualiDecimal.ZERO).equals(new KualiDecimal(transactionArchive.getPrincipalCashAmount())));
     // Do NOT need to check step 3, because it is non cash
        LOG.info("testEADNonCashDoc() method finished.");        
    }
}
