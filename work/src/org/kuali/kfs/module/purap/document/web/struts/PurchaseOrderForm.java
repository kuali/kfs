/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.web.struts;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetLocation;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItemCapitalAsset;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetLocation;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderCloseDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderPaymentHoldDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRemoveHoldDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderReopenDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRetransmitDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderSplitDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderVoidDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Struts Action Form for Purchase Order document.
 */
public class PurchaseOrderForm extends PurchasingFormBase {

    private PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine;
    private PurchaseOrderVendorQuote newPurchaseOrderVendorQuote;
    private Long awardedVendorNumber;
    
    // Retransmit.
    private String[] retransmitItemsSelected = {};
    private String retransmitTransmissionMethod;
    private String retransmitFaxNumber;
    private String retransmitHeader;

    // Need this for amendment for accounting line only
    protected Map accountingLineEditingMode;
    
    private String splitNoteText;

    // Assign Sensitive Data related fields
    private String sensitiveDataAssignmentReason = null; // reason for current assignment of sensitive data to the PO
    private SensitiveDataAssignment lastSensitiveDataAssignment = null; // last sensitive data assignment info for the PO
    private SensitiveData newSensitiveDataLine = null; // new sensitive data entry to be added to the PO
    private List<SensitiveData> sensitiveDatasAssigned = null;  // sensitive data entries currently assigned to the PO

