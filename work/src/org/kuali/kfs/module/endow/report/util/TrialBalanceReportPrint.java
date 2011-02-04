/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report.util;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class TrialBalanceReportPrint {

    public boolean printTrailBalanceReport(List<TrialBalanceReportDataHolder> trialBalanceReports, HttpServletResponse response) {
        
        final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceReportPrint.class);
        
        if (trialBalanceReports != null && trialBalanceReports.size() > 0) {
            
            try {
                Document document = new Document();
                document.setPageSize(PageSize.LETTER);
                document.addTitle("Endowment Trial Balance");
                
                response.setContentType("application/pdf");
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();
    
                // header
                
                // head
                Phrase header = new Paragraph(new Date().toString());
                document.add(header);
                
                Paragraph title = new Paragraph("KEMID Trial Balance");
                title.setAlignment(Element.ALIGN_CENTER);
                title.add("\nAs of <Date>\n\n\n");
                document.add(title);
                           
                PdfPTable table = new PdfPTable(7);
                // titles
                table.addCell("KEMID");
                table.addCell("KEMID Name");
                table.addCell("Income Cash Balance");
                table.addCell("Principal\nCash\nBalance");
                table.addCell("KEMID Total\nMarket Value");
                table.addCell("Available\nExpendable\nFunds");
                table.addCell("FY Remainder\nEstimated\nIncome");
                //body
                for (TrialBalanceReportDataHolder trialBalanceReport : trialBalanceReports) {
                    table.addCell(trialBalanceReport.getKemid());
                    table.addCell(trialBalanceReport.getKemidName());
                    if (trialBalanceReport.getInocmeCashBalance() != null) { 
                        table.addCell(trialBalanceReport.getInocmeCashBalance().toString());
                    } else { 
                        table.addCell("0.00");
                    }
                    if (trialBalanceReport.getPrincipalcashBalance() != null) {
                        table.addCell(trialBalanceReport.getPrincipalcashBalance().toString());
                    } else {
                        table.addCell("0.00");
                    }
                    if (trialBalanceReport.getKemidTotalMarketValue() != null) {
                        table.addCell(trialBalanceReport.getKemidTotalMarketValue().toString());
                    } else {
                        table.addCell("0.00");
                    }
                    if (trialBalanceReport.getAvailableExpendableFunds() != null) {
                        table.addCell(trialBalanceReport.getAvailableExpendableFunds().toString());
                    } else {
                        table.addCell("0.00");
                    }
                    if (trialBalanceReport.getFyRemainderEstimatedIncome() != null) {
                        table.addCell(trialBalanceReport.getFyRemainderEstimatedIncome().toString());
                    } else {
                        table.addCell("0.00");
                    }
                }
                document.add(table);
                document.close();
                
            } catch (Exception e) {
                LOG.error("PDF Error: " + e.getMessage());
                return false;
            }            
        } 
        
        return true;

    }
}
