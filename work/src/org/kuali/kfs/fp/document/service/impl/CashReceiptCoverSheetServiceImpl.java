/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.fp.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.service.CashReceiptCoverSheetService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Implementation of service for handling creation of the cover sheet of the <code>{@link CashReceiptDocument}</code>
 */
public class CashReceiptCoverSheetServiceImpl implements CashReceiptCoverSheetService {
    private static Log LOG = LogFactory.getLog(CashReceiptCoverSheetService.class);
    
    private DataDictionaryService dataDictionaryService;
    private DocumentHelperService documentHelperService;

    public static final String CR_COVERSHEET_TEMPLATE_NM = "CashReceiptCoverSheetTemplate.pdf";

    private static final float LEFT_MARGIN = 45;
    private static final float TOP_MARGIN = 45;
    private static final float TOP_FIRST_PAGE = 440;

    private static final String DOCUMENT_NUMBER_FIELD = "DocumentNumber";
    private static final String INITIATOR_FIELD = "Initiator";
    private static final String CREATED_DATE_FIELD = "CreatedDate";
    private static final String AMOUNT_FIELD = "Amount";
    private static final String ORG_DOC_NUMBER_FIELD = "OrgDocNumber";
    private static final String CAMPUS_FIELD = "Campus";
    private static final String DEPOSIT_DATE_FIELD = "DepositDate";
    private static final String DESCRIPTION_FIELD = "Description";
    private static final String EXPLANATION_FIELD = "Explanation";
    private static final String CHECKS_FIELD = "Checks";
    private static final String CURRENCY_FIELD = "Currency";
    private static final String COIN_FIELD = "Coin";
    private static final String CREDIT_CARD_FIELD = "CreditCard";
    private static final String CHANGE_OUT_FIELD = "ChangeOut";
    private static final String TOTAL_RECONCILIATION_FIELD = "ReconciliationTotal";

    private static final int FRONT_PAGE = 1;
    private static final int CHECK_PAGE_NORMAL = 2;
    private static final float CHECK_DETAIL_HEADING_HEIGHT = 45;
    private static final float CHECK_LINE_SPACING = 12;
    private static final float CHECK_FIELD_MARGIN = 12;
    private static final float CHECK_NORMAL_FIELD_LENGTH = 100;
    private static final float CHECK_FIELD_HEIGHT = 10;
    private static final int MAX_CHECKS_FIRST_PAGE = 30;
    private static final int MAX_CHECKS_NORMAL = 65;

    private static final float CHECK_HEADER_HEIGHT = 12;
    private static final String CHECK_NUMBER_FIELD_PREFIX = "CheckNumber";
    private static final float CHECK_NUMBER_FIELD_POSITION = LEFT_MARGIN;

    private static final String CHECK_DATE_FIELD_PREFIX = "CheckDate";
    private static final float CHECK_DATE_FIELD_POSITION = CHECK_NUMBER_FIELD_POSITION + CHECK_NORMAL_FIELD_LENGTH + CHECK_FIELD_MARGIN;

    private static final String CHECK_DESCRIPTION_FIELD_PREFIX = "CheckDescription";
    private static final float CHECK_DESCRIPTION_FIELD_POSITION = CHECK_DATE_FIELD_POSITION + CHECK_NORMAL_FIELD_LENGTH + CHECK_FIELD_MARGIN;
    private static final float CHECK_DESCRIPTION_FIELD_LENGTH = 250;

    private static final String CHECK_AMOUNT_FIELD_PREFIX = "CheckAmount";
    private static final float CHECK_AMOUNT_FIELD_POSITION = CHECK_DESCRIPTION_FIELD_POSITION + CHECK_DESCRIPTION_FIELD_LENGTH + CHECK_FIELD_MARGIN;

    private float _yPos;


    /**
     * This method determines if cover sheet printing is allowed by reviewing the CashReceiptDocumentRule to see if the 
     * cover sheet is printable.
     * 
     * @param crDoc The document the cover sheet is being printed for.
     * @return True if the cover sheet is printable, false otherwise.
     * 
     * @see org.kuali.kfs.fp.document.service.CashReceiptCoverSheetService#isCoverSheetPrintingAllowed(org.kuali.kfs.fp.document.CashReceiptDocument)
     * @see org.kuali.kfs.fp.document.validation.impl.CashReceiptDocumentRule#isCoverSheetPrintable(org.kuali.kfs.fp.document.CashReceiptFamilyBase)
     */
    public boolean isCoverSheetPrintingAllowed(CashReceiptDocument crDoc) {
        WorkflowDocument workflowDocument = crDoc.getDocumentHeader().getWorkflowDocument();
        return !(workflowDocument.isCanceled() || workflowDocument.isInitiated() || workflowDocument.isDisapproved() || workflowDocument.isException() || workflowDocument.isDisapproved() || workflowDocument.isSaved());
    }
    
