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
package org.kuali.kfs.module.endow.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferGLTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.kfs.module.endow.fixture.EndowmentRecurringCashTransferFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentRecurringCashTransferGlTargetFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentRecurringCashTransferKemidTargetFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class EndowmentRecurringCashTransferTransactionRulesTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EndowmentRecurringCashTransferTransactionRulesTest.class);
    
    private EndowmentRecurringCashTransferTransactionRule rule;
    private EndowmentRecurringCashTransfer endowmentRecurringCashTransfer;
    private DocumentService documentService;
    private UnitTestSqlDao unitTestSqlDao;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new EndowmentRecurringCashTransferTransactionRule();
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);  
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        endowmentRecurringCashTransfer = null;
        documentService = null;
        
        super.tearDown();
    }

    /**
     */
    public void testValidEntriesForKEMID() {
        LOG.info("testValidEntriesForKEMID() method entered.");
        
        // create an asset decrease document
        endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) EndowmentRecurringCashTransferFixture.VALID_ECT_SOURCE_1.createEndowmentRecurringCashTransfer();
        EndowmentRecurringCashTransferKEMIDTarget kemidTarget = EndowmentRecurringCashTransferKemidTargetFixture.VALID_KEMID_TARGET_1.createEndowmentRecurringCashTransferKEMIDTarget();
        
        endowmentRecurringCashTransfer.getKemidTarget().add(kemidTarget);
        
        // check target existence
        assertTrue(rule.checkTargetExistence(endowmentRecurringCashTransfer));
        
        assertTrue(rule.checkTransactionType(endowmentRecurringCashTransfer));
        
        
        // check eTran code
        String kemid = endowmentRecurringCashTransfer.getSourceKemid();
        String etranCode = endowmentRecurringCashTransfer.getSourceEtranCode();
        String ipIndicator = endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal();
        // refresh Etran obj
        endowmentRecurringCashTransfer.refreshReferenceObject(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_ETRAN_CODE_OBJ);
        
        assertTrue(rule.checkEtranCodeWithChart(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_SOURCE_ETRAN_CODE, kemid, etranCode, ipIndicator));
        
        assertTrue(rule.checkFrequencyCodeReferenceExists(endowmentRecurringCashTransfer));

        // KEMIDTarget rules
        for (EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget : endowmentRecurringCashTransfer.getKemidTarget()) {
            assertTrue(rule.validateKEMIDTarget(endowmentRecurringCashTransferKEMIDTarget));
        }
        
        assertTrue(rule.checkKemidAllPercent(endowmentRecurringCashTransfer.getKemidTarget()));

        assertTrue(rule.checkKemidPercentForSameEtranCode(endowmentRecurringCashTransfer.getKemidTarget()));
        
        LOG.info("testValidEntriesForKEMID() method finished.");  
    }
    
    public void testValidEntriesForGl() {
        LOG.info("testValidEntriesForGl() method entered.");
        
        // create an asset decrease document
        endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) EndowmentRecurringCashTransferFixture.VALID_EGLT_SOURCE_2.createEndowmentRecurringCashTransfer();
        EndowmentRecurringCashTransferGLTarget glTarget3 = EndowmentRecurringCashTransferGlTargetFixture.VALID_GL_TARGET_3.createEndowmentRecurringCashTransferGlTarget();
        
        endowmentRecurringCashTransfer.getGlTarget().add(glTarget3);
        
        // check target existence
        assertTrue(rule.checkTargetExistence(endowmentRecurringCashTransfer));
        
        assertTrue(rule.checkTransactionType(endowmentRecurringCashTransfer));
        
        // check eTran code
        String kemid = endowmentRecurringCashTransfer.getSourceKemid();
        String etranCode = endowmentRecurringCashTransfer.getSourceEtranCode();
        String ipIndicator = endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal();
        // refresh Etran obj
        endowmentRecurringCashTransfer.refreshReferenceObject(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_ETRAN_CODE_OBJ);
        
        assertTrue(rule.checkEtranCodeWithChart(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_SOURCE_ETRAN_CODE, kemid, etranCode, ipIndicator));
        
        assertTrue(rule.checkFrequencyCodeReferenceExists(endowmentRecurringCashTransfer));

        // GLTarget rules
        for (EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget : endowmentRecurringCashTransfer.getGlTarget()) {
            assertTrue(rule.validateGlTarget(endowmentRecurringCashTransferGLTarget));
        }

        assertTrue(rule.checkGlAllPercent(endowmentRecurringCashTransfer.getGlTarget()));

        assertTrue(rule.checkGlPercentForSameEtranCode(endowmentRecurringCashTransfer.getGlTarget()));
        
        LOG.info("testValidEntriesForGl() method finished.");  
    }
}
