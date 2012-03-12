/*
 * Copyright 2008 The Kuali Foundation
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

import java.math.BigDecimal;
import java.sql.Date;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurityBase;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.LiabilityDecreaseDocument;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the rules in LiabilityDecreaseDocumentRules class
 */
@ConfigureContext(session = khuntley)
public class LiabilityDecreaseDocumentRulesTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(LiabilityDecreaseDocumentRulesTest.class);

    private LiabilityDecreaseDocumentRules rule;
    private LiabilityDecreaseDocument document;
    private DocumentService documentService;
    private Security security;
    private KEMID kemid;
    
    private static final KualiDecimal ZERO_AMOUNT = KualiDecimal.ZERO;
    private static final KualiDecimal NEGATIVE_AMOUNT = new KualiDecimal("-1.00");
    private static final KualiDecimal POSITIVE_AMOUNT = new KualiDecimal("2.00");
    private static final KualiDecimal NEGATIVE_UNITS = new KualiDecimal("-1.00");
    private static final KualiDecimal POSITIVE_UNITS = new KualiDecimal("2.00");

    private static final String REFERENCE_DOCUMENT_NUMBER = "123456";
    private static final String REFERENCE_DOCUMENT_DESCRIPTION = "Document Description - Unit Test";
    private static final String INVALID_REGISTRATION_CODE = "...";
    private static final String INVALID_SECURITY_ID = "WRONG_ID";
    private static final String ETRAN_CODE = "42020";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new LiabilityDecreaseDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        
        security = SecurityFixture.ENDOWMENT_SECURITY_RECORD.createSecurityRecord("TESTSECID", "910", BigDecimal.ONE, "M01", Date.valueOf("2010-01-01"), BigDecimal.valueOf(20L), true, BigDecimal.valueOf(100.20));
        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();

        //create the Liability Decrease document
        document = createLiabilityDecreaseDocument();
        
        //add a corresponding tax lot record so it can be updated by the rules class..
        HoldingTaxLotRebalance holdingTaxLotRebalance = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_LIABILITY.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLot holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_LIABILITY.createHoldingTaxLotRecord();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        documentService = null;
        
        super.tearDown();
    }

    /**
     * create a LiabilityDecreaseDocument
     * @return doc
     */
    protected LiabilityDecreaseDocument createLiabilityDecreaseDocument() throws WorkflowException {
        LOG.info("createLiabilityDecreaseDocument() entered.");
        
        LiabilityDecreaseDocument doc = (LiabilityDecreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentTransactionDocument(LiabilityDecreaseDocument.class);
        doc.getDocumentHeader().setDocumentDescription("Testing Liability Decrease document.");

        EndowmentTransactionSecurityBase etsb = (EndowmentSourceTransactionSecurity) EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(true);
        etsb.setDocumentNumber(doc.getDocumentNumber());
        
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD_FOR_LIABILITY.createRegistrationCode();
        etsb.setRegistrationCode(registrationCode.getCode());
        etsb.refreshNonUpdateableReferences();
        
        doc.setSourceTransactionSecurity(etsb);
        
        return doc;
    }
    
    /**
     * Test validateSecurity method in the rule class
     */
    public void testValidateSecurity() {
        LOG.info("testValidateSecurity() entered.");
        
        String securityId = document.getSourceTransactionSecurity().getSecurityID();
        document.getSourceTransactionSecurity().setSecurityID(INVALID_SECURITY_ID);
        
        document.getSourceTransactionSecurity().setSecurityID(securityId);        
        assertTrue(rule.validateSecurity(true, document, true));
    }
    
    /**
     * Test validateRegistration method in the rule class
     */
    public void testValidateRegistration() {
        LOG.info("testValidateRegistration() entered.");
        
        String registrationCode = document.getSourceTransactionSecurity().getRegistrationCode();
        document.getSourceTransactionSecurity().setRegistrationCode(INVALID_REGISTRATION_CODE);
        
        document.getSourceTransactionSecurity().setRegistrationCode(registrationCode);        
        assertTrue(rule.validateRegistration(true, document, true));
        
    }
    
    /**
     * Test checkCashTransactionEndowmentCode method in the rule class
     */
    public void testCheckCashTransactionEndowmentCode() {
        LOG.info("testCheckCashTransactionEndowmentCode() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        line.setEtranCode(ETRAN_CODE);
        assertFalse(rule.checkCashTransactionEndowmentCode(document, line, null));
        
        line.setEtranCode(null);
        assertTrue(rule.checkCashTransactionEndowmentCode(document, line, null));
    }
    
    /**
     * Test to check validateTransactionAmountLessThanZero method in the rule class
     */
    public void testValidateTransactionAmount() {
        LOG.info("testValidateTransactionAmount() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        
        line.setTransactionAmount(POSITIVE_AMOUNT);
        assertFalse(rule.validateTransactionAmountLessThanZero(line, null));
        
        line.setTransactionAmount(NEGATIVE_AMOUNT);
        assertTrue(rule.validateTransactionAmountLessThanZero(line, null));

        line.setTransactionAmount(POSITIVE_AMOUNT);
        assertTrue(rule.validateTransactionAmountGreaterThanZero(line, null));
        
        line.setTransactionAmount(NEGATIVE_AMOUNT);
        assertFalse(rule.validateTransactionAmountGreaterThanZero(line, null));
    }
    
    /**
     * Test to check units are positive or negative by calling the methods in the rule class
     */
    public void testValidateTransactionUnits() {
        LOG.info("testValidateTransactionUnits() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        
        line.setTransactionUnits(POSITIVE_UNITS);
        assertFalse(rule.validateTransactionUnitsLessThanZero(line, null));
        
        line.setTransactionUnits(NEGATIVE_UNITS);
        assertTrue(rule.validateTransactionUnitsLessThanZero(line, null));

        line.setTransactionUnits(POSITIVE_UNITS);
        assertTrue(rule.validateTransactionUnitsGreaterThanZero(line, null));
        
        line.setTransactionUnits(NEGATIVE_UNITS);
        assertFalse(rule.validateTransactionUnitsGreaterThanZero(line, null));
    }
    
    /**
     * Test to see if units and amounts entered are equal.
     */
    public void testValidateTransactionUnitsAmountEqual() {
        LOG.info("testValidateTransactionUnitsAmountEqual() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        
        line.setTransactionAmount(POSITIVE_AMOUNT);
        line.setTransactionUnits(NEGATIVE_UNITS);
        assertFalse(rule.validateTransactionUnitsAmountEqual(line, null));
        
        line.setTransactionAmount(POSITIVE_AMOUNT);
        line.setTransactionUnits(POSITIVE_UNITS);
        assertTrue(rule.validateTransactionUnitsAmountEqual(line, null));
    }
    
    /**
     * Test to check transactionLineSizeGreaterThanZero method in the rule class
     */
    public void testTransactionLineSizeGreaterThanZero() {
        LOG.info("testTransactionLineSizeGreaterThanZero() entered.");
        
        assertFalse(rule.transactionLineSizeGreaterThanZero(document, true)); //no transaction lines in the document

        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        line.setTransactionAmount(POSITIVE_AMOUNT);
        document.getSourceTransactionLines().add(line);
        
        assertTrue(rule.transactionLineSizeGreaterThanZero(document, true)); //no transaction lines in the document
    }
    
    /**
     * test to add a transaction line..
     */
    public void testProcessAddTransactionLineRules() {
        LOG.info("testProcessAddTransactionLineRules() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        line.setDocumentNumber(document.getDocumentNumber());
        line.setTransactionAmount(POSITIVE_AMOUNT);
        line.setTransactionUnits(POSITIVE_UNITS);

        assertTrue(rule.processAddTransactionLineRules(document, line));
    }
    
    /**
     * Test to check processCustomRouteDocumentBusinessRules in the rule class...
     */
    public void testProcessCustomRouteDocumentBusinessRules() {
        LOG.info("testProcessCustomRouteDocumentBusinessRules() entered.");

        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        line.setDocumentNumber(document.getDocumentNumber());
        line.setTransactionAmount(POSITIVE_AMOUNT);
        line.setTransactionUnits(POSITIVE_UNITS);

        assertTrue(rule.processAddTransactionLineRules(document, line));
        document.getSourceTransactionLines().add(line);
        
        assertTrue(rule.processCustomRouteDocumentBusinessRules(document));
    }
}