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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.service.GeneralLedgerInterfaceBatchProcessService;
import org.kuali.kfs.module.endow.batch.service.KemidFeeService;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchExceptionTableRowValues;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchStatisticsReportHeader;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchStatisticsTableRowValues;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchTotalsProcessedReportHeader;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchTotalsProcessedTableRowValues;
import org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.dataaccess.KemidFeeDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the GeneralLedgerInterfaceBatchProcessService.
 */
@Transactional
public class GeneralLedgerInterfaceBatchProcessServiceImpl implements GeneralLedgerInterfaceBatchProcessService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerInterfaceBatchProcessServiceImpl.class);
    
    protected String enterpriseFeedDirectoryName;
    
    protected KemidFeeService kemidFeeService;
    protected FeeMethodService feeMethodService;
    protected KEMService kemService;
    protected TransactionArchiveDao transactionArchiveDao;
    protected HoldingHistoryDao holdingHistoryDao;
    protected CurrentTaxLotBalanceDao currentTaxLotBalanceDao;
    protected KemidFeeDao kemidFeeDao;
    protected KualiConfigurationService configService;
    
    //report writer services for statistics, totals processed, and exception reports
    protected ReportWriterService gLInterfaceBatchStatisticsReportsWriterService;
    protected ReportWriterService gLInterfaceBatchTotalProcessedReportsWriterService;
    protected ReportWriterService gLInterfaceBatchExceptionReportsWriterService;
    
    //report headers..bos
    protected GLInterfaceBatchExceptionReportHeader gLInterfaceBatchExceptionReportHeader;
    protected GLInterfaceBatchTotalsProcessedReportHeader gLInterfaceBatchTotalProcessedReportHeader;
    protected GLInterfaceBatchStatisticsReportHeader gLInterfaceBatchStatisticsReportHeader;    
    
    //Exception Report Table Row value and Row Message only...bos
    protected GLInterfaceBatchExceptionTableRowValues gLInterfaceBatchExceptionTableRowValues;
    protected GLInterfaceBatchExceptionTableRowValues gLInterfaceBatchExceptionRowReason;    
    
    //Totals Processed report's table row..details, subtotal at chart, subtotal at document type and grand total lines
    protected GLInterfaceBatchTotalsProcessedTableRowValues gLInterfaceBatchTotalsProcessedTableRowValues;
    
    //statics table row values...bos
    protected GLInterfaceBatchStatisticsTableRowValues gLInterfaceBatchStatisticsTableRowValues;
    
    //the properties to totals processed at chart level..sub totals.
    protected BigDecimal chartDebitAmountSubTotal = BigDecimal.ZERO;
    protected BigDecimal chartCreditAmountSubTotal = BigDecimal.ZERO;
    protected long chartNumberOfRecordsSubTotal = 0;
    
    //the properties to totals processed at Document Type level..sub totals.
    protected BigDecimal documentTypeDebitAmountSubTotal = BigDecimal.ZERO;
    protected BigDecimal documentTypeCreditAmountSubTotal = BigDecimal.ZERO;
    protected long documentTypeNumberOfRecordsSubTotal = 0;
    
    //the properties to totals processed at Document Type level..Grand totals.
    protected BigDecimal documentTypeDebitAmountGrandTotal = BigDecimal.ZERO;
    protected BigDecimal documentTypeCreditAmountGrandTotal = BigDecimal.ZERO;
    protected long documentTypeNumberOfRecordsGrandTotal = 0;    
    
    //statistics reports details...
    protected long numberOfGLEntriesByDocumentType = 0;
    protected long numberOfExceptionsByDocumentType = 0;
    
    /**
     * Constructs a HoldingHistoryMarketValuesUpdateServiceImpl instance
     */
    public GeneralLedgerInterfaceBatchProcessServiceImpl() {
        //report writer headers
        gLInterfaceBatchExceptionReportHeader = new GLInterfaceBatchExceptionReportHeader();
        gLInterfaceBatchTotalProcessedReportHeader = new GLInterfaceBatchTotalsProcessedReportHeader();
        gLInterfaceBatchStatisticsReportHeader = new GLInterfaceBatchStatisticsReportHeader();
        
        //exception report detail and a reason lines.
        gLInterfaceBatchExceptionTableRowValues = new GLInterfaceBatchExceptionTableRowValues();
        gLInterfaceBatchExceptionRowReason = new GLInterfaceBatchExceptionTableRowValues();  
        
        //Totals processed report....This one will be used to write all the rows in totals procesed report.
        gLInterfaceBatchTotalsProcessedTableRowValues = new GLInterfaceBatchTotalsProcessedTableRowValues();
        
        //statistics report...
        gLInterfaceBatchStatisticsTableRowValues = new GLInterfaceBatchStatisticsTableRowValues();
    }

    /**
     * The fee process is intended to provide as much flexibility to the institution as possible when 
     * designing the charges to be assessed against a KEMID.  The fees can be based on either balances 
     * or activity and can be charged, accrued or waived at the KEMID level.
     * @see oorg.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService#processFeeTransactions()\
     * return boolean true if successful else false
     */
    public boolean processKEMActivityToCreateGLEntries() {
        LOG.info("processFeeTransactions() started");
        
        boolean success = true;
        writeReportHeaders();
        
        //main job to process KEM activity...
        processKEMActivity();
        
        return success;
    }
    
    /**
     * process the KEM Activity transactions to create gl entries in the enterprise feed file
     */
    public void processKEMActivity() {
        PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps = createActivityEnterpriseFeedDataFile();
        PrintStream OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps = createActivityEnterpriseFeedReconcileFile();
        
        OUTPUT_KEM_TO_GL_DATA_FILE_ps.close();
        OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps.close();
    }
    
    /**
     * Writes the reports headers for totals processed, waived and accrued fee, and exceptions reports.
     */
    protected void writeReportHeaders() {
        //writes the exception report header
        gLInterfaceBatchExceptionReportsWriterService.writeNewLines(1);
        gLInterfaceBatchExceptionReportsWriterService.writeTableHeader(gLInterfaceBatchExceptionReportHeader);

        //statistics report header....
        gLInterfaceBatchStatisticsReportsWriterService.writeNewLines(1);
        gLInterfaceBatchStatisticsReportsWriterService.writeTableHeader(gLInterfaceBatchStatisticsReportHeader);
        
        //writes the Totals Processed report header....
        gLInterfaceBatchTotalProcessedReportsWriterService.writeNewLines(1);
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableHeader(gLInterfaceBatchTotalProcessedReportHeader);
    }
    
    /**
     * create the main data file in the gl enterpriseFeed folder
     */
    PrintStream createActivityEnterpriseFeedDataFile() {
        try{
            PrintStream OUTPUT_KEM_TO_GL_FILE_ps = new PrintStream(enterpriseFeedDirectoryName + File.separator + EndowConstants.KemToGLInterfaceBatchProcess.KEM_TO_GL_ACTIVITY_OUTPUT_DATA_FILE + EndowConstants.KemToGLInterfaceBatchProcess.DATA_FILE_SUFFIX);
            return OUTPUT_KEM_TO_GL_FILE_ps;
            
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("processKEMActivityToCreateGLEntries Stopped: " + e1.getMessage(), e1);
        }
    }

    /**
     * create the main reconciliation file in the gl enterpriseFeed folder
     */
    PrintStream createActivityEnterpriseFeedReconcileFile() {
        try{
            PrintStream OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps = new PrintStream(enterpriseFeedDirectoryName + File.separator + EndowConstants.KemToGLInterfaceBatchProcess.KEM_TO_GL_ACTIVITY_OUTPUT_RECONCILE_FILE + EndowConstants.KemToGLInterfaceBatchProcess.RECON_FILE_SUFFIX);
            return OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps;
            
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("processKEMActivityToCreateGLEntries Stopped: " + e1.getMessage(), e1);
        }
    }
    
    /**
     * writeTotalsProcessedDetailTotalsLine method to write details total line.
     * @param documentNumber
     * @param feeMethodCode
     * @param totalLinesGenerated
     */
    protected void writeTotalsProcessedDetailTotalsLine(String documentNumber, String feeMethodCode, int totalLinesGenerated) {
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableRow(gLInterfaceBatchTotalsProcessedTableRowValues);
    //    gLInterfaceBatchTotalProcessedReportsWriterService.writeNewLines(1);

        // add the edoc subtotals to fee method subtotal...
    }
    
    protected void writeTotalsProcessedSubTotalsLine(String feeMethodCode) {
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableRow(gLInterfaceBatchTotalsProcessedTableRowValues);
        gLInterfaceBatchTotalProcessedReportsWriterService.writeNewLines(1);
        
    }
    
    protected void writeTotalsProcessedGrandTotalsLine() {
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableRow(gLInterfaceBatchTotalsProcessedTableRowValues);
    }
    
    /**
     * Writes the exception report line after setting fee method code and kemid and the reason
     * @param feeMethodCode
     * @param kemid
     * @param reason the reason written on the reason line.
     */
    protected void writeExceptionReportLine(String feeMethodCode, String kemid, String reason) {
        writeTableRowAndTableReason(reason);
    }
    
    /**
     * writes out the table row values then writes the reason row and inserts a blank line
     * @param reasonMessage the reason message
     */
    protected void writeTableRowAndTableReason(String reasonMessage) {
        gLInterfaceBatchExceptionReportsWriterService.writeTableRow(gLInterfaceBatchExceptionTableRowValues);            
        setExceptionReportTableRowReason(reasonMessage);
        gLInterfaceBatchExceptionReportsWriterService.writeTableRow(gLInterfaceBatchExceptionRowReason);            
        gLInterfaceBatchExceptionReportsWriterService.writeNewLines(1);
    }
    
    /**
     * sets the exception message with the passed in value.
     * @param reasonForException The reason that will be set in the exception report
     */
    protected void setExceptionReportTableRowReason(String reasonForException) {
    }

    
    /**
     * Gets the gLInterfaceBatchExceptionReportsWriterService attribute. 
     * @return Returns the gLInterfaceBatchExceptionReportsWriterService.
     */
    protected ReportWriterService getgLInterfaceBatchExceptionReportsWriterService() {
        return gLInterfaceBatchExceptionReportsWriterService;
    }
    
    /**
     * Sets the gLInterfaceBatchExceptionReportsWriterService attribute value.
     * @param gLInterfaceBatchExceptionReportsWriterService The gLInterfaceBatchExceptionReportsWriterService to set.
     */
    public void setgLInterfaceBatchExceptionReportsWriterService(ReportWriterService gLInterfaceBatchExceptionReportsWriterService) {
        this.gLInterfaceBatchExceptionReportsWriterService = gLInterfaceBatchExceptionReportsWriterService;
    }

    /**
     * Gets the gLInterfaceBatchTotalProcessedReportsWriterService attribute. 
     * @return Returns the gLInterfaceBatchTotalProcessedReportsWriterService.
     */
    public ReportWriterService getgLInterfaceBatchTotalProcessedReportsWriterService() {
        return gLInterfaceBatchTotalProcessedReportsWriterService;
    }

    /**
     * Sets the gLInterfaceBatchTotalProcessedReportsWriterService attribute value.
     * @param gLInterfaceBatchTotalProcessedReportsWriterService The gLInterfaceBatchTotalProcessedReportsWriterService to set.
     */
    public void setgLInterfaceBatchTotalProcessedReportsWriterService(ReportWriterService gLInterfaceBatchTotalProcessedReportsWriterService) {
        this.gLInterfaceBatchTotalProcessedReportsWriterService = gLInterfaceBatchTotalProcessedReportsWriterService;
    }
    
    /**
     * Gets the gLInterfaceBatchStatisticsReportsWriterService attribute. 
     * @return Returns the gLInterfaceBatchStatisticsReportsWriterService.
     */
    public ReportWriterService getgLInterfaceBatchStatisticsReportsWriterService() {
        return gLInterfaceBatchStatisticsReportsWriterService;
    }

    /**
     * Sets the gLInterfaceBatchStatisticsReportsWriterService attribute value.
     * @param gLInterfaceBatchStatisticsReportsWriterService The gLInterfaceBatchStatisticsReportsWriterService to set.
     */
    public void setgLInterfaceBatchStatisticsReportsWriterService(ReportWriterService gLInterfaceBatchStatisticsReportsWriterService) {
        this.gLInterfaceBatchStatisticsReportsWriterService = gLInterfaceBatchStatisticsReportsWriterService;
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
     * Sets the gLInterfaceBatchExceptionReportHeader attribute value.
     * @param processFeeTransactionsExceptionReportHeader The processFeeTransactionsExceptionReportHeader to set.
     */
    public void setGLInterfaceBatchExceptionReportHeader(GLInterfaceBatchExceptionReportHeader gLInterfaceBatchExceptionReportHeader) {
        this.gLInterfaceBatchExceptionReportHeader = gLInterfaceBatchExceptionReportHeader;
    }

    /**
     * Gets the processFeeTransactionsTotalProcessedReportHeader attribute. 
     * @return Returns the processFeeTransactionsTotalProcessedReportHeader.
     */
    protected GLInterfaceBatchExceptionReportHeader getGLInterfaceBatchExceptionReportHeader() {
        return gLInterfaceBatchExceptionReportHeader;
    }

    /**
     * Sets the gLInterfaceBatchStatisticsReportHeader attribute value.
     * @param gLInterfaceBatchStatisticsReportHeader The gLInterfaceBatchStatisticsReportHeader to set.
     */
    public void setGLInterfaceBatchStatisticsReportHeader(GLInterfaceBatchStatisticsReportHeader gLInterfaceBatchStatisticsReportHeader) {
        this.gLInterfaceBatchStatisticsReportHeader = gLInterfaceBatchStatisticsReportHeader;
    }

    /**
     * Gets the gLInterfaceBatchStatisticsReportHeader attribute. 
     * @return Returns the gLInterfaceBatchStatisticsReportHeader.
     */
    protected GLInterfaceBatchStatisticsReportHeader getGLInterfaceBatchStatisticsReportHeader() {
        return gLInterfaceBatchStatisticsReportHeader;
    }

    /**
     * Gets the gLInterfaceBatchExceptionTableRowValues attribute. 
     * @return Returns the gLInterfaceBatchExceptionTableRowValues.
     */
    protected GLInterfaceBatchExceptionTableRowValues getGLInterfaceBatchExceptionTableRowValues() {
        return gLInterfaceBatchExceptionTableRowValues;
    }

    /**
     * Sets the gLInterfaceBatchExceptionTableRowValues attribute value.
     * @param gLInterfaceBatchExceptionTableRowValues The gLInterfaceBatchExceptionTableRowValues to set.
     */
    public void setGLInterfaceBatchExceptionTableRowValues(GLInterfaceBatchExceptionTableRowValues gLInterfaceBatchExceptionTableRowValues) {
        this.gLInterfaceBatchExceptionTableRowValues = gLInterfaceBatchExceptionTableRowValues;
    }

    /**
     * Gets the gLInterfaceBatchExceptionRowReason attribute. 
     * @return Returns the gLInterfaceBatchExceptionRowReason.
     */
    protected GLInterfaceBatchExceptionTableRowValues getGLInterfaceBatchExceptionRowReason() {
        return gLInterfaceBatchExceptionRowReason;
    }

    /**
     * Sets the gLInterfaceBatchExceptionRowReason attribute value.
     * @param gLInterfaceBatchExceptionRowReason The gLInterfaceBatchExceptionRowReason to set.
     */
    public void setGLInterfaceBatchExceptionRowReason(GLInterfaceBatchExceptionTableRowValues gLInterfaceBatchExceptionRowReason) {
        this.gLInterfaceBatchExceptionRowReason = gLInterfaceBatchExceptionRowReason;
    }
    
    /**
     * Gets the gLInterfaceBatchTotalsProcessedTableRowValues attribute. 
     * @return Returns the gLInterfaceBatchTotalsProcessedTableRowValues.
     */
    protected GLInterfaceBatchTotalsProcessedTableRowValues getGLInterfaceBatchTotalsProcessedTableRowValues() {
        return gLInterfaceBatchTotalsProcessedTableRowValues;
    }

    /**
     * Sets the gLInterfaceBatchTotalsProcessedTableRowValues attribute value.
     * @param gLInterfaceBatchTotalsProcessedTableRowValues The gLInterfaceBatchTotalsProcessedTableRowValues to set.
     */
    public void setGLInterfaceBatchTotalsProcessedTableRowValues(GLInterfaceBatchTotalsProcessedTableRowValues gLInterfaceBatchTotalsProcessedTableRowValues) {
        this.gLInterfaceBatchTotalsProcessedTableRowValues = gLInterfaceBatchTotalsProcessedTableRowValues;
    }
    
    /**
     * Gets the gLInterfaceBatchStatisticsTableRowValues attribute. 
     * @return Returns the gLInterfaceBatchStatisticsTableRowValues.
     */
    protected GLInterfaceBatchStatisticsTableRowValues getGLInterfaceBatchStatisticsTableRowValues() {
        return gLInterfaceBatchStatisticsTableRowValues;
    }

    /**
     * Sets the gLInterfaceBatchStatisticsTableRowValues attribute value.
     * @param gLInterfaceBatchStatisticsTableRowValues The gLInterfaceBatchStatisticsTableRowValues to set.
     */
    public void setGLInterfaceBatchStatisticsTableRowValues(GLInterfaceBatchStatisticsTableRowValues gLInterfaceBatchStatisticsTableRowValues) {
        this.gLInterfaceBatchStatisticsTableRowValues = gLInterfaceBatchStatisticsTableRowValues;
    }
    
    /**
     * Gets the transactionArchiveDao attribute. 
     * @return Returns the transactionArchiveDao.
     */
    protected TransactionArchiveDao getTransactionArchiveDao() {
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
    protected HoldingHistoryDao getHoldingHistoryDao() {
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
    protected CurrentTaxLotBalanceDao getCurrentTaxLotBalanceDao() {
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
    protected KemidFeeDao getKemidFeeDao() {
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
     * Gets the configService attribute. 
     * @return Returns the configService.
     */
    protected KualiConfigurationService getConfigService() {
        return configService;
    }

    /**
     * Sets the configService.
     * @param configService
     */
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
    
    /**
     * This method sets the enterpriseFeedDirectoryName
     * @param enterpriseFeedDirectoryName
     */
    public void setEnterpriseFeedDirectoryName(String enterpriseFeedDirectoryName) {
        this.enterpriseFeedDirectoryName = enterpriseFeedDirectoryName;
    }
}

