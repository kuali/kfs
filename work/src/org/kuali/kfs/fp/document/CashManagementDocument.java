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

import static org.kuali.core.util.AssertionUtils.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.DepositConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.CashieringItemInProcess;
import org.kuali.module.financial.bo.CashieringTransaction;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashManagementService;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * This class represents the CashManagementDocument.
 */
public class CashManagementDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource {
    private static final long serialVersionUID = 7475843770851900297L;
    private static Logger LOG = Logger.getLogger(CashManagementDocument.class);

    private String workgroupName;
    private String referenceFinancialDocumentNumber;

    private List<Deposit> deposits;

    private List<Check> checks;

    private transient CashieringTransaction currentTransaction;
    private CashDrawer cashDrawer;
    
    private final static String GENERAL_LEDGER_POSTING_HELPER_BEAN_ID = "kfsGenericGeneralLedgerPostingHelper";

    /**
     * Default constructor.
     */
    public CashManagementDocument() {
        super();
        deposits = new ArrayList<Deposit>();
        checks = new ArrayList<Check>();
        this.resetCurrentTransaction();
    }


    /**
     * @return current value of referenceFinancialDocumentNumber.
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute value.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }


    /**
     * @return current value of workgroupName.
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute value.
     * 
     * @param workgroupName The workgroupName to set.
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }

    /**
     * Derives and returns the cash drawer status for the document's workgroup
     */
    public String getCashDrawerStatus() {
        return getCashDrawer().getStatusCode();
    }

    /**
     * @param cashDrawerStatus
     */
    public void setCashDrawerStatus(String cashDrawerStatus) {
        // ignored, because that value is dynamically retrieved from the service
        // required, because POJO pitches a fit if this method doesn't exist
    }

    /**
     * Alias for getCashDrawerStatus which avoids the automagic formatting
     */
    public String getRawCashDrawerStatus() {
        return getCashDrawerStatus();
    }

    /* Deposit-list maintenance */
    /**
     * @return current List of Deposits
     */
    public List<Deposit> getDeposits() {
        return deposits;
    }

    /**
     * Sets the current List of Deposits
     * 
     * @param deposits
     */
    public void setDeposits(List<Deposit> deposits) {
        this.deposits = deposits;
    }

    /**
     * Implementation creates empty Deposits as a side-effect, so that Struts' efforts to set fields of lines which haven't been
     * created will succeed rather than causing a NullPointerException.
     * 
     * @return Deposit at the given index
     */
    public Deposit getDeposit(int index) {
        extendDeposits(index + 1);

        return (Deposit) deposits.get(index);
    }

    /**
     * Removes and returns the Deposit at the given index.
     * 
     * @param index
     * @return Deposit at the given index
     */
    public Deposit removeDeposit(int index) {
        extendDeposits(index + 1);

        return (Deposit) deposits.remove(index);
    }


    /**
     * @return true if one of the Deposits contained in this document has a type of "final"
     */
    public boolean hasFinalDeposit() {
        boolean hasFinal = false;

        for (Iterator i = deposits.iterator(); !hasFinal && i.hasNext();) {
            Deposit d = (Deposit) i.next();

            hasFinal = StringUtils.equals(DepositConstants.DEPOSIT_TYPE_FINAL, d.getDepositTypeCode());
        }

        return hasFinal;
    }

    /**
     * @return lowest unused deposit-line-number, to simplify adding and canceling deposits out-of-order
     */
    public Integer getNextDepositLineNumber() {
        int maxLineNumber = -1;

        for (Iterator i = deposits.iterator(); i.hasNext();) {
            Deposit d = (Deposit) i.next();

            Integer depositLineNumber = d.getFinancialDocumentDepositLineNumber();
            if ((depositLineNumber != null) && (depositLineNumber.intValue() > maxLineNumber)) {
                maxLineNumber = depositLineNumber.intValue();
            }
        }

        return new Integer(maxLineNumber + 1);
    }

    /**
     * Adds default AccountingLineDecorators to sourceAccountingLineDecorators until it contains at least minSize elements
     * 
     * @param minSize
     */
    private void extendDeposits(int minSize) {
        while (deposits.size() < minSize) {
            deposits.add(new Deposit());
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getDeposits());

        return managedLists;
    }


    /**
     * Gets the cashDrawer attribute.
     * 
     * @return Returns the cashDrawer.
     */
    public CashDrawer getCashDrawer() {
        return cashDrawer;
        // return cashDrawerService.getByWorkgroupName(this.workgroupName, false);
    }

