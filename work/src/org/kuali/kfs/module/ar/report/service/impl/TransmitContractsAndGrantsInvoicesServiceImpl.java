/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.module.ar.report.service.TransmitContractsAndGrantsInvoicesService;
import org.kuali.kfs.module.ar.service.AREmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import com.lowagie.text.DocumentException;

/**
 * Default implementation of the TransmitContractsAndGrantsInvoicesService
 */
public class TransmitContractsAndGrantsInvoicesServiceImpl implements TransmitContractsAndGrantsInvoicesService {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected ContractsGrantsInvoiceReportService contractsGrantsInvoiceReportService;
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected AREmailService arEmailService;

    protected static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("_yyyy-MM-dd_hhmmss");

    /**
     * @see org.kuali.kfs.module.ar.report.service.TransmitContractsAndGrantsInvoicesService#getInvoicesByParametersFromRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getInvoicesByParametersFromRequest(Map fieldValues) throws WorkflowException, ParseException {
        Date fromDate = null;
        Date toDate = null;
        String unformattedToDate = (String)fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_PRINT_DATE_TO);
        if (StringUtils.isNotEmpty(unformattedToDate)) {
            toDate = dateTimeService.convertToDate(unformattedToDate);
        }
        String unformattedFromDate = (String)fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_PRINT_DATE_FROM);
        if (StringUtils.isNotEmpty(unformattedFromDate)) {
            fromDate = dateTimeService.convertToDate(unformattedFromDate);
        }
        String invoiceInitiatorPrincipalName = (String) fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_INITIATOR_PRINCIPAL_NAME);
        if (StringUtils.isNotEmpty(invoiceInitiatorPrincipalName)) {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(invoiceInitiatorPrincipalName);
            if (ObjectUtils.isNotNull(principal)) {
                fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.INITIATOR_PRINCIPAL_ID, principal.getPrincipalId());
            } else {
                throw new IllegalArgumentException("The parameter value for initiatorPrincipalName [" + invoiceInitiatorPrincipalName + "] passed in does not map to a person.");
            }
        }

        String invoiceAmount = (String) fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_AMOUNT);
        if (StringUtils.isNotEmpty(invoiceAmount)) {
            fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, invoiceAmount);
        }

        String invoiceTransmissionMethodCode = (String) fieldValues.get(ArPropertyConstants.INVOICE_TRANSMISSION_METHOD_CODE);
        if (StringUtils.isNotBlank(invoiceTransmissionMethodCode)) {
            fieldValues.put(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_TRANSMISSION_METHOD_CODE, invoiceTransmissionMethodCode);
        }

        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, DocumentStatus.FINAL.getCode() + SearchOperator.OR.op() + DocumentStatus.PROCESSED.getCode());

        Collection<ContractsGrantsInvoiceDocument> list = getContractsGrantsInvoiceDocumentService().retrieveAllCGInvoicesByCriteria(fieldValues);
        Collection<ContractsGrantsInvoiceDocument> finalList = new ArrayList<ContractsGrantsInvoiceDocument>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (ContractsGrantsInvoiceDocument item : list) {
            ContractsGrantsInvoiceDocument invoice = (ContractsGrantsInvoiceDocument)getDocumentService().getByDocumentHeaderId(item.getDocumentNumber());

            if (isInvoiceBetween(invoice, fromDate, toDate)) {
                if ((StringUtils.equals(ArConstants.InvoiceTransmissionMethod.EMAIL, invoiceTransmissionMethodCode) && isInvoiceValidToEmail(invoice)) ||
                    (StringUtils.equals(ArConstants.InvoiceTransmissionMethod.MAIL, invoiceTransmissionMethodCode) && isInvoiceValidToMail(invoice))) {
                    finalList.add(invoice);
                }
            }
        }
        return finalList;
    }

    /**
     * Checks whether invoice is between the dates provided.
     * @param invoice
     * @param fromDate
     * @param toDate
     * @return
     */
    protected boolean isInvoiceBetween(ContractsGrantsInvoiceDocument invoice, Date fromDate, Date toDate) {
        Date dateCreated = invoice.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate();
        if (ObjectUtils.isNotNull(fromDate)) {
            if (fromDate.after(dateCreated)) {
                return false;
            }
        }
        if (ObjectUtils.isNotNull(toDate)) {
            if (toDate.before(dateCreated) && !KfsDateUtils.isSameDay(toDate, dateCreated)) {
                return false;
            }
        }
        return true;

    }

