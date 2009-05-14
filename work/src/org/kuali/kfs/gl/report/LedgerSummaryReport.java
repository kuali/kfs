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
package org.kuali.kfs.gl.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine;
import org.kuali.kfs.gl.businessobject.NightlyOutPendingEntryLedgerSummaryDetailLine;
import org.kuali.kfs.gl.businessobject.NightlyOutPendingEntryLedgerSummaryTotalLine;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;

/**
 * Helper class which can summarize entries by balance type and then print out a ledger summary report
 */
public class LedgerSummaryReport {
    private NightlyOutPendingEntryLedgerSummaryTotalLine ledgerTotalLine;
    private Map<String, NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine> balanceTypeTotals;
    Map<String, NightlyOutPendingEntryLedgerSummaryDetailLine> details;
    
    /**
     * Constructs a LedgerSummaryReport
     */
    public LedgerSummaryReport() {
        ledgerTotalLine = new NightlyOutPendingEntryLedgerSummaryTotalLine();
        balanceTypeTotals = new LinkedHashMap<String, NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine>();
        details = new LinkedHashMap<String, NightlyOutPendingEntryLedgerSummaryDetailLine>();
    }
    
    /**
     * Summarizes an entry into the various totals which this report is keeping
     * @param entry an entry to summarize
     */
    public void summarizeEntry(OriginEntry entry) {
        NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine balanceTypeTotal = getBalanceTypeSummaryTotalLine(entry, balanceTypeTotals);
        NightlyOutPendingEntryLedgerSummaryDetailLine detailLine = getDetailLine(entry, details);
        addEntryToLedgerSummaries(entry, ledgerTotalLine, balanceTypeTotal, detailLine);
    }
    
    /**
     * Retrieves the proper balance type summarizer from the given map, or creates a new summarizer and puts it in the Map if it doesn't already exist
     * @param entry the origin entry to find a balance type summarizer for
     * @param balanceTypeTotals the Map of balance type summarizers
     * @return the proper balance type summarizer
     */
    protected NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine getBalanceTypeSummaryTotalLine(OriginEntry entry, Map<String, NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine> balanceTypeTotals) {
        final String balanceTypeCode = entry.getFinancialBalanceTypeCode();
        NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine balanceTypeTotal = balanceTypeTotals.get(balanceTypeCode);
        if (balanceTypeTotal == null) {
            balanceTypeTotal = new NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine(balanceTypeCode);
            balanceTypeTotals.put(balanceTypeCode, balanceTypeTotal);
        }
        return balanceTypeTotal;
    }
    
    /**
     * Retrieves the proper detail line summarizer from the given map, or creates a new summarizer and adds it to the map if needed
     * @param entry the origin entry to find a detail line summarizer for
     * @param detailLines a Map of detail line summarizers
     * @return the proper detail line summarizer
     */
    protected NightlyOutPendingEntryLedgerSummaryDetailLine getDetailLine(OriginEntry entry, Map<String, NightlyOutPendingEntryLedgerSummaryDetailLine> detailLines) {
        final String key = NightlyOutPendingEntryLedgerSummaryDetailLine.getKeyString(entry);
        NightlyOutPendingEntryLedgerSummaryDetailLine detailLine = detailLines.get(key);
        if (detailLine == null) {
            detailLine = new NightlyOutPendingEntryLedgerSummaryDetailLine(entry.getFinancialBalanceTypeCode(), entry.getFinancialSystemOriginationCode(), entry.getUniversityFiscalYear(), entry.getUniversityFiscalPeriodCode());
            detailLines.put(detailLine.getKey(), detailLine);
        }
        return detailLine;
    }
    
    /**
     * Adds the amount of the origin entry into the appropriate total - debit, credit, or budget - on the various ledger summarizers
     * @param originEntry the origin entry to add the total from
     * @param totalLine a complete total to add the amount to
     * @param balanceTypeTotal the total for the entries with the same balance type as the origin entry to add the amount to
     * @param detailLine the proper detail amount to add the amoun tto
     */
    protected void addEntryToLedgerSummaries(OriginEntry originEntry, NightlyOutPendingEntryLedgerSummaryTotalLine totalLine, NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine balanceTypeTotal, NightlyOutPendingEntryLedgerSummaryDetailLine detailLine) {
        if (originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
            totalLine.addDebitAmount(originEntry.getTransactionLedgerEntryAmount());
            balanceTypeTotal.addDebitAmount(originEntry.getTransactionLedgerEntryAmount());
            detailLine.addDebitAmount(originEntry.getTransactionLedgerEntryAmount());
        } else if (originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
            totalLine.addCreditAmount(originEntry.getTransactionLedgerEntryAmount());
            balanceTypeTotal.addCreditAmount(originEntry.getTransactionLedgerEntryAmount());
            detailLine.addCreditAmount(originEntry.getTransactionLedgerEntryAmount());
        } else if (originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_BUDGET_CODE)) {
            totalLine.addBudgetAmount(originEntry.getTransactionLedgerEntryAmount());
            balanceTypeTotal.addBudgetAmount(originEntry.getTransactionLedgerEntryAmount());
            detailLine.addBudgetAmount(originEntry.getTransactionLedgerEntryAmount());
        }
    }
    
    /**
     * Writes the report of totals to the given reportWriterService
     * @param reportWriterService a report writer service to write the ledger summary report to
     */
    public void writeReport(ReportWriterService reportWriterService) {
        if (details.size() > 0) {
            List<NightlyOutPendingEntryLedgerSummaryDetailLine> detailList = new ArrayList<NightlyOutPendingEntryLedgerSummaryDetailLine>(details.values());
            Collections.sort(detailList, NightlyOutPendingEntryLedgerSummaryDetailLine.getStandardComparator());
        
            reportWriterService.writeTableHeader(detailList.get(0));
            String currentBalanceType = detailList.get(0).getFinancialBalanceTypeCode();
            for (NightlyOutPendingEntryLedgerSummaryDetailLine detailLine : detailList) {
                if (!detailLine.getFinancialBalanceTypeCode().equals(currentBalanceType)) {
                    reportWriterService.writeTableRow(balanceTypeTotals.get(currentBalanceType));
                    currentBalanceType = detailLine.getFinancialBalanceTypeCode();
                }
                reportWriterService.writeTableRow(detailLine);
            }
            reportWriterService.writeTableRow(balanceTypeTotals.get(detailList.get(detailList.size()-1).getFinancialBalanceTypeCode()));
            reportWriterService.writeTableRow(ledgerTotalLine);
        }
    }
    
}
