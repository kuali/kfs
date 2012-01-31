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
package org.kuali.kfs.module.ar.web.struts;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * Action class for Invoice Report Delivery.
 */
public class InvoiceReportDeliveryAction extends KualiAction {

    private static final String NO_DELIVERY_TYPE_SELECTED = "No delivery type selected. Please try again.";
    private static final String NO_MATCHING_INVOICE = "No CG Invoice Documents match your search.";
    private static final String MARKED_FOR_PROCESSING_BY_BATCH_JOB = "Invoices successfully marked for processing for email delivery.";
    private static final String INVOICES_PRINT_SUCCESSFULL = "Invoices successfully generated for mail delivery.";
    private static final String INVOICES_PRINT_UNSUCCESSFULL = "No Invoices were generated.";
    private static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("_yyyy-MM-dd_hhmmss");

    /**
     * Constructs a InvoiceReportDeliveryAction.java.
     */
    public InvoiceReportDeliveryAction() {
        super();
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InvoiceReportDeliveryForm irdForm = (InvoiceReportDeliveryForm) form;
        irdForm.setDocumentNumber(null);
        irdForm.setChartCode(null);
        irdForm.setOrgCode(null);
        irdForm.setUserId(null);
        irdForm.setToDate(null);
        irdForm.setFromDate(null);
        irdForm.setDeliveryType(null);
        irdForm.setInvoiceAmount(null);
        irdForm.setProposalNumber(null);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method forwards the print request according to the selections.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InvoiceReportDeliveryForm irdForm = (InvoiceReportDeliveryForm) form;
        String basePath = getBasePath(request);
        String deliveryType = irdForm.getDeliveryType();
        Collection<ContractsGrantsInvoiceDocument> list = this.getInvoicesByParametersFromRequest(irdForm);
        if (StringUtils.isEmpty(deliveryType)) {
            irdForm.setMessage(NO_DELIVERY_TYPE_SELECTED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        if (CollectionUtils.isNotEmpty(list)) {
            if (ArConstants.InvoiceIndicator.MAIL.equals(deliveryType)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (this.printInvoicesAndEnvelopesZip(mapping, irdForm, list, baos)) {
                    response.setContentType("application/zip");
                    response.setHeader("Content-disposition", "attachment; filename=Invoice-report" + FILE_NAME_TIMESTAMP.format(new Date()) + ".zip");
                    response.setHeader("Expires", "0");
                    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                    response.setHeader("Pragma", "public");
                    response.setContentLength((int) baos.size());
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    IOUtils.copy(bais, response.getOutputStream());
                    response.getOutputStream().flush();
                    irdForm.setMessage(INVOICES_PRINT_SUCCESSFULL);
                    return null;
                }
                else {
                    irdForm.setMessage(INVOICES_PRINT_UNSUCCESSFULL);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }
            else if (ArConstants.InvoiceIndicator.EMAIL.equals(deliveryType))
                return emailInvoicePDF(mapping, irdForm, list);
        }
        irdForm.setMessage(NO_MATCHING_INVOICE);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Returns the list of invoices that match the search criteria.
     * 
     * @param form
     * @return collections of Contracts and Grants Invoice Document
     * @throws WorkflowException
     * @throws ParseException
     */
    private Collection<ContractsGrantsInvoiceDocument> getInvoicesByParametersFromRequest(InvoiceReportDeliveryForm form) throws WorkflowException, ParseException {
        Date toDate = form.getToDate();
        Date fromDate = form.getFromDate();
        String user = form.getUserId();
        ContractsGrantsInvoiceDocumentService invoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(form.getProposalNumber()))
            criteria.addEqualTo("proposalNumber", form.getProposalNumber());
        if (StringUtils.isNotEmpty(form.getDocumentNumber()))
            criteria.addEqualTo("documentNumber", form.getDocumentNumber());
        if (ObjectUtils.isNotNull(form.getInvoiceAmount()))
            criteria.addEqualTo("documentHeader.financialDocumentTotalAmount", form.getInvoiceAmount());
        if (StringUtils.isNotEmpty(form.getChartCode()))
            criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT_CODE, form.getChartCode());
        if (StringUtils.isNotEmpty(form.getChartCode()))
            criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, form.getChartCode());
        Collection<ContractsGrantsInvoiceDocument> list = invoiceDocumentService.retrieveAllCGInvoicesByCriteria(criteria);
        Collection<ContractsGrantsInvoiceDocument> finalList = new ArrayList<ContractsGrantsInvoiceDocument>();
        if (CollectionUtils.isEmpty(list))
            return null;
        for (ContractsGrantsInvoiceDocument item : list) {
            Document document = KNSServiceLocator.getDocumentService().getByDocumentHeaderId(item.getDocumentNumber());
            if (ArConstants.CGIN_DOCUMENT_TYPE.equals(document.getDocumentHeader().getWorkflowDocument().getDocumentType())) {
                ContractsGrantsInvoiceDocument invoice = (ContractsGrantsInvoiceDocument) document;
                if (invoice.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                    if (StringUtils.isNotEmpty(user)) {
                        Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(user);
                        if (person == null)
                            throw new IllegalArgumentException("The parameter value for initiatorPrincipalName [" + user + "] passed in does not map to a person.");
                        if (invoice.getDocumentHeader().getWorkflowDocument().userIsInitiator(person))
                            if (this.isInvoiceBetween(invoice, fromDate, toDate)) {
                                if (ObjectUtils.isNull(invoice.getDateReportProcessed()) && !invoice.isMarkedForProcessing())
                                    finalList.add(invoice);
                            }
                    }
                    else if (this.isInvoiceBetween(invoice, fromDate, toDate))
                        if (ObjectUtils.isNull(invoice.getDateReportProcessed()) && !invoice.isMarkedForProcessing())
                            finalList.add(invoice);
                }
            }
        }
        return finalList;
    }

    /**
     * Checks whether invoice is between the dates provided.
     * 
     * @param invoice
     * @param fromDate
     * @param toDate
     * @return
     */
    private boolean isInvoiceBetween(ContractsGrantsInvoiceDocument invoice, Date fromDate, Date toDate) {
        if (ObjectUtils.isNotNull(fromDate))
            if (fromDate.after(invoice.getDocumentHeader().getWorkflowDocument().getCreateDate()))
                return false;
        if (ObjectUtils.isNotNull(toDate))
            if (toDate.before(invoice.getDocumentHeader().getWorkflowDocument().getCreateDate()))
                return false;
        return true;
    }

    /**
     * Marks the invoices for email delivery.
     * 
     * @param mapping
     * @param irdForm
     * @param list
     * @return
     * @throws Exception
     */
    public ActionForward emailInvoicePDF(ActionMapping mapping, InvoiceReportDeliveryForm irdForm, Collection<ContractsGrantsInvoiceDocument> list) throws Exception {
        if (CollectionUtils.isNotEmpty(list)) {
            ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
            reportService.sendEmailForListofInvoicesToAgency(list);
        }
        irdForm.setMessage(MARKED_FOR_PROCESSING_BY_BATCH_JOB);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method generates the actual pdf files to print.
     * 
     * @param mapping
     * @param form
     * @param list
     * @return
     * @throws Exception
     */
    public boolean printInvoicesAndEnvelopesZip(ActionMapping mapping, InvoiceReportDeliveryForm form, Collection<ContractsGrantsInvoiceDocument> list, ByteArrayOutputStream baos) throws Exception {
        if (CollectionUtils.isNotEmpty(list)) {
            ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
            byte[] envelopes = reportService.generateListOfInvoicesEnvelopesPdfToPrint(list);
            byte[] report = reportService.generateListOfInvoicesPdfToPrint(list);

            ZipOutputStream zos = new ZipOutputStream(baos);
            int bytesRead;
            byte[] buffer = new byte[1024];
            CRC32 crc = new CRC32();

            if (ObjectUtils.isNotNull(report)) {
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
            }

            if (ObjectUtils.isNotNull(envelopes)) {
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
            }
            zos.close();
            return true;
        }
        return false;
    }
}
