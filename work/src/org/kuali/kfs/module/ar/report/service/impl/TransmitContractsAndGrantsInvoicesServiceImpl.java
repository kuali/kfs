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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
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
    protected DocumentService documentService;
    protected PersonService personService;

    protected static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("_yyyy-MM-dd_hhmmss");

    /**
     * Creates a map, does a look up based on that, and then collects the documents found
     * @see org.kuali.kfs.module.ar.report.service.TransmitContractsAndGrantsInvoicesService#getInvoicesByParametersFromRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getInvoicesByParametersFromRequest(String userId, String documentNumber, String proposalNumber, String invoiceAmount, String chartOfAccountsCode, String organizationCode, String unformattedToDate, String unformattedFromDate, String invoiceTransmissionMethodCode) throws WorkflowException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(KFSConstants.MONTH_DAY_YEAR_DATE_FORMAT);
        SimpleDateFormat reqDateFormat = new SimpleDateFormat(ArConstants.YEAR_MONTH_DAY_HOUR_MINUTE_SECONDS_DATE_FORMAT);
        Timestamp fromDate = null;
        Timestamp toDate = null;
        if (StringUtils.isNotEmpty(unformattedToDate)) {
            toDate = Timestamp.valueOf(reqDateFormat.format(dateFormat.parse(unformattedToDate)));
        }
        if (StringUtils.isNotEmpty(unformattedFromDate)) {
            fromDate = Timestamp.valueOf(reqDateFormat.format(dateFormat.parse(unformattedFromDate)));
        }
        Map<String, String> fieldValues = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(proposalNumber)) {
            fieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if (StringUtils.isNotEmpty(documentNumber)) {
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        }
        if (ObjectUtils.isNotNull(invoiceAmount)) {
            fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, invoiceAmount);
        }
        if (StringUtils.isNotEmpty(chartOfAccountsCode)) {
            fieldValues.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT_CODE, chartOfAccountsCode);
        }
        if (StringUtils.isNotEmpty(organizationCode)) {
            fieldValues.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, organizationCode);
        }
        if (StringUtils.isNotBlank(invoiceTransmissionMethodCode) && !StringUtils.equalsIgnoreCase(invoiceTransmissionMethodCode, ArConstants.InvoiceTransmissionMethod.BOTH)) {
            fieldValues.put(ArPropertyConstants.INVOICE_TRANSMISSION_METHOD_CODE, invoiceTransmissionMethodCode);
        }
        Collection<ContractsGrantsInvoiceDocument> list = getContractsGrantsInvoiceDocumentService().retrieveAllCGInvoicesByCriteria(fieldValues);
        Collection<ContractsGrantsInvoiceDocument> finalList = new ArrayList<ContractsGrantsInvoiceDocument>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (ContractsGrantsInvoiceDocument item : list) {
            if (ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE.equals(item.getFinancialSystemDocumentHeader().getWorkflowDocumentTypeName())) {
                ContractsGrantsInvoiceDocument invoice = (ContractsGrantsInvoiceDocument)getDocumentService().getByDocumentHeaderId(item.getDocumentNumber());

                boolean invoiceIsFinal = StringUtils.equals(item.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode(), DocumentStatus.FINAL.getCode());
                boolean invoiceIsProcessed = StringUtils.equals(item.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode(), DocumentStatus.PROCESSED.getCode());

                if ( invoiceIsFinal || invoiceIsProcessed ) {
                    if (StringUtils.isNotEmpty(userId)) {
                        Person person = getPersonService().getPersonByPrincipalName(userId);
                        if (person == null) {
                            throw new IllegalArgumentException("The parameter value for initiatorPrincipalName [" + userId + "] passed in does not map to a person.");
                        }
                        if (StringUtils.equalsIgnoreCase(item.getFinancialSystemDocumentHeader().getInitiatorPrincipalId(), person.getPrincipalId())) {
                            if (isInvoiceBetween(invoice, fromDate, toDate)) {
                                if (isInvoiceValidToEmail(invoice) || isInvoiceValidToMail(invoice)) {
                                    finalList.add(invoice);
                                }
                            }
                        }
                    }
                    else if (isInvoiceBetween(invoice, fromDate, toDate)) {
                        if (isInvoiceValidToEmail(invoice) || isInvoiceValidToMail(invoice)) {
                            finalList.add(invoice);
                        }
                    }
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
    protected boolean isInvoiceBetween(ContractsGrantsInvoiceDocument invoice, Timestamp fromDate, Timestamp toDate) {
        if (ObjectUtils.isNotNull(fromDate)) {
            if (fromDate.after(new Timestamp(invoice.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis()))) {
                return false;
            }
        }
        if (ObjectUtils.isNotNull(toDate)) {
            if (toDate.before(new Timestamp(invoice.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isInvoiceValidToEmail(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (ObjectUtils.isNull(contractsGrantsInvoiceDocument.getMarkedForProcessing())) {
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
            byte[] envelopes = contractsGrantsInvoiceReportService.generateListOfInvoicesEnvelopesPdfToPrint(list);
            byte[] report = contractsGrantsInvoiceReportService.generateListOfInvoicesPdfToPrint(list);
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
        String invoiceInitiatorPrincipalName = fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesFields.INVOICE_INITIATOR_PRINCIPAL_NAME);
        String invoicePrintDateFromString = fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesFields.INVOICE_PRINT_DATE_FROM);
        String invoicePrintDateToString = fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesFields.INVOICE_PRINT_DATE_TO);
        String invoiceAmount = fieldValues.get(ArPropertyConstants.TransmitContractsAndGrantsInvoicesFields.INVOICE_AMOUNT);

        // To validate the input fields before fetching invoices.
        if (StringUtils.isNotBlank(invoicePrintDateFromString)) {
            SimpleDateFormat sdf = new SimpleDateFormat(KFSConstants.MONTH_DAY_YEAR_DATE_FORMAT);
            Date testDate = null;
            try {
                testDate = sdf.parse(invoicePrintDateFromString);
            }
            catch (ParseException e) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.TransmitContractsAndGrantsInvoicesFields.INVOICE_PRINT_DATE_FROM, KFSKeyConstants.ERROR_DATE_TIME, ArPropertyConstants.PRINT_INVOICES_FROM_LABEL);
            }
        }
        if (StringUtils.isNotBlank(invoicePrintDateToString)) {
            SimpleDateFormat sdf = new SimpleDateFormat(KFSConstants.MONTH_DAY_YEAR_DATE_FORMAT);
            Date testDate = null;
            try {
                testDate = sdf.parse(invoicePrintDateToString);
            }
            catch (ParseException e) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.TransmitContractsAndGrantsInvoicesFields.INVOICE_PRINT_DATE_TO, KFSKeyConstants.ERROR_DATE_TIME, ArPropertyConstants.PRINT_INVOICES_TO_LABEL);
            }
        }
        if (!StringUtils.isNumeric(invoiceAmount)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.TransmitContractsAndGrantsInvoicesFields.INVOICE_AMOUNT, KFSKeyConstants.ERROR_NUMERIC, ArPropertyConstants.INVOICE_AMOUNT_LABEL);
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
    public void sendEmailForListofInvoicesToAgency(Collection<ContractsGrantsInvoiceDocument> list) {
        for (ContractsGrantsInvoiceDocument invoiceDocument : list) {
            invoiceDocument.setMarkedForProcessing(ArConstants.INV_RPT_PRCS_IN_PROGRESS);
            documentService.updateDocument(invoiceDocument);
        }
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

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

}
