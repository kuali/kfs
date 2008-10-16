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
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.validation.impl.PaymentApplicationDocumentRuleUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

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
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
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
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) kualiDocumentFormBase;
        loadInvoices(pform);
    }

    private void addFieldError(String propertyName, String errorKey) {
        GlobalVariables.getErrorMap().putError(propertyName, errorKey);
    }
    
    private void addGlobalError(String errorKey) {
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, errorKey);
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
            } else {
                invoiceItemNumbers.addAll(Arrays.asList((String[]) applyToInvoiceItems));
            }
        }

        Collection<CustomerInvoiceDetail> customerInvoiceDetails = applicationDocumentForm.getCustomerInvoiceDetails();
            //new ArrayList<CustomerInvoiceDetail>(applicationDocumentForm.getCustomerInvoiceDetails());
        PaymentApplicationDocumentService paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        
        // KULAR-414
        boolean amountToBeAppliedIsValid = 
            PaymentApplicationDocumentRuleUtil.validateAmountToBeApplied(customerInvoiceDetails);
        
        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            paymentApplicationDocumentService.updateCustomerInvoiceDetailInfo(
                applicationDocumentForm.getPaymentApplicationDocument(), customerInvoiceDetail);
            Integer invoicePaidAppliedItemNbr = applicationDocument.getAppliedPayments().size() + 1;
            // if the customer invoice detail number is in the list of selected details to apply full amounts
            if (invoiceItemNumbers.indexOf(customerInvoiceDetail.getSequenceNumber().toString()) != -1) {
                // apply full amount for the detail
                InvoicePaidApplied invoicePaidApplied = 
                    paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(
                        customerInvoiceDetail, applicationDocNbr, universityFiscalYear, universityFiscalPeriodCode, 
                        customerInvoiceDetail.getOpenAmount(), invoicePaidAppliedItemNbr);
                // if there was not another invoice paid applied already created for the current 
                // detail then invoicePaidApplied will not be null
                if (invoicePaidApplied != null) {
                    // add it to the payment application document list of applied payments
                    if(amountToBeAppliedIsValid) {
                        applicationDocument.getAppliedPayments().add(invoicePaidApplied);
                    }
                    customerInvoiceDetail.setAmountToBeApplied(customerInvoiceDetail.getAmount());
                }
            } else {
                KualiDecimal invoiceDetailAmountToBeApplied = customerInvoiceDetail.getAmountToBeApplied();
                KualiDecimal invoiceDetailOpenAmount = customerInvoiceDetail.getOpenAmount();
                
                boolean invoiceDetailAmountToBeAppliedIsLessEqualThanOpenAmount = 
                    invoiceDetailAmountToBeApplied.isLessEqual(invoiceDetailOpenAmount);
                if(!invoiceDetailAmountToBeAppliedIsLessEqualThanOpenAmount) {
                    addGlobalError(ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_EXCEEDS_OPEN_INVOICE_DETAIL_AMOUNT);
                }
                
                // TODO This validation should happen when saving, not when adding
                boolean invoiceDetailAmountToBeAppliedIsPositive = invoiceDetailAmountToBeApplied.isGreaterEqual(KualiDecimal.ZERO);
                if(!invoiceDetailAmountToBeAppliedIsPositive) {
                    addGlobalError(ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_MUST_BE_POSTIIVE);
                }
                
                // if the detail was not selected to apply full amount than check 
                // if amount to be applied is not zero and less than or equal to the total applied amount
                if (invoiceDetailAmountToBeAppliedIsPositive && invoiceDetailAmountToBeAppliedIsLessEqualThanOpenAmount) {
                    // apply the amount entered to the customer invoice detail
                    InvoicePaidApplied invoicePaidApplied = 
                        paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(
                            customerInvoiceDetail, applicationDocNbr, universityFiscalYear, universityFiscalPeriodCode, 
                            customerInvoiceDetail.getAmountToBeApplied(), invoicePaidAppliedItemNbr);
                    // if there was not another invoice paid applied already created for the current
                    // detail then invoicePaidApplied will not be null
                    if (invoicePaidApplied != null) {
                        // add the new invoice paid applied to the payment application document list of applied payments
                        if(amountToBeAppliedIsValid) {
                            applicationDocument.getAppliedPayments().add(invoicePaidApplied);
                        }
                    }
                }
            }
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

        PaymentApplicationDocumentService service = SpringContext.getBean(PaymentApplicationDocumentService.class);
        // go over the selected invoices and apply full amount to each of their details
        for (String invoiceNbr : invoiceNumbers) {
            // get the customer invoice details for the current invoice number
            Collection<CustomerInvoiceDetail> customerInvoiceDetails = getCustomerInvoiceDetailsForInvoice(applicationDocumentForm, invoiceNbr);
            for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
                service.updateCustomerInvoiceDetailInfo(applicationDocumentForm.getPaymentApplicationDocument(), customerInvoiceDetail);
                // next invoice paid applied number is the size of the appliedPayments list + 1
                Integer invoicePaidAppliedItemNbr = pAppDoc.getAppliedPayments().size() + 1;
                // apply the full amount for this detail
                InvoicePaidApplied invoicePaidApplied = paymentApplicationDocumentService.createInvoicePaidAppliedForInvoiceDetail(customerInvoiceDetail, paymentApplicationDocumentNbr, universityFiscalYear, universityFiscalPeriodCode, customerInvoiceDetail.getOpenAmount(), invoicePaidAppliedItemNbr);
                // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will
                // not be null
                if (invoicePaidApplied != null) {
                    // add it to the payment application document list of applied payments
                    pAppDoc.getAppliedPayments().add(invoicePaidApplied);
                    // set the new applied amount for the customer invoice detail
                    customerInvoiceDetail.setAmountToBeApplied(customerInvoiceDetail.getAmount());
                }

            }
            if (invoiceNbr.equals(applicationDocumentForm.getEnteredInvoiceDocumentNumber())) {
                loadInvoice(applicationDocumentForm, invoiceNbr);
            }
        }

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
    public ActionForward addNonAr(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean trap = true;
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        PaymentApplicationDocument pAppDoc = (PaymentApplicationDocument) pform.getDocument();
        NonInvoiced nonInvoiced = pform.getNonInvoicedAddLine();
        nonInvoiced.setDocumentNumber(pAppDoc.getDocumentNumber());
        nonInvoiced.setFinancialDocumentLineNumber(pform.getNextNonInvoicedLineNumber());

        // advanceDeposit business rules
        boolean rulePassed = validateNewNonInvoiced(nonInvoiced);
        if (rulePassed) {
            // add advanceDeposit
            pAppDoc.getNonInvoicedPayments().add(nonInvoiced);

            // clear the used advanceDeposit
            pform.setNonInvoicedAddLine(new NonInvoiced());
        }

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
    private void initializeForm(PaymentApplicationDocumentForm form) {
        if(null != form) {
            if(null != form.getDocument()) {
                PaymentApplicationDocument d = form.getPaymentApplicationDocument();
                if(null != d.getNonInvoicedDistributions()) {
                    for(NonInvoicedDistribution u : d.getNonInvoicedDistributions()) {
                        if(null == form.getNextNonInvoicedLineNumber()) {
                            form.setNextNonInvoicedLineNumber(u.getFinancialDocumentLineNumber());
                        } else if (u.getFinancialDocumentLineNumber() > form.getNextNonInvoicedLineNumber()) {
                            form.setNextNonInvoicedLineNumber(u.getFinancialDocumentLineNumber());
                        }
                    }
                }
            }
            if(null == form.getNextNonInvoicedLineNumber()) {
                form.setNextNonInvoicedLineNumber(1);
            }
        }
    }

    /**
     * This method loads the invoices for currently selected customer
     * 
     * @param applicationDocumentForm
     */
    private void loadInvoices(PaymentApplicationDocumentForm applicationDocumentForm) {
        PaymentApplicationDocument applicationDocument = applicationDocumentForm.getPaymentApplicationDocument();
        String customerNumber = applicationDocument.getAccountsReceivableDocumentHeader() == null ? null : applicationDocument.getAccountsReceivableDocumentHeader().getCustomerNumber();
        String currentInvoiceNumber = applicationDocumentForm.getEnteredInvoiceDocumentNumber();

        // if customer number is null but invoice number is not null then get the customer number based on the invoice number
        if ((customerNumber == null || customerNumber.equals("")) && (currentInvoiceNumber != null && !currentInvoiceNumber.equals(""))) {
            Customer customer = customerInvoiceDocumentService.getCustomerByInvoiceDocumentNumber(currentInvoiceNumber);
            customerNumber = customer.getCustomerNumber();
            applicationDocument.getAccountsReceivableDocumentHeader().setCustomerNumber(customerNumber);
        }
        // get current customer invoices
        applicationDocumentForm.setInvoices(new ArrayList(customerInvoiceDocumentService.getCustomerInvoiceDocumentsByCustomerNumber(customerNumber)));

        // if no invoice number entered than get the first invoice
        if ((customerNumber != null && !customerNumber.equals("")) && (currentInvoiceNumber == null || "".equalsIgnoreCase(currentInvoiceNumber))) {
            if (applicationDocumentForm.getInvoices() != null && applicationDocumentForm.getInvoices().size() > 0) {
                currentInvoiceNumber = applicationDocumentForm.getInvoices().iterator().next().getDocumentNumber();
                applicationDocumentForm.setEnteredInvoiceDocumentNumber(currentInvoiceNumber);
            }
        }
        // set the selected invoice to be the first one in the list
        applicationDocumentForm.setSelectedInvoiceDocumentNumber(currentInvoiceNumber);

        if (currentInvoiceNumber != null && !currentInvoiceNumber.equals("")) {
            // load information for the current selected invoice
            loadInvoice(applicationDocumentForm, currentInvoiceNumber);
        }
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
        if (currentInvoiceNumber != null && !currentInvoiceNumber.equals("")) {
            // set entered invoice number to be the current selected invoice number
            pform.setEnteredInvoiceDocumentNumber(currentInvoiceNumber);
            // load information for the current selected invoice
            loadInvoice(pform, currentInvoiceNumber);
        }

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
        updateInvoiceInfo(pform, invoice);
    }

    /**
     * This method updates the invoice information : total, balance, applied amount
     * @param applicationDocumentForm
     * @param invoice
     */
    private void updateInvoiceInfo(PaymentApplicationDocumentForm applicationDocumentForm, CustomerInvoiceDocument invoice) {
        applicationDocumentForm.setSelectedInvoiceTotalAmount(invoice.getSourceTotal());
        applicationDocumentForm.setSelectedInvoiceBalance(getInvoiceBalance(applicationDocumentForm, invoice.getDocumentNumber()));
        applicationDocumentForm.setAmountAppliedDirectlyToInvoice(computeAmountAppliedToInvoice(applicationDocumentForm, invoice.getDocumentNumber()));
    }

    /**
     * This method calculates the invoice balance: it sums the balances of the invoice details
     * 
     * @param applicationDocumentForm
     * @param currentInvoiceNumber
     * @return the invoice balance
     */
    private KualiDecimal getInvoiceBalance(PaymentApplicationDocumentForm applicationDocumentForm, String currentInvoiceNumber) {
        Collection<CustomerInvoiceDetail> invoiceDetails = getCustomerInvoiceDetailsForInvoice(applicationDocumentForm, currentInvoiceNumber);
        KualiDecimal balance = KualiDecimal.ZERO;

        for (CustomerInvoiceDetail detail : invoiceDetails) {
            balance = balance.add(detail.getBalance());
        }

        return balance;
    }

    /**
     * This method computes the amount applied to invoice
     * 
     * @param pform
     * @param currentInvoiceNumber
     * @return
     */
    private KualiDecimal computeAmountAppliedToInvoice(PaymentApplicationDocumentForm applicationDocumentForm, String currentInvoiceNumber) {

        KualiDecimal amountAppliedDirectlyToInvoice = KualiDecimal.ZERO;

        Collection<InvoicePaidApplied> invoicePaidAppliedsForInvoice = getInvoicePaidAppliedsForInvoice(applicationDocumentForm, currentInvoiceNumber);

        for (InvoicePaidApplied invoicePaidApplied : invoicePaidAppliedsForInvoice) {
            amountAppliedDirectlyToInvoice = amountAppliedDirectlyToInvoice.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }

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
        String currentInvoiceNumber = pform.getNextInvoiceDocumentNumber();
        if (currentInvoiceNumber != null && !currentInvoiceNumber.equals("")) {

            // set entered invoice number to be the current selected invoice number
            pform.setEnteredInvoiceDocumentNumber(currentInvoiceNumber);
            pform.setSelectedInvoiceDocumentNumber(currentInvoiceNumber);
            // load information for the current selected invoice
            loadInvoice(pform, currentInvoiceNumber);
        }

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
        String currentInvoiceNumber = pform.getPreviousInvoiceDocumentNumber();
        if (currentInvoiceNumber != null && !currentInvoiceNumber.equals("")) {

            // set entered invoice number to be the current selected invoice number
            pform.setEnteredInvoiceDocumentNumber(currentInvoiceNumber);
            pform.setSelectedInvoiceDocumentNumber(currentInvoiceNumber);
            // load information for the current selected invoice
            loadInvoice(pform, currentInvoiceNumber);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Validate a new non-invoiced line.
     *
     * @param nonInvoiced
     * @return
     */
    public boolean validateNewNonInvoiced(NonInvoiced nonInvoiced) {
        return PaymentApplicationDocumentRuleUtil.validateNonInvoiced(nonInvoiced);
    }

    /**
     * Set the customer so we can pull up invoices for that customer.
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

    /**
     * This method gets the customer invoice details for the given invoice
     * @param applicationDocumentForm
     * @param customerInvoiceDocumentNumber
     * @return
     */
    private ArrayList<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(PaymentApplicationDocumentForm applicationDocumentForm, String customerInvoiceDocumentNumber) {
        ArrayList<CustomerInvoiceDetail> customerInvoiceDetails = new ArrayList(customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoice(customerInvoiceDocumentNumber));

        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            PaymentApplicationDocumentService service = SpringContext.getBean(PaymentApplicationDocumentService.class);
            service.updateCustomerInvoiceDetailInfo(applicationDocumentForm.getPaymentApplicationDocument(), customerInvoiceDetail);
        }
        return customerInvoiceDetails;
    }

    /**
     * This method gets the invoice paid applieds from the form for the given invoice
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     * @return  the invoice paid applieds from the form for the given invoice
     */
    private Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(PaymentApplicationDocumentForm applicationDocumentForm, String invoiceNumber) {
        //get the invoice paid applieds from the form
        Collection<InvoicePaidApplied> invPaidAppliedsForm = applicationDocumentForm.getPaymentApplicationDocument().getAppliedPayments();
        Collection<InvoicePaidApplied> invPaidAppliedsFormForThisInvoice = new ArrayList<InvoicePaidApplied>();

        // get the invoice paid applieds from the form for this detail
        for (InvoicePaidApplied invoicePaidApplied : invPaidAppliedsForm) {
            if (invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber().equals(invoiceNumber)) {
                invPaidAppliedsFormForThisInvoice.add(invoicePaidApplied);
            }
        }
        return invPaidAppliedsFormForThisInvoice;
    }

    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm _form = (PaymentApplicationDocumentForm) form;
        if(null == _form.getCashControlDocument()) {
            super.cancel(mapping, form, request, response);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward commitUnapplied(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
