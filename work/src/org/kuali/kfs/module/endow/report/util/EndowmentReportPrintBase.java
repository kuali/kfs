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

import org.kuali.kfs.module.endow.report.util.EndowmentReportFooterDataHolder.BenefittingForFooter;

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
    
    public static final Font headerShheetTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
    public static final Font headerSheetRegularFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
    
    public static final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
    public static final Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
    public static final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.GRAY);
    
    public static final Font footerTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD);
    public static final Font footerRegularFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, Color.DARK_GRAY);
    
    protected static final Rectangle LETTER_PORTRAIT = PageSize.LETTER;
    protected static final Rectangle LETTER_LANDSCAPE = PageSize.LETTER.rotate();
    
    protected static final String FORMAT192 = "#,###,###,###,###,###,##0.00";
    protected static final String FORMAT195 = "##,###,###,###,##0.00000";
    protected static final String FORMAT164 = "###,###,###,##0.0000";
    
    /** 
     * Generates the report header sheet
     * 
     * @param reportRequestHeaderDataHolder
     * @param document
     * @return
     */
    public boolean printReportHeaderPage(EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder, Document document, String listKemidsInHeader) {
        
        try {
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
                        
            Paragraph reportRequested = new Paragraph(reportRequestHeaderDataHolder.getReportRequested(), headerSheetRegularFont);
            Paragraph dateRequested = new Paragraph(new Date().toString(), headerSheetRegularFont);
            Paragraph requestedBy = new Paragraph(reportRequestHeaderDataHolder.getRequestedBy(), headerSheetRegularFont);
            Paragraph endowmentOption = new Paragraph(reportRequestHeaderDataHolder.getEndowmentOption(), headerSheetRegularFont);
            Paragraph reportOption = new Paragraph(reportRequestHeaderDataHolder.getReportOption(), headerSheetRegularFont);
            
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
    
            Paragraph benefittingCampus = new Paragraph(reportRequestHeaderDataHolder.getBenefittingCampus(), headerSheetRegularFont);
            Paragraph benefittingChart = new Paragraph(reportRequestHeaderDataHolder.getBenefittingChart(), headerSheetRegularFont);
            Paragraph benefittingOrganization = new Paragraph(reportRequestHeaderDataHolder.getBenefittingOrganization(), headerSheetRegularFont);
            Paragraph kemidTypeCode = new Paragraph(reportRequestHeaderDataHolder.getKemidTypeCode(), headerSheetRegularFont);
            Paragraph kemidPurposeCode = new Paragraph(reportRequestHeaderDataHolder.getKemidPurposeCode(), headerSheetRegularFont);            
            Paragraph combinationGroupCode = new Paragraph(reportRequestHeaderDataHolder.getCombineGroupCode(), headerSheetRegularFont);
            
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
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getKemid(), headerSheetRegularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getCampus(), headerSheetRegularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getChart(), headerSheetRegularFont));
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getOrganization(), headerSheetRegularFont));            
                    kemidWithMultipleBenefittingOrganizationTable.addCell(new Paragraph(kmbo.getPercent().toString(), headerSheetRegularFont));
                }
                document.add(kemidWithMultipleBenefittingOrganizationTable);
            } else {
                Paragraph noneExistMessage = new Paragraph("NONE EXIST\n\n", headerSheetRegularFont);
                document.add(noneExistMessage);
            }
            
            // kemids selected
            if ("Y".equalsIgnoreCase(listKemidsInHeader)) {
                List<String> kemidsSelected = reportRequestHeaderDataHolder.getKemidsSelected();
                int totalKemidsSelected = reportRequestHeaderDataHolder.getKemidsSelected().size();
                Paragraph kemidsSelectedTitle = new Paragraph("\nKEMIDs Selected: " + totalKemidsSelected + "\n\n");
                document.add(kemidsSelectedTitle);
                
                PdfPTable kemidsTable = new PdfPTable(KEMIDS_SELECTED_COLUMN_NUM);
                kemidsTable.setWidthPercentage(KEMID_SELECTED_TABLE_WIDTH);
                kemidsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                
                for (int i = 0; i < totalKemidsSelected ; i++) {
                    kemidsTable.addCell(new Paragraph(kemidsSelected.get(i), headerSheetRegularFont) );                
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
    
        if (footerData == null) {
            return false;
        }
        
        try {
            document.add(new Phrase("\n"));
            
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(FULL_TABLE_WIDTH);
            int[] colWidths = {40, 60};
            table.setWidths(colWidths);
            table.getDefaultCell().setPadding(2);
            
            // left column
            PdfPTable leftTable = new PdfPTable(2);
            leftTable.setWidths(colWidths);
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
            
            rightTable.addCell(createCell("Campus", footerTitleFont, Element.ALIGN_LEFT, false));
            rightTable.addCell(createCell("Chart", footerTitleFont, Element.ALIGN_LEFT, false));
            rightTable.addCell(createCell("Organizaztion", footerTitleFont, Element.ALIGN_LEFT, false));
            rightTable.addCell(createCell("Percent", footerTitleFont, Element.ALIGN_LEFT, false));
            
            List<BenefittingForFooter> benefittingList = footerData.getBenefittingList();
            if (benefittingList != null) {
                for (BenefittingForFooter benefitting : benefittingList) {
                    rightTable.addCell(createCell(benefitting.getCampusName(), footerRegularFont, Element.ALIGN_LEFT, Element.ALIGN_TOP, false));
                    rightTable.addCell(createCell(benefitting.getChartName(), footerRegularFont, Element.ALIGN_LEFT, Element.ALIGN_TOP, false));
                    rightTable.addCell(createCell(benefitting.getOrganizationName(), footerRegularFont, Element.ALIGN_LEFT, Element.ALIGN_TOP, false));
                    rightTable.addCell(createCell(benefitting.getBenefittingPercent(), footerRegularFont, Element.ALIGN_LEFT, Element.ALIGN_TOP, false));
                }
            }
            
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
        return createCell(s, headerSheetRegularFont, alignment, false);
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
    public PdfPCell createCell(String contents, Font font, int horizontalAlignment, boolean borderLine) {
        return createCell(contents, font, horizontalAlignment, Element.ALIGN_BOTTOM, borderLine);
    } 
    
    /**
     * Creates a call with given font, alignments, and borderline
     * 
     * @param contents
     * @param font
     * @param horizontalAlignment
     * @param verticalAlignment
     * @param borderLine
     * @return
     */
    public PdfPCell createCell(String contents, Font font, int horizontalAlignment, int verticalAlignment, boolean borderLine) {
        if (contents == null) contents = "";
        Phrase phr = new Phrase(contents, font);
        PdfPCell cell = new PdfPCell(phr);
        cell.setHorizontalAlignment(horizontalAlignment);
        if (!borderLine) {
            cell.setBorderWidth(0);
        }
        cell.setVerticalAlignment(verticalAlignment);
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
     * Created a cell with a specific font and format
     * 
     * @param value
     * @param font
     * @param format
     * @return
     */
    protected PdfPCell getAmountCell(BigDecimal value, Font font, String format) {        
        String amount = (value == null) ? ZERO_FOR_REPORT : formatAmount(value, format);        
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
    
    /**
     * Format the dollar amount
     * 
     * @param amount
     * @param format
     * @return
     */
    protected String formatAmount(BigDecimal amount, String format) {        
        NumberFormat formatter = new DecimalFormat(format + ";(" + format + ")");
        return  formatter.format(amount.doubleValue());
    }
    
    /**
     * Converts a string to upper case
     * 
     * @param s
     * @return
     */
    protected String convertToUpperCase(String s) {
        if (s == null) {
            return "";
        } else {
            return s.toUpperCase();
        }
    }
}
