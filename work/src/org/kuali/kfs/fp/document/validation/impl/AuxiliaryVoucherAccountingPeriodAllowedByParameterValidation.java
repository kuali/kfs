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

import static org.kuali.kfs.KFSConstants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD;
import static org.kuali.kfs.KFSKeyConstants.AuxiliaryVoucher.ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_PERIOD_CODES;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;

/**
 * A validation for the Auxiliary Voucher document, this checks that the given accounting period on
 * the document is allowed by the associated system paramter.
 */
public class AuxiliaryVoucherAccountingPeriodAllowedByParameterValidation extends GenericValidation {
    private AuxiliaryVoucherDocument auxiliaryVoucherDocumentForValidation;
    private ParameterService parameterService;
    private AccountingPeriodService accountingPeriodService;

    /**
     * Using the KFS-FP / AuxiliaryVoucherDocument / RestrictedPeriodCodes parameter, checks that the accounting period specified on the document is valid.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        AccountingPeriod acctPeriod = getAccountingPeriodService().getByPeriod(auxiliaryVoucherDocumentForValidation.getPostingPeriodCode(), auxiliaryVoucherDocumentForValidation.getPostingYear());

        valid = getParameterService().getParameterEvaluator(AuxiliaryVoucherDocument.class, RESTRICTED_PERIOD_CODES, auxiliaryVoucherDocumentForValidation.getPostingPeriodCode()).evaluationSucceeds();
        if (!valid) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_PERIOD_STATUS_CODE_FIELD, ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE);
        }
        
        return valid;
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
    public void setAuxiliaryVoucherDocumentForValidation(AuxiliaryVoucherDocument auxiliaryVoucherDocumentForValidation) {
        this.auxiliaryVoucherDocumentForValidation = auxiliaryVoucherDocumentForValidation;
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
}
