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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.lowagie.text.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.LockboxService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.dataaccess.LockboxDao;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.pdf.PdfWriter;

/**
 * Lockbox Iterators are sorted by processedInvoiceDate and batchSequenceNumber.
 * Potentially there could be many batches on the same date.
 * For each set of records with the same processedInvoiceDate and batchSequenceNumber,
 * there will be one Cash-Control document. Each record within this set will create one Application document.
 */


public class LockboxServiceImpl implements LockboxService {
    public static final String INVOICE_DOESNT_EXIST = "INVOICE DOESNT EXIST";
    public static final String INVOICE_NUMBER_IS_BLANK = "INVOICE NUMBER IS BLANK";
    public static final String INVOICE_ALREADY_CLOSED = "INVOICE ALREADY CLOSED";
    public static final String CREATED_AND_SAVED = "CREATED & SAVED";
    private static Logger LOG = org.apache.log4j.Logger.getLogger(LockboxServiceImpl.class);

    private DocumentService documentService;
    private SystemInformationService systemInformationService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private CashControlDocumentService cashControlDocumentService;
    private PaymentApplicationDocumentService payAppDocService;
    private DateTimeService dateTimeService;
    private BusinessObjectService boService;
    private CustomerService customerService;
    private DocumentTypeService documentTypeService;
    private LockboxDao lockboxDao;
    private String reportsDirectory;

    Lockbox ctrlLockbox;
    CashControlDocument cashControlDocument;
    boolean anyRecordsFound = false;


    @Override
    @NonTransactional
    public boolean processLockboxes() throws WorkflowException {

        ctrlLockbox = new Lockbox();
        cashControlDocument = null;
        anyRecordsFound = false;
        try {
            com.lowagie.text.Document pdfdoc = getPdfDoc();

            //  this giant try/catch is to make sure that something gets written to the
            // report.  please dont use it for specific exception handling, rather nest
            // new try/catch handlers inside this.
            try {
                Iterator<Lockbox> itr = getAllLockboxes().iterator();

                while (itr.hasNext()) {
                    processLockbox(itr.next(), pdfdoc);
                }

                if (cashControlDocument != null) {
                    LOG.info("   routing cash control document.");

                    cashControlDocument.getDocumentHeader().getWorkflowDocument().route("Routed by Lockbox Batch process.");

                    DocumentType documentType = documentTypeService.getDocumentTypeByName(cashControlDocument.getFinancialDocumentTypeCode());
                    DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(documentType.getApplicationId());
                    queue.indexDocument(cashControlDocument.getDocumentNumber());
                }

                if (!anyRecordsFound) {
                    writeDetailLine(pdfdoc, "NO LOCKBOX RECORDS WERE FOUND");
                }

                //  this annoying all-encompassing try/catch is here to make sure that the report gets
                // written.  without it, if anything goes wrong, the report will end up a zero-byte document.
            } catch (Exception e) {
                writeDetailLine(pdfdoc, "AN EXCEPTION OCCURRED:");
                writeDetailLine(pdfdoc, "");
                writeDetailLine(pdfdoc, e.getMessage());
                writeDetailLine(pdfdoc, "");
                writeExceptionStackTrace(pdfdoc, e);

                throw new RuntimeException("An exception occured while processing Lockboxes.", e);
            } finally {
                if (pdfdoc != null) {
                    pdfdoc.close();
                }
            }
        } catch (IOException | DocumentException ex) {
            throw new RuntimeException("Could not open file for lockbox processing results report", ex);
        }
        return true;

    }

    @Transactional
    protected Collection<Lockbox> getAllLockboxes() {
        return lockboxDao.getAllLockboxes();
    }