    /**
     * Sets the cashDrawer attribute
     * 
     * @param cd the cash drawer to set
     */
    public void setCashDrawer(CashDrawer cd) {
        cashDrawer = cd;
    }

    /**
     * Gets the currentTransaction attribute.
     * 
     * @return Returns the currentTransaction.
     */
    public CashieringTransaction getCurrentTransaction() {
        return currentTransaction;
    }


    /**
     * Sets the currentTransaction attribute value.
     * 
     * @param currentTransaction The currentTransaction to set.
     */
    public void setCurrentTransaction(CashieringTransaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    /**
     * Gets the checks attribute.
     * 
     * @return Returns the checks.
     */
    public List<Check> getChecks() {
        return checks;
    }

    /**
     * Sets the checks attribute value.
     * 
     * @param checks The checks to set.
     */
    public void setChecks(List<Check> checks) {
        this.checks = checks;
    }

    /**
     * Add a check to the cash management document
     * 
     * @param check
     */
    public void addCheck(Check check) {
        this.checks.add(check);
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        KualiWorkflowDocument kwd = getDocumentHeader().getWorkflowDocument();

        if (LOG.isDebugEnabled()) {
            logState();
        }

        if (kwd.stateIsProcessed()) {
            // all approvals have been processed, finalize everything
            SpringContext.getBean(CashManagementService.class).finalizeCashManagementDocument(this);
        }
        else if (kwd.stateIsCanceled() || kwd.stateIsDisapproved()) {
            // document has been canceled or disapproved
            SpringContext.getBean(CashManagementService.class).cancelCashManagementDocument(this);
        }
    }

    private void logState() {
        KualiWorkflowDocument kwd = getDocumentHeader().getWorkflowDocument();

        if (kwd.stateIsInitiated()) {
            LOG.debug("CMD stateIsInitiated");
        }
        if (kwd.stateIsProcessed()) {
            LOG.debug("CMD stateIsProcessed");
        }
        if (kwd.stateIsCanceled()) {
            LOG.debug("CMD stateIsCanceled");
        }
        if (kwd.stateIsDisapproved()) {
            LOG.debug("CMD stateIsDisapproved");
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        // grab the cash drawer
        if (this.getWorkgroupName() != null) {
            this.cashDrawer = SpringContext.getBean(CashDrawerService.class).getByWorkgroupName(this.getWorkgroupName(), false);
            this.resetCurrentTransaction();
        }
        SpringContext.getBean(CashManagementService.class).populateCashDetailsForDeposit(this);
    }


    /* utility methods */
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        m.put("workgroupName", getWorkgroupName());
        return m;
    }

    /**
     * This method creates a clean current transaction to be the new current transaction on this document
     */
    public void resetCurrentTransaction() {
        if (this.currentTransaction != null) {
            this.currentTransaction.setTransactionEnded(SpringContext.getBean(DateTimeService.class).getCurrentDate());
        }
        currentTransaction = new CashieringTransaction(workgroupName, referenceFinancialDocumentNumber);
        if (this.getWorkgroupName() != null) {
            List<CashieringItemInProcess> openItemsInProcess = SpringContext.getBean(CashManagementService.class).getOpenItemsInProcess(this);
            if (openItemsInProcess != null) {
                currentTransaction.setOpenItemsInProcess(openItemsInProcess);
            }
            currentTransaction.setNextCheckSequenceId(SpringContext.getBean(CashManagementService.class).selectNextAvailableCheckLineNumber(this.documentNumber));
        }
    }


    /**
     * Does nothing, as there aren't any accounting lines on this doc, so no GeneralLedgerPendingEntrySourceDetail create GLPEs
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.bo.GeneralLedgerPendingEntry)
     */
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {}


    /**
     * Does nothing save return true, as this document has no GLPEs created from a source of GeneralLedgerPostables
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#customizeOffsetGeneralLedgerPendingEntry(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.bo.GeneralLedgerPendingEntry, org.kuali.kfs.bo.GeneralLedgerPendingEntry)
     */
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        return true;
    }


