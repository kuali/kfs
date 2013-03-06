/*
 * Copyright 2006 The Kuali Foundation
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
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.PurApGenericAttributes;
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
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderSplitDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Struts Action Form for Purchase Order document.
 */
public class PurchaseOrderForm extends PurchasingFormBase {

    protected PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine;
    protected PurchaseOrderVendorQuote newPurchaseOrderVendorQuote;
    protected Long awardedVendorNumber;

    // Retransmit.
    protected String[] retransmitItemsSelected = {};
    protected String retransmitTransmissionMethod;
    protected String retransmitFaxNumber;
    protected String retransmitHeader;

    // Need this for amendment for accounting line only
    protected Map accountingLineEditingMode;

    protected String splitNoteText;

    // Assign Sensitive Data related fields
    protected String sensitiveDataAssignmentReason = null; // reason for current assignment of sensitive data to the PO
    protected SensitiveDataAssignment lastSensitiveDataAssignment = null; // last sensitive data assignment info for the PO
    protected SensitiveData newSensitiveDataLine = null; // new sensitive data entry to be added to the PO
    protected List<SensitiveData> sensitiveDatasAssigned = null;  // sensitive data entries currently assigned to the PO

    protected final String PURCHASING_PROCESSOR_ROLE_NAME = "Purchasing Processor";

    /**
     * Constructs a PurchaseOrderForm instance and sets up the appropriately casted document.
     */
    public PurchaseOrderForm() {
        super();

        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());
        setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        this.accountingLineEditingMode = new HashMap();
        //on PO, account distribution should be read only
        setReadOnlyAccountDistributionMethod(true);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "PO";
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

    public boolean isReadOnlyReceivingRequired() {

        PurchaseOrderDocument poDoc = getPurchaseOrderDocument();

        if (poDoc instanceof PurchaseOrderAmendmentDocument) {
            if (!poDoc.isReceivingDocumentRequiredIndicator()) {
                if (GlobalVariables.getMessageMap().hasNoErrors()) {
                    // only check whether the PO has active payment requests if there is currently no error in the MessageMap,
                    // otherwise
                    // we're going to get WorkflowServiceErrorException("Document Search Validation Errors") in the
                    // DocumentSearchService
                    // when this next line execute
                    return SpringContext.getBean(PaymentRequestService.class).hasActivePaymentRequestsForPurchaseOrder(poDoc.getPurapDocumentIdentifier());
                }
                else {
                    return true;
                }
            }
        }

        return false;
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
            if (StringUtils.equals(getPurchaseOrderDocument().getApplicationDocumentStatus(),PurchaseOrderStatuses.APPDOC_IN_PROCESS)){
                return PurchaseOrderStatuses.APPDOC_IN_PROCESS;
            } else if (StringUtils.equals(getPurchaseOrderDocument().getApplicationDocumentStatus(),PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT)){
                return PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT;
            }else if (StringUtils.equals(getPurchaseOrderDocument().getApplicationDocumentStatus(),PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR)){
                return PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR;
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

        if (KRADConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && methodToCallList.contains(methodToCallParameterValue)) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    @Override
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);

        String poIDstr = getPurchaseOrderDocument().getPurapDocumentIdentifier().toString();

        //KFSMI-4576 masking/unmasking PO number...
        //If the document status is not FINAL then check for permissions
        if (!workflowDocument.getStatus().getCategory().equals(DocumentStatusCategory.SUCCESSFUL)) {
            String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            String namespaceCode = KFSConstants.ParameterNamespaces.KNS;
            String permissionTemplateName = KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD;

            Map<String,String> roleQualifiers = new HashMap<String,String>();

            Map<String,String> permissionDetails = new HashMap<String,String>();
            permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, PurchaseOrderDocument.class.getSimpleName());
            permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, PurapPropertyConstants.PURAP_DOC_ID);

            IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
            Boolean isAuthorized = identityManagementService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers);

            //principalId is not authorized to see the PO number so mask the value.
            if (!isAuthorized) {
                DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
                if (ObjectUtils.isNotNull(getPurchaseOrderDocument().getPurapDocumentIdentifier())) {
                    poIDstr = "";
                    int strLength = dataDictionaryService.getAttributeMaxLength(PurApGenericAttributes.class.getName(), PurapPropertyConstants.PURAP_DOC_ID);
                    for (int i = 0; i < strLength; i++) {
                        poIDstr = poIDstr.concat("*");
                    }
               }
            }
        }

        if (ObjectUtils.isNotNull(getPurchaseOrderDocument().getPurapDocumentIdentifier())) {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", poIDstr));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", PurapConstants.PURAP_APPLICATION_DOCUMENT_ID_NOT_AVAILABLE));
        }
        if (ObjectUtils.isNotNull(getPurchaseOrderDocument().getApplicationDocumentStatus())) {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.applicationDocumentStatus", getPurchaseOrderDocument().getApplicationDocumentStatus()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.applicationDocumentStatus", PurapConstants.PURAP_APPLICATION_DOCUMENT_STATUS_NOT_AVAILABLE));
        }
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
//    @Override
 //   public void populate(HttpServletRequest request) {
 //       super.populate(request);
