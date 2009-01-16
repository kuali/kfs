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
package org.kuali.kfs.module.purap.document.authorization;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.CreditMemoView;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderCloseDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderPaymentHoldDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRemoveHoldDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderReopenDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRetransmitDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderSplitDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderVoidDocument;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.document.validation.impl.PurchaseOrderCloseDocumentRule;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class determines permissions for a user to view the
 * buttons on Purchase Order Document.
 * 
 */
public class PurchaseOrderDocumentActionAuthorizer implements Serializable {

    private boolean currentIndicator;
    private boolean pendingActionIndicator;
    private boolean automaticIndicator;
    
    private PurchaseOrderDocument purchaseOrder;
    private Map editingMode;
    private Map documentActions;
    private Person user;
    
    private String docStatus;
    private String documentType;

    /**
     * Constructs a PurchaseOrderDocumentActionAuthorizer.
     * 
     * @param po A PurchaseOrderDocument
     */
    public PurchaseOrderDocumentActionAuthorizer(PurchaseOrderDocument purchaseOrder, Map editingMode, Map documentActions) {
        docStatus = purchaseOrder.getStatusCode();
        documentType = purchaseOrder.getDocumentHeader().getWorkflowDocument().getDocumentType();

        currentIndicator = purchaseOrder.isPurchaseOrderCurrentIndicator();
        pendingActionIndicator = purchaseOrder.isPendingActionIndicator();
        automaticIndicator = purchaseOrder.getPurchaseOrderAutomaticIndicator();       

        this.purchaseOrder = purchaseOrder;
        this.editingMode = editingMode;       
        this.documentActions = documentActions;       
        user = GlobalVariables.getUserSession().getPerson();
    }

    /**
     * Determines whether whether to display the calculate button.
     * 
     * @return boolean true if the calculate button shall be displayed.
     */
    public boolean canCalculate() {
        // check PO status etc
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
        can = can || docStatus.equals(PurapConstants.PurchaseOrderStatuses.AWAIT_PURCHASING_REVIEW);
        
        // don't show calculate button on the split PO item selection page
        can = can && !editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);
        
