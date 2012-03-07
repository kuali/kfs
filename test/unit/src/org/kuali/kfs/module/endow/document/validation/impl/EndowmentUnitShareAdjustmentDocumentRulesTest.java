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

import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.EndowmentUnitShareAdjustmentDocument;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.api.exception.WorkflowException;

@ConfigureContext(session = khuntley)
public class EndowmentUnitShareAdjustmentDocumentRulesTest extends KualiTestBase {

    private EndowmentUnitShareAdjustmentDocumentRules rule;
    private EndowmentUnitShareAdjustmentDocument document;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new EndowmentUnitShareAdjustmentDocumentRules();
        document = createEndowmentUnitShareAdjustmentDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        super.tearDown();
    }

    private EndowmentUnitShareAdjustmentDocument createEndowmentUnitShareAdjustmentDocument() throws WorkflowException {

        // create an Endowment Unit Share Adjustment Document
        document = (EndowmentUnitShareAdjustmentDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_UNIT_SHARE_ADJ.createEndowmentTransactionDocument(EndowmentUnitShareAdjustmentDocument.class);
        document.getDocumentHeader().setDocumentDescription("This is a test Endowment Unit Share Adjustment Document.");

        return document;
    }

    // validate security details

    /**
     * Validates that validateSecurityCode returns true when a valid security is set.
     */
    public void testValidateSecurity_True() {
        // add security details
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();

        EndowmentSourceTransactionSecurity sourcetTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourcetTransactionSecurity.setSecurityID(security.getId());
        sourcetTransactionSecurity.setSecurity(security);

        document.setSourceTransactionSecurity(sourcetTransactionSecurity);

        assertTrue(rule.validateSecurityCode(document, true));
    }

    /**
     * Validates that validateSecurityCode returns false when an invalid security is set.
     */
    public void testValidateSecurity_False() {
        EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourceTransactionSecurity.setSecurityID(EndowTestConstants.INVALID_SECURITY_ID);

        document.setSourceTransactionSecurity(sourceTransactionSecurity);

        assertFalse(rule.validateSecurityCode(document, true));
    }

    /**
     * Validates that validateRegistrationCode returns true when a valid registration code is set.
     */
    public void testValidateRegistration_True() {

        // add security details
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();

        EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourceTransactionSecurity.setRegistrationCode(registrationCode.getCode());
        sourceTransactionSecurity.setRegistrationCodeObj(registrationCode);

        document.setSourceTransactionSecurity(sourceTransactionSecurity);

        assertTrue(rule.validateRegistrationCode(document, true));

    }

    /**
     * Validates that validateRegistrationCode returns false when a valid registration code is set.
     */
    public void testValidateRegistration_False() {

        EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourceTransactionSecurity.setRegistrationCode(EndowTestConstants.INVALID_REGISTRATION_CODE);

        document.setSourceTransactionSecurity(sourceTransactionSecurity);

        assertFalse(rule.validateRegistrationCode(document, true));

    }

    /**
     * Validates that isSecurityActive returns true when an active security is added.
     */
    public void testActiveSecurity_True() {
        // add security details
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();

        EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourceTransactionSecurity.setSecurityID(security.getId());
        sourceTransactionSecurity.setSecurity(security);

        document.setSourceTransactionSecurity(sourceTransactionSecurity);

        assertTrue(rule.isSecurityActive(document, true));
    }

    /**
     * Validates that isSecurityActive returns false when an inactive security is added.
     */
    public void testActiveSecurity_False() {
        // add security details
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.INACTIVE_SECURITY.createSecurityRecord();

        EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourceTransactionSecurity.setSecurityID(security.getId());
        sourceTransactionSecurity.setSecurity(security);

        document.setSourceTransactionSecurity(sourceTransactionSecurity);

        assertFalse(rule.isSecurityActive(document, true));
    }

    /**
     * Validates that validateSecurityClassCodeTypeNotLiability returns true when a security with a class code other that Liability
     * is added.
     */
    public void testLiabilityClassCode_True() {
        // add security details
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.NOT_LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();

        security.setClassCode(classCode);
        security.setSecurityClassCode(classCode.getCode());

        EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourceTransactionSecurity.setSecurityID(security.getId());
        sourceTransactionSecurity.setSecurity(security);

        document.getSourceTransactionSecurities().add(sourceTransactionSecurity);

        assertTrue(rule.validateSecurityClassCodeTypeNotLiability(document, true));
    }

    /**
     * Validates that validateSecurityClassCodeTypeNotLiability returns false when a security with a class code of Liability is
     * added.
     */
    public void testLiabilityClassCode_False() {
        // add security details

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();

        security.setClassCode(classCode);
        security.setSecurityClassCode(classCode.getCode());

        EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourceTransactionSecurity.setSecurityID(security.getId());
        sourceTransactionSecurity.setSecurity(security);

        document.getSourceTransactionSecurities().add(sourceTransactionSecurity);

        assertFalse(rule.validateSecurityClassCodeTypeNotLiability(document, true));

    }

    // validate transaction lines

    /**
     * Validates that validateKemId returns true when the kemid on the transaction line exists in the db.
     */
    public void testValidateKemIdSource_True() {

        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);

        assertTrue(rule.validateKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateKemId returns true when the kemid on the transaction line exists in the db.
     */
    public void testValidateKemIdTarget_True() {

        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);

        assertTrue(rule.validateKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateKemId returns false when the kemid on the transaction line does not exist in the db.
     */
    public void testValidateKemIdSource_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        endowmentTransactionLine.setKemid(EndowTestConstants.INVALID_KEMID);

        assertFalse(rule.validateKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateKemId returns false when the kemid on the transaction line does not exist in the db.
     */
    public void testValidateKemIdTarget_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);
        endowmentTransactionLine.setKemid(EndowTestConstants.INVALID_KEMID);

        assertFalse(rule.validateKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that isActiveKemId returns true when the KEMID is open (closed indicator is false).
     */
    public void testIsActiveKemIdSource_True() {
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);

        assertTrue(rule.isActiveKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that isActiveKemId returns true when the KEMID is open (closed indicator is false).
     */
    public void testIsActiveKemIdTarget_True() {
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);

        assertTrue(rule.isActiveKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that isActiveKemId returns false when the KEMID is closed (closed indicator is true).
     */
    public void testIsActiveKemIdSource_False() {
        KEMID kemid = KemIdFixture.CLOSED_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);

        assertFalse(rule.isActiveKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that isActiveKemId returns false when the KEMID is closed (closed indicator is true).
     */
    public void testIsActiveKemIdTarget_False() {
        KEMID kemid = KemIdFixture.CLOSED_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);

        assertFalse(rule.isActiveKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateNoTransactionRestriction returns false when the KEMID has a transaction restriction code equal to
     * NTRAN.
     */
    public void testTransactionsNotAllowedForNTRANKemidSource_False() {

        KEMID ntranKemid = KemIdFixture.NO_TRAN_KEMID_RECORD.createKemidRecord();
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setKemid(ntranKemid.getKemid());
        endowmentSourceTransactionLine.setKemidObj(ntranKemid);

        assertFalse(rule.validateNoTransactionRestriction(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateNoTransactionRestriction returns false when the KEMID has a transaction restriction code equal to
     * NTRAN.
     */
    public void testTransactionsNotAllowedForNTRANKemidTarget_False() {

        KEMID ntranKemid = KemIdFixture.NO_TRAN_KEMID_RECORD.createKemidRecord();
        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTargetTransactionLine.setKemid(ntranKemid.getKemid());
        endowmentTargetTransactionLine.setKemidObj(ntranKemid);

        assertFalse(rule.validateNoTransactionRestriction(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    /**
     * Validates that validateNoTransactionRestriction returns true when the KEMID has a transaction restriction code different from
     * NTRAN.
     */
    public void testTransactionsNotAllowedForNTRANKemidSource_True() {

        KEMID ntranKemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setKemid(ntranKemid.getKemid());
        endowmentSourceTransactionLine.setKemidObj(ntranKemid);

        assertTrue(rule.validateNoTransactionRestriction(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateNoTransactionRestriction returns true when the KEMID has a transaction restriction code different from
     * NTRAN.
     */
    public void testTransactionsNotAllowedForNTRANKemidTarget_True() {

        KEMID ntranKemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTargetTransactionLine.setKemid(ntranKemid.getKemid());
        endowmentTargetTransactionLine.setKemidObj(ntranKemid);

        assertTrue(rule.validateNoTransactionRestriction(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    /**
     * Validates that validateTransactionUnitsGreaterThanZero returns true when transaction units is greater than zero.
     */
    public void testTransactionUnitsPositiveSource_True() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_UNITS.createEndowmentTransactionLine(true);

        assertTrue(rule.validateTransactionUnitsGreaterThanZero(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateTransactionUnitsGreaterThanZero returns true when transaction units is greater than zero.
     */
    public void testTransactionUnitsPositiveTarget_True() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_UNITS.createEndowmentTransactionLine(false);

        assertTrue(rule.validateTransactionUnitsGreaterThanZero(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }


    /**
     * Validates that validateTransactionUnitsGreaterThanZero returns false when transaction units is zero.
     */
    public void testTransactionUnitsPositiveSource_False() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_ZERO_UNITS.createEndowmentTransactionLine(true);

        assertFalse(rule.validateTransactionUnitsGreaterThanZero(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateTransactionUnitsGreaterThanZero returns false when transaction units is zero.
     */
    public void testTransactionUnitsPositiveTarget_False() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_ZERO_UNITS.createEndowmentTransactionLine(false);

        assertFalse(rule.validateTransactionUnitsGreaterThanZero(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }


    // IF the END _TRAN_LN_T: TRAN_IP_IND_CD for the transaction line is equal to P, then the KEMID must have a principal
    // restriction (END_KEMID_T: TYP_PRIN_RESTR_CD) that is not equal to NA which implies that the KEMID cannot have any activity in
    // Principal. This would guarantee that the KEMID has an active general ledger account with the Income/Principal indicator equal
    // to P.

    /**
     * Validates that canKEMIDHaveAPrincipalTransaction returns true when the transaction line IP indicator is P and the principal
     * restriction code is not NA.
     */
    public void testKemidPrincRestrNotNAWhenTransLinePrincipalSource_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(true);
        KEMID kemid = KemIdFixture.NOT_NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();
        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertTrue(rule.canKEMIDHaveAPrincipalTransaction(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that canKEMIDHaveAPrincipalTransaction returns true when the transaction line IP indicator is P and the principal
     * restriction code is not NA.
     */
    public void testKemidPrincRestrNotNAWhenTransLinePrincipalTarget_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(false);
        KEMID kemid = KemIdFixture.NOT_NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();
        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertTrue(rule.canKEMIDHaveAPrincipalTransaction(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that canKEMIDHaveAPrincipalTransaction returns false when the transaction line IP indicator is P and the principal
     * restriction code is NA.
     */
    public void testKemidPrincRestrNotNAWhenTransLinePrincipalSource_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(true);
        KEMID kemid = KemIdFixture.NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();
        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertFalse(rule.canKEMIDHaveAPrincipalTransaction(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that canKEMIDHaveAPrincipalTransaction returns false when the transaction line IP indicator is P and the principal
     * restriction code is NA.
     */
    public void testKemidPrincRestrNotNAWhenTransLinePrincipalTarget_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(false);
        KEMID kemid = KemIdFixture.NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();
        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertFalse(rule.canKEMIDHaveAPrincipalTransaction(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateKemidHasTaxLots returns true when the given kemid has tax lots.
     */
    public void testValidateKemidHasTaxLotsSource_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(true);
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.TEST_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();
        // need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_EAD.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLot holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_EAD.createHoldingTaxLotRecord();

        document.getSourceTransactionSecurity().setSecurityID(security.getId());
        document.getSourceTransactionSecurity().setSecurity(security);
        document.getSourceTransactionSecurity().setRegistrationCode(registrationCode.getCode());
        document.getSourceTransactionSecurity().setRegistrationCodeObj(registrationCode);

        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertTrue(rule.validateKemidHasTaxLots(document, endowmentTransactionLine, -1));

    }

    /**
     * Validates that validateKemidHasTaxLots returns false when the given kemid does not have tax lots.
     */
    public void testValidateKemidHasTaxLotsSource_False() {

        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(true);
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.TEST_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();

        document.getSourceTransactionSecurity().setSecurityID(security.getId());
        document.getSourceTransactionSecurity().setSecurity(security);
        document.getSourceTransactionSecurity().setRegistrationCode(registrationCode.getCode());
        document.getSourceTransactionSecurity().setRegistrationCodeObj(registrationCode);

        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertFalse(rule.validateKemidHasTaxLots(document, endowmentTransactionLine, -1));

    }

    /**
     * Validates that validateKemidHasTaxLots returns true when the given kemid has tax lots.
     */
    public void testValidateKemidHasTaxLotsTarget_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(false);
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.TEST_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();
        // need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_EAD.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLot holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_EAD.createHoldingTaxLotRecord();

        document.getSourceTransactionSecurity().setSecurityID(security.getId());
        document.getSourceTransactionSecurity().setSecurity(security);
        document.getSourceTransactionSecurity().setRegistrationCode(registrationCode.getCode());
        document.getSourceTransactionSecurity().setRegistrationCodeObj(registrationCode);

        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertTrue(rule.validateKemidHasTaxLots(document, endowmentTransactionLine, -1));

    }

    /**
     * Validates that validateKemidHasTaxLots returns false when the given kemid does not have tax lots.
     */
    public void testValidateKemidHasTaxLotsTarget_False() {

        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(false);
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.TEST_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();

        document.getSourceTransactionSecurity().setSecurityID(security.getId());
        document.getSourceTransactionSecurity().setSecurity(security);
        document.getSourceTransactionSecurity().setRegistrationCode(registrationCode.getCode());
        document.getSourceTransactionSecurity().setRegistrationCodeObj(registrationCode);

        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertFalse(rule.validateKemidHasTaxLots(document, endowmentTransactionLine, -1));

    }


    // validate document

    /**
     * Validates that hasAtLeastOneTransactionLine returns true if the document has at least one transaction line.
     */
    public void testHasAtLeastOneTransactionLine_True() {
        EndowmentTransactionLine endowmentTransactionLineFrom = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_EUSA_BASIC.createEndowmentTransactionLine(true);
        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLineFrom);

        assertTrue(rule.hasAtLeastOneTransactionLine(document));
    }

    /**
     * Validates that hasAtLeastOneTransactionLine returns false if the document has no transaction lines.
     */
    public void testHasAtLeastOneTransactionLine_False() {

        assertFalse(rule.hasAtLeastOneTransactionLine(document));
    }

    /**
     * Validates that hasOnlySourceOrTargetTransactionLines returns true if the document has only source or target transaction
     * lines.
     */
    public void testHasOnlySourceOrTargetTransactionLines_True() {
        EndowmentTransactionLine endowmentTransactionLineFrom = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_EUSA_BASIC.createEndowmentTransactionLine(true);
        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLineFrom);

        assertTrue(rule.hasOnlySourceOrTargetTransactionLines(document));
    }

    /**
     * Validates that hasOnlySourceOrTargetTransactionLines returns false if the document has both source and target transaction
     * lines.
     */
    public void testHasOnlySourceOrTargetTransactionLines_False() {
        EndowmentTransactionLine endowmentTransactionLineFrom = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_EUSA_BASIC.createEndowmentTransactionLine(true);
        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLineFrom);

        EndowmentTransactionLine endowmentTransactionLineTo = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_EUSA_BASIC.createEndowmentTransactionLine(false);
        document.addTargetTransactionLine((EndowmentTargetTransactionLine) endowmentTransactionLineTo);

        assertFalse(rule.hasOnlySourceOrTargetTransactionLines(document));
    }


}
