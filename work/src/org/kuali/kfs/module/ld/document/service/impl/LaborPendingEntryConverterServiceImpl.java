/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document.service.impl;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.YearEndDocument;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.BenefitsCalculation;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.util.DebitCreditUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;

/**
 * Default implementation of the LaborPendingEntryConverterService
 */
public class LaborPendingEntryConverterServiceImpl implements LaborPendingEntryConverterService {
    protected HomeOriginationService homeOriginationService;
    protected LaborBenefitsCalculationService laborBenefitsCalculationService;
    protected OptionsService optionsService;
    protected ObjectCodeService objectCodeService;
    protected DataDictionaryService dataDictionaryService;
    protected DateTimeService dateTimeService;
    protected YearEndPendingEntryService yearEndPendingEntryService;

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getBenefitA21PendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument, org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.rice.core.api.util.type.KualiDecimal, java.lang.String)
     */
    public LaborLedgerPendingEntry getBenefitA21PendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        LaborLedgerPendingEntry pendingEntry = getBenefitPendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);

        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_A21);
        String debitCreditCode = DebitCreditUtil.getReverseDebitCreditCode(pendingEntry.getTransactionDebitCreditCode());
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getBenefitA21ReversalPendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument, org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.rice.core.api.util.type.KualiDecimal, java.lang.String)
     */
    public LaborLedgerPendingEntry getBenefitA21ReversalPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        LaborLedgerPendingEntry pendingEntry = getBenefitA21PendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);

        pendingEntry.setUniversityFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        pendingEntry.setUniversityFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());

        String debitCreditCode = DebitCreditUtil.getReverseDebitCreditCode(pendingEntry.getTransactionDebitCreditCode());
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getBenefitClearingPendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public LaborLedgerPendingEntry getBenefitClearingPendingEntry(LaborLedgerPostingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String accountNumber, String chartOfAccountsCode, String benefitTypeCode, KualiDecimal clearingAmount) {
        LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document);

        pendingEntry.setChartOfAccountsCode(chartOfAccountsCode);
        pendingEntry.setAccountNumber(accountNumber);
        pendingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);

        Integer fiscalYear = getOptionsService().getCurrentYearOptions().getUniversityFiscalYear();
        BenefitsCalculation benefitsCalculation = getLaborBenefitsCalculationService().getBenefitsCalculation(fiscalYear, chartOfAccountsCode, benefitTypeCode);
        String objectCode = benefitsCalculation.getPositionFringeBenefitObjectCode();
        pendingEntry.setFinancialObjectCode(objectCode);

        ObjectCode oc = getObjectCodeService().getByPrimaryId(fiscalYear, chartOfAccountsCode, objectCode);
        pendingEntry.setFinancialObjectTypeCode(oc.getFinancialObjectTypeCode());

        pendingEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        pendingEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));

        String debitCreditCode = DebitCreditUtil.getDebitCreditCode(clearingAmount, false);
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);
        pendingEntry.setTransactionLedgerEntryAmount(clearingAmount.abs());

        pendingEntry.setProjectCode(KFSConstants.getDashProjectCode());
        pendingEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
        pendingEntry.setEmplid(LaborConstants.getDashEmplId());
        pendingEntry.setTransactionTotalHours(null);

        overrideEntryForYearEndIfNecessary(document, pendingEntry);

        return pendingEntry;
    }
    
    /**
     * Updates the given LLPE for year end documents
     * @param document the document to check if it is YearEnd
     * @param pendingEntry the pending entry to update
     */
    protected void overrideEntryForYearEndIfNecessary(LaborLedgerPostingDocument document, LaborLedgerPendingEntry pendingEntry) {
        // year end document should post to previous fiscal year and final period
        if (document instanceof YearEndDocument) {
            pendingEntry.setUniversityFiscalYear(getYearEndPendingEntryService().getPreviousFiscalYear());
            pendingEntry.setUniversityFiscalPeriodCode(getYearEndPendingEntryService().getFinalAccountingPeriod());
        }
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getBenefitPendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument, org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.rice.core.api.util.type.KualiDecimal, java.lang.String)
     */
    public LaborLedgerPendingEntry getBenefitPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document, accountingLine);

        // if account doesn't accept fringe charges, use reports to account
        if (!accountingLine.getAccount().isAccountsFringesBnftIndicator()) {
            pendingEntry.setChartOfAccountsCode(accountingLine.getAccount().getReportsToChartOfAccountsCode());
            pendingEntry.setAccountNumber(accountingLine.getAccount().getReportsToAccountNumber());
        }

        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        pendingEntry.setFinancialObjectCode(pickValue(fringeBenefitObjectCode, KFSConstants.getDashFinancialObjectCode()));

        ObjectCode fringeObjectCode = getObjectCodeService().getByPrimaryId(accountingLine.getPayrollEndDateFiscalYear(), accountingLine.getChartOfAccountsCode(), fringeBenefitObjectCode);
        pendingEntry.setFinancialObjectTypeCode(fringeObjectCode.getFinancialObjectTypeCode());

        pendingEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        pendingEntry.setTransactionLedgerEntryAmount(benefitAmount.abs());
        pendingEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
        pendingEntry.setEmplid(LaborConstants.getDashEmplId());
        pendingEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));

        // year end document should post to previous fiscal year and final period
        overrideEntryForYearEndIfNecessary(document, pendingEntry);

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getDefaultPendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument, org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine)
     */
    public LaborLedgerPendingEntry getDefaultPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine) {
        LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document);

        pendingEntry.setUniversityFiscalYear(accountingLine.getPostingYear());
        
        pendingEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        pendingEntry.setAccountNumber(accountingLine.getAccountNumber());
        pendingEntry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());

        String subAccountNumber = pickValue(accountingLine.getSubAccountNumber(), KFSConstants.getDashSubAccountNumber());
        pendingEntry.setSubAccountNumber(subAccountNumber);

        String subObjectCode = pickValue(accountingLine.getFinancialSubObjectCode(), KFSConstants.getDashFinancialSubObjectCode());
        pendingEntry.setFinancialSubObjectCode(subObjectCode);

        String projectCode = pickValue(accountingLine.getProjectCode(), KFSConstants.getDashProjectCode());
        pendingEntry.setProjectCode(projectCode);

        accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
        String objectTypeCode = accountingLine.getObjectCode().getFinancialObjectTypeCode();
        pendingEntry.setFinancialObjectTypeCode(objectTypeCode);

        KualiDecimal transactionAmount = accountingLine.getAmount();
        String debitCreditCode = DebitCreditUtil.getDebitCreditCodeForExpenseDocument(accountingLine);
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);
        pendingEntry.setTransactionLedgerEntryAmount(transactionAmount.abs());

        pendingEntry.setPositionNumber(accountingLine.getPositionNumber());
        pendingEntry.setEmplid(accountingLine.getEmplid());
        pendingEntry.setPayrollEndDateFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        pendingEntry.setPayrollEndDateFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());
        pendingEntry.setTransactionTotalHours(accountingLine.getPayrollTotalHours());
        pendingEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getDefaultPendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument)
     */
    public LaborLedgerPendingEntry getDefaultPendingEntry(LaborLedgerPostingDocument document) {
        LaborLedgerPendingEntry pendingEntry = getSimpleDefaultPendingEntry();
        DocumentHeader documentHeader = document.getDocumentHeader();

        String documentTypeCode = getDataDictionaryService().getDocumentTypeNameByClass(document.getClass());
        pendingEntry.setFinancialDocumentTypeCode(documentTypeCode);

        pendingEntry.setDocumentNumber(documentHeader.getDocumentNumber());
        pendingEntry.setTransactionLedgerEntryDescription(documentHeader.getDocumentDescription());
        pendingEntry.setOrganizationDocumentNumber(documentHeader.getOrganizationDocumentNumber());

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getExpenseA21PendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument, org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public LaborLedgerPendingEntry getExpenseA21PendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LaborLedgerPendingEntry pendingEntry = getExpensePendingEntry(document, accountingLine, sequenceHelper);

        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_A21);
        String debitCreditCode = DebitCreditUtil.getReverseDebitCreditCode(pendingEntry.getTransactionDebitCreditCode());
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getExpenseA21ReversalPendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument, org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public LaborLedgerPendingEntry getExpenseA21ReversalPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LaborLedgerPendingEntry pendingEntry = getExpenseA21PendingEntry(document, accountingLine, sequenceHelper);

        pendingEntry.setUniversityFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        pendingEntry.setUniversityFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());

        String debitCreditCode = DebitCreditUtil.getReverseDebitCreditCode(pendingEntry.getTransactionDebitCreditCode());
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getExpensePendingEntry(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument, org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public LaborLedgerPendingEntry getExpensePendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document, accountingLine);

        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        pendingEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));

        // year end document should post to previous fiscal year and final period
        overrideEntryForYearEndIfNecessary(document, pendingEntry);

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService#getSimpleDefaultPendingEntry()
     */
    public LaborLedgerPendingEntry getSimpleDefaultPendingEntry() {
        LaborLedgerPendingEntry pendingEntry = new LaborLedgerPendingEntry();

        pendingEntry.setUniversityFiscalYear(null);
        pendingEntry.setUniversityFiscalPeriodCode(null);

        String originationCode = getHomeOriginationService().getHomeOrigination().getFinSystemHomeOriginationCode();
        pendingEntry.setFinancialSystemOriginationCode(originationCode);

        Date transactionDate = getDateTimeService().getCurrentSqlDate();
        pendingEntry.setTransactionDate(transactionDate);

        pendingEntry.setFinancialDocumentReversalDate(null);
        pendingEntry.setReferenceFinancialSystemOriginationCode(null);
        pendingEntry.setReferenceFinancialDocumentNumber(null);
        pendingEntry.setReferenceFinancialDocumentTypeCode(null);

        return pendingEntry;
    }
    
    /**
     * Pick one from target and backup values based on the availability of target value
     * 
     * @param targetValue the given target value
     * @param backupValue the backup value of the target value
     * @return target value if it is not null; otherwise, return its backup
     */
    protected String pickValue(String targetValue, String backupValue) {
        return StringUtils.isNotBlank(targetValue) ? targetValue : backupValue;
    }

    /**
     * This method gets the next sequence number and increments.
     * 
     * @param sequenceHelper the given sequence helper
     * @return the next sequence number and increments
     */
    protected Integer getNextSequenceNumber(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        Integer nextSequenceNumber = sequenceHelper.getSequenceCounter();
        sequenceHelper.increment();

        return nextSequenceNumber;
    }

    public HomeOriginationService getHomeOriginationService() {
        return homeOriginationService;
    }

    public void setHomeOriginationService(HomeOriginationService homeOriginationService) {
        this.homeOriginationService = homeOriginationService;
    }

    public LaborBenefitsCalculationService getLaborBenefitsCalculationService() {
        return laborBenefitsCalculationService;
    }

    public void setLaborBenefitsCalculationService(LaborBenefitsCalculationService laborBenefitsCalculationService) {
        this.laborBenefitsCalculationService = laborBenefitsCalculationService;
    }

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public YearEndPendingEntryService getYearEndPendingEntryService() {
        return yearEndPendingEntryService;
    }

    public void setYearEndPendingEntryService(YearEndPendingEntryService yearEndPendingEntryService) {
        this.yearEndPendingEntryService = yearEndPendingEntryService;
    }

}
