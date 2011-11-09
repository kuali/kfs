/*
 * Copyright 2011 The Kuali Foundation.
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
import java.sql.Date;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentMaintenanceDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the rules in SecurityRule class
 */
@ConfigureContext(session = khuntley)
public class SecurityRuleTest extends MaintenanceRuleTestBase {

    private static final Logger LOG = Logger.getLogger(SecurityRuleTest.class);
    private SecurityRule rule;
    private FinancialSystemMaintenanceDocument document;
    private DocumentService documentService;
    private Security security;
    private KEMID kemid;

    private static final BigDecimal ZERO_AMOUNT = new BigDecimal(0);
    private static final BigDecimal NEGATIVE_AMOUNT = new BigDecimal(-1);
    private static final BigDecimal POSITIVE_AMOUNT = new BigDecimal(2);


    private static final String REFERENCE_DOCUMENT_NUMBER = "123456";
    private static final String REFERENCE_DOCUMENT_DESCRIPTION = "Document Description - Unit Test";
    private static final String INCOME_PAY_FREQUENCY = "AA01";
    private static final String EMPTY_INCOME_PAY_FREQUENCY = "";
    public static final String POOLED_INVESTMENT = "P";
    private static final String reqClassCode = "400";
    private static final String newClassCode = "300";
    private Date currentDate;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new SecurityRule();
        documentService = SpringContext.getBean(DocumentService.class);
        document = createFinancialSystemMaintenanceDocument();
        rule.initializeAttributes(document);
    }

    @Override
    protected void tearDown() throws Exception {
        document = null;
        rule = null;
        super.tearDown();
    }

    /**
     * create a SecurityRule Document
     * 
     * @return doc
     */
    protected FinancialSystemMaintenanceDocument createFinancialSystemMaintenanceDocument() throws WorkflowException {
        LOG.info("createSecurityRuleDocument() entered.");

        FinancialSystemMaintenanceDocument fsd = EndowmentMaintenanceDocumentFixture.ENDOWMENT_MAINTENANCE_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentMaintenanceDocument();
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
        fsd.getNewMaintainableObject().setBusinessObject(security);
        fsd.getOldMaintainableObject().setBusinessObject(security);

        return fsd;
    }

    public void testCheckCustomRequiredFields() {
        rule.newSecurity.setSecurityClassCode(reqClassCode);
        // false scenarios for AccrualMethod "3"
        rule.newSecurity.getClassCode().setSecurityAccrualMethod(EndowConstants.AccrualMethod.MORTGAGE_30);

        rule.newSecurity.setMaturityDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomePayFrequency(EMPTY_INCOME_PAY_FREQUENCY);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomeRate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIssueDate(null);
        assertFalse(rule.checkCustomRequiredFields());


        // false scenarios for AccrualMethod M
        rule.newSecurity.getClassCode().setSecurityAccrualMethod(EndowConstants.AccrualMethod.TIME_DEPOSITS);

        rule.newSecurity.setMaturityDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomePayFrequency(EMPTY_INCOME_PAY_FREQUENCY);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomeRate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIssueDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        // false scenarios for AccrualMethod "T"
        rule.newSecurity.getClassCode().setSecurityAccrualMethod(EndowConstants.AccrualMethod.TREASURY_NOTES_AND_BONDS);

        rule.newSecurity.setMaturityDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomePayFrequency(EMPTY_INCOME_PAY_FREQUENCY);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomeRate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIssueDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        // false scenarios for AccrualMethod "6"
        rule.newSecurity.getClassCode().setSecurityAccrualMethod(EndowConstants.AccrualMethod.MORTGAGE_60);

        rule.newSecurity.setMaturityDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomePayFrequency(EMPTY_INCOME_PAY_FREQUENCY);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomeRate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIssueDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        // false scenarios for AccrualMethod "D"
        rule.newSecurity.getClassCode().setSecurityAccrualMethod(EndowConstants.AccrualMethod.DISCOUNT_BONDS);

        rule.newSecurity.setMaturityDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomePayFrequency(EMPTY_INCOME_PAY_FREQUENCY);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIncomeRate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.setIssueDate(null);
        assertFalse(rule.checkCustomRequiredFields());

        rule.newSecurity.getClassCode().setSecurityAccrualMethod(EndowConstants.AccrualMethod.AUTOMATED_CASH_MANAGEMENT);
        rule.newSecurity.getClassCode().setClassCodeType(EndowConstants.ClassCodeTypes.ALTERNATIVE_INVESTMENT);
        // false scenario
        rule.newSecurity.setCommitmentAmount(BigDecimal.ZERO);
        assertFalse(rule.checkCustomRequiredFields());

        // true scenario
        rule.newSecurity.setCommitmentAmount(POSITIVE_AMOUNT);
        assertTrue(rule.checkCustomRequiredFields());
    }

    public void testCheckClassCode_True() {
        rule.newSecurity.getClassCode().setClassCodeType(rule.oldSecurity.getClassCode().getClassCodeType());
        assertTrue(rule.checkClassCode());
    }

    /**
     * test to make sure if UnitValuePositive returns false.
     */

    public void testCheckUnitValue() {
        // false scenarios
        rule.newSecurity.getClassCode().setClassCodeType(EndowConstants.ClassCodeTypes.LIABILITY);
        rule.newSecurity.setUnitValue(POSITIVE_AMOUNT);
        assertFalse(rule.checkUnitValue());

        rule.newSecurity.getClassCode().setClassCodeType(EndowConstants.ClassCodeTypes.OTHER);
        rule.newSecurity.setUnitValue(NEGATIVE_AMOUNT);
        assertFalse(rule.checkUnitValue());

        // true scenarios
        rule.newSecurity.getClassCode().setClassCodeType(EndowConstants.ClassCodeTypes.LIABILITY);
        rule.newSecurity.setUnitValue(NEGATIVE_AMOUNT);
        assertTrue(rule.checkUnitValue());

        rule.newSecurity.getClassCode().setClassCodeType(EndowConstants.ClassCodeTypes.OTHER);
        rule.newSecurity.setUnitValue(POSITIVE_AMOUNT);
        assertTrue(rule.checkUnitValue());


    }

    /**
     * test to make sure checkValuationMethodForUnitOrSecurityValue returns false.
     */
    public void testCheckValuesBasedOnValuationMethod() {
        rule.newSecurity.setSecurityClassCode(newClassCode);
        // false scenarios
        // when valuation method is U, market value should be empty/null
        rule.newSecurity.getClassCode().setValuationMethod(EndowConstants.ValuationMethod.UNITS);
        rule.newSecurity.setSecurityValueByMarket(POSITIVE_AMOUNT);
        assertFalse(rule.checkValuesBasedOnValuationMethod());

        // when valuation method is M, unit value should be empty/null
        rule.newSecurity.getClassCode().setValuationMethod(EndowConstants.ValuationMethod.MARKET);
        rule.newSecurity.setUnitValue(POSITIVE_AMOUNT);
        assertFalse(rule.checkValuesBasedOnValuationMethod());

        // true scenarios
        // when valuation method is U, market value should be empty/null
        rule.newSecurity.getClassCode().setValuationMethod(EndowConstants.ValuationMethod.UNITS);
        rule.newSecurity.setSecurityValueByMarket(null);
        assertTrue(rule.checkValuesBasedOnValuationMethod());
        
        // when valuation method is M, unit value should be empty/null
        rule.newSecurity.getClassCode().setValuationMethod(EndowConstants.ValuationMethod.MARKET);
        rule.newSecurity.setUnitValue(null);
        assertTrue(rule.checkValuesBasedOnValuationMethod());
    }


    public void testCheckIncomeFrequencyCodeWhenPooledFundClassCodeUsed() {

        // Validate income pay frequency is entered
        // false scenarios
        rule.newSecurity.setSecurityClassCode(newClassCode);
        rule.newSecurity.setIncomePayFrequency(EMPTY_INCOME_PAY_FREQUENCY);
        assertFalse(rule.checkIncomeFrequencyCodeWhenPooledFundClassCodeUsed());
        
        // true scenarios
        rule.newSecurity.setIncomePayFrequency(INCOME_PAY_FREQUENCY);
        assertTrue(rule.checkIncomeFrequencyCodeWhenPooledFundClassCodeUsed());
    }

}
