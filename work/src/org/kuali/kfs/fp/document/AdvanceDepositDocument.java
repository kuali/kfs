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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.ElectronicPaymentClaim;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.service.GeneralLedgerPostingHelper;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.AdvanceDepositDetail;

/**
 * This is the business object that represents the AdvanceDeposit document in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since an Advance Deposit document is a one sided
 * transactional document, only accepting funds into the university, the accounting line data will be held in the source accounting
 * line data structure only.
 */
public class AdvanceDepositDocument extends CashReceiptFamilyBase implements Copyable, AmountTotaling {
    // holds details about each advance deposit
    private List<AdvanceDepositDetail> advanceDeposits = new ArrayList<AdvanceDepositDetail>();

    // incrementers for detail lines
    private Integer nextAdvanceDepositLineNumber = 1;

    // monetary attributes
    private KualiDecimal totalAdvanceDepositAmount = KualiDecimal.ZERO;
    
    // name of the electronic payment claim account parameter
    private static final String ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER = "ELECTRONIC_FUNDS_ACCOUNTS";

    /**
     * Default constructor that calls super.
     */
    public AdvanceDepositDocument() {
        super();
    }

    /**
     * Gets the total advance deposit amount.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTotalAdvanceDepositAmount() {
        return totalAdvanceDepositAmount;
    }

    /**
     * This method returns the advance deposit total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalAdvanceDepositAmount() {
        return (String) new CurrencyFormatter().format(totalAdvanceDepositAmount);
    }

    /**
     * Sets the total advance deposit amount which is the sum of all advance deposits on this document.
     * 
     * @param advanceDepositAmount
     */
    public void setTotalAdvanceDepositAmount(KualiDecimal advanceDepositAmount) {
        this.totalAdvanceDepositAmount = advanceDepositAmount;
    }

    /**
     * Gets the list of advance deposits which is a list of AdvanceDepositDetail business objects.
     * 
     * @return List
     */
    public List<AdvanceDepositDetail> getAdvanceDeposits() {
        return advanceDeposits;
    }

    /**
     * Sets the advance deposits list.
     * 
     * @param advanceDeposits
     */
    public void setAdvanceDeposits(List<AdvanceDepositDetail> advanceDeposits) {
        this.advanceDeposits = advanceDeposits;
    }

    /**
     * Adds a new advance deposit to the list.
     * 
     * @param advanceDepositDetail
     */
    public void addAdvanceDeposit(AdvanceDepositDetail advanceDepositDetail) {
        // these three make up the primary key for an advance deposit detail record
        prepareNewAdvanceDeposit(advanceDepositDetail);

        // add the new detail record to the list
        this.advanceDeposits.add(advanceDepositDetail);

        // increment line number
        this.nextAdvanceDepositLineNumber++;

        // update the overall amount
        this.totalAdvanceDepositAmount = this.totalAdvanceDepositAmount.add(advanceDepositDetail.getFinancialDocumentAdvanceDepositAmount());
    }