    @Override
    public boolean isInvoiceValidToEmail(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (ObjectUtils.isNull(contractsGrantsInvoiceDocument.getDateEmailProcessed())) {
            for (InvoiceAddressDetail invoiceAddressDetail : contractsGrantsInvoiceDocument.getInvoiceAddressDetails()) {
                if (ArConstants.InvoiceTransmissionMethod.EMAIL.equals(invoiceAddressDetail.getInvoiceTransmissionMethodCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isInvoiceValidToMail(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (ObjectUtils.isNull(contractsGrantsInvoiceDocument.getDateReportProcessed())) {
            for (InvoiceAddressDetail invoiceAddressDetail : contractsGrantsInvoiceDocument.getInvoiceAddressDetails()) {
                if (ArConstants.InvoiceTransmissionMethod.MAIL.equals(invoiceAddressDetail.getInvoiceTransmissionMethodCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean printInvoicesAndEnvelopesZip(Collection<ContractsGrantsInvoiceDocument> list, ByteArrayOutputStream baos) throws DocumentException, IOException {
        if (CollectionUtils.isNotEmpty(list)) {
            byte[] envelopes = contractsGrantsInvoiceReportService.combineInvoicePdfEnvelopes(list);
            byte[] report = contractsGrantsInvoiceReportService.combineInvoicePdfs(list);
            boolean invoiceFileWritten = false;
            boolean envelopeFileWritten = false;

            ZipOutputStream zos = new ZipOutputStream(baos);
            try {
                int bytesRead;
                byte[] buffer = new byte[1024];
                CRC32 crc = new CRC32();

                String invoiceFileName = ArConstants.INVOICES_FILE_PREFIX + FILE_NAME_TIMESTAMP.format(new Date()) + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
                invoiceFileWritten = writeFile(report, zos, invoiceFileName);

                String envelopeFileName = ArConstants.INVOICE_ENVELOPES_FILE_PREFIX + FILE_NAME_TIMESTAMP.format(new Date()) + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
                envelopeFileWritten = writeFile(envelopes, zos, envelopeFileName);
            } finally {
                zos.close();
            }
            return invoiceFileWritten || envelopeFileWritten;
        }
        return false;
    }

    /**
     *
     * @param report
     * @param invoiceFileWritten
     * @param zos
     * @param buffer
     * @param crc
     * @return
     * @throws IOException
     */
    private boolean writeFile(byte[] arrayToWrite, ZipOutputStream zos, String fileName) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[1024];
        CRC32 crc = new CRC32();

        if (ObjectUtils.isNotNull(arrayToWrite) && arrayToWrite.length > 0) {
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(arrayToWrite));
            try {
                while ((bytesRead = bis.read(buffer)) != -1) {
                    crc.update(buffer, 0, bytesRead);
                }
                bis.close();
                // Reset to beginning of input stream
                bis = new BufferedInputStream(new ByteArrayInputStream(arrayToWrite));
                ZipEntry entry = new ZipEntry(fileName);
                entry.setMethod(ZipEntry.STORED);
                entry.setCompressedSize(arrayToWrite.length);
                entry.setSize(arrayToWrite.length);
                entry.setCrc(crc.getValue());
                zos.putNextEntry(entry);
                while ((bytesRead = bis.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytesRead);
                }
            } finally {
                bis.close();
            }
            return true;
        }
        return false;
    }

    @Override
    public void validateSearchParameters(Map<String,String> fieldValues) {
        String invoiceTransmissionMethodCode = fieldValues.get(ArPropertyConstants.INVOICE_TRANSMISSION_METHOD_CODE);
        String invoiceInitiatorPrincipalName = fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_INITIATOR_PRINCIPAL_NAME);
        String invoicePrintDateFromString = fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_PRINT_DATE_FROM);
        String invoicePrintDateToString = fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_PRINT_DATE_TO);
        String invoiceAmount = fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_AMOUNT);

        // To validate the input fields before fetching invoices.
        if (StringUtils.isBlank(invoiceTransmissionMethodCode)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.INVOICE_TRANSMISSION_METHOD_CODE, KFSKeyConstants.ERROR_REQUIRED, ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_TRANSMISSION_METHOD_CODE_LABEL);
        }

        if (StringUtils.isNotBlank(invoicePrintDateFromString)) {
            try {
                dateTimeService.convertToDate(invoicePrintDateFromString);
            }
            catch (ParseException e) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_PRINT_DATE_FROM, KFSKeyConstants.ERROR_DATE_TIME, ArPropertyConstants.PRINT_INVOICES_FROM_LABEL);
            }
        }
        if (StringUtils.isNotBlank(invoicePrintDateToString)) {
            try {
                dateTimeService.convertToDate(invoicePrintDateToString);
            }
            catch (ParseException e) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_PRINT_DATE_TO, KFSKeyConstants.ERROR_DATE_TIME, ArPropertyConstants.PRINT_INVOICES_TO_LABEL);
            }
        }
        if (StringUtils.isNotBlank(invoiceAmount) && !KualiDecimal.isNumeric(invoiceAmount)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_AMOUNT, KFSKeyConstants.ERROR_NUMERIC, ArPropertyConstants.INVOICE_AMOUNT_LABEL);
        }
        if (StringUtils.isNotEmpty(invoiceInitiatorPrincipalName)) {
            Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(invoiceInitiatorPrincipalName);
            if (ObjectUtils.isNull(person)) {
                GlobalVariables.getMessageMap().putErrorForSectionId(ArPropertyConstants.LOOKUP_SECTION_ID, ArKeyConstants.NO_PRINCIPAL_NAME_FOUND);
            }
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("Error(s) in search criteria");
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.TransmitContractsAndGrantsInvoicesService#sendEmailForListofInvoicesToAgency(java.util.Collection)
     */
    @Override
    public boolean sendEmailForListofInvoicesToAgency(Collection<ContractsGrantsInvoiceDocument> list) throws InvalidAddressException, MessagingException {
        return arEmailService.sendInvoicesViaEmail(list);
    }

    /**
     * Returns FILE_NAME_TIMESTAMP
     * @see org.kuali.kfs.module.ar.report.service.TransmitContractsAndGrantsInvoicesService#getFileNameTimestampFormat()
     */
    @Override
    public SimpleDateFormat getFileNameTimestampFormat() {
        return FILE_NAME_TIMESTAMP;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public ContractsGrantsInvoiceReportService getContractsGrantsInvoiceReportService() {
        return contractsGrantsInvoiceReportService;
    }

    public void setContractsGrantsInvoiceReportService(ContractsGrantsInvoiceReportService contractsGrantsInvoiceReportService) {
        this.contractsGrantsInvoiceReportService = contractsGrantsInvoiceReportService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public AREmailService getArEmailService() {
        return arEmailService;
    }

    public void setArEmailService(AREmailService arEmailService) {
        this.arEmailService = arEmailService;
    }

}
