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

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurityBase;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.LiabilityIncreaseDocument;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the rules in LiabilityIncreaseDocumentRules class
 */
@ConfigureContext(session = khuntley)
public class LiabilityIncreaseDocumentRulesTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(LiabilityIncreaseDocumentRulesTest.class);

    private LiabilityIncreaseDocumentRules rule;
    private LiabilityIncreaseDocument document;
    private DocumentService documentService;
    private Security security;
    private ClassCode classCode;
    private KEMID kemid;
    private SecurityReportingGroup reportingGroup;
    private EndowmentTransactionCode endowmentTransactionCode;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new LiabilityIncreaseDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        classCode = ClassCodeFixture.LIABILITY_INCREASE_LIABILITY_CLASS_CODE_2.createClassCodeRecord();
        security = SecurityFixture.LIABILITY_INCREASE_ACTIVE_SECURITY.createSecurityRecord();
        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();

        //create the Liability Decrease document
        document = createLiabilityIncreaseDocument();
        
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
    protected LiabilityIncreaseDocument createLiabilityIncreaseDocument() throws WorkflowException {
        LOG.info("createLiabilityDecreaseDocument() entered.");
        
        LiabilityIncreaseDocument doc = (LiabilityIncreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentTransactionDocument(LiabilityIncreaseDocument.class);
        doc.getDocumentHeader().setDocumentDescription("Testing Liability Increase document.");

        EndowmentTransactionSecurityBase etsb = (EndowmentTargetTransactionSecurity) EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(false);
        etsb.setDocumentNumber(doc.getDocumentNumber());
        
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD_FOR_LIABILITY.createRegistrationCode();
        etsb.setRegistrationCode(registrationCode.getCode());
        etsb.refreshNonUpdateableReferences();
        
        doc.setTargetTransactionSecurity(etsb);
        
        return doc;
    }

    /**
     * Test validateSecurity method in the rule class
     */
    public void testValidateSecurity() {
        LOG.info("testValidateSecurity() entered.");
        
        String securityId = document.getTargetTransactionSecurity().getSecurityID();
        document.getTargetTransactionSecurity().setSecurityID(EndowTestConstants.INVALID_SECURITY_ID);
        
        document.getTargetTransactionSecurity().setSecurityID(securityId);        
        assertTrue(rule.validateSecurity(true, document, false));
    }
    
    /**
     * Test validateRegistration method in the rule class
     */
    public void testValidateRegistration() {
        LOG.info("testValidateRegistration() entered.");
        
        String registrationCode = document.getTargetTransactionSecurity().getRegistrationCode();
        document.getTargetTransactionSecurity().setRegistrationCode(EndowTestConstants.INVALID_REGISTRATION_CODE);
        
        document.getTargetTransactionSecurity().setRegistrationCode(registrationCode);        
        assertTrue(rule.validateRegistration(true, document, false));
        
    }
    
    /**
     * Test checkCashTransactionEndowmentCode method in the rule class
     */
    public void testCheckCashTransactionEndowmentCode() {
        LOG.info("testCheckCashTransactionEndowmentCode() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        line.setEtranCode(EndowTestConstants.ETRAN_CODE);
        assertFalse(rule.checkCashTransactionEndowmentCode(document, line, null));
        
        line.setEtranCode(null);
        assertTrue(rule.checkCashTransactionEndowmentCode(document, line, null));
    }
    
    /**
     * Test to check validateTransactionAmountLessThanZero method in the rule class
     */
    public void testValidateTransactionAmount() {
        LOG.info("testValidateTransactionAmount() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        
        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        assertFalse(rule.validateTransactionAmountLessThanZero(line, null));
        
        line.setTransactionAmount(EndowTestConstants.NEGATIVE_AMOUNT);
        assertTrue(rule.validateTransactionAmountLessThanZero(line, null));

        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        assertTrue(rule.validateTransactionAmountGreaterThanZero(line, null));
        
        line.setTransactionAmount(EndowTestConstants.NEGATIVE_AMOUNT);
        assertFalse(rule.validateTransactionAmountGreaterThanZero(line, null));
    }
    
    /**
     * Test to check units are positive or negative by calling the methods in the rule class
     */
    public void testValidateTransactionUnits() {
        LOG.info("testValidateTransactionUnits() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        
        line.setTransactionUnits(EndowTestConstants.POSITIVE_UNITS);
        assertFalse(rule.validateTransactionUnitsLessThanZero(line, null));
        
        line.setTransactionUnits(EndowTestConstants.NEGATIVE_UNITS);
        assertTrue(rule.validateTransactionUnitsLessThanZero(line, null));

        line.setTransactionUnits(EndowTestConstants.POSITIVE_UNITS);
        assertTrue(rule.validateTransactionUnitsGreaterThanZero(line, null));
        
        line.setTransactionUnits(EndowTestConstants.NEGATIVE_UNITS);
        assertFalse(rule.validateTransactionUnitsGreaterThanZero(line, null));
    }
    
    /**
     * Test to see if units and amounts entered are equal.
     */
    public void testValidateTransactionUnitsAmountEqual() {
        LOG.info("testValidateTransactionUnitsAmountEqual() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        
        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        line.setTransactionUnits(EndowTestConstants.NEGATIVE_UNITS);
        assertFalse(rule.validateTransactionUnitsAmountEqual(line, null));
        
        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        line.setTransactionUnits(EndowTestConstants.POSITIVE_UNITS);
        assertTrue(rule.validateTransactionUnitsAmountEqual(line, null));
    }
    
    /**
     * Test to check transactionLineSizeGreaterThanZero method in the rule class
     */
    public void testTransactionLineSizeGreaterThanZero() {
        LOG.info("testTransactionLineSizeGreaterThanZero() entered.");
        
        assertFalse(rule.transactionLineSizeGreaterThanZero(document, false)); //no transaction lines in the document

        EndowmentTransactionLineBase line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        document.getTargetTransactionLines().add(line);
        
        assertTrue(rule.transactionLineSizeGreaterThanZero(document, false)); //no transaction lines in the document
    }
    
    /**
     * test to add a transaction line..
     */
    public void testProcessAddTransactionLineRules() {
        LOG.info("testProcessAddTransactionLineRules() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        line.setDocumentNumber(document.getDocumentNumber());
        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        line.setTransactionUnits(EndowTestConstants.POSITIVE_UNITS);

        assertTrue(rule.processAddTransactionLineRules(document, line));
    }
    
    /**
     * Test to check processCustomRouteDocumentBusinessRules in the rule class...
     */
    public void testProcessCustomRouteDocumentBusinessRules() {
        LOG.info("testProcessCustomRouteDocumentBusinessRules() entered.");

        EndowmentTransactionLineBase line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        line.setDocumentNumber(document.getDocumentNumber());
        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        line.setTransactionUnits(EndowTestConstants.POSITIVE_UNITS);

        assertTrue(rule.processAddTransactionLineRules(document, line));
        document.getTargetTransactionLines().add(line);
        
        assertTrue(rule.processCustomRouteDocumentBusinessRules(document));
    }
}