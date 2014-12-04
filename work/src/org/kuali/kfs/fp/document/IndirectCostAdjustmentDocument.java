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
package org.kuali.kfs.fp.document;

import java.math.BigDecimal;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.fp.document.validation.impl.IndirectCostAdjustmentDocumentRuleConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class IndirectCostAdjustmentDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {

    /**
     * Constructs a IndirectCostAdjustmentDocument.java.
     */
    public IndirectCostAdjustmentDocument() {
        super();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.GRANT;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.ICR;
    }

    /**
     * per ICA specs, adds a target(receipt) line when an source(grant) line is added using the following logic to populate the
     * target line.
     * <ol>
     * <li>receipt line's chart = chart from grant line
     * <li>receipt line's account = ICR account for the account entered on the grant line
     * <li>receipt line's object code = Financial System Parameter APC for the document global receipt line object code (see APC
     * setion below)
     * <li>receipt line's amount = amount from grant line
     * </ol>
     *
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#addSourceAccountingLine(SourceAccountingLine)
     */
    @Override
    public void addSourceAccountingLine(SourceAccountingLine line) {
        // add source
        super.addSourceAccountingLine(line);

        if (GlobalVariables.getMessageMap().hasNoMessages()) {
            if  (line != null && ObjectUtils.isNotNull(line.getAccount()) && line.getAccount().getActiveIndirectCostRecoveryAccounts() != null && line.getAccount().getActiveIndirectCostRecoveryAccounts().size() > 0) {
                for (IndirectCostRecoveryAccount icrAccount : line.getAccount().getActiveIndirectCostRecoveryAccounts()) {

                    KualiDecimal percentDecimal = new KualiDecimal(icrAccount.getAccountLinePercent().divide(new BigDecimal(100)));

                    // create and populate target line
                    TargetAccountingLine targetAccountingLine = null;
                    try {
                        targetAccountingLine = (TargetAccountingLine) getTargetAccountingLineClass().newInstance();
                    }
                    catch (Exception e) {
                        throw new InfrastructureException("unable to create a target accounting line", e);
                    }
                    // get apc object code value
                    String objectCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(IndirectCostAdjustmentDocument.class, IndirectCostAdjustmentDocumentRuleConstants.RECEIPT_OBJECT_CODE);
                    targetAccountingLine.setFinancialObjectCode(objectCode);
                    targetAccountingLine.setAccountNumber(icrAccount.getIndirectCostRecoveryAccountNumber());
                    targetAccountingLine.setChartOfAccountsCode(icrAccount.getIndirectCostRecoveryFinCoaCode());
                    targetAccountingLine.setDocumentNumber(line.getDocumentNumber());
                    targetAccountingLine.setPostingYear(line.getPostingYear());
                    targetAccountingLine.setAmount(line.getAmount().multiply(percentDecimal));
                    // refresh reference objects

                    targetAccountingLine.refresh();
                    // add target line
                    addTargetAccountingLine(targetAccountingLine);
                }
            }
        }
    }

    /**
     * Same logic as <code>IsDebitUtils#isDebitConsideringType(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)</code>
     * but has the following accounting line restrictions:
     *
     * for grant lines(source):
     * <ol>
     * <li>only allow expense object type codes
     * </ol>
     * for receipt lines(target):
     * <ol>
     * <li>only allow income object type codes
     * </ol>
     *
     * @param transactionDocument The document associated with the accounting line being reviewed to determine if it's a debit.
     * @param accountingLine The accounting line being reviewed to determine if it's a debit line.
     * @return True if the accounting line is a debit.  See IsDebitUtils.isDebitConsideringType().
     * @throws IllegalStateException Thrown if the accounting line given is a source accounting line representing an expense
     * or is a target accounting line representing an income.
     *
     * @see IsDebitUtils#isDebitConsideringType(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) throws IllegalStateException {
        AccountingLine accountingLine = (AccountingLine)postable;
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (!(accountingLine.isSourceAccountingLine() && isDebitUtils.isExpense(accountingLine)) && !(accountingLine.isTargetAccountingLine() && isDebitUtils.isIncome(accountingLine))) {
            throw new IllegalStateException(isDebitUtils.getDebitCalculationIllegalStateExceptionMessage());
        }

        return isDebitUtils.isDebitConsideringType(this, accountingLine);
    }
}
