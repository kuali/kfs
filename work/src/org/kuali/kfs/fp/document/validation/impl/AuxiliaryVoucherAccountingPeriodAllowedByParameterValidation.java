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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.fp.document.validation.impl.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_PERIOD_CODES;
import static org.kuali.kfs.sys.KFSConstants.ACCOUNTING_PERIOD_ACTIVE_INDICATOR_FIELD;
import static org.kuali.kfs.sys.KFSKeyConstants.AuxiliaryVoucher.ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.fp.document.AuxiliaryVoucherDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

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
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        AccountingPeriod acctPeriod = getAccountingPeriodService().getByPeriod(auxiliaryVoucherDocumentForValidation.getPostingPeriodCode(), auxiliaryVoucherDocumentForValidation.getPostingYear());

        valid = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(AuxiliaryVoucherDocument.class, RESTRICTED_PERIOD_CODES, auxiliaryVoucherDocumentForValidation.getPostingPeriodCode()).evaluationSucceeds();
        if (!valid) {
            GlobalVariables.getMessageMap().putError(ACCOUNTING_PERIOD_ACTIVE_INDICATOR_FIELD, ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE);
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
