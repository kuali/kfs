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

import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class TrialBalanceReportPrint {

    private final String ZERO_FOR_REPORT = "0.00";
    
    private final int KEMIDS_SELECTED_COLUMN_NUM = 5;
    private final int REQUEST_INFO_TABLE_WIDTH = 80;
    private final int CRITERIA_TABLE_WIDTH = 80;
    private final int MULTIPLE_KEMID_TABLE_WIDTH = 80;
    private final int KEMID_SELECTED_TABLE_WIDTH = 80;
    private final int TRIAL_BALANCE_TABLE_WIDTH = 100;
    
    private final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private final Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
    private final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.GRAY);
    private final Font regularGreyFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, Color.GRAY); 
    
    public boolean printTrialBalanceReport(ReportRequestHeaderDataHolder reportRequestHeaderDataHolder, List<TrialBalanceReportDataHolder> trialBalanceDataReportHolders, HttpServletResponse response) {
        
        final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceReportPrint.class);
        
        response.setContentType("application/pdf");
        
        Document document = new Document();
        document.setPageSize(PageSize.LETTER);
        document.addTitle("Endowment Trial Balance");

        try {            
            if (trialBalanceDataReportHolders != null && trialBalanceDataReportHolders.size() > 1) {            
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();
    
                // page
                HeaderFooter header = new HeaderFooter(new Phrase(new Date().toString() + "     Page: ", headerFont), true);
                header.setBorder(Rectangle.NO_BORDER);
                header.setAlignment(Element.ALIGN_RIGHT);
                header.setPageNumber(0);
                document.setHeader(header);
                
                // print the report header
                if (printReportHeaderPage(reportRequestHeaderDataHolder, document)) {
                    
                    // page break
                    Chunk chunk = new Chunk("");
                    chunk.setNewPage();
                    Paragraph newPage= new Paragraph (chunk);
                    document.add(newPage);
                    
                    // print Trial Balance report
                    if (printTrialBalanceReport(trialBalanceDataReportHolders, document)) {
                        LOG.error("Report Body Error");
                    }                    
                } else {
                    LOG.error("Report Header Error");
                }
            } else {
                Paragraph message = new Paragraph("No kemids found for report");
                document.add(message);
            }
            
            document.close();

        } catch (Exception e) {
            LOG.error("PDF Error: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    public boolean printTrialBalanceReport(List<TrialBalanceReportDataHolder> trialBalanceReports, Document document) {
                
        try {            
            // title
            Paragraph title = new Paragraph("KEMID Trial Balance");
            title.setAlignment(Element.ALIGN_CENTER);
            title.add("\nAs of <Date>\n\n");
            document.add(title);
               
            // report table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(TRIAL_BALANCE_TABLE_WIDTH);
            
            // table titles
            table.addCell(new Phrase("KEMID", titleFont));
            table.addCell(new Phrase("KEMID\nName", titleFont));
            table.addCell(new Phrase("Income\nCash\nBalance", titleFont));
            table.addCell(new Phrase("Principal\nCash\nBalance", titleFont));
            table.addCell(new Phrase("KEMID Total\nMarket Value", titleFont));
            table.addCell(new Phrase("Available\nExpendable\nFunds", titleFont));
            table.addCell(new Phrase("FY\nRemainder\nEstimated\nIncome", titleFont));
            
            // table body
            for (TrialBalanceReportDataHolder trialBalanceReport : trialBalanceReports) {
                table.addCell(new Phrase(trialBalanceReport.getKemid(), regularFont));
                table.addCell(new Phrase(trialBalanceReport.getKemidName(), regularFont));
                if (trialBalanceReport.getInocmeCashBalance() != null) { 
                    String amount = formatAmount(trialBalanceReport.getInocmeCashBalance().bigDecimalValue());
                    table.addCell(new Phrase(amount, regularFont));
                } else { 
                    table.addCell(new Phrase(ZERO_FOR_REPORT, regularFont));
                }
                if (trialBalanceReport.getPrincipalcashBalance() != null) {
                    String amount = formatAmount(trialBalanceReport.getPrincipalcashBalance().bigDecimalValue());
                    table.addCell(new Phrase(amount, regularFont));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, regularFont));
                }
                if (trialBalanceReport.getKemidTotalMarketValue() != null) {
                    String amount = formatAmount(trialBalanceReport.getKemidTotalMarketValue());
                    table.addCell(new Phrase(amount, regularFont));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, regularFont));
                }
                if (trialBalanceReport.getAvailableExpendableFunds() != null) {
                    String amount = formatAmount(trialBalanceReport.getAvailableExpendableFunds());
                    table.addCell(new Phrase(amount, regularFont));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, regularFont));
                }
                if (trialBalanceReport.getFyRemainderEstimatedIncome() != null) {
                    String amount = formatAmount(trialBalanceReport.getFyRemainderEstimatedIncome());
                    table.addCell(new Phrase(amount, regularFont));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, regularFont));
                }
            }
            // count page
