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
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
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
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLines);
        
        assertTrue(rule.canOnlyAddSourceOrTargetTransactionLines(document, document.getSourceTransactionLines().get(index), index));
        
        //add a target transaction line and check the rule - should pass.
        document.getSourceTransactionLines().clear();
        List<EndowmentTransactionLine> targetTransactionLines = createTargetTransactionLine(index);
        document.setTargetTransactionLines(targetTransactionLines);
        
        assertTrue(rule.canOnlyAddSourceOrTargetTransactionLines(document, document.getTargetTransactionLines().get(index), index));        
    }

    /**
     * Helper Method to create a source transaction line
     */
    private List<EndowmentTransactionLine> createSourceTransactionLine(int index) {
        List<EndowmentTransactionLine> sourceTransactionLines = new TypedArrayList(EndowmentSourceTransactionLine.class);
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setDocumentNumber(REFERENCE_DOCUMENT_NUMBER);
        sourceTransactionLines.add(index, endowmentSourceTransactionLine);
        
        return sourceTransactionLines;
    }
    
    /**
     * Helper Method to create a source transaction line
     */
    private List<EndowmentTransactionLine> createTargetTransactionLine(int index) {
        List<EndowmentTransactionLine> targetTransactionLines = new TypedArrayList(EndowmentTargetTransactionLine.class);
        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTargetTransactionLine.setDocumentNumber(REFERENCE_DOCUMENT_NUMBER);
        targetTransactionLines.add(index, endowmentTargetTransactionLine);
        
        return targetTransactionLines;
    }
    
    /**
     * Method to validate the rule canOnlyAddSourceOrTargetTransactionLines
     * Entered both source and target lines to the document and call the rule - should fail...
     */
    public void testCanOnlyAddSourceOrTargetTransactionLines_False() {
        int index = 0;
        
        document.getSourceTransactionLines().clear();        
        document.getTargetTransactionLines().clear();
        
        // add a source transaction line and check the rule - the rule should pass.
        List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
        document.setSourceTransactionLines(sourceTransactionLines);
        
        //add a target transaction line and check the rule - should pass.
        List<EndowmentTransactionLine> targetTransactionLines = createTargetTransactionLine(index);
        document.setTargetTransactionLines(targetTransactionLines);
        
        assertFalse(rule.canOnlyAddSourceOrTargetTransactionLines(document, document.getSourceTransactionLines().get(index), index));        
    }
    
    /**
     * Method to test validateKemidHasTaxLots rule
     */
    public void testValidateKemidHasTaxLots_False() {
        int index = 0 ;
        
        setupDataToTestValidateKemidHasTaxLots(index);        
        document.getSourceTransactionSecurity().setRegistrationCode("..."); //search should fail...
        
        assertFalse(rule.validateKemidHasTaxLots(document, document.getSourceTransactionLines().get(index), index));
        
    }

    /**
     * Method to test validateKemidHasTaxLots rule
     */
    public void testValidateKemidHasTaxLots_True() {
        int index = 0 ;
        
        setupDataToTestValidateKemidHasTaxLots(index);        
        document.getSourceTransactionSecurity().setRegistrationCode(REGISTRATION_CODE); //search should succeed...
        
        assertTrue(rule.validateKemidHasTaxLots(document, document.getSourceTransactionLines().get(index), index));
    }
    
    /**
     * Helper method to setup the data for testing ValidateKemidHasTaxLots rule.
     */
    private void setupDataToTestValidateKemidHasTaxLots(int index) {
        //      String sqlForKemidDelete = "DELETE FROM END_KEMID_T WHERE KEMID = '0000000000'";
        //      int rowsForKemIdDelete = unitTestSqlDao.sqlCommand(sqlForKemidDelete);
              
        //      String sqlForKemId = "insert into END_KEMID_T columns (KEMID, SHRT_TTL, LONG_TTL, OPND_DT, ESTBL_DT, TYP_CD, PRPS_CD, INC_CAE_CD, PRIN_CAE_CD, RESP_ADMIN_CD, TRAN_RESTR_CD, CSH_SWEEP_MDL_ID, INC_ACI_MDL_ID, PRIN_ACI_MDL_ID, DORMANT_IND, CLOSED_IND, CLOSED_TO_KEMID, CLOSE_CD, FND_DISP, CLOSE_DT, OBJ_ID, TYP_INC_RESTR_CD, TYP_PRIN_RESTR_CD) values ('0000000000', 'Gift Annuity Trust 1', 'Gift Annuity Trust 1', TO_DATE('11/1/2005', 'mm/dd/yyyy'), TO_DATE('3/19/1999', 'mm/dd/yyyy'), '046', 'P', '9', 'L', 'TRST', 'NONE', '1', null, '1', 'N', 'N', null, null, null,TO_DATE(null, 'mm/dd/yyyy'),sys_guid(), 'TRU', 'TRU')";
        //      int rowsForKemId = unitTestSqlDao.sqlCommand(sqlForKemId);
              
          //    String sqlForClassCodeDelete = "DELETE FROM  END_CLS_CD_T WHERE SEC_CLS_CD = '300'";
          //    int rowsForClassCodeDelete = unitTestSqlDao.sqlCommand(sqlForClassCodeDelete);
              
         //     String sqlForSecurityCodeDelete = "DELETE FROM  END_SEC_T WHERE SEC_ID = '000000000'";
         //     int rowsForSecurityCodeDelete = unitTestSqlDao.sqlCommand(sqlForSecurityCodeDelete);
              
        //      String sqlForSecurity = "insert into END_SEC_T columns (SEC_ID, SEC_DESC, SEC_TKR_SYMB, SEC_CLS_CD, SEC_SUBCLS_CD, SEC_UNIT_VAL, SEC_UNITS_HELD, SEC_ISS_DT, SEC_MAT_DT, SEC_VAL_DT, SEC_RT, UNIT_VAL_SRC, PREV_UNIT_VAL, PREV_UNIT_VAL_DT, SEC_CVAL, LAST_TRAN_DT, SEC_INC_PAY_FREQ, SEC_INC_NEXT_PAY_DT, SEC_INC_CHG_DT, SEC_DVDND_REC_DT, SEC_EX_DVDND_DT, SEC_DVDND_PAY_DT, SEC_DVDND_AMT, CMTMNT_AMT, FRGN_WITH_PCT, NXT_FSCL_YR_DSTRB_AMT, ROW_ACTV_IND, OBJ_ID) values ('000000000', 'Bond Trust Investment Pool', '', '300', null, '9.9269', '0', TO_DATE('', 'mm/dd/yyyy'), TO_DATE('', 'mm/dd/yyyy'), TO_DATE('10/31/2009', 'mm/dd/yyyy'), '0.2448', 'PFVD', '9.9406', TO_DATE('09/30/2009', 'mm/dd/yyyy'), '0.00', TO_DATE('10/11/2005', 'mm/dd/yyyy'), '', TO_DATE('', 'mm/dd/yyyy'), TO_DATE('11/4/2009', 'mm/dd/yyyy'), TO_DATE('', 'mm/dd/yyyy'), TO_DATE('', 'mm/dd/yyyy'), TO_DATE('', 'mm/dd/yyyy'), '0', null, '0', '0', 'Y',sys_guid())";
        //      int rowsForSecurity = unitTestSqlDao.sqlCommand(sqlForSecurity);   
              
              String sqlForTaxLotsDelete = "DELETE FROM END_HLDG_TAX_LOT_T WHERE KEMID = '046G007720' AND SEC_ID = '99BTIP011' AND REGIS_CD = '0CP' AND  HLDG_IP_IND = 'P'";
              int rowsForTaxLots = unitTestSqlDao.sqlCommand(sqlForTaxLotsDelete);
              
              String sqlForHoldingTaxLots = "insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, HLDG_FRGN_TAX_WITH, LAST_TRAN_DT, OBJ_ID) values ('046G007720', '99BTIP011', '0CP', '1', 'P', TO_DATE('6/26/2009', 'mm/dd/yyyy'), '1000', '20000', '0', '0', '0', TO_DATE('6/26/2009', 'mm/dd/yyyy'),sys_guid())";
              rowsForTaxLots = unitTestSqlDao.sqlCommand(sqlForHoldingTaxLots);

              sqlForHoldingTaxLots = "insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, HLDG_FRGN_TAX_WITH, LAST_TRAN_DT, OBJ_ID) values ('046G007720', '99BTIP011', '0CP', '2', 'P', TO_DATE('8/27/2009', 'mm/dd/yyyy'), '200', '3500', '0', '0', '0', TO_DATE('8/27/2009', 'mm/dd/yyyy'),sys_guid())";
              rowsForTaxLots = unitTestSqlDao.sqlCommand(sqlForHoldingTaxLots);
                    
              sqlForHoldingTaxLots = "insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, HLDG_FRGN_TAX_WITH, LAST_TRAN_DT, OBJ_ID) values ('046G007720', '99BTIP011', '0CP', '3', 'P', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '300', '6400', '0', '0', '0', TO_DATE('9/30/2008', 'mm/dd/yyyy'),sys_guid())";
              rowsForTaxLots = unitTestSqlDao.sqlCommand(sqlForHoldingTaxLots);

              List<EndowmentTransactionLine> sourceTransactionLines = createSourceTransactionLine(index);
              document.setSourceTransactionLines(sourceTransactionLines);
              document.getSourceTransactionLines().get(index).setKemid(KEM_ID);
              document.getSourceTransactionLines().get(index).setTransactionIPIndicatorCode(IP_INDICATOR);
              
              document.getSourceTransactionSecurity().setSecurityID(SECURITY_ID);
              //      HoldingTaxLotService holdingTaxLotService = SpringContext.getBean(HoldingTaxLotService.class);
              //      List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getAllTaxLotsWithPositiveUnits(KEM_ID, SECURITY_ID, "0CP", "P");
    }
    
    /**
     * Test to check the rule processDeleteTaxLotLineRules
     */
    public void checkProcessDeleteTaxLotLineRules_False() {

        int index = 0 ;
        Number transLineIndex = 0;
        Number taxLotLineIndex = 0;
        
        setupDataToTestValidateKemidHasTaxLots(index);        
        document.getSourceTransactionSecurity().setRegistrationCode(REGISTRATION_CODE); //search should succeed...

        EndowmentTaxLotLinesDocument endowmentTaxLotLinesDocument = null;
        EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine = null;
        EndowmentTransactionLine endowmentTransactionLine = document.getSourceTransactionLines().get(index);
        endowmentTransactionLine.getTaxLotLines().clear();
        
        assertFalse(rule.processDeleteTaxLotLineRules(endowmentTaxLotLinesDocument, endowmentTransactionTaxLotLine, endowmentTransactionLine, transLineIndex, taxLotLineIndex));
    }
    
    /**
     * Test to check the rule processDeleteTaxLotLineRules
     */
    public void checkProcessDeleteTaxLotLineRules_True() {

        int index = 0 ;
        Number transLineIndex = 0;
        Number taxLotLineIndex = 0;
        
        setupDataToTestValidateKemidHasTaxLots(index);        
        document.getSourceTransactionSecurity().setRegistrationCode(REGISTRATION_CODE); //search should succeed...

        EndowmentTaxLotLinesDocument endowmentTaxLotLinesDocument = null;
        EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine = new EndowmentTransactionTaxLotLine();
        
        EndowmentTransactionLine endowmentTransactionLine = document.getSourceTransactionLines().get(index);
        
        endowmentTransactionTaxLotLine.setDocumentLineNumber(1);
        endowmentTransactionTaxLotLine.setDocumentNumber(REFERENCE_DOCUMENT_NUMBER);
        endowmentTransactionTaxLotLine.setKemid(KEM_ID);
        endowmentTransactionTaxLotLine.setSecurityID(SECURITY_ID);
        endowmentTransactionTaxLotLine.setRegistrationCode(REGISTRATION_CODE);
        endowmentTransactionTaxLotLine.setIpIndicator(IP_INDICATOR);
        endowmentTransactionTaxLotLine.setDocumentLineNumber(1);
        
        List<EndowmentTransactionTaxLotLine> taxLotLines = endowmentTransactionLine.getTaxLotLines();
        taxLotLines.add(endowmentTransactionTaxLotLine);

        endowmentTransactionTaxLotLine.setDocumentLineNumber(2);
        taxLotLines.add(endowmentTransactionTaxLotLine);
        
        endowmentTransactionLine.setTaxLotLines(taxLotLines);
        
        assertTrue(rule.processDeleteTaxLotLineRules(endowmentTaxLotLinesDocument, endowmentTransactionTaxLotLine, endowmentTransactionLine, transLineIndex, taxLotLineIndex));
    }    
}

