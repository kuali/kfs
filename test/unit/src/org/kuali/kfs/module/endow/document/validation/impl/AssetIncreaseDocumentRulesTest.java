/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;

@ConfigureContext(session = khuntley)
public class AssetIncreaseDocumentRulesTest extends KualiTestBase {

    private AssetIncreaseDocumentRules rule;
    private AssetIncreaseDocument document;
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private UpdateAssetIncreaseDocumentTaxLotsService assetIncreaseDocumentTaxLotsService;


    // 
    private static final String INCOME_PRINCIPAL_IND = "I";
    private static final String ETRAN_CODE = "42000";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new AssetIncreaseDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        assetIncreaseDocumentTaxLotsService = SpringContext.getBean(UpdateAssetIncreaseDocumentTaxLotsService.class);
        document = createAssetIncreaseDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        documentService = null;
        businessObjectService = null;
        assetIncreaseDocumentTaxLotsService = null;
        super.tearDown();
    }

    private AssetIncreaseDocument createAssetIncreaseDocument() throws WorkflowException {

        // create an asset increase document
        document = (AssetIncreaseDocument) documentService.getNewDocument(AssetIncreaseDocument.class);
        document.getDocumentHeader().setDocumentDescription("This is a test document.");

        // // add security details
        // Security security = SecurityFixture.ENDOWMENT_SECURITY_RECORD.createSecurityRecord();
        // EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        // sourceTransactionSecurity.setSecurityID(security.getId());
        //
        // RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();
        // sourceTransactionSecurity.setRegistrationCode(registrationCode.getCode());
        //
        // document.setSourceTransactionSecurity(sourceTransactionSecurity);
        //
        // // add transaction lines
        // KEMID kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        // EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        // endowmentTargetTransactionLine.setDocumentNumber(document.getDocumentNumber());
        // endowmentTargetTransactionLine.setKemid(kemid.getKemid());
        // endowmentTargetTransactionLine.setTransactionIPIndicatorCode(INCOME_PRINCIPAL_IND);
        // endowmentTargetTransactionLine.setTransactionAmount(new KualiDecimal(100));
        // endowmentTargetTransactionLine.setEtranCode(ETRAN_CODE);
        //
        // document.addTargetTransactionLine(endowmentTargetTransactionLine);
        //
        // assetIncreaseDocumentTaxLotsService.updateTransactionLineTaxLots(document, endowmentTargetTransactionLine);

        // TODO do I have to save asset increase doc?
        documentService.saveDocument(document);

        return document;
    }

    // validate security details

    /**
     * 4. A valid security must be selected or entered in the Security tab. (END_TRAN_SEC_T: SEC_ID must exist in END_SEC_T) and
     * must be active. Only one security record can be inserted in each transaction eDoc.
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
     * This method...
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
     * 5. The security entered must NOT have a CLS_CD_TYP equal to L (Liabilities)
     */
    public void testLiabilityClassCode_True() {
        // add security details

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();

        security.setClassCode(classCode);
        security.setSecurityClassCode(classCode.getCode());

        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        targetTransactionSecurity.setSecurityID(security.getId());

        document.getTargetTransactionSecurities().add(targetTransactionSecurity);

        assertTrue(rule.validateSecurityClassCodeTypeNotLiability(document, false));
    }

    /**
     * This method...
     */
    public void testLiabilityClassCode_False() {
        // add security details
//        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
//        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
//        ClassCode classCode = ClassCodeFixture.NOT_LIABILITY_CLASS_CODE.createClassCodeRecord();
//        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
//
//
//        security.setClassCode(classCode);
//        security.setSecurityClassCode(classCode.getCode());
//
//        EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
//        targetTransactionSecurity.setSecurityID(security.getId());
//
//        document.getTargetTransactionSecurities().add(targetTransactionSecurity);
//
//        assertFalse(rule.validateSecurityClassCodeTypeNotLiability(document, false));
    }

    // validate transaction lines
    // If the END_KEMID_T: TRAN_RESTR_CD is NTRAN, no transactions are allowed for the KEMID.

    /**
     * If the END_KEMID_T: TRAN_RESTR_CD is NTRAN, no transactions are allowed for the KEMID.
     */
    public void testTransactionsNotAllowedForNTRANKemid_False() {

//        KEMID ntranKemid = KemIdFixture.NO_TRAN_KEMID_RECORD.createKemidRecord();
//        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
//        endowmentTargetTransactionLine.setKemid(ntranKemid.getKemid());
//        endowmentTargetTransactionLine.setKemidObj(ntranKemid);
//
//        assertFalse(rule.validateNoTransactionRestriction(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    /**
     * This method...
     */
    public void testTransactionsNotAllowedForNTRANKemid_True() {

//        KEMID ntranKemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
//        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
//        endowmentTargetTransactionLine.setKemid(ntranKemid.getKemid());
//        endowmentTargetTransactionLine.setKemidObj(ntranKemid);
//
//        assertTrue(rule.validateNoTransactionRestriction(endowmentTargetTransactionLine, rule.getErrorPrefix(endowmentTargetTransactionLine, -1)));

    }

    // All values entered must be positive values.

    // The ETRAN code used in the Transaction Line must have an ETRAN Type code equal to I ( Income ) or E (Expense)

    // The ETRAN Code used must have an appropriately identified general ledger object code record; one that matches the Chart for
    // the KEMID associated general ledger account.
    // -If the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to I, the chart must match the chart of the active END_KEMID_GL_LNK_T record
    // where the IP_IND_CD is equal to I.
    // - If the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P, the chart must match the chart of the active END_KEMID_GL_LNK_T record
    // where the IP_IND_CD is equal to P.

    // If the END_KEMID_T: PRIN_RESTR_CD has the END_TYP_RESTR_CD_T: PERM equal to Yes, AND if the ETRAN code record has the
    // END_ETRAN_CD_T: CORPUS_IND set to Yes, then transaction lines where the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P will
    // affect the corpus value for the KEMID. Upon adding the transaction line, the END_TRAN_LN_T: CORPUS_IND for the transaction
    // line will be set to Yes.

    // IF the END _TRAN_LN_T: TRAN_IP_IND_CD for the transaction line is equal to P, then the KEMID must have a principal
    // restriction (END_KEMID_T: TYP_PRIN_RESTR_CD) that is not equal to NA which implies that the KEMID cannot have any activity in
    // Principal. This would guarantee that the KEMID has an active general ledger account with the Income/Principal indicator equal
    // to “P”.

    // The initiator must enter a number greater than zero for the security units in the transaction line.

    // The Etran Codes for the Security must have an appropriately identified general ledger object code record; one that matches
    // the Chart for the KEMID associated general ledger account in the transaction line.
    // -If the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to I, the chart must match the chart of the active END_KEMID_GL_LNK_T record
    // where the IP_IND_CD is equal to I.
    // - If the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P, the chart must match the chart of the active END_KEMID_GL_LNK_T record
    // where the IP_IND_CD is equal to P.


    // validate document

    // There must be at least one transaction line in each transaction line section (To and/or From) that is required for the
    // document type to successfully submit the document.

    // If the SECURITY_ID has a class code type of A (Alternative Investments), the system must validate that the total
    // END_HLDG_TAX_LOT_T: HLDG_COST for the SECURITY_ID plus the END_TRAN_LN_T: TRAN_AMT does not exceed the value in END_SEC_T:
    // CMTMNT_AMT for the Security. If it does, the transaction should not be allowed.
}
