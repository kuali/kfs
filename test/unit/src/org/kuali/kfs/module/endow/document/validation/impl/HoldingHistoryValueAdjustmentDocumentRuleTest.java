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
import java.util.Collection;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class tests the rules in HoldingHistoryValueAdjustmentDocumentRule
 */
@ConfigureContext(session = khuntley)
public class HoldingHistoryValueAdjustmentDocumentRuleTest extends KualiTestBase {

    private HoldingHistoryValueAdjustmentDocumentRules rule;
    private HoldingHistoryValueAdjustmentDocument document;
    private DocumentService documentService;
    private HoldingHistoryService holdingHistoryService;
    private SecurityService securityService;
    private UnitTestSqlDao unitTestSqlDao;
    
    private static final BigDecimal ZERO_AMOUNT = new BigDecimal(0);
    private static final BigDecimal NEGATIVE_AMOUNT = new BigDecimal(-1);
    private static final BigDecimal POSITIVE_AMOUNT = new BigDecimal(2);

    private static final String REFERENCE_DOCUMENT_NUMBER = "123456";
    private static final String REFERENCE_DOCUMENT_DESCRIPTION = "Document Description - Unit Test";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new HoldingHistoryValueAdjustmentDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        holdingHistoryService = SpringContext.getBean(HoldingHistoryService.class);
        securityService = SpringContext.getBean(SecurityService.class);  
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);  
        
        //create the document
        document = createHoldingHistoryValueAdjustmentDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        documentService = null;
        holdingHistoryService = null;
        securityService = null;
        
        super.tearDown();
    }

    /**
     * create a blank for HistoryHoldingValueAdjustmentDocuemnt
     * @return doc
     * @throws WorkflowException
     */
    protected HoldingHistoryValueAdjustmentDocument createHoldingHistoryValueAdjustmentDocument() throws WorkflowException {

        //insert into security class code END_SEC_CLS_CD_T table to validate security class code later...
        String sqlForSecurityClassCode = "INSERT INTO END_CLS_CD_T VALUES ('MUD','Test Class Code Description','LIAB','N','22025','74100','N','L','Y','BLAH BLAH BLAH',1,'U')";
        int rowsForSecurityClassCode = unitTestSqlDao.sqlCommand(sqlForSecurityClassCode);   
        
        //first add into END_SEC_T with securityid = 000000000 so this record would be unique.
        String sqlForSecurity = "INSERT INTO END_SEC_T VALUES ('000000000','','','MUD','','','','','','','','','','','','','','','','','','','','','','Y','TEST TEST TEST',1,'')";
        int rowsForSecurity = unitTestSqlDao.sqlCommand(sqlForSecurity);   
                
        HoldingHistoryValueAdjustmentDocument doc = (HoldingHistoryValueAdjustmentDocument) documentService.getNewDocument(HoldingHistoryValueAdjustmentDocument.class);
        doc.getDocumentHeader().setDocumentDescription("This is a test document.");
        
        doc.getDocumentHeader().setDocumentNumber(REFERENCE_DOCUMENT_NUMBER);
        doc.getDocumentHeader().setDocumentDescription(REFERENCE_DOCUMENT_DESCRIPTION);
     
        doc.setSecurityId("000000000");
        doc.setHoldingMonthEndDate(new KualiInteger(1));
        doc.setSecurityUnitValue(ZERO_AMOUNT);
        doc.setSecurityMarketValue(ZERO_AMOUNT);
        
    //    documentService.saveDocument(doc);
        
        return doc;
    }
    
    /**
     * test to make sure isSecurityCodeEmpty validation returns true.
     */
    public void testIsSecurityCodeEmpty_True() {
        document.setSecurityId(null);
        
        assertTrue(rule.isSecurityCodeEmpty(document));
    }
    
    /**
     * test to make sure isSecurityCodeEmpty validation returns false.
     */
    public void testIsSecurityCodeEmpty_False() {
        document.setSecurityId("000000000"); 
        
        assertFalse(rule.isSecurityCodeEmpty(document));
    } 

    /**
     * test to make sure validate validation returns false.
     */
    public void testValidateSecurityCode_False() {
        document.setSecurityId(null); 
        
        assertFalse(rule.validateSecurityCode(document));
    }
    
    /**
     * test to make sure validate validation returns true.
     */
    public void testValidateSecurityCode_True() {
        document.setSecurityId("000000000"); 
        
        assertTrue(rule.validateSecurityCode(document));
    }
    
    /**
     * Method to create security object and insert into the document.
     */
    protected void setSecurityObjectIntoDocument(String securityId) {
        document.setSecurityId(securityId);        
        Security security = (Security) SpringContext.getBean(SecurityService.class).getByPrimaryKey(document.getSecurityId());
        document.setSecurity(security);
    }
    
    /**
     * test to make sure that security code does not exists in the END_SEC_T table...
     * testing the service SecurityService.class
     */
    public void testSecurityCodeExists_False() {
        setSecurityObjectIntoDocument(null);
        
        assertFalse(!ObjectUtils.isNull(document.getSecurity()));
    }
    
    /**
     * test to make sure that security code exists in the END_SEC_T table...
     * testing the service SecurityService.class
     */
    public void testSecurityCodeExists_True() {
        setSecurityObjectIntoDocument("000000000");
        
        assertTrue(ObjectUtils.isNotNull(document.getSecurity()));
    }

    /**
     * test to make sure isSecurityActive returns false.
     */
    public void testIsSecurityActive_False() {
        setSecurityObjectIntoDocument("000000000");        
        document.getSecurity().setActive(false);            
        
        assertFalse(rule.isSecurityActive(document));
    }    
    
    /**
     * test to make sure isSecurityActive returns true.
     */
    public void testIsSecurityActive_True() {
        setSecurityObjectIntoDocument("000000000");        
        document.getSecurity().setActive(true);            
        
        assertTrue(rule.isSecurityActive(document));
    }    

    /**
     * test to make sure validateSecurityClassCodeTypeNotLiability returns false.
     */
    public void testValidateSecurityClassCodeTypeNotLiability_False() {
        setSecurityObjectIntoDocument("000000000");
        document.getSecurity().getClassCode().setCode("MUD");          
        
        assertFalse(rule.validateSecurityClassCodeTypeNotLiability(document));
    }    

    /**
     * test to make sure validateSecurityClassCodeTypeNotLiability returns true
     */
    public void testValidateSecurityClassCodeTypeNotLiability_True() {
        setSecurityObjectIntoDocument("000000000");
        document.getSecurity().getClassCode().setClassCodeType("A");        
        
        assertTrue(rule.validateSecurityClassCodeTypeNotLiability(document));
    }
    
    /**
     * test to make sure checkValuationMethodForUnitOrSecurityValue returns false.
     */
    public void testCheckValuationMethodForUnitOrSecurityValue_False() {
        setSecurityObjectIntoDocument("000000000");
        document.getSecurity().getClassCode().setCode("MUD");          
        
        //when valuation method is U, unit value can not be null
        document.getSecurity().getClassCode().getSecurityValuationMethod().setCode("U");
        document.setSecurityUnitValue(null);
        assertFalse(rule.checkValuationMethodForUnitOrSecurityValue(document));

        //when valuation method is U, unit value can not be null
        document.getSecurity().getClassCode().getSecurityValuationMethod().setCode("M");
        document.setSecurityMarketValue(null);
        assertFalse(rule.checkValuationMethodForUnitOrSecurityValue(document));
    }    
    
    /**
     * test to make sure checkValuationMethodForUnitOrSecurityValue sets market value to null and 
     * returns true .
     */
    public void testCheckValuationMethodAndSetMarketValueToNull_True() {
        setSecurityObjectIntoDocument("000000000");
        document.getSecurity().getClassCode().setCode("MUD");          
        
        //when valuation method is U, make security market value field = null
        document.setSecurityUnitValue(new BigDecimal(1));
        document.setSecurityMarketValue(new BigDecimal(1));
        //setSecurityMarketValue will be set to null in the rule class...
        assertTrue(rule.checkValuationMethodForUnitOrSecurityValue(document) == ObjectUtils.isNull(document.getSecurityMarketValue()));        
    }
    
    /**
     * test to make sure calculateUnitValueWhenMarketValueEntered rule calculates the value
     */
    public void testCalculateUnitValueWhenMarketValueEntered() {
        
        BigDecimal unitValue = BigDecimal.ZERO;
        BigDecimal totalUnits = BigDecimal.ZERO;
        
        // insert into HoldingHistory table some test records....
        String sqlForHoldingHistory = "INSERT INTO END_HLDG_HIST_T COLUMNS (KEMID, ME_DT_ID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, HLDG_FRGN_TAX_WITH, LAST_TRAN_DT, HLDG_MVAL, AVG_MVAL, OBJ_ID) VALUES ('032A017014', '1', '000000000', '0AI', '1', 'I', '1', '1', '107852.3', '0.00', '107852.3', '1', TO_DATE('1/1/2007', 'mm/dd/yyyy'), '0', '0', '0', TO_DATE('6/30/2007', 'mm/dd/yyyy'), '6234237', '0',sys_guid())";
        int rowsForHoldingHistory = unitTestSqlDao.sqlCommand(sqlForHoldingHistory);
        sqlForHoldingHistory = "INSERT INTO END_HLDG_HIST_T COLUMNS (KEMID, ME_DT_ID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, HLDG_FRGN_TAX_WITH, LAST_TRAN_DT, HLDG_MVAL, AVG_MVAL, OBJ_ID) VALUES ('032A017014', '1', '000000000', '0AI', '2', 'I', '2', '2', '96.42', '0.00', '96.42', '1', TO_DATE('1/1/2007', 'mm/dd/yyyy'), '0', '33.8345', '0', TO_DATE('6/30/2007', 'mm/dd/yyyy'), '3214', '0',sys_guid())";
        rowsForHoldingHistory = unitTestSqlDao.sqlCommand(sqlForHoldingHistory);
        sqlForHoldingHistory = "INSERT INTO END_HLDG_HIST_T COLUMNS (KEMID, ME_DT_ID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, HLDG_FRGN_TAX_WITH, LAST_TRAN_DT, HLDG_MVAL, AVG_MVAL, OBJ_ID) VALUES ('032A017014', '1', '000000000', '0BI', '3', 'I', '3', '3', '0', '0.00', '0', '1', TO_DATE('1/1/2007', 'mm/dd/yyyy'), '0', '0', '0', TO_DATE('6/30/2007', 'mm/dd/yyyy'), '10000', '10000',sys_guid())";        
        rowsForHoldingHistory = unitTestSqlDao.sqlCommand(sqlForHoldingHistory);       
        sqlForHoldingHistory = "INSERT INTO END_HLDG_HIST_T COLUMNS (KEMID, ME_DT_ID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, HLDG_FRGN_TAX_WITH, LAST_TRAN_DT, HLDG_MVAL, AVG_MVAL, OBJ_ID) VALUES ('032A017014', '1', '000000000', '0CI', '4', 'I', '4', '4', '39098.39', '0.00', '39098.39', '1.35134', TO_DATE('1/1/2007', 'mm/dd/yyyy'), '0', '0', '0', TO_DATE('6/30/2007', 'mm/dd/yyyy'), '917278', '917278',sys_guid())";
        rowsForHoldingHistory = unitTestSqlDao.sqlCommand(sqlForHoldingHistory);

        Collection<HoldingHistory> holdingHistoryRecords = SpringContext.getBean(HoldingHistoryService.class).getHoldingHistoryBySecuritIdAndMonthEndId(document.getSecurityId(), document.getHoldingMonthEndDate());
        assertTrue(holdingHistoryRecords.size() == 4);
        
        setSecurityObjectIntoDocument("000000000");
        document.getSecurity().getClassCode().setCode("MUD");          
        document.getSecurity().getClassCode().getSecurityValuationMethod().setCode("M");
        document.setSecurityMarketValue(new BigDecimal(1)); 
        
        document.setSecurityUnitValue(rule.calculateUnitValueWhenMarketValueEntered(document));
        
        //the test case classcode type = S (stocks) .. in rule class it is division....
        assertTrue(document.getSecurityUnitValue().toString().compareTo("0.10000") == 0);
    }
    
    /**
     * test to make sure if UnitValuePositive returns false.
     */
    public void testIsUnitValuePositive_False() {
        document.setSecurityUnitValue(ZERO_AMOUNT);
        assertFalse(rule.isUnitValuePositive(document));
    }    

    /**
     * test to make sure if UnitValuePositive returns true.
     */
    public void testIsUnitValuePositive_True() {
        document.setSecurityUnitValue(new BigDecimal(1000));
        assertTrue(rule.isUnitValuePositive(document));
    }    
    
    /**
     * test to make sure if isMarketValuePositive returns false.
     */
    public void testIsMarketValuePositive_False() {
        setSecurityObjectIntoDocument("000000000");
        document.getSecurity().getClassCode().setCode("MUD");          
        document.getSecurity().getClassCode().getSecurityValuationMethod().setCode("M");
        
        document.setSecurityMarketValue(ZERO_AMOUNT);
        assertFalse(rule.isMarketValuePositive(document));
    }    

    /**
     * test to make sure if isMarketValuePositive returns true.
     */
    public void testIsMarketValuePositive_True() {
        setSecurityObjectIntoDocument("000000000");
        document.getSecurity().getClassCode().setCode("MUD");          
        document.getSecurity().getClassCode().getSecurityValuationMethod().setCode("M");
        
        document.setSecurityMarketValue(new BigDecimal(1000));
        assertTrue(rule.isMarketValuePositive(document));
    }    
}

