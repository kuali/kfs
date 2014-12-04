/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.report;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryBalanceTypeFiscalYearTotal;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryBalanceTypeTotal;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryEntry;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryTotal;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.service.PosterOutputSummaryService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * A class which builds up the data and then reports the PosterOutputSummary report
 */
public class PosterOutputSummaryReport {
    private Map<String, PosterOutputSummaryEntry> posterOutputSummaryEntries;
    private PosterOutputSummaryTotal posterOutputSummaryTotal;
    private PosterOutputSummaryService posterOutputSummaryService;
    
    /**
     * Constructs a PosterOutputSummaryReport
     */
    public PosterOutputSummaryReport() {
        posterOutputSummaryTotal = new PosterOutputSummaryTotal();
        posterOutputSummaryEntries = new LinkedHashMap<String, PosterOutputSummaryEntry>();
    }
    
    /**
     * Summarizes a transaction for this report
     * @param transaction the transaction to summarize
     */
    public void summarize(Transaction transaction) {
        getPosterOutputSummaryService().summarize(transaction, posterOutputSummaryEntries);
    }
    
    /**
     * Summarizes an origin entry for this report
     * @param originEntry the origin entry to summarize
     */
    public void summarize(OriginEntryInformation originEntry) {
        getPosterOutputSummaryService().summarize(originEntry, posterOutputSummaryEntries);
    }
    
    /**
     * Writes the report to the given reportWriterService
     * @param reportWriterService the reportWriterService to write the report to
     */
    public void writeReport(ReportWriterService reportWriterService) {
        List<PosterOutputSummaryEntry> entries = new ArrayList<PosterOutputSummaryEntry>(posterOutputSummaryEntries.values());
        
        if (entries.size() > 0) {
            Collections.sort(entries, getPosterOutputSummaryService().getEntryComparator());
            final ConfigurationService configurationService = SpringContext.getBean(ConfigurationService.class);
            
            String currentBalanceTypeCode = entries.get(0).getBalanceTypeCode();
            PosterOutputSummaryBalanceTypeTotal balanceTypeTotal = new PosterOutputSummaryBalanceTypeTotal(currentBalanceTypeCode);
            Integer currentFiscalYear = entries.get(0).getUniversityFiscalYear();
            PosterOutputSummaryBalanceTypeFiscalYearTotal balanceTypeFiscalYearTotal = new PosterOutputSummaryBalanceTypeFiscalYearTotal(currentBalanceTypeCode, currentFiscalYear);
            String currentFiscalPeriod = entries.get(0).getFiscalPeriodCode();
            PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal balanceTypeFiscalYearAndPeriodTotal = new PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal(currentBalanceTypeCode, currentFiscalYear, currentFiscalPeriod);
        
            final String titleMessage = configurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_TITLE_LINE);
            String formattedTitle = MessageFormat.format(titleMessage, entries.get(0).getUniversityFiscalYear().toString(), entries.get(0).getBalanceTypeCode());
            
            reportWriterService.writeFormattedMessageLine(formattedTitle);
            reportWriterService.writeTableHeader(entries.get(0));
            
            for (PosterOutputSummaryEntry entry : entries) {
                if (!entry.getBalanceTypeCode().equals(currentBalanceTypeCode)) {
                    reportWriterService.writeTableRow(balanceTypeFiscalYearAndPeriodTotal);
                    reportWriterService.writeTableRow(balanceTypeFiscalYearTotal);
                    reportWriterService.writeTableRow(balanceTypeTotal);
                    
                    balanceTypeFiscalYearAndPeriodTotal = new PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal(entry.getBalanceTypeCode(), entry.getUniversityFiscalYear(), entry.getFiscalPeriodCode());
                    balanceTypeFiscalYearTotal = new PosterOutputSummaryBalanceTypeFiscalYearTotal(entry.getBalanceTypeCode(), entry.getUniversityFiscalYear());
                    balanceTypeTotal = new PosterOutputSummaryBalanceTypeTotal(entry.getBalanceTypeCode());
                    currentBalanceTypeCode = entry.getBalanceTypeCode();
                    currentFiscalYear = entry.getUniversityFiscalYear();
                    currentFiscalPeriod = entry.getFiscalPeriodCode();
                    
                    // new top-level header for balance types
                    reportWriterService.pageBreak();
                    formattedTitle = MessageFormat.format(titleMessage, currentFiscalYear.toString(), currentBalanceTypeCode);
                    reportWriterService.writeFormattedMessageLine(formattedTitle);
                    reportWriterService.writeTableHeader(entry);
                } else if (!entry.getUniversityFiscalYear().equals(currentFiscalYear)) {
                    reportWriterService.writeTableRow(balanceTypeFiscalYearAndPeriodTotal);
                    reportWriterService.writeTableRow(balanceTypeFiscalYearTotal);
                    
                    balanceTypeFiscalYearAndPeriodTotal = new PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal(entry.getBalanceTypeCode(), entry.getUniversityFiscalYear(), entry.getFiscalPeriodCode());
                    balanceTypeFiscalYearTotal = new PosterOutputSummaryBalanceTypeFiscalYearTotal(entry.getBalanceTypeCode(), entry.getUniversityFiscalYear());
                    currentFiscalYear = entry.getUniversityFiscalYear();
                    currentFiscalPeriod = entry.getFiscalPeriodCode();
                } else if (!entry.getFiscalPeriodCode().equals(currentFiscalPeriod)) {
                    reportWriterService.writeTableRow(balanceTypeFiscalYearAndPeriodTotal);
                    
                    balanceTypeFiscalYearAndPeriodTotal = new PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal(entry.getBalanceTypeCode(), entry.getUniversityFiscalYear(), entry.getFiscalPeriodCode());
                    currentFiscalPeriod = entry.getFiscalPeriodCode();
                }
                
                reportWriterService.writeTableRow(entry);
                balanceTypeFiscalYearAndPeriodTotal.addAmount(entry);
                balanceTypeFiscalYearTotal.addAmount(entry);
                balanceTypeTotal.addAmount(entry);
                posterOutputSummaryTotal.addAmount(entry);
            }
            
            reportWriterService.writeTableRow(balanceTypeFiscalYearAndPeriodTotal);
            reportWriterService.writeTableRow(balanceTypeFiscalYearTotal);
            reportWriterService.writeTableRow(balanceTypeTotal);
            reportWriterService.writeNewLines(1);
            reportWriterService.writeTableRow(posterOutputSummaryTotal);
        }
    }
    
    /**
     * @return an implementation of the PosterOutputSummaryService
     */
    public PosterOutputSummaryService getPosterOutputSummaryService() {
        if (posterOutputSummaryService == null) {
            posterOutputSummaryService = SpringContext.getBean(PosterOutputSummaryService.class);
        }
        
        return posterOutputSummaryService;
    }
}
