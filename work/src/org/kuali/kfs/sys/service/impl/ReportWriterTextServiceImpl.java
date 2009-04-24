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
package org.kuali.kfs.sys.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.gl.businessobject.AccountBalanceHistory;
import org.kuali.kfs.gl.businessobject.EncumbranceHistory;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.LedgerEntryHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DateTimeService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Text output implementation of <code>ReportWriterService</code> interface.<br>
 * If you are a developer attempting to add a new business object for error report writing, take a look at writeError and writeErrorHeader. Add
 * your condition to that.
 */
public class ReportWriterTextServiceImpl implements ReportWriterService, InitializingBean {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportWriterTextServiceImpl.class);
    
    // Changing the initial line number would only affect that a page break occurs early. It does not actually print in the
    // middle of the page. Hence changing this has little use.
    protected static final int INITIAL_LINE_NUMBER = 0;
    
    private String filePath;
    private String fileNamePrefix;
    private String fileNameSuffix;
    private String title;
    private int pageWidth;
    private int pageLength;
    private int initialPageNumber;
    private String statisticsLeftPadding;
    private DateTimeService dateTimeService;
    
    protected PrintStream printStream;
    protected int page;
    protected int line = INITIAL_LINE_NUMBER;
    protected String errorFormat;
    protected String keyBlank;
    
    // Includes length of key, including spaces between fields, and spaces before message starts
    protected int keyLength;
    
    // Ensures that the statistics header isn't written multiple times. Does not check that a user doesn't write other stuff into the statistics
    // section. A developer is responsible for ensuring that themselves
    protected boolean modeStatistics = false;
    
    // So that writeError knows when to writeErrorHeader
    protected boolean newPage = true;
    
    // For printing new headers when the BO is changed
    protected Class<? extends BusinessObject> businessObjectClass;
    
    public void afterPropertiesSet() throws Exception {
        // TODO put initialize() here. Wait until we figure our whether we use scope="prototype" in spring-*.xml or do addition to AbstractBatchTransactionalCachingStep
        // TODO if this is removed don't forget to also remove InitializingBean
    }
    
    /**
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() {
        printStream.close();
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeError(java.lang.Class, org.kuali.kfs.sys.Message)
     */
    public void writeError(BusinessObject businessObject, Message message) {
        this.initialize();
        
        // Check if we need to write a new table header. We do this if it hasn't been written before or if the businessObject changed
        if (newPage || businessObjectClass == null || !businessObjectClass.getName().equals(businessObject.getClass().getName())) {
            this.writeErrorHeader(businessObject);
            newPage = false;
            businessObjectClass = businessObject.getClass();
        }
        
        // Extract the values from the businessObject
        List<Object> keys = new ArrayList<Object>();
        if ((businessObject instanceof Transaction || businessObject instanceof OriginEntryFull || businessObject instanceof LaborOriginEntry) && !(businessObject instanceof LedgerEntryHistory)) {
            Transaction t = (Transaction) businessObject;
            keys.add(t.getUniversityFiscalYear() == null ? "" : t.getUniversityFiscalYear().toString());
            keys.add(t.getChartOfAccountsCode());
            keys.add(t.getAccountNumber());
            keys.add(t.getSubAccountNumber());
            keys.add(t.getFinancialObjectCode());
            keys.add(t.getFinancialSubObjectCode());
            keys.add(t.getFinancialBalanceTypeCode());
            keys.add(t.getFinancialObjectTypeCode());
            keys.add(t.getUniversityFiscalPeriodCode());
            keys.add(t.getFinancialDocumentTypeCode());
            keys.add(t.getFinancialSystemOriginationCode());
            keys.add(t.getDocumentNumber());
            keys.add(t.getTransactionLedgerEntrySequenceNumber() == null ? "" : t.getTransactionLedgerEntrySequenceNumber().toString());
        } else if (businessObject instanceof SufficientFundRebuild) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) businessObject;
            keys.add(sfrb.getChartOfAccountsCode());
            keys.add(sfrb.getAccountFinancialObjectTypeCode());
            keys.add(sfrb.getAccountNumberFinancialObjectCode());
        } else if (businessObject instanceof ExpenditureTransaction) {
            ExpenditureTransaction et = (ExpenditureTransaction) businessObject;
            keys.add(et.getUniversityFiscalYear() == null ? "" : et.getUniversityFiscalYear().toString());
            keys.add(et.getChartOfAccountsCode());
            keys.add(et.getAccountNumber());
            keys.add(et.getSubAccountNumber());
            keys.add(et.getObjectCode());
            keys.add(et.getSubObjectCode());
            keys.add(et.getBalanceTypeCode());
            keys.add(et.getObjectTypeCode());
            keys.add(et.getUniversityFiscalAccountingPeriod());
            keys.add(et.getProjectCode());
            keys.add(et.getOrganizationReferenceId());
        } else if (businessObject instanceof LedgerBalanceHistory) {
            LedgerBalanceHistory ledgerBalanceHistory = (LedgerBalanceHistory) businessObject;
            keys.add(ledgerBalanceHistory.getUniversityFiscalYear() == null ? "" : ledgerBalanceHistory.getUniversityFiscalYear().toString());
            keys.add(ledgerBalanceHistory.getChartOfAccountsCode());
            keys.add(ledgerBalanceHistory.getAccountNumber());
            keys.add(ledgerBalanceHistory.getSubAccountNumber());
            keys.add(ledgerBalanceHistory.getFinancialObjectCode());
            keys.add(ledgerBalanceHistory.getFinancialSubObjectCode());
            keys.add(ledgerBalanceHistory.getFinancialBalanceTypeCode());
            keys.add(ledgerBalanceHistory.getFinancialObjectTypeCode());
        } else if (businessObject instanceof LedgerEntryHistory) {
            LedgerEntryHistory ledgerEntryHistory = (LedgerEntryHistory) businessObject;
            keys.add(ledgerEntryHistory.getUniversityFiscalYear() == null ? "" : ledgerEntryHistory.getUniversityFiscalYear().toString());
            keys.add(ledgerEntryHistory.getChartOfAccountsCode());
            keys.add(ledgerEntryHistory.getFinancialObjectCode());
            keys.add(ledgerEntryHistory.getFinancialBalanceTypeCode());
            keys.add(ledgerEntryHistory.getUniversityFiscalPeriodCode());
            keys.add(ledgerEntryHistory.getTransactionDebitCreditCode());
        } else if (businessObject instanceof AccountBalanceHistory) {
            AccountBalanceHistory accountBalanceHistory = (AccountBalanceHistory) businessObject;
            keys.add(accountBalanceHistory.getUniversityFiscalYear() == null ? "" : accountBalanceHistory.getUniversityFiscalYear().toString());
            keys.add(accountBalanceHistory.getChartOfAccountsCode());
            keys.add(accountBalanceHistory.getAccountNumber());
            keys.add(accountBalanceHistory.getSubAccountNumber());
            keys.add(accountBalanceHistory.getObjectCode());
            keys.add(accountBalanceHistory.getSubObjectCode());
        } else if (businessObject instanceof EncumbranceHistory) {
            EncumbranceHistory encumbranceHistory = (EncumbranceHistory) businessObject;
            keys.add(encumbranceHistory.getUniversityFiscalYear() == null ? "" : encumbranceHistory.getUniversityFiscalYear().toString());
            keys.add(encumbranceHistory.getChartOfAccountsCode());
            keys.add(encumbranceHistory.getAccountNumber());
            keys.add(encumbranceHistory.getSubAccountNumber());
            keys.add(encumbranceHistory.getObjectCode());
            keys.add(encumbranceHistory.getSubObjectCode());
            keys.add(encumbranceHistory.getBalanceTypeCode());
            keys.add(encumbranceHistory.getDocumentTypeCode());
            keys.add(encumbranceHistory.getObjectCode());
            keys.add(encumbranceHistory.getDocumentNumber());
        } else {
            throw new RuntimeException(businessObject.getClass().toString() + " is not handled");
        }
        
        // Print the values of the businessObject per formatting determined by writeErrorHeader
        printStream.printf(errorFormat, keys.toArray());
        
        this.writeMessageHelper(message);
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeError(java.lang.Class, java.util.List)
     */
    public void writeError(BusinessObject businessObject, List<Message> messages) {
        int i = 0;
        for (Iterator<Message> messagesIter = messages.iterator(); messagesIter.hasNext();) {
            Message message = messagesIter.next();
            
            if (i == 0) {
                // First one has its values written
                this.writeError(businessObject, message);
            } else {
                // Any consecutive one doesn't have it's values written, we only write the message
                printStream.printf(keyBlank);
                this.writeMessageHelper(message);
            }
            
            i++;
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeStatistic(java.lang.String, java.lang.Object[])
     */
    public void writeStatistic(String message, Object ... args) {
        this.initialize();
        
        // Statistics header is only written if it hasn't been written before
        if (!modeStatistics) {
            this.modeStatistics = true;
            
            // If nothing has been written to the report we don't want to page break
            if (!(page == initialPageNumber && line == INITIAL_LINE_NUMBER)) {
                this.pageBreak();
            }
            
            printStream.printf("***********************************************************************************************************************************\n");
            printStream.printf("***********************************************************************************************************************************\n");
            printStream.printf("********************                                    S T A T I S T I C S                                    ********************\n");
            printStream.printf("***********************************************************************************************************************************\n");
            printStream.printf("***********************************************************************************************************************************\n");
            line += 5;
        }
        
        if (statisticsLeftPadding.length() + message.length() > pageWidth) {
            LOG.warn("writeStatistic message written is out of bounds. Writing anyway.");
        }
        
        this.writeFormattedMessage(statisticsLeftPadding + message, args);
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#printf(java.lang.String)
     */
    public void writeFormattedMessage(String format) {
        this.writeFormattedMessage(format, new Object());
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#printf(java.lang.String, java.lang.Object[])
     */
    public void writeFormattedMessage(String format, Object ... args) {
        this.initialize();
        
        printStream.printf(format, args);
        
        line++;
        if (line >= pageLength) {
            this.pageBreak();
        }
    }
    
    /**
     * Initializes the file stream. Would be nice to do this in the constructor but Spring won't have injected content yet
     */
    protected void initialize() {
        // File writing is only initialized if it hasn't been initialized before.
        if (printStream != null) {
            return;
        }
        
        try {
            printStream = new PrintStream(filePath + File.separator + this.fileNamePrefix + dateTimeService.toDateStringForFilename(dateTimeService.getCurrentDate()) + fileNameSuffix);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        page = initialPageNumber;
        
        // Initial header
        this.writeHeader(title);
    }
    
    
    /**
     * Helper method for writeError methods. Takes care of truncating the message and page breaking if necessary
     * @param message to print
     */
    protected void writeMessageHelper(Message message) {
        // Truncate message if necessary
        if (message.getMessage().length() > pageWidth - keyLength) {
            printStream.printf("%s\n", message.getMessage().substring(0, pageWidth - keyLength));
        } else {
            printStream.printf("%s\n", message.getMessage());
        }
        
        line++;
        if (line >= pageLength) {
            pageBreak();
        }
    }
    
    /**
     * Breaking the page and writing a new header
     */
    protected void pageBreak() {
        printStream.printf("%c\n",12);
        page++;
        line = INITIAL_LINE_NUMBER;
        newPage = true;
        
        this.writeHeader(title);
    }
    
    /**
     * Writes a header
     * @param title that should be printed on this header
     */
    protected void writeHeader(String title) {
        String headerText = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM",dateTimeService.getCurrentDate());
        int reportTitlePadding = pageWidth/2 - headerText.length() - title.length()/2;
        headerText = String.format("%s%" + (reportTitlePadding + title.length()) + "s%" + reportTitlePadding + "s", headerText, title, "");
        
        printStream.printf("%sPAGE:%,9d\n\n", headerText, page);
        line += 2;
    }
    
    /**
     * Helper method to write the error header
     * @param businessObject to print header for
     */
    protected void writeErrorHeader(BusinessObject businessObject) {
        String errorHeaderLine;
        String errorSeparatorLine;
        
        if ((businessObject instanceof Transaction || businessObject instanceof OriginEntry) && !(businessObject instanceof LedgerEntryHistory)) {
            errorHeaderLine    = "YEAR COA ACCOUNT SACCT OBJ  SOBJ BT OT PRD DTYP ORIG DOC #     SEQ #    MESSAGE";
            errorSeparatorLine = "---- --- ------- ----- ---- ---- -- -- --- ---- ---- --------- -----    ----------------------------------------------------------";
            errorFormat = "%-4s %-2s  %-7s %-5s %-4s %-3s  %-2s %-2s %-2s  %-4s %-2s   %-9s %5s    ";
        } else if (businessObject instanceof SufficientFundRebuild) {
            errorHeaderLine    = "COA A/O VALUE      MESSAGE";
            errorSeparatorLine = "--- --- -------    ---------------------------------------------------------------------------------------------------------------";
            errorFormat = "%-2s  %-1s   %-7s    ";
        } else if (businessObject instanceof ExpenditureTransaction) {
            errorHeaderLine    = "YEAR COA ACCOUNT SACCT OBJ  SOBJ BT OT PRD PROJECT CD ORG REF     MESSAGE";
            errorSeparatorLine = "---- --- ------- ----- ---- ---- -- -- --- ---------- --------    ----------------------------------------------------------------";
            errorFormat = "%-4s %-2s  %-7s %-5s %-4s %-3s  %-2s %-2s %-2s  %-10s %-8s    ";
        } else if (businessObject instanceof LedgerBalanceHistory) {
            errorHeaderLine    = "YEAR COA ACCOUNT SACCT OBJ  SOBJ BT OT    MESSAGE";
            errorSeparatorLine = "---- --- ------- ----- ---- ---- -- --    ----------------------------------------------------------";
            errorFormat =        "%-4s %-2s  %-7s %-5s %-4s %-3s  %-2s %-2s    ";
        } else if (businessObject instanceof LedgerEntryHistory) {
            errorHeaderLine    = "YEAR COA OBJ  BT PRD DC    MESSAGE";
            errorSeparatorLine = "---- --- ---- -- --- --    ----------------------------------------------------------";
            errorFormat = "%-4s %-2s  %-4s %-2s %-2s  %-1s     ";
        } else if (businessObject instanceof AccountBalanceHistory) {
            errorHeaderLine    = "YEAR COA ACCOUNT SACCT OBJ  SOBJ     MESSAGE";
            errorSeparatorLine = "---- --- ------- ----- ---- ----     ----------------------------------------------------------";
            errorFormat =        "%-4s %-2s  %-7s %-5s %-4s %-3s      ";
        } else if (businessObject instanceof EncumbranceHistory) {
            errorHeaderLine    = "YEAR COA ACCOUNT SACCT OBJ  SOBJ BT DTYP ORIG DOC #         MESSAGE";
            errorSeparatorLine = "---- --- ------- ----- ---- ---- -- ---- ---- ---------     ----------------------------------------------------------";
            errorFormat = "%-4s %-2s  %-7s %-5s %-4s %-3s  %-2s %-4s %-2s %-9s     ";
        } else {
            throw new RuntimeException(businessObject.getClass().toString() + " is not handled");
        }
        
        keyBlank = String.format(errorFormat, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        keyLength = keyBlank.length();
        
        printStream.printf("%s\n", errorHeaderLine);
        printStream.printf("%s\n", errorSeparatorLine);
        line += 2;
    }
    
    /**
     * Sets the filePath
     * 
     * @param filePath The filePath to set.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Sets the fileNamePrefix
     * 
     * @param fileNamePrefix The fileNamePrefix to set.
     */
    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }
    
    /**
     * Sets the fileNameSuffix
     * 
     * @param fileNameSuffix The fileNameSuffix to set.
     */
    public void setFileNameSuffix(String fileNameSuffix) {
        this.fileNameSuffix = fileNameSuffix;
    }

    /**
     * Sets the title
     * 
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Sets the pageWidth
     * 
     * @param pageWidth The pageWidth to set.
     */
    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    /**
     * Sets the pageLength
     * 
     * @param pageLength The pageLength to set.
     */
    public void setPageLength(int pageLength) {
        this.pageLength = pageLength;
    }

    /**
     * Sets the initialPageNumber
     * 
     * @param initialPageNumber The initialPageNumber to set.
     */
    public void setInitialPageNumber(int initialPageNumber) {
        this.initialPageNumber = initialPageNumber;
    }

    /**
     * Sets the statisticsLeftPadding
     * 
     * @param statisticsLeftPadding The statisticsLeftPadding to set.
     */
    public void setStatisticsLeftPadding(String statisticsLeftPadding) {
        this.statisticsLeftPadding = statisticsLeftPadding;
    }

    /**
     * Sets the DateTimeService
     * 
     * @param dateTimeService The DateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
