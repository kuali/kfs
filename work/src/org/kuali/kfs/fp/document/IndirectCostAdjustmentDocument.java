/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.document;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.IndirectCostAdjustmentDocumentAccountingLineParser;
import org.kuali.module.financial.rules.IndirectCostAdjustmentDocumentRuleConstants;

public class IndirectCostAdjustmentDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {

    /**
     * Constructs a IndirectCostAdjustmentDocument.java.
     */
    public IndirectCostAdjustmentDocument() {
        super();
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.GRANT;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
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
     * @see org.kuali.kfs.document.AccountingDocumentBase#addSourceAccountingLine(SourceAccountingLine)
     */
    @Override
    public void addSourceAccountingLine(SourceAccountingLine line) {
        // add source
        super.addSourceAccountingLine(line);
        // create and populate target line
        TargetAccountingLine targetAccountingLine = null;
        try {
            targetAccountingLine = (TargetAccountingLine) getTargetAccountingLineClass().newInstance();
        }
        catch (Exception e) {
            throw new InfrastructureException("unable to create a target accounting line", e);
        }
        // get apc object code value
        String objectCode = SpringContext.getBean(ParameterService.class).getParameterValue(IndirectCostAdjustmentDocument.class, IndirectCostAdjustmentDocumentRuleConstants.RECEIPT_OBJECT_CODE);
        targetAccountingLine.setFinancialObjectCode(objectCode);
        targetAccountingLine.setAccountNumber(line.getAccount().getIndirectCostRecoveryAcctNbr());
        targetAccountingLine.setChartOfAccountsCode(line.getChartOfAccountsCode());
        targetAccountingLine.setDocumentNumber(line.getDocumentNumber());
        targetAccountingLine.setPostingYear(line.getPostingYear());
        targetAccountingLine.setAmount(line.getAmount());
        // refresh reference objects

        targetAccountingLine.refresh();
        // add target line
        addTargetAccountingLine(targetAccountingLine);
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new IndirectCostAdjustmentDocumentAccountingLineParser();
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
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) throws IllegalStateException {
        AccountingLine accountingLine = (AccountingLine)postable;
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (!(accountingLine.isSourceAccountingLine() && isDebitUtils.isExpense(accountingLine)) && !(accountingLine.isTargetAccountingLine() && isDebitUtils.isIncome(accountingLine))) {
            throw new IllegalStateException(isDebitUtils.getDebitCalculationIllegalStateExceptionMessage());
        }

        return isDebitUtils.isDebitConsideringType(this, accountingLine);
    }
}
