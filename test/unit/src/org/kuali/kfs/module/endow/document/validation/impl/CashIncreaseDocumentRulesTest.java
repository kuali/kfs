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
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.exception.WorkflowException;

@ConfigureContext(session = kfs)
public class CashIncreaseDocumentRulesTest extends KualiTestBase {
    
    private CashIncreaseDocumentRules rule;
    private CashIncreaseDocument document;
    private EndowmentTargetTransactionLine transactionLine;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {                         
        super.setUp();
        rule = new CashIncreaseDocumentRules();
        document = createCashIncreaseDocument();
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
    private CashIncreaseDocument createCashIncreaseDocument() throws WorkflowException {
        document = (CashIncreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_CASH_INCREASE.createEndowmentTransactionDocument(CashIncreaseDocument.class);
        document.getDocumentHeader().setDocumentDescription("Cash Increase Document Test");
        document.setTargetTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(false));        
        transactionLine = (EndowmentTargetTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECDD.createEndowmentTransactionLine(false);
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET);
        document.addTargetTransactionLine(transactionLine);
        return document;
    }
    
    /**
     * validates security code
     * should return false since the security code is invalid
     */
    public void testValidateSecurityCode() {        
        assertFalse(rule.validateSecurityCode(document, false));
    }
    
    /**
     * validates transaction line
     * should return true
     */
    public void testValidateCashTransactionLine() {
        assertTrue(rule.transactionLineSizeGreaterThanZero(document, false));
        assertTrue(rule.validateCashTransactionLine(document, transactionLine, 0));
    }
    
    /**
     * validates transaction type code
     * should return true
     */
    public void testValidateEndowmentTransactionTypeCode() {
        transactionLine.setEtranCodeObj(EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode());
        assertTrue(rule.validateEndowmentTransactionTypeCode(document, transactionLine, ""));        
    }

}
