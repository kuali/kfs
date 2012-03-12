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
import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.CorpusAdjustmentDocument;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the rules in CorpusAdjustmentDocumentRules class
 */
@ConfigureContext(session = khuntley)
public class CorpusAdjustmentDocumentRulesTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(CorpusAdjustmentDocumentRulesTest.class);

    private CorpusAdjustmentDocumentRules rule;
    private CorpusAdjustmentDocument document;
    private DocumentService documentService;
    private Security security;
    private ClassCode classCode;
    private KEMID kemid;
    private SecurityReportingGroup reportingGroup;
    private EndowmentTransactionCode endowmentTransactionCode;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new CorpusAdjustmentDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        classCode = ClassCodeFixture.LIABILITY_INCREASE_LIABILITY_CLASS_CODE_2.createClassCodeRecord();
        security = SecurityFixture.LIABILITY_INCREASE_ACTIVE_SECURITY.createSecurityRecord();
        kemid = KemIdFixture.NOT_NA_PRINC_RESTR_KEMID_RECORD.createKemidRecord();

        //create the Corpus Adjustment Document
        document = createCorpusAdjustmentDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        documentService = null;
        
        super.tearDown();
    }

    /**
     * create a CorpusAdjustmentDocument
     * @return doc
     */
    protected CorpusAdjustmentDocument createCorpusAdjustmentDocument() throws WorkflowException {
        LOG.info("createCorpusAdjustmentDocument() entered.");
        
        CorpusAdjustmentDocument doc = (CorpusAdjustmentDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentTransactionDocument(CorpusAdjustmentDocument.class);
        doc.getDocumentHeader().setDocumentDescription("Testing Corpus Adjustment document.");
        doc.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        doc.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.MANUAL);
        return doc;
    }

    /**
     * test to validate the method validateKemId() in rule class
     */
    public void testValidateKemId() {
        LOG.info("testValidateKemId() entered.");
        
        //create a Increase (From) transaction line
        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        String kemId = line.getKemid();
        line.setDocumentNumber(document.getDocumentNumber());
        line.setKemid(EndowTestConstants.INVALID_KEMID);
        assertFalse(rule.validateKemId(line, null));
        
        line.setKemid(kemId);
        assertTrue(rule.validateKemId(line, null));
    }
    
    /**
     * test to validate isActiveKemId() method in the rule classs
     */
    public void testIsActiveKemId() {
        LOG.info("testIsActiveKemId() entered.");
        
        //create a Increase (From) transaction line
        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        line.setDocumentNumber(document.getDocumentNumber());

        line.getKemidObj().setClose(true);
        assertFalse(rule.isActiveKemId(line, null));
        
        line.getKemidObj().setClose(false);
        assertTrue(rule.isActiveKemId(line, null));
    }
    
    /**
     * test to validate the method validateNoTransactionRestriction() in the rule class
     */
    public void testValidateNoTransactionRestriction() {
        LOG.info("testValidateNoTransactionRestriction() entered.");
        
        //create a Increase (From) transaction line
        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        line.setDocumentNumber(document.getDocumentNumber());
        
        String transactionCode = line.getKemidObj().getTransactionRestrictionCode();
        line.getKemidObj().setTransactionRestrictionCode(EndowTestConstants.INVALID_TRANSACTION_CODE);
        line.getKemidObj().refreshNonUpdateableReferences();
        assertFalse(rule.validateNoTransactionRestriction(line, null));
        
        line.getKemidObj().setTransactionRestrictionCode(transactionCode);
        line.getKemidObj().refreshNonUpdateableReferences();
        assertTrue(rule.validateNoTransactionRestriction(line, null));
    }
    
    /**
     * test to validate method isSubTypeEmpty() in the rule class
     */
    public void testIsSubTypeEmpty() {
        LOG.info("testIsSubTypeEmpty() entered.");
        
        document.setTransactionSubTypeCode(null);
        assertFalse(rule.isSubTypeEmpty(document));
        
        document.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        assertTrue(rule.isSubTypeEmpty(document));
    }
    
    /**
     * test to validate method nonCashTransaction() in the rule class
     */
    public void testNonCashTransaction() {
        LOG.info("testNonCashTransaction() entered.");
        
        document.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
        assertFalse(rule.nonCashTransaction(document));
        
        document.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        assertTrue(rule.nonCashTransaction(document));

    }
    
    /**
     * test to validate method canKEMIDHaveAPrincipalTransaction() in the rule class
     */
    public void testCanKEMIDHaveAPrincipalTransaction() {
        LOG.info("testCanKEMIDHaveAPrincipalTransaction() entered.");
        
        String principalRestrictionCode = kemid.getPrincipalRestrictionCode();
        
        //create a Increase (From) transaction line
        EndowmentTransactionLineBase sourceLine = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        sourceLine.setDocumentNumber(document.getDocumentNumber());
        assertTrue(rule.canKEMIDHaveAPrincipalTransaction(sourceLine, null));
        
        //create a Decrease (To) transaction line
        kemid.setPrincipalRestrictionCode(EndowTestConstants.NOT_APPLICABLE_TYPE_RESTRICTION_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(kemid);
        kemid.refreshNonUpdateableReferences();
        
        EndowmentTransactionLineBase targetLine = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        targetLine.setDocumentNumber(document.getDocumentNumber());
        assertFalse(rule.canKEMIDHaveAPrincipalTransaction(targetLine, null));
        
        kemid.setPrincipalRestrictionCode(principalRestrictionCode);
        SpringContext.getBean(BusinessObjectService.class).save(kemid);
        kemid.refreshNonUpdateableReferences();
    }
    
    /**
     * test to add a transaction line..
     */
    public void testProcessAddTransactionLineRules() {
        LOG.info("testProcessAddTransactionLineRules() entered.");
        
        EndowmentTransactionLineBase line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        line.setDocumentNumber(document.getDocumentNumber());
        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);

        assertTrue(rule.processAddTransactionLineRules(document, line));
    }
    
    /**
     * Test to check processCustomRouteDocumentBusinessRules in the rule class...
     */
    public void testProcessCustomRouteDocumentBusinessRules() {
        LOG.info("testProcessCustomRouteDocumentBusinessRules() entered.");

        EndowmentTransactionLineBase line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(true);
        line.setDocumentNumber(document.getDocumentNumber());
        line.setTransactionAmount(EndowTestConstants.POSITIVE_AMOUNT);

        assertTrue(rule.processAddTransactionLineRules(document, line));
        document.getSourceTransactionLines().add(line);
        
        assertTrue(rule.processCustomRouteDocumentBusinessRules(document));
    }
}