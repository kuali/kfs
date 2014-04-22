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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedDistribution;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.businessobject.ReceivableCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.BlanketApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentApplicationDocument extends GeneralLedgerPostingDocumentBase implements AmountTotaling, GeneralLedgerPendingEntrySource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocument.class);

    protected static final String LAUNCHED_FROM_BATCH = "LaunchedBySystemUser";

    protected String hiddenFieldForErrors;
    protected List<InvoicePaidApplied> invoicePaidApplieds;
    protected List<NonInvoiced> nonInvoiceds;
    protected Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    protected Collection<NonAppliedDistribution> nonAppliedDistributions;
    protected NonAppliedHolding nonAppliedHolding;
    protected AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;

    protected transient PaymentApplicationDocumentService paymentApplicationDocumentService;
    protected transient CashControlDetail cashControlDetail;
    protected transient FinancialSystemUserService fsUserService;
    protected transient CustomerInvoiceDocumentService invoiceDocService;
    protected transient DocumentService docService;
    protected transient NonAppliedHoldingService nonAppliedHoldingService;
    protected transient BusinessObjectService boService;

    // used for non-cash-control payapps
    protected ArrayList<NonAppliedHolding> nonAppliedHoldingsForCustomer; // control docs for non-cash-control payapps

    public PaymentApplicationDocument() {
        super();
        this.invoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
        this.nonInvoiceds = new ArrayList<NonInvoiced>();
        this.nonInvoicedDistributions = new ArrayList<NonInvoicedDistribution>();
        this.nonAppliedDistributions = new ArrayList<NonAppliedDistribution>();
        this.nonAppliedHoldingsForCustomer = new ArrayList<NonAppliedHolding>();
    }

    /**
     * Returns the PaymentMediumIdentifier on the associated CashControlDetail,
     * if one exists, otherwise returns null.
     * @return CustomerPaymentMediumIdentifier from the associated CashControlDetail if
     *         one exists, otherwise null.
     */
    public String getPaymentNumber() {
        return hasCashControlDetail() ? getCashControlDetail().getCustomerPaymentMediumIdentifier() : null;
    }

    public boolean hasCashControlDocument() {
        return (getCashControlDocument() != null);
    }

    /**
     * @return
     * @throws WorkflowException
     */
    public CashControlDocument getCashControlDocument() {
        CashControlDetail cashControlDetail = getCashControlDetail();
        if(ObjectUtils.isNull(cashControlDetail)) {
            return null;
        }
        return cashControlDetail.getCashControlDocument();
    }

    public boolean hasCashControlDetail() {
        return (null != getCashControlDetail());
    }

    /**
     * @return
     * @throws WorkflowException
     */
    public CashControlDetail getCashControlDetail() {
        if (cashControlDetail == null) {
            cashControlDetail = getPaymentApplicationDocumentService().getCashControlDetailForPayAppDocNumber(getDocumentNumber());
        }
        return cashControlDetail;
    }

    public void setCashControlDetail(CashControlDetail cashControlDetail) {
        this.cashControlDetail = cashControlDetail;
    }

    /**
     * This method calculates the total amount available to be applied on this document.
     *
     * @return The total from the cash control detail if this is a
     * cash-control based payapp.  Otherwise, it just returns the total
     * available to be applied from previously unapplied holdings.
     */
    public KualiDecimal getTotalFromControl() {
        if (hasCashControlDetail()) {
            return getCashControlDetail().getFinancialDocumentLineAmount();
        }
        else {
            return getNonAppliedControlAvailableUnappliedAmount();
        }
    }

    /**
     * This method calculates the total amount available to be applied from
     * previously unapplied funds for the associated customer.
     *
     * @return The total amount of previously NonApplied funds available
     * to apply to invoices and other applications on this document.
     */
    public KualiDecimal getNonAppliedControlAvailableUnappliedAmount() {
        KualiDecimal amount = KualiDecimal.ZERO;
        for (NonAppliedHolding nonAppliedHolding : nonAppliedHoldingsForCustomer) {
            amount = amount.add(nonAppliedHolding.getAvailableUnappliedAmount());
        }
        return amount;
    }

    /**
     * @return the sum of all invoice paid applieds.
     */
    public KualiDecimal getSumOfInvoicePaidApplieds() {
        KualiDecimal amount = KualiDecimal.ZERO;
        for(InvoicePaidApplied payment : getInvoicePaidApplieds()) {
            KualiDecimal _amount = payment.getInvoiceItemAppliedAmount();
            if (null == _amount) { _amount = KualiDecimal.ZERO; }
            amount = amount.add(_amount);
        }
        return amount;
    }

    /**
     * @return the sum of all non-invoiced amounts
     */
    public KualiDecimal getSumOfNonInvoiceds() {
        KualiDecimal total = KualiDecimal.ZERO;
        for(NonInvoiced payment : getNonInvoiceds()) {
            total = total.add(payment.getFinancialDocumentLineAmount());
        }
        return total;
    }

    /**
     * @return the sum of all non-invoiced distributions
     */
    public KualiDecimal getSumOfNonInvoicedDistributions() {
        KualiDecimal amount = KualiDecimal.ZERO;
        for(NonInvoicedDistribution nonInvoicedDistribution : getNonInvoicedDistributions()) {
            amount = amount.add(nonInvoicedDistribution.getFinancialDocumentLineAmount());
        }
        return amount;
    }

    /**
     * @return the sum of all non-applied distributions
     */
    public KualiDecimal getSumOfNonAppliedDistributions() {
        KualiDecimal amount = KualiDecimal.ZERO;
        for(NonAppliedDistribution nonAppliedDistribution : getNonAppliedDistributions()) {
            amount = amount.add(nonAppliedDistribution.getFinancialDocumentLineAmount());
        }
        return amount;
    }

    /**
     * @return the non-applied holding total.
     */
    public KualiDecimal getNonAppliedHoldingAmount() {
        if(ObjectUtils.isNull(getNonAppliedHolding())) {
            return KualiDecimal.ZERO;
        }
        if(ObjectUtils.isNull(getNonAppliedHolding().getFinancialDocumentLineAmount())) {
            return KualiDecimal.ZERO;
        }
        return getNonAppliedHolding().getFinancialDocumentLineAmount();
    }

    /**
     * This method returns the total amount allocated against the cash
     * control total.
     *
     * @return
     */
    public KualiDecimal getTotalApplied() {
        KualiDecimal amount = KualiDecimal.ZERO;
        amount = amount.add(getSumOfInvoicePaidApplieds());
        amount = amount.add(getSumOfNonInvoiceds());
        amount = amount.add(getNonAppliedHoldingAmount());
        return amount;
    }

    /**
     * @see org.kuali.kfs.sys.document.AmountTotaling#getTotalDollarAmount()
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getTotalFromControl();
    }

    /**
     * This method subtracts the sum of the invoice paid applieds, non-ar and
     * unapplied totals from the outstanding amount received via the cash
     * control document.
     *
     * NOTE this method is not useful for a non-cash control PayApp, as it
     * doesnt have access to the control documents until it is saved.  Use
     * the same named method on the Form instead.
     *
     * @return
     * @throws WorkflowException
     */
    public KualiDecimal getUnallocatedBalance() {

        KualiDecimal amount = getTotalFromControl();
        amount = amount.subtract(getTotalApplied());
        return amount;
    }

    public KualiDecimal getNonArTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (NonInvoiced item : getNonInvoiceds()) {
            total = total.add(item.getFinancialDocumentLineAmount());
        }
        return total;
    }

    public boolean isFinal() {
        return isApproved();
    }

    public boolean isApproved() {
        return getDocumentHeader().getWorkflowDocument().isApproved();
    }

    /**
     *
     * This method is very specialized for a specific use.  It
     * retrieves the list of invoices that have been paid-applied
     * by this PayApp document.
     *
     * It is only used to retrieve what invoices were applied to it,
     * when the document is being viewed in Final state.
     *
     * @return
     */
    public List<CustomerInvoiceDocument> getInvoicesPaidAgainst() {
        List<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        //  short circuit if no paidapplieds available
        if (invoicePaidApplieds == null || invoicePaidApplieds.isEmpty()) {
            return invoices;
        }

        //  get the list of invoice docnumbers from paidapplieds
        List<String> invoiceDocNumbers = new ArrayList<String>();
        for (InvoicePaidApplied paidApplied : invoicePaidApplieds) {
            invoiceDocNumbers.add(paidApplied.getFinancialDocumentReferenceInvoiceNumber());
        }

        //  attempt to retrieve all the invoices paid applied against
        try {
            for ( Document doc : getDocService().getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, invoiceDocNumbers) ) {
                invoices.add( (CustomerInvoiceDocument) doc );
            }
        } catch (WorkflowException e) {
            throw new RuntimeException("A WorkflowException was thrown while trying to retrieve documents.", e);
        }
        return invoices;
    }

    /**
     *
     * This is a very specialized method, that is only intended to be used once the
     * document is in a Final/Approved state.
     *
     * It retrieves the PaymentApplication documents that were used as a control source
     * for this document, if any, or none, if none.
     *
     * @return
     */
    public List<PaymentApplicationDocument> getPaymentApplicationDocumentsUsedAsControlDocuments() {
        List<PaymentApplicationDocument> payApps = new ArrayList<PaymentApplicationDocument>();

        //  short circuit if no non-applied-distributions available
        if ((nonAppliedDistributions == null || nonAppliedDistributions.isEmpty()) &&
                (nonInvoicedDistributions == null || nonInvoicedDistributions.isEmpty())) {
            return payApps;
        }

        //  get the list of payapp docnumbers from non-applied-distributions
        List<String> payAppDocNumbers = new ArrayList<String>();
        for (NonAppliedDistribution nonAppliedDistribution : nonAppliedDistributions) {
            if (!payAppDocNumbers.contains(nonAppliedDistribution.getReferenceFinancialDocumentNumber())) {
                payAppDocNumbers.add(nonAppliedDistribution.getReferenceFinancialDocumentNumber());
            }
        }

        //  get the list of payapp docnumbers from non-applied-distributions
        for (NonInvoicedDistribution nonInvoicedDistribution : nonInvoicedDistributions) {
            if (!payAppDocNumbers.contains(nonInvoicedDistribution.getReferenceFinancialDocumentNumber())) {
                payAppDocNumbers.add(nonInvoicedDistribution.getReferenceFinancialDocumentNumber());
            }
        }

        //  exit out if no results, dont even try to retrieve
        if (payAppDocNumbers.isEmpty()) {
            return payApps;
        }

        //  attempt to retrieve all the invoices paid applied against
        try {
            for ( Document doc : getDocService().getDocumentsByListOfDocumentHeaderIds(PaymentApplicationDocument.class, payAppDocNumbers) ) {
                payApps.add( (PaymentApplicationDocument) doc );
            }
        } catch (WorkflowException e) {
            throw new RuntimeException("A WorkflowException was thrown while trying to retrieve documents.", e);
        }
        return payApps;
    }

    public List<NonAppliedHolding> getNonAppliedHoldingsUsedAsControls() {
        List<NonAppliedHolding> nonAppliedHoldingControls = new ArrayList<NonAppliedHolding>();

        //  short circuit if no non-applied-distributions available
        if ((nonAppliedDistributions == null || nonAppliedDistributions.isEmpty()) &&
                (nonInvoicedDistributions == null || nonInvoicedDistributions.isEmpty())) {
            return nonAppliedHoldingControls;
        }

        //  get the list of payapp docnumbers from non-applied-distributions
        List<String> payAppDocNumbers = new ArrayList<String>();
        for (NonAppliedDistribution nonAppliedDistribution : nonAppliedDistributions) {
            if (!payAppDocNumbers.contains(nonAppliedDistribution.getReferenceFinancialDocumentNumber())) {
                payAppDocNumbers.add(nonAppliedDistribution.getReferenceFinancialDocumentNumber());
            }
        }

        //  get the list of non-invoiced/non-ar distro payapp doc numbers
        for (NonInvoicedDistribution nonInvoicedDistribution : nonInvoicedDistributions) {
            if (!payAppDocNumbers.contains(nonInvoicedDistribution.getReferenceFinancialDocumentNumber())) {
                payAppDocNumbers.add(nonInvoicedDistribution.getReferenceFinancialDocumentNumber());
            }
        }

        //  attempt to retrieve all the non applied holdings used as controls
        if (!payAppDocNumbers.isEmpty()) {
            nonAppliedHoldingControls.addAll(getNonAppliedHoldingService().getNonAppliedHoldingsByListOfDocumentNumbers(payAppDocNumbers));
        }
        return nonAppliedHoldingControls;
    }

    public List<InvoicePaidApplied> getInvoicePaidApplieds() {
        return invoicePaidApplieds;
    }

    public void setInvoicePaidApplieds(List<InvoicePaidApplied> appliedPayments) {
        this.invoicePaidApplieds = appliedPayments;
    }

    public List<NonInvoiced> getNonInvoiceds() {
        return nonInvoiceds;
    }

    public void setNonInvoiceds(List<NonInvoiced> nonInvoiceds) {
        this.nonInvoiceds = nonInvoiceds;
    }

    public Collection<NonInvoicedDistribution> getNonInvoicedDistributions() {
        return nonInvoicedDistributions;
    }

    public void setNonInvoicedDistributions(Collection<NonInvoicedDistribution> nonInvoicedDistributions) {
        this.nonInvoicedDistributions = nonInvoicedDistributions;
    }

    public Collection<NonAppliedDistribution> getNonAppliedDistributions() {
        return nonAppliedDistributions;
    }

    public void setNonAppliedDistributions(Collection<NonAppliedDistribution> nonAppliedDistributions) {
        this.nonAppliedDistributions = nonAppliedDistributions;
    }

    public NonAppliedHolding getNonAppliedHolding() {
        return nonAppliedHolding;
    }

    public void setNonAppliedHolding(NonAppliedHolding nonAppliedHolding) {
        this.nonAppliedHolding = nonAppliedHolding;
    }

    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }

    /**
     * This method retrieves a specific applied payment from the list, by array index
     *
     * @param index the index of the applied payment to retrieve
     * @return an InvoicePaidApplied
     */
    public InvoicePaidApplied getInvoicePaidApplied(int index) {

        return index < getInvoicePaidApplieds().size() ? getInvoicePaidApplieds().get(index) : new InvoicePaidApplied();
    }

    /**
     * This method retrieves a specific non invoiced payment from the list, by array index
     *
     * @param index the index of the non invoiced payment to retrieve
     * @return an NonInvoiced
     */
    public NonInvoiced getNonInvoiced(int index) {
        return index < getNonInvoiceds().size() ? getNonInvoiceds().get(index) : new NonInvoiced();
    }

    /**
     * This method gets an ObjectCode from an invoice document.
     *
     * @param invoicePaidApplied
     * @return
     * @throws WorkflowException
     */
    protected ObjectCode getInvoiceReceivableObjectCode(InvoicePaidApplied invoicePaidApplied) throws WorkflowException {
        CustomerInvoiceDocument customerInvoiceDocument = invoicePaidApplied.getCustomerInvoiceDocument();
        CustomerInvoiceDetail customerInvoiceDetail = invoicePaidApplied.getInvoiceDetail();
        ReceivableCustomerInvoiceDetail receivableInvoiceDetail = new ReceivableCustomerInvoiceDetail(customerInvoiceDetail, customerInvoiceDocument);
        ObjectCode objectCode = null;
        if(ObjectUtils.isNotNull(receivableInvoiceDetail) && ObjectUtils.isNotNull(receivableInvoiceDetail.getFinancialObjectCode())) {
            objectCode = receivableInvoiceDetail.getObjectCode();
        }
        return objectCode;
    }

    /**
     * @param sequenceHelper
     * @return the pending entries for the document
     */
    protected List<GeneralLedgerPendingEntry> createPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) throws WorkflowException {

        // Collection of all generated entries
        List<GeneralLedgerPendingEntry> generatedEntries = new ArrayList<GeneralLedgerPendingEntry>();

        // Get handles to the services we need
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        BalanceTypeService balanceTypeService = SpringContext.getBean(BalanceTypeService.class);
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        SystemInformationService systemInformationService = SpringContext.getBean(SystemInformationService.class);
        OffsetDefinitionService offsetDefinitionService = SpringContext.getBean(OffsetDefinitionService.class);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        // Current fiscal year
        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();

        // The processing chart and org comes from the the cash control document if there is one.
        // If the payment application document is created from scratch though, then we pull it
        // from the current user.  Note that we're not checking here that the user actually belongs
        // to a billing or processing org, we're assuming that is handled elsewhere.
        String processingChartCode = null;
        String processingOrganizationCode = null;
        if (hasCashControlDocument()) {
            processingChartCode = getCashControlDocument().getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
            processingOrganizationCode = getCashControlDocument().getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        }
        else {
            Person currentUser = GlobalVariables.getUserSession().getPerson();
            ChartOrgHolder userOrg = getFsUserService().getPrimaryOrganization(currentUser.getPrincipalId(), ArConstants.AR_NAMESPACE_CODE);
            processingChartCode = userOrg.getChartOfAccountsCode();
            processingOrganizationCode = userOrg.getOrganizationCode();
        }

        // Some information comes from the cash control document
        CashControlDocument cashControlDocument = getCashControlDocument();

        // Get the System Information
        SystemInformation unappliedSystemInformation =
            systemInformationService.getByProcessingChartOrgAndFiscalYear(
                    processingChartCode, processingOrganizationCode, currentFiscalYear);

        // Get the university clearing account
        unappliedSystemInformation.refreshReferenceObject("universityClearingAccount");
        Account universityClearingAccount = unappliedSystemInformation.getUniversityClearingAccount();

        // Get the university clearing object, object type and sub-object code
        String unappliedSubAccountNumber = unappliedSystemInformation.getUniversityClearingSubAccountNumber();
        String unappliedObjectCode = unappliedSystemInformation.getUniversityClearingObjectCode();
        String unappliedObjectTypeCode = unappliedSystemInformation.getUniversityClearingObject().getFinancialObjectTypeCode();
        String unappliedSubObjectCode = unappliedSystemInformation.getUniversityClearingSubObjectCode();

        // Get the object code for the university clearing account.
        SystemInformation universityClearingAccountSystemInformation =
            systemInformationService.getByProcessingChartOrgAndFiscalYear(
                    processingChartCode, processingOrganizationCode, currentFiscalYear);
        String universityClearingAccountObjectCode = universityClearingAccountSystemInformation.getUniversityClearingObjectCode();

        // Generate glpes for unapplied
        NonAppliedHolding holding = getNonAppliedHolding();
        if(ObjectUtils.isNotNull(holding)) {
            GeneralLedgerPendingEntry actualCreditUnapplied = new GeneralLedgerPendingEntry();
            actualCreditUnapplied.setUniversityFiscalYear(getPostingYear());
            actualCreditUnapplied.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            actualCreditUnapplied.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            actualCreditUnapplied.setAccountNumber(universityClearingAccount.getAccountNumber());
            actualCreditUnapplied.setFinancialObjectCode(unappliedObjectCode);
            actualCreditUnapplied.setFinancialObjectTypeCode(unappliedObjectTypeCode);
            actualCreditUnapplied.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            actualCreditUnapplied.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            actualCreditUnapplied.setTransactionLedgerEntryAmount(holding.getFinancialDocumentLineAmount());
            if (StringUtils.isBlank(unappliedSubAccountNumber)) {
                actualCreditUnapplied.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            else {
                actualCreditUnapplied.setSubAccountNumber(unappliedSubAccountNumber);
            }
            if (StringUtils.isBlank(unappliedSubObjectCode)) {
                actualCreditUnapplied.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            else {
                actualCreditUnapplied.setFinancialSubObjectCode(unappliedSubObjectCode);
            }
            actualCreditUnapplied.setProjectCode(KFSConstants.getDashProjectCode());
            actualCreditUnapplied.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            actualCreditUnapplied.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(actualCreditUnapplied);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetDebitUnapplied = new GeneralLedgerPendingEntry();
            offsetDebitUnapplied.setUniversityFiscalYear(actualCreditUnapplied.getUniversityFiscalYear());
            offsetDebitUnapplied.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            offsetDebitUnapplied.setChartOfAccountsCode(actualCreditUnapplied.getChartOfAccountsCode());
            offsetDebitUnapplied.setAccountNumber(actualCreditUnapplied.getAccountNumber());
            OffsetDefinition offsetDebitDefinition =
                offsetDefinitionService.getByPrimaryId(
                    getPostingYear(), universityClearingAccount.getChartOfAccountsCode(),
                    ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
            offsetDebitDefinition.refreshReferenceObject("financialObject");
            offsetDebitUnapplied.setFinancialObjectCode(offsetDebitDefinition.getFinancialObjectCode());
            offsetDebitUnapplied.setFinancialObjectTypeCode(offsetDebitDefinition.getFinancialObject().getFinancialObjectTypeCode());
            offsetDebitUnapplied.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            offsetDebitUnapplied.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            offsetDebitUnapplied.setTransactionLedgerEntryAmount(actualCreditUnapplied.getTransactionLedgerEntryAmount());
            offsetDebitUnapplied.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            offsetDebitUnapplied.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            offsetDebitUnapplied.setProjectCode(KFSConstants.getDashProjectCode());
            offsetDebitUnapplied.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            offsetDebitUnapplied.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(offsetDebitUnapplied);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry actualDebitUnapplied = new GeneralLedgerPendingEntry();
            actualDebitUnapplied.setUniversityFiscalYear(getPostingYear());
            actualDebitUnapplied.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            actualDebitUnapplied.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            actualDebitUnapplied.setAccountNumber(universityClearingAccount.getAccountNumber());
            actualDebitUnapplied.setFinancialObjectCode(unappliedObjectCode);
            actualDebitUnapplied.setFinancialObjectTypeCode(unappliedObjectTypeCode);
            actualDebitUnapplied.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            actualDebitUnapplied.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            actualDebitUnapplied.setTransactionLedgerEntryAmount(holding.getFinancialDocumentLineAmount());
            if (StringUtils.isBlank(unappliedSubAccountNumber)) {
                actualDebitUnapplied.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            else {
                actualDebitUnapplied.setSubAccountNumber(unappliedSubAccountNumber);
            }
            actualDebitUnapplied.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            actualDebitUnapplied.setProjectCode(KFSConstants.getDashProjectCode());
            actualDebitUnapplied.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            actualDebitUnapplied.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(actualDebitUnapplied);
            sequenceHelper.increment();

            // Offsets for unapplied entries are just offsets to themselves, same info.
            // So set the values into the offsets based on the values in the actuals.
            GeneralLedgerPendingEntry offsetCreditUnapplied = new GeneralLedgerPendingEntry();
            offsetCreditUnapplied.setUniversityFiscalYear(actualDebitUnapplied.getUniversityFiscalYear());
            offsetCreditUnapplied.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            offsetCreditUnapplied.setChartOfAccountsCode(actualDebitUnapplied.getChartOfAccountsCode());
            offsetCreditUnapplied.setAccountNumber(actualDebitUnapplied.getAccountNumber());
            OffsetDefinition offsetCreditDefinition =
                offsetDefinitionService.getByPrimaryId(
                    getPostingYear(), universityClearingAccount.getChartOfAccountsCode(),
                    ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
            offsetCreditDefinition.refreshReferenceObject("financialObject");
            offsetCreditUnapplied.setFinancialObjectCode(offsetCreditDefinition.getFinancialObjectCode());
            offsetCreditUnapplied.setFinancialObjectTypeCode(offsetCreditDefinition.getFinancialObject().getFinancialObjectTypeCode());
            offsetCreditUnapplied.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            offsetCreditUnapplied.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            offsetCreditUnapplied.setTransactionLedgerEntryAmount(actualDebitUnapplied.getTransactionLedgerEntryAmount());
            offsetCreditUnapplied.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            offsetCreditUnapplied.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            offsetCreditUnapplied.setProjectCode(KFSConstants.getDashProjectCode());
            offsetCreditUnapplied.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            offsetCreditUnapplied.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(offsetCreditUnapplied);
            sequenceHelper.increment();
        }

        // Generate glpes for non-ar
        for(NonInvoiced nonInvoiced : getNonInvoiceds()) {
            // Actual entries
            GeneralLedgerPendingEntry actualCreditEntry = new GeneralLedgerPendingEntry();
            actualCreditEntry.setUniversityFiscalYear(getPostingYear());
            actualCreditEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            actualCreditEntry.setChartOfAccountsCode(nonInvoiced.getChartOfAccountsCode());
            actualCreditEntry.setAccountNumber(nonInvoiced.getAccountNumber());
            actualCreditEntry.setFinancialObjectCode(nonInvoiced.getFinancialObjectCode());
            nonInvoiced.refreshReferenceObject("financialObject");
            actualCreditEntry.setFinancialObjectTypeCode(nonInvoiced.getFinancialObject().getFinancialObjectTypeCode());
            actualCreditEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            actualCreditEntry.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            actualCreditEntry.setTransactionLedgerEntryAmount(nonInvoiced.getFinancialDocumentLineAmount());
            if (StringUtils.isBlank(nonInvoiced.getSubAccountNumber())) {
                actualCreditEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            else {
                actualCreditEntry.setSubAccountNumber(nonInvoiced.getSubAccountNumber());
            }
            if (StringUtils.isBlank(nonInvoiced.getFinancialSubObjectCode())) {
                actualCreditEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            else {
                actualCreditEntry.setFinancialSubObjectCode(nonInvoiced.getFinancialSubObjectCode());
            }
            if (StringUtils.isBlank(nonInvoiced.getProjectCode())) {
                actualCreditEntry.setProjectCode(KFSConstants.getDashProjectCode());
            }
            else {
                actualCreditEntry.setProjectCode(nonInvoiced.getProjectCode());
            }
            actualCreditEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            actualCreditEntry.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(actualCreditEntry);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry actualDebitEntry = new GeneralLedgerPendingEntry();
            actualDebitEntry.setUniversityFiscalYear(getPostingYear());
            actualDebitEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            actualDebitEntry.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            actualDebitEntry.setAccountNumber(universityClearingAccount.getAccountNumber());

            if (hasCashControlDocument()) {
                actualDebitEntry.setFinancialObjectCode(universityClearingAccountObjectCode);
                ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);
                ObjectCode objectCode = objectCodeService.getByPrimaryIdWithCaching(getPostingYear(),  universityClearingAccountSystemInformation.getUniversityClearingChartOfAccounts().getChartOfAccountsCode(), universityClearingAccountObjectCode);
                actualDebitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
                actualDebitEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            else {
                actualDebitEntry.setFinancialObjectCode(unappliedObjectCode);
                actualDebitEntry.setFinancialObjectTypeCode(unappliedObjectTypeCode);
                if (StringUtils.isBlank(unappliedSubObjectCode)) {
                    actualDebitEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                }
                else {
                    actualDebitEntry.setFinancialSubObjectCode(unappliedSubObjectCode);
                }
            }
            actualDebitEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            actualDebitEntry.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            actualDebitEntry.setTransactionLedgerEntryAmount(nonInvoiced.getFinancialDocumentLineAmount());
            if (StringUtils.isBlank(unappliedSubAccountNumber)) {
                actualDebitEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            else {
                actualDebitEntry.setSubAccountNumber(unappliedSubAccountNumber);
            }
            actualDebitEntry.setProjectCode(KFSConstants.getDashProjectCode());
            actualDebitEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            actualDebitEntry.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(actualDebitEntry);
            sequenceHelper.increment();

            // Offset entries
            GeneralLedgerPendingEntry offsetDebitEntry = new GeneralLedgerPendingEntry();
            offsetDebitEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            offsetDebitEntry.setChartOfAccountsCode(nonInvoiced.getChartOfAccountsCode());
            offsetDebitEntry.setAccountNumber(nonInvoiced.getAccountNumber());
            offsetDebitEntry.setUniversityFiscalYear(getPostingYear());
            OffsetDefinition debitOffsetDefinition =
                offsetDefinitionService.getByPrimaryId(
                    getPostingYear(), nonInvoiced.getChartOfAccountsCode(),
                    ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
            debitOffsetDefinition.refreshReferenceObject("financialObject");
            offsetDebitEntry.setFinancialObjectCode(debitOffsetDefinition.getFinancialObjectCode());
            offsetDebitEntry.setFinancialObjectTypeCode(debitOffsetDefinition.getFinancialObject().getFinancialObjectTypeCode());
            offsetDebitEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            offsetDebitEntry.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            offsetDebitEntry.setTransactionLedgerEntryAmount(nonInvoiced.getFinancialDocumentLineAmount());
            offsetDebitEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            offsetDebitEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            offsetDebitEntry.setProjectCode(KFSConstants.getDashProjectCode());
            offsetDebitEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            offsetDebitEntry.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(offsetDebitEntry);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetCreditEntry = new GeneralLedgerPendingEntry();
            offsetCreditEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            offsetCreditEntry.setUniversityFiscalYear(getPostingYear());
            offsetCreditEntry.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            offsetCreditEntry.setAccountNumber(universityClearingAccount.getAccountNumber());
            Integer fiscalYearForCreditOffsetDefinition = null == cashControlDocument ? currentFiscalYear : cashControlDocument.getUniversityFiscalYear();
            OffsetDefinition creditOffsetDefinition =
                offsetDefinitionService.getByPrimaryId(
                        fiscalYearForCreditOffsetDefinition, processingChartCode,
                        ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
            creditOffsetDefinition.refreshReferenceObject("financialObject");
            offsetCreditEntry.setFinancialObjectCode(creditOffsetDefinition.getFinancialObjectCode());
            offsetCreditEntry.setFinancialObjectTypeCode(creditOffsetDefinition.getFinancialObject().getFinancialObjectTypeCode());
            offsetCreditEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            offsetCreditEntry.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            offsetCreditEntry.setTransactionLedgerEntryAmount(nonInvoiced.getFinancialDocumentLineAmount());
            offsetCreditEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            offsetCreditEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            offsetCreditEntry.setProjectCode(KFSConstants.getDashProjectCode());
            offsetCreditEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            offsetCreditEntry.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(offsetCreditEntry);
            sequenceHelper.increment();
        }

        // Generate GLPEs for applied payments
        List<InvoicePaidApplied> appliedPayments = getInvoicePaidApplieds();
        for(InvoicePaidApplied ipa : appliedPayments) {

            // Skip payments for 0 dollar amount
            if(KualiDecimal.ZERO.equals(ipa.getInvoiceItemAppliedAmount())) {
                continue;
            }

            ipa.refreshNonUpdateableReferences();
            Account billingOrganizationAccount = ipa.getInvoiceDetail().getAccount();
            ObjectCode invoiceObjectCode = getInvoiceReceivableObjectCode(ipa);
            ObjectUtils.isNull(invoiceObjectCode); // Refresh
            ObjectCode accountsReceivableObjectCode = ipa.getAccountsReceivableObjectCode();
            ObjectCode unappliedCashObjectCode = ipa.getSystemInformation().getUniversityClearingObject();

            GeneralLedgerPendingEntry actualDebitEntry = new GeneralLedgerPendingEntry();
            actualDebitEntry.setUniversityFiscalYear(getPostingYear());
            actualDebitEntry.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            actualDebitEntry.setAccountNumber(universityClearingAccount.getAccountNumber());
            actualDebitEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            actualDebitEntry.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            if (hasCashControlDocument()) {
                actualDebitEntry.setFinancialObjectCode(unappliedCashObjectCode.getFinancialObjectCode());
                actualDebitEntry.setFinancialObjectTypeCode(unappliedCashObjectCode.getFinancialObjectTypeCode());
                actualDebitEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            else {
                actualDebitEntry.setFinancialObjectCode(unappliedObjectCode);
                actualDebitEntry.setFinancialObjectTypeCode(unappliedObjectTypeCode);
                if (StringUtils.isBlank(unappliedSubObjectCode)) {
                    actualDebitEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                }
                else {
                    actualDebitEntry.setFinancialSubObjectCode(unappliedSubObjectCode);
                }
            }
            if (StringUtils.isBlank(unappliedSubAccountNumber)) {
                actualDebitEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            else {
                actualDebitEntry.setSubAccountNumber(unappliedSubAccountNumber);
            }
            actualDebitEntry.setProjectCode(KFSConstants.getDashProjectCode());
            actualDebitEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            actualDebitEntry.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            actualDebitEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            actualDebitEntry.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(actualDebitEntry);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry actualCreditEntry = new GeneralLedgerPendingEntry();
            actualCreditEntry.setUniversityFiscalYear(getPostingYear());
            actualCreditEntry.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            actualCreditEntry.setAccountNumber(universityClearingAccount.getAccountNumber());
            actualCreditEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            actualCreditEntry.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            actualCreditEntry.setFinancialObjectCode(invoiceObjectCode.getFinancialObjectCode());
            actualCreditEntry.setFinancialObjectTypeCode(invoiceObjectCode.getFinancialObjectTypeCode());
            actualCreditEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            actualCreditEntry.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            actualCreditEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            actualCreditEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            actualCreditEntry.setProjectCode(KFSConstants.getDashProjectCode());
            glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), actualDebitEntry, sequenceHelper, actualCreditEntry);
            actualCreditEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            generatedEntries.add(actualCreditEntry);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetDebitEntry = new GeneralLedgerPendingEntry();
            offsetDebitEntry.setUniversityFiscalYear(getPostingYear());
            offsetDebitEntry.setAccountNumber(billingOrganizationAccount.getAccountNumber());
            offsetDebitEntry.setChartOfAccountsCode(billingOrganizationAccount.getChartOfAccountsCode());
            offsetDebitEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            offsetDebitEntry.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            offsetDebitEntry.setFinancialObjectCode(invoiceObjectCode.getFinancialObjectCode());
            offsetDebitEntry.setFinancialObjectTypeCode(invoiceObjectCode.getFinancialObjectTypeCode());
            offsetDebitEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            offsetDebitEntry.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            if (StringUtils.isBlank(ipa.getInvoiceDetail().getSubAccountNumber())) {
                offsetDebitEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            else {
                offsetDebitEntry.setSubAccountNumber(ipa.getInvoiceDetail().getSubAccountNumber());
            }

            offsetDebitEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            if (StringUtils.isBlank(ipa.getInvoiceDetail().getProjectCode())) {
                offsetDebitEntry.setProjectCode(KFSConstants.getDashProjectCode());
            }
            else {
                offsetDebitEntry.setProjectCode(ipa.getInvoiceDetail().getProjectCode());
            }
            offsetDebitEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            offsetDebitEntry.setTransactionLedgerEntryDescription(getDocumentHeader().getDocumentDescription());
            generatedEntries.add(offsetDebitEntry);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetCreditEntry = new GeneralLedgerPendingEntry();
            offsetCreditEntry.setUniversityFiscalYear(getPostingYear());
            offsetCreditEntry.setAccountNumber(billingOrganizationAccount.getAccountNumber());
            offsetCreditEntry.setChartOfAccountsCode(billingOrganizationAccount.getChartOfAccountsCode());
            offsetCreditEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            offsetCreditEntry.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            offsetCreditEntry.setFinancialObjectCode(accountsReceivableObjectCode.getFinancialObjectCode());
            offsetCreditEntry.setFinancialObjectTypeCode(accountsReceivableObjectCode.getFinancialObjectTypeCode());
            offsetCreditEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
            offsetCreditEntry.setFinancialDocumentTypeCode(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            if (StringUtils.isBlank(ipa.getInvoiceDetail().getSubAccountNumber())) {
                offsetCreditEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            else {
                offsetCreditEntry.setSubAccountNumber(ipa.getInvoiceDetail().getSubAccountNumber());
            }

            offsetCreditEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            offsetCreditEntry.setProjectCode(KFSConstants.getDashProjectCode());
            offsetCreditEntry.refreshNonUpdateableReferences();
            glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), offsetDebitEntry, sequenceHelper, offsetCreditEntry);
            generatedEntries.add(offsetCreditEntry);
            sequenceHelper.increment();
        }

        // Set the origination code for all entries.
        for(GeneralLedgerPendingEntry entry : generatedEntries) {
            entry.setFinancialSystemOriginationCode("01");
        }

        return generatedEntries;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        try {
            List<GeneralLedgerPendingEntry> entries = createPendingEntries(sequenceHelper);
            for(GeneralLedgerPendingEntry entry : entries) {
                addPendingEntry(entry);
            }
        } catch(Throwable t) {
            LOG.error("Exception encountered while generating pending entries.",t);
            return false;
        }

        return true;
    }

    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    @Override
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail) {
        return null;
    }

    @Override
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        return new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
    }

    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }

    /**
     *
     * This method is used ONLY for handleRouteStatus change and other
     * postProcessor related tasks (like getWorkflowEngineDocumentIdsToLock())
     * and should not otherwise be used.  The reason this is its own method
     * is to make sure that handleRouteStatusChange and
     * getWorkflowEngineDocumentIdsToLock use the same method to retrieve
     * what invoices to update.
     * @return
     */
    protected List<String> getInvoiceNumbersToUpdateOnFinal() {
        List<String> docIds = new ArrayList<String>();
        for(InvoicePaidApplied ipa : getInvoicePaidApplieds()) {
            docIds.add(ipa.getFinancialDocumentReferenceInvoiceNumber());
        }
        return docIds;
    }

    @Override
    public List<String> getWorkflowEngineDocumentIdsToLock() {
        List<String> docIdStrings = getInvoiceNumbersToUpdateOnFinal();
        if (docIdStrings == null || docIdStrings.isEmpty()) {
            return null;
        }
        return docIdStrings;
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        if(getDocumentHeader().getWorkflowDocument().isFinal()) {
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

            //  get the now time to stamp invoices with
            java.sql.Date today = new java.sql.Date(dateTimeService.getCurrentDate().getTime());

            List<String> invoiceDocNumbers = getInvoiceNumbersToUpdateOnFinal();
            for(String invoiceDocumentNumber : invoiceDocNumbers) {
                CustomerInvoiceDocument invoice = null;

                //  attempt to retrieve the invoice doc
                try {
                     invoice = (CustomerInvoiceDocument) getDocService().getByDocumentHeaderId(invoiceDocumentNumber);
                } catch(WorkflowException we) {
                    LOG.error("Failed to load the Invoice document due to a WorkflowException.", we);
                }
                if (invoice == null) {
                    throw new RuntimeException("DocumentService returned a Null CustomerInvoice Document for Doc# " + invoiceDocumentNumber + ".");
                }

                // KULAR-384 - close the invoice if its open and the openAmount is zero
                if (invoice.getOpenAmount().isZero() && invoice.isOpenInvoiceIndicator()) {
                    invoice.setClosedDate(today);
                    invoice.setOpenInvoiceIndicator(false);
                    getDocService().updateDocument(invoice);
                }
            }
        }
    }

    @Override
    public List buildListOfDeletionAwareLists() {
        List deletionAwareLists = super.buildListOfDeletionAwareLists();
        if (invoicePaidApplieds != null) { deletionAwareLists.add(invoicePaidApplieds); }
        if (nonInvoiceds != null) { deletionAwareLists.add(nonInvoiceds); }
        if (nonInvoicedDistributions != null) { deletionAwareLists.add(nonInvoicedDistributions); }
        if (nonAppliedDistributions != null) { deletionAwareLists.add(nonAppliedDistributions); }
        return deletionAwareLists;
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);

        // set primary key for NonAppliedHolding if data entered
        if (ObjectUtils.isNotNull(this.nonAppliedHolding)) {
            if (ObjectUtils.isNull(this.nonAppliedHolding.getReferenceFinancialDocumentNumber())) {
                this.nonAppliedHolding.setReferenceFinancialDocumentNumber(this.documentNumber);
            }
        }

        //  generate GLPEs only when routing or blanket approving
        if (event instanceof RouteDocumentEvent || event instanceof BlanketApproveDocumentEvent) {
            // if this document is not generated thru CashControl,
            // create nonApplied and nonInvoiced Distributions
            if (!this.hasCashControlDetail()) {
                createDistributions();
            }

            GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
            if (!glpeService.generateGeneralLedgerPendingEntries(this)) {
                logErrors();
                throw new ValidationException("general ledger GLPE generation failed");
            }
        }

    }

    public PaymentApplicationDocumentService getPaymentApplicationDocumentService() {
        if(null == paymentApplicationDocumentService) {
            paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        }
        return paymentApplicationDocumentService;
    }

    protected FinancialSystemUserService getFsUserService() {
        if (fsUserService == null) {
            fsUserService = SpringContext.getBean(FinancialSystemUserService.class);
        }
        return fsUserService;
    }

    protected CustomerInvoiceDocumentService getInvoiceDocService() {
        if (invoiceDocService == null) {
            invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        }
        return invoiceDocService;
    }

    protected DocumentService getDocService() {
        if (docService == null) {
            docService = SpringContext.getBean(DocumentService.class);
        }
        return docService;
    }

    protected NonAppliedHoldingService getNonAppliedHoldingService() {
        if (nonAppliedHoldingService == null) {
            nonAppliedHoldingService = SpringContext.getBean(NonAppliedHoldingService.class);
        }
        return nonAppliedHoldingService;
    }

    protected BusinessObjectService getBoService() {
        if (boService == null) {
            boService = SpringContext.getBean(BusinessObjectService.class);
        }
        return boService;
    }

    public String getHiddenFieldForErrors() {
        return hiddenFieldForErrors;
    }

    public void setHiddenFieldForErrors(String hiddenFieldForErrors) {
        this.hiddenFieldForErrors = hiddenFieldForErrors;
    }

    /**
     *
     * Retrieves the NonApplied Holdings that are the Controls for this
     * PaymentApplication.
     *
     * Note that this is dangerous to use and should not be relied upon.
     * The data is never persisted to the database, so will always be
     * null/empty when retrieved fresh.  It is only populated while the
     * document is live from the website, or while its in flight in workflow, due
     * to the fact that it has been serialized.
     *
     * You should probably not be using this method unless you are sure you know
     * what you are doing.
     *
     * @return
     */
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer() {
        return nonAppliedHoldingsForCustomer;
    }

    /**
     *
     * Warning, this property is not ever persisted to the database, and is only
     * used during workflow processing (since its been serialized) and during
     * presentation of the document on the webapp.
     *
     * You should probably not be using this method unless you are sure you know
     * what you are doing.
     *
     * @param nonApplieds
     */
    public void setNonAppliedHoldingsForCustomer(ArrayList<NonAppliedHolding> nonApplieds) {
        this.nonAppliedHoldingsForCustomer = nonApplieds;
    }

    /**
     *
     * Collects and returns the combined distributions from NonInvoiced/NonAr and Unapplied.
     *
     * This method is intended to be used only when the document has gone to final, to show what
     * control documents were issued what funds.
     *
     * The return value is a Map<String,KualiDecimal> where the key is the NonAppliedHolding's
     * ReferenceFinancialDocumentNumber and the value is the Amount to be applied.
     *
     * @return
     */
    public Map<String,KualiDecimal> getDistributionsFromControlDocuments() {
        if (!isFinal()) {
            throw new UnsupportedOperationException("This method should only be used once the document has been approved/gone to final.");
        }

        Map<String,KualiDecimal> distributions = new HashMap<String,KualiDecimal>();

        //  short circuit if no non-applied-distributions available
        if ((nonAppliedDistributions == null || nonAppliedDistributions.isEmpty()) &&
                (nonInvoicedDistributions == null || nonInvoicedDistributions.isEmpty())) {
            return distributions;
        }

        //  get the list of payapp docnumbers from non-applied-distributions
        for (NonAppliedDistribution nonAppliedDistribution : nonAppliedDistributions) {
            String refDocNbr = nonAppliedDistribution.getReferenceFinancialDocumentNumber();
            if (distributions.containsKey(refDocNbr)) {
                distributions.put(refDocNbr, (distributions.get(refDocNbr).add(nonAppliedDistribution.getFinancialDocumentLineAmount())));
            }
            else {
                distributions.put(refDocNbr, nonAppliedDistribution.getFinancialDocumentLineAmount());
            }
        }

        //  get the list of payapp docnumbers from non-applied-distributions
        for (NonInvoicedDistribution nonInvoicedDistribution : nonInvoicedDistributions) {
            String refDocNbr = nonInvoicedDistribution.getReferenceFinancialDocumentNumber();
            if (distributions.containsKey(refDocNbr)) {
                distributions.put(refDocNbr, (distributions.get(refDocNbr).add(nonInvoicedDistribution.getFinancialDocumentLineAmount())));
            }
            else {
                distributions.put(refDocNbr, nonInvoicedDistribution.getFinancialDocumentLineAmount());
            }
        }

        return distributions;
    }

    /**
     *
     *  Walks through the nonAppliedHoldings passed in (the control docs) and allocates how the
     *  funding should be allocated.
     *
     *  This function is intended to be used when the document is still live, ie not for when its
     *  been finalized.
     *
     *  The return value is a Map<String,KualiDecimal> where the key is the NonAppliedHolding's
     *  ReferenceFinancialDocumentNumber and the value is the Amount to be applied.
     *
     */
    public Map<String,KualiDecimal> allocateFundsFromUnappliedControls(List<NonAppliedHolding> nonAppliedHoldings, KualiDecimal amountToBeApplied) {
        if (nonAppliedHoldings == null) {
            throw new IllegalArgumentException("A null value for the parameter [nonAppliedHoldings] was passed in.");
        }
        if (amountToBeApplied == null) {
            throw new IllegalArgumentException("A null ovalue for the parameter [amountToBeApplied] was passed in.");
        }
        if (isFinal()) {
            throw new UnsupportedOperationException("This method should not be used when the document has been approved/gone to final.");
        }

        // special-case the situation where the amountToBeApplied is negative, then make all allocations zero
        if (amountToBeApplied.isNegative()) {
            Map<String,KualiDecimal> allocations = new HashMap<String,KualiDecimal>();
            for (NonAppliedHolding nonAppliedHolding : nonAppliedHoldings) {
                allocations.put(nonAppliedHolding.getReferenceFinancialDocumentNumber(), KualiDecimal.ZERO);
            }
            return allocations;
        }

        Map<String,KualiDecimal> allocations = new HashMap<String,KualiDecimal>();
        KualiDecimal remainingAmount = new KualiDecimal(amountToBeApplied.toString()); //clone it

        //  due to the way the control list is generated, this will result in applying
        // from the oldest to newest, which is the ordering desired.  If this ever changes,
        // then the internal logic here should be to apply to the oldest doc first, and then
        // move forward in time until you run out of money or docs
        for (NonAppliedHolding nonAppliedHolding : nonAppliedHoldings) {
            String refDocNumber = nonAppliedHolding.getReferenceFinancialDocumentNumber();

            //  this shouldnt ever happen, but lets sanity check it
            if (allocations.containsKey(nonAppliedHolding.getReferenceFinancialDocumentNumber())) {
                throw new RuntimeException("The same NonAppliedHolding RefDocNumber came up twice, which should never happen.");
            }
            else {
                allocations.put(refDocNumber, KualiDecimal.ZERO);
            }

            if (remainingAmount.isGreaterThan(KualiDecimal.ZERO)) {
                if (nonAppliedHoldings.iterator().hasNext()) {
                    if (remainingAmount.isLessEqual(nonAppliedHolding.getAvailableUnappliedAmount())) {
                        allocations.put(refDocNumber, remainingAmount);
                        remainingAmount = remainingAmount.subtract(remainingAmount);
                    }
                    else {
                        allocations.put(refDocNumber, nonAppliedHolding.getAvailableUnappliedAmount());
                        remainingAmount = remainingAmount.subtract(nonAppliedHolding.getAvailableUnappliedAmount());
                    }
                }
            }
        }
        return allocations;
    }

    // this method is only used by Unapplied PayApp.
    // create nonApplied and nonInvoiced Distributions
    public void createDistributions() {

        // if there are non nonApplieds, then we have nothing to do
        if (nonAppliedHoldingsForCustomer == null || nonAppliedHoldingsForCustomer.isEmpty()) {
            return;
        }

        Collection<InvoicePaidApplied> invoicePaidAppliedsForCurrentDoc = this.getInvoicePaidApplieds();
        Collection<NonInvoiced> nonInvoicedsForCurrentDoc = this.getNonInvoiceds();

        for(NonAppliedHolding nonAppliedHoldings : this.getNonAppliedHoldingsForCustomer()) {

            // check if payment has been applied to Invoices
            // create Unapplied Distribution for each PaidApplied
            KualiDecimal remainingUnappliedForDistribution = nonAppliedHoldings.getAvailableUnappliedAmount();
            for(InvoicePaidApplied invoicePaidAppliedForCurrentDoc : invoicePaidAppliedsForCurrentDoc) {
                KualiDecimal paidAppliedDistributionAmount = invoicePaidAppliedForCurrentDoc.getPaidAppiedDistributionAmount();
                KualiDecimal remainingPaidAppliedForDistribution = invoicePaidAppliedForCurrentDoc.getInvoiceItemAppliedAmount().subtract(paidAppliedDistributionAmount);
                if (remainingPaidAppliedForDistribution.equals(KualiDecimal.ZERO) ||
                    remainingUnappliedForDistribution.equals(KualiDecimal.ZERO)) {
                    continue;
                }

                    // set NonAppliedDistributions for the current document
                    NonAppliedDistribution nonAppliedDistribution = new NonAppliedDistribution();
                    nonAppliedDistribution.setDocumentNumber(invoicePaidAppliedForCurrentDoc.getDocumentNumber());
                    nonAppliedDistribution.setPaidAppliedItemNumber(invoicePaidAppliedForCurrentDoc.getPaidAppliedItemNumber());
                    nonAppliedDistribution.setReferenceFinancialDocumentNumber(nonAppliedHoldings.getReferenceFinancialDocumentNumber());
                    if (remainingPaidAppliedForDistribution.isLessEqual(remainingUnappliedForDistribution)) {
                        nonAppliedDistribution.setFinancialDocumentLineAmount(remainingPaidAppliedForDistribution);
                        remainingUnappliedForDistribution = remainingUnappliedForDistribution.subtract(remainingPaidAppliedForDistribution);
                        invoicePaidAppliedForCurrentDoc.setPaidAppiedDistributionAmount(paidAppliedDistributionAmount.add(remainingPaidAppliedForDistribution));
                    }
                    else {
                        nonAppliedDistribution.setFinancialDocumentLineAmount(remainingUnappliedForDistribution);
                        invoicePaidAppliedForCurrentDoc.setPaidAppiedDistributionAmount(paidAppliedDistributionAmount.add(remainingUnappliedForDistribution));
                        remainingUnappliedForDistribution = KualiDecimal.ZERO;
                    }
                    this.nonAppliedDistributions.add(nonAppliedDistribution);
            }

            // check if payment has been applied to NonAR
            // create NonAR distribution for each NonAR Applied row
            for(NonInvoiced nonInvoicedForCurrentDoc : nonInvoicedsForCurrentDoc) {
                KualiDecimal nonInvoicedDistributionAmount = nonInvoicedForCurrentDoc.getNonInvoicedDistributionAmount();
                KualiDecimal remainingNonInvoicedForDistribution = nonInvoicedForCurrentDoc.getFinancialDocumentLineAmount().subtract(nonInvoicedDistributionAmount);
                if (remainingNonInvoicedForDistribution.equals(KualiDecimal.ZERO) ||
                    remainingUnappliedForDistribution.equals(KualiDecimal.ZERO)) {
                    continue;
                }

                // set NonAppliedDistributions for the current document
                NonInvoicedDistribution nonInvoicedDistribution = new NonInvoicedDistribution();
                nonInvoicedDistribution.setDocumentNumber(nonInvoicedForCurrentDoc.getDocumentNumber());
                nonInvoicedDistribution.setFinancialDocumentLineNumber(nonInvoicedForCurrentDoc.getFinancialDocumentLineNumber());
                nonInvoicedDistribution.setReferenceFinancialDocumentNumber(nonAppliedHoldings.getReferenceFinancialDocumentNumber());
                if (remainingNonInvoicedForDistribution.isLessEqual(remainingUnappliedForDistribution)) {
                    nonInvoicedDistribution.setFinancialDocumentLineAmount(remainingNonInvoicedForDistribution);
                    remainingUnappliedForDistribution = remainingUnappliedForDistribution.subtract(remainingNonInvoicedForDistribution);
                    nonInvoicedForCurrentDoc.setNonInvoicedDistributionAmount(nonInvoicedDistributionAmount.add(remainingNonInvoicedForDistribution));
                }
                else {
                    nonInvoicedDistribution.setFinancialDocumentLineAmount(remainingUnappliedForDistribution);
                    nonInvoicedForCurrentDoc.setNonInvoicedDistributionAmount(nonInvoicedDistributionAmount.add(remainingUnappliedForDistribution));
                    remainingUnappliedForDistribution = KualiDecimal.ZERO;
                }
                this.nonInvoicedDistributions.add(nonInvoicedDistribution);
            }
        }
    }

    /**
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (LAUNCHED_FROM_BATCH.equals(nodeName)) {
            return launchedFromBatch();
        }
        throw new UnsupportedOperationException("answerSplitNode('" + nodeName + "') was called but no handler for nodeName specified.");
    }

    // determines if the doc was launched by SYSTEM_USER, if so, then it was launched from batch
    protected boolean launchedFromBatch() {
        boolean result = false;
        Principal initiator = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
        result = initiator.getPrincipalId().equalsIgnoreCase(getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        return result;
    }

    /** CUSTOM SEARCH HELPER METHODS **/

    /**
     *
     * This method is defined to assist in the custom search implementation.
     * @return
     */
    public String getUnappliedCustomerNumber() {
        if(nonAppliedHolding==null) {
            return "";
        }
        return nonAppliedHolding.getCustomerNumber();
    }

    /**
     *
     * This method is defined to assist in the custom search implementation.
     * @return
     */
    public String getUnappliedCustomerName() {
        if(nonAppliedHolding==null) {
            return "";
        }
        return nonAppliedHolding.getCustomer().getCustomerName();
    }

    /**
     *
     * This method is defined to assist in the custom search implementation.
     * @return
     */
    public String getInvoiceAppliedCustomerNumber() {
        return getAccountsReceivableDocumentHeader().getCustomerNumber();
    }

    /**
     *
     * This method is defined to assist in the custom search implementation.
     * @return
     */
    public String getInvoiceAppliedCustomerName() {
        return getAccountsReceivableDocumentHeader().getCustomer().getCustomerName();
    }

}

