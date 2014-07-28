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

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.TransmitContractsAndGrantsInvoicesLookupDataHolder;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.service.TransmitContractsAndGrantsInvoicesService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Action class for Transmit Contracts & Grants Invoices.
 */
public class TransmitContractsAndGrantsInvoicesLookupAction extends ContractsGrantsReportLookupAction {

    protected static volatile TransmitContractsAndGrantsInvoicesService invoiceReportDeliveryService;

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
    @Override
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TransmitContractsAndGrantsInvoicesLookupForm lookupForm = (TransmitContractsAndGrantsInvoicesLookupForm) form;
        String basePath = getApplicationBaseUrl();
        Map fieldValues = lookupForm.getFieldsForLookup();

        String invoiceTransmissionMethodCode = (String) fieldValues.get(ArPropertyConstants.INVOICE_TRANSMISSION_METHOD_CODE);

        getTransmitContractsAndGrantsInvoicesService().validateSearchParameters(fieldValues);

        // Fetch the invoices with the input parameters
        Collection<ContractsGrantsInvoiceDocument> list = getTransmitContractsAndGrantsInvoicesService().getInvoicesByParametersFromRequest(fieldValues);

        if (CollectionUtils.isNotEmpty(list)) {
            // mapping to return to once delivery method is processed
            ActionForward forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            Set<ContractsGrantsInvoiceDocument> emailSet = new HashSet<ContractsGrantsInvoiceDocument>();
            Set<ContractsGrantsInvoiceDocument> mailSet = new HashSet<ContractsGrantsInvoiceDocument>();

            // Check delivery type for EMAIL
            if (ArConstants.InvoiceTransmissionMethod.EMAIL.equalsIgnoreCase(invoiceTransmissionMethodCode)) {
                // Get all email-able invoices
                for (ContractsGrantsInvoiceDocument invoice : list) {
                    if (getTransmitContractsAndGrantsInvoicesService().isInvoiceValidToEmail(invoice)) {
                        emailSet.add(invoice);
                    }
                }
                if (CollectionUtils.isNotEmpty(emailSet)) {
                    if (getTransmitContractsAndGrantsInvoicesService().sendEmailForListofInvoicesToAgency(emailSet)) {
                        GlobalVariables.getMessageMap().putInfoForSectionId(ArPropertyConstants.LOOKUP_SECTION_ID, ArKeyConstants.INVOICE_EMAILS_SENT);
                    } else {
                        GlobalVariables.getMessageMap().putInfoForSectionId(ArPropertyConstants.LOOKUP_SECTION_ID, ArKeyConstants.ERROR_SENDING_INVOICE_EMAILS);
                    }
                }
                else {
                    // this is so the message about no search results shows up in the same place as when doing a search
                    request.setAttribute(KFSPropertyConstants.REQUEST_SEARCH_RESULTS_ACTUAL_SIZE, 0 );
                }
            } else {
                // Get all mail-able invoices
                for (ContractsGrantsInvoiceDocument invoice : list) {
                    if (getTransmitContractsAndGrantsInvoicesService().isInvoiceValidToMail(invoice)) {
                        mailSet.add(invoice);
                    }
                }

                // Process mailable invoices found
                if (CollectionUtils.isNotEmpty(mailSet)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (getTransmitContractsAndGrantsInvoicesService().printInvoicesAndEnvelopesZip(mailSet, baos)) {
                        String fileName = ArConstants.INVOICE_ZIP_FILE_PREFIX + getTransmitContractsAndGrantsInvoicesService().getFileNameTimestampFormat().format(new Date()) + KFSConstants.ReportGeneration.ZIP_FILE_EXTENSION;
                        WebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.ZIP_MIME_TYPE, baos, fileName);
                        GlobalVariables.getMessageMap().putInfoForSectionId(ArPropertyConstants.LOOKUP_SECTION_ID, ArKeyConstants.INVOICES_PRINT_SUCCESSFULL);
                        request.setAttribute(KFSPropertyConstants.REQUEST_SEARCH_RESULTS_ACTUAL_SIZE, mailSet.size());

                        return null;
                    }
                    else {
                        GlobalVariables.getMessageMap().putInfoForSectionId(ArPropertyConstants.LOOKUP_SECTION_ID, ArKeyConstants.INVOICES_PRINT_UNSUCCESSFULL);
                    }
                }
                else {
                    // if no invoices to be mailed were found add status message
                    // this is so the message about no search results shows up in the same place as when doing a search
                    request.setAttribute(KFSPropertyConstants.REQUEST_SEARCH_RESULTS_ACTUAL_SIZE, 0 );
                }
            }
            return forward;
        }
        // Catch all
        // this is so the message about no search results shows up in the same place as when doing a search
        request.setAttribute(KFSPropertyConstants.REQUEST_SEARCH_RESULTS_ACTUAL_SIZE, 0 );

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected TransmitContractsAndGrantsInvoicesService getTransmitContractsAndGrantsInvoicesService() {
        if (invoiceReportDeliveryService == null) {
            invoiceReportDeliveryService = SpringContext.getBean(TransmitContractsAndGrantsInvoicesService.class);
        }
        return invoiceReportDeliveryService;
    }

    @Override
    protected String getSortFieldName() {
        return null;
    }

    @Override
    public String getReportBuilderServiceBeanName() {
        return null;
    }

    @Override
    public Class<? extends BusinessObject> getPrintSearchCriteriaClass() {
        return TransmitContractsAndGrantsInvoicesLookupDataHolder.class;
    }

    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        return null;
    }
}
