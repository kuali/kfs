/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/document/service/impl/CashReceiptCoverSheetServiceImpl.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.financial.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.rules.CashReceiptDocumentRule;
import org.kuali.module.financial.service.CashReceiptCoverSheetService;

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
 * 
 * 
 */
public class CashReceiptCoverSheetServiceImpl implements CashReceiptCoverSheetService {
    private static Log LOG = LogFactory.getLog(CashReceiptCoverSheetService.class);

    public static final String CR_COVERSHEET_TEMPLATE_RELATIVE_DIR = "templates/financial";
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
    private static final String ADV_DEPOSIT_FIELD = "AdvancedDeposit";
    private static final String CHANGE_OUT_FIELD = "ChangeOut";
    private static final String REVIV_FUND_OUT_FIELD = "RevivFundOut";

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
     * @see org.kuali.module.financial.service.CashReceiptCoverSheetService#isCoverSheetPrintingAllowed(org.kuali.module.financial.document.CashReceiptDocument)
     */
    public boolean isCoverSheetPrintingAllowed(CashReceiptDocument crDoc) {
        CashReceiptDocumentRule rule = (CashReceiptDocumentRule) SpringServiceLocator.getKualiRuleService().getBusinessRulesInstance(crDoc, CashReceiptDocumentRule.class);

        return rule.isCoverSheetPrintable(crDoc);
    }

    /**
     * Generate a cover sheet for the <code>{@link CashReceiptDocument}</code>. An <code>{@link OutputStream}</code> is written
     * to for the coversheet.
     * 
     * @param document
     * @param searchPath
     * @param returnStream
     * @exception DocumentException
     * @exception IOException
     * @see org.kuali.core.module.financial.service.CashReceiptCoverSheetServiceImpl#generateCoverSheet(
     *      org.kuali.module.financial.documentCashReceiptDocument )
     */
    public void generateCoverSheet(CashReceiptDocument document, String searchPath, OutputStream returnStream) throws Exception {

        if (isCoverSheetPrintingAllowed(document)) {
            ByteArrayOutputStream stamperStream;
            PdfWriter writer;
            PdfReader reader;
            Document pdfDoc;

            stamperStream = new ByteArrayOutputStream();
            stampPdfFormValues(document, searchPath, stamperStream);
            reader = new PdfReader(stamperStream.toByteArray());
            pdfDoc = new Document(reader.getPageSize(FRONT_PAGE));
            writer = PdfWriter.getInstance(pdfDoc, returnStream);
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
     * @param document
     * @param searchPath
     * @param returnStream
     */
    private void stampPdfFormValues(CashReceiptDocument document, String searchPath, OutputStream returnStream) throws Exception {
        String templateName = CR_COVERSHEET_TEMPLATE_NM;

        try {
            // populate form with document values
            PdfStamper stamper = new PdfStamper(new PdfReader(searchPath + File.separator + templateName), returnStream);
            AcroFields populatedCoverSheet = stamper.getAcroFields();

            populatedCoverSheet.setField(DOCUMENT_NUMBER_FIELD, document.getDocumentNumber());
            populatedCoverSheet.setField(INITIATOR_FIELD, document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId());
            populatedCoverSheet.setField(CREATED_DATE_FIELD, document.getDocumentHeader().getWorkflowDocument().getCreateDate().toString());
            populatedCoverSheet.setField(AMOUNT_FIELD, document.getSumTotalAmount().toString());
            populatedCoverSheet.setField(ORG_DOC_NUMBER_FIELD, document.getDocumentHeader().getOrganizationDocumentNumber());
            populatedCoverSheet.setField(CAMPUS_FIELD, document.getCampusLocationCode());
            if (document.getDepositDate() != null) {
                // This value won't be set until the CR document is
                // deposited. A CR document is desposited only when it has
                // been associated with a Cash Management Document (CMD)
                // and with a Deposit within that CMD. And only when the
                // CMD is submitted and FINAL, will the CR documents
                // associated with it, be "deposited." So this value will
                // fill in at an arbitrarily later point in time. So your
                // code shouldn't expect it, but if it's there, then
                // display it.
                populatedCoverSheet.setField(DEPOSIT_DATE_FIELD, document.getDepositDate().toString());
            }
            populatedCoverSheet.setField(DESCRIPTION_FIELD, document.getDocumentHeader().getFinancialDocumentDescription());
            populatedCoverSheet.setField(EXPLANATION_FIELD, document.getExplanation());
            populatedCoverSheet.setField(CHECKS_FIELD, document.getTotalCheckAmount().toString());
            populatedCoverSheet.setField(CURRENCY_FIELD, document.getTotalCashAmount().toString());
            populatedCoverSheet.setField(COIN_FIELD, document.getTotalCoinAmount().toString());
            /*
             * Fields currently not used. Pulling them out. These are advanced features of the CR which will come during the
             * post-3/31 timeframe populatedCoverSheet.setField( CREDIT_CARD_FIELD, document.getDocumentNumber() );
             * populatedCoverSheet.setField( ADV_DEPOSIT_FIELD, document.getDocumentNumber() );
             * populatedCoverSheet.setField( CHANGE_OUT_FIELD, document.getDocumentNumber() );
             * populatedCoverSheet.setField( REVIV_FUND_OUT_FIELD, document.getDocumentNumber() );
             */

            stamper.setFormFlattening(true);
            stamper.close();
        }
        catch (Exception e) {
            LOG.error("Error creating coversheet for: " + document.getDocumentNumber() + ". ::" + e);
            throw e;
        }
    }

    private void writeCheckNumber(PdfContentByte output, Check check) {
        writeCheckField(output, CHECK_NUMBER_FIELD_POSITION, check.getCheckNumber().toString());
    }

    private void writeCheckDate(PdfContentByte output, Check check) {
        writeCheckField(output, CHECK_DATE_FIELD_POSITION, check.getCheckDate().toString());
    }

    private void writeCheckDescription(PdfContentByte output, Check check) {
        writeCheckField(output, CHECK_DESCRIPTION_FIELD_POSITION, check.getDescription());
    }

    private void writeCheckAmount(PdfContentByte output, Check check) {
        writeCheckField(output, CHECK_AMOUNT_FIELD_POSITION, check.getAmount().toString());
    }

    private void writeCheckField(PdfContentByte output, float xPos, String fieldValue) {
        output.beginText();
        output.setTextMatrix(xPos, getCurrentRenderingYPosition());
        output.newlineShowText(fieldValue);
        output.endText();
    }

    /**
     * Read-only accessor for <code>{@link BaseFont}</code>. Used for creating the check detail information
     * 
     * @return BaseFont
     */
    private BaseFont getTextFont() throws DocumentException, IOException {
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    }

    /**
     * Defines a state of Y positon for the text.
     * 
     * @param y
     */
    private void setCurrentRenderingYPosition(float y) {
        _yPos = y;
    }

    /**
     * Defines a state of Y positon for the text.
     * 
     * @return float
     */
    private float getCurrentRenderingYPosition() {
        return _yPos;
    }

    /**
     * Method responsible for producing Check Detail section of the cover sheet. Not all Cash Receipt documents have checks.
     * 
     * @param pdfDoc
     * @param crDoc
     */
    private void populateCheckDetail(CashReceiptDocument crDoc, PdfWriter writer, PdfReader reader) throws Exception {
        PdfContentByte content;
        ModifiableInteger pageNumber;
        int checkCount = 0;
        int maxCheckCount = MAX_CHECKS_FIRST_PAGE;

        pageNumber = new ModifiableInteger(0);
        content = startNewPage(writer, reader, pageNumber);

        for (Iterator check_it = crDoc.getChecks().iterator(); check_it.hasNext();) {
            Check current = (Check) check_it.next();

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
     * @param writer
     * @param reader
     * @param pageNumber
     * 
     * @return PdfContentByte
     * @exception DocumentException
     * @exception IOException
     */
    private PdfContentByte startNewPage(PdfWriter writer, PdfReader reader, ModifiableInteger pageNumber) throws DocumentException, IOException {
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
}

/**
 * Utility class used to replace an <code>{@link Integer}</code> because an integer cannot be modified once it has been
 * instantiated.
 * 
 */
class ModifiableInteger {
    int _value;

    public ModifiableInteger(Integer val) {
        this(val.intValue());
    }

    public ModifiableInteger(int val) {
        setInt(val);
    }

    public void setInt(int val) {
        _value = val;
    }

    public int getInt() {
        return _value;
    }

    public ModifiableInteger increment() {
        _value++;
        return this;
    }

    public ModifiableInteger increment(int inc) {
        _value += inc;
        return this;
    }

    public ModifiableInteger decrement() {
        _value--;
        return this;
    }

    public ModifiableInteger decrement(int dec) {
        _value -= dec;
        return this;
    }

    public Integer getInteger() {
        return new Integer(_value);
    }

    @Override
    public String toString() {
        return getInteger().toString();
    }
}
