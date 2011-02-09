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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class TrialBalanceReportPrint {

    private final String ZERO_FOR_REPORT = "0.00";
    private final float TITLE_SIZE = 10f;
    private final float SUB_TITLE_SIZE = 10f;
    private final float REG_SIZE = 8f;
    private final int KEMIDS_SELECTED_COLUMN_NUM = 5;
    private final int REQUEST_INFO_TABLE_WIDTH = 80;
    private final int CRITERIA_TABLE_WIDTH = 80;
    private final int MULTIPLE_KEMID_TABLE_WIDTH = 80;
    private final int KEMID_SELECTED_TABLE_WIDTH = 80;
    private final int TRIAL_BALANCE_TABLE_WIDTH = 100;
    
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
            Phrase header = new Paragraph(new Date().toString());
            document.add(header);
            
            Paragraph title = new Paragraph("KEMID Trial Balance");
            title.setAlignment(Element.ALIGN_CENTER);
            title.add("\nAs of <Date>\n\n");
            document.add(title);
                       
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(TRIAL_BALANCE_TABLE_WIDTH);
            
            // titles
            table.addCell(new Phrase("KEMID", FontFactory.getFont(FontFactory.HELVETICA, TITLE_SIZE)));
            table.addCell(new Phrase("KEMID\nName", FontFactory.getFont(FontFactory.HELVETICA, TITLE_SIZE)));
            table.addCell(new Phrase("Income\nCash\nBalance", FontFactory.getFont(FontFactory.HELVETICA, TITLE_SIZE)));
            table.addCell(new Phrase("Principal\nCash\nBalance", FontFactory.getFont(FontFactory.HELVETICA, TITLE_SIZE)));
            table.addCell(new Phrase("KEMID Total\nMarket Value", FontFactory.getFont(FontFactory.HELVETICA, TITLE_SIZE)));
            table.addCell(new Phrase("Available\nExpendable\nFunds", FontFactory.getFont(FontFactory.HELVETICA, TITLE_SIZE)));
            table.addCell(new Phrase("FY\nRemainder\nEstimated\nIncome", FontFactory.getFont(FontFactory.HELVETICA, TITLE_SIZE)));
            //body
            for (TrialBalanceReportDataHolder trialBalanceReport : trialBalanceReports) {
                table.addCell(new Phrase(trialBalanceReport.getKemid(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                table.addCell(new Phrase(trialBalanceReport.getKemidName(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                if (trialBalanceReport.getInocmeCashBalance() != null) { 
                    String amount = formatAmount(trialBalanceReport.getInocmeCashBalance().bigDecimalValue());
                    table.addCell(new Phrase(amount, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                } else { 
                    table.addCell(new Phrase(ZERO_FOR_REPORT, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                }
                if (trialBalanceReport.getPrincipalcashBalance() != null) {
                    String amount = formatAmount(trialBalanceReport.getPrincipalcashBalance().bigDecimalValue());
                    table.addCell(new Phrase(amount, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                }
                if (trialBalanceReport.getKemidTotalMarketValue() != null) {
                    String amount = formatAmount(trialBalanceReport.getKemidTotalMarketValue());
                    table.addCell(new Phrase(amount, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                }
                if (trialBalanceReport.getAvailableExpendableFunds() != null) {
                    String amount = formatAmount(trialBalanceReport.getAvailableExpendableFunds());
                    table.addCell(new Phrase(amount, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                }
                if (trialBalanceReport.getFyRemainderEstimatedIncome() != null) {
                    String amount = formatAmount(trialBalanceReport.getFyRemainderEstimatedIncome());
                    table.addCell(new Phrase(amount, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
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
            
            Paragraph reportRequested = new Paragraph(reportRequestHeaderDataHolder.getReportRequested(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            Paragraph dateRequested = new Paragraph(new Date().toString(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            Paragraph requestedBy = new Paragraph(reportRequestHeaderDataHolder.getRequestedBy(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            Paragraph endowmentOption = new Paragraph(reportRequestHeaderDataHolder.getEndowmentOption(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            Paragraph reportOption = new Paragraph(reportRequestHeaderDataHolder.getReportOption(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            
            requestTable.addCell(new Paragraph("Report Requested:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            requestTable.addCell(reportRequested);
            requestTable.addCell(new Paragraph("Date Requested:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            requestTable.addCell(dateRequested);
            requestTable.addCell(new Paragraph("Reqeusted by:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            requestTable.addCell(requestedBy);
            requestTable.addCell(new Paragraph("Endowment Option:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            requestTable.addCell(endowmentOption);
            requestTable.addCell(new Paragraph("Report Option:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            requestTable.addCell(reportOption);
            document.add(requestTable);
            
            // Criteria
            Paragraph criteria = new Paragraph("\nCriteria:\n\n");
            document.add(criteria);
            
            PdfPTable criteriaTable = new PdfPTable(2);
            criteriaTable.setWidthPercentage(CRITERIA_TABLE_WIDTH);
    
            Paragraph benefittingCampus = new Paragraph(reportRequestHeaderDataHolder.getBenefittingCampus(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            Paragraph benefittingChart = new Paragraph(reportRequestHeaderDataHolder.getBenefittingChart(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            Paragraph benefittingOrganization = new Paragraph(reportRequestHeaderDataHolder.getBenefittingOrganization(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            Paragraph kemidTypeCode = new Paragraph(reportRequestHeaderDataHolder.getKemidTypeCode(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            Paragraph kemidPurposeCode = new Paragraph(reportRequestHeaderDataHolder.getKemidPurposeCode(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));            
            Paragraph combinationGroupCode = new Paragraph(reportRequestHeaderDataHolder.getCombineGroupCode(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
            
            criteriaTable.addCell(new Paragraph("Benefitting Campus:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            criteriaTable.addCell(benefittingCampus);
            criteriaTable.addCell(new Paragraph("Benefitting Chart:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            criteriaTable.addCell(benefittingChart);
            criteriaTable.addCell(new Paragraph("Benefitting Organization:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            criteriaTable.addCell(benefittingOrganization);
            criteriaTable.addCell(new Paragraph("KEMID Type Code:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            criteriaTable.addCell(kemidTypeCode);
            criteriaTable.addCell(new Paragraph("KEMID Purpose Code:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            criteriaTable.addCell(kemidPurposeCode);
            criteriaTable.addCell(new Paragraph("Combine Group Code:", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
            criteriaTable.addCell(combinationGroupCode);
            document.add(criteriaTable);
            
            // kemids with multiple benefitting organization
            Paragraph kemidWithMultipleBenefittingOrganization = new Paragraph("\nKEMDIs with Multiple Benefitting Organizations:\n\n");
            document.add(kemidWithMultipleBenefittingOrganization);
            
            List<KemidsWithMultipleBenefittingOrganizationsDataHolder> kemidsWithMultipleBenefittingOrganizationsDataHolder = reportRequestHeaderDataHolder.getKemidsWithMultipleBenefittingOrganizationsDataHolders();
            if (kemidsWithMultipleBenefittingOrganizationsDataHolder != null && !kemidsWithMultipleBenefittingOrganizationsDataHolder.isEmpty()) {
                PdfPTable kemidWithMultipleBenefittingOrganizationTable = new PdfPTable(5);
                kemidWithMultipleBenefittingOrganizationTable.setWidthPercentage(MULTIPLE_KEMID_TABLE_WIDTH);
                
                Paragraph kemid = new Paragraph("KEMID", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
                Paragraph campus = new Paragraph("Campus", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
                Paragraph chart = new Paragraph("Chart", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
                Paragraph organization = new Paragraph("Organziation", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
                Paragraph percent = new Paragraph("Percent", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE));
                kemidWithMultipleBenefittingOrganizationTable.addCell(kemid);
                kemidWithMultipleBenefittingOrganizationTable.addCell(campus);
                kemidWithMultipleBenefittingOrganizationTable.addCell(chart);
                kemidWithMultipleBenefittingOrganizationTable.addCell(organization);
                kemidWithMultipleBenefittingOrganizationTable.addCell(percent);
                
                for (KemidsWithMultipleBenefittingOrganizationsDataHolder kmbo : kemidsWithMultipleBenefittingOrganizationsDataHolder) {
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getKemid(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                    //kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getCampus(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph("", FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getChart(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getOrganization(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));            
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getPercent().toString(), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)));
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
                kemidsTable.addCell(new Paragraph(kemidsSelected.get(i), FontFactory.getFont(FontFactory.HELVETICA, REG_SIZE)) );                
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
