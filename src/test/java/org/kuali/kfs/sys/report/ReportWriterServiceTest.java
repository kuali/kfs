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
package org.kuali.kfs.sys.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.GlSummary;
import org.kuali.kfs.gl.businessobject.LedgerEntryForReporting;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;

@ConfigureContext
public class ReportWriterServiceTest extends KualiTestBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportWriterServiceTest.class);
    
    protected ReportWriterService tableReportWriterService;
    protected ReportWriterService colspanTableReportWriterService;
    protected ReportWriterService ledgerReportWriterService;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Map<String, ReportWriterService> businessObjectReportHelperBeans = SpringContext.getBeansOfType(ReportWriterService.class);
        tableReportWriterService = businessObjectReportHelperBeans.get("tableReportWriterService");
        colspanTableReportWriterService = businessObjectReportHelperBeans.get("colspanTableReportWriterService");
        ledgerReportWriterService = businessObjectReportHelperBeans.get("testLedgerReportWriterService");
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
        ((WrappingBatchService) tableReportWriterService).destroy();
        ((WrappingBatchService) colspanTableReportWriterService).destroy();
        ((WrappingBatchService) ledgerReportWriterService).destroy();
    }
    
    public void testWriteTable() throws Exception{             
        ((WrappingBatchService) tableReportWriterService).initialize();
        
        List<GlSummary> summaryList = this.getTestData(40);
        tableReportWriterService.writeTable(summaryList, true, false);
    }
    
    public void testWriteRowWithColspan() throws Exception{
        ((WrappingBatchService) colspanTableReportWriterService).initialize();
        
        List<GlSummary> summaryList = this.getTestData(20);
        colspanTableReportWriterService.writeTableHeader(summaryList.get(0));
        
        int index = 1;
        for(GlSummary summary : summaryList) {
            colspanTableReportWriterService.writeTableRow(summary);
            
            if(index++ % 5 == 0) {
                GlSummary subTotal = new GlSummary();
                subTotal.setFundGroup("Sub Totals (AC):");
                
                colspanTableReportWriterService.writeTableRowWithColspan(subTotal);                
                colspanTableReportWriterService.writeTableRowSeparationLine(summary);
            }
        }
        
        GlSummary grandTotal = new GlSummary();
        grandTotal.setFundGroup("Grand Totals (AC):");
        colspanTableReportWriterService.writeTableRowWithColspan(grandTotal);
    }
    
    public void testLedgerReport() throws Exception{             
        ((WrappingBatchService) ledgerReportWriterService).initialize();
        
        List<LedgerEntryForReporting> ledgerEntries = this.getLedgerEntryTestData(40);
        ledgerReportWriterService.writeTable(ledgerEntries, true, false);
    }

    protected List<LedgerEntryForReporting> getLedgerEntryTestData(int countOfData) {
        List<LedgerEntryForReporting> ledgerEntries = new ArrayList<LedgerEntryForReporting>();
        for(int i = 0; i < countOfData; i++) {
            LedgerEntryForReporting entry = new LedgerEntryForReporting();
            entry.setBalanceType("Bal-" + i);
            entry.setFiscalYear(2000 + i);
            entry.setOriginCode("0" + i);
            entry.setPeriod("P-" + i);
            
            ledgerEntries.add(entry);
        }
        
        return ledgerEntries;
    }

    protected List<GlSummary> getTestData(int countOfData) {
        List<GlSummary> summaryList = new ArrayList<GlSummary>();
        for(int i = 0; i < countOfData; i++) {
            GlSummary summary = new GlSummary();
            summary.setFundGroup("FG-" + i);
            
            summaryList.add(summary);
        }
        
        return summaryList;
    }
}
