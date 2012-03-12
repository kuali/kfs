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

package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringItemInProcess;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.DepositConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;

/**
 * This class represents the CashManagementDocument.
 */
public class CashManagementDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource {
    protected static final long serialVersionUID = 7475843770851900297L;
    protected static Logger LOG = Logger.getLogger(CashManagementDocument.class);

    protected String campusCode;

    protected List<Deposit> deposits;

    protected List<Check> checks;

    protected CashieringTransaction currentTransaction;
    protected CashDrawer cashDrawer;
    protected Campus campus;

    private KualiDecimal financialDocumentHundredDollarAmount;
    private KualiDecimal financialDocumentFiftyDollarAmount;
    private KualiDecimal financialDocumentTwentyDollarAmount;
    private KualiDecimal financialDocumentTenDollarAmount;
    private KualiDecimal financialDocumentFiveDollarAmount;
    private KualiDecimal financialDocumentTwoDollarAmount;
    private KualiDecimal financialDocumentOneDollarAmount;
    private KualiDecimal financialDocumentOtherDollarAmount;
    private KualiDecimal financialDocumentHundredCentAmount;
    private KualiDecimal financialDocumentFiftyCentAmount;
    private KualiDecimal financialDocumentTwentyFiveCentAmount;
    private KualiDecimal financialDocumentTenCentAmount;
    private KualiDecimal financialDocumentFiveCentAmount;
    private KualiDecimal financialDocumentOneCentAmount;
    private KualiDecimal financialDocumentOtherCentAmount;


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
     * @return current value of campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute value.
     *
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
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
    protected void extendDeposits(int minSize) {
        while (deposits.size() < minSize) {
            deposits.add(new Deposit());
        }
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#buildListOfDeletionAwareLists()
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
     * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        WorkflowDocument kwd = getDocumentHeader().getWorkflowDocument();

        if (LOG.isDebugEnabled()) {
            logState();
        }

        if (kwd.isProcessed()) {
            // all approvals have been processed, finalize everything
            SpringContext.getBean(CashManagementService.class).finalizeCashManagementDocument(this);
        }
        else if (kwd.isCanceled() || kwd.isDisapproved()) {
            // document has been canceled or disapproved
            SpringContext.getBean(CashManagementService.class).cancelCashManagementDocument(this);
        }
    }

    protected void logState() {
        WorkflowDocument kwd = getDocumentHeader().getWorkflowDocument();

        if (kwd.isInitiated()) {
            LOG.debug("CMD isInitiated");
        }
        if (kwd.isProcessed()) {
            LOG.debug("CMD isProcessed");
        }
        if (kwd.isCanceled()) {
            LOG.debug("CMD isCanceled");
        }
        if (kwd.isDisapproved()) {
            LOG.debug("CMD isDisapproved");
        }
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        // grab the cash drawer
        if (this.getCampusCode() != null) {
            this.cashDrawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(this.getCampusCode());
            this.resetCurrentTransaction();
        }
        SpringContext.getBean(CashManagementService.class).populateCashDetailsForDeposit(this);
    }

    /**
     * This method creates a clean current transaction to be the new current transaction on this document
     */
    public void resetCurrentTransaction() {
        if (this.currentTransaction != null) {
            this.currentTransaction.setTransactionEnded(SpringContext.getBean(DateTimeService.class).getCurrentDate());
        }
        currentTransaction = new CashieringTransaction(campusCode, documentNumber);
        if (this.getCampusCode() != null) {
            List<CashieringItemInProcess> openItemsInProcess = SpringContext.getBean(CashManagementService.class).getOpenItemsInProcess(this);
            if (openItemsInProcess != null) {
                currentTransaction.setOpenItemsInProcess(openItemsInProcess);
            }
            currentTransaction.setNextCheckSequenceId(SpringContext.getBean(CashManagementService.class).selectNextAvailableCheckLineNumber(this.documentNumber));
        }
    }


