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
