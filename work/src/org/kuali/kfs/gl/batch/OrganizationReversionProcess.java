/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl.orgreversion;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountIntf;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.chart.bo.OrganizationReversionDetail;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.OrgReversionUnitOfWork;
import org.kuali.module.gl.bo.OrgReversionUnitOfWorkCategoryAmount;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.service.OrgReversionUnitOfWorkService;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.kuali.module.gl.service.OrganizationReversionSelection;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.util.FatalErrorException;
import org.kuali.module.gl.util.Summary;

public class OrganizationReversionProcess {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionProcess.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    // Services
    private OrganizationReversionService organizationReversionService;
    private KualiConfigurationService kualiConfigurationService;
    private BalanceService balanceService;
    private OrganizationReversionSelection organizationReversionSelection;
    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryService originEntryService;
    private PersistenceService persistenceService;
    private DateTimeService dateTimeService;
    private OrganizationReversionCategoryLogic cashOrganizationReversionCategoryLogic;
    private PriorYearAccountService priorYearAccountService;
    private ReportService reportService;
    private OrgReversionUnitOfWorkService orgReversionUnitOfWorkService;

    // Job Parameters
    private Integer paramUniversityFiscalYear;
    private java.sql.Date paramTransactionDate;
    private String paramUnallocObjectCode;
    private String paramBegBudgetCashObjectCode;
    private String paramFundBalanceObjectCode;

    private OriginEntryGroup outputGroup;
    private java.util.Date runDate;
    private OrgReversionUnitOfWork unitOfWork;
    private Map<String, OrganizationReversionCategoryLogic> categories;
    private List<OrganizationReversionCategory> categoryList;
    private OrganizationReversion organizationReversion;
    private AccountIntf account;

    private boolean endOfYear;

    public final String[] ORGANIZATION_REVERSION_COA;
    public final String CARRY_FORWARD_OBJECT_CODE;
    public final String DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE;
    public final String DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE;
    public final String DEFAULT_FINANCIAL_BALANCE_TYPE_CODE;
    public final String DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END;
    public final String DEFAULT_DOCUMENT_NUMBER_PREFIX;
    
