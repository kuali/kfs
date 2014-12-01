/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.businessobject.BudgetAdjustmentAccountingLine;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates an accounting line on a budget adjustment document whether the base amount on the line can be changed or not
 */
public class BudgetAdjustmentAccountingLineBaseAmountValidation extends GenericValidation {
    private BudgetAdjustmentDocument accountingDocumentForValidation;
    private BudgetAdjustmentAccountingLine accountingLineForValidation;
    private FiscalYearFunctionControlService fiscalYearFunctionControlService;

    /**
     * Validate that, if a base amount is entered for a line, that it can be adjusted for the posting year
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean allow = true;
        if (getAccountingLineForValidation().getBaseBudgetAdjustmentAmount().isNonZero() && !fiscalYearFunctionControlService.isBaseAmountChangeAllowed(getAccountingDocumentForValidation().getPostingYear())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNT_CHANGE_NOT_ALLOWED);
            allow = false;
        }
        return allow;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public BudgetAdjustmentAccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(BudgetAdjustmentAccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Gets the fiscalYearFunctionControlService attribute. 
     * @return Returns the fiscalYearFunctionControlService.
     */
    public FiscalYearFunctionControlService getFiscalYearFunctionControlService() {
        return fiscalYearFunctionControlService;
    }

    /**
     * Sets the fiscalYearFunctionControlService attribute value.
     * @param fiscalYearFunctionControlService The fiscalYearFunctionControlService to set.
     */
    public void setFiscalYearFunctionControlService(FiscalYearFunctionControlService fiscalYearFunctionControlService) {
        this.fiscalYearFunctionControlService = fiscalYearFunctionControlService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public BudgetAdjustmentDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(BudgetAdjustmentDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
