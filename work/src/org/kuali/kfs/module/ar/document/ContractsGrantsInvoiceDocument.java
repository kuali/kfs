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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.businessobject.FinalDisposition;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.ReferralType;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contracts Grants Invoice document extending Customer Invoice document.
 */

public class ContractsGrantsInvoiceDocument extends CustomerInvoiceDocument {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceDocument.class);
    private Long proposalNumber;
    private KualiDecimal paymentAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceDue = KualiDecimal.ZERO;
    private List<InvoiceDetail> invoiceDetails;
    private List<Event> events;
    private List<InvoiceDetail> directCostInvoiceDetails;
    private List<InvoiceDetail> inDirectCostInvoiceDetails;
    private List<InvoiceDetail> totalInvoiceDetails;
    private List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes;
    private List<InvoiceAddressDetail> invoiceAddressDetails;
    private List<InvoiceAccountDetail> accountDetails;
    private InvoiceGeneralDetail invoiceGeneralDetail;
    private List<InvoiceMilestone> invoiceMilestones;
    private List<InvoiceBill> invoiceBills;
    private List<InvoiceSuspensionCategory> invoiceSuspensionCategories;
    private ContractsAndGrantsBillingAward award;
    private static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("MM-dd-yyyy");

    protected String letterOfCreditCreationType;// To categorize the CG Invoices based on Award LOC Type
    protected String letterOfCreditFundGroupCode;
    protected String letterOfCreditFundCode;

    private String referralTypeCode;
    private String finalDispositionCode;

    private ReferralType referralType;
    private FinalDisposition finalDisposition;

    public java.util.Date dateReportProcessed;
    public Date paymentDate;
    public String markedForProcessing;
    private final String REQUIRES_APPROVAL_SPLIT = "RequiresApprovalSplit";
    private boolean showEventsInd = true;

    /**
     * Default constructor.
     */
    public ContractsGrantsInvoiceDocument() {

        invoiceAddressDetails = new ArrayList<InvoiceAddressDetail>();
        invoiceDetails = new ArrayList<InvoiceDetail>();
        events = new ArrayList<Event>();
        directCostInvoiceDetails = new ArrayList<InvoiceDetail>();
        inDirectCostInvoiceDetails = new ArrayList<InvoiceDetail>();
        totalInvoiceDetails = new ArrayList<InvoiceDetail>();
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
        return this.getDocumentHeader().getWorkflowDocument().getDateCreated().isAfter(this.getAward().getAwardEndingDate().getTime());
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
     * Gets the markedForProcessing attribute.
     *
     * @return Returns the markedForProcessing.
     */
    public String getMarkedForProcessing() {
        return markedForProcessing;
    }

    /**
     * Sets the markedForProcessing attribute value.
     *
     * @param markedForProcessing The markedForProcessing to set.
     */

    public void setMarkedForProcessing(String markedForProcessing) {
        this.markedForProcessing = markedForProcessing;
    }

    /**
     * Gets the dateReportProcessed attribute.
     *
     * @return Returns the dateReportProcessed.
     */
    public java.util.Date getDateReportProcessed() {
        return dateReportProcessed;
    }

    /**
     * Sets the dateReportProcessed attribute value.
     *
     * @param dateReportProcessed The dateReportProcessed to set.
     */
    public void setDateReportProcessed(java.util.Date date) {
        this.dateReportProcessed = date;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#prepareForSave()
     */

    @Override
    public void prepareForSave() {
        super.prepareForSave();
        // To do a recalculate of current expenditures in invoice details section so that the totals get affected properly.

        // To be performed whenever the document is saved only for awards without Milestones, Bills or LOC Billing
        if (!this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && !this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {
            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

            contractsGrantsInvoiceDocumentService.recalculateNewTotalBilled(this);


        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @SuppressWarnings("null")
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        // To set the status of the document to award account.
        setAwardAccountInvoiceDocumentStatus(this.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus());
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        // performed only when document is in final state
        if (getDocumentHeader().getWorkflowDocument().isFinal()) {

            // update award accounts to final billed
            contractsGrantsInvoiceDocumentService.doWhenFinalInvoice(this);
            if (this.isInvoiceReversal()) { // Invoice correction process when corrected invoice goes to FINAL
                try {
                    this.getInvoiceGeneralDetail().setFinalBillIndicator(false);
                    ContractsGrantsInvoiceDocument invoice = (ContractsGrantsInvoiceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(this.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber());
                    if (ObjectUtils.isNotNull(invoice)) {
                        invoice.setInvoiceDueDate(new java.sql.Date(new Date().getTime()));
                        invoice.getInvoiceGeneralDetail().setFinalBillIndicator(false);
                        SpringContext.getBean(DocumentService.class).updateDocument(invoice);
                        // update correction to the AwardAccount Objects since the Invoice was unmarked as Final
                        contractsGrantsInvoiceDocumentService.updateUnfinalizationToAwardAccount(invoice.getAccountDetails(),invoice.getProposalNumber());
                        // Update invoice, award account and award with the last billed Date.
                        String invoiceStatus = "CORRECTED";
                        contractsGrantsInvoiceDocumentService.updateLastBilledDate(invoiceStatus,this);// Setting award account and award last billed date
                        this.getInvoiceGeneralDetail().setLastBilledDate(null);// Set invoice last billed date to null.

                        if (invoice.getInvoiceGeneralDetail().getBillingFrequency().equals(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
                            contractsGrantsInvoiceDocumentService.correctMilestones(invoice.getInvoiceMilestones());
                        }
                        else if (invoice.getInvoiceGeneralDetail().getBillingFrequency().equals(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
                            contractsGrantsInvoiceDocumentService.correctBills(invoice.getInvoiceBills());
                        }
                    }
                    else {
                        GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CORRECTED_INVOICE_NOT_FOUND_ERROR, ArConstants.CORRECTED_INVOICE_NOT_FOUND_ERROR);
                    }
                }
                catch (WorkflowException ex) {
                    LOG.error("problem during ContractsGrantsInvoiceDocument.doRouteStatusChange()", ex);
                }
            }
            else {
                // update Milestones and Bills when invoice goes to final state
                contractsGrantsInvoiceDocumentService.updateBillsAndMilestones(KFSConstants.ParameterValues.STRING_YES,invoiceMilestones,invoiceBills);
                // Update invoice, award account and award with the last billed Date.
                String invoiceStatus = "FINAL";
                contractsGrantsInvoiceDocumentService.updateLastBilledDate(invoiceStatus,this);
            }

            contractsGrantsInvoiceDocumentService.addToAccountObjectCodeBilledTotal(invoiceDetailAccountObjectCodes);

            // generate the invoices from templates
            contractsGrantsInvoiceDocumentService.generateInvoicesForInvoiceAddresses(this);
        }
    }


    /**
     * This method updates the ContractsAndGrantsBillingAwardAccount object's invoiceDocumentStatus Variable with the value provided
     *
     * @param id
     * @param value
     */
    private void setAwardAccountInvoiceDocumentStatus(String value) {
        Iterator iterator = accountDetails.iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.getProposalNumber());

            // To set invoiceDocumentStatus to award Account
            SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setAwardAccountInvoiceDocumentStatus(mapKey, value);
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
        SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).correctContractsGrantsInvoiceDocument(this);
    }

    /**
     * Gets the invoiceDetails attribute.
     *
     * @return Returns the invoiceDetails.
     */
    public List<InvoiceDetail> getInvoiceDetails() {
        // To get the list of invoice Details without the Total fields or any indirect cost categories
        List<InvoiceDetail> invDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail invD : invoiceDetails) {
            if (!invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_DIRECT_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_IN_DIRECT_COST_CD) && !invD.isIndirectCostIndicator()) {
                invDetails.add(invD);
            }
        }
        return invDetails;
    }

    public List<InvoiceDetail> getInvoiceDetailsWithIndirectCosts(){
     // To get the list of invoice Details without the Total fields
        List<InvoiceDetail> invDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail invD : invoiceDetails) {
            if (!invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_DIRECT_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_IN_DIRECT_COST_CD)) {
                invDetails.add(invD);
            }
        }
        return invDetails;
    }


    /**
     * This method returns a list of invoice details which are indirect costs only.
     * These invoice details are not shown on the document and is different form the
     * other method getInDirectCostInvoiceDetails() because that method returns the total.
     */
    public List<InvoiceDetail> getInvoiceDetailsIndirectCostOnly(){
        List<InvoiceDetail> invDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail invD : invoiceDetails) {
            if (!invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_DIRECT_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_IN_DIRECT_COST_CD) && invD.isIndirectCostIndicator()) {
               invDetails.add(invD);
            }
        }
        return invDetails;
    }

    /**
     * Sets the invoiceDetails attribute value.
     *
     * @param invoiceDetails The invoiceDetails to set.
     */
    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
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
        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.documentNumber);
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        m.put("letterOfCreditFundCode", letterOfCreditFundCode);
        m.put("invoiceGeneralDetail", invoiceGeneralDetail);
        m.put("letterOfCreditFundGroupCode", letterOfCreditFundGroupCode);
        m.put("award", award);
        m.put("letterOfCreditCreationType", letterOfCreditCreationType);
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
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsBillingAward getAward() {
        if ( ObjectUtils.isNull(proposalNumber)) {
            award = null;
        } else {
            if ( award == null || !award.getProposalNumber().equals(proposalNumber))  {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class);
                if ( moduleService != null ) {
                    Map<String,Object> key = new HashMap<String, Object>(1);
                    key.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.getProposalNumber());
                    award = moduleService.getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, key);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return award;

    }

    /**
     * Sets the award attribute value.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsBillingAward award) {
        this.award = award;
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
     * Gets the letterOfCreditCreationType attribute.
     *
     * @return Returns the letterOfCreditCreationType.
     */
    public String getLetterOfCreditCreationType() {
        return letterOfCreditCreationType;
    }

    /**
     * Sets the letterOfCreditCreationType attribute value.
     *
     * @param letterOfCreditCreationType The letterOfCreditCreationType to set.
     */
    public void setLetterOfCreditCreationType(String letterOfCreditCreationType) {
        this.letterOfCreditCreationType = letterOfCreditCreationType;
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode.
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute value.
     *
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    /**
     * Gets the letterOfCreditFundCode attribute.
     *
     * @return Returns the letterOfCreditFundCode.
     */
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    /**
     * Sets the letterOfCreditFundCode attribute value.
     *
     * @param letterOfCreditFundCode The letterOfCreditFundCode to set.
     */
    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    /**
     * Gets the directCostInvoiceDetails attribute.
     *
     * @return Returns the directCostInvoiceDetails.
     */
    public List<InvoiceDetail> getDirectCostInvoiceDetails() {
        // To get the list of invoice Details for direct cost
        List<InvoiceDetail> dcInvDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail dInvD : directCostInvoiceDetails) {
            if (dInvD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_DIRECT_COST_CD)) {
                dcInvDetails.add(dInvD);
            }
        }

        return dcInvDetails;
    }

    /**
     * Sets the directCostInvoiceDetails attribute value.
     *
     * @param directCostInvoiceDetails The directCostInvoiceDetails to set.
     */
    public void setDirectCostInvoiceDetails(List<InvoiceDetail> directCostInvoiceDetails) {
        this.directCostInvoiceDetails = directCostInvoiceDetails;
    }

    /**
     * Gets the list of Events.
     *
     * @return Returns the events.
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Sets the list of Events.
     *
     * @param events The events to set.
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Gets the inDirectCostInvoiceDetails attribute.
     *
     * @return Returns the inDirectCostInvoiceDetails.
     */
    public List<InvoiceDetail> getInDirectCostInvoiceDetails() {
        // To get the list of invoice Details for indirect cost
        List<InvoiceDetail> indInvDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail indInvD : inDirectCostInvoiceDetails) {
            if (indInvD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_IN_DIRECT_COST_CD)) {
                indInvDetails.add(indInvD);
            }
        }

        return indInvDetails;
    }

    /**
     * Sets the inDirectCostInvoiceDetails attribute value.
     *
     * @param inDirectCostInvoiceDetails The inDirectCostInvoiceDetails to set.
     */
    public void setInDirectCostInvoiceDetails(List<InvoiceDetail> inDirectCostInvoiceDetails) {
        this.inDirectCostInvoiceDetails = inDirectCostInvoiceDetails;
    }

    /**
     * Gets the totalInvoiceDetails attribute.
     *
     * @return Returns the totalInvoiceDetails.
     */
    public List<InvoiceDetail> getTotalInvoiceDetails() {
        // To get the list of invoice Details for total cost
        List<InvoiceDetail> totalInvDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail tInvD : totalInvoiceDetails) {
            if (tInvD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_COST_CD)) {
                totalInvDetails.add(tInvD);
            }
        }

        return totalInvDetails;
    }

    /**
     * Adds the totalInvoiceDetail to the totalInvoiceDetails list
     *
     * @param totalInvoiceDetail The InvoiceDetail to add to the totalInvoiceDetails List.
     */
    public void addTotalInvoiceDetail(InvoiceDetail totalInvoiceDetail) {
        totalInvoiceDetails.add(totalInvoiceDetail);
    }

    /**
     * Sets the totalInvoiceDetails attribute value.
     *
     * @param totalInvoiceDetails The totalInvoiceDetails to set.
     */
    public void setTotalInvoiceDetails(List<InvoiceDetail> totalInvoiceDetails) {
        this.totalInvoiceDetails = totalInvoiceDetails;
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
        result = !this.getAward().getAutoApproveIndicator() || !this.getInvoiceSuspensionCategories().isEmpty();
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

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets the finalDispositionCode attribute.
     *
     * @return Returns the finalDispositionCode.
     */
    public String getFinalDispositionCode() {
        return finalDispositionCode;
    }

    /**
     * Sets the finalDispositionCode attribute.
     *
     * @param finalDispositionCode The finalDispositionCode to set.
     */
    public void setFinalDispositionCode(String finalDispositionCode) {
        this.finalDispositionCode = finalDispositionCode;
    }

    /**
     * Gets the referralTypeCode attribute.
     *
     * @return Returns the referralTypeCode attribute.
     */
    public String getReferralTypeCode() {
        return referralTypeCode;
    }

    /**
     * Sets the referralTypeCode attribute.
     *
     * @param referralTypeCode The referralTypeCode to set.
     */
    public void setReferralTypeCode(String referralTypeCode) {
        this.referralTypeCode = referralTypeCode;
    }

    /**
     * Gets the finalDisposition attribute.
     *
     * @return Returns the finalDisposition attribute.
     */
    public FinalDisposition getFinalDisposition() {
        return finalDisposition;
    }

    /**
     * Sets the finalDisposition attribute.
     *
     * @param finalDisposition The finalDisposition to set.
     */
    public void setFinalDisposition(FinalDisposition finalDisposition) {
        this.finalDisposition = finalDisposition;
    }

    /**
     * Gets the referralType attribute.
     *
     * @return Returns the referralType attribute.
     */
    public ReferralType getReferralType() {
        return referralType;
    }

    /**
     * Sets the referralType attribute.
     *
     * @param referralType The referralType to set.
     */
    public void setReferralType(ReferralType referralType) {
        this.referralType = referralType;
    }

    /**
     * Gets the showEventsInd attribute.
     *
     * @return Returns the showEventsInd attribute.
     */
    public boolean isShowEventsInd() {
        return showEventsInd;
    }

    /**
     * Sets the showEventsInd attribute.
     *
     * @param showEventsInd The showEventsInd to set.
     */
    public void setShowEventsInd(boolean showEventsInd) {
        this.showEventsInd = showEventsInd;
    }

    public String getCustomerNumber() {
        return accountsReceivableDocumentHeader.getCustomerNumber();
    }
}
