/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_ACTUAL;
import static org.kuali.kfs.sys.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.fp.businessobject.OffsetAccount;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.gl.service.SufficientFundsServiceConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the GeneralLedgerPendingEntry structure. This is the default implementation, that is
 * delivered with Kuali.
 */
@Transactional
public class GeneralLedgerPendingEntryServiceImpl implements GeneralLedgerPendingEntryService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerPendingEntryServiceImpl.class);

    protected GeneralLedgerPendingEntryDao generalLedgerPendingEntryDao;
    protected KualiRuleService kualiRuleService;
    protected ChartService chartService;
    protected OptionsService optionsService;
    protected ParameterService parameterService;
    protected BalanceTypeService balanceTypeService;
    protected DateTimeService dateTimeService;
    protected DataDictionaryService dataDictionaryService;
    protected PersistenceStructureService persistenceStructureService;
    protected UniversityDateService universityDateService;

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getExpenseSummary(java.util.List, java.lang.String,
     *      java.lang.String, boolean, boolean)
     */
    @Override
    public KualiDecimal getExpenseSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isDebit, boolean isYearEnd) {
        LOG.debug("getExpenseSummary() started");

        // FIXME! - this ObjectTypeService should be injected
        ObjectTypeService objectTypeService = SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getExpenseObjectTypes(universityFiscalYear);

        // FIXME! - cache this list - balance type code will not change during the lifetime of the server
        SystemOptions options = optionsService.getOptions(universityFiscalYear);

        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, objectTypes, balanceTypeCodes, sufficientFundsObjectCode, isDebit, isYearEnd);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getEncumbranceSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean, boolean)
     */
    @Override
    public KualiDecimal getEncumbranceSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isDebit, boolean isYearEnd) {
        LOG.debug("getEncumbranceSummary() started");

        // FIXME! - this ObjectTypeService should be injected
        ObjectTypeService objectTypeService = SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getExpenseObjectTypes(universityFiscalYear);

        SystemOptions options = optionsService.getOptions(universityFiscalYear);

        // FIXME! - cache this list - balance type code will not change during the lifetime of the server
        List<String> balanceTypeCodes = balanceTypeService.getEncumbranceBalanceTypes(universityFiscalYear);


        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, objectTypes, balanceTypeCodes, sufficientFundsObjectCode, isDebit, isYearEnd);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getBudgetSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean)
     */
    @Override
    public KualiDecimal getBudgetSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, boolean isYearEnd) {
        LOG.debug("getBudgetSummary() started");

        ObjectTypeService objectTypeService = SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getExpenseObjectTypes(universityFiscalYear);

        SystemOptions options = optionsService.getOptions(universityFiscalYear);

        // FIXME! - cache this list - balance type code will not change during the lifetime of the server
        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getBudgetCheckingBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYear, chartOfAccountsCode, accountNumber, objectTypes, balanceTypeCodes, sufficientFundsObjectCode, isYearEnd);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getCashSummary(java.util.Collection, java.lang.String,
     *      java.lang.String, boolean)
     */
    @Override
    public KualiDecimal getCashSummary(List universityFiscalYears, String chartOfAccountsCode, String accountNumber, boolean isDebit) {
        LOG.debug("getCashSummary() started");

        Chart c = chartService.getByPrimaryId(chartOfAccountsCode);

        // Note, we are getting the options from the first fiscal year in the list. We are assuming that the
        // balance type code for actual is the same in all the years in the list.
        SystemOptions options = optionsService.getOptions((Integer) universityFiscalYears.get(0));

        // FIXME! - cache this list - will not change during the lifetime of the server
        Collection objectCodes = new ArrayList();
        objectCodes.add(c.getFinancialCashObjectCode());

        // FIXME! - cache this list - balance type code will not change during the lifetime of the server
        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYears, chartOfAccountsCode, accountNumber, objectCodes, balanceTypeCodes, isDebit);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getActualSummary(java.util.List, java.lang.String,
     *      java.lang.String, boolean)
     */
    @Override
    public KualiDecimal getActualSummary(List universityFiscalYears, String chartOfAccountsCode, String accountNumber, boolean isDebit) {
        LOG.debug("getActualSummary() started");

        List<String> codes = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, SufficientFundsServiceConstants.SUFFICIENT_FUNDS_OBJECT_CODE_SPECIALS) );

        // Note, we are getting the options from the first fiscal year in the list. We are assuming that the
        // balance type code for actual is the same in all the years in the list.
        SystemOptions options = optionsService.getOptions((Integer) universityFiscalYears.get(0));

        // FIXME! - cache this list - balance type code will not change during the lifetime of the server
        Collection balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());

        return generalLedgerPendingEntryDao.getTransactionSummary(universityFiscalYears, chartOfAccountsCode, accountNumber, codes, balanceTypeCodes, isDebit);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#getByPrimaryId(java.lang.Integer, java.lang.String)
     */
    @Override
    public GeneralLedgerPendingEntry getByPrimaryId(Integer transactionEntrySequenceId, String documentHeaderId) {
        LOG.debug("getByPrimaryId() started");
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentHeaderId);
        keys.put("transactionLedgerEntrySequenceNumber", transactionEntrySequenceId);
        // FIXME! - this ObjectService should be injected
        return SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(GeneralLedgerPendingEntry.class, keys);
    }

    @Override
    public void fillInFiscalPeriodYear(GeneralLedgerPendingEntry glpe) {
        LOG.debug("fillInFiscalPeriodYear() started");

        // TODO Handle year end documents

        if ((glpe.getUniversityFiscalPeriodCode() == null) || (glpe.getUniversityFiscalYear() == null)) {
            UniversityDate ud = universityDateService.getCurrentUniversityDate();

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
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySource glpeSource) {
        boolean success = true;

        // we must clear them first before creating new ones
        glpeSource.clearAnyGeneralLedgerPendingEntries();

        if (LOG.isDebugEnabled()) {
            LOG.debug("deleting existing gl pending ledger entries for document " + glpeSource.getDocumentHeader().getDocumentNumber());
        }
        delete(glpeSource.getDocumentHeader().getDocumentNumber());

        if (LOG.isDebugEnabled()) {
            LOG.debug("generating gl pending ledger entries for document " + glpeSource.getDocumentHeader().getDocumentNumber());
        }

        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
        for (GeneralLedgerPendingEntrySourceDetail glpeSourceDetail : glpeSource.getGeneralLedgerPendingEntrySourceDetails()) {
            success &= glpeSource.generateGeneralLedgerPendingEntries(glpeSourceDetail, sequenceHelper);
            sequenceHelper.increment();
        }

        // doc specific pending entries generation
        success &= glpeSource.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);

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
    @Override
    public void populateExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry explicitEntry) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");
        }

        explicitEntry.setFinancialDocumentTypeCode(glpeSource.getFinancialDocumentTypeCode());
        explicitEntry.setVersionNumber(new Long(1));
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        Timestamp transactionTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());
        explicitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        explicitEntry.setTransactionEntryProcessedTs(transactionTimestamp);
        explicitEntry.setAccountNumber(glpeSourceDetail.getAccountNumber());

        Account account = SpringContext.getBean(AccountService.class).getByPrimaryIdWithCaching(glpeSourceDetail.getChartOfAccountsCode(), glpeSourceDetail.getAccountNumber());
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryIdWithCaching( glpeSource.getPostingYear(), glpeSourceDetail.getChartOfAccountsCode(), glpeSourceDetail.getFinancialObjectCode());

        if ( account != null ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("GLPE: Testing to see what should be used for SF Object Code: " + glpeSourceDetail );
            }
            String sufficientFundsCode = account.getAccountSufficientFundsCode();
            if (StringUtils.isBlank(sufficientFundsCode)) {
                sufficientFundsCode = KFSConstants.SF_TYPE_NO_CHECKING;
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Code was blank on the account - using 'N'");
                }
            }

            if (objectCode != null) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("SF Code / Object: " + sufficientFundsCode + " / " + objectCode);
                }
                String sifficientFundsObjectCode = SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(objectCode, sufficientFundsCode);
                explicitEntry.setAcctSufficientFundsFinObjCd(sifficientFundsObjectCode);
            } else {
                LOG.debug( "Object code object was null, skipping setting of SF object field." );
            }
        }

        if ( objectCode != null ) {
            explicitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        }

        explicitEntry.setFinancialDocumentApprovedCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.NO);
        explicitEntry.setTransactionEncumbranceUpdateCode(BLANK_SPACE);
        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL); // this is the default that most documents use
        explicitEntry.setChartOfAccountsCode(glpeSourceDetail.getChartOfAccountsCode());
        explicitEntry.setTransactionDebitCreditCode(glpeSource.isDebit(glpeSourceDetail) ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        explicitEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
        explicitEntry.setDocumentNumber(glpeSourceDetail.getDocumentNumber());
        explicitEntry.setFinancialObjectCode(glpeSourceDetail.getFinancialObjectCode());

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
        explicitEntry.setTransactionLedgerEntryDescription(getEntryValue(glpeSourceDetail.getFinancialDocumentLineDescription(), glpeSource.getDocumentHeader().getDocumentDescription()));
        explicitEntry.setUniversityFiscalPeriodCode(null); // null here, is assigned during batch or in specific document rule
        // classes
        explicitEntry.setUniversityFiscalYear(glpeSource.getPostingYear());
        // TODO wait for core budget year data structures to be put in place
        // explicitEntry.setBudgetYear(accountingLine.getBudgetYear());
        // explicitEntry.setBudgetYearFundingSourceCode(budgetYearFundingSourceCode);

        if (LOG.isDebugEnabled()) {
            LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
        }
    }

    /**
     * Convenience method to build a GLPE without a generalLedgerPendingEntrySourceDetail
     *
     * @param document a GeneralLedgerPostingDocument
     * @param account the account for use in the GLPE
     * @param objectCode the object code for use in the GLPE
     * @param subAccountNumber the sub account number for use in the GLPE
     * @param subObjectCode the subobject code for use in the GLPE
     * @param organizationReferenceId the organization reference id to use in the GLPE
     * @param projectCode the project code to use in the GLPE
     * @param referenceNumber the reference number to use in the GLPE
     * @param referenceTypeCode the reference type code to use in the GLPE
     * @param referenceOriginCode the reference origin code to use in the GLPE
     * @param description the description to put in the GLPE
     * @param isDebit true if the GLPE represents a debit, false if it represents a credit
     * @param amount the amount of the GLPE
     * @param sequenceHelper the sequence helper to use
     * @return a populated general ledger pending entry
     */
    @Override
    public GeneralLedgerPendingEntry buildGeneralLedgerPendingEntry(GeneralLedgerPostingDocument document, Account account, ObjectCode objectCode, String subAccountNumber, String subObjectCode, String organizationReferenceId, String projectCode, String referenceNumber, String referenceTypeCode, String referenceOriginCode, String description, boolean isDebit, KualiDecimal amount, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");
        }

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        explicitEntry.setFinancialDocumentTypeCode(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        explicitEntry.setVersionNumber(new Long(1));
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        Timestamp transactionTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());
        explicitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        explicitEntry.setTransactionEntryProcessedTs(transactionTimestamp);
        explicitEntry.setAccountNumber(account.getAccountNumber());
        if (account.getAccountSufficientFundsCode() == null) {
            account.setAccountSufficientFundsCode(KFSConstants.SF_TYPE_NO_CHECKING);
        }
        // FIXME! - inject the sufficient funds service
        explicitEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(objectCode, account.getAccountSufficientFundsCode()));
        explicitEntry.setFinancialDocumentApprovedCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.NO);
        explicitEntry.setTransactionEncumbranceUpdateCode(BLANK_SPACE);
        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL); // this is the default that most documents use
        explicitEntry.setChartOfAccountsCode(account.getChartOfAccountsCode());
        explicitEntry.setTransactionDebitCreditCode(isDebit ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        // FIXME! - Home origination service should be injected and the result cached - this value never changes
        explicitEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
        explicitEntry.setDocumentNumber(document.getDocumentNumber());
        explicitEntry.setFinancialObjectCode(objectCode.getFinancialObjectCode());
        explicitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        explicitEntry.setOrganizationDocumentNumber(document.getDocumentHeader().getOrganizationDocumentNumber());
        explicitEntry.setOrganizationReferenceId(organizationReferenceId);
        explicitEntry.setProjectCode(getEntryValue(projectCode, GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode()));
        explicitEntry.setReferenceFinancialDocumentNumber(getEntryValue(referenceNumber, BLANK_SPACE));
        explicitEntry.setReferenceFinancialDocumentTypeCode(getEntryValue(referenceTypeCode, BLANK_SPACE));
        explicitEntry.setReferenceFinancialSystemOriginationCode(getEntryValue(referenceOriginCode, BLANK_SPACE));
        explicitEntry.setSubAccountNumber(getEntryValue(subAccountNumber, GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber()));
        explicitEntry.setFinancialSubObjectCode(getEntryValue(subObjectCode, GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode()));
        explicitEntry.setTransactionEntryOffsetIndicator(false);
        explicitEntry.setTransactionLedgerEntryAmount(amount);
        explicitEntry.setTransactionLedgerEntryDescription(getEntryValue(description, document.getDocumentHeader().getDocumentDescription()));
        explicitEntry.setUniversityFiscalPeriodCode(null); // null here, is assigned during batch or in specific document rule
        // classes
        explicitEntry.setUniversityFiscalYear(document.getPostingYear());

        if (LOG.isDebugEnabled()) {
            LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
        }

        return explicitEntry;
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
    @Override
    public boolean populateOffsetGeneralLedgerPendingEntry(Integer universityFiscalYear, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("populateOffsetGeneralLedgerPendingEntry(Integer, GeneralLedgerPendingEntry, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        boolean success = true;

        // lookup offset object info
        // FIXME! - OffsetDefinitionService should be injected (and probably cache the result)
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(universityFiscalYear, explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        if (ObjectUtils.isNull(offsetDefinition)) {
            success = false;
            GlobalVariables.getMessageMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_OFFSET_DEFINITION, universityFiscalYear.toString(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        }
        else {
            // FIXME! - FlexibleOffsetAccountService should be injected
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
                throw new RuntimeException("offset object code " + offsetEntry.getUniversityFiscalYear() + "-" + offsetEntry.getChartOfAccountsCode() + "-" + offsetEntry.getFinancialObjectCode());
            }
            // FIXME! - inject the sufficient funds service
            Account account = SpringContext.getBean(AccountService.class).getByPrimaryIdWithCaching(offsetEntry.getChartOfAccountsCode(), offsetEntry.getAccountNumber());
            offsetEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(financialObject, account.getAccountSufficientFundsCode()));
        }

        offsetEntry.setFinancialObjectTypeCode(getOffsetFinancialObjectTypeCode(offsetDefinition));
        offsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        offsetEntry.setTransactionEntryOffsetIndicator(true);
        offsetEntry.setTransactionLedgerEntryDescription(KFSConstants.GL_PE_OFFSET_STRING);
        offsetEntry.setFinancialSystemOriginationCode(explicitEntry.getFinancialSystemOriginationCode());

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
    protected void flexOffsetAccountIfNecessary(OffsetAccount flexibleOffsetAccount, GeneralLedgerPendingEntry offsetEntry) {
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
            throw new RuntimeException("flexible offset account " + flexCoa + "-" + flexAccountNumber);
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
     * posted as a side-effect if the given bank has not defined the necessary bank offset relations.
     *
     * @param bank
     * @param depositAmount
     * @param financialDocument
     * @param universityFiscalYear
     * @param sequenceHelper
     * @param bankOffsetEntry
     * @param errorPropertyName
     * @return whether the entry was populated successfully
     */
    @Override
    public boolean populateBankOffsetGeneralLedgerPendingEntry(Bank bank, KualiDecimal depositAmount, GeneralLedgerPostingDocument financialDocument, Integer universityFiscalYear, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry bankOffsetEntry, String errorPropertyName) {
        bankOffsetEntry.setFinancialDocumentTypeCode(dataDictionaryService.getDocumentTypeNameByClass(financialDocument.getClass()));
        bankOffsetEntry.setVersionNumber(1L);
        bankOffsetEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
        Timestamp transactionTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());
        bankOffsetEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        bankOffsetEntry.setTransactionEntryProcessedTs(transactionTimestamp);
        Account cashOffsetAccount = bank.getCashOffsetAccount();

        if (ObjectUtils.isNull(cashOffsetAccount)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_NO_ACCOUNT, new String[] { bank.getBankCode() });
            return false;
        }

        if (!cashOffsetAccount.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_ACCOUNT_CLOSED, new String[] { bank.getBankCode(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber() });
            return false;
        }

        if (cashOffsetAccount.isExpired()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_ACCOUNT_EXPIRED, new String[] { bank.getBankCode(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber() });
            return false;
        }

        bankOffsetEntry.setChartOfAccountsCode(bank.getCashOffsetFinancialChartOfAccountCode());
        bankOffsetEntry.setAccountNumber(bank.getCashOffsetAccountNumber());
        bankOffsetEntry.setFinancialDocumentApprovedCode(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.NO);
        bankOffsetEntry.setTransactionEncumbranceUpdateCode(BLANK_SPACE);
        bankOffsetEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        bankOffsetEntry.setTransactionDebitCreditCode(depositAmount.isPositive() ? GL_DEBIT_CODE : GL_CREDIT_CODE);
        bankOffsetEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
        bankOffsetEntry.setDocumentNumber(financialDocument.getDocumentNumber());

        ObjectCode cashOffsetObject = bank.getCashOffsetObject();
        if (ObjectUtils.isNull(cashOffsetObject)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_NO_OBJECT_CODE, new String[] { bank.getBankCode() });
            return false;
        }

        if (!cashOffsetObject.isFinancialObjectActiveCode()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_OBJECT_CODE, new String[] { bank.getBankCode(), cashOffsetObject.getFinancialObjectCode() });
            return false;
        }

        bankOffsetEntry.setFinancialObjectCode(bank.getCashOffsetObjectCode());
        bankOffsetEntry.setFinancialObjectTypeCode(bank.getCashOffsetObject().getFinancialObjectTypeCode());
        bankOffsetEntry.setOrganizationDocumentNumber(financialDocument.getDocumentHeader().getOrganizationDocumentNumber());
        bankOffsetEntry.setOrganizationReferenceId(null);
        bankOffsetEntry.setProjectCode(KFSConstants.getDashProjectCode());
        bankOffsetEntry.setReferenceFinancialDocumentNumber(null);
        bankOffsetEntry.setReferenceFinancialDocumentTypeCode(null);
        bankOffsetEntry.setReferenceFinancialSystemOriginationCode(null);

        if (StringUtils.isBlank(bank.getCashOffsetSubAccountNumber())) {
            bankOffsetEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        else {
            SubAccount cashOffsetSubAccount = bank.getCashOffsetSubAccount();
            if (ObjectUtils.isNull(cashOffsetSubAccount)) {
                GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_NONEXISTENT_SUB_ACCOUNT, new String[] { bank.getBankCode(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber(), bank.getCashOffsetSubAccountNumber() });
                return false;
            }

            if (!cashOffsetSubAccount.isActive()) {
                GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_SUB_ACCOUNT, new String[] { bank.getBankCode(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber(), bank.getCashOffsetSubAccountNumber() });
                return false;
            }

            bankOffsetEntry.setSubAccountNumber(bank.getCashOffsetSubAccountNumber());
        }

        if (StringUtils.isBlank(bank.getCashOffsetSubObjectCode())) {
            bankOffsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            SubObjectCode cashOffsetSubObject = bank.getCashOffsetSubObject();
            if (ObjectUtils.isNull(cashOffsetSubObject)) {
                GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_NONEXISTENT_SUB_OBJ, new String[] { bank.getBankCode(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber(), cashOffsetObject.getFinancialObjectCode(), bank.getCashOffsetSubObjectCode() });
                return false;
            }

            if (!cashOffsetSubObject.isActive()) {
                GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_SUB_OBJ, new String[] { bank.getBankCode(), cashOffsetAccount.getChartOfAccountsCode(), cashOffsetAccount.getAccountNumber(), cashOffsetObject.getFinancialObjectCode(), bank.getCashOffsetSubObjectCode() });
                return false;
            }

            bankOffsetEntry.setFinancialSubObjectCode(bank.getCashOffsetSubObjectCode());
        }

        bankOffsetEntry.setTransactionEntryOffsetIndicator(true);
        bankOffsetEntry.setTransactionLedgerEntryAmount(depositAmount.abs());
        bankOffsetEntry.setUniversityFiscalPeriodCode(null); // null here, is assigned during batch or in specific document rule
        bankOffsetEntry.setUniversityFiscalYear(universityFiscalYear);
        bankOffsetEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(cashOffsetObject, cashOffsetAccount.getAccountSufficientFundsCode()));

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService#save(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void save(GeneralLedgerPendingEntry generalLedgerPendingEntry) {
        LOG.debug("save() started");
        SpringContext.getBean(BusinessObjectService.class).save(generalLedgerPendingEntry);
    }

    @Override
    public void delete(String documentHeaderId) {
        LOG.debug("delete() started");

        this.generalLedgerPendingEntryDao.delete(documentHeaderId);
    }

    @Override
    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        LOG.debug("deleteByFinancialDocumentApprovedCode() started");

        this.generalLedgerPendingEntryDao.deleteByFinancialDocumentApprovedCode(financialDocumentApprovedCode);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findApprovedPendingLedgerEntries()
     */
    @Override
    public Iterator findApprovedPendingLedgerEntries() {
        LOG.debug("findApprovedPendingLedgerEntries() started");

        return generalLedgerPendingEntryDao.findApprovedPendingLedgerEntries();
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntries(org.kuali.kfs.gl.businessobject.Encumbrance,
     *      boolean)
     */
    @Override
    public Iterator findPendingLedgerEntries(Encumbrance encumbrance, boolean isApproved) {
        LOG.debug("findPendingLedgerEntries() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntries(encumbrance, isApproved);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#hasPendingGeneralLedgerEntry(org.kuali.kfs.coa.businessobject.Account)
     */
    @Override
    public boolean hasPendingGeneralLedgerEntry(Account account) {
        LOG.debug("hasPendingGeneralLedgerEntry() started");

        return generalLedgerPendingEntryDao.countPendingLedgerEntries(account) > 0;
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntries(Balance, boolean, boolean)
     */
    @Override
    public Iterator findPendingLedgerEntries(Balance balance, boolean isApproved, boolean isConsolidated) {
        LOG.debug("findPendingLedgerEntries() started");

        return generalLedgerPendingEntryDao.findPendingLedgerEntries(balance, isApproved, isConsolidated);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForEntry(java.util.Map, boolean)
     */
    @Override
    public Iterator findPendingLedgerEntriesForEntry(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEntry() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        List<String> encumbranceBalanceTypes = getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForEntry(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForEncumbrance(Map, boolean)
     */
    @Override
    public Iterator findPendingLedgerEntriesForEncumbrance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEncumbrance() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        SystemOptions currentYearOptions = optionsService.getCurrentYearOptions();
        List<String> encumbranceBalanceTypes = getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForEncumbrance(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, currentYearOptions, encumbranceBalanceTypes);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForCashBalance(java.util.Map,
     *      boolean)
     */
    @Override
    public Iterator findPendingLedgerEntriesForCashBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForCashBalance() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        List<String> encumbranceBalanceTypes = getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForCashBalance(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForBalance(java.util.Map, boolean)
     */
    @Override
    public Iterator findPendingLedgerEntriesForBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForBalance() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        List<String> encumbranceBalanceTypes = getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForBalance(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    @Override
    public Iterator findPendingLedgerEntriesForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForAccountBalance() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        List<String> encumbranceBalanceTypes = getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return generalLedgerPendingEntryDao.findPendingLedgerEntriesForAccountBalance(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntrySummaryForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    @Override
    public Iterator findPendingLedgerEntrySummaryForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntrySummaryForAccountBalance() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        List<String> encumbranceBalanceTypes = getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return generalLedgerPendingEntryDao.findPendingLedgerEntrySummaryForAccountBalance(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
    }

    @Override
    public Collection findPendingEntries(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingEntries() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        List<String> encumbranceBalanceTypes = getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return generalLedgerPendingEntryDao.findPendingEntries(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
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

    /**
     * Determines if the given GeneralLedgerPendingEntry represents offsets to cash
     *
     * @param generalLedgerPendingEntry the GeneralLedgerPendingEntry to check
     * @return true if the GeneralLedgerPendingEntry represents an offset to cash; false otherwise
     */
    @Override
    public boolean isOffsetToCash(GeneralLedgerPendingEntry generalLedgerPendingEntry) {
        if (generalLedgerPendingEntry.isTransactionEntryOffsetIndicator()) {
            final Chart entryChart = chartService.getByPrimaryId(generalLedgerPendingEntry.getChartOfAccountsCode());
            if (!ObjectUtils.isNull(entryChart)) {
                return (entryChart.getFinancialCashObjectCode().equals(generalLedgerPendingEntry.getFinancialObjectCode()));
            }
        }
        return false;
    }

    /**
     * Adds up the amounts of all cash to offset GeneralLedgerPendingEntry records on the given AccountingDocument
     *
     * @param glPostingDocument the accounting document total the offset to cash amount for
     * @return the offset to cash amount, where debited values have been subtracted and credited values have been added
     */
    @Override
    public KualiDecimal getOffsetToCashAmount(GeneralLedgerPostingDocument glPostingDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (GeneralLedgerPendingEntry glpe : glPostingDocument.getGeneralLedgerPendingEntries()) {
            if (isOffsetToCash(glpe)) {
                if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
                    total = total.subtract(glpe.getTransactionLedgerEntryAmount());
                }
                else if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
                    total = total.add(glpe.getTransactionLedgerEntryAmount());
                }
            }
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService#getEncumbranceBalanceTypes(java.util.Map, java.lang.Integer)
     */
    @Override
    public List<String> getEncumbranceBalanceTypes(Map fieldValues, Integer currentFiscalYear) {

        String fiscalYearFromForm = null;
        if (fieldValues.containsKey(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            fiscalYearFromForm = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }
        boolean includeNullFiscalYearInLookup = null != currentFiscalYear && currentFiscalYear.toString().equals(fiscalYearFromForm);

        // handle encumbrance balance type
        Map<String, Object> localFieldValues = new HashMap();
        localFieldValues.putAll(fieldValues);

        if (includeNullFiscalYearInLookup) {
            localFieldValues.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }

        // parse the fiscal year (it's not a required field on the lookup screens
        String universityFiscalYearStr = (String) localFieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        if (StringUtils.isNotBlank(universityFiscalYearStr)) {
            Integer universityFiscalYear = new Integer(universityFiscalYearStr);
            return balanceTypeService.getEncumbranceBalanceTypes(universityFiscalYear);
        }
        else {
            return balanceTypeService.getCurrentYearEncumbranceBalanceTypes();
        }

    }

    public void setBalanceTypeService(BalanceTypeService balanceTypeService) {
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

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     *
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the persistenceStructureService attribute.
     *
     * @return Returns the persistenceStructureService.
     */
    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     *
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets the universityDateService.
     *
     * @param universityDateService
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
}
