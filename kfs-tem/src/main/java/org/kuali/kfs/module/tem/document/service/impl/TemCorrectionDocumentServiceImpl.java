/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.document.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.kuali.kfs.module.tem.businessobject.AgencyEntryFull;
import org.kuali.kfs.module.tem.document.TemCorrectionProcessDocument;
import org.kuali.kfs.module.tem.document.service.TemCorrectionDocumentService;
import org.kuali.kfs.module.tem.document.web.struts.TemCorrectionForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.krad.comparator.NumericValueComparator;
import org.kuali.rice.krad.comparator.StringValueComparator;
import org.kuali.rice.krad.comparator.TemporalValueComparator;
/*import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;*/

public class TemCorrectionDocumentServiceImpl implements TemCorrectionDocumentService {

    private List<Column> cachedColumns = null;

    protected String batchFileDirectoryName;
    /**
     * Returns metadata to help render columns in the TMCP. Do not modify this list or the contents in this list.
     *
     * @param docId the document id of a GLCP document
     * @return a List of Columns to render
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#getTableRenderColumnMetadata(java.lang.String)
     */
    @Override
    public List<Column> getTableRenderColumnMetadata(String docId) {
        synchronized (this) {
            if (cachedColumns == null) {
                cachedColumns = new ArrayList<Column>();
                Column columnToAdd;

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Agency Code");
                columnToAdd.setPropertyName("creditCardOrAgencyCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Agency");
                columnToAdd.setPropertyName("agency");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Merchant Name");
                columnToAdd.setPropertyName("merchantName");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Trip Invoice #");
                columnToAdd.setPropertyName("tripInvoiceNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Traveler Name");
                columnToAdd.setPropertyName("travelerName");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Traveler ID");
                columnToAdd.setPropertyName("travelerId");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Expense Amount");
                columnToAdd.setPropertyName("tripExpenseAmount");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Arranger Name");
                columnToAdd.setPropertyName("tripArrangerName");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Departure Date");
                columnToAdd.setPropertyName("tripDepartureDate");
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Air Book Date");
                columnToAdd.setPropertyName("airBookDate");
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Air Carrier Code");
                columnToAdd.setPropertyName("airCarrierCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Air Ticket Number");
                columnToAdd.setPropertyName("airTicketNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("PNR Number");
                columnToAdd.setPropertyName("pnrNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Unique Id");
                columnToAdd.setPropertyName("transactionUniqueId");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Posting Date");
                columnToAdd.setPropertyName("transactionPostingDate");
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                cachedColumns = Collections.unmodifiableList(cachedColumns);
            }
        }
        return cachedColumns;
    }

    @Override
    public void persistAgencyEntryGroupsForDocumentSave(TemCorrectionProcessDocument document, TemCorrectionForm correctionForm) {
        FileWriter out;
        try {
            out = new FileWriter(this.getBatchFileDirectoryName() + File.separator + correctionForm.getInputGroupId());
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(out);
            writer.writeStartDocument();
            writer.writeStartElement("agencyData");

            for (AgencyEntryFull agency: correctionForm.getAllEntries()) {
                writer.writeStartElement("record");
                writer.writeStartElement("creditCardOrAgencyCode"); writer.writeCharacters(agency.getCreditCardOrAgencyCode()); writer.writeEndElement();
                writer.writeStartElement("agency"); writer.writeCharacters(agency.getAgency()); writer.writeEndElement();
                writer.writeStartElement("agencyFileName"); writer.writeCharacters(agency.getAgencyFileName()); writer.writeEndElement();
                writer.writeStartElement("merchantName"); writer.writeCharacters(agency.getMerchantName()); writer.writeEndElement();
                writer.writeStartElement("tripInvoiceNumber"); writer.writeCharacters(agency.getTripInvoiceNumber()); writer.writeEndElement();
                writer.writeStartElement("travelerName"); writer.writeCharacters(agency.getTravelerName()); writer.writeEndElement();
                writer.writeStartElement("travelerId"); writer.writeCharacters(agency.getTravelerId()); writer.writeEndElement();
                writer.writeStartElement("tripExpenseAmount"); writer.writeCharacters(agency.getTripExpenseAmount().toString()); writer.writeEndElement();
                writer.writeStartElement("tripArrangerName"); writer.writeCharacters(agency.getTripArrangerName()); writer.writeEndElement();
                writer.writeStartElement("tripDepartureDate"); writer.writeCharacters(agency.getTripDepartureDate().toString()); writer.writeEndElement();
                writer.writeStartElement("airBookDate"); writer.writeCharacters(agency.getAirBookDate().toString()); writer.writeEndElement();
                writer.writeStartElement("airCarrierCode"); writer.writeCharacters(agency.getAirCarrierCode()); writer.writeEndElement();
                writer.writeStartElement("airTicketNumber"); writer.writeCharacters(agency.getAirTicketNumber()); writer.writeEndElement();
                writer.writeStartElement("pnrNumber"); writer.writeCharacters(agency.getPnrNumber()); writer.writeEndElement();
                writer.writeStartElement("transactionUniqueId"); writer.writeCharacters(agency.getTransactionUniqueId()); writer.writeEndElement();
                writer.writeStartElement("transactionPostingDate"); writer.writeCharacters(agency.getTransactionPostingDate().toString()); writer.writeEndElement();
                writer.writeEndElement(); // end the record element
            }


            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
            out.close();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Could not write XML for agency groups", ioe);
        }
        catch (XMLStreamException xmlse) {
            throw new RuntimeException("Could not write XML for agency groups", xmlse);
        }

    }

    @Override
    public String getBatchFileDirectoryName() {
        return this.batchFileDirectoryName;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
