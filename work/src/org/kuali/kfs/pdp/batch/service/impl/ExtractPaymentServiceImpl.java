/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.service.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Bank;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentNoteText;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.dao.ProcessDao;
import org.kuali.module.pdp.service.ExtractPaymentService;
import org.kuali.module.pdp.service.PaymentGroupService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ExtractPaymentServiceImpl implements ExtractPaymentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractPaymentServiceImpl.class);

    public KualiConfigurationService kualiConfigurationService;
    public PaymentGroupService paymentGroupService;
    public String directoryName;
    public ProcessDao processDao;

    /**
     * @see org.kuali.module.pdp.service.ExtractPaymentService#extractAchPayments()
     */
    public void extractAchPayments() {
        LOG.debug("extractAchPayments() started");

        // Get the process ID

        // TODO This is broken
        String pids = kualiConfigurationService.getParameterValue(KFSConstants.PDP_NAMESPACE, "",PdpConstants.ApplicationParameterKeys.EXTRACT_PROCESS_ID);
        pids = pids.trim();

        Integer processId = null;
        try {
            processId = Integer.parseInt(pids);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Unable to convert the process ID to a number");
        }

        // TODO This is broken
        String filename = null;
        // directoryName + kualiConfigurationService.getApplicationParameterValue(PdpConstants.PDP_APPLICATION, PdpConstants.ApplicationParameterKeys.ACH_EXTRACT_FILE);

        // Open file
        BufferedWriter outputStream = null;

        try {
            outputStream = new BufferedWriter(new FileWriter(filename));

            // Get all of the ach payments
            //    Get more info about it
            //    Write all the info into the file
            //    Update the extract timestamp/indicator
            // Send email
        } catch (IOException ie) {
            LOG.error("extractAchPayments() Problem reading file:  "+ filename,ie);
            throw new IllegalArgumentException("Error writing to output file: " + ie.getMessage());
        } finally {
            // Close file
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ie) {
                    // Not much we can do now
                }
            }
        }
    }

    /**
     * @see org.kuali.module.pdp.service.ExtractPaymentService#extractCancelledChecks()
     */
    public void extractCancelledChecks() {
        LOG.debug("extractCancelledChecks() started");

        // Open file
        // Get all of the cancelled checks from group history
        //    Get more info about the check
        //    Write all the info into the file
        //    Update the extract timestamp/indicator
        // Close file
        // Send email
    }

    /**
     * @see org.kuali.module.pdp.service.ExtractPaymentService#extractChecks()
     */
    public void extractChecks() {
        LOG.debug("extractChecks() started");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Get the process ID
        
        // TODO This is broken
        String pids = null; 
            // kualiConfigurationService.getApplicationParameterValue(PdpConstants.PDP_APPLICATION, PdpConstants.ApplicationParameterKeys.EXTRACT_PROCESS_ID);
        pids = pids.trim();

        Integer processId = null;
        try {
            processId = Integer.parseInt(pids);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Unable to convert the process ID to a number");
        }

        PaymentProcess p = processDao.get(processId);
        if ( p != null ) {
            throw new IllegalArgumentException("Invalid process ID");
        }

        // TODO This is broken
        String filename = null; 
            // directoryName + kualiConfigurationService.getApplicationParameterValue(PdpConstants.PDP_APPLICATION, PdpConstants.ApplicationParameterKeys.CHECK_EXTRACT_FILE);

        LOG.debug("extractChecks() filename: " + filename);

        // Open file
        BufferedWriter os = null;

        try {
            os = new BufferedWriter(new FileWriter(filename));
            os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writeOpenTagAttribute(os, 0, "checks", "processId", processId.toString(),"campus",p.getCampus());

            Iterator iter = paymentGroupService.getByProcessIdDisbursementType(processId, PdpConstants.DisbursementTypeCodes.CHECK);
            while (iter.hasNext()) {
                PaymentGroup pg = (PaymentGroup)iter.next();
                writeOpenTagAttribute(os, 2, "check", "disbursementNbr", pg.getDisbursementNbr().toString());

                // Write check level information

                writeBank(os,4,pg.getBank());

                writeTag(os, 4, "disbursementDate", pg.getDisbursementDate().toString());
                writeTag(os, 4, "netAmount", pg.getNetPaymentAmount().toString());

                writePayee(os,4,pg);
                writeTag(os, 4,"campusAddressIndicator",pg.getCampusAddress().booleanValue() ? "Y" : "N");
                writeTag(os, 4,"attachmentIndicator",pg.getPymtAttachment().booleanValue() ? "Y" : "N");
                writeTag(os, 4,"specialHandlingIndicator",pg.getPymtSpecialHandling().booleanValue() ? "Y" : "N");
                writeTag(os, 4,"immediatePaymentIndicator",pg.getProcessImmediate().booleanValue() ? "Y" : "N");
                writeTag(os, 4,"customerUnivNbr",pg.getCustomerIuNbr());
                writeTag(os, 4,"paymentDate",sdf.format(pg.getPaymentDate()));

                // Write customer profile information
                CustomerProfile cp = pg.getBatch().getCustomerProfile();
                writeCustomerProfile(os,4,cp);

                // Write all payment level information
                writeOpenTag(os,4, "payments");
                List pdList = pg.getPaymentDetails();
                for (Iterator iterator = pdList.iterator(); iterator.hasNext();) {
                    PaymentDetail pd = (PaymentDetail)iterator.next();
                    writeOpenTag(os, 4, "payment");

                    // Write detail info
                    writeTag(os,6,"purchaseOrderNbr",pd.getPurchaseOrderNbr());
                    writeTag(os,6,"invoiceNbr",pd.getInvoiceNbr());
                    writeTag(os,6,"requisitionNbr",pd.getRequisitionNbr());
                    writeTag(os,6,"custPaymentDocNbr",pd.getCustPaymentDocNbr());
                    writeTag(os,6,"invoiceDate",sdf.format(pd.getInvoiceDate()));

                    writeTag(os,6,"origInvoiceAmount",pd.getOrigInvoiceAmount().toString());
                    writeTag(os,6,"netPaymentAmount",pd.getNetPaymentAmount().toString());
                    writeTag(os,6,"invTotDiscountAmount",pd.getInvTotDiscountAmount().toString());
                    writeTag(os,6,"invTotShipAmount",pd.getInvTotShipAmount().toString());
                    writeTag(os,6,"invTotOtherDebitAmount",pd.getInvTotOtherDebitAmount().toString());
                    writeTag(os,6,"invTotOtherCreditAmount",pd.getInvTotOtherCreditAmount().toString());

                    writeOpenTag(os, 6, "notes");
                    for (Iterator i = pd.getNotes().iterator(); i.hasNext();) {
                        PaymentNoteText note = (PaymentNoteText)iterator.next();
                        writeTag(os,8,"note",note.getCustomerNoteText());
                    }
                    writeCloseTag(os, 6, "notes");

                    writeCloseTag(os,4,"payment");
                }
                writeCloseTag(os,4,"payments");
                writeCloseTag(os,2,"check");
            }
            writeCloseTag(os,0,"checks");

            // Send email
        } catch (IOException ie) {
            LOG.error("extractChecks() Problem reading file:  "+ filename,ie);
            throw new IllegalArgumentException("Error writing to output file: " + ie.getMessage());
        } finally {
            // Close file
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ie) {
                    // Not much we can do now
                }
            }
        }
    }

    private static String SPACES = "                                                       ";

    private void writeTag(BufferedWriter os,int indent,String tag,String data) throws IOException {
        os.write(SPACES.substring(indent));
        os.write("<" + tag + ">" + data + "</" + tag + ">\n");
    }

    private void writeOpenTag(BufferedWriter os,int indent,String tag) throws IOException {
        os.write(SPACES.substring(indent));
        os.write("<" + tag + ">\n");
    }

    private void writeOpenTagAttribute(BufferedWriter os,int indent,String tag,String attr,String attrVal) throws IOException {
        os.write(SPACES.substring(indent));
        os.write("<" + tag + " " + attr + "=\"" + attrVal + "\">\n");
    }

    private void writeOpenTagAttribute(BufferedWriter os,int indent,String tag,String attr1,String attr1Val,String attr2,String attr2Val) throws IOException {
        os.write(SPACES.substring(indent));
        os.write("<" + tag + " " + attr1 + "=\"" + attr1Val + "\" " + attr2 + "=\"" + attr2Val + "\">\n");
    }

    private void writeCloseTag(BufferedWriter os,int indent,String tag) throws IOException {
        os.write(SPACES.substring(indent));
        os.write("</" + tag + ">\n");
    }

    private void writeBank(BufferedWriter os,int indent,Bank b) throws IOException {
        writeOpenTagAttribute(os,indent,"bank","id",b.getId().toString());
        writeTag(os,indent + 2,"accountNumber",b.getAccountNumber());
        writeTag(os,indent + 2,"routingNumber",b.getRoutingNumber());
        writeCloseTag(os,indent,"bank");
    }

    private void writeCustomerProfile(BufferedWriter os,int indent,CustomerProfile cp) throws IOException {
        writeOpenTag(os, indent, "customerProfile");
        writeTag(os,indent + 2,"chartCode",cp.getChartCode());
        writeTag(os,indent + 2,"orgCode",cp.getOrgCode());
        writeTag(os,indent + 2,"subUnitCode",cp.getSubUnitCode());
        writeOpenTag(os, indent + 2, "checkHeaderNoteLines");
        if ( cp.getAdditionalCheckNoteTextLine1() != null ) {
            writeTag(os,indent + 4,"note",cp.getAdditionalCheckNoteTextLine1());
        }
        if ( cp.getAdditionalCheckNoteTextLine2() != null ) {
            writeTag(os,indent + 4,"note",cp.getAdditionalCheckNoteTextLine2());
        }
        if ( cp.getAdditionalCheckNoteTextLine3() != null ) {
            writeTag(os,indent + 4,"note",cp.getAdditionalCheckNoteTextLine3());
        }
        if ( cp.getAdditionalCheckNoteTextLine4() != null ) {
            writeTag(os,indent + 4,"note",cp.getAdditionalCheckNoteTextLine4());
        }
        writeCloseTag(os, indent + 2, "checkHeaderNoteLines");
        writeOpenTag(os, indent + 2, "additionalCheckNoteLines");
        if ( cp.getAdditionalCheckNoteTextLine1() != null ) {
            writeTag(os,indent + 4,"note",cp.getAdditionalCheckNoteTextLine1());
        }
        if ( cp.getAdditionalCheckNoteTextLine2() != null ) {
            writeTag(os,indent + 4,"note",cp.getAdditionalCheckNoteTextLine2());
        }
        if ( cp.getAdditionalCheckNoteTextLine3() != null ) {
            writeTag(os,indent + 4,"note",cp.getAdditionalCheckNoteTextLine3());
        }
        if ( cp.getAdditionalCheckNoteTextLine4() != null ) {
            writeTag(os,indent + 4,"note",cp.getAdditionalCheckNoteTextLine4());
        }
        writeCloseTag(os, indent + 2, "additionalCheckNoteLines");        
        writeCloseTag(os, indent, "customerProfile");
    }

    private void writePayee(BufferedWriter os,int indent,PaymentGroup pg) throws IOException {
        os.write(SPACES.substring(indent));
        os.write("<payee id=\"" + pg.getPayeeId() + "\" type=\"" + pg.getPayeeIdTypeCd() + "\">\n");
        writeTag(os,indent + 2,"payeeName",pg.getPayeeName());
        writeTag(os,indent + 2,"line1Address",pg.getLine1Address());
        writeTag(os,indent + 2,"line2Address",pg.getLine2Address());
        writeTag(os,indent + 2,"line3Address",pg.getLine3Address());
        writeTag(os,indent + 2,"line4Address",pg.getLine4Address());
        writeTag(os,indent + 2,"city",pg.getCity());
        writeTag(os,indent + 2,"state",pg.getState());
        writeTag(os,indent + 2,"zipCd",pg.getZipCd());
        writeTag(os,indent + 2,"country",pg.getCountry());
        writeCloseTag(os,indent,"payee");
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setDirectoryName(String dn) {
        directoryName = dn;
    }

    public void setPaymentGroupService(PaymentGroupService pgs) {
        paymentGroupService = pgs;
    }
}
