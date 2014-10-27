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
package org.kuali.kfs.module.ar.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contracts Grants Invoice document extending Customer Invoice document.
 */

public class ContractsGrantsInvoiceDocument extends CustomerInvoiceDocument {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceDocument.class);
    private KualiDecimal paymentAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceDue = KualiDecimal.ZERO;
    private List<ContractsGrantsInvoiceDetail> invoiceDetails;
    private List<CollectionEvent> collectionEvents;
    private List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes;
    private List<InvoiceAddressDetail> invoiceAddressDetails;
    private List<InvoiceAccountDetail> accountDetails;
    private InvoiceGeneralDetail invoiceGeneralDetail;
    private List<InvoiceMilestone> invoiceMilestones;
    private List<InvoiceBill> invoiceBills;
    private List<InvoiceSuspensionCategory> invoiceSuspensionCategories;

    private final String REQUIRES_APPROVAL_SPLIT = "RequiresApprovalSplit";

    /**
     * Default constructor.
     */
    public ContractsGrantsInvoiceDocument() {

        invoiceAddressDetails = new ArrayList<InvoiceAddressDetail>();
        invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        collectionEvents = new ArrayList<CollectionEvent>();
        accountDetails = new ArrayList<InvoiceAccountDetail>();
        invoiceMilestones = new ArrayList<InvoiceMilestone>();
        invoiceBills = new ArrayList<InvoiceBill>();
        invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceSuspensionCategories = new ArrayList<InvoiceSuspensionCategory>();
    }

    /**
     * Gets the finalizable attribute.
     *
     * @return Returns the finalizable.
     */
    public boolean isFinalizable() {
        if (this == null) {
            return false;
        }
        return this.getDocumentHeader().getWorkflowDocument().getDateCreated().isAfter(getInvoiceGeneralDetail().getAward().getAwardEndingDate().getTime());
    }

    /**
     * This method returns true if this is a correction document
     *
     * @return is Error Correction Document
     */
    public boolean isCorrectionDocument() {
        return !StringUtils.isEmpty(this.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#prepareForSave()
     */

    @Override
    public void prepareForSave() {
        super.prepareForSave();
        // To do a recalculate of current expenditures in invoice details section so that the totals get affected properly.

        // To be performed whenever the document is saved only for awards without Milestones, Bills or LOC Billing
        if (!this.getInvoiceGeneralDetail().getBillingFrequencyCode().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !this.getInvoiceGeneralDetail().getBillingFrequencyCode().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && !this.getInvoiceGeneralDetail().getBillingFrequencyCode().equalsIgnoreCase(ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
            contractsGrantsInvoiceDocumentService.recalculateNewTotalBilled(this);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

        if ( getDocumentHeader().getWorkflowDocument().isProcessed() ) {
            // update award accounts to final billed
            contractsGrantsInvoiceDocumentService.updateLastBilledDate(this);
            if (isInvoiceReversal()) { // Invoice correction process when corrected invoice goes to FINAL
                try {
                    getInvoiceGeneralDetail().setFinalBillIndicator(false);
                    ContractsGrantsInvoiceDocument invoice = (ContractsGrantsInvoiceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(this.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber());
                    if (ObjectUtils.isNotNull(invoice)) {
                        invoice.setInvoiceDueDate(new Date(new java.util.Date().getTime()));
                        invoice.getInvoiceGeneralDetail().setFinalBillIndicator(false);
                        SpringContext.getBean(DocumentService.class).updateDocument(invoice);
                        // update correction to the AwardAccount Objects since the Invoice was unmarked as Final
                        contractsGrantsInvoiceDocumentService.updateUnfinalizationToAwardAccount(invoice.getAccountDetails(),invoice.getInvoiceGeneralDetail().getProposalNumber());
                        getInvoiceGeneralDetail().setLastBilledDate(null);// Set invoice last billed date to null.

                        if (invoice.getInvoiceGeneralDetail().getBillingFrequencyCode().equals(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
                            contractsGrantsInvoiceDocumentService.updateMilestonesBilledIndicator(false,invoice.getInvoiceMilestones());
                        }
                        else if (invoice.getInvoiceGeneralDetail().getBillingFrequencyCode().equals(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
                            contractsGrantsInvoiceDocumentService.updateBillsBilledIndicator(false,invoice.getInvoiceBills());
                        }
                    }
                    else {
                        GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CORRECTED_INVOICE_NOT_FOUND_ERROR, ArKeyConstants.CORRECTED_INVOICE_NOT_FOUND_ERROR);
                    }
                }
                catch (WorkflowException ex) {
                    LOG.error("problem during ContractsGrantsInvoiceDocument.doRouteStatusChange()", ex);
                    throw new RuntimeException("WorkflowException during ContractsGrantsInvoiceDocument.doRouteStatusChange()", ex);  // if KEW is down, how are we even here?  bad data that should no longer exist or something?
                }
            }
            else {
                // update Milestones and Bills when invoice goes to final state
                contractsGrantsInvoiceDocumentService.updateBillsAndMilestones(true, invoiceMilestones, invoiceBills);

                // generate the invoices from templates
                contractsGrantsInvoiceDocumentService.generateInvoicesForInvoiceAddresses(this);
            }

            contractsGrantsInvoiceDocumentService.addToAccountObjectCodeBilledTotal(invoiceDetailAccountObjectCodes);
        }

    }

    /**
     * Make changes here to implement what needs to be done when correction button is clicked
     *
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        invoiceSuspensionCategories.clear();
        SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).correctContractsGrantsInvoiceDocument(this);
    }

    /**
     * Gets the list of invoice Details without the Total fields or any indirect cost categories
     *
     * @return Returns the invoiceDetails.
     */
    public List<ContractsGrantsInvoiceDetail> getDirectCostInvoiceDetails() {
        List<ContractsGrantsInvoiceDetail> invDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        for (ContractsGrantsInvoiceDetail invD : invoiceDetails) {
            if (!invD.isIndirectCostIndicator()) {
                invDetails.add(invD);
            }
        }
        return invDetails;
    }

    /**
     * This method returns a list of invoice details which are indirect costs only.
     * These invoice details are not shown on the document and is different from the
     * other method getInDirectCostInvoiceDetails() because that method returns the total.
     */
    public List<ContractsGrantsInvoiceDetail> getIndirectCostInvoiceDetails(){
        List<ContractsGrantsInvoiceDetail> invDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        for (ContractsGrantsInvoiceDetail invD : invoiceDetails) {
            if (invD.isIndirectCostIndicator()) {
               invDetails.add(invD);
            }
        }
        return invDetails;
    }

    /**
     * Gets the invoiceDetails attribute value.
     *
     * @param invoiceDetails The invoiceDetails to set.
     */
    public List<ContractsGrantsInvoiceDetail> getInvoiceDetails() {
        return invoiceDetails;
    }

    /**
     * Sets the invoiceDetails attribute value.
     *
     * @param invoiceDetails The invoiceDetails to set.
     */
    public void setInvoiceDetails(List<ContractsGrantsInvoiceDetail> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }

    /**
     * @return
     */
    public List<InvoiceDetailAccountObjectCode> getInvoiceDetailAccountObjectCodes() {
        return invoiceDetailAccountObjectCodes;
    }

    /**
     * @param invoiceDetailAccountObjectCodes
     */
    public void setInvoiceDetailAccountObjectCodes(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        this.invoiceDetailAccountObjectCodes = invoiceDetailAccountObjectCodes;
    }

    /**
     * Gets the invoiceAddressDetails attribute.
     *
     * @return Returns the invoiceAddressDetails.
     */
    public List<InvoiceAddressDetail> getInvoiceAddressDetails() {
        return invoiceAddressDetails;
    }

    /**
     * Sets the invoiceAddressDetails attribute value.
     *
     * @param invoiceAddressDetails The invoiceAddressDetails to set.
     */
    public void setInvoiceAddressDetails(List<InvoiceAddressDetail> invoiceAddressDetails) {
        this.invoiceAddressDetails = invoiceAddressDetails;
    }

    /**
     * Gets the accountDetails attribute.
     *
     * @return Returns the accountDetails.
     */
    public List<InvoiceAccountDetail> getAccountDetails() {
        return accountDetails;
    }

    /**
     * Sets the accountDetails attribute value.
     *
     * @param accountDetails The accountDetails to set.
     */
    public void setAccountDetails(List<InvoiceAccountDetail> accountDetails) {
        this.accountDetails = accountDetails;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("invoiceGeneralDetail", invoiceGeneralDetail);
        return m;
    }

    /**
     * Gets the invoiceGeneralDetail attribute.
     *
     * @return Returns the invoiceGeneralDetail.
     */
    public InvoiceGeneralDetail getInvoiceGeneralDetail() {
        return invoiceGeneralDetail;
    }

    /**
     * Sets the invoiceGeneralDetail attribute value.
     *
     * @param invoiceGeneralDetail The invoiceGeneralDetail to set.
     */
    public void setInvoiceGeneralDetail(InvoiceGeneralDetail invoiceGeneralDetail) {
        this.invoiceGeneralDetail = invoiceGeneralDetail;
    }

    /**
     * Gets the invoiceMilestones attribute.
     *
     * @return Returns the invoiceMilestones.
     */
    public List<InvoiceMilestone> getInvoiceMilestones() {
        return invoiceMilestones;
    }

    /**
     * Sets the invoiceMilestones attribute value.
     *
     * @param invoiceMilestones The invoiceMilestones to set.
     */
    public void setInvoiceMilestones(List<InvoiceMilestone> invoiceMilestones) {
        this.invoiceMilestones = invoiceMilestones;
    }

    /**
     * Gets the invoiceBills attribute.
     *
     * @return Returns the invoiceBills.
     */
    public List<InvoiceBill> getInvoiceBills() {
        return invoiceBills;
    }

    /**
     * Sets the invoiceBills attribute value.
     *
     * @param invoiceBills The invoiceBills to set.
     */
    public void setInvoiceBills(List<InvoiceBill> invoiceBills) {
        this.invoiceBills = invoiceBills;
    }

    /**
     * Gets the Total Direct Cost InvoiceDetail.
     *
     * @return Returns the total direct Cost InvoiceDetails.
     */
    public ContractsGrantsInvoiceDetail getTotalDirectCostInvoiceDetail() {
        ContractsGrantsInvoiceDetail totalDirectCostInvoiceDetail = new ContractsGrantsInvoiceDetail();
        for (ContractsGrantsInvoiceDetail currentInvoiceDetail : getDirectCostInvoiceDetails()) {
            totalDirectCostInvoiceDetail.sumInvoiceDetail(currentInvoiceDetail);
        }
        return totalDirectCostInvoiceDetail;
    }

    /**
     * Gets the list of Collection Events.
     *
     * @return Returns the collectionEvents.
     */
    public List<CollectionEvent> getCollectionEvents() {
        return collectionEvents;
    }

    /**
     * Sets the list of Collection Events.
     *
     * @param events The collectionEvents to set.
     */
    public void setCollectionEvents(List<CollectionEvent> collectionEvents) {
        this.collectionEvents = collectionEvents;
    }

    /**
     * Generate the next Collection Event Code by concatenating a the count of current events +1 formatted, to the
     * document number for this invoice.
     *
     * @return next collection event code
     */
    public String getNextCollectionEventCode() {
        String nextCollectionEventCode = documentNumber + "-" + String.format("%03d", collectionEvents.size() + 1);
        return nextCollectionEventCode;
    }

    /**
     * Gets the total indirect cost InvoiceDetail
     *
     * @return Returns the total indirect cost InvoiceDetail.
     */
    public ContractsGrantsInvoiceDetail getTotalIndirectCostInvoiceDetail() {
        ContractsGrantsInvoiceDetail totalInDirectCostInvoiceDetail = new ContractsGrantsInvoiceDetail();
        for (ContractsGrantsInvoiceDetail currentInvoiceDetail : getIndirectCostInvoiceDetails()) {
            totalInDirectCostInvoiceDetail.sumInvoiceDetail(currentInvoiceDetail);
        }
        return totalInDirectCostInvoiceDetail;
    }

    /**
     * Gets the total cost InvoiceDetail.
     *
     * @return Returns the total cost InvoiceDetail.
     */
    public ContractsGrantsInvoiceDetail getTotalCostInvoiceDetail() {
        ContractsGrantsInvoiceDetail totalCostInvoiceDetail = new ContractsGrantsInvoiceDetail();
        totalCostInvoiceDetail.sumInvoiceDetail(getTotalDirectCostInvoiceDetail());
        totalCostInvoiceDetail.sumInvoiceDetail(getTotalIndirectCostInvoiceDetail());
        return totalCostInvoiceDetail;
    }

    /**
     * @return
     */
    public List<InvoiceSuspensionCategory> getInvoiceSuspensionCategories() {
        return invoiceSuspensionCategories;
    }

    /**
     * @param invoiceSuspensionCategories
     */
    public void setInvoiceSuspensionCategories(List<InvoiceSuspensionCategory> invoiceSuspensionCategories) {
        this.invoiceSuspensionCategories = invoiceSuspensionCategories;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(REQUIRES_APPROVAL_SPLIT)) {
            return isRequiresFundingManagerApproval();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * @return
     */
    private boolean isRequiresFundingManagerApproval() {
        // if auto approve on the award is false or suspension exists, then we need to have funds manager approve.
        boolean result;
        result = !getInvoiceGeneralDetail().getAward().getAutoApproveIndicator() || !this.getInvoiceSuspensionCategories().isEmpty();
        return result;
    }

    public KualiDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(KualiDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public KualiDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(KualiDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    public String getCustomerNumber() {
        return accountsReceivableDocumentHeader.getCustomerNumber();
    }

    public KualiDecimal getBudgetAmountTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (InvoiceAccountDetail accountDetail : accountDetails) {
            total = total.add(accountDetail.getBudgetAmount());
        }

        return total;
    }

    public KualiDecimal getExpenditureAmountTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (InvoiceAccountDetail accountDetail : accountDetails) {
            total = total.add(accountDetail.getExpenditureAmount());
        }
        return total;
    }

    public KualiDecimal getCumulativeAmountTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (InvoiceAccountDetail accountDetail : accountDetails) {
            total = total.add(accountDetail.getCumulativeAmount());
        }
        return total;
    }

    public KualiDecimal getBalanceAmountTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (InvoiceAccountDetail accountDetail : accountDetails) {
            total = total.add(accountDetail.getBalanceAmount());
        }
        return total;
    }

    public KualiDecimal getBilledAmountTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (InvoiceAccountDetail accountDetail : accountDetails) {
            total = total.add(accountDetail.getBilledAmount());
        }
        return total;
    }
}
