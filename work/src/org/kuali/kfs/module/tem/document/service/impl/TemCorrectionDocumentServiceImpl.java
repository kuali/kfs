/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AgencyEntryFull;
import org.kuali.kfs.module.tem.document.TemCorrectionProcessDocument;
import org.kuali.kfs.module.tem.document.service.TemCorrectionDocumentService;
import org.kuali.kfs.module.tem.document.web.struts.TemCorrectionForm;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.web.comparator.NumericValueComparator;
import org.kuali.rice.kns.web.comparator.StringValueComparator;
import org.kuali.rice.kns.web.comparator.TemporalValueComparator;
import org.kuali.rice.kns.web.ui.Column;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

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
        Document xml = DocumentHelper.createDocument();
        
        Element root = xml.addElement( "agencyData" );
        
        for (AgencyEntryFull agency: correctionForm.getAllEntries()) {
            Element agencyXml = root.addElement("record");
            agencyXml.addElement("creditCardOrAgencyCode").addText(agency.getCreditCardOrAgencyCode());
            agencyXml.addElement("agency").addText(agency.getAgency());
            agencyXml.addElement("agencyFileName").addText(agency.getAgencyFileName());
            agencyXml.addElement("merchantName").addText(agency.getMerchantName());
            agencyXml.addElement("tripInvoiceNumber").addText(agency.getTripInvoiceNumber());
            agencyXml.addElement("travelerName").addText(agency.getTravelerName());
            agencyXml.addElement("travelerId").addText(agency.getTravelerId());
            agencyXml.addElement("tripExpenseAmount").addText(agency.getTripExpenseAmount().toString());
            agencyXml.addElement("tripArrangerName").addText(agency.getTripArrangerName());
            agencyXml.addElement("tripDepartureDate").addText(agency.getTripDepartureDate().toString());
            agencyXml.addElement("airBookDate").addText(agency.getAirBookDate().toString());
            agencyXml.addElement("airCarrierCode").addText(agency.getAirCarrierCode());
            agencyXml.addElement("airTicketNumber").addText(agency.getAirTicketNumber());
            agencyXml.addElement("pnrNumber").addText(agency.getPnrNumber());
            agencyXml.addElement("transactionUniqueId").addText(agency.getTransactionUniqueId());
            agencyXml.addElement("transactionPostingDate").addText(agency.getTransactionPostingDate().toString());
        }

        FileWriter out;
        try {
            out = new FileWriter(this.getBatchFileDirectoryName() + File.separator + correctionForm.getInputGroupId());
            xml.write(out);
        }
        catch (IOException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
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