    /**
     * Constructs a PurchaseOrderForm instance and sets up the appropriately casted document.
     */
    public PurchaseOrderForm() {
        super();
        setDocument(new PurchaseOrderDocument());

        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());
        setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        this.accountingLineEditingMode = new HashMap();
    }

    public Map getAccountingLineEditingMode() {
        return accountingLineEditingMode;
    }

    public void setAccountingLineEditingMode(Map accountingLineEditingMode) {
        this.accountingLineEditingMode = accountingLineEditingMode;
    }

    public Long getAwardedVendorNumber() {
        return awardedVendorNumber;
    }

    public void setAwardedVendorNumber(Long awardedVendorNumber) {
        this.awardedVendorNumber = awardedVendorNumber;
    }

    public PurchaseOrderVendorStipulation getNewPurchaseOrderVendorStipulationLine() {
        return newPurchaseOrderVendorStipulationLine;
    }

    public void setNewPurchaseOrderVendorStipulationLine(PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine) {
        this.newPurchaseOrderVendorStipulationLine = newPurchaseOrderVendorStipulationLine;
    }

    public PurchaseOrderVendorQuote getNewPurchaseOrderVendorQuote() {
        return newPurchaseOrderVendorQuote;
    }

    public void setNewPurchaseOrderVendorQuote(PurchaseOrderVendorQuote newPurchaseOrderVendorQuote) {
        this.newPurchaseOrderVendorQuote = newPurchaseOrderVendorQuote;
    }

    public String[] getRetransmitItemsSelected() {
        return retransmitItemsSelected;
    }

    public void setRetransmitItemsSelected(String[] retransmitItemsSelected) {
        this.retransmitItemsSelected = retransmitItemsSelected;
    }

    public PurchaseOrderDocument getPurchaseOrderDocument() {
        return (PurchaseOrderDocument) getDocument();
    }

    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        setDocument(purchaseOrderDocument);
    }
    
    public String getSplitNoteText() {
        return splitNoteText;
    }

    public void setSplitNoteText(String splitNoteText) {
        this.splitNoteText = splitNoteText;
    }       
    
    public String getSensitiveDataAssignmentReason() {
        return sensitiveDataAssignmentReason;
    }

    public void setSensitiveDataAssignmentReason(String sensitiveDataAssignmentReason) {
        this.sensitiveDataAssignmentReason = sensitiveDataAssignmentReason;
    }

    public SensitiveDataAssignment getLastSensitiveDataAssignment() {
        return lastSensitiveDataAssignment;
    }

    public void setLastSensitiveDataAssignment(SensitiveDataAssignment lastSensitiveDataAssignment) {
        this.lastSensitiveDataAssignment = lastSensitiveDataAssignment;
    }

    public SensitiveData getNewSensitiveDataLine() {
        return newSensitiveDataLine;
    }

    public void setNewSensitiveDataLine(SensitiveData newSensitiveDataLine) {
        this.newSensitiveDataLine = newSensitiveDataLine;
    }

    public List<SensitiveData> getSensitiveDatasAssigned() {
        return sensitiveDatasAssigned;
    }

    public void setSensitiveDatasAssigned(List<SensitiveData> poSensitiveData) {
        this.sensitiveDatasAssigned = poSensitiveData;
    }
    
    @Override
    public Class getCapitalAssetLocationClass() {
        return PurchaseOrderCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return PurchaseOrderItemCapitalAsset.class;
    }

    @Override
    public CapitalAssetLocation setupNewPurchasingCapitalAssetLocationLine() {
        CapitalAssetLocation location = new RequisitionCapitalAssetLocation();
        return location;
    }    
    
    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingItemLine()
     */
    @Override
    public PurApItem setupNewPurchasingItemLine() {
        return new PurchaseOrderItem();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingAccountingLine()
     */
    @Override
    public PurchaseOrderAccount setupNewPurchasingAccountingLine() {
        return new PurchaseOrderAccount();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewAccountDistributionAccountingLine()
     */
    @Override
    public PurchaseOrderAccount setupNewAccountDistributionAccountingLine() {
        PurchaseOrderAccount account = setupNewPurchasingAccountingLine();
        account.setAccountLinePercent(new BigDecimal(100));
        return account;
    }

    /**
     * Returns the new Purchase Order Vendor Stipulation Line and resets it.
     * 
     * @return the new Purchase Order Vendor Stipulation Line.
     */
    public PurchaseOrderVendorStipulation getAndResetNewPurchaseOrderVendorStipulationLine() {
        PurchaseOrderVendorStipulation aPurchaseOrderVendorStipulationLine = getNewPurchaseOrderVendorStipulationLine();
        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());

        aPurchaseOrderVendorStipulationLine.setDocumentNumber(getPurchaseOrderDocument().getDocumentNumber());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationAuthorEmployeeIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

        return aPurchaseOrderVendorStipulationLine;
    }

    public String getStatusChange() {
        if (StringUtils.isNotEmpty(getPurchaseOrderDocument().getStatusChange())){
            return getPurchaseOrderDocument().getStatusChange();
        } else {
            if (StringUtils.equals(getPurchaseOrderDocument().getStatusCode(),PurchaseOrderStatuses.IN_PROCESS)){
                return PurchaseOrderStatuses.IN_PROCESS;
            } else if (StringUtils.equals(getPurchaseOrderDocument().getStatusCode(),PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT)){
                return PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT;
            }else if (StringUtils.equals(getPurchaseOrderDocument().getStatusCode(),PurchaseOrderStatuses.WAITING_FOR_VENDOR)){
                return PurchaseOrderStatuses.WAITING_FOR_VENDOR;   
            }else{
                return null;
            }
        }
    }

    public void setStatusChange(String statusChange) {
        getPurchaseOrderDocument().setStatusChange(statusChange);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        List<String> methodToCallList = Arrays.asList(new String[]{"printPurchaseOrderPDFOnly", "printingRetransmitPoOnly", "printPoQuoteListOnly"});

        if (KNSConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && methodToCallList.contains(methodToCallParameterValue)) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    @Override
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (ObjectUtils.isNotNull(getPurchaseOrderDocument().getPurapDocumentIdentifier())) {
            String poIDstr = getPurchaseOrderDocument().getPurapDocumentIdentifier().toString();
            if (!getPurchaseOrderDocument().getIsApproved()) {
                poIDstr += " UNAPPROVED";
            }
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", poIDstr));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", "Not Available"));
        }
        if (ObjectUtils.isNotNull(getPurchaseOrderDocument().getStatus())) {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.statusCode", ((PurchaseOrderDocument) this.getDocument()).getStatus().getStatusDescription()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.statusCode", "Not Available"));
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        PurchaseOrderDocument po = (PurchaseOrderDocument) this.getDocument();

        // call this to make sure it's refreshed from the database if need be since the populate setter doesn't do that
        po.getDocumentBusinessObject();
        
        super.populate(request);
        
        if (ObjectUtils.isNotNull(po.getPurapDocumentIdentifier())) {
            po.refreshDocumentBusinessObject();
        }

        for (org.kuali.rice.kns.bo.Note note : (java.util.List<org.kuali.rice.kns.bo.Note>) po.getDocumentBusinessObject().getBoNotes()) {
            note.refreshReferenceObject("attachment");
        }        
    }
    
    /**
     * Processes validation rules having to do with any payment requests that the given purchase order may have. Specifically,
     * validates that at least one payment request exists, and makes further checks about the status of such payment requests.
     * 
     * @param document A PurchaseOrderDocument
     * @return True if the document passes all the validations.
     */
    private boolean processPaymentRequestRulesForCanClose(PurchaseOrderDocument document) {
        boolean valid = true;
        // The PO must have at least one PREQ against it.
        Integer poDocId = document.getPurapDocumentIdentifier();
        List<PaymentRequestDocument> pReqs = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestsByPurchaseOrderId(poDocId);
        if (ObjectUtils.isNotNull(pReqs)) {
            if (pReqs.size() == 0) {
                valid = false;
            }
            else {
                boolean checkInProcess = true;
                boolean hasInProcess = false;

                for (PaymentRequestDocument pReq : pReqs) {
                    // skip exception docs
                    if (pReq.getDocumentHeader().getWorkflowDocument().stateIsException()) {
                        continue;
                    }
                    // TODO NOTE for below, this could/should be changed to look at the first route level after full entry instead of
                    // being tied to AwaitingFiscal (in case full entry is moved)
                    // look for a doc that is currently routing, that will probably be the one that called this close if called from
                    // preq (with close po box)
                    if (StringUtils.equalsIgnoreCase(pReq.getStatusCode(), PaymentRequestStatuses.AWAITING_FISCAL_REVIEW) && !StringUtils.equalsIgnoreCase(pReq.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames(), PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum.ACCOUNT_REVIEW.getName())) {
                        // terminate the search since this close doc is probably being called by this doc, a doc should never be In
                        // Process and enroute in any other case
                        checkInProcess = false;
                        break;
                    }
                    if (StringUtils.equalsIgnoreCase(pReq.getStatusCode(), PaymentRequestStatuses.IN_PROCESS)) {
                        hasInProcess = true;
                    }
                }
                if (checkInProcess && hasInProcess) {
                    valid = false;
                }
            }
        }

        return valid;
    }

    /**
     * Determines whether to display the amend button for the purchase order document. The document status must be open, and the
     * purchase order must be current and not pending, and the user must be in purchasing group. These are same as the conditions
     * for displaying the payment hold button. In addition to these conditions, we also have to check that there is no In Process
     * Payment Requests nor Credit Memos associated with the PO.
     * 
     * @return boolean true if the amend button can be displayed.
     */
    private boolean canAmend() {
        boolean can = SpringContext.getBean(PurchaseOrderService.class).isPurchaseOrderOpenForProcessing(getPurchaseOrderDocument());
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderAmendmentDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }

        return can;
    }
    
    /**
     * Determines whether to display the void button for the purchase order document. Conditions:
     * PO is in Pending Print status, or is in Open status and has no PREQs against it;
     * PO's current indicator is true and pending indicator is false;
     * and the user is a member of the purchasing group).
     * 
     * @return boolean true if the void button can be displayed.
     */
    private boolean canVoid() {
        // check PO status etc
        boolean can = getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();
               
        if (can) {
            boolean pendingPrint = PurchaseOrderStatuses.PENDING_PRINT.equals(getPurchaseOrderDocument().getStatusCode());
            boolean open = PurchaseOrderStatuses.OPEN.equals(getPurchaseOrderDocument().getStatusCode());
            List<PaymentRequestView> preqViews = getPurchaseOrderDocument().getRelatedViews().getRelatedPaymentRequestViews();
            boolean hasPaymentRequest = preqViews != null && preqViews.size() > 0;
            can = pendingPrint || (open && !hasPaymentRequest);
        }

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderVoidDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }

        return can;        
    }
    
    /**
     * Determines whether to display the close order button to close the purchase order document. Conditions:
     * PO must be in Open status; must have at least one Payment Request in any status other than "In Process", 
     * and the PO cannot have any Payment Requests in "In Process" status. 
     * This button is available to all faculty/staff.
     * 
     * @return boolean true if the close order button can be displayed.
     */
    private boolean canClose() {        
        // check PO status etc
        boolean can = PurchaseOrderStatuses.OPEN.equals(getPurchaseOrderDocument().getStatusCode());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();
        can = can && processPaymentRequestRulesForCanClose(getPurchaseOrderDocument());
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderCloseDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }

        return can;        
    }

    /**
     * Determines whether to display the open order button to reopen the purchase order document.
     * Conditions: PO status is close, PO is current and not pending, and the user is in purchasing group.
     * 
     * @return boolean true if the reopen order button can be displayed.
     */
    private boolean canReopen() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.CLOSED.equals(getPurchaseOrderDocument().getStatusCode());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderReopenDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }

        return can;
    }
    
    /**
     * Determines whether to display the payment hold buttons for the purchase order document.
     * Conditions: PO status must be open, must be current and not pending, and the user must be in purchasing group.
     * 
     * @return boolean true if the payment hold button can be displayed.
     */
    private boolean canHoldPayment() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.OPEN.equals(getPurchaseOrderDocument().getStatusCode());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderPaymentHoldDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }

        return can;        
    }
    
    /**
     * Determines whether to display the remove hold button for the purchase order document.
     * Conditions are: PO status must be payment hold, must be current and not pending, and the user must be in purchasing group.
     * 
     * @return boolean true if the remove hold button can be displayed.
     */
    private boolean canRemoveHold() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.PAYMENT_HOLD.equals(getPurchaseOrderDocument().getStatusCode());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderRemoveHoldDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }

        return can;        
    }
        
    /**
     * Determines whether to display the retransmit button. Conditions: 
     * PO status must be open, and must be current and not pending, and the last transmit date must not be null. 
     * If the purchase order is an Automated Purchase Order (APO) and does not have any sensitive data set to true, 
     * then any users can see the retransmit button, otherwise, only users in the purchasing group can see it.
     * 
     * @return boolean true if the retransmit button can be displayed.
     */
    private boolean canRetransmit() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.OPEN.equals(getPurchaseOrderDocument().getStatusCode());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();
        can = can && getPurchaseOrderDocument().getPurchaseOrderLastTransmitTimestamp() != null;
        can = can && !editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB);
        
        if (!can) {
            return false;       
        }
        
        // check user authorization
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
        if (getPurchaseOrderDocument().getPurchaseOrderAutomaticIndicator()) {
            // for APO use authorization for PurchaseOrderRetransmitDocument, which is anybody
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderRetransmitDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }
        else {
            // for NON_APO use authorization for PurchaseOrderDocument, which is purchasing user
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());            
        }
        
        return can;
    }

    /**
     * Determines whether to display the button to print the pdf on a retransmit document. 
     * We're currently sharing the same button image as the button for creating a retransmit document but this may change someday. 
     * This button should only appear on Retransmit Document. If it is an Automated Purchase Order (APO) 
     * then any users can see this button, otherwise, only users in the purchasing group can see it.
     * 
     * @return boolean true if the print retransmit button can be displayed.
     */
    private boolean canPrintRetransmit() {
        // check PO status etc
        boolean can = getPurchaseOrderDocument().getDocumentHeader().getWorkflowDocument().getDocumentType().equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT);
        can = can && editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB);
        
        if (can) {
            // check user authorization: same as retransmit init, since whoever can init retransmit PO shall be able to print
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            if (getPurchaseOrderDocument().getPurchaseOrderAutomaticIndicator()) {
                // for APO use authorization for PurchaseOrderRetransmitDocument, which is anybody
                String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderRetransmitDocument.class);
                can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
            }
            else {
                // for NON_APO use authorization for PurchaseOrderDocument, which is purchasing user
                String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderDocument.class);
                can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());            
            }
        }
      
        return can;
    }    
    
    /**
     * Determines if a Split PO Document can be created from this purchase order. Conditions: 
     * The parent PO status is either "In Process" or "Awaiting Purchasing Review"; requisition source is not B2B; has at least 2 items, 
     * and PO is not in the process of being split; user must be in purchasing group.
     * 
     * @return boolean true if the split PO button can be displayed.
     */
    private boolean canSplitPo() {
        // PO must be in either "In Process" or "Awaiting Purchasing Review"
        boolean can = PurchaseOrderStatuses.IN_PROCESS.equals(getPurchaseOrderDocument().getStatusCode());
        can = can && !getPurchaseOrderDocument().getDocumentHeader().getWorkflowDocument().stateIsEnroute(); 
        can = can || PurchaseOrderStatuses.AWAIT_PURCHASING_REVIEW.equals(getPurchaseOrderDocument().getStatusCode());
        
        // can't split a SplitPO Document, according to new specs
        can = can && !(getPurchaseOrderDocument() instanceof PurchaseOrderSplitDocument); 
        
        // can't initiate another split during the splitting process. 
        can = can && !editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);
        
        // Requisition Source must not be B2B.
        can = can && !getPurchaseOrderDocument().getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B);
        
        // PO must have more than one line item.
        if (can) {
            List<PurApItem> items = (List<PurApItem>)getPurchaseOrderDocument().getItems();
            int itemsBelowTheLine = PurApItemUtils.countBelowTheLineItems(items);
            can = items.size() - itemsBelowTheLine > 1;
        }
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderSplitDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }
              
        return can;
    }
    
    /**
     * Determines whether the PO is in a status that signifies it has enough information to generate a Split PO.
     * 
     * @return  True if the PO can continue to be split.
     */
    private boolean canContinuePoSplit() {
        boolean can = editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderSplitDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }
              
        return can;        
    }
    
    /**
     * Determines if a line item receiving document can be created for the purchase order.
     * 
     * @return boolean true if the receiving document button can be displayed.
     */
    private boolean canCreateReceiving() {       
        // check PO status and item info 
        boolean can = SpringContext.getBean(ReceivingService.class).canCreateLineItemReceivingDocument(getPurchaseOrderDocument());
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(LineItemReceivingDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
        }
              
        return can;
    }
    
    /**
     * Creates a MAP for all the buttons to appear on the Purchase Order Form, and sets the attributes of these buttons.
     * 
     * @return the button map created.
     */
    private Map<String, ExtraButton> createButtonsMap() {
        HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();
        
        // Retransmit button
        ExtraButton retransmitButton = new ExtraButton();
        retransmitButton.setExtraButtonProperty("methodToCall.retransmitPo");
        retransmitButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_retransmit.gif");
        retransmitButton.setExtraButtonAltText("Retransmit");

        // Printing Retransmit button
        ExtraButton printingRetransmitButton = new ExtraButton();
        printingRetransmitButton.setExtraButtonProperty("methodToCall.printingRetransmitPo");
        printingRetransmitButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_retransmit.gif");
        printingRetransmitButton.setExtraButtonAltText("PrintingRetransmit");

        // Printing Preview button
        ExtraButton printingPreviewButton = new ExtraButton();
        printingPreviewButton.setExtraButtonProperty("methodToCall.printingPreviewPo");
        printingPreviewButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_previewpf.gif");
        printingPreviewButton.setExtraButtonAltText("PrintingPreview");
        
        // Print button
        ExtraButton printButton = new ExtraButton();
        printButton.setExtraButtonProperty("methodToCall.firstTransmitPrintPo");
        printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_print.gif");
        printButton.setExtraButtonAltText("Print");

        // Reopen PO button
        ExtraButton reopenButton = new ExtraButton();
        reopenButton.setExtraButtonProperty("methodToCall.reopenPo");
        reopenButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_openorder.gif");
        reopenButton.setExtraButtonAltText("Reopen");

        // Close PO button
        ExtraButton closeButton = new ExtraButton();
        closeButton.setExtraButtonProperty("methodToCall.closePo");
        closeButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_closeorder.gif");
        closeButton.setExtraButtonAltText("Close PO");

        // Void PO button
        ExtraButton voidButton = new ExtraButton();
        voidButton.setExtraButtonProperty("methodToCall.voidPo");
        voidButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_voidorder.gif");
        voidButton.setExtraButtonAltText("Void PO");

        // Payment Hold PO button
        ExtraButton paymentHoldButton = new ExtraButton();
        paymentHoldButton.setExtraButtonProperty("methodToCall.paymentHoldPo");
        paymentHoldButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_paymenthold.gif");
        paymentHoldButton.setExtraButtonAltText("Payment Hold");

        // Amend button
        ExtraButton amendButton = new ExtraButton();
        amendButton.setExtraButtonProperty("methodToCall.amendPo");
        amendButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_amend.gif");
        amendButton.setExtraButtonAltText("Amend");

        // Remove Hold button
        ExtraButton removeHoldButton = new ExtraButton();
        removeHoldButton.setExtraButtonProperty("methodToCall.removeHoldPo");
        removeHoldButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_removehold.gif");
        removeHoldButton.setExtraButtonAltText("Remove Hold");

        // Resend PO Cxml button
        ExtraButton resendPoCxmlButton = new ExtraButton();
        resendPoCxmlButton.setExtraButtonProperty("methodToCall.resendPoCxml");
        resendPoCxmlButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_resendpo.gif");
        resendPoCxmlButton.setExtraButtonAltText("Resend PO CXML");

        // Receiving button
        ExtraButton receivingButton = new ExtraButton();
        receivingButton.setExtraButtonProperty("methodToCall.createReceivingLine");
        receivingButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_receiving.gif");
        receivingButton.setExtraButtonAltText("Receiving");
        
        // Split PO button
        ExtraButton splitPoButton = new ExtraButton();
        splitPoButton.setExtraButtonProperty("methodToCall.splitPo");
        splitPoButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_splitorder.gif");
        splitPoButton.setExtraButtonAltText("Split this PO");
        
        // Continue button
        ExtraButton continueButton = new ExtraButton();
        continueButton.setExtraButtonProperty("methodToCall.continuePurchaseOrderSplit");
        continueButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_continue.gif");
        continueButton.setExtraButtonAltText("Continue");
        
        // Cancel Split button
        ExtraButton cancelSplitButton = new ExtraButton();
        cancelSplitButton.setExtraButtonProperty("methodToCall.cancelPurchaseOrderSplit");
        cancelSplitButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_cancelsplit.gif");
        cancelSplitButton.setExtraButtonAltText("Cancel Splitting the PO");
        
        // Assign Sensitive Data button
        ExtraButton assignSensitiveDataButton = new ExtraButton();
        assignSensitiveDataButton.setExtraButtonProperty("methodToCall.assignSensitiveData");
        assignSensitiveDataButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_sensitivedata.gif ");
        assignSensitiveDataButton.setExtraButtonAltText("Assign sensitive data to the PO");
        
        // Submit Sensitive Data Assignment button
        ExtraButton submitSensitiveDataButton = new ExtraButton();
        submitSensitiveDataButton.setExtraButtonProperty("methodToCall.submitSensitiveData");
        submitSensitiveDataButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_submit.gif");
        submitSensitiveDataButton.setExtraButtonAltText("Submit sensitive data assignment");
        
        // Cancel Sensitive Data Assignment button
        ExtraButton cancelSensitiveDataButton = new ExtraButton();
        cancelSensitiveDataButton.setExtraButtonProperty("methodToCall.cancelSensitiveData");
        cancelSensitiveDataButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_cancel.gif");
        cancelSensitiveDataButton.setExtraButtonAltText("Cancel sensitive data assignment");
        
        result.put(retransmitButton.getExtraButtonProperty(), retransmitButton);
        result.put(printingRetransmitButton.getExtraButtonProperty(), printingRetransmitButton);
        result.put(printingPreviewButton.getExtraButtonProperty(), printingPreviewButton);
        result.put(printButton.getExtraButtonProperty(), printButton);
        result.put(reopenButton.getExtraButtonProperty(), reopenButton);
        result.put(closeButton.getExtraButtonProperty(), closeButton);
        result.put(voidButton.getExtraButtonProperty(), voidButton);
        result.put(paymentHoldButton.getExtraButtonProperty(), paymentHoldButton);
        result.put(amendButton.getExtraButtonProperty(), amendButton);
        result.put(removeHoldButton.getExtraButtonProperty(), removeHoldButton);
        result.put(receivingButton.getExtraButtonProperty(), receivingButton);
        result.put(splitPoButton.getExtraButtonProperty(), splitPoButton);
        result.put(continueButton.getExtraButtonProperty(), continueButton);
        result.put(cancelSplitButton.getExtraButtonProperty(), cancelSplitButton);
        result.put(assignSensitiveDataButton.getExtraButtonProperty(), assignSensitiveDataButton);
        result.put(submitSensitiveDataButton.getExtraButtonProperty(), submitSensitiveDataButton);
        result.put(cancelSensitiveDataButton.getExtraButtonProperty(), cancelSensitiveDataButton);
        result.put(resendPoCxmlButton.getExtraButtonProperty(), resendPoCxmlButton);
        
        return result;
    }

    /**
     * Override the superclass method to add appropriate buttons for
     * PurchaseOrderDocument.
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();        
        Map buttonsMap = createButtonsMap();   
        
        if (getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.ASSIGN_SENSITIVE_DATA)) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.assignSensitiveData"));
            if (getPurchaseOrderDocument().getAssigningSensitiveData()) {
                // no other extra buttons except the following shall appear on "Assign Sensitive Data" page
                // and these buttons use the same permissions as the "Assign Sensitive Data" button
                extraButtons.clear();
                extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.submitSensitiveData"));
                extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.cancelSensitiveData"));
                return extraButtons;
            }
        }

        if (getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.PREVIEW_PRINT_PURCHASE_ORDER)) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.printingPreviewPo"));
        }

        if (getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.PRINT_PURCHASE_ORDER)) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.firstTransmitPrintPo"));
        }

        if (getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.RESEND_PURCHASE_ORDER)) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.resendPoCxml"));
        }

        if (canRetransmit()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.retransmitPo"));
        }

        if (canPrintRetransmit()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.printingRetransmitPo"));
        }

        if (canReopen()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.reopenPo"));
        }

        if (canClose()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.closePo"));
        }

        if (canHoldPayment()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.paymentHoldPo"));
        }

        if (canAmend()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.amendPo"));
        }

        if (canVoid()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.voidPo"));
        }

        if (canRemoveHold()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.removeHoldPo"));
        }

        if (canCreateReceiving()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.createReceivingLine"));
        }

        if (canSplitPo()) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.splitPo"));
        }

        if (canContinuePoSplit()) {
            extraButtons.clear();
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.continuePurchaseOrderSplit"));
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.cancelPurchaseOrderSplit"));
        }
        
        return extraButtons;
    }
    
}

