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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurityBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionTaxLotLineFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class tests the rules in HoldingAdjustmentDocumentRule
 */
@ConfigureContext(session = khuntley)
public class HoldingAdjustmentDocumentRulesTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(HoldingAdjustmentDocumentRulesTest.class);

    private HoldingAdjustmentDocumentRules rule;
    private HoldingAdjustmentDocument document;
    private DocumentService documentService;
    private UnitTestSqlDao unitTestSqlDao;

    private Security security;
    private ClassCode classCode;
    private KEMID kemid;
    private SecurityReportingGroup reportingGroup;
    private EndowmentTransactionCode endowmentTransactionCode;

    private static final BigDecimal ZERO_AMOUNT = new BigDecimal(0);
    private static final BigDecimal NEGATIVE_AMOUNT = new BigDecimal(-1);
    private static final BigDecimal POSITIVE_AMOUNT = new BigDecimal(2);

    private static final String REFERENCE_DOCUMENT_NUMBER = "123456";
    private static final String REFERENCE_DOCUMENT_DESCRIPTION = "Document Description - Unit Test";

    private static final String KEM_ID = "046G007720";
    private static final String SECURITY_ID = "99BTIP011";
    private static final String REGISTRATION_CODE= "0CP";
    private static final String IP_INDICATOR = "P";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new HoldingAdjustmentDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);

        reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        classCode = ClassCodeFixture.NOT_LIABILITY_CLASS_CODE.createClassCodeRecord();
        security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();

        //create the document
        document = createHoldingAdjustmentDocument();

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
     * create a blank for HistoryHoldingValueAdjustmentDocuemnt
     * @return doc
     * @throws WorkflowException
     */
    protected HoldingAdjustmentDocument createHoldingAdjustmentDocument() throws WorkflowException {
        HoldingAdjustmentDocument doc = (HoldingAdjustmentDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentTransactionDocument(HoldingAdjustmentDocument.class);
        doc.getDocumentHeader().setDocumentDescription("Testing Holding Adjustment.");
        doc.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
        doc.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.MANUAL);

        //adding security details...
        EndowmentTransactionSecurityBase etsb = EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(true);
        etsb.setDocumentNumber(doc.getDocumentNumber());

        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD_FOR_LIABILITY.createRegistrationCode();
        etsb.setRegistrationCode(registrationCode.getCode());
        etsb.refreshNonUpdateableReferences();

        doc.setSourceTransactionSecurity(etsb);
        doc.getSourceTransactionSecurity().refreshNonUpdateableReferences();

        return doc;
    }

    /**
     * Test validateSecurity method to check for security code
     * The test returns false if an invalid security code entered,
     * returns true if valid security code
     */
    public void testValidateSecurity() {
        LOG.info("testValidateSecurity() entered.");

        String securityId = document.getSourceTransactionSecurity().getSecurityID();

        //setting an empty security code so the test fails..
        document.getSourceTransactionSecurity().setSecurityID(null);
        assertFalse(rule.validateSecurity(true, document, true));

        //setting an invalid security code so the test fails..
        document.getSourceTransactionSecurity().setSecurityID(EndowTestConstants.INVALID_SECURITY_ID);
        assertFalse(rule.validateSecurity(true, document, true));

        //setting an inactive security code so the test fails..
        document.getSourceTransactionSecurity().setSecurityID(securityId);
        document.getSourceTransactionSecurity().refreshNonUpdateableReferences();
        document.getSourceTransactionSecurity().getSecurity().setActive(false);
        assertFalse(rule.validateSecurity(true, document, true));

        document.getSourceTransactionSecurity().getSecurity().setActive(true);

        //valid security code so the test should pass..
        document.getSourceTransactionSecurity().setSecurityID(securityId);
        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.validateSecurity(true, document, true));
    }

    /**
     * test to check validateSecurityClassCodeTypeNotLiability() method in the rule class
     */
    public void testValidateSecurityClassCodeTypeNotLiability() {
        LOG.info("testValidateSecurityClassCodeTypeNotLiability() entered.");

        //classcode type is L so the test should assert false...
        String liabilityClassCode = document.getSourceTransactionSecurity().getSecurity().getClassCode().getClassCodeType();
        document.getSourceTransactionSecurity().getSecurity().getClassCode().setClassCodeType(EndowTestConstants.LIABILITY_CLASS_TYPE_CODE);
        document.getSourceTransactionSecurity().getSecurity().getClassCode().refreshNonUpdateableReferences();
        assertFalse(rule.validateSecurityClassCodeTypeNotLiability(document, true));

        document.getSourceTransactionSecurity().getSecurity().getClassCode().setClassCodeType(liabilityClassCode);
        document.getSourceTransactionSecurity().getSecurity().getClassCode().refreshNonUpdateableReferences();
        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.validateSecurityClassCodeTypeNotLiability(document, true));
    }

    /**
     * test to check validateRegistration() method in the rule class.
     * This method check if registration code empty, if it is valid, and if it is active
     */
    public void testValidateRegistration() {
        LOG.info("testValidateRegistration() entered.");

        String registrationCode = document.getSourceTransactionSecurity().getRegistrationCode();

        //setting an empty registration code so the test fails..
        document.getSourceTransactionSecurity().setRegistrationCode(null);
        assertFalse(rule.validateRegistration(true, document, true));

        //setting an invalid registration code so the test fails..
        document.getSourceTransactionSecurity().setRegistrationCode(EndowTestConstants.INVALID_REGISTRATION_CODE);
        assertFalse(rule.validateRegistration(true, document, true));

        //setting an inactive registration code so the test fails..
        document.getSourceTransactionSecurity().setRegistrationCode(registrationCode);
        document.getSourceTransactionSecurity().refreshNonUpdateableReferences();
        document.getSourceTransactionSecurity().getRegistrationCodeObj().setActive(false);
        assertFalse(rule.validateRegistration(true, document, true));

        document.getSourceTransactionSecurity().getRegistrationCodeObj().setActive(true);
        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.validateRegistration(true, document, true));
    }

    /**
     * Method to validate the rule canOnlyAddSourceOrTargetTransactionLines
     * Remove both source and target lines from the document and call the rule -- should pass the test
     * add only source and assert and the results should be true
     * add only target and assert and the results should be true
     * add both source and target and assert and the results should be false.
     */
    public void testCanOnlyAddSourceOrTargetTransactionLines() {
        LOG.info("testCanOnlyAddSourceOrTargetTransactionLines() entered.");

        // remove any source or transaction lines from the document and test the rule.
        int index = 0;

        document.getTargetTransactionLines().clear();
        document.getSourceTransactionLines().clear();

        //test is succeeds when there are no transaction lines in source or target sections
        assertTrue(rule.canOnlyAddSourceOrTargetTransactionLines(document, null, index));

        // add a source transaction line and check the rule - the rule should pass.
        document.getTargetTransactionLines().clear();
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLines);
        assertTrue(rule.canOnlyAddSourceOrTargetTransactionLines(document, document.getSourceTransactionLines().get(index), index));

        //add a target transaction line and check the rule - should pass.
        document.getSourceTransactionLines().clear();
        List<EndowmentTransactionLine> targetTransactionLines = createTargetTransactionLine(index);
        document.setTargetTransactionLines(targetTransactionLines);

        assertTrue(rule.canOnlyAddSourceOrTargetTransactionLines(document, document.getTargetTransactionLines().get(index), index));

        //test fails since there are lines in both sections...
        document.getTargetTransactionLines().clear();
        document.getSourceTransactionLines().clear();

        List<EndowmentTransactionLine> sourceTransactionLinesForBoth = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLinesForBoth);
        List<EndowmentTransactionLine> targetTransactionLinesForBoth = createTargetTransactionLine(index);
        document.setTargetTransactionLines(targetTransactionLinesForBoth);
        assertFalse(rule.canOnlyAddSourceOrTargetTransactionLines(document, document.getTargetTransactionLines().get(index), index));
    }

    /**
     * test to validate hasOnlySourceOrTargetTransactionLines() method in the rule class
     */
    public void testHasOnlySourceOrTargetTransactionLines() {
        LOG.info("testHasOnlySourceOrTargetTransactionLines() entered.");

        int index = 0;

        // add a source transaction line and check the rule - the rule should pass.
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLines);
        List<EndowmentTransactionLine> targetTransactionLines = createTargetTransactionLine(index);
        document.setTargetTransactionLines(targetTransactionLines);
        //fails since there are lines in both sections....
        assertFalse("Both Source and Target Lines entered.", rule.hasOnlySourceOrTargetTransactionLines(document));

        document.getTargetTransactionLines().clear();
        document.getSourceTransactionLines().clear();
        //fails since there are no lines in either sections....
        assertTrue("There can only be either Source or Target Lines.", rule.hasOnlySourceOrTargetTransactionLines(document));

        document.setSourceTransactionLines(sourceTransactionLines);
        assertTrue("There can only be either Source or Target Lines.", rule.hasOnlySourceOrTargetTransactionLines(document));
    }

    /**
     * Helper Method to create a source transaction line
     */
    private List<EndowmentTransactionLine> createSourceTransactionLine(int index) {
        ArrayList<EndowmentTransactionLine> sourceTransactionLine = new ArrayList<EndowmentTransactionLine>();

        EndowmentTransactionLineBase endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        endowmentSourceTransactionLine.setEtranCode(EndowTestConstants.ETRAN_CODE);
        endowmentSourceTransactionLine.setTransactionLineDescription("Source Line Description");
        endowmentSourceTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        endowmentSourceTransactionLine.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        endowmentSourceTransactionLine.setUnitAdjustmentAmount(EndowTestConstants.POSITIVE_UNITS.bigDecimalValue());

        endowmentSourceTransactionLine.setDocumentNumber(document.getDocumentNumber());
        sourceTransactionLine.add(index, endowmentSourceTransactionLine);

        return sourceTransactionLine;
    }

    /**
     * Helper Method to create a source transaction line
     */
    private List<EndowmentTransactionLine> createTargetTransactionLine(int index) {
        ArrayList<EndowmentTransactionLine> targetTransactionLine = new ArrayList<EndowmentTransactionLine>();

        EndowmentTransactionLineBase endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        endowmentTargetTransactionLine.setEtranCode(EndowTestConstants.ETRAN_CODE);
        endowmentTargetTransactionLine.setTransactionLineDescription("Target Line Description");
        endowmentTargetTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        endowmentTargetTransactionLine.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        endowmentTargetTransactionLine.setUnitAdjustmentAmount(EndowTestConstants.POSITIVE_UNITS.bigDecimalValue());

        endowmentTargetTransactionLine.setDocumentNumber(document.getDocumentNumber());
        targetTransactionLine.add(index, endowmentTargetTransactionLine);

        return targetTransactionLine;
    }

    /**
     * test to validate method checkIfBothTransactionAmountAndUnitAdjustmentAmountEmpty()
     * assert fails if either transaction amount and units adjustment amount entered
     * as zero or null else returns true.
     */
    public void testCheckIfBothTransactionAmountAndUnitAdjustmentAmountEmpty() {
        LOG.info("testCheckIfBothTransactionAmountAndUnitAdjustmentAmountEmpty() entered.");

        int index = 0;

        // add a source transaction line and check the rule - the rule should pass.
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        sourceTransactionLines.get(index).setTransactionAmount(null);
        sourceTransactionLines.get(index).setUnitAdjustmentAmount(null);
        //the assertion should be true
        assertTrue(rule.checkIfBothTransactionAmountAndUnitAdjustmentAmountEmpty(sourceTransactionLines.get(index), index));

        sourceTransactionLines.get(index).setTransactionAmount(EndowTestConstants.ZERO_AMOUNT);
        sourceTransactionLines.get(index).setUnitAdjustmentAmount(BigDecimal.ZERO);
        //the assertion should be true
        assertTrue(rule.checkIfBothTransactionAmountAndUnitAdjustmentAmountEmpty(sourceTransactionLines.get(index), index));

        sourceTransactionLines.get(index).setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        sourceTransactionLines.get(index).setUnitAdjustmentAmount(null);
        //the assertion should be true
        assertFalse(rule.checkIfBothTransactionAmountAndUnitAdjustmentAmountEmpty(sourceTransactionLines.get(index), index));
    }

    /**
     * test to validate method checkIfBothTransactionAmountAndUnitAdjustmentAmountEmpty()
     * assert true if both transaction amount and units adjustment amount entered nonzero
     * else returns false.
     */
    public void testCheckIfBothTransactionAmountAndUnitAdjustmentAmountEntered() {
        LOG.info("testCheckIfBothTransactionAmountAndUnitAdjustmentAmountEntered() entered.");

        int index = 0;

        // add a source transaction line and check the rule - the rule should pass.
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        sourceTransactionLines.get(index).setTransactionAmount(null);
        sourceTransactionLines.get(index).setUnitAdjustmentAmount(null);
        //the assertion should be false
        assertFalse(rule.checkIfBothTransactionAmountAndUnitAdjustmentAmountEntered(sourceTransactionLines.get(index), index));

        sourceTransactionLines.get(index).setTransactionAmount(EndowTestConstants.ZERO_AMOUNT);
        sourceTransactionLines.get(index).setUnitAdjustmentAmount(BigDecimal.ZERO);
        //the assertion should be false
        assertFalse(rule.checkIfBothTransactionAmountAndUnitAdjustmentAmountEntered(sourceTransactionLines.get(index), index));

        sourceTransactionLines.get(index).setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);
        sourceTransactionLines.get(index).setUnitAdjustmentAmount(BigDecimal.ZERO);
        //the assertion should be false
        assertTrue(rule.checkIfBothTransactionAmountAndUnitAdjustmentAmountEntered(sourceTransactionLines.get(index), index));

    }

    /**
     * Method to test validateKemidHasTaxLots rule
     */
    public void testValidateKemidHasTaxLots() {
        LOG.info("testValidateKemidHasTaxLots() entered.");

        int index = 0;

        String registrationCode = document.getSourceTransactionSecurity().getRegistrationCode();

        // add a source transaction line and check the rule - the rule should pass.
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLines);
        document.getSourceTransactionSecurity().setRegistrationCode(EndowTestConstants.INVALID_REGISTRATION_CODE);

        assertFalse("validateKemidHasTaxLots should have returned false",rule.validateKemidHasTaxLots(document, document.getSourceTransactionLines().get(index), index));

        document.getSourceTransactionSecurity().setRegistrationCode(registrationCode);
        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.validateKemidHasTaxLots(document, document.getSourceTransactionLines().get(index), index));
    }

    /**
     * test to validate processAddTransactionLineRules() method in the rule class.
     */
    public void testProcessAddTransactionLineRules() {
        LOG.info("testProcessAddTransactionLineRules() entered.");

        int index = 0;

        String registrationCode = document.getSourceTransactionSecurity().getRegistrationCode();

        // add a source transaction line and check the rule - the rule should pass.
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLines);
        sourceTransactionLines.get(index).setUnitAdjustmentAmount(null);
        document.getSourceTransactionSecurity().setRegistrationCode(EndowTestConstants.INVALID_REGISTRATION_CODE);
        //should fail since we set an invalid registration code...
        assertFalse("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.processAddTransactionLineRules(document, document.getSourceTransactionLines().get(index)));

        //should pass...first clear the error messages from the above assertFalse test..
        GlobalVariables.getMessageMap().clearErrorMessages();
        document.getSourceTransactionSecurity().setRegistrationCode(registrationCode);
        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.processAddTransactionLineRules(document, document.getSourceTransactionLines().get(index)));
    }

    /**
     * test to validate processDeleteTaxLotLineRules() method in the rule class
     */
    public void testProcessDeleteTaxLotLineRules() {
        LOG.info("testProcessDeleteTaxLotLineRules() entered.");

        int index = 0;

        //add a source line...
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLines);
        document.getSourceTransactionLines().get(index).setUnitAdjustmentAmount(null);

        EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine1 = EndowmentTransactionTaxLotLineFixture.TAX_LOT_RECORD1.createEndowmentTransactionTaxLotLineRecord();
        endowmentTransactionTaxLotLine1.setDocumentNumber(document.getDocumentNumber());
        List<EndowmentTransactionTaxLotLine> taxLotLines = new ArrayList();

        taxLotLines.add(endowmentTransactionTaxLotLine1);

        //add the tax lot lines
        document.getSourceTransactionLines().get(index).setTaxLotLines(taxLotLines);

        //exactly one tax lot record.  So assert should fail...
        assertFalse(rule.processDeleteTaxLotLineRules(document, endowmentTransactionTaxLotLine1, document.getSourceTransactionLines().get(index), index, index));

        EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine2 = EndowmentTransactionTaxLotLineFixture.TAX_LOT_RECORD2.createEndowmentTransactionTaxLotLineRecord();
        endowmentTransactionTaxLotLine2.setDocumentNumber(document.getDocumentNumber());
        document.getSourceTransactionLines().get(index).getTaxLotLines().add(endowmentTransactionTaxLotLine2);

        //Two tax lot records.  So assert should true...
        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.processDeleteTaxLotLineRules(document, endowmentTransactionTaxLotLine1, document.getSourceTransactionLines().get(index), index, index));
    }

    /**
     * test to validate processCustomRouteDocumentBusinessRules() method in the rule class.
     */
    public void testProcessCustomRouteDocumentBusinessRules() {
        LOG.info("testProcessCustomRouteDocumentBusinessRules() entered.");

        int index = 0;

        //there are no transaction lines added yet... so assert should fails...
        assertFalse(rule.processCustomRouteDocumentBusinessRules(document));

        //clear any error messages before the preceding assertion...
        GlobalVariables.getMessageMap().clearErrorMessages();

        // add a source transaction line and check the rule - the rule should pass.
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLines);
        sourceTransactionLines.get(index).setUnitAdjustmentAmount(new BigDecimal("20.00"));
        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.processCustomRouteDocumentBusinessRules(document));
    }
}