    /**
     * Generate a cover sheet for the <code>{@link CashReceiptDocument}</code>. An <code>{@link OutputStream}</code> is written
     * to for the cover sheet.
     * 
     * @param document The cash receipt document the cover sheet is for.
     * @param searchPath The directory path to the template to be used to generate the cover sheet.
     * @param returnStream The output stream the cover sheet will be written to.
     * @exception DocumentException Thrown if the document provided is invalid, including null.
     * @exception IOException Thrown if there is a problem writing to the output stream.
     * @see org.kuali.rice.kns.module.financial.service.CashReceiptCoverSheetServiceImpl#generateCoverSheet(
     *      org.kuali.module.financial.documentCashReceiptDocument )
     */
    public void generateCoverSheet(CashReceiptDocument document, String searchPath, OutputStream returnStream) throws Exception {

        if (isCoverSheetPrintingAllowed(document)) {
            ByteArrayOutputStream stamperStream = new ByteArrayOutputStream();

            stampPdfFormValues(document, searchPath, stamperStream);
            
            PdfReader reader = new PdfReader(stamperStream.toByteArray());
            Document pdfDoc = new Document(reader.getPageSize(FRONT_PAGE));
            PdfWriter writer = PdfWriter.getInstance(pdfDoc, returnStream);

            pdfDoc.open();
            populateCheckDetail(document, writer, reader);
            pdfDoc.close();
            writer.close();
        }
    }

