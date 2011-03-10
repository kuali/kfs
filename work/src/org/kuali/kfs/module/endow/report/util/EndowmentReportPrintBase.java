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

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public abstract class EndowmentReportPrintBase {
   
    public final String ZERO_FOR_REPORT = "0.00";
    
    public static final int KEMIDS_SELECTED_COLUMN_NUM = 5;
    public static final int REQUEST_INFO_TABLE_WIDTH = 100;
    public static final int CRITERIA_TABLE_WIDTH = 80;
    public static final int MULTIPLE_KEMID_TABLE_WIDTH = 80;
    public static final int KEMID_SELECTED_TABLE_WIDTH = 80;
    public static final int FULL_TABLE_WIDTH = 100;
    
    public static final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
    public static final Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
    public static final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.GRAY);
    
    public static final Font footerTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD);
    public static final Font footerRegularFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, Color.DARK_GRAY);
    
    protected Rectangle LETTER_PORTRAIT = PageSize.LETTER;
    protected Rectangle LETTER_LANDSCAPE = PageSize.LETTER.rotate();
    
    /** 
     * Generates the report header sheet
     * 
     * @param reportRequestHeaderDataHolder
     * @param document
     * @return
     */
    public boolean printReportHeaderPage(EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder, Document document, String listKemidsInHeader, boolean totalsExists) {
        
        try {
            document.setPageSize(LETTER_PORTRAIT);
            
            // report header
            Phrase header = new Paragraph(new Date().toString());
            Paragraph title = new Paragraph(reportRequestHeaderDataHolder.getInstitutionName());
            title.setAlignment(Element.ALIGN_CENTER);
            title.add("\nReport Request Header Sheet\n\n");
            document.add(title);
            
            PdfPTable requestTable = new PdfPTable(2);
            requestTable.setWidthPercentage(REQUEST_INFO_TABLE_WIDTH);
            int[] requestWidths = {20, 80};
            requestTable.setWidths(requestWidths);
            requestTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                        
            Paragraph reportRequested = new Paragraph(reportRequestHeaderDataHolder.getReportRequested(), regularFont);
            Paragraph dateRequested = new Paragraph(new Date().toString(), regularFont);
            Paragraph requestedBy = new Paragraph(reportRequestHeaderDataHolder.getRequestedBy(), regularFont);
            Paragraph endowmentOption = new Paragraph(reportRequestHeaderDataHolder.getEndowmentOption(), regularFont);
            Paragraph reportOption = new Paragraph(reportRequestHeaderDataHolder.getReportOption(), regularFont);
            
            requestTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Report Requested:", Element.ALIGN_RIGHT));
            requestTable.addCell(reportRequested);
            requestTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Date Requested:", Element.ALIGN_RIGHT));
            requestTable.addCell(dateRequested);
            requestTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Reqeusted by:", Element.ALIGN_RIGHT));
            requestTable.addCell(requestedBy);
            requestTable.addCell("");
            requestTable.addCell("");
            requestTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Endowment Option:", Element.ALIGN_RIGHT));
            requestTable.addCell(endowmentOption);
            requestTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Report Option:", Element.ALIGN_RIGHT));
            requestTable.addCell(reportOption);
            document.add(requestTable);
            
            // Criteria
            Paragraph criteria = new Paragraph("\nCriteria:\n\n");
            document.add(criteria);
            
            PdfPTable criteriaTable = new PdfPTable(2);
            criteriaTable.setWidthPercentage(CRITERIA_TABLE_WIDTH);
            int[] criteriaWidths = {30, 50};
            criteriaTable.setWidths(criteriaWidths);
            criteriaTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
    
            Paragraph benefittingCampus = new Paragraph(reportRequestHeaderDataHolder.getBenefittingCampus(), regularFont);
            Paragraph benefittingChart = new Paragraph(reportRequestHeaderDataHolder.getBenefittingChart(), regularFont);
            Paragraph benefittingOrganization = new Paragraph(reportRequestHeaderDataHolder.getBenefittingOrganization(), regularFont);
            Paragraph kemidTypeCode = new Paragraph(reportRequestHeaderDataHolder.getKemidTypeCode(), regularFont);
            Paragraph kemidPurposeCode = new Paragraph(reportRequestHeaderDataHolder.getKemidPurposeCode(), regularFont);            
            Paragraph combinationGroupCode = new Paragraph(reportRequestHeaderDataHolder.getCombineGroupCode(), regularFont);
            
            criteriaTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Benefitting Campus:", Element.ALIGN_RIGHT));
            criteriaTable.addCell(benefittingCampus);
            criteriaTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Benefitting Chart:", Element.ALIGN_RIGHT));
            criteriaTable.addCell(benefittingChart);
            criteriaTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Benefitting Organization:", Element.ALIGN_RIGHT));
            criteriaTable.addCell(benefittingOrganization);
            criteriaTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("KEMID Type Code:", Element.ALIGN_RIGHT));
            criteriaTable.addCell(kemidTypeCode);
            criteriaTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("KEMID Purpose Code:", Element.ALIGN_RIGHT));
            criteriaTable.addCell(kemidPurposeCode);
            criteriaTable.addCell(createCellWithDefaultFontAndWithoutBorderLine("Combine Group Code:", Element.ALIGN_RIGHT));
            criteriaTable.addCell(combinationGroupCode);
            document.add(criteriaTable);
            
            // kemids with multiple benefitting organization
            Paragraph kemidWithMultipleBenefittingOrganization = new Paragraph("\nKEMIDs with Multiple Benefitting Organizations:\n\n");
            document.add(kemidWithMultipleBenefittingOrganization);
            
            List<KemidsWithMultipleBenefittingOrganizationsDataHolder> kemidsWithMultipleBenefittingOrganizationsDataHolder = reportRequestHeaderDataHolder.getKemidsWithMultipleBenefittingOrganizationsDataHolders();
            if (kemidsWithMultipleBenefittingOrganizationsDataHolder != null && !kemidsWithMultipleBenefittingOrganizationsDataHolder.isEmpty()) {
                PdfPTable kemidWithMultipleBenefittingOrganizationTable = new PdfPTable(5);
                kemidWithMultipleBenefittingOrganizationTable.setWidthPercentage(MULTIPLE_KEMID_TABLE_WIDTH);
                kemidWithMultipleBenefittingOrganizationTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                
                Paragraph kemid = new Paragraph("KEMID", titleFont);
                Paragraph campus = new Paragraph("Campus", titleFont);
                Paragraph chart = new Paragraph("Chart", titleFont);
                Paragraph organization = new Paragraph("Organziation", titleFont);
                Paragraph percent = new Paragraph("Percent", titleFont);
                kemidWithMultipleBenefittingOrganizationTable.addCell(kemid);
                kemidWithMultipleBenefittingOrganizationTable.addCell(campus);
                kemidWithMultipleBenefittingOrganizationTable.addCell(chart);
                kemidWithMultipleBenefittingOrganizationTable.addCell(organization);
                kemidWithMultipleBenefittingOrganizationTable.addCell(percent);
                
                for (KemidsWithMultipleBenefittingOrganizationsDataHolder kmbo : kemidsWithMultipleBenefittingOrganizationsDataHolder) {
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getKemid(), regularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getCampus(), regularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getChart(), regularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getOrganization(), regularFont));            
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getPercent().toString(), regularFont));
                }
                document.add(kemidWithMultipleBenefittingOrganizationTable);
            } else {
                Paragraph noneExistMessage = new Paragraph("NONE EXIST\n\n", regularFont);
                document.add(noneExistMessage);
            }
            
            // kemids selected
            if ("Y".equalsIgnoreCase(listKemidsInHeader)) {
                List<String> kemidsSelected = reportRequestHeaderDataHolder.getKemidsSelected();
                int totalKemidsSelected = reportRequestHeaderDataHolder.getKemidsSelected().size();
                if (totalsExists) totalKemidsSelected--;
                Paragraph kemidsSelectedTitle = new Paragraph("\nKEMIDs Selected: " + totalKemidsSelected + "\n\n");
                document.add(kemidsSelectedTitle);
                
                PdfPTable kemidsTable = new PdfPTable(KEMIDS_SELECTED_COLUMN_NUM);
                kemidsTable.setWidthPercentage(KEMID_SELECTED_TABLE_WIDTH);
                kemidsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                
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
            }
        
        } catch (Exception e) {
            return false;
        }  

        return true;
    }
    
    /**
     * Generates the footer
     * 
     * @param footerData
     * @param document
     */
    public boolean printFooter(EndowmentReportFooterDataHolder footerData, Document document) {
    
        try {
            document.add(new Phrase("\n"));
            
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(FULL_TABLE_WIDTH);
            int[] colWidths = {40, 60};
            table.setWidths(colWidths);
            table.getDefaultCell().setPadding(2);
            
            // left column
            PdfPTable leftTable = new PdfPTable(2);
            leftTable.setWidthPercentage(40);
            
            leftTable.addCell(createCell("Reference: ", footerTitleFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell(footerData.getReference(), footerRegularFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell("Date Established: ", footerTitleFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell(footerData.getEstablishedDate(), footerRegularFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell("KEMID Type: ", footerTitleFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell(footerData.getKemidType(), footerRegularFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell("KEMID Purpose: ", footerTitleFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell(footerData.getKemidPurpose(), footerRegularFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell("Report Run Date: ", footerTitleFont, Element.ALIGN_LEFT, false));
            leftTable.addCell(createCell(footerData.getReportRunDate(), footerRegularFont, Element.ALIGN_LEFT, false));
            table.addCell(leftTable);
            
            // right column
            PdfPTable rightTable = new PdfPTable(4);
            rightTable.setWidthPercentage(60);
            
            PdfPCell cellBenefitting = new PdfPCell(new Paragraph("BENEFITTING\n", titleFont));
            cellBenefitting.setColspan(4);
            cellBenefitting.setBorderWidth(0);
            rightTable.addCell(cellBenefitting);  
            
            rightTable.addCell(createCell(footerData.getCampusName(), footerRegularFont, Element.ALIGN_LEFT, false));
            rightTable.addCell(createCell(footerData.getChartName(), footerRegularFont, Element.ALIGN_LEFT, false));
            rightTable.addCell(createCell(footerData.getOrganizationName(), footerRegularFont, Element.ALIGN_LEFT, false));
            rightTable.addCell(createCell(footerData.getBenefittingPercent(), footerRegularFont, Element.ALIGN_LEFT, false));  
            table.addCell(rightTable);
            
            document.add(table);
            
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Creates a cell
     * 
     * @param s
     * @param alignment
     * @return
     */
    protected PdfPCell createCellWithDefaultFontAndBorderLine(String s, int alignment) throws BadElementException {        
        return createCell(s, regularFont, alignment, true);
    }
    
    /**
     * Creates a cell without borderline
     * 
     * @param s
     * @param alignment
     * @return
     * @throws BadElementException
     */
    protected PdfPCell createCellWithDefaultFontAndWithoutBorderLine(String s, int alignment) throws BadElementException {        
        return createCell(s, regularFont, alignment, false);
    }
    
    /**
     * Create a cell with the given font, alignment, and borderline option 
     * 
     * @param content
     * @param font
     * @param alignment
     * @param borderLine
     * @return
     */
    public PdfPCell createCell(String contents, Font font, int alignment, boolean borderLine) {
        if (contents == null) contents = "";
        Phrase phr = new Phrase(contents, font);
        PdfPCell cell = new PdfPCell(phr);
        cell.setHorizontalAlignment(alignment);
        if (!borderLine) {
            cell.setBorderWidth(0);
        }
        return cell;
    } 
    
    /**
     * Created a cell with a specific font
     * 
     * @param value
     * @param font
     * @return
     */
    protected PdfPCell getAmountCell(BigDecimal value, Font font) {        
        String amount = (value == null) ? ZERO_FOR_REPORT : formatAmount(value);        
        return createCell(amount, font, Element.ALIGN_RIGHT, true);        
    }
    
    /** 
     * Format the dollar amount - 19,2 decimal
     * 
     * @param amount
     * @return
     */
    protected String formatAmount(BigDecimal amount) {                
        NumberFormat formatter = new DecimalFormat("#,###,###,###,###,###,##0.00;(#,###,###,###,###,###,##0.00)");
        return  formatter.format(amount.doubleValue());
    }
}
