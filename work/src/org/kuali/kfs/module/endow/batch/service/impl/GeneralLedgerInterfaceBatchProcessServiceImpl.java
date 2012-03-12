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
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchTotalsProcessedReportHeader;
import org.kuali.kfs.module.endow.businessobject.GLInterfaceBatchTotalsProcessedTableRowValues;
import org.kuali.kfs.module.endow.businessobject.GlInterfaceBatchProcessKemLine;
import org.kuali.kfs.module.endow.dataaccess.EndowmentAccountingLineBaseDao;
import org.kuali.kfs.module.endow.dataaccess.GLLinkDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the GeneralLedgerInterfaceBatchProcessService.
 */
@Transactional
public class GeneralLedgerInterfaceBatchProcessServiceImpl implements GeneralLedgerInterfaceBatchProcessService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerInterfaceBatchProcessServiceImpl.class);

    protected String batchFileDirectoryName;

    protected KEMService kemService;
    protected GLLinkDao gLLinkDao;
    protected TransactionArchiveDao transactionArchiveDao;
    protected EndowmentAccountingLineBaseDao endowmentAccountingLineBaseDao;
    protected GLInterfaceBatchProcessDao gLInterfaceBatchProcessDao;
    protected ParameterService parameterService;

    //report writer services for statistics, totals processed, and exception reports
    protected ReportWriterService gLInterfaceBatchStatisticsReportsWriterService;
    protected ReportWriterService gLInterfaceBatchTotalProcessedReportsWriterService;
    protected ReportWriterService gLInterfaceBatchExceptionReportsWriterService;

    //report headers..bos
    protected GLInterfaceBatchExceptionReportHeader gLInterfaceBatchExceptionReportHeader;
    protected GLInterfaceBatchTotalsProcessedReportHeader gLInterfaceBatchTotalProcessedReportHeader;

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

    protected String previousDocumentTypeCode = null;
    protected String previousChartCode = null;
    protected String previousObjectCode = null;

    protected boolean documentTypeWritten = false ;
    protected boolean statisticsHeaderWritten = false;

    /**
     * Constructs a GeneralLedgerInterfaceBatchProcessServiceImpl instance
     */
    public GeneralLedgerInterfaceBatchProcessServiceImpl() {
        //report writer headers
        gLInterfaceBatchExceptionReportHeader = new GLInterfaceBatchExceptionReportHeader();
        gLInterfaceBatchTotalProcessedReportHeader = new GLInterfaceBatchTotalsProcessedReportHeader();

        //exception report detail and a reason lines.
        gLInterfaceBatchExceptionTableRowValues = new GLInterfaceBatchExceptionTableRowValues();
        gLInterfaceBatchExceptionRowReason = new GLInterfaceBatchExceptionTableRowValues();

        //Totals processed report....This one will be used to write all the rows in totals procesed report.
        gLInterfaceBatchTotalsProcessedTableRowValues = new GLInterfaceBatchTotalsProcessedTableRowValues();

        //statistics report...
        gLInterfaceBatchStatisticsReportDetailTableRow = new GLInterfaceBatchStatisticsReportDetailTableRow();
    }

    /**
     * The process is intended to serve to consolidate KEM activity for the day into
     * valid general ledger debits and credits to update the institution's records.
     * @see oorg.kuali.kfs.module.endow.batch.service.GeneralLedgerInterfaceBatchProcessService#processKEMActivityToCreateGLEntries
     * return boolean true if successful else false
     */
    @Override
    public boolean processKEMActivityToCreateGLEntries() {
        LOG.debug("processKEMActivityToCreateGLEntries() started.");

        boolean success = true;

        writeReportHeaders();

        //main job to process KEM activity...
        success = processKEMActivity();

        LOG.debug("processKEMActivityToCreateGLEntries() exited.");

        return success;
    }

    /**
     * process the KEM Activity transactions to create gl entries in the origin entry file
     */
    public boolean processKEMActivity() {
        LOG.debug("processKEMActivity() started.");

        boolean success = true;
        previousDocumentTypeCode = null;
        previousChartCode = null;
        previousObjectCode = null;

        PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps = createActivityOriginEntryFullStream();

        String combineTransactions = parameterService.getParameterValueAsString(GeneralLedgerInterfaceBatchProcessStep.class, EndowParameterKeyConstants.GLInterfaceBatchProcess.COMBINE_ENDOWMENT_GL_ENTRIES_IND);
        java.util.Date postedDate = kemService.getCurrentDate();

        Collection<GLInterfaceBatchStatisticsReportDetailTableRow> statisticsReportRows = new ArrayList<GLInterfaceBatchStatisticsReportDetailTableRow>();

        Collection<GlInterfaceBatchProcessKemLine> transactionArchives = new ArrayList<GlInterfaceBatchProcessKemLine>();

        //get all the available document types names sorted
        Collection<String> documentTypes = gLInterfaceBatchProcessDao.findDocumentTypes();

        for (String documentType : documentTypes) {
            if (!EndowConstants.DocumentTypeNames.ENDOWMENT_CORPUS_ADJUSTMENT.equalsIgnoreCase(documentType) &&
                    !EndowConstants.DocumentTypeNames.ENDOWMENT_UNIT_SHARE_ADJUSTMENT.equalsIgnoreCase(documentType)) {
                //add a new statisticsReportRow to the collection...
                GLInterfaceBatchStatisticsReportDetailTableRow statisticsDataRow = new GLInterfaceBatchStatisticsReportDetailTableRow();
                statisticsDataRow.setDocumentType(documentType);

                if (EndowConstants.YES.equalsIgnoreCase(combineTransactions)) {
                    //combine the entries...GL lines based on chart/account/object code
                    transactionArchives = gLInterfaceBatchProcessDao.getAllKemCombinedTransactionsByDocumentType(documentType, postedDate);
                }
                else {
                    //single transaction gl lines...
                    transactionArchives = gLInterfaceBatchProcessDao.getAllKemTransactionsByDocumentType(documentType, postedDate);
                }

                if (transactionArchives.size() > 0) {
                    if (previousDocumentTypeCode == null) {
                        previousDocumentTypeCode = documentType;
                    }
                    success = createGlEntriesForTransactionArchives(documentType, transactionArchives, OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate, statisticsDataRow);
                }

                //add statistics row to the collection
                statisticsReportRows.add(statisticsDataRow);

                if (transactionArchives.size() > 0) {
                    processDocumentTypeTotals();
                }
            }
        }

        //grand total line...
        writeTotalsProcessedGrandTotalsLine();

        OUTPUT_KEM_TO_GL_DATA_FILE_ps.close();

        //write the statistics report now...
        writeStatisticsReport(statisticsReportRows);

        LOG.debug("processKEMActivity() exited.");

        return success;
    }

    /**
     * Writes the reports headers for totals processed, waived and accrued fee, and exceptions reports.
     */
    protected void writeReportHeaders() {
        //writes the exception report header
        gLInterfaceBatchExceptionReportsWriterService.writeNewLines(1);
        gLInterfaceBatchExceptionReportsWriterService.writeTableHeader(gLInterfaceBatchExceptionReportHeader);

        //writes the Totals Processed report header....
        gLInterfaceBatchTotalProcessedReportsWriterService.writeNewLines(1);
        gLInterfaceBatchTotalProcessedReportsWriterService.writeTableHeader(gLInterfaceBatchTotalProcessedReportHeader);
    }

    /**
     * create the main data file in the stating\gl\originEntry folder
     */
    PrintStream createActivityOriginEntryFullStream() {
        try{
            PrintStream OUTPUT_KEM_TO_GL_FILE_ps = new PrintStream(batchFileDirectoryName + File.separator + EndowConstants.KemToGLInterfaceBatchProcess.KEM_TO_GL_ACTIVITY_OUTPUT_DATA_FILE + EndowConstants.KemToGLInterfaceBatchProcess.DATA_FILE_SUFFIX);
            return OUTPUT_KEM_TO_GL_FILE_ps;

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("createActivityOriginEntryFullStream Stopped: " + e1.getMessage(), e1);
        }
    }

    /**
     * For the transaction archives this method creates GL entries into the output file.
     * @param documentType
     * @param transactionArchives transaction archive records sorted by document type name
     * @param postedDate
     * @param statisticsDataRow to collect the statistics about GL entries, exception entries
     * @param OUTPUT_KEM_TO_GL_DATA_FILE_ps GL origin entry  file
     * @return success true if origin entry  files are created, false if files are not created.
     */
    public boolean createGlEntriesForTransactionArchives(String documentType, Collection<GlInterfaceBatchProcessKemLine> transactionArchives,
                                                         PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps,
                                                         java.util.Date postedDate, GLInterfaceBatchStatisticsReportDetailTableRow statisticsDataRow) {
        boolean success = true;

        for (GlInterfaceBatchProcessKemLine transactionArchive : transactionArchives) {
            if (previousChartCode == null && previousObjectCode == null) {
                previousChartCode = transactionArchive.getChartCode();
                previousObjectCode = transactionArchive.getObjectCode();
            }
            //process cash activity
            if (transactionArchive.getSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.CASH)) {
                //process the cash entry record...
                success &= createCashEntry(transactionArchive, OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate, statisticsDataRow);
            }

            //process non-cash activity
            if (transactionArchive.getSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.NON_CASH)) {
                //process the non-cash entry record...
                success &= createNonCashEntry(transactionArchive, OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate, statisticsDataRow);
            }

            // if GLET or EGLT then create unique document number accounting lines gl entries.....
            if (transactionArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_TO_GENERAL_LEDGER_TRANSFER) ||
                    transactionArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.GENERAL_LEDGER_TO_ENDOWMENT_TRANSFER)) {
                //process the GLET or EGLT records..
                success &= createGLEntriesForEGLTOrGLET(transactionArchive, OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate, statisticsDataRow);
            }
        }

        return success;
    }

    /**
     * method to create cash entry GL record
     * @param transactionArchive,OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate, statisticsDataRow
     * @return true if successful, else false
     */
    protected boolean createCashEntry(GlInterfaceBatchProcessKemLine transactionArchive, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps,
                                      java.util.Date postedDate, GLInterfaceBatchStatisticsReportDetailTableRow statisticsDataRow) {
        boolean success = true;

        OriginEntryFull oef = createOriginEntryFull(transactionArchive, postedDate, statisticsDataRow);

        try {
            createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
            statisticsDataRow.increaseGLEntriesGeneratedCount();
            updateTotalsProcessed(transactionArchive);
        } catch (Exception ex) {
            //write the error details to the exception report...
            statisticsDataRow.increaseNumberOfExceptionsCount();
            writeExceptionRecord(transactionArchive, ex.getMessage());
            return false;
        }

        //need to create an net gain/loss entry...if document type name = EAD....
        if (transactionArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE)) {
            if ((transactionArchive.getLongTermGainLoss().add(transactionArchive.getShortTermGainLoss())).compareTo(BigDecimal.ZERO) != 0) {
                success = createGainLossEntry(oef, transactionArchive, OUTPUT_KEM_TO_GL_DATA_FILE_ps, statisticsDataRow);
            }
        }

        return success;
    }

    /**
     * method to create non-cash entry GL record
     * @param transactionArchive,OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate
     * @return true if successful, else false
     */
    protected boolean createNonCashEntry(GlInterfaceBatchProcessKemLine transactionArchive, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps, java.util.Date postedDate, GLInterfaceBatchStatisticsReportDetailTableRow statisticsDataRow) {
        boolean success = true;

        OriginEntryFull oef = createOriginEntryFull(transactionArchive, postedDate, statisticsDataRow);
        BigDecimal transactionAmount = getTransactionAmount(transactionArchive);

        try {
            createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
            statisticsDataRow.increaseGLEntriesGeneratedCount();
            updateTotalsProcessed(transactionArchive);
        } catch (Exception ex) {
            //write the error details to the exception report...
            statisticsDataRow.increaseNumberOfExceptionsCount();
            writeExceptionRecord(transactionArchive, ex.getMessage());
            return false;
        }

        //create the offset or (loss/gain entry for EAD) document types where subtype is Non-Cash
        if (!EndowConstants.DocumentTypeNames.ENDOWMENT_CORPORATE_REORGANZATION.equalsIgnoreCase(transactionArchive.getTypeCode())) {
            success = createOffsetEntry(oef, transactionArchive, OUTPUT_KEM_TO_GL_DATA_FILE_ps, statisticsDataRow);
        }

        return success;
    }

    /**
     * method to create a gain/loss record when document type = EAD...
     * @param oef OriginEntryFull
     * @param transactionArchive
     * @param OUTPUT_KEM_TO_GL_DATA_FILE_ps the output file
     * @param statisticsDataRow
     * @return success true if successfully created offset or gain loss entry else false
     */
    protected boolean createGainLossEntry(OriginEntryFull oef, GlInterfaceBatchProcessKemLine transactionArchive, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps, GLInterfaceBatchStatisticsReportDetailTableRow statisticsDataRow) {
        boolean success = true;

        //need to create an net gain/loss entry...if document type name = EAD....
        if (transactionArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE)) {
            oef.setFinancialObjectCode(parameterService.getParameterValueAsString(GeneralLedgerInterfaceBatchProcessStep.class, EndowParameterKeyConstants.GLInterfaceBatchProcess.CASH_SALE_GAIN_LOSS_OBJECT_CODE));
            BigDecimal transactionAmount = transactionArchive.getShortTermGainLoss().add(transactionArchive.getLongTermGainLoss());
            oef.setTransactionLedgerEntryAmount(new KualiDecimal(transactionAmount.abs()));
            oef.setTransactionDebitCreditCode(getTransactionDebitCreditCodeForOffSetEntry(transactionAmount));
            try {
                createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
                statisticsDataRow.increaseGLEntriesGeneratedCount();

                GlInterfaceBatchProcessKemLine transactionArchiveLossGain = new GlInterfaceBatchProcessKemLine();
                transactionArchiveLossGain.setTypeCode(transactionArchive.getTypeCode());
                transactionArchiveLossGain.setChartCode(transactionArchive.getChartCode());
                transactionArchiveLossGain.setObjectCode(oef.getFinancialObjectCode());
                transactionArchiveLossGain.setShortTermGainLoss(transactionArchive.getShortTermGainLoss());
                transactionArchiveLossGain.setLongTermGainLoss(transactionArchive.getLongTermGainLoss());
                transactionArchiveLossGain.setSubTypeCode(transactionArchive.getSubTypeCode());
                transactionArchiveLossGain.setHoldingCost(transactionArchive.getHoldingCost());
                transactionArchiveLossGain.setTransactionArchiveIncomeAmount(transactionArchive.getTransactionArchiveIncomeAmount());
                transactionArchiveLossGain.setTransactionArchivePrincipalAmount(transactionArchive.getTransactionArchivePrincipalAmount());

                updateTotalsProcessed(transactionArchiveLossGain);
            } catch (Exception ex) {
                //write the error details to the exception report...
                statisticsDataRow.increaseNumberOfExceptionsCount();
                writeExceptionRecord(transactionArchive, ex.getMessage());
                success = false;
            }
        }

        return success;
    }

    /**
     * method to create an offset record when document type is NON-CASH
     * @param oef OriginEntryFull
     * @param transactionArchive
     * @param OUTPUT_KEM_TO_GL_DATA_FILE_ps the output file
     * @param statisticsDataRow
     * @return success true if successfully created offset or gain loss entry else false
     */
    protected boolean createOffsetEntry(OriginEntryFull oef, GlInterfaceBatchProcessKemLine transactionArchive, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps, GLInterfaceBatchStatisticsReportDetailTableRow statisticsDataRow) {
        boolean success = true;

        oef.setFinancialObjectCode(transactionArchive.getNonCashOffsetObjectCode());

        BigDecimal transactionAmount = transactionArchive.getHoldingCost();
        oef.setTransactionLedgerEntryAmount(new KualiDecimal(transactionAmount.abs()));
        oef.setTransactionDebitCreditCode(getTransactionDebitCreditCodeForOffSetEntry(transactionAmount));
        try {
            createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
            statisticsDataRow.increaseGLEntriesGeneratedCount();

            GlInterfaceBatchProcessKemLine transactionArchiveLossGain = new GlInterfaceBatchProcessKemLine();
            transactionArchiveLossGain.setTypeCode(transactionArchive.getTypeCode());
            transactionArchiveLossGain.setChartCode(transactionArchive.getChartCode());
            transactionArchiveLossGain.setObjectCode(oef.getFinancialObjectCode());
            transactionArchiveLossGain.setShortTermGainLoss(transactionArchive.getShortTermGainLoss());
            transactionArchiveLossGain.setLongTermGainLoss(transactionArchive.getLongTermGainLoss());
            transactionArchiveLossGain.setSubTypeCode(transactionArchive.getSubTypeCode());
            transactionArchiveLossGain.setHoldingCost(transactionArchive.getHoldingCost());
            transactionArchiveLossGain.setTransactionArchiveIncomeAmount(transactionArchive.getTransactionArchiveIncomeAmount());
            transactionArchiveLossGain.setTransactionArchivePrincipalAmount(transactionArchive.getTransactionArchivePrincipalAmount());

            updateTotalsProcessed(transactionArchiveLossGain);
        } catch (Exception ex) {
            //write the error details to the exception report...
            statisticsDataRow.increaseNumberOfExceptionsCount();
            writeExceptionRecord(transactionArchive, ex.getMessage());
            success = false;
        }

        return success;
    }

    /**
     * method to create cash entry GL record for each transaction archive financial doc number
     * @param transactionArchive,OUTPUT_KEM_TO_GL_DATA_FILE_ps, postedDate
     * @return true if successful, else false
     */
    protected boolean createGLEntriesForEGLTOrGLET(GlInterfaceBatchProcessKemLine transactionArchive, PrintStream OUTPUT_KEM_TO_GL_DATA_FILE_ps, java.util.Date postedDate, GLInterfaceBatchStatisticsReportDetailTableRow statisticsDataRow) {
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
                oef.setTransactionDebitCreditCode(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE);
            }
            else {
                oef.setTransactionDebitCreditCode(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE);
            }
            oef.setProjectCode(endowmentAccountingLineBase.getProjectCode());
            oef.setOrganizationReferenceId(endowmentAccountingLineBase.getOrganizationReferenceId());

            try {
                createOutputEntry(oef, OUTPUT_KEM_TO_GL_DATA_FILE_ps);
                statisticsDataRow.increaseGLEntriesGeneratedCount();
                updateTotalsProcessed(transactionArchive);
            } catch (Exception ex) {
                //write the error details to the exception report...
                statisticsDataRow.increaseNumberOfExceptionsCount();
                writeExceptionRecord(transactionArchive, ex.getMessage());
                return false;
            }
        } //end of for loop

        return success;
    }

    /**
     * method to create origin entry and populate the fields
     * @param transactionArchive, postedDate
     * @return oef
     */
    protected OriginEntryFull createOriginEntryFull(GlInterfaceBatchProcessKemLine transactionArchive,
                                                    java.util.Date postedDate, GLInterfaceBatchStatisticsReportDetailTableRow statisticsDataRow) {

        OriginEntryFull oef = new OriginEntryFull();

        try {
            oef.setChartOfAccountsCode(transactionArchive.getChartCode());
            oef.setAccountNumber(transactionArchive.getAccountNumber());
            oef.setFinancialObjectCode(transactionArchive.getObjectCode());
            oef.setFinancialDocumentTypeCode(transactionArchive.getTypeCode());
            oef.setFinancialSystemOriginationCode(EndowConstants.KemToGLInterfaceBatchProcess.SYSTEM_ORIGINATION_CODE_FOR_ENDOWMENT);
            oef.setDocumentNumber(transactionArchive.getDocumentNumber());
            oef.setTransactionLedgerEntryDescription(getTransactionDescription(transactionArchive, postedDate));
            BigDecimal transactionAmount = getTransactionAmount(transactionArchive);
            oef.setTransactionLedgerEntryAmount(new KualiDecimal(transactionAmount.abs()));
            oef.setTransactionDebitCreditCode(getTransactionDebitCreditCode(transactionArchive.getTypeCode(), transactionAmount, transactionArchive.getSubTypeCode()));
        }
        catch (Exception ex) {
            statisticsDataRow.increaseNumberOfExceptionsCount();
            writeExceptionRecord(transactionArchive, ex.getMessage());
        }

        return oef;
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

        return ("Net " + transactionArchive.getTypeCode() + " " + actityType + " Activity on " + postedDate.toString());
    }

    /**
     * method to get transaction amount
     * @param transactionArchive
     * @return transaction amount
     */
    protected BigDecimal getTransactionAmount(GlInterfaceBatchProcessKemLine transactionArchive) {
        BigDecimal transactionAmount = BigDecimal.ZERO;

        if (transactionArchive.getSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.CASH)) {
            if (transactionArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE)) {
                transactionAmount = transactionArchive.getHoldingCost();
            }
            else {
                transactionAmount = transactionArchive.getTransactionArchiveIncomeAmount().add(transactionArchive.getTransactionArchivePrincipalAmount());
            }
        }
        else {
            transactionAmount = transactionArchive.getHoldingCost();
        }

        return transactionAmount;
    }

    /**
     * method to get transaction debit/credit code
     * @param transactionAmount
     * @param subTypeCode
     * @return transaction debit or credit code
     */
    protected String getTransactionDebitCreditCode(String documentType, BigDecimal transactionAmount, String subTypeCode) {
        if (subTypeCode.equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.CASH)) {
            if (transactionAmount.compareTo(BigDecimal.ZERO) == 1) {
                if (EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE.equalsIgnoreCase(documentType)) {
                    return (EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE);
                } else {
                    return (EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE);
                }
            }
            else {
                if (EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE.equalsIgnoreCase(documentType)) {
                    return (EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE);
                }
                else {
                    return (EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE);
                }
            }
        }
        else {
            if (transactionAmount.compareTo(BigDecimal.ZERO) == 1) {
                return (EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE);
            }
            else {
                return (EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE);
            }
        }
    }

    /**
     * method to get transaction debit/credit code
     * @param transactionAmount
     * @return transaction debit or credit code
     */
    protected String getTransactionDebitCreditCodeForOffSetEntry(BigDecimal transactionAmount) {
        if (transactionAmount.compareTo(BigDecimal.ZERO) == 1) {
            return (EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE);
        }
        else {
            return (EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE);
        }
    }

    protected void createOutputEntry(Transaction entry, PrintStream group) throws IOException {
        OriginEntryFull oef = new OriginEntryFull();

        oef.copyFieldsFromTransaction(entry);
        oef.setUniversityFiscalYear(null);

        try {
            group.printf("%s\n", oef.getLine());
        }
        catch (Exception ex) {
            throw new IOException(ex.toString());
        }
    }

    /**
     * method to update the totals processed amount variables.
     * If same chart and object code then update totals, else
     * if chart is different then first write out the object level totals line, then add the
     * chart totals and then write the chart level totals line.  If only different object code
     * then write the object code level totals line and then add the totals to the chart
     * level totals.
     * @param transactionArchive
     */
    protected void updateTotalsProcessed(GlInterfaceBatchProcessKemLine transactionArchive) {
        if (StringUtils.equals(previousChartCode, transactionArchive.getChartCode()) &&
                StringUtils.equals(previousObjectCode, transactionArchive.getObjectCode())) {
            updateTotals(transactionArchive);
        }
        else {
            if (!StringUtils.equals(previousChartCode, transactionArchive.getChartCode())) {
                writeTotalsProcessedObjectDetailTotalsLine(previousDocumentTypeCode, previousChartCode, previousObjectCode);
                addTotalsToChartTotals();
                previousObjectCode = transactionArchive.getObjectCode();

                updateTotals(transactionArchive);

                writeTotalsProcessedChartDetailTotalsLine();
                addChartTotalsToDocumentTypeTotals();
                updateTotals(transactionArchive);
                previousChartCode = transactionArchive.getChartCode();
            }
            else {
                if (!StringUtils.equals(previousObjectCode, transactionArchive.getObjectCode())) {
                    //object code change..reset the total and add the object detail line
                    //current detail totals to the sub totals...
                    writeTotalsProcessedObjectDetailTotalsLine(previousDocumentTypeCode, previousChartCode, previousObjectCode);
                    addTotalsToChartTotals();
                    previousObjectCode = transactionArchive.getObjectCode();

                    updateTotals(transactionArchive);
                }
            }
        }
    }
    /**
     * add debit and credit totals and number of lines processed to detail line.
     * If object code equals to loss/gain object code, then total is short term loss/gai added to
     * long term loss/gain amount else total amount is retrieved by calling
     * method getTransactionAmount.  The debit or credit code is determined based on
     * the totalAmount value.
     * @param transactionArchive
     */
    protected void updateTotals(GlInterfaceBatchProcessKemLine transactionArchive) {
        BigDecimal totalAmount =  BigDecimal.ZERO;
        String debitCreditCode = null;

        String lossGainObjectCode = parameterService.getParameterValueAsString(GeneralLedgerInterfaceBatchProcessStep.class, EndowParameterKeyConstants.GLInterfaceBatchProcess.CASH_SALE_GAIN_LOSS_OBJECT_CODE);
        if (transactionArchive.getObjectCode().equalsIgnoreCase(lossGainObjectCode)) {
            totalAmount = transactionArchive.getShortTermGainLoss().add(transactionArchive.getLongTermGainLoss());
            debitCreditCode = getTransactionDebitCreditCodeForOffSetEntry(totalAmount);
        }
        else {
            totalAmount = getTransactionAmount(transactionArchive);
            debitCreditCode = getTransactionDebitCreditCode(transactionArchive.getTypeCode(), totalAmount, transactionArchive.getSubTypeCode());
        }

        if (debitCreditCode.equalsIgnoreCase(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE)) {
            chartObjectDebitAmountSubTotal = chartObjectDebitAmountSubTotal.add(totalAmount);
        }
        if (debitCreditCode.equalsIgnoreCase(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE)) {
            chartObjectCreditAmountSubTotal = chartObjectCreditAmountSubTotal.add(totalAmount);
        }
        chartObjectNumberOfRecordsSubTotal += 1;
    }

    /**
     * write TotalsProcessedDetailTotalsLine method to write details total line.
     * @param previousDocumentTypeCode
     * @param feeMethodCode
     * @param totalLinesGenerated
     */
    protected void writeTotalsProcessedObjectDetailTotalsLine(String previousDocumentTypeCode, String previousChartCode, String previousObjectCode) {
        //to suppress duplicate document type value on the report.
        if (!documentTypeWritten) {
            gLInterfaceBatchTotalsProcessedTableRowValues.setDocumentType(previousDocumentTypeCode);
            documentTypeWritten = true;
        }
        else {
            gLInterfaceBatchTotalsProcessedTableRowValues.setDocumentType(null);
        }

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
        //need to write the header line with "-"s.
        gLInterfaceBatchTotalProcessedReportsWriterService.writeFormattedMessageLine(getSepartorLine());

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
     */
    protected void writeTotalsProcessedDocumentTypeDetailTotalsLine() {
        //need to write the header line with "-"s.
        gLInterfaceBatchTotalProcessedReportsWriterService.writeFormattedMessageLine(getSepartorLine());

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
     * method to write document type totals whenever document type changes.
     * First write out any pending details row and then write the pending chart details before writing
     * the document type level totals.
     */
    protected void processDocumentTypeTotals() {
        //add to the grand total and write the document sub-totals....
        writeTotalsProcessedObjectDetailTotalsLine(previousDocumentTypeCode, previousChartCode, previousObjectCode);
        addTotalsToChartTotals();
        documentTypeWritten = false ;

        writeTotalsProcessedChartDetailTotalsLine();
        addChartTotalsToDocumentTypeTotals();
        writeTotalsProcessedDocumentTypeDetailTotalsLine();
        addDocumentTypeTotalsToGrandTotals();
        previousChartCode = null;
        previousObjectCode = null;
        previousDocumentTypeCode = null;
    }

    /**
     * write document TotalsProcessedDetailTotalsLine method to write details total line.
     */
    protected void writeTotalsProcessedGrandTotalsLine() {
        //need to write the header line with "-"s.
        gLInterfaceBatchTotalProcessedReportsWriterService.writeFormattedMessageLine(getSepartorLine());

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
     * add totals to grand totals and reset document level totals...
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
     * add chart level totals to document type totals and reset the chart/object level totals.
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
        initializeChartObjectTotals();
    }

    protected void addTotalsToChartTotals() {
        //the properties to totals processed at chart level..sub totals.
        chartDebitAmountSubTotal = chartDebitAmountSubTotal.add(chartObjectDebitAmountSubTotal);
        chartCreditAmountSubTotal = chartCreditAmountSubTotal.add(chartObjectCreditAmountSubTotal);
        chartNumberOfRecordsSubTotal += chartObjectNumberOfRecordsSubTotal;

        //reset the object level totals...
        initializeChartObjectTotals();
    }

    /**
     * method to initialize object level totals....
     */
    protected void initializeChartObjectTotals() {
        chartObjectDebitAmountSubTotal = BigDecimal.ZERO;
        chartObjectCreditAmountSubTotal = BigDecimal.ZERO;
        chartObjectNumberOfRecordsSubTotal = 0;
    }

    /**
     * method to write the statistics report....
     * @param statisticsReportRows Collection of statistics detail rows
     */
    protected void writeStatisticsReport(Collection<GLInterfaceBatchStatisticsReportDetailTableRow> statisticsReportRows) {
        //now print the statistics report.....
        long totalNumberOfGLEntries = 0;
        long totalNumberOfExceptions = 0;

        for (GLInterfaceBatchStatisticsReportDetailTableRow statisticsReportRow: statisticsReportRows) {
            if (!statisticsHeaderWritten) {
                //write the header line....
                gLInterfaceBatchStatisticsReportsWriterService.writeStatisticLine("Document Type\t\tNumber of Gl Entries\t\tNumber of Exceptions");
                gLInterfaceBatchStatisticsReportsWriterService.writeStatisticLine("-------------\t\t--------------------\t\t--------------------");
                statisticsHeaderWritten = true;
            }

            totalNumberOfGLEntries += statisticsReportRow.getGlEntriesGenerated();
            totalNumberOfExceptions += statisticsReportRow.getNumberOfExceptions();
            gLInterfaceBatchStatisticsReportsWriterService.writeStatisticLine("%s\t\t\t\t%9d\t\t\t\t%9d", statisticsReportRow.getDocumentType(), statisticsReportRow.getGlEntriesGenerated(), statisticsReportRow.getNumberOfExceptions());
        }
        //writes the total line of the report....
        gLInterfaceBatchStatisticsReportsWriterService.writeStatisticLine("             \t\t--------------------\t\t--------------------");
        gLInterfaceBatchStatisticsReportsWriterService.writeStatisticLine("%s\t\t\t\t%9d\t\t\t\t%9d", "Total", totalNumberOfGLEntries, totalNumberOfExceptions);
    }

    /**
     * get the separator line - to write a header line in statistics report
     * @return the separator line
     */
    public String getSepartorLine() {
        int attributeLength = 0;
        StringBuffer separatorLine = new StringBuffer();
        GLInterfaceBatchTotalsProcessedReportHeader reportHeader = new GLInterfaceBatchTotalsProcessedReportHeader();

        attributeLength += reportHeader.getColumn1MaxLength();
        attributeLength += reportHeader.getColumn2MaxLength();
        attributeLength += reportHeader.getColumn3MaxLength();
        separatorLine = separatorLine.append(StringUtils.rightPad(StringUtils.EMPTY, attributeLength, " ")).append("   ");
        attributeLength = reportHeader.getColumn4MaxLength();
        separatorLine = separatorLine.append(StringUtils.rightPad(StringUtils.EMPTY, attributeLength, KFSConstants.DASH)).append(" ");
        attributeLength = reportHeader.getColumn5MaxLength();
        separatorLine = separatorLine.append(StringUtils.rightPad(StringUtils.EMPTY, attributeLength, KFSConstants.DASH)).append(" ");
        attributeLength = reportHeader.getColumn6MaxLength();
        separatorLine = separatorLine.append(StringUtils.rightPad(StringUtils.EMPTY, attributeLength, KFSConstants.DASH)).append(" ");

        return separatorLine.toString();
    }

    /**
     * writes out the table row values then writes the reason row and inserts a blank line
     * @param transactionArchive
     * @param reasonMessage the reason message
     */
    protected void writeExceptionRecord(GlInterfaceBatchProcessKemLine transactionArchive, String reasonMessage) {
        gLInterfaceBatchExceptionTableRowValues.setDocumentType(transactionArchive.getTypeCode());
        gLInterfaceBatchExceptionTableRowValues.setDocumentNumber(transactionArchive.getDocumentNumber());
        gLInterfaceBatchExceptionTableRowValues.setKemid(transactionArchive.getKemid());
        gLInterfaceBatchExceptionTableRowValues.setIncomeAmount(transactionArchive.getTransactionArchiveIncomeAmount());
        gLInterfaceBatchExceptionTableRowValues.setPrincipalAmountt(transactionArchive.getTransactionArchivePrincipalAmount());
        gLInterfaceBatchExceptionTableRowValues.setSecurityCost(transactionArchive.getHoldingCost());
        gLInterfaceBatchExceptionTableRowValues.setLongTermGainLoss(transactionArchive.getLongTermGainLoss());
        gLInterfaceBatchExceptionTableRowValues.setShortTermGainLoss(transactionArchive.getShortTermGainLoss());
        gLInterfaceBatchExceptionReportsWriterService.writeTableRow(gLInterfaceBatchExceptionTableRowValues);

        // now write the error message line...
        gLInterfaceBatchExceptionTableRowValues.setDocumentType("Reason: " + reasonMessage);
        gLInterfaceBatchExceptionTableRowValues.setDocumentNumber(null);
        gLInterfaceBatchExceptionTableRowValues.setKemid(null);
        gLInterfaceBatchExceptionTableRowValues.setIncomeAmount(null);
        gLInterfaceBatchExceptionTableRowValues.setPrincipalAmountt(null);
        gLInterfaceBatchExceptionTableRowValues.setSecurityCost(null);
        gLInterfaceBatchExceptionTableRowValues.setLongTermGainLoss(null);
        gLInterfaceBatchExceptionTableRowValues.setShortTermGainLoss(null);
        gLInterfaceBatchExceptionReportsWriterService.writeTableRow(gLInterfaceBatchExceptionTableRowValues);
        gLInterfaceBatchExceptionReportsWriterService.writeNewLines(1);
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
     * This method sets the batchFileDirectoryName
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
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

    /**
     * gets attribute gLLinkDao
     * @return gLLinkDao
     */
    protected GLLinkDao getgLLinkDao() {
        return gLLinkDao;
    }

    /**
     * sets attribute gLLinkDao
     */
    public void setgLLinkDao(GLLinkDao gLLinkDao) {
        this.gLLinkDao = gLLinkDao;
    }
}