    /**
     * This is a helper method that automatically populates document specfic information into the advance deposit
     * (AdvanceDepositDetail) instance.
     * 
     * @param advanceDepositDetail
     */
    public final void prepareNewAdvanceDeposit(AdvanceDepositDetail advanceDepositDetail) {
        advanceDepositDetail.setFinancialDocumentLineNumber(this.nextAdvanceDepositLineNumber);
        advanceDepositDetail.setFinancialDocumentColumnTypeCode(KFSConstants.AdvanceDepositConstants.CASH_RECEIPT_ADVANCE_DEPOSIT_COLUMN_TYPE_CODE);
        advanceDepositDetail.setDocumentNumber(this.getDocumentNumber());
        advanceDepositDetail.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(this.getClass()));
    }

    /**
     * Retrieve a particular advance deposit at a given index in the list of advance deposits.
     * 
     * @param index
     * @return AdvanceDepositDetail
     */
    public AdvanceDepositDetail getAdvanceDepositDetail(int index) {
        while (this.advanceDeposits.size() <= index) {
            advanceDeposits.add(new AdvanceDepositDetail());
        }
        return advanceDeposits.get(index);
    }

    /**
     * This method removes an advance deposit from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeAdvanceDeposit(int index) {
        AdvanceDepositDetail advanceDepositDetail = advanceDeposits.remove(index);
        this.totalAdvanceDepositAmount = this.totalAdvanceDepositAmount.subtract(advanceDepositDetail.getFinancialDocumentAdvanceDepositAmount());
    }

    /**
     * @return Integer
     */
    public Integer getNextAdvanceDepositLineNumber() {
        return nextAdvanceDepositLineNumber;
    }

    /**
     * @param nextAdvanceDepositLineNumber
     */
    public void setNextAdvanceDepositLineNumber(Integer nextAdvanceDepositLineNumber) {
        this.nextAdvanceDepositLineNumber = nextAdvanceDepositLineNumber;
    }

    /**
     * This method returns the overall total of the document - the advance deposit total.
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return this.totalAdvanceDepositAmount;
    }

    /**
     * This method defers to its parent's version of handleRouteStatusChange, but then, if the document is processed, it creates ElectronicPaymentClaim records
     * for any qualifying accountings lines in the document.
     * 
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            Map<String, List<String>> electronicFundAccounts = getElectronicFundAccounts();
            for (Object accountingLineAsObj: getSourceAccountingLines()) {
                AccountingLine accountingLine = (AccountingLine)accountingLineAsObj;
                List<String> electronicFundsAccountNumbers = electronicFundAccounts.get(accountingLine.getChartOfAccountsCode());
                if (electronicFundsAccountNumbers != null && electronicFundsAccountNumbers.contains(accountingLine.getAccountNumber())) {
                    ElectronicPaymentClaim electronicPayment = createElectronicPayment(accountingLine);
                    boService.save(electronicPayment);
                }
            }
        }
    }
    
    /**
     * This method uses the ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER to find which accounts should cause an accounting line to create an ElectronicPaymentClaim record.
     * @return a List of Maps, where each Map represents an account that electronic funds are posted to.  Each Map has a chart of accounts code as a key and a List of account numbers as a value.
     */
    private Map<String, List<String>> getElectronicFundAccounts() {
        Map<String, List<String>> electronicFundAccounts = new HashMap<String, List<String>>();
        String electronicPaymentAccounts = SpringContext.getBean(ParameterService.class).getParameterValue(AdvanceDepositDocument.class, ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER);
        
        String[] chartSections = electronicPaymentAccounts.split(";");
        for (String chartSection: chartSections) {
            String[] chartAccountPieces = chartSection.split("=");
            if (chartAccountPieces.length >= 2) {
                String chartCode = chartAccountPieces[0];
                if (chartCode != null && chartCode.length() > 0) {
                    String[] accountNumbers = chartAccountPieces[1].split(",");
                    List<String> accountNumbersForChart = electronicFundAccounts.get(chartCode);
                    if (accountNumbersForChart == null) {
                        accountNumbersForChart = new ArrayList<String>();
                    }
                    for (String accountNumber: accountNumbers) {
                        if (accountNumber != null && accountNumber.length() > 0) {
                            accountNumbersForChart.add(accountNumber);
                        }
                    }
                    electronicFundAccounts.put(chartCode, accountNumbersForChart);
                }
            }
        }
        return electronicFundAccounts;
    }
    
    /**
     * Creates an electronic payment claim record to match the given accounting line on the document
     * @param accountingLine an accounting line that an electronic payment claim record should be created for
     * @return the created ElectronicPaymentClaim business object
     */
    private ElectronicPaymentClaim createElectronicPayment(AccountingLine accountingLine) {
        ElectronicPaymentClaim electronicPayment = new ElectronicPaymentClaim();
        electronicPayment.setDocumentNumber(getDocumentNumber());
        electronicPayment.setFinancialDocumentLineNumber(accountingLine.getSequenceNumber());
        electronicPayment.setFinancialDocumentPostingPeriodCode(getPostingPeriodCode());
        electronicPayment.setFinancialDocumentPostingYear(getPostingYear());
        return electronicPayment;
    }

    /**
     * Overrides super to call super and then also add in the new list of advance deposits that have to be managed.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getAdvanceDeposits());

        return managedLists;
    }
    
    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     * 
     * @param financialDocument submitted financial document
     * @param sequenceHelper helper class which will allows us to increment a reference without using an Integer
     * @return true if there are no issues creating GLPE's
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public void processGenerateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        if (isBankCashOffsetEnabled()) {
            int displayedDepositNumber = 1;
            GeneralLedgerPostingHelper glPostingHelper = getGeneralLedgerPostingHelper();
            for (AdvanceDepositDetail detail : getAdvanceDeposits()) {
                detail.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_ACCOUNT);

                GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
                if (!glPostingHelper.populateBankOffsetGeneralLedgerPendingEntry(detail.getFinancialDocumentBankAccount(), detail.getFinancialDocumentAdvanceDepositAmount(), this, getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.ADVANCE_DEPOSITS_LINE_ERRORS)) {
                    success = false;
                    continue; // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at
                    // all.
                }
                AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
                bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.AdvanceDeposit.DESCRIPTION_GLPE_BANK_OFFSET, displayedDepositNumber++));
                getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(bankOffsetEntry);
                success &= glPostingHelper.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                getGeneralLedgerPendingEntries().add(offsetEntry);
                sequenceHelper.increment();
            }
        }
    }
}