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

import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * A validation which checks if a Budget Adjustment document is balanced before heading to routing
 */
public class BudgetAdjustmentDocumentBalancedValidation extends GenericValidation {
    public BudgetAdjustmentDocument accountingDocumentForValidation;

    /**
     * Validates that the budget adjustment document is balanced, based on whether the source base amount equals the target base amount
     * and that the income stream balance map has no non-zero values.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        MessageMap errors = GlobalVariables.getMessageMap();

        boolean balanced = true;

        // check base amounts are equal
        //KFSMI-3036
        KualiInteger sourceBaseBudgetTotal = getAccountingDocumentForValidation().getSourceBaseBudgetIncomeTotal().subtract( getAccountingDocumentForValidation().getSourceBaseBudgetExpenseTotal());
        KualiInteger targetBaseBudgetTotal = getAccountingDocumentForValidation().getTargetBaseBudgetIncomeTotal().subtract( getAccountingDocumentForValidation().getTargetBaseBudgetExpenseTotal());
        if (sourceBaseBudgetTotal.compareTo(targetBaseBudgetTotal) != 0) {
            GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNTS_BALANCED);
            balanced = false;
        }

        // check current amounts balance, income stream balance Map should add to 0
        Map incomeStreamMap = getAccountingDocumentForValidation().buildIncomeStreamBalanceMapForDocumentBalance();
        KualiDecimal totalCurrentAmount = new KualiDecimal(0);
        for (Iterator iter = incomeStreamMap.values().iterator(); iter.hasNext();) {
            KualiDecimal streamAmount = (KualiDecimal) iter.next();
            totalCurrentAmount = totalCurrentAmount.add(streamAmount);
        }

        if (totalCurrentAmount.isNonZero()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_CURRENT_AMOUNTS_BALANCED);
            balanced = false;
        }

        return balanced;
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