//
 //       PurchaseOrderDocument po = (PurchaseOrderDocument)getDocument();

        // RICE20 : need to determine what to do about documentBusinessObject
//        if (ObjectUtils.isNotNull(po.getPurapDocumentIdentifier())) {
//            po.refreshDocumentBusinessObject();
//        }
 //       NoteService noteService = SpringContext.getBean(NoteService.class);
//        for (Note note : noteService.getByRemoteObjectId(po.getObjectId())) {
 //           note.refreshReferenceObject("attachment");
  //      }
  //  }

    /**
     * Processes validation rules having to do with any payment requests that the given purchase order may have. Specifically,
     * validates that at least one payment request exists, and makes further checks about the status of such payment requests.
     *
     * @param document A PurchaseOrderDocument
     * @return True if the document passes all the validations.
     */
    protected boolean processPaymentRequestRulesForCanClose(PurchaseOrderDocument document) {
        boolean valid = false;
        Integer poDocId = document.getPurapDocumentIdentifier();

        boolean checkInProcess = false;
        boolean hasInProcess = false;

        Map <String, String> processPaymentRequestResult = new HashMap<String, String>();
        processPaymentRequestResult = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestsByStatusAndPurchaseOrderId(PaymentRequestStatuses.APPDOC_IN_PROCESS, poDocId);

        if ("Y".equals(processPaymentRequestResult.get("hasInProcess"))) {
            hasInProcess = true;
        }

        if ("Y".equals(processPaymentRequestResult.get("checkInProcess"))) {
            checkInProcess = true;
        }

        if (checkInProcess && !hasInProcess) {
            valid = true;
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
    protected boolean canAmend() {
        boolean can = SpringContext.getBean(PurchaseOrderService.class).isPurchaseOrderOpenForProcessing(getPurchaseOrderDocument());

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_AMENDMENT, GlobalVariables.getUserSession().getPerson());
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
    protected boolean canVoid() {
        // check PO status etc
        boolean can = getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();

        if (can) {
            boolean pendingPrint = PurchaseOrderStatuses.APPDOC_PENDING_PRINT.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
            boolean open = PurchaseOrderStatuses.APPDOC_OPEN.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
            boolean errorCxml = PurchaseOrderStatuses.APPDOC_CXML_ERROR.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
            boolean errorFax = PurchaseOrderStatuses.APPDOC_FAX_ERROR.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());

            List<PaymentRequestView> preqViews = getPurchaseOrderDocument().getRelatedViews().getRelatedPaymentRequestViews();
            boolean hasPaymentRequest = preqViews != null && preqViews.size() > 0;

            can = pendingPrint || (open && !hasPaymentRequest) || errorCxml || errorFax;
        }

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_VOID, GlobalVariables.getUserSession().getPerson());
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
    protected boolean canClose() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.APPDOC_OPEN.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();
        can = can && processPaymentRequestRulesForCanClose(getPurchaseOrderDocument());

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_CLOSE, GlobalVariables.getUserSession().getPerson());
        }

        return can;
    }

    /**
     * Determines whether to display the open order button to reopen the purchase order document.
     * Conditions: PO status is close, PO is current and not pending, and the user is in purchasing group.
     *
     * @return boolean true if the reopen order button can be displayed.
     */
    protected boolean canReopen() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.APPDOC_CLOSED.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_REOPEN, GlobalVariables.getUserSession().getPerson());
        }

        return can;
    }

    /**
     * Determines whether to display the payment hold buttons for the purchase order document.
     * Conditions: PO status must be open, must be current and not pending, and the user must be in purchasing group.
     *
     * @return boolean true if the payment hold button can be displayed.
     */
    protected boolean canHoldPayment() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.APPDOC_OPEN.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_PAYMENT_HOLD, GlobalVariables.getUserSession().getPerson());
        }

        return can;
    }

    /**
     * Determines whether to display the remove hold button for the purchase order document.
     * Conditions are: PO status must be payment hold, must be current and not pending, and the user must be in purchasing group.
     *
     * @return boolean true if the remove hold button can be displayed.
     */
    protected boolean canRemoveHold() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.APPDOC_PAYMENT_HOLD.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_REMOVE_HOLD, GlobalVariables.getUserSession().getPerson());
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
    protected boolean canRetransmit() {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.APPDOC_OPEN.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
        can = can && getPurchaseOrderDocument().isPurchaseOrderCurrentIndicator() && !getPurchaseOrderDocument().isPendingActionIndicator();
        can = can && getPurchaseOrderDocument().getPurchaseOrderLastTransmitTimestamp() != null;
        can = can && !PurapConstants.RequisitionSources.B2B.equals(getPurchaseOrderDocument().getRequisitionSourceCode());
        can = can && !editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB);

        if (!can) {
            return false;
        }

        // check user authorization
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
        if (getPurchaseOrderDocument().getPurchaseOrderAutomaticIndicator()) {
            // for APO use authorization for PurchaseOrderRetransmitDocument, which is anybody
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_RETRANSMIT, GlobalVariables.getUserSession().getPerson());
        }
        else {
            // for NON_APO use authorization for PurchaseOrderDocument, which is purchasing user
            can = (documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER, GlobalVariables.getUserSession().getPerson()) ||
                    documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.CONTRACT_MANAGER_ASSIGNMENT, GlobalVariables.getUserSession().getPerson()));        }

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
    protected boolean canPrintRetransmit() {
        // check PO status etc
        boolean can = getPurchaseOrderDocument().getDocumentHeader().getWorkflowDocument().getDocumentTypeName().equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT);
        can = can && editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB);

        if (can) {
            // check user authorization: same as retransmit init, since whoever can init retransmit PO shall be able to print
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            if (getPurchaseOrderDocument().getPurchaseOrderAutomaticIndicator()) {
                // for APO use authorization for PurchaseOrderRetransmitDocument, which is anybody
                can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_RETRANSMIT, GlobalVariables.getUserSession().getPerson());
            }
            else {
                // for NON_APO use authorization for PurchaseOrderDocument, which is purchasing user
                can = (documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER, GlobalVariables.getUserSession().getPerson()) ||
                        documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.CONTRACT_MANAGER_ASSIGNMENT, GlobalVariables.getUserSession().getPerson()));
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
    protected boolean canSplitPo() {
        // PO must be in either "In Process" or "Awaiting Purchasing Review"
        boolean can = PurchaseOrderStatuses.APPDOC_IN_PROCESS.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());
        can = can && !getPurchaseOrderDocument().getDocumentHeader().getWorkflowDocument().isEnroute();
        can = can || PurchaseOrderStatuses.APPDOC_AWAIT_PURCHASING_REVIEW.equals(getPurchaseOrderDocument().getApplicationDocumentStatus());

        // can't split a SplitPO Document, according to new specs
        can = can && !(getPurchaseOrderDocument() instanceof PurchaseOrderSplitDocument);

        // can't initiate another split during the splitting process.
        can = can && !editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);

        // Requisition Source must not be B2B.
        can = can && !getPurchaseOrderDocument().getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B);

        // PO must have more than one line item.
        if (can) {
            List<PurApItem> items = getPurchaseOrderDocument().getItems();
            int itemsBelowTheLine = PurApItemUtils.countBelowTheLineItems(items);
            can = items.size() - itemsBelowTheLine > 1;
        }

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_SPLIT, GlobalVariables.getUserSession().getPerson());
        }

        return can;
    }

    /**
     * Determines whether the PO is in a status that signifies it has enough information to generate a Split PO.
     *
     * @return  True if the PO can continue to be split.
     */
    protected boolean canContinuePoSplit() {
        boolean can = editingMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_SPLIT, GlobalVariables.getUserSession().getPerson());
        }

        return can;
    }

    /**
     * Determines if a line item receiving document can be created for the purchase order.
     *
     * @return boolean true if the receiving document button can be displayed.
     */
    protected boolean canCreateReceiving() {
        // check PO status and item info
        boolean can = SpringContext.getBean(ReceivingService.class).canCreateLineItemReceivingDocument(getPurchaseOrderDocument());

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPurchaseOrderDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.LINE_ITEM_RECEIVING, GlobalVariables.getUserSession().getPerson());
        }

        return can;
    }

    /**
     * Creates a MAP for all the buttons to appear on the Purchase Order Form, and sets the attributes of these buttons.
     *
     * @return the button map created.
     */
    protected Map<String, ExtraButton> createButtonsMap() {
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

