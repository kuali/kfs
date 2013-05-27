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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.AppliedPayment;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.rules.rule.event.BlanketApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service method implementations for Payment Application Document.
 */
@Transactional
public class PaymentApplicationDocumentServiceImpl implements PaymentApplicationDocumentService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentServiceImpl.class);;

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private NonAppliedHoldingService nonAppliedHoldingService;
    private InvoicePaidAppliedService<AppliedPayment> invoicePaidAppliedService;
    private UniversityDateService universityDateService;
    private CashControlDetailDao cashControlDetailDao;
    private ConfigurationService kualiConfigurationService;
    private SystemInformationService systemInformationService;
    private CustomerAddressService customerAddressService;
    private ParameterService parameterService;

    /**
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    @Override
    public PaymentApplicationDocument createPaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {

        PaymentApplicationDocument applicationDocument = (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);

        // get the processing chart & org off the invoice, we'll create the payapp with the same processing org
        String processingChartCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrgCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();

        AccountsReceivableDocumentHeaderService arDocHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader arDocHeader = arDocHeaderService.getNewAccountsReceivableDocumentHeader(processingChartCode, processingOrgCode);
        arDocHeader.setDocumentNumber(applicationDocument.getDocumentNumber());
        applicationDocument.setAccountsReceivableDocumentHeader(arDocHeader);

        // This code is basically copied from PaymentApplicationDocumentAction.quickApply
        int paidAppliedItemNumber = 1;
        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {
            InvoicePaidApplied invoicePaidApplied = createInvoicePaidAppliedForInvoiceDetail(customerInvoiceDetail, applicationDocument, paidAppliedItemNumber);
            // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will not
            // be null
            if (invoicePaidApplied != null) {
                // add it to the payment application document list of applied payments
                applicationDocument.getInvoicePaidApplieds().add(invoicePaidApplied);
                paidAppliedItemNumber++;
            }
        }

        return applicationDocument;
    }

    /**
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    @Override
    public PaymentApplicationDocument createAndSavePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        PaymentApplicationDocument applicationDocument = createPaymentApplicationToMatchInvoice(customerInvoiceDocument);
        documentService.saveDocument(applicationDocument);
        return applicationDocument;
    }

    /**
     * @param customerInvoiceDocument
     * @param approvalAnnotation
     * @param workflowNotificationRecipients
     * @return
     * @throws WorkflowException
     */
    @Override
    public PaymentApplicationDocument createSaveAndApprovePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument, String approvalAnnotation, List workflowNotificationRecipients) throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PaymentApplicationDocument applicationDocument = createAndSavePaymentApplicationToMatchInvoice(customerInvoiceDocument);
        documentService.approveDocument(applicationDocument, approvalAnnotation, workflowNotificationRecipients);
        return applicationDocument;
    }

    /**
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
        if (null != nonAppliedHolding) {
            KualiDecimal amount = nonAppliedHolding.getFinancialDocumentLineAmount();
            if (null != amount) {
                total = total.add(amount);
            }
        }

        return total;
    }

    /**
     * @param document
     * @return
     */
    public KualiDecimal getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal totalUnapplied = getTotalUnappliedFundsForPaymentApplicationDocument(document);
        KualiDecimal totalApplied = getTotalAppliedAmountForPaymentApplicationDocument(document);
        return totalUnapplied.subtract(totalApplied);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDocumentForPaymentApplicationDocument(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    @Override
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument paymentApplicationDocument) {
        if (paymentApplicationDocument == null) {
            throw new IllegalArgumentException("A null paymentApplicationDocument parameter was passed in.");
        }
        String payAppDocNumber = paymentApplicationDocument.getDocumentHeader().getDocumentNumber();
        return getCashControlDocumentForPayAppDocNumber(payAppDocNumber);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDocumentForPayAppDocNumber(java.lang.String)
     */
    @Override
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
            // TODO we may need to swallow this ...
            throw new RuntimeException("A workflow exception was thrown when trying to retrieve document [" + cashControlDetail.getDocumentNumber() + "].", e);
        }
        return cashControlDocument;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDetailForPaymentApplicationDocument(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    @Override
    public CashControlDetail getCashControlDetailForPaymentApplicationDocument(PaymentApplicationDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("A null paymentApplicationDocument parameter was passed in.");
        }
        String payAppDocumentNumber = document.getDocumentNumber();
        CashControlDetail cashControlDetail = getCashControlDetailForPayAppDocNumber(payAppDocumentNumber);
        return cashControlDetail;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDetailForPayAppDocNumber(java.lang.String)
     */
    @Override
    public CashControlDetail getCashControlDetailForPayAppDocNumber(String payAppDocNumber) {
        if (StringUtils.isBlank(payAppDocNumber)) {
            throw new IllegalArgumentException("A null or blank payAppDocNumber parameter was passed in.");
        }
        CashControlDetail cashControlDetail = cashControlDetailDao.getCashControlDetailByRefDocNumber(payAppDocNumber);
        return cashControlDetail;
    }

    /**
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createInvoicePaidAppliedsForEntireInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument,
     *      org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    @Override
    public PaymentApplicationDocument createInvoicePaidAppliedsForEntireInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument, PaymentApplicationDocument paymentApplicationDocument) {

        // clear any existing paidapplieds
        paymentApplicationDocument.getInvoicePaidApplieds().clear();

        int paidAppliedItemNumber = 1;
        for (CustomerInvoiceDetail detail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {

            // create the new paidapplied
            InvoicePaidApplied invoicePaidApplied = createInvoicePaidAppliedForInvoiceDetail(detail, paymentApplicationDocument, paidAppliedItemNumber);

            // add it to the payment application document list of applied payments
            paymentApplicationDocument.getInvoicePaidApplieds().add(invoicePaidApplied);
            paidAppliedItemNumber++;
        }

        return paymentApplicationDocument;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createInvoicePaidAppliedForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
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
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#customerInvoiceDetailPairsWithInvoicePaidApplied(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail,
     *      org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied)
     */
    @Override
    public boolean customerInvoiceDetailPairsWithInvoicePaidApplied(CustomerInvoiceDetail customerInvoiceDetail, InvoicePaidApplied invoicePaidApplied) {
        boolean pairs = true;
        pairs &= customerInvoiceDetail.getSequenceNumber().equals(invoicePaidApplied.getInvoiceItemNumber());
        pairs &= customerInvoiceDetail.getDocumentNumber().equals(invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber());
        return pairs;
    }

    /* Start TEM REFUND merge */
    /**
     * Creates a new disbursement voucher document populated from the given payment application document. The DV is initiated as the
     * initiator of the payment application document. The DV is then saved to inbox, routed, or blanket approved based on parameter
     * configuration
     *
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createDisbursementVoucherDocumentForRefund(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    @Override
    public void createDisbursementVoucherDocumentForRefund(PaymentApplicationDocument paymentApplicationDocument) {
        // changed session to initiator of payment application so DV will save to their inbox
        UserSession userSession = GlobalVariables.getUserSession();

        Person initiator = SpringContext.getBean(PersonService.class).getPerson(paymentApplicationDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        GlobalVariables.setUserSession(new UserSession(initiator.getPrincipalName()));

        DisbursementVoucherDocument disbursementVoucherDocument = null;
        try {
            disbursementVoucherDocument = (DisbursementVoucherDocument) documentService.getNewDocument(DisbursementVoucherDocument.class);
        }
        catch (Exception e) {
            LOG.error("Error creating new disbursement voucher document: " + e.getMessage());
            throw new RuntimeException("Error creating new disbursement voucher document: " + e.getMessage(), e);
        }

        populateDisbursementVoucherFields(disbursementVoucherDocument, paymentApplicationDocument);

        // save, route, or blanketapprove based on parameter
        boolean saveDV = false;
        boolean routeDV = false;
        boolean blanketApproveDV = false;

        String dvRouteConfig = parameterService.getParameterValueAsString(PaymentApplicationDocument.class, ArConstants.ArRefunding.DV_ROUTE_PARAMETER_NAME);
        if (StringUtils.isNotBlank(dvRouteConfig)) {
            if (ArConstants.ArRefunding.DV_ROUTE_SAVE.equals(dvRouteConfig)) {
                saveDV = true;
            }
            else if (ArConstants.ArRefunding.DV_ROUTE_ROUTE.equals(dvRouteConfig)) {
                routeDV = true;
            }
            else if (ArConstants.ArRefunding.DV_ROUTE_BLANKETAPPROVE.equals(dvRouteConfig)) {
                blanketApproveDV = true;
            }
        }
        else {
            // default
            saveDV = true;
        }

        try {
            if (blanketApproveDV) {
                boolean isValid = SpringContext.getBean(KualiRuleService.class).applyRules(new BlanketApproveDocumentEvent(disbursementVoucherDocument));
                if (isValid) {
                    documentService.blanketApproveDocument(disbursementVoucherDocument, "blanket approved for payment application refund", new ArrayList());
                }
                else {
                    saveDV = true;
                }
            }
            else if (routeDV) {
                boolean isValid = SpringContext.getBean(KualiRuleService.class).applyRules(new RouteDocumentEvent(disbursementVoucherDocument));
                if (isValid) {
                    documentService.routeDocument(disbursementVoucherDocument, "routed for payment application refund", new ArrayList());
                }
                else {
                    saveDV = true;
                }
            }

            if (saveDV) {
                // clear message map so save can happen
                GlobalVariables.setMessageMap(new MessageMap());

                documentService.saveDocument(disbursementVoucherDocument);
            }
        }
        catch (Exception ex) {
            LOG.error("cannot submit DV " + disbursementVoucherDocument.getDocumentNumber(), ex);
            throw new RuntimeException("cannot submit DV " + disbursementVoucherDocument.getDocumentNumber(), ex);
        }

        // update payment app with dv info
        paymentApplicationDocument.setRefundDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
        businessObjectService.save(paymentApplicationDocument);

        // set user session back
        GlobalVariables.setUserSession(userSession);
    }

    /**
     * Populates the given disbursement voucher document from the given payment application document based on predetermined rules
     *
     * @param disbursementVoucherDocument - disbursement voucher document instance to populate
     * @param paymentApplicationDocument - payment application document instance to pull values from
     */
    protected void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, PaymentApplicationDocument paymentApplicationDocument) {
        disbursementVoucherDocument.setRefundIndicator(true);

        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription(kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.MESSAGE_REFUND_DV_DOCUMENT_DESCRIPTION));
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(paymentApplicationDocument.getDocumentNumber());
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

        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(systemInformation.getRefundPaymentReasonCode());
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(systemInformation.getRefundDocumentationLocationCode());

        // build check stub text
        String appliedInvoices = "";
        for (InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()) {
            if (StringUtils.isNotBlank(appliedInvoices)) {
                appliedInvoices += ", ";
            }
            appliedInvoices += invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber();
        }

        String checkStubText = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.MESSAGE_REFUND_DV_CHECK_STUB_TEXT);
        checkStubText = MessageFormat.format(checkStubText, paymentApplicationDocument.getDocumentNumber(), appliedInvoices);
        disbursementVoucherDocument.setDisbVchrCheckStubText(checkStubText);

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

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#addNoteToPaymentRequestDocument(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void addNoteToRelatedPaymentRequestDocument(String relatedDocumentNumber, String noteText) {
        // retrieve payment request
        Map<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put(ArPropertyConstants.PaymentApplicationDocumentFields.REFUND_DOCUMENT_NUMBER, relatedDocumentNumber);

        List<PaymentApplicationDocument> documents = (List<PaymentApplicationDocument>) businessObjectService.findMatching(PaymentApplicationDocument.class, searchParameters);
        if (documents != null && !documents.isEmpty()) {
            PaymentApplicationDocument document = documents.get(0);

            Note note = documentService.createNoteFromDocument(document, noteText);
            note.setRemoteObjectIdentifier(document.getObjectId());
            note.setAuthorUniversalIdentifier(GlobalVariables.getUserSession().getPrincipalId());
            note.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());

            document.addNote(note);

            businessObjectService.save(document);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getProcessingOrganizationForRelatedPaymentRequestDocument(java.lang.String)
     */
    @Override
    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber) {
        // retrieve payment request
        Map<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put(ArPropertyConstants.PaymentApplicationDocumentFields.REFUND_DOCUMENT_NUMBER, relatedDocumentNumber);

        PaymentApplicationDocument document = (PaymentApplicationDocument) businessObjectService.findMatching(PaymentApplicationDocument.class, searchParameters);
        if (document != null) {
            return document.getAccountsReceivableDocumentHeader().getProcessingOrganization();
        }

        return null;
    }

    /* End TEM REFUND merge */

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

    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    protected SystemInformationService getSystemInformationService() {
        return systemInformationService;
    }

    public void setSystemInformationService(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    protected CustomerAddressService getCustomerAddressService() {
        return customerAddressService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    /* End TEM REFUND Merge */


    public Collection<PaymentApplicationDocument> getPaymentApplicationDocumentByInvoiceDocument(String invoiceNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("financialDocumentReferenceInvoiceNumber", invoiceNumber);
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);

        Collection<PaymentApplicationDocument> payments = service.findMatching(PaymentApplicationDocument.class, fieldValues);

        return payments;
    }
}
