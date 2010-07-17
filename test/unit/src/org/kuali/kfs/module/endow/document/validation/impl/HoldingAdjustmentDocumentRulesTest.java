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
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This class tests the rules in HoldingAdjustmentDocumentRule
 */
@ConfigureContext(session = khuntley)
public class HoldingAdjustmentDocumentRulesTest extends KualiTestBase {

    private HoldingAdjustmentDocumentRules rule;
    private HoldingAdjustmentDocument document;
    private DocumentService documentService;
    private UnitTestSqlDao unitTestSqlDao;
    
    private static final BigDecimal ZERO_AMOUNT = new BigDecimal(0);
    private static final BigDecimal NEGATIVE_AMOUNT = new BigDecimal(-1);
    private static final BigDecimal POSITIVE_AMOUNT = new BigDecimal(2);

    private static final String REFERENCE_DOCUMENT_NUMBER = "123456";
    private static final String REFERENCE_DOCUMENT_DESCRIPTION = "Document Description - Unit Test";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new HoldingAdjustmentDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);  
        
        //create the document
        document = createHoldingAdjustmentDocument();
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

        //insert into security class code END_SEC_CLS_CD_T table to validate security class code later...
        String sqlForSecurityClassCode = "INSERT INTO END_CLS_CD_T VALUES ('MUD','Test Class Code Description','LIAB','N','22025','74100','N','L','Y','BLAH BLAH BLAH',1,'U')";
        int rowsForSecurityClassCode = unitTestSqlDao.sqlCommand(sqlForSecurityClassCode);   
        
        //first add into END_SEC_T with securityid = 000000000 so this record would be unique.
        String sqlForSecurity = "INSERT INTO END_SEC_T VALUES ('000000000','','','MUD','','','','','','','','','','','','','','','','','','','','','','Y','TEST TEST TEST',1,'')";
        int rowsForSecurity = unitTestSqlDao.sqlCommand(sqlForSecurity);   
                
        HoldingAdjustmentDocument doc = (HoldingAdjustmentDocument) documentService.getNewDocument(HoldingAdjustmentDocument.class);
        doc.getDocumentHeader().setDocumentDescription("This is a test document.");
        
        doc.getDocumentHeader().setDocumentNumber(REFERENCE_DOCUMENT_NUMBER);
        doc.getDocumentHeader().setDocumentDescription(REFERENCE_DOCUMENT_DESCRIPTION);
        
        doc.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
        doc.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.MANUAL);

        return doc;
    }
    
    /**
     * Method to validate the rule canOnlyAddSourceOrTargetTransactionLines
     * Remove both source and target lines from the document and call the rule
     */
    public void testCanOnlyAddSourceOrTargetTransactionLines_True() {
        // remove any source or transaction lines from the document and test the rule.
        int index = 0;
        
        document.getTargetTransactionLines().clear();
        document.getSourceTransactionLines().clear();
        
        assertTrue(rule.canOnlyAddSourceOrTargetTransactionLines(document, null, index));
        
        // add a source transaction line and check the rule - the rule should pass.
        document.getTargetTransactionLines().clear();        
        List<EndowmentTransactionLine> sourceTransactionLines = new TypedArrayList(EndowmentSourceTransactionLine.class);
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setDocumentNumber(REFERENCE_DOCUMENT_NUMBER);
        sourceTransactionLines.add(index, endowmentSourceTransactionLine);
        
        assertTrue(rule.canOnlyAddSourceOrTargetTransactionLines(document, document.getSourceTransactionLine(index), index));
        
        //add a target transaction line and check the rule - should pass.
        document.getSourceTransactionLines().clear();
        List<EndowmentTransactionLine> targetTransactionLines = new TypedArrayList(EndowmentTargetTransactionLine.class);
        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTargetTransactionLine.setDocumentNumber(REFERENCE_DOCUMENT_NUMBER);
        targetTransactionLines.add(index, endowmentTargetTransactionLine);

        assertTrue(rule.canOnlyAddSourceOrTargetTransactionLines(document, document.getTargetTransactionLine(index), index));        
    }
}

