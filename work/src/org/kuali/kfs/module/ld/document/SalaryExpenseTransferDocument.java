/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Labor Document Class for the Salary Expense Transfer Document.
 */
public class SalaryExpenseTransferDocument extends LaborExpenseTransferDocumentBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SalaryExpenseTransferDocument.class);

    private Map<String, KualiDecimal> approvalObjectCodeBalances;

    /**
     * Default Constructor.
     */
    public SalaryExpenseTransferDocument() {
        super();
        approvalObjectCodeBalances = new HashMap<String, KualiDecimal>();
    }

    /**
     * Gets the approvalObjectCodeBalances attribute.
     * 
     * @return Returns the approvalObjectCodeBalances.
     */
    public Map<String, KualiDecimal> getApprovalObjectCodeBalances() {
        return approvalObjectCodeBalances;
    }

    /**
     * Sets the approvalObjectCodeBalances attribute value.
     * 
     * @param approvalObjectCodeBalances The approvalObjectCodeBalances to set.
     */
    public void setApprovalObjectCodeBalances(Map<String, KualiDecimal> approvalObjectCodeBalances) {
        this.approvalObjectCodeBalances = approvalObjectCodeBalances;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase#generateLaborLedgerPendingEntries(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateLaborLedgerPendingEntries(AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("started generateLaborLedgerPendingEntries()");
        
        boolean isSuccessful = true;
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        List<LaborLedgerPendingEntry> expensePendingEntries = LaborPendingEntryGenerator.generateExpensePendingEntries(this, expenseTransferAccountingLine, sequenceHelper);
        if (expensePendingEntries != null && !expensePendingEntries.isEmpty()) {
            isSuccessful &= this.getLaborLedgerPendingEntries().addAll(expensePendingEntries);
        }

        List<LaborLedgerPendingEntry> benefitPendingEntries = LaborPendingEntryGenerator.generateBenefitPendingEntries(this, expenseTransferAccountingLine, sequenceHelper);
        if (benefitPendingEntries != null && !benefitPendingEntries.isEmpty()) {
            isSuccessful &= this.getLaborLedgerPendingEntries().addAll(benefitPendingEntries);
        }

        return isSuccessful;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase#generateLaborLedgerBenefitClearingPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateLaborLedgerBenefitClearingPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("started generateLaborLedgerBenefitClearingPendingEntries()");

        String chartOfAccountsCode = SpringContext.getBean(ParameterService.class).getParameterValue(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.BENEFIT_CLEARING_CHART_PARM_NM);
        String accountNumber = SpringContext.getBean(ParameterService.class).getParameterValue(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.BENEFIT_CLEARING_ACCOUNT_PARM_NM);

        List<LaborLedgerPendingEntry> benefitClearingPendingEntries = LaborPendingEntryGenerator.generateBenefitClearingPendingEntries(this, sequenceHelper, accountNumber, chartOfAccountsCode);

        if (benefitClearingPendingEntries != null && !benefitClearingPendingEntries.isEmpty()) {
            return this.getLaborLedgerPendingEntries().addAll(benefitClearingPendingEntries);
        }

        return true;
    }
}
