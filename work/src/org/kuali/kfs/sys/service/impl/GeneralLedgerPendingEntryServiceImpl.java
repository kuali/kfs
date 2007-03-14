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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.dao.GeneralLedgerPendingEntryDao;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.rule.event.GenerateGeneralLedgerDocumentPendingEntriesEvent;
import org.kuali.kfs.rule.event.GenerateGeneralLedgerPendingEntriesEvent;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.UniversityDate;
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
    private KualiConfigurationService kualiConfigurationService;
    private BalanceTypService balanceTypeService;

    /**
     * 
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getExpenseSummary(java.util.List, java.lang.String,
     *      java.lang.String, boolean, boolean)
     */
    public KualiDecimal getExpenseSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isDebit, boolean isYearEnd) {
        LOG.debug("getExpenseSummary() started");

        String[] objectTypes = kualiConfigurationService.getApplicationParameterValues("SYSTEM", "ExpenseObjectTypeCodes");

        Options options = optionsService.getOptions(universityFiscalYear);

        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, Arrays.asList(objectTypes), balanceTypeCodes, sufficientFundsObjectCode, isDebit, isYearEnd);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getEncumbranceSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean, boolean)
     */
    public KualiDecimal getEncumbranceSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isDebit, boolean isYearEnd) {
        LOG.debug("getEncumbranceSummary() started");

        String[] objectTypes = kualiConfigurationService.getApplicationParameterValues("SYSTEM", "ExpenseObjectTypeCodes");

        Options options = optionsService.getOptions(universityFiscalYear);

        List<String> balanceTypeCodes = new ArrayList();
        for (BalanceTyp balanceTyp : (Collection<BalanceTyp>) balanceTypeService.getEncumbranceBalanceTypes()) {
            balanceTypeCodes.add(balanceTyp.getCode());
        }

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, Arrays.asList(objectTypes), balanceTypeCodes, sufficientFundsObjectCode, isDebit, isYearEnd);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getBudgetSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean)
     */
    public KualiDecimal getBudgetSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isYearEnd) {
        LOG.debug("getBudgetSummary() started");

        String[] objectTypes = kualiConfigurationService.getApplicationParameterValues("SYSTEM", "ExpenseObjectTypeCodes");

        Options options = optionsService.getOptions(universityFiscalYear);

        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getBudgetCheckingBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, Arrays.asList(objectTypes), balanceTypeCodes, sufficientFundsObjectCode, isYearEnd);
    }

    /**
     * 
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
     * 
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getActualSummary(java.util.List, java.lang.String,
     *      java.lang.String, boolean)
     */
    public KualiDecimal getActualSummary(List universityFiscalYears, String chartOfAccountsCode, String accountNumber, boolean isDebit) {
        LOG.debug("getActualSummary() started");

        String[] codes = kualiConfigurationService.getApplicationParameterValues("Kuali.FinancialTransactionProcessing.SufficientFundsService", "SufficientFundsServiceSpecialFinancialObjectCodes");

        // Note, we are getting the options from the first fiscal year in the list. We are assuming that the
        // balance type code for actual is the same in all the years in the list.
        Options options = optionsService.getOptions((Integer) universityFiscalYears.get(0));

        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYears, chartOfAccountsCode, accountNumber, Arrays.asList(codes), balanceTypeCodes, isDebit);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getByPrimaryId(java.lang.Integer, java.lang.String)
     */
    public GeneralLedgerPendingEntry getByPrimaryId(Integer transactionEntrySequenceId, String documentHeaderId) {
        LOG.debug("getByPrimaryId() started");

        return generalLedgerPendingEntryDao.getByPrimaryId(documentHeaderId, transactionEntrySequenceId);
    }

    public void deleteEntriesForCancelledOrDisapprovedDocuments() {
        LOG.debug("deleteEntriesForCancelledOrDisapprovedDocuments() started");

        generalLedgerPendingEntryDao.deleteEntriesForCancelledOrDisapprovedDocuments();
    }

    public void fillInFiscalPeriodYear(GeneralLedgerPendingEntry glpe) {
        LOG.debug("fillInFiscalPeriodYear() started");

        // TODO Handle year end documents

        if ((glpe.getUniversityFiscalPeriodCode() == null) || (glpe.getUniversityFiscalYear() == null)) {
            UniversityDate ud = SpringServiceLocator.getUniversityDateService().getCurrentUniversityDate();

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
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPostingDocument document) {
        boolean success = true;

        // we must clear them first before creating new ones
        document.getGeneralLedgerPendingEntries().clear();

        LOG.info("deleting existing gl pending ledger entries for document " + document.getDocumentNumber());
        delete(document.getDocumentNumber());

        LOG.info("generating gl pending ledger entries for document " + document.getDocumentNumber());
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
        if (document instanceof AccountingDocument) {
            AccountingDocument transactionalDocument = (AccountingDocument) document;
            List sourceAccountingLines = transactionalDocument.getSourceAccountingLines();
            if (sourceAccountingLines != null) {
                for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
                    success &= processGeneralLedgerPendingEntryForAccountingLine(transactionalDocument, sequenceHelper, iter);
                }
            }

            List targetAccountingLines = transactionalDocument.getTargetAccountingLines();
            if (targetAccountingLines != null) {
                for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
                    success &= processGeneralLedgerPendingEntryForAccountingLine(transactionalDocument, sequenceHelper, iter);
                }
            }
        }

        // doc specific pending entries generation
        GenerateGeneralLedgerDocumentPendingEntriesEvent event = new GenerateGeneralLedgerDocumentPendingEntriesEvent(document, sequenceHelper);
        success &= kualiRuleService.applyRules(event);
        return success;
    }

    /**
     * This method handles generically taking an accounting line, doing a deep copy on it so that we have a new instance without
     * reference to the original (won't affect the tran doc's acct lines), performing a retrieveNonKeyFields on the line to make
     * sure it's populated properly, and then calling the rule framework driven GLPE generation code.
     * 
     * @param document
     * @param sequenceHelper
     * @param iter
     * @return whether the business rules succeeded
     */
    private boolean processGeneralLedgerPendingEntryForAccountingLine(AccountingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, Iterator iter) {
        LOG.debug("processGeneralLedgerPendingEntryForAccountingLine() started");
        boolean success = true;

        AccountingLine accountingLine = (AccountingLine) iter.next();

        GenerateGeneralLedgerPendingEntriesEvent event = new GenerateGeneralLedgerPendingEntriesEvent(document, accountingLine, sequenceHelper);
        success &= kualiRuleService.applyRules(event);
        sequenceHelper.increment(); // increment for the next line
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

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findApprovedPendingLedgerEntries());
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntries(org.kuali.module.gl.bo.Encumbrance,
     *      boolean)
     */
    public Iterator findPendingLedgerEntries(Encumbrance encumbrance, boolean isApproved) {
        LOG.debug("findPendingLedgerEntries() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findPendingLedgerEntries(encumbrance, isApproved));
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

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findPendingLedgerEntries(balance, isApproved, isConsolidated));
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForEntry(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForEntry(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEntry() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findPendingLedgerEntriesForEntry(fieldValues, isApproved));
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForEncumbrance(Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForEncumbrance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEncumbrance() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findPendingLedgerEntriesForEncumbrance(fieldValues, isApproved));
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForCashBalance(java.util.Map,
     *      boolean)
     */
    public Iterator findPendingLedgerEntriesForCashBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForCashBalance() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findPendingLedgerEntriesForCashBalance(fieldValues, isApproved));
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForBalance(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForBalance() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findPendingLedgerEntriesForBalance(fieldValues, isApproved));
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    public Iterator findPendingLedgerEntriesForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForAccountBalance() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findPendingLedgerEntriesForAccountBalance(fieldValues, isApproved));
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntrySummaryForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    public Iterator findPendingLedgerEntrySummaryForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntrySummaryForAccountBalance() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(generalLedgerPendingEntryDao.findPendingLedgerEntrySummaryForAccountBalance(fieldValues, isApproved));
    }

    public Collection findPendingEntries(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingEntries() started");

        return generalLedgerPendingEntryDao.findPendingEntries(fieldValues, isApproved);
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

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}