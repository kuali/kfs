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

import static org.kuali.kfs.module.ld.document.validation.impl.SalaryExpenseTransferDocumentRuleConstants.DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED;
import static org.kuali.kfs.module.ld.document.validation.impl.SalaryExpenseTransferDocumentRuleConstants.ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND;

import java.util.List;

import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates Salary Expense Transfer document's accounting lines and error certification tab
 */
public class SalaryExpenseTransferErrorCertificationValidation extends GenericValidation {
    private static ParameterService parameterService;
    private static UniversityDateService universityDateService;

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalaryExpenseTransferErrorCertificationValidation.class);

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validate error certification tab called");
        }

        SalaryExpenseTransferDocument currDocument = (SalaryExpenseTransferDocument) event.getDocument();
        boolean success = true;
        boolean olderThanFiscalPeriods = false;
        int numBlankFields = 0;
        List<ExpenseTransferSourceAccountingLine> sourceAcctLines;
        List<ExpenseTransferTargetAccountingLine> targetAcctLines;

        int defaultNumFiscalPeriods = Integer.parseInt(parameterService.getParameterValueAsString(KfsParameterConstants.LABOR_DOCUMENT.class, DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED));
        if (LOG.isDebugEnabled()) {
            LOG.debug("defaultPeriodsFromParameter: " + defaultNumFiscalPeriods);
        }

        // check fiscal periods and the system parameters involving age of transaction
        sourceAcctLines = currDocument.getSourceAccountingLines();
        olderThanFiscalPeriods = !defaultNumberOfFiscalPeriodsCheck(currDocument.getSourceAccountingLines(), defaultNumFiscalPeriods);
        if (LOG.isDebugEnabled()) {
            LOG.debug("olderThanFiscalPeriods for source accounting lines: " + olderThanFiscalPeriods);
        }

        if (!olderThanFiscalPeriods) {
            targetAcctLines = currDocument.getTargetAccountingLines();
            olderThanFiscalPeriods = !defaultNumberOfFiscalPeriodsCheck(currDocument.getTargetAccountingLines(), defaultNumFiscalPeriods);
            if (LOG.isDebugEnabled()) {
                LOG.debug("olderThanFiscalPeriods for target accounting lines: " + olderThanFiscalPeriods);
            }
        }

        // check for blank fields in the Error Certification Tab
        ErrorCertificationValidation ecValidation = new ErrorCertificationValidation();
        numBlankFields = ecValidation.errorCertificationBlankFieldsCheck(currDocument);
        if (LOG.isDebugEnabled()) {
            LOG.debug("numBlankFields: " + numBlankFields);
        }

        // if there is a transaction older than DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED and Error Certification tab is partially filled
        if (olderThanFiscalPeriods && ((numBlankFields > 0) && (numBlankFields <= LaborConstants.ErrorCertification.NUM_ERROR_CERT_FIELDS))) {
            GlobalVariables.getMessageMap().putErrorForSectionId(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY, LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_FISCAL_PER_PARAM_TRIGGERED, "error.errorCert.fiscalPerParamTriggered");
        }

        return success;
    }

    /**
     * Determines if the age of the transactions are older than the value in the parameter. By default, use the fiscal periods in
     * periodsFromParameter. If a target accounting line, check account's sub fund and maybe use
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
     * <ol>
     * <li>Loop through {@link ExpenseTransferTargetAccountingLine} instances in the {@link SalaryExpenseTransferDocument}.</li>
     * <li>Get fiscal year and fiscal period of each line.</li>
     * <li>Check sub fund of each line to possibly reassign periodsFromParameter.</li>
     * </ol>
     *
     * @param accountingLines
     * @param periodsFromParameter
     * @return true if all of the transaction dates are younger by fiscal periods than specified in the appropriate parameter; false
     *         otherwise
     */
    protected boolean defaultNumberOfFiscalPeriodsCheck(List<LaborLedgerExpenseTransferAccountingLine> accountingLines, Integer periodsFromParameter) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("in defaultNumberOfFiscalPeriodsCheck");
        }

        Integer currPayrollEndDateFiscalYear;
        String currPayrollEndDateFiscalPeriodCode;
        UniversityDate currUnivDate = universityDateService.getCurrentUniversityDate();
        Integer currFiscalYear = currUnivDate.getUniversityFiscalYear();
        String currFiscalPeriod = currUnivDate.getUniversityFiscalAccountingPeriod();
        if (LOG.isDebugEnabled()) {
            LOG.debug("currFiscalPeriod: " + currFiscalPeriod);
        }

        for (LaborLedgerExpenseTransferAccountingLine currentLine : accountingLines) {
            currPayrollEndDateFiscalYear = currentLine.getPayrollEndDateFiscalYear();
            if (LOG.isDebugEnabled()) {
                LOG.debug("target line fiscal year: " + currPayrollEndDateFiscalYear);
            }

            currPayrollEndDateFiscalPeriodCode = currentLine.getPayrollEndDateFiscalPeriodCode();
            if (LOG.isDebugEnabled()) {
                LOG.debug("target line fiscal period: " + currPayrollEndDateFiscalPeriodCode);
            }

            // check sub fund associated with the target accounting line
            if (currentLine instanceof ExpenseTransferTargetAccountingLine) {
                periodsFromParameter = checkCurrentSubFund(periodsFromParameter, (ExpenseTransferTargetAccountingLine) currentLine);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("periodsFromParameter: " + periodsFromParameter);
                }
            }

            if (periodsFromParameter != null) {
                if (currPayrollEndDateFiscalYear < currFiscalYear) { // if in different FY, return false immediately
                    return false;
                }

                // if fiscal period is more than periodsFromParameter older than the transaction being moved, return false
                if (Integer.valueOf(currFiscalPeriod) - (Integer.valueOf(currPayrollEndDateFiscalPeriodCode)) >= periodsFromParameter) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method checks the sub fund associated with the account in a target accounting line. If sub fund is in
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter, use different # of fiscal periods
     *
     * @param periodsFromParameter initial periods from a parameter
     * @param currentTargetLine
     * @return the periodsFromParameter, which may have value in ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND
     */
    protected Integer checkCurrentSubFund(Integer periodsFromParameter, ExpenseTransferTargetAccountingLine currentTargetLine) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("in checkCurrentSubFund");
        }
        SubFundGroup subFundGroup = currentTargetLine.getAccount().getSubFundGroup();
        String subFundGroupCode = subFundGroup.getSubFundGroupCode();
        String newComparePeriods = parameterService.getSubParameterValueAsString(KfsParameterConstants.LABOR_DOCUMENT.class, ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND, subFundGroupCode);

        if (newComparePeriods != null) {
            periodsFromParameter = new Integer(newComparePeriods);
        }

        return periodsFromParameter;
    }

    /**
     * Gets the injected implementation of ParameterService to use.
     *
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }

        return parameterService;
    }

    /**
     * Sets the parameterService attribute.
     *
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the universityDateService attribute.
     *
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        if (universityDateService == null) {
            universityDateService = SpringContext.getBean(UniversityDateService.class);
        }

        return universityDateService;
    }

    /**
     * Sets the universityDateService attribute value.
     *
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
}
