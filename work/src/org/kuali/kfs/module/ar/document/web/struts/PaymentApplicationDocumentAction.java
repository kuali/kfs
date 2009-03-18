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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.validation.impl.PaymentApplicationDocumentRuleUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

public class PaymentApplicationDocumentAction extends FinancialSystemTransactionalDocumentActionBase {

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Should we be doing the re-calcs before saving?
        return super.save(mapping, form, request, response);
    }

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentAction.class);
    
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private PaymentApplicationDocumentService paymentApplicationDocumentService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private CustomerInvoiceDetailService customerInvoiceDetailService;
    private NonAppliedHoldingService nonAppliedHoldingService;

    /**
     * Constructs a PaymentApplicationDocumentAction.java.
     */
    public PaymentApplicationDocumentAction() {
        super();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
        paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        nonAppliedHoldingService = SpringContext.getBean(NonAppliedHoldingService.class);
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ServletRequest request, ServletResponse response) throws Exception {
        doApplicationOfFunds((PaymentApplicationDocumentForm)form);
        return super.execute(mapping, form, request, response);
    }

    /**
     * This is overridden in order to recalculate the invoice totals before doing the submit.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //TODO should we be re-calcing things before routing?
        return super.route(mapping, form, request, response);
    }

    public ActionForward deleteNonArLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm paymentApplicationDocumentForm = (PaymentApplicationDocumentForm) form;
        
        //TODO Andrew - should this be run or not here?
        //doApplicationOfFunds((PaymentApplicationDocumentForm)form);
        
        PaymentApplicationDocument paymentApplicationDocument = paymentApplicationDocumentForm.getPaymentApplicationDocument();
        Map<String,Object> parameters = request.getParameterMap();
        //Set<Integer> indices = new HashSet<Integer>();
        String _indexToRemove = null;
        Integer indexToRemove = null;
        
        // Figure out which line to remove.
        for(String k : parameters.keySet()) {
            if(k.startsWith(ArPropertyConstants.PaymentApplicationDocumentFields.DELETE_NON_INVOICED_LINE_PREFIX) && k.endsWith(".x")) {
                if(null != parameters.get(k)) {
                    int beginIndex = ArPropertyConstants.PaymentApplicationDocumentFields.DELETE_NON_INVOICED_LINE_PREFIX.length();
                    int endIndex   = k.lastIndexOf(".");
                    if(beginIndex >= 0 && endIndex > beginIndex) {
                        _indexToRemove = k.substring(beginIndex,endIndex);
                    }
                    break;
                }
            }
        }
        
        // If we know which line to remove, remove it.
        if(null != _indexToRemove) {
            indexToRemove = new Integer(_indexToRemove);
            NonInvoiced toRemove = null;
            for(NonInvoiced nonInvoiced : paymentApplicationDocument.getNonInvoiceds()) {
                if(indexToRemove.equals(nonInvoiced.getFinancialDocumentLineNumber())) {
                    toRemove = nonInvoiced;
                    break;
                }
            }
            if(null != toRemove) {
                paymentApplicationDocument.getNonInvoiceds().remove(toRemove);
            }
        }
        
        // re-number the non-invoiceds
        Integer nonInvoicedItemNumber = 1;
        for (NonInvoiced n : paymentApplicationDocument.getNonInvoiceds()) {
            n.setFinancialDocumentLineNumber(nonInvoicedItemNumber++);
        }
                
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Create an InvoicePaidApplied for a CustomerInvoiceDetail and validate it.
     * If the validation succeeds the paidApplied is returned.
     * If the validation does succeed a null is returned.
     * 
     * @param customerInvoiceDetail
     * @param paymentApplicationDocument
     * @param amount
     * @param fieldName
     * @return
     * @throws WorkflowException
     */
    private InvoicePaidApplied generateAndValidateNewPaidApplied(PaymentApplicationInvoiceDetailApply detailApplication, String fieldName) {
        
        InvoicePaidApplied paidApplied = detailApplication.generatePaidApplied();
        
        // If the new invoice paid applied is valid, add it to the document
        if (PaymentApplicationDocumentRuleUtil.validateInvoicePaidApplied(paidApplied, fieldName)) {
            return paidApplied;
        }
        return null;
    }
    
    public ActionForward applyAllAmounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        doApplicationOfFunds((PaymentApplicationDocumentForm)form);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    private void doApplicationOfFunds(PaymentApplicationDocumentForm paymentApplicationDocumentForm) throws WorkflowException {
        PaymentApplicationDocument paymentApplicationDocument = paymentApplicationDocumentForm.getPaymentApplicationDocument();
        
        List<InvoicePaidApplied> invoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
        
        //  apply invoice detail entries
        invoicePaidApplieds.addAll(applyToIndividualCustomerInvoiceDetails(paymentApplicationDocumentForm));
        
        //  quick-apply invoices
        invoicePaidApplieds.addAll(quickApplyToInvoices(paymentApplicationDocumentForm, invoicePaidApplieds));
        
        //  re-number the paidApplieds internal sequence numbers
        int paidAppliedItemNumber = 0;
        for(InvoicePaidApplied i : invoicePaidApplieds) {
            i.setPaidAppliedItemNumber(paidAppliedItemNumber++);
        }
        
        //  apply non-Invoiced
        NonInvoiced nonInvoiced = applyNonInvoiced(paymentApplicationDocumentForm, false);
        
        //  apply non-applied holdings 
        NonAppliedHolding nonAppliedHolding = applyUnapplied(paymentApplicationDocumentForm);
        
        //  sum up the paid applieds
        KualiDecimal sumOfInvoicePaidApplieds = KualiDecimal.ZERO;
        for(InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            KualiDecimal amount = invoicePaidApplied.getInvoiceItemAppliedAmount();
            if (null == amount) { amount = KualiDecimal.ZERO; }
            sumOfInvoicePaidApplieds = sumOfInvoicePaidApplieds.add(amount);
        }
        
        // sum up all applieds
        KualiDecimal appliedAmount = KualiDecimal.ZERO;
        appliedAmount = appliedAmount.add(sumOfInvoicePaidApplieds);
        if (null != nonInvoiced && null != nonInvoiced.getFinancialDocumentLineAmount()) {
            appliedAmount = appliedAmount.add(nonInvoiced.getFinancialDocumentLineAmount());
        }
        appliedAmount = appliedAmount.add(paymentApplicationDocument.getSumOfNonAppliedDistributions());
        appliedAmount = appliedAmount.add(paymentApplicationDocument.getSumOfNonInvoicedDistributions());
        appliedAmount = appliedAmount.add(paymentApplicationDocument.getSumOfNonInvoiceds());
        if (null != paymentApplicationDocument.getNonAppliedHoldingAmount()) {
            appliedAmount = appliedAmount.add(paymentApplicationDocument.getNonAppliedHoldingAmount());
        }
        
        //  check that we havent applied more than our control total
        KualiDecimal controlTotalAmount;
        if (paymentApplicationDocument.hasCashControlDetail()) {
            controlTotalAmount = paymentApplicationDocument.getCashControlDetail().getFinancialDocumentLineAmount();
        }
        else {
            //TODO Vivek ... this is for you to fill out
            controlTotalAmount = KualiDecimal.ZERO;
        }

        //  if the person over-applies, we dont stop them, we just complain
        if (appliedAmount.isGreaterThan(controlTotalAmount)) {
            addGlobalError(ArKeyConstants.PaymentApplicationDocumentErrors.CANNOT_APPLY_MORE_THAN_CASH_CONTROL_TOTAL_AMOUNT);
        } 
        
        //  swap out the old paidApplieds with the newly generated
        paymentApplicationDocument.getInvoicePaidApplieds().clear();
        paymentApplicationDocument.getInvoicePaidApplieds().addAll(invoicePaidApplieds);
        
        //  nonInvoiced/advanceDeposit stuff
        if (null != nonInvoiced) {
            // add advanceDeposit
            paymentApplicationDocument.getNonInvoiceds().add(nonInvoiced);

            // Set the NonInvoicedItemNumbers properly
            Integer nonInvoicedItemNumber = 1;
            for (NonInvoiced n : paymentApplicationDocument.getNonInvoiceds()) {
                n.setFinancialDocumentLineNumber(nonInvoicedItemNumber++);
            }
                
            // clear the used advanceDeposit
            paymentApplicationDocumentForm.setNonInvoicedAddLine(new NonInvoiced());
        }
    }
    
    private List<InvoicePaidApplied> applyToIndividualCustomerInvoiceDetails(PaymentApplicationDocumentForm paymentApplicationDocumentForm) {
        PaymentApplicationDocument paymentApplicationDocument = paymentApplicationDocumentForm.getPaymentApplicationDocument();
        String applicationDocNbr = paymentApplicationDocument.getDocumentNumber();

        // Handle amounts applied at the invoice detail level
        int paidAppliedsGenerated = 1;
        int simpleInvoiceDetailApplicationCounter = 0;
        
        List<InvoicePaidApplied> invoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
        for (PaymentApplicationInvoiceApply invoiceApplication : paymentApplicationDocumentForm.getInvoiceApplications()) {
            for (PaymentApplicationInvoiceDetailApply detailApplication : invoiceApplication.getDetailApplications()) {
                
                String fieldName = "detailApplications[" + Integer.toString(simpleInvoiceDetailApplicationCounter) + "]";
                simpleInvoiceDetailApplicationCounter++; // needs to be incremented even if we skip this line

                // Don't add lines where the amount to apply is zero. Wouldn't make any sense to do that.
                if (KualiDecimal.ZERO.equals(detailApplication.getAmountApplied())) {
                    continue;
                } 
                
                //  determine the field name for validation purposes
                if (detailApplication.isFullApply()) {
                    fieldName += ".fullApply";
                } else {
                    fieldName += ".amountApplied";
                }
                
                //  generate and validate the paidApplied, and add it to the list if it passes validation
                InvoicePaidApplied invoicePaidApplied = generateAndValidateNewPaidApplied(detailApplication, fieldName);
                if (null != invoicePaidApplied) {
                    invoicePaidApplieds.add(invoicePaidApplied);
                    paidAppliedsGenerated++;
                }
            }
        }
        
        return invoicePaidApplieds;
    }
    
    private List<InvoicePaidApplied> quickApplyToInvoices(PaymentApplicationDocumentForm paymentApplicationDocumentForm, List<InvoicePaidApplied> appliedToIndividualDetails) {
        PaymentApplicationDocument applicationDocument = (PaymentApplicationDocument) paymentApplicationDocumentForm.getDocument();
        List<InvoicePaidApplied> invoicePaidApplieds = new ArrayList<InvoicePaidApplied>();

        //Map<String,Boolean> previouslyQuickAppliedInvoices = paymentApplicationDocumentForm.getPreviouslyQuickAppliedInvoices();
        
        // go over the selected invoices and apply full amount to each of their details
        int index = 0;
        for (PaymentApplicationInvoiceApply invoiceApplication : paymentApplicationDocumentForm.getQuickAppliableInvoiceApplications()) {
            String invoiceDocNumber = invoiceApplication.getDocumentNumber();
            
            //  skip the line if its not set to quick apply
            if (!invoiceApplication.isQuickApply()) {
                continue;
            }
            
            // make sure none of the invoices selected have zero open amounts, complain if so
            if (invoiceApplication.getOpenAmount().isZero()) {
                addGlobalError(ArKeyConstants.PaymentApplicationDocumentErrors.CANNOT_QUICK_APPLY_ON_INVOICE_WITH_ZERO_OPEN_AMOUNT);
                return invoicePaidApplieds;
            }

            //TODO Andrew - temporarily commented out as quick applies dont work this way anymore, but holding on to 
            //     this code just in case
//            // get the customer invoice details for the current invoice number
//            Collection<CustomerInvoiceDetail> customerInvoiceDetails = 
//                customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(customerInvoiceDocumentNumber);
//            Set previousKeys = previouslyQuickAppliedInvoices.keySet();
//            Collection previousValues = previouslyQuickAppliedInvoices.values();
//            
//            Boolean v = previouslyQuickAppliedInvoices.get(customerInvoiceDocumentNumber);
//            if(null == v || Boolean.FALSE.equals(v)) {
//                // Invoice was not previously quick-applied. Nothing special needs to be done.
//                previouslyQuickAppliedInvoices.put(customerInvoiceDocumentNumber, _invoice.isQuickApply());
//            } else {
//                if(_invoiceApplication.isQuickApply()) {
//                    // Invoice was quick-applied before and still is now
//                    previouslyQuickAppliedInvoices.put(customerInvoiceDocumentNumber, Boolean.TRUE);
//                } else {
//                    // Invoice was quick-applied before but is NOT now.
//                    // Reset the amounts on all details to zero b/c the amounts won't come from the form
//                    // due to the fact that the amount fields were disabled in the UI.
//                    for(CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
//                        customerInvoiceDetail.setAmount(KualiDecimal.ZERO);
//                        dontApply = true;
//                    }
//                }
//            }

            //  remove any existing paidApplieds for this invoice
            for (int i = appliedToIndividualDetails.size() - 1; i >= 0 ; i--) {
                InvoicePaidApplied applied = appliedToIndividualDetails.get(i);
                if (applied.getDocumentNumber().equals(invoiceApplication.getDocumentNumber())) {
                    appliedToIndividualDetails.remove(i);
                }
            }
            
            // create and validate the paid applieds for each invoice detail
            String fieldName = "invoiceApplications[" + invoiceDocNumber + "].quickApply"; 
            for (PaymentApplicationInvoiceDetailApply detailApplication : invoiceApplication.getDetailApplications()) {
                InvoicePaidApplied paidApplied = generateAndValidateNewPaidApplied(detailApplication, fieldName);
                if (paidApplied != null) {
                    invoicePaidApplieds.add(paidApplied);
                }
            }
            
            //  disable the quickApply value, so it doesnt show up checked next time 
            invoiceApplication.setQuickApply(false);
            
            //  maintain the selected doc number
            if (invoiceDocNumber.equals(paymentApplicationDocumentForm.getEnteredInvoiceDocumentNumber())) {
                paymentApplicationDocumentForm.setSelectedInvoiceDocumentNumber(invoiceDocNumber);
            }
        }
        
        return invoicePaidApplieds;
    }
    
    private NonInvoiced applyNonInvoiced(PaymentApplicationDocumentForm paymentApplicationDocumentForm, boolean addToDocument) throws WorkflowException {
        PaymentApplicationDocument applicationDocument = (PaymentApplicationDocument) paymentApplicationDocumentForm.getDocument();
        
        NonInvoiced nonInvoiced = paymentApplicationDocumentForm.getNonInvoicedAddLine();
        
        // Only apply if the user entered an amount
        if(ObjectUtils.isNotNull(nonInvoiced.getFinancialDocumentLineAmount())) {
            nonInvoiced.setFinancialDocumentPostingYear(applicationDocument.getPostingYear());
            nonInvoiced.setDocumentNumber(applicationDocument.getDocumentNumber());
            nonInvoiced.setFinancialDocumentLineNumber(paymentApplicationDocumentForm.getNextNonInvoicedLineNumber());
            
            if (addToDocument && PaymentApplicationDocumentRuleUtil.validateNonInvoiced(nonInvoiced, applicationDocument)) {
                // add advanceDeposit
                applicationDocument.getNonInvoiceds().add(nonInvoiced);

                // clear the used advanceDeposit
                paymentApplicationDocumentForm.setNonInvoicedAddLine(new NonInvoiced());
            }
        } else {
            return null;
        }
        
        return nonInvoiced;
    }
    
    private NonAppliedHolding applyUnapplied(PaymentApplicationDocumentForm paymentApplicationDocumentForm) throws WorkflowException {
        PaymentApplicationDocument applicationDocument = paymentApplicationDocumentForm.getPaymentApplicationDocument();
        NonAppliedHolding nonAppliedHolding = applicationDocument.getNonAppliedHolding();
        if(ObjectUtils.isNull(nonAppliedHolding)) {
            return null;
        }
        nonAppliedHolding.setReferenceFinancialDocumentNumber(applicationDocument.getDocumentNumber());
        
        if(PaymentApplicationDocumentRuleUtil.validateNonAppliedHolding(applicationDocument)) {
            paymentApplicationDocumentForm.setOldNonAppliedHoldingAmount(nonAppliedHolding.getFinancialDocumentLineAmount());
        } else {
            nonAppliedHolding.setFinancialDocumentLineAmount(paymentApplicationDocumentForm.getOldNonAppliedHoldingAmount());
        }

        return nonAppliedHolding;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
//    @Override
//    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        PaymentApplicationDocumentForm applicationDocumentForm = (PaymentApplicationDocumentForm) form;
//        for(CustomerInvoiceDocument d : applicationDocumentForm.getInvoices()) {
//            //d.setQuickApply(false);
//        }
//        String[] bad = {"save","route","cancel","reload","approve","blanketApprove"};
//        if(0 > Arrays.binarySearch(bad, applicationDocumentForm.getMethodToCall())) {
//            initializeForm(applicationDocumentForm, customerNumber);
//        }
//        ActionForward actionForward = super.execute(mapping, form, request, response);
//
//        return actionForward;
//    }

//    /**
//     * This method initializes the form
//     * 
//     * @param applicationDocumentForm
//     */
//    private void initializeForm(PaymentApplicationDocumentForm form, String customerNumber) throws WorkflowException {
//        
//        // Don't apply funds on the first load, which is the only time when the customerNumber will be non-null
//        if(null == customerNumber) {
//            //doApplicationOfFunds(form);
//        }
//    }

    /**
     * This method loads the invoices for currently selected customer
     * 
     * @param applicationDocumentForm
     */
    private void loadInvoices(PaymentApplicationDocumentForm applicationDocumentForm, String selectedInvoiceNumber) throws WorkflowException {
        PaymentApplicationDocument applicationDocument = applicationDocumentForm.getPaymentApplicationDocument();
        String customerNumber = applicationDocument.getAccountsReceivableDocumentHeader() == null ? null : applicationDocument.getAccountsReceivableDocumentHeader().getCustomerNumber();
        String currentInvoiceNumber = selectedInvoiceNumber;

        // if customer number is null but invoice number is not null then get the customer number based on the invoice number
        if (StringUtils.isBlank(customerNumber) && StringUtils.isNotBlank(currentInvoiceNumber)) {
            Customer customer = customerInvoiceDocumentService.getCustomerByInvoiceDocumentNumber(currentInvoiceNumber);
            customerNumber = customer.getCustomerNumber();
            applicationDocument.getAccountsReceivableDocumentHeader().setCustomerNumber(customerNumber);
        }

        // get open invoices for the current customer
        if(ObjectUtils.isNull(applicationDocumentForm.getInvoices()) || applicationDocumentForm.getInvoices().isEmpty()) {
            Collection<CustomerInvoiceDocument> openInvoicesForCustomer = customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerNumber(customerNumber);
            applicationDocumentForm.setInvoices(new ArrayList<CustomerInvoiceDocument>(openInvoicesForCustomer));
            applicationDocumentForm.setupInvoiceWrappers(applicationDocument.getDocumentNumber());
        }

        // if no invoice number entered than get the first invoice
        if (StringUtils.isNotBlank(customerNumber) && StringUtils.isBlank(currentInvoiceNumber)) {
            if (applicationDocumentForm.getInvoices() != null && applicationDocumentForm.getInvoices().size() > 0) {
                currentInvoiceNumber = applicationDocumentForm.getInvoices().iterator().next().getDocumentNumber();
            }
        }
        
        // set the selected invoice to be the first one in the list
        applicationDocumentForm.setSelectedInvoiceDocumentNumber(currentInvoiceNumber);
        applicationDocumentForm.setEnteredInvoiceDocumentNumber(currentInvoiceNumber);

        // load information for the current selected invoice
        if (StringUtils.isNotBlank(currentInvoiceNumber)) {
            applicationDocumentForm.setSelectedInvoiceDocumentNumber(currentInvoiceNumber);
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
        PaymentApplicationDocumentForm paymentApplicationDocumentForm = (PaymentApplicationDocumentForm) form;
        loadInvoices(paymentApplicationDocumentForm, paymentApplicationDocumentForm.getSelectedInvoiceDocumentNumber());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
        PaymentApplicationDocumentForm paymentApplicationDocumentForm = (PaymentApplicationDocumentForm) form;
        loadInvoices(paymentApplicationDocumentForm, paymentApplicationDocumentForm.getNextInvoiceDocumentNumber());
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
        PaymentApplicationDocumentForm paymentApplicationDocumentForm = (PaymentApplicationDocumentForm) form;
        loadInvoices(paymentApplicationDocumentForm, paymentApplicationDocumentForm.getPreviousInvoiceDocumentNumber());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
     * Retrieve all invoices for the selected customer.
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
        loadInvoices(pform, pform.getEnteredInvoiceDocumentNumber());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Cancel the document.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm _form = (PaymentApplicationDocumentForm) form;
        if (null == _form.getCashControlDocument()) {
            return super.cancel(mapping, form, request, response);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
        loadInvoices(pform, pform.getEnteredInvoiceDocumentNumber());
    }

    /**
     * Get an error to display in the UI for a certain field.
     * 
     * @param propertyName
     * @param errorKey
     */
    private void addFieldError(String propertyName, String errorKey) {
        GlobalVariables.getErrorMap().putError(propertyName, errorKey);
    }

    /**
     * Get an error to display at the global level, for the whole document.
     * 
     * @param errorKey
     */
    private void addGlobalError(String errorKey) {
        GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KNSConstants.DOCUMENT_ERRORS, errorKey, "document.hiddenFieldForErrors");
    }

}
