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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;

public class PaymentApplicationDocumentAction extends KualiAccountingDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentAction.class);

    private DocumentService documentService;
    private DocumentTypeService documentTypeService;
    private WorkflowDocumentService workflowDocumentService;
    private PaymentApplicationDocumentService paymentApplicationDocumentService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private InvoicePaidAppliedService invoicePaidAppliedService;

    public PaymentApplicationDocumentAction() {
        super();
        documentService = SpringContext.getBean(DocumentService.class);
        documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
        paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        invoicePaidAppliedService = SpringContext.getBean(InvoicePaidAppliedService.class);
    }

    /**
     * This method applies detail amounts
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward apply(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean trap = true;

        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        PaymentApplicationDocument pAppDoc = (PaymentApplicationDocument) pform.getDocument();
        String paymentApplicationDocumentNbr = pAppDoc.getDocumentNumber();
        Integer universityFiscalYear = pAppDoc.getAccountingPeriod().getUniversityFiscalYear();
        String universityFiscalPeriodCode = pAppDoc.getAccountingPeriod().getUniversityFiscalPeriodCode();
        Object applyToInvoiceItems = request.getParameter("fullApply");
        List<String> invoiceItemNumbers = new ArrayList<String>();
        String selectedInvoice = pform.getSelectedInvoiceDocumentNumber();

        if (applyToInvoiceItems != null) {
            if (!applyToInvoiceItems.getClass().isArray()) {
                invoiceItemNumbers.add((String) applyToInvoiceItems);
            }
            else {
                invoiceItemNumbers.addAll(Arrays.asList((String[]) applyToInvoiceItems));
            }
        }

        Collection<CustomerInvoiceDetail> customerInvoiceDetails = pform.getCustomerInvoiceDetailsForSelectedCustomerInvoiceDocument();
        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {

            Object amountToBeApplied = request.getParameter("amountToBeApplied_" + customerInvoiceDetail.getSequenceNumber());
            KualiDecimal amount = new KualiDecimal(0);
            if (amountToBeApplied != null) {
                amount = new KualiDecimal((String) amountToBeApplied);
            }


            if (invoiceItemNumbers.indexOf(customerInvoiceDetail.getSequenceNumber().toString()) != -1) {
                InvoicePaidApplied invoicePaidApplied = paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(customerInvoiceDetail, paymentApplicationDocumentNbr, universityFiscalYear, universityFiscalPeriodCode, customerInvoiceDetail.getAmount());
                if (invoicePaidApplied != null) {
                    invoicePaidApplied.setDocumentNumber(pAppDoc.getDocumentNumber());
                    pAppDoc.getAppliedPayments().add(invoicePaidApplied);
                }
            }
            else {
                if (amount.isNonZero() && (amount.subtract(customerInvoiceDetail.getAppliedAmount())).isNonZero()) {
                    InvoicePaidApplied invoicePaidApplied = paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(customerInvoiceDetail, paymentApplicationDocumentNbr, universityFiscalYear, universityFiscalPeriodCode, amount);
                    if (invoicePaidApplied != null) {
                        invoicePaidApplied.setDocumentNumber(pAppDoc.getDocumentNumber());
                        pAppDoc.getAppliedPayments().add(invoicePaidApplied);
                    }
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method applies amounts to the selected invoices 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward quickApply(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean trap = true;
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        PaymentApplicationDocument pAppDoc = (PaymentApplicationDocument) pform.getDocument();
        String paymentApplicationDocumentNbr = pAppDoc.getDocumentNumber();
        Integer universityFiscalYear = pAppDoc.getAccountingPeriod().getUniversityFiscalYear();
        String universityFiscalPeriodCode = pAppDoc.getAccountingPeriod().getUniversityFiscalPeriodCode();

        Object applyToInvoices = request.getParameter("quickApply");

        List<String> invoiceNumbers = new ArrayList<String>();
        if (applyToInvoices != null) {
            if (!applyToInvoices.getClass().isArray()) {
                invoiceNumbers.add((String) applyToInvoices);
            }
            else {
                invoiceNumbers.addAll(Arrays.asList((String[]) applyToInvoices));
            }
        }

        for (String invoiceNbr : invoiceNumbers) {
            Collection<CustomerInvoiceDetail> customerInvoiceDetails = pform.getCustomerInvoiceDetailsForCustomerInvoiceDocumentNbr(invoiceNbr);
            for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {

                InvoicePaidApplied invoicePaidApplied = paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(customerInvoiceDetail, paymentApplicationDocumentNbr, universityFiscalYear, universityFiscalPeriodCode, customerInvoiceDetail.getAmount());
                if (invoicePaidApplied != null) {
                    pAppDoc.getAppliedPayments().add(invoicePaidApplied);
                }
            }
        }


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addNonAr(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean trap = true;

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        return super.execute(mapping, form, request, response);
    }

    /**
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward goToInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        pform.setSelectedInvoiceDocumentNumber(request.getParameter("goToInvoiceDocumentNumber"));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward setCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        Collection<NonAppliedHolding> holdings = pform.getNonAppliedHoldingsForCustomer();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
