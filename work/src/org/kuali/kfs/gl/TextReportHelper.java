/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.gl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.LedgerEntryHistory;
import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * TODO This is an initial version from what IU will be contributing to kuali as part of their GL / Labor optimization. Once the merge occurs
 * this should be replaced with the final version. It was added to the project early so that KFSMI-2477 (balancing process) can benefit from it.<br>
 * TODO CHANGELOG (from branch)<br>
 * 1) Added method writeErrors(Object o, Message errorMessage). Impact: None<br>
 * 2) Added LedgerBalanceHistory and LedgerEntryHistory cases for error printing. Impact: None<br>
 * 3) Moved "ERROR AND STATISTICS REPORT" heading into writeTitle so that multiple error sections can be written without heading appearing multiple times. Also added newline before errorHeader. Impact: Visual only<br>
 * 4) Added "titleWritten" to writeTitle to avoid multiple titles if multiple writeErrorHeader written. Impact: Should be none (unconfirmed)<br>
 * 5) Removed generic typing related to #4 (i.e. class isn't typed but calls to writeErrorHeader are). This also removed the o argument to the constructor, however, it added it to writeErrorHeader. Impact: class initialization, constructor call, writeErrorHeader call<br>
 * TODO Enhancement suggestions<br>
 * A. writeErrors(PersistableBusinessObjectBase bo, Message errorMessage) implementation is a bit silly. Could be better encapsulated with writeErrors(bo, List)
 * B. Make this class a service IF technical leads meeting decides PrintStream should be DAO encapsulated
 */
public class TextReportHelper {
    private int reportLine = 0;
    private int reportPage = 0;
    private PrintStream PRINT_FILE_ps;
    private DateTimeService dateTimeService;
    private String reportTitle;
    private boolean titleWritten = false;
    private int pageWidth = 130;
    private int pageLength = 57;
    private String headerText;
    private Object[] blanks = {"","","","","","","","","","","","","","","","","","","",""};
    
    private String errorHeaderLine;
    private String errorSeparatorLine;
    private String errorFormat;
    private String keyBlank;
    private int keyLength;  //NOTE: this includes length of key, including spaces between fields, and spaces before message starts
    
    public TextReportHelper (String reportTitle, PrintStream ps, DateTimeService dateTimeService) {
        super();
        this.reportTitle = reportTitle;
        this.PRINT_FILE_ps = ps;
        this.dateTimeService = dateTimeService;
        this.headerText = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM",dateTimeService.getCurrentDate());
        int reportTitlePadding = pageWidth/2 - headerText.length() - reportTitle.length()/2;
        this.headerText = String.format("%s%" + (reportTitlePadding + reportTitle.length()) + "s%" + reportTitlePadding + "s", headerText, reportTitle, "");
    }

    public void writeTitle() {
        if (!titleWritten) {
            if (reportPage == 0) { //presumably a statistics-only report, no error listing, thus cannot be more than one page
                PRINT_FILE_ps.printf("%s\n\n", headerText);
            } else {
                PRINT_FILE_ps.printf("%sPAGE:%,9d\n\n", headerText,reportPage);
                PRINT_FILE_ps.printf("                                                    ERROR AND STATISTICS REPORT\n");
            }
            reportLine += 2;
            
            titleWritten = true;
        }
    }

    public void writeErrorHeader(Object o) {
        if (o instanceof Transaction && !(o instanceof LedgerEntryHistory)) {
            this.errorHeaderLine    = "YEAR COA ACCOUNT SACCT OBJ  SOBJ BT OT PRD DTYP ORIG DOC #     SEQ #    MESSAGE (VALUE)";
            this.errorSeparatorLine = "---- --- ------- ----- ---- ---- -- -- --- ---- ---- --------- -----    ----------------------------------------------------------";
            this.errorFormat = "%-4s %-2s  %-7s %-5s %-4s %-3s  %-2s %-2s %-2s  %-4s %-2s   %-9s %5s    ";
        } else if (o instanceof SufficientFundRebuild) {
            this.errorHeaderLine    = "COA A/O VALUE      MESSAGE";
            this.errorSeparatorLine = "--- --- -------    ---------------------------------------------------------------------------------------------------------------";
            this.errorFormat = "%-2s  %-1s   %-7s    ";
        } else if (o instanceof ExpenditureTransaction) {
            this.errorHeaderLine    = "YEAR COA ACCOUNT SACCT OBJ  SOBJ BT OT PRD PROJECT CD ORG REF     MESSAGE (VALUE)";
            this.errorSeparatorLine = "---- --- ------- ----- ---- ---- -- -- --- ---------- --------    ----------------------------------------------------------------";
            this.errorFormat = "%-4s %-2s  %-7s %-5s %-4s %-3s  %-2s %-2s %-2s  %-10s %-8s    ";
        } else if (o instanceof LedgerBalanceHistory) {
            this.errorHeaderLine    = "YEAR COA ACCOUNT SACCT OBJ  SOBJ BT OT    MESSAGE";
            this.errorSeparatorLine = "---- --- ------- ----- ---- ---- -- --    ----------------------------------------------------------";
            this.errorFormat =        "%-4s %-2s  %-7s %-5s %-4s %-3s  %-2s %-2s    ";
        } else if (o instanceof LedgerEntryHistory) {
            this.errorHeaderLine    = "YEAR COA OBJ  BT PRD DC    MESSAGE";
            this.errorSeparatorLine = "---- --- ---- -- --- --    ----------------------------------------------------------";
            this.errorFormat = "%-4s %-2s  %-4s %-2s %-2s  %-1s     ";
        } else {
            throw new RuntimeException(o.getClass().toString() + " is not handled in TextReporterHelper");
        }
        
        keyBlank = String.format(errorFormat, blanks);
        keyLength = keyBlank.length();
        
        reportPage++;
        writeTitle();
        PRINT_FILE_ps.printf("\n%s\n", errorHeaderLine);
        PRINT_FILE_ps.printf("%s\n", errorSeparatorLine);
        reportLine += 3;
    }

