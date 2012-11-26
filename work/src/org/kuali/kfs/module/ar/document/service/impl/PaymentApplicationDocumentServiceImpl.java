/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.AppliedPayment;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentApplicationDocumentServiceImpl implements PaymentApplicationDocumentService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentServiceImpl.class);;

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private NonAppliedHoldingService nonAppliedHoldingService;
    private InvoicePaidAppliedService<AppliedPayment> invoicePaidAppliedService;
    private UniversityDateService universityDateService;
    private CashControlDetailDao cashControlDetailDao;
    
    /**
     * 
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createPaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        
        PaymentApplicationDocument applicationDocument = 
            (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);

        //  get the processing chart & org off the invoice, we'll create the payapp with the same processing org
        String processingChartCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrgCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        
        AccountsReceivableDocumentHeaderService arDocHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader arDocHeader = arDocHeaderService.getNewAccountsReceivableDocumentHeader(processingChartCode, processingOrgCode);
        arDocHeader.setDocumentNumber(applicationDocument.getDocumentNumber());
        applicationDocument.setAccountsReceivableDocumentHeader(arDocHeader);
        
        // This code is basically copied from PaymentApplicationDocumentAction.quickApply
        int paidAppliedItemNumber = 1;
        for(CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {
            InvoicePaidApplied invoicePaidApplied = 
                createInvoicePaidAppliedForInvoiceDetail(
                    customerInvoiceDetail, applicationDocument, paidAppliedItemNumber);
            // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will not be null
            if (invoicePaidApplied != null) {
                // add it to the payment application document list of applied payments
                applicationDocument.getInvoicePaidApplieds().add(invoicePaidApplied);
                paidAppliedItemNumber++;
            }
        }
        
        return applicationDocument;
    }

    /**
     *
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createAndSavePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        PaymentApplicationDocument applicationDocument = createPaymentApplicationToMatchInvoice(customerInvoiceDocument);
        documentService.saveDocument(applicationDocument);
        return applicationDocument;
    }

    /**
     *
     * @param customerInvoiceDocument
     * @param approvalAnnotation
     * @param workflowNotificationRecipients
     * @return
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createSaveAndApprovePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument, String approvalAnnotation, List workflowNotificationRecipients) throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PaymentApplicationDocument applicationDocument = createAndSavePaymentApplicationToMatchInvoice(customerInvoiceDocument);
        documentService.approveDocument(applicationDocument, approvalAnnotation, workflowNotificationRecipients);
        return applicationDocument;
    }

    /**
     *
     * @param document
     * @return
     */
    public KualiDecimal getTotalAppliedAmountForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;
        Collection<InvoicePaidApplied> invoicePaidApplieds = document.getInvoicePaidApplieds();

        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            total = total.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }

        // Include non-ar funds as well
        total = total.add(document.getSumOfNonInvoiceds());

        return total;
    }

    /**
     *
     * @param document
     * @return
     */
    public KualiDecimal getTotalUnappliedFundsForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;

        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();
        Collection<NonAppliedHolding> nonAppliedHoldings = nonAppliedHoldingService.getNonAppliedHoldingsForCustomer(customerNumber);

        for (NonAppliedHolding nonAppliedHolding : nonAppliedHoldings) {
            total = total.add(nonAppliedHolding.getFinancialDocumentLineAmount());
        }

        // Add the amount for this document, if it's set
        NonAppliedHolding nonAppliedHolding = document.getNonAppliedHolding();
        if(null != nonAppliedHolding) {
            KualiDecimal amount = nonAppliedHolding.getFinancialDocumentLineAmount();
            if(null != amount) {
                total = total.add(amount);
            }
        }

        return total;
    }

    /**
     *
     * @param document
     * @return
     */
    public KualiDecimal getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal totalUnapplied = getTotalUnappliedFundsForPaymentApplicationDocument(document);
        KualiDecimal totalApplied = getTotalAppliedAmountForPaymentApplicationDocument(document);
        return totalUnapplied.subtract(totalApplied);
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDocumentForPaymentApplicationDocument(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument paymentApplicationDocument) {
        if (paymentApplicationDocument == null) {
            throw new IllegalArgumentException("A null paymentApplicationDocument parameter was passed in.");
        }
        String payAppDocNumber = paymentApplicationDocument.getDocumentHeader().getDocumentNumber();
        return getCashControlDocumentForPayAppDocNumber(payAppDocNumber);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDocumentForPayAppDocNumber(java.lang.String)
     */
    public CashControlDocument getCashControlDocumentForPayAppDocNumber(String paymentApplicationDocumentNumber) {
        if (StringUtils.isBlank(paymentApplicationDocumentNumber)) {
            throw new IllegalArgumentException("A null or blank paymentApplicationDocumentNumber paraemter was passed in.");
        }
        CashControlDetail cashControlDetail = getCashControlDetailForPayAppDocNumber(paymentApplicationDocumentNumber);
        if (cashControlDetail == null) {
            return null;
        }
        CashControlDocument cashControlDocument = null;
        try {
            cashControlDocument = (CashControlDocument) documentService.getByDocumentHeaderId(cashControlDetail.getDocumentNumber());
        }
        catch (WorkflowException e) {
            //TODO we may need to swallow this ...
            throw new RuntimeException("A workflow exception was thrown when trying to retrieve document [" + cashControlDetail.getDocumentNumber() + "].", e);
        }
        return cashControlDocument;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDetailForPaymentApplicationDocument(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    public CashControlDetail getCashControlDetailForPaymentApplicationDocument(PaymentApplicationDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("A null paymentApplicationDocument parameter was passed in.");
        }
        String payAppDocumentNumber = document.getDocumentNumber();
        CashControlDetail cashControlDetail = getCashControlDetailForPayAppDocNumber(payAppDocumentNumber);
        return cashControlDetail;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDetailForPayAppDocNumber(java.lang.String)
     */
    public CashControlDetail getCashControlDetailForPayAppDocNumber(String payAppDocNumber) {
        if (StringUtils.isBlank(payAppDocNumber)) {
            throw new IllegalArgumentException("A null or blank payAppDocNumber parameter was passed in.");
        }
        CashControlDetail cashControlDetail = cashControlDetailDao.getCashControlDetailByRefDocNumber(payAppDocNumber);
        return cashControlDetail;
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public PaymentApplicationDocument createInvoicePaidAppliedsForEntireInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument, PaymentApplicationDocument paymentApplicationDocument) {
        
        //  clear any existing paidapplieds
        paymentApplicationDocument.getInvoicePaidApplieds().clear();

        int paidAppliedItemNumber = 1;
        for(CustomerInvoiceDetail detail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {

            //  create the new paidapplied
            InvoicePaidApplied invoicePaidApplied = createInvoicePaidAppliedForInvoiceDetail(
                    detail, paymentApplicationDocument, paidAppliedItemNumber);
            
            // add it to the payment application document list of applied payments
            paymentApplicationDocument.getInvoicePaidApplieds().add(invoicePaidApplied);
            paidAppliedItemNumber++;
        }
        
        return paymentApplicationDocument;
    }
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createInvoicePaidAppliedForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, PaymentApplicationDocument paymentApplicationDocument, Integer paidAppliedItemNumber) {

        Integer universityFiscalYear = universityDateService.getCurrentFiscalYear();
        String universityFiscalPeriodCode = universityDateService.getCurrentUniversityDate().getAccountingPeriod().getUniversityFiscalPeriodCode();
        
        InvoicePaidApplied invoicePaidApplied = new InvoicePaidApplied();
        
        // set the document number for the invoice paid applied to the payment application document number.
        invoicePaidApplied.setDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        
        // Set the invoice paid applied ref doc number to the document number for the customer invoice document
        invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDetail.getDocumentNumber());
        
        invoicePaidApplied.setInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
        invoicePaidApplied.setInvoiceItemAppliedAmount(customerInvoiceDetail.getAmountOpen());
        invoicePaidApplied.setUniversityFiscalYear(universityFiscalYear);
        invoicePaidApplied.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
        invoicePaidApplied.setPaidAppliedItemNumber(paidAppliedItemNumber);

        return invoicePaidApplied;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#customerInvoiceDetailPairsWithInvoicePaidApplied(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail, org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied)
     */
    public boolean customerInvoiceDetailPairsWithInvoicePaidApplied(CustomerInvoiceDetail customerInvoiceDetail, InvoicePaidApplied invoicePaidApplied) {
        boolean pairs = true;
        pairs &= customerInvoiceDetail.getSequenceNumber().equals(invoicePaidApplied.getInvoiceItemNumber());
        pairs &= customerInvoiceDetail.getDocumentNumber().equals(invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber());
        return pairs;
    }
    
    //CLEANUP - REMOVE START
    /**
     * Creates a new disbursement voucher document populated from the given payment application document. The DV is initiated as the
     * initiator of the payment application document. The DV is then saved to inbox, routed, or blanket approved based on parameter
     * configuration
     * 
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createDisbursementVoucherDocumentForRefund(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
/*    public void createDisbursementVoucherDocumentForRefund(PaymentApplicationDocument paymentApplicationDocument) {
        // changed session to initiator of payment application so DV will save to their inbox
        UserSession userSession = GlobalVariables.getUserSession();
        
        Person initiator = KIMServiceLocator.getPersonService().getPerson(paymentApplicationDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());

        //Check if the APP doc was generated from a TEM doc
        String orgDocNum = paymentApplicationDocument.getDocumentHeader().getOrganizationDocumentNumber();
        boolean fromTEMDoc = travelEntertainmentMovingModuleService.isTEMDocument(orgDocNum);
        if(fromTEMDoc) {
            TravelEntertainmentMovingTravelDocument doc = null;
            doc = travelEntertainmentMovingModuleService.getTEMDocument(orgDocNum);
            
            if(doc != null) {
                //Use initiator from TEM doc instead of APP doc
                initiator = KIMServiceLocator.getPersonService().getPerson(doc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            }
        }
        
        GlobalVariables.setUserSession(new UserSession(initiator.getPrincipalName()));
        
        DisbursementVoucherDocument disbursementVoucherDocument = null;
        try {
            disbursementVoucherDocument = (DisbursementVoucherDocument) documentService.getNewDocument(DisbursementVoucherDocument.class);
        }
        catch (Exception e) {
            LOG.error("Error creating new disbursement voucher document: " + e.getMessage());
            throw new RuntimeException("Error creating new disbursement voucher document: " + e.getMessage(), e);
        }

        populateDisbursementVoucherFields(disbursementVoucherDocument, paymentApplicationDocument, fromTEMDoc);

        // save, route, or blanketapprove based on parameter
        boolean routeDV = false;
        boolean blanketApproveDV = false;

        String dvRouteConfig = parameterService.getParameterValue(PaymentApplicationDocument.class, ArConstants.ArRefundingParameters.DV_ROUTE_PARAMETER_NAME);
        if (StringUtils.isNotBlank(dvRouteConfig)) {
            if (ArConstants.ArRefunding.DV_ROUTE_ROUTE.equals(dvRouteConfig)) {
                routeDV = true;
            }
            else if (ArConstants.ArRefunding.DV_ROUTE_BLANKETAPPROVE.equals(dvRouteConfig)) {
                blanketApproveDV = true;
            }
        }
        
        // always save DV
        try {
            disbursementVoucherDocument.prepareForSave();
            
            businessObjectService.save(disbursementVoucherDocument);
            KNSServiceLocator.getWorkflowDocumentService().save(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), null);
        }
        catch (Exception ex1) {
            // if we can't save DV, need to stop processing
            LOG.error("cannot save DV " + disbursementVoucherDocument.getDocumentNumber(), ex1);
            throw new RuntimeException("cannot save DV " + disbursementVoucherDocument.getDocumentNumber(), ex1);
        }

        KualiWorkflowDocument originalWorkflowDocument = disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument();
        
        try {
            if (blanketApproveDV) {

                //Change initiator to KFS system user for blanket approval if TEM doc
                if(fromTEMDoc) {
                    //Original initiator may not have permission to blanket approve the DV
                    GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
                        
                    KualiWorkflowDocument newWorkflowDocument;
                    try {
                        newWorkflowDocument = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(Long.valueOf(disbursementVoucherDocument.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
                        newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());
                        
                        disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(newWorkflowDocument);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    } catch (WorkflowException ex) {
                        ex.printStackTrace();
                    }
                }
            	
                boolean isValid = KNSServiceLocator.getKualiRuleService().applyRules(new BlanketApproveDocumentEvent(disbursementVoucherDocument));
                if (isValid) {
                    documentService.blanketApproveDocument(disbursementVoucherDocument, "blanket approved for payment application refund", new ArrayList());
                }
                else {
                    LOG.info("Cannot route DV. Validation failed.");
                    LOG.info(GlobalVariables.getErrorMap());
                }
            }
            else if (routeDV) {
                boolean isValid = KNSServiceLocator.getKualiRuleService().applyRules(new RouteDocumentEvent(disbursementVoucherDocument));
                if (isValid) {
                    documentService.routeDocument(disbursementVoucherDocument, "routed for payment application refund", new ArrayList());
                }
            }
        }
        catch (Exception ex) {
            // if we can't submit DV, let go so save will hold
            LOG.warn("cannot submit DV " + disbursementVoucherDocument.getDocumentNumber(), ex);
        }
        finally {
            //Replace the original workflow if TEM doc
            if(fromTEMDoc) {
                disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
            }
        }
        
        // update payment app with dv info
        paymentApplicationDocument.setRefundDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
        businessObjectService.save(paymentApplicationDocument);
        
        // set user session back
        GlobalVariables.setUserSession(userSession);
    }
    */
    /**
     * Populates the given disbursement voucher document from the given payment application document based on predetermined rules
     * 
     * @param disbursementVoucherDocument - disbursement voucher document instance to populate
     * @param paymentApplicationDocument - payment application document instance to pull values from
     */
/*    protected void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, PaymentApplicationDocument paymentApplicationDocument, boolean fromTEMDoc) {

        //Get TEM doc if there is one
        TravelEntertainmentMovingTravelDocument temDoc = null;
        if(fromTEMDoc) {
            temDoc = travelEntertainmentMovingModuleService.getTEMDocument(paymentApplicationDocument.getDocumentHeader().getOrganizationDocumentNumber());
            //Set the payee employee code
            if(ObjectUtils.isNotNull(temDoc)) {
                disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(travelEntertainmentMovingModuleService.isTEMProfileEmployee(temDoc));
            }
        }
        
        disbursementVoucherDocument.setRefundIndicator(true);
        
        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription(kualiConfigurationService.getPropertyString(ArKeyConstants.MESSAGE_REFUND_DV_DOCUMENT_DESCRIPTION));
        
        //If TEM doc exists set organization doc number to TEM doc number not APP doc number
        if(fromTEMDoc && temDoc != null) {
            disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(temDoc.getTravelDocumentIdentifier());
        } else {
            disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        }
        
        disbursementVoucherDocument.getDvPayeeDetail().setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());

        // init document
        disbursementVoucherDocument.initiateDocument();

        // get customer primary address to populate address dv fields
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(paymentApplicationDocument.getAccountsReceivableDocumentHeader().getCustomerNumber());
        if (customerAddress != null) {
            disbursementVoucherDocument.templateCustomer(paymentApplicationDocument.getAccountsReceivableDocumentHeader().getCustomer(), customerAddress);
        }

        // set defaults
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode("P");

        // get system information for processing org
        SystemInformation systemInformation = systemInformationService.getByProcessingChartOrgAndFiscalYear(paymentApplicationDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode(), paymentApplicationDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode(), paymentApplicationDocument.getPostingYear());
        if (StringUtils.isNotBlank(systemInformation.getRefundPaymentReasonCode())) {
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(systemInformation.getRefundPaymentReasonCode());
        }
        else {
            String defaultPaymentReason = parameterService.getParameterValue(PaymentApplicationDocument.class, ArConstants.ArRefundingParameters.DEFAULT_REFUND_PAYMENT_REASON_CODE_PARAMETER_NAME);
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(defaultPaymentReason);
        }
//        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(systemInformation.getRefundDocumentationLocationCode());

        // build check stub text
        String appliedInvoices = "";
        for (InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()) {
            if (StringUtils.isNotBlank(appliedInvoices)) {
                appliedInvoices += ", ";
            }
            appliedInvoices += invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber();
        }

        String checkStubText = kualiConfigurationService.getPropertyString(ArKeyConstants.MESSAGE_REFUND_DV_CHECK_STUB_TEXT);
        String documentDescription = kualiConfigurationService.getPropertyString(ArKeyConstants.MESSAGE_REFUND_DV_DOCUMENT_DESCRIPTION);
        checkStubText = MessageFormat.format(checkStubText, paymentApplicationDocument.getDocumentNumber(), appliedInvoices);

        //If TEM doc exists add the trip information to check sub text
        if(fromTEMDoc && temDoc != null) {
            checkStubText += " for " + temDoc.getTravelDocumentIdentifier() + " " + temDoc.getTraveler().getLastName() + " - " + temDoc.getPrimaryDestinationName() + " - " + temDoc.getTripBegin();
        }
        
        disbursementVoucherDocument.setDisbVchrCheckStubText(checkStubText);
        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription(documentDescription + " - " + paymentApplicationDocument.getDocumentNumber());

        // set accounting
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (NonInvoiced nonInvoiced : paymentApplicationDocument.getNonInvoiceds()) {
            if (nonInvoiced.isRefundIndicator()) {
                SourceAccountingLine accountingLine = new SourceAccountingLine();

                accountingLine.setChartOfAccountsCode(nonInvoiced.getChartOfAccountsCode());
                accountingLine.setAccountNumber(nonInvoiced.getAccountNumber());

                if (StringUtils.isNotBlank(nonInvoiced.getSubAccountNumber())) {
                    accountingLine.setSubAccountNumber(nonInvoiced.getSubAccountNumber());
                }

                if (StringUtils.isNotBlank(systemInformation.getRefundFinancialObjectCode())) {
                    accountingLine.setFinancialObjectCode(systemInformation.getRefundFinancialObjectCode());
                }
                else {
                    accountingLine.setFinancialObjectCode(nonInvoiced.getFinancialObjectCode());
                }

                if (StringUtils.isNotBlank(nonInvoiced.getFinancialSubObjectCode())) {
                    accountingLine.setFinancialSubObjectCode(nonInvoiced.getFinancialSubObjectCode());
                }

                if (StringUtils.isNotBlank(nonInvoiced.getProjectCode())) {
                    accountingLine.setProjectCode(nonInvoiced.getProjectCode());
                }
                
                accountingLine.setAmount(nonInvoiced.getFinancialDocumentLineAmount());
                accountingLine.setPostingYear(paymentApplicationDocument.getPostingYear());
                accountingLine.setDocumentNumber(paymentApplicationDocument.getDocumentNumber());

                disbursementVoucherDocument.addSourceAccountingLine(accountingLine);

                totalAmount = totalAmount.add(nonInvoiced.getFinancialDocumentLineAmount());
            }
        }

        disbursementVoucherDocument.setDisbVchrCheckTotalAmount(totalAmount);
    }
*/
    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#addNoteToPaymentRequestDocument(java.lang.String,
     *      java.lang.String)
     */
 /*   public void addNoteToRelatedPaymentRequestDocument(String relatedDocumentNumber, String noteText) {
        // retrieve payment request
        Map<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put(ArPropertyConstants.PaymentApplicationDocumentFields.REFUND_DOCUMENT_NUMBER, relatedDocumentNumber);

        List<PaymentApplicationDocument> documents = (List<PaymentApplicationDocument>) businessObjectService.findMatching(PaymentApplicationDocument.class, searchParameters);
        if (documents != null && !documents.isEmpty()) {
            PaymentApplicationDocument document = documents.get(0);
            
            Note note = new Note();
            note.setNoteText(noteText);
            note.setRemoteObjectIdentifier(document.getObjectId());
            note.setAuthorUniversalIdentifier(GlobalVariables.getUserSession().getPrincipalId());
            note.setNoteTypeCode(KNSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());

            document.getDocumentHeader().addNote(note);
            
            businessObjectService.save(document);
        }
    }
    */
    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getProcessingOrganizationForRelatedPaymentRequestDocument(java.lang.String)
     */
/*    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber) {
        // retrieve payment request
        Map<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put(ArPropertyConstants.PaymentApplicationDocumentFields.REFUND_DOCUMENT_NUMBER, relatedDocumentNumber);

        PaymentApplicationDocument document = (PaymentApplicationDocument) businessObjectService.findMatching(PaymentApplicationDocument.class, searchParameters);
        if (document != null) {
            return document.getAccountsReceivableDocumentHeader().getProcessingOrganization();
        }

        return null;
    }
    */

    /*
    public Collection<PaymentApplicationDocument> getPaymentApplicationDocumentsByCustomerNumber(String customerNumber) {

        Collection<PaymentApplicationDocument> payments = new ArrayList<PaymentApplicationDocument>();

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("customerNumber", customerNumber);

        Collection<AccountsReceivableDocumentHeader> documentHeaders = businessObjectService.findMatching(AccountsReceivableDocumentHeader.class, fieldValues);

        List<String> documentHeaderIds = new ArrayList<String>();
        for (AccountsReceivableDocumentHeader header : documentHeaders) {
            String documentNumber = null;
            try {
                Long.parseLong(header.getDocumentHeader().getDocumentNumber());
                documentNumber = header.getDocumentHeader().getDocumentNumber();
                documentHeaderIds.add(documentNumber);
            }
            catch (NumberFormatException nfe) {
            }
        }

        if (0 < documentHeaderIds.size()) {
            try {
                payments = documentService.getDocumentsByListOfDocumentHeaderIds(PaymentApplicationDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                //LOG.error(e.getMessage(), e);
            }
        }
        return payments;
    }

    public Collection<PaymentApplicationDocument> getPaymentApplicationDocumentsByAccountNumber(String accountNumber) {

        Collection<CustomerInvoiceDocument> invoiceList = SpringContext.getBean(CustomerInvoiceDocumentService.class).getCustomerInvoiceDocumentsByAccountNumber(accountNumber);
        
        Set<String> customerNumberSet = new HashSet<String>();
        for (CustomerInvoiceDocument invoice : invoiceList) {
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put("documentNumber", invoice.getDocumentNumber());

            AccountsReceivableDocumentHeader arDocHeader = (AccountsReceivableDocumentHeader)businessObjectService.findByPrimaryKey(AccountsReceivableDocumentHeader.class, fieldValues);
            customerNumberSet.add(arDocHeader.getCustomerNumber());
        }

        Collection<PaymentApplicationDocument> paymentApplicationDocumentList = new ArrayList<PaymentApplicationDocument>();        
        for (String customerNumber : customerNumberSet) {
            paymentApplicationDocumentList.addAll(getPaymentApplicationDocumentsByCustomerNumber(customerNumber));
        }
        
        return paymentApplicationDocumentList;
    }
    */
    
    
    //CLEANUP - REMOVE ENDS
    public Collection<PaymentApplicationDocument> getPaymentApplicationDocumentByInvoiceDocument(String invoiceNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("financialDocumentReferenceInvoiceNumber", invoiceNumber);
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        
        Collection<PaymentApplicationDocument> payments = service.findMatching(PaymentApplicationDocument.class, fieldValues);
        
        return payments;
    }
    
    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }
    
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public NonAppliedHoldingService getNonAppliedHoldingService() {
        return nonAppliedHoldingService;
    }

    public void setNonAppliedHoldingService(NonAppliedHoldingService nonAppliedHoldingService) {
        this.nonAppliedHoldingService = nonAppliedHoldingService;
    }
    
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setCashControlDetailDao(CashControlDetailDao cashControlDetailDao) {
        this.cashControlDetailDao = cashControlDetailDao;
    }
    
}
