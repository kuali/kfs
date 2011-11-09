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
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurityBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.CorporateReorganizationDocument;
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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the rules in CorporateReorganizationDocumentRules class
 */
@ConfigureContext(session = khuntley)
public class CorporateReorganizationDocumentRulesTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(CorporateReorganizationDocumentRulesTest.class);

    private CorporateReorganizationDocumentRules rule;
    private CorporateReorganizationDocument document;
    private DocumentService documentService;
    private Security sourceSecurity;
    private Security targetSecurity;    
    private ClassCode classCode;
    private KEMID kemid;
    private SecurityReportingGroup reportingGroup;
    private EndowmentTransactionCode endowmentTransactionCode;
    private HoldingTaxLot holdingTaxLot;
    private RegistrationCode registrationCode;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        rule = new CorporateReorganizationDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        
        reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        classCode = ClassCodeFixture.LIABILITY_INCREASE_LIABILITY_CLASS_CODE_2.createClassCodeRecord();
        sourceSecurity = SecurityFixture.CORPORATE_REORGANIZATION_SOURCE_SECURITY.createSecurityRecord();
        targetSecurity = SecurityFixture.CORPORATE_REORGANIZATION_TARGET_SECURITY.createSecurityRecord();
        kemid = KemIdFixture.NOT_NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();

        registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();
        //need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_CORPORATE_REORGANIZATION.createHoldingTaxLotRebalanceRecord();
        //setup records in END_HLDG_TAX_LOT_T to get the totals by Income or Principal indicators.
        holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_CORPORATE_REORGANIZATION.createHoldingTaxLotRecord();
        
        //create the Corporate Reorganization document  
        document = createCorporateReorganizationDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        documentService = null;
        
        super.tearDown();
    }

    /**
     * create a CorporateReorganizationDocument
     * @return doc
     */
    protected CorporateReorganizationDocument createCorporateReorganizationDocument() throws WorkflowException {
        LOG.info("createCorpusAdjustmentDocument() entered.");
        
        CorporateReorganizationDocument doc = (CorporateReorganizationDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentTransactionDocument(CorporateReorganizationDocument.class);
        doc.getDocumentHeader().setDocumentDescription("Testing Corporate Reorganization Document.");
        doc.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        doc.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.MANUAL);

        EndowmentTransactionSecurityBase etsb1 = (EndowmentSourceTransactionSecurity) EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SOURCE_SECURITY.createEndowmentTransactionSecurity(true);
        etsb1.setDocumentNumber(doc.getDocumentNumber());
        etsb1.setRegistrationCode(registrationCode.getCode());
        etsb1.refreshNonUpdateableReferences();
        
        doc.setSourceTransactionSecurity(etsb1);
         
        EndowmentTransactionSecurityBase etsb2 = (EndowmentTargetTransactionSecurity) EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_TARGET_SECURITY.createEndowmentTransactionSecurity(false);        
        etsb2.setDocumentNumber(doc.getDocumentNumber());
        etsb2.setRegistrationCode(registrationCode.getCode());
        etsb2.refreshNonUpdateableReferences();
        
        doc.setTargetTransactionSecurity(etsb2);
        
        return doc;
    }

    /**
     * helper method to add one source line to the document and then create an another 
     * source line for testing.
     */
    private EndowmentTransactionLineBase createSourceTransactionLinesForTesting() {
        //create a Increase (From) transaction line
        EndowmentTransactionLineBase sourceLine1 = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        sourceLine1.setDocumentNumber(document.getDocumentNumber());
        document.getSourceTransactionLines().add(sourceLine1);

        //create an extra source line and validate it if can be added.  Should fail.
        EndowmentTransactionLineBase sourceLine2 = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        sourceLine2.setDocumentNumber(document.getDocumentNumber());

        return sourceLine2;
    }
    
    /**
     * test to validate method validateOnlyOneSourceTransactionLine() in the rule class
     * @see org.kuali.kfs.module.endow.document.validation.impl.CorporateReorganizationDocumentRules#validateOnlyOneSourceTransactionLine
     * (boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    public void testValidateOnlyOneSourceTransactionLine_AddFalse() {
        LOG.info("testValidateOnlyOneSourceTransactionLine_AddFalse() entered.");
        
        EndowmentTransactionLineBase sourceLine2 = createSourceTransactionLinesForTesting();
        assertFalse(rule.validateOnlyOneSourceTransactionLine(true, document, sourceLine2, 1));
        
        LOG.info("testValidateOnlyOneSourceTransactionLine_AddFalse() exited.");
    }
    
    /**
     * test to validate method validateOnlyOneSourceTransactionLine() in the rule class
     * @see org.kuali.kfs.module.endow.document.validation.impl.CorporateReorganizationDocumentRules#validateOnlyOneSourceTransactionLine
     * (boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    public void testValidateOnlyOneSourceTransactionLine_AddTrue() {
        LOG.info("testValidateOnlyOneSourceTransactionLine_AddTrue() entered.");
        
        EndowmentTransactionLineBase sourceLine1 = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        sourceLine1.setDocumentNumber(document.getDocumentNumber());

        assertTrue(rule.validateOnlyOneSourceTransactionLine(true, document, sourceLine1, 1));
        
        LOG.info("testValidateOnlyOneSourceTransactionLine_AddTrue() exited.");
    }

    /**
     * test to validate method validateOnlyOneSourceTransactionLine() in the rule class
     * @see org.kuali.kfs.module.endow.document.validation.impl.CorporateReorganizationDocumentRules#validateOnlyOneSourceTransactionLine
     * (boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    public void testValidateOnlyOneSourceTransactionLine_SaveTrue() {
        LOG.info("testValidateOnlyOneSourceTransactionLine_SaveFalse() entered.");
        
        EndowmentTransactionLineBase sourceLine2 = createSourceTransactionLinesForTesting();
        assertTrue(rule.validateOnlyOneSourceTransactionLine(false, document, sourceLine2, 1));
        
        LOG.info("testValidateOnlyOneSourceTransactionLine_SaveFalse() exited.");
    }
    
    /**
     * test to validate method validateOnlyOneSourceTransactionLine() in the rule class
     * @see org.kuali.kfs.module.endow.document.validation.impl.CorporateReorganizationDocumentRules#validateOnlyOneSourceTransactionLine
     * (boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    public void testValidateOnlyOneSourceTransactionLine_SaveFalse() {
        LOG.info("testValidateOnlyOneSourceTransactionLine_SaveTrue() entered.");
        
        //create a Increase (From) transaction line
        EndowmentTransactionLineBase sourceLine1 = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        sourceLine1.setDocumentNumber(document.getDocumentNumber());
        assertFalse(rule.validateOnlyOneSourceTransactionLine(false, document, sourceLine1, 0));
        
        LOG.info("testValidateOnlyOneSourceTransactionLine_SaveTrue() exited.");
    }
    
    /**
     * test to validate method validateCorpReorganizationTransferTransactionLine() in the rule class
     * @see org.kuali.kfs.module.endow.document.validation.impl.CorporateReorganizationDocumentRules#validateCorpReorganizationTransferTransactionLine
     * (boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int, int)
     */
    public void testValidateCorpReorganizationTransferTransactionLine_UnitZeroFalse() {
        LOG.info("testValidateCorpReorganizationTransferTransactionLine_UnitZeroFalse() entered.");

        EndowmentTransactionLineBase sourceLine1 = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        sourceLine1.setDocumentNumber(document.getDocumentNumber());
        sourceLine1.setTransactionUnits(KualiDecimal.ZERO);
        document.getSourceTransactionLines().add(sourceLine1);
        
        assertFalse("Should have failed since transaction units are zero.", rule.validateCorpReorganizationTransferTransactionLine(true, document, sourceLine1, 0, 0));
        LOG.info("testValidateCorpReorganizationTransferTransactionLine_UnitZeroFalse() exited.");
    }
    
    /**
     * test to validate method validateSufficientUnits() in the rule class
     * @see org.kuali.kfs.module.endow.document.validation.impl.CorporateReorganizationDocumentRules#validateCorpReorganizationTransferTransactionLine
     * (boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int, int)
     */
    public void testValidateCorpReorganizationTransferTransactionLine_SufficientUnitsFalse() {
        LOG.info("testValidateCorpReorganizationTransferTransactionLine_SufficientUnitsFalse() entered.");

        EndowmentTransactionLineBase sourceLine1 = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        sourceLine1.setDocumentNumber(document.getDocumentNumber());
        sourceLine1.setTransactionUnits(new KualiDecimal("100"));
        
        document.getSourceTransactionLines().add(sourceLine1);
        
        assertFalse("Should have failed since transaction units are more than holding tax lot units.", rule.validateCorpReorganizationTransferTransactionLine(true, document, sourceLine1, 0, 0));
        LOG.info("testValidateCorpReorganizationTransferTransactionLine_SufficientUnitsFalse() exited.");
    }
    
    /**
     * test to add a transaction line..
     */
    public void testProcessAddTransactionLineRules() {
        LOG.info("testProcessAddTransactionLineRules() entered.");
        
        //create a Increase (From) transaction line
        EndowmentTransactionLineBase sourceLine1 = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        sourceLine1.setDocumentNumber(document.getDocumentNumber());
        assertTrue(rule.processAddTransactionLineRules(document, sourceLine1));

        LOG.info("testProcessAddTransactionLineRules() exited.");        
    }
    
    /**
     * Test to check processCustomRouteDocumentBusinessRules in the rule class...
     */
    public void testProcessCustomRouteDocumentBusinessRules() {
        LOG.info("testProcessCustomRouteDocumentBusinessRules() entered.");

        EndowmentTransactionLineBase sourceLine1 = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(true);
        sourceLine1.setDocumentNumber(document.getDocumentNumber());
        EndowmentTransactionTaxLotLine taxLotLine1 = EndowmentTransactionTaxLotLineFixture.TAX_LOT_SOURCE_RECORD1.createEndowmentTransactionTaxLotLineRecord();
        taxLotLine1.setDocumentNumber(document.getDocumentNumber());
        taxLotLine1.setTransactionHoldingLotNumber(1);
        sourceLine1.getTaxLotLines().add(taxLotLine1);
        document.getSourceTransactionLines().add(sourceLine1);
        
        EndowmentTransactionLineBase sourceLine2 = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_INCOME.createEndowmentTransactionLine(false);
        sourceLine2.setDocumentNumber(document.getDocumentNumber());
        EndowmentTransactionTaxLotLine taxLotLine2 = EndowmentTransactionTaxLotLineFixture.TAX_LOT_TARGET_RECORD1.createEndowmentTransactionTaxLotLineRecord();
        taxLotLine2.setDocumentNumber(document.getDocumentNumber());
        sourceLine2.getTaxLotLines().add(taxLotLine2);
        taxLotLine2.setTransactionHoldingLotNumber(1);
        document.getTargetTransactionLines().add(sourceLine2);        
        
        assertTrue(rule.processCustomRouteDocumentBusinessRules(document));
    }
}