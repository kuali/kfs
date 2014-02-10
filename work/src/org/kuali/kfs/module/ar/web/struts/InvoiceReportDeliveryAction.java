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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

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
        String basePath = getApplicationBaseUrl();
        String deliveryType = irdForm.getDeliveryType();
        //To validate the input fields before fetching invoices.
        if (ObjectUtils.isNotNull(irdForm.getFromDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
            Date testDate = null;
            try{
                testDate = sdf.parse(irdForm.getFromDate());
            }
            catch(Exception e){
                irdForm.setMessage(NO_MATCHING_INVOICE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        if (ObjectUtils.isNotNull(irdForm.getToDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
            Date testDate = null;
            try{
                testDate = sdf.parse(irdForm.getToDate());
            }
            catch(Exception e){
                irdForm.setMessage(NO_MATCHING_INVOICE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        if (ObjectUtils.isNotNull(irdForm.getInvoiceAmount())) {
            try{
                KualiDecimal invoiceAmount =  new KualiDecimal(irdForm.getInvoiceAmount());
            }
            catch(Exception e){
                irdForm.setMessage(NO_MATCHING_INVOICE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        if (StringUtils.isEmpty(deliveryType)) {
            irdForm.setMessage(NO_DELIVERY_TYPE_SELECTED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // Fetch the invoices with the input parameters
        Collection<ContractsGrantsInvoiceDocument> list = this.getInvoicesByParametersFromRequest(irdForm);

        if (CollectionUtils.isNotEmpty(list)) {
            if (ArConstants.InvoiceIndicator.MAIL.equals(deliveryType)) {
                Collection<ContractsGrantsInvoiceDocument> mailList = new ArrayList<ContractsGrantsInvoiceDocument>();
                Set<ContractsGrantsInvoiceDocument> mailSet = new HashSet<ContractsGrantsInvoiceDocument>();
                for(ContractsGrantsInvoiceDocument invoice: list){
                    if(ObjectUtils.isNull(invoice.getDateReportProcessed())){
                        for(InvoiceAddressDetail invoiceAddressDetail: invoice.getInvoiceAddressDetails()){
                            if(ArConstants.InvoiceIndicator.MAIL.equals(invoiceAddressDetail.getPreferredInvoiceIndicatorCode())){
                                mailSet.add(invoice);
                            }
                        }


                    }
                }
                mailList.addAll(mailSet);
                if(CollectionUtils.isNotEmpty(mailList)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (this.printInvoicesAndEnvelopesZip(mapping, irdForm, mailList, baos)) {
                    response.setContentType("application/zip");
                    response.setHeader("Content-disposition", "attachment; filename=Invoice-report" + FILE_NAME_TIMESTAMP.format(new Date()) + ".zip");
                    response.setHeader("Expires", "0");
                    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                    response.setHeader("Pragma", "public");
                    response.setContentLength(baos.size());
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
                else{
                    irdForm.setMessage(NO_MATCHING_INVOICE);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }
            else if (ArConstants.InvoiceIndicator.EMAIL.equals(deliveryType)){
                Collection<ContractsGrantsInvoiceDocument> emailList = new ArrayList<ContractsGrantsInvoiceDocument>();
                Set<ContractsGrantsInvoiceDocument> emailSet = new HashSet<ContractsGrantsInvoiceDocument>();
                for(ContractsGrantsInvoiceDocument invoice: list){
                    if(ObjectUtils.isNull(invoice.getMarkedForProcessing())){
                        for(InvoiceAddressDetail invoiceAddressDetail: invoice.getInvoiceAddressDetails()){
                            if(ArConstants.InvoiceIndicator.EMAIL.equals(invoiceAddressDetail.getPreferredInvoiceIndicatorCode())){
                                emailSet.add(invoice);
                            }
                        }


                    }
                }
                emailList.addAll(emailSet);
                if(CollectionUtils.isNotEmpty(emailList)) {
                return emailInvoicePDF(mapping, irdForm, emailList);
                }
                else{
                    irdForm.setMessage(NO_MATCHING_INVOICE);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
        SimpleDateFormat reqDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Timestamp fromDate = null;
        Timestamp toDate  = null;
        if(StringUtils.isNotEmpty(form.getToDate())){
        toDate = Timestamp.valueOf(reqDateFormat.format(dateFormat.parse(form.getToDate())));
        }
        if(StringUtils.isNotEmpty(form.getFromDate())){
            fromDate = Timestamp.valueOf(reqDateFormat.format(dateFormat.parse(form.getFromDate())));
        }
        String user = form.getUserId();
        ContractsGrantsInvoiceDocumentService invoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        Map<String,String> fieldValues = new HashMap<String,String>();
        if (StringUtils.isNotEmpty(form.getProposalNumber())) {
            fieldValues.put("proposalNumber", form.getProposalNumber());
        }
        if (StringUtils.isNotEmpty(form.getDocumentNumber())) {
            fieldValues.put("documentNumber", form.getDocumentNumber());
        }
        if (ObjectUtils.isNotNull(form.getInvoiceAmount())) {
            fieldValues.put("documentHeader.financialDocumentTotalAmount", form.getInvoiceAmount());
        }
        if (StringUtils.isNotEmpty(form.getChartCode())) {
            fieldValues.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT_CODE, form.getChartCode());
        }
        if (StringUtils.isNotEmpty(form.getOrgCode())) {
            fieldValues.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, form.getOrgCode());
        }
        Collection<ContractsGrantsInvoiceDocument> list = invoiceDocumentService.retrieveAllCGInvoicesByCriteria(fieldValues);
        Collection<ContractsGrantsInvoiceDocument> finalList = new ArrayList<ContractsGrantsInvoiceDocument>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (ContractsGrantsInvoiceDocument item : list) {
            Document document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(item.getDocumentNumber());
            if (ArConstants.CGIN_DOCUMENT_TYPE.equals(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName())) {
                ContractsGrantsInvoiceDocument invoice = (ContractsGrantsInvoiceDocument) document;
                if (invoice.getDocumentHeader().getWorkflowDocument().isFinal()) {
                    if (StringUtils.isNotEmpty(user)) {
                        Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(user);
                        if (person == null) {
                            throw new IllegalArgumentException("The parameter value for initiatorPrincipalName [" + user + "] passed in does not map to a person.");
                        }
                        if(StringUtils.equalsIgnoreCase( invoice.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId(), person.getPrincipalId())){
//                        if (invoice.getDocumentHeader().getWorkflowDocument().userIsInitiator(person)){
                        }
                        if (invoice.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId().equals(person.getPrincipalId())){
                            if (this.isInvoiceBetween(invoice, fromDate, toDate)) {
                                if (ObjectUtils.isNull(invoice.getDateReportProcessed()) || ObjectUtils.isNull(invoice.getMarkedForProcessing())){
                                    finalList.add(invoice);
                            }
                            }
                        }
                    }
                    else if (this.isInvoiceBetween(invoice, fromDate, toDate)){
                        if (ObjectUtils.isNull(invoice.getDateReportProcessed()) || ObjectUtils.isNull(invoice.getMarkedForProcessing())){
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
     *
     * @param invoice
     * @param fromDate
     * @param toDate
     * @return
     */
    private boolean isInvoiceBetween(ContractsGrantsInvoiceDocument invoice, Timestamp fromDate, Timestamp toDate) {
        if (ObjectUtils.isNotNull(fromDate)){
            if (fromDate.after(new Timestamp(invoice.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis()))){
                return false;
            }
        }
        if (ObjectUtils.isNotNull(toDate)){
            if (toDate.before(new Timestamp(invoice.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis()))){
                return false;
            }
        }
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
