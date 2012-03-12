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
package org.kuali.kfs.gl.batch;

import java.io.PrintStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.PriorYearAccount;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.batch.service.impl.exception.NonFatalErrorException;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.InvalidFlexibleOffsetException;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A class to hold significant state for a balance forward job; it also has the methods that actually accomplish the job
 */
public class BalanceForwardRuleHelper {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardRuleHelper.class);
    private FlexibleOffsetAccountService flexibleOffsetAccountService;

    /**
     * A container for the state of the balance forward process. The way state is handled is heavily dependent upon the way in which
     * YearEndServiceImpl.forwardBalancesForFiscalYear works.
     */
    public static class BalanceForwardProcessState {
        private int globalReadCount;
        private int globalSelectCount;
        private int sequenceNumber;
        private int sequenceClosedCount;
        private int sequenceWriteCount;
        private String accountNumberHold;
        private int nonFatalCount;

        public String getAccountNumberHold() {
            return accountNumberHold;
        }

        public void setAccountNumberHold(String accountNumberHold) {
            this.accountNumberHold = accountNumberHold;
        }

        public void incrementGlobalReadCount() {
            globalReadCount++;
        }

        public void incrementGlobalSelectCount() {
            globalSelectCount++;
        }

        public void incrementSequenceNumber() {
            sequenceNumber++;
        }

        public void incrementSequenceClosedCount() {
            sequenceClosedCount++;
        }

        public void incrementSequenceWriteCount() {
            sequenceWriteCount++;
        }

        public void incrementNonFatalCount() {
            nonFatalCount += 1;
        }

        public int getGlobalReadCount() {
            return globalReadCount;
        }

        public void setGlobalReadCount(int globalReadCount) {
            this.globalReadCount = globalReadCount;
        }

        public int getGlobalSelectCount() {
            return globalSelectCount;
        }

        public void setGlobalSelectCount(int globalSelectCount) {
            this.globalSelectCount = globalSelectCount;
        }

        public int getSequenceClosedCount() {
            return sequenceClosedCount;
        }

        public int getNonFatalCount() {
            return nonFatalCount;
        }

        public void setSequenceClosedCount(int sequenceClosedCount) {
            this.sequenceClosedCount = sequenceClosedCount;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public int getSequenceWriteCount() {
            return sequenceWriteCount;
        }

        public void setSequenceWriteCount(int sequenceWriteCount) {
            this.sequenceWriteCount = sequenceWriteCount;
        }

        public void setNonFatalCount(int nonFatalCount) {
            this.nonFatalCount = nonFatalCount;
        }
    }

    private Integer closingFiscalYear;
    private Date transactionDate;
    
    private String balanceForwardsUnclosedFileName; 
    private String balanceForwardsclosedFileName;
    
    private PriorYearAccountService priorYearAccountService;
    private SubFundGroupService subFundGroupService;
    private OriginEntryService originEntryService;
    private ParameterService parameterService;
    private SystemOptions currentYearOptions;
    private LedgerSummaryReport openAccountForwardBalanceLedgerReport;
    private LedgerSummaryReport closedAccountForwardBalanceLedgerReport;
    private String[] priorYearAccountObjectTypes;
    private String[] generalSwObjectTypes;
    private String annualClosingDocType;
    private String glOriginationCode;
    private Map<String, Boolean> balanceTypeEncumbranceIndicators;

    private BalanceForwardProcessState state;

    /**
     * Constructs a BalanceForwardRuleHelper
     */
    public BalanceForwardRuleHelper() {
        super();
        state = new BalanceForwardProcessState();
        flexibleOffsetAccountService = SpringContext.getBean(FlexibleOffsetAccountService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
        annualClosingDocType = parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE);
        glOriginationCode = parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE);
        openAccountForwardBalanceLedgerReport = new LedgerSummaryReport();
        closedAccountForwardBalanceLedgerReport = new LedgerSummaryReport();
    }

    /**
     * Constructs a BalanceForwardRuleHelper, using a fiscal year. This also initializes object type arrays based on the options of
     * the closing fiscal year
     * 
     * @param closingFiscalYear the fiscal year that is closing out
     */
    public BalanceForwardRuleHelper(Integer closingFiscalYear) {
        this();
        setClosingFiscalYear(closingFiscalYear);

        SystemOptions jobYearRunOptions = SpringContext.getBean(OptionsService.class).getOptions(closingFiscalYear);

        generalSwObjectTypes = new String[3];
        generalSwObjectTypes[0] = jobYearRunOptions.getFinancialObjectTypeAssetsCd();
        generalSwObjectTypes[1] = jobYearRunOptions.getFinObjectTypeLiabilitiesCode();
        generalSwObjectTypes[2] = jobYearRunOptions.getFinObjectTypeFundBalanceCd();

        // "EE", "ES", "EX", "IC", "TE", "TI", "IN", "CH"
        priorYearAccountObjectTypes = new String[8];
        priorYearAccountObjectTypes[0] = jobYearRunOptions.getFinObjTypeExpendNotExpCode();
        priorYearAccountObjectTypes[1] = jobYearRunOptions.getFinObjTypeExpNotExpendCode();
        priorYearAccountObjectTypes[2] = jobYearRunOptions.getFinObjTypeExpenditureexpCd();
        priorYearAccountObjectTypes[3] = jobYearRunOptions.getFinObjTypeIncomeNotCashCd();
        priorYearAccountObjectTypes[4] = jobYearRunOptions.getFinancialObjectTypeTransferExpenseCd();
        priorYearAccountObjectTypes[5] = jobYearRunOptions.getFinancialObjectTypeTransferIncomeCd();
        priorYearAccountObjectTypes[6] = jobYearRunOptions.getFinObjectTypeIncomecashCode();
        priorYearAccountObjectTypes[7] = jobYearRunOptions.getFinObjTypeCshNotIncomeCd();
    }

    /**
     * Constructs a BalanceForwardRuleHelper, but this one goes whole hog: initializes all of the relevant parameters and the
     * balance types to process
     * 
     * @param closingFiscalYear the fiscal year to close
     * @param transactionDate the date this job is being run
     * @param closedPriorYearAccountGroup the group to put balance forwarding origin entries with closed accounts into
     * @param unclosedPriorYearAccountGroup the group to put balance forwarding origin entries with open accounts into
     */
    public BalanceForwardRuleHelper(Integer closingFiscalYear, Date transactionDate, String balanceForwardsclosedFileName, String balanceForwardsUnclosedFileName) {
        this(closingFiscalYear);
        setTransactionDate(transactionDate);
        setClosingFiscalYear(closingFiscalYear);
        
        setBalanceForwardsclosedFileName(balanceForwardsclosedFileName);
        setBalanceForwardsUnclosedFileName(balanceForwardsUnclosedFileName);
        currentYearOptions = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();

        balanceTypeEncumbranceIndicators = new HashMap<String, Boolean>();
        for (Object balanceTypAsObj : SpringContext.getBean(BalanceTypeService.class).getAllBalanceTypes()) {
            BalanceType balanceType = (BalanceType) balanceTypAsObj;
            balanceTypeEncumbranceIndicators.put(balanceType.getCode(), (balanceType.isFinBalanceTypeEncumIndicator() ? Boolean.TRUE : Boolean.FALSE));
        }
    }


    /**
     * The balance to create a general balance forward origin entry for
     * 
     * @param balance a balance to create an origin entry for
     * @param closedPriorYearAccountGroup the group to put balance forwarding origin entries with closed accounts into
     * @param unclosedPriorYearAccountGroup the group to put balance forwarding origin entries with open accounts into
     * @throws FatalErrorException
     */
    public void processGeneralForwardBalance(Balance balance, PrintStream closedPs, PrintStream unclosedPs) {
        if (ObjectUtils.isNull(balance.getPriorYearAccount())) {
            LOG.info(("COULD NOT RETRIEVE INFORMATION ON ACCOUNT " + balance.getChartOfAccountsCode() + "-" + balance.getAccountNumber()));
        } 
        else {
            if ((null == balance.getAccountNumber() && null == state.getAccountNumberHold()) || (null != balance.getAccountNumber() && balance.getAccountNumber().equals(state.getAccountNumberHold()))) {
                state.incrementSequenceNumber();
            }
            else {
                state.setSequenceNumber(1);
            }
            state.incrementGlobalSelectCount();
            OriginEntryFull entry = generateGeneralForwardOriginEntry(balance);
            saveForwardingEntry(balance, entry, closedPs, unclosedPs);
        }
    }

    /**
     * This method creates an origin entry for a cumulative balance forward and saves it in its proper origin entry group
     * 
     * @param balance a balance which needs to have a cumulative origin entry generated for it
     * @param closedPriorYearAccountGroup the origin entry group where forwarding origin entries with closed prior year accounts go
     * @param unclosedPriorYearAcocuntGroup the origin entry group where forwarding origin entries with open prior year accounts go
     */
    public void processCumulativeForwardBalance(Balance balance, PrintStream closedPs, PrintStream unclosedPs) {
        if ((null == balance.getAccountNumber() && null == state.getAccountNumberHold()) || (null != balance.getAccountNumber() && balance.getAccountNumber().equals(state.getAccountNumberHold()))) {
            state.incrementSequenceNumber();
        }
        else {
            state.setSequenceNumber(1);
        }
        state.incrementGlobalSelectCount();
        OriginEntryFull activeEntry = generateCumulativeForwardOriginEntry(balance);
        saveForwardingEntry(balance, activeEntry, closedPs, unclosedPs);
    }

    /**
     * This method generates an origin entry for a given cumulative balance forward balance
     * 
     * @param balance a balance to foward, cumulative style
     * @return an OriginEntryFull to forward the given balance
     */
    public OriginEntryFull generateCumulativeForwardOriginEntry(Balance balance) {
        OriginEntryFull activeEntry = new OriginEntryFull();
        activeEntry.setUniversityFiscalYear(new Integer(closingFiscalYear.intValue() + 1));
        activeEntry.setChartOfAccountsCode(balance.getChartOfAccountsCode());
        activeEntry.setAccountNumber(balance.getAccountNumber());
        activeEntry.setSubAccountNumber(balance.getSubAccountNumber());
        activeEntry.setFinancialObjectCode(balance.getObjectCode());
        activeEntry.setFinancialSubObjectCode(balance.getSubObjectCode());
        activeEntry.setFinancialBalanceTypeCode(balance.getBalanceTypeCode());
        activeEntry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

        try {
            flexibleOffsetAccountService.updateOffset(activeEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("processBalance() Balance Forward Flexible Offset Error: " + e.getMessage());    
            }
        }
        activeEntry.setUniversityFiscalPeriodCode(KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE);
        activeEntry.setFinancialDocumentTypeCode(this.annualClosingDocType);
        activeEntry.setFinancialSystemOriginationCode(this.glOriginationCode);
        activeEntry.setDocumentNumber(new StringBuffer(KFSConstants.BALANCE_TYPE_ACTUAL).append(balance.getAccountNumber()).toString());
        activeEntry.setTransactionLedgerEntrySequenceNumber(new Integer(state.getSequenceNumber()));
        activeEntry.setTransactionLedgerEntryDescription(new StringBuffer("BEG C & G BAL BROUGHT FORWARD FROM ").append(closingFiscalYear).toString());
        activeEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount().add(balance.getContractsGrantsBeginningBalanceAmount()));
        if (KFSConstants.BALANCE_TYPE_CURRENT_BUDGET.equals(balance.getBalanceTypeCode()) 
                || KFSConstants.BALANCE_TYPE_BASE_BUDGET.equals(balance.getBalanceTypeCode())  ) {
            activeEntry.setTransactionDebitCreditCode(null);
        }
        else {

            String wsFinancialObjectTypeDebitCreditCode = null;

            try {
                wsFinancialObjectTypeDebitCreditCode = getFinancialObjectTypeDebitCreditCode(balance);
            }
            catch (NonFatalErrorException nfee) {
                getState().incrementNonFatalCount();
                wsFinancialObjectTypeDebitCreditCode = KFSConstants.GL_CREDIT_CODE;
                LOG.info(nfee.getMessage());
            }
            if (activeEntry.getTransactionLedgerEntryAmount().isNegative()) {
                if (KFSConstants.GL_CREDIT_CODE.equals(wsFinancialObjectTypeDebitCreditCode)) {
                    activeEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                }
                else {
                    activeEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                }
            }
            else {
                activeEntry.setTransactionDebitCreditCode(wsFinancialObjectTypeDebitCreditCode);
            }

        }
        activeEntry.setTransactionDate(transactionDate);
        activeEntry.setOrganizationDocumentNumber(null);
        activeEntry.setProjectCode(KFSConstants.getDashProjectCode());
        activeEntry.setOrganizationReferenceId(null);
        activeEntry.setReferenceFinancialDocumentNumber(null);
        activeEntry.setReferenceFinancialSystemOriginationCode(null);
        activeEntry.setReferenceFinancialDocumentNumber(null);
        activeEntry.setReversalDate(null);
        String transactionEncumbranceUpdateCode = null;
        try {
            transactionEncumbranceUpdateCode = getTransactionEncumbranceUpdateCode(balance);
        }
        catch (NonFatalErrorException nfee) {
            getState().incrementNonFatalCount();
            LOG.info(nfee.getMessage());
        }

        activeEntry.setTransactionEncumbranceUpdateCode(transactionEncumbranceUpdateCode);
        if (KFSConstants.BALANCE_TYPE_AUDIT_TRAIL.equals(balance.getBalanceTypeCode())) {
            activeEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        }
        if (activeEntry.getTransactionLedgerEntryAmount().isNegative()) {
            if (KFSConstants.BALANCE_TYPE_ACTUAL.equals(activeEntry.getFinancialBalanceTypeCode())) {
                activeEntry.setTransactionLedgerEntryAmount(activeEntry.getTransactionLedgerEntryAmount().negated());
            }
        }

        return activeEntry;
    }

    /**
     * Creates an origin entry that will forward this "general" balance
     * 
     * @param balance the balance to create a general origin entry for
     * @return the generated origin entry
     */
    public OriginEntryFull generateGeneralForwardOriginEntry(Balance balance) {

        OriginEntryFull entry = new OriginEntryFull();
        entry.setUniversityFiscalYear(new Integer(closingFiscalYear.intValue() + 1));
        entry.setChartOfAccountsCode(balance.getChartOfAccountsCode());
        entry.setAccountNumber(balance.getAccountNumber());
        entry.setSubAccountNumber(balance.getSubAccountNumber());
        entry.setFinancialObjectCode(balance.getObjectCode());
        entry.setFinancialSubObjectCode(balance.getSubObjectCode());
        entry.setFinancialBalanceTypeCode(balance.getBalanceTypeCode());
        if (currentYearOptions.getFinObjTypeExpendNotExpCode().equals(balance.getObjectTypeCode())) {
            entry.setFinancialObjectTypeCode(currentYearOptions.getFinancialObjectTypeAssetsCd());
        }
        else {
            entry.setFinancialObjectTypeCode(balance.getObjectTypeCode());
        }
        entry.setUniversityFiscalPeriodCode(KFSConstants.PERIOD_CODE_BEGINNING_BALANCE);
        entry.setFinancialDocumentTypeCode(this.annualClosingDocType);
        entry.setFinancialSystemOriginationCode(this.glOriginationCode);

        // FIXME Once tests are running properly uncomment the code to include the
        // chartOfAccountsCode in the document number. It will cause the tests to
        // break given the current framework but is desired as an enhancement for Kuali.
        entry.setDocumentNumber(new StringBuffer(KFSConstants.BALANCE_TYPE_ACTUAL).append(balance.getAccountNumber())/* .append(balance.getChartOfAccountsCode()) */.toString());
        entry.setTransactionLedgerEntrySequenceNumber(new Integer(state.getSequenceNumber()));
        entry.setTransactionLedgerEntryDescription(new StringBuffer("BEG BAL BROUGHT FORWARD FROM ").append(closingFiscalYear).toString());

        String transactionEncumbranceUpdateCode = null;
        try {
            transactionEncumbranceUpdateCode = getTransactionEncumbranceUpdateCode(balance);
        }
        catch (NonFatalErrorException nfee) {
            getState().incrementNonFatalCount();
            LOG.info(nfee.getMessage());
        }
        entry.setTransactionEncumbranceUpdateCode(transactionEncumbranceUpdateCode);
        KualiDecimal transactionLedgerEntryAmount = KualiDecimal.ZERO;
        transactionLedgerEntryAmount = transactionLedgerEntryAmount.add(balance.getAccountLineAnnualBalanceAmount()).add(balance.getBeginningBalanceLineAmount()).add(balance.getContractsGrantsBeginningBalanceAmount());

        String wsFinancialObjectTypeDebitCreditCode = null;
        try {
            wsFinancialObjectTypeDebitCreditCode = getFinancialObjectTypeDebitCreditCode(balance);
        }
        catch (NonFatalErrorException nfee) {
            getState().incrementNonFatalCount();
            wsFinancialObjectTypeDebitCreditCode = KFSConstants.GL_CREDIT_CODE;
            LOG.info(nfee.getMessage());
        }

        if (transactionLedgerEntryAmount.isNegative()) {
            if (KFSConstants.GL_DEBIT_CODE.equals(wsFinancialObjectTypeDebitCreditCode)) {
                entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }
        }
        else {
            entry.setTransactionDebitCreditCode(wsFinancialObjectTypeDebitCreditCode);
        }
        entry.setTransactionDate(transactionDate);
        entry.setOrganizationDocumentNumber(null);
        entry.setProjectCode(KFSConstants.getDashProjectCode());
        entry.setOrganizationReferenceId(null);
        entry.setReferenceFinancialDocumentTypeCode(null);
        entry.setReferenceFinancialSystemOriginationCode(null);
        entry.setReferenceFinancialDocumentNumber(null);
        entry.setFinancialDocumentReversalDate(null);
        if (KFSConstants.BALANCE_TYPE_AUDIT_TRAIL.equals(entry.getFinancialBalanceTypeCode())) {
            entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        }
        if (transactionLedgerEntryAmount.isNegative()) {
            if (KFSConstants.BALANCE_TYPE_ACTUAL.equals(entry.getFinancialBalanceTypeCode())) {
                transactionLedgerEntryAmount = transactionLedgerEntryAmount.negated();
            }
        }
        entry.setTransactionLedgerEntryAmount(transactionLedgerEntryAmount);
        return entry;
    }

    /**
     * Retrieves the transaction encumbrance update code, based on the balance type code of the balance. These codes are cached,
     * based off a cache generated in the big constructor
     * 
     * @param balance the balance to find the encumbrance update code for
     * @return the transaction update code
     * @throws NonFatalErrorException if an encumbrance update code cannot be found for this balance
     */
    private String getTransactionEncumbranceUpdateCode(Balance balance) throws NonFatalErrorException {
        String updateCode = null;
        Boolean encumIndicator = this.balanceTypeEncumbranceIndicators.get(balance.getBalanceTypeCode());
        if (encumIndicator == null) {
            throw new NonFatalErrorException(new StringBuffer(" ERROR ").append(balance.getBalanceTypeCode()).append(" NOT ON TABLE ").toString());
        }
        else if (encumIndicator.booleanValue()) {
            updateCode = KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD;
        }

        return updateCode;
    }

    /**
     * This method attempts to determine the debit/credit code of a given balance based on the object type
     * 
     * @param balance the balance to determin the debit/credit code for
     * @return the debit or credit code
     */
    private String getFinancialObjectTypeDebitCreditCode(Balance balance) throws NonFatalErrorException {
        String balanceObjectTypeDebitCreditCode = null != balance.getObjectType() ? balance.getObjectType().getFinObjectTypeDebitcreditCd() : null;

        String wsFinancialObjectTypeDebitCreditCode = null;

        if (null != balanceObjectTypeDebitCreditCode) {
            if (ObjectHelper.isOneOf(balanceObjectTypeDebitCreditCode, new String[] { KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE })) {
                wsFinancialObjectTypeDebitCreditCode = balanceObjectTypeDebitCreditCode;
            }
            else {
                wsFinancialObjectTypeDebitCreditCode = KFSConstants.GL_CREDIT_CODE;
            }
        }
        else {
            throw new NonFatalErrorException(new StringBuffer("FIN OBJ TYP CODE ").append(balance.getObjectTypeCode()).append(" NOT IN TABLE").toString());
        }
        return wsFinancialObjectTypeDebitCreditCode;
    }

    /**
     * Saves a generated origin entry to the database, within the proper group
     * 
     * @param balance the original balance, which still has the account to check if it is closed or not
     * @param entry the origin entry to save
     * @param closedPriorYearAccountGroup the group to put balance forwarding origin entries with closed accounts into
     * @param unclosedPriorYearAccountGroup the group to put balance forwarding origin entries with open accounts into
     */
    private void saveForwardingEntry(Balance balance, OriginEntryFull entry, PrintStream closedPs, PrintStream unclosedPs) {
        final PriorYearAccount account = priorYearAccountService.getByPrimaryKey(balance.getChartOfAccountsCode(), balance.getAccountNumber());
        if (ObjectUtils.isNotNull(account) && !account.isClosed()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Prior Year Account "+account.getChartOfAccountsCode()+"-"+account.getAccountNumber()+" is not closed");
            }
            originEntryService.createEntry(entry, unclosedPs);
            state.incrementSequenceWriteCount();
            openAccountForwardBalanceLedgerReport.summarizeEntry(entry);

            if (0 == state.getSequenceWriteCount() % 1000) {
                LOG.info("  SEQUENTIAL RECORDS WRITTEN = " + state.getSequenceWriteCount());
            }
        }
        else {
            if (LOG.isDebugEnabled()) {
                if (ObjectUtils.isNull(account)) {
                    LOG.debug("Prior Year Account for "+balance.getChartOfAccountsCode()+"-"+balance.getAccountNumber()+" cannot be found");
                } else {
                    LOG.debug("Prior Year Account "+account.getChartOfAccountsCode()+"-"+account.getAccountNumber()+" is closed");
                }
            }
            originEntryService.createEntry(entry, closedPs);
            state.incrementSequenceClosedCount();
            closedAccountForwardBalanceLedgerReport.summarizeEntry(entry);
            if (0 == state.getSequenceClosedCount() % 1000) {
                LOG.info("  CLOSED SEQUENTIAL RECORDS WRITTEN = " + state.getSequenceClosedCount());
            }
        }
    }

    /**
     * Writes the ledger report for general balance forward entries to the given reportWriterService
     * @param reportWriteService the reportWriterService to write to
     */
    public void writeOpenAccountBalanceForwardLedgerSummaryReport(ReportWriterService reportWriterService) {
        openAccountForwardBalanceLedgerReport.writeReport(reportWriterService);
    }
    
    /**
     * Writes the ledger report for cumulative balance forward entries to the given reportWriterService
     * @param reportWriteService the reportWriterService to write to
     */
    public void writeClosedAccountBalanceForwardLedgerSummaryReport(ReportWriterService reportWriterService) {
        closedAccountForwardBalanceLedgerReport.writeReport(reportWriterService);
    }
    
    /**
     * @param priorYearAccountService The priorYearAccountService to set.
     */
    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

    /**
     * @param subFundGroupService The subFundGroupService to set.
     */
    public void setSubFundGroupService(SubFundGroupService subFundGroupService) {
        this.subFundGroupService = subFundGroupService;
    }

    /**
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public Integer getClosingFiscalYear() {
        return closingFiscalYear;
    }

    public void setClosingFiscalYear(Integer fiscalYear) {
        this.closingFiscalYear = fiscalYear;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getBalanceForwardsUnclosedFileName() {
        return balanceForwardsUnclosedFileName;
    }

    public void setBalanceForwardsUnclosedFileName(String balanceForwardsUnclosedFileName) {
        this.balanceForwardsUnclosedFileName = balanceForwardsUnclosedFileName;
    }

    public String getBalanceForwardsclosedFileName() {
        return balanceForwardsclosedFileName;
    }

    public void setBalanceForwardsclosedFileName(String balanceForwardsclosedFileName) {
        this.balanceForwardsclosedFileName = balanceForwardsclosedFileName;
    }

    public BalanceForwardProcessState getState() {
        return state;
    }

    /**
     * Gets the glOriginationCode attribute. 
     * @return Returns the glOriginationCode.
     */
    public String getGlOriginationCode() {
        return glOriginationCode;
    }

    /**
     * Gets the annualClosingDocType attribute. 
     * @return Returns the annualClosingDocType.
     */
    public String getAnnualClosingDocType() {
        return annualClosingDocType;
    }
}