//            You need to process the output from a PdfWriter to a bytestream first with a dummy page count.
//            Then create a PdfReader from that bytestream, calling PdfReader.getNumberOfPages to get the actual page count.
//            Then recreate the PDF output, knowing what the page count will be, changing the footer accordingly.
            
            document.add(table);
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
       
    public boolean printReportHeaderPage(ReportRequestHeaderDataHolder reportRequestHeaderDataHolder, Document document) {
        
        try {
            // report header
            Phrase header = new Paragraph(new Date().toString());
            Paragraph title = new Paragraph(reportRequestHeaderDataHolder.getInstitutionName());
            title.setAlignment(Element.ALIGN_CENTER);
            title.add("\nReport Request Header Sheet\n\n");
            document.add(title);
            
            PdfPTable requestTable = new PdfPTable(2);
            requestTable.setWidthPercentage(REQUEST_INFO_TABLE_WIDTH);
            
            Paragraph reportRequested = new Paragraph(reportRequestHeaderDataHolder.getReportRequested(), regularFont);
            Paragraph dateRequested = new Paragraph(new Date().toString(), regularFont);
            Paragraph requestedBy = new Paragraph(reportRequestHeaderDataHolder.getRequestedBy(), regularFont);
            Paragraph endowmentOption = new Paragraph(reportRequestHeaderDataHolder.getEndowmentOption(), regularFont);
            Paragraph reportOption = new Paragraph(reportRequestHeaderDataHolder.getReportOption(), regularFont);
            
            requestTable.addCell(new Paragraph("Report Requested:", regularFont));
            requestTable.addCell(reportRequested);
            requestTable.addCell(new Paragraph("Date Requested:", regularFont));
            requestTable.addCell(dateRequested);
            requestTable.addCell(new Paragraph("Reqeusted by:", regularFont));
            requestTable.addCell(requestedBy);
            requestTable.addCell(new Paragraph("Endowment Option:", regularFont));
            requestTable.addCell(endowmentOption);
            requestTable.addCell(new Paragraph("Report Option:", regularFont));
            requestTable.addCell(reportOption);
            document.add(requestTable);
            
            // Criteria
            Paragraph criteria = new Paragraph("\nCriteria:\n\n");
            document.add(criteria);
            
            PdfPTable criteriaTable = new PdfPTable(2);
            criteriaTable.setWidthPercentage(CRITERIA_TABLE_WIDTH);
    
            Paragraph benefittingCampus = new Paragraph(reportRequestHeaderDataHolder.getBenefittingCampus(), regularFont);
            Paragraph benefittingChart = new Paragraph(reportRequestHeaderDataHolder.getBenefittingChart(), regularFont);
            Paragraph benefittingOrganization = new Paragraph(reportRequestHeaderDataHolder.getBenefittingOrganization(), regularFont);
            Paragraph kemidTypeCode = new Paragraph(reportRequestHeaderDataHolder.getKemidTypeCode(), regularFont);
            Paragraph kemidPurposeCode = new Paragraph(reportRequestHeaderDataHolder.getKemidPurposeCode(), regularFont);            
            Paragraph combinationGroupCode = new Paragraph(reportRequestHeaderDataHolder.getCombineGroupCode(), regularFont);
            
            criteriaTable.addCell(new Paragraph("Benefitting Campus:", regularFont));
            criteriaTable.addCell(benefittingCampus);
            criteriaTable.addCell(new Paragraph("Benefitting Chart:", regularFont));
            criteriaTable.addCell(benefittingChart);
            criteriaTable.addCell(new Paragraph("Benefitting Organization:", regularFont));
            criteriaTable.addCell(benefittingOrganization);
            criteriaTable.addCell(new Paragraph("KEMID Type Code:", regularFont));
            criteriaTable.addCell(kemidTypeCode);
            criteriaTable.addCell(new Paragraph("KEMID Purpose Code:", regularFont));
            criteriaTable.addCell(kemidPurposeCode);
            criteriaTable.addCell(new Paragraph("Combine Group Code:", regularFont));
            criteriaTable.addCell(combinationGroupCode);
            document.add(criteriaTable);
            
            // kemids with multiple benefitting organization
            Paragraph kemidWithMultipleBenefittingOrganization = new Paragraph("\nKEMIDs with Multiple Benefitting Organizations:\n\n");
            document.add(kemidWithMultipleBenefittingOrganization);
            
            List<KemidsWithMultipleBenefittingOrganizationsDataHolder> kemidsWithMultipleBenefittingOrganizationsDataHolder = reportRequestHeaderDataHolder.getKemidsWithMultipleBenefittingOrganizationsDataHolders();
            if (kemidsWithMultipleBenefittingOrganizationsDataHolder != null && !kemidsWithMultipleBenefittingOrganizationsDataHolder.isEmpty()) {
                PdfPTable kemidWithMultipleBenefittingOrganizationTable = new PdfPTable(5);
                kemidWithMultipleBenefittingOrganizationTable.setWidthPercentage(MULTIPLE_KEMID_TABLE_WIDTH);
                
                Paragraph kemid = new Paragraph("KEMID", regularFont);
                Paragraph campus = new Paragraph("Campus", regularFont);
                Paragraph chart = new Paragraph("Chart", regularFont);
                Paragraph organization = new Paragraph("Organziation", regularFont);
                Paragraph percent = new Paragraph("Percent", regularFont);
                kemidWithMultipleBenefittingOrganizationTable.addCell(kemid);
                kemidWithMultipleBenefittingOrganizationTable.addCell(campus);
                kemidWithMultipleBenefittingOrganizationTable.addCell(chart);
                kemidWithMultipleBenefittingOrganizationTable.addCell(organization);
                kemidWithMultipleBenefittingOrganizationTable.addCell(percent);
                
                for (KemidsWithMultipleBenefittingOrganizationsDataHolder kmbo : kemidsWithMultipleBenefittingOrganizationsDataHolder) {
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getKemid(), regularFont));
                    //kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getCampus(), regularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph("", regularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getChart(), regularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getOrganization(), regularFont));            
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getPercent().toString(), regularFont));
                }
                document.add(kemidWithMultipleBenefittingOrganizationTable);
            }
            
            // kemids selected
            List<String> kemidsSelected = reportRequestHeaderDataHolder.getKemidsSelected();
            int totalKemidsSelected = reportRequestHeaderDataHolder.getKemidsSelected().size() - 1;
            Paragraph kemidsSelectedTitle = new Paragraph("\nKEMIDs Selected: " + totalKemidsSelected + "\n\n");
            document.add(kemidsSelectedTitle);
            
            PdfPTable kemidsTable = new PdfPTable(KEMIDS_SELECTED_COLUMN_NUM);
            kemidsTable.setWidthPercentage(KEMID_SELECTED_TABLE_WIDTH);
            
            for (int i = 0; i < totalKemidsSelected ; i++) {
                kemidsTable.addCell(new Paragraph(kemidsSelected.get(i), regularFont) );                
            }
            // to fill out the rest of the empty cells. Otherwise, the row won't be displayed
            if (totalKemidsSelected % KEMIDS_SELECTED_COLUMN_NUM != 0) {
                for (int i = 0; i < (KEMIDS_SELECTED_COLUMN_NUM - totalKemidsSelected % KEMIDS_SELECTED_COLUMN_NUM) ; i++) {
                    kemidsTable.addCell("");
                }
            }
            document.add(kemidsTable);
        
        } catch (Exception e) {
            return false;
        }  

        return true;
    }
    
    protected String formatAmount(BigDecimal amount) {        
        
        String amountString = "";;
        NumberFormat formatter = new DecimalFormat("#,###,###,###,###,###,##0.00");
        
        if (amount.doubleValue() < 0) {
            amount = amount.negate();
            amountString = "(" + formatter.format(amount.doubleValue()) + ")";
            
        } else {
            amountString = formatter.format(amount.doubleValue());
        }
        return amountString;         
    }
}
