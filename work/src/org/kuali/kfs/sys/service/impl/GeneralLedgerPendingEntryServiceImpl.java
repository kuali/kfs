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
package org.kuali.kfs.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPostable;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.dao.GeneralLedgerPendingEntryDao;
import org.kuali.kfs.document.GeneralLedgerPoster;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.GeneralLedgerPostingHelper;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.SufficientFundsServiceConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the GeneralLedgerPendingEntry structure. This is the default implementation, that is
 * delivered with Kuali.
 */
@Transactional
public class GeneralLedgerPendingEntryServiceImpl implements GeneralLedgerPendingEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerPendingEntryServiceImpl.class);

    private GeneralLedgerPendingEntryDao generalLedgerPendingEntryDao;
    private KualiRuleService kualiRuleService;
    private ChartService chartService;
    private OptionsService optionsService;
    private ParameterService parameterService;
    private BalanceTypService balanceTypeService;

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getExpenseSummary(java.util.List, java.lang.String,
     *      java.lang.String, boolean, boolean)
     */
    public KualiDecimal getExpenseSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isDebit, boolean isYearEnd) {
        LOG.debug("getExpenseSummary() started");

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getExpenseObjectTypes(universityFiscalYear);

        Options options = optionsService.getOptions(universityFiscalYear);

        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, objectTypes, balanceTypeCodes, sufficientFundsObjectCode, isDebit, isYearEnd);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getEncumbranceSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean, boolean)
     */
    public KualiDecimal getEncumbranceSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isDebit, boolean isYearEnd) {
        LOG.debug("getEncumbranceSummary() started");

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getExpenseObjectTypes(universityFiscalYear);

        Options options = optionsService.getOptions(universityFiscalYear);

        List<String> balanceTypeCodes = balanceTypeService.getCurrentYearEncumbranceBalanceTypes();
            

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, objectTypes, balanceTypeCodes, sufficientFundsObjectCode, isDebit, isYearEnd);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getBudgetSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean)
     */
    public KualiDecimal getBudgetSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isYearEnd) {
        LOG.debug("getBudgetSummary() started");

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getExpenseObjectTypes(universityFiscalYear);

        Options options = optionsService.getOptions(universityFiscalYear);

        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getBudgetCheckingBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, objectTypes, balanceTypeCodes, sufficientFundsObjectCode, isYearEnd);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getCashSummary(java.util.Collection, java.lang.String,
     *      java.lang.String, boolean)
     */
    public KualiDecimal getCashSummary(List universityFiscalYears, String chartOfAccountsCode, String accountNumber, boolean isDebit) {
        LOG.debug("getCashSummary() started");

        Chart c = chartService.getByPrimaryId(chartOfAccountsCode);

        // Note, we are getting the options from the first fiscal year in the list. We are assuming that the
        // balance type code for actual is the same in all the years in the list.
        Options options = optionsService.getOptions((Integer) universityFiscalYears.get(0));

        Collection objectCodes = new ArrayList();
        objectCodes.add(c.getFinancialCashObjectCode());

        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYears, chartOfAccountsCode, accountNumber, objectCodes, balanceTypeCodes, isDebit);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getActualSummary(java.util.List, java.lang.String,
     *      java.lang.String, boolean)
     */
    public KualiDecimal getActualSummary(List universityFiscalYears, String chartOfAccountsCode, String accountNumber, boolean isDebit) {
        LOG.debug("getActualSummary() started");

        List<String> codes = parameterService.getParameterValues(ParameterConstants.FINANCIAL_SYSTEM_ALL.class, SufficientFundsServiceConstants.SUFFICIENT_FUNDS_OBJECT_CODE_SPECIALS);

        // Note, we are getting the options from the first fiscal year in the list. We are assuming that the
        // balance type code for actual is the same in all the years in the list.
        Options options = optionsService.getOptions((Integer) universityFiscalYears.get(0));

        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYears, chartOfAccountsCode, accountNumber, codes, balanceTypeCodes, isDebit);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getByPrimaryId(java.lang.Integer, java.lang.String)
     */
    public GeneralLedgerPendingEntry getByPrimaryId(Integer transactionEntrySequenceId, String documentHeaderId) {
        LOG.debug("getByPrimaryId() started");

        return generalLedgerPendingEntryDao.getByPrimaryId(documentHeaderId, transactionEntrySequenceId);
    }

    public void fillInFiscalPeriodYear(GeneralLedgerPendingEntry glpe) {
        LOG.debug("fillInFiscalPeriodYear() started");

        // TODO Handle year end documents

        if ((glpe.getUniversityFiscalPeriodCode() == null) || (glpe.getUniversityFiscalYear() == null)) {
            UniversityDate ud = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();

            glpe.setUniversityFiscalYear(ud.getUniversityFiscalYear());
            glpe.setUniversityFiscalPeriodCode(ud.getUniversityFiscalAccountingPeriod());
        }
    }

    /**
     * Invokes generateEntries method on the financial document.
     * 
     * @param document - document whose pending entries need generated
     * @return whether the business rules succeeded
     */
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPoster poster) {
        boolean success = true;

        // we must clear them first before creating new ones
        poster.clearAnyGeneralLedgerPendingEntries();

        LOG.info("deleting existing gl pending ledger entries for document " + poster.getDocumentHeader().getDocumentNumber());
        delete(poster.getDocumentHeader().getDocumentNumber());

        LOG.info("generating gl pending ledger entries for document " + poster.getDocumentHeader().getDocumentNumber());
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
        GeneralLedgerPostingHelper postingHelper = poster.getGeneralLedgerPostingHelper();
        for (GeneralLedgerPostable postable: poster.getGeneralLedgerPostables()) {
            success &= postingHelper.processGenerateGeneralLedgerPendingEntries(poster, postable, sequenceHelper);
            sequenceHelper.increment();
        }
        
        // doc specific pending entries generation
        poster.processGenerateDocumentGeneralLedgerPendingEntries(sequenceHelper);
        return success;
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#save(org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    public void save(GeneralLedgerPendingEntry generalLedgerPendingEntry) {
        LOG.debug("save() started");

        generalLedgerPendingEntryDao.save(generalLedgerPendingEntry);
    }

    public void delete(String documentHeaderId) {
        LOG.debug("delete() started");

        this.generalLedgerPendingEntryDao.delete(documentHeaderId);
    }

    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        LOG.debug("deleteByFinancialDocumentApprovedCode() started");

        this.generalLedgerPendingEntryDao.deleteByFinancialDocumentApprovedCode(financialDocumentApprovedCode);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findApprovedPendingLedgerEntries()
     */
    public Iterator findApprovedPendingLedgerEntries() {
        LOG.debug("findApprovedPendingLedgerEntries() started");

        return generalLedgerPendingEntryDao.findApprovedPendingLedgerEntries();
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntries(org.kuali.module.gl.bo.Encumbrance,
     *      boolean)
     */
    public Iterator findPendingLedgerEntries(Encumbrance encumbrance, boolean isApproved) {
        LOG.debug("findPendingLedgerEntries() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntries(encumbrance, isApproved);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#hasPendingGeneralLedgerEntry(org.kuali.module.chart.bo.Account)
     */
    public boolean hasPendingGeneralLedgerEntry(Account account) {
        LOG.debug("hasPendingGeneralLedgerEntry() started");

        return generalLedgerPendingEntryDao.countPendingLedgerEntries(account) > 0;
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntries(Balance, boolean, boolean)
     */
    public Iterator findPendingLedgerEntries(Balance balance, boolean isApproved, boolean isConsolidated) {
        LOG.debug("findPendingLedgerEntries() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntries(balance, isApproved, isConsolidated);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForEntry(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForEntry(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEntry() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForEntry(fieldValues, isApproved);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForEncumbrance(Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForEncumbrance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEncumbrance() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForEncumbrance(fieldValues, isApproved);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForCashBalance(java.util.Map,
     *      boolean)
     */
    public Iterator findPendingLedgerEntriesForCashBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForCashBalance() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForCashBalance(fieldValues, isApproved);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForBalance(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForBalance() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForBalance(fieldValues, isApproved);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    public Iterator findPendingLedgerEntriesForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForAccountBalance() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForAccountBalance(fieldValues, isApproved);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntrySummaryForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    public Iterator findPendingLedgerEntrySummaryForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntrySummaryForAccountBalance() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntrySummaryForAccountBalance(fieldValues, isApproved);
    }

    public Collection findPendingEntries(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingEntries() started");

        return generalLedgerPendingEntryDao.findPendingEntries(fieldValues, isApproved);
    }
    
    /**
     * A helper method that checks the intended target value for null and empty strings. If the intended target value is not null or
     * an empty string, it returns that value, ohterwise, it returns a backup value.
     * 
     * @param targetValue
     * @param backupValue
     * @return String
     */
    protected final String getEntryValue(String targetValue, String backupValue) {
        LOG.debug("getEntryValue(String, String) - start");

        if (StringUtils.isNotBlank(targetValue)) {
            LOG.debug("getEntryValue(String, String) - end");
            return targetValue;
        }
        else {
            LOG.debug("getEntryValue(String, String) - end");
            return backupValue;
        }
    }

    public void setBalanceTypeService(BalanceTypService balanceTypeService) {
        this.balanceTypeService = balanceTypeService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setGeneralLedgerPendingEntryDao(GeneralLedgerPendingEntryDao generalLedgerPendingEntryDao) {
        this.generalLedgerPendingEntryDao = generalLedgerPendingEntryDao;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}