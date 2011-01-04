/*
 * Copyright 2011 The Kuali Foundation.
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

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.exception.WorkflowException;

@ConfigureContext(session = kfs)
public class CashTransferDocumentRulesTest extends KualiTestBase {
    
    private CashTransferDocumentRules rule;
    private CashTransferDocument document;
    private EndowmentSourceTransactionLine sourceTransactionLine;
    private EndowmentTargetTransactionLine targetTransactionLine;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {                         
        super.setUp();
        rule = new CashTransferDocumentRules();
        document = createCashTransferDocument();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        document = null;
        rule = null;
        super.tearDown();
    }
    
    /**
     * creates an ECI 
     * @return
     * @throws WorkflowException
     */
    private CashTransferDocument createCashTransferDocument() throws WorkflowException {
        document = (CashTransferDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_CASH_TRANSFER.createEndowmentTransactionDocument(CashTransferDocument.class);
        document.getDocumentHeader().setDocumentDescription("Cash Transfer Document Test");

        // for source
        document.setSourceTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(true));
        sourceTransactionLine = (EndowmentSourceTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECDD.createEndowmentTransactionLine(true);
        sourceTransactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        document.addSourceTransactionLine(sourceTransactionLine);
        
        // for target
        document.setTargetTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(false));                
        targetTransactionLine = (EndowmentTargetTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECI.createEndowmentTransactionLine(false);
        targetTransactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET);
        document.addTargetTransactionLine(targetTransactionLine);
        
        return document;
    }

    /**
     * validates source security code
     * should return false since the security code is invalid
     */
    public void testValidateSecurityCode_Source() {        
        assertFalse(rule.validateSecurityCode(document, true));
    }
    
    /**
     * validates target security code
     * should return false since the security code is invalid
     */
    public void testValidateSecurityCode_Target() {        
        assertFalse(rule.validateSecurityCode(document, false));
    }
    
    /**
     * validates source transaction line
     * should return true
     */
    public void testValidateCashTransactionLine_Soruce() {
        assertTrue(rule.transactionLineSizeGreaterThanZero(document, true));
        assertTrue(rule.validateCashTransactionLine(document, sourceTransactionLine, 0));
    }
    
    /**
     * validates target transaction line
     * should return true
     */
    public void testValidateCashTransactionLine_Target() {
        assertTrue(rule.validateCashTransactionLine(document, targetTransactionLine, 0));
        assertTrue(rule.transactionLineSizeGreaterThanZero(document, false));
    }
    
    /**
     * validates source transaction type code
     * should return true
     */
    public void testValidateEndowmentTransactionTypeCode_() {
        sourceTransactionLine.setEtranCodeObj(EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode());
        assertTrue(rule.validateEndowmentTransactionTypeCode(document, sourceTransactionLine, ""));        
    }
    
    /**
     * validates target transaction type code
     * should return true
     */
    public void testValidateEndowmentTransactionTypeCode() {
        targetTransactionLine.setEtranCodeObj(EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode());
        assertTrue(rule.validateEndowmentTransactionTypeCode(document, targetTransactionLine, ""));        
    }

}
