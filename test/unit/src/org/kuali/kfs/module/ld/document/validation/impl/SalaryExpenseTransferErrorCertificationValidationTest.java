/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ErrorCertification;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.service.impl.SalaryExpenseTransferTransactionAgeServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.service.impl.UniversityDateServiceImpl;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * The unit tests for methods in SalaryExpenseTransferErrorCertificationValidation. Note that this validation also
 * depends on methods in SalaryExpenseTransferTransactionAgeService.
 *
 * @see org.kuali.kfs.module.ld.document.validation.impl.SalaryExpenseTransferErrorCertificationValidation
 */

@ConfigureContext
public class SalaryExpenseTransferErrorCertificationValidationTest extends KualiTestBase {
    private static final String DEFAULT_PARM_FISCAL_PERIODS="3";
    private static final String DEFAULT_PARM_SUBFUND="FEDERA=2";
    private static final String YOUNGER_FISCAL_PER="11";
    private static final String OLDER_FISCAL_PER="7";
    private static final String NON_PARM_SUBFUND="AG";
    private static final String PARM_SUBFUND="FEDERA";

    private SalaryExpenseTransferErrorCertificationValidation validation;
    private SalaryExpenseTransferTransactionAgeServiceImpl salaryExpenseTransferTransactionAgeService;
    private MyUniversityDateService universityDateService;
    private UniversityDateService oldDateService;
    private SalaryExpenseTransferDocument stDoc;
    private MyAttributedDocumentEvent event;
    private ErrorCertification errorCertification;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        validation = new SalaryExpenseTransferErrorCertificationValidation();
        salaryExpenseTransferTransactionAgeService = new SalaryExpenseTransferTransactionAgeServiceImpl();
        universityDateService = new MyUniversityDateService();
        oldDateService = salaryExpenseTransferTransactionAgeService.getUniversityDateService();