    public OrganizationReversionProcess() {
        super();

        KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        this.ORGANIZATION_REVERSION_COA = kualiConfigurationService.getApplicationParameterValues(GLConstants.GL_ORGANIZATION_REVERSION_PROCESS_GROUP, GLConstants.OrganizationReversionProcess.ORGANIZATION_REVERSION_COA);
        this.CARRY_FORWARD_OBJECT_CODE = kualiConfigurationService.getApplicationParameterValue(GLConstants.GL_ORGANIZATION_REVERSION_PROCESS_GROUP, GLConstants.OrganizationReversionProcess.CARRY_FORWARD_OBJECT_CODE);
        this.DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE = kualiConfigurationService.getApplicationParameterValue(GLConstants.GL_ORGANIZATION_REVERSION_PROCESS_GROUP, GLConstants.OrganizationReversionProcess.DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE);
        this.DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE = kualiConfigurationService.getApplicationParameterValue(GLConstants.GL_ORGANIZATION_REVERSION_PROCESS_GROUP, GLConstants.OrganizationReversionProcess.DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        this.DEFAULT_FINANCIAL_BALANCE_TYPE_CODE = kualiConfigurationService.getApplicationParameterValue(GLConstants.GL_ORGANIZATION_REVERSION_PROCESS_GROUP, GLConstants.OrganizationReversionProcess.DEFAULT_FINANCIAL_BALANCE_TYPE_CODE);
        this.DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END = kualiConfigurationService.getApplicationParameterValue(GLConstants.GL_ORGANIZATION_REVERSION_PROCESS_GROUP, GLConstants.OrganizationReversionProcess.DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END);
        this.DEFAULT_DOCUMENT_NUMBER_PREFIX = kualiConfigurationService.getApplicationParameterValue(GLConstants.GL_ORGANIZATION_REVERSION_PROCESS_GROUP, GLConstants.OrganizationReversionProcess.DEFAULT_DOCUMENT_NUMBER_PREFIX);
    }
    
    public OrganizationReversionProcess(boolean endOfYear, OrganizationReversionService ors, KualiConfigurationService kcs, BalanceService bs, OrganizationReversionSelection orgrs, OriginEntryGroupService oegs, OriginEntryService oes, PersistenceService ps, DateTimeService dts, OrganizationReversionCategoryLogic corc, PriorYearAccountService pyas, ReportService rs, OrgReversionUnitOfWorkService oruows) {
        this();

        this.endOfYear = endOfYear;
        balanceService = bs;
        organizationReversionService = ors;
        kualiConfigurationService = kcs;
        organizationReversionSelection = orgrs;
        originEntryGroupService = oegs;
        originEntryService = oes;
        persistenceService = ps;
        dateTimeService = dts;
        cashOrganizationReversionCategoryLogic = corc;
        priorYearAccountService = pyas;
        reportService = rs;
        orgReversionUnitOfWorkService = oruows;
    }

    public void organizationReversionProcess() {
        LOG.debug("organizationReversionProcess() started");

        setParameters();

        int balancesRead = 0;
        int balancesSelected = 0;
        int sequenceRecordsWritten = 0;

        runDate = dateTimeService.getCurrentDate();
        
        // clear out summary tables
        orgReversionUnitOfWorkService.destroyAllUnitOfWorkSummaries();

        // Create output group
        outputGroup = originEntryGroupService.createGroup(new java.sql.Date(runDate.getTime()), OriginEntrySource.YEAR_END_ORG_REVERSION, true, false, true);

        categories = organizationReversionService.getCategories();
        categoryList = organizationReversionService.getCategoryList();

        Iterator<Balance> balances = balanceService.findBalancesForFiscalYear(paramUniversityFiscalYear);
        while (balances.hasNext()) {
            Balance bal = balances.next();
            if (balancesRead == 0) {
                unitOfWork = new OrgReversionUnitOfWork(bal.getChartOfAccountsCode(), bal.getAccountNumber(), bal.getSubAccountNumber());
                unitOfWork.setCategories(categoryList);
                unitOfWork.setFields(bal.getChartOfAccountsCode(), bal.getAccountNumber(), bal.getSubAccountNumber());
            }
            balancesRead++;

            try {
                if (!unitOfWork.isSame(bal.getChartOfAccountsCode(), bal.getAccountNumber(), bal.getSubAccountNumber())) {
                    calculateTotals(bal);
                    sequenceRecordsWritten += writeActivity(bal);
                    orgReversionUnitOfWorkService.save(unitOfWork);
                    unitOfWork.setFields(bal.getChartOfAccountsCode(), bal.getAccountNumber(), bal.getSubAccountNumber());
                }
    
                if (organizationReversionSelection.isIncluded(bal)) {
                    balancesSelected += 1;
                    if (cashOrganizationReversionCategoryLogic.containsObjectCode(bal.getFinancialObject())) {
                        unitOfWork.addTotalCash(bal.getBeginningBalanceLineAmount());
                        unitOfWork.addTotalCash(bal.getAccountLineAnnualBalanceAmount());
                    }
                    else {
                        for (Iterator<OrganizationReversionCategory> iter = categoryList.iterator(); iter.hasNext();) {
                            OrganizationReversionCategory cat = iter.next();
                            OrganizationReversionCategoryLogic logic = categories.get(cat.getOrganizationReversionCategoryCode());
                            if (logic.containsObjectCode(bal.getFinancialObject())) {
                                if (bal.getOption().getActualFinancialBalanceTypeCd().equals(bal.getBalanceTypeCode())) {
                                    // Actual
                                    unitOfWork.addActualAmount(cat.getOrganizationReversionCategoryCode(), bal.getBeginningBalanceLineAmount());
                                    unitOfWork.addActualAmount(cat.getOrganizationReversionCategoryCode(), bal.getAccountLineAnnualBalanceAmount());
                                }
                                else if (bal.getOption().getFinObjTypeExpenditureexpCd().equals(bal.getBalanceTypeCode()) || bal.getOption().getCostShareEncumbranceBalanceTypeCd().equals(bal.getBalanceTypeCode()) || bal.getOption().getIntrnlEncumFinBalanceTypCd().equals(bal.getBalanceTypeCode())) {
                                    // Encumbrance
                                    KualiDecimal amount = bal.getBeginningBalanceLineAmount().add(bal.getAccountLineAnnualBalanceAmount());
                                    if (amount.isPositive()) {
                                        unitOfWork.addEncumbranceAmount(cat.getOrganizationReversionCategoryCode(), amount);
                                    }
                                }
                                else if (KFSConstants.BALANCE_TYPE_CURRENT_BUDGET.equals(bal.getBalanceTypeCode())) {
                                    // Budget 
                                    // if balance's object code is the "carry forward" object code
                                    if (!CARRY_FORWARD_OBJECT_CODE.equals(bal.getObjectCode())) {
                                        unitOfWork.addBudgetAmount(cat.getOrganizationReversionCategoryCode(), bal.getBeginningBalanceLineAmount());
                                        unitOfWork.addBudgetAmount(cat.getOrganizationReversionCategoryCode(), bal.getAccountLineAnnualBalanceAmount());
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            } catch (FatalErrorException fee) {
                LOG.info(fee.getMessage());
            }
        }
        
        // prepare statistics for report
        Map jobParameters = new HashMap();
        jobParameters.put(KFSConstants.UNIV_FISCAL_YR, this.paramUniversityFiscalYear);
        SimpleDateFormat reportDateFormat = new SimpleDateFormat(DATE_FORMAT);
        jobParameters.put(KFSConstants.TRANSACTION_DT, reportDateFormat.format(paramTransactionDate));
        jobParameters.put(KFSConstants.UNALLOC_OBJECT_CD, this.paramUnallocObjectCode);
        jobParameters.put(KFSConstants.FUND_BAL_OBJECT_CD, this.paramFundBalanceObjectCode);
        jobParameters.put(KFSConstants.BEG_BUD_CASH_OBJECT_CD, this.paramBegBudgetCashObjectCode);
        
        Summary totalRecordCountSummary = new Summary();
        totalRecordCountSummary.setSortOrder(Summary.TOTAL_RECORD_COUNT_SUMMARY_SORT_ORDER);
        totalRecordCountSummary.setDescription("NUMBER OF GLBL RECORDS READ....:");
        totalRecordCountSummary.setCount(balancesRead);
        
        Summary selectedRecordCountSummary = new Summary();
        selectedRecordCountSummary.setSortOrder(Summary.SELECTED_RECORD_COUNT_SUMMARY_SORT_ORDER);
        selectedRecordCountSummary.setDescription("NUMBER OF GLBL RECORDS SELECTED:");
        selectedRecordCountSummary.setCount(balancesSelected);
        
        Summary sequenceRecordsWrittenSummary = new Summary();
        sequenceRecordsWrittenSummary.setSortOrder(Summary.SEQUENCE_RECORDS_WRITTEN_SUMMARY_SORT_ORDER);
        sequenceRecordsWrittenSummary.setDescription("NUMBER OF SEQ RECORDS WRITTEN..:");
        sequenceRecordsWrittenSummary.setCount(sequenceRecordsWritten);
        
        List<Summary> summaries = new ArrayList<Summary>();
        summaries.add(totalRecordCountSummary);
        summaries.add(selectedRecordCountSummary);
        summaries.add(sequenceRecordsWrittenSummary);
        
        Date runDate = new Date(dateTimeService.getCurrentDate().getTime());
        reportService.generateOrgReversionStatisticsReport(jobParameters, summaries, runDate, outputGroup);
    }

    private int writeActivity(Balance bal) throws FatalErrorException {
        int recordsWritten = 0;
        if (unitOfWork.getTotalReversion().compareTo(KualiDecimal.ZERO) != 0) {
            recordsWritten += writeReversion();
        }
        if ((unitOfWork.getTotalCarryForward().compareTo(KualiDecimal.ZERO) != 0) && (!organizationReversion.isCarryForwardByObjectCodeIndicator())) {
            recordsWritten += writeCarryForward();
        }
        if ((unitOfWork.getTotalCarryForward().compareTo(KualiDecimal.ZERO) != 0) && (organizationReversion.isCarryForwardByObjectCodeIndicator())) {
            recordsWritten += writeMany();
        }
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) != 0) {
            recordsWritten += writeCash();
        }
        return recordsWritten;
    }

    private OriginEntry getEntry() {
        OriginEntry entry = new OriginEntry();
        entry.setUniversityFiscalYear(paramUniversityFiscalYear);
        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);
        entry.setFinancialDocumentTypeCode(DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE);
        entry.setFinancialSystemOriginationCode(DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        entry.setTransactionLedgerEntrySequenceNumber(1);
        entry.setTransactionDebitCreditCode(KFSConstants.GL_BUDGET_CODE);
        entry.setTransactionDate(paramTransactionDate);
        entry.setProjectCode(KFSConstants.getDashProjectCode());
        return entry;
    }

    private int writeCash() throws FatalErrorException {
        int entriesWritten = 0;
        OriginEntry entry = getEntry();
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode(organizationReversion.getChartOfAccounts().getFinancialCashObjectCode());
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(entry.getOption().getNominalFinancialBalanceTypeCd());

        persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (entry.getFinancialObject() == null) {
            throw new FatalErrorException("Object Code for Entry not found: "+entry);
            // TODO Error! Line 3426
        }

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + entry.getAccountNumber());
        entry.setTransactionLedgerEntryDescription(KFSKeyConstants.OrganizationReversionProcess.CASH_REVERTED_TO + organizationReversion.getCashReversionAccountNumber());
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash());
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) > 0) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash().negated());
        }

