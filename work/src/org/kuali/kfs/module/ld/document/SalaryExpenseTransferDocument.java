/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.ld.document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;


/**
 * Labor Document Class for the Salary Expense Transfer Document.
 */
public class SalaryExpenseTransferDocument extends LaborExpenseTransferDocumentBase {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SalaryExpenseTransferDocument.class);

    protected Map<String, KualiDecimal> approvalObjectCodeBalances;

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
    @Override
    public boolean generateLaborLedgerPendingEntries(AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("started generateLaborLedgerPendingEntries()");

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
    @Override
    public boolean generateLaborLedgerBenefitClearingPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("started generateLaborLedgerBenefitClearingPendingEntries()");

        String chartOfAccountsCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.BENEFIT_CLEARING_CHART_PARM_NM);
        String accountNumber = SpringContext.getBean(ParameterService.class).getParameterValueAsString(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.BENEFIT_CLEARING_ACCOUNT_PARM_NM);

        List<LaborLedgerPendingEntry> benefitClearingPendingEntries = LaborPendingEntryGenerator.generateBenefitClearingPendingEntries(this, sequenceHelper, accountNumber, chartOfAccountsCode);

        if (benefitClearingPendingEntries != null && !benefitClearingPendingEntries.isEmpty()) {
            return this.getLaborLedgerPendingEntries().addAll(benefitClearingPendingEntries);
        }

        return true;
    }

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        // KFSMI-4606 added routeNode condition
        if (nodeName.equals(KFSConstants.REQUIRES_WORKSTUDY_REVIEW)) {
            return checkOjbectCodeForWorkstudy();
        }
        else {
            return super.answerSplitNodeQuestion(nodeName);
        }
    }

    /**
     * KFSMI-4606 check routeNode condition
     * @return boolean
     */
    protected boolean checkOjbectCodeForWorkstudy(){
        Collection<String> workstudyRouteObjectcodes = SpringContext.getBean(ParameterService.class).getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_DOCUMENT.class, KFSConstants.WORKSTUDY_ROUTE_OBJECT_CODES_PARM_NM);

        List<SourceAccountingLine> sourceAccountingLines = getSourceAccountingLines();
        List<TargetAccountingLine> targetAccountingLines = getTargetAccountingLines();

        // check object code in source and target accounting lines
        for (SourceAccountingLine sourceLine : sourceAccountingLines){
            if (workstudyRouteObjectcodes.contains(sourceLine.getFinancialObjectCode())) {
                return true;
            }
        }

        for (TargetAccountingLine targetLine : targetAccountingLines){
            if (workstudyRouteObjectcodes.contains(targetLine.getFinancialObjectCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * KFSMI-4606 Set GLPE descriptions to persons name. Take care that this needs to overwrite prepareForSave so that it
     * catches pending entries generated by generateLaborLedgerPendingEntries and generateLaborLedgerBenefitClearingPendingEntries.
     * @see org.kuali.kfs.module.ld.document.LaborLedgerPostingDocumentBase#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);

        for (Iterator<LaborLedgerPendingEntry> iterator = this.getLaborLedgerPendingEntries().iterator(); iterator.hasNext();) {
            LaborLedgerPendingEntry laborLedgerPendingEntry = iterator.next();

            String personName = SpringContext.getBean(FinancialSystemUserService.class).getPersonNameByEmployeeId(this.getEmplid());

            // Get the maxlength of the description field we are setting
            BusinessObjectEntry laborLedgerPendingEntryBusinessObjectEntry = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(LaborLedgerPendingEntry.class.getName());
            AttributeDefinition laborLedgerPendingEntryAttribute = laborLedgerPendingEntryBusinessObjectEntry.getAttributeDefinition(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
            int descriptionLength = laborLedgerPendingEntryAttribute.getMaxLength();

            // Set the description field truncating name if necessary
            laborLedgerPendingEntry.setTransactionLedgerEntryDescription(personName.length() > descriptionLength ? personName.substring(0, descriptionLength - 1) : personName);
        }
    }

    @Override
    public List getLaborLedgerPendingEntriesForSearching() {
        return super.getLaborLedgerPendingEntries();
    }

}
