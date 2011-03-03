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

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants.IncomePrincipalIndicator;
import org.kuali.kfs.module.endow.report.util.AssetStatementReportDataHolder.ReportGroupData;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class AssetStatementReportPrint extends EndowmentReportPrintBase {
    
    public ByteArrayOutputStream printAssetStatementReport(
            EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder, 
            List<AssetStatementReportDataHolder> endowmentAssetStatementReportDataHolders, 
            List<AssetStatementReportDataHolder> nonEndowedAssetStatementReportDataHolders, 
            String endowmentOption,
            String reportOption, 
            String listKemidsOnHeader) {
        
        final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionStatementReportPrint.class);
        
        Document document = new Document();
        //Document document = new Document(PageSize.LETTER.rotate(), 0, 0, 0, 0);
        document.addTitle("Endowment Asset Statement");
        
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        try {            
            PdfWriter.getInstance(document, pdfStream);            
            document.open();

            // page
            HeaderFooter header = new HeaderFooter(new Phrase(new Date().toString() + "     Page: ", headerFont), true);
            header.setBorder(Rectangle.NO_BORDER);
            header.setAlignment(Element.ALIGN_RIGHT);
            header.setPageNumber(0);
            document.setHeader(header);
            
            if ("Y".equalsIgnoreCase(endowmentOption) && "D".equalsIgnoreCase(reportOption)) {
                if (endowmentAssetStatementReportDataHolders != null && endowmentAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);
                    printAssetStatementReportBodyForEndowment(endowmentAssetStatementReportDataHolders, document, true);
                }
            }
            else if ("Y".equalsIgnoreCase(endowmentOption) && "T".equalsIgnoreCase(reportOption)) {
                if (endowmentAssetStatementReportDataHolders != null && endowmentAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForEndowment(endowmentAssetStatementReportDataHolders, document, false);
                }
            }
            else if ("Y".equalsIgnoreCase(endowmentOption) && "B".equalsIgnoreCase(reportOption)) {
                if (endowmentAssetStatementReportDataHolders != null && endowmentAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForEndowment(endowmentAssetStatementReportDataHolders, document, true);
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForEndowment(endowmentAssetStatementReportDataHolders, document, false);
                }
            }
            else if ("N".equalsIgnoreCase(endowmentOption) && "D".equalsIgnoreCase(reportOption)) {
                if (nonEndowedAssetStatementReportDataHolders != null && nonEndowedAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForNonEndowed(nonEndowedAssetStatementReportDataHolders, document, true);
                }
            }
            else if ("N".equalsIgnoreCase(endowmentOption) && "T".equalsIgnoreCase(reportOption)) {
                if (nonEndowedAssetStatementReportDataHolders != null && nonEndowedAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForNonEndowed(nonEndowedAssetStatementReportDataHolders, document, false);
                }
            }
            else if ("N".equalsIgnoreCase(endowmentOption) && "B".equalsIgnoreCase(reportOption)) {
                if (nonEndowedAssetStatementReportDataHolders != null && nonEndowedAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForNonEndowed(nonEndowedAssetStatementReportDataHolders, document, true);
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForNonEndowed(nonEndowedAssetStatementReportDataHolders, document, false);
                }
            }
            else if ("B".equalsIgnoreCase(endowmentOption) && "D".equalsIgnoreCase(reportOption)) {
                if (endowmentAssetStatementReportDataHolders != null && endowmentAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForEndowment(endowmentAssetStatementReportDataHolders, document, true);
                }
                if (nonEndowedAssetStatementReportDataHolders != null && nonEndowedAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForNonEndowed(nonEndowedAssetStatementReportDataHolders, document, true);
                }
            }
            else if ("B".equalsIgnoreCase(endowmentOption) && "T".equalsIgnoreCase(reportOption)) {
                if (endowmentAssetStatementReportDataHolders != null && endowmentAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForEndowment(endowmentAssetStatementReportDataHolders, document, false);
                }
                if (nonEndowedAssetStatementReportDataHolders != null && nonEndowedAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForNonEndowed(nonEndowedAssetStatementReportDataHolders, document, false);
                }
            }
            else if ("B".equalsIgnoreCase(endowmentOption) && "B".equalsIgnoreCase(reportOption)) {
                if (endowmentAssetStatementReportDataHolders != null && endowmentAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForEndowment(endowmentAssetStatementReportDataHolders, document, true);
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForEndowment(endowmentAssetStatementReportDataHolders, document, false);
                }
                if (nonEndowedAssetStatementReportDataHolders != null && nonEndowedAssetStatementReportDataHolders.size() > 0) {
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForNonEndowed(nonEndowedAssetStatementReportDataHolders, document, true);
                    header.setPageNumber(0);
                    document.setHeader(header);
                    printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsOnHeader, false);                
                    printAssetStatementReportBodyForNonEndowed(nonEndowedAssetStatementReportDataHolders, document, false);
                }

            } else {
                LOG.error("Asset Statement Report Header Error");
            }

            document.close();

        } catch (Exception e) {
            LOG.error("PDF Error: " + e.getMessage());
            return null;
        }
        
        return pdfStream;
    }
    
    /**
     * Generates the Asset Statement report for Endowment    
     * 
     * @param transactionStatementReports
     * @param document
     * @return
     */
    public boolean printAssetStatementReportBodyForEndowment(List<AssetStatementReportDataHolder> endowmentAssetStatementReportDataHolders, Document document, boolean isDetail) {
        
        BigDecimal amount = BigDecimal.ZERO;
        
        document.setPageCount(0);
        document.setPageSize(LETTER_LANDSCAPE);
                
        // for each kemid
        try {                               
            Font cellFont = regularFont;
            for (AssetStatementReportDataHolder reportData : endowmentAssetStatementReportDataHolders) {
                
                // new page
                document.newPage();
                
                // header
                StringBuffer title = new StringBuffer();
                title.append(reportData.getInstitution()).append("\n");
                title.append("STATEMENT OF ASSETS FOR PERIOD ENDING").append("\n");
                title.append(reportData.getMonthEndDate()).append("\n");
                title.append(reportData.getKemid()).append("     ").append(reportData.getKemidLongTitle()).append("\n\n\n");
                Paragraph header = new Paragraph(title.toString());
                header.setAlignment(Element.ALIGN_CENTER);                
                document.add(header);

                // report table
                float[] colsWidth = {15f, 17f, 17f, 17f, 17f, 17f};
                PdfPTable table = new PdfPTable(colsWidth);
                table.setWidthPercentage(FULL_TABLE_WIDTH);                
                table.getDefaultCell().setPadding(5);
                
                // column titles
                table.addCell("");
                table.addCell(createCell("UNITS\nHELD", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("MARKET\nVALUE", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("ESTIMATED\nANNUAL\nINCOME", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("FY\nREMAINDER\nESTIMATED\n\nANNUAL\nINCOME", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("NEXT\nFY\nESTIMATED\n\nANNUAL\nINCOME", titleFont, Element.ALIGN_RIGHT, true));
                              
                // -- Expendable funds -- 
                
                PdfPCell cellExpendableFunds = new PdfPCell(new Paragraph("EXPENDABLE FUNDS\n\nCASH AND\nEQUIVALENTS", titleFont));
                cellExpendableFunds.setColspan(6);
                table.addCell(cellExpendableFunds);

                table.addCell(new Paragraph("Income Cash", cellFont));
                table.addCell("");
                table.addCell(getAmountCell(reportData.getHistoryIncomeCash(), cellFont));
                table.addCell("");
                table.addCell("");
                table.addCell("");
                
                // report groups 
                printReportGroupForIncome(reportData, document, table, cellFont);
                
                // -- Endowed funds -- 
                
                PdfPCell cellEndowedFunds = new PdfPCell(new Paragraph("ENDOWMED FUNDS\\n\nCASH AND\nEQUIVALENTS", titleFont));
                cellEndowedFunds.setColspan(6);
                table.addCell(cellEndowedFunds);
                
                table.addCell(new Paragraph("Principal Cash", cellFont));
                table.addCell("");
                table.addCell(getAmountCell(reportData.getHistoryPrincipalCash(), cellFont));
                table.addCell("");
                table.addCell("");
                table.addCell("");
                                                            
                printReportGroupForPrincipal(reportData, document, table, cellFont);
                
                // footer
                if (isDetail && reportData.getFooter() != null) {
                    printFooter(reportData.getFooter(), document);
                }
            }
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
    
    protected void printReportGroupForIncome(AssetStatementReportDataHolder reportData, Document docuement, PdfPTable table, Font cellFont) {
                
        // report group 1
        Map<String, ReportGroupData> reportGroupCashEquivalents = reportData.getReportGroupsForIncome().get(new Integer(1));
        Iterator<String> iter = reportGroupCashEquivalents.keySet().iterator();
        while (iter.hasNext()) {
            String securityId = iter.next();
            ReportGroupData data = reportGroupCashEquivalents.get(securityId);
            table.addCell(new Paragraph(data.getSecurityDesc(), cellFont));
            table.addCell(getAmountCell(data.getSumOfUnits(), cellFont));
            table.addCell(getAmountCell(data.getSumOfMarketValue(), cellFont));
            table.addCell(getAmountCell(data.getSumOfEstimatedIncome(), cellFont));
            table.addCell(getAmountCell(data.getSumOfRemainderOfFYEstimated(), cellFont));
            table.addCell(getAmountCell(data.getSumOfNextFYEstimatedIncome(), cellFont));
        }

        table.addCell(new Paragraph("TOTAL CASH AND\nEQUIVALENTS", cellFont));
        table.addCell("");
        table.addCell(getAmountCell(reportData.getTotalIncomeMarketValues1().add(reportData.getHistoryIncomeCash()), cellFont));
        table.addCell("");
        table.addCell("");
        table.addCell("");

        // report group n
        Map<Integer, Map<String, ReportGroupData>> otherReportGroups = reportData.getReportGroupsForIncome();
        Iterator<Integer> reportGroupOders = otherReportGroups.keySet().iterator();
        while (reportGroupOders.hasNext()) {                    
            Integer reportGroupOrder = reportGroupOders.next();
            if (reportGroupOrder.intValue() == 1) {
                // report group 1 was already displayed
                continue;
            }
            Map<String, ReportGroupData> otherReportGroup = reportData.getReportGroupsForIncome().get(reportGroupOrder);
            Iterator<String> iter2 = otherReportGroup.keySet().iterator();
            while (iter2.hasNext()) {
                String securityId = iter2.next();
                ReportGroupData data = reportGroupCashEquivalents.get(securityId);
                table.addCell(new Paragraph(data.getReportGroupDesc() + " " + data.getReportGroupOrder(), cellFont));
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell(new Paragraph(data.getSecurityDesc(), cellFont));
                table.addCell(getAmountCell(data.getSumOfUnits(), cellFont));
                table.addCell(getAmountCell(data.getSumOfMarketValue(), cellFont));
                table.addCell(getAmountCell(data.getSumOfEstimatedIncome(), cellFont));
                table.addCell(getAmountCell(data.getSumOfRemainderOfFYEstimated(), cellFont));
                table.addCell(getAmountCell(data.getSumOfNextFYEstimatedIncome(), cellFont));
                
                // totals
                table.addCell(new Paragraph("TOTAL <" + data.getReportGroupDesc() + " " + data.getReportGroupOrder(), cellFont));
                table.addCell("");
                table.addCell(getAmountCell(reportData.getTotalIncomeMarketValuesN().add(reportData.getHistoryIncomeCash()), cellFont));
                table.addCell("");
                table.addCell("");
                table.addCell("");
            }
        }

        // total expendable funds
        table.addCell(new Paragraph("TOTAL EXPENDABLE FUNDS", cellFont));
        table.addCell("");
        table.addCell(getAmountCell(reportData.getTotalSumOfMarketValue(IncomePrincipalIndicator.INCOME), cellFont));
        table.addCell(getAmountCell(reportData.getTotalSumOfEstimatedIncome(IncomePrincipalIndicator.INCOME), cellFont));
        table.addCell(getAmountCell(reportData.getTotalSumOfRemainderOfFYEstimated(IncomePrincipalIndicator.INCOME), cellFont));
        table.addCell(getAmountCell(reportData.getTotalSumOfEstimatedIncome(IncomePrincipalIndicator.INCOME), cellFont));                  
    }

    protected void printReportGroupForPrincipal(AssetStatementReportDataHolder reportData, Document docuement, PdfPTable table, Font cellFont) {
        
        // report group 1
        Map<String, ReportGroupData> reportGroupCashEquivalents = reportData.getReportGroupsForPrincipal().get(new Integer(1));
        Iterator<String> iter = reportGroupCashEquivalents.keySet().iterator();
        while (iter.hasNext()) {
            String securityId = iter.next();
            ReportGroupData data = reportGroupCashEquivalents.get(securityId);
            table.addCell(new Paragraph(data.getSecurityDesc(), cellFont));
            table.addCell(getAmountCell(data.getSumOfUnits(), cellFont));
            table.addCell(getAmountCell(data.getSumOfMarketValue(), cellFont));
            table.addCell(getAmountCell(data.getSumOfEstimatedIncome(), cellFont));
            table.addCell(getAmountCell(data.getSumOfRemainderOfFYEstimated(), cellFont));
            table.addCell(getAmountCell(data.getSumOfNextFYEstimatedIncome(), cellFont));
        }

        table.addCell(new Paragraph("TOTAL CASH AND\nEQUIVALENTS", cellFont));
        table.addCell("");
        table.addCell(getAmountCell(reportData.getTotalPricipalMarketValues1().add(reportData.getHistoryPrincipalCash()), cellFont));
        table.addCell("");
        table.addCell("");
        table.addCell("");

        // report group n
        Map<Integer, Map<String, ReportGroupData>> otherReportGroups = reportData.getReportGroupsForPrincipal();
        Iterator<Integer> reportGroupOders = otherReportGroups.keySet().iterator();
        while (reportGroupOders.hasNext()) {                    
            Integer reportGroupOrder = reportGroupOders.next();
            if (reportGroupOrder.intValue() == 1) {
                // report group 1 was already displayed
                continue;
            }
            Map<String, ReportGroupData> otherReportGroup = reportData.getReportGroupsForPrincipal().get(reportGroupOrder);
            Iterator<String> iter2 = otherReportGroup.keySet().iterator();
            while (iter2.hasNext()) {
                String securityId = iter2.next();
                ReportGroupData data = reportGroupCashEquivalents.get(securityId);
                table.addCell(new Paragraph(data.getReportGroupDesc() + " " + data.getReportGroupOrder(), cellFont));
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell(new Paragraph(data.getSecurityDesc(), cellFont));
                table.addCell(getAmountCell(data.getSumOfUnits(), cellFont));
                table.addCell(getAmountCell(data.getSumOfMarketValue(), cellFont));
                table.addCell(getAmountCell(data.getSumOfEstimatedIncome(), cellFont));
                table.addCell(getAmountCell(data.getSumOfRemainderOfFYEstimated(), cellFont));
                table.addCell(getAmountCell(data.getSumOfNextFYEstimatedIncome(), cellFont));
                
                // totals
                table.addCell(new Paragraph("TOTAL <" + data.getReportGroupDesc() + " " + data.getReportGroupOrder(), cellFont));
                table.addCell("");
                table.addCell(getAmountCell(reportData.getTotalPrincipalMarketValuesN().add(reportData.getHistoryPrincipalCash()), cellFont));
                table.addCell("");
                table.addCell("");
                table.addCell("");
            }
        }

        // total expendable funds
        table.addCell(new Paragraph("TOTAL EXPENDABLE FUNDS", cellFont));
        table.addCell("");
        table.addCell(getAmountCell(reportData.getTotalSumOfMarketValue(IncomePrincipalIndicator.INCOME), cellFont));
        table.addCell(getAmountCell(reportData.getTotalSumOfEstimatedIncome(IncomePrincipalIndicator.INCOME), cellFont));
        table.addCell(getAmountCell(reportData.getTotalSumOfRemainderOfFYEstimated(IncomePrincipalIndicator.INCOME), cellFont));
        table.addCell(getAmountCell(reportData.getTotalSumOfEstimatedIncome(IncomePrincipalIndicator.INCOME), cellFont));                  
    }
    
    /**
     * Generates the Asset Statement report for Non-Endowed    
     * 
     * @param transactionStatementReports
     * @param document
     * @return
     */
    public boolean printAssetStatementReportBodyForNonEndowed(List<AssetStatementReportDataHolder> nonEndowedAssetStatementReportDataHolders, Document document, boolean isDetail) {
        
        BigDecimal amount = BigDecimal.ZERO;
        
        document.setPageCount(0);
                
        // for each kemid
        try {                               
            Font cellFont = regularFont;
            for (AssetStatementReportDataHolder reportData : nonEndowedAssetStatementReportDataHolders) {
                
                // new page
                document.newPage();
                
                // header
                StringBuffer title = new StringBuffer();
                title.append(reportData.getInstitution()).append("\n");
                title.append("STATEMENT OF ASSETS FOR PERIOD ENDING").append("\n");
                title.append(reportData.getMonthEndDate()).append("\n");
                title.append(reportData.getKemid()).append("     ").append(reportData.getKemidLongTitle()).append("\n\n\n");
                Paragraph header = new Paragraph(title.toString());
                header.setAlignment(Element.ALIGN_CENTER);                
                document.add(header);

                // report table
                float[] colsWidth = {15f, 17f, 17f, 17f, 17f, 17f};
                PdfPTable table = new PdfPTable(colsWidth);
                table.setWidthPercentage(FULL_TABLE_WIDTH);                
                table.getDefaultCell().setPadding(5);
                
                // column titles
                table.addCell("");
                table.addCell(createCell("UNITS\nHELD", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("MARKET\nVALUE", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("ESTIMATED\nANNUAL\nINCOME", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("FY\nREMAINDER\nESTIMATED\n\nANNUAL\nINCOME", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("NEXT\nFY\nESTIMATED\n\nANNUAL\nINCOME", titleFont, Element.ALIGN_RIGHT, true));
                              
                // Expendable funds 
                
                PdfPCell cellExpendableFunds = new PdfPCell(new Paragraph("CASH AND\nEQUIVALENTS", titleFont));
                cellExpendableFunds.setColspan(6);
                table.addCell(cellExpendableFunds);

                table.addCell(new Paragraph("Income Cash", cellFont));
                table.addCell("");
                table.addCell(getAmountCell(reportData.getHistoryIncomeCash(), cellFont));
                table.addCell("");
                table.addCell("");
                table.addCell("");
                
                table.addCell(new Paragraph("Principal Cash", cellFont));
                table.addCell("");
                table.addCell(getAmountCell(reportData.getHistoryPrincipalCash(), cellFont));
                table.addCell("");
                table.addCell("");
                table.addCell("");
                
                // report groups 
                printReportGroupForIncome(reportData, document, table, cellFont);
                                
                document.add(table);
                
                // footer
                if (isDetail && reportData.getFooter() != null) {
                    printFooter(reportData.getFooter(), document);
                }
            }
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
}
