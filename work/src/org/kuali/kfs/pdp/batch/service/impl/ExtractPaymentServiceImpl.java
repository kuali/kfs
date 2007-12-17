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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Bank;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentNoteText;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.PaymentStatus;
import org.kuali.module.pdp.dao.PaymentGroupHistoryDao;
import org.kuali.module.pdp.dao.ProcessDao;
import org.kuali.module.pdp.service.ExtractPaymentService;
import org.kuali.module.pdp.service.PaymentDetailService;
import org.kuali.module.pdp.service.PaymentGroupService;
import org.kuali.module.pdp.service.ReferenceService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ExtractPaymentServiceImpl implements ExtractPaymentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractPaymentServiceImpl.class);

    public ReferenceService referenceService;
    public DateTimeService dateTimeService;
    public ParameterService parameterService;
    public PaymentGroupService paymentGroupService;
    public PaymentDetailService paymentDetailService;
    public PaymentGroupHistoryDao paymentGroupHistoryDao;
    public String directoryName;
    public ProcessDao processDao;

    // Set this to true to run this process without updating the database. This
    // should stay false for production.
    public static boolean testMode = false;

    private String getOutputFile(String fileprefix, Date runDate) {
        String filename = directoryName + fileprefix + "_";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        filename = filename + sdf.format(runDate);
        filename = filename + ".xml";

        return filename;
    }

    /**
     * @see org.kuali.module.pdp.service.ExtractPaymentService#extractCancelledChecks()
     */
    public void extractCanceledChecks() {
        LOG.debug("extractCancelledChecks() started");

        Date processDate = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String filename = getOutputFile("pdp_cancel", processDate);
        LOG.debug("extractCanceledChecks() filename = " + filename);

        // Open file
        BufferedWriter os = null;

        try {
            os = new BufferedWriter(new FileWriter(filename));
            os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writeOpenTag(os, 0, "canceledChecks");

            Iterator paymentIterator = paymentGroupHistoryDao.getCanceledChecks();
            while (paymentIterator.hasNext()) {
                PaymentGroupHistory history = (PaymentGroupHistory) paymentIterator.next();

                writeOpenTag(os, 2, "check");

                writeBank(os, 4, history.getPaymentGroup().getBank());
                writePayee(os, 4, history.getPaymentGroup());

                writeTag(os, 4, "netAmount", history.getPaymentGroup().getNetPaymentAmount().toString());
                if ( history.getOrigDisburseNbr() != null ) {
                    writeTag(os, 4, "disbursementNumber", history.getOrigDisburseNbr().toString());                    
                } else {
                    writeTag(os, 4, "disbursementNumber", history.getPaymentGroup().getDisbursementNbr().toString());
                }
                if(history.getPaymentGroup().getDisbursementType() != null) {
                    writeTag(os, 4, "disbursementType", history.getPaymentGroup().getDisbursementType().getCode());
                } else {
                    writeTag(os, 4, "disbursementType", history.getDisbursementType().getCode());
                }

                writeCloseTag(os, 2, "check");

                if (!testMode) {
                    history.setLastUpdate(new Timestamp(processDate.getTime()));
                    history.setPmtCancelExtractDate(new Timestamp(processDate.getTime()));
                    history.setPmtCancelExtractStat(Boolean.TRUE);
                    paymentGroupHistoryDao.save(history);
                }
            }

            writeCloseTag(os, 0, "canceledChecks");
        }
        catch (IOException ie) {
            LOG.error("extractCanceledChecks() Problem reading file:  " + filename, ie);
            throw new IllegalArgumentException("Error writing to output file: " + ie.getMessage());
        }
        finally {
            // Close file
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ie) {
                    // Not much we can do now
                }
            }
        }
    }

    /**
     * @see org.kuali.module.pdp.service.ExtractPaymentService#extractAchPayments()
     */
    public void extractAchPayments() {
        LOG.debug("extractAchPayments() started");

        Date processDate = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        PaymentStatus extractedStatus = (PaymentStatus) referenceService.getCode("PaymentStatus", PdpConstants.PaymentStatusCodes.EXTRACTED);

        String filename = getOutputFile("pdp_ach", processDate);
        LOG.debug("extractAchPayments() filename = " + filename);

        // Open file
        BufferedWriter os = null;

        try {
            os = new BufferedWriter(new FileWriter(filename));
            os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writeOpenTag(os, 0, "achPayments");

            Iterator iter = paymentGroupService.getByDisbursementTypeStatusCode(PdpConstants.DisbursementTypeCodes.ACH, PdpConstants.PaymentStatusCodes.PENDING_ACH);
            while (iter.hasNext()) {
                PaymentGroup pg = (PaymentGroup) iter.next();
                if (!testMode) {
                    pg.setDisbursementDate(new Timestamp(processDate.getTime()));
                    pg.setLastUpdate(new Timestamp(processDate.getTime()));
                    pg.setPaymentStatus(extractedStatus);
                    paymentGroupService.save(pg);
                }

                writeOpenTagAttribute(os, 2, "ach", "disbursementNbr", pg.getDisbursementNbr().toString());
                PaymentProcess paymentProcess = pg.getProcess();
                writeTag(os, 4, "processCampus", paymentProcess.getCampus());
                writeTag(os, 4, "processId", paymentProcess.getId().toString());

                writeBank(os, 4, pg.getBank());

                writeTag(os, 4, "disbursementDate", sdf.format(processDate));
                writeTag(os, 4, "netAmount", pg.getNetPaymentAmount().toString());

                writePayeeAch(os, 4, pg);
                writeTag(os, 4, "customerUnivNbr", pg.getCustomerInstitutionNumber());
                writeTag(os, 4, "paymentDate", sdf.format(pg.getPaymentDate()));

                // Write customer profile information
                CustomerProfile cp = pg.getBatch().getCustomerProfile();
                writeCustomerProfile(os, 4, cp);

                // Write all payment level information
                writeOpenTag(os, 4, "payments");
                List pdList = pg.getPaymentDetails();
                for (Iterator iterator = pdList.iterator(); iterator.hasNext();) {
                    PaymentDetail pd = (PaymentDetail) iterator.next();
                    writeOpenTag(os, 6, "payment");

                    // Write detail info
                    writeTag(os, 6, "purchaseOrderNbr", pd.getPurchaseOrderNbr());
                    writeTag(os, 6, "invoiceNbr", pd.getInvoiceNbr());
                    writeTag(os, 6, "requisitionNbr", pd.getRequisitionNbr());
                    writeTag(os, 6, "custPaymentDocNbr", pd.getCustPaymentDocNbr());
                    writeTag(os, 6, "invoiceDate", sdf.format(pd.getInvoiceDate()));

                    writeTag(os, 6, "origInvoiceAmount", pd.getOrigInvoiceAmount().toString());
                    writeTag(os, 6, "netPaymentAmount", pd.getNetPaymentAmount().toString());
                    writeTag(os, 6, "invTotDiscountAmount", pd.getInvTotDiscountAmount().toString());
                    writeTag(os, 6, "invTotShipAmount", pd.getInvTotShipAmount().toString());
                    writeTag(os, 6, "invTotOtherDebitAmount", pd.getInvTotOtherDebitAmount().toString());
                    writeTag(os, 6, "invTotOtherCreditAmount", pd.getInvTotOtherCreditAmount().toString());

                    writeOpenTag(os, 6, "notes");
                    for (Iterator i = pd.getNotes().iterator(); i.hasNext();) {
                        PaymentNoteText note = (PaymentNoteText) i.next();
                        writeTag(os, 8, "note", escapeString(note.getCustomerNoteText()));
                    }
                    writeCloseTag(os, 6, "notes");

                    writeCloseTag(os, 4, "payment");
                }
                writeCloseTag(os, 4, "payments");
                writeCloseTag(os, 2, "ach");
            }
            writeCloseTag(os, 0, "achPayments");
        }
        catch (IOException ie) {
            LOG.error("extractAchPayments() Problem reading file:  " + filename, ie);
            throw new IllegalArgumentException("Error writing to output file: " + ie.getMessage());
        }
        finally {
            // Close file
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ie) {
                    // Not much we can do now
                }
            }
        }
    }

    /**
     * @see org.kuali.module.pdp.service.ExtractPaymentService#extractChecks()
     */
    public void extractChecks() {
        LOG.debug("extractChecks() started");

        Date processDate = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Get the process ID

        if ( ! parameterService.parameterExists(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.EXTRACT_PROCESS_ID) ) {
            throw new RuntimeException("This job should only be triggered by the format process.  It should not be run manually");
        }
        String pids = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.EXTRACT_PROCESS_ID);
        pids = pids.trim();

        Integer processId = null;
        try {
            processId = Integer.parseInt(pids);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Unable to convert the process ID to a number");
        }

        PaymentProcess p = processDao.get(processId);
        if (p == null) {
            throw new IllegalArgumentException("Invalid process ID");
        }

        String filename = getOutputFile("pdp_check", processDate);
        LOG.debug("extractChecks() filename: " + filename);

        // Open file
        BufferedWriter os = null;

        try {
            os = new BufferedWriter(new FileWriter(filename));
            os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writeOpenTagAttribute(os, 0, "checks", "processId", processId.toString(), "campus", p.getCampus());

            List<Integer> disbNbrs = paymentGroupService.getDisbursementNumbersByDisbursementType(processId, PdpConstants.DisbursementTypeCodes.CHECK);
            for (Iterator iter = disbNbrs.iterator(); iter.hasNext();) {
                Integer disbursementNbr = (Integer)iter.next();

                boolean first = true;

                Iterator i = paymentDetailService.getByDisbursementNumber(disbursementNbr);
                while ( i.hasNext() ) {
                    PaymentDetail pd = (PaymentDetail)i.next();
                    PaymentGroup pg = pd.getPaymentGroup();
                    if (!testMode) {
                        if ( pg.getDisbursementDate() == null ) {
                            pg.setDisbursementDate(new Timestamp(processDate.getTime()));
                            pg.setLastUpdate(new Timestamp(processDate.getTime()));
                            paymentGroupService.save(pg);
                        }
                    }

                    if ( first ) {
                        writeOpenTagAttribute(os, 2, "check", "disbursementNbr", pg.getDisbursementNbr().toString());

                        // Write check level information

                        writeBank(os, 4, pg.getBank());

                        writeTag(os, 4, "disbursementDate", sdf.format(processDate));
                        writeTag(os, 4, "netAmount", pg.getNetPaymentAmount().toString());

                        writePayee(os, 4, pg);
                        writeTag(os, 4, "campusAddressIndicator", pg.getCampusAddress().booleanValue() ? "Y" : "N");
                        writeTag(os, 4, "attachmentIndicator", pg.getPymtAttachment().booleanValue() ? "Y" : "N");
                        writeTag(os, 4, "specialHandlingIndicator", pg.getPymtSpecialHandling().booleanValue() ? "Y" : "N");
                        writeTag(os, 4, "immediatePaymentIndicator", pg.getProcessImmediate().booleanValue() ? "Y" : "N");
                        writeTag(os, 4, "customerUnivNbr", pg.getCustomerInstitutionNumber());
                        writeTag(os, 4, "paymentDate", sdf.format(pg.getPaymentDate()));

                        // Write customer profile information
                        CustomerProfile cp = pg.getBatch().getCustomerProfile();
                        writeCustomerProfile(os, 4, cp);

                        writeOpenTag(os, 4, "payments");

                    }

                    writeOpenTag(os, 6, "payment");

                    writeTag(os, 8, "purchaseOrderNbr", pd.getPurchaseOrderNbr());
                    writeTag(os, 8, "invoiceNbr", pd.getInvoiceNbr());
                    writeTag(os, 8, "requisitionNbr", pd.getRequisitionNbr());
                    writeTag(os, 8, "custPaymentDocNbr", pd.getCustPaymentDocNbr());
                    writeTag(os, 8, "invoiceDate", sdf.format(pd.getInvoiceDate()));

                    writeTag(os, 8, "origInvoiceAmount", pd.getOrigInvoiceAmount().toString());
                    writeTag(os, 8, "netPaymentAmount", pd.getNetPaymentAmount().toString());
                    writeTag(os, 8, "invTotDiscountAmount", pd.getInvTotDiscountAmount().toString());
                    writeTag(os, 8, "invTotShipAmount", pd.getInvTotShipAmount().toString());
                    writeTag(os, 8, "invTotOtherDebitAmount", pd.getInvTotOtherDebitAmount().toString());
                    writeTag(os, 8, "invTotOtherCreditAmount", pd.getInvTotOtherCreditAmount().toString());

                    writeOpenTag(os, 8, "notes");
                    for (Iterator ix = pd.getNotes().iterator(); ix.hasNext();) {
                        PaymentNoteText note = (PaymentNoteText) ix.next();
                        writeTag(os, 10, "note", note.getCustomerNoteText());
                    }
                    writeCloseTag(os, 8, "notes");

                    writeCloseTag(os, 6, "payment");

                    first = false;
                }
                writeCloseTag(os, 4, "payments");
                writeCloseTag(os, 2, "check");
            }
            writeCloseTag(os, 0, "checks");
        }
        catch (IOException ie) {
            LOG.error("extractChecks() Problem reading file:  " + filename, ie);
            throw new IllegalArgumentException("Error writing to output file: " + ie.getMessage());
        }
        finally {
            // Close file
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ie) {
                    // Not much we can do now
                }
            }
        }
    }

    private static String SPACES = "                                                       ";

    private void writeTag(BufferedWriter os, int indent, String tag, String data) throws IOException {
        if (data != null) {
            os.write(SPACES.substring(0, indent));
            os.write("<" + tag + ">" + escapeString(data) + "</" + tag + ">\n");
        }
    }

    private void writeOpenTag(BufferedWriter os, int indent, String tag) throws IOException {
        os.write(SPACES.substring(0, indent));
        os.write("<" + tag + ">\n");
    }

    private void writeOpenTagAttribute(BufferedWriter os, int indent, String tag, String attr, String attrVal) throws IOException {
        os.write(SPACES.substring(0, indent));
        os.write("<" + tag + " " + attr + "=\"" + escapeString(attrVal) + "\">\n");
    }

    private void writeOpenTagAttribute(BufferedWriter os, int indent, String tag, String attr1, String attr1Val, String attr2, String attr2Val) throws IOException {
        os.write(SPACES.substring(0, indent));
        os.write("<" + tag + " " + attr1 + "=\"" + escapeString(attr1Val) + "\" " + attr2 + "=\"" + escapeString(attr2Val) + "\">\n");
    }

    private void writeCloseTag(BufferedWriter os, int indent, String tag) throws IOException {
        os.write(SPACES.substring(0, indent));
        os.write("</" + tag + ">\n");
    }

    private void writeBank(BufferedWriter os, int indent, Bank b) throws IOException {
        if ( b != null ) {
            writeOpenTagAttribute(os, indent, "bank", "id", b.getId().toString());
            writeTag(os, indent + 2, "accountNumber", b.getAccountNumber());
            writeTag(os, indent + 2, "routingNumber", b.getRoutingNumber());
            writeCloseTag(os, indent, "bank");
        }
    }

    private void writeCustomerProfile(BufferedWriter os, int indent, CustomerProfile cp) throws IOException {
        writeOpenTag(os, indent, "customerProfile");
        writeTag(os, indent + 2, "chartCode", cp.getChartCode());
        writeTag(os, indent + 2, "orgCode", cp.getOrgCode());
        writeTag(os, indent + 2, "subUnitCode", cp.getSubUnitCode());
        writeOpenTag(os, indent + 2, "checkHeaderNoteLines");
        writeTag(os, indent + 4, "note", cp.getCheckHeaderNoteTextLine1());
        writeTag(os, indent + 4, "note", cp.getCheckHeaderNoteTextLine2());
        writeTag(os, indent + 4, "note", cp.getCheckHeaderNoteTextLine3());
        writeTag(os, indent + 4, "note", cp.getCheckHeaderNoteTextLine4());
        writeCloseTag(os, indent + 2, "checkHeaderNoteLines");
        writeOpenTag(os, indent + 2, "additionalCheckNoteLines");
        writeTag(os, indent + 4, "note", cp.getAdditionalCheckNoteTextLine1());
        writeTag(os, indent + 4, "note", cp.getAdditionalCheckNoteTextLine2());
        writeTag(os, indent + 4, "note", cp.getAdditionalCheckNoteTextLine3());
        writeTag(os, indent + 4, "note", cp.getAdditionalCheckNoteTextLine4());
        writeCloseTag(os, indent + 2, "additionalCheckNoteLines");
        writeCloseTag(os, indent, "customerProfile");
    }

    private void writePayeeAch(BufferedWriter os, int indent, PaymentGroup pg) throws IOException {
        writePayeeInformation(os, indent, pg, true);
    }

    private void writePayee(BufferedWriter os, int indent, PaymentGroup pg) throws IOException {
        writePayeeInformation(os, indent, pg, false);
    }

    private void writePayeeInformation(BufferedWriter os, int indent, PaymentGroup pg, boolean includeAch) throws IOException {
        os.write(SPACES.substring(0, indent));
        os.write("<payee id=\"" + pg.getPayeeId() + "\" type=\"" + pg.getPayeeIdTypeCd() + "\">\n");
        writeTag(os, indent + 2, "payeeName", pg.getPayeeName());
        writeTag(os, indent + 2, "line1Address", pg.getLine1Address());
        writeTag(os, indent + 2, "line2Address", pg.getLine2Address());
        writeTag(os, indent + 2, "line3Address", pg.getLine3Address());
        writeTag(os, indent + 2, "line4Address", pg.getLine4Address());
        writeTag(os, indent + 2, "city", pg.getCity());
        writeTag(os, indent + 2, "state", pg.getState());
        writeTag(os, indent + 2, "zipCd", pg.getZipCd());
        writeTag(os, indent + 2, "country", pg.getCountry());

        if (includeAch) {
            writeTag(os, indent + 2, "achBankRoutingNbr", pg.getAchBankRoutingNbr());
            writeTag(os, indent + 2, "achBankAccountNbr", pg.getAchAccountNumber().getAchBankAccountNbr());
            writeTag(os, indent + 2, "achAccountType", pg.getAchAccountType());
        }
        writeCloseTag(os, indent, "payee");
    }

    private String escapeString(String input) {
        String output = input.replaceAll("\\&", "&amp;");
        output = output.replaceAll("\"", "&quot;");
        output = output.replaceAll("\\'", "&apos;");
        output = output.replaceAll("\\<", "&lt;");
        output = output.replaceAll("\\>", "&gt;");
        return output;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDirectoryName(String dn) {
        directoryName = dn;
    }

    public void setPaymentGroupService(PaymentGroupService pgs) {
        paymentGroupService = pgs;
    }

    public void setProcessDao(ProcessDao pd) {
        processDao = pd;
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setReferenceService(ReferenceService rs) {
        referenceService = rs;
    }

    public void setPaymentGroupHistoryDao(PaymentGroupHistoryDao p) {
        paymentGroupHistoryDao = p;
    }

    public void setPaymentDetailService(PaymentDetailService pds) {
        paymentDetailService = pds;
    }
}
