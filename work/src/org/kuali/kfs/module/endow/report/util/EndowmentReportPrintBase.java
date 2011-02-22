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
import com.lowagie.text.Chunk;
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
   
    public static final int KEMIDS_SELECTED_COLUMN_NUM = 4;
    public static final int REQUEST_INFO_TABLE_WIDTH = 100;
    public static final int CRITERIA_TABLE_WIDTH = 80;
    public static final int MULTIPLE_KEMID_TABLE_WIDTH = 80;
    public static final int KEMID_SELECTED_TABLE_WIDTH = 80;
    public static final int TRIAL_BALANCE_TABLE_WIDTH = 100;
    
    public static final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
    public static final Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
    public static final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.GRAY);
    
    protected Rectangle pageSize = PageSize.LETTER;
    
    /** 
     * Generates the report header sheet
     * 
     * @param reportRequestHeaderDataHolder
     * @param document
     * @return
     */
    public boolean printReportHeaderPage(ReportRequestHeaderDataHolder reportRequestHeaderDataHolder, Document document, boolean totalsExists) {
        
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
            List<String> kemidsSelected = reportRequestHeaderDataHolder.getKemidsSelected();
            int totalKemidsSelected = reportRequestHeaderDataHolder.getKemidsSelected().size();
            if (totalsExists) totalKemidsSelected--;
            Paragraph kemidsSelectedTitle = new Paragraph("\nKEMIDs Selected: \n\n");
            document.add(kemidsSelectedTitle);
            //Paragraph numberOfKemids = new Paragraph(totalKemidsSelected + "\n\n", regularFont);
            //document.add(numberOfKemids);
            
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
    
    protected PdfPCell createCellWithDefaultFontAndWithoutBorderLine(String s, int alignment) throws BadElementException {        
        return createCell(s, regularFont, alignment, false);
    }
    
    public PdfPCell createCell(String content, Font font, int alignment, boolean borderLine) throws BadElementException {    
      Phrase phr = new Phrase(content, font);
      PdfPCell cell = new PdfPCell(phr);
      cell.setHorizontalAlignment(alignment);
      if (!borderLine) {
          cell.setBorderWidth(0);
      }
      return cell;
    } 
    
    /**
     * Sets the page break
     * 
     * @param document
     * @throws Exception
     */
    protected void setPageBreak(Document document) throws Exception {
        Chunk chunk = new Chunk("");
        chunk.setNewPage();
        Paragraph newPage= new Paragraph (chunk);
        document.add(newPage);
    }
    
    /** 
     * Format the dollar amount - 19,2 decimal
     * 
     * @param amount
     * @return
     */
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
