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

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService;
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
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * This class...
 */
@ConfigureContext(session = khuntley)
public class AssetIncreaseDocumentRulesTest extends KualiTestBase {

    private AssetIncreaseDocumentRules rule;
    private AssetIncreaseDocument document;
    private UpdateAssetIncreaseDocumentTaxLotsService assetIncreaseDocumentTaxLotsService;


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new AssetIncreaseDocumentRules();
        assetIncreaseDocumentTaxLotsService = SpringContext.getBean(UpdateAssetIncreaseDocumentTaxLotsService.class);
        document = createAssetIncreaseDocument();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        assetIncreaseDocumentTaxLotsService = null;
        super.tearDown();
    }

    /**
     * Creates an Asset Increase document
     *
     * @return an Asset Increase document
     * @throws WorkflowException
     */
    private AssetIncreaseDocument createAssetIncreaseDocument() throws WorkflowException {

        // create an asset increase document
        document = (AssetIncreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_ASSET_INCREASE.createEndowmentTransactionDocument(AssetIncreaseDocument.class);
        document.getDocumentHeader().setDocumentDescription("This is a test Asset Increase document.");

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

        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setSecurityID(security.getId());
        targetTransactionSecurity.setSecurity(security);

        document.setTargetTransactionSecurity(targetTransactionSecurity);

        assertTrue(rule.validateSecurityCode(document, false));
    }

    /**
     * Validates that validateSecurityCode returns false when an invalid security is set.
     */
    public void testValidateSecurity_False() {
        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setSecurityID(EndowTestConstants.INVALID_SECURITY_ID);

        document.setTargetTransactionSecurity(targetTransactionSecurity);

        assertFalse(rule.validateSecurityCode(document, false));
    }

    /**
     * Validates that validateRegistrationCode returns true when a valid registration code is set.
     */
    public void testValidateRegistration_True() {

        // add security details
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();

        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setRegistrationCode(registrationCode.getCode());
        targetTransactionSecurity.setRegistrationCodeObj(registrationCode);

        document.setTargetTransactionSecurity(targetTransactionSecurity);

        assertTrue(rule.validateRegistrationCode(document, false));

    }

    /**
     * Validates that validateRegistrationCode returns false when a valid registration code is set.
     */
    public void testValidateRegistration_False() {

        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setRegistrationCode(EndowTestConstants.INVALID_REGISTRATION_CODE);

        document.setTargetTransactionSecurity(targetTransactionSecurity);

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

        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setSecurityID(security.getId());
        targetTransactionSecurity.setSecurity(security);

        document.setTargetTransactionSecurity(targetTransactionSecurity);

        assertTrue(rule.isSecurityActive(document, false));
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
        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setSecurityID(security.getId());
        targetTransactionSecurity.setSecurity(security);

        document.setTargetTransactionSecurity(targetTransactionSecurity);

        assertFalse(rule.isSecurityActive(document, false));
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

        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setSecurityID(security.getId());
        targetTransactionSecurity.setSecurity(security);

        document.getTargetTransactionSecurities().add(targetTransactionSecurity);

        assertTrue(rule.validateSecurityClassCodeTypeNotLiability(document, false));
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

        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setSecurityID(security.getId());
        targetTransactionSecurity.setSecurity(security);

        document.getTargetTransactionSecurities().add(targetTransactionSecurity);

        assertFalse(rule.validateSecurityClassCodeTypeNotLiability(document, false));

    }

    // validate transaction lines

    /**
     * Validates that validateKemId returns true when the kemid on the transaction line exists in the db.
     */
    public void testValidateKemId_True() {

        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);

        assertTrue(rule.validateKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateKemId returns false when the kemid on the transaction line does not exist in the db.
     */
    public void testValidateKemId_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);
        endowmentTransactionLine.setKemid(EndowTestConstants.INVALID_KEMID);

        assertFalse(rule.validateKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that isActiveKemId returns true when the KEMID is open (closed indicator is false).
     */
    public void testIsActiveKemId_True() {
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);

        assertTrue(rule.isActiveKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that isActiveKemId returns false when the KEMID is closed (closed indicator is true).
     */
    public void testIsActiveKemId_False() {
        KEMID kemid = KemIdFixture.CLOSED_KEMID_RECORD.createKemidRecord();
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);

        assertFalse(rule.isActiveKemId(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    /**
     * Validates that validateNoTransactionRestriction returns false when the KEMID has a transaction restriction code equal to
     * NTRAN.
     */
    public void testTransactionsNotAllowedForNTRANKemid_False() {

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
    public void testTransactionsNotAllowedForNTRANKemid_True() {

        KEMID ntranKemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTargetTransactionLine.setKemid(ntranKemid.getKemid());
        endowmentTargetTransactionLine.setKemidObj(ntranKemid);

        assertTrue(rule.validateNoTransactionRestriction(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }


    /**
     * Validates that validateTransactionAmountGreaterThanZero returns true when transaction amount is greater than zero.
     */
    public void testTransactionsLineAmountPositive_True() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(false);

        assertTrue(rule.validateTransactionAmountGreaterThanZero(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    /**
     * Validates that validateTransactionAmountGreaterThanZero returns false when transaction amount is zero.
     */
    public void testTransactionsLineAmountPositive_False() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_ZERO_AMT.createEndowmentTransactionLine(false);

        assertFalse(rule.validateTransactionAmountGreaterThanZero(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    /**
     * Validates that validateTransactionUnitsGreaterThanZero returns true when transaction units is greater than zero.
     */
    public void testTransactionUnitsPositive_True() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_UNITS.createEndowmentTransactionLine(false);

        assertTrue(rule.validateTransactionUnitsGreaterThanZero(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }


    /**
     * Validates that validateTransactionUnitsGreaterThanZero returns false when transaction units is zero.
     */
    public void testTransactionUnitsPositive_False() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_ZERO_UNITS.createEndowmentTransactionLine(false);

        assertFalse(rule.validateTransactionUnitsGreaterThanZero(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    // The ETRAN code used in the Transaction Line must have an ETRAN Type code equal to I ( Income ) or E (Expense)
    /**
     * Validates that validateEndowmentTransactionTypeCode returns true when the etran code type is income.
     */
    public void testEtranCodeIncome_True() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(false);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        endowmentTargetTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentTargetTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertTrue(rule.validateEndowmentTransactionTypeCode(document, endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    /**
     * Validates that validateEndowmentTransactionTypeCode returns true when the etran code type is expense.
     */
    public void testEtranCodeExpense_True() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(false);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.EXPENSE_TRANSACTION_CODE.createEndowmentTransactionCode();
        endowmentTargetTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentTargetTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertTrue(rule.validateEndowmentTransactionTypeCode(document, endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    /**
     * Validates that validateEndowmentTransactionTypeCode returns false when the etran code type is not income or expense.
     */
    public void testEtranCodeIncomeOrExpense_False() {

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(false);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.ASSET_TRANSACTION_CODE.createEndowmentTransactionCode();
        endowmentTargetTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentTargetTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertFalse(rule.validateEndowmentTransactionTypeCode(document, endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    // The ETRAN Code used must have an appropriately identified general ledger object code record; one that matches the Chart for
    // the KEMID associated general ledger account.

    /**
     * Validates that validateChartMatch returns true when etran code gl chart matches the chart for KEMID general ledger account.
     */
    public void testKemidEtranCodeMatch_True() {
        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        GLLink glLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT.createKemidGeneralLedgerAccount();

        kemid.getKemidGeneralLedgerAccounts().add(generalLedgerAccount);
        endowmentTransactionCode.getGlLinks().add(glLink);
        endowmentTargetTransactionLine.setKemid(kemid.getKemid());
        endowmentTargetTransactionLine.setKemidObj(kemid);
        endowmentTargetTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentTargetTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertTrue(rule.validateChartMatch(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));
    }

    /**
     * Validates that validateChartMatch returns false when etran code gl chart does not match the chart for KEMID general ledger
     * account.
     */
    public void testKemidEtranCodeMatch_False() {
        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        GLLink glLink = GLLinkFixture.GL_LINK_UA_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT.createKemidGeneralLedgerAccount();

        kemid.getKemidGeneralLedgerAccounts().add(generalLedgerAccount);
        endowmentTransactionCode.getGlLinks().add(glLink);
        endowmentTargetTransactionLine.setKemid(kemid.getKemid());
        endowmentTargetTransactionLine.setKemidObj(kemid);
        endowmentTargetTransactionLine.setEtranCode(endowmentTransactionCode.getCode());
        endowmentTargetTransactionLine.setEtranCodeObj(endowmentTransactionCode);

        assertFalse(rule.validateChartMatch(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));
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
    public void testKemidPrincRestrNotNAWhenTransLinePrincipal_False() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL.createEndowmentTransactionLine(false);
        KEMID kemid = KemIdFixture.NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();
        endowmentTransactionLine.setKemid(kemid.getKemid());
        endowmentTransactionLine.setKemidObj(kemid);

        assertFalse(rule.canKEMIDHaveAPrincipalTransaction(endowmentTransactionLine, rule.getErrorPrefix(endowmentTransactionLine, -1)));
    }

    // validate document

    // There must be at least one transaction line in each transaction line section (To and/or From) that is required for the
    // document type to successfully submit the document.

    /**
     * Validates that transactionLineSizeGreaterThanZero returns true if the document has at least one target transaction line.
     */
    public void testDocumentHasAtLeastOneTransactionLine_True() {
        EndowmentTransactionLine endowmentTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);
        document.addTargetTransactionLine((EndowmentTargetTransactionLine) endowmentTransactionLine);

        assertTrue(rule.transactionLineSizeGreaterThanZero(document, false));
    }

    /**
     * Validates that transactionLineSizeGreaterThanZero returns false if the document has no target transaction lines.
     */
    public void testDocumentHasAtLeastOneTransactionLine_False() {

        assertFalse(rule.transactionLineSizeGreaterThanZero(document, false));
    }


    /**
     * Validates that the validateTaxLotsCostAndTransactionAmountLessOrEqualToSecurityCommitment returns true when the SECURITY_ID
     * has a class code type of A (Alternative Investments), and the total END_HLDG_TAX_LOT_T: HLDG_COST for the SECURITY_ID plus
     * the END_TRAN_LN_T: TRAN_AMT does not exceed the value in END_SEC_T: CMTMNT_AMT for the Security
     */
    public void testValidateTaxLotsCostAndTransactionAmountLessOrEqualToSecurityCommitment_True() {
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        // need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLot holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD.createHoldingTaxLotRecord();
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.ALTERNATIVE_INVESTMENT_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ALTERNATIVE_INVEST_ACTIVE_SECURITY.createSecurityRecord();
        security.setCommitmentAmount(new BigDecimal("500"));

        document.getTargetTransactionSecurity().setSecurityID(security.getId());
        document.getTargetTransactionSecurity().setSecurity(security);

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(false);
        document.addTargetTransactionLine((EndowmentTargetTransactionLine) endowmentTargetTransactionLine);

        assetIncreaseDocumentTaxLotsService.updateTransactionLineTaxLots(document, endowmentTargetTransactionLine);

        assertTrue(rule.validateTaxLotsCostAndTransactionAmountLessOrEqualToSecurityCommitment(document));

    }

    /**
     * Validates that the validateTaxLotsCostAndTransactionAmountLessOrEqualToSecurityCommitment returns false when the SECURITY_ID
     * has a class code type of A (Alternative Investments), and the total END_HLDG_TAX_LOT_T: HLDG_COST for the SECURITY_ID plus
     * the END_TRAN_LN_T: TRAN_AMT exceeds the value in END_SEC_T: CMTMNT_AMT for the Security
     */
    public void testValidateTaxLotsCostAndTransactionAmountLessOrEqualToSecurityCommitment_False() {
        KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        // need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLot holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD.createHoldingTaxLotRecord();
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.ALTERNATIVE_INVESTMENT_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ALTERNATIVE_INVEST_ACTIVE_SECURITY.createSecurityRecord();
        security.setCommitmentAmount(BigDecimal.ZERO);

        document.getTargetTransactionSecurity().setSecurityID(security.getId());
        document.getTargetTransactionSecurity().setSecurity(security);

        EndowmentTransactionLine endowmentTargetTransactionLine = EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT.createEndowmentTransactionLine(false);
        document.addTargetTransactionLine((EndowmentTargetTransactionLine) endowmentTargetTransactionLine);

        assetIncreaseDocumentTaxLotsService.updateTransactionLineTaxLots(document, endowmentTargetTransactionLine);

        assertFalse(rule.validateTaxLotsCostAndTransactionAmountLessOrEqualToSecurityCommitment(document));

    }


}