        // check user authorization: whoever can edit can calculate
        can = can && documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT);
      
        return can;
    }
    
    /**
     * Determines whether to display the amend button for the purchase order document.
     * The document status must be open, and the purchase order must be current and not pending, 
     * and the user must be in purchasing group. 
     * These are same as the conditions for displaying the payment hold button. 
     * In addition to these conditions, we also have to check that there is no In Process Payment Requests nor Credit Memos associated with the PO.
     * 
     * @return boolean true if the amend button can be displayed.
     */
    public boolean canAmend() {
        // check PO status etc
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.OPEN);
        can = can && currentIndicator && !pendingActionIndicator;
        
        // in addition, check conditions about No In Process PREQ and CM) 
        if (can) {
            List<PaymentRequestView> preqViews = purchaseOrder.getRelatedViews().getRelatedPaymentRequestViews();
            if ( preqViews != null ) {
                for (PaymentRequestView preqView : preqViews) {
                    if (StringUtils.equalsIgnoreCase(preqView.getStatusCode(), PaymentRequestStatuses.IN_PROCESS)) {
                        return false;
                    }
                }
            }            
            List<CreditMemoView> cmViews = purchaseOrder.getRelatedViews().getRelatedCreditMemoViews();
            if ( cmViews != null ) {
                for (CreditMemoView cmView : cmViews) {
                    if (StringUtils.equalsIgnoreCase(cmView.getCreditMemoStatusCode(), CreditMemoStatuses.IN_PROCESS)) {
                        return false;
                    }
                }
            }
        }
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderAmendmentDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
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
    public boolean canVoid() {
        // check PO status etc
        boolean can = currentIndicator && !pendingActionIndicator;
        
        if (can) {
            boolean pendingPrint = docStatus.equals(PurapConstants.PurchaseOrderStatuses.PENDING_PRINT);
            boolean open = docStatus.equals(PurapConstants.PurchaseOrderStatuses.OPEN);
            List<PaymentRequestView> preqViews = purchaseOrder.getRelatedViews().getRelatedPaymentRequestViews();
            boolean hasPaymentRequest = preqViews != null && preqViews.size() > 0;
            can = pendingPrint || (open && !hasPaymentRequest);
        }

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderVoidDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
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
    public boolean canClose() {        
        // invoke the validation in the business rule class to find out whether this purchase order is eligible to be closed
        //TODO why calling processRouteDocument instead of processValidation ???
        PurchaseOrderCloseDocumentRule rule = new PurchaseOrderCloseDocumentRule();
        boolean can = rule.processRouteDocument(purchaseOrder);

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderCloseDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }

        return can;        
    }
    
    /**
     * Determines whether to display the open order button to reopen the purchase order document.
     * Conditions: PO status is close, PO is current and not pending, and the user is in purchasing group.
     * 
     * @return boolean true if the reopen order button can be displayed.
     */
    public boolean canReopen() {
        // check PO status etc
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.CLOSED);
        can = can && currentIndicator && !pendingActionIndicator;
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderReopenDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }

        return can;
    }
    
    /**
     * Determines whether to display the amend and payment hold buttons for the purchase order document.
     * Conditions: PO status must be open, must be current and not pending, and the user must be in purchasing group.
     * 
     * @return boolean true if the payment hold button can be displayed.
     */
    public boolean canHoldPayment() {
        // check PO status etc
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.OPEN);
        can = can && currentIndicator && !pendingActionIndicator;
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderPaymentHoldDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }

        return can;        
    }
    
    /**
     * Determines whether to display the remove hold button for the purchase order document.
     * Conditions are: PO status must be payment hold, must be current and not pending, and the user must be in purchasing group.
     * 
     * @return boolean true if the remove hold button can be displayed.
     */
    public boolean canRemoveHold() {
        // check PO status etc
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD);
        can = can && currentIndicator && !pendingActionIndicator;
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderRemoveHoldDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
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
    public boolean canRetransmit() {
        // check PO status etc
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.OPEN);
        can = can && currentIndicator && !pendingActionIndicator;
        can = can && purchaseOrder.getPurchaseOrderLastTransmitTimestamp() != null;
        
        if (!can)
            return false;       
        
        // check user authorization
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
        if (automaticIndicator) {
            // for APO use authorization for PurchaseOrderRetransmitDocument, which is anybody
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderRetransmitDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }
        else {
            // for NON_APO use authorization for PurchaseOrderDocument, which is purchasing user
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);            
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
    public boolean canPrintRetransmit() {
        // check PO status etc
        boolean can = documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT);
        can = can && docStatus.equals(PurapConstants.PurchaseOrderStatuses.CHANGE_IN_PROCESS);
        can = can && editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB);
        
        // check user authorization: same as retransmit init, since whoever can init retransmit PO shall be able to print
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
        if (automaticIndicator) {
            // for APO use authorization for PurchaseOrderRetransmitDocument, which is anybody
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderRetransmitDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }
        else {
            // for NON_APO use authorization for PurchaseOrderDocument, which is purchasing user
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);            
        }
      
        return can;
    }
    
    /**
     * Determines whether to display the button to print the pdf for the first time transmit. 
     * Conditions: PO status is Pending Print or the transmission method is changed to PRINT during the amendment. 
     * User is either in Purchasing group or an action is requested of them for the document transmission route node.
     * 
     * @return boolean true if the print first transmit button can be displayed.
     */
    public boolean canFirstTransmitPrintPo() {
        // status shall be Pending Print, or the transmission method is changed to PRINT during amendment, 
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.PENDING_PRINT);
        if (!can) {
            can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.OPEN);
            can = can && purchaseOrder.getDocumentHeader().getWorkflowDocument().stateIsFinal();
            can = can && purchaseOrder.getPurchaseOrderLastTransmitTimestamp() == null;
            String method = purchaseOrder.getPurchaseOrderTransmissionMethodCode();
            can = can && method != null && method.equals(PurapConstants.POTransmissionMethods.PRINT);
        }
        
        // user is either authorized or an action is requested of them for the document transmission route node, 
        if (can) {
            PurApWorkflowIntegrationService service = SpringContext.getBean(PurApWorkflowIntegrationService.class);
            can = service.isActionRequestedOfUserAtNodeName(purchaseOrder.getDocumentNumber(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), user);
            if (!can) {
                DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
                can = documentAuthorizer.isAuthorized(purchaseOrder, PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.PRINT_PO, user.getPrincipalId());               
            }
        }
        
        return can;
    }
    
    /**
     * Determines whether to display the print preview button for the first time transmit. Conditions are:
     * available prior to the document going to final;
     * available for only a certain number of PO transmission types which are stored in a parameter (default to PRIN and FAX);
     * viewable by new workgroup (default to PA_PUR_USERS).
     * 
     * @return boolean true if the preview print button can be displayed.
     */
   public boolean canPreviewPrintPo() {
        // PO is prior to FINAL
        boolean can = !purchaseOrder.getDocumentHeader().getWorkflowDocument().stateIsFinal();

        // transmission method must be one of those specified by the parameter
        if (can) {
            List<String> methods = SpringContext.getBean(ParameterService.class).getParameterValues(PurchaseOrderDocument.class, PurapParameterConstants.PURAP_PO_PRINT_PREVIEW_TRANSMISSION_METHOD_TYPES);
            String method = purchaseOrder.getPurchaseOrderTransmissionMethodCode();
            can = (methods == null || methods.contains(method));
        }
        
        // check user authorization: same as print PO
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            can = documentAuthorizer.isAuthorized(purchaseOrder, PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.PRINT_PO, user.getPrincipalId());              
        }

        return can;
    }