    @Override
    @Transactional
    public void processLockbox(Lockbox lockbox, com.lowagie.text.Document pdfdoc) {
        anyRecordsFound = true;
        LOG.info("LOCKBOX: '" + lockbox.getLockboxNumber() + "'");

        SystemInformation sysInfo = systemInformationService.getByLockboxNumberForCurrentFiscalYear(lockbox.getLockboxNumber());
        String initiator = sysInfo.getFinancialDocumentInitiatorIdentifier();
        LOG.info("   using SystemInformation: '" + sysInfo.toString() + "'");
        LOG.info("   using Financial Document Initiator ID: '" + initiator + "'");

        Principal principal = KimApiServiceLocator.getIdentityService().getPrincipal(initiator);
        if (principal == null) {
            LOG.warn("   could not find [" + initiator + "] when searching by PrincipalID, so trying to find as a PrincipalName.");
            principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(initiator);
            //  puke if the initiator stored in the systemInformation table is no good
            if (principal == null) {
                LOG.error("Financial Document Initiator ID [" + initiator + "] specified in SystemInformation [" + sysInfo.toString() + "] for Lockbox Number " + lockbox.getLockboxNumber() + " is not present in the system as either a PrincipalID or a PrincipalName.");
                throw new RuntimeException("Financial Document Initiator ID [" + initiator + "] specified in SystemInformation [" + sysInfo.toString() + "] for Lockbox Number " + lockbox.getLockboxNumber() + " is not present in the system as either a PrincipalID or a PrincipalName.");
            } else {
                LOG.info("   found [" + initiator + "] in the system as a PrincipalName.");
            }
        } else {
            LOG.info("   found [" + initiator + "] in the system as a PrincipalID.");
        }

        GlobalVariables.clear();
        GlobalVariables.setUserSession(new UserSession(principal.getPrincipalName()));

        if (foundDifferentLockbox(lockbox)) {
            createCashControlDocumentFor(lockbox, sysInfo);
            writeBatchGroupSectionTitle(pdfdoc, lockbox.getBatchSequenceNumber().toString(), lockbox.getProcessedInvoiceDate(), cashControlDocument.getDocumentNumber());
        }
        ctrlLockbox = lockbox;

        writeLockboxRecordLine(pdfdoc, lockbox.getLockboxNumber(), lockbox.getCustomerNumber(), lockbox.getFinancialDocumentReferenceInvoiceNumber(),
                lockbox.getInvoicePaidOrAppliedAmount(), lockbox.getCustomerPaymentMediumCode(), lockbox.getBankCode());

        if (lockbox.getInvoicePaidOrAppliedAmount().isZero()) {
            LOG.warn("   lockbox has a zero dollar amount, so we're skipping it.");
            writeSummaryDetailLine(pdfdoc, "ZERO-DOLLAR LOCKBOX - NO FURTHER PROCESSING");
            deleteProcessedLockboxEntry(lockbox);
            return;
        }
        if (lockbox.getInvoicePaidOrAppliedAmount().isLessThan(KualiDecimal.ZERO)) {
            LOG.warn("   lockbox has a negative dollar amount, so we're skipping it.");
            writeCashControlDetailLine(pdfdoc, lockbox.getInvoicePaidOrAppliedAmount(), "SKIPPED");
            writeSummaryDetailLine(pdfdoc, "NEGATIVE-DOLLAR LOCKBOX - NO FURTHER PROCESSING - LOCKBOX ENTRY NOT DELETED");
            return;
        }

        CashControlDetail detail = new CashControlDetail();
        if (ObjectUtils.isNotNull(lockbox.getCustomerNumber())) {
            Customer customer = customerService.getByPrimaryKey(lockbox.getCustomerNumber());
            if (ObjectUtils.isNotNull(customer)) {
                detail.setCustomerNumber(lockbox.getCustomerNumber());
            }
        }
        detail.setFinancialDocumentLineAmount(lockbox.getInvoicePaidOrAppliedAmount());
        detail.setCustomerPaymentDate(lockbox.getProcessedInvoiceDate());
        detail.setCustomerPaymentDescription("Lockbox Remittance  " + lockbox.getFinancialDocumentReferenceInvoiceNumber());

        LOG.info("   creating detail for $" + lockbox.getInvoicePaidOrAppliedAmount() + " with invoiceDate: " + lockbox.getProcessedInvoiceDate());

        try {
            cashControlDocumentService.addNewCashControlDetail(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION, cashControlDocument, detail);
        } catch (WorkflowException e) {
            LOG.error("A Exception was thrown while trying to create a new CashControl detail.", e);
            throw new RuntimeException("A Exception was thrown while trying to create a new CashControl detail.", e);
        }

        String payAppDocNumber = detail.getReferenceFinancialDocumentNumber();
        LOG.info("   new PayAppDoc was created: " + payAppDocNumber + ".");

        String invoiceNumber = lockbox.getFinancialDocumentReferenceInvoiceNumber();
        LOG.info("   lockbox references invoice number [" + invoiceNumber + "].");

        if (StringUtils.isBlank(invoiceNumber)) {
            //  if thats the case, dont even bother looking for an invoice, just save the CashControl
            LOG.info("   invoice number is blank; cannot load an invoice.");
            saveCashControlDocument(lockbox, detail, ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER);
            writeReportDetails(pdfdoc, detail, INVOICE_NUMBER_IS_BLANK, CREATED_AND_SAVED);
            deleteProcessedLockboxEntry(lockbox);
            return;
        }

        if (!documentService.documentExists(invoiceNumber)) {
            LOG.info("   invoice number [" + invoiceNumber + "] does not exist in system, so cannot load the original invoice.");
            saveCashControlDocument(lockbox, detail, ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER);
            writeReportDetails(pdfdoc, detail, INVOICE_DOESNT_EXIST, CREATED_AND_SAVED);
            deleteProcessedLockboxEntry(lockbox);
            return;
        }

        LOG.info("   loading invoice number [" + invoiceNumber + "].");
        CustomerInvoiceDocument customerInvoiceDocument = retrieveCustomerInvoiceDocumentBy(invoiceNumber);

        writeInvoiceDetailLine(pdfdoc, invoiceNumber, customerInvoiceDocument.isOpenInvoiceIndicator(), customerInvoiceDocument.getCustomer().getCustomerNumber(), customerInvoiceDocument.getOpenAmount());

        if (!customerInvoiceDocument.isOpenInvoiceIndicator()) {
            LOG.info("   invoice is already closed, so saving CashControl doc and moving on.");
            saveCashControlDocument(lockbox, detail, ArConstants.LOCKBOX_REMITTANCE_FOR_CLOSED_INVOICE_NUMBER);
            writeReportDetails(pdfdoc, detail, INVOICE_ALREADY_CLOSED, CREATED_AND_SAVED);
            deleteProcessedLockboxEntry(lockbox);
            return;
        }

        PaymentApplicationDocument payAppDoc = retrievePaymentApplicationDocumentBy(payAppDocNumber);

        boolean autoApprove = canAutoApprove(customerInvoiceDocument, lockbox, payAppDoc);
        String annotation = CREATED_AND_SAVED;

        //  if the lockbox amount matches the invoice amount, then create, save and approve a PayApp, and then
        if (autoApprove) {
            annotation = "CREATED, SAVED, and BLANKET APPROVED";

            LOG.debug("   lockbox amount matches invoice total document amount [" + customerInvoiceDocument.getTotalDollarAmount() + "].");
            LOG.debug("   loading the generated PayApp [" + payAppDocNumber + "], so we can route or approve it.");
            LOG.debug("   attempting to create paidApplieds on the PayAppDoc for every detail on the invoice.");

            payAppDoc = payAppDocService.createInvoicePaidAppliedsForEntireInvoiceDocument(customerInvoiceDocument, payAppDoc);

            LOG.debug("   PayAppDoc has TotalApplied of " + payAppDoc.getTotalApplied() + " for a Control Balance of " + payAppDoc.getTotalFromControl() + ".");
            LOG.debug("   attempting to blanketApprove the PayApp Doc.");

            blanketApprove(payAppDoc);
            writeReportDetails(pdfdoc, detail, "LOCKBOX AMOUNT MATCHES INVOICE OPEN AMOUNT", annotation);
        } else {
            LOG.info("   lockbox amount does NOT match invoice total document amount [" + customerInvoiceDocument.getTotalDollarAmount() + "].");
            writeReportDetails(pdfdoc, detail, getSummaryMessage(lockbox, customerInvoiceDocument), annotation);
        }

        saveCustomerPaymentDocument(lockbox, detail);
        deleteProcessedLockboxEntry(lockbox);
    }

