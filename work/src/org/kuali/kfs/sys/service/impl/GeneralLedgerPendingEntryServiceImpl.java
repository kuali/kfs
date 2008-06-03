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

import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_ACTUAL;
import static org.kuali.kfs.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.dao.GeneralLedgerPendingEntryDao;
import org.kuali.kfs.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.HomeOriginationService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.SufficientFundsService;
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

        List<String> balanceTypeCodes = balanceTypeService.getEncumbranceBalanceTypes(universityFiscalYear);


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
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySource glpeSource) {
        boolean success = true;

        // we must clear them first before creating new ones
        glpeSource.clearAnyGeneralLedgerPendingEntries();

        LOG.info("deleting existing gl pending ledger entries for document " + glpeSource.getDocumentHeader().getDocumentNumber());
        delete(glpeSource.getDocumentHeader().getDocumentNumber());

        LOG.info("generating gl pending ledger entries for document " + glpeSource.getDocumentHeader().getDocumentNumber());
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
        List<GeneralLedgerPendingEntry> entries;
        for (GeneralLedgerPendingEntrySourceDetail glpeSourceDetail : glpeSource.getGeneralLedgerPendingEntrySourceDetails()) {
            success &= glpeSource.generateGeneralLedgerPendingEntries(glpeSourceDetail, sequenceHelper);
            sequenceHelper.increment();
        }

        // doc specific pending entries generation
        success = glpeSource.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);
        return success;
    }

    /**
     * This populates an empty GeneralLedgerPendingEntry explicitEntry object instance with default values.
     * 
     * @param accountingDocument
     * @param accountingLine
     * @param sequenceHelper
     * @param explicitEntry
     */
    public void populateExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry explicitEntry) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        explicitEntry.setFinancialDocumentTypeCode(glpeSource.getFinancialDocumentTypeCode());
        explicitEntry.setVersionNumber(new Long(1));
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        explicitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        explicitEntry.setTransactionEntryProcessedTs(new java.sql.Date(transactionTimestamp.getTime()));
        explicitEntry.setAccountNumber(glpeSourceDetail.getAccountNumber());
        if (glpeSourceDetail.getAccount().getAccountSufficientFundsCode() == null) {
            glpeSourceDetail.getAccount().setAccountSufficientFundsCode(KFSConstants.SF_TYPE_NO_CHECKING);
        }
        explicitEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(glpeSourceDetail.getObjectCode(), glpeSourceDetail.getAccount().getAccountSufficientFundsCode()));
        explicitEntry.setFinancialDocumentApprovedCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.NO);
        explicitEntry.setTransactionEncumbranceUpdateCode(BLANK_SPACE);
        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL); // this is the default that most documents use
        explicitEntry.setChartOfAccountsCode(glpeSourceDetail.getChartOfAccountsCode());
        explicitEntry.setTransactionDebitCreditCode(glpeSource.isDebit(glpeSourceDetail) ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        explicitEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
        explicitEntry.setDocumentNumber(glpeSourceDetail.getDocumentNumber());
        explicitEntry.setFinancialObjectCode(glpeSourceDetail.getFinancialObjectCode());
        ObjectCode objectCode = glpeSourceDetail.getObjectCode();
        if (ObjectUtils.isNull(objectCode)) {
            glpeSourceDetail.refreshReferenceObject("objectCode");
        }
        explicitEntry.setFinancialObjectTypeCode(glpeSourceDetail.getObjectCode().getFinancialObjectTypeCode());
        explicitEntry.setOrganizationDocumentNumber(glpeSource.getDocumentHeader().getOrganizationDocumentNumber());
        explicitEntry.setOrganizationReferenceId(glpeSourceDetail.getOrganizationReferenceId());
        explicitEntry.setProjectCode(getEntryValue(glpeSourceDetail.getProjectCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode()));
        explicitEntry.setReferenceFinancialDocumentNumber(getEntryValue(glpeSourceDetail.getReferenceNumber(), BLANK_SPACE));
        explicitEntry.setReferenceFinancialDocumentTypeCode(getEntryValue(glpeSourceDetail.getReferenceTypeCode(), BLANK_SPACE));
        explicitEntry.setReferenceFinancialSystemOriginationCode(getEntryValue(glpeSourceDetail.getReferenceOriginCode(), BLANK_SPACE));
        explicitEntry.setSubAccountNumber(getEntryValue(glpeSourceDetail.getSubAccountNumber(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber()));
        explicitEntry.setFinancialSubObjectCode(getEntryValue(glpeSourceDetail.getFinancialSubObjectCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode()));
        explicitEntry.setTransactionEntryOffsetIndicator(false);
        explicitEntry.setTransactionLedgerEntryAmount(glpeSource.getGeneralLedgerPendingEntryAmountForDetail(glpeSourceDetail));
        explicitEntry.setTransactionLedgerEntryDescription(getEntryValue(glpeSourceDetail.getFinancialDocumentLineDescription(), glpeSource.getDocumentHeader().getFinancialDocumentDescription()));
        explicitEntry.setUniversityFiscalPeriodCode(null); // null here, is assigned during batch or in specific document rule
        // classes
        explicitEntry.setUniversityFiscalYear(glpeSource.getPostingYear());
        // TODO wait for core budget year data structures to be put in place
        // explicitEntry.setBudgetYear(accountingLine.getBudgetYear());
        // explicitEntry.setBudgetYearFundingSourceCode(budgetYearFundingSourceCode);

        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
    }

    /**
     * This populates an GeneralLedgerPendingEntry offsetEntry object instance with values that differ from the values supplied in
     * the explicit entry that it was cloned from. Note that the entries do not contain BOs now.
     * 
     * @param universityFiscalYear
     * @param explicitEntry
     * @param sequenceHelper
     * @param offsetEntry Cloned from the explicit entry
     * @return whether the offset generation is successful
     */
    public boolean populateOffsetGeneralLedgerPendingEntry(Integer universityFiscalYear, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("populateOffsetGeneralLedgerPendingEntry(Integer, GeneralLedgerPendingEntry, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        boolean success = true;

        // lookup offset object info
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(universityFiscalYear, explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        if (ObjectUtils.isNull(offsetDefinition)) {
            success = false;
            GlobalVariables.getErrorMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_OFFSET_DEFINITION, universityFiscalYear.toString(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        }
        else {
            OffsetAccount flexibleOffsetAccount = SpringContext.getBean(FlexibleOffsetAccountService.class).getByPrimaryIdIfEnabled(explicitEntry.getChartOfAccountsCode(), explicitEntry.getAccountNumber(), getOffsetFinancialObjectCode(offsetDefinition));
            flexOffsetAccountIfNecessary(flexibleOffsetAccount, offsetEntry);
        }

        // update offset entry fields that are different from the explicit entry that it was created from
        offsetEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        offsetEntry.setTransactionDebitCreditCode(getOffsetEntryDebitCreditCode(explicitEntry));

        String offsetObjectCode = getOffsetFinancialObjectCode(offsetDefinition);
        offsetEntry.setFinancialObjectCode(offsetObjectCode);
        if (offsetObjectCode.equals(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode())) {
            // no BO, so punt
            offsetEntry.setAcctSufficientFundsFinObjCd(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode());
        }
        else {
            // Need current ObjectCode and Account BOs to get sufficient funds code. (Entries originally have no BOs.)
            // todo: private or other methods to get these BOs, instead of using the entry and leaving some BOs filled in?
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            ObjectCode financialObject = offsetEntry.getFinancialObject();
            // The ObjectCode reference may be invalid because a flexible offset account changed its chart code.
            if (ObjectUtils.isNull(financialObject)) {
                throw new ReferentialIntegrityException("offset object code " + offsetEntry.getUniversityFiscalYear() + "-" + offsetEntry.getChartOfAccountsCode() + "-" + offsetEntry.getFinancialObjectCode());
            }
            offsetEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(financialObject, offsetEntry.getAccount().getAccountSufficientFundsCode()));
        }

        offsetEntry.setFinancialObjectTypeCode(getOffsetFinancialObjectTypeCode(offsetDefinition));
        offsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        offsetEntry.setTransactionEntryOffsetIndicator(true);
        offsetEntry.setTransactionLedgerEntryDescription(KFSConstants.GL_PE_OFFSET_STRING);

        LOG.debug("populateOffsetGeneralLedgerPendingEntry(Integer, GeneralLedgerPendingEntry, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
        return success;
    }

    /**
     * Applies the given flexible offset account to the given offset entry. Does nothing if flexibleOffsetAccount is null or its COA
     * and account number are the same as the offset entry's.
     * 
     * @param flexibleOffsetAccount may be null
     * @param offsetEntry may be modified
     */
    private void flexOffsetAccountIfNecessary(OffsetAccount flexibleOffsetAccount, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("flexOffsetAccountIfNecessary(OffsetAccount, GeneralLedgerPendingEntry) - start");

        if (flexibleOffsetAccount == null) {
            LOG.debug("flexOffsetAccountIfNecessary(OffsetAccount, GeneralLedgerPendingEntry) - end");
            return; // They are not required and may also be disabled.
        }
        String flexCoa = flexibleOffsetAccount.getFinancialOffsetChartOfAccountCode();
        String flexAccountNumber = flexibleOffsetAccount.getFinancialOffsetAccountNumber();
        if (flexCoa.equals(offsetEntry.getChartOfAccountsCode()) && flexAccountNumber.equals(offsetEntry.getAccountNumber())) {
            LOG.debug("flexOffsetAccountIfNecessary(OffsetAccount, GeneralLedgerPendingEntry) - end");
            return; // no change, so leave sub-account as is
        }
        if (ObjectUtils.isNull(flexibleOffsetAccount.getFinancialOffsetAccount())) {
            throw new ReferentialIntegrityException("flexible offset account " + flexCoa + "-" + flexAccountNumber);
        }
        offsetEntry.setChartOfAccountsCode(flexCoa);
        offsetEntry.setAccountNumber(flexAccountNumber);
        // COA and account number are part of the sub-account's key, so the original sub-account would be invalid.
        offsetEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());

        LOG.debug("flexOffsetAccountIfNecessary(OffsetAccount, GeneralLedgerPendingEntry) - end");
    }

    /**
     * Helper method that determines the offset entry's financial object type code.
     * 
     * @param offsetDefinition
     * @return String
     */
    protected String getOffsetFinancialObjectTypeCode(OffsetDefinition offsetDefinition) {
        LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - start");

        if (null != offsetDefinition && null != offsetDefinition.getFinancialObject()) {
            String returnString = getEntryValue(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode(), AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType());
            LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - end");
            return returnString;
        }
        else {
            LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - end");
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType();
        }

    }

    /**
     * Helper method that determines the debit/credit code for the offset entry. If the explicit was a debit, the offset is a
     * credit. Otherwise, it's opposite.
     * 
     * @param explicitEntry
     * @return String
     */
    protected String getOffsetEntryDebitCreditCode(GeneralLedgerPendingEntry explicitEntry) {
        LOG.debug("getOffsetEntryDebitCreditCode(GeneralLedgerPendingEntry) - start");

        String offsetDebitCreditCode = KFSConstants.GL_BUDGET_CODE;
        if (KFSConstants.GL_DEBIT_CODE.equals(explicitEntry.getTransactionDebitCreditCode())) {
            offsetDebitCreditCode = KFSConstants.GL_CREDIT_CODE;
        }
        else if (KFSConstants.GL_CREDIT_CODE.equals(explicitEntry.getTransactionDebitCreditCode())) {
            offsetDebitCreditCode = KFSConstants.GL_DEBIT_CODE;
        }

        LOG.debug("getOffsetEntryDebitCreditCode(GeneralLedgerPendingEntry) - end");
        return offsetDebitCreditCode;
    }

    /**
     * Helper method that determines the offset entry's financial object code.
     * 
     * @param offsetDefinition
     * @return String
     */
    protected String getOffsetFinancialObjectCode(OffsetDefinition offsetDefinition) {
        LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - start");

        if (null != offsetDefinition) {
            String returnString = getEntryValue(offsetDefinition.getFinancialObjectCode(), AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode());
            LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - end");
            return returnString;
        }
        else {
            LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - end");
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode();
        }

    }

    /**
     * This populates an empty GeneralLedgerPendingEntry instance with default values for a bank offset. A global error will be
     * posted as a side-effect if the given BankAccount has not defined the necessary bank offset relations.
     * 
     * @param bankAccount
     * @param depositAmount
     * @param financialDocument
     * @param universityFiscalYear
     * @param sequenceHelper
     * @param bankOffsetEntry
     * @param errorPropertyName
     * @return whether the entry was populated successfully
     */
    public boolean populateBankOffsetGeneralLedgerPendingEntry(BankAccount bankAccount, KualiDecimal depositAmount, GeneralLedgerPostingDocument financialDocument, Integer universityFiscalYear, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry bankOffsetEntry, String errorPropertyName) {
        bankOffsetEntry.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(financialDocument.getClass()));
        bankOffsetEntry.setVersionNumber(1L);
        bankOffsetEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        bankOffsetEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        bankOffsetEntry.setTransactionEntryProcessedTs(new java.sql.Date(transactionTimestamp.getTime()));
        Account cashOffsetAccount = bankAccount.getCashOffsetAccount();
        if (ObjectUtils.isNull(cashOffsetAccount)) {
            // Bank account offsets are optional, so a BankAccount might not have the necessary relations. Validate them here where
            // they are used so the need is clear.
            // todo: put() varargs, revisit this in phase 2 when error reporting is discussed. -Leo
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_NO_ACCOUNT, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber() });
            return false;
        }
        if (cashOffsetAccount.isAccountClosedIndicator()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_ACCOUNT_CLOSED, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber() });
            return false;
        }
        if (cashOffsetAccount.isExpired()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_ACCOUNT_EXPIRED, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber() });
            return false;
        }
        bankOffsetEntry.setChartOfAccountsCode(bankAccount.getCashOffsetFinancialChartOfAccountCode());
        bankOffsetEntry.setAccountNumber(bankAccount.getCashOffsetAccountNumber());
        bankOffsetEntry.setFinancialDocumentApprovedCode(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.NO);
        bankOffsetEntry.setTransactionEncumbranceUpdateCode(BLANK_SPACE);
        bankOffsetEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        bankOffsetEntry.setTransactionDebitCreditCode(depositAmount.isPositive() ? GL_DEBIT_CODE : GL_CREDIT_CODE);
        bankOffsetEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
        bankOffsetEntry.setDocumentNumber(financialDocument.getDocumentNumber());
        ObjectCode cashOffsetObject = bankAccount.getCashOffsetObject();
        if (ObjectUtils.isNull(cashOffsetObject)) {
            // Bank account offsets are optional, so a BankAccount might not have the necessary relations. Validate them here where
            // they are used so the need is clear.
            // todo: put() varargs, revisit this in phase 2 when error reporting is discussed. -Leo
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_NO_OBJECT_CODE, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber() });
            return false;
        }
        if (!cashOffsetObject.isFinancialObjectActiveCode()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_OBJECT_CODE, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber(), cashOffsetObject.getFinancialObjectCode() });
            return false;
        }
        bankOffsetEntry.setFinancialObjectCode(bankAccount.getCashOffsetObjectCode());
        bankOffsetEntry.setFinancialObjectTypeCode(bankAccount.getCashOffsetObject().getFinancialObjectTypeCode());
        bankOffsetEntry.setOrganizationDocumentNumber(financialDocument.getDocumentHeader().getOrganizationDocumentNumber());
        bankOffsetEntry.setOrganizationReferenceId(null);
        bankOffsetEntry.setProjectCode(KFSConstants.getDashProjectCode());
        bankOffsetEntry.setReferenceFinancialDocumentNumber(null);
        bankOffsetEntry.setReferenceFinancialDocumentTypeCode(null);
        bankOffsetEntry.setReferenceFinancialSystemOriginationCode(null);
        if (StringUtils.isBlank(bankAccount.getCashOffsetSubAccountNumber())) {
            bankOffsetEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        else {
            SubAccount cashOffsetSubAccount = bankAccount.getCashOffsetSubAccount();
            if (ObjectUtils.isNull(cashOffsetSubAccount)) {
                GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_NONEXISTENT_SUB_ACCOUNT, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber(), bankAccount.getCashOffsetSubAccountNumber() });
                return false;
            }
            if (!cashOffsetSubAccount.isSubAccountActiveIndicator()) {
                GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_SUB_ACCOUNT, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber(), bankAccount.getCashOffsetSubAccountNumber() });
                return false;
            }
            bankOffsetEntry.setSubAccountNumber(bankAccount.getCashOffsetSubAccountNumber());
        }
        if (StringUtils.isBlank(bankAccount.getCashOffsetSubObjectCode())) {
            bankOffsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            SubObjCd cashOffsetSubObject = bankAccount.getCashOffsetSubObject();
            if (ObjectUtils.isNull(cashOffsetSubObject)) {
                GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_NONEXISTENT_SUB_OBJ, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber(), cashOffsetObject.getFinancialObjectCode(), bankAccount.getCashOffsetSubObjectCode() });
                return false;
            }
            if (!cashOffsetSubObject.isFinancialSubObjectActiveIndicator()) {
                GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_SUB_OBJ, new String[] { bankAccount.getFinancialDocumentBankCode(), bankAccount.getFinDocumentBankAccountNumber(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber(), cashOffsetObject.getFinancialObjectCode(), bankAccount.getCashOffsetSubObjectCode() });
                return false;
            }
            bankOffsetEntry.setFinancialSubObjectCode(bankAccount.getCashOffsetSubObjectCode());
        }
        bankOffsetEntry.setFinancialSubObjectCode(getEntryValue(bankAccount.getCashOffsetSubObjectCode(), KFSConstants.getDashFinancialSubObjectCode()));
        bankOffsetEntry.setTransactionEntryOffsetIndicator(true);
        bankOffsetEntry.setTransactionLedgerEntryAmount(depositAmount.abs());
        bankOffsetEntry.setUniversityFiscalPeriodCode(null); // null here, is assigned during batch or in specific document rule
        // classes
        bankOffsetEntry.setUniversityFiscalYear(universityFiscalYear);
        // TODO wait for core budget year data structures to be put in place
        // bankOffsetEntry.setBudgetYear(accountingLine.getBudgetYear());
        // bankOffsetEntry.setBudgetYearFundingSourceCode(budgetYearFundingSourceCode);
        bankOffsetEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(cashOffsetObject, cashOffsetAccount.getAccountSufficientFundsCode()));
        return true;
    }

    /**
     * @see org.kuali.kfs.service.GeneralLedgerPendingEntryService#save(org.kuali.kfs.bo.GeneralLedgerPendingEntry)
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