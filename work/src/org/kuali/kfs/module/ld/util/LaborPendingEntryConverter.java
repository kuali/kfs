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
package org.kuali.module.labor.util;

import java.math.BigDecimal;
import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.HomeOriginationService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.document.YearEndDocument;
import org.kuali.module.financial.document.YearEndDocumentUtil;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.service.LaborBenefitsCalculationService;

/**
 * This class provides a set of facilities that can conver the accounting document and its accounting lines into labor pending
 * entries
 */
public class LaborPendingEntryConverter {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborPendingEntryConverter.class);

    /**
     * convert the given document and accounting line into the expense pending entries
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @return a set of expense pending entries
     */
    public static LaborLedgerPendingEntry getExpensePendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document, accountingLine);

        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        pendingEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));

        // year end document should post to previous fiscal year and final period
        if (document instanceof YearEndDocument) {
            pendingEntry.setUniversityFiscalYear(YearEndDocumentUtil.getPreviousFiscalYear());
            pendingEntry.setUniversityFiscalPeriodCode(YearEndDocumentUtil.getFINAL_ACCOUNTING_PERIOD());
        }

        return pendingEntry;
    }

    /**
     * convert the given document and accounting line into the expense pending entries for effort reporting
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @return a set of expense pending entries for effort reporting
     */
    public static LaborLedgerPendingEntry getExpenseA21PendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LaborLedgerPendingEntry pendingEntry = getExpensePendingEntry(document, accountingLine, sequenceHelper);

        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_A21);
        String debitCreditCode = DebitCreditUtil.getReverseDebitCreditCode(pendingEntry.getTransactionDebitCreditCode());
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);

        return pendingEntry;
    }

    /**
     * convert the given document and accounting line into the expense reversal pending entries for effort reporting
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @return a set of expense reversal pending entries for effort reporting
     */
    public static LaborLedgerPendingEntry getExpenseA21ReversalPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LaborLedgerPendingEntry pendingEntry = getExpenseA21PendingEntry(document, accountingLine, sequenceHelper);

        pendingEntry.setUniversityFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        pendingEntry.setUniversityFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());

        String debitCreditCode = DebitCreditUtil.getReverseDebitCreditCode(pendingEntry.getTransactionDebitCreditCode());
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);

        return pendingEntry;
    }

    /**
     * convert the given document and accounting line into the benefit pending entries
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @param benefitAmount the given benefit amount
     * @param fringeBenefitObjectCode the given finge benefit object code
     * @return a set of benefit pending entries
     */
    public static LaborLedgerPendingEntry getBenefitPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document, accountingLine);

        // if account doesn't accept fringe charges, use reports to account
        if (!accountingLine.getAccount().isAccountsFringesBnftIndicator()) {
            pendingEntry.setChartOfAccountsCode(accountingLine.getAccount().getReportsToChartOfAccountsCode());
            pendingEntry.setAccountNumber(accountingLine.getAccount().getReportsToAccountNumber());
        }

        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        pendingEntry.setFinancialObjectCode(pickValue(fringeBenefitObjectCode, KFSConstants.getDashFinancialObjectCode()));

        ObjectCode fringeObjectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(accountingLine.getPayrollEndDateFiscalYear(), accountingLine.getChartOfAccountsCode(), fringeBenefitObjectCode);
        pendingEntry.setFinancialObjectTypeCode(fringeObjectCode.getFinancialObjectTypeCode());

        pendingEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        pendingEntry.setTransactionLedgerEntryAmount(benefitAmount.abs());
        pendingEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
        pendingEntry.setEmplid(LaborConstants.getDashEmplId());
        pendingEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));

        // year end document should post to previous fiscal year and final period
        if (document instanceof YearEndDocument) {
            pendingEntry.setUniversityFiscalYear(YearEndDocumentUtil.getPreviousFiscalYear());
            pendingEntry.setUniversityFiscalPeriodCode(YearEndDocumentUtil.getFINAL_ACCOUNTING_PERIOD());
        }

        return pendingEntry;
    }

    /**
     * convert the given document and accounting line into the benefit pending entry for effort reporting
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @param benefitAmount the given benefit amount
     * @param fringeBenefitObjectCode the given finge benefit object code
     * @return a set of benefit pending entries for effort reporting
     */
    public static LaborLedgerPendingEntry getBenefitA21PendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        LaborLedgerPendingEntry pendingEntry = getBenefitPendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);

        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_A21);
        String debitCreditCode = DebitCreditUtil.getReverseDebitCreditCode(pendingEntry.getTransactionDebitCreditCode());
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);

        return pendingEntry;
    }

    /**
     * convert the given document and accounting line into the benefit reversal pending entries for effort reporting
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @param benefitAmount the given benefit amount
     * @param fringeBenefitObjectCode the given finge benefit object code
     * @return a set of benefit reversal pending entries for effort reporting
     */
    public static LaborLedgerPendingEntry getBenefitA21ReversalPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        LaborLedgerPendingEntry pendingEntry = getBenefitA21PendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);

        pendingEntry.setUniversityFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        pendingEntry.setUniversityFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());

        String debitCreditCode = DebitCreditUtil.getReverseDebitCreditCode(pendingEntry.getTransactionDebitCreditCode());
        pendingEntry.setTransactionDebitCreditCode(debitCreditCode);

        return pendingEntry;
    }

    /**
     * convert the given document into benefit clearing pending entries with the given account, chart, amount and benefit type
     * 
     * @param document the given accounting document
     * @param sequenceHelper the given squence helper
     * @param accountNumber the given account number that the benefit clearing amount can be charged
     * @param chartOfAccountsCode the given chart of accounts code that the benefit clearing amount can be charged
     * @param benefitTypeCode the given benefit type code
     * @param clearingAmount the benefit clearing amount
     * @return a set of benefit clearing pending entries
     */
    public static LaborLedgerPendingEntry getBenefitClearingPendingEntry(LaborLedgerPostingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String accountNumber, String chartOfAccountsCode, String benefitTypeCode, KualiDecimal clearingAmount) {
        LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document);

        pendingEntry.setChartOfAccountsCode(chartOfAccountsCode);
        pendingEntry.setAccountNumber(accountNumber);
        pendingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);

        Integer fiscalYear = SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getUniversityFiscalYear();
        BenefitsCalculation benefitsCalculation = SpringContext.getBean(LaborBenefitsCalculationService.class).getBenefitsCalculation(fiscalYear, chartOfAccountsCode, benefitTypeCode);
        String objectCode = benefitsCalculation.getPositionFringeBenefitObjectCode();
        pendingEntry.setFinancialObjectCode(objectCode);

        ObjectCode oc = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(fiscalYear, chartOfAccountsCode, objectCode);
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

        // year end document should post to previous fiscal year and final period
        if (document instanceof YearEndDocument) {
            pendingEntry.setUniversityFiscalYear(YearEndDocumentUtil.getPreviousFiscalYear());
            pendingEntry.setUniversityFiscalPeriodCode(YearEndDocumentUtil.getFINAL_ACCOUNTING_PERIOD());
        }

        return pendingEntry;
    }

    /**
     * contruct a LaborLedgerPendingEntry object based on the information in the given document and accounting line. The object can
     * be used as a template
     * 
     * @param document the given document
     * @param accountingLine the given accounting line
     * @return a LaborLedgerPendingEntry object based on the information in the given document and accounting line
     */
    public static LaborLedgerPendingEntry getDefaultPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine) {
        LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document);

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
     * contruct a LaborLedgerPendingEntry object based on the information in the given document. The object can be used as a
     * template
     * 
     * @param document the given document
     * @return a LaborLedgerPendingEntry object based on the information in the given document
     */
    public static LaborLedgerPendingEntry getDefaultPendingEntry(LaborLedgerPostingDocument document) {
        LaborLedgerPendingEntry pendingEntry = getSimpleDefaultPendingEntry();
        DocumentHeader documentHeader = document.getDocumentHeader();

        String documentTypeCode = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(document.getClass());
        pendingEntry.setFinancialDocumentTypeCode(documentTypeCode);

        pendingEntry.setDocumentNumber(documentHeader.getDocumentNumber());
        pendingEntry.setTransactionLedgerEntryDescription(documentHeader.getFinancialDocumentDescription());
        pendingEntry.setOrganizationDocumentNumber(documentHeader.getOrganizationDocumentNumber());

        return pendingEntry;
    }

    /**
     * contruct a LaborLedgerPendingEntry object based on the information in the given document and accounting line. The object can
     * be used as a template
     * 
     * @param document the given document
     * @param accountingLine the given accounting line
     * @return a LaborLedgerPendingEntry object based on the information in the given document and accounting line
     */
    public static LaborLedgerPendingEntry getSimpleDefaultPendingEntry() {
        LaborLedgerPendingEntry pendingEntry = new LaborLedgerPendingEntry();

        pendingEntry.setUniversityFiscalYear(null);
        pendingEntry.setUniversityFiscalPeriodCode(null);

        String originationCode = SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode();
        pendingEntry.setFinancialSystemOriginationCode(originationCode);

        Date transactionDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        pendingEntry.setTransactionDate(transactionDate);

        pendingEntry.setFinancialDocumentReversalDate(null);
        pendingEntry.setReferenceFinancialSystemOriginationCode(null);
        pendingEntry.setReferenceFinancialDocumentNumber(null);
        pendingEntry.setReferenceFinancialDocumentTypeCode(null);

        return pendingEntry;
    }

    /**
     * Pick one from target and backup values based on the availabilty of target value
     * 
     * @param targetValue the given target value
     * @param backupValue the backup value of the target value
     * @return target value if it is not null; otherwise, return its backup
     */
    private static String pickValue(String targetValue, String backupValue) {
        return StringUtils.isNotBlank(targetValue) ? targetValue : backupValue;
    }

    /**
     * This method gets the next sequence number and increments.
     * 
     * @param sequenceHelper the given squence helper
     * @return the next sequence number and increments
     */
    private static Integer getNextSequenceNumber(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        Integer nextSequenceNumber = sequenceHelper.getSequenceCounter();
        sequenceHelper.increment();

        return nextSequenceNumber;
    }
}
