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
package org.kuali.kfs.module.ar.service.impl;

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
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.module.ar.service.InvoiceReportDeliveryService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

import com.lowagie.text.DocumentException;

/**
 * Default implementation of the InvoiceReportDeliveryService
 */
public class InvoiceReportDeliveryServiceImpl implements InvoiceReportDeliveryService {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected ContractsGrantsInvoiceReportService contractsGrantsInvoiceReportService;
    protected DocumentService documentService;
    protected PersonService personService;

    protected static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("_yyyy-MM-dd_hhmmss");

    /**
     * Creates a map, does a look up based on that, and then collects the documents found
     * @see org.kuali.kfs.module.ar.service.InvoiceReportDeliveryService#getInvoicesByParametersFromRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getInvoicesByParametersFromRequest(String userId, String documentNumber, String proposalNumber, String invoiceAmount, String chartOfAccountsCode, String organizationCode, String unformattedToDate, String unformattedFromDate) throws WorkflowException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
        SimpleDateFormat reqDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
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
            fieldValues.put("proposalNumber", proposalNumber);
        }
        if (StringUtils.isNotEmpty(documentNumber)) {
            fieldValues.put("documentNumber", documentNumber);
        }
        if (ObjectUtils.isNotNull(invoiceAmount)) {
            fieldValues.put("documentHeader.financialDocumentTotalAmount", invoiceAmount);
        }
        if (StringUtils.isNotEmpty(chartOfAccountsCode)) {
            fieldValues.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT_CODE, chartOfAccountsCode);
        }
        if (StringUtils.isNotEmpty(organizationCode)) {
            fieldValues.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, organizationCode);
        }
        Collection<ContractsGrantsInvoiceDocument> list = getContractsGrantsInvoiceDocumentService().retrieveAllCGInvoicesByCriteria(fieldValues);
        Collection<ContractsGrantsInvoiceDocument> finalList = new ArrayList<ContractsGrantsInvoiceDocument>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (ContractsGrantsInvoiceDocument item : list) {
            if (ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE.equals(item.getFinancialSystemDocumentHeader().getWorkflowDocumentTypeName())) {
                ContractsGrantsInvoiceDocument invoice = (ContractsGrantsInvoiceDocument)getDocumentService().getByDocumentHeaderId(item.getDocumentNumber());
                if (StringUtils.equals(item.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode(), DocumentStatus.FINAL.getCode())) {
                    if (StringUtils.isNotEmpty(userId)) {
                        Person person = getPersonService().getPersonByPrincipalName(userId);
                        if (person == null) {
                            throw new IllegalArgumentException("The parameter value for initiatorPrincipalName [" + userId + "] passed in does not map to a person.");
                        }
                        if (StringUtils.equalsIgnoreCase(item.getFinancialSystemDocumentHeader().getInitiatorPrincipalId(), person.getPrincipalId())) {
                            if (isInvoiceBetween(invoice, fromDate, toDate)) {
                                if (ObjectUtils.isNull(invoice.getDateReportProcessed()) || ObjectUtils.isNull(invoice.getMarkedForProcessing())) {
                                    finalList.add(invoice);
                                }
                            }
                        }
                    }
                    else if (isInvoiceBetween(invoice, fromDate, toDate)) {
                        if (ObjectUtils.isNull(invoice.getDateReportProcessed()) || ObjectUtils.isNull(invoice.getMarkedForProcessing())) {
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
    public boolean printInvoicesAndEnvelopesZip(Collection<ContractsGrantsInvoiceDocument> list, ByteArrayOutputStream baos) throws DocumentException, IOException {
        if (CollectionUtils.isNotEmpty(list)) {
            ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
            byte[] envelopes = reportService.generateListOfInvoicesEnvelopesPdfToPrint(list);
            byte[] report = reportService.generateListOfInvoicesPdfToPrint(list);
            boolean invoiceFileWritten = false;
            boolean envelopeFileWritten = false;

            ZipOutputStream zos = new ZipOutputStream(baos);
            int bytesRead;
            byte[] buffer = new byte[1024];
            CRC32 crc = new CRC32();

            if (ObjectUtils.isNotNull(report) && report.length > 0) {
                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(report));
                crc.reset();
                while ((bytesRead = bis.read(buffer)) != -1) {
                    crc.update(buffer, 0, bytesRead);
                }
                bis.close();
                // Reset to beginning of input stream
                bis = new BufferedInputStream(new ByteArrayInputStream(report));
                ZipEntry entry = new ZipEntry("Invoices-" + FILE_NAME_TIMESTAMP.format(new Date()) + ".pdf");
                entry.setMethod(ZipEntry.STORED);
                entry.setCompressedSize(report.length);
                entry.setSize(report.length);
                entry.setCrc(crc.getValue());
                zos.putNextEntry(entry);
                while ((bytesRead = bis.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytesRead);
                }
                bis.close();
                invoiceFileWritten = true;
            }

            if (ObjectUtils.isNotNull(envelopes) && envelopes.length > 0) {
                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(envelopes));
                crc.reset();
                while ((bytesRead = bis.read(buffer)) != -1) {
                    crc.update(buffer, 0, bytesRead);
                }
                bis.close();
                // Reset to beginning of input stream
                bis = new BufferedInputStream(new ByteArrayInputStream(envelopes));
                ZipEntry entry = new ZipEntry("InvoiceEnvelopes-" + FILE_NAME_TIMESTAMP.format(new Date()) + ".pdf");
                entry.setMethod(ZipEntry.STORED);
                entry.setCompressedSize(envelopes.length);
                entry.setSize(envelopes.length);
                entry.setCrc(crc.getValue());
                zos.putNextEntry(entry);
                while ((bytesRead = bis.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytesRead);
                }
                bis.close();
                envelopeFileWritten = true;
            }
            zos.close();
            return invoiceFileWritten || envelopeFileWritten;
        }
        return false;
    }

    /**
     * Returns FILE_NAME_TIMESTAMP
     * @see org.kuali.kfs.module.ar.service.InvoiceReportDeliveryService#getFileNameTimestampFormat()
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
