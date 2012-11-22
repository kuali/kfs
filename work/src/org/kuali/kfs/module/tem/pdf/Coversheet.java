/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.pdf;

import static com.lowagie.text.Element.ALIGN_RIGHT;
import static com.lowagie.text.PageSize.LETTER;
import static com.lowagie.text.Rectangle.NO_BORDER;
import static java.awt.Color.BLACK;
import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.TEM_FAX_NUMBER;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.module.tem.report.util.BarcodeHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Class representing a PDF Coversheet of the {@link TravelReimbursementDocument}.
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */ 
public class Coversheet implements PdfStream {
    private static final int ALIGNMENT_MARK_HEIGHT = 8;
    private static final int ALIGNMENT_MARK_WIDTH  = 10;
    private static final int ALIGNMENT_MARGIN      = 5;
    private static final int TOP_MARGIN            = 50;

    private String initiatorName;
    private String initiatorPrincipalName;
    private String initiatorPhone;
    private String initiatorEmail;
    private String travelerName;
    private String travelerPrincipalName;
    private String travelerPhone;
    private String travelerEmail;
    private String instructions;
    private String mailTo;
    private String date;
    private String tripId;
    private String destination;
    private String documentNumber;
   

    private Collection<Map<String, String>> expenses;

    /**
     * Sets the initiatorName attribute
     *
     * @param initiatorName is the String initiatorName value 
     */
    public void setInitiatorName(final String initiatorName) {
        this.initiatorName = initiatorName;
    }

    /**
     * Gets the initiatorName attribute
     *
     * @return String value of initiatorName
     */
    public String getInitiatorName() {
        if (initiatorName == null) {
            return "";
        }
        return this.initiatorName;
    }

    /**
     * Sets the initiatorPrincipalName attribute
     *
     * @param initiatorPrincipalName is the String initiatorPrincipalName value 
     */
    public void setInitiatorPrincipalName(final String initiatorPrincipalName) {
        this.initiatorPrincipalName = initiatorPrincipalName;
    }

    /**
     * Gets the initiatorPrincipalName attribute
     *
     * @return String value of initiatorPrincipalName
     */
    public String getInitiatorPrincipalName() {
        if (initiatorPrincipalName == null) {
            return "";
        }
        return this.initiatorPrincipalName;
    }

    /**
     * Sets the initiatorPhone attribute
     *
     * @param initiatorPhone is the String initiatorPhone value 
     */
    public void setInitiatorPhone(final String initiatorPhone) {
        this.initiatorPhone = initiatorPhone;
    }

    /**
     * Gets the initiatorPhone attribute
     *
     * @return String value of initiatorPhone
     */
    public String getInitiatorPhone() {
        if (initiatorPhone == null) {
            return "";
        }
        return this.initiatorPhone;
    }

    /**
     * Sets the initiatorEmail attribute
     *
     * @param initiatorEmail is the String initiatorEmail value 
     */
    public void setInitiatorEmail(final String initiatorEmail) {
        this.initiatorEmail = initiatorEmail;
    }

    /**
     * Gets the initiatorEmail attribute
     *
     * @return String value of initiatorEmail
     */
    public String getInitiatorEmail() {
        if (initiatorEmail == null) {
            return "";
        }
        return this.initiatorEmail;
    }

    /**
     * Sets the travelerName attribute
     *
     * @param travelerName is the String travelerName value 
     */
    public void setTravelerName(final String travelerName) {
        this.travelerName = travelerName;
    }

    /**
     * Gets the travelerName attribute
     *
     * @return String value of travelerName
     */
    public String getTravelerName() {
        if (travelerName == null) {
            return "";
        }
                
        return this.travelerName;
    }

    /**
     * Sets the travelerPrincipalName attribute
     *
     * @param travelerPrincipalName is the String travelerPrincipalName value 
     */
    public void setTravelerPrincipalName(final String travelerPrincipalName) {
        this.travelerPrincipalName = travelerPrincipalName;
    }