    public void writeStatisticsHeader() {
        PRINT_FILE_ps.printf("%c\n",12); //page break
        PRINT_FILE_ps.printf("***********************************************************************************************************************************\n");
        PRINT_FILE_ps.printf("***********************************************************************************************************************************\n");
        PRINT_FILE_ps.printf("********************                                    S T A T I S T I C S                                    ********************\n");
        PRINT_FILE_ps.printf("***********************************************************************************************************************************\n");
        PRINT_FILE_ps.printf("***********************************************************************************************************************************\n");
    }
    
    public void writeErrors(Object o, List<Message> errorMessages) {
        int messageNumber = 0;
        List<Object> keys = new ArrayList<Object>();

        for (Message errorMessage : errorMessages) {
            //print header
            if (reportLine == 0) {
                writeErrorHeader(o.getClass());
            }
            keys.clear();
            
            messageNumber++;
            if (messageNumber == 1) {
                if (o instanceof Transaction && !(o instanceof LedgerEntryHistory)) {
                    Transaction t = (Transaction) o;
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
                } else if (o instanceof SufficientFundRebuild) {
                    SufficientFundRebuild sfrb = (SufficientFundRebuild) o;
                    keys.add(sfrb.getChartOfAccountsCode());
                    keys.add(sfrb.getAccountFinancialObjectTypeCode());
                    keys.add(sfrb.getAccountNumberFinancialObjectCode());
                } else if (o instanceof ExpenditureTransaction) {
                    ExpenditureTransaction et = (ExpenditureTransaction) o;
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
                } else if (o instanceof LedgerBalanceHistory) {
                    LedgerBalanceHistory ledgerBalanceHistory = (LedgerBalanceHistory) o;
                    keys.add(ledgerBalanceHistory.getUniversityFiscalYear() == null ? "" : ledgerBalanceHistory.getUniversityFiscalYear().toString());
                    keys.add(ledgerBalanceHistory.getChartOfAccountsCode());
                    keys.add(ledgerBalanceHistory.getAccountNumber());
                    keys.add(ledgerBalanceHistory.getSubAccountNumber());
                    keys.add(ledgerBalanceHistory.getFinancialObjectCode());
                    keys.add(ledgerBalanceHistory.getFinancialSubObjectCode());
                    keys.add(ledgerBalanceHistory.getFinancialBalanceTypeCode());
                    keys.add(ledgerBalanceHistory.getFinancialObjectTypeCode());
                } else if (o instanceof LedgerEntryHistory) {
                    LedgerEntryHistory ledgerEntryHistory = (LedgerEntryHistory) o;
                    keys.add(ledgerEntryHistory.getUniversityFiscalYear() == null ? "" : ledgerEntryHistory.getUniversityFiscalYear().toString());
                    keys.add(ledgerEntryHistory.getChartOfAccountsCode());
                    keys.add(ledgerEntryHistory.getFinancialObjectCode());
                    keys.add(ledgerEntryHistory.getFinancialBalanceTypeCode());
                    keys.add(ledgerEntryHistory.getUniversityFiscalPeriodCode());
                    keys.add(ledgerEntryHistory.getTransactionDebitCreditCode());
                } else {
                    throw new RuntimeException(o.getClass().toString() + " is not handled in TextReporterHelper (writeErrors)");
                }
                PRINT_FILE_ps.printf(errorFormat,keys.toArray());
            } else {
                PRINT_FILE_ps.printf(keyBlank);
            }
            //TODO Sterling says it's OK to truncate error message if it's too long, but might want to check what longest error message is
            if (errorMessage.getMessage().length() > pageWidth - keyLength) {
                errorMessage.setMessage(errorMessage.getMessage().substring(0, pageWidth - keyLength));
            }
            PRINT_FILE_ps.printf("%s\n",errorMessage.getMessage());

            reportLine++;
            if (reportLine >= pageLength) {
                reportLine = 0;
                PRINT_FILE_ps.printf("%c\n",12); //page break
            }
        }
    }
    
    /**
     * Wrapper for same named method to accept only one message
     * @param o
     * @param errorMessage
     */
    public void writeErrors(Object o, Message errorMessage) {
        List<Message> errorMessages = new ArrayList<Message>();
        errorMessages.add(errorMessage);
        
        this.writeErrors(o, errorMessages);
    }
}
