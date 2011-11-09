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

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.HoldingHistoryFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.MonthEndDateFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class tests the rules in HoldingHistoryValueAdjustmentDocumentRule
 */
@ConfigureContext(session = khuntley)
public class HoldingHistoryValueAdjustmentDocumentRuleTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(HoldingHistoryValueAdjustmentDocumentRuleTest.class);

    private HoldingHistoryValueAdjustmentDocumentRules rule;
    private HoldingHistoryValueAdjustmentDocument document;
    private DocumentService documentService;
    private HoldingHistoryService holdingHistoryService;
    private SecurityService securityService;
    private UnitTestSqlDao unitTestSqlDao;
    private Security security;
    private KEMID kemid;
    private ClassCode classCode;
    private SecurityReportingGroup reportingGroup;
    private EndowmentTransactionCode endowmentTransactionCode;
    private MonthEndDate monthEndDate;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new HoldingHistoryValueAdjustmentDocumentRules();
        documentService = SpringContext.getBean(DocumentService.class);
        holdingHistoryService = SpringContext.getBean(HoldingHistoryService.class);
        securityService = SpringContext.getBean(SecurityService.class);  
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);  
        
        monthEndDate = MonthEndDateFixture.MONTH_END_DATE_TEST_RECORD.createMonthEndDate();
        reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        classCode = ClassCodeFixture.HOLDING_HISTORY_VALUE_ADJUSTMENT_CLASS_CODE_2.createClassCodeRecord();
        security = SecurityFixture.HOLDING_HISTORY_VALUE_ADJUSTMENT_ACTIVE_SECURITY.createSecurityRecord();
        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        
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
        
        HoldingHistoryValueAdjustmentDocument doc = (HoldingHistoryValueAdjustmentDocument) documentService.getNewDocument(HoldingHistoryValueAdjustmentDocument.class);
        doc.getDocumentHeader().setDocumentDescription("This is a test document.");
        
        doc.getDocumentHeader().setDocumentNumber(EndowTestConstants.REFERENCE_DOCUMENT_NUMBER);
        doc.getDocumentHeader().setDocumentDescription(EndowTestConstants.REFERENCE_DOCUMENT_DESCRIPTION);
        doc.setSecurityId(security.getId());
        doc.setHoldingMonthEndDate(EndowTestConstants.FIRST_MONTH_END_DATE_ID);
        doc.setSecurityUnitValue(EndowTestConstants.ZERO_AMOUNT.bigDecimalValue());
        doc.setSecurityMarketValue(EndowTestConstants.ZERO_AMOUNT.bigDecimalValue());
        
        return doc;
    }

    /**
     * create a blank for HistoryHoldingValueAdjustmentDocuemnt
     * @return doc
     * @throws WorkflowException
     */
    protected HoldingHistoryValueAdjustmentDocument createHoldingHistoryValueAdjustmentDocument2() throws WorkflowException {
        
        HoldingHistoryValueAdjustmentDocument doc = (HoldingHistoryValueAdjustmentDocument) documentService.getNewDocument(HoldingHistoryValueAdjustmentDocument.class);
        doc.getDocumentHeader().setDocumentDescription("This is a test document.");
        
        doc.getDocumentHeader().setDocumentNumber(EndowTestConstants.REFERENCE_DOCUMENT_NUMBER);
        doc.getDocumentHeader().setDocumentDescription(EndowTestConstants.REFERENCE_DOCUMENT_DESCRIPTION);
     
        doc.setHoldingMonthEndDate(EndowTestConstants.FIRST_MONTH_END_DATE_ID);
        doc.setSecurityUnitValue(EndowTestConstants.ZERO_AMOUNT.bigDecimalValue());
        doc.setSecurityMarketValue(EndowTestConstants.ZERO_AMOUNT.bigDecimalValue());
        
        return doc;
    }
    
    /**
     * test to make sure isSecurityCodeEmpty validation returns true when security id is empty
     * and return false when security id is filled in.
     */
    public void testIsSecurityCodeEmpty() {
        LOG.info("testIsSecurityCodeEmpty() entered");
        
        document.setSecurityId(null);
        assertTrue(rule.isSecurityCodeEmpty(document));

        document.setSecurityId(security.getId()); 
        assertFalse(rule.isSecurityCodeEmpty(document));
    }
    
    /**
     * test to make sure validate validation works correctly.
     */
    public void testValidateSecurityCode_False() {
        LOG.info("testValidateSecurityCode_False() entered");
        
        document.setSecurityId(EndowTestConstants.INVALID_SECURITY_ID); 
        assertFalse(rule.validateSecurityCode(document));
        
        document.setSecurityId(security.getId()); 
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
    public void testSecurityCodeExists() {
        LOG.info("testSecurityCodeExists() entered");
                
        setSecurityObjectIntoDocument(null);
        assertFalse(!ObjectUtils.isNull(document.getSecurity()));
        
        setSecurityObjectIntoDocument(security.getId());
        assertTrue(ObjectUtils.isNotNull(document.getSecurity()));
    }
    
    /**
     * testing isSecurityActive rule.
     */
    public void testIsSecurityActive() {
        LOG.info("testIsSecurityActive() entered");
        
        setSecurityObjectIntoDocument(security.getId());
        document.getSecurity().setActive(false);
        assertFalse(rule.isSecurityActive(document));
        
        document.getSecurity().setActive(true);
        assertTrue(rule.isSecurityActive(document));
    }    
    
    /**
     * test validateSecurityClassCodeTypeNotLiability rule.
     */
    public void testValidateSecurityClassCodeTypeNotLiability() {
        LOG.info("testValidateSecurityClassCodeTypeNotLiability() entered");
        
        setSecurityObjectIntoDocument(security.getId());
        assertFalse(rule.validateSecurityClassCodeTypeNotLiability(document));
        
        ClassCode classCode = ClassCodeFixture.HOLDING_HISTORY_VALUE_ADJUSTMENT_NOT_LIABILITY_CLASS_CODE_2.createClassCodeRecord();
        Security security = SecurityFixture.HOLDING_HISTORY_VALUE_ADJUSTMENT_ACTIVE_SECURITY_2.createSecurityRecord();

        HoldingHistoryValueAdjustmentDocument doc = null;
        
        try {
            doc = createHoldingHistoryValueAdjustmentDocument2();
        } catch (WorkflowException wfe) {
            
        }
        doc.setSecurityId(security.getId());
        Security sec = (Security) SpringContext.getBean(SecurityService.class).getByPrimaryKey(doc.getSecurityId());
        doc.setSecurity(sec);
        assertTrue(rule.validateSecurityClassCodeTypeNotLiability(doc));
    }    

    /**
     * test to make sure checkValuationMethodForUnitOrSecurityValue returns false.
     */
    public void testCheckValuationMethodForUnitOrSecurityValue_False() {
        LOG.info("testCheckValuationMethodForUnitOrSecurityValue_False() entered");
        
        setSecurityObjectIntoDocument(security.getId());
        
        //when valuation method is U, unit value can not be null
        document.getSecurity().getClassCode().setValuationMethod(EndowTestConstants.UNIT_VALUATION_METHOD_CODE);
        document.getSecurity().getClassCode().refreshNonUpdateableReferences();
        document.setSecurityUnitValue(null);
        assertFalse(rule.checkValuationMethodForUnitOrSecurityValue(document));

        //when valuation method is U, unit value can not be null
        document.getSecurity().getClassCode().setValuationMethod(EndowTestConstants.MARKET_VALUATION_METHOD_CODE);
        document.getSecurity().getClassCode().refreshNonUpdateableReferences();
        document.setSecurityMarketValue(null);
        assertFalse(rule.checkValuationMethodForUnitOrSecurityValue(document));
    }    
    
    /**
     * test to make sure checkValuationMethodForUnitOrSecurityValue sets market value to null and 
     * returns true .
     */
    public void testCheckValuationMethodAndSetMarketValueToNull_True() {
        LOG.info("testCheckValuationMethodAndSetMarketValueToNull_True() entered");
        
        setSecurityObjectIntoDocument(security.getId());
        
        //when valuation method is U, make security market value field = null
        document.getSecurity().getClassCode().setValuationMethod(EndowTestConstants.UNIT_VALUATION_METHOD_CODE);
        document.getSecurity().getClassCode().refreshNonUpdateableReferences();
        document.setSecurityUnitValue(new BigDecimal(1));
        document.setSecurityMarketValue(new BigDecimal(1));
        //setSecurityMarketValue will be set to null in the rule class...
        assertTrue(rule.checkValuationMethodForUnitOrSecurityValue(document) == ObjectUtils.isNull(document.getSecurityMarketValue()));        
    }
    
    /**
     * test to make sure calculateUnitValueWhenMarketValueEntered rule calculates the value
     */
    public void testCalculateUnitValueWhenMarketValueEntered() {
        LOG.info("testCalculateUnitValueWhenMarketValueEntered() entered");
        
        BigDecimal unitValue = BigDecimal.ZERO;
        BigDecimal totalUnits = BigDecimal.ZERO;
        
        RegistrationCode registrationCode1 = RegistrationCodeFixture.REGISTRATION_CODE_RECORD1_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT.createRegistrationCode();
        RegistrationCode registrationCode2 = RegistrationCodeFixture.REGISTRATION_CODE_RECORD2_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT.createRegistrationCode();
        
        HoldingHistory hh1 = HoldingHistoryFixture.HOLDING_HISTORY_RECORD1_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT.createHoldingHistoryRecord();
        HoldingHistory hh2 = HoldingHistoryFixture.HOLDING_HISTORY_RECORD2_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT.createHoldingHistoryRecord();
        
        Collection<HoldingHistory> holdingHistoryRecords = SpringContext.getBean(HoldingHistoryService.class).getHoldingHistoryBySecuritIdAndMonthEndId(document.getSecurityId(), document.getHoldingMonthEndDate());
        assertTrue(holdingHistoryRecords.size() == 2);
        setSecurityObjectIntoDocument(security.getId());
        document.setSecurityMarketValue(new BigDecimal(1)); 
        document.setSecurityUnitValue(rule.calculateUnitValueWhenMarketValueEntered(document));

        assertTrue(document.getSecurityUnitValue().toString().compareTo("0.00250") == 0);
        
        // case of classCodeType = B for BONDS....
        ClassCode classCode = ClassCodeFixture.HOLDING_HISTORY_VALUE_ADJUSTMENT_NOT_LIABILITY_CLASS_CODE_2.createClassCodeRecord();
        Security security2 = SecurityFixture.HOLDING_HISTORY_VALUE_ADJUSTMENT_ACTIVE_SECURITY_2.createSecurityRecord();

        HoldingHistoryValueAdjustmentDocument doc = null;
        
        try {
            doc = createHoldingHistoryValueAdjustmentDocument2();
        } catch (WorkflowException wfe) {
            
        }
        doc.setSecurityId(security2.getId());
        Security sec = (Security) SpringContext.getBean(SecurityService.class).getByPrimaryKey(doc.getSecurityId());
        doc.setSecurity(sec);
        doc.setSecurityMarketValue(new BigDecimal(1)); 

        HoldingHistory hh3 = HoldingHistoryFixture.HOLDING_HISTORY_RECORD3_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT.createHoldingHistoryRecord();
        HoldingHistory hh4 = HoldingHistoryFixture.HOLDING_HISTORY_RECORD4_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT.createHoldingHistoryRecord();
        
        doc.setSecurityUnitValue(rule.calculateUnitValueWhenMarketValueEntered(doc));
        
        assertTrue(doc.getSecurityUnitValue().toString().compareTo("0.25000") == 0);
    }
    
    /**
     * test to make sure if UnitValuePositive returns false.
     */
    public void testIsUnitValuePositive_False() {
        LOG.info("testIsUnitValuePositive_False() entered");
        
        document.setSecurityUnitValue(EndowTestConstants.ZERO_AMOUNT.bigDecimalValue());
        assertFalse(rule.isUnitValuePositive(document));
    }    

    /**
     * test to make sure if UnitValuePositive returns true.
     */
    public void testIsUnitValuePositive_True() {
        LOG.info("testIsUnitValuePositive_True() entered");
        
        document.setSecurityUnitValue(new BigDecimal(1000));
        assertTrue(rule.isUnitValuePositive(document));
    }    
    
    /**
     * test to make sure if isMarketValuePositive returns false.
     */
    public void testIsMarketValuePositive_False() {
        LOG.info("testIsMarketValuePositive_False() entered");
        
        setSecurityObjectIntoDocument(security.getId());
        document.getSecurity().getClassCode().setValuationMethod(EndowTestConstants.MARKET_VALUATION_METHOD_CODE);
        document.getSecurity().getClassCode().refreshNonUpdateableReferences();
        document.setSecurityMarketValue(EndowTestConstants.ZERO_AMOUNT.bigDecimalValue());
        assertFalse(rule.isMarketValuePositive(document));
    }    

    /**
     * test to make sure if isMarketValuePositive returns true.
     */
    public void testIsMarketValuePositive_True() {
        LOG.info("testIsMarketValuePositive_True() entered");
        
        setSecurityObjectIntoDocument(security.getId());
        document.getSecurity().getClassCode().setValuationMethod(EndowTestConstants.MARKET_VALUATION_METHOD_CODE);
        document.getSecurity().getClassCode().refreshNonUpdateableReferences();
        document.setSecurityMarketValue(new BigDecimal(1000));
        assertTrue(rule.isMarketValuePositive(document));
    }    
}

