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

package org.kuali.module.purap.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ConciseXmlDocumentConversionService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.AccountsPayableDocumentSpecificService;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.bo.PaymentTermType;
import org.kuali.module.vendor.bo.PurchaseOrderCostSource;
import org.kuali.module.vendor.bo.ShippingPaymentTerms;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Payment Request Document Business Object. Contains the fields associated with the main document table.
 */
public class PaymentRequestDocument extends AccountsPayableDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDocument.class);

    private Date invoiceDate;
    private String invoiceNumber;
    private KualiDecimal vendorInvoiceAmount;
    private String vendorPaymentTermsCode;
    private String vendorShippingPaymentTermsCode;
    private Date paymentRequestPayDate;
    private String paymentRequestCostSourceCode;
    private boolean paymentRequestedCancelIndicator;
    private boolean paymentAttachmentIndicator;
    private boolean immediatePaymentIndicator;
    private String specialHandlingInstructionLine1Text;
    private String specialHandlingInstructionLine2Text;
    private String specialHandlingInstructionLine3Text;
    private Date paymentPaidDate;
    private boolean paymentRequestElectronicInvoiceIndicator;
    private String accountsPayableRequestCancelIdentifier;
    private Integer originalVendorHeaderGeneratedIdentifier;
    private Integer originalVendorDetailAssignedIdentifier;
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String purchaseOrderNotes;
    private String recurringPaymentTypeCode;
    private boolean receivingDocumentRequiredIndicator;
    private boolean paymentRequestPositiveApprovalIndicator;
    
    // NOT PERSISTED IN DB
    private String vendorShippingTitleCode;
    private Date purchaseOrderEndDate;
    private String primaryVendorName;
    
    // BELOW USED BY ROUTING
    private Integer requisitionIdentifier;

    // REFERENCE OBJECTS
    private PaymentTermType vendorPaymentTerms;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
    private PurchaseOrderCostSource paymentRequestCostSource;
    private RecurringPaymentType recurringPaymentType;


    /**
     * Default constructor.
     */
    public PaymentRequestDocument() {
        super();
    }

    /**
     * @see org.kuali.core.document.DocumentBase#getDocumentRepresentationForSerialization()
     */
    @Override
    protected Document getDocumentRepresentationForSerialization() {
        return SpringContext.getBean(ConciseXmlDocumentConversionService.class).getDocumentForSerialization(this);
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        this.setRequisitionIdentifier(getPurchaseOrderDocument().getRequisitionIdentifier());
        super.populateDocumentForRouting();
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public KualiDecimal getVendorInvoiceAmount() {
        return vendorInvoiceAmount;
    }

    public void setVendorInvoiceAmount(KualiDecimal vendorInvoiceAmount) {
        this.vendorInvoiceAmount = vendorInvoiceAmount;
    }

    public String getVendorPaymentTermsCode() {
        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    public PaymentTermType getVendorPaymentTerms() {
        if (ObjectUtils.isNull(vendorPaymentTerms)) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_PAYMENT_TERMS);
        }
        return vendorPaymentTerms;
    }

    public void setVendorPaymentTerms(PaymentTermType vendorPaymentTerms) {
        this.vendorPaymentTerms = vendorPaymentTerms;
    }

    public String getVendorShippingPaymentTermsCode() {
        if (ObjectUtils.isNull(vendorPaymentTerms)) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_SHIPPING_PAYMENT_TERMS);
        }
        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public Date getPaymentRequestPayDate() {
        return paymentRequestPayDate;
    }

    public void setPaymentRequestPayDate(Date paymentRequestPayDate) {
        this.paymentRequestPayDate = paymentRequestPayDate;
    }

    public String getPaymentRequestCostSourceCode() {
        return paymentRequestCostSourceCode;
    }

    public void setPaymentRequestCostSourceCode(String paymentRequestCostSourceCode) {
        this.paymentRequestCostSourceCode = paymentRequestCostSourceCode;
    }

    public boolean getPaymentRequestedCancelIndicator() {
        return paymentRequestedCancelIndicator;
    }

    public boolean isPaymentRequestedCancelIndicator() {
        return paymentRequestedCancelIndicator;
    }

    public void setPaymentRequestedCancelIndicator(boolean paymentRequestedCancelIndicator) {
        this.paymentRequestedCancelIndicator = paymentRequestedCancelIndicator;
    }

    public boolean getPaymentAttachmentIndicator() {
        return paymentAttachmentIndicator;
    }

    public void setPaymentAttachmentIndicator(boolean paymentAttachmentIndicator) {
        this.paymentAttachmentIndicator = paymentAttachmentIndicator;
    }

    public boolean getImmediatePaymentIndicator() {
        return immediatePaymentIndicator;
    }

    public void setImmediatePaymentIndicator(boolean immediatePaymentIndicator) {
        this.immediatePaymentIndicator = immediatePaymentIndicator;
    }

    public String getSpecialHandlingInstructionLine1Text() {
        return specialHandlingInstructionLine1Text;
    }

    public void setSpecialHandlingInstructionLine1Text(String specialHandlingInstructionLine1Text) {
        this.specialHandlingInstructionLine1Text = specialHandlingInstructionLine1Text;
    }

    public String getSpecialHandlingInstructionLine2Text() {
        return specialHandlingInstructionLine2Text;
    }

    public void setSpecialHandlingInstructionLine2Text(String specialHandlingInstructionLine2Text) {
        this.specialHandlingInstructionLine2Text = specialHandlingInstructionLine2Text;
    }

    public String getSpecialHandlingInstructionLine3Text() {
        return specialHandlingInstructionLine3Text;
    }

    public void setSpecialHandlingInstructionLine3Text(String specialHandlingInstructionLine3Text) {
        this.specialHandlingInstructionLine3Text = specialHandlingInstructionLine3Text;
    }

    public Date getPaymentPaidDate() {
        return paymentPaidDate;
    }

    public void setPaymentPaidDate(Date paymentPaidDate) {
        this.paymentPaidDate = paymentPaidDate;
    }

    public boolean getPaymentRequestElectronicInvoiceIndicator() {
        return paymentRequestElectronicInvoiceIndicator;
    }

    public void setPaymentRequestElectronicInvoiceIndicator(boolean paymentRequestElectronicInvoiceIndicator) {
        this.paymentRequestElectronicInvoiceIndicator = paymentRequestElectronicInvoiceIndicator;
    }

    public String getAccountsPayableRequestCancelIdentifier() {
        return accountsPayableRequestCancelIdentifier;
    }

    public void setAccountsPayableRequestCancelIdentifier(String accountsPayableRequestCancelIdentifier) {
        this.accountsPayableRequestCancelIdentifier = accountsPayableRequestCancelIdentifier;
    }

    public Integer getOriginalVendorHeaderGeneratedIdentifier() {
        return originalVendorHeaderGeneratedIdentifier;
    }

    public void setOriginalVendorHeaderGeneratedIdentifier(Integer originalVendorHeaderGeneratedIdentifier) {
        this.originalVendorHeaderGeneratedIdentifier = originalVendorHeaderGeneratedIdentifier;
    }

    public Integer getOriginalVendorDetailAssignedIdentifier() {
        return originalVendorDetailAssignedIdentifier;
    }

    public void setOriginalVendorDetailAssignedIdentifier(Integer originalVendorDetailAssignedIdentifier) {
        this.originalVendorDetailAssignedIdentifier = originalVendorDetailAssignedIdentifier;
    }

    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return alternateVendorHeaderGeneratedIdentifier;
    }

    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }

    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return alternateVendorDetailAssignedIdentifier;
    }

    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    public ShippingPaymentTerms getVendorShippingPaymentTerms() {
        return vendorShippingPaymentTerms;
    }

    public void setVendorShippingPaymentTerms(ShippingPaymentTerms vendorShippingPaymentTerms) {
        this.vendorShippingPaymentTerms = vendorShippingPaymentTerms;
    }

    public String getVendorShippingTitleCode() {
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument())) {
            return this.getPurchaseOrderDocument().getVendorShippingTitleCode();
        }
        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    /**
     * Gets the paymentRequestPositiveApprovalIndicator attribute. 
     * @return Returns the paymentRequestPositiveApprovalIndicator.
     */
    public boolean isPaymentRequestPositiveApprovalIndicator() {
        return paymentRequestPositiveApprovalIndicator;
    }

    /**
     * Sets the paymentRequestPositiveApprovalIndicator attribute value.
     * @param paymentRequestPositiveApprovalIndicator The paymentRequestPositiveApprovalIndicator to set.
     */
    public void setPaymentRequestPositiveApprovalIndicator(boolean paymentRequestPositiveApprovalIndicator) {
        this.paymentRequestPositiveApprovalIndicator = paymentRequestPositiveApprovalIndicator;
    }

    /**
     * Gets the receivingDocumentRequiredIndicator attribute. 
     * @return Returns the receivingDocumentRequiredIndicator.
     */
    public boolean isReceivingDocumentRequiredIndicator() {
        return receivingDocumentRequiredIndicator;
    }

    /**
     * Sets the receivingDocumentRequiredIndicator attribute value.
     * @param receivingDocumentRequiredIndicator The receivingDocumentRequiredIndicator to set.
     */
    public void setReceivingDocumentRequiredIndicator(boolean receivingDocumentRequiredIndicator) {
        this.receivingDocumentRequiredIndicator = receivingDocumentRequiredIndicator;
    }

    /**
     * Perform logic needed to initiate PREQ Document
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        UniversalUser currentUser = (UniversalUser) GlobalVariables.getUserSession().getUniversalUser();
        this.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
        this.setAccountsPayableProcessorIdentifier(currentUser.getPersonUniversalIdentifier());
        this.setProcessingCampusCode(currentUser.getCampusCode());
        this.refreshNonUpdateableReferences();
    }

    /**
     * Perform logic needed to clear the initial fields on a PREQ Document
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");
        // Clearing document overview fields
        this.getDocumentHeader().setFinancialDocumentDescription(null);
        this.getDocumentHeader().setExplanation(null);
        this.getDocumentHeader().setFinancialDocumentTotalAmount(null);
        this.getDocumentHeader().setOrganizationDocumentNumber(null);

        // Clearing document Init fields
        this.setPurchaseOrderIdentifier(null);
        this.setInvoiceNumber(null);
        this.setInvoiceDate(null);
        this.setVendorInvoiceAmount(null);
        this.setSpecialHandlingInstructionLine1Text(null);
        this.setSpecialHandlingInstructionLine2Text(null);
        this.setSpecialHandlingInstructionLine3Text(null);
    }

    /**
     * Populates a preq from a PO - delegate method
     * 
     * @param po -
     */
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po) {
        populatePaymentRequestFromPurchaseOrder(po, new HashMap<String, ExpiredOrClosedAccountEntry>());
    }


    /**
     * Populates a preq from a PO
     * 
     * @param po Purchase Order Document used for populating the PREQ
     * @param expiredOrClosedAccountList a list of closed or expired accounts
     */
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        this.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        this.getDocumentHeader().setOrganizationDocumentNumber(po.getDocumentHeader().getOrganizationDocumentNumber());
        this.setPostingYear(po.getPostingYear());
        this.setReceivingDocumentRequiredIndicator(po.isReceivingDocumentRequiredIndicator());
        this.setPaymentRequestPositiveApprovalIndicator(po.isPaymentRequestPositiveApprovalIndicator());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        if (po.getPurchaseOrderCostSource() != null) {
            this.setPaymentRequestCostSource(po.getPurchaseOrderCostSource());
            this.setPaymentRequestCostSourceCode(po.getPurchaseOrderCostSourceCode());
        }
        if (po.getVendorShippingPaymentTerms() != null) {
            this.setVendorShippingPaymentTerms(po.getVendorShippingPaymentTerms());
            this.setVendorShippingPaymentTermsCode(po.getVendorShippingPaymentTermsCode());
        }

        if (po.getRecurringPaymentType() != null) {
            this.setRecurringPaymentType(po.getRecurringPaymentType());
            this.setRecurringPaymentTypeCode(po.getRecurringPaymentTypeCode());
        }

        this.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        this.setVendorName(po.getVendorName());
        
        //set original vendor
        this.setOriginalVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setOriginalVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());        
        
        //set alternate vendor info as well
        this.setAlternateVendorHeaderGeneratedIdentifier(po.getAlternateVendorHeaderGeneratedIdentifier());
        this.setAlternateVendorDetailAssignedIdentifier(po.getAlternateVendorDetailAssignedIdentifier());
        
        // populate preq vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getUniversalUser().getCampusCode();
        VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(po.getVendorHeaderGeneratedIdentifier(), po.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            this.templateVendorAddress(vendorAddress);
            this.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
        }
        else {
            // set address from PO
            this.setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
            this.setVendorLine1Address(po.getVendorLine1Address());
            this.setVendorLine2Address(po.getVendorLine2Address());
            this.setVendorCityName(po.getVendorCityName());
            this.setVendorStateCode(po.getVendorStateCode());
            this.setVendorPostalCode(po.getVendorPostalCode());
            this.setVendorCountryCode(po.getVendorCountryCode());
        }

        if ((po.getVendorPaymentTerms() == null) || ("".equals(po.getVendorPaymentTerms().getVendorPaymentTermsCode()))) {
            this.setVendorPaymentTerms(new PaymentTermType());
            this.vendorPaymentTerms.setVendorPaymentTermsCode("");
        }
        else {
            this.setVendorPaymentTermsCode(po.getVendorPaymentTermsCode());
            this.setVendorPaymentTerms(po.getVendorPaymentTerms());
        }
        this.setPaymentRequestPayDate(SpringContext.getBean(PaymentRequestService.class).calculatePayDate(this.getInvoiceDate(), this.getVendorPaymentTerms()));

        AccountsPayableService accountsPayableService = SpringContext.getBean(AccountsPayableService.class);
        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) po.getItems()) {
            // check to make sure it's eligible for payment (i.e. active and has encumberance available
            if (getDocumentSpecificService().poItemEligibleForAp(this, poi)) {
                this.getItems().add(new PaymentRequestItem(poi, this, expiredOrClosedAccountList));
            }
        }
        // add missing below the line
        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

        this.refreshNonUpdateableReferences();
    }

    /**
     * @see org.kuali.core.document.DocumentBase#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        if (SpringContext.getBean(ParameterService.class).getIndicatorParameter(PaymentRequestDocument.class, PurapParameterConstants.PURAP_OVERRIDE_PREQ_DOC_TITLE)) {
            return getCustomDocumentTitle();
        }
        return super.getDocumentTitle();
    }

    /**
     * Returns a custom document title based on the workflow document title. Depending on what route level the document is currently
     * in, the PO, vendor, amount, account number, dept, campus may be added to the documents title.
     * 
     * @return - Customized document title text dependent upon route level.
     */
    private String getCustomDocumentTitle() {
        try {
            // set the workflow document title
            String poNumber = getPurchaseOrderIdentifier().toString();
            String vendorName = StringUtils.trimToEmpty(getVendorName());
            String preqAmount = getGrandTotal().toString();
            String indicator = getTitleIndicator();

            String documentTitle = new StringBuffer("PO: ").append(poNumber).append(" Vendor: ").append(vendorName).append(" Amount: ").append(preqAmount).append(" ").append(indicator).toString();
            String[] nodeNames = getDocumentHeader().getWorkflowDocument().getNodeNames();
            if ((nodeNames.length == 1) && (NodeDetailEnum.VENDOR_TAX_REVIEW.getName().equals(nodeNames[0]))) {
                // tax review
                // grab the first account
                PurApAccountingLine theAccount = getFirstAccount();
                // setup variables
                String deliveryCampus = StringUtils.trimToEmpty((getProcessingCampus() != null ? getProcessingCampus().getCampusShortName() : ""));
                String accountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getAccountNumber()) : "");
                String department = (theAccount != null ? StringUtils.trimToEmpty((((theAccount.getAccount() != null) && (theAccount.getAccount().getOrganization() != null)) ? theAccount.getAccount().getOrganization().getOrganizationName() : "")) : "");
                documentTitle = new StringBuffer("Vendor: ").append(vendorName).append(" PO: ").append(poNumber).append(" Account Number: ").append(accountNumber).append(" Dept: ").append(department).append(" Delivery Campus: ").append(deliveryCampus).toString();
            }
            return documentTitle;
        }
        catch (WorkflowException e) {
            LOG.error("Error updating Payment Request document: " + e.getMessage());
            throw new RuntimeException("Error updating Payment Request document: " + e.getMessage());
        }
    }

    /**
     * Returns the first payment item's first account (assuming the item list is sequentially ordered).
     * 
     * @return - Accounting Line object for first account of first payment item.
     */
    private PurApAccountingLine getFirstAccount() {
        // loop through items, and pick the first item
        if ((getItems() != null) && (!getItems().isEmpty())) {
            PaymentRequestItem itemToUse = null;
            for (Iterator iter = getItems().iterator(); iter.hasNext();) {
                PaymentRequestItem item = (PaymentRequestItem) iter.next();
                if ((item.isConsideredEntered()) && ((item.getSourceAccountingLines() != null) && (!item.getSourceAccountingLines().isEmpty()))) {
                    // accounting lines are not empty so pick the first account
                    PurApAccountingLine accountLine = item.getSourceAccountingLine(0);
                    accountLine.refreshNonUpdateableReferences();
                    return accountLine;
                }
            }
        }
        return null;
    }

    /**
     * Determines the indicator text that will appear in the workflow document title
     * 
     * @return - Text of hold or request cancel
     */
    private String getTitleIndicator() {
        if (isHoldIndicator()) {
            return PurapConstants.PaymentRequestIndicatorText.HOLD;
        }
        else if (isPaymentRequestedCancelIndicator()) {
            return PurapConstants.PaymentRequestIndicatorText.REQUEST_CANCEL;
        }
        return "";
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();
        try {
            // DOCUMENT PROCESSED
            if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
                if (!PaymentRequestStatuses.AUTO_APPROVED.equals(getStatusCode())) {
                    SpringContext.getBean(PurapService.class).updateStatus(this, PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED);
                    populateDocumentForRouting();
                    SpringContext.getBean(PaymentRequestService.class).saveDocumentWithoutValidation(this);
                    return;
                }
            }
            // DOCUMENT DISAPPROVED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(nodeName);
                if (ObjectUtils.isNotNull(currentNode)) {
                    String newStatusCode = currentNode.getDisapprovedStatusCode();
                    if ((StringUtils.isBlank(newStatusCode)) && ((StringUtils.isBlank(currentNode.getDisapprovedStatusCode())) && ((PaymentRequestStatuses.INITIATE.equals(getStatusCode())) || (PaymentRequestStatuses.IN_PROCESS.equals(getStatusCode()))))) {
                        newStatusCode = PaymentRequestStatuses.CANCELLED_IN_PROCESS;
                    }
                    if (StringUtils.isNotBlank(newStatusCode)) {
                        SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, nodeName);
                        return;
                    }
                }
                logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
            }
            // DOCUMENT CANCELED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
                String currentNodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(this.getDocumentHeader().getWorkflowDocument());
                NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(currentNodeName);
                if (ObjectUtils.isNotNull(currentNode)) {
                    String cancelledStatusCode = currentNode.getDisapprovedStatusCode();
                    if (StringUtils.isNotBlank(cancelledStatusCode)) {
                        SpringContext.getBean(PurapService.class).updateStatus(this, cancelledStatusCode);
                        SpringContext.getBean(PaymentRequestService.class).saveDocumentWithoutValidation(this);
                        return;
                    }
                }
                logAndThrowRuntimeException("No status found to set for document being canceled in node '" + currentNode + "'");
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    /**
     * Generates correcting entries to the GL if accounts are modified.
     * 
     * @see org.kuali.core.document.Document#doActionTaken(edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO)
     */
    @Override
    public void doActionTaken(ActionTakenEventVO event) {
        super.doActionTaken(event);
        KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        try {
            String currentNode = null;
            if (workflowDocument.getNodeNames().length > 0) {
                currentNode = workflowDocument.getNodeNames()[0];
            }

            // everything in the below list requires correcting entries to be written to the GL
            if (NodeDetailEnum.getNodesRequiringCorrectingGeneralLedgerEntries().contains(currentNode)) {
                if (NodeDetailEnum.ACCOUNT_REVIEW.getName().equals(currentNode)) {
                    SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesModifyPaymentRequest(this);
                }
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#preProcessNodeChange(java.lang.String, java.lang.String)
     */
    public boolean processNodeChange(String newNodeName, String oldNodeName) {
        if (PaymentRequestStatuses.AUTO_APPROVED.equals(getStatusCode())) {
            // do nothing for an auto approval
            return false;
        }
        if (NodeDetailEnum.ADHOC_REVIEW.getName().equals(oldNodeName)) {
            SpringContext.getBean(AccountsPayableService.class).performLogicForFullEntryCompleted(this);
        }
        else if (NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW.getName().equals(oldNodeName)) {
            setAccountsPayableApprovalDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        }
        return true;
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getNodeDetailEnum(java.lang.String)
     */
    public NodeDetails getNodeDetailEnum(String nodeName) {
        return NodeDetailEnum.getNodeDetailEnumByName(nodeName);
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#saveDocumentFromPostProcessing()
     */
    public void saveDocumentFromPostProcessing() {
        SpringContext.getBean(PaymentRequestService.class).saveDocumentWithoutValidation(this);

        // if we've hit full entry completed then close/reopen po
        if (PurapConstants.PaymentRequestStatuses.STATUS_ORDER.isFullDocumentEntryCompleted(this.getStatusCode()) && this.isClosePurchaseOrderIndicator()) {
            SpringContext.getBean(PurapService.class).performLogicForCloseReopenPO(this);
        }
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return PaymentRequestItem.class;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchaseOrderDocument getPurApSourceDocumentIfPossible() {
        return getPurchaseOrderDocument();
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(PurchaseOrderDocument.class);
    }

    public String getPurchaseOrderNotes() {

        ArrayList poNotes = (ArrayList) this.getPurchaseOrderDocument().getBoNotes();

        if (poNotes.size() > 0) {
            return "Yes";
        }
        return "No";
    }

    public void setPurchaseOrderNotes(String purchaseOrderNotes) {
        this.purchaseOrderNotes = purchaseOrderNotes;
    }

    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    /**
     * Returns the total encumbered amount from the purchase order excluding below the line.
     * 
     * @return Total cost excluding below the line
     */
    public KualiDecimal getItemTotalPoEncumbranceAmount() {
        // get total from po excluding below the line and inactive
        return this.getPurchaseOrderDocument().getTotalDollarAmount(false, false);
    }

    public KualiDecimal getItemTotalPoEncumbranceAmountRelieved() {
        return getItemTotalPoEncumbranceAmountRelieved(false);
    }

    public KualiDecimal getItemTotalPoEncumbranceAmountRelieved(boolean includeBelowTheLine) {

        KualiDecimal total = KualiDecimal.ZERO;

        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getPurchaseOrderDocument().getItems()) {
            ItemType it = item.getItemType();
            if (includeBelowTheLine || it.isItemTypeAboveTheLineIndicator()) {
                total = total.add(item.getItemEncumbranceRelievedAmount());
            }
        }
        return total;
    }

    public KualiDecimal getLineItemTotal() {
        return this.getTotalDollarAmountAboveLineItems();
    }

    public KualiDecimal getGrandTotal() {
        return this.getTotalDollarAmount();
    }

    public KualiDecimal getGrandTotalExcludingDiscount() {
        String[] discountCode = new String[] { PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE };
        return this.getTotalDollarAmountWithExclusions(discountCode, true);
    }

    public boolean isDiscount() {
        return SpringContext.getBean(PaymentRequestService.class).hasDiscountItem(this);
    }

    /**
     * The total that was paid on the po excluding below the line
     * 
     * @return total paid
     */
    public KualiDecimal getItemTotalPoPaidAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getPurchaseOrderDocument().getItems()) {
            ItemType iT = item.getItemType();
            if (iT.isItemTypeAboveTheLineIndicator()) {
                KualiDecimal itemPaid = item.getItemPaidAmount();
                total = total.add(itemPaid);
            }
        }
        return total;
    }

    /**
     * Returns the name of who requested cancel.
     * 
     * @return - name of who requested cancel.
     */
    public String getAccountsPayableRequestCancelPersonName() {
        String personName = null;
        try {
            UniversalUser user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(getAccountsPayableRequestCancelIdentifier());
            personName = user.getPersonName();
        }
        catch (UserNotFoundException unfe) {
            personName = "";
        }

        return personName;
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     * 
     * @param amount - total po amount paid
     */
    public void setItemTotalPoPaidAmount(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     * 
     * @param amount - total po encumbrance
     */
    public void setItemTotalPoEncumbranceAmount(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     * 
     * @param amount - total po encumbrance amount relieved
     */
    public void setItemTotalPoEncumbranceAmountRelieved(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Determinines the route levels for a given document.
     * 
     * @param workflowDocument - work flow document
     * @return List - list of route levels
     */
    protected List getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * USED FOR ROUTING ONLY
     * 
     * @deprecated
     */
    public String getStatusDescription() {
        return "";
    }

    /**
     * USED FOR ROUTING ONLY
     * 
     * @deprecated
     */
    public void setStatusDescription(String statusDescription) {
    }

    public RecurringPaymentType getRecurringPaymentType() {
        if (ObjectUtils.isNull(recurringPaymentType)) {
            refreshReferenceObject(PurapPropertyConstants.RECURRING_PAYMENT_TYPE);
        }
        return recurringPaymentType;
    }

    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }

    public PurchaseOrderCostSource getPaymentRequestCostSource() {
        return paymentRequestCostSource;
    }

    public void setPaymentRequestCostSource(PurchaseOrderCostSource paymentRequestCostSource) {
        this.paymentRequestCostSource = paymentRequestCostSource;
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getPoDocumentTypeForAccountsPayableDocumentApprove()
     */
    public String getPoDocumentTypeForAccountsPayableDocumentCancel() {
        return PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT;
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getInitialAmount()
     */
    public KualiDecimal getInitialAmount() {
        return this.getVendorInvoiceAmount();
    }

    /**
     * Populates the payment request document, then continues with preparing for save.
     * 
     * @see org.kuali.core.document.Document#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {

        // first populate, then call super
        if (event instanceof ContinueAccountsPayableEvent) {
            SpringContext.getBean(PaymentRequestService.class).populatePaymentRequest(this);
        }

        super.prepareForSave(event);
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#isAttachmentRequired()
     */
    @Override
    protected boolean isAttachmentRequired() {
        return StringUtils.equalsIgnoreCase("Y", SpringContext.getBean(ParameterService.class).getParameterValue(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PREQ_REQUIRE_ATTACHMENT));
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocument#getDocumentSpecificService()
     */
    @Override
    public AccountsPayableDocumentSpecificService getDocumentSpecificService() {
        return (AccountsPayableDocumentSpecificService) SpringContext.getBean(PaymentRequestService.class);
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItem(int)
     */
    @Override
    public PurApItem getItem(int pos) {
        PaymentRequestItem item = (PaymentRequestItem) super.getItem(pos);
        if (item.getPaymentRequest() == null) {
            item.setPaymentRequest(this);
        }
        return item;
    }

    public String getPrimaryVendorName() {
        
        if(primaryVendorName == null){
            VendorDetail vd = SpringContext.getBean(VendorService.class).getVendorDetail(
                    this.getOriginalVendorHeaderGeneratedIdentifier(), this.getOriginalVendorDetailAssignedIdentifier());
        
            if (vd != null){
                primaryVendorName = vd.getVendorName();
            }
        }
        
        return primaryVendorName;
    }

    /**
     * @deprecated
     */
    public void setPrimaryVendorName(String primaryVendorName){
    }
    
    /**
     * Forces general ledger entries to be approved, does not wait for payment request document final approval.
     * 
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);


        SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(this, (AccountingLine)postable, explicitEntry, getPurchaseOrderIdentifier(), getDebitCreditCodeForGLEntries(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT, isGenerateEncumbranceEntries());

        // PREQs do not wait for document final approval to post GL entries; here we are forcing them to be APPROVED
        explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
    }
}
