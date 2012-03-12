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
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
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

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();       
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.ASSET_CLASS_CODE.createClassCodeRecord();        
        Security security = SecurityFixture.ENDOWMENT_ASSET_SECURITY_RECORD.createSecurityRecord();
        
        KEMID kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        GLLink glLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT.createKemidGeneralLedgerAccount();
        kemid.getKemidGeneralLedgerAccounts().add(generalLedgerAccount);
        endowmentTransactionCode.getGlLinks().add(glLink);

        document = (CashTransferDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_CASH_TRANSFER.createEndowmentTransactionDocument(CashTransferDocument.class);
        document.getDocumentHeader().setDocumentDescription("Cash Transfer Document Test");

        // for source transaction line
        
        sourceTransactionLine = (EndowmentSourceTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECDD.createEndowmentTransactionLine(true);
        sourceTransactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE); 
        
        document.setSourceTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(true));
        document.addSourceTransactionLine(sourceTransactionLine);
       
        // for target transaction line

        targetTransactionLine = (EndowmentTargetTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECI.createEndowmentTransactionLine(false);
        targetTransactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET); 
        
        document.setTargetTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(false));                
        document.addTargetTransactionLine(targetTransactionLine);
        
        return document;
    }

    /**
     * validates source security code
     * should return true
     */
    public void testValidateSecurityCode_Source() {        
        assertTrue(rule.validateSecurityCode(document, true));
    }
    
    /**
     * validates target security code
     * should return true
     */
    public void testValidateSecurityCode_Target() {        
        assertTrue(rule.validateSecurityCode(document, false));
    }
    
    /**
     * validates source transaction line
     * should return true
     */
    public void testValidateCashTransactionLine_Soruce() {
        assertTrue(rule.validateCashTransactionLine(document, sourceTransactionLine, 0));
    }
    
    /**
     * validates target transaction line
     * should return true
     */
    public void testValidateCashTransactionLine_Target() {
        assertTrue(rule.validateCashTransactionLine(document, targetTransactionLine, 0));
    }
    
    /**
     * validates source transaction type code
     * should return true
     */
    public void testValidateEndowmentTransactionTypeCode_Source() {
        assertTrue(rule.validateEndowmentTransactionTypeCode(document, sourceTransactionLine, ""));        
    }
    
    /**
     * validates target transaction type code
     * should return true
     */
    public void testValidateEndowmentTransactionTypeCode_Target() {
        assertTrue(rule.validateEndowmentTransactionTypeCode(document, targetTransactionLine, ""));        
    }

}