    /**
     * Use iText <code>{@link PdfStamper}</code> to stamp information from <code>{@link CashReceiptDocument}</code> into field
     * values on a PDF Form Template.
     * 
     * @param document The cash receipt document the values will be pulled from.
     * @param searchPath The directory path of the template to be used to generate the cover sheet.
     * @param returnStream The output stream the cover sheet will be written to.
     */
    protected void stampPdfFormValues(CashReceiptDocument document, String searchPath, OutputStream returnStream) throws Exception {
        String templateName = CR_COVERSHEET_TEMPLATE_NM;

        try {
            // populate form with document values
            
            //KFSMI-7303
            //The PDF template is retrieve through web static URL rather than file path, so the File separator is unnecessary
            final boolean isWebResourcePath = StringUtils.containsIgnoreCase(searchPath, "HTTP");
            
            //skip the File.separator if reference by web resource
            PdfStamper stamper = new PdfStamper(new PdfReader(searchPath + (isWebResourcePath? "" : File.separator) + templateName), returnStream);
            AcroFields populatedCoverSheet = stamper.getAcroFields();
            
            populatedCoverSheet.setField(DOCUMENT_NUMBER_FIELD, document.getDocumentNumber());
            populatedCoverSheet.setField(INITIATOR_FIELD, document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            populatedCoverSheet.setField(CREATED_DATE_FIELD, document.getDocumentHeader().getWorkflowDocument().getDateCreated().toString());
            populatedCoverSheet.setField(AMOUNT_FIELD, document.getTotalDollarAmount().toString());
            populatedCoverSheet.setField(ORG_DOC_NUMBER_FIELD, document.getDocumentHeader().getOrganizationDocumentNumber());
            populatedCoverSheet.setField(CAMPUS_FIELD, document.getCampusLocationCode());
            if (document.getDepositDate() != null) {
                // This value won't be set until the CR document is
                // deposited. A CR document is deposited only when it has
                // been associated with a Cash Management Document (CMD)
                // and with a Deposit within that CMD. And only when the
                // CMD is submitted and FINAL, will the CR documents
                // associated with it, be "deposited." So this value will
                // fill in at an arbitrarily later point in time. So your
                // code shouldn't expect it, but if it's there, then
                // display it.
                populatedCoverSheet.setField(DEPOSIT_DATE_FIELD, document.getDepositDate().toString());
            }
            populatedCoverSheet.setField(DESCRIPTION_FIELD, document.getDocumentHeader().getDocumentDescription());
            populatedCoverSheet.setField(EXPLANATION_FIELD, document.getDocumentHeader().getExplanation());
            populatedCoverSheet.setField(CHECKS_FIELD, document.getTotalCheckAmount().toString());
            populatedCoverSheet.setField(CURRENCY_FIELD, document.getTotalCashAmount().toString());
            populatedCoverSheet.setField(COIN_FIELD, document.getTotalCoinAmount().toString());
            populatedCoverSheet.setField(CHANGE_OUT_FIELD, document.getTotalChangeAmount().toString());
            
            populatedCoverSheet.setField(TOTAL_RECONCILIATION_FIELD, document.getTotalDollarAmount().toString());

            stamper.setFormFlattening(true);
            stamper.close();
        }
        catch (Exception e) {
            LOG.error("Error creating coversheet for: " + document.getDocumentNumber() + ". ::" + e);
            throw e;
        }
    }

    /**
     * 
     * This method writes the check number from the check provided to the PDF template.
     * @param output The PDF output field the check number will be written to.
     * @param check The check the check number will be retrieved from.
     */
    protected void writeCheckNumber(PdfContentByte output, Check check) {
        writeCheckField(output, CHECK_NUMBER_FIELD_POSITION, check.getCheckNumber().toString());
    }

    /**
     * 
     * This method writes the check date from the check provided to the PDF template.
     * @param output The PDF output field the check date will be written to.
     * @param check The check the check date will be retrieved from.
     */
    protected void writeCheckDate(PdfContentByte output, Check check) {
        writeCheckField(output, CHECK_DATE_FIELD_POSITION, check.getCheckDate().toString());
    }

    /**
     * 
     * This method writes the check description from the check provided to the PDF template.
     * @param output The PDF output field the check description will be written to.
     * @param check The check the check description will be retrieved from.
     */
    protected void writeCheckDescription(PdfContentByte output, Check check) {
        writeCheckField(output, CHECK_DESCRIPTION_FIELD_POSITION, check.getDescription());
    }

    /**
     * 
     * This method writes the check amount from the check provided to the PDF template.
     * @param output The PDF output field the check amount will be written to.
     * @param check The check the check amount will be retrieved from.
     */
    protected void writeCheckAmount(PdfContentByte output, Check check) {
        writeCheckField(output, CHECK_AMOUNT_FIELD_POSITION, check.getAmount().toString());
    }

    /**
     * 
     * This method writes out the value provided to the output provided and aligns the value outputted using the xPos float
     * provided.
     * @param output The content byte used to write out the field to the PDF template.
     * @param xPos The x coordinate of the starting point on the document where the value will be written to.
     * @param fieldValue The value to be written to the PDF cover sheet.
     */
    protected void writeCheckField(PdfContentByte output, float xPos, String fieldValue) {
        output.beginText();
        output.setTextMatrix(xPos, getCurrentRenderingYPosition());
        output.newlineShowText(fieldValue);
        output.endText();
    }

    /**
     * Read-only accessor for <code>{@link BaseFont}</code>. Used for creating the check detail information.  The font being 
     * used is  Helvetica.
     * 
     * @return A BaseFont object used to identify what type of font is used on the cover sheet.
     */
    protected BaseFont getTextFont() throws DocumentException, IOException {
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    }

    /**
     * Defines a state of Y position for the text.
     * 
     * @param y The y coordinate to be set.
     */
    protected void setCurrentRenderingYPosition(float y) {
        _yPos = y;
    }

    /**
     * Defines a state of Y position for the text.
     * 
     * @return The current y coordinate.
     */
    protected float getCurrentRenderingYPosition() {
        return _yPos;
    }

    /**
     * Method responsible for producing Check Detail section of the cover sheet. Not all Cash Receipt documents have checks.
     * 
     * @param crDoc The CashReceipt document the cover sheet is being created for.
     * @param writer The output writer used to write the check data to the PDF file.
     * @param reader The input reader used to read data from the PDF file.
     */
    protected void populateCheckDetail(CashReceiptDocument crDoc, PdfWriter writer, PdfReader reader) throws Exception {
        PdfContentByte content;
        ModifiableInteger pageNumber;
        int checkCount = 0;
        int maxCheckCount = MAX_CHECKS_FIRST_PAGE;

        pageNumber = new ModifiableInteger(0);
        content = startNewPage(writer, reader, pageNumber);

        for (Check current : crDoc.getChecks()) {
            writeCheckNumber(content, current);
            writeCheckDate(content, current);
            writeCheckDescription(content, current);
            writeCheckAmount(content, current);
            setCurrentRenderingYPosition(getCurrentRenderingYPosition() - CHECK_FIELD_HEIGHT);

            checkCount++;

            if (checkCount > maxCheckCount) {
                checkCount = 0;
                maxCheckCount = MAX_CHECKS_NORMAL;
                content = startNewPage(writer, reader, pageNumber);
            }
        }
    }

    /**
     * Responsible for creating a new PDF page and workspace through <code>{@link PdfContentByte}</code> for direct writing to the
     * PDF.
     * 
     * @param writer The PDF writer used to write to the new page with.
     * @param reader The PDF reader used to read information from the PDF file.
     * @param pageNumber The current number of pages in the PDF file, which will be incremented by one inside this method.
     * 
     * @return The PDFContentByte used to access the new PDF page.
     * @exception DocumentException
     * @exception IOException
     */
    protected PdfContentByte startNewPage(PdfWriter writer, PdfReader reader, ModifiableInteger pageNumber) throws DocumentException, IOException {
        PdfContentByte retval;
        PdfContentByte under;
        Rectangle pageSize;
        Document pdfDoc;
        PdfImportedPage newPage;

        pageNumber.increment();
        pageSize = reader.getPageSize(FRONT_PAGE);
        retval = writer.getDirectContent();
        // under = writer.getDirectContentUnder();

        if (pageNumber.getInt() > FRONT_PAGE) {
            newPage = writer.getImportedPage(reader, CHECK_PAGE_NORMAL);
            setCurrentRenderingYPosition(pageSize.top(TOP_MARGIN + CHECK_DETAIL_HEADING_HEIGHT));
        }
        else {
            newPage = writer.getImportedPage(reader, FRONT_PAGE);
            setCurrentRenderingYPosition(pageSize.top(TOP_FIRST_PAGE));
        }

        pdfDoc = retval.getPdfDocument();
        pdfDoc.newPage();
        retval.addTemplate(newPage, 0, 0);
        retval.setFontAndSize(getTextFont(), 8);

        return retval;
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the documentHelperService attribute. 
     * @return Returns the documentHelperService.
     */
    public DocumentHelperService getDocumentHelperService() {
        return documentHelperService;
    }

    /**
     * Sets the documentHelperService attribute value.
     * @param documentHelperService The documentHelperService to set.
     */
    public void setDocumentHelperService(DocumentHelperService documentHelperService) {
        this.documentHelperService = documentHelperService;
    }

}



/**
 * Utility class used to replace an <code>{@link Integer}</code> because an integer cannot be modified once it has been
 * instantiated.
 */
class ModifiableInteger {
    int _value;

    /**
     * 
     * Constructs a ModifiableInteger object.
     * @param val The initial value of the object.
     */
    public ModifiableInteger(Integer val) {
        this(val.intValue());
    }

    /**
     * 
     * Constructs a ModifiableInteger object.
     * @param val The initial value of the object.
     */
    public ModifiableInteger(int val) {
        setInt(val);
    }

    /**
     * 
     * This method sets the local attribute to the value given.
     * @param val The int value to be set.
     */
    public void setInt(int val) {
        _value = val;
    }

    /**
     * 
     * This method retrieves the value of the object.
     * @return The int value of this object.
     */
    public int getInt() {
        return _value;
    }

    /**
     * 
     * This method increments the value of this class by one.
     * @return An instance of this class with the value incremented by one.
     */
    public ModifiableInteger increment() {
        _value++;
        return this;
    }

    /**
     * 
     * This method increments the value of this class by the amount specified.
     * @param inc The amount the class value should be incremented by.
     * @return An instance of this class with the value incremented by the amount specified.
     */
    public ModifiableInteger increment(int inc) {
        _value += inc;
        return this;
    }

    /**
     * 
     * This method decrements the value of this class by one.
     * @return An instance of this class with the value decremented by one.
     */
    public ModifiableInteger decrement() {
        _value--;
        return this;
    }

    /**
     * 
     * This method decrements the value of this class by the amount specified.
     * @param dec The amount the class value should be decremented by.
     * @return An instance of this class with the value decremented by the amount specified.
     */
    public ModifiableInteger decrement(int dec) {
        _value -= dec;
        return this;
    }

    /**
     * 
     * This method converts the value of this class and returns it as an Integer object.
     * @return The value of this class formatted as an Integer.
     */
    public Integer getInteger() {
        return new Integer(_value);
    }

    /**
     * This method generates and returns a String representation of this class.
     * @return A string representation of this object.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getInteger().toString();
    }
}
