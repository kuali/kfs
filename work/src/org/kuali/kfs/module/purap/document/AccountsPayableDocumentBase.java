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
package org.kuali.module.purap.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.RouteDocumentEvent;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Accounts Payable Document Base
 * 
 */
public abstract class AccountsPayableDocumentBase extends PurchasingAccountsPayableDocumentBase implements AccountsPayableDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentBase.class);
    
    // SHARED FIELDS BETWEEN PAYMENT REQUEST AND CREDIT MEMO
    private Date accountsPayableApprovalDate;
    private String lastActionPerformedByUniversalUserId;
    private String accountsPayableProcessorIdentifier;
    private boolean holdIndicator;
    private Date extractedDate;
    private Integer purchaseOrderIdentifier;
    private String processingCampusCode;
    private String noteLine1Text;
    private String noteLine2Text;
    private String noteLine3Text;   
    private boolean continuationAccountIndicator;
    
    private boolean unmatchedOverride; // not persisted
    
    // NOT PERSISTED IN DB
    // BELOW USED BY ROUTING
    private String chartOfAccountsCode;
    private String organizationCode;

    private boolean closeReopenPoIndicator;
    
    // NOT PERSISTED IN DB
    // BELOW USED BY GL ENTRY CREATION
    private boolean generateEncumbranceEntries;
    private boolean generateCancelEntries;

    // REFERENCE OBJECTS
    private Campus processingCampus;
    private transient PurchaseOrderDocument purchaseOrderDocument;

    public AccountsPayableDocumentBase() {
        super();
        setUnmatchedOverride(false);
    }

    public boolean requiresAccountsPayableReviewRouting() {
        return documentHasNoImagesAttached();
    }

    public boolean approvalAtAccountsPayableReviewAllowed() {
        return !documentHasNoImagesAttached();
    }
    
    private boolean documentHasNoImagesAttached() {
        List boNotes = this.getDocumentBusinessObject().getBoNotes();
        if (ObjectUtils.isNotNull(boNotes)) {
            for (Object obj : boNotes) {
                Note note = (Note) obj;
                //may need to refresh this attachment because of a bug - see see KULPURAP-1397
                note.refreshReferenceObject("attachment");
                if (ObjectUtils.isNotNull(note.getAttachment())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        if(ObjectUtils.isNotNull(getPurchaseOrderDocument())) {
            this.setChartOfAccountsCode(getPurchaseOrderDocument().getChartOfAccountsCode());
            this.setOrganizationCode(getPurchaseOrderDocument().getOrganizationCode());
        }
        super.populateDocumentForRouting();
    }

    /**
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        //if routing and closereopen po indicator set, call closereopen po method
        if((event instanceof RouteDocumentEvent) && this.isCloseReopenPoIndicator()){
            processCloseReopenPo();
            this.setCloseReopenPoIndicator(false);
        }

        //copied from super because we can't call super for AP docs
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(this);

        //DO NOT CALL SUPER HERE!!  Cannot call super because it will mess up the GL entry creation process (hjs)
        //super.prepareForSave(event);
    }

    /**
     * Helper method to be called from custom prepare for save and to be
     * overriden by sub class.
     */
    public abstract String getPoDocumentTypeForAccountsPayableDocumentApprove();
    
    /**
     * This method should be called from child class from overridden processCloseReopenPo(), it will pass the action it will take,
     * which is document specific.
     * 
     * @param docType
     */
    public void processCloseReopenPo() {
        String docType = getPoDocumentTypeForAccountsPayableDocumentApprove();
        String action = null;
        if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT.equals(docType)) {
            action = "closed";
        }
        else if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT.equals(docType)) {
            action = "reopened";
        }
        else {
            logAndThrowRuntimeException("Method processCloseReopenPo called using ID + '" + getPurapDocumentIdentifier() + "' and invalid doc type '" + docType + "'");
        }
//        SpringContext.getBean(PurchaseOrderService.class).updateFlagsAndRoute(this.getPurchaseOrderDocument().getDocumentNumber(), docType, assemblePurchaseOrderNote(docType, identifier, action, docName), new ArrayList());
        SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(this.getPurchaseOrderDocument().getDocumentNumber(), docType, assemblePurchaseOrderNote(docType, action), new ArrayList());
    }

    /**
     * This method generates a note for the close/reopen po method.
     * 
     * @param docType
     * @param preqId
     * @return
     */
    private String assemblePurchaseOrderNote(String docType, String action) {
        String documentLabel = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(getClass());
        StringBuffer closeReopenNote = new StringBuffer("");
        String userName = GlobalVariables.getUserSession().getUniversalUser().getPersonName();
        closeReopenNote.append(SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(PurchaseOrderDocument.class));
        closeReopenNote.append(" will be manually ");
        closeReopenNote.append(action);
        closeReopenNote.append(" by ");
        closeReopenNote.append(userName);
        closeReopenNote.append(" when approving ");
        closeReopenNote.append(documentLabel);
        closeReopenNote.append(" with ");
        closeReopenNote.append(SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(getClass(), PurapPropertyConstants.PURAP_DOC_ID));
        closeReopenNote.append(" ");
        closeReopenNote.append(getPurapDocumentIdentifier());

        return closeReopenNote.toString();
    }
    
    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    @Override
    public void handleRouteLevelChange(DocumentRouteLevelChangeVO levelChangeEvent) {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange(levelChangeEvent);
        try {
            String newNodeName = levelChangeEvent.getNewNodeName();
            if (processNodeChange(newNodeName, levelChangeEvent.getOldNodeName())) {
                if (StringUtils.isNotBlank(newNodeName)) {
                    ReportCriteriaVO reportCriteriaVO = new ReportCriteriaVO(Long.valueOf(getDocumentNumber()));
                    reportCriteriaVO.setTargetNodeName(newNodeName);
                    if (SpringContext.getBean(KualiWorkflowInfo.class).documentWillHaveAtLeastOneActionRequest(
                            reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ})) {
                        NodeDetails nodeDetailEnum = getNodeDetailEnum(newNodeName);
                        if (ObjectUtils.isNotNull(nodeDetailEnum)) {
                            String statusCode = nodeDetailEnum.getAwaitingStatusCode();
                            if (StringUtils.isNotBlank(statusCode)) {
                                SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(this, statusCode);
                                saveDocumentFromPostProcessing();
                            }
                            else {
                                LOG.debug("Document with id " + getDocumentNumber() + " will stop in route node '" + newNodeName + "' but no awaiting status found to set");
                            }
                        }
                        else {
                            LOG.debug("Document with id " + getDocumentNumber() + " will not stop in route node '" + newNodeName + "' but node cannot be found to get awaiting status");
                        }
                    } 
                    else {
                        LOG.debug("Document with id " + getDocumentNumber() + " will not stop in route node '" + newNodeName + "'");
                    }
                }
            }
        }
        catch (WorkflowException e) {
            String errorMsg = "Workflow Error found checking actions requests on document with id " + getDocumentNumber() + ". *** WILL NOT UPDATE PURAP STATUS ***";
            LOG.warn(errorMsg, e);
        }
    }
    
    public abstract boolean processNodeChange(String newNodeName, String oldNodeName);
    
    public abstract NodeDetails getNodeDetailEnum(String nodeName);
    
    public abstract void saveDocumentFromPostProcessing();

    // GETTERS AND SETTERS    
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public String getAccountsPayableProcessorIdentifier() { 
        return accountsPayableProcessorIdentifier;
    }

    public void setAccountsPayableProcessorIdentifier(String accountsPayableProcessorIdentifier) {
        this.accountsPayableProcessorIdentifier = accountsPayableProcessorIdentifier;
    }

    public String getLastActionPerformedByUniversalUserId() {
        return lastActionPerformedByUniversalUserId;
    }

    public void setLastActionPerformedByUniversalUserId(String lastActionPerformedByUniversalUserId) {
        this.lastActionPerformedByUniversalUserId = lastActionPerformedByUniversalUserId;
    }

    public String getProcessingCampusCode() { 
        return processingCampusCode;
    }

    public void setProcessingCampusCode(String processingCampusCode) {
        this.processingCampusCode = processingCampusCode;
    }

    public Date getAccountsPayableApprovalDate() { 
        return accountsPayableApprovalDate;
    }

    public void setAccountsPayableApprovalDate(Date accountsPayableApprovalDate) {
        this.accountsPayableApprovalDate = accountsPayableApprovalDate;
    }

    public Date getExtractedDate() {
        return extractedDate;
    }

    public void setExtractedDate(Date extractedDate) {
        this.extractedDate = extractedDate;
    }

    public boolean isHoldIndicator() {
        return holdIndicator;
    }

    public void setHoldIndicator(boolean holdIndicator) {
        this.holdIndicator = holdIndicator;
    }

    public String getNoteLine1Text() {
        return noteLine1Text;
    }

    public void setNoteLine1Text(String noteLine1Text) {
        this.noteLine1Text = noteLine1Text;
    }

    public String getNoteLine2Text() {
        return noteLine2Text;
    }

    public void setNoteLine2Text(String noteLine2Text) {
        this.noteLine2Text = noteLine2Text;
    }

    public String getNoteLine3Text() {
        return noteLine3Text;
    }

    public void setNoteLine3Text(String noteLine3Text) {
        this.noteLine3Text = noteLine3Text;
    }

    public Campus getProcessingCampus() { 
        return processingCampus;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public boolean isGenerateEncumbranceEntries() {
        return generateEncumbranceEntries;
    }

    public void setGenerateEncumbranceEntries(boolean generateEncumbranceEntries) {
        this.generateEncumbranceEntries = generateEncumbranceEntries;
    }

    public boolean isGenerateCancelEntries() {
        return generateCancelEntries;
    }

    public void setGenerateCancelEntries(boolean generateCancelEntries) {
        this.generateCancelEntries = generateCancelEntries;
    }

    public PurchaseOrderDocument getPurchaseOrderDocument() {
        if ( (ObjectUtils.isNull(purchaseOrderDocument)) && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier())) ) {
            setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(this.getPurchaseOrderIdentifier()));
        }
        return purchaseOrderDocument;
    }

    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            //KUALI-PURAP 1185 PO Id not being set to null, instead throwing error on main screen that value is invalid.
            //setPurchaseOrderIdentifier(null);
            this.purchaseOrderDocument = null;
        } else {
            setPurchaseOrderIdentifier(purchaseOrderDocument.getPurapDocumentIdentifier());
            this.purchaseOrderDocument = purchaseOrderDocument;
        }
    }

    public boolean isCloseReopenPoIndicator() {
        return closeReopenPoIndicator;
    }

    public void setCloseReopenPoIndicator(boolean closeReopenPoIndicator) {
        this.closeReopenPoIndicator = closeReopenPoIndicator;
    }

    //Helper methods
    public UniversalUser getLastActionPerformedByUser(){
        try {
            UniversalUser user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(getLastActionPerformedByUniversalUserId());
            return user;
        }
        catch (UserNotFoundException unfe) {
            return null;
        }
    }

    public String getLastActionPerformedByPersonName(){
        UniversalUser user = getLastActionPerformedByUser();
        if (ObjectUtils.isNull(user)) {
            return "";
        }
        else {
            return user.getPersonName();
        }
    }

    /**
     * Gets the unmatchedOverride attribute.
     * 
     * @return Returns the unmatchedOverride.
     */
    public boolean isUnmatchedOverride() {
        return unmatchedOverride;
    }

    /**
     * Sets the unmatchedOverride attribute value.
     * 
     * @param unmatchedOverride The unmatchedOverride to set.
     */
    public void setUnmatchedOverride(boolean unmatchedOverride) {
        this.unmatchedOverride = unmatchedOverride;
    }

    public abstract KualiDecimal getGrandTotal();
    
    /** 
     * This method returns the amount entered on the initial screen.
     * 
     * @return
     */
    public abstract KualiDecimal getInitialAmount();    

    /**
     * Gets the continuationAccountIndicator attribute. 
     * @return Returns the continuationAccountIndicator.
     */
    public boolean isContinuationAccountIndicator() {
        return continuationAccountIndicator;
    }

    /**
     * Sets the continuationAccountIndicator attribute value.
     * @param continuationAccountIndicator The continuationAccountIndicator to set.
     */
    public void setContinuationAccountIndicator(boolean continuationAccountIndicator) {
        this.continuationAccountIndicator = continuationAccountIndicator;
    }
    
    public boolean isExtracted() {
        return (this.getExtractedDate()==null)?false:true;
    }
}
