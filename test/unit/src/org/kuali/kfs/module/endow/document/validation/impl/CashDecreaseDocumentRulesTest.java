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
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.exception.WorkflowException;

@ConfigureContext(session = kfs)
public class CashDecreaseDocumentRulesTest extends KualiTestBase {

    private CashDecreaseDocumentRules rule;
    private CashDecreaseDocument document;
    private EndowmentSourceTransactionLine transactionLine;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {                         
        super.setUp();
        rule = new CashDecreaseDocumentRules();
        document = createCashDecreaseDocument();
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
     * creates an ECDD 
     * @return
     * @throws WorkflowException
     */
    private CashDecreaseDocument createCashDecreaseDocument() throws WorkflowException {
        document = (CashDecreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_CASH_DECREASE.createEndowmentTransactionDocument(CashDecreaseDocument.class);
        document.getDocumentHeader().setDocumentDescription("Cash Decrease Document Test");
        document.setSourceTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(true));        
        transactionLine = (EndowmentSourceTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECDD.createEndowmentTransactionLine(true);
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        document.addSourceTransactionLine(transactionLine);
        return document;
    }
    
    /**
     * validates security code
     * should return false since the security code is invalid
     */
    public void testValidateSecurityCode() {        
        assertFalse(rule.validateSecurityCode(document, true));
    }
    
    /**
     * validates transaction line
     * should return true
     */
    public void testValidateCashTransactionLine() {
        assertTrue(rule.transactionLineSizeGreaterThanZero(document, true));
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
