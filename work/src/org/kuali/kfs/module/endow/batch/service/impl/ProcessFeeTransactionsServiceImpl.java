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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.service.KemidFeeService;
import org.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;

//import org.kuali.kfs.module.endow.businessobject.FeeProcessingWaivedAndAccruedDetailTotalLine;
//import org.kuali.kfs.module.endow.businessobject.FeeProcessingWaivedAndAccruedGrandTotalLine;
//import org.kuali.kfs.module.endow.businessobject.FeeProcessingWaivedAndAccruedSubTotalLine;

import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.dataaccess.KemidFeeDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.TransactionArchiveService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the ProcessFeeTransactionsService.
 */
@Transactional
public class ProcessFeeTransactionsServiceImpl implements ProcessFeeTransactionsService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessFeeTransactionsServiceImpl.class);
    
    private KemidFeeService kemidFeeService;
    private FeeMethodService feeMethodService;
    protected KEMService kemService;
    protected TransactionArchiveService transactionArchiveService;
    protected TransactionArchiveDao transactionArchiveDao;
    protected HoldingHistoryDao holdingHistoryDao;
    protected CurrentTaxLotBalanceDao currentTaxLotBalanceDao;
    protected KemidFeeDao kemidFeeDao;
    protected DocumentService documentService;
    protected KualiRuleService kualiRuleService;
    protected NoteService noteService;
    protected PersonService personService;
    
    private ReportWriterService processFeeTransactionsExceptionReportsWriterService;
    private ReportWriterService processFeeTransactionsTotalProcessedReportsWriterService;
    private ReportWriterService processFeeTransactionsWaivedAndAccruedFeesReportsWriterService;
    
    private EndowmentExceptionReportHeader processFeeTransactionsExceptionReportHeader;
    private EndowmentExceptionReportHeader processFeeTransactionsTotalProcessedReportHeader;
    private EndowmentExceptionReportHeader processFeeTransactionsWaivedAndAccruedFeesReportHeader;    
    
    private EndowmentExceptionReportHeader processFeeTransactionsRowValues;
    private EndowmentExceptionReportHeader processFeeTransactionsExceptionRowReason;    
    
  //  private FeeProcessingWaivedAndAccruedDetailTotalLine feeProcessingWaivedAndAccruedDetailTotalLine;
 //   private FeeProcessingWaivedAndAccruedSubTotalLine feeProcessingWaivedAndAccruedSubTotalLine;
 //   private FeeProcessingWaivedAndAccruedGrandTotalLine feeProcessingWaivedAndAccruedGrandTotalLine;
    
    
    //the properties to hold count, total amounts and fee etc.
    long totalNumberOfRecords = 0;
    BigDecimal totalAmountCalculated = new BigDecimal("0");
    BigDecimal feeToBeCharged = new BigDecimal("0");    
    BigDecimal transactionIncomeAmount = new BigDecimal("0");
    BigDecimal transacationPrincipalAmount = new BigDecimal("0");
    BigDecimal totalHoldingUnits = new BigDecimal("0");
    
    /**
     * Constructs a HoldingHistoryMarketValuesUpdateServiceImpl instance
     */
    public ProcessFeeTransactionsServiceImpl() {
        processFeeTransactionsExceptionReportHeader = new EndowmentExceptionReportHeader();
        processFeeTransactionsTotalProcessedReportHeader = new EndowmentExceptionReportHeader();
        processFeeTransactionsWaivedAndAccruedFeesReportHeader = new EndowmentExceptionReportHeader();
        
        processFeeTransactionsRowValues = new EndowmentExceptionReportHeader();
        processFeeTransactionsExceptionRowReason = new EndowmentExceptionReportHeader();  
        
        //waiver and accrual report....
      //  feeProcessingWaivedAndAccruedDetailTotalLine = new FeeProcessingWaivedAndAccruedDetailTotalLine();
     //   feeProcessingWaivedAndAccruedSubTotalLine = new FeeProcessingWaivedAndAccruedSubTotalLine();
    //    feeProcessingWaivedAndAccruedGrandTotalLine = new FeeProcessingWaivedAndAccruedGrandTotalLine();
        
    }

    /**
     * The fee process is intended to provide as much flexibility to the institution as possible when 
     * designing the charges to be assessed against a KEMID.  The fees can be based on either balances 
     * or activity and can be charged, accrued or waived at the KEMID level.
     * @see oorg.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService#processFeeTransactions()\
     * return boolean true if successful else false
     */
    public boolean processFeeTransactions() {
        boolean success = true;
        
        LOG.debug("processFeeTransactions() started");
        
        writeReportHeaders();
        
        if (!updateKemidFeeWaivedYearToDateAmount()) {
            return false;
        }
        
        //update the fee transactions.
        success &= processUpdateFeeTransactions();
        
        if (success) {
            //generate the waived and accrued report...
            success &= generateWaivedAndAccruedReport();
        }
        return success;
    }
    
    protected boolean updateKemidFeeWaivedYearToDateAmount() {
        // 6.2.1 Basic Process - Step 1:
        boolean updated = true;
        
        if (!kemidFeeDao.updateKemidFeeWaivedFeeYearToDateToZero()) {
            setExceptionReportTableRowReason("Batch Process Fee Transactions job is aborted.  Unable to update KEMID Year-To-Date Waiver Fee amounts");
            processFeeTransactionsExceptionReportsWriterService.writeTableRow(processFeeTransactionsExceptionRowReason);            
            processFeeTransactionsExceptionReportsWriterService.writeNewLines(1);
            return false;
        }
        
        return updated;
    }
    
    /**
     * Writes the reports headers for totals processed, waived and accrued fee, and exceptions reports.
     */
    protected void writeReportHeaders() {
        //writes the exception report header
        processFeeTransactionsExceptionReportsWriterService.writeNewLines(1);
        processFeeTransactionsExceptionReportsWriterService.writeTableHeader(processFeeTransactionsExceptionReportHeader);

        //writes the Waived and Accrued Fees report header....
        processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeNewLines(1);
        processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableHeader(processFeeTransactionsWaivedAndAccruedFeesReportHeader);
        
        //writes the Totals Processed report header....
        processFeeTransactionsTotalProcessedReportsWriterService.writeNewLines(1);
        processFeeTransactionsTotalProcessedReportsWriterService.writeTableHeader(processFeeTransactionsTotalProcessedReportHeader);
    }
    
    /**
     * Process update Fee Transactions
     */
    protected boolean processUpdateFeeTransactions() {
        boolean success = true;
        
        Date currentDate = kemService.getCurrentDate();
        
        Collection<FeeMethod> feeMethods = feeMethodService.getFeeMethodsByNextProcessingDate(currentDate);

        for (FeeMethod feeMethod : feeMethods) {
           //1. IF the END_FEE_MTHD_T:  FEE_TYP_CD is equal to T (Transactions)
            if (feeMethod.getFeeTypeCode().equals(EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_TRANSACTIONS)) {
                processTransactionArchivesCountForTransactionsFeeType(feeMethod);
            }
            
            //2. IF the END_FEE_MTHD_T:  FEE_TYP_CD is equal to B (Balance)
            if (feeMethod.getFeeTypeCode().equals(EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_BALANCES)) {
                processBalanceFeeType(feeMethod);
            }
            
            performCalculationsForKemId(feeMethod);
            success &= generateCashDecreaseDocument(feeMethod);
        }
        
        return success;
    }
    
    /**
     * Generates the fee waived and fee accrued report
     */
    protected boolean generateWaivedAndAccruedReport() {
        boolean success = true;
        KualiDecimal accruedFeeGrandTotal = new KualiDecimal("0");
        KualiDecimal waivedFeeGrandTotal = new KualiDecimal("0");
        
     //   FeeProcessingWaivedAndAccruedDetailTotalLine feeProcessingWaivedAndAccruedDetailTotalLine = new FeeProcessingWaivedAndAccruedDetailTotalLine();
     //   FeeProcessingWaivedAndAccruedSubTotalLine feeProcessingWaivedAndAccruedSubTotalLine = new FeeProcessingWaivedAndAccruedSubTotalLine();
      //  FeeProcessingWaivedAndAccruedGrandTotalLine feeProcessingWaivedAndAccruedGrandTotalLine = new FeeProcessingWaivedAndAccruedGrandTotalLine();
        
        Date currentDate = kemService.getCurrentDate();
        
        Collection<FeeMethod> feeMethods = feeMethodService.getFeeMethodsByNextProcessingDate(currentDate);
        Collection<KemidFee> kemidFeeRecords = new ArrayList();

        for (FeeMethod feeMethod : feeMethods) {
            KualiDecimal accruedFeeSubTotal = new KualiDecimal("0");
            KualiDecimal waivedFeeSubTotal = new KualiDecimal("0");
            
            for (KemidFee kemidFee : kemidFeeRecords) {
             //   feeProcessingWaivedAndAccruedDetailTotalLine.setTotal(feeMethod.getCode());
            //    feeProcessingWaivedAndAccruedDetailTotalLine.setKemid(kemidFee.getKemid());
           //     feeProcessingWaivedAndAccruedDetailTotalLine.setTotalAccruedFees(kemidFee.getTotalAccruedFees());
           //     feeProcessingWaivedAndAccruedDetailTotalLine.setTotalWaivedFees(kemidFee.getTotalWaivedFees());

              //  processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableRow(feeProcessingWaivedAndAccruedDetailTotalLine);
                processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeNewLines(1);
                
                accruedFeeSubTotal.add(kemidFee.getTotalAccruedFees());
                waivedFeeSubTotal.add(kemidFee.getTotalWaivedFees());
            }
            
        //    feeProcessingWaivedAndAccruedSubTotalLine.setTotalAccruedFees(accruedFeeSubTotal);
        //    feeProcessingWaivedAndAccruedSubTotalLine.setTotalWaivedFees(waivedFeeSubTotal);
            
        //    processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableRow(feeProcessingWaivedAndAccruedSubTotalLine);
            processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeNewLines(1);
            
            accruedFeeGrandTotal.add(accruedFeeSubTotal);
            waivedFeeGrandTotal.add(waivedFeeSubTotal);
        }
        
      //  feeProcessingWaivedAndAccruedGrandTotalLine.setTotalAccruedFees(accruedFeeGrandTotal);
     //   feeProcessingWaivedAndAccruedGrandTotalLine.setTotalWaivedFees(waivedFeeGrandTotal);
     //   processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableRow(feeProcessingWaivedAndAccruedGrandTotalLine);
        
        return success;
    }
    
    /**
     * 
     */
    protected void processTransactionArchivesCountForTransactionsFeeType(FeeMethod feeMethod) {
        // case: FEE_RT_DEF_CD = C for count
        if (feeMethod.getFeeRateDefinitionCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_COUNT)) {
            totalNumberOfRecords = transactionArchiveDao.getTransactionArchivesCountForTransactions(feeMethod);
            totalAmountCalculated = KEMCalculationRoundingHelper.multiply(feeMethod.getFirstFeeRate(), BigDecimal.valueOf(totalNumberOfRecords), EndowConstants.Scale.SECURITY_INCOME_RATE);
        }
            
        // case: FEE_RT_DEF_CD = V for value...
        if (feeMethod.getFeeRateDefinitionCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_VALUE)) {
            HashMap<String, BigDecimal> incomeAndPrincipalValues = transactionArchiveDao.getTransactionArchivesIncomeAndPrincipalCashAmountForTransactions(feeMethod);
              
            transactionIncomeAmount = incomeAndPrincipalValues.get(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_CASH_AMOUNT);
            transacationPrincipalAmount = incomeAndPrincipalValues.get(EndowPropertyConstants.TRANSACTION_ARCHIVE_PRINCIPAL_CASH_AMOUNT);
        }
    }
    
    /**
     * 
     */
    protected void processBalanceFeeType(FeeMethod feeMethod) {
        if (feeMethod.getFeeRateDefinitionCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_COUNT)) {
            //when FEE_BAL_TYP_CD = AU OR CU then total END_HLDG_HIST_T:HLDG_UNITS column
            if (feeMethod.getFeeBalanceTypeCode().equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS) || 
                feeMethod.getFeeBalanceTypeCode().equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_UNITS)) { 
                totalHoldingUnits = holdingHistoryDao.getHoldingHistoryTotalHoldingUnits(feeMethod);
            }
            
            if (feeMethod.getFeeBalanceTypeCode().equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_UNITS)) {
                totalHoldingUnits = currentTaxLotBalanceDao.getCurrentTaxLotBalanceTotalHoldingUnits(feeMethod);
            }
            
            totalAmountCalculated = KEMCalculationRoundingHelper.multiply(feeMethod.getFirstFeeRate(), totalHoldingUnits, EndowConstants.Scale.SECURITY_INCOME_RATE);
        }
        
        //if FEE_RATE_DEFINITION_CODE_FOR_COUNT = "V"
        if (feeMethod.getFeeRateDefinitionCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_VALUE)) {
            //when FEE_BAL_TYP_CD = AU OR CU then total END_HLDG_HIST_T:HLDG_UNITS column
            if (feeMethod.getFeeBalanceTypeCode().equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE) || 
                feeMethod.getFeeBalanceTypeCode().equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_MARKET_VALUE)) { 
                totalHoldingUnits = holdingHistoryDao.getHoldingHistoryTotalHoldingMarketValue(feeMethod);
            }
            
            if (feeMethod.getFeeBalanceTypeCode().equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_MARKET_VALUE)) {
                totalHoldingUnits = currentTaxLotBalanceDao.getCurrentTaxLotBalanceTotalHoldingMarketValue(feeMethod);
            }
            
            totalAmountCalculated = KEMCalculationRoundingHelper.multiply(feeMethod.getFirstFeeRate(), totalHoldingUnits, EndowConstants.Scale.SECURITY_INCOME_RATE);
        }
    }
    
    /**
     * Performs the calculations to get the fee amount to be charged against the selected kemids
     * @param feeMethod 
     */
    protected void performCalculationsForKemId(FeeMethod feeMethod) {
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
    }
    
    protected void performCalculationsAgainstTotalAmountCalculated(FeeMethod feeMethod) {
        if (totalAmountCalculated.compareTo(feeMethod.getFirstFeeBreakpoint().bigDecimalValue()) <= 0) {
            feeToBeCharged.add(KEMCalculationRoundingHelper.multiply(totalAmountCalculated, feeMethod.getFirstFeeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE));
        }
        
        if (totalAmountCalculated.compareTo(feeMethod.getFirstFeeBreakpoint().bigDecimalValue()) > 0 &&
            totalAmountCalculated.compareTo(feeMethod.getSecondFeeBreakpoint().bigDecimalValue()) <= 0){
            feeToBeCharged.add(KEMCalculationRoundingHelper.multiply(totalAmountCalculated, feeMethod.getSecondFeeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE));
        }
        
        if (totalAmountCalculated.compareTo(feeMethod.getSecondFeeBreakpoint().bigDecimalValue()) > 0){
            feeToBeCharged.add(KEMCalculationRoundingHelper.multiply(totalAmountCalculated, feeMethod.getThirdFeeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE));
        }
    }
    
    protected void calculateMinumumFeeAmount(FeeMethod feeMethod) {
        if (totalAmountCalculated.compareTo(feeMethod.getMinimumFeeToCharge().bigDecimalValue()) < 0) {
            feeToBeCharged = feeMethod.getMinimumFeeToCharge().bigDecimalValue();
        }
    }
    
    protected boolean checkForMinimumThresholdAmount(FeeMethod feeMethod, KemidFee kemidFee) {
        boolean shouldCharge = true;
        
        if (feeToBeCharged.compareTo(feeMethod.getMinimumFeeThreshold().bigDecimalValue()) < 0) {
            writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Fee is not charged as the fee is less than the minimum threshold");
            return false;
        }
        
        return shouldCharge;
    }
    
    /**
     * IF the field ACR_FEE is equal to Y (Yes), then add the calculated fee amount to the value in 
     * END_KEMID_FEE_MTHD_T: ACRD_FEE_TO_DT.
     * @param feeMethod, kemidFee
     * @return feeAcrrued true if fee amount is added to total accrued fees else return false
     */
    protected boolean processFeeAccrual(FeeMethod feeMethod, KemidFee kemidFee) {
        boolean feeAcrrued = true;
        
        KualiDecimal accruelFee = new KualiDecimal(feeToBeCharged.toString());
        kemidFee.setTotalAccruedFees(kemidFee.getTotalAccruedFees().add(accruelFee));
            
        //unable to save. write to exception...
        if (!kemidFeeService.saveKemidFee(kemidFee)) {
            writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Unable to add Calculated Fee to Total Accrued Fees in END_KEMID_FEE_T table.");
            return false;
        }
        
        return feeAcrrued;
    }
    
    /**
     * IF the field WAIVE_FEE is equal to Y (Yes), then add the calculated fee amount to the value 
     * in END_KEMID_FEE_MTHD_T: WAIVED_FEE_TO_DT and add the calculated fee amount to the value in 
     * END_KEMID_FEE_MTHD_T: WAIVED_FEE_YDT
     * @param feeMethod, kemidFee
     * @return feeWaived - true if fee amount is added to total waived fees else return false
     */
    protected boolean processFeeWaiver(FeeMethod feeMethod, KemidFee kemidFee) {
        boolean feeWaived = true;
        
        KualiDecimal accruelFee = new KualiDecimal(feeToBeCharged.toString());
        kemidFee.setTotalWaivedFeesThisFiscalYear(kemidFee.getTotalWaivedFeesThisFiscalYear().add(accruelFee));
        kemidFee.setTotalWaivedFees(kemidFee.getTotalWaivedFees().add(accruelFee));
            
        //save kemidFee record.
        if (!kemidFeeService.saveKemidFee(kemidFee)) {
            writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Unable to add Calculated Fee to Total Waived Fees in END_KEMID_FEE_T table.");
            return false;
        }
        
        return feeWaived;
    }
    
    /**
     * Generate a CashDecreaseDocument (ECDD) and processes the document by submitting/routing it.
     * @param feeMethod, kemidFee
     */
    protected boolean generateCashDecreaseDocument(FeeMethod feeMethod) {
        int lineNumber = 1;
        int maxNumberOfTransacationLines = kemService.getMaxNumberOfTransactionLinesPerDocument();                    
        
        // initialize CashDecreaseDocument...
        CashDecreaseDocument cashDecreaseDocument = (CashDecreaseDocument) createNewCashDecreaseDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
        if (ObjectUtils.isNull(cashDecreaseDocument)) {
            writeExceptionReportLine(feeMethod.getCode(), null, "Reason: Unable to create a new CashDecreaseDocument.");
            return false;
        }
        
        setDocumentOverviewAndDetails(cashDecreaseDocument, feeMethod.getName());
        
        Collection<KemidFee> kemidFeeRecords = new ArrayList();
        
        kemidFeeRecords = kemidFeeService.getAllKemidForFeeMethodCode(feeMethod.getCode());
        
        for (KemidFee kemidFee : kemidFeeRecords) {
            if (lineNumber <= maxNumberOfTransacationLines) {
                if (!createTransactionLines(cashDecreaseDocument, feeMethod, kemidFee, lineNumber, maxNumberOfTransacationLines)) {
                    //write out the exception for this kemid but keep continuing....
                    //TODO: write exception line.
                    writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Unable to add the transaction line to the document.");
                }
            }
            else {
                // reached max transactions.  submit and then create a new document....
                if (kualiRuleService.applyRules(new RouteDocumentEvent(cashDecreaseDocument))) {
                    if (submitDocumentForApprovalProcess(cashDecreaseDocument, feeMethod)) {
                        // initialize CashDecreaseDocument...
                        cashDecreaseDocument = (CashDecreaseDocument) createNewCashDecreaseDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
                        if (ObjectUtils.isNull(cashDecreaseDocument)) {
                            writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Unable to create a new CashDecreaseDocument.");
                            return false;
                        }

                        setDocumentOverviewAndDetails(cashDecreaseDocument, feeMethod.getName());
                        lineNumber = 1;
                    }
                    else {
                        //write out exception since can not submit the document....
                        writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: Unable to submit or route the document.");
                    }
                }
                else {
                    // document rules did not pass the validations.  so write exception report....
                    writeExceptionReportLine(feeMethod.getCode(), kemidFee.getKemid(), "Reason: The document did not pass the rule validations.");
                }
            }
        }
        
        return true;
    }
    
    protected void setDocumentOverviewAndDetails(CashDecreaseDocument cashDecreaseDocument, String documentDescription) {
        cashDecreaseDocument.getDocumentHeader().setDocumentDescription(documentDescription);
        cashDecreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
        cashDecreaseDocument.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
    }
    
    protected boolean submitDocumentForApprovalProcess(CashDecreaseDocument cashDecreaseDocument, FeeMethod feeMethod) {
        boolean success = true;
        
        if (feeMethod.getFeePostPendingIndicator()) {
            success = submitCashDecreaseDocument(cashDecreaseDocument);
        }
        else {
            success = routeCashDecreaseDocument(cashDecreaseDocument);
        }
        
        return success;
    }
    
    protected CashDecreaseDocument createNewCashDecreaseDocument(String documentType) {
        CashDecreaseDocument newCashDecreaseDocument = null;
        
        try {
            newCashDecreaseDocument = (CashDecreaseDocument) documentService.getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName(documentType));     
        } catch (WorkflowException wfe) {
            LOG.error("Failed to initialize CashIncreaseDocument");            
            return null;
        }
        
        return newCashDecreaseDocument;
    }
    
    protected boolean createTransactionLines(CashDecreaseDocument cashDecreaseDocument, FeeMethod feeMethod, KemidFee kemidFee, int lineNumber, int maxNumberOfTransacationLines) {
        // logic as in 9.3.b
        if (kemidFee.getPercentOfFeeChargedToIncome().equals(new KualiDecimal("1"))) {
            EndowmentSourceTransactionLine endowmentSourceTransactionLine = createEndowmentSourceTransactionLine(lineNumber, feeMethod, kemidFee, EndowConstants.IncomePrincipalIndicator.INCOME, feeToBeCharged);
            return addTransactionLineToDocument(cashDecreaseDocument, endowmentSourceTransactionLine, lineNumber);
        }
        
        // logic to charge according to logic in 9.3.c
        BigDecimal feeAmountForIncome = KEMCalculationRoundingHelper.multiply(feeToBeCharged, new BigDecimal(kemidFee.getPercentOfFeeChargedToIncome().toString()), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = createEndowmentSourceTransactionLine(lineNumber, feeMethod, kemidFee, EndowConstants.IncomePrincipalIndicator.INCOME, feeAmountForIncome);
        addTransactionLineToDocument(cashDecreaseDocument, endowmentSourceTransactionLine, ++lineNumber);
        
        BigDecimal feeAmountForPrincipal = KEMCalculationRoundingHelper.multiply(feeToBeCharged, new BigDecimal(kemidFee.getPercentOfFeeChargedToPrincipal().toString()), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        
        if (lineNumber <= maxNumberOfTransacationLines) {
            endowmentSourceTransactionLine = createEndowmentSourceTransactionLine(lineNumber, feeMethod, kemidFee, EndowConstants.IncomePrincipalIndicator.PRINCIPAL, feeAmountForPrincipal);
            return addTransactionLineToDocument(cashDecreaseDocument, endowmentSourceTransactionLine, ++lineNumber);
        }
        else {
            boolean submitted = submitDocumentForApprovalProcess(cashDecreaseDocument, feeMethod);
            
            // initialize CashDecreaseDocument...
            cashDecreaseDocument = (CashDecreaseDocument) createNewCashDecreaseDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
            if (ObjectUtils.isNull(cashDecreaseDocument)) {
                writeExceptionReportLine(feeMethod.getCode(), null, "Reason: Unable to create a new CashDecreaseDocument.");
                return false;
            }

            setDocumentOverviewAndDetails(cashDecreaseDocument, feeMethod.getName());
            lineNumber = 1;
            endowmentSourceTransactionLine = createEndowmentSourceTransactionLine(lineNumber, feeMethod, kemidFee, EndowConstants.IncomePrincipalIndicator.PRINCIPAL, feeAmountForPrincipal);
            return addTransactionLineToDocument(cashDecreaseDocument, endowmentSourceTransactionLine, ++lineNumber);
        }
    }
    
    protected boolean addTransactionLineToDocument(CashDecreaseDocument cashDecreaseDocument, EndowmentSourceTransactionLine endowmentSourceTransactionLine, int lineNumber) {
        boolean added = true;
        
        if (kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, cashDecreaseDocument, endowmentSourceTransactionLine))) {
            cashDecreaseDocument.getSourceTransactionLines().add(endowmentSourceTransactionLine);
            cashDecreaseDocument.setNextSourceLineNumber(lineNumber);
        }
        else {
            LOG.info("CashDecreaseDocument Rules Failed.  Can not process this document");
            return false;
        }
        
        return added;
    }
    
    protected EndowmentSourceTransactionLine createEndowmentSourceTransactionLine(int lineNumber, FeeMethod feeMethod, KemidFee kemidFee, String iPIndicator, BigDecimal feeAmount) {
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setTransactionLineNumber(lineNumber);
        endowmentSourceTransactionLine.setKemid(kemidFee.getChargeFeeToKemid());
        endowmentSourceTransactionLine.setEtranCode(feeMethod.getFeeExpenseETranCode());
        endowmentSourceTransactionLine.setTransactionIPIndicatorCode(iPIndicator);
        endowmentSourceTransactionLine.setTransactionAmount(new KualiDecimal(feeToBeCharged.toString()));
 
       return endowmentSourceTransactionLine;
    }
    
    protected boolean submitDocument(CashDecreaseDocument cashDecreaseDocument, boolean feePostPendingIndicator) {
        boolean success = true;
        
        if (feePostPendingIndicator) {
            success = submitCashDecreaseDocument(cashDecreaseDocument);
        }
        else {
            success = routeCashDecreaseDocument(cashDecreaseDocument);
        }
        
        return success;
    }
    
    
    protected boolean submitCashDecreaseDocument(CashDecreaseDocument cashDecreaseDocument) {
        boolean approved = true;
        
        cashDecreaseDocument.setNoRouteIndicator(true);
        
        try {
            Note approveNote = noteService.createNote(new Note(), cashDecreaseDocument.getDocumentHeader());
            approveNote.setNoteText("Submitted the document as a 'No Route'.");

            Person systemUser = getPersonService().getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
            approveNote.setAuthorUniversalIdentifier(systemUser.getPrincipalId());
            
            noteService.save(approveNote);
            
            cashDecreaseDocument.addNote(approveNote);
            
            documentService.saveDocument(cashDecreaseDocument);
        }
        catch (WorkflowException wfe) {
            return false;
        }
        catch (Exception ex) {
            return false;            
        }
        
        return approved;
    }
    
    protected boolean routeCashDecreaseDocument(CashDecreaseDocument cashDecreaseDocument) {
        boolean routed = true;
        
        try {
            documentService.routeDocument(cashDecreaseDocument, "Submitted the document for routing.", null);
        }
        catch (WorkflowException wfe) {
            return false;
        }
        
        return routed;
    }
    
    protected void writeExceptionReportLine(String feeMethodCode, String kemid, String reason) {
        processFeeTransactionsRowValues.setColumnHeading1(feeMethodCode);
        processFeeTransactionsRowValues.setColumnHeading2(kemid);
        processFeeTransactionsRowValues.setColumnHeading3(feeToBeCharged.toString());
        writeTableRowAndTableReason(reason);
    }
    
    /**
     * writes out the table row values for document type, secuityId, kemId and then writes the reason row and inserts a blank line
     * @param ehva the holding history value adjustment document
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
     * @param reasonForException The reason that will be set in the exception report
     */
    protected void setExceptionReportTableRowReason(String reasonForException) {
        
        processFeeTransactionsExceptionRowReason.setColumnHeading1("Reason: " + reasonForException);
        processFeeTransactionsExceptionRowReason.setColumnHeading2("");
        processFeeTransactionsExceptionRowReason.setColumnHeading3("");
    }

    
    /**
     * Gets the processFeeTransactionsExceptionReportsWriterService attribute. 
     * @return Returns the processFeeTransactionsExceptionReportsWriterService.
     */
    protected ReportWriterService getProcessFeeTransactionsExceptionReportsWriterService() {
        return processFeeTransactionsExceptionReportsWriterService;
    }
    
    /**
     * Sets the processFeeTransactionsExceptionReportsWriterService attribute value.
     * @param processFeeTransactionsExceptionReportsWriterService The processFeeTransactionsExceptionReportsWriterService to set.
     */
    public void setProcessFeeTransactionsExceptionReportsWriterService(ReportWriterService processFeeTransactionsExceptionReportsWriterService) {
        this.processFeeTransactionsExceptionReportsWriterService = processFeeTransactionsExceptionReportsWriterService;
    }

    /**
     * Gets the processFeeTransactionsTotalProcessedReportsWriterService attribute. 
     * @return Returns the processFeeTransactionsTotalProcessedReportsWriterService.
     */
    public ReportWriterService getProcessFeeTransactionsTotalProcessedReportsWriterService() {
        return processFeeTransactionsTotalProcessedReportsWriterService;
    }

    /**
     * Sets the processFeeTransactionsTotalProcessedReportsWriterService attribute value.
     * @param processFeeTransactionsTotalProcessedReportsWriterService The processFeeTransactionsTotalProcessedReportsWriterService to set.
     */
    public void setProcessFeeTransactionsTotalProcessedReportsWriterService(ReportWriterService processFeeTransactionsTotalProcessedReportsWriterService) {
        this.processFeeTransactionsTotalProcessedReportsWriterService = processFeeTransactionsTotalProcessedReportsWriterService;
    }
    
    /**
     * Gets the processFeeTransactionsWaivedAndAccruedFeesReportsWriterService attribute. 
     * @return Returns the processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.
     */
    public ReportWriterService getProcessFeeTransactionsWaivedAndAccruedFeesReportsWriterService() {
        return processFeeTransactionsWaivedAndAccruedFeesReportsWriterService;
    }

    /**
     * Sets the processFeeTransactionsWaivedAndAccruedFeesReportsWriterService attribute value.
     * @param processFeeTransactionsWaivedAndAccruedFeesReportsWriterService The processFeeTransactionsWaivedAndAccruedFeesReportsWriterService to set.
     */
    public void setProcessFeeTransactionsWaivedAndAccruedFeesReportsWriterService(ReportWriterService processFeeTransactionsWaivedAndAccruedFeesReportsWriterService) {
        this.processFeeTransactionsWaivedAndAccruedFeesReportsWriterService = processFeeTransactionsWaivedAndAccruedFeesReportsWriterService;
    }
    
    /**
     * Gets the holdingHistoryService attribute. 
     * @return Returns the holdingHistoryService.
     */
    protected KemidFeeService getKemidFeeService() {
        return kemidFeeService;
    }

    /**
     * Sets the kKemidFeeService attribute value.
     * @param kemidFeeService The kemidFeeService to set.
     */
    public void setKemidFeeService(KemidFeeService kemidFeeService) {
        this.kemidFeeService = kemidFeeService;
    }
    
    /**
     * Gets the feeMethodService attribute. 
     * @return Returns the feeMethodService.
     */
    protected FeeMethodService getFeeMethodService() {
        return feeMethodService;
    }

    /**
     * Sets the feeMethodService attribute value.
     * @param feeMethodService The feeMethodService to set.
     */
    public void setFeeMethodService(FeeMethodService feeMethodService) {
        this.feeMethodService = feeMethodService;
    }
    
    /**
     * Gets the kemService.
     * @return kemService
     */
    protected KEMService getKemService() {
        return kemService;
    }

    /**
     * Sets the kemService.
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Gets the processFeeTransactionsExceptionReportHeader attribute. 
     * @return Returns the processFeeTransactionsExceptionReportHeader.
     */
    public EndowmentExceptionReportHeader getProcessFeeTransactionsExceptionReportHeader() {
        return processFeeTransactionsExceptionReportHeader;
    }

    /**
     * Sets the processFeeTransactionsExceptionReportHeader attribute value.
     * @param processFeeTransactionsExceptionReportHeader The processFeeTransactionsExceptionReportHeader to set.
     */
    public void setProcessFeeTransactionsExceptionReportHeader(EndowmentExceptionReportHeader processFeeTransactionsExceptionReportHeader) {
        this.processFeeTransactionsExceptionReportHeader = processFeeTransactionsExceptionReportHeader;
    }

    /**
     * Gets the processFeeTransactionsTotalProcessedReportHeader attribute. 
     * @return Returns the processFeeTransactionsTotalProcessedReportHeader.
     */
    public EndowmentExceptionReportHeader getProcessFeeTransactionsTotalProcessedReportHeader() {
        return processFeeTransactionsTotalProcessedReportHeader;
    }

    /**
     * Sets the processFeeTransactionsTotalProcessedReportHeader attribute value.
     * @param processFeeTransactionsTotalProcessedReportHeader The processFeeTransactionsTotalProcessedReportHeader to set.
     */
    public void setProcessFeeTransactionsTotalProcessedReportHeader(EndowmentExceptionReportHeader processFeeTransactionsTotalProcessedReportHeader) {
        this.processFeeTransactionsTotalProcessedReportHeader = processFeeTransactionsTotalProcessedReportHeader;
    }

    /**
     * Gets the processFeeTransactionsWaivedAndAccruedFeesReportHeader attribute. 
     * @return Returns the processFeeTransactionsWaivedAndAccruedFeesReportHeader.
     */
    public EndowmentExceptionReportHeader getProcessFeeTransactionsWaivedAndAccruedFeesReportHeader() {
        return processFeeTransactionsWaivedAndAccruedFeesReportHeader;
    }

    /**
     * Sets the processFeeTransactionsWaivedAndAccruedFeesReportHeader attribute value.
     * @param processFeeTransactionsWaivedAndAccruedFeesReportHeader The processFeeTransactionsWaivedAndAccruedFeesReportHeader to set.
     */
    public void setProcessFeeTransactionsWaivedAndAccruedFeesReportHeader(EndowmentExceptionReportHeader processFeeTransactionsWaivedAndAccruedFeesReportHeader) {
        this.processFeeTransactionsWaivedAndAccruedFeesReportHeader = processFeeTransactionsWaivedAndAccruedFeesReportHeader;
    }

    /**
     * Gets the processFeeTransactionsRowValues attribute. 
     * @return Returns the processFeeTransactionsRowValues.
     */
    public EndowmentExceptionReportHeader getProcessFeeTransactionsRowValues() {
        return processFeeTransactionsRowValues;
    }

    /**
     * Sets the processFeeTransactionsRowValues attribute value.
     * @param processFeeTransactionsRowValues The processFeeTransactionsRowValues to set.
     */
    public void setProcessFeeTransactionsRowValues(EndowmentExceptionReportHeader processFeeTransactionsRowValues) {
        this.processFeeTransactionsRowValues = processFeeTransactionsRowValues;
    }

    /**
     * Gets the processFeeTransactionsExceptionRowReason attribute. 
     * @return Returns the processFeeTransactionsExceptionRowReason.
     */
    public EndowmentExceptionReportHeader getProcessFeeTransactionsExceptionRowReason() {
        return processFeeTransactionsExceptionRowReason;
    }

    /**
     * Sets the processFeeTransactionsExceptionRowReason attribute value.
     * @param processFeeTransactionsExceptionRowReason The processFeeTransactionsExceptionRowReason to set.
     */
    public void setProcessFeeTransactionsExceptionRowReason(EndowmentExceptionReportHeader processFeeTransactionsExceptionRowReason) {
        this.processFeeTransactionsExceptionRowReason = processFeeTransactionsExceptionRowReason;
    }
    
    /**
     * Gets the transactionArchiveService attribute. 
     * @return Returns the transactionArchiveService.
     */
    public TransactionArchiveService getTransactionArchiveService() {
        return transactionArchiveService;
    }

    /**
     * Sets the transactionArchiveService attribute value.
     * @param transactionArchiveService The transactionArchiveService to set.
     */
    public void setTransactionArchiveService(TransactionArchiveService transactionArchiveService) {
        this.transactionArchiveService = transactionArchiveService;
    }
    
    /**
     * Gets the transactionArchiveDao attribute. 
     * @return Returns the transactionArchiveDao.
     */
    public TransactionArchiveDao getTransactionArchiveDao() {
        return transactionArchiveDao;
    }

    /**
     * Sets the transactionArchiveDao attribute value.
     * @param transactionArchiveDao The transactionArchiveDao to set.
     */
    public void setTransactionArchiveDao(TransactionArchiveDao transactionArchiveDao) {
        this.transactionArchiveDao = transactionArchiveDao;
    }
    /**
     * Gets the holdingHistoryDao attribute. 
     * @return Returns the holdingHistoryDao.
     */
    public HoldingHistoryDao getHoldingHistoryDao() {
        return holdingHistoryDao;
    }

    /**
     * Sets the holdingHistoryDao attribute value.
     * @param holdingHistoryDao The holdingHistoryDao to set.
     */
    public void setHoldingHistoryDao(HoldingHistoryDao holdingHistoryDao) {
        this.holdingHistoryDao = holdingHistoryDao;
    }
    
    /**
     * Gets the currentTaxLotBalanceDao attribute. 
     * @return Returns the currentTaxLotBalanceDao.
     */
    public CurrentTaxLotBalanceDao getCurrentTaxLotBalanceDao() {
        return currentTaxLotBalanceDao;
    }

    /**
     * Sets the currentTaxLotBalanceDao attribute value.
     * @param currentTaxLotBalanceDao The currentTaxLotBalanceDao to set.
     */
    public void setCurrentTaxLotBalanceDao(CurrentTaxLotBalanceDao currentTaxLotBalanceDao) {
        this.currentTaxLotBalanceDao = currentTaxLotBalanceDao;
    }

    /**
     * Gets the kemidFeeDao attribute. 
     * @return Returns the kemidFeeDao.
     */
    public KemidFeeDao getKemidFeeDao() {
        return kemidFeeDao;
    }

    /**
     * Sets the kemidFeeDao attribute value.
     * @param kemidFeeDao The kemidFeeDao to set.
     */
    public void setKemidFeeDao(KemidFeeDao kemidFeeDao) {
        this.kemidFeeDao = kemidFeeDao;
    }
    /**
     * Sets the documentService attribute value.
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
     * @return Returns the kualiRuleService.
     */
    protected KualiRuleService getKualiRuleService() {
        return kualiRuleService;
    }

    /**
     * Sets the kualiRuleService attribute value.
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }
  
    /**
     * Gets the NoteService, lazily initializing if necessary
     * @return the NoteService
     */
    protected synchronized NoteService getNoteService() {
        if (this.noteService == null) {
            this.noteService = KNSServiceLocator.getNoteService();
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
    protected PersonService<Person> getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }
    
    /**
     * Gets the feeProcessingWaivedAndAccruedDetailTotalLine attribute. 
     * @return Returns the feeProcessingWaivedAndAccruedDetailTotalLine.
     */
 //   public FeeProcessingWaivedAndAccruedDetailTotalLine getFeeProcessingWaivedAndAccruedDetailTotalLine() {
 //       return feeProcessingWaivedAndAccruedDetailTotalLine;
 //   }

    /**
     * Sets the feeProcessingWaivedAndAccruedDetailTotalLine attribute value.
     * @param feeProcessingWaivedAndAccruedDetailTotalLine The feeProcessingWaivedAndAccruedDetailTotalLine to set.
     */
  //  public void setFeeProcessingWaivedAndAccruedDetailTotalLine(FeeProcessingWaivedAndAccruedDetailTotalLine feeProcessingWaivedAndAccruedDetailTotalLine) {
 //       this.feeProcessingWaivedAndAccruedDetailTotalLine = feeProcessingWaivedAndAccruedDetailTotalLine;
  //  }

    /**
     * Gets the feeProcessingWaivedAndAccruedSubTotalLine attribute. 
     * @return Returns the feeProcessingWaivedAndAccruedSubTotalLine.
     */
 //   public FeeProcessingWaivedAndAccruedSubTotalLine getFeeProcessingWaivedAndAccruedSubTotalLine() {
 //       return feeProcessingWaivedAndAccruedSubTotalLine;
 //   }

    /**
     * Sets the feeProcessingWaivedAndAccruedSubTotalLine attribute value.
     * @param feeProcessingWaivedAndAccruedSubTotalLine The feeProcessingWaivedAndAccruedSubTotalLine to set.
     */
 //   public void setFeeProcessingWaivedAndAccruedSubTotalLine(FeeProcessingWaivedAndAccruedSubTotalLine feeProcessingWaivedAndAccruedSubTotalLine) {
 //       this.feeProcessingWaivedAndAccruedSubTotalLine = feeProcessingWaivedAndAccruedSubTotalLine;
 //   }

    /**
     * Gets the feeProcessingWaivedAndAccruedGrandTotalLine attribute. 
     * @return Returns the feeProcessingWaivedAndAccruedGrandTotalLine.
     */
 //   public FeeProcessingWaivedAndAccruedGrandTotalLine getFeeProcessingWaivedAndAccruedGrandTotalLine() {
 //       return feeProcessingWaivedAndAccruedGrandTotalLine;
 //   }

    /**
     * Sets the feeProcessingWaivedAndAccruedGrandTotalLine attribute value.
     * @param feeProcessingWaivedAndAccruedGrandTotalLine The feeProcessingWaivedAndAccruedGrandTotalLine to set.
     */
 //   public void setFeeProcessingWaivedAndAccruedGrandTotalLine(FeeProcessingWaivedAndAccruedGrandTotalLine feeProcessingWaivedAndAccruedGrandTotalLine) {
 //       this.feeProcessingWaivedAndAccruedGrandTotalLine = feeProcessingWaivedAndAccruedGrandTotalLine;
 //   }
}