        salaryExpenseTransferTransactionAgeService.setUniversityDateService(universityDateService);
        validation.setParameterService(SpringContext.getBean(ParameterService.class));
        validation.setSalaryExpenseTransferTransactionAgeService(salaryExpenseTransferTransactionAgeService);
        stDoc = new SalaryExpenseTransferDocument();
        event = new MyAttributedDocumentEvent(stDoc);
        errorCertification = new ErrorCertification();
        errorCertification.setDocumentNumber("1");
        stDoc.setErrorCertification(errorCertification);
        TestUtils.setSystemParameter(KfsParameterConstants.LABOR_DOCUMENT.class, SalaryExpenseTransferDocumentRuleConstants.DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED, DEFAULT_PARM_FISCAL_PERIODS);
        TestUtils.setSystemParameter(KfsParameterConstants.LABOR_DOCUMENT.class, SalaryExpenseTransferDocumentRuleConstants.ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND, DEFAULT_PARM_SUBFUND);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        this.salaryExpenseTransferTransactionAgeService.setUniversityDateService(oldDateService);
    }

    /**
     * Test the case where the source accounting line is "younger" and the error certification tab is complete.
     * This is based on fiscal periods and the DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED parameter.
     * The source accounting line will use the current year and will be set to YOUNGER_FISCAL_PER.
     * The validation will use the UniversityDateService that is defined in this test class.
     */
    public void testSourceAccountingLine() {
        // create a source accounting line
        ExpenseTransferSourceAccountingLine sourceAccountingLine = new ExpenseTransferSourceAccountingLine();
        sourceAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        sourceAccountingLine.setPayrollEndDateFiscalPeriodCode(YOUNGER_FISCAL_PER);

        stDoc.addSourceAccountingLine(sourceAccountingLine);

        // modify error certification's state as completely filled out
        setUpErrorCertificationObject(true);

        validation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertFalse("Unexpected requirement of Error Certification Tab.", hasError);
    }

    /**
     * Test the case where the source accounting line is "older" and the error certification tab is incomplete.
     * This is based on the fiscal periods and the DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED parameter.
     * The source accounting line will use the current year, but will be set to OLDER_FISCAL_PER.
     * The validation will use the UniversityDateService that is defined in this test class.
     */
    public void testOlderSourceAccountingLine() {
        // create a source accounting line
        ExpenseTransferSourceAccountingLine sourceAccountingLine = new ExpenseTransferSourceAccountingLine();
        sourceAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        sourceAccountingLine.setPayrollEndDateFiscalPeriodCode(OLDER_FISCAL_PER);

        stDoc.addSourceAccountingLine(sourceAccountingLine);

        // modify error certification's state as partially filled out
        setUpErrorCertificationObject(false);

        validation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertTrue("Error Certification Tab isn't required, but should be.", hasError);
    }

    /**
     * Test the case where the source accounting line is in previous fiscal year and the error certification tab is incomplete.
     * This is based on the fiscal year and the DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED parameter.
     * The source accounting line will use the previous year and will be set to OLDER_FISCAL_PER.
     * The validation will use the UniversityDateService that is defined in this test class.
     */
    public void testPriorYearSourceAccountingLine() {
        // create a source accounting line
        ExpenseTransferSourceAccountingLine sourceAccountingLine = new ExpenseTransferSourceAccountingLine();
        sourceAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear() - 1);
        sourceAccountingLine.setPayrollEndDateFiscalPeriodCode(OLDER_FISCAL_PER);

        stDoc.addSourceAccountingLine(sourceAccountingLine);

        // modify error certification's state as partially filled out
        setUpErrorCertificationObject(false);

        validation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertTrue("Error Certification Tab isn't required, but should be.", hasError);
    }

    /**
     * Test the case where the target accounting line is "younger" and the error certification tab is complete.
     * This is based on the fiscal periods and the DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED parameter.
     * The target accounting line will use the current year and will be set to YOUNGER_FISCAL_PER. It will also have a sub fund that is
     * not in the contribution approved ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
     * The validation will use the UniversityDateService that is defined in this test class.
     */
    public void testTargetAccountingLine() {
        Account account = new Account();
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(NON_PARM_SUBFUND);
        account.setSubFundGroup(subFundGroup);

        // create a target accounting line for testing in set object
        ExpenseTransferTargetAccountingLine targetAccountingLine = new ExpenseTransferTargetAccountingLine();
        targetAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        targetAccountingLine.setPayrollEndDateFiscalPeriodCode(YOUNGER_FISCAL_PER);
        targetAccountingLine.setAccount(account);

        stDoc.addTargetAccountingLine(targetAccountingLine);

        // modify error certification's state as completely filled out
        setUpErrorCertificationObject(true);

        validation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertFalse("Unexpected requirement of Error Certification Tab.", hasError);
    }

    /**
     * Test the case where the target accounting line is "older" and the error certification tab is complete.
     * This is based on the fiscal periods and the DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED parameter.
     * The target accounting line will use the current year and will be set to OLDER_FISCAL_PER. It will also have a sub fund that is
     * not in the contribution approved ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
     * The validation will use the UniversityDateService that is defined in this test class.
     */
    public void testOlderTargetAccountingLine() {
        Account account = new Account();
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(NON_PARM_SUBFUND);
        account.setSubFundGroup(subFundGroup);

        // create a target accounting line for testing in set object
        ExpenseTransferTargetAccountingLine targetAccountingLine = new ExpenseTransferTargetAccountingLine();
        targetAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        targetAccountingLine.setPayrollEndDateFiscalPeriodCode(OLDER_FISCAL_PER);
        targetAccountingLine.setAccount(account);

        stDoc.addTargetAccountingLine(targetAccountingLine);

        // modify error certification's state as completely filled out
        setUpErrorCertificationObject(true);

        validation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertFalse("Unexpected requirement of Error Certification Tab.", hasError);
    }

    /**
     * Test the case where the target accounting line is in previous fiscal year and the error certification tab is complete.
     * This is based on the fiscal periods and the DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED parameter.
     * The target accounting line will use the previous year and will be set to OLDER_FISCAL_PER. It will also have a sub fund that is
     * not in the contribution approved ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
     * The validation will use the UniversityDateService that is defined in this test class.
     */
    @SuppressWarnings("deprecation")
    public void testPriorYearTargetAccountingLine() {
        Account account = new Account();
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(NON_PARM_SUBFUND);
        account.setSubFundGroup(subFundGroup);

        // create a target accounting line for testing in set object
        ExpenseTransferTargetAccountingLine targetAccountingLine = new ExpenseTransferTargetAccountingLine();
        targetAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear() - 1);
        targetAccountingLine.setPayrollEndDateFiscalPeriodCode(OLDER_FISCAL_PER);
        targetAccountingLine.setAccount(account);

        stDoc.addTargetAccountingLine(targetAccountingLine);

        // modify error certification's state as filled out
        setUpErrorCertificationObject(true);

        validation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertFalse("Unexpected requirement of Error Certification Tab.", hasError);
    }

    /**
     * Test the case where the target accounting line is "older", has a subfund in the contribution approved
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter, and the error certification tab is incomplete.
     * This is based on the fiscal periods and the ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
     * The target accounting line will use the current year and will be set to OLDER_FISCAL_PER. It will also have a sub fund that is in
     * the contribution approved ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
     * The validation will use the UniversityDateService that is defined in this test class.
     */
    @SuppressWarnings("deprecation")
    public void testOlderSubFundTargetAccountingLine() {
        Account account = new Account();
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(PARM_SUBFUND);
        account.setSubFundGroup(subFundGroup);

        // create a target accounting line for testing in set object
        ExpenseTransferTargetAccountingLine targetAccountingLine = new ExpenseTransferTargetAccountingLine();
        targetAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        targetAccountingLine.setPayrollEndDateFiscalPeriodCode(OLDER_FISCAL_PER);
        targetAccountingLine.setAccount(account);

        stDoc.addTargetAccountingLine(targetAccountingLine);

        // modify error certification's state as partially filled out
        setUpErrorCertificationObject(false);

        validation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertTrue("Error Certification Tab isn't required, but should be.", hasError);
    }

    /**
     * Test the case where the target accounting line is from the previous fiscal year, has a subfund in the contribution approved
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter, and the error certification tab is incomplete.
     * This is based on the fiscal periods and the ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
     * The target accounting line will use the previous year and will be set to OLDER_FISCAL_PER. It will also have a sub fund that is
     * in the contribution approved ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
     * The validation will use the UniversityDateService that is defined in this test class.
     */
    @SuppressWarnings("deprecation")
    public void testPriorYearSubFundTargetAccountingLine() {
        Account account = new Account();
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(PARM_SUBFUND);
        account.setSubFundGroup(subFundGroup);

        // create a target accounting line for testing in set object
        ExpenseTransferTargetAccountingLine targetAccountingLine = new ExpenseTransferTargetAccountingLine();
        targetAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear() - 1);
        targetAccountingLine.setPayrollEndDateFiscalPeriodCode(OLDER_FISCAL_PER);
        targetAccountingLine.setAccount(account);

        stDoc.addTargetAccountingLine(targetAccountingLine);

        // modify error certification's state as partially filled out
        setUpErrorCertificationObject(false);

        validation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertTrue("Error Certification Tab isn't required, but should be.", hasError);
    }

    /**
     * Create errorCertification object for use in the test validations. Can specify whether the fields should be complete or not.
     *
     * @param isCompleted
     */
    public void setUpErrorCertificationObject(boolean isCompleted) {
        if (isCompleted) {
            errorCertification.setErrorCorrectionReason("test reason");
            errorCertification.setErrorDescription("test desc");
            errorCertification.setExpenditureDescription("test description");
            errorCertification.setExpenditureProjectBenefit("test benefit");
        }
        else {
            errorCertification.setErrorCorrectionReason("test reason");
            errorCertification.setErrorDescription("test desc");
            errorCertification.setExpenditureDescription("");
            errorCertification.setExpenditureProjectBenefit("");
        }
    }

    /**
     * Fake the current university date for this test to be in period 13.
     */
    static class MyUniversityDateService extends UniversityDateServiceImpl {
        @Override
        public UniversityDate getCurrentUniversityDate() {
            UniversityDate universityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
            universityDate.setUniversityFiscalAccountingPeriod("13");
            return universityDate;
        }
    }

    static class MyAttributedDocumentEvent extends AttributedDocumentEventBase {
        public MyAttributedDocumentEvent(SalaryExpenseTransferDocument d) {
            super("", "", d);
        }
    }
}