    protected void saveCustomerPaymentDocument(Lockbox lockbox, CashControlDetail detail) {
        detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVOICE_NUMBER + lockbox.getFinancialDocumentReferenceInvoiceNumber());
        LOG.info("   saving cash control document.");
        try {
            documentService.saveDocument(cashControlDocument);
        } catch (WorkflowException e) {
            LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
            throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
        }
    }

    protected String getSummaryMessage(Lockbox lockbox, CustomerInvoiceDocument customerInvoiceDocument) {
        String summaryDetail;
        if (lockbox.getInvoicePaidOrAppliedAmount().isLessThan(customerInvoiceDocument.getOpenAmount())) {
            summaryDetail = "LOCKBOX UNDERPAID INVOICE";
        } else {
            summaryDetail = "LOCKBOX OVERPAID INVOICE";
        }
        return summaryDetail;
    }

    protected void blanketApprove(PaymentApplicationDocument payAppDoc) {
        try {
            documentService.blanketApproveDocument(payAppDoc, "Automatically approved by Lockbox batch job.", null);
        } catch (WorkflowException e) {
            LOG.error("A Exception was thrown while trying to blanketApprove PayAppDoc #" + payAppDoc.getDocumentNumber() + ".", e);
            throw new RuntimeException("A Exception was thrown while trying to blanketApprove PayAppDoc #" + payAppDoc.getDocumentNumber() + ".", e);
        }
    }

    protected PaymentApplicationDocument retrievePaymentApplicationDocumentBy(String payAppDocNumber) {
        PaymentApplicationDocument payAppDoc;
        try {
            payAppDoc = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(payAppDocNumber);
        } catch (WorkflowException e) {
            LOG.error("A Exception was thrown while trying to load PayApp #" + payAppDocNumber + ".", e);
            throw new RuntimeException("A Exception was thrown while trying to load PayApp #" + payAppDocNumber + ".", e);
        }
        return payAppDoc;
    }

    protected CustomerInvoiceDocument retrieveCustomerInvoiceDocumentBy(String invoiceNumber) {
        CustomerInvoiceDocument customerInvoiceDocument;
        try {
            customerInvoiceDocument = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(invoiceNumber);
        } catch (WorkflowException e) {
            LOG.error("A Exception was thrown while trying to load invoice #" + invoiceNumber + ".", e);
            throw new RuntimeException("A Exception was thrown while trying to load invoice #" + invoiceNumber + ".", e);
        }
        return customerInvoiceDocument;
    }

    protected void writeReportDetails(Document pdfdoc, CashControlDetail detail, String summaryDetail, String payAppAction) {
        writeCashControlDetailLine(pdfdoc, detail.getFinancialDocumentLineAmount(), detail.getCustomerPaymentDescription());
        writePayAppLine(pdfdoc, detail.getReferenceFinancialDocumentNumber(), payAppAction);
        writeSummaryDetailLine(pdfdoc, summaryDetail);
    }

    protected void saveCashControlDocument(Lockbox lockbox, CashControlDetail detail, String customerPaymentDescription) {
        detail.setCustomerPaymentDescription(customerPaymentDescription + lockbox.getFinancialDocumentReferenceInvoiceNumber());
        try {
            documentService.saveDocument(cashControlDocument);
        } catch (WorkflowException e) {
            LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
            throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
        }
    }

    protected boolean foundDifferentLockbox(Lockbox lockbox) {
        return lockbox.compareTo(ctrlLockbox) != 0;
    }

    protected void createCashControlDocumentFor(Lockbox lockbox, SystemInformation sysInfo) {
        // If we made it in here, then we have hit a different batchSequenceNumber and processedInvoiceDate.
        // When this is the case, we create a new cashcontroldocument and start tacking subsequent lockboxes on
        // to the current cashcontroldocument as cashcontroldetails.
        LOG.info("New Lockbox batch");

        if (cashControlDocument != null) {
            routeCashControlDocument();
        }

        LOG.info("Creating new CashControl document for invoice: " + lockbox.getFinancialDocumentReferenceInvoiceNumber() + ".");
        try {
            cashControlDocument = (CashControlDocument) documentService.getNewDocument(KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL);
        } catch (Exception e) {
            LOG.error("A Exception was thrown while trying to initiate a new CashControl document.", e);
            throw new RuntimeException("A Exception was thrown while trying to initiate a new CashControl document.", e);
        }
        LOG.info("   CashControl documentNumber == '" + cashControlDocument.getDocumentNumber() + "'");


        cashControlDocument.setCustomerPaymentMediumCode(lockbox.getCustomerPaymentMediumCode());
        if (ObjectUtils.isNotNull(lockbox.getBankCode())) {
            cashControlDocument.setBankCode(lockbox.getBankCode());
        }
        cashControlDocument.getDocumentHeader().setDocumentDescription(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION + lockbox.getLockboxNumber());

        LOG.info("   creating AR header for customer: [" + lockbox.getCustomerNumber() + "] and ProcessingOrg: " + sysInfo.getProcessingChartOfAccountCode() + "-" + sysInfo.getProcessingOrganizationCode() + ".");
        cashControlDocument.setAccountsReceivableDocumentHeader(createAccountsReceivableDocHeader(lockbox, sysInfo));
    }

    protected AccountsReceivableDocumentHeader createAccountsReceivableDocHeader(Lockbox lockbox, SystemInformation sysInfo) {
        AccountsReceivableDocumentHeader arDocHeader = new AccountsReceivableDocumentHeader();
        arDocHeader.setProcessingChartOfAccountCode(sysInfo.getProcessingChartOfAccountCode());
        arDocHeader.setProcessingOrganizationCode(sysInfo.getProcessingOrganizationCode());
        arDocHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());

        if (ObjectUtils.isNotNull(lockbox.getCustomerNumber())) {
            Customer customer = customerService.getByPrimaryKey(lockbox.getCustomerNumber());
            if (ObjectUtils.isNotNull(customer)) {
                arDocHeader.setCustomerNumber(lockbox.getCustomerNumber());
            }
        }
        return arDocHeader;
    }

    protected void routeCashControlDocument() {
        LOG.info("   routing cash control document.");
        try {
            cashControlDocument.getDocumentHeader().getWorkflowDocument().route("Routed by Lockbox Batch process.");

            DocumentType documentType = documentTypeService.getDocumentTypeByName(cashControlDocument.getFinancialDocumentTypeCode());
            DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(documentType.getApplicationId());
            queue.indexDocument(cashControlDocument.getDocumentNumber());
        } catch (Exception e) {
            LOG.error("A Exception was thrown while trying to route the CashControl document.", e);
            throw new RuntimeException("A Exception was thrown while trying to route the CashControl document.", e);
        }
    }

    protected boolean canAutoApprove(CustomerInvoiceDocument invoice, Lockbox lockbox, PaymentApplicationDocument payAppDoc) {
        boolean retVal = invoice.getOpenAmount().equals(lockbox.getInvoicePaidOrAppliedAmount());
        retVal &= ObjectUtils.isNotNull(payAppDoc.getCashControlDetail().getCustomerNumber());
        return retVal;
    }

    protected void deleteProcessedLockboxEntry(Lockbox lockboxEntry) {
        Map<String, Object> pkMap = new HashMap<>();
        pkMap.put(ArPropertyConstants.INVOICE_SEQUENCE_NUMBER, lockboxEntry.getInvoiceSequenceNumber());
        boService.deleteMatching(Lockbox.class, pkMap);
    }

    protected com.lowagie.text.Document getPdfDoc() throws IOException, DocumentException {
        String reportDropFolder = reportsDirectory + "/" + ArConstants.Lockbox.LOCKBOX_REPORT_SUBFOLDER + "/";
        String fileName = ArConstants.Lockbox.BATCH_REPORT_BASENAME + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(dateTimeService.getCurrentDate()) + ".pdf";

        File reportFile = new File(reportDropFolder + fileName);
        FileOutputStream fileOutStream;
        fileOutStream = new FileOutputStream(reportFile);
        BufferedOutputStream buffOutStream = new BufferedOutputStream(fileOutStream);

        com.lowagie.text.Document pdfdoc = new com.lowagie.text.Document(PageSize.LETTER, 54, 54, 72, 72);
        PdfWriter.getInstance(pdfdoc, buffOutStream);

        pdfdoc.open();

        return pdfdoc;
    }

    protected String rightPad(String valToPad, int sizeToPadTo) {
        return rightPad(valToPad, sizeToPadTo, " ");
    }

    protected String rightPad(String valToPad, int sizeToPadTo, String padChar) {
        if (StringUtils.isBlank(valToPad)) {
            return StringUtils.repeat(padChar, sizeToPadTo);
        }
        if (valToPad.length() >= sizeToPadTo) {
            return valToPad;
        }
        return valToPad + StringUtils.repeat(padChar, sizeToPadTo - valToPad.length());
    }

    protected void writeBatchGroupSectionTitle(com.lowagie.text.Document pdfDoc, String batchSeqNbr, java.sql.Date procInvDt, String cashControlDocNumber) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD);

        String lineText = "CASHCTL " + rightPad(cashControlDocNumber, 12) + " " +
                "BATCH GROUP: " + rightPad(batchSeqNbr, 5) + " " +
                rightPad((procInvDt == null ? "NONE" : procInvDt.toString()), 35);

        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
        Chunk chunk = new Chunk(lineText, font);
        chunk.setBackground(Color.LIGHT_GRAY, 5, 5, 5, 5);
        paragraph.add(chunk);

        paragraph.add(new Chunk("", font));

        try {
            pdfDoc.add(paragraph);
        } catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }

    protected void writeLockboxRecordLine(com.lowagie.text.Document pdfDoc, String lockboxNumber, String customerNumber, String invoiceNumber,
                                          KualiDecimal invoiceTotalAmount, String paymentMediumCode, String bankCode) {

        writeDetailLine(pdfDoc, StringUtils.repeat("-", 100));

        StringBuilder sb = new StringBuilder();
        sb.append("   ");                                                        // 3:   1 - 3
        sb.append("LOCKBOX: " + rightPad(lockboxNumber, 10) + " ");              // 20:  4 - 23
        sb.append("CUST: " + rightPad(customerNumber, 9) + " ");                 // 15:  24 - 38
        sb.append("INV: " + rightPad(invoiceNumber, 10) + " ");                  // 16:  39 - 55
        sb.append(StringUtils.repeat(" ", 28));                                  // 28:  56 - 83
        sb.append("AMT: " + rightPad(invoiceTotalAmount.toString(), 11) + " ");  // 17:  84 - 100

        writeDetailLine(pdfDoc, sb.toString());
    }

    protected void writeInvoiceDetailLine(com.lowagie.text.Document pdfDoc, String invoiceNumber, boolean open, String customerNumber, KualiDecimal openAmount) {

        StringBuilder sb = new StringBuilder();
        sb.append("   ");                                                        // 3:   1 - 3
        sb.append("INVOICE: " + rightPad(invoiceNumber, 10) + " ");              // 20:  4 - 23
        sb.append("CUST: " + rightPad(customerNumber, 9) + " ");                 // 15:  24 - 38
        if (open) {
            sb.append(rightPad("OPEN", 16) + " ");                               // 16:  39 - 55
        } else {
            sb.append(rightPad("CLOSED", 16) + " ");                             // 16:  39 - 55
        }
        sb.append(StringUtils.repeat(" ", 22));                                  // 28:  56 - 83
        sb.append("OPEN AMT: " + rightPad(openAmount.toString(), 11) + " ");  // 17:  84 - 100

        writeDetailLine(pdfDoc, sb.toString());
    }

    protected void writeCashControlDetailLine(com.lowagie.text.Document pdfDoc, KualiDecimal amount, String description) {

        StringBuilder sb = new StringBuilder();
        sb.append("   ");                                                        // 3:   1 - 3
        sb.append("CASHCTL DTL: " + rightPad(description, 66) + " ");            // 80:  4 - 83
        sb.append("AMT: " + rightPad(amount.toString(), 11) + " ");              // 17:  84 - 100

        writeDetailLine(pdfDoc, sb.toString());
    }

    protected void writeSummaryDetailLine(com.lowagie.text.Document pdfDoc, String summary) {
        writeDetailLine(pdfDoc, "   " + summary);
    }

    protected void writePayAppLine(com.lowagie.text.Document pdfDoc, String payAppDocNbr, String description) {

        StringBuilder sb = new StringBuilder();
        sb.append("   ");                                                    // 3:   1 - 3
        sb.append("PAYAPP DOC NBR: " + rightPad(payAppDocNbr, 12) + " ");    // 29:  4 - 32
        sb.append("ACTION: " + description);                                 // 40:  33 - 72

        writeDetailLine(pdfDoc, sb.toString());
    }

    protected void writeExceptionStackTrace(com.lowagie.text.Document pdfDoc, Exception e) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(outStream, true);

        e.printStackTrace(printWriter);
        printWriter.flush();
        writeDetailLine(pdfDoc, outStream.toString());
    }

    protected void writeDetailLine(com.lowagie.text.Document pdfDoc, String detailLineText) {
        if (ObjectUtils.isNotNull(detailLineText)) {
            Font font = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);

            Paragraph paragraph = new Paragraph();
            paragraph.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
            paragraph.add(new Chunk(detailLineText, font));

            try {
                pdfDoc.add(paragraph);
            } catch (DocumentException e) {
                LOG.error("iText DocumentException thrown when trying to write content.", e);
                throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
            }
        }
    }

    @Override
    @Transactional
    public Long getMaxLockboxSequenceNumber() {
        return lockboxDao.getMaxLockboxSequenceNumber();
    }

    @NonTransactional
    public LockboxDao getLockboxDao() {
        return lockboxDao;
    }

    @NonTransactional
    public void setLockboxDao(LockboxDao lockboxDao) {
        this.lockboxDao = lockboxDao;
    }

    @NonTransactional
    public SystemInformationService getSystemInformationService() {
        return systemInformationService;
    }

    @NonTransactional
    public void setSystemInformationService(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    @NonTransactional
    public AccountsReceivableDocumentHeaderService getAccountsReceivableDocumentHeaderService() {
        return accountsReceivableDocumentHeaderService;
    }

    @NonTransactional
    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    @NonTransactional
    public void setPaymentApplicationDocumentService(PaymentApplicationDocumentService paymentApplicationDocumentService) {
        this.payAppDocService = paymentApplicationDocumentService;
    }

    @NonTransactional
    public DocumentService getDocumentService() {
        return documentService;
    }

    @NonTransactional
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @NonTransactional
    public CashControlDocumentService getCashControlDocumentService() {
        return cashControlDocumentService;
    }

    @NonTransactional
    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }

    @NonTransactional
    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    @NonTransactional
    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    @NonTransactional
    public CustomerService getCustomerService() {
        return customerService;
    }

    @NonTransactional
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @NonTransactional
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
}