    /**
     * Gets the travelerPrincipalName attribute
     *
     * @return String value of travelerPrincipalName
     */
    public String getTravelerPrincipalName() {
        if (travelerPrincipalName == null) {
            return "";
        }
        return this.travelerPrincipalName;
    }

    /**
     * Sets the travelerPhone attribute
     *
     * @param travelerPhone is the String travelerPhone value 
     */
    public void setTravelerPhone(final String travelerPhone) {
        this.travelerPhone = travelerPhone;
    }

    /**
     * Gets the travelerPhone attribute
     *
     * @return String value of travelerPhone
     */
    public String getTravelerPhone() {
        if (travelerPhone == null) {
            return "";
        }
        return this.travelerPhone;
    }

    /**
     * Sets the travelerEmail attribute
     *
     * @param travelerEmail is the String travelerEmail value 
     */
    public void setTravelerEmail(final String travelerEmail) {
        this.travelerEmail = travelerEmail;
    }

    /**
     * Gets the travelerEmail attribute
     *
     * @return String value of travelerEmail
     */
    public String getTravelerEmail() {
        if (travelerEmail == null) {
            return "";
        }
        return this.travelerEmail;
    }

    /**
     * Sets the instructions attribute
     *
     * @param instructions is the String instructions value 
     */
    public void setInstructions(final String instructions) {
        this.instructions = instructions;
    }

    /**
     * Gets the instructions attribute
     *
     * @return String value of instructions
     */
    public String getInstructions() {
        if (instructions == null) {
            return "";
        }
        return this.instructions;
    }

    /**
     * Sets the date attribute
     *
     * @param date is the String date value 
     */
    public void setDate(final String date) {
        this.date = date;
    }

    /**
     * Gets the date attribute
     *
     * @return String value of date
     */
    public String getDate() {
        if (date == null) {
            return "";
        }
        return this.date;
    }

    /**
     * Sets the mailTo attribute
     *
     * @param mailTo is the String mailTo value 
     */
    public void setMailTo(final String mailTo) {
        this.mailTo = mailTo;
    }

    /**
     * Gets the mailTo attribute
     *
     * @return String value of mailTo
     */
    public String getMailTo() {
        if (mailTo == null) {
            return "";
        }
        return this.mailTo;
    }

    /**
     * Sets the destination attribute
     *
     * @param destination is the String destination value 
     */
    public void setDestination(final String destination) {
        this.destination = destination;
    }

    /**
     * Gets the destination attribute
     *
     * @return String value of destination
     */
    public String getDestination() {
        if (destination == null) {
            return "";
        }

        return this.destination;
    }

    /**
     * Sets the tripId attribute
     *
     * @param tripId is the String tripId value 
     */
    public void setTripId(final String tripId) {
        this.tripId = tripId;
    }

    /**
     * Gets the tripId attribute
     *
     * @return String value of tripId
     */
    public String getTripId() {
        if (tripId == null) {
            return "";
        }
        return this.tripId;
    }

    /**
     * Sets the documentNumber attribute
     *
     * @param documentNumber is the String documentNumber value 
     */
    public void setDocumentNumber(final String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the documentNumber attribute
     *
     * @return String value of documentNumber
     */
    public String getDocumentNumber() {
        return this.documentNumber;
    }

    /**
     * Creates instructions section of the coverpage
     *
     * @returns a {@link Paragraph} for the PDF
     */
    protected Paragraph getInstructionsParagraph() {
        final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
        final Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        final Paragraph retval = new Paragraph();
        retval.add(new Chunk("Instructions", headerFont));
        retval.add(Chunk.NEWLINE);
        retval.add(new Phrase(getInstructions(), normalFont));
        return retval;
    }

    /**
     * Creates mailTo section for the coversheet. The MailTo section is where the coversheet will be mailed.
     *
     * @returns a {@link Paragraph} for the PDF
     */
    protected Paragraph getMailtoParagraph() {
        final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
        final Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        final Paragraph retval = new Paragraph();
        retval.add(new Chunk("Mail coversheet to:", headerFont));
        retval.add(Chunk.NEWLINE);
        retval.add(new Phrase(getMailTo(), normalFont));
        return retval;
    }

    /**
     * Information about the person that initiated the {@link TravelReimbursementDocument}
     */
    protected Cell getInitiatorInfo() throws BadElementException {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getInitiatorName()).append("\n")
            .append(getInitiatorPrincipalName()).append("\n")
            .append(getInitiatorPhone()).append("\n")
            .append(getInitiatorEmail()).append("\n");
        final Cell retval = getBorderlessCell(strBuilder.toString());
        return retval;
    }

