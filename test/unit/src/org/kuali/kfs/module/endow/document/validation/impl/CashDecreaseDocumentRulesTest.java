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
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.module.endow.fixture.GLLinkFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.KemidGeneralLedgerAccountFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.api.exception.WorkflowException;

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

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();       
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.ASSET_CLASS_CODE.createClassCodeRecord();        
        Security security = SecurityFixture.ENDOWMENT_ASSET_SECURITY_RECORD.createSecurityRecord();
                
        KEMID kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        GLLink glLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT.createKemidGeneralLedgerAccount();
        kemid.getKemidGeneralLedgerAccounts().add(generalLedgerAccount);
        endowmentTransactionCode.getGlLinks().add(glLink);

        transactionLine = (EndowmentSourceTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECDD.createEndowmentTransactionLine(true);
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        
        document = (CashDecreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_CASH_DECREASE.createEndowmentTransactionDocument(CashDecreaseDocument.class);
        document.getDocumentHeader().setDocumentDescription("Cash Decrease Document Test");
        document.setSourceTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(true));        
        document.addSourceTransactionLine(transactionLine);
        
        return document;
    }
    
    /**
     * validates security code
     * should return true
     */
    public void testValidateSecurityCode() {        
        assertTrue(rule.validateSecurityCode(document, true));
    }
    
    /**
     * validates transaction line
     * should return true
     */
    public void testValidateCashTransactionLine() {
        assertTrue(rule.validateCashTransactionLine(document, transactionLine, 0));
    }
    
    /**
     * validates transaction type code
     * should return true
     */
    public void testValidateEndowmentTransactionTypeCode() {
        assertTrue(rule.validateEndowmentTransactionTypeCode(document, transactionLine, ""));        
    }
}
