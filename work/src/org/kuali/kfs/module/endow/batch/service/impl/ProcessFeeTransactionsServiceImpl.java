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

import java.sql.Date;
import java.util.Collection;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.KemidFeeService;
import org.kuali.kfs.sys.service.ReportWriterService;
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

    private ReportWriterService processFeeTransactionsExceptionReportsWriterService;
    private ReportWriterService processFeeTransactionsTotalProcessedReportsWriterService;
    private ReportWriterService processFeeTransactionsWaivedAndAccruedFeesReportsWriterService;
    
    private EndowmentExceptionReportHeader processFeeTransactionsExceptionReportHeader;
    private EndowmentExceptionReportHeader processFeeTransactionsTotalProcessedReportHeader;
    private EndowmentExceptionReportHeader processFeeTransactionsWaivedAndAccruedFeesReportHeader;    
    
    private EndowmentExceptionReportHeader processFeeTransactionsRowValues;
    private EndowmentExceptionReportHeader processFeeTransactionsExceptionRowReason;    
    
    /**
     * Constructs a HoldingHistoryMarketValuesUpdateServiceImpl instance
     */
    public ProcessFeeTransactionsServiceImpl() {
        processFeeTransactionsExceptionReportHeader = new EndowmentExceptionReportHeader();
        processFeeTransactionsTotalProcessedReportHeader = new EndowmentExceptionReportHeader();
        processFeeTransactionsWaivedAndAccruedFeesReportHeader = new EndowmentExceptionReportHeader();
        
        processFeeTransactionsRowValues = new EndowmentExceptionReportHeader();
        processFeeTransactionsExceptionRowReason = new EndowmentExceptionReportHeader();                
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
        
        //writes the exception report header
        processFeeTransactionsExceptionReportsWriterService.writeNewLines(1);
        processFeeTransactionsExceptionReportsWriterService.writeTableHeader(processFeeTransactionsExceptionReportHeader);

        //writes the Waived and Accrued Fees report header....
        processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeNewLines(1);
        processFeeTransactionsWaivedAndAccruedFeesReportsWriterService.writeTableHeader(processFeeTransactionsWaivedAndAccruedFeesReportHeader);
        
        //writes the Totals Processed report header....
        processFeeTransactionsTotalProcessedReportsWriterService.writeNewLines(1);
        processFeeTransactionsTotalProcessedReportsWriterService.writeTableHeader(processFeeTransactionsTotalProcessedReportHeader);
        
        // 6.2.1 Basic Process - Step 1:
        if (!updateKemIdYearToDateWaiverFeeAmounts()) {
            setExceptionReportTableRowReason("Batch Process Fee Transactions job is aborted.  Unable to update KEMID Year-To-Date Waiver Fee amounts");
            processFeeTransactionsExceptionReportsWriterService.writeTableRow(processFeeTransactionsExceptionRowReason);            
            processFeeTransactionsExceptionReportsWriterService.writeNewLines(1);
        }
        
        //update the fee transactions.
    //    success = processUpdateFeeTransactions();
        
        return success;
    }
    
    /**
     * Updates WAIVE_FEE_YTD IN END_KEMID_FEE_T table to zero when current date is the first day of the
     * institution's fiscal year (FISCAL_YEAR_END_DAY_AND_MONTH system parameter).
     *
     * @return true if successful else false
     */
    protected boolean updateKemIdYearToDateWaiverFeeAmounts() {
        boolean updated = true;
        
        //use the service to update the waiver fee YTD totals in END_KEMID_FEE_T table.
        if (!kemidFeeService.updateWaiverFeeYearToDateTotals()) {
            return false;
        }
        
        return updated;
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
                
            }
            
            //2. IF the END_FEE_MTHD_T:  FEE_TYP_CD is equal to B (Balance)
            if (feeMethod.getFeeTypeCode().equals(EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_BALANCES)) {
                
            }
            
            //3. IF the END_FEE_MTHD_T:  FEE_TYP_CD is equal to P (Payment), 
            if (feeMethod.getFeeTypeCode().equals(EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_PAYMENTS)) {
                
            }
            
        }
        
        return success;
    }
    
    /**
     * writes out the table row values for document type, secuityId, kemId and then writes the reason row and inserts a blank line
     * @param ehva the holding history value adjustment document
     * @param reasonMessage the reason message
     */
    protected void writeTableRowAndTableReason(HoldingHistoryValueAdjustmentDocument ehva, String reasonMessage) {
        getExceptionReportTableRowValues(ehva);
        
        processFeeTransactionsExceptionReportsWriterService.writeTableRow(processFeeTransactionsRowValues);            
        setExceptionReportTableRowReason(reasonMessage);
        processFeeTransactionsExceptionReportsWriterService.writeTableRow(processFeeTransactionsExceptionRowReason);            
        processFeeTransactionsExceptionReportsWriterService.writeNewLines(1);
    }
    
    /**
     * gets the values for document type, securityId, and kemdId that will be written to the exception report
     * @param ehva
     */
    protected void getExceptionReportTableRowValues(HoldingHistoryValueAdjustmentDocument ehva) {
        String documentTypeName = ehva.getDocumentHeader().getWorkflowDocument().getDocumentType();
        
        processFeeTransactionsRowValues.setColumnHeading1(documentTypeName);
        processFeeTransactionsRowValues.setColumnHeading2(ehva.getSecurityId());
   //     processFeeTransactionsRowValues.setColumnHeading3(holdingHistoryService.getKemIdFromHoldingHistory(ehva.getSecurityId()));
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
}
