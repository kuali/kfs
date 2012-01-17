/*
 * Copyright 2006-2009 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.AccountIntf;
import org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo;
import org.kuali.kfs.coa.businessobject.ClosedAccountOrganizationReversion;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategoryInfo;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic;
import org.kuali.kfs.gl.batch.service.OrganizationReversionProcess;
import org.kuali.kfs.gl.batch.service.OrganizationReversionUnitOfWorkService;
import org.kuali.kfs.gl.batch.service.impl.exception.FatalErrorException;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.OrgReversionUnitOfWork;
import org.kuali.kfs.gl.businessobject.OrgReversionUnitOfWorkCategoryAmount;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class actually runs the year end organization reversion process
 */
@Transactional
public class OrganizationReversionProcessImpl implements OrganizationReversionProcess, InitializingBean {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionProcessImpl.class);

    // Services
    private OrganizationReversionService organizationReversionService;
    private BalanceService balanceService;
    private OriginEntryService originEntryService;
    private PersistenceService persistenceService;
    private DateTimeService dateTimeService;
    private OrganizationReversionCategoryLogic cashOrganizationReversionCategoryLogic;
    private PriorYearAccountService priorYearAccountService;
    private OrganizationReversionUnitOfWorkService orgReversionUnitOfWorkService;
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private ParameterService parameterService;
    private ConfigurationService configurationService;

    private String batchFileDirectoryName;
    private String outputFileName;
    private OrgReversionUnitOfWork unitOfWork;
    private Map<String, OrganizationReversionCategoryLogic> categories;
    private List<OrganizationReversionCategory> categoryList;
    private CarryForwardReversionProcessOrganizationInfo organizationReversion;
    private AccountIntf account;

    private Map jobParameters;
    private Map<String, Integer> organizationReversionCounts;

    private boolean usePriorYearInformation;

    private boolean holdGeneratedOriginEntries = false;
    private List<OriginEntryFull> generatedOriginEntries;

    public String CARRY_FORWARD_OBJECT_CODE;
    public String DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE;
    public String DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE;
    public String DEFAULT_FINANCIAL_BALANCE_TYPE_CODE;
    public String DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END;
    public String DEFAULT_DOCUMENT_NUMBER_PREFIX;

    private String CASH_REVERTED_TO_MESSAGE;
    private String FUND_BALANCE_REVERTED_TO_MESSAGE;
    private String CASH_REVERTED_FROM_MESSAGE;
    private String FUND_BALANCE_REVERTED_FROM_MESSAGE;
    private String FUND_CARRIED_MESSAGE;
    private String FUND_REVERTED_TO_MESSAGE;
    private String FUND_REVERTED_FROM_MESSAGE;

    private SystemOptions systemOptions;
    private Integer paramFiscalYear;
    
    private LedgerSummaryReport ledgerReport;
    
    private PrintStream outputPs;
     
    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        this.CARRY_FORWARD_OBJECT_CODE = getParameterService().getParameterValueAsString(OrganizationReversion.class, GeneralLedgerConstants.OrganizationReversionProcess.CARRY_FORWARD_OBJECT_CODE);
        this.DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE = getParameterService().getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_DOCUMENT_TYPE);
        this.DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE = getParameterService().getParameterValueAsString(OrganizationReversion.class, GeneralLedgerConstants.OrganizationReversionProcess.DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        this.DEFAULT_FINANCIAL_BALANCE_TYPE_CODE = getParameterService().getParameterValueAsString(OrganizationReversion.class, GeneralLedgerConstants.OrganizationReversionProcess.DEFAULT_FINANCIAL_BALANCE_TYPE_CODE);
        this.DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END = getParameterService().getParameterValueAsString(OrganizationReversion.class, GeneralLedgerConstants.OrganizationReversionProcess.DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END);
        this.DEFAULT_DOCUMENT_NUMBER_PREFIX = getParameterService().getParameterValueAsString(OrganizationReversion.class, GeneralLedgerConstants.OrganizationReversionProcess.DEFAULT_DOCUMENT_NUMBER_PREFIX);
        this.CASH_REVERTED_TO_MESSAGE = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.OrganizationReversionProcess.CASH_REVERTED_TO);
        this.FUND_BALANCE_REVERTED_TO_MESSAGE = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.OrganizationReversionProcess.FUND_BALANCE_REVERTED_TO);
        this.CASH_REVERTED_FROM_MESSAGE = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.OrganizationReversionProcess.CASH_REVERTED_FROM);
        this.FUND_BALANCE_REVERTED_FROM_MESSAGE = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.OrganizationReversionProcess.FUND_BALANCE_REVERTED_FROM);
        this.FUND_CARRIED_MESSAGE = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.OrganizationReversionProcess.FUND_CARRIED);
        this.FUND_REVERTED_TO_MESSAGE = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.OrganizationReversionProcess.FUND_REVERTED_TO);
        this.FUND_REVERTED_FROM_MESSAGE = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.OrganizationReversionProcess.FUND_REVERTED_FROM);
        
        outputFileName = getBatchFileDirectoryName() + File.separator + (usePriorYearInformation ? GeneralLedgerConstants.BatchFileSystem.ORGANIZATION_REVERSION_CLOSING_FILE : GeneralLedgerConstants.BatchFileSystem.ORGANIZATION_REVERSION_PRE_CLOSING_FILE) + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
    }

    /**
     * This evilly named method actually runs the organization reversion process.
     */
    public void organizationReversionProcess(Map jobParameters, Map<String, Integer> organizationReversionCounts) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("organizationReversionProcess() started");
        }
        this.jobParameters = jobParameters;
        this.organizationReversionCounts = organizationReversionCounts;

        LOG.info("Initializing the process");
        initializeProcess();
        
        //create files
        File outputFile = new File(outputFileName);
        
        try {
            outputPs = new PrintStream(outputFile);
        
            Iterator<Balance> balances = getBalanceService().findOrganizationReversionBalancesForFiscalYear((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR), usePriorYearInformation);
            processBalances(balances);
            
            outputPs.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Organization Reversion File Files doesn't exist " + outputFileName);
        }

    }

    /**
     * Given a list of balances, this method generates the origin entries for the organization reversion/carry forward process, and saves those
     * to an initialized origin entry group
     * 
     * @param balances an iterator of balances to process; each balance returned by the iterator will be processed by this method
     */
    public void processBalances(Iterator<Balance> balances) {
        boolean skipToNextUnitOfWork = false;
        unitOfWork = new OrgReversionUnitOfWork();
        unitOfWork.setCategories(categoryList);
        
        Balance bal;
        while (balances.hasNext()) {
            bal = balances.next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("BALANCE SELECTED: " + bal.getUniversityFiscalYear() + bal.getChartOfAccountsCode() + bal.getAccountNumber() + bal.getSubAccountNumber() + bal.getObjectCode() + bal.getSubObjectCode() + bal.getBalanceTypeCode() + bal.getObjectTypeCode() + " " + bal.getAccountLineAnnualBalanceAmount().add(bal.getBeginningBalanceLineAmount()));
            }

            try {
                if (!unitOfWork.isInitialized()) {
                    unitOfWork.setFields(bal.getChartOfAccountsCode(), bal.getAccountNumber(), bal.getSubAccountNumber());
                    retrieveCurrentReversionAndAccount(bal);
                }
                else if (!unitOfWork.wouldHold(bal)) {
                    if (!skipToNextUnitOfWork) {
                        calculateTotals();
                        List<OriginEntryFull> originEntriesToWrite = generateOutputOriginEntries();
                        summarizeOriginEntries(originEntriesToWrite);
                        if (holdGeneratedOriginEntries) {
                            generatedOriginEntries.addAll(originEntriesToWrite);
                        }
                        int recordsWritten = writeOriginEntries(originEntriesToWrite);
                        incrementCount("recordsWritten", recordsWritten);
                        getOrgReversionUnitOfWorkService().save(unitOfWork);
                    }
                    unitOfWork.setFields(bal.getChartOfAccountsCode(), bal.getAccountNumber(), bal.getSubAccountNumber());
                    retrieveCurrentReversionAndAccount(bal);
                    skipToNextUnitOfWork = false;
                }
                if (skipToNextUnitOfWork) {
                    continue; // if there is no org reversion or an org reversion detail is missing or the balances are off for
                    // this unit of work,
                    // just skip all the balances until we change unit of work
                }
                calculateBucketAmounts(bal);
            }
            catch (FatalErrorException fee) {
                LOG.info(fee.getMessage());
                skipToNextUnitOfWork = true;
            }
        }
        // save the final unit of work
        if (!skipToNextUnitOfWork && getBalancesSelected() > 0) {
            try {
                calculateTotals();
                List<OriginEntryFull> originEntriesToWrite = generateOutputOriginEntries();
                summarizeOriginEntries(originEntriesToWrite);
                if (holdGeneratedOriginEntries) {
                    generatedOriginEntries.addAll(originEntriesToWrite);
                }
                int recordsWritten = writeOriginEntries(originEntriesToWrite);
                incrementCount("recordsWritten", recordsWritten);
                getOrgReversionUnitOfWorkService().save(unitOfWork);
            }
            catch (FatalErrorException fee) {
                LOG.info(fee.getMessage());
            }
        }
        
    }

    /**
     * Given a balance, returns the current organization reversion record and account or prior year account for the balance; it sets them
     * to private properties
     * 
     * @param bal the balance to find the account/prior year account and organization reversion record for 
     * @throws FatalErrorException if an organization reversion record cannot be found in the database 
     */
    protected void retrieveCurrentReversionAndAccount(Balance bal) throws FatalErrorException {
        // initialize the account
        if ((account == null) || (!bal.getChartOfAccountsCode().equals(account.getChartOfAccountsCode())) || (!bal.getAccountNumber().equals(account.getAccountNumber()))) {
            if (usePriorYearInformation) {
                account = getPriorYearAccountService().getByPrimaryKey(bal.getChartOfAccountsCode(), bal.getAccountNumber());
            }
            else {
                account = bal.getAccount();
            }
        }

        if ((organizationReversion == null) || (!organizationReversion.getChartOfAccountsCode().equals(bal.getChartOfAccountsCode())) || (!organizationReversion.getOrganizationCode().equals(account.getOrganizationCode()))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Organization Reversion Service: " + getOrganizationReversionService() + "; fiscal year: " + (Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR) + "; account: " + account + "; account organization code: " + account.getOrganizationCode() + "; balance: " + bal + "; balance chart: " + bal.getChartOfAccountsCode());
            }
            organizationReversion = getOrganizationReversionService().getByPrimaryId((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR), bal.getChartOfAccountsCode(), account.getOrganizationCode());
        }

        if (organizationReversion == null) {
            // we can't find an organization reversion for this balance? Throw exception
            throw new FatalErrorException("No Organization Reversion found for: " + (Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR) + "-" + bal.getChartOfAccountsCode() + "-" + account.getOrganizationCode());
        }
        
        if (account.isClosed()) {
            organizationReversion = new ClosedAccountOrganizationReversion(organizationReversion);
        }
    }

    /**
     * This method initializes several properties needed for the process to run correctly
     */
    public void initializeProcess() {

        // clear out summary tables
        LOG.info("destroying all unit of work summaries");
        orgReversionUnitOfWorkService.destroyAllUnitOfWorkSummaries();

        categories = getOrganizationReversionService().getCategories();
        categoryList = getOrganizationReversionService().getCategoryList();

        this.paramFiscalYear = (Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR);

        organizationReversionCounts.put("balancesRead", balanceService.countBalancesForFiscalYear(paramFiscalYear));
        organizationReversionCounts.put("balancesSelected", new Integer(0));
        organizationReversionCounts.put("recordsWritten", new Integer(0));

        this.systemOptions = SpringContext.getBean(OptionsService.class).getOptions(paramFiscalYear);
        
        ledgerReport = new LedgerSummaryReport();
    }

    /**
     * Depending on the category that this balance belongs to, adds the balance to the appropriate bucket 
     * 
     * @param bal the current balance to process
     */
    protected void calculateBucketAmounts(Balance bal) {
        getPersistenceService().retrieveReferenceObject(bal, "financialObject");

        if (LOG.isDebugEnabled()) {
            LOG.debug("CONSIDERING IF TO ADD BALANCE: " + bal.getUniversityFiscalYear() + bal.getChartOfAccountsCode() + bal.getAccountNumber() + bal.getSubAccountNumber() + bal.getObjectCode() + bal.getSubObjectCode() + bal.getBalanceTypeCode() + bal.getObjectTypeCode() + " " + bal.getAccountLineAnnualBalanceAmount().add(bal.getBeginningBalanceLineAmount()));
        }

        if (getCashOrganizationReversionCategoryLogic().containsObjectCode(bal.getFinancialObject()) && bal.getBalanceTypeCode().equals(systemOptions.getActualFinancialBalanceTypeCd())) {
            unitOfWork.addTotalCash(bal.getBeginningBalanceLineAmount());
            unitOfWork.addTotalCash(bal.getAccountLineAnnualBalanceAmount());
            incrementCount("balancesSelected");
            if (LOG.isDebugEnabled()) {
                LOG.debug("ADDING BALANCE TO CASH: " + bal.getUniversityFiscalYear() + bal.getChartOfAccountsCode() + bal.getAccountNumber() + bal.getSubAccountNumber() + bal.getObjectCode() + bal.getSubObjectCode() + bal.getBalanceTypeCode() + bal.getObjectTypeCode() + " " + bal.getAccountLineAnnualBalanceAmount().add(bal.getBeginningBalanceLineAmount()) + " TO CASH, TOTAL CASH NOW = " + unitOfWork.getTotalCash());
            }
        }
        else {
            for (OrganizationReversionCategory cat : categoryList) {
                OrganizationReversionCategoryLogic logic = categories.get(cat.getOrganizationReversionCategoryCode());
                if (logic.containsObjectCode(bal.getFinancialObject())) {
                    if (systemOptions.getActualFinancialBalanceTypeCd().equals(bal.getBalanceTypeCode())) {
                        // Actual
                        unitOfWork.addActualAmount(cat.getOrganizationReversionCategoryCode(), bal.getBeginningBalanceLineAmount());
                        unitOfWork.addActualAmount(cat.getOrganizationReversionCategoryCode(), bal.getAccountLineAnnualBalanceAmount());
                        incrementCount("balancesSelected");
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ADDING BALANCE TO ACTUAL: " + bal.getUniversityFiscalYear() + bal.getChartOfAccountsCode() + bal.getAccountNumber() + bal.getSubAccountNumber() + bal.getObjectCode() + bal.getSubObjectCode() + bal.getBalanceTypeCode() + bal.getObjectTypeCode() + " " + bal.getAccountLineAnnualBalanceAmount().add(bal.getBeginningBalanceLineAmount()) + " TO ACTUAL, ACTUAL FOR CATEGORY " + cat.getOrganizationReversionCategoryName() + " NOW = " + unitOfWork.getCategoryAmounts().get(cat.getOrganizationReversionCategoryCode()).getActual());
                        }
                    }
                    else if (systemOptions.getFinObjTypeExpenditureexpCd().equals(bal.getBalanceTypeCode()) || systemOptions.getCostShareEncumbranceBalanceTypeCd().equals(bal.getBalanceTypeCode()) || systemOptions.getIntrnlEncumFinBalanceTypCd().equals(bal.getBalanceTypeCode())) {
                        // Encumbrance
                        KualiDecimal amount = bal.getBeginningBalanceLineAmount().add(bal.getAccountLineAnnualBalanceAmount());
                        if (amount.isPositive()) {
                            unitOfWork.addEncumbranceAmount(cat.getOrganizationReversionCategoryCode(), amount);
                            incrementCount("balancesSelected");
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("ADDING BALANCE TO ENCUMBRANCE: " + bal.getUniversityFiscalYear() + bal.getChartOfAccountsCode() + bal.getAccountNumber() + bal.getSubAccountNumber() + bal.getObjectCode() + bal.getSubObjectCode() + bal.getBalanceTypeCode() + bal.getObjectTypeCode() + " " + bal.getAccountLineAnnualBalanceAmount().add(bal.getBeginningBalanceLineAmount()) + " TO ENCUMBRANCE, ENCUMBRANCE FOR CATEGORY " + cat.getOrganizationReversionCategoryName() + " NOW = " + unitOfWork.getCategoryAmounts().get(cat.getOrganizationReversionCategoryCode()).getEncumbrance());
                            }
                        }
                    }
                    else if (KFSConstants.BALANCE_TYPE_CURRENT_BUDGET.equals(bal.getBalanceTypeCode())) {
                        // Budget
                        if (!CARRY_FORWARD_OBJECT_CODE.equals(bal.getObjectCode())) {
                            unitOfWork.addBudgetAmount(cat.getOrganizationReversionCategoryCode(), bal.getBeginningBalanceLineAmount());
                            unitOfWork.addBudgetAmount(cat.getOrganizationReversionCategoryCode(), bal.getAccountLineAnnualBalanceAmount());
                            incrementCount("balancesSelected");
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("ADDING BALANCE TO BUDGET: " + bal.getUniversityFiscalYear() + bal.getChartOfAccountsCode() + bal.getAccountNumber() + bal.getSubAccountNumber() + bal.getObjectCode() + bal.getSubObjectCode() + bal.getBalanceTypeCode() + bal.getObjectTypeCode() + " " + bal.getAccountLineAnnualBalanceAmount().add(bal.getBeginningBalanceLineAmount()) + " TO CURRENT BUDGET, CURRENT BUDGET FOR CATEGORY " + cat.getOrganizationReversionCategoryName() + " NOW = " + unitOfWork.getCategoryAmounts().get(cat.getOrganizationReversionCategoryCode()).getBudget());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * This method determines which origin entries (reversion, cash reversion, or carry forward) need to be generated for the current unit of work,
     * and then delegates to the origin entry generation methods to create those entries
     * 
     * @return a list of OriginEntries which need to be written
     * @throws FatalErrorException thrown if object codes are missing in any of the generation methods
     */
    public List<OriginEntryFull> generateOutputOriginEntries() throws FatalErrorException {
        List<OriginEntryFull> originEntriesToWrite = new ArrayList<OriginEntryFull>();
        if (unitOfWork.getTotalReversion().compareTo(KualiDecimal.ZERO) != 0) {
            generateReversions(originEntriesToWrite);
        }
        if ((unitOfWork.getTotalCarryForward().compareTo(KualiDecimal.ZERO) != 0)) {
            if (!organizationReversion.isCarryForwardByObjectCodeIndicator()) {
                generateCarryForwards(originEntriesToWrite);
            }
            else {
                generateMany(originEntriesToWrite);
            }
        }
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) != 0) {
            generateCashReversions(originEntriesToWrite);
        }
        return originEntriesToWrite;
    }

    /**
     * This method writes a list of OriginEntryFulls to a given origin entry group
     * 
     * @param writeGroup the origin entry group to write to
     * @param originEntriesToWrite a list of origin entry fulls to write
     * @return the count of origin entries that were written
     */
    protected int writeOriginEntries(List<OriginEntryFull> originEntriesToWrite) {
        int originEntriesWritten = 0;

        for (OriginEntryFull originEntry : originEntriesToWrite) {
            getOriginEntryService().createEntry(originEntry, outputPs);
            originEntriesWritten += 1;
        }

        return originEntriesWritten;
    }

    /**
     * This method starts the creation of an origin entry, by setting fields that are the same in every Org Rev origin entries
     * 
     * @return an OriginEntryFull partially filled out with constant information
     */
    protected OriginEntryFull getEntry() {
        OriginEntryFull entry = new OriginEntryFull();
        entry.setUniversityFiscalYear((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR));
        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);
        entry.setFinancialDocumentTypeCode(DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE);
        entry.setFinancialSystemOriginationCode(DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        entry.setTransactionLedgerEntrySequenceNumber(1);
        entry.setTransactionDebitCreditCode(KFSConstants.GL_BUDGET_CODE);
        entry.setTransactionDate((Date) jobParameters.get(KFSConstants.TRANSACTION_DT));
        entry.setProjectCode(KFSConstants.getDashProjectCode());
        return entry;
    }

    /**
     * This method generates cash reversion origin entries for the current organization reversion, and adds them to the given list
     * 
     * @param originEntriesToWrite a list of OriginEntryFulls to stick generated origin entries into
     * @throws FatalErrorException thrown if an origin entry's object code can't be found
     */
    public void generateCashReversions(List<OriginEntryFull> originEntriesToWrite) throws FatalErrorException {
        int entriesWritten = 0;
        
        // Reversion of cash from the actual account in the fiscal year ending (balance type of NB)
        OriginEntryFull entry = getEntry();
        entry.refreshReferenceObject("option");

        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode(organizationReversion.getOrganizationChartCashObjectCode());
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(systemOptions.getNominalFinancialBalanceTypeCd());

        getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (ObjectUtils.isNull(entry.getFinancialObject())) {
            throw new FatalErrorException("Object Code for Entry not found: " + entry);
        }

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + entry.getAccountNumber());
        entry.setTransactionLedgerEntryDescription(CASH_REVERTED_TO_MESSAGE + " " + organizationReversion.getCashReversionAccountNumber());
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash());
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) > 0) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash().negated());
        }
        entry.setFinancialObjectTypeCode(entry.getFinancialObject().getFinancialObjectTypeCode());

        // 3468 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3469 WS-AMT-N.
        // 3470 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntriesToWrite.add(entry);

        // Reversion of fund balance, starting with the actual account, to match the cash that was reverted (balance type of NB) 
        entry = getEntry();
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode((String) jobParameters.get(KFSConstants.FUND_BAL_OBJECT_CD));
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE);

        getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (ObjectUtils.isNull(entry.getFinancialObject())) {
            throw new FatalErrorException("Object Code for Entry not found: " + entry);
        }

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntryDescription(FUND_BALANCE_REVERTED_TO_MESSAGE + organizationReversion.getCashReversionAccountNumber());
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash().abs());
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) > 0) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        entry.setFinancialObjectTypeCode(entry.getFinancialObject().getFinancialObjectTypeCode());

        // 3570 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3571 WS-AMT-N.
        // 3572 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        getFlexibleOffsetAccountService().updateOffset(entry);
        originEntriesToWrite.add(entry);

        // Reversion of cash to the cash reversion account in the fiscal year ending (balance type of NB)
        entry = getEntry();
        entry.setChartOfAccountsCode(organizationReversion.getCashReversionFinancialChartOfAccountsCode());
        entry.setAccountNumber(organizationReversion.getCashReversionAccountNumber());
        entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        entry.setFinancialObjectCode(organizationReversion.getCashReversionChartCashObjectCode());
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE);

        getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (ObjectUtils.isNull(entry.getFinancialObject())) {
            throw new FatalErrorException("Object Code for Entry not found: " + entry);
        }

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntryDescription(CASH_REVERTED_FROM_MESSAGE + unitOfWork.accountNumber + " " + unitOfWork.subAccountNumber);
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash());
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) > 0) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash().negated());
        }
        entry.setFinancialObjectTypeCode(entry.getFinancialObject().getFinancialObjectTypeCode());

        // 3668 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3669 WS-AMT-N.
        // 3670 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntriesToWrite.add(entry);

        // Reversion of fund balance, starting with the cash reversion account, to match the cash that was reverted (balance type of NB) 
        entry = getEntry();
        entry.setChartOfAccountsCode(organizationReversion.getCashReversionFinancialChartOfAccountsCode());
        entry.setAccountNumber(organizationReversion.getCashReversionAccountNumber());
        entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        entry.setFinancialObjectCode((String) jobParameters.get(KFSConstants.FUND_BAL_OBJECT_CD));
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE);

        getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (ObjectUtils.isNull(entry.getFinancialObject())) {
            throw new FatalErrorException("Object Code for Entry not found: " + entry);
        }

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntryDescription(FUND_BALANCE_REVERTED_FROM_MESSAGE + unitOfWork.accountNumber + " " + unitOfWork.subAccountNumber);
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash());
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) > 0) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash().negated());
        }
        entry.setFinancialObjectTypeCode(entry.getFinancialObject().getFinancialObjectTypeCode());

        // 3768 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3769 WS-AMT-N.
        // 3770 MOVE WS-AMT-X TO TRN-AMT-RED-X.
        
        getFlexibleOffsetAccountService().updateOffset(entry);
        originEntriesToWrite.add(entry);
    }

    /**
     * Generates carry forward origin entries on a category by category basis (if the organization reversion record asks for that), assuming carry
     * forwards are required for the current unit of work
     * 
     * @param originEntriesToWrite a list of origin entries to write, which any generated origin entries should be added to
     * @throws FatalErrorException thrown if an object code cannot be found
     */
    public void generateMany(List<OriginEntryFull> originEntriesToWrite) throws FatalErrorException {
        int originEntriesCreated = 0;
        for (Iterator<OrganizationReversionCategory> iter = categoryList.iterator(); iter.hasNext();) {
            OrganizationReversionCategory cat = iter.next();
            OrganizationReversionCategoryInfo detail = organizationReversion.getOrganizationReversionDetail(cat.getOrganizationReversionCategoryCode());
            OrgReversionUnitOfWorkCategoryAmount amount = unitOfWork.amounts.get(cat.getOrganizationReversionCategoryCode());

            if (!amount.getCarryForward().isZero()) {
                KualiDecimal commonAmount = amount.getCarryForward();
                String commonObject = detail.getOrganizationReversionObjectCode();

                OriginEntryFull entry = getEntry();
                entry.setUniversityFiscalYear((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR) + 1);
                entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
                entry.setAccountNumber(unitOfWork.accountNumber);
                entry.setSubAccountNumber(unitOfWork.subAccountNumber);
                entry.setFinancialObjectCode((String) jobParameters.get(KFSConstants.BEG_BUD_CASH_OBJECT_CD));
                entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);

                getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
                if (ObjectUtils.isNull(entry.getFinancialObject())) {
                    throw new FatalErrorException("Object Code for Entry not found: " + entry);
                }

                ObjectCode objectCode = entry.getFinancialObject();
                entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
                entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH1);
                entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
                entry.setTransactionLedgerEntryDescription(FUND_CARRIED_MESSAGE + (Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR));
                entry.setTransactionLedgerEntryAmount(commonAmount);

                // 3259 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
                // 3260 WS-AMT-N.
                // 3261 MOVE WS-AMT-X TO TRN-AMT-RED-X.

                originEntriesToWrite.add(entry);

                entry = getEntry();
                entry.setUniversityFiscalYear((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR) + 1);
                entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
                entry.setAccountNumber(unitOfWork.accountNumber);
                entry.setSubAccountNumber(unitOfWork.subAccountNumber);

                entry.setFinancialObjectCode(commonObject);
                entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);

                getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
                if (ObjectUtils.isNull(entry.getFinancialObject())) {
                    throw new FatalErrorException("Object Code for Entry not found: " + entry);
                }

                objectCode = entry.getFinancialObject();
                entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
                entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH1);
                entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
                entry.setTransactionLedgerEntryDescription(FUND_CARRIED_MESSAGE + (Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR));
                entry.setTransactionLedgerEntryAmount(commonAmount);

                // 3343 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
                // 3344 WS-AMT-N.
                // 3345 MOVE WS-AMT-X TO TRN-AMT-RED-X.

                originEntriesToWrite.add(entry);
            }
        }
    }

    /**
     * If carry forwards need to be generated for this unit of work, this method will generate the origin entries to accomplish those object codes.
     * Note: this will only be called if the organization reversion record tells the process to munge all carry forwards for all categories
     * together; if the organization reversion record does not call for such a thing, then generateMany will be called
     * 
     * @param originEntriesToWrite a list of origin entries to write, that any generated origin entries should be added to
     * @throws FatalErrorException thrown if the current object code can't be found in the database
     */
    public void generateCarryForwards(List<OriginEntryFull> originEntriesToWrite) throws FatalErrorException {
        int originEntriesWritten = 0;

        OriginEntryFull entry = getEntry();
        entry.setUniversityFiscalYear((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR) + 1);
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode((String) jobParameters.get(KFSConstants.BEG_BUD_CASH_OBJECT_CD));
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);

        getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (ObjectUtils.isNull(entry.getFinancialObject())) {
            throw new FatalErrorException("Object Code for Entry not found: " + entry);
        }

        ObjectCode objectCode = entry.getFinancialObject();
        entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH1);
        entry.setFinancialDocumentTypeCode(DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE);
        entry.setFinancialSystemOriginationCode(DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntrySequenceNumber(1);
        entry.setTransactionLedgerEntryDescription(FUND_CARRIED_MESSAGE + (Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR));
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCarryForward());
        entry.setTransactionDate((Date) jobParameters.get(KFSConstants.TRANSACTION_DT));
        entry.setProjectCode(KFSConstants.getDashProjectCode());
        // 2995 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 2996 WS-AMT-N.
        // 2997 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntriesToWrite.add(entry);

        entry = getEntry();
        entry.setUniversityFiscalYear((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR) + 1);
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode((String) jobParameters.get(KFSConstants.UNALLOC_OBJECT_CD));

        getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (ObjectUtils.isNull(entry.getFinancialObject())) {
            throw new FatalErrorException("Object Code for Entry not found: " + entry);
        }

        objectCode = entry.getFinancialObject();
        entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);
        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH1);
        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntryDescription(FUND_CARRIED_MESSAGE + (Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR));
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCarryForward());

        // 3079 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3080 WS-AMT-N.
        // 3081 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntriesToWrite.add(entry);

    }

    /**
     * If reversions are necessary, this will generate the origin entries for those reversions
     * 
     * @param originEntriesToWrite the list of origin entries to add reversions into
     * @throws FatalErrorException thrown if object code if the entry can't be found
     */
    public void generateReversions(List<OriginEntryFull> originEntriesToWrite) throws FatalErrorException {
        int originEntriesWritten = 0;

        OriginEntryFull entry = getEntry();
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode((String) jobParameters.get(KFSConstants.UNALLOC_OBJECT_CD));
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END);

        getPersistenceService().retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (ObjectUtils.isNull(entry.getFinancialObject())) {
            throw new FatalErrorException("Object Code for Entry not found: " + entry);
        }

        ObjectCode objectCode = entry.getFinancialObject();
        entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + entry.getAccountNumber());

        entry.setTransactionLedgerEntryDescription(FUND_REVERTED_TO_MESSAGE + organizationReversion.getBudgetReversionAccountNumber());
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalReversion().negated());

        originEntriesToWrite.add(entry);

        entry = getEntry();
        entry.setChartOfAccountsCode(organizationReversion.getBudgetReversionChartOfAccountsCode());
        entry.setAccountNumber(organizationReversion.getBudgetReversionAccountNumber());
        entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        entry.setFinancialObjectCode((String) jobParameters.get(KFSConstants.UNALLOC_OBJECT_CD));
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END);
        entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);
        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        if (unitOfWork.accountNumber.equals(KFSConstants.getDashSubAccountNumber())) {
            entry.setTransactionLedgerEntryDescription(FUND_REVERTED_FROM_MESSAGE + unitOfWork.accountNumber);
        }
        else {
            entry.setTransactionLedgerEntryDescription(FUND_REVERTED_FROM_MESSAGE + unitOfWork.accountNumber + " " + unitOfWork.subAccountNumber);
        }
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalReversion());

        // 2899 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 2900 WS-AMT-N.
        // 2901 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntriesToWrite.add(entry);
    }

    /**
     * This method calculates the totals for a given unit of work's reversion
     * 
     * @throws FatalErrorException
     */
    public void calculateTotals() throws FatalErrorException {
        /*
         * How this works: At the start, in the clearCalculationTotals(), both the unit of work's totalAvailable and totalReversion
         * are set to the available amounts from each of the category amounts. Then, as the logic is applied, the totalCarryForward
         * is added to and the totalReversion is subtracted from. Let's look at a simple example: Let's say you've got an amount for
         * C01, which has $2000 available, no encumbrances, that's all you've got. This means that at the end of
         * clearCalculationTotals(), there's $2000 in totalAvailable, $2000 in totalReversion, and $0 in totalCarryForward. Now, C01,
         * let's say, is for code A. So, look below at the if that catches Code A. You'll note that it adds the available amount to
         * totalCarryForward, it's own carryForward, the negated available to totalReversion, and that, done, it sets available to
         * $0. With our example, that means that $2000 is in totalCarryForward (and in the amount's carryForward), the
         * totalReversion has been knocked down to $0, and the available is $0. So, carry forward origin entries get created, and
         * reversions do not. This is also why you don't see a block about calculating R2 totals below...the process has a natural
         * inclination towards creating R2 (ie, ignore encumbrances and revert all available) entries.
         */

        // clear out the unit of work totals we're going to calculate values in, in preperation for applying rules
        clearCalculationTotals();

        // For each category, apply the rules
        for (OrganizationReversionCategory category : categoryList) {
            String categoryCode = category.getOrganizationReversionCategoryCode();
            OrganizationReversionCategoryLogic logic = categories.get(categoryCode);
            OrgReversionUnitOfWorkCategoryAmount amount = unitOfWork.amounts.get(categoryCode);

            OrganizationReversionCategoryInfo detail = organizationReversion.getOrganizationReversionDetail(categoryCode);

            if (detail == null) {
                throw new FatalErrorException("Organization Reversion " + organizationReversion.getUniversityFiscalYear() + "-" + organizationReversion.getChartOfAccountsCode() + "-" + organizationReversion.getOrganizationCode() + " does not have a detail for category " + categoryCode);
            }
            String ruleCode = detail.getOrganizationReversionCode();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Unit of Work: " + unitOfWork.getChartOfAccountsCode() + unitOfWork.getAccountNumber() + unitOfWork.getSubAccountNumber() + ", category " + category.getOrganizationReversionCategoryName() + ": budget = " + amount.getBudget() + "; actual = " + amount.getActual() + "; encumbrance = " + amount.getEncumbrance() + "; available = " + amount.getAvailable() + "; apply rule code " + ruleCode);
            }

            if (KFSConstants.RULE_CODE_R1.equals(ruleCode) || KFSConstants.RULE_CODE_N1.equals(ruleCode) || KFSConstants.RULE_CODE_C1.equals(ruleCode)) {
                if (amount.getAvailable().compareTo(KualiDecimal.ZERO) > 0) { // do we have budget left?
                    if (amount.getAvailable().compareTo(amount.getEncumbrance()) > 0) { // is it more than enough to cover our
                        // encumbrances?
                        unitOfWork.addTotalCarryForward(amount.getEncumbrance());
                        amount.addCarryForward(amount.getEncumbrance());
                        unitOfWork.addTotalReversion(amount.getEncumbrance().negated());
                        amount.addAvailable(amount.getEncumbrance().negated());
                    }
                    else {
                        // there's not enough available left to cover the encumbrances; cover what we can
                        unitOfWork.addTotalCarryForward(amount.getAvailable());
                        amount.addCarryForward(amount.getAvailable());
                        unitOfWork.addTotalReversion(amount.getAvailable().negated());
                        amount.setAvailable(KualiDecimal.ZERO);
                    }
                }
            }

            if (KFSConstants.RULE_CODE_A.equals(ruleCode)) {
                unitOfWork.addTotalCarryForward(amount.getAvailable());
                amount.addCarryForward(amount.getAvailable());
                unitOfWork.addTotalReversion(amount.getAvailable().negated());
                amount.setAvailable(KualiDecimal.ZERO);
            }

            if (KFSConstants.RULE_CODE_C1.equals(ruleCode) || KFSConstants.RULE_CODE_C2.equals(ruleCode)) {
                if (amount.getAvailable().compareTo(KualiDecimal.ZERO) > 0) {
                    unitOfWork.addTotalCarryForward(amount.getAvailable());
                    amount.addCarryForward(amount.getAvailable());
                    unitOfWork.addTotalReversion(amount.getAvailable().negated());
                    amount.setAvailable(KualiDecimal.ZERO);
                }
            }

            if (KFSConstants.RULE_CODE_N1.equals(ruleCode) || KFSConstants.RULE_CODE_N2.equals(ruleCode)) {
                if (amount.getAvailable().compareTo(KualiDecimal.ZERO) < 0) {
                    unitOfWork.addTotalCarryForward(amount.getAvailable());
                    amount.addCarryForward(amount.getAvailable());
                    unitOfWork.addTotalReversion(amount.getAvailable().negated());
                    amount.setAvailable(KualiDecimal.ZERO);
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Totals Now: " + unitOfWork.getChartOfAccountsCode() + unitOfWork.getAccountNumber() + unitOfWork.getSubAccountNumber() + ", total cash now " + unitOfWork.getTotalCash() + ": total available = " + unitOfWork.getTotalAvailable() + "; total reversion = " + unitOfWork.getTotalReversion() + "; total carry forward = " + unitOfWork.getTotalCarryForward());
            }
        }
    }

    /**
     * This method clears the unit of work's amounts to what they should be before each category bucket is calculated; specifically,
     * the total available for each category is calculated, and the total available and total reversion are set to the sum of all
     * available from each category bucket.  The total carry forward is set to 0.
     */
    protected void clearCalculationTotals() {
        // Initialize all the amounts before applying the proper rule
        KualiDecimal totalAvailable = KualiDecimal.ZERO;
        for (OrganizationReversionCategory category : categoryList) {
            OrganizationReversionCategoryLogic logic = categories.get(category.getOrganizationReversionCategoryCode());

            OrgReversionUnitOfWorkCategoryAmount amount = unitOfWork.amounts.get(category.getOrganizationReversionCategoryCode());
            if (logic.isExpense()) {
                amount.setAvailable(amount.getBudget().subtract(amount.getActual()));
            }
            else {
                amount.setAvailable(amount.getActual().subtract(amount.getBudget()));
            }
            totalAvailable = totalAvailable.add(amount.getAvailable());
            amount.setCarryForward(KualiDecimal.ZERO);
        }
        unitOfWork.setTotalAvailable(totalAvailable);
        unitOfWork.setTotalReversion(totalAvailable);
        unitOfWork.setTotalCarryForward(KualiDecimal.ZERO);
    }
    
    /**
     * Summarizes the given origin entries to the ledger report
     * @param originEntries the List of originEntries to summarize
     */
    protected void summarizeOriginEntries(List<OriginEntryFull> originEntries) {
        for (OriginEntryFull originEntry: originEntries) {
            ledgerReport.summarizeEntry(originEntry);
        }
    }

    public OrgReversionUnitOfWork getUnitOfWork() {
        return unitOfWork;
    }

    public void setUnitOfWork(OrgReversionUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public List<OrganizationReversionCategory> getCategoryList() {
        return this.categoryList;
    }

    /**
     * Gets the generatedOriginEntries attribute.
     * 
     * @return Returns the generatedOriginEntries.
     */
    public List<OriginEntryFull> getGeneratedOriginEntries() {
        return generatedOriginEntries;
    }

    /**
     * Sets the holdGeneratedOriginEntries attribute value.
     * 
     * @param holdGeneratedOriginEntries The holdGeneratedOriginEntries to set.
     */
    public void setHoldGeneratedOriginEntries(boolean holdGeneratedOriginEntries) {
        this.holdGeneratedOriginEntries = holdGeneratedOriginEntries;
        this.generatedOriginEntries = new ArrayList<OriginEntryFull>();
    }

    /**
     * Returns the total number of balances for the previous fiscal year
     * 
     * @return the total number of balances for the previous fiscal year
     */
    public int getBalancesRead() {
        return organizationReversionCounts.get("balancesRead").intValue();
    }

    /**
     * Returns the total number of balances selected for inclusion in this process
     * 
     * @return the total number of balances selected for inclusion in this process
     */
    public int getBalancesSelected() {
        return organizationReversionCounts.get("balancesSelected").intValue();
    }

    /**
     * Returns the total number of origin entries written by this process
     * 
     * @return the total number of origin entries written by this process
     */
    public int getRecordsWritten() {
        return organizationReversionCounts.get("recordsWritten").intValue();
    }

    /**
     * Used mainly for unit testing, this method allows a way to change the output group of a org reversion process run
     * 
     * @param outputGroup
     */
    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * Increments one of the totals held in the count map this process uses for reported statistics
     * 
     * @param countName the name of the count to increment
     */
    private void incrementCount(String countName) {
        incrementCount(countName, 1);
    }

    /**
     * Increments one of the totals held in the count map this process uses for reported statistics by a given increment
     * 
     * @param countName the name of the count to increment
     * @param increment the amount to increment
     */
    protected void incrementCount(String countName, int increment) {
        Integer count = organizationReversionCounts.get(countName);
        if (countName.equals("recordsWritten")) {
            int countAsInt = count.intValue();
            // add by 1, so we're guaranteed to hit the 1000th
            for (int i = 1; i <= increment; i++) {
                countAsInt += 1;
                if (countAsInt % 1000 == 0) {
                    LOG.info(" ORIGIN ENTRIES INSERTED = "+countAsInt);
                } else if (countAsInt == 367471) {
                    LOG.info(" YOU HAVE ACHIEVED 367471 ORIGIN ENTRIES INSERTED!  TRIUMPH IS YOURS!  ");
                }
            }
            organizationReversionCounts.put(countName, new Integer(countAsInt));
        } else {
            organizationReversionCounts.put(countName, new Integer(count.intValue() + increment));
        }
    }
    
    /**
     * Writes out the encapsulated origin entry ledger report to the given reportWriterService
     * @param reportWriterService the report to write the ledger summary report to
     */
    public void writeLedgerSummaryReport(ReportWriterService reportWriterService) {
        ledgerReport.writeReport(reportWriterService);
    }

    /**
     * Gets the organizationReversionService attribute. 
     * @return Returns the organizationReversionService.
     */
    public OrganizationReversionService getOrganizationReversionService() {
        return organizationReversionService;
    }

    /**
     * Sets the organizationReversionService attribute value.
     * @param organizationReversionService The organizationReversionService to set.
     */
    public void setOrganizationReversionService(OrganizationReversionService organizationReversionService) {
        this.organizationReversionService = organizationReversionService;
    }

    /**
     * Gets the balanceService attribute. 
     * @return Returns the balanceService.
     */
    public BalanceService getBalanceService() {
        return balanceService;
    }

    /**
     * Sets the balanceService attribute value.
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * Gets the originEntryService attribute. 
     * @return Returns the originEntryService.
     */
    public OriginEntryService getOriginEntryService() {
        return originEntryService;
    }

    /**
     * Sets the originEntryService attribute value.
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    /**
     * Gets the persistenceService attribute. 
     * @return Returns the persistenceService.
     */
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * Sets the persistenceService attribute value.
     * @param persistenceService The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Gets the dateTimeService attribute. 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the priorYearAccountService attribute. 
     * @return Returns the priorYearAccountService.
     */
    public PriorYearAccountService getPriorYearAccountService() {
        return priorYearAccountService;
    }

    /**
     * Sets the priorYearAccountService attribute value.
     * @param priorYearAccountService The priorYearAccountService to set.
     */
    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

    /**
     * Gets the orgReversionUnitOfWorkService attribute. 
     * @return Returns the orgReversionUnitOfWorkService.
     */
    public OrganizationReversionUnitOfWorkService getOrgReversionUnitOfWorkService() {
        return orgReversionUnitOfWorkService;
    }

    /**
     * Sets the orgReversionUnitOfWorkService attribute value.
     * @param orgReversionUnitOfWorkService The orgReversionUnitOfWorkService to set.
     */
    public void setOrgReversionUnitOfWorkService(OrganizationReversionUnitOfWorkService orgReversionUnitOfWorkService) {
        this.orgReversionUnitOfWorkService = orgReversionUnitOfWorkService;
    }

    /**
     * Gets the flexibleOffsetAccountService attribute. 
     * @return Returns the flexibleOffsetAccountService.
     */
    public FlexibleOffsetAccountService getFlexibleOffsetAccountService() {
        return flexibleOffsetAccountService;
    }

    /**
     * Sets the flexibleOffsetAccountService attribute value.
     * @param flexibleOffsetAccountService The flexibleOffsetAccountService to set.
     */
    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the configurationService attribute. 
     * @return Returns the configurationService.
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Sets the configurationService attribute value.
     * @param configurationService The configurationService to set.
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Gets the usePriorYearInformation attribute. 
     * @return Returns the usePriorYearInformation.
     */
    public boolean isUsePriorYearInformation() {
        return usePriorYearInformation;
    }

    /**
     * Sets the usePriorYearInformation attribute value.
     * @param usePriorYearInformation The usePriorYearInformation to set.
     */
    public void setUsePriorYearInformation(boolean endOfYear) {
        this.usePriorYearInformation = endOfYear;
    }

    /**
     * Gets the cashOrganizationReversionCategoryLogic attribute. 
     * @return Returns the cashOrganizationReversionCategoryLogic.
     */
    public OrganizationReversionCategoryLogic getCashOrganizationReversionCategoryLogic() {
        return cashOrganizationReversionCategoryLogic;
    }

    /**
     * Sets the cashOrganizationReversionCategoryLogic attribute value.
     * @param cashOrganizationReversionCategoryLogic The cashOrganizationReversionCategoryLogic to set.
     */
    public void setCashOrganizationReversionCategoryLogic(OrganizationReversionCategoryLogic cashOrganizationReversionCategoryLogic) {
        this.cashOrganizationReversionCategoryLogic = cashOrganizationReversionCategoryLogic;
    }


    /**
     * Gets the batchFileDirectoryName attribute. 
     * @return Returns the batchFileDirectoryName.
     */
    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    /**
     * Sets the jobParameters attribute value.
     * @param jobParameters The jobParameters to set.
     */
    public void setJobParameters(Map jobParameters) {
        this.jobParameters = jobParameters;
    }

    /**
     * Sets the organizationReversionCounts attribute value.
     * @param organizationReversionCounts The organizationReversionCounts to set.
     */
    public void setOrganizationReversionCounts(Map<String, Integer> organizationReversionCounts) {
        this.organizationReversionCounts = organizationReversionCounts;
    }
    
}
