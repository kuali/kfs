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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.service.UpdateAssetDecreaseDocumentTaxLotsService;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.GLLinkFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.KemidGeneralLedgerAccountFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * This class...
 */
@ConfigureContext(session = khuntley)
public class AssetDecreaseDocumentRulesTest extends KualiTestBase {

    private AssetDecreaseDocumentRules rule;
    private AssetDecreaseDocument document;
    private UpdateAssetDecreaseDocumentTaxLotsService assetDecreaseDocumentTaxLotsService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new AssetDecreaseDocumentRules();
        assetDecreaseDocumentTaxLotsService = SpringContext.getBean(UpdateAssetDecreaseDocumentTaxLotsService.class);
        document = createAssetDecreaseDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        assetDecreaseDocumentTaxLotsService = null;
        super.tearDown();
    }

    private AssetDecreaseDocument createAssetDecreaseDocument() throws WorkflowException {

        // create an asset decrease document
        document = (AssetDecreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_ASSET_DECREASE.createEndowmentTransactionDocument(AssetDecreaseDocument.class);
        document.getDocumentHeader().setDocumentDescription("This is a test Asset Decrease document.");

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
    public void testValidateKemId_True() {

        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);

        assertTrue(rule.validateKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateKemId returns false when the kemid on the transaction line does not exist in the db.
     */
    public void testValidateKemId_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        endowmentTransactionLine.setKemid(EndowTestConstants.INVALID_KEMID);

        assertFalse(rule.validateKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that isActiveKemId returns true when the KEMID is open (closed indicator is false).
     */
    public void testIsActiveKemId_True() {
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);

        assertTrue(rule.isActiveKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that isActiveKemId returns false when the KEMID is closed (closed indicator is true).
     */
    public void testIsActiveKemId_False() {
        KEMID kemid = KemIdFixture.CLOSED_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);

        assertFalse(rule.isActiveKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateNoTransactionRestriction returns false when the KEMID has a transaction restriction code equal to
     * NTRAN.
     */
    public void testTransactionsNotAllowedForNTRANKemid_False() {

        KEMID ntranKemid = KemIdFixture.NO_TRAN_KEMID_RECORD.createKemidRecord();
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setKemid(ntranKemid.getKemid());
        endowmentSourceTransactionLine.setKemidObj(ntranKemid);

        assertFalse(rule.validateNoTransactionRestriction(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateNoTransactionRestriction returns true when the KEMID has a transaction restriction code different from
     * NTRAN.
     */
    public void testTransactionsNotAllowedForNTRANKemid_True() {

        KEMID ntranKemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setKemid(ntranKemid.getKemid());
        endowmentSourceTransactionLine.setKemidObj(ntranKemid);

        assertTrue(rule.validateNoTransactionRestriction(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }


    /**
     * Validates that validateTransactionAmountGreaterThanZero returns true when transaction amount is greater than zero.
     */
    public void testTransactionsLineAmountPositive_True() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(true);

        assertTrue(rule.validateTransactionAmountGreaterThanZero(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateTransactionAmountGreaterThanZero returns false when transaction amount is zero.
     */
    public void testTransactionsLineAmountPositive_False() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_ZERO_AMT.createEndowmentTransactionLine(true);

        assertFalse(rule.validateTransactionAmountGreaterThanZero(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateTransactionUnitsGreaterThanZero returns true when transaction units is greater than zero.
     */
    public void testTransactionUnitsPositive_True() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_UNITS.createEndowmentTransactionLine(true);

        assertTrue(rule.validateTransactionUnitsGreaterThanZero(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }


    /**
     * Validates that validateTransactionUnitsGreaterThanZero returns false when transaction units is zero.
     */
    public void testTransactionUnitsPositive_False() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_ZERO_UNITS.createEndowmentTransactionLine(true);

        assertFalse(rule.validateTransactionUnitsGreaterThanZero(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    // The ETRAN code used in the Transaction Line must have an ETRAN Type code equal to I ( Income ) or E (Expense)
    /**
     * Validates that validateEndowmentTransactionTypeCode returns true when the etran code type is income.
     */
    public void testEtranCodeIncome_True() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(true);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        endowmentSourceTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentSourceTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertTrue(rule.validateEndowmentTransactionTypeCode(document, endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateEndowmentTransactionTypeCode returns true when the etran code type is expense.
     */
    public void testEtranCodeExpense_True() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(true);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.EXPENSE_TRANSACTION_CODE.createEndowmentTransactionCode();
        endowmentSourceTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentSourceTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertTrue(rule.validateEndowmentTransactionTypeCode(document, endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    /**
     * Validates that validateEndowmentTransactionTypeCode returns false when the etran code type is not income or expense.
     */
    public void testEtranCodeIncomeOrExpense_False() {

        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(true);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.ASSET_TRANSACTION_CODE.createEndowmentTransactionCode();
        endowmentSourceTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentSourceTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertFalse(rule.validateEndowmentTransactionTypeCode(document, endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));

    }

    // The ETRAN Code used must have an appropriately identified general ledger object code record; one that matches the Chart for
    // the KEMID associated general ledger account.

    /**
     * Validates that validateChartMatch returns true when etran code gl chart matches the chart for KEMID general ledger account.
     */
    public void testKemidEtranCodeMatch_True() {
        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        GLLink glLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT.createKemidGeneralLedgerAccount();

        kemid.getKemidGeneralLedgerAccounts().add(generalLedgerAccount);
        endowmentTransactionCode.getGlLinks().add(glLink);
        endowmentSourceTransactionLine.setKemid(kemid.getKemid());
        endowmentSourceTransactionLine.setKemidObj(kemid);
        endowmentSourceTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentSourceTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertTrue(rule.validateChartMatch(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));
    }

    /**
     * Validates that validateChartMatch returns false when etran code gl chart does not match the chart for KEMID general ledger
     * account.
     */
    public void testKemidEtranCodeMatch_False() {
        EndowmentTransactionLine endowmentSourceTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        GLLink glLink = GLLinkFixture.GL_LINK_UA_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT.createKemidGeneralLedgerAccount();

        kemid.getKemidGeneralLedgerAccounts().add(generalLedgerAccount);
        endowmentTransactionCode.getGlLinks().add(glLink);
        endowmentSourceTransactionLine.setKemid(kemid.getKemid());
        endowmentSourceTransactionLine.setKemidObj(kemid);
        endowmentSourceTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentSourceTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertFalse(rule.validateChartMatch(endowmentSourceTransactionLine, rule.getErrorPrefix(endowmentSourceTransactionLine, -1)));
    }

    // IF the END _TRAN_LN_T: TRAN_IP_IND_CD for the transaction line is equal to P, then the KEMID must have a principal
    // restriction (END_KEMID_T: TYP_PRIN_RESTR_CD) that is not equal to NA which implies that the KEMID cannot have any activity in
    // Principal. This would guarantee that the KEMID has an active general ledger account with the Income/Principal indicator equal
    // to P.

    /**
     * Validates that canKEMIDHaveAPrincipalTransaction returns true when the transaction line IP indicator is P and the principal
     * restriction code is not NA.
     */
    public void testKemidPrincRestrNotNAWhenTransLinePrincipal_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(true);
        KEMID kemid = KemIdFixture.NOT_NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();
        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertTrue(rule.canKEMIDHaveAPrincipalTransaction(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that canKEMIDHaveAPrincipalTransaction returns false when the transaction line IP indicator is P and the principal
     * restriction code is NA.
     */
    public void testKemidPrincRestrNotNAWhenTransLinePrincipal_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(true);
        KEMID kemid = KemIdFixture.NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();
        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertFalse(rule.canKEMIDHaveAPrincipalTransaction(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateSufficientUnits returns true when the tax lots for the given kemid has enough units to perform the
     * given transaction.
     */
    public void testValidateSufficientUnits_True() {
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

        assertTrue(rule.validateSufficientUnits(true, document, endowmentTransactionLine, -1, -1));

    }

    /**
     * Validates that validateSufficientUnits returns false when the tax lots for the given kemid does not have enough units to
     * perform the given transaction.
     */
    public void testValidateSufficientUnits_False() {

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

        endowmentTransactionLine.setTransactionUnits(new KualiDecimal(1000000));

        assertFalse(rule.validateSufficientUnits(true, document, endowmentTransactionLine, -1, -1));

    }

    // if Sub-Type Cash
    // The initiator cannot enter an Etran Code in the transaction line.
    /**
     * Validates that checkCashTransactionEndowmentCode returns true if the transaction line is sub type is cash and the user did
     * not enter an endowment transaction code.
     */
    public void testCheckCashTransactionEndowmentCodeSubTypeCash_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_EAD_NO_ETRAN_CD.createEndowmentTransactionLine(true);

        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);
        document.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);

        assertTrue(rule.checkCashTransactionEndowmentCode(document, endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that checkCashTransactionEndowmentCode returns false if the transaction line is sub type is cash and the user did
     * enter an endowment transaction code.
     */
    public void testCheckCashTransactionEndowmentCodeSubTypeCash_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_EAD_WITH_ETRAN_CD.createEndowmentTransactionLine(true);

        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);
        document.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);

        assertFalse(rule.checkCashTransactionEndowmentCode(document, endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateTotalAmountAndUnits returns true when transaction line units and amount equal the tax lot lines units
     * and cost.
     */
    public void testValidateTotalAmountAndUnits_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(true);

        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);
        assetDecreaseDocumentTaxLotsService.updateTransactionLineTaxLots(document, endowmentTransactionLine);

        assertTrue(rule.validateTotalAmountAndUnits(document, endowmentTransactionLine, 0));
    }

    /**
     * Validates that validateTotalAmountAndUnits returns false when transaction line units and amount do not equal the tax lot
     * lines units and cost.
     */
    public void testValidateTotalAmountAndUnits_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(true);

        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);
        // do not update the tax lot lines

        assertFalse(rule.validateTotalAmountAndUnits(document, endowmentTransactionLine, 0));
    }

    /**
     * Validates that validateTaxLots returns true when transaction line has same kemid, security, registration code and IP
     * indicator as the tax lot lines.
     */
    public void testValidateTaxLots_True() {
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();

        EndowmentSourceTransactionSecurity sourcetTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourcetTransactionSecurity.setSecurityID(security.getId());
        sourcetTransactionSecurity.setSecurity(security);

        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();

        sourcetTransactionSecurity.setRegistrationCode(registrationCode.getCode());
        sourcetTransactionSecurity.setRegistrationCodeObj(registrationCode);

        document.setSourceTransactionSecurity(sourcetTransactionSecurity);

        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(true);

        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);
        assetDecreaseDocumentTaxLotsService.updateTransactionLineTaxLots(document, endowmentTransactionLine);

        assertTrue(rule.validateTaxLots(document, endowmentTransactionLine, 0));
    }

    /**
     * Validates thatvalidateTaxLots returns false when transaction line does not have same kemid, security, registration code and
     * IP indicator as the tax lot lines.
     */
    public void testValidateTaxLots_False() {
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();

        EndowmentSourceTransactionSecurity sourcetTransactionSecurity = new EndowmentSourceTransactionSecurity();
        sourcetTransactionSecurity.setSecurityID(security.getId());
        sourcetTransactionSecurity.setSecurity(security);

        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();

        sourcetTransactionSecurity.setRegistrationCode(registrationCode.getCode());
        sourcetTransactionSecurity.setRegistrationCodeObj(registrationCode);

        document.setSourceTransactionSecurity(sourcetTransactionSecurity);

        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(true);

        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);
        assetDecreaseDocumentTaxLotsService.updateTransactionLineTaxLots(document, endowmentTransactionLine);

        // change registration code after the tax lots have been updated
        RegistrationCode registrationCode2 = RegistrationCodeFixture.REGISTRATION_CODE_RECORD2.createRegistrationCode();

        sourcetTransactionSecurity.setRegistrationCode(registrationCode2.getCode());
        sourcetTransactionSecurity.setRegistrationCodeObj(registrationCode2);

        assertFalse(rule.validateTaxLots(document, endowmentTransactionLine, 0));
    }


    // validate document

    // There must be at least one transaction line in each transaction line section (To and/or From) that is required for the
    // document type to successfully submit the document.

    /**
     * Validates that transactionLineSizeGreaterThanZero returns true if the document has at least one source transaction line.
     */
    public void testDocumentHasAtLeastOneTransactionLine_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        document.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);

        assertTrue(rule.transactionLineSizeGreaterThanZero(document, true));
    }

    /**
     * Validates that transactionLineSizeGreaterThanZero returns false if the document has no source transaction lines.
     */
    public void testDocumentHasAtLeastOneTransactionLine_False() {

        assertFalse(rule.transactionLineSizeGreaterThanZero(document, true));
    }

}
