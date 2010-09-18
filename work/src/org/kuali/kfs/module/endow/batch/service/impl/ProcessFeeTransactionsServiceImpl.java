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

import org.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
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
    
    private ReportWriterService processFeeTransactionsExceptionReportsWriterService;
    
    EndowmentExceptionReportHeader processFeeTransactionsExceptionReportHeader;
    EndowmentExceptionReportHeader processFeeTransactionsExceptionRowValues;
    EndowmentExceptionReportHeader processFeeTransactionsExceptionRowReason;    
    
    /**
     * Constructs a HoldingHistoryMarketValuesUpdateServiceImpl instance
     */
    public ProcessFeeTransactionsServiceImpl() {
        processFeeTransactionsExceptionReportHeader = new EndowmentExceptionReportHeader();
        processFeeTransactionsExceptionRowValues = new EndowmentExceptionReportHeader();
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
        processFeeTransactionsExceptionReportHeader.setColumnHeading1("Fee Method");
        processFeeTransactionsExceptionReportHeader.setColumnHeading2("KEMID");
        processFeeTransactionsExceptionReportHeader.setColumnHeading3("Fee Amount");
        
        processFeeTransactionsExceptionReportsWriterService.writeTableHeader(processFeeTransactionsExceptionReportHeader);
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
        
        
        return success;
    }
    
    /**
     * writes out the table row values for document type, secuityId, kemId and then writes the reason row and inserts a blank line
     * @param ehva the holding history value adjustment document
     * @param reasonMessage the reason message
     */
    protected void writeTableRowAndTableReason(HoldingHistoryValueAdjustmentDocument ehva, String reasonMessage) {
        getExceptionReportTableRowValues(ehva);
        
        processFeeTransactionsExceptionReportsWriterService.writeTableRow(processFeeTransactionsExceptionRowValues);            
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
        
        processFeeTransactionsExceptionRowValues.setColumnHeading1(documentTypeName);
        processFeeTransactionsExceptionRowValues.setColumnHeading2(ehva.getSecurityId());
   //     processFeeTransactionsExceptionRowValues.setColumnHeading3(holdingHistoryService.getKemIdFromHoldingHistory(ehva.getSecurityId()));
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
     * Gets the holdingHistoryService attribute. 
     * @return Returns the holdingHistoryService.
     */
    public KemidFeeService getKemidFeeService() {
        return kemidFeeService;
    }

    /**
     * Sets the kKemidFeeService attribute value.
     * @param kemidFeeService The kemidFeeService to set.
     */
    public void setKemidFeeService(KemidFeeService kemidFeeService) {
        this.kemidFeeService = kemidFeeService;
    }

}