    /**
     * Information about the traveler described in the trip for the {@link TravelReimbursementDocument}
     * 
     */
    protected Cell getTravelerInfo() throws BadElementException {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getTravelerName()).append("\n")
            .append(getTravelerPrincipalName()).append("\n")
            .append(getTravelerPhone()).append("\n")
            .append(getTravelerEmail()).append("\n");
        final Cell retval = getBorderlessCell(strBuilder.toString());
        return retval;
    }

    /**
     * Get the PDF Table containing trip information like trip id, date, and destination
     * 
     * @returns {@link Table} used for a PDF
     */
    protected Table getTripInfo() throws BadElementException {
        final Table retval = new Table(3);
        retval.setWidth(100f);
        retval.setBorder(NO_BORDER);
        retval.addCell(getHeaderCell("Trip/Event ID"));
        
        final Cell dateHeaderCell = getHeaderCell("Date");

        retval.addCell(dateHeaderCell);
        retval.addCell(getHeaderCell("Destination/Event Name"));
        retval.endHeaders();
        retval.addCell(getBorderlessCell(getTripId()));
        
        final Cell dateCell = getBorderlessCell(getDate());

        retval.addCell(dateCell);
        retval.addCell(getBorderlessCell(getDestination()));
        return retval;
    }

    /**
     * Get the PDF Table with personal information about the initiator and traveler
     *
     * @returns {@link Table} used for a PDF
     */
    protected Table getPersonalInfo() throws BadElementException {
        final Table retval = new Table(2);
        retval.setWidth(100f);
        retval.setBorder(NO_BORDER);
        retval.addCell(getHeaderCell("Traveler"));
        
        final Cell initiatorHeaderCell = getHeaderCell("Request Submitted By");

        retval.addCell(initiatorHeaderCell);
        retval.endHeaders();
        retval.addCell(getTravelerInfo());

        final Cell initiatorCell = getInitiatorInfo();

        retval.addCell(initiatorCell);
        return retval;
    }

    public void setExpenses(final Collection<Map<String, String>> expenses) {
        this.expenses = expenses;
    }
            
    public Table getExpenses() throws BadElementException {
        final Table retval = new Table(3);
        retval.setWidth(100f);
        retval.setBorder(NO_BORDER);
        retval.addCell(getHeaderCell("Expenses"));
        retval.addCell(getHeaderCell("Amount"));
        retval.addCell(getHeaderCell("Receipt Required?"));
        retval.endHeaders();
        
        for (final Map<String, String> expense : expenses) {
            retval.addCell(getBorderlessCell(expense.get("expenseType")));
            retval.addCell(getBorderlessCell(expense.get("amount")));
            retval.addCell(getBorderlessCell(expense.get("receipt")));
        }
        return retval;
    }

    /**
     * Helper method to create a Header Cell from text
     *
     * @returns {@link Cell} with the header flag set
     */
    protected Cell getBorderlessCell(final String text) throws BadElementException {
        final Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        final Cell retval = new Cell(new Chunk(text, normalFont));
        retval.setBorder(NO_BORDER);
        return retval;
    }

    /**
     * Helper method to create a Header Cell from text
     *
     * @returns {@link Cell} with the header flag set
     */
    protected Cell getHeaderCell(final String text) throws BadElementException {
        final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
        final Cell retval = new Cell(new Chunk(text, headerFont));
        retval.setBorder(NO_BORDER);
        retval.setHeader(true);
        return retval;
    }

    protected void upperLeftAlignmentMark(final PdfContentByte cb) {
        cb.saveState();
        cb.rectangle(ALIGNMENT_MARGIN, 
                     (LETTER.height() + TOP_MARGIN) - ALIGNMENT_MARK_HEIGHT - ALIGNMENT_MARGIN,
                     ALIGNMENT_MARK_WIDTH, 
                     ALIGNMENT_MARK_HEIGHT);
        cb.setColorFill(BLACK);
        cb.fill();
        cb.restoreState();
    }

    protected void lowerLeftAlignmentMark(final PdfContentByte cb) {
        cb.saveState();
        cb.rectangle(ALIGNMENT_MARGIN, 
                     ALIGNMENT_MARGIN, 
                     ALIGNMENT_MARK_WIDTH,
                     ALIGNMENT_MARK_HEIGHT);
        cb.setColorFill(BLACK);
        cb.fill();
        cb.restoreState();        
    }

    protected void lowerRightAlignmentMark(final PdfContentByte cb) {
        cb.saveState();
        cb.rectangle(LETTER.width() - (ALIGNMENT_MARGIN * 4) - ALIGNMENT_MARK_WIDTH,
                     ALIGNMENT_MARGIN,
                     ALIGNMENT_MARK_WIDTH,
                     ALIGNMENT_MARK_HEIGHT);
        cb.setColorFill(BLACK);
        cb.fill();
        cb.restoreState();
    }

    protected void upperRightAlignmentMark(final PdfContentByte cb) {
        cb.saveState();
        cb.rectangle(LETTER.width() - (ALIGNMENT_MARGIN * 4) - ALIGNMENT_MARK_WIDTH,
                     (LETTER.height() + TOP_MARGIN) - ALIGNMENT_MARGIN - ALIGNMENT_MARK_HEIGHT,
                     ALIGNMENT_MARK_WIDTH,
                     ALIGNMENT_MARK_HEIGHT);
        cb.setColorFill(BLACK);
        cb.fill();
        cb.restoreState();
    }

    protected void drawAlignmentMarks(final PdfContentByte cb) {
        upperLeftAlignmentMark(cb);
        lowerLeftAlignmentMark(cb);
        upperRightAlignmentMark(cb);
        lowerRightAlignmentMark(cb);
    }

    /**
     * @see org.kuali.kfs.module.tem.pdf.PdfStream#print(java.io.OutputStream)
     * @throws Exception
     */
    @Override
    public void print(final OutputStream stream) throws Exception {
        final Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD);
        final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
        final Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);

        final Document doc = new Document();
        final PdfWriter writer = PdfWriter.getInstance(doc, stream);
        doc.open();
        if(getDocumentNumber()!=null){
            Image image=Image.getInstance(new BarcodeHelper().generateBarcodeImage(getDocumentNumber()),null);
            doc.add(image);
        }
        
        final Paragraph title = new Paragraph("TEM Coversheet", titleFont);
        doc.add(title);
        
        final Paragraph faxNumber = new Paragraph("Fax this page to " + SpringContext.getBean(ParameterService.class).getParameterValueAsString(PARAM_NAMESPACE, PARAM_DTL_TYPE, TEM_FAX_NUMBER), normalFont);
        doc.add(faxNumber);

        final Paragraph header = new Paragraph("", headerFont);
        header.setAlignment(ALIGN_RIGHT);
        header.add("Document Number: " + getDocumentNumber());
        doc.add(header);
        doc.add(getInstructionsParagraph());
        doc.add(getMailtoParagraph());
        doc.add(Chunk.NEWLINE);
        doc.add(getTripInfo());
        doc.add(Chunk.NEWLINE);
        doc.add(getPersonalInfo());
        doc.add(Chunk.NEWLINE);
        doc.add(getExpenses());

        drawAlignmentMarks(writer.getDirectContent());

        doc.close();        
        writer.close();
    }

    
}