        // 3468 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3469 WS-AMT-N.
        // 3470 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntryService.createEntry(entry, outputGroup);

        // 3472 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        // 3478 028100 ADD +1 TO SEQ-WRITE-COUNT.
        entriesWritten += 1;

        entry = getEntry();
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode(paramFundBalanceObjectCode);
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE);

        persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (entry.getFinancialObject() == null) {
            throw new FatalErrorException("Object Code for Entry not found: "+entry);
            // TODO Error! Line 3522
        }

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.FUND_BALANCE_REVERTED_TO) + organizationReversion.getCashReversionAccountNumber());
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash().abs());
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) > 0) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }

        // 3570 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3571 WS-AMT-N.
        // 3572 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntryService.createEntry(entry, outputGroup);

        // 3574 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        // 3580 029080 ADD +1 TO SEQ-WRITE-COUNT.

        entry = getEntry();
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(organizationReversion.getCashReversionAccountNumber());
        entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        entry.setFinancialObjectCode(organizationReversion.getChartOfAccounts().getFinancialCashObjectCode());
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE);

        persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (entry.getFinancialObject() == null) {
            throw new FatalErrorException("Object Code for Entry not found: "+entry);
            // TODO Error! Line 3624
        }

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.CASH_REVERTED_FROM) + unitOfWork.accountNumber + " " + unitOfWork.subAccountNumber);
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash());
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) > 0) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash().negated());
        }

        // 3668 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3669 WS-AMT-N.
        // 3670 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntryService.createEntry(entry, outputGroup);

        // 3672 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        // 3678 030020 ADD +1 TO SEQ-WRITE-COUNT.
        entriesWritten += 1;

        entry = getEntry();
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(organizationReversion.getCashReversionAccountNumber());
        entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        entry.setFinancialObjectCode(paramFundBalanceObjectCode);
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE);

        persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (entry.getFinancialObject() == null) {
            throw new FatalErrorException("Object Code for Entry not found: "+entry);
            // TODO Error! Line 3722
        }

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.FUND_BALANCE_REVERTED_FROM) + unitOfWork.accountNumber + " " + unitOfWork.subAccountNumber);
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash());
        if (unitOfWork.getTotalCash().compareTo(KualiDecimal.ZERO) > 0) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCash().negated());
        }

        // 3768 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3769 WS-AMT-N.
        // 3770 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntryService.createEntry(entry, outputGroup);

        // 3772 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        // 3778 030960 ADD +1 TO SEQ-WRITE-COUNT.
        entriesWritten += 1;
        
        return entriesWritten;
    }

    private int writeMany() throws FatalErrorException {
        int originEntriesCreated = 0;
        for (Iterator<OrganizationReversionCategory> iter = categoryList.iterator(); iter.hasNext();) {
            OrganizationReversionCategory cat = iter.next();
            OrganizationReversionDetail detail = organizationReversion.getOrganizationReversionDetail(cat.getOrganizationReversionCategoryCode());
            OrgReversionUnitOfWorkCategoryAmount amount = unitOfWork.amounts.get(cat.getOrganizationReversionCategoryCode());

            if (!amount.getCarryForward().isZero()) {
                KualiDecimal commonAmount = amount.getCarryForward();
                String commonObject = detail.getOrganizationReversionObjectCode();

                OriginEntry entry = getEntry();
                entry.setUniversityFiscalYear(paramUniversityFiscalYear + 1);
                entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
                if (ArrayUtils.contains(ORGANIZATION_REVERSION_COA, unitOfWork.chartOfAccountsCode)) {
                    entry.setAccountNumber(organizationReversion.getBudgetReversionAccountNumber());
                    entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
                }
                else {
                    entry.setAccountNumber(unitOfWork.accountNumber);
                    entry.setSubAccountNumber(unitOfWork.subAccountNumber);
                }
                entry.setFinancialObjectCode(paramBegBudgetCashObjectCode);
                entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);

                persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
                if (entry.getFinancialObject() == null) {
                    throw new FatalErrorException("Object Code for Entry not found: "+entry);
                    // TODO Error! Line 3224
                }

                entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH1);
                entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
                entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.FUND_CARRIED) + paramUniversityFiscalYear);
                entry.setTransactionLedgerEntryAmount(commonAmount);

                // 3259 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
                // 3260 WS-AMT-N.
                // 3261 MOVE WS-AMT-X TO TRN-AMT-RED-X.

                originEntryService.createEntry(entry, outputGroup);

                // 3263 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
                // 3269 026090 ADD +1 TO SEQ-WRITE-COUNT.
                originEntriesCreated += 1;

                entry = getEntry();
                entry.setUniversityFiscalYear(paramUniversityFiscalYear + 1);
                entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
                entry.setAccountNumber(unitOfWork.accountNumber);
                entry.setSubAccountNumber(unitOfWork.subAccountNumber);

                entry.setFinancialObjectCode(commonObject);
                entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);

                persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
                if (entry.getFinancialObject() == null) {
                    throw new FatalErrorException("Object Code for Entry not found: "+entry);
                    // TODO Error! Line 3304
                }

                entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH1);
                entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
                entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.FUND_CARRIED) + paramUniversityFiscalYear);
                entry.setTransactionLedgerEntryAmount(commonAmount);

                // 3343 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
                // 3344 WS-AMT-N.
                // 3345 MOVE WS-AMT-X TO TRN-AMT-RED-X.

                originEntryService.createEntry(entry, outputGroup);

                // 3347 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
                // 3348 026840 ADD +1 TO SEQ-WRITE-COUNT.
                originEntriesCreated += 1;
            }
        }
        return originEntriesCreated;
    }

    private int writeCarryForward() throws FatalErrorException {
        int originEntriesWritten = 0;
        
        OriginEntry entry = getEntry();
        entry.setUniversityFiscalYear(paramUniversityFiscalYear + 1);
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);

        if (ArrayUtils.contains(ORGANIZATION_REVERSION_COA, unitOfWork.chartOfAccountsCode)) {
            entry.setAccountNumber(organizationReversion.getBudgetReversionAccountNumber());
        }
        else {
            entry.setAccountNumber(unitOfWork.accountNumber);
        }
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode(paramBegBudgetCashObjectCode);
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);

        persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (entry.getFinancialObject() == null) {
            throw new FatalErrorException("Object Code for Entry not found: "+entry);
            // TODO Error! Line 2960
        }

        ObjectCode objectCode = entry.getFinancialObject();
        entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH1);
        entry.setFinancialDocumentTypeCode(DEFAULT_FINANCIAL_DOCUMENT_TYPE_CODE);
        entry.setFinancialSystemOriginationCode(DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntrySequenceNumber(1);
        entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.FUND_CARRIED) + paramUniversityFiscalYear);
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCarryForward());
        entry.setTransactionDate(paramTransactionDate);
        entry.setProjectCode(KFSConstants.getDashProjectCode());
        // 2995 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 2996 WS-AMT-N.
        // 2997 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntryService.createEntry(entry, outputGroup);

        // 2999 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        // 3005 023530 ADD +1 TO SEQ-WRITE-COUNT.
        originEntriesWritten += 1;

        entry = getEntry();
        entry.setUniversityFiscalYear(paramUniversityFiscalYear + 1);
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode(paramUnallocObjectCode);

        persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (entry.getFinancialObject() == null) {
            // TODO Error! Line 3040
        }

        objectCode = entry.getFinancialObject();
        entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);
        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH1);
        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber);
        entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.FUND_CARRIED) + paramUniversityFiscalYear);
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalCarryForward());

        // 3079 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 3080 WS-AMT-N.
        // 3081 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntryService.createEntry(entry, outputGroup);

        // 3083 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        // 3089 024330 ADD +1 TO SEQ-WRITE-COUNT.
        originEntriesWritten += 1;
        
        return originEntriesWritten;
    }

    private int writeReversion() throws FatalErrorException {
        int originEntriesWritten = 0;
        
        OriginEntry entry = getEntry();
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(unitOfWork.accountNumber);
        entry.setSubAccountNumber(unitOfWork.subAccountNumber);
        entry.setFinancialObjectCode(paramUnallocObjectCode);
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END);

        persistenceService.retrieveReferenceObject(entry, KFSPropertyConstants.FINANCIAL_OBJECT);
        if (entry.getFinancialObject() == null) {
            // TODO Error! Line 2807
        }

        ObjectCode objectCode = entry.getFinancialObject();
        entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);

        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + entry.getAccountNumber());

        entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.FUND_REVERTED_TO) + organizationReversion.getBudgetReversionAccountNumber());
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalReversion().negated());

        // 2841 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 2842 WS-AMT-N.
        // 2843 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntryService.createEntry(entry, outputGroup);

        // 2845 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        // 2851 022070 ADD +1 TO SEQ-WRITE-COUNT.
        originEntriesWritten += 1;

        entry = getEntry();
        entry.setChartOfAccountsCode(unitOfWork.chartOfAccountsCode);
        entry.setAccountNumber(organizationReversion.getBudgetReversionAccountNumber());
        entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        entry.setFinancialObjectCode(paramUnallocObjectCode);
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END);
        entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        entry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);
        entry.setDocumentNumber(DEFAULT_DOCUMENT_NUMBER_PREFIX + unitOfWork.accountNumber + unitOfWork.subAccountNumber);
        entry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.OrganizationReversionProcess.FUND_REVERTED_FROM) + unitOfWork.accountNumber + " " + unitOfWork.subAccountNumber);
        entry.setTransactionLedgerEntryAmount(unitOfWork.getTotalReversion());

        // 2899 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
        // 2900 WS-AMT-N.
        // 2901 MOVE WS-AMT-X TO TRN-AMT-RED-X.

        originEntryService.createEntry(entry, outputGroup);

        // 2903 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        // 2909 022610 ADD +1 TO SEQ-WRITE-COUNT.
        originEntriesWritten += 1;
        
        return originEntriesWritten;
    }

    private void calculateTotals(Balance bal) throws FatalErrorException {
        if ((account == null) || (!bal.getChartOfAccountsCode().equals(account.getChartOfAccountsCode())) || (!bal.getAccountNumber().equals(account.getAccountNumber()))) {
            if (endOfYear) {
                account = bal.getAccount();
            }
            else {
                account = priorYearAccountService.getByPrimaryKey(bal.getChartOfAccountsCode(), bal.getAccountNumber());
            }
        }

        // Initialize all the amounts before applying the proper rule
        KualiDecimal totalAvailable = KualiDecimal.ZERO;
        for (Iterator iter = categoryList.iterator(); iter.hasNext();) {
            OrganizationReversionCategory category = (OrganizationReversionCategory) iter.next();
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

        if ((organizationReversion == null) || (!organizationReversion.getChartOfAccountsCode().equals(bal.getChartOfAccountsCode())) || (!organizationReversion.getOrganizationCode().equals(account.getOrganizationCode()))) {
            organizationReversion = organizationReversionService.getByPrimaryId(paramUniversityFiscalYear, bal.getChartOfAccountsCode(), account.getOrganizationCode());
        }

        if (organizationReversion == null) {
            // we can't find an organization reversion for this balance?  Throw exception
            throw new FatalErrorException("No Organization Reversion found for: "+paramUniversityFiscalYear+"-"+bal.getChartOfAccountsCode()+"-"+account.getOrganizationCode());
            // TODO ERROR! Line 2058
        }
        // Accounts with the type of S3 have all rules always set to A
        if (KFSConstants.ACCOUNT_TYPE_S3.equals(bal.getAccount().getAccountTypeCode())) {
            List details = organizationReversion.getOrganizationReversionDetail();
            for (Iterator iter = details.iterator(); iter.hasNext();) {
                OrganizationReversionDetail element = (OrganizationReversionDetail) iter.next();
                element.setOrganizationReversionCode(KFSConstants.EMPLOYEE_ACTIVE_STATUS);
            }
        }

        // For each category, apply the rules
        for (Iterator iter = categoryList.iterator(); iter.hasNext();) {
            OrganizationReversionCategory category = (OrganizationReversionCategory) iter.next();
            String categoryCode = category.getOrganizationReversionCategoryCode();
            OrganizationReversionCategoryLogic logic = categories.get(categoryCode);
            OrgReversionUnitOfWorkCategoryAmount amount = unitOfWork.amounts.get(categoryCode);

            OrganizationReversionDetail detail = organizationReversion.getOrganizationReversionDetail(categoryCode);
            
            if (detail == null) {
                throw new FatalErrorException("Organization Reversion "+organizationReversion.getUniversityFiscalYear()+"-"+organizationReversion.getChartOfAccountsCode()+"-"+organizationReversion.getOrganizationCode()+" does not have a detail for category "+categoryCode);
            }
            String ruleCode = detail.getOrganizationReversionCode();

            if (KFSConstants.RULE_CODE_R1.equals(ruleCode) || KFSConstants.RULE_CODE_N1.equals(ruleCode) || KFSConstants.RULE_CODE_C1.equals(ruleCode)) {
                if (amount.getAvailable().compareTo(KualiDecimal.ZERO) > 0) {
                    if (amount.getAvailable().compareTo(amount.getEncumbrance()) > 0) {
                        unitOfWork.addTotalCarryForward(amount.getEncumbrance());
                        amount.addCarryForward(amount.getEncumbrance());
                        unitOfWork.addTotalReversion(amount.getEncumbrance().negated());
                        unitOfWork.addTotalAvailable(amount.getEncumbrance().negated());
                    }
                    else {
                        unitOfWork.addTotalCarryForward(amount.getAvailable());
                        amount.addCarryForward(amount.getAvailable());
                        unitOfWork.addTotalReversion(amount.getAvailable().negated());
                        amount.setAvailable(KualiDecimal.ZERO);
                    }
                }
            }

            if (KFSConstants.EMPLOYEE_ACTIVE_STATUS.equals(ruleCode)) {
                unitOfWork.addTotalCarryForward(amount.getCarryForward());
                amount.addCarryForward(amount.getAvailable());
                unitOfWork.addTotalReversion(amount.getAvailable().negated());
                amount.setAvailable(KualiDecimal.ZERO);
            }

            if (KFSConstants.RULE_CODE_C1.equals(ruleCode) || KFSConstants.RULE_CODE_C2.equals(ruleCode)) {
                if (amount.getAvailable().compareTo(KualiDecimal.ZERO) > 0) {
                    unitOfWork.addTotalCarryForward(amount.getAvailable());
                    amount.addCarryForward(amount.getAvailable());
                    unitOfWork.addTotalReversion(amount.getAvailable());
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
        }
    }

    private void setParameters() {
        // Get job parameters
        String strTransactionDate = kualiConfigurationService.getApplicationParameterValue(KFSConstants.GENERAL_LEDGER_YEAR_END_SCRIPT, KFSConstants.TRANSACTION_DT);
        paramUnallocObjectCode = kualiConfigurationService.getApplicationParameterValue(KFSConstants.GENERAL_LEDGER_YEAR_END_SCRIPT, KFSConstants.UNALLOC_OBJECT_CD);
        paramBegBudgetCashObjectCode = kualiConfigurationService.getApplicationParameterValue(KFSConstants.GENERAL_LEDGER_YEAR_END_SCRIPT, KFSConstants.BEG_BUD_CASH_OBJECT_CD);
        paramFundBalanceObjectCode = kualiConfigurationService.getApplicationParameterValue(KFSConstants.GENERAL_LEDGER_YEAR_END_SCRIPT, KFSConstants.FUND_BAL_OBJECT_CD);
        String strUniversityFiscalYear = kualiConfigurationService.getApplicationParameterValue(KFSConstants.GENERAL_LEDGER_YEAR_END_SCRIPT, KFSConstants.UNIV_FISCAL_YR);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            java.util.Date jud = sdf.parse(strTransactionDate);
            paramTransactionDate = new java.sql.Date(jud.getTime());
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("TRANSACTION_DT is an invalid date");
        }
        try {
            paramUniversityFiscalYear = new Integer(strUniversityFiscalYear);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("UNIV_FISCAL_YR is an invalid year");
        }
    }

    /**
     * Gets the reportService attribute. 
     * @return Returns the reportService.
     */
    public ReportService getReportService() {
        return reportService;
    }

    /**
     * Sets the reportService attribute value.
     * @param reportService The reportService to set.
     */
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
    
}