    /**
     * Returns an empty list as this document has no GeneralLedgerPostables
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#getGeneralLedgerPostables()
     */
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        return new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
    }


    /**
     * Always returns true, as there are no GeneralLedgerPostables to create GLPEs
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#isDebit(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return true;
    }


    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     * 
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class to keep track of sequence of general ledger pending entries
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        if (isBankCashOffsetEnabled()) {
            GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
            Integer universityFiscalYear = getUniversityFiscalYear();
            int interimDepositNumber = 1;
            for (Iterator iterator = getDeposits().iterator(); iterator.hasNext();) {
                // todo: getDeposits() should return List<Deposit> not List
                Deposit deposit = (Deposit) iterator.next();
                deposit.refreshReferenceObject(KFSPropertyConstants.BANK_ACCOUNT);

                GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
                if (!glpeService.populateBankOffsetGeneralLedgerPendingEntry(deposit.getBankAccount(), deposit.getDepositAmount(), this, universityFiscalYear, sequenceHelper, bankOffsetEntry, KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS)) {
                    success = false;
                    LOG.warn("Skipping ledger entries for depost " + deposit.getDepositTicketNumber() + ".");
                    continue; // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at
                    // all.
                }
                bankOffsetEntry.setTransactionLedgerEntryDescription(createDescription(deposit, interimDepositNumber++));
                getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(bankOffsetEntry);
                success &= glpeService.populateOffsetGeneralLedgerPendingEntry(universityFiscalYear, bankOffsetEntry, sequenceHelper, offsetEntry);
                getGeneralLedgerPendingEntries().add(offsetEntry);
                sequenceHelper.increment();
                /*
                 * Only the final deposit will have non-null currency and coin. If this is the final deposit, generate the ledger
                 * entries for currency and coin.
                 */
                if (deposit.getDepositTypeCode().equals(KFSConstants.DocumentStatusCodes.CashReceipt.FINAL)) {
                    KualiDecimal totalCoinCurrencyAmount = deposit.getDepositedCurrency().getTotalAmount().add(deposit.getDepositedCoin().getTotalAmount());
                    GeneralLedgerPendingEntry coinCurrencyBankOffsetEntry = new GeneralLedgerPendingEntry();
                    if (!glpeService.populateBankOffsetGeneralLedgerPendingEntry(deposit.getBankAccount(), totalCoinCurrencyAmount, this, universityFiscalYear, sequenceHelper, coinCurrencyBankOffsetEntry, KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS)) {
                        success = false;
                        // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at all.
                        LOG.warn("Skipping ledger entries for coin and currency.");
                        continue;
                    }

                    coinCurrencyBankOffsetEntry.setTransactionLedgerEntryDescription(createDescription(deposit, interimDepositNumber++));
                    getGeneralLedgerPendingEntries().add(coinCurrencyBankOffsetEntry);
                    sequenceHelper.increment();

                    GeneralLedgerPendingEntry coinCurrnecyOffsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(coinCurrencyBankOffsetEntry);
                    success &= glpeService.populateOffsetGeneralLedgerPendingEntry(universityFiscalYear, coinCurrencyBankOffsetEntry, sequenceHelper, coinCurrnecyOffsetEntry);
                    getGeneralLedgerPendingEntries().add(coinCurrnecyOffsetEntry);
                    sequenceHelper.increment();

                }

            }

        }
        return success;
    }
    
    /**
     * Create description for deposit
     * 
     * @param deposit deposit from cash management document
     * @param interimDepositNumber
     * @return the description for the given deposit's GLPE bank offset
     */
    private static String createDescription(Deposit deposit, int interimDepositNumber) {
        String descriptionKey;
        if (KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL.equals(deposit.getDepositTypeCode())) {
            descriptionKey = KFSKeyConstants.CashManagement.DESCRIPTION_GLPE_BANK_OFFSET_FINAL;
        }
        else {
            assertThat(KFSConstants.DepositConstants.DEPOSIT_TYPE_INTERIM.equals(deposit.getDepositTypeCode()), deposit.getDepositTypeCode());
            descriptionKey = KFSKeyConstants.CashManagement.DESCRIPTION_GLPE_BANK_OFFSET_INTERIM;
        }
        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        return accountingDocumentRuleUtil.formatProperty(descriptionKey, interimDepositNumber);
    }

    /**
     * Gets the fiscal year for the GLPEs generated by this document. This works the same way as in TransactionalDocumentBase. The
     * property is down in TransactionalDocument because no FinancialDocument (currently only CashManagementDocument) allows the
     * user to override it. So, that logic is duplicated here. A comment in TransactionalDocumentBase says that this implementation
     * is a hack right now because it's intended to be set by the
     * <code>{@link org.kuali.module.chart.service.AccountingPeriodService}</code>, which suggests to me that pulling that
     * property up to FinancialDocument is preferable to duplicating this logic here.
     * 
     * @return the fiscal year for the GLPEs generated by this document
     */
    private Integer getUniversityFiscalYear() {
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }

    /**
     * The Cash Management doc doesn't have accounting lines, so it doesn't create general ledger pending entries for the accounting lines it doesn't have
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#generateGeneralLedgerPendingEntries(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }


    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount().abs();
    }

    
}
