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

import static org.kuali.Constants.ParameterGroups.SYSTEM;
import static org.kuali.Constants.SystemGroupParameterNames.LABOR_YEAR_END_DOCUMENT_TYPE_CODE;
import static org.kuali.Constants.SystemGroupParameterNames.LABOR_YEAR_END_FUND_GROUP_PROCESSED;
import static org.kuali.Constants.SystemGroupParameterNames.LABOR_YEAR_END_ORIGINATION_CODE;
import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_YEAR_END_BALANCE_FORWARD;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.spring.Logged;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.util.Message;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.service.LaborReportService;
import org.kuali.module.labor.service.LaborYearEndBalanceForwardService;
import org.kuali.module.labor.util.MessageBuilder;
import org.kuali.module.labor.util.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class LaborYearEndBalanceForwardServiceImpl implements LaborYearEndBalanceForwardService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborYearEndBalanceForwardServiceImpl.class);

    private LaborOriginEntryService laborOriginEntryService;
    private LaborLedgerBalanceService laborLedgerBalanceService;
    private OriginEntryGroupService originEntryGroupService;
    private AccountService accountService;
    private OptionsService optionsService;

    private LaborReportService reportService;
    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.module.labor.service.LaborYearEndBalanceForwardService#forwardBalance(java.lang.Integer)
     */
    @Logged
    public void forwardBalance(Integer fiscalYear) {
        this.forwardBalance(fiscalYear, fiscalYear + 1);
    }

    // forward the labor balances in the given fiscal year to the new fiscal year
    @Logged
    public void forwardBalance(Integer fiscalYear, Integer newFiscalYear) {
        Date runDate = dateTimeService.getCurrentSqlDate();
        Map<Transaction, List<Message>> errorMap = new HashMap<Transaction, List<Message>>();
        OriginEntryGroup validGroup = originEntryGroupService.createGroup(runDate, LABOR_YEAR_END_BALANCE_FORWARD, true, false, true);

        // TODO: in order to improve performance, we need to create a new method for this.
        Iterator<LedgerBalance> balanceIterator = laborLedgerBalanceService.findBalancesForFiscalYear(fiscalYear);
        while (balanceIterator != null && balanceIterator.hasNext()) {
            LedgerBalance balance = balanceIterator.next();
            List<Message> errors = null;

            boolean isValidBalance = validateBalance(balance, errors);
            if (isValidBalance) {
                this.postAsOriginEntry(balance, newFiscalYear, validGroup, runDate);
            }
            else if (!errors.isEmpty()) {
                LaborOriginEntry originEntry = new LaborOriginEntry();
                ObjectUtil.buildObject(originEntry, balance);
                errorMap.put(originEntry, errors);
            }
        }
    }

    // determine if the given balance is qualified to be carried forward to new fiscal year
    @Logged
    private boolean validateBalance(LedgerBalance balance, List<Message> errors) {
        Integer fiscalYear = balance.getUniversityFiscalYear();
        if (!ArrayUtils.contains(this.getProcessableBalanceTypeCode(fiscalYear), balance.getFinancialBalanceTypeCode())) {
            return false;
        }
        if (!ArrayUtils.contains(this.getProcessableObjectTypeCodes(fiscalYear), balance.getFinancialObjectTypeCode())) {
            return false;
        }

        Account account = accountService.getByPrimaryId(balance.getChartOfAccountsCode(), balance.getAccountNumber());
        if (account == null) {
            errors = new ArrayList<Message>();
            errors.add(MessageBuilder.buildErrorMessage(KeyConstants.Labor.ERROR_ACCOUNT_NOT_FOUND, null, Message.TYPE_FATAL));
            return false;
        }

        SubFundGroup subFundGroup = account.getSubFundGroup();
        if (subFundGroup == null) {
            errors = new ArrayList<Message>();
            errors.add(MessageBuilder.buildErrorMessage(KeyConstants.Labor.ERROR_SUB_FUND_GROUP_NOT_FOUND, null, Message.TYPE_FATAL));
            return false;
        }
        if (!ArrayUtils.contains(this.getFundGroupProcessed(), subFundGroup.getFundGroupCode())) {
            return false;
        }
        return true;
    }

    // post the qualified balance into origin entry table for the further labor ledger processing
    @Logged
    private void postAsOriginEntry(LedgerBalance balance, Integer newFiscalYear, OriginEntryGroup validGroup, Date postingDate) {
        LaborOriginEntry originEntry = new LaborOriginEntry();

        originEntry.setEntryGroupId(validGroup.getId());
        originEntry.setUniversityFiscalYear(newFiscalYear);

        originEntry.setAccountNumber(balance.getAccountNumber());
        originEntry.setSubAccountNumber(balance.getSubAccountNumber());
        originEntry.setFinancialObjectCode(balance.getObjectCode());
        originEntry.setFinancialSubObjectCode(balance.getSubObjectCode());
        originEntry.setFinancialBalanceTypeCode(balance.getBalanceTypeCode());
        originEntry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

        originEntry.setPositionNumber(balance.getPositionNumber());
        originEntry.setEmplid(balance.getEmplid());
        originEntry.setDocumentNumber(balance.getFinancialBalanceTypeCode() + balance.getAccountNumber());

        originEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        originEntry.setUniversityFiscalPeriodCode(Constants.CG_BEGINNING_BALANCE);

        originEntry.setFinancialDocumentTypeCode(this.getDocumentTypeCode());
        originEntry.setFinancialSystemOriginationCode(this.getOriginationCode());
        originEntry.setTransactionLedgerEntryDescription(this.getDescription());

        KualiDecimal transactionLedgerEntryAmount = balance.getAccountLineAnnualBalanceAmount();
        transactionLedgerEntryAmount = transactionLedgerEntryAmount.add(balance.getContractsGrantsBeginningBalanceAmount());
        originEntry.setTransactionLedgerEntryAmount(transactionLedgerEntryAmount.abs());

        String debitCreditCode = transactionLedgerEntryAmount.isPositive() ? Constants.GL_DEBIT_CODE : Constants.GL_CREDIT_CODE;
        originEntry.setTransactionDebitCreditCode(debitCreditCode);

        originEntry.setTransactionLedgerEntrySequenceNumber(1);
        originEntry.setTransactionTotalHours(BigDecimal.ZERO);
        originEntry.setTransactionDate(postingDate);

        laborOriginEntryService.save(originEntry);
    }

    // get the fund group codes that are acceptable by year-end process
    private String[] getFundGroupProcessed() {
        return kualiConfigurationService.getApplicationParameterValues(SYSTEM, LABOR_YEAR_END_FUND_GROUP_PROCESSED);
    }

    // get the balance type codes that are acceptable by year-end process
    private String[] getProcessableBalanceTypeCode(Integer fiscalYear) {
        Options options = optionsService.getOptions(fiscalYear);
        String[] processableBalanceTypeCodes = { options.getActualFinancialBalanceTypeCd() };
        return processableBalanceTypeCodes;
    }

    // get the object type codes that are acceptable by year-end process
    private String[] getProcessableObjectTypeCodes(Integer fiscalYear) {
        Options options = optionsService.getOptions(fiscalYear);
        String[] processableObjectTypeCodes = { options.getFinObjTypeExpenditureexpCd(), options.getFinObjTypeExpNotExpendCode() };
        return processableObjectTypeCodes;
    }

    // get the document type code of the transaction posted by year-end process
    private String getDocumentTypeCode() {
        return kualiConfigurationService.getApplicationParameterValue(SYSTEM, LABOR_YEAR_END_DOCUMENT_TYPE_CODE);
    }

    // get the origination code of the transaction posted by year-end process
    private String getOriginationCode() {
        return kualiConfigurationService.getApplicationParameterValue(SYSTEM, LABOR_YEAR_END_ORIGINATION_CODE);
    }

    // get the description of the transaction posted by year-end process
    private String getDescription() {
        return kualiConfigurationService.getPropertyString(KeyConstants.Labor.ERROR_UNPOSTABLE_OBJECT_CODE);
    }
}