/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.service.KemidFeeService;
import org.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeEndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeProcessingTotalsProcessedDetailTotalLine;
import org.kuali.kfs.module.endow.businessobject.FeeProcessingTotalsProcessedGrandTotalLine;
import org.kuali.kfs.module.endow.businessobject.FeeProcessingTotalsProcessedReportHeader;
import org.kuali.kfs.module.endow.businessobject.FeeProcessingTotalsProcessedSubTotalLine;
import org.kuali.kfs.module.endow.businessobject.FeeProcessingWaivedAndAccruedDetailTotalLine;
import org.kuali.kfs.module.endow.businessobject.FeeProcessingWaivedAndAccruedGrandTotalLine;
import org.kuali.kfs.module.endow.businessobject.FeeProcessingWaivedAndAccruedReportHeader;
import org.kuali.kfs.module.endow.businessobject.FeeProcessingWaivedAndAccruedSubTotalLine;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.FeeTransaction;
import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.dataaccess.KemidFeeDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.document.service.CurrentTaxLotService;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.util.GloabalVariablesExtractHelper;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the ProcessFeeTransactionsService.
 */
@Transactional
public class ProcessFeeTransactionsServiceImpl implements ProcessFeeTransactionsService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessFeeTransactionsServiceImpl.class);

    protected KemidFeeService kemidFeeService;
    protected FeeMethodService feeMethodService;
    protected KEMService kemService;
    protected TransactionArchiveDao transactionArchiveDao;
    protected HoldingHistoryDao holdingHistoryDao;
    protected CurrentTaxLotBalanceDao currentTaxLotBalanceDao;
    protected KemidFeeDao kemidFeeDao;
    protected DocumentService documentService;
    protected KualiRuleService kualiRuleService;
    protected NoteService noteService;
    protected PersonService personService;
    protected ConfigurationService configService;
    protected CurrentTaxLotService currentTaxLotService;
    protected DataDictionaryService dataDictionaryService;
    protected HoldingHistoryService holdingHistoryService;
    protected ParameterService parameterService;

    protected ReportWriterService processFeeTransactionsExceptionReportsWriterService;
    protected ReportWriterService processFeeTransactionsTotalProcessedReportsWriterService;
    protected ReportWriterService processFeeTransactionsWaivedAndAccruedFeesReportsWriterService;

    protected EndowmentExceptionReportHeader processFeeTransactionsExceptionReportHeader;
    protected FeeProcessingTotalsProcessedReportHeader processFeeTransactionsTotalProcessedReportHeader;
    protected FeeProcessingWaivedAndAccruedReportHeader processFeeTransactionsWaivedAndAccruedFeesReportHeader;

    protected EndowmentExceptionReportHeader processFeeTransactionsRowValues;
    protected EndowmentExceptionReportHeader processFeeTransactionsExceptionRowReason;

    protected FeeProcessingWaivedAndAccruedDetailTotalLine feeProcessingWaivedAndAccruedDetailTotalLine;
    protected FeeProcessingWaivedAndAccruedSubTotalLine feeProcessingWaivedAndAccruedSubTotalLine;
    protected FeeProcessingWaivedAndAccruedGrandTotalLine feeProcessingWaivedAndAccruedGrandTotalLine;

    protected FeeProcessingTotalsProcessedDetailTotalLine feeProcessingTotalsProcessedDetailTotalLine;
    protected FeeProcessingTotalsProcessedSubTotalLine feeProcessingTotalsProcessedSubTotalLine;
    protected FeeProcessingTotalsProcessedGrandTotalLine feeProcessingTotalsProcessedGrandTotalLine;

    // the properties to hold count, total amounts and fee etc.
    protected long totalNumberOfRecords = 0;
    protected BigDecimal totalAmountCalculated = BigDecimal.ZERO;
    protected BigDecimal feeToBeCharged = BigDecimal.ZERO;
    protected BigDecimal transactionIncomeAmount = BigDecimal.ZERO;
    protected BigDecimal transacationPrincipalAmount = BigDecimal.ZERO;
    protected BigDecimal totalHoldingUnits = BigDecimal.ZERO;

    // properties to help in writing subtotals and grand totals lines.
    // lines generated
    protected int totalProcessedLinesGeneratedSubTotal = 0;
    protected int totalProcessedLinesGeneratedGrandTotal = 0;

    // income, principal subtotals at the eDoc level
    protected BigDecimal totalProcessedIncomeAmountSubTotalEDoc = BigDecimal.ZERO;
    protected BigDecimal totalProcessedPrincipalAmountSubTotalEDoc = BigDecimal.ZERO;

    // income, principal subtotals at the fee method level
    protected BigDecimal totalProcessedIncomeAmountSubTotal = BigDecimal.ZERO;
    protected BigDecimal totalProcessedPrincipalAmountSubTotal = BigDecimal.ZERO;

    // income, principal subtotals at the grand total level
    protected BigDecimal totalProcessedIncomeAmountGrandTotal = BigDecimal.ZERO;
    protected BigDecimal totalProcessedPrincipalAmountGrandTotal = BigDecimal.ZERO;

    /**
     * Constructs a HoldingHistoryMarketValuesUpdateServiceImpl instance
     */
    public ProcessFeeTransactionsServiceImpl() {
        // report writer headers
        processFeeTransactionsExceptionReportHeader = new EndowmentExceptionReportHeader();
        processFeeTransactionsTotalProcessedReportHeader = new FeeProcessingTotalsProcessedReportHeader();
        processFeeTransactionsWaivedAndAccruedFeesReportHeader = new FeeProcessingWaivedAndAccruedReportHeader();

        processFeeTransactionsRowValues = new EndowmentExceptionReportHeader();
        processFeeTransactionsExceptionRowReason = new EndowmentExceptionReportHeader();

        // waiver and accrual report....
        feeProcessingWaivedAndAccruedDetailTotalLine = new FeeProcessingWaivedAndAccruedDetailTotalLine();
        feeProcessingWaivedAndAccruedSubTotalLine = new FeeProcessingWaivedAndAccruedSubTotalLine();
        feeProcessingWaivedAndAccruedGrandTotalLine = new FeeProcessingWaivedAndAccruedGrandTotalLine();

        // Totals processed report....
        feeProcessingTotalsProcessedDetailTotalLine = new FeeProcessingTotalsProcessedDetailTotalLine();
        feeProcessingTotalsProcessedSubTotalLine = new FeeProcessingTotalsProcessedSubTotalLine();
        feeProcessingTotalsProcessedGrandTotalLine = new FeeProcessingTotalsProcessedGrandTotalLine();
    }

    /**
     * The fee process is intended to provide as much flexibility to the institution as possible when designing the charges to be
     * assessed against a KEMID. The fees can be based on either balances or activity and can be charged, accrued or waived at the
     * KEMID level.
     *
     * @see oorg.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService#processFeeTransactions()\ return boolean true if
     *      successful else false
     */
    @Override
    public boolean processFeeTransactions() {
        LOG.debug("processFeeTransactions() started");

        boolean success = true;

        writeReportHeaders();

        if (!updateKemidFeeWaivedYearToDateAmount()) {
            writeTableRowAndTableReason("Reason: unable to update update Waiver Fee Year To Date column to Zero.");
            return false;
        }

        // Update the fee transactions.
        success &= processUpdateFeeTransactions();

        // generate the waived and accrued report...
        success &= generateWaivedAndAccruedReport();

        return success;
    }

    /**
     * Updates waived fee year to date column to zero in WAIVED_FEE_YTD
     *
     * @return true if updated successfully else return false
     */
    protected boolean updateKemidFeeWaivedYearToDateAmount() {
        LOG.debug("updateKemidFeeWaivedFeeYearToDateToZero() started");

        // 6.2.1 Basic Process - Step 1:
        boolean updated = true;

        Date firstDayAfterFiscalYear = kemService.getFirstDayAfterFiscalYearEndDayAndMonth();
        Date currentDate = kemService.getCurrentDate();
        boolean fiscalYearMonthAndDayParamExists = parameterService.parameterExists(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowParameterKeyConstants.FISCAL_YEAR_END_MONTH_AND_DAY);

        if (!kemidFeeDao.updateKemidFeeWaivedFeeYearToDateToZero(firstDayAfterFiscalYear, currentDate, fiscalYearMonthAndDayParamExists)) {
            writeTableRowAndTableReason("Batch Process Fee Transactions job is aborted.  Unable to update KEMID Year-To-Date Waiver Fee amounts");
            return false;
        }

        LOG.debug("updateKemidFeeWaivedFeeYearToDateToZero() ended.");
        return updated;
    }

    /**
     * Writes the reports headers for totals processed, waived and accrued fee, and exceptions reports.
     */
    protected void writeReportHeaders() {
        // writes the exception report header
        processFeeTransactionsExceptionReportsWriterService.writeNewLines(1);
        processFeeTransactionsExceptionReportsWriterService.writeTableHeader(processFeeTransactionsExceptionReportHeader);

        // writes the Waived and Accrued Fees report header....
        processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeNewLines(1);
        processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableHeader(processFeeTransactionsWaivedAndAccruedFeesReportHeader);

        // writes the Totals Processed report header....
        processFeeTransactionsTotalProcessedReportsWriterService.writeNewLines(1);
        processFeeTransactionsTotalProcessedReportsWriterService.writeTableHeader(processFeeTransactionsTotalProcessedReportHeader);
    }

    /**
     * Processes update Fee Transactions
     */
    protected boolean processUpdateFeeTransactions() {
        LOG.debug("processUpdateFeeTransactions() started");

        boolean success = true;

        java.util.Date currentDate = kemService.getCurrentDate();
        int maxNumberOfTransactionLinesPerDocument = kemService.getMaxNumberOfTransactionLinesPerDocument();

        Collection<FeeMethod> feeMethods = feeMethodService.getFeeMethodsByNextProcessingDate(currentDate);
        for (FeeMethod feeMethod : feeMethods) {
            String feeTypeCode = feeMethod.getFeeTypeCode();

            // 1. IF the END_FEE_MTHD_T: FEE_TYP_CD is equal to T (Transactions)
            if (feeTypeCode.equals(EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_TRANSACTIONS)) {
                processTransactionArchivesCountForTransactionsFeeType(feeMethod);
            }

            // 2. IF the END_FEE_MTHD_T: FEE_TYP_CD is equal to B (Balance)
            if (feeTypeCode.equals(EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_BALANCES)) {
                processBalanceFeeType(feeMethod);
            }

            if (feeTypeCode.equals(EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_TRANSACTIONS) || feeTypeCode.equals(EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_BALANCES)) {
                performCalculationsForKemId(feeMethod);
                success &= generateCashDecreaseDocument(feeMethod, maxNumberOfTransactionLinesPerDocument);
            }
        }

        if (feeMethods.size() > 0) { // REMOVE
            // write out the grand totals line for Totals processed report...
            writeTotalsProcessedGrandTotalsLine();
        }

        LOG.debug("processUpdateFeeTransactions() ended.");

        return success;
    }

    /**
     * Generates the fee waived and fee accrued report
     */
    protected boolean generateWaivedAndAccruedReport() {
        LOG.debug("generateWaivedAndAccruedReport() started");

        boolean success = true;
        KualiDecimal accruedFeeGrandTotal = KualiDecimal.ZERO;
        KualiDecimal waivedFeeGrandTotal = KualiDecimal.ZERO;

        Date currentDate = kemService.getCurrentDate();

        Collection<FeeMethod> feeMethods = feeMethodService.getFeeMethodsByNextProcessingDate(currentDate);

        for (FeeMethod feeMethod : feeMethods) {
            KualiDecimal accruedFeeSubTotal = KualiDecimal.ZERO;
            KualiDecimal waivedFeeSubTotal = KualiDecimal.ZERO;

            Collection<KemidFee> kemidFeeRecords = kemidFeeService.getAllKemidForFeeMethodCode(feeMethod.getCode());

            for (KemidFee kemidFee : kemidFeeRecords) {
                feeProcessingWaivedAndAccruedDetailTotalLine.setTotal(feeMethod.getCode());
                feeProcessingWaivedAndAccruedDetailTotalLine.setKemid(kemidFee.getKemid());
                feeProcessingWaivedAndAccruedDetailTotalLine.setTotalAccruedFees(kemidFee.getTotalAccruedFees());
                feeProcessingWaivedAndAccruedDetailTotalLine.setTotalWaivedFees(kemidFee.getTotalWaivedFees());

                processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableRow(feeProcessingWaivedAndAccruedDetailTotalLine);

                accruedFeeSubTotal = accruedFeeSubTotal.add(kemidFee.getTotalAccruedFees());
                waivedFeeSubTotal = waivedFeeSubTotal.add(kemidFee.getTotalWaivedFees());
            }

            feeProcessingWaivedAndAccruedSubTotalLine.setTotalAccruedFees(accruedFeeSubTotal);
            feeProcessingWaivedAndAccruedSubTotalLine.setTotalWaivedFees(waivedFeeSubTotal);

            if (kemidFeeRecords.size() > 0) {
                processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableRow(feeProcessingWaivedAndAccruedSubTotalLine);
                processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeNewLines(1);
            }
            accruedFeeGrandTotal = accruedFeeGrandTotal.add(accruedFeeSubTotal);
            waivedFeeGrandTotal = waivedFeeGrandTotal.add(waivedFeeSubTotal);
        }

        feeProcessingWaivedAndAccruedGrandTotalLine.setTotalAccruedFees(accruedFeeGrandTotal);
        feeProcessingWaivedAndAccruedGrandTotalLine.setTotalWaivedFees(waivedFeeGrandTotal);
        processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableRow(feeProcessingWaivedAndAccruedGrandTotalLine);

        LOG.debug("generateWaivedAndAccruedReport() ended.");

        return success;
    }

    /**
     * IF the END_FEE_MTHD_T: FEE_TYP_CD is equal to T (Transactions), then the fee will use the transaction records from
     * END_TRAN_ARCHV_T to calculate the fee. IF the END_FEE_MTHD_T: FEE_RT_DEF_CD is equal to C (Count), then the process will
     * total the number of the records that fit the selection criteria (6.2.2.1) where the END_TRAN_ARCHV_T:TRAN_PSTD_DT is greater
     * than the END_KEMID_FEE_TFEE_MTHD_T: FEE_LST_PROC_DT. IF the END_FEE_MTHD_T: FEE_RT_DEF_CD is equal to V (Value), then the
     * process will total the TRAN_INC_CSH_AMT, and/or TRAN_PRIN_CSH_AMT of the records that fit the selection criteria (6.2.2.1)
     * where the END_TRAN_ARCHV_T: TRAN_PSTD_DT is greater than the END_KEMID_FEE_TFEE_MTHD_T: FEE_LST_PROC_DT.
     */
    protected void processTransactionArchivesCountForTransactionsFeeType(FeeMethod feeMethod) {
        String feeMethodCodeForTypeCodes = feeMethod.getCode();
        if (dataDictionaryService.getAttributeForceUppercase(FeeTransaction.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
            feeMethodCodeForTypeCodes = feeMethodCodeForTypeCodes.toUpperCase();
        }

        String feeMethodCodeForEtranCodes = feeMethod.getCode();
        if (dataDictionaryService.getAttributeForceUppercase(FeeEndowmentTransactionCode.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
            feeMethodCodeForEtranCodes = feeMethodCodeForEtranCodes.toUpperCase();
        }

        // case: FEE_RT_DEF_CD = C for count
        if (feeMethod.getFeeRateDefinitionCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_COUNT)) {

            totalNumberOfRecords = transactionArchiveDao.getTransactionArchivesCountForTransactions(feeMethod, feeMethodCodeForTypeCodes, feeMethodCodeForEtranCodes);
            totalAmountCalculated = KEMCalculationRoundingHelper.multiply(feeMethod.getFirstFeeRate(), BigDecimal.valueOf(totalNumberOfRecords), EndowConstants.Scale.SECURITY_INCOME_RATE);
        }

        // case: FEE_RT_DEF_CD = V for value...
        if (feeMethod.getFeeRateDefinitionCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_VALUE)) {
            HashMap<String, BigDecimal> incomeAndPrincipalValues = transactionArchiveDao.getTransactionArchivesIncomeAndPrincipalCashAmountForTransactions(feeMethod, feeMethodCodeForTypeCodes, feeMethodCodeForEtranCodes);
            transactionIncomeAmount = incomeAndPrincipalValues.get(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_CASH_AMOUNT);
            transacationPrincipalAmount = incomeAndPrincipalValues.get(EndowPropertyConstants.TRANSACTION_ARCHIVE_PRINCIPAL_CASH_AMOUNT);
        }
    }

    /**
     * IF fee rate code is equal to C (Count), then process will examine the number of units held and If fee balance type code is
     * equal to AU (Average Units) the process will total the holding units where month end date is greater than last process date
     * divided by number of records selected. If fee balance type code is equal to MU (Month End Units) the process will total
     * holding units for all records where the END_ME_DT_T: ME_DT is the most recent date. If the fee balance type code is equal to
     * CU (Current Units) the process will total the holding units IF rate def code is equal to V (Value), then the process will
     * examine the Market Value of the records. If fee balance type code is equal to AMV (Average Market Value) the process will
     * total holding market value where month end date is greater last process date and divide the result by the number of records
     * selected. If fee balance type code is equal to MMV (Month End market Value) the process will total holding market value for
     * all records where month end date is the most recent date. If fee balance type code is equal to CMV (Current Market Value) the
     * process will total holding market value for all selected records.
     */
    protected void processBalanceFeeType(FeeMethod feeMethod) {
        if (feeMethod.getFeeRateDefinitionCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_COUNT)) {
            // when FEE_BAL_TYP_CD = AU OR CU then total END_HLDG_HIST_T:HLDG_UNITS column
            performFeeRateDefintionForCountCalculations(feeMethod);
        }

        // if FEE_RATE_DEFINITION_CODE = "V"
        if (feeMethod.getFeeRateDefinitionCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_VALUE)) {
            performFeeRateDefintionForValueCalculations(feeMethod);
        }
    }

    /**
     * performs calculations when Fee Rate Definition Code is C
     */
    protected void performFeeRateDefintionForCountCalculations(FeeMethod feeMethod) {
        String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();

        String feeMethodCodeForSecurityClassCodes = feeMethod.getCode();
        if (dataDictionaryService.getAttributeForceUppercase(FeeClassCode.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
            feeMethodCodeForSecurityClassCodes = feeMethodCodeForSecurityClassCodes.toUpperCase();
        }
        String feeMethodCodeForSecurityIds = feeMethod.getCode();
        if (dataDictionaryService.getAttributeForceUppercase(FeeSecurity.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
            feeMethodCodeForSecurityIds = feeMethodCodeForSecurityIds.toUpperCase();
        }
        // when FEE_BAL_TYP_CD = AU OR MU then total END_HLDG_HIST_T:HLDG_UNITS column
        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS) || feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_UNITS)) {
            totalHoldingUnits = holdingHistoryService.getHoldingHistoryTotalHoldingUnits(feeMethod, feeMethodCodeForSecurityClassCodes, feeMethodCodeForSecurityIds);
        }

        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_UNITS)) {
            totalHoldingUnits = currentTaxLotService.getCurrentTaxLotBalanceTotalHoldingUnits(feeMethod);
        }

        totalAmountCalculated = KEMCalculationRoundingHelper.multiply(feeMethod.getFirstFeeRate(), totalHoldingUnits, EndowConstants.Scale.SECURITY_MARKET_VALUE);
    }

    /**
     * performs calculations when Fee Rate Definition Code is V
     */
    protected void performFeeRateDefintionForValueCalculations(FeeMethod feeMethod) {
        String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();

        String feeMethodCodeForSecurityClassCodes = feeMethod.getCode();
        if (dataDictionaryService.getAttributeForceUppercase(FeeClassCode.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
            feeMethodCodeForSecurityClassCodes = feeMethodCodeForSecurityClassCodes.toUpperCase();
        }
        String feeMethodCodeForSecurityIds = feeMethod.getCode();
        if (dataDictionaryService.getAttributeForceUppercase(FeeSecurity.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
            feeMethodCodeForSecurityIds = feeMethodCodeForSecurityIds.toUpperCase();
        }

        // when FEE_BAL_TYP_CD = AMV OR MMV then total END_HLDG_HIST_T:HLDG_UNITS column
        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE) || feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_MARKET_VALUE)) {
            totalHoldingUnits = holdingHistoryService.getHoldingHistoryTotalHoldingMarketValue(feeMethod, feeMethodCodeForSecurityClassCodes, feeMethodCodeForSecurityIds);
        }

        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_MARKET_VALUE)) {
            totalHoldingUnits = currentTaxLotService.getCurrentTaxLotBalanceTotalHoldingMarketValue(feeMethod);
        }

        totalAmountCalculated = KEMCalculationRoundingHelper.multiply(feeMethod.getFirstFeeRate(), totalHoldingUnits, EndowConstants.Scale.SECURITY_MARKET_VALUE);
    }

    /**
     * Performs the calculations to get the fee amount to be charged against the selected kemids
     *
     * @param feeMethod
     */
    protected void performCalculationsForKemId(FeeMethod feeMethod) {
        LOG.debug("performCalculationsForKemId() started");

        Collection<KemidFee> kemidFeeRecords = new ArrayList();

        kemidFeeRecords = kemidFeeService.getAllKemidForFeeMethodCode(feeMethod.getCode());

        for (KemidFee kemidFee : kemidFeeRecords) {
            if (kemidFeeService.chargeFeeToKemid(feeMethod, kemidFee)) {
                performCalculationsAgainstTotalAmountCalculated(feeMethod);
                calculateMinumumFeeAmount(feeMethod);
                if (checkForMinimumThresholdAmount(feeMethod, kemidFee)) {
                    if (kemidFee.isAccrueFees()) {
                        processFeeAccrual(feeMethod, kemidFee);
                    }
                    if (kemidFee.isWaiveFees()) {
                        processFeeWaiver(feeMethod, kemidFee);
                    }
                }
            }
        }

        LOG.debug("performCalculationsForKemId() ended.");
    }

    /**
     * Perform the calculations against the total amount calculated for each KEMID following the process outlined in step three
     * above and calculate the fee amount by adding together the results of the following calculations: 1. Multiply the value of the
     * total amount calculated that is less than or equal to the END_KEMID_FEE_TFEE_MTHD_T: FEE_BRK_1 by END_KEMID_FEE_TFEE_MTHD_T:
     * FEE_RT_1 2. Multiply the value of the total amount calculated that is greater than the END_KEMID_FEE_TFEE_MTHD_T: FEE_BRK_1
     * and less than or equal to the END_KEMID_FEE_TFEE_MTHD_T: FEE_BRK_2 by END_KEMID_FEE_TFEE_MTHD_T: FEE_RT_2 3. Multiply the
     * value of the total amount calculated that is greater than the END_KEMID_FEE_TFEE_MTHD_T: FEE_BRK_2 by
     * END_KEMID_FEE_TFEE_MTHD_T: FEE_RT_3.
     *
     * @param feeMethod
     */
    protected void performCalculationsAgainstTotalAmountCalculated(FeeMethod feeMethod) {
        BigDecimal firstFeeBreakpoint = feeMethod.getFirstFeeBreakpoint().bigDecimalValue();
        BigDecimal secondFeeBreakpoint = feeMethod.getSecondFeeBreakpoint().bigDecimalValue();

        if (totalAmountCalculated.compareTo(firstFeeBreakpoint) <= 0) {
            feeToBeCharged = feeToBeCharged.add(KEMCalculationRoundingHelper.multiply(totalAmountCalculated, feeMethod.getFirstFeeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE));
        }

        if (totalAmountCalculated.compareTo(firstFeeBreakpoint) > 0 && totalAmountCalculated.compareTo(secondFeeBreakpoint) <= 0) {
            feeToBeCharged = feeToBeCharged.add(KEMCalculationRoundingHelper.multiply(totalAmountCalculated, feeMethod.getSecondFeeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE));
        }

        if (totalAmountCalculated.compareTo(secondFeeBreakpoint) > 0) {
            feeToBeCharged = feeToBeCharged.add(KEMCalculationRoundingHelper.multiply(totalAmountCalculated, feeMethod.getThirdFeeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE));
        }
    }

    /**
     * IF the calculated fee is less than the amount in END_FEE_MTHD_T: FEE_MIN_AMT, then the feee to be charged is the minimum fee
     * amount..
     *
     * @param feeMethod
     */
    protected void calculateMinumumFeeAmount(FeeMethod feeMethod) {
        if (totalAmountCalculated.compareTo(feeMethod.getMinimumFeeToCharge().bigDecimalValue()) < 0) {
            feeToBeCharged = feeMethod.getMinimumFeeToCharge().bigDecimalValue();
        }
    }

    /**
     * IF the calculated fee amount is LESS than the value in END_FEE_MTHD_T: FEE_MIN_THRSHLD, then do not charge the fee (no
     * transaction generated. The information should be reported as an exception on the exception report.
     *
     * @param feeMethod
     * @param kemidFee
     * @return true calculated fee amount is greater than 0
     */
    protected boolean checkForMinimumThresholdAmount(FeeMethod feeMethod, KemidFee kemidFee) {
        boolean shouldNotCharge = true;

        if (feeToBeCharged.compareTo(feeMethod.getMinimumFeeThreshold().bigDecimalValue()) < 0) {
            writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Fee is not charged as the fee is less than the minimum threshold");
            return false;
        }

        return shouldNotCharge;
    }

    /**
     * IF the field ACR_FEE is equal to Y (Yes), then add the calculated fee amount to the value in END_KEMID_FEE_MTHD_T:
     * ACRD_FEE_TO_DT.
     *
     * @param feeMethod, kemidFee
     * @return feeAcrrued true if fee amount is added to total accrued fees else return false
     */
    protected boolean processFeeAccrual(FeeMethod feeMethod, KemidFee kemidFee) {
        boolean feeAcrrued = true;

        KualiDecimal accruelFee = new KualiDecimal(feeToBeCharged.toString());
        kemidFee.setTotalAccruedFees(kemidFee.getTotalAccruedFees().add(accruelFee));

        // unable to save. write to exception...
        if (!kemidFeeService.saveKemidFee(kemidFee)) {
            writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Unable to add Calculated Fee to Total Accrued Fees in END_KEMID_FEE_T table.");
            return false;
        }

        return feeAcrrued;
    }

    /**
     * IF the field WAIVE_FEE is equal to Y (Yes), then add the calculated fee amount to the value in END_KEMID_FEE_MTHD_T:
     * WAIVED_FEE_TO_DT and add the calculated fee amount to the value in END_KEMID_FEE_MTHD_T: WAIVED_FEE_YDT
     *
     * @param feeMethod, kemidFee
     * @return feeWaived - true if fee amount is added to total waived fees else return false
     */
    protected boolean processFeeWaiver(FeeMethod feeMethod, KemidFee kemidFee) {
        boolean feeWaived = true;

        KualiDecimal accruelFee = new KualiDecimal(feeToBeCharged.toString());
        kemidFee.setTotalWaivedFeesThisFiscalYear(kemidFee.getTotalWaivedFeesThisFiscalYear().add(accruelFee));
        kemidFee.setTotalWaivedFees(kemidFee.getTotalWaivedFees().add(accruelFee));

        // save kemidFee record.
        if (!kemidFeeService.saveKemidFee(kemidFee)) {
            writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Unable to add Calculated Fee to Total Waived Fees in END_KEMID_FEE_T table.");
            return false;
        }

        return feeWaived;
    }

    /**
     * Generate a CashDecreaseDocument (ECDD) and processes the document by submitting/routing it.
     *
     * @param feeMethod, kemidFee
     */
    protected boolean generateCashDecreaseDocument(FeeMethod feeMethod, int maxNumberOfTransacationLines) {
        LOG.debug("generateCashDecreaseDocument() entered.");

        String feeMethodCode = feeMethod.getCode();
        int lineNumber = 0;

        Collection<KemidFee> kemidFeeRecords = kemidFeeService.getAllKemidForFeeMethodCode(feeMethodCode);

        if (kemidFeeRecords.size() <= 0) {
            return true;
        }

        // initialize CashDecreaseDocument...
        CashDecreaseDocument cashDecreaseDocument = (CashDecreaseDocument) createNewCashDecreaseDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
        if (ObjectUtils.isNull(cashDecreaseDocument)) {
            writeExceptionReportLine(feeMethodCode, null, "Reason: Unable to create a new CashDecreaseDocument.");
            return false;
        }

        // sets document description and source type code and subtype code
        setDocumentOverviewAndDetails(cashDecreaseDocument, feeMethod.getName());

        for (KemidFee kemidFee : kemidFeeRecords) {
            if (lineNumber <= maxNumberOfTransacationLines) {
                if (!createTransactionLines(cashDecreaseDocument, feeMethod, kemidFee, ++lineNumber, maxNumberOfTransacationLines)) {
                    // did not add the line so reduce the number of lines generated....
                    --lineNumber;
                }
            }
            else {
                // reached max transactions. submit and then create a new document....
                if (kualiRuleService.applyRules(new RouteDocumentEvent(cashDecreaseDocument))) {
                    if (submitDocumentForApprovalProcess(cashDecreaseDocument, feeMethod)) {
                        // write the detail line for eDoc level...
                        writeTotalsProcessedDetailTotalsLine(cashDecreaseDocument.getDocumentNumber(), feeMethodCode, lineNumber);

                        // initialize CashDecreaseDocument...
                        cashDecreaseDocument = (CashDecreaseDocument) createNewCashDecreaseDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
                        if (ObjectUtils.isNull(cashDecreaseDocument)) {
                            writeExceptionReportLine(feeMethodCode, kemidFee.getKemid(), "Reason: Unable to create a new CashDecreaseDocument.");
                            return false;
                        }

                        setDocumentOverviewAndDetails(cashDecreaseDocument, feeMethod.getName());
                        lineNumber = 0;
                    }
                    else {
                        // write out exception since can not submit the document....
                        writeExceptionReportLine(feeMethodCode, kemidFee.getKemid(), "Reason: Unable to submit or route the document.");
                        writeExceptionReportLine(feeMethodCode, kemidFee.getKemid(), "Reason: The document did not pass the rule validations during routing.");
                    }
                }
                else {
                    // document rules did not pass the validations. so write exception report....
                    writeExceptionReportLine(feeMethodCode, kemidFee.getKemid(), "Reason: The document did not pass the rule validations during routing.");
                    wrtieExceptionMessagaeFromGlobalVariables(feeMethodCode, kemidFee.getKemid());
                }
            }
        }

        // submit the document as all the transactional lines have been added..
        if (kualiRuleService.applyRules(new RouteDocumentEvent(cashDecreaseDocument))) {
            if (submitDocumentForApprovalProcess(cashDecreaseDocument, feeMethod)) {
                // write the detail line for eDoc level...
                writeTotalsProcessedDetailTotalsLine(cashDecreaseDocument.getDocumentNumber(), feeMethodCode, lineNumber);
            }
            else {
                // write out exception since can not submit the document....
                writeExceptionReportLine(feeMethodCode, null, "Reason: Unable to submit or route the document.");
                wrtieExceptionMessagaeFromGlobalVariables(feeMethodCode, null);
            }

        }
        else {
            // document rules did not pass the validations. so write exception report....
            writeExceptionReportLine(feeMethodCode, null, "Reason: The document did not pass the rule validations during routing.");
            wrtieExceptionMessagaeFromGlobalVariables(feeMethodCode, null);
        }

        writeTotalsProcessedSubTotalsLine(feeMethodCode);
        LOG.debug("generateCashDecreaseDocument() ended.");

        return true;
    }

    protected void writeTotalsProcessedDetailTotalsLine(String documentNumber, String feeMethodCode, int totalLinesGenerated) {
        feeProcessingTotalsProcessedDetailTotalLine.setFeeMethodCode(feeMethodCode);
        feeProcessingTotalsProcessedDetailTotalLine.setDocumentNumber(documentNumber);
        feeProcessingTotalsProcessedDetailTotalLine.setLinesGenerated(totalLinesGenerated);
        feeProcessingTotalsProcessedDetailTotalLine.setTotalIncomeAmount(new KualiDecimal(totalProcessedIncomeAmountSubTotalEDoc.toString()));
        feeProcessingTotalsProcessedDetailTotalLine.setTotalPrincipalAmount(new KualiDecimal(totalProcessedPrincipalAmountSubTotalEDoc.toString()));

        processFeeTransactionsTotalProcessedReportsWriterService.writeTableRow(feeProcessingTotalsProcessedDetailTotalLine);
        // processFeeTransactionsTotalProcessedReportsWriterService.writeNewLines(1);

        // add the edoc subtotals to fee method subtotal...
        totalProcessedLinesGeneratedSubTotal += totalLinesGenerated;
        totalProcessedIncomeAmountSubTotal = totalProcessedIncomeAmountSubTotal.add(totalProcessedIncomeAmountSubTotalEDoc);
        totalProcessedPrincipalAmountSubTotal = totalProcessedPrincipalAmountSubTotal.add(totalProcessedPrincipalAmountSubTotalEDoc);
    }

    protected void writeTotalsProcessedSubTotalsLine(String feeMethodCode) {
        feeProcessingTotalsProcessedSubTotalLine.setDocumentNumber("");
        feeProcessingTotalsProcessedSubTotalLine.setLinesGenerated(totalProcessedLinesGeneratedSubTotal);
        feeProcessingTotalsProcessedSubTotalLine.setTotalIncomeAmount(new KualiDecimal(totalProcessedIncomeAmountSubTotal.toString()));
        feeProcessingTotalsProcessedSubTotalLine.setTotalPrincipalAmount(new KualiDecimal(totalProcessedPrincipalAmountSubTotal.toString()));

        processFeeTransactionsTotalProcessedReportsWriterService.writeTableRow(feeProcessingTotalsProcessedSubTotalLine);
        processFeeTransactionsTotalProcessedReportsWriterService.writeNewLines(1);

        totalProcessedIncomeAmountGrandTotal = totalProcessedIncomeAmountGrandTotal.add(totalProcessedIncomeAmountSubTotal);
        totalProcessedPrincipalAmountGrandTotal = totalProcessedPrincipalAmountGrandTotal.add(totalProcessedPrincipalAmountSubTotal);
        totalProcessedLinesGeneratedGrandTotal += totalProcessedLinesGeneratedSubTotal;
    }

    protected void writeTotalsProcessedGrandTotalsLine() {
        feeProcessingTotalsProcessedGrandTotalLine.setDocumentNumber("");
        feeProcessingTotalsProcessedGrandTotalLine.setLinesGenerated(totalProcessedLinesGeneratedGrandTotal);
        feeProcessingTotalsProcessedGrandTotalLine.setTotalIncomeAmount(new KualiDecimal(totalProcessedIncomeAmountGrandTotal.toString()));
        feeProcessingTotalsProcessedGrandTotalLine.setTotalPrincipalAmount(new KualiDecimal(totalProcessedPrincipalAmountGrandTotal.toString()));

        processFeeTransactionsTotalProcessedReportsWriterService.writeTableRow(feeProcessingTotalsProcessedGrandTotalLine);
    }

    /**
     * Sets document description, source type code to A (automated), and subtype code to C (cash)
     *
     * @param cashDecreaseDocument newly generated document.
     * @param documentDescription fee method description to be used as document description
     */
    protected void setDocumentOverviewAndDetails(CashDecreaseDocument cashDecreaseDocument, String documentDescription) {
        cashDecreaseDocument.getDocumentHeader().setDocumentDescription(documentDescription);
        cashDecreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
        cashDecreaseDocument.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
    }

    /**
     * After the last transaction line allowed in the eDoc (based on the institutional parameter) or the last KEMID fee calculated
     * for the fee method, IF the END_FEE_MTHD_T: FEE_POST_PEND_IND is equal to Y submit the document as a blanket approved 'No
     * Route' document. Otherwise, submit the document for routing and approval.
     *
     * @param cashDecreaseDocument
     * @param feeMethod
     * @return true if successful in submitting or routing the document.
     */
    protected boolean submitDocumentForApprovalProcess(CashDecreaseDocument cashDecreaseDocument, FeeMethod feeMethod) {
        LOG.debug("submitDocumentForApprovalProcess() entered.");

        boolean success = true;

        if (feeMethod.getFeePostPendingIndicator()) {
            cashDecreaseDocument.setNoRouteIndicator(false);
            success = submitCashDecreaseDocument(cashDecreaseDocument, feeMethod.getCode());
        }
        else {
            cashDecreaseDocument.setNoRouteIndicator(true);
            success = routeCashDecreaseDocument(cashDecreaseDocument, feeMethod.getCode());
        }

        LOG.debug("submitDocumentForApprovalProcess() ended.");

        return success;
    }

    /**
     * Gets a new document of the document type from the workflow using document service.
     *
     * @param documentType
     * @return newCashDecreaseDocument if successfully created a new document else return null
     */
    protected CashDecreaseDocument createNewCashDecreaseDocument(String documentType) {
        CashDecreaseDocument newCashDecreaseDocument = null;

        try {
            newCashDecreaseDocument = (CashDecreaseDocument) documentService.getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName(documentType));
        }
        catch (WorkflowException wfe) {
            LOG.info("Failed to initialize CashDecreaseDocument");
            return null;
        }

        return newCashDecreaseDocument;
    }

    /**
     * IF the END_KEMID_FEE_T: PCT_CHRG_FEE_TO_INC is equal to 100%, then generate the transaction line(s) for the eDoc
     *
     * @param cashDecreaseDocument
     * @param feeMethod
     * @param kemidFee
     * @param lineNumber current transaction line number
     * @param maxNumberOfTransacationLines The system parameter specifying the max number of lines
     * @return true if transaction lines created.
     */
    protected boolean createTransactionLines(CashDecreaseDocument cashDecreaseDocument, FeeMethod feeMethod, KemidFee kemidFee, int lineNumber, int maxNumberOfTransacationLines) {
        // logic as in 9.3.b
        if (kemidFee.getPercentOfFeeChargedToIncome().equals(new KualiDecimal("1"))) {
            EndowmentSourceTransactionLine endowmentSourceTransactionLine = createEndowmentSourceTransactionLine(lineNumber, feeMethod, kemidFee, EndowConstants.IncomePrincipalIndicator.INCOME, feeToBeCharged);
            if (addTransactionLineToDocument(cashDecreaseDocument, endowmentSourceTransactionLine, lineNumber, feeMethod.getCode())) {
                totalProcessedIncomeAmountSubTotalEDoc = totalProcessedIncomeAmountSubTotalEDoc.add(feeToBeCharged);
                return true;
            }
            else {
                return false;
            }
        }

        // logic to charge according to logic in 9.3.c
        BigDecimal feeAmountForIncome = KEMCalculationRoundingHelper.multiply(feeToBeCharged, new BigDecimal(kemidFee.getPercentOfFeeChargedToIncome().toString()), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = createEndowmentSourceTransactionLine(lineNumber, feeMethod, kemidFee, EndowConstants.IncomePrincipalIndicator.INCOME, feeAmountForIncome);

        if (addTransactionLineToDocument(cashDecreaseDocument, endowmentSourceTransactionLine, ++lineNumber, feeMethod.getCode())) {
            totalProcessedIncomeAmountSubTotalEDoc = totalProcessedIncomeAmountSubTotalEDoc.add(feeAmountForIncome);
        }
        else {
            return false;
        }

        BigDecimal feeAmountForPrincipal = KEMCalculationRoundingHelper.multiply(feeToBeCharged, new BigDecimal(kemidFee.getPercentOfFeeChargedToPrincipal().toString()), EndowConstants.Scale.SECURITY_MARKET_VALUE);

        if (lineNumber <= maxNumberOfTransacationLines) {
            endowmentSourceTransactionLine = createEndowmentSourceTransactionLine(lineNumber, feeMethod, kemidFee, EndowConstants.IncomePrincipalIndicator.PRINCIPAL, feeAmountForPrincipal);
            if (addTransactionLineToDocument(cashDecreaseDocument, endowmentSourceTransactionLine, ++lineNumber, feeMethod.getCode())) {
                totalProcessedPrincipalAmountSubTotalEDoc = totalProcessedPrincipalAmountSubTotalEDoc.add(feeAmountForPrincipal);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            boolean submitted = submitDocumentForApprovalProcess(cashDecreaseDocument, feeMethod);
            // write sub totals at eDoc leve....
            writeTotalsProcessedDetailTotalsLine(cashDecreaseDocument.getDocumentNumber(), feeMethod.getCode(), lineNumber);

            // initialize CashDecreaseDocument...
            cashDecreaseDocument = (CashDecreaseDocument) createNewCashDecreaseDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
            if (ObjectUtils.isNull(cashDecreaseDocument)) {
                writeExceptionReportLine(feeMethod.getCode(), null, "Reason: Unable to create a new CashDecreaseDocument.");
                return false;
            }

            setDocumentOverviewAndDetails(cashDecreaseDocument, feeMethod.getName());

            lineNumber = 0;
            endowmentSourceTransactionLine = createEndowmentSourceTransactionLine(lineNumber, feeMethod, kemidFee, EndowConstants.IncomePrincipalIndicator.PRINCIPAL, feeAmountForPrincipal);
            return addTransactionLineToDocument(cashDecreaseDocument, endowmentSourceTransactionLine, ++lineNumber, feeMethod.getCode());
        }
    }

    /**
     * Add the new transaction line after applying the validation rules to the line.
     *
     * @param cashDecreaseDocument
     * @param endowmentSourceTransactionLine
     * @param lineNumber
     * @return true if the line passed the business rules and added successfully else false.
     */
    protected boolean addTransactionLineToDocument(CashDecreaseDocument cashDecreaseDocument, EndowmentSourceTransactionLine endowmentSourceTransactionLine, int lineNumber, String feeMethodCode) {
        boolean added = true;

        if (kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, cashDecreaseDocument, endowmentSourceTransactionLine))) {
            cashDecreaseDocument.getSourceTransactionLines().add(endowmentSourceTransactionLine);
            cashDecreaseDocument.setNextSourceLineNumber(lineNumber);
        }
        else {
            LOG.info("CashDecreaseDocument Rules Failed.  The transaction line is not added for Kemid: " + endowmentSourceTransactionLine.getKemid());
            wrtieExceptionMessagaeFromGlobalVariables(feeMethodCode, endowmentSourceTransactionLine.getKemid());
            --lineNumber;

            return false;
        }

        return added;
    }

    /**
     * extracts the error messages in the global variables for this session id and writes as exception report
     */
    protected void wrtieExceptionMessagaeFromGlobalVariables(String feeMethodCode, String kemid) {
        processFeeTransactionsRowValues.setColumnHeading1(feeMethodCode);
        processFeeTransactionsRowValues.setColumnHeading2(kemid);
        processFeeTransactionsRowValues.setColumnHeading3(feeToBeCharged.toString());
        processFeeTransactionsExceptionReportsWriterService.writeTableRow(processFeeTransactionsRowValues);

        // List<String> errorMessages = extractGlobalVariableErrors();
        List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();

        for (String errorMessage : errorMessages) {
            processFeeTransactionsExceptionReportsWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
        }

        processFeeTransactionsExceptionReportsWriterService.writeNewLines(1);
    }

    /**
     * Creates a source transaction line
     *
     * @param lineNumber the current transaction line number
     * @param feeMethod
     * @param kemidFee
     * @param iPIndicator Income or principal indicator for this line
     * @param feeAmount the calculate fee amount for the transaction amount field
     * @return endowmentSourceTransactionLine the new source transaction line
     */
    protected EndowmentSourceTransactionLine createEndowmentSourceTransactionLine(int lineNumber, FeeMethod feeMethod, KemidFee kemidFee, String iPIndicator, BigDecimal feeAmount) {
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setTransactionLineNumber(lineNumber);
        endowmentSourceTransactionLine.setKemid(kemidFee.getChargeFeeToKemid());
        endowmentSourceTransactionLine.setEtranCode(feeMethod.getFeeExpenseETranCode());
        endowmentSourceTransactionLine.setTransactionIPIndicatorCode(iPIndicator);
        endowmentSourceTransactionLine.setTransactionAmount(new KualiDecimal(feeToBeCharged.toString()));

        return endowmentSourceTransactionLine;
    }

    /**
     * submits the document. It sets the no route indicator to true and creates a note and sets its text and adds the note to the
     * document. The document is saved and put into workflow
     *
     * @param cashDecreaseDocument
     * @return true if document submitted else false
     */
    protected boolean submitCashDecreaseDocument(CashDecreaseDocument cashDecreaseDocument, String feeMethodCode) {
        boolean saved = true;

        try {
            documentService.saveDocument(cashDecreaseDocument);
        }
        catch (WorkflowException wfe) {
            LOG.info("CashDecreaseDocument Rules Failed.  The transaction line is not added for Document: " + cashDecreaseDocument.getDocumentNumber());
            wrtieExceptionMessagaeFromGlobalVariables(feeMethodCode, null);
            return false;
        }
        catch (Exception ex) {
            return false;
        }

        return saved;
    }

    /**
     * Routes the document
     *
     * @param cashDecreaseDocument
     * @return true if successful else return false
     */
    protected boolean routeCashDecreaseDocument(CashDecreaseDocument cashDecreaseDocument, String feeMethodCode) {
        boolean routed = true;

        try {
            documentService.routeDocument(cashDecreaseDocument, "Created by Fee Transactions Batch Job and routed.", null);
        }
        catch (WorkflowException wfe) {
            try {
                // write errors messages and clear them befor saving.....
                LOG.info("CashDecreaseDocument Rules Failed.  The transaction line is not added for Document: " + cashDecreaseDocument.getDocumentNumber());
                wrtieExceptionMessagaeFromGlobalVariables(feeMethodCode, null);

                documentService.saveDocument(cashDecreaseDocument);
            }
            catch (WorkflowException wfesave) {
                return false;
            }
            catch (Exception ex) {
                return false;
            }

            return false;
        }

        return routed;
    }

    /**
     * Writes the exception report line after setting fee method code and kemid and the reason
     *
     * @param feeMethodCode
     * @param kemid
     * @param reason the reason written on the reason line.
     */
    protected void writeExceptionReportLine(String feeMethodCode, String kemid, String reason) {
        processFeeTransactionsRowValues.setColumnHeading1(feeMethodCode);
        processFeeTransactionsRowValues.setColumnHeading2(kemid);
        processFeeTransactionsRowValues.setColumnHeading3(feeToBeCharged.toString());
        writeTableRowAndTableReason(reason);
    }

    /**
     * writes out the table row values then writes the reason row and inserts a blank line
     *
     * @param reasonMessage the reason message
     */
    protected void writeTableRowAndTableReason(String reasonMessage) {
        processFeeTransactionsExceptionReportsWriterService.writeTableRow(processFeeTransactionsRowValues);
        setExceptionReportTableRowReason(reasonMessage);
        processFeeTransactionsExceptionReportsWriterService.writeTableRow(processFeeTransactionsExceptionRowReason);
        processFeeTransactionsExceptionReportsWriterService.writeNewLines(1);
    }

    /**
     * sets the exception message with the passed in value.
     *
     * @param reasonForException The reason that will be set in the exception report
     */
    protected void setExceptionReportTableRowReason(String reasonForException) {

        processFeeTransactionsExceptionRowReason.setColumnHeading1("Reason: " + reasonForException);
        processFeeTransactionsExceptionRowReason.setColumnHeading2("");
        processFeeTransactionsExceptionRowReason.setColumnHeading3("");
    }


    /**
     * Gets the processFeeTransactionsExceptionReportsWriterService attribute.
     *
     * @return Returns the processFeeTransactionsExceptionReportsWriterService.
     */
    protected ReportWriterService getProcessFeeTransactionsExceptionReportsWriterService() {
        return processFeeTransactionsExceptionReportsWriterService;
    }

    /**
     * Sets the processFeeTransactionsExceptionReportsWriterService attribute value.
     *
     * @param processFeeTransactionsExceptionReportsWriterService The processFeeTransactionsExceptionReportsWriterService to set.
     */
    public void setProcessFeeTransactionsExceptionReportsWriterService(ReportWriterService processFeeTransactionsExceptionReportsWriterService) {
        this.processFeeTransactionsExceptionReportsWriterService = processFeeTransactionsExceptionReportsWriterService;
    }

    /**
     * Gets the processFeeTransactionsTotalProcessedReportsWriterService attribute.
     *
     * @return Returns the processFeeTransactionsTotalProcessedReportsWriterService.
     */
    public ReportWriterService getProcessFeeTransactionsTotalProcessedReportsWriterService() {
        return processFeeTransactionsTotalProcessedReportsWriterService;
    }

    /**
     * Sets the processFeeTransactionsTotalProcessedReportsWriterService attribute value.
     *
     * @param processFeeTransactionsTotalProcessedReportsWriterService The processFeeTransactionsTotalProcessedReportsWriterService
     *        to set.
     */
    public void setProcessFeeTransactionsTotalProcessedReportsWriterService(ReportWriterService processFeeTransactionsTotalProcessedReportsWriterService) {
        this.processFeeTransactionsTotalProcessedReportsWriterService = processFeeTransactionsTotalProcessedReportsWriterService;
    }

    /**
     * Gets the processFeeTransactionsWaivedAndAccruedFeesReportsWriterService attribute.
     *
     * @return Returns the processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.
     */
    public ReportWriterService getProcessFeeTransactionsWaivedAndAccruedFeesReportsWriterService() {
        return processFeeTransactionsWaivedAndAccruedFeesReportsWriterService;
    }

    /**
     * Sets the processFeeTransactionsWaivedAndAccruedFeesReportsWriterService attribute value.
     *
     * @param processFeeTransactionsWaivedAndAccruedFeesReportsWriterService The
     *        processFeeTransactionsWaivedAndAccruedFeesReportsWriterService to set.
     */
    public void setProcessFeeTransactionsWaivedAndAccruedFeesReportsWriterService(ReportWriterService processFeeTransactionsWaivedAndAccruedFeesReportsWriterService) {
        this.processFeeTransactionsWaivedAndAccruedFeesReportsWriterService = processFeeTransactionsWaivedAndAccruedFeesReportsWriterService;
    }

    /**
     * Gets the holdingHistoryService attribute.
     *
     * @return Returns the holdingHistoryService.
     */
    protected KemidFeeService getKemidFeeService() {
        return kemidFeeService;
    }

    /**
     * Sets the kKemidFeeService attribute value.
     *
     * @param kemidFeeService The kemidFeeService to set.
     */
    public void setKemidFeeService(KemidFeeService kemidFeeService) {
        this.kemidFeeService = kemidFeeService;
    }

    /**
     * Gets the feeMethodService attribute.
     *
     * @return Returns the feeMethodService.
     */
    protected FeeMethodService getFeeMethodService() {
        return feeMethodService;
    }

    /**
     * Sets the feeMethodService attribute value.
     *
     * @param feeMethodService The feeMethodService to set.
     */
    public void setFeeMethodService(FeeMethodService feeMethodService) {
        this.feeMethodService = feeMethodService;
    }

    /**
     * Gets the kemService.
     *
     * @return kemService
     */
    protected KEMService getKemService() {
        return kemService;
    }

    /**
     * Sets the kemService.
     *
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Gets the processFeeTransactionsExceptionReportHeader attribute.
     *
     * @return Returns the processFeeTransactionsExceptionReportHeader.
     */
    public EndowmentExceptionReportHeader getProcessFeeTransactionsExceptionReportHeader() {
        return processFeeTransactionsExceptionReportHeader;
    }

    /**
     * Sets the processFeeTransactionsExceptionReportHeader attribute value.
     *
     * @param processFeeTransactionsExceptionReportHeader The processFeeTransactionsExceptionReportHeader to set.
     */
    public void setProcessFeeTransactionsExceptionReportHeader(EndowmentExceptionReportHeader processFeeTransactionsExceptionReportHeader) {
        this.processFeeTransactionsExceptionReportHeader = processFeeTransactionsExceptionReportHeader;
    }

    /**
     * Gets the processFeeTransactionsTotalProcessedReportHeader attribute.
     *
     * @return Returns the processFeeTransactionsTotalProcessedReportHeader.
     */
    public FeeProcessingTotalsProcessedReportHeader getProcessFeeTransactionsTotalProcessedReportHeader() {
        return processFeeTransactionsTotalProcessedReportHeader;
    }

    /**
     * Sets the processFeeTransactionsTotalProcessedReportHeader attribute value.
     *
     * @param processFeeTransactionsTotalProcessedReportHeader The processFeeTransactionsTotalProcessedReportHeader to set.
     */
    public void setProcessFeeTransactionsTotalProcessedReportHeader(FeeProcessingTotalsProcessedReportHeader processFeeTransactionsTotalProcessedReportHeader) {
        this.processFeeTransactionsTotalProcessedReportHeader = processFeeTransactionsTotalProcessedReportHeader;
    }

    /**
     * Gets the processFeeTransactionsWaivedAndAccruedFeesReportHeader attribute.
     *
     * @return Returns the processFeeTransactionsWaivedAndAccruedFeesReportHeader.
     */
    public FeeProcessingWaivedAndAccruedReportHeader getProcessFeeTransactionsWaivedAndAccruedFeesReportHeader() {
        return processFeeTransactionsWaivedAndAccruedFeesReportHeader;
    }

    /**
     * Sets the processFeeTransactionsWaivedAndAccruedFeesReportHeader attribute value.
     *
     * @param processFeeTransactionsWaivedAndAccruedFeesReportHeader The processFeeTransactionsWaivedAndAccruedFeesReportHeader to
     *        set.
     */
    public void setProcessFeeTransactionsWaivedAndAccruedFeesReportHeader(FeeProcessingWaivedAndAccruedReportHeader processFeeTransactionsWaivedAndAccruedFeesReportHeader) {
        this.processFeeTransactionsWaivedAndAccruedFeesReportHeader = processFeeTransactionsWaivedAndAccruedFeesReportHeader;
    }

    /**
     * Gets the processFeeTransactionsRowValues attribute.
     *
     * @return Returns the processFeeTransactionsRowValues.
     */
    public EndowmentExceptionReportHeader getProcessFeeTransactionsRowValues() {
        return processFeeTransactionsRowValues;
    }

    /**
     * Sets the processFeeTransactionsRowValues attribute value.
     *
     * @param processFeeTransactionsRowValues The processFeeTransactionsRowValues to set.
     */
    public void setProcessFeeTransactionsRowValues(EndowmentExceptionReportHeader processFeeTransactionsRowValues) {
        this.processFeeTransactionsRowValues = processFeeTransactionsRowValues;
    }

    /**
     * Gets the processFeeTransactionsExceptionRowReason attribute.
     *
     * @return Returns the processFeeTransactionsExceptionRowReason.
     */
    public EndowmentExceptionReportHeader getProcessFeeTransactionsExceptionRowReason() {
        return processFeeTransactionsExceptionRowReason;
    }

    /**
     * Sets the processFeeTransactionsExceptionRowReason attribute value.
     *
     * @param processFeeTransactionsExceptionRowReason The processFeeTransactionsExceptionRowReason to set.
     */
    public void setProcessFeeTransactionsExceptionRowReason(EndowmentExceptionReportHeader processFeeTransactionsExceptionRowReason) {
        this.processFeeTransactionsExceptionRowReason = processFeeTransactionsExceptionRowReason;
    }

    /**
     * Gets the transactionArchiveDao attribute.
     *
     * @return Returns the transactionArchiveDao.
     */
    protected TransactionArchiveDao getTransactionArchiveDao() {
        return transactionArchiveDao;
    }

    /**
     * Sets the transactionArchiveDao attribute value.
     *
     * @param transactionArchiveDao The transactionArchiveDao to set.
     */
    public void setTransactionArchiveDao(TransactionArchiveDao transactionArchiveDao) {
        this.transactionArchiveDao = transactionArchiveDao;
    }

    /**
     * Gets the holdingHistoryDao attribute.
     *
     * @return Returns the holdingHistoryDao.
     */
    protected HoldingHistoryDao getHoldingHistoryDao() {
        return holdingHistoryDao;
    }

    /**
     * Sets the holdingHistoryDao attribute value.
     *
     * @param holdingHistoryDao The holdingHistoryDao to set.
     */
    public void setHoldingHistoryDao(HoldingHistoryDao holdingHistoryDao) {
        this.holdingHistoryDao = holdingHistoryDao;
    }

    /**
     * Gets the currentTaxLotBalanceDao attribute.
     *
     * @return Returns the currentTaxLotBalanceDao.
     */
    protected CurrentTaxLotBalanceDao getCurrentTaxLotBalanceDao() {
        return currentTaxLotBalanceDao;
    }

    /**
     * Sets the currentTaxLotBalanceDao attribute value.
     *
     * @param currentTaxLotBalanceDao The currentTaxLotBalanceDao to set.
     */
    public void setCurrentTaxLotBalanceDao(CurrentTaxLotBalanceDao currentTaxLotBalanceDao) {
        this.currentTaxLotBalanceDao = currentTaxLotBalanceDao;
    }

    /**
     * Gets the kemidFeeDao attribute.
     *
     * @return Returns the kemidFeeDao.
     */
    protected KemidFeeDao getKemidFeeDao() {
        return kemidFeeDao;
    }

    /**
     * Sets the kemidFeeDao attribute value.
     *
     * @param kemidFeeDao The kemidFeeDao to set.
     */
    public void setKemidFeeDao(KemidFeeDao kemidFeeDao) {
        this.kemidFeeDao = kemidFeeDao;
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the documentService attribute value.
     */
    protected DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Gets the kualiRuleService attribute.
     *
     * @return Returns the kualiRuleService.
     */
    protected KualiRuleService getKualiRuleService() {
        return kualiRuleService;
    }

    /**
     * Sets the kualiRuleService attribute value.
     *
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Gets the NoteService, lazily initializing if necessary
     *
     * @return the NoteService
     */
    protected synchronized NoteService getNoteService() {
        if (this.noteService == null) {
            this.noteService = SpringContext.getBean(NoteService.class);
        }
        return this.noteService;
    }

    /**
     * Sets the noteService attribute value.
     *
     * @param noteService The noteService to set.
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService getPersonService() {
        if (personService == null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

    /**
     * Gets the feeProcessingWaivedAndAccruedDetailTotalLine attribute.
     *
     * @return Returns the feeProcessingWaivedAndAccruedDetailTotalLine.
     */
    protected FeeProcessingWaivedAndAccruedDetailTotalLine getFeeProcessingWaivedAndAccruedDetailTotalLine() {
        return feeProcessingWaivedAndAccruedDetailTotalLine;
    }

    /**
     * Sets the feeProcessingWaivedAndAccruedDetailTotalLine attribute value.
     *
     * @param feeProcessingWaivedAndAccruedDetailTotalLine The feeProcessingWaivedAndAccruedDetailTotalLine to set.
     */
    public void setFeeProcessingWaivedAndAccruedDetailTotalLine(FeeProcessingWaivedAndAccruedDetailTotalLine feeProcessingWaivedAndAccruedDetailTotalLine) {
        this.feeProcessingWaivedAndAccruedDetailTotalLine = feeProcessingWaivedAndAccruedDetailTotalLine;
    }

    /**
     * Gets the feeProcessingWaivedAndAccruedSubTotalLine attribute.
     *
     * @return Returns the feeProcessingWaivedAndAccruedSubTotalLine.
     */
    protected FeeProcessingWaivedAndAccruedSubTotalLine getFeeProcessingWaivedAndAccruedSubTotalLine() {
        return feeProcessingWaivedAndAccruedSubTotalLine;
    }

    /**
     * Sets the feeProcessingWaivedAndAccruedSubTotalLine attribute value.
     *
     * @param feeProcessingWaivedAndAccruedSubTotalLine The feeProcessingWaivedAndAccruedSubTotalLine to set.
     */
    public void setFeeProcessingWaivedAndAccruedSubTotalLine(FeeProcessingWaivedAndAccruedSubTotalLine feeProcessingWaivedAndAccruedSubTotalLine) {
        this.feeProcessingWaivedAndAccruedSubTotalLine = feeProcessingWaivedAndAccruedSubTotalLine;
    }

    /**
     * Gets the feeProcessingWaivedAndAccruedGrandTotalLine attribute.
     *
     * @return Returns the feeProcessingWaivedAndAccruedGrandTotalLine.
     */
    protected FeeProcessingWaivedAndAccruedGrandTotalLine getFeeProcessingWaivedAndAccruedGrandTotalLine() {
        return feeProcessingWaivedAndAccruedGrandTotalLine;
    }

    /**
     * Sets the feeProcessingWaivedAndAccruedGrandTotalLine attribute value.
     *
     * @param feeProcessingWaivedAndAccruedGrandTotalLine The feeProcessingWaivedAndAccruedGrandTotalLine to set.
     */
    public void setFeeProcessingWaivedAndAccruedGrandTotalLine(FeeProcessingWaivedAndAccruedGrandTotalLine feeProcessingWaivedAndAccruedGrandTotalLine) {
        this.feeProcessingWaivedAndAccruedGrandTotalLine = feeProcessingWaivedAndAccruedGrandTotalLine;
    }

    /**
     * Gets the feeProcessingTotalsProcessedDetailTotalLine attribute.
     *
     * @return Returns the feeProcessingTotalsProcessedDetailTotalLine.
     */
    protected FeeProcessingTotalsProcessedDetailTotalLine getFeeProcessingTotalsProcessedDetailTotalLine() {
        return feeProcessingTotalsProcessedDetailTotalLine;
    }

    /**
     * Sets the feeProcessingTotalsProcessedDetailTotalLine attribute value.
     *
     * @param feeProcessingTotalsProcessedDetailTotalLine The feeProcessingTotalsProcessedDetailTotalLine to set.
     */
    public void setFeeProcessingTotalsProcessedDetailTotalLine(FeeProcessingTotalsProcessedDetailTotalLine feeProcessingTotalsProcessedDetailTotalLine) {
        this.feeProcessingTotalsProcessedDetailTotalLine = feeProcessingTotalsProcessedDetailTotalLine;
    }

    /**
     * Gets the feeProcessingTotalsProcessedSubTotalLine attribute.
     *
     * @return Returns the feeProcessingTotalsProcessedSubTotalLine.
     */
    protected FeeProcessingTotalsProcessedSubTotalLine getFeeProcessingTotalsProcessedSubTotalLine() {
        return feeProcessingTotalsProcessedSubTotalLine;
    }

    /**
     * Sets the feeProcessingTotalsProcessedSubTotalLine attribute value.
     *
     * @param feeProcessingTotalsProcessedSubTotalLine The feeProcessingTotalsProcessedSubTotalLine to set.
     */
    public void setFeeProcessingTotalsProcessedSubTotalLine(FeeProcessingTotalsProcessedSubTotalLine feeProcessingTotalsProcessedSubTotalLine) {
        this.feeProcessingTotalsProcessedSubTotalLine = feeProcessingTotalsProcessedSubTotalLine;
    }

    /**
     * Gets the feeProcessingTotalsProcessedGrandTotalLine attribute.
     *
     * @return Returns the feeProcessingTotalsProcessedGrandTotalLine.
     */
    protected FeeProcessingTotalsProcessedGrandTotalLine getFeeProcessingTotalsProcessedGrandTotalLine() {
        return feeProcessingTotalsProcessedGrandTotalLine;
    }

    /**
     * Sets the feeProcessingTotalsProcessedGrandTotalLine attribute value.
     *
     * @param feeProcessingTotalsProcessedGrandTotalLine The feeProcessingTotalsProcessedGrandTotalLine to set.
     */
    public void setFeeProcessingTotalsProcessedGrandTotalLine(FeeProcessingTotalsProcessedGrandTotalLine feeProcessingTotalsProcessedGrandTotalLine) {
        this.feeProcessingTotalsProcessedGrandTotalLine = feeProcessingTotalsProcessedGrandTotalLine;
    }

    /**
     * Gets the configService attribute.
     *
     * @return Returns the configService.
     */
    protected ConfigurationService getConfigService() {
        return configService;
    }

    /**
     * Sets the configService.
     *
     * @param configService
     */
    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    public void setCurrentTaxLotService(CurrentTaxLotService currentTaxLotService) {
        this.currentTaxLotService = currentTaxLotService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the holdingHistoryService.
     *
     * @param holdingHistoryService
     */
    public void setHoldingHistoryService(HoldingHistoryService holdingHistoryService) {
        this.holdingHistoryService = holdingHistoryService;
    }

    /**
     * Sets the parameterService.
     *
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
