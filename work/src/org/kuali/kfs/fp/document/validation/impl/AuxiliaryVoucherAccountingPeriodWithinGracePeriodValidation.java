/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.document.validation.impl;

import static org.kuali.kfs.KFSConstants.ACCOUNTING_PERIOD_STATUS_CLOSED;
import static org.kuali.kfs.KFSConstants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD;
import static org.kuali.kfs.KFSConstants.DOCUMENT_ERRORS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC;
import static org.kuali.kfs.KFSKeyConstants.AuxiliaryVoucher.ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.AUXILIARY_VOUCHER_ACCOUNTING_PERIOD_GRACE_PERIOD;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_PERIOD_CODES;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRule;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * Validation for Auxiliary Voucher documents that tests whether the accounting period for the document is within the defined grace period.
 */
public class AuxiliaryVoucherAccountingPeriodWithinGracePeriodValidation extends GenericValidation {
    private AuxiliaryVoucherDocument auxiliaryVoucherDocumentForValidation;
    private AccountingPeriodService accountingPeriodService;
    private UniversityDateService universityDateService;
    private ParameterService parameterService;

    /**
     * A validation to check if the given accounting period is within the "grace period" of the AV doc, defined in JIRA KULRNE-4634.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        /*
         * Nota bene: a full summarization of these rules can be found in the comments to KULRNE-4634
         */
        // first we need to get the period itself to check these things
        boolean valid = true;
        AccountingPeriod acctPeriod = getAccountingPeriodService().getByPeriod(getAuxiliaryVoucherDocumentForValidation().getPostingPeriodCode(), getAuxiliaryVoucherDocumentForValidation().getPostingYear());        

        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        AccountingPeriod currPeriod = getAccountingPeriodService().getByDate(new Date(ts.getTime()));

        if (acctPeriod.getUniversityFiscalYear().equals(getUniversityDateService().getCurrentFiscalYear())) {
            if (getAccountingPeriodService().compareAccountingPeriodsByDate(acctPeriod, currPeriod) < 0) {
                // we've only got problems if the av's accounting period is earlier than now

                // are we in the grace period for this accounting period?
                if (!calculateIfWithinGracePeriod(new Date(ts.getTime()), acctPeriod)) {
                    GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                    return false;
                }
            }
        }
        else {
            // it's not the same fiscal year, so we need to test whether we are currently
            // in the grace period of the acctPeriod
            if (!calculateIfWithinGracePeriod(new Date(ts.getTime()), acctPeriod) && isEndOfPreviousFiscalYear(acctPeriod)) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
        }

        return valid;
    }
    
    /**
     * This method checks if a given moment of time is within an accounting period, or its auxiliary voucher grace period.
     * 
     * @param today a date to check if it is within the period
     * @param periodToCheck the account period to check against
     * @return true if a given moment in time is within an accounting period or an auxiliary voucher grace period
     */
    public boolean calculateIfWithinGracePeriod(Date today, AccountingPeriod periodToCheck) {
        boolean result = false;
        int todayAsComparableDate = AuxiliaryVoucherDocumentRule.comparableDateForm(today);
        int periodClose = new Integer(AuxiliaryVoucherDocumentRule.comparableDateForm(periodToCheck.getUniversityFiscalPeriodEndDate()));
        int periodBegin = AuxiliaryVoucherDocumentRule.comparableDateForm(calculateFirstDayOfMonth(periodToCheck.getUniversityFiscalPeriodEndDate()));
        int gracePeriodClose = periodClose + new Integer(getParameterService().getParameterValue(AuxiliaryVoucherDocument.class, AUXILIARY_VOUCHER_ACCOUNTING_PERIOD_GRACE_PERIOD)).intValue();
        return (todayAsComparableDate >= periodBegin && todayAsComparableDate <= gracePeriodClose);
    }
    /**
     * This method returns a date as an approximate count of days since the BCE epoch.
     * 
     * @param d the date to convert
     * @return an integer count of days, very approximate
     */
    public int comparableDateForm(Date d) {
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(d);
        return cal.get(java.util.Calendar.YEAR) * 365 + cal.get(java.util.Calendar.DAY_OF_YEAR);
    }

    /**
     * Given a day, this method calculates what the first day of that month was.
     * 
     * @param d date to find first of month for
     * @return date of the first day of the month
     */
    public Date calculateFirstDayOfMonth(Date d) {
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(d);
        int dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH) - 1;
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1 * dayOfMonth);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * This method checks if the given accounting period ends on the last day of the previous fiscal year
     * 
     * @param acctPeriod accounting period to check
     * @return true if the accounting period ends with the fiscal year, false if otherwise
     */
    public boolean isEndOfPreviousFiscalYear(AccountingPeriod acctPeriod) {
        Date firstDayOfCurrFiscalYear = new Date(getUniversityDateService().getFirstDateOfFiscalYear(getUniversityDateService().getCurrentFiscalYear()).getTime());
        Date periodClose = acctPeriod.getUniversityFiscalPeriodEndDate();
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(periodClose);
        cal.add(java.util.Calendar.DATE, 1);
        return (firstDayOfCurrFiscalYear.equals(new Date(cal.getTimeInMillis())));
    }

    /**
     * Gets the auxiliaryVoucherDocumentForValidation attribute. 
     * @return Returns the auxiliaryVoucherDocumentForValidation.
     */
    public AuxiliaryVoucherDocument getAuxiliaryVoucherDocumentForValidation() {
        return auxiliaryVoucherDocumentForValidation;
    }

    /**
     * Sets the auxiliaryVoucherDocumentForValidation attribute value.
     * @param auxiliaryVoucherDocumentForValidation The auxiliaryVoucherDocumentForValidation to set.
     */
    public void setAuxiliaryVoucherDocumentForValidation(AuxiliaryVoucherDocument accountingDocumentForValidation) {
        this.auxiliaryVoucherDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingPeriodService attribute. 
     * @return Returns the accountingPeriodService.
     */
    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    /**
     * Sets the accountingPeriodService attribute value.
     * @param accountingPeriodService The accountingPeriodService to set.
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    /**
     * Gets the universityDateService attribute. 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    /**
     * Sets the universityDateService attribute value.
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
