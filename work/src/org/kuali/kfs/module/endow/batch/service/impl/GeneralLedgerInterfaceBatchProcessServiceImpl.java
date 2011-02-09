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
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.GeneralLedgerInterfaceBatchProcessStep;
import org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao;
import org.kuali.kfs.module.endow.batch.service.GeneralLedgerInterfaceBatchProcessService;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineBase;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchExceptionTableRowValues;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchStatisticsReportDetailTableRow;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchStatisticsReportHeader;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchStatisticsTableRowValues;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchTotalsProcessedReportHeader;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchTotalsProcessedTableRowValues;
import org.kuali.kfs.module.endow.businessobject.GlInterfaceBatchProcessKemLine;
import org.kuali.kfs.module.endow.dataaccess.EndowmentAccountingLineBaseDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the GeneralLedgerInterfaceBatchProcessService.
 */
@Transactional
public class GeneralLedgerInterfaceBatchProcessServiceImpl implements GeneralLedgerInterfaceBatchProcessService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerInterfaceBatchProcessServiceImpl.class);
    
    protected String enterpriseFeedDirectoryName;
    
    protected KEMService kemService;
    protected TransactionArchiveDao transactionArchiveDao;
    protected EndowmentAccountingLineBaseDao endowmentAccountingLineBaseDao;
    protected GLInterfaceBatchProcessDao gLInterfaceBatchProcessDao;
    protected KualiConfigurationService configService;
    protected ParameterService parameterService;
    
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
    protected GLInterfaceBatchStatisticsReportDetailTableRow gLInterfaceBatchStatisticsReportDetailTableRow;
    
    //the properties to totals processed at chart/object level..sub totals.
    protected BigDecimal chartObjectDebitAmountSubTotal = BigDecimal.ZERO;
    protected BigDecimal chartObjectCreditAmountSubTotal = BigDecimal.ZERO;
    protected long chartObjectNumberOfRecordsSubTotal = 0;
    
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
    
    protected String previousChartCode = null;
    protected String previousObjectCode = null;

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
        gLInterfaceBatchStatisticsReportDetailTableRow = new GLInterfaceBatchStatisticsReportDetailTableRow();
    }

    /**
     * The fee process is intended to provide as much flexibility to the institution as possible when 
     * designing the charges to be assessed against a KEMID.  The fees can be based on either balances 
     * or activity and can be charged, accrued or waived at the KEMID level.
     * @see oorg.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService#processFeeTransactions()\
     * return boolean true if successful else false
     */
    public boolean processKEMActivityToCreateGLEntries() {
        LOG.info("processKEMActivityToCreateGLEntries() started.");
        
        boolean success = true;
        
        writeReportHeaders();
        
        //main job to process KEM activity...
        success = processKEMActivity();
        
        LOG.info("processKEMActivityToCreateGLEntries() exited.");
        
        return success;
    }
    
    /**
     * process the KEM Activity transactions to create gl entries in the enterprise feed file
     */
    public boolean processKEMActivity() {
        LOG.info("processKEMActivity() started.");
        
        boolean success = true;
        
        PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps = createActivityEnterpriseFeedDataFile();
        PrintStream OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps = createActivityEnterpriseFeedReconcileFile();
        
        //Step 1: get records from END_TRAN_ARCH_T for the given posted date
        String combineTransactions = parameterService.getParameterValue(GeneralLedgerInterfaceBatchProcessStep.class, EndowParameterKeyConstants.GLInterfaceBatchProcess.COMBINE_ENDOWMENT_GL_ENTRIES_IND);
        java.util.Date postedDate = kemService.getCurrentDate();
        
        Collection<GlInterfaceBatchProcessKemLine> transactionArchives = new ArrayList();
        
        //get all the available document types names sorted
        Collection<String> documentTypes = gLInterfaceBatchProcessDao.findDocumentTypes();
        
        for (String documentType : documentTypes) {
            transactionArchives = gLInterfaceBatchProcessDao.getAllKemTransactionsByDocumentType(documentType, postedDate);
            if (EndowConstants.YES.equalsIgnoreCase(combineTransactions)) {
                //combine the entries...GL lines based on chart/account/object code
                
            }
            else {
                //single transaction gl lines...
                success = createGlEntriesForTransactionArchives(documentType, transactionArchives, OUTPUT_KEM_TO_GL_DATA_FILE_ps, OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps, postedDate);
            }
            
            if (transactionArchives.size() > 0) {
                //add to the grand total and write the document subtotals....
                
                writeTotalsProcessedChartDetailTotalsLine(); 
                //add to the document level sub-totals....
             //   addChartTotalsToDocumentTypeTotals();
                
                //write document type line...
                writeTotalsProcessedDocumentTypeDetailTotalsLine();
                addDocumentTypeTotalsToGrandTotals();
            }
        }
        
        
        //grand total line...
        writeTotalsProcessedGrandTotalsLine();
        
        OUTPUT_KEM_TO_GL_DATA_FILE_ps.close();
        OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps.close();
        
        LOG.info("processKEMActivity() exited.");
        
        return success;
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
     * For the transaction archives this method creates gl entries into the output file.
     * @param documentType
     * @param transactionArchives transaction archive records sorted by document type name
     * @param OUTPUT_KEM_TO_GL_DATA_FILE_ps GL enterprise feed file
     * @param OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps GL reconciliation enterprise feed file
     * @return success true if enterprise feed files are created, false if files are not created.
     */
    public boolean createGlEntriesForTransactionArchives(String documentType, Collection<GlInterfaceBatchProcessKemLine> transactionArchives, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps, PrintStream OUTPUT_KEM_TO_GL_RECONCILE_FILE_ps, java.util.Date postedDate) {
        boolean success = true;
        
        for (GlInterfaceBatchProcessKemLine transactionArchive : transactionArchives) {
            if (previousChartCode == null && previousObjectCode == null) {
                previousChartCode = transactionArchive.getChartCode();
                previousObjectCode = transactionArchive.getObjectCode();
            }
            if (transactionArchive.getSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.CASH)) {
                //process the cash entry record...
                if (!createCashEntry(transactionArchive, OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate)) {
                    //error exception writing..
                }
            }
            //process non cash
            if (transactionArchive.getSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.NON_CASH)) {
                //process the non-cash entry record...
                if (!createNonCashEntry(transactionArchive, OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate)) {
                    //exception generated...
                }
            }
            // if GLET or EGLT then create unique document number accounting lines gl entries.....
            if (transactionArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_TO_GENERAL_LEDGER_TRANSFER) || 
                    transactionArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.GENERAL_LEDGER_TO_ENDOWMENT_TRANSFER)) {
                //process the GLET or EGLT records..
                if (!createGLEntriesForEGLTOrGLET(transactionArchive, OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate)) {
                    //exception generated...
                }
            }
        }
        
        return success;
    }
    
    /**
     * method to create cash entry gl record
     * @param transactionArchive,OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate
     * @return true if successful, else false
     */
    protected boolean createCashEntry(GlInterfaceBatchProcessKemLine transactionArchive, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps, java.util.Date postedDate) {
        boolean success = true;
        
        OriginEntryFull oef = new OriginEntryFull();
        oef.setChartOfAccountsCode(transactionArchive.getChartCode());
        oef.setAccountNumber(transactionArchive.getAccountNumber());
        oef.setFinancialObjectCode(transactionArchive.getObjectCode());
        oef.setFinancialDocumentTypeCode(transactionArchive.getTypeCode());
        oef.setFinancialSystemOriginationCode(EndowConstants.KemToGLInterfaceBatchProcess.SYSTEM_ORIGINATION_CODE_FOR_ENDOWMENT);
        oef.setDocumentNumber(transactionArchive.getDocumentNumber());
        oef.setTransactionLedgerEntryDescription(getTransactionDescription(transactionArchive, postedDate));
        BigDecimal transactionAmount = getTransactionAmount(transactionArchive);
        oef.setTransactionLedgerEntryAmount(new KualiDecimal(transactionAmount.abs()));
        oef.setTransactionDebitCreditCode(getTransactionDebitCreditCode(transactionAmount));
        
        try {
            createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
            updateTotalsProcessed(transactionArchive);            
        } catch (IOException ioe) {
            return false;
        }
        
        return success;
    }

    /**
     * method to create non-cash entry gl record
     * @param transactionArchive,OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate
     * @return true if successful, else false
     */
    protected boolean createNonCashEntry(GlInterfaceBatchProcessKemLine transactionArchive, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps, java.util.Date postedDate) {
        boolean success = true;
        String lossGainObjectCode = parameterService.getParameterValue(GeneralLedgerInterfaceBatchProcessStep.class, EndowParameterKeyConstants.GLInterfaceBatchProcess.CASH_SALE_GAIN_LOSS_OBJECT_CODE);
        
        OriginEntryFull oef = new OriginEntryFull();
        
        oef.setChartOfAccountsCode(transactionArchive.getChartCode());
        oef.setAccountNumber(transactionArchive.getAccountNumber());
        oef.setFinancialObjectCode(transactionArchive.getObjectCode());
        oef.setFinancialDocumentTypeCode(transactionArchive.getTypeCode());
        oef.setFinancialSystemOriginationCode(EndowConstants.KemToGLInterfaceBatchProcess.SYSTEM_ORIGINATION_CODE_FOR_ENDOWMENT);
        oef.setDocumentNumber(transactionArchive.getDocumentNumber());
        oef.setTransactionLedgerEntryDescription(getTransactionDescription(transactionArchive, postedDate));
        BigDecimal transactionAmount = getTransactionAmount(transactionArchive);
        oef.setTransactionLedgerEntryAmount(new KualiDecimal(transactionAmount.abs()));
        oef.setTransactionDebitCreditCode(getTransactionDebitCreditCode(transactionAmount));
        
        try {
            createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
            updateTotalsProcessed(transactionArchive);             
        } catch (IOException ioe) {
            return false;
        }
        
        //create the offset or (loss/gain entry for EAD) document types where subtype is Non-Cash
        if (transactionArchive.getSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.NON_CASH)) {
            //need to create an offset entry...
            if (transactionArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE)) {
                oef.setFinancialObjectCode(lossGainObjectCode);
                transactionAmount = transactionArchive.getShortTermGainLoss().add(transactionArchive.getLongTermGainLoss());
                oef.setTransactionLedgerEntryAmount(new KualiDecimal(transactionAmount.abs()));
            }
            oef.setTransactionDebitCreditCode(getTransactionDebitCreditCodeForOffSetEntry(transactionAmount));
            try {
                createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
                
                GlInterfaceBatchProcessKemLine transactionArchiveLossGain = new GlInterfaceBatchProcessKemLine();
                transactionArchiveLossGain.setTypeCode(transactionArchive.getTypeCode());
                transactionArchiveLossGain.setChartCode(transactionArchive.getChartCode());
                transactionArchiveLossGain.setObjectCode(lossGainObjectCode);
                transactionArchiveLossGain.setShortTermGainLoss(transactionArchive.getShortTermGainLoss());
                transactionArchiveLossGain.setLongTermGainLoss(transactionArchive.getLongTermGainLoss());
                transactionArchiveLossGain.setSubTypeCode(transactionArchive.getSubTypeCode());
                transactionArchiveLossGain.setHoldingCost(transactionArchive.getHoldingCost());
                transactionArchiveLossGain.setTransactionArchiveIncomeAmount(transactionArchive.getTransactionArchiveIncomeAmount());
                transactionArchiveLossGain.setTransactionArchivePrincipalAmount(transactionArchive.getTransactionArchivePrincipalAmount());
                
                updateTotalsProcessed(transactionArchiveLossGain);                 
            } catch (IOException ioe) {
                return false;
            }
        }
        
        return success;
    }

    /**
     * method to create cash entry gl record for each transaction archive fdoc number
     * @param transactionArchive,OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate
     * @return true if successful, else false
     */
    protected boolean createGLEntriesForEGLTOrGLET(GlInterfaceBatchProcessKemLine transactionArchive, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps, java.util.Date postedDate) {
        boolean success = true;
        
        Collection<EndowmentAccountingLineBase> endowmentAccountingLines = endowmentAccountingLineBaseDao.getAllEndowmentAccountingLines(transactionArchive.getDocumentNumber());
        for (EndowmentAccountingLineBase endowmentAccountingLineBase : endowmentAccountingLines) {
            OriginEntryFull oef = new OriginEntryFull();
            
            oef.setChartOfAccountsCode(endowmentAccountingLineBase.getChartOfAccountsCode());
            oef.setAccountNumber(endowmentAccountingLineBase.getAccountNumber());
            oef.setSubAccountNumber(endowmentAccountingLineBase.getSubAccountNumber());
            oef.setFinancialObjectCode(endowmentAccountingLineBase.getFinancialObjectCode());
            oef.setFinancialSubObjectCode(endowmentAccountingLineBase.getFinancialSubObjectCode());
            oef.setFinancialDocumentTypeCode(transactionArchive.getTypeCode());
            oef.setFinancialSystemOriginationCode(EndowConstants.KemToGLInterfaceBatchProcess.SYSTEM_ORIGINATION_CODE_FOR_ENDOWMENT);
            oef.setDocumentNumber(transactionArchive.getDocumentNumber());
            oef.setTransactionLedgerEntryDescription(getTransactionDescription(transactionArchive, postedDate));
            BigDecimal transactionAmount = getTransactionAmount(transactionArchive);
            oef.setTransactionLedgerEntryAmount(endowmentAccountingLineBase.getAmount());
            if (endowmentAccountingLineBase.getFinancialDocumentLineTypeCode().equalsIgnoreCase(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE)) {
                oef.setTransactionDebitCreditCode(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE);    
            }
            else {
                oef.setTransactionDebitCreditCode(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE);    
            }
            oef.setProjectCode(endowmentAccountingLineBase.getProjectCode());
            oef.setOrganizationReferenceId(endowmentAccountingLineBase.getOrganizationReferenceId());
            
            try {
                createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
                updateTotalsProcessed(transactionArchive);                 
            } catch (IOException ioe) {
                return false;
            }
        } //end of for loop
        
        return success;
    }
    
    /**
     * method to get transaction description
     * @param transactionArchive
     * @return transaction description
     */
    protected String getTransactionDescription(GlInterfaceBatchProcessKemLine transactionArchive, java.util.Date postedDate) {
        String actityType = null;
        
        if (transactionArchive.getSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.CASH)) {
            actityType = EndowConstants.KemToGLInterfaceBatchProcess.SUB_TYPE_CASH;
        }
        else {
            actityType = EndowConstants.KemToGLInterfaceBatchProcess.SUB_TYPE_NON_CASH;   
        }
        
        return ("Net " + transactionArchive.getTypeCode() + " " + actityType + " Activity for " + postedDate.toString());
    }

    /**
     * method to get transaction amount
     * @param transactionArchive
     * @return transaction amount
     */
    protected BigDecimal getTransactionAmount(GlInterfaceBatchProcessKemLine transactionArchive) {
        BigDecimal transactionAmount = BigDecimal.ZERO;
        
        if (transactionArchive.getSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.CASH)) {
            transactionAmount = transactionArchive.getTransactionArchiveIncomeAmount().add(transactionArchive.getTransactionArchivePrincipalAmount());
        }
        else {
            transactionAmount = transactionArchive.getHoldingCost();
        }
        
        return transactionAmount;
    }
    
    /**
     * method to get transaction debit/credit code
     * @param transactionAmount
     * @return transaction debit or credit code
     */
    protected String getTransactionDebitCreditCode(BigDecimal transactionAmount) {
        
        if (transactionAmount.abs().compareTo(BigDecimal.ZERO) == 1) {
            return (EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE);
        }
        else {
            return (EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE);
        }
    }

    /**
     * method to get transaction debit/credit code
     * @param transactionAmount
     * @return transaction debit or credit code
     */
    protected String getTransactionDebitCreditCodeForOffSetEntry(BigDecimal transactionAmount) {
        
        if (transactionAmount.abs().compareTo(BigDecimal.ZERO) == 1) {
            return (EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE);
        }
        else {
            return (EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE);
        }
    }
    
    protected void createOutputEntry(Transaction entry, PrintStream group) throws IOException {
        OriginEntryFull oef = new OriginEntryFull();
        
        oef.copyFieldsFromTransaction(entry);
        oef.setUniversityFiscalYear(null);
        
        try {
            group.printf("%s\n", oef.getLine());
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    protected void writeErrorEntry(String line, PrintStream invaliGroup) throws IOException {
        try {
            invaliGroup.printf("%s\n", line);
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }
    
    protected void updateTotalsProcessed(GlInterfaceBatchProcessKemLine transactionArchive) {
        if (StringUtils.equals(previousChartCode, transactionArchive.getChartCode()) &&
                StringUtils.equals(previousObjectCode, transactionArchive.getObjectCode())) {
            updateTotals(transactionArchive);
        }
        else {
            if (!StringUtils.equals(previousObjectCode, transactionArchive.getObjectCode())) {
                //object code change..reset the total and add the object detail line
                //current detail totals to the sub totals...
                writeTotalsProcessedObjectDetailTotalsLine(transactionArchive.getTypeCode(), previousChartCode, previousObjectCode);
                addTotalsToChartTotals();
                updateTotals(transactionArchive);                
                previousObjectCode = transactionArchive.getObjectCode();
            }
            if (!StringUtils.equals(previousChartCode, transactionArchive.getChartCode())) {
                writeTotalsProcessedChartDetailTotalsLine();
                addChartTotalsToDocumentTypeTotals();
                updateTotals(transactionArchive);                
                previousChartCode = transactionArchive.getChartCode();
                
                writeTotalsProcessedObjectDetailTotalsLine(transactionArchive.getTypeCode(), previousChartCode, previousObjectCode);
                //the properties to totals processed at chart level..sub totals.
                chartDebitAmountSubTotal = chartDebitAmountSubTotal.add(chartObjectDebitAmountSubTotal);
                chartCreditAmountSubTotal = chartCreditAmountSubTotal.add(chartObjectCreditAmountSubTotal);
                chartNumberOfRecordsSubTotal += chartObjectNumberOfRecordsSubTotal;
            }
        }
    }
    /**
     * add debit and credit totals and number of lines processed to detail line.
     */
    protected void updateTotals(GlInterfaceBatchProcessKemLine transactionArchive) {
        BigDecimal totalAmount =  BigDecimal.ZERO;
        String debitCreditCode = null;
        
        String lossGainObjectCode = parameterService.getParameterValue(GeneralLedgerInterfaceBatchProcessStep.class, EndowParameterKeyConstants.GLInterfaceBatchProcess.CASH_SALE_GAIN_LOSS_OBJECT_CODE);
        if (transactionArchive.getObjectCode().equalsIgnoreCase(lossGainObjectCode)) {
            totalAmount = transactionArchive.getShortTermGainLoss().add(transactionArchive.getLongTermGainLoss());
            debitCreditCode = getTransactionDebitCreditCodeForOffSetEntry(totalAmount);           
        }
        else {
            totalAmount = getTransactionAmount(transactionArchive);
            debitCreditCode = getTransactionDebitCreditCode(totalAmount);
        }
        
        if (debitCreditCode.equalsIgnoreCase(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE)) {
            chartObjectDebitAmountSubTotal = chartObjectDebitAmountSubTotal.add(totalAmount.abs());
        }
        if (debitCreditCode.equalsIgnoreCase(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE)) {
            chartObjectCreditAmountSubTotal = chartObjectCreditAmountSubTotal.add(totalAmount.abs());
        }
        chartObjectNumberOfRecordsSubTotal += 1;
    }
    
    
    /**
     * write TotalsProcessedDetailTotalsLine method to write details total line.
     * @param documentNumber
     * @param feeMethodCode
     * @param totalLinesGenerated
     */
    protected void writeTotalsProcessedObjectDetailTotalsLine(String documentTypeCode, String previousChartCode, String previousObjectCode) {
        gLInterfaceBatchTotalsProcessedTableRowValues.setDocumentType(documentTypeCode);
        gLInterfaceBatchTotalsProcessedTableRowValues.setChartCode(previousChartCode);
        gLInterfaceBatchTotalsProcessedTableRowValues.setObjectCode(previousObjectCode);
        gLInterfaceBatchTotalsProcessedTableRowValues.setDebitAmount(new KualiDecimal(chartObjectDebitAmountSubTotal));
        gLInterfaceBatchTotalsProcessedTableRowValues.setCreditAmount(new KualiDecimal(chartObjectCreditAmountSubTotal));
        gLInterfaceBatchTotalsProcessedTableRowValues.setNumberOfEntries(chartObjectNumberOfRecordsSubTotal);
        
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableRow(gLInterfaceBatchTotalsProcessedTableRowValues);
    }
    
    /**
     * write chart TotalsProcessedDetailTotalsLine method to write details total line.
     */
    protected void writeTotalsProcessedChartDetailTotalsLine() {
        gLInterfaceBatchTotalsProcessedTableRowValues.setDocumentType(null);
        gLInterfaceBatchTotalsProcessedTableRowValues.setChartCode("Subtotal by Chart");
        gLInterfaceBatchTotalsProcessedTableRowValues.setObjectCode(null);
        gLInterfaceBatchTotalsProcessedTableRowValues.setDebitAmount(new KualiDecimal(chartDebitAmountSubTotal));
        gLInterfaceBatchTotalsProcessedTableRowValues.setCreditAmount(new KualiDecimal(chartCreditAmountSubTotal));
        gLInterfaceBatchTotalsProcessedTableRowValues.setNumberOfEntries(chartNumberOfRecordsSubTotal);
        
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableRow(gLInterfaceBatchTotalsProcessedTableRowValues);
        gLInterfaceBatchTotalProcessedReportsWriterService.writeNewLines(1);
    }

    /**
     * write document type TotalsProcessedDetailTotalsLine method to write details total line.
     * @param documentNumber
     * @param feeMethodCode
     * @param totalLinesGenerated
     */
    protected void writeTotalsProcessedDocumentTypeDetailTotalsLine() {
        gLInterfaceBatchTotalsProcessedTableRowValues.setDocumentType("Subtotal by Document Type");
        gLInterfaceBatchTotalsProcessedTableRowValues.setChartCode(null);
        gLInterfaceBatchTotalsProcessedTableRowValues.setObjectCode(null);
        gLInterfaceBatchTotalsProcessedTableRowValues.setDebitAmount(new KualiDecimal(documentTypeDebitAmountSubTotal));
        gLInterfaceBatchTotalsProcessedTableRowValues.setCreditAmount(new KualiDecimal(documentTypeCreditAmountSubTotal));
        gLInterfaceBatchTotalsProcessedTableRowValues.setNumberOfEntries(documentTypeNumberOfRecordsSubTotal);
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableRow(gLInterfaceBatchTotalsProcessedTableRowValues);
        gLInterfaceBatchTotalProcessedReportsWriterService.writeNewLines(1);
    }
    
    /**
     * write document TotalsProcessedDetailTotalsLine method to write details total line.
     * @param documentNumber
     * @param feeMethodCode
     * @param totalLinesGenerated
     */
    protected void writeTotalsProcessedGrandTotalsLine() {
        gLInterfaceBatchTotalsProcessedTableRowValues.setDocumentType("Grand Total");
        gLInterfaceBatchTotalsProcessedTableRowValues.setChartCode(null);
        gLInterfaceBatchTotalsProcessedTableRowValues.setObjectCode(null);
        gLInterfaceBatchTotalsProcessedTableRowValues.setDebitAmount(new KualiDecimal(documentTypeDebitAmountGrandTotal));
        gLInterfaceBatchTotalsProcessedTableRowValues.setCreditAmount(new KualiDecimal(documentTypeCreditAmountGrandTotal));
        gLInterfaceBatchTotalsProcessedTableRowValues.setNumberOfEntries(documentTypeNumberOfRecordsGrandTotal);
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableRow(gLInterfaceBatchTotalsProcessedTableRowValues);
        gLInterfaceBatchTotalProcessedReportsWriterService.writeNewLines(1);
    }
    
    /**
     * add total to grand totals and reset document level totals...
     */
    protected void addDocumentTypeTotalsToGrandTotals() {
        //the properties to totals processed at Document Type level..sub totals.
        documentTypeDebitAmountGrandTotal = documentTypeDebitAmountGrandTotal.add(documentTypeDebitAmountSubTotal);
        documentTypeCreditAmountGrandTotal = documentTypeCreditAmountGrandTotal.add(documentTypeCreditAmountSubTotal);
        documentTypeNumberOfRecordsGrandTotal += documentTypeNumberOfRecordsSubTotal;    

        documentTypeDebitAmountSubTotal = BigDecimal.ZERO;
        documentTypeCreditAmountSubTotal = BigDecimal.ZERO;
        documentTypeNumberOfRecordsSubTotal = 0;
    }
    
    /**
     * add totals to 
     */
    protected void addChartTotalsToDocumentTypeTotals() {
        //add to the document level subtotals....
        documentTypeDebitAmountSubTotal = documentTypeDebitAmountSubTotal.add(chartDebitAmountSubTotal);
        documentTypeCreditAmountSubTotal = documentTypeCreditAmountSubTotal.add(chartCreditAmountSubTotal);
        documentTypeNumberOfRecordsSubTotal += chartNumberOfRecordsSubTotal;
        
        //the properties to totals processed at chart level..sub totals.
        chartDebitAmountSubTotal = BigDecimal.ZERO;
        chartCreditAmountSubTotal = BigDecimal.ZERO;
        chartNumberOfRecordsSubTotal = 0;
        
        //reset the object level totals...
        chartObjectDebitAmountSubTotal = BigDecimal.ZERO;
        chartObjectCreditAmountSubTotal = BigDecimal.ZERO;
        chartObjectNumberOfRecordsSubTotal = 0;
        
    }
    
    protected void addTotalsToChartTotals() {
        //the properties to totals processed at chart level..sub totals.
        chartDebitAmountSubTotal = chartDebitAmountSubTotal.add(chartObjectDebitAmountSubTotal);
        chartCreditAmountSubTotal = chartCreditAmountSubTotal.add(chartObjectCreditAmountSubTotal);
        chartNumberOfRecordsSubTotal += chartObjectNumberOfRecordsSubTotal;

        //reset the object level totals...
        chartObjectDebitAmountSubTotal = BigDecimal.ZERO;
        chartObjectCreditAmountSubTotal = BigDecimal.ZERO;
        chartObjectNumberOfRecordsSubTotal = 0;
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
     * Gets the gLInterfaceBatchStatisticsReportDetailTableRow attribute. 
     * @return Returns the gLInterfaceBatchStatisticsReportDetailTableRow.
     */
    protected GLInterfaceBatchStatisticsReportDetailTableRow getGLInterfaceBatchStatisticsReportDetailTableRow() {
        return gLInterfaceBatchStatisticsReportDetailTableRow;
    }

    /**
     * Sets the gLInterfaceBatchStatisticsReportDetailTableRow attribute value.
     * @param gLInterfaceBatchStatisticsReportDetailTableRow The gLInterfaceBatchStatisticsTableRowValues to set.
     */
    public void setGLInterfaceBatchStatisticsReportDetailTableRow(GLInterfaceBatchStatisticsReportDetailTableRow gLInterfaceBatchStatisticsReportDetailTableRow) {
        this.gLInterfaceBatchStatisticsReportDetailTableRow = gLInterfaceBatchStatisticsReportDetailTableRow;
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
     * Gets the gLInterfaceBatchProcessDao attribute. 
     * @return Returns the gLInterfaceBatchProcessDao.
     */
 //   protected GLInterfaceBatchProcessDao getGLInterfaceBatchProcessDao() {
 //       return gLInterfaceBatchProcessDao;
 //   }

    /**
     * Sets the gLInterfaceBatchProcessDao attribute value.
     * @param gLInterfaceBatchProcessDao The gLInterfaceBatchProcessDao to set.
     */
 //   public void setGLInterfaceBatchProcessDao(GLInterfaceBatchProcessDao gLInterfaceBatchProcessDao) {
 //       this.gLInterfaceBatchProcessDao = gLInterfaceBatchProcessDao;
 //   }

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

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */    
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /**
     * Gets the endowmentAccountingLineBaseDao attribute. 
     * @return Returns the endowmentAccountingLineBaseDao.
     */
    protected EndowmentAccountingLineBaseDao getEndowmentAccountingLineBaseDao() {
        return endowmentAccountingLineBaseDao;
    }

    /**
     * Sets the endowmentAccountingLineBaseDao attribute value.
     * @param endowmentAccountingLineBaseDao The endowmentAccountingLineBaseDao to set.
     */
    public void setEndowmentAccountingLineBaseDao(EndowmentAccountingLineBaseDao endowmentAccountingLineBaseDao) {
        this.endowmentAccountingLineBaseDao = endowmentAccountingLineBaseDao;
    }
    
    /**
     * Gets the gLInterfaceBatchProcessDao attribute. 
     * @return Returns the gLInterfaceBatchProcessDao.
     */
    public GLInterfaceBatchProcessDao getgLInterfaceBatchProcessDao() {
        return gLInterfaceBatchProcessDao;
    }

    /**
     * Sets the gLInterfaceBatchProcessDao attribute value.
     * @param gLInterfaceBatchProcessDao The gLInterfaceBatchProcessDao to set.
     */
    public void setgLInterfaceBatchProcessDao(GLInterfaceBatchProcessDao gLInterfaceBatchProcessDao) {
        this.gLInterfaceBatchProcessDao = gLInterfaceBatchProcessDao;
    }
    
}

