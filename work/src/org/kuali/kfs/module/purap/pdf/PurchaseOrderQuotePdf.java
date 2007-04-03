
package org.kuali.module.purap.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.CampusParameter;
import org.kuali.module.purap.bo.ContractManager;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.exceptions.PurError;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PurchaseOrderQuotePdf extends PurapPdf {
    private static Log LOG = LogFactory.getLog(PurchaseOrderQuotePdf.class);

    public PurchaseOrderQuotePdf() {
        super();
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
        LOG.debug("onOpenDocument() started.");
        try {
            float[] headerWidths = {0.20f, 0.60f, 0.20f};
            headerTable = new PdfPTable(headerWidths);
            headerTable.setWidthPercentage(100);
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            headerTable.getDefaultCell().setBorderWidth(0);
            headerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
      
            logo = Image.getInstance(logoImage);
            logo.scalePercent(3,3);
            headerTable.addCell(new Phrase(new Chunk(logo, 0, 0)));

            PdfPCell cell;
            cell = new PdfPCell(new Paragraph("REQUEST FOR QUOTATION\nTHIS IS NOT AN ORDER",ver_17_normal));
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(cell);

            Paragraph p = new Paragraph();
            p.add(new Chunk("\n     R.Q. Number: ", ver_8_bold));
            p.add(new Chunk(po.getPurapDocumentIdentifier()+"\n", cour_10_normal));
            cell = new PdfPCell(p);
            cell.setBorderWidth(0);
            headerTable.addCell(cell);

            // initialization of the template
            tpl = writer.getDirectContent().createTemplate(100, 100);
            // initialization of the font
            helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
        } catch(Exception e) {
            throw new ExceptionConverter(e);
        }
    }    

    /**
     * Gets a PageEvents object.
     * @return a new PageEvents object
     */
    public PurchaseOrderQuotePdf getPageEvents() {
        LOG.debug("getPageEvents() started.");
        return new PurchaseOrderQuotePdf();
    }
  
    public void generatePOQuotePDF (PurchaseOrderDocument po, PurchaseOrderVendorQuote poqv, 
        String campusName, String contractManagerCampusCode, String logoImage, 
        ByteArrayOutputStream byteArrayOutputStream, String environment) {
        LOG.debug("generatePOQuotePDF() started for po number " + po.getPurapDocumentIdentifier());
    
        Collection errors = new ArrayList();
    
        try {
            Document doc = getDocument(9, 9, 70, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, byteArrayOutputStream);
            this.createPOQuotePdf(po, poqv, campusName, contractManagerCampusCode, logoImage, 
                doc, writer, environment);
        } catch (DocumentException de) {
            LOG.error(de.getMessage(),de);
            de.printStackTrace();
            throw new PurError("Document Exception when trying to save a Purchase Order Quote PDF", de);
        } 
    }

    public void savePOQuotePDF (PurchaseOrderDocument po, PurchaseOrderVendorQuote poqv, 
        String pdfFileLocation, String pdfFilename, String campusName, String contractManagerCampusCode, 
        String logoImage, String environment) {
        LOG.debug("savePOQuotePDF() started for po number " + po.getPurapDocumentIdentifier());
    
        try {
            Document doc = this.getDocument(9, 9, 70, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfFileLocation + pdfFilename));
            this.createPOQuotePdf(po, poqv, campusName, contractManagerCampusCode, logoImage, 
                doc, writer, environment);
        } catch (DocumentException de) {
            LOG.error(de.getMessage(),de);
            de.printStackTrace();
            throw new PurError("Document Exception when trying to save a Purchase Order Quote PDF", de);
        } catch (FileNotFoundException f) {
            LOG.error(f.getMessage(),f);
            f.printStackTrace();
            throw new PurError("FileNotFound Exception when trying to save a Purchase Order Quote PDF", f);
        }
    }

    /**
     * Create a PDF.
     * 
     * @return
     */
    private void createPOQuotePdf (PurchaseOrderDocument po, PurchaseOrderVendorQuote poqv, String campusName, 
        String contractManagerCampusCode, String logoImage, Document document, PdfWriter writer, 
        String environment) throws DocumentException {
        LOG.debug("createQuotePdf() started for po number " + po.getPurapDocumentIdentifier());
        // These have to be set because they are used by the onOpenDocument() and onStartPage() methods.
        this.campusName = campusName;
        this.po = po;
        this.logoImage = logoImage;
        this.environment = environment;
    
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);

        // TODO 2006 ripierce: We decided to hard code the purchasing addresses for the 11/19/05 release,
        //   will redesign and probably get them from the campus table later.
        /*
        if (contractManagerCampusCode.equals("BL")) {
            purchasingAddressFull = "     Indiana University\n     Purchasing Department\n     Poplars Building\n     P.O. Box 4040\n     Bloomington, IN  47402";
            purchasingAddressPartial = "Indiana University, Bloomington, IN  47402";
        } else if (contractManagerCampusCode.equals("IN")) {
            purchasingAddressFull = "     Indiana Univ. Purdue Univ. - Indianapolis\n     Purchasing Department\n     620 Union Drive\n     Suite 571a\n     Indianapolis, IN  46202-5169";
            purchasingAddressPartial = "Indiana Univ. Purdue Univ., Indianapolis, IN  46202-5169";
        } else if (contractManagerCampusCode.equals("KO")) {
            purchasingAddressFull = "     Indiana University Kokomo\n     2300 S Washington St\n     Kokomo, IN  46904-9003";
            purchasingAddressPartial = "Indiana University Kokomo, Kokomo, IN  46904-9003";
        } else if (contractManagerCampusCode.equals("SB")) {
            purchasingAddressFull = "     Indiana University South Bend\n     1700 Mishawaka Ave, Rm A239\n     P.O. Box 7111\n     South Bend, IN  46634";
            purchasingAddressPartial = "Indiana University South Bend, South Bend, IN  46634";
        } else if (contractManagerCampusCode.equals("SE")) {
            purchasingAddressFull = "     Indiana University Southeast\n     Service Building\n     4201 Grant Line Road\n     New Albany, IN  47150";
            purchasingAddressPartial = "Indiana University Southeast, New Albany, IN  47150";
        } else if (contractManagerCampusCode.equals("NW")) {
            purchasingAddressFull = "     Indiana University Northwest\n     Purchasing Department\n     3400 Broadway\n     Gary, IN  46408";
            purchasingAddressPartial = "Indiana University Northwest, Gary, IN  46408";
        } else if (contractManagerCampusCode.equals("EA")) {
            purchasingAddressFull = "     Indiana University East\n     Purchasing Department\n     2325 Chester Boulevard\n     Richmond, IN  47374-1289";
            purchasingAddressPartial = "Indiana University East, Richmond, IN  47374-1289";
        }
        */
        
        CampusParameter campusParameter = getCampusParameter(contractManagerCampusCode);
        String purchasingAddressFull = getPurchasingAddressFull(campusParameter);
        String purchasingAddressPartial = getPurchasingAddressPartial(campusParameter);
        
        // Turn on the page events that handle the header and page numbers.
        PurchaseOrderQuotePdf events = new PurchaseOrderQuotePdf().getPageEvents();
        writer.setPageEvent(this); // Passing in "this" lets it know about the po, campusName, etc.

        document.open();

        PdfPCell cell;
        Paragraph p = new Paragraph();

        // ***** Info table (address, vendor, other info) *****
        LOG.debug("createQuotePdf() info table started.");
        float[] infoWidths = {0.45f, 0.55f};
        PdfPTable infoTable = new PdfPTable(infoWidths);
        infoTable.setWidthPercentage(100);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSplitLate(false);
    
        p = new Paragraph();
        ContractManager contractManager = po.getVendorContract().getContractManager();
        p.add(new Chunk("\n Return this form to:\n", ver_8_bold));
        p.add(new Chunk(purchasingAddressFull+"\n", cour_10_normal));
        p.add(new Chunk("\n", cour_10_normal));
        p.add(new Chunk(" Fax #: ", ver_6_normal));
        p.add(new Chunk(contractManager.getContractManagerFaxNumber()+"\n", cour_10_normal));
        p.add(new Chunk(" Contract Manager: ", ver_6_normal));
        p.add(new Chunk(contractManager.getContractManagerName()+" "+contractManager.getContractManagerPhoneNumber()+"\n", cour_10_normal));
        p.add(new Chunk("\n", cour_10_normal));
        p.add(new Chunk(" To:\n", ver_6_normal));
        StringBuffer vendorInfo = new StringBuffer();
        if (StringUtils.isNotBlank(poqv.getVendorAttentionName())) {
            vendorInfo.append("     Attention: "+poqv.getVendorAttentionName().trim()+"\n");
        }
        vendorInfo.append("     "+poqv.getVendorName()+"\n");
        if (StringUtils.isNotBlank(poqv.getVendorLine1Address())) {
            vendorInfo.append("     "+poqv.getVendorLine1Address()+"\n");
        }
        if (StringUtils.isNotBlank(poqv.getVendorLine2Address())) {
            vendorInfo.append("     "+poqv.getVendorLine2Address()+"\n");
        }
        if (StringUtils.isNotBlank(poqv.getVendorCityName())) {
            vendorInfo.append("     "+poqv.getVendorCityName());
        }
        if ( (StringUtils.isNotBlank(poqv.getVendorStateCode())) && (! poqv.getVendorStateCode().equals("--")) ) {
            vendorInfo.append(", "+poqv.getVendorStateCode());
        }
        if (StringUtils.isNotBlank(poqv.getVendorPostalCode())) {
            vendorInfo.append(" "+poqv.getVendorPostalCode()+"\n");
        } else {
            vendorInfo.append("\n");
        }

        if (! Constants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(poqv.getVendorCountryCode())) {
            //TODO: Get the vendor country name instead of country code   
            vendorInfo.append("     "+poqv.getPurchaseOrder().getVendorCountry().getPostalCountryName()+"\n\n");
        } else {
            vendorInfo.append("\n\n");
        }

        p.add(new Chunk(vendorInfo.toString(), cour_10_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);
    
        p = new Paragraph();
        p.add(new Chunk("\n     R.Q. Number: ", ver_8_bold));
        p.add(new Chunk(po.getPurapDocumentIdentifier()+"\n", cour_10_normal));
        Timestamp now = new Timestamp((new java.util.Date()).getTime());
        String requestDate = (new Date(now.getTime())).toString();
        if (poqv.getPurchaseOrderQuoteTransmitDate() != null) {
          requestDate = (new Date(poqv.getPurchaseOrderQuoteTransmitDate().getTime())).toString();
        }
        p.add(new Chunk("     R.Q. Date: ", ver_8_bold));
        p.add(new Chunk(requestDate+"\n", cour_10_normal));
        String dueDate = (new Date(po.getPurchaseOrderQuoteDueDate().getTime())).toString();
        p.add(new Chunk("     RESPONSE MUST BE RECEIVED BY: ", ver_8_bold));
        p.add(new Chunk(dueDate+"\n\n", cour_10_normal));
    
        StringBuffer quoteStipulations = new StringBuffer();
        quoteStipulations.append(" - Unless otherwise understood, there are no restrictions on the number of items or the quantity that may be ordered.\n");
        quoteStipulations.append(" - No substitutes will be considered unless a complete description is given.\n");
        quoteStipulations.append(" - The right is reserved to reject any offer.\n");
        quoteStipulations.append(" - Indiana University is a political subdivision of the State of Indiana and is not subject to any sales or use tax imposed by the State of Indiana nor excise taxes imposed by the Federal Government.\n");
        quoteStipulations.append(" - Indiana University is an Equal Opportunity Employer.\n");
        quoteStipulations.append(" - We are a public entity subject to statutory disclosure requirements. All prohibitions against disclosure of a supplier’s pricing, proprietary and confidential information submitted under this RFQ, shall be construed as permitting disclosure when required by the Indiana Open Records Act, Indiana Code Section I C 5-14-et seq.\n");
        p.add(new Chunk(quoteStipulations.toString(), ver_6_normal));
        p.add(new Chunk("\n ALL QUOTES MUST BE TOTALED", ver_12_normal));
        cell = new PdfPCell(p);
        infoTable.addCell(cell);
    
        document.add(infoTable);

        // ***** Notes and Stipulations table *****
        // ripierce: the notes and stipulations table is type Table instead of PdfPTable
        //   because Table has the method setCellsFitPage and I can't find an equivalent
        //   in PdfPTable. Long notes or stipulations would break to the next page, leaving
        //   a large white space on the previous page.
        PdfPTable notesStipulationsTable = new PdfPTable(1);
        notesStipulationsTable.setWidthPercentage(100);
        notesStipulationsTable.setSplitLate(false);
    
        p = new Paragraph();
        p.add(new Chunk("  Vendor Stipulations and Information\n\n", ver_6_normal));
        if ( (po.getPurchaseOrderBeginDate() != null) && (po.getPurchaseOrderEndDate() != null) ) {
            p.add(new Chunk("     Order in effect from "+po.getPurchaseOrderBeginDate()+" to "+po.getPurchaseOrderEndDate()+".\n", cour_10_normal));
        }
        Collection<PurchaseOrderVendorStipulation> vendorStipulationsList = po.getPurchaseOrderVendorStipulations();
        if (vendorStipulationsList.size() > 0) {
            StringBuffer vendorStipulations = new StringBuffer();
            for (PurchaseOrderVendorStipulation povs: vendorStipulationsList) {
                vendorStipulations.append("     "+povs.getVendorStipulationDescription()+"\n");
            }
            p.add(new Chunk("     "+vendorStipulations.toString(), cour_10_normal));
        }
    //  For testing large stipulations.
    //    String longText = "long\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\nlong\n text";
    //    p.add(new Chunk("     "+longText, cour_10_normal));

        PdfPCell tableCell = new PdfPCell(p);
        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        tableCell.setVerticalAlignment(Element.ALIGN_TOP);
        notesStipulationsTable.addCell(tableCell);
        document.add(notesStipulationsTable);
    
        // ***** Items table *****
        LOG.debug("createQuotePdf() items table started.");
        float[] itemsWidths = {0.07f, 0.1f, 0.07f, 0.50f, 0.13f, 0.13f};
        PdfPTable itemsTable = new PdfPTable(6);
        //    itemsTable.setCellsFitPage(false); With this set to true a large cell will
        //      skip to the next page. The default Table behaviour seems to be what we want:
        //      start the large cell on the same page and continue it to the next.    
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(itemsWidths);
        itemsTable.setSplitLate(false);
    
        tableCell = createCell("Item\nNo.", false, false, false, false, Element.ALIGN_CENTER,  ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("Quantity", false, false, false, false, Element.ALIGN_CENTER,  ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("UOM", false, false, false, false, Element.ALIGN_CENTER,  ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("Description", false, false, false, false, Element.ALIGN_CENTER,  ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("Unit Cost\n(Required)", false, false, false, false, Element.ALIGN_CENTER,  ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("Extended Cost\n(Required)", false, false, false, false, Element.ALIGN_CENTER,  ver_6_normal);
        itemsTable.addCell(tableCell);
    
        if (StringUtils.isNotBlank(po.getPurchaseOrderQuoteVendorNoteText())) {
            // Vendor notes line.
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            tableCell = createCell(po.getPurchaseOrderQuoteVendorNoteText(), false, false, false, false, Element.ALIGN_LEFT, cour_10_normal);
            itemsTable.addCell(tableCell);
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
        }
    
        for (PurchaseOrderItem poi: (List<PurchaseOrderItem>)po.getItems()) {
            if ( (poi.getItemType() != null) && 
      	         ( StringUtils.isNotBlank(poi.getItemDescription())) &&
                 ( poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE) ||
                poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE) ||
                poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE) ||
                poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE) ||
                poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE) )
            ) {
                // "ITEM"s display the line number; other types don't.
        
           
                String description = "";
                description = (StringUtils.isNotBlank(poi.getItemCatalogNumber())) ? poi.getItemCatalogNumber().trim()+" - " : "";
                description = description+((StringUtils.isNotBlank(poi.getItemDescription())) ? poi.getItemDescription().trim() : "");
 
                //If this is a full order discount item or trade in item, we add the 
                //itemType description and a dash to the purchase order item description.        
                if (StringUtils.isNotBlank(poi.getItemDescription())) {
                    if (poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE) ||
                        poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)) {
          	            description = poi.getItemDescription() + " - " + description;  
                    } 
                } 

                //We can do the normal table now because description is not too long.
                String itemLineNumber = new String();
                if (poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE)) {
                    itemLineNumber = poi.getItemLineNumber().toString();
                } else {
                    itemLineNumber = "";
                }
                tableCell = createCell(itemLineNumber, false, false, false, false, Element.ALIGN_CENTER, cour_10_normal);
                itemsTable.addCell(tableCell);
                String quantity = (poi.getItemOrderedQuantity() != null) ? poi.getItemOrderedQuantity().toString() : " ";
                tableCell = createCell(quantity, false, false, false, false, Element.ALIGN_CENTER, cour_10_normal);
                itemsTable.addCell(tableCell);
                tableCell = createCell(poi.getItemUnitOfMeasureCode(), false, false, false, false, Element.ALIGN_CENTER, cour_10_normal);
                itemsTable.addCell(tableCell);
              
                tableCell = createCell(description, false, false, false, false, Element.ALIGN_LEFT, cour_10_normal);
                itemsTable.addCell(tableCell);
                itemsTable.addCell(" ");
                itemsTable.addCell(" ");

            }
        }

        // Blank line before totals
        createBlankRowInItemsTable(itemsTable);

        // Totals line.
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        tableCell = createCell("Total: ", false, false, false, false, Element.ALIGN_RIGHT, ver_10_normal);
        itemsTable.addCell(tableCell);
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        // Blank line after totals
        createBlankRowInItemsTable(itemsTable);

        document.add(itemsTable);
    
        LOG.debug("createQuotePdf() vendorFillsIn table started.");
        float[] vendorFillsInWidths = {0.50f, 0.50f};
        PdfPTable vendorFillsInTable = new PdfPTable(vendorFillsInWidths);
        vendorFillsInTable.setWidthPercentage(100);
        vendorFillsInTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        vendorFillsInTable.setSplitLate(false);
        vendorFillsInTable.getDefaultCell().setBorderWidth(0);
        vendorFillsInTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        vendorFillsInTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
    
        // New row
        String important = new String("\nIMPORTANT: The information and signature below MUST BE COMPLETED or your offer may be rejected.\n");
        cell = createCell(important, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        cell.setColspan(2);
        vendorFillsInTable.addCell(cell);
        // New row
        String cashDiscount = new String("Terms of Payment:  Cash discount_________%_________Days-Net________Days\n");
        cell = createCell(cashDiscount, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        cell.setColspan(2);
        vendorFillsInTable.addCell(cell);
        // New row
        String fob = new String(" FOB: __ Destination (Title)\n");
        cell = createCell(fob, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        String freightVendor = new String(" __ Freight Vendor Paid (Allowed)\n");
        cell = createCell(freightVendor, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        // New row
        String shipping = new String("          __ Shipping Point (Title)\n");
        cell = createCell(shipping, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        String freightPrepaid = new String(" __ Freight Prepaid & Added Amount $_________\n");
        cell = createCell(freightPrepaid, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        // New row
        String commonCarrier = new String("      If material will ship common carrier, please provide the following:\n");
        cell = createCell(commonCarrier, true, false, false, false, Element.ALIGN_LEFT, ver_8_bold);
        cell.setColspan(2);
        vendorFillsInTable.addCell(cell);
        // New row
        String origin = new String ("      Point of origin and zip code: ______________________ Weight: _________ Class: _________\n");
        cell = createCell(origin, true, false, false, false, Element.ALIGN_LEFT, ver_8_bold);
        cell.setColspan(2);
        vendorFillsInTable.addCell(cell);
        // New row
        p = new Paragraph();
        p.add(new Chunk(" Unless otherwise stated, all material to be shipped to ", ver_8_normal));
        purchasingAddressPartial = po.getDeliveryCityName()+", "+po.getDeliveryStateCode()+" "+po.getDeliveryPostalCode();
        p.add(new Chunk(purchasingAddressPartial+"\n", cour_10_normal));
        cell = new PdfPCell(p);
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0);
        vendorFillsInTable.addCell(cell);
        // New row
        String offerEffective = new String(" Offer effective until (Date):_____________\n");
        cell = createCell(offerEffective, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        String deliverBy = new String(" Delivery can be made by (Date):_____________\n");
        cell = createCell(deliverBy, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        // New row
        String sign = new String(" SIGN HERE:____________________________\n");
        cell = createCell(sign, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        String date = new String(" DATE:____________________________\n");
        cell = createCell(date, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        // New row
        String name = new String(" PRINT NAME:____________________________\n");
        cell = createCell(name, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        String phone = new String(" PHONE:____________________________\n");
        cell = createCell(phone, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        // New row
        String company = new String(" COMPANY:____________________________\n");
        cell = createCell(company, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        String fax = new String(" FAX:____________________________\n");
        cell = createCell(fax, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);

        document.add(vendorFillsInTable);
        document.close();
        LOG.debug("createQuotePdf()pdf document closed.");
    }  // End of createQuotePdf()
  
  
    private void createBlankRowInItemsTable(PdfPTable itemsTable) {
        //We're adding 6 cells because each row in the items table
  	    //contains 6 columns.
  	    for (int i=0; i<6; i++) {	
            itemsTable.addCell(" ");  	
        }
    }
    /**
     *  This is a helper method to create a PdfPCell. We can specify the content, font,
     *  horizontal alignment, border (borderless, no bottom border, no right border, 
     *  no top border, etc.
     * 
     * @param content
     * @param borderless
     * @param noBottom
     * @param noRight
     * @param noTop
     * @param horizontalAlignment
     * @param font
     * @return an instance of PdfPCell which content and attributes were set by the input params.
     */
    private PdfPCell createCell(String content, boolean borderless, boolean noBottom, boolean noRight, boolean noTop, int horizontalAlignment, Font font) {
        PdfPCell tableCell = new PdfPCell(new Paragraph(content, font));
        if (borderless) {
            tableCell.setBorder(0);
        }
        if (noBottom) {
            tableCell.setBorderWidthBottom(0);
        }
        if (noTop) {
            tableCell.setBorderWidthTop(0);
        }
        if (noRight) {
            tableCell.setBorderWidthRight(0);
        }
        tableCell.setHorizontalAlignment(horizontalAlignment);
        return tableCell;
    }
    
    private CampusParameter getCampusParameter(String contractManagerCampusCode) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(PropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter)((List) SpringServiceLocator.getBusinessObjectService().findMatching(CampusParameter.class, criteria)).get(0);
        
        return campusParameter;
    }
 
    private String getPurchasingAddressFull(CampusParameter campusParameter) {
        String purchasingAddressFull = ""; // Used at the top of the quote  - "Return this form to"
        String indent = "     ";
        String purchasingCampusName = indent + campusParameter.getPurchasingInstitutionName();
        String purchasingDepartmentName = indent + campusParameter.getPurchasingDepartmentName();
        String purchasingAddressLine1 = indent + campusParameter.getPurchasingDepartmentLine1Address();
        String purchasingAddressLine2 = indent + campusParameter.getPurchasingDepartmentLine2Address();
        String purchasingCity = indent + campusParameter.getPurchasingDepartmentCityName();
        String purchasingState = campusParameter.getPurchasingDepartmentStateCode();
        String purchasingZipCode = campusParameter.getPurchasingDepartmentZipCode();
        String purchasingCountry = campusParameter.getPurchasingDepartmentCountryCode();
        purchasingAddressFull = purchasingCampusName + "\n" + purchasingDepartmentName + "\n" + purchasingAddressLine1 + "\n" + purchasingAddressLine2 + "\n" + purchasingCity + ", " + purchasingState + " " + purchasingZipCode + " " + purchasingCountry;
        
        return purchasingAddressFull;
    }
    
    private String getPurchasingAddressPartial(CampusParameter campusParameter) {
        String purchasingAddressPartial = ""; // Used at the bottom - "All material to be shipped to"

        String purchasingCampusName = campusParameter.getPurchasingInstitutionName();
        String purchasingCity = campusParameter.getPurchasingDepartmentCityName();
        String purchasingState = campusParameter.getPurchasingDepartmentStateCode();
        String purchasingZipCode = campusParameter.getPurchasingDepartmentZipCode();
        
        purchasingAddressPartial = purchasingCampusName + ", " + purchasingCity + ", " + purchasingState + " " + purchasingZipCode;
  
        return purchasingAddressPartial;
    }
}
/*
Copyright (c) 2004, 2005 The National Association of College and
University Business Officers, Cornell University, Trustees of Indiana
University, Michigan State University Board of Trustees, Trustees of San
Joaquin Delta College, University of Hawai'i, The Arizona Board of
Regents on behalf of the University of Arizona, and the r*smart group.

Licensed under the Educational Community License Version 1.0 (the
"License"); By obtaining, using and/or copying this Original Work, you
agree that you have read, understand, and will comply with the terms and
conditions of the Educational Community License.

You may obtain a copy of the License at:

http://kualiproject.org/license.html

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE. 
*/
