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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;

import edu.iu.uis.eden.exception.WorkflowException;

public class PaymentApplicationDocumentAction extends FinancialSystemTransactionalDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentAction.class);

    private DocumentService documentService;
    private DocumentTypeService documentTypeService;
    private WorkflowDocumentService workflowDocumentService;
    private PaymentApplicationDocumentService paymentApplicationDocumentService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private CustomerInvoiceDetailService customerInvoiceDetailService;
    private InvoicePaidAppliedService invoicePaidAppliedService;
    private NonAppliedHoldingService nonAppliedHoldingService;
    

    /**
     * Constructs a PaymentApplicationDocumentAction.java.
     */
    public PaymentApplicationDocumentAction() {
        super();
        documentService = SpringContext.getBean(DocumentService.class);
        documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
        paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        invoicePaidAppliedService = SpringContext.getBean(InvoicePaidAppliedService.class);
        nonAppliedHoldingService = SpringContext.getBean(NonAppliedHoldingService.class);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        PaymentApplicationDocumentForm form = (PaymentApplicationDocumentForm) kualiDocumentFormBase;
        PaymentApplicationDocument document = form.getPaymentApplicationDocument();

        // create new accounts receivable header and set it to the payment application document
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) kualiDocumentFormBase;
    }

    /**
     * This method applies detail amounts
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward apply(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PaymentApplicationDocumentForm applicationDocumentForm = (PaymentApplicationDocumentForm) form;
        PaymentApplicationDocument applicationDocument = applicationDocumentForm.getPaymentApplicationDocument();
        String applicationDocNbr = applicationDocument.getDocumentNumber();
        // get the university fiscal year and fiscal period code
        Integer universityFiscalYear = applicationDocument.getAccountingPeriod().getUniversityFiscalYear();
        String universityFiscalPeriodCode = applicationDocument.getAccountingPeriod().getUniversityFiscalPeriodCode();

        // get the list of customer invoice details to apply full amounts
        Object applyToInvoiceItems = request.getParameter("fullApply");
        List<String> invoiceItemNumbers = new ArrayList<String>();

        if (applyToInvoiceItems != null) {
            if (!applyToInvoiceItems.getClass().isArray()) {
                invoiceItemNumbers.add((String) applyToInvoiceItems);
            }
            else {
                invoiceItemNumbers.addAll(Arrays.asList((String[]) applyToInvoiceItems));
            }
        }


        ArrayList<CustomerInvoiceDetail> customerInvoiceDetails = new ArrayList<CustomerInvoiceDetail>(applicationDocumentForm.getCustomerInvoiceDetails());

        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            updateCustomerInvoiceDetailInfo(applicationDocumentForm, customerInvoiceDetail);
            Integer invoicePaidAppliedItemNbr = customerInvoiceDetail.getInvoicePaidApplieds().size() + 1;
            // if the customer invoice detail number is in the list of selected details to apply full amounts
            if (invoiceItemNumbers.indexOf(customerInvoiceDetail.getSequenceNumber().toString()) != -1) {
                // aply full amount for the detail
                //
                InvoicePaidApplied invoicePaidApplied = paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(customerInvoiceDetail, applicationDocNbr, universityFiscalYear, universityFiscalPeriodCode, customerInvoiceDetail.getAmount(), invoicePaidAppliedItemNbr);
                // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will not be null
                if (invoicePaidApplied != null) {
                    // add it to the payment application document list of applied payments
                    applicationDocument.getAppliedPayments().add(invoicePaidApplied);
                    customerInvoiceDetail.setAmountToBeApplied(customerInvoiceDetail.getAmount());
                }
            }
            else {
                // if the detail was note selected to apply full amount than check if amount to be applied is not zero and different than the total applied amount
                if (customerInvoiceDetail.getAmountToBeApplied().isNonZero() && (customerInvoiceDetail.getAppliedAmount().subtract(customerInvoiceDetail.getAmountToBeApplied())).isNonZero() && customerInvoiceDetail.getAmountToBeApplied().isLessEqual(customerInvoiceDetail.getAmount())) {
                    // apply the amount entered to the customer invoice detail
                    //TODO the amount to be applied should be computed (substract the amounts already applied and approved)
                    InvoicePaidApplied invoicePaidApplied = paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(customerInvoiceDetail, applicationDocNbr, universityFiscalYear, universityFiscalPeriodCode, customerInvoiceDetail.getAmountToBeApplied(), invoicePaidAppliedItemNbr);
                    // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will not be null
                    if (invoicePaidApplied != null) {
                        // add the new invoice paid applied to the payment application document list of applied payments
                        applicationDocument.getAppliedPayments().add(invoicePaidApplied);
                    }
                }
            }
            updateCustomerInvoiceDetailInfo(applicationDocumentForm, customerInvoiceDetail);
        }


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method applies amounts to the selected invoices
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward quickApply(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PaymentApplicationDocumentForm applicationDocumentForm = (PaymentApplicationDocumentForm) form;
        PaymentApplicationDocument pAppDoc = (PaymentApplicationDocument) applicationDocumentForm.getDocument();
        String paymentApplicationDocumentNbr = pAppDoc.getDocumentNumber();

        // get the university fiscal year and fiscal period code
        Integer universityFiscalYear = pAppDoc.getAccountingPeriod().getUniversityFiscalYear();
        String universityFiscalPeriodCode = pAppDoc.getAccountingPeriod().getUniversityFiscalPeriodCode();

        // get the list of invoices that were selected for quick invoice
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

        // go over the selected invoices and apply full amount to each of their details
        for (String invoiceNbr : invoiceNumbers) {
            // get the customer invoice details for the current invoice number
            Collection<CustomerInvoiceDetail> customerInvoiceDetails = getCustomerInvoiceDetailsForInvoice(applicationDocumentForm, invoiceNbr);
            for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
                updateCustomerInvoiceDetailInfo(applicationDocumentForm, customerInvoiceDetail);
                // next invoice paid applied number is the size of the appliedPayments list + 1
                Integer invoicePaidAppliedItemNbr = pAppDoc.getAppliedPayments().size() + 1;
                // apply the full amount for this detail
                // TODO change amount with balance in the future !!!
                InvoicePaidApplied invoicePaidApplied = paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(customerInvoiceDetail, paymentApplicationDocumentNbr, universityFiscalYear, universityFiscalPeriodCode, customerInvoiceDetail.getAmount(), invoicePaidAppliedItemNbr);
                // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will
                // not be null
                if (invoicePaidApplied != null) {
                    // add it to the payment application document list of applied payments
                    pAppDoc.getAppliedPayments().add(invoicePaidApplied);
                    // set the new applied amount for the customer invoice detail
                    customerInvoiceDetail.setAmountToBeApplied(customerInvoiceDetail.getAmount());
                }

            }
            if(invoiceNbr.equals(applicationDocumentForm.getEnteredInvoiceDocumentNumber()))
            {
                loadInvoice(applicationDocumentForm, invoiceNbr);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addNonAr(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        PaymentApplicationDocumentForm applicationDocumentForm = (PaymentApplicationDocumentForm) form;
        initializeForm(applicationDocumentForm);
        ActionForward actionForward = super.execute(mapping, form, request, response);

        return actionForward;
    }

    /**
     * This method initializes the form
     * 
     * @param applicationDocumentForm
     */
    private void initializeForm(PaymentApplicationDocumentForm applicationDocumentForm) {
      
    }

    /**
     * This method loads the invoices for currently selected customer
     * 
     * @param applicationDocumentForm
     */
    private void loadInvoices(PaymentApplicationDocumentForm applicationDocumentForm) {
        PaymentApplicationDocument applicationDocument = applicationDocumentForm.getPaymentApplicationDocument();
        String customerNumber = applicationDocument.getAccountsReceivableDocumentHeader() == null ? null : applicationDocument.getAccountsReceivableDocumentHeader().getCustomerNumber();
        if (customerNumber == null || customerNumber.equals("")) {
            String currentInvoiceNumber = applicationDocumentForm.getEnteredInvoiceDocumentNumber();
            if (currentInvoiceNumber != null && !currentInvoiceNumber.equals("")) {
                Customer customer = customerInvoiceDocumentService.getCustomerByInvoiceDocumentNumber(currentInvoiceNumber);
                customerNumber = customer.getCustomerNumber();
                applicationDocument.getAccountsReceivableDocumentHeader().setCustomerNumber(customerNumber);
            }
        }
        applicationDocumentForm.setInvoices(new ArrayList(customerInvoiceDocumentService.getCustomerInvoiceDocumentsByCustomerNumber(customerNumber)));
    }

    /**
     * This method updates the customer invoice details when a new invoice is selected
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward goToInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        String currentInvoiceNumber = pform.getSelectedInvoiceDocumentNumber();

        // set entered invoice number to be the current selected invoice number
        pform.setEnteredInvoiceDocumentNumber(currentInvoiceNumber);
        // load information for the current selected invoice
        loadInvoice(pform, currentInvoiceNumber);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method loads the information for the current selected invoice
     * 
     * @param currentInvoiceNumber
     */
    private void loadInvoice(PaymentApplicationDocumentForm pform, String currentInvoiceNumber) {

        CustomerInvoiceDocument invoice = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(currentInvoiceNumber);

        loadCustomerInvoiceDetails(pform, currentInvoiceNumber);

        pform.setSelectedInvoiceDocument(invoice);
        pform.setSelectedInvoiceTotalAmount(invoice.getSourceTotal());
        pform.setSelectedInvoiceBalance(invoice.getBalance());
        pform.setAmountAppliedDirectlyToInvoice(computeAmountAppliedToInvoice(pform, currentInvoiceNumber));

    }

    private KualiDecimal computeAmountAppliedToInvoice(PaymentApplicationDocumentForm pform, String currentInvoiceNumber) {

        KualiDecimal amountAppliedDirectlyToInvoice = KualiDecimal.ZERO;

        // calculate amount here

        return amountAppliedDirectlyToInvoice;

    }

    /**
     * This method updates customer invoice details when next invoice is selected
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward goToNextInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method updates customer invoice details when previous invoice is selected
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward goToPreviousInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method...
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward setCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method...
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadInvoices(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;

        loadInvoices(pform);

        String currentInvoiceNumber = pform.getEnteredInvoiceDocumentNumber();
        String customerNumber = pform.getPaymentApplicationDocument().getAccountsReceivableDocumentHeader().getCustomerNumber();

        // if no invoice number entered than get the first invoice
        if ((customerNumber != null && !customerNumber.equals("")) && (currentInvoiceNumber == null || "".equalsIgnoreCase(currentInvoiceNumber))) {
            currentInvoiceNumber = pform.getInvoices().iterator().next().getDocumentNumber();
            pform.setEnteredInvoiceDocumentNumber(currentInvoiceNumber);
        }
        // set the selected invoice to be the first one in the list
        pform.setSelectedInvoiceDocumentNumber(currentInvoiceNumber);

        if (currentInvoiceNumber != null && !currentInvoiceNumber.equals("")) {
            // load information for the current selected invoice
            loadInvoice(pform, currentInvoiceNumber);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method loads the customer invoice details for the current selected invoice
     * 
     * @param applicationDocumentForm
     */
    private void loadCustomerInvoiceDetails(PaymentApplicationDocumentForm applicationDocumentForm, String customerInvoiceDocumentNumber) {

        ArrayList<CustomerInvoiceDetail> customerInvoiceDetails = getCustomerInvoiceDetailsForInvoice(applicationDocumentForm, customerInvoiceDocumentNumber);
        applicationDocumentForm.setCustomerInvoiceDetails(customerInvoiceDetails);
    }
    
    private ArrayList<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(PaymentApplicationDocumentForm applicationDocumentForm,String customerInvoiceDocumentNumber){
        ArrayList<CustomerInvoiceDetail> customerInvoiceDetails = new ArrayList(customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoice(customerInvoiceDocumentNumber));

        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            updateCustomerInvoiceDetailInfo(applicationDocumentForm, customerInvoiceDetail);
        }
        return customerInvoiceDetails;
    }

    /**
     * This method update customer invoice detail information.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    private void updateCustomerInvoiceDetailInfo(PaymentApplicationDocumentForm applicationDocumentForm, CustomerInvoiceDetail customerInvoiceDetail) {
        // update information for customer invoice detail: update the list of invoice paid applieds, compute applied amount and balance(should be done in this order as
        // balance calculation depends on applied amount )
        updateCustomerInvoiceDetailAppliedPayments(applicationDocumentForm, customerInvoiceDetail);
        updateCustomerInvoiceDetailAppliedAmount(applicationDocumentForm, customerInvoiceDetail);
        updateCustomerInvoiceDetailBalance(applicationDocumentForm, customerInvoiceDetail);
        updateAmountAppliedOnDetail(applicationDocumentForm, customerInvoiceDetail);
    }

    /**
     * This method updates the applied amount for the given customer invoice detail.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    private void updateCustomerInvoiceDetailAppliedAmount(PaymentApplicationDocumentForm applicationDocumentForm, CustomerInvoiceDetail customerInvoiceDetail) {
        ArrayList<InvoicePaidApplied> invoicePaidApplieds = new ArrayList(customerInvoiceDetail.getInvoicePaidApplieds());
        KualiDecimal appliedAmount = KualiDecimal.ZERO;

        // TODO we might want to compute this based on the applied payments on this doc...
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        customerInvoiceDetail.setAppliedAmount(appliedAmount);
    }

    /**
     * This method updates the balance for the given customer invoice detail.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    private void updateCustomerInvoiceDetailBalance(PaymentApplicationDocumentForm applicationDocumentForm, CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal totalAmount = customerInvoiceDetail.getAmount();
        KualiDecimal appliedAmount = customerInvoiceDetail.getAppliedAmount();
        KualiDecimal balance = totalAmount.subtract(appliedAmount);
        customerInvoiceDetail.setBalance(balance);
    }
    
    /**
     * This method will update the list of the applied payments for this customer invoice detail taking into account the applied
     * payments on the form that are not yet saved in the db
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    private void updateCustomerInvoiceDetailAppliedPayments(PaymentApplicationDocumentForm applicationDocumentForm, CustomerInvoiceDetail customerInvoiceDetail) {
        String applicationDocNumber = applicationDocumentForm.getPaymentApplicationDocument().getDocumentNumber();

        // get the invoice paid applieds for this detail that where saved in the db for this app doc
        Collection<InvoicePaidApplied> detailInvPaidApplieds = invoicePaidAppliedService.getInvoicePaidAppliedsForCustomerInvoiceDetail(customerInvoiceDetail, applicationDocNumber);

        Collection<InvoicePaidApplied> invPaidAppliedsFormForThisDetail = getInvoicePaidAppliedsForDetail(applicationDocumentForm, customerInvoiceDetail);

        Collection<InvoicePaidApplied> invPaidAppliedsToBeAdded = new ArrayList<InvoicePaidApplied>();

        // go over the invoice paid applieds from the form for this detail and check if they are in the detail inv paid applieds list; if not add the in the invPaidAppliedsToBeAdded collection
        for (InvoicePaidApplied invoicePaidApplied2 : invPaidAppliedsFormForThisDetail) {
            boolean found = false;
            for (InvoicePaidApplied invoicePaidApplied1 : detailInvPaidApplieds) {

                String invoiceNumber1 = invoicePaidApplied1.getFinancialDocumentReferenceInvoiceNumber();
                String invoiceNumber2 = invoicePaidApplied2.getFinancialDocumentReferenceInvoiceNumber();
                Integer detailNumber1 = invoicePaidApplied1.getInvoiceItemNumber();
                Integer detailNumber2 = invoicePaidApplied2.getInvoiceItemNumber();
                Integer paidAppliedNumber1 = invoicePaidApplied1.getPaidAppliedItemNumber();
                Integer paidAppliedNumber2 = invoicePaidApplied2.getPaidAppliedItemNumber();

                if (invoiceNumber1.equals(invoiceNumber2) && detailNumber1.equals(detailNumber2) && paidAppliedNumber1.equals(paidAppliedNumber2)) {
                    found = true;
                    break;
                }

            }
            if (!found) {
                invPaidAppliedsToBeAdded.add(invoicePaidApplied2);
            }
        }

        detailInvPaidApplieds.addAll(invPaidAppliedsToBeAdded);

        customerInvoiceDetail.setInvoicePaidApplieds(detailInvPaidApplieds);
    }
    
    private Collection<InvoicePaidApplied> getInvoicePaidAppliedsForDetail (PaymentApplicationDocumentForm applicationDocumentForm, CustomerInvoiceDetail customerInvoiceDetail)
    {
        //get the invoice paid applieds from the form
        Collection<InvoicePaidApplied> invPaidAppliedsForm = applicationDocumentForm.getPaymentApplicationDocument().getAppliedPayments();
        Collection<InvoicePaidApplied> invPaidAppliedsFormForThisDetail = new ArrayList<InvoicePaidApplied>();

        // get the invoice paid applieds from the form for this detail
        for (InvoicePaidApplied invoicePaidApplied : invPaidAppliedsForm) {
            if (invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber().equals(customerInvoiceDetail.getDocumentNumber()) && invoicePaidApplied.getInvoiceItemNumber().equals(customerInvoiceDetail.getSequenceNumber())) {
                invPaidAppliedsFormForThisDetail.add(invoicePaidApplied);
            }
        }
        return invPaidAppliedsFormForThisDetail;
    }
    
    /**
     * This method updates amount to be applied on invoice detail
     * @param applicationDocumentForm
     */
    private void updateAmountAppliedOnDetail(PaymentApplicationDocumentForm applicationDocumentForm, CustomerInvoiceDetail customerInvoiceDetail) {
        Collection<InvoicePaidApplied> invoicePaidApplieds = getInvoicePaidAppliedsForDetail(applicationDocumentForm, customerInvoiceDetail);
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            customerInvoiceDetail.setAmountToBeApplied(invoicePaidApplied.getInvoiceItemAppliedAmount());
            //there should be actualy only one paid applied per detail
            break;
        }
    }

}