//FIXME hjs
//    public boolean canCreateReceiving() {        
//      return SpringContext.getBean(ReceivingService.class).canCreateLineItemReceivingDocument(purchaseOrder) && isUserAuthorized;
//    }
    
    /**
     * Determines if a Split PO Document can be created from this purchase order. Conditions: 
     * The parent PO status is either "In Process" or "Awaiting Purchasing Review"; requisition source is not B2B; has at least 2 items, 
     * and PO is not in the process of being split; user must be in purchasing group.
     * 
     * @return boolean true if the split PO button can be displayed.
     */
    public boolean canSplitPo() {
        // PO must be in either "In Process" or "Awaiting Purchasing Review"
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
        can = can && !purchaseOrder.getDocumentHeader().getWorkflowDocument().stateIsEnroute(); 
        can = can || docStatus.equals(PurapConstants.PurchaseOrderStatuses.AWAIT_PURCHASING_REVIEW);
        
        // can't split a SplitPO Document, according to new specs
        can = can && !(purchaseOrder instanceof PurchaseOrderSplitDocument); 
        
        // can't initiate another split during the splitting process. 
        can = can && !editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);
        
        // Requisition Source must not be B2B.
        can = can && !purchaseOrder.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B);
        
        // PO must have more than one line item.
        if (can) {
            List<PurApItem> items = (List<PurApItem>)purchaseOrder.getItems();
            int itemsBelowTheLine = PurApItemUtils.countBelowTheLineItems(items);
            can = items.size() - itemsBelowTheLine > 1;
        }
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderSplitDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }
              
        return can;
    }
    
    /**
     * Determines whether the PO is in a status that signifies it has enough information to generate a Split PO.
     * 
     * @return  True if the PO can continue to be split.
     */
    public boolean canContinuePoSplit() {
        boolean can = editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PurchaseOrderSplitDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }
              
        return can;        
    }
    
    /**
     * Determines if a line item receiving document can be created for the purchase order.
     * 
     * @return boolean true if the receiving document button can be displayed.
     */
    public boolean canCreateReceiving() {       
        // check PO status and item info 
        boolean can = SpringContext.getBean(ReceivingService.class).canCreateLineItemReceivingDocument(purchaseOrder);
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(LineItemReceivingDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }
              
        return can;
    }
    
    /**
     * Determines whether to display the resend po button for the purchase order document.
     * Conditions: PO status must be error sending cxml, must be current and not pending, and the user must be in purchasing group.
     * 
     * @return boolean true if the resend po button shall be displayed.
     */
    public boolean canResendCxml() {
        // check PO status etc
        boolean can = docStatus.equals(PurapConstants.PurchaseOrderStatuses.CXML_ERROR);
        can = can && currentIndicator && !pendingActionIndicator;
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
            can = documentAuthorizer.isAuthorized(purchaseOrder, PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.RESEND_PO, user.getPrincipalId());
        }
      
        return can;
    }
        
    /**
     * Determines whether the current user has the authorization to assign sensitive data to the PO in its current status,
     * and thus show the assign sensitive data button.
     * 
     * @return boolean true if the assign sensitive data button shall be displayed.
     */
    public boolean canAssignSensitiveData() {
        // check user authorization
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrder);
        return documentAuthorizer.isAuthorized(purchaseOrder, PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.ASSIGN_SENSITIVE_DATA, user.getPrincipalId());
    }

}

