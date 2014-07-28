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

import java.util.List;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.service.SalaryExpenseTransferTransactionAgeService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates Salary Expense Transfer document's accounting lines and error certification tab
 */
public class SalaryExpenseTransferErrorCertificationValidation extends GenericValidation {
    protected static ParameterService parameterService;
    protected static SalaryExpenseTransferTransactionAgeService salaryTransferTransactionAgeService;

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalaryExpenseTransferErrorCertificationValidation.class);

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validate accouting lines and error certification tab called");
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
        olderThanFiscalPeriods = !salaryTransferTransactionAgeService.defaultNumberOfFiscalPeriodsCheck(currDocument.getSourceAccountingLines(), defaultNumFiscalPeriods);
        if (LOG.isDebugEnabled()) {
            LOG.debug("olderThanFiscalPeriods for source accounting lines: " + olderThanFiscalPeriods);
        }

        if (!olderThanFiscalPeriods) {
            targetAcctLines = currDocument.getTargetAccountingLines();
            olderThanFiscalPeriods = !salaryTransferTransactionAgeService.defaultNumberOfFiscalPeriodsCheck(currDocument.getTargetAccountingLines(), defaultNumFiscalPeriods);
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
            GlobalVariables.getMessageMap().putErrorForSectionId(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY, LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_FISCAL_PER_PARAM_TRIGGERED);
        }

        return success;
    }

    /**
     * Gets the injected implementation of ParameterService to use.
     *
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
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
     * Gets the SalaryTransferTransactionAgeService.
     *
     * @return Returns the salaryTransferTransactionAgeService
     */
    public SalaryExpenseTransferTransactionAgeService getSalaryExpenseTransferTransactionAgeService() {
        return salaryTransferTransactionAgeService;
    }

    /**
     * Sets the salaryTransferTransactionAgeService attribute.
     *
     * @param salaryExpenseTransferTransactionAgeService
     */
    public void setSalaryExpenseTransferTransactionAgeService(SalaryExpenseTransferTransactionAgeService salaryExpenseTransferTransactionAgeService) {
        this.salaryTransferTransactionAgeService = salaryExpenseTransferTransactionAgeService;
    }
}
