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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class IndirectCostAdjustmentChartOfAccountsValidation extends GenericValidation {
    protected AccountingLine accountingLineForValidation;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostAdjustmentChartOfAccountsValidation.class);
    
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        AccountingLine accountingLine = getAccountingLineForValidation();

        if (accountingLine.isSourceAccountingLine()) {
            String icrExpense = accountingLine.getChart().getIcrExpenseFinancialObjectCd();
            isValid &= StringUtils.isNotBlank(icrExpense);
            if (!isValid) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_GRANT_INVALID_CHART_OF_ACCOUNTS, accountingLine.getChartOfAccountsCode());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("rule failure: " + KFSKeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_GRANT_INVALID_CHART_OF_ACCOUNTS + " / " + accountingLine.getChartOfAccountsCode() );
                }
            }
        }
        else {
            String icrIncome = accountingLine.getChart().getIcrIncomeFinancialObjectCode();
            isValid &= StringUtils.isNotBlank(icrIncome);
            if (!isValid) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_RECEIPT_INVALID_CHART_OF_ACCOUNTS, accountingLine.getChartOfAccountsCode());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("rule failure: " + KFSKeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_RECEIPT_INVALID_CHART_OF_ACCOUNTS + " / " + accountingLine.getChartOfAccountsCode() );
                }
            }
        }

        return isValid;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
    
}
