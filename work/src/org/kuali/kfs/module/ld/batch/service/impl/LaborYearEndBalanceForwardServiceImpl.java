/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service.impl;

import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_YEAR_END_BALANCE_FORWARD;
import static org.kuali.module.labor.LaborConstants.DestinationNames.LEDGER_BALANCE;
import static org.kuali.module.labor.LaborConstants.DestinationNames.ORIGN_ENTRY;
import static org.kuali.module.labor.LaborConstants.ParameterGroups.YEAR_END;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.labor.LaborConstants.YearEnd;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.service.LaborReportService;
import org.kuali.module.labor.service.LaborYearEndBalanceForwardService;
import org.kuali.module.labor.util.MessageBuilder;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.ReportRegistry;
import org.kuali.module.labor.util.TransactionTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Labor Ledger Year End – Inception to Date Beginning Balance process moves the Year-to-Date Total plus the Contracts and Grants
 * Beginning Balances to the Contracts and Grants Beginning Balances of the new fiscal year for a designated group of accounts (to
 * be identified by fund group and sub fund group).
 */
@Transactional
public class LaborYearEndBalanceForwardServiceImpl implements LaborYearEndBalanceForwardService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborYearEndBalanceForwardServiceImpl.class);

    private LaborLedgerBalanceService laborLedgerBalanceService;
    private OriginEntryGroupService originEntryGroupService;

    private AccountService accountService;
    private OptionsService optionsService;

    private BusinessObjectService businessObjectService;
    private LaborReportService laborReportService;
    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;

    private final static int LINE_INTERVAL = 2;


    /**
     * @see org.kuali.module.labor.service.LaborYearEndBalanceForwardService#forwardBalance()
     */
    public void forwardBalance() {
        Integer fiscalYear = Integer.valueOf(kualiConfigurationService.getApplicationParameterValue(YEAR_END, YearEnd.OLD_FISCAL_YEAR));
        this.forwardBalance(fiscalYear);
    }

    /**
     * @see org.kuali.module.labor.service.LaborYearEndBalanceForwardService#forwardBalance(java.lang.Integer)
     */
    public void forwardBalance(Integer fiscalYear) {
        forwardBalance(fiscalYear, fiscalYear + 1);
    }

    /**
     * @see org.kuali.module.labor.service.LaborYearEndBalanceForwardService#forwardBalance(java.lang.Integer, java.lang.Integer)
     */
    public void forwardBalance(Integer fiscalYear, Integer newFiscalYear) {
        String reportsDirectory = ReportRegistry.getReportsDirectory();
        Date runDate = dateTimeService.getCurrentSqlDate();

        List<Summary> reportSummary = new ArrayList<Summary>();
        Map<Transaction, List<Message>> errorMap = new HashMap<Transaction, List<Message>>();
        OriginEntryGroup validGroup = originEntryGroupService.createGroup(runDate, LABOR_YEAR_END_BALANCE_FORWARD, true, true, true);

        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        int numberOfBalance = businessObjectService.countMatching(LedgerBalance.class, fieldValues);
        int numberOfSelectedBalance = 0;

        Iterator<LedgerBalance> balanceIterator = laborLedgerBalanceService.findBalancesForFiscalYear(fiscalYear);
        while (balanceIterator != null && balanceIterator.hasNext()) {
            LedgerBalance balance = balanceIterator.next();
            List<Message> errors = null;

            boolean isValidBalance = validateBalance(balance, errors);
            if (isValidBalance) {
                this.postAsOriginEntry(balance, newFiscalYear, validGroup, runDate);
                numberOfSelectedBalance++;
            }
            else if (errors != null && !errors.isEmpty()) {
                LaborOriginEntry originEntry = new LaborOriginEntry();
                ObjectUtil.buildObject(originEntry, balance);
                errorMap.put(originEntry, errors);
            }
        }

        Summary.updateReportSummary(reportSummary, LEDGER_BALANCE, KFSConstants.OperationType.READ, numberOfBalance, 0);
        Summary.updateReportSummary(reportSummary, LEDGER_BALANCE, KFSConstants.OperationType.SELECT, numberOfSelectedBalance, 0);
        Summary.updateReportSummary(reportSummary, LEDGER_BALANCE, KFSConstants.OperationType.REPORT_ERROR, errorMap.size(), 0);
        reportSummary.add(new Summary(reportSummary.size() + LINE_INTERVAL, "", 0));
        Summary.updateReportSummary(reportSummary, ORIGN_ENTRY, KFSConstants.OperationType.INSERT, numberOfSelectedBalance, 0);

        laborReportService.generateStatisticsReport(reportSummary, errorMap, ReportRegistry.LABOR_YEAR_END_STATISTICS, reportsDirectory, runDate);
        laborReportService.generateOutputSummaryReport(validGroup, ReportRegistry.LABOR_YEAR_END_OUTPUT, reportsDirectory, runDate);
    }

    /**
     * determine if the given balance is qualified to be carried forward to new fiscal year
     * 
     * @param balance the given ledger balance that could be carried forward
     * @param errors the error list that is updated if the given balacne is not qualified for carry forward
     * @return true if the balance is qualified; otherwise, false
     */
    private boolean validateBalance(LedgerBalance balance, List<Message> errors) {
        Integer fiscalYear = balance.getUniversityFiscalYear();
        if (!ArrayUtils.contains(this.getProcessableBalanceTypeCode(fiscalYear), balance.getFinancialBalanceTypeCode())) {
            LOG.warn("Cannot process the balance type code: " + balance.getFinancialBalanceTypeCode());
            return false;
        }

        if (!ArrayUtils.contains(this.getProcessableObjectTypeCodes(fiscalYear), balance.getFinancialObjectTypeCode())) {
            LOG.warn("Cannot process the object type Code: " + balance.getFinancialObjectTypeCode());
            return false;
        }

        String chartOfAccountsCode = balance.getChartOfAccountsCode();
        String accountNumber = balance.getAccountNumber();
        Account account = accountService.getByPrimaryId(chartOfAccountsCode, accountNumber);
        if (account == null) {
            StringBuilder invalidAccountValue = new StringBuilder();
            invalidAccountValue.append(chartOfAccountsCode).append("-").append(accountNumber);

            errors = new ArrayList<Message>();
            errors.add(MessageBuilder.buildErrorMessage(KFSKeyConstants.Labor.ERROR_ACCOUNT_NOT_FOUND, invalidAccountValue.toString(), Message.TYPE_FATAL));
            return false;
        }

        SubFundGroup subFundGroup = account.getSubFundGroup();
        String subFundGroupCode = account.getSubFundGroupCode();
        if (subFundGroup == null) {
            StringBuilder invalidSubFundValue = new StringBuilder();
            invalidSubFundValue.append(chartOfAccountsCode).append("-").append(accountNumber).append("-").append(subFundGroupCode);

            errors = new ArrayList<Message>();
            errors.add(MessageBuilder.buildErrorMessage(KFSKeyConstants.Labor.ERROR_SUB_FUND_GROUP_NOT_FOUND, invalidSubFundValue.toString(), Message.TYPE_FATAL));
            return false;
        }

        if (!ArrayUtils.contains(this.getFundGroupProcessed(), subFundGroup.getFundGroupCode())) {
            LOG.warn("Cannot process the fund group code: " + subFundGroup.getFundGroupCode());
            return false;
        }
        return true;
    }

    /**
     * post the qualified balance into origin entry table for the further labor ledger processing
     * 
     * @param balance the given ledger balance that will be carried forward
     * @param newFiscalYear the new fiscal year
     * @param validGroup the group that the posted transaction belongs to
     * @param postingDate the date the transaction is posted
     */
    private void postAsOriginEntry(LedgerBalance balance, Integer newFiscalYear, OriginEntryGroup validGroup, Date postingDate) {
        try {
            LaborOriginEntry originEntry = new LaborOriginEntry();

            originEntry.setEntryGroupId(validGroup.getId());
            originEntry.setUniversityFiscalYear(newFiscalYear);

            originEntry.setAccountNumber(balance.getAccountNumber());
            originEntry.setChartOfAccountsCode(balance.getChartOfAccountsCode());
            originEntry.setSubAccountNumber(balance.getSubAccountNumber());
            originEntry.setFinancialObjectCode(balance.getObjectCode());
            originEntry.setFinancialSubObjectCode(balance.getSubObjectCode());
            originEntry.setFinancialBalanceTypeCode(balance.getBalanceTypeCode());
            originEntry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

            originEntry.setPositionNumber(balance.getPositionNumber());
            originEntry.setEmplid(balance.getEmplid());
            originEntry.setDocumentNumber(balance.getFinancialBalanceTypeCode() + balance.getAccountNumber());

            originEntry.setProjectCode(KFSConstants.DASHES_PROJECT_CODE);
            originEntry.setUniversityFiscalPeriodCode(KFSConstants.CG_BEGINNING_BALANCE);

            originEntry.setFinancialDocumentTypeCode(this.getDocumentTypeCode());
            originEntry.setFinancialSystemOriginationCode(this.getOriginationCode());
            originEntry.setTransactionLedgerEntryDescription(this.getDescription());

            KualiDecimal transactionAmount = balance.getAccountLineAnnualBalanceAmount();
            transactionAmount = transactionAmount.add(balance.getContractsGrantsBeginningBalanceAmount());

            originEntry.setTransactionLedgerEntryAmount(transactionAmount.abs());
            originEntry.setTransactionDebitCreditCode(TransactionTemplate.getDebitCreditCode(transactionAmount, false));

            originEntry.setTransactionLedgerEntrySequenceNumber(1);
            originEntry.setTransactionTotalHours(BigDecimal.ZERO);
            originEntry.setTransactionDate(postingDate);

            businessObjectService.save(originEntry);
        }
        catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * get the fund group codes that are acceptable by year-end process
     * 
     * @return the fund group codes that are acceptable by year-end process
     */
    private String[] getFundGroupProcessed() {
        return kualiConfigurationService.getApplicationParameterValues(YEAR_END, YearEnd.FUND_GROUP_PROCESSED);
    }

    /**
     * get the balance type codes that are acceptable by year-end process
     * 
     * @return the balance type codes that are acceptable by year-end process
     */
    private String[] getProcessableBalanceTypeCode(Integer fiscalYear) {
        Options options = optionsService.getOptions(fiscalYear);
        String[] processableBalanceTypeCodes = { options.getActualFinancialBalanceTypeCd() };
        return processableBalanceTypeCodes;
    }

    /**
     * get the object type codes that are acceptable by year-end process
     * 
     * @param fiscalYear the given fiscal year
     * @return the object type codes that are acceptable by year-end process
     */
    private String[] getProcessableObjectTypeCodes(Integer fiscalYear) {
        Options options = optionsService.getOptions(fiscalYear);
        String[] processableObjectTypeCodes = { options.getFinObjTypeExpenditureexpCd(), options.getFinObjTypeExpNotExpendCode() };
        return processableObjectTypeCodes;
    }

    /**
     * get the document type code of the transaction posted by year-end process
     * 
     * @return the document type code of the transaction posted by year-end process
     */
    private String getDocumentTypeCode() {
        return kualiConfigurationService.getApplicationParameterValue(YEAR_END, YearEnd.DOCUMENT_TYPE_CODE);
    }

    /**
     * get the origination code of the transaction posted by year-end process
     * 
     * @return the origination code of the transaction posted by year-end process
     */
    private String getOriginationCode() {
        return kualiConfigurationService.getApplicationParameterValue(YEAR_END, YearEnd.ORIGINATION_CODE);
    }

    /**
     * get the description of the transaction posted by year-end process
     * 
     * @return the description of the transaction posted by year-end process
     */
    private String getDescription() {
        return kualiConfigurationService.getPropertyString(KFSKeyConstants.Labor.MESSAGE_YEAR_END_TRANSACTION_DESCRIPTON);
    }

    /**
     * Sets the accountService attribute value.
     * 
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the laborLedgerBalanceService attribute value.
     * 
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
    }

    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * Sets the laborReportService attribute value.
     * 
     * @param laborReportService The laborReportService to set.
     */
    public void setLaborReportService(LaborReportService laborReportService) {
        this.laborReportService = laborReportService;
    }
}