/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRetransmitDocument;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.util.PurApDateFormatUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Base class to handle pdf for purchase order documents.
 */
public class PurchaseOrderPdf extends PurapPdf {
    private static Log LOG = LogFactory.getLog(PurchaseOrderPdf.class);

    /** headerTable pieces need to be public */

    public PurchaseOrderPdf() {
        super();
    }

    /**
     * Overrides the method in PdfPageEventHelper from itext to create and set the headerTable and set its logo image if
     * there is a logoImage to be used, creates and sets the nestedHeaderTable and its content.
     *
     * @param writer    The PdfWriter for this document.
     * @param document  The document.
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("onOpenDocument() started. isRetransmit is " + isRetransmit);
        }
        try {
            float[] headerWidths = { 0.20f, 0.80f };
            headerTable = new PdfPTable(headerWidths);
            headerTable.setWidthPercentage(100);
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.setSplitLate(false);
            headerTable.getDefaultCell().setBorderWidth(0);
            headerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            Image logo = null;
            if (StringUtils.isNotBlank(logoImage)) {
                try {
                    logo = Image.getInstance(logoImage);
                }
                catch (FileNotFoundException e) {
                    LOG.info("The logo image [" + logoImage + "] is not available.  Defaulting to the default image.");
                }
            }

            if (logo == null) {
                // if we don't use images
                headerTable.addCell(new Phrase(new Chunk("")));
            }
            else {
                logo.scalePercent(3, 3);
                headerTable.addCell(new Phrase(new Chunk(logo, 0, 0)));
            }
            // Nested table for titles, etc.
            float[] nestedHeaderWidths = { 0.70f, 0.30f };
            nestedHeaderTable = new PdfPTable(nestedHeaderWidths);
            nestedHeaderTable.setSplitLate(false);
            PdfPCell cell;

            // New nestedHeaderTable row
            cell = new PdfPCell(new Paragraph(po.getBillingName(), ver_15_normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidth(0);
            nestedHeaderTable.addCell(cell);
            cell = new PdfPCell(new Paragraph(" ", ver_15_normal));
            cell.setBorderWidth(0);
            nestedHeaderTable.addCell(cell);
            // New nestedHeaderTable row
            if (isRetransmit) {
                cell = new PdfPCell(new Paragraph(po.getRetransmitHeader(), ver_15_normal));
            }
            else {
                cell = new PdfPCell(new Paragraph("PURCHASE ORDER", ver_15_normal));
            }
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidth(0);
            nestedHeaderTable.addCell(cell);
            Paragraph p = new Paragraph();
            p.add(new Chunk("PO Number: ", ver_11_normal));
            p.add(new Chunk(po.getPurapDocumentIdentifier().toString(), cour_16_bold));

            cell = new PdfPCell(p);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidth(0);
            nestedHeaderTable.addCell(cell);
            if (!po.getPurchaseOrderAutomaticIndicator()) { // Contract manager name goes on non-APOs.
                // New nestedHeaderTable row, spans both columns
                p = new Paragraph();
                p.add(new Chunk("Contract Manager: ", ver_11_normal));
                p.add(new Chunk(po.getContractManager().getContractManagerName(), cour_7_normal));
                cell = new PdfPCell(p);
                cell.setColspan(2);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderWidth(0);
                nestedHeaderTable.addCell(cell);
            }
            // Add the nestedHeaderTable to the headerTable
            cell = new PdfPCell(nestedHeaderTable);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidth(0);
            headerTable.addCell(cell);

            // initialization of the template
            tpl = writer.getDirectContent().createTemplate(100, 100);
            // initialization of the font
            helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /**
     * Gets a PageEvents object.
     *
     * @return a new PageEvents object
     */
    @Override
    public PurchaseOrderPdf getPageEvents() {
        LOG.debug("getPageEvents() started.");
        return new PurchaseOrderPdf();
    }

