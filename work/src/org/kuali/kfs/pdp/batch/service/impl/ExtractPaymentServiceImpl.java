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
package org.kuali.kfs.pdp.batch.service.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.batch.service.ExtractPaymentService;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupHistoryDao;
import org.kuali.kfs.pdp.dataaccess.ProcessDao;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ExtractPaymentServiceImpl implements ExtractPaymentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractPaymentServiceImpl.class);

    private String directoryName;

    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    private PaymentGroupService paymentGroupService;
    private PaymentDetailService paymentDetailService;
    private PaymentGroupHistoryDao paymentGroupHistoryDao;
    private ProcessDao processDao;
    private PdpEmailService paymentFileEmailService;
    private KualiCodeService kualiCodeService;
    private BusinessObjectService businessObjectService;
    private KualiConfigurationService kualiConfigurationService;

    // Set this to true to run this process without updating the database. This
    // should stay false for production.
    public static boolean testMode = false;

    private String getOutputFile(String fileprefix, Date runDate) {
        String filename = directoryName + "/" + fileprefix + "_";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        filename = filename + sdf.format(runDate);
        filename = filename + ".xml";

        return filename;
    }

    /**
     * @see org.kuali.kfs.pdp.batch.service.ExtractPaymentService#extractCancelledChecks()
     */
    public void extractCanceledChecks() {
        LOG.debug("extractCancelledChecks() started");

        Date processDate = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String checkCancelledFilePrefix = this.kualiConfigurationService.getPropertyString(PdpKeyConstants.ExtractPayment.CHECK_FILENAME);
        checkCancelledFilePrefix = MessageFormat.format(checkCancelledFilePrefix, new Object[]{ null });
        
        String filename = getOutputFile(checkCancelledFilePrefix, processDate);
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
                if (ObjectUtils.isNotNull(history.getOrigDisburseNbr()) ) {
                    writeTag(os, 4, "disbursementNumber", history.getOrigDisburseNbr().toString());
                }
                else {
                    writeTag(os, 4, "disbursementNumber", history.getPaymentGroup().getDisbursementNbr().toString());
                }
                if (ObjectUtils.isNotNull(history.getPaymentGroup().getDisbursementType()) ) {
                    writeTag(os, 4, "disbursementType", history.getPaymentGroup().getDisbursementType().getCode());
                }
                else {
                    writeTag(os, 4, "disbursementType", history.getDisbursementType().getCode());
                }

                writeCloseTag(os, 2, "check");

                if (!testMode) {
                    history.setLastUpdate(new Timestamp(processDate.getTime()));
                    history.setPmtCancelExtractDate(new Timestamp(processDate.getTime()));
                    history.setPmtCancelExtractStat(Boolean.TRUE);
                    history.setChangeTime(new Timestamp(new Date().getTime()));

                    this.businessObjectService.save(history);
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
     * @see org.kuali.kfs.pdp.batch.service.ExtractPaymentService#extractAchPayments()
     */
    public void extractAchPayments() {
        LOG.debug("extractAchPayments() started");

        Date processDate = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        PaymentStatus extractedStatus = (PaymentStatus) this.kualiCodeService.getByCode(PaymentStatus.class, PdpConstants.PaymentStatusCodes.EXTRACTED);

        String achFilePrefix = this.kualiConfigurationService.getPropertyString(PdpKeyConstants.ExtractPayment.ACH_FILENAME);
        achFilePrefix = MessageFormat.format(achFilePrefix, new Object[]{ null });
        
        String filename = getOutputFile(achFilePrefix, processDate);
        LOG.debug("extractAchPayments() filename = " + filename);

        // Open file
        BufferedWriter os = null;
        
        writeExtractAchFile(extractedStatus, filename, processDate, sdf);
        
    }
        
    /**
     * @see org.kuali.kfs.pdp.batch.service.ExtractPaymentService#extractChecks()
     */
    public void extractChecks() {
        LOG.debug("extractChecks() started");

        Date processDate = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String checkFilePrefix = this.kualiConfigurationService.getPropertyString(PdpKeyConstants.ExtractPayment.CHECK_FILENAME);
        checkFilePrefix = MessageFormat.format(checkFilePrefix, new Object[]{ null });
        
        String filename = getOutputFile(checkFilePrefix, processDate);
        LOG.debug("extractChecks() filename: " + filename);

        List<PaymentProcess> extractsToRun = this.processDao.getAllExtractsToRun();
        for (PaymentProcess extractToRun : extractsToRun) {
            writeExtractCheckFile(extractToRun, filename, extractToRun.getId().intValue());
            this.processDao.setExtractProcessAsComplete(extractToRun);
        }
    }

    private void writeExtractCheckFile(PaymentProcess p, String filename, Integer processId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date processDate = dateTimeService.getCurrentDate();
        BufferedWriter os = null;

        try {
            os = new BufferedWriter(new FileWriter(filename));
            os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writeOpenTagAttribute(os, 0, "checks", "processId", processId.toString(), "campusCode", p.getCampusCode());

            List<Integer> disbNbrs = paymentGroupService.getDisbursementNumbersByDisbursementType(processId, PdpConstants.DisbursementTypeCodes.CHECK);
            for (Iterator iter = disbNbrs.iterator(); iter.hasNext();) {
                Integer disbursementNbr = (Integer) iter.next();

                boolean first = true;

                Iterator i = paymentDetailService.getByDisbursementNumber(disbursementNbr);
                while (i.hasNext()) {
                    PaymentDetail pd = (PaymentDetail) i.next();
                    PaymentGroup pg = pd.getPaymentGroup();
                    if (!testMode) {
                        if (ObjectUtils.isNull(pg.getDisbursementDate()) ) {
                            pg.setDisbursementDate(new java.sql.Date(processDate.getTime()));
                            this.businessObjectService.save(pg);
                        }
                    }

                    if (first) {
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

    private void writeExtractAchFile(PaymentStatus extractedStatus, String filename, Date processDate, SimpleDateFormat sdf) {
        BufferedWriter os = null;
        try {
            os = new BufferedWriter(new FileWriter(filename));
            os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writeOpenTag(os, 0, "achPayments");

            // totals for summary
            Map<String, Integer> unitCounts = new HashMap<String, Integer>();
            Map<String, KualiDecimal> unitTotals = new HashMap<String, KualiDecimal>();

            Iterator iter = paymentGroupService.getByDisbursementTypeStatusCode(PdpConstants.DisbursementTypeCodes.ACH, PdpConstants.PaymentStatusCodes.PENDING_ACH);
            while (iter.hasNext()) {
                PaymentGroup paymentGroup = (PaymentGroup) iter.next();
                if (!testMode) {
                    paymentGroup.setDisbursementDate(new java.sql.Date(processDate.getTime()));
                    paymentGroup.setPaymentStatus(extractedStatus);
                    businessObjectService.save(paymentGroup);
                }

                writeOpenTagAttribute(os, 2, "ach", "disbursementNbr", paymentGroup.getDisbursementNbr().toString());
                PaymentProcess paymentProcess = paymentGroup.getProcess();
                writeTag(os, 4, "processCampus", paymentProcess.getCampusCode());
                writeTag(os, 4, "processId", paymentProcess.getId().toString());

                writeBank(os, 4, paymentGroup.getBank());

                writeTag(os, 4, "disbursementDate", sdf.format(processDate));
                writeTag(os, 4, "netAmount", paymentGroup.getNetPaymentAmount().toString());

                writePayeeAch(os, 4, paymentGroup);
                writeTag(os, 4, "customerUnivNbr", paymentGroup.getCustomerInstitutionNumber());
                writeTag(os, 4, "paymentDate", sdf.format(paymentGroup.getPaymentDate()));

                // Write customer profile information
                CustomerProfile cp = paymentGroup.getBatch().getCustomerProfile();
                writeCustomerProfile(os, 4, cp);

                // Write all payment level information
                writeOpenTag(os, 4, "payments");
                List pdList = paymentGroup.getPaymentDetails();
                for (Iterator iterator = pdList.iterator(); iterator.hasNext();) {
                    PaymentDetail paymentDetail = (PaymentDetail) iterator.next();
                    writeOpenTag(os, 6, "payment");

                    // Write detail info
                    writeTag(os, 6, "purchaseOrderNbr", paymentDetail.getPurchaseOrderNbr());
                    writeTag(os, 6, "invoiceNbr", paymentDetail.getInvoiceNbr());
                    writeTag(os, 6, "requisitionNbr", paymentDetail.getRequisitionNbr());
                    writeTag(os, 6, "custPaymentDocNbr", paymentDetail.getCustPaymentDocNbr());
                    writeTag(os, 6, "invoiceDate", sdf.format(paymentDetail.getInvoiceDate()));

                    writeTag(os, 6, "origInvoiceAmount", paymentDetail.getOrigInvoiceAmount().toString());
                    writeTag(os, 6, "netPaymentAmount", paymentDetail.getNetPaymentAmount().toString());
                    writeTag(os, 6, "invTotDiscountAmount", paymentDetail.getInvTotDiscountAmount().toString());
                    writeTag(os, 6, "invTotShipAmount", paymentDetail.getInvTotShipAmount().toString());
                    writeTag(os, 6, "invTotOtherDebitAmount", paymentDetail.getInvTotOtherDebitAmount().toString());
                    writeTag(os, 6, "invTotOtherCreditAmount", paymentDetail.getInvTotOtherCreditAmount().toString());

                    writeOpenTag(os, 6, "notes");
                    for (Iterator i = paymentDetail.getNotes().iterator(); i.hasNext();) {
                        PaymentNoteText note = (PaymentNoteText) i.next();
                        writeTag(os, 8, "note", escapeString(note.getCustomerNoteText()));
                    }
                    writeCloseTag(os, 6, "notes");

                    writeCloseTag(os, 4, "payment");

                    String unit = paymentGroup.getBatch().getCustomerProfile().getChartCode() + "-" + paymentGroup.getBatch().getCustomerProfile().getUnitCode() + "-" + paymentGroup.getBatch().getCustomerProfile().getSubUnitCode();

                    Integer count = 1;
                    if (unitCounts.containsKey(unit)) {
                        count = 1 + unitCounts.get(unit);
                    }
                    unitCounts.put(unit, count);

                    KualiDecimal unitTotal = paymentDetail.getNetPaymentAmount();
                    if (unitTotals.containsKey(unit)) {
                        unitTotal = paymentDetail.getNetPaymentAmount().add(unitTotals.get(unit));
                    }
                    unitTotals.put(unit, unitTotal);
                }

                writeCloseTag(os, 4, "payments");
                writeCloseTag(os, 2, "ach");
            }
            writeCloseTag(os, 0, "achPayments");

            // send summary email
            paymentFileEmailService.sendAchSummaryEmail(unitCounts, unitTotals, dateTimeService.getCurrentDate());
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
        if (b != null) {
            writeOpenTagAttribute(os, indent, "bank", "code", b.getBankCode());
            writeTag(os, indent + 2, "accountNumber", b.getBankAccountNumber());
            writeTag(os, indent + 2, "routingNumber", b.getBankRoutingNumber());
            writeCloseTag(os, indent, "bank");
        }
    }

    private void writeCustomerProfile(BufferedWriter os, int indent, CustomerProfile cp) throws IOException {
        writeOpenTag(os, indent, "customerProfile");
        writeTag(os, indent + 2, "chartCode", cp.getChartCode());
        writeTag(os, indent + 2, "orgCode", cp.getUnitCode());
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

    /**
     * Sets the directoryName attribute value.
     * 
     * @param directoryName The directoryName to set.
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }


    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the paymentGroupService attribute value.
     * 
     * @param paymentGroupService The paymentGroupService to set.
     */
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    /**
     * Sets the paymentDetailService attribute value.
     * 
     * @param paymentDetailService The paymentDetailService to set.
     */
    public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    /**
     * Sets the paymentGroupHistoryDao attribute value.
     * 
     * @param paymentGroupHistoryDao The paymentGroupHistoryDao to set.
     */
    public void setPaymentGroupHistoryDao(PaymentGroupHistoryDao paymentGroupHistoryDao) {
        this.paymentGroupHistoryDao = paymentGroupHistoryDao;
    }

    /**
     * Sets the processDao attribute value.
     * 
     * @param processDao The processDao to set.
     */
    public void setProcessDao(ProcessDao processDao) {
        this.processDao = processDao;
    }

    /**
     * Sets the paymentFileEmailService attribute value.
     * 
     * @param paymentFileEmailService The paymentFileEmailService to set.
     */
    public void setPaymentFileEmailService(PdpEmailService paymentFileEmailService) {
        this.paymentFileEmailService = paymentFileEmailService;
    }

    /**
     * Sets the kualiCodeService attribute value.
     * 
     * @param kualiCodeService The kualiCodeService to set.
     */
    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }

    /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