    /**
     * Does nothing, as there aren't any accounting lines on this doc, so no GeneralLedgerPendingEntrySourceDetail create GLPEs
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {}


    /**
     * Does nothing save return true, as this document has no GLPEs created from a source of GeneralLedgerPostables
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#customizeOffsetGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        return true;
    }


    /**
     * Returns an empty list as this document has no GeneralLedgerPostables
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#getGeneralLedgerPostables()
     */
    @Override
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        return new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
    }


    /**
     * Always returns true, as there are no GeneralLedgerPostables to create GLPEs
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return true;
    }


    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     *
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class to keep track of sequence of general ledger pending entries
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);

        if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            Integer universityFiscalYear = getUniversityFiscalYear();
            int interimDepositNumber = 1;
            for (Deposit deposit: getDeposits()) {
                deposit.refreshReferenceObject(KFSPropertyConstants.BANK);

                GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
                if (!glpeService.populateBankOffsetGeneralLedgerPendingEntry(deposit.getBank(), deposit.getDepositAmount(), this, universityFiscalYear, sequenceHelper, bankOffsetEntry, KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS)) {
                    success = false;
                    LOG.warn("Skipping ledger entries for deposit " + deposit.getDepositTicketNumber() + ".");
                    continue; // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it
                }

                bankOffsetEntry.setTransactionLedgerEntryDescription(createDescription(deposit, interimDepositNumber++));
                getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(bankOffsetEntry);
                success &= glpeService.populateOffsetGeneralLedgerPendingEntry(universityFiscalYear, bankOffsetEntry, sequenceHelper, offsetEntry);
                getGeneralLedgerPendingEntries().add(offsetEntry);
                sequenceHelper.increment();
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
    protected static String createDescription(Deposit deposit, int interimDepositNumber) {
        String descriptionKey;
        if (KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL.equals(deposit.getDepositTypeCode())) {
            descriptionKey = KFSKeyConstants.CashManagement.DESCRIPTION_GLPE_BANK_OFFSET_FINAL;
        }
        else {
           // assertThat(KFSConstants.DepositConstants.DEPOSIT_TYPE_INTERIM.equals(deposit.getDepositTypeCode()), deposit.getDepositTypeCode());
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
     * <code>{@link org.kuali.kfs.coa.service.AccountingPeriodService}</code>, which suggests to me that pulling that
     * property up to FinancialDocument is preferable to duplicating this logic here.
     *
     * @return the fiscal year for the GLPEs generated by this document
     */
    protected Integer getUniversityFiscalYear() {
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }

    /**
     * The Cash Management doc doesn't have accounting lines, so it doesn't create general ledger pending entries for the accounting lines it doesn't have
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#generateGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }


    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount().abs();
    }

    /**
     * Helper method on document for determining whether the document can have GLPEs.
     *
     * @return true if document can have GLPEs
     */
    public boolean getBankCashOffsetEnabled() {
        return SpringContext.getBean(BankService.class).isBankSpecificationEnabled();
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#prepareForSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        if (getBankCashOffsetEnabled()) {
            if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(this)) {
                logErrors();
                throw new ValidationException("general ledger GLPE generation failed");
            }
        }

        super.prepareForSave(event);
    }

    /**
     * @return the campus associated with this cash drawer
     */
    public Campus getCampus() {
        if (campusCode != null && (campus == null || !campus.getCode().equals(campusCode))) {
            campus = retrieveCampus();
        }
        return campus;
    }

    protected Campus retrieveCampus() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KRADPropertyConstants.CAMPUS_CODE, campusCode);
        return campus = SpringContext.getBean(CampusService.class).getCampus(campusCode/*RICE_20_REFACTORME  criteria */);
    }


    /**
     * Gets the financialDocumentHundredDollarAmount attribute.
     * @return Returns the financialDocumentHundredDollarAmount.
     */
    public KualiDecimal getFinancialDocumentHundredDollarAmount() {
        return financialDocumentHundredDollarAmount;
    }


    /**
     * Sets the financialDocumentHundredDollarAmount attribute value.
     * @param financialDocumentHundredDollarAmount The financialDocumentHundredDollarAmount to set.
     */
    public void setFinancialDocumentHundredDollarAmount(KualiDecimal financialDocumentHundredDollarAmount) {
        this.financialDocumentHundredDollarAmount = financialDocumentHundredDollarAmount;
    }

    /**
     * Gets the financialDocumentFiftyDollarAmount attribute.
     * @return Returns the financialDocumentFiftyDollarAmount.
     */
    public KualiDecimal getFinancialDocumentFiftyDollarAmount() {
        return financialDocumentFiftyDollarAmount;
    }


    /**
     * Sets the financialDocumentFiftyDollarAmount attribute value.
     * @param financialDocumentFiftyDollarAmount The financialDocumentFiftyDollarAmount to set.
     */
    public void setFinancialDocumentFiftyDollarAmount(KualiDecimal financialDocumentFiftyDollarAmount) {
        this.financialDocumentFiftyDollarAmount = financialDocumentFiftyDollarAmount;
    }


    /**
     * Gets the financialDocumentTwentyDollarAmount attribute.
     * @return Returns the financialDocumentTwentyDollarAmount.
     */
    public KualiDecimal getFinancialDocumentTwentyDollarAmount() {
        return financialDocumentTwentyDollarAmount;
    }


    /**
     * Sets the financialDocumentTwentyDollarAmount attribute value.
     * @param financialDocumentTwentyDollarAmount The financialDocumentTwentyDollarAmount to set.
     */
    public void setFinancialDocumentTwentyDollarAmount(KualiDecimal financialDocumentTwentyDollarAmount) {
        this.financialDocumentTwentyDollarAmount = financialDocumentTwentyDollarAmount;
    }


    /**
     * Gets the financialDocumentTenDollarAmount attribute.
     * @return Returns the financialDocumentTenDollarAmount.
     */
    public KualiDecimal getFinancialDocumentTenDollarAmount() {
        return financialDocumentTenDollarAmount;
    }


    /**
     * Sets the financialDocumentTenDollarAmount attribute value.
     * @param financialDocumentTenDollarAmount The financialDocumentTenDollarAmount to set.
     */
    public void setFinancialDocumentTenDollarAmount(KualiDecimal financialDocumentTenDollarAmount) {
        this.financialDocumentTenDollarAmount = financialDocumentTenDollarAmount;
    }


    /**
     * Gets the financialDocumentFiveDollarAmount attribute.
     * @return Returns the financialDocumentFiveDollarAmount.
     */
    public KualiDecimal getFinancialDocumentFiveDollarAmount() {
        return financialDocumentFiveDollarAmount;
    }


    /**
     * Sets the financialDocumentFiveDollarAmount attribute value.
     * @param financialDocumentFiveDollarAmount The financialDocumentFiveDollarAmount to set.
     */
    public void setFinancialDocumentFiveDollarAmount(KualiDecimal financialDocumentFiveDollarAmount) {
        this.financialDocumentFiveDollarAmount = financialDocumentFiveDollarAmount;
    }


    /**
     * Gets the financialDocumentTwoDollarAmount attribute.
     * @return Returns the financialDocumentTwoDollarAmount.
     */
    public KualiDecimal getFinancialDocumentTwoDollarAmount() {
        return financialDocumentTwoDollarAmount;
    }


    /**
     * Sets the financialDocumentTwoDollarAmount attribute value.
     * @param financialDocumentTwoDollarAmount The financialDocumentTwoDollarAmount to set.
     */
    public void setFinancialDocumentTwoDollarAmount(KualiDecimal financialDocumentTwoDollarAmount) {
        this.financialDocumentTwoDollarAmount = financialDocumentTwoDollarAmount;
    }


    /**
     * Gets the financialDocumentOneDollarAmount attribute.
     * @return Returns the financialDocumentOneDollarAmount.
     */
    public KualiDecimal getFinancialDocumentOneDollarAmount() {
        return financialDocumentOneDollarAmount;
    }


    /**
     * Sets the financialDocumentOneDollarAmount attribute value.
     * @param financialDocumentOneDollarAmount The financialDocumentOneDollarAmount to set.
     */
    public void setFinancialDocumentOneDollarAmount(KualiDecimal financialDocumentOneDollarAmount) {
        this.financialDocumentOneDollarAmount = financialDocumentOneDollarAmount;
    }


    /**
     * Gets the financialDocumentOtherDollarAmount attribute.
     * @return Returns the financialDocumentOtherDollarAmount.
     */
    public KualiDecimal getFinancialDocumentOtherDollarAmount() {
        return financialDocumentOtherDollarAmount;
    }


    /**
     * Sets the financialDocumentOtherDollarAmount attribute value.
     * @param financialDocumentOtherDollarAmount The financialDocumentOtherDollarAmount to set.
     */
    public void setFinancialDocumentOtherDollarAmount(KualiDecimal financialDocumentOtherDollarAmount) {
        this.financialDocumentOtherDollarAmount = financialDocumentOtherDollarAmount;
    }


    /**
     * Gets the financialDocumentHundredCentAmount attribute.
     * @return Returns the financialDocumentHundredCentAmount.
     */
    public KualiDecimal getFinancialDocumentHundredCentAmount() {
        return financialDocumentHundredCentAmount;
    }


    /**
     * Sets the financialDocumentHundredCentAmount attribute value.
     * @param financialDocumentHundredCentAmount The financialDocumentHundredCentAmount to set.
     */
    public void setFinancialDocumentHundredCentAmount(KualiDecimal financialDocumentHundredCentAmount) {
        this.financialDocumentHundredCentAmount = financialDocumentHundredCentAmount;
    }


    /**
     * Gets the financialDocumentFiftyCentAmount attribute.
     * @return Returns the financialDocumentFiftyCentAmount.
     */
    public KualiDecimal getFinancialDocumentFiftyCentAmount() {
        return financialDocumentFiftyCentAmount;
    }


    /**
     * Sets the financialDocumentFiftyCentAmount attribute value.
     * @param financialDocumentFiftyCentAmount The financialDocumentFiftyCentAmount to set.
     */
    public void setFinancialDocumentFiftyCentAmount(KualiDecimal financialDocumentFiftyCentAmount) {
        this.financialDocumentFiftyCentAmount = financialDocumentFiftyCentAmount;
    }


    /**
     * Gets the financialDocumentTwentyFiveCentAmount attribute.
     * @return Returns the financialDocumentTwentyFiveCentAmount.
     */
    public KualiDecimal getFinancialDocumentTwentyFiveCentAmount() {
        return financialDocumentTwentyFiveCentAmount;
    }


    /**
     * Sets the financialDocumentTwentyFiveCentAmount attribute value.
     * @param financialDocumentTwentyFiveCentAmount The financialDocumentTwentyFiveCentAmount to set.
     */
    public void setFinancialDocumentTwentyFiveCentAmount(KualiDecimal financialDocumentTwentyFiveCentAmount) {
        this.financialDocumentTwentyFiveCentAmount = financialDocumentTwentyFiveCentAmount;
    }


    /**
     * Gets the financialDocumentTenCentAmount attribute.
     * @return Returns the financialDocumentTenCentAmount.
     */
    public KualiDecimal getFinancialDocumentTenCentAmount() {
        return financialDocumentTenCentAmount;
    }


    /**
     * Sets the financialDocumentTenCentAmount attribute value.
     * @param financialDocumentTenCentAmount The financialDocumentTenCentAmount to set.
     */
    public void setFinancialDocumentTenCentAmount(KualiDecimal financialDocumentTenCentAmount) {
        this.financialDocumentTenCentAmount = financialDocumentTenCentAmount;
    }


    /**
     * Gets the financialDocumentFiveCentAmount attribute.
     * @return Returns the financialDocumentFiveCentAmount.
     */
    public KualiDecimal getFinancialDocumentFiveCentAmount() {
        return financialDocumentFiveCentAmount;
    }


    /**
     * Sets the financialDocumentFiveCentAmount attribute value.
     * @param financialDocumentFiveCentAmount The financialDocumentFiveCentAmount to set.
     */
    public void setFinancialDocumentFiveCentAmount(KualiDecimal financialDocumentFiveCentAmount) {
        this.financialDocumentFiveCentAmount = financialDocumentFiveCentAmount;
    }


    /**
     * Gets the financialDocumentOneCentAmount attribute.
     * @return Returns the financialDocumentOneCentAmount.
     */
    public KualiDecimal getFinancialDocumentOneCentAmount() {
        return financialDocumentOneCentAmount;
    }


    /**
     * Sets the financialDocumentOneCentAmount attribute value.
     * @param financialDocumentOneCentAmount The financialDocumentOneCentAmount to set.
     */
    public void setFinancialDocumentOneCentAmount(KualiDecimal financialDocumentOneCentAmount) {
        this.financialDocumentOneCentAmount = financialDocumentOneCentAmount;
    }


    /**
     * Gets the financialDocumentOtherCentAmount attribute.
     * @return Returns the financialDocumentOtherCentAmount.
     */
    public KualiDecimal getFinancialDocumentOtherCentAmount() {
        return financialDocumentOtherCentAmount;
    }


    /**
     * Sets the financialDocumentOtherCentAmount attribute value.
     * @param financialDocumentOtherCentAmount The financialDocumentOtherCentAmount to set.
     */
    public void setFinancialDocumentOtherCentAmount(KualiDecimal financialDocumentOtherCentAmount) {
        this.financialDocumentOtherCentAmount = financialDocumentOtherCentAmount;
    }


}