    /**
     * Generates the pdf document based on the data in the given PurchaseOrderDocument, the pdf parameters,
     * environment, retransmit items, creates a pdf writer using the given byteArrayOutputStream then
     * write the pdf document into the writer.
     *
     * @param po                     The PurchaseOrderDocument to be used to generate the pdf.
     * @param pdfParameters          The PurchaseOrderPdfParameters to be used to generate the pdf.
     * @param byteArrayOutputStream  The ByteArrayOutputStream where the pdf document will be written to.
     * @param isRetransmit           The boolean to indicate whether this is for a retransmit purchase order document.
     * @param environment            The current environment used (e.g. DEV if it is a development environment).
     * @param retransmitItems        The items selected by the user to be retransmitted.
     */
    public void generatePdf(PurchaseOrderDocument po, PurchaseOrderTransmitParameters pdfParameters, ByteArrayOutputStream byteArrayOutputStream, boolean isRetransmit, String environment, List<PurchaseOrderItem> retransmitItems) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generatePdf() started for po number " + po.getPurapDocumentIdentifier());
        }

        this.isRetransmit = isRetransmit;
        String statusInquiryUrl = pdfParameters.getStatusInquiryUrl();
        String campusName = pdfParameters.getCampusParameter().getCampus().getName();
        String contractLanguage = pdfParameters.getContractLanguage();
        String logoImage = pdfParameters.getLogoImage();
        String directorSignatureImage = pdfParameters.getDirectorSignatureImage();
        String directorName = pdfParameters.getCampusParameter().getCampusPurchasingDirectorName();
        String directorTitle = pdfParameters.getCampusParameter().getCampusPurchasingDirectorTitle();
        String contractManagerSignatureImage = pdfParameters.getContractManagerSignatureImage();

        try {
            Document doc = this.getDocument(9, 9, 70, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, byteArrayOutputStream);
            this.createPdf(po, doc, writer, statusInquiryUrl, campusName, contractLanguage, logoImage, directorSignatureImage, directorName, directorTitle, contractManagerSignatureImage, isRetransmit, environment, retransmitItems);
        }
        catch (DocumentException de) {
            LOG.error("generatePdf() DocumentException: " + de.getMessage(), de);
            throw new PurError("Document Exception when trying to save a Purchase Order PDF", de);
        }
        catch (IOException i) {
            LOG.error("generatePdf() IOException: " + i.getMessage(), i);
            throw new PurError("IO Exception when trying to save a Purchase Order PDF", i);
        }
        catch (Exception t) {
            LOG.error("generatePdf() EXCEPTION: " + t.getMessage(), t);
            throw new PurError("Exception when trying to save a Purchase Order PDF", t);
        }
    }

    /**
     * Invokes the createPdf method to create a pdf document and saves it into a file
     * which name and location are specified in the pdfParameters.
     *
     * @param po             The PurchaseOrderDocument to be used to create the pdf.
     * @param pdfParameters  The pdfParameters containing some of the parameters information needed by the pdf for example, the pdf file name and pdf file location, purchasing director name, etc.
     * @param isRetransmit   The boolean to indicate whether this is for a retransmit purchase order document.
     * @param environment    The current environment used (e.g. DEV if it is a development environment).
     */
    public void savePdf(PurchaseOrderDocument po, PurchaseOrderParameters pdfParameters, boolean isRetransmit, String environment) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("savePdf() started for po number " + po.getPurapDocumentIdentifier());
        }

        PurchaseOrderTransmitParameters pdfTransmitParameters = (PurchaseOrderTransmitParameters)pdfParameters;

        this.isRetransmit = isRetransmit;
        String statusInquiryUrl = pdfTransmitParameters.getStatusInquiryUrl();
        String campusName = pdfTransmitParameters.getCampusParameter().getCampus().getName();
        String contractLanguage = pdfTransmitParameters.getContractLanguage();
        String logoImage = pdfTransmitParameters.getLogoImage();
        String directorSignatureImage = pdfTransmitParameters.getDirectorSignatureImage();
        String directorName = pdfTransmitParameters.getCampusParameter().getCampusPurchasingDirectorName();
        String directorTitle = pdfTransmitParameters.getCampusParameter().getCampusPurchasingDirectorTitle();
        String contractManagerSignatureImage = pdfTransmitParameters.getContractManagerSignatureImage();
        String pdfFileLocation = pdfTransmitParameters.getPdfFileLocation();
        String pdfFileName = pdfTransmitParameters.getPdfFileName();

        try {
            Document doc = this.getDocument(9, 9, 70, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfFileLocation + pdfFileName));
            this.createPdf(po, doc, writer, statusInquiryUrl, campusName, contractLanguage, logoImage, directorSignatureImage, directorName, directorTitle, contractManagerSignatureImage, isRetransmit, environment);
        }
        catch (DocumentException de) {
            LOG.error("savePdf() DocumentException: " + de.getMessage(), de);
            throw new PurError("Document Exception when trying to save a Purchase Order PDF", de);
        }
        catch (FileNotFoundException f) {
            LOG.error("savePdf() FileNotFoundException: " + f.getMessage(), f);
            throw new PurError("FileNotFound Exception when trying to save a Purchase Order PDF", f);
        }
        catch (IOException i) {
            LOG.error("savePdf() IOException: " + i.getMessage(), i);
            throw new PurError("IO Exception when trying to save a Purchase Order PDF", i);
        }
        catch (Exception t) {
            LOG.error("savePdf() EXCEPTION: " + t.getMessage(), t);
            throw new PurError("Exception when trying to save a Purchase Order PDF", t);
        }
    }

    /**
     * Creates the purchase order pdf, and pass in null as the retransmitItems List because it doesn't need retransmit.
     *
     * @param po                             The PurchaseOrderDocument to be used to create the pdf.
     * @param document                       The pdf document whose margins have already been set.
     * @param writer                         The PdfWriter to write the pdf document into.
     * @param statusInquiryUrl               The status inquiry url to be displayed on the pdf document.
     * @param campusName                     The campus name to be displayed on the pdf document.
     * @param contractLanguage               The contract language to be displayed on the pdf document.
     * @param logoImage                      The logo image file name to be displayed on the pdf document.
     * @param directorSignatureImage         The director signature image to be displayed on the pdf document.
     * @param directorName                   The director name to be displayed on the pdf document.
     * @param directorTitle                  The director title to be displayed on the pdf document.
     * @param contractManagerSignatureImage  The contract manager signature image to be displayed on the pdf document.
     * @param isRetransmit                   The boolean to indicate whether this is for a retransmit purchase order document.
     * @param environment                    The current environment used (e.g. DEV if it is a development environment).
     * @throws DocumentException
     * @throws IOException
     */
    private void createPdf(PurchaseOrderDocument po, Document document, PdfWriter writer, String statusInquiryUrl, String campusName, String contractLanguage, String logoImage, String directorSignatureImage, String directorName, String directorTitle, String contractManagerSignatureImage, boolean isRetransmit, String environment) throws DocumentException, IOException {
        createPdf(po, document, writer, statusInquiryUrl, campusName, contractLanguage, logoImage, directorSignatureImage, directorName, directorTitle, contractManagerSignatureImage, isRetransmit, environment, null);
    }

    /**
     * Create a PDF using the given input parameters.
     *
     * @param po                             The PurchaseOrderDocument to be used to create the pdf.
     * @param document                       The pdf document whose margins have already been set.
     * @param writer                         The PdfWriter to write the pdf document into.
     * @param statusInquiryUrl               The status inquiry url to be displayed on the pdf document.
     * @param campusName                     The campus name to be displayed on the pdf document.
     * @param contractLanguage               The contract language to be displayed on the pdf document.
     * @param logoImage                      The logo image file name to be displayed on the pdf document.
     * @param directorSignatureImage         The director signature image to be displayed on the pdf document.
     * @param directorName                   The director name to be displayed on the pdf document.
     * @param directorTitle                  The director title to be displayed on the pdf document.
     * @param contractManagerSignatureImage  The contract manager signature image to be displayed on the pdf document.
     * @param isRetransmit                   The boolean to indicate whether this is for a retransmit purchase order document.
     * @param environment                    The current environment used (e.g. DEV if it is a development environment).
     * @param retransmitItems                The items selected by the user to be retransmitted.
     * @throws DocumentException
     * @throws IOException
     */
    private void createPdf(PurchaseOrderDocument po, Document document, PdfWriter writer, String statusInquiryUrl, String campusName, String contractLanguage, String logoImage, String directorSignatureImage, String directorName, String directorTitle, String contractManagerSignatureImage, boolean isRetransmit, String environment, List<PurchaseOrderItem> retransmitItems) throws DocumentException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("createPdf() started for po number " + po.getPurapDocumentIdentifier().toString());
        }

        // These have to be set because they are used by the onOpenDocument() and onStartPage() methods.
        this.campusName = campusName;
        this.po = po;
        this.logoImage = logoImage;
        this.environment = environment;

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        Collection errors = new ArrayList();

        // Date format pattern: MM-dd-yyyy
        SimpleDateFormat sdf = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.KUALI_SIMPLE_DATE_FORMAT_2);

        // This turns on the page events that handle the header and page numbers.
        PurchaseOrderPdf events = new PurchaseOrderPdf().getPageEvents();
        writer.setPageEvent(this); // Passing in "this" lets it know about the po, campusName, etc.

        document.open();

        PdfPCell cell;
        Paragraph p = new Paragraph();

        // ***** Info table (vendor, address info) *****
        LOG.debug("createPdf() info table started.");
        float[] infoWidths = { 0.50f, 0.50f };
        PdfPTable infoTable = new PdfPTable(infoWidths);

        infoTable.setWidthPercentage(100);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSplitLate(false);

        StringBuffer vendorInfo = new StringBuffer();
        vendorInfo.append("\n");
        if (StringUtils.isNotBlank(po.getVendorName())) {
            vendorInfo.append("     " + po.getVendorName() + "\n");
        }

        vendorInfo.append("     ATTN: " + po.getVendorAttentionName() + "\n");

        if (StringUtils.isNotBlank(po.getVendorLine1Address())) {
            vendorInfo.append("     " + po.getVendorLine1Address() + "\n");
        }
        if (StringUtils.isNotBlank(po.getVendorLine2Address())) {
            vendorInfo.append("     " + po.getVendorLine2Address() + "\n");
        }
        if (StringUtils.isNotBlank(po.getVendorCityName())) {
            vendorInfo.append("     " + po.getVendorCityName());
        }
        if (StringUtils.isNotBlank(po.getVendorStateCode())) {
            vendorInfo.append(", " + po.getVendorStateCode());
        }
        if (StringUtils.isNotBlank(po.getVendorAddressInternationalProvinceName())) {
            vendorInfo.append(", " + po.getVendorAddressInternationalProvinceName());
        }
        if (StringUtils.isNotBlank(po.getVendorPostalCode())) {
            vendorInfo.append(" " + po.getVendorPostalCode() + "\n");
        }
        else {
            vendorInfo.append("\n");
        }
        if (!KFSConstants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(po.getVendorCountryCode()) && po.getVendorCountry() != null) {
            vendorInfo.append("     " + po.getVendorCountry().getName() + "\n\n");
        }
        else {
            vendorInfo.append("\n\n");
        }
        p = new Paragraph();
        p.add(new Chunk(" Vendor", ver_5_normal));
        p.add(new Chunk(vendorInfo.toString(), cour_7_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);

        StringBuffer shipToInfo = new StringBuffer();
        shipToInfo.append("\n");

        if (po.getAddressToVendorIndicator()) { // use receiving address
            shipToInfo.append("     " + po.getReceivingName() + "\n");
            shipToInfo.append("     " + po.getReceivingLine1Address() + "\n");
            if (StringUtils.isNotBlank(po.getReceivingLine2Address())) {
                shipToInfo.append("     " + po.getReceivingLine2Address() + "\n");
            }
            shipToInfo.append("     " + po.getReceivingCityName() + ", " + po.getReceivingStateCode() + " " + po.getReceivingPostalCode() + "\n");
            if (StringUtils.isNotBlank(po.getReceivingCountryCode()) && !KFSConstants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(po.getReceivingCountryCode())) {
                shipToInfo.append("     " + po.getReceivingCountryName() + "\n");
            }
        }
        else { // use delivery address
            shipToInfo.append("     " + po.getDeliveryToName() + "\n");
            // extra space needed below to separate other text going on same PDF line
            String deliveryBuildingName = po.getDeliveryBuildingName() + " ";
            if (po.isDeliveryBuildingOtherIndicator()) {
                deliveryBuildingName = "";
            }
            shipToInfo.append("     " + deliveryBuildingName + "Room #" + po.getDeliveryBuildingRoomNumber() + "\n");
            shipToInfo.append("     " + po.getDeliveryBuildingLine1Address() + "\n");
            if (StringUtils.isNotBlank(po.getDeliveryBuildingLine2Address())) {
                shipToInfo.append("     " + po.getDeliveryBuildingLine2Address() + "\n");
            }
            shipToInfo.append("     " + po.getDeliveryCityName() + ", " + po.getDeliveryStateCode() + " " + po.getDeliveryPostalCode() + "\n");
            if (StringUtils.isNotBlank(po.getDeliveryCountryCode()) && !KFSConstants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(po.getDeliveryCountryCode())) {
                shipToInfo.append("     " + po.getDeliveryCountryName() + "\n");
            }
        }
        // display deliveryToPhoneNumber disregard of whether receiving or delivery address is used
        shipToInfo.append("     " + po.getDeliveryToPhoneNumber());
        /*
        // display deliveryToPhoneNumber based on the parameter indicator, disregard of whether receiving or delivery address is used
        boolean displayDeliveryPhoneNumber = SpringContext.getBean(ParameterService.class).getIndicatorParameter(PurchaseOrderDocument.class, PurapParameterConstants.DISPLAY_DELIVERY_PHONE_NUMBER_ON_PDF_IND);
        if (displayDeliveryPhoneNumber && StringUtils.isNotBlank(po.getDeliveryToPhoneNumber())) {
            shipToInfo.append("     " + po.getDeliveryToPhoneNumber());
        }
        */

        p = new Paragraph();
        p.add(new Chunk("  Shipping Address", ver_5_normal));
        p.add(new Chunk(shipToInfo.toString(), cour_7_normal));
        cell = new PdfPCell(p);
        infoTable.addCell(cell);

        p = new Paragraph();
        p.add(new Chunk("  Shipping Terms\n", ver_5_normal));
        if (po.getVendorShippingPaymentTerms() != null && po.getVendorShippingTitle() != null) {
            p.add(new Chunk("     " + po.getVendorShippingPaymentTerms().getVendorShippingPaymentTermsDescription(), cour_7_normal));
            p.add(new Chunk(" - " + po.getVendorShippingTitle().getVendorShippingTitleDescription(), cour_7_normal));
        }
        else if (po.getVendorShippingPaymentTerms() != null && po.getVendorShippingTitle() == null) {
            p.add(new Chunk("     " + po.getVendorShippingPaymentTerms().getVendorShippingPaymentTermsDescription(), cour_7_normal));
        }
        else if (po.getVendorShippingTitle() != null && po.getVendorShippingPaymentTerms() == null) {
            p.add(new Chunk("     " + po.getVendorShippingTitle().getVendorShippingTitleDescription(), cour_7_normal));
        }
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);

        p = new Paragraph();
        p.add(new Chunk("  Payment Terms\n", ver_5_normal));
        if (po.getVendorPaymentTerms() != null) {
            p.add(new Chunk("     " + po.getVendorPaymentTerms().getVendorPaymentTermsDescription(), cour_7_normal));
        }
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);

        p = new Paragraph();
        p.add(new Chunk("  Delivery Required By\n", ver_5_normal));

        if (po.getDeliveryRequiredDate() != null && po.getDeliveryRequiredDateReason() != null) {
            p.add(new Chunk("     " + sdf.format(po.getDeliveryRequiredDate()), cour_7_normal));
            p.add(new Chunk(" - " + po.getDeliveryRequiredDateReason().getDeliveryRequiredDateReasonDescription(), cour_7_normal));
        }
        else if (po.getDeliveryRequiredDate() != null && po.getDeliveryRequiredDateReason() == null) {
            p.add(new Chunk("     " + sdf.format(po.getDeliveryRequiredDate()), cour_7_normal));
        }
        else if (po.getDeliveryRequiredDate() == null && po.getDeliveryRequiredDateReason() != null) {
            p.add(new Chunk("     " + po.getDeliveryRequiredDateReason().getDeliveryRequiredDateReasonDescription(), cour_7_normal));
        }
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);

        p = new Paragraph();
        p.add(new Chunk("  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);

        // Nested table for Order Date, etc.
        float[] nestedInfoWidths = { 0.50f, 0.50f };
        PdfPTable nestedInfoTable = new PdfPTable(nestedInfoWidths);
        nestedInfoTable.setSplitLate(false);

        p = new Paragraph();
        p.add(new Chunk("  Order Date\n", ver_5_normal));

        String orderDate = "";
        if (po.getPurchaseOrderInitialOpenTimestamp() != null) {
            orderDate = sdf.format(po.getPurchaseOrderInitialOpenTimestamp());
        }
        else {
            // This date isn't set until the first time this document is printed, so will be null the first time; use today's date.
            orderDate = sdf.format(getDateTimeService().getCurrentSqlDate());
        }

        p.add(new Chunk("     " + orderDate, cour_7_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        nestedInfoTable.addCell(cell);

        p = new Paragraph();
        p.add(new Chunk("  Customer #\n", ver_5_normal));
        if (po.getVendorCustomerNumber() != null) {
            p.add(new Chunk("     " + po.getVendorCustomerNumber(), cour_7_normal));
        }
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        nestedInfoTable.addCell(cell);

        p = new Paragraph();
        p.add(new Chunk("  Delivery Instructions\n", ver_5_normal));
        if (StringUtils.isNotBlank(po.getDeliveryInstructionText())) {
            p.add(new Chunk("     " + po.getDeliveryInstructionText(), cour_7_normal));
        }
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        nestedInfoTable.addCell(cell);

        p = new Paragraph();
        p.add(new Chunk("  Contract ID\n", ver_5_normal));
        if (po.getVendorContract() != null) {
            p.add(new Chunk(po.getVendorContract().getVendorContractName(), cour_7_normal));
        }
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        nestedInfoTable.addCell(cell);

        // Add the nestedInfoTable to the infoTable
        cell = new PdfPCell(nestedInfoTable);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.addCell(cell);

        StringBuffer billToInfo = new StringBuffer();
        billToInfo.append("\n");
        billToInfo.append("     " + po.getBillingName() + "\n");
        billToInfo.append("     " + po.getBillingLine1Address() + "\n");
        if (po.getBillingLine2Address() != null) {
            billToInfo.append("     " + po.getBillingLine2Address() + "\n");
        }
        billToInfo.append("     " + po.getBillingCityName() + ", " + po.getBillingStateCode() + " " + po.getBillingPostalCode() + "\n");
        if (po.getBillingPhoneNumber() != null) {
            billToInfo.append("     " + po.getBillingPhoneNumber());
        }
        if (po.getBillingEmailAddress() != null) {
            billToInfo.append("\n     " + po.getBillingEmailAddress());
        }
        p = new Paragraph();
        p.add(new Chunk("  Billing Address", ver_5_normal));
        p.add(new Chunk("     " + billToInfo.toString(), cour_7_normal));
        p.add(new Chunk("\n Invoice status inquiry: " + statusInquiryUrl, ver_6_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);

        document.add(infoTable);

        PdfPTable notesStipulationsTable = new PdfPTable(1);
        notesStipulationsTable.setWidthPercentage(100);
        notesStipulationsTable.setSplitLate(false);

        p = new Paragraph();
        p.add(new Chunk("  Vendor Note(s)\n", ver_5_normal));
        if (po.getVendorNoteText() != null) {
            p.add(new Chunk("     " + po.getVendorNoteText() + "\n", cour_7_normal));
        }

        PdfPCell tableCell = new PdfPCell(p);
        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        tableCell.setVerticalAlignment(Element.ALIGN_TOP);

        notesStipulationsTable.addCell(tableCell);

        p = new Paragraph();
        p.add(new Chunk("  Vendor Stipulations and Information\n", ver_5_normal));
        if ((po.getPurchaseOrderBeginDate() != null) && (po.getPurchaseOrderEndDate() != null)) {
            p.add(new Chunk("     Order in effect from " + sdf.format(po.getPurchaseOrderBeginDate()) + " to " + sdf.format(po.getPurchaseOrderEndDate()) + ".\n", cour_7_normal));

        }
        Collection<PurchaseOrderVendorStipulation> vendorStipulationsList = po.getPurchaseOrderVendorStipulations();
        if (vendorStipulationsList.size() > 0) {
            StringBuffer vendorStipulations = new StringBuffer();
            for (PurchaseOrderVendorStipulation povs : vendorStipulationsList) {
                vendorStipulations.append("     " + povs.getVendorStipulationDescription() + "\n");
            }
            p.add(new Chunk("     " + vendorStipulations.toString(), cour_7_normal));
        }

        tableCell = new PdfPCell(p);
        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        tableCell.setVerticalAlignment(Element.ALIGN_TOP);
        notesStipulationsTable.addCell(tableCell);

        document.add(notesStipulationsTable);

        // ***** Items table *****
        LOG.debug("createPdf() items table started.");

        float[] itemsWidths = { 0.07f, 0.1f, 0.07f, 0.45f, 0.15f, 0.15f};

        if (!po.isUseTaxIndicator()){
            itemsWidths = ArrayUtils.add(itemsWidths, 0.14f);
            itemsWidths = ArrayUtils.add(itemsWidths, 0.15f);
        }

        PdfPTable itemsTable = new PdfPTable(itemsWidths.length);

        // itemsTable.setCellsFitPage(false); With this set to true a large cell will
        // skip to the next page. The default Table behaviour seems to be what we want:
        // start the large cell on the same page and continue it to the next.
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(itemsWidths);
        itemsTable.setSplitLate(false);

        tableCell = new PdfPCell(new Paragraph("Item\nNo.", ver_5_normal));
        tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(tableCell);
        tableCell = new PdfPCell(new Paragraph("Quantity", ver_5_normal));
        tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(tableCell);
        tableCell = new PdfPCell(new Paragraph("UOM", ver_5_normal));
        tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(tableCell);
        tableCell = new PdfPCell(new Paragraph("Description", ver_5_normal));
        tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(tableCell);
        tableCell = new PdfPCell(new Paragraph("Unit Cost", ver_5_normal));
        tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(tableCell);
        tableCell = new PdfPCell(new Paragraph("Extended Cost", ver_5_normal));
        tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(tableCell);

        if (!po.isUseTaxIndicator()){
            tableCell = new PdfPCell(new Paragraph("Tax Amount", ver_5_normal));
            tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(tableCell);

            tableCell = new PdfPCell(new Paragraph("Total Amount", ver_5_normal));
            tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(tableCell);
        }

        Collection<PurchaseOrderItem> itemsList = new ArrayList();
        if (isRetransmit) {
            itemsList = retransmitItems;
        }
        else {
            itemsList = po.getItems();
        }
        for (PurchaseOrderItem poi : itemsList) {
            if ((poi.getItemType() != null) && (poi.getItemType().isLineItemIndicator() || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)) && lineItemDisplaysOnPdf(poi)) {

                String description = (poi.getItemCatalogNumber() != null) ? poi.getItemCatalogNumber().trim() + " - " : "";
                description = description + ((poi.getItemDescription() != null) ? poi.getItemDescription().trim() : "");
                if (StringUtils.isNotBlank(description)) {
                    if (poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE)) {
                        // If this is a full order discount or trade-in item, we add the item type description to the description.
                        description = poi.getItemType().getItemTypeDescription() + " - " + description;
                    }
                }

                // Above the line item types items display the line number; other types don't.
                if (poi.getItemType().isLineItemIndicator()) {
                    tableCell = new PdfPCell(new Paragraph(poi.getItemLineNumber().toString(), cour_7_normal));
                }
                else {
                    tableCell = new PdfPCell(new Paragraph(" ", cour_7_normal));
                }
                tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                itemsTable.addCell(tableCell);
                String quantity = (poi.getItemQuantity() != null) ? poi.getItemQuantity().toString() : " ";
                tableCell = new PdfPCell(new Paragraph(quantity, cour_7_normal));
                tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell.setNoWrap(true);
                itemsTable.addCell(tableCell);
                tableCell = new PdfPCell(new Paragraph(poi.getItemUnitOfMeasureCode(), cour_7_normal));
                tableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                itemsTable.addCell(tableCell);

                tableCell = new PdfPCell(new Paragraph(" " + description, cour_7_normal));
                tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                itemsTable.addCell(tableCell);
                String unitPrice = poi.getItemUnitPrice().setScale(4, BigDecimal.ROUND_HALF_UP).toString();
                tableCell = new PdfPCell(new Paragraph(unitPrice + " ", cour_7_normal));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCell.setNoWrap(true);
                itemsTable.addCell(tableCell);
                tableCell = new PdfPCell(new Paragraph(numberFormat.format(poi.getExtendedPrice()) + " ", cour_7_normal));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCell.setNoWrap(true);
                itemsTable.addCell(tableCell);

                if (!po.isUseTaxIndicator()){
                    KualiDecimal taxAmount = poi.getItemTaxAmount();
                    taxAmount = taxAmount == null ? KualiDecimal.ZERO : taxAmount;
                    tableCell = new PdfPCell(new Paragraph(numberFormat.format(taxAmount) + " ", cour_7_normal));
                    tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCell.setNoWrap(true);
                    itemsTable.addCell(tableCell);

                    tableCell = new PdfPCell(new Paragraph(numberFormat.format(poi.getTotalAmount()) + " ", cour_7_normal));
                    tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCell.setNoWrap(true);
                    itemsTable.addCell(tableCell);
                }

            }
        }
        // Blank line before totals
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");

        if (!po.isUseTaxIndicator()){
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
        }

        //Next Line
        if (!po.isUseTaxIndicator()){

            //Print Total Prior to Tax
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");

            tableCell = new PdfPCell(new Paragraph("Total Prior to Tax: ", ver_10_normal));
            tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(tableCell);
            itemsTable.addCell(" ");
            KualiDecimal totalDollarAmount = new KualiDecimal(BigDecimal.ZERO);
            if (po instanceof PurchaseOrderRetransmitDocument) {
                totalDollarAmount = ((PurchaseOrderRetransmitDocument) po).getTotalPreTaxDollarAmountForRetransmit();
            }
            else {
                totalDollarAmount = po.getTotalPreTaxDollarAmount();
            }
            tableCell = new PdfPCell(new Paragraph(numberFormat.format(totalDollarAmount) + " ", cour_7_normal));
            tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCell.setNoWrap(true);
            itemsTable.addCell(tableCell);

            //Print Total Tax
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");

            tableCell = new PdfPCell(new Paragraph("Total Tax: ", ver_10_normal));
            tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(tableCell);
            itemsTable.addCell(" ");
            totalDollarAmount = new KualiDecimal(BigDecimal.ZERO);
            if (po instanceof PurchaseOrderRetransmitDocument) {
                totalDollarAmount = ((PurchaseOrderRetransmitDocument) po).getTotalTaxDollarAmountForRetransmit();
            }
            else {
                totalDollarAmount = po.getTotalTaxAmount();
            }
            tableCell = new PdfPCell(new Paragraph(numberFormat.format(totalDollarAmount) + " ", cour_7_normal));
            tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCell.setNoWrap(true);
            itemsTable.addCell(tableCell);

        }

        // Totals line; first 3 cols empty
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");

        if (!po.isUseTaxIndicator()){
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
        }

        tableCell = new PdfPCell(new Paragraph("Total order amount: ", ver_10_normal));
        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemsTable.addCell(tableCell);
        itemsTable.addCell(" ");
        KualiDecimal totalDollarAmount = new KualiDecimal(BigDecimal.ZERO);
        if (po instanceof PurchaseOrderRetransmitDocument) {
            totalDollarAmount = ((PurchaseOrderRetransmitDocument) po).getTotalDollarAmountForRetransmit();
        }
        else {
            totalDollarAmount = po.getTotalDollarAmount();
        }
        tableCell = new PdfPCell(new Paragraph(numberFormat.format(totalDollarAmount) + " ", cour_7_normal));
        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tableCell.setNoWrap(true);
        itemsTable.addCell(tableCell);
        // Blank line after totals
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");

        if (!po.isUseTaxIndicator()){
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
        }

        document.add(itemsTable);

        // Contract language.
        LOG.debug("createPdf() contract language started.");
        document.add(new Paragraph(contractLanguage, ver_6_normal));
        document.add(new Paragraph("\n", ver_6_normal));

        // ***** Signatures table *****
        LOG.debug("createPdf() signatures table started.");
        float[] signaturesWidths = { 0.30f, 0.70f };
        PdfPTable signaturesTable = new PdfPTable(signaturesWidths);
        signaturesTable.setWidthPercentage(100);
        signaturesTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        signaturesTable.setSplitLate(false);

        // Director signature and "for more info" line; only on APOs
        if (po.getPurchaseOrderAutomaticIndicator()) {
            // Empty cell.
            cell = new PdfPCell(new Paragraph(" ", cour_7_normal));
            cell.setBorderWidth(0);
            signaturesTable.addCell(cell);

            //boolean displayRequestorEmail = true; //SpringContext.getBean(ParameterService.class).getIndicatorParameter(PurchaseOrderDocument.class, PurapParameterConstants.DISPLAY_REQUESTOR_EMAIL_ADDRESS_ON_PDF_IND);
            if (StringUtils.isBlank(po.getInstitutionContactName()) || StringUtils.isBlank(po.getInstitutionContactPhoneNumber()) || StringUtils.isBlank(po.getInstitutionContactEmailAddress())) {
                //String emailAddress = displayRequestorEmail ? "  " + po.getRequestorPersonEmailAddress() : "";
                //p = new Paragraph("For more information contact: " + po.getRequestorPersonName() + "  " + po.getRequestorPersonPhoneNumber() + emailAddress, cour_7_normal);
                p = new Paragraph("For more information contact: " + po.getRequestorPersonName() + "  " + po.getRequestorPersonPhoneNumber() + "  " + po.getRequestorPersonEmailAddress(), cour_7_normal);
            }
            else {
                //String emailAddress = displayRequestorEmail ? "  " + po.getInstitutionContactEmailAddress() : "";
                //p = new Paragraph("For more information contact: " + po.getInstitutionContactName() + "  " + po.getInstitutionContactPhoneNumber() + emailAddress, cour_7_normal);
                p = new Paragraph("For more information contact: " + po.getInstitutionContactName() + "  " + po.getInstitutionContactPhoneNumber() + "  " + po.getInstitutionContactEmailAddress(), cour_7_normal);
            }
            cell = new PdfPCell(p);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidth(0);
            signaturesTable.addCell(cell);

            Image directorSignature = null;
            if (StringUtils.isNotBlank(directorSignatureImage)) {
                try {
                    directorSignature = Image.getInstance(directorSignatureImage);
                }
                catch (FileNotFoundException e) {
                    LOG.info("The director signature image [" + directorSignatureImage + "] is not available.  Defaulting to the default image.");
                }
            }

            if (directorSignature == null) {
                // an empty cell if the contract manager signature image is not available.
                cell = new PdfPCell();
            }
            else {
                directorSignature.scalePercent(30, 30);
                cell = new PdfPCell(directorSignature, false);
            }

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidth(0);
            signaturesTable.addCell(cell);

            // Empty cell.
            cell = new PdfPCell(new Paragraph(" ", cour_7_normal));
            cell.setBorderWidth(0);
            signaturesTable.addCell(cell);
        }

        // Director name and title; on every pdf.
        p = new Paragraph();
        if (LOG.isDebugEnabled()) {
            LOG.debug("createPdf() directorName parameter: " + directorName);
        }
        if (po.getPurchaseOrderAutomaticIndicator()) { // The signature is on the pdf; use small font.
            p.add(new Chunk(directorName, ver_6_normal));
        }
        else { // The signature isn't on the pdf; use larger font.
            p.add(new Chunk(directorName, ver_10_normal));
        }
        p.add(new Chunk("\n" + directorTitle, ver_4_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBorderWidth(0);
        signaturesTable.addCell(cell);

        // Contract manager signature, name and phone; only on non-APOs
        if (!po.getPurchaseOrderAutomaticIndicator()) {

            Image contractManagerSignature = null;
            if (StringUtils.isNotBlank(contractManagerSignatureImage)) {
                try {
                    contractManagerSignature = Image.getInstance(contractManagerSignatureImage);
                } catch (FileNotFoundException e) {
                    LOG.info("The contract manager image [" + contractManagerSignatureImage + "] is not available.  Defaulting to the default image.");
                }
            }

            if (contractManagerSignature == null) {
                // an empty cell if the contract manager signature image is not available.
                cell = new PdfPCell();
            }
            else {
                contractManagerSignature.scalePercent(15, 15);
                cell = new PdfPCell(contractManagerSignature, false);
            }
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidth(0);
            signaturesTable.addCell(cell);

            // Empty cell.
            cell = new PdfPCell(new Paragraph(" ", ver_10_normal));
            cell.setBorderWidth(0);
            signaturesTable.addCell(cell);

            cell = new PdfPCell(new Paragraph(po.getContractManager().getContractManagerName() + "  " + po.getContractManager().getContractManagerPhoneNumber(), cour_7_normal));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorderWidth(0);
            signaturesTable.addCell(cell);
        }
        else { // Empty cell.
            cell = new PdfPCell(new Paragraph(" ", ver_10_normal));
            cell.setBorderWidth(0);
            signaturesTable.addCell(cell);
        }
        document.add(signaturesTable);

        document.close();
        LOG.debug("createPdf()pdf document closed.");
    } // End of createPdf()

    /**
     * Determines whether the item should be displayed on the pdf.
     *
     * @param poi  The PurchaseOrderItem to be determined whether it should be displayed on the pdf.
     * @return     boolean true if it should be displayed on the pdf.
     */
    private boolean lineItemDisplaysOnPdf(PurchaseOrderItem poi) {
        LOG.debug("lineItemDisplaysOnPdf() started");

        // Shipping, freight, full order discount and trade in items.
        if ((poi.getItemType() != null) && (poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE) || poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE))) {

            // If the unit price is not null and either the unit price > 0 or the item type is full order discount or trade in,
            // we'll display this line item on pdf.
            if ((poi.getItemUnitPrice() != null) && ((poi.getItemUnitPrice().compareTo(zero.bigDecimalValue()) == 1) || (poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) || (poi.getItemType().getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("lineItemDisplaysOnPdf() Item type is " + poi.getItemType().getItemTypeCode() + ". Unit price is " + poi.getItemUnitPrice() + ". Display on pdf.");
                }
                return true;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("lineItemDisplaysOnPdf() Item type is " + poi.getItemType().getItemTypeCode() + ". Unit price is " + poi.getItemUnitPrice() + ". Don't display on pdf.");
            }
            return false;
        }
        else if ((poi.getItemType() != null) && poi.getItemType().isLineItemIndicator()) {
            if (poi.getItemQuantity() == null && poi.getItemUnitPrice() == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("lineItemDisplaysOnPdf() Item type is " + poi.getItemType().getItemTypeCode() + " OrderQuantity and unit price are both null. Display on pdf.");
                }
                return true;
            }
            if ((poi.getItemType().isAmountBasedGeneralLedgerIndicator() && ((poi.getItemUnitPrice() != null) && (poi.getItemUnitPrice().compareTo(zero.bigDecimalValue()) >= 0))) || (((poi.getItemType().isQuantityBasedGeneralLedgerIndicator()) && (poi.getItemQuantity().isGreaterThan(zero))) && (poi.getItemUnitPrice() != null))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("lineItemDisplaysOnPdf() Item type is " + poi.getItemType().getItemTypeCode() + " OrderQuantity is " + poi.getItemQuantity() + ". Unit price is " + poi.getItemUnitPrice() + ". Display on pdf.");
                }
                return true;
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("lineItemDisplaysOnPdf() Item type is " + poi.getItemType().getItemTypeCode() + " and item order quantity is " + poi.getItemQuantity() + " and item unit price is " + poi.getItemUnitPrice() + ". Don't display on pdf.");
                }
            }
        }
        return false;
    }

}
