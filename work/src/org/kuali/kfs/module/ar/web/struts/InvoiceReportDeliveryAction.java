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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.module.ar.service.InvoiceReportDeliveryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action class for Invoice Report Delivery.
 */
public class InvoiceReportDeliveryAction extends KualiAction {

    private static final String NO_DELIVERY_TYPE_SELECTED = "No delivery type selected. Please try again.";
    private static final String NO_PRINCIPAL_NAME_FOUND = "The Invoice Initiator Principal Name not found. Please try again.";
    private static final String NO_MATCHING_INVOICE = "No values match this search.";
    private static final String MARKED_FOR_PROCESSING_BY_BATCH_JOB = "Invoices successfully marked for processing for email delivery.";
    private static final String INVOICES_PRINT_SUCCESSFULL = "Invoices successfully generated for mail delivery.";
    private static final String INVOICES_PRINT_UNSUCCESSFULL = "No Invoices were generated.";

    protected static volatile InvoiceReportDeliveryService invoiceReportDeliveryService;

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
        irdForm.setDeliveryType(ArConstants.InvoiceTransmissionMethod.BOTH);
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
        // To validate the input fields before fetching invoices.
        if (ObjectUtils.isNotNull(irdForm.getFromDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
            Date testDate = null;
            try {
                testDate = sdf.parse(irdForm.getFromDate());
            }
            catch (Exception e) {
                irdForm.setMessage(NO_MATCHING_INVOICE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        if (ObjectUtils.isNotNull(irdForm.getToDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
            Date testDate = null;
            try {
                testDate = sdf.parse(irdForm.getToDate());
            }
            catch (Exception e) {
                irdForm.setMessage(NO_MATCHING_INVOICE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        if (ObjectUtils.isNotNull(irdForm.getInvoiceAmount())) {
            try {
                KualiDecimal invoiceAmount = new KualiDecimal(irdForm.getInvoiceAmount());
            }
            catch (Exception e) {
                irdForm.setMessage(NO_MATCHING_INVOICE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        if (StringUtils.isEmpty(deliveryType)) {
            irdForm.setMessage(NO_DELIVERY_TYPE_SELECTED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        // Check principal name if entered
        String principalName = irdForm.getUserId();
        if (StringUtils.isNotEmpty(principalName)) {
            Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(principalName);
            if (ObjectUtils.isNull(person)) {
                irdForm.setMessage(NO_PRINCIPAL_NAME_FOUND);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        // Fetch the invoices with the input parameters
        Collection<ContractsGrantsInvoiceDocument> list = getInvoiceReportDeliveryService().getInvoicesByParametersFromRequest(irdForm.getDocumentNumber(),irdForm.getUserId(),irdForm.getProposalNumber(),irdForm.getInvoiceAmount(),irdForm.getChartCode(),irdForm.getOrgCode(),irdForm.getToDate(),irdForm.getFromDate());

        if (CollectionUtils.isNotEmpty(list)) {
            // mapping to return to once delivery method is processed
            ActionForward forward = new ActionForward();
            // Buffer for message to display on the form
            StringBuffer statusMessage = new StringBuffer();

            // Check delivery type for EMAIL (or BOTH)
            if (ArConstants.InvoiceTransmissionMethod.EMAIL.equalsIgnoreCase(deliveryType)
                    || ArConstants.InvoiceTransmissionMethod.BOTH.equalsIgnoreCase(deliveryType)) {

                Collection<ContractsGrantsInvoiceDocument> emailList = new ArrayList<ContractsGrantsInvoiceDocument>();
                Set<ContractsGrantsInvoiceDocument> emailSet = new HashSet<ContractsGrantsInvoiceDocument>();

                // Get all email-able invoices
                for (ContractsGrantsInvoiceDocument invoice : list) {
                    if (ObjectUtils.isNull(invoice.getMarkedForProcessing())) {
                        for (InvoiceAddressDetail invoiceAddressDetail : invoice.getInvoiceAddressDetails()) {
                            if (ArConstants.InvoiceTransmissionMethod.EMAIL.equals(invoiceAddressDetail.getInvoiceTransmissionMethodCode())) {
                                emailSet.add(invoice);
                            }
                        }
                    }
                }
                emailList.addAll(emailSet);

                // mark invoices to be emailed for processing
                if (CollectionUtils.isNotEmpty(emailList)) {
                    forward = emailInvoicePDF(mapping, irdForm, emailList);
                    statusMessage.append(MARKED_FOR_PROCESSING_BY_BATCH_JOB);
                    statusMessage.append("\n");
                }
                else {
                    // if no invoices to be emailed were found and EMAIL was the option, add status message
                    if(ArConstants.InvoiceTransmissionMethod.EMAIL.equalsIgnoreCase(deliveryType)){
                        statusMessage.append(NO_MATCHING_INVOICE);
                        statusMessage.append("\n");
                    }
                    // set forward to basic path
                    forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }

            // Check delivery type for MAIL (or BOTH)
            if (ArConstants.InvoiceTransmissionMethod.MAIL.equalsIgnoreCase(deliveryType)
                    || ArConstants.InvoiceTransmissionMethod.BOTH.equalsIgnoreCase(deliveryType)) {

                // Get all mail-able invoices
                Collection<ContractsGrantsInvoiceDocument> mailList = new ArrayList<ContractsGrantsInvoiceDocument>();
                Set<ContractsGrantsInvoiceDocument> mailSet = new HashSet<ContractsGrantsInvoiceDocument>();
                for (ContractsGrantsInvoiceDocument invoice : list) {
                    if (ObjectUtils.isNull(invoice.getDateReportProcessed())) {
                        for (InvoiceAddressDetail invoiceAddressDetail : invoice.getInvoiceAddressDetails()) {
                            if (ArConstants.InvoiceTransmissionMethod.MAIL.equals(invoiceAddressDetail.getInvoiceTransmissionMethodCode())) {
                                mailSet.add(invoice);
                            }
                        }
                    }
                }
                mailList.addAll(mailSet);

                // Process mailable invoices found
                if (CollectionUtils.isNotEmpty(mailList)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (getInvoiceReportDeliveryService().printInvoicesAndEnvelopesZip(mailList, baos)) {
                        response.setContentType("application/zip");
                        response.setHeader("Content-disposition", "attachment; filename=Invoice-report" + getInvoiceReportDeliveryService().getFileNameTimestampFormat().format(new Date()) + ".zip");
                        response.setHeader("Expires", "0");
                        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                        response.setHeader("Pragma", "public");
                        response.setContentLength(baos.size());
                        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                        IOUtils.copy(bais, response.getOutputStream());
                        response.getOutputStream().flush();
                        statusMessage.append(INVOICES_PRINT_SUCCESSFULL);
                        statusMessage.append("\n");
                        irdForm.setMessage(statusMessage.toString());

                        return null;
                    }
                    else {
                        statusMessage.append(INVOICES_PRINT_UNSUCCESSFULL);
                        statusMessage.append("\n");
                        forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                    }
                }
                else {
                    statusMessage.append(NO_MATCHING_INVOICE);
                    statusMessage.append("\n");
                    forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }

            irdForm.setMessage(statusMessage.toString());
            return forward;
        }
        // Catch all
        irdForm.setMessage(NO_MATCHING_INVOICE);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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

    protected InvoiceReportDeliveryService getInvoiceReportDeliveryService() {
        if (invoiceReportDeliveryService == null) {
            invoiceReportDeliveryService = SpringContext.getBean(InvoiceReportDeliveryService.class);
        }
        return invoiceReportDeliveryService;
    }
}
