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
package org.kuali.module.purap.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.PurapWorkflowConstants.CreditMemoDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.CreditMemoAccount;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.CreditMemoDao;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.vendor.util.VendorUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Provides services to support the creation of a Credit Memo Document.
 */
@Transactional
public class CreditMemoServiceImpl implements CreditMemoService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoServiceImpl.class);

    private CreditMemoDao creditMemoDao;
    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private NoteService noteService;
    private PurapService purapService;
    private PurapGeneralLedgerService purapGeneralLedgerService;
    private PaymentRequestService paymentRequestService;
    private PurchaseOrderService purchaseOrderService;

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#getCreditMemosToExtract(java.lang.String)
     */
    public Iterator<CreditMemoDocument> getCreditMemosToExtract(String chartCode) {
        LOG.debug("getCreditMemosToExtract() started");

        return creditMemoDao.getCreditMemosToExtract(chartCode);
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#creditMemoDuplicateMessages(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public String creditMemoDuplicateMessages(CreditMemoDocument cmDocument) {
        String duplicateMessage = null;

        String vendorNumber = cmDocument.getVendorNumber();
        if (StringUtils.isEmpty(vendorNumber)) {
            PurchasingAccountsPayableDocument sourceDocument = cmDocument.getPurApSourceDocumentIfPossible();
            if (ObjectUtils.isNotNull(sourceDocument)) {
                vendorNumber = sourceDocument.getVendorNumber();
            }
        }

        if (StringUtils.isNotEmpty(vendorNumber)) {
            // check for existence of another credit memo with the same vendor and vendor credit memo number
            if (creditMemoDao.duplicateExists(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber), cmDocument.getCreditMemoNumber())) {
                duplicateMessage = kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER);
            }

            // check for existence of another credit memo with the same vendor and credit memo date
            if (creditMemoDao.duplicateExists(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber), cmDocument.getCreditMemoDate(), cmDocument.getCreditMemoAmount())) {
                duplicateMessage = kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER_DATE_AMOUNT);
            }
        }

        return duplicateMessage;
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#getPOInvoicedItems(org.kuali.module.purap.document.PurchaseOrderDocument)
     */
    public List<PurchaseOrderItem> getPOInvoicedItems(PurchaseOrderDocument poDocument) {
        List<PurchaseOrderItem> invoicedItems = new ArrayList<PurchaseOrderItem>();

        for (Iterator iter = poDocument.getItems().iterator(); iter.hasNext();) {
            PurchaseOrderItem poItem = (PurchaseOrderItem) iter.next();

            // only items of type above the line can be considered for being invoiced
            if (!poItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                continue;
            }

            if (poItem.getItemType().isQuantityBasedGeneralLedgerIndicator() && poItem.getItemInvoicedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)) {
                invoicedItems.add(poItem);
            }
            else {
                BigDecimal unitPrice = (poItem.getItemUnitPrice() == null ? new BigDecimal(0) : poItem.getItemUnitPrice());
                if (unitPrice.doubleValue() > poItem.getItemOutstandingEncumberedAmount().doubleValue()) {
                    invoicedItems.add(poItem);
                }
            }
        }

        return invoicedItems;
    }


    /**
     * @see org.kuali.module.purap.service.CreditMemoService#calculateCreditMemo(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void calculateCreditMemo(CreditMemoDocument cmDocument) {
        
        cmDocument.updateExtendedPriceOnItems();
        
        for (CreditMemoItem item : (List<CreditMemoItem>) cmDocument.getItems()) {
            // update unit price for service items
            if (item.getItemType().isItemTypeAboveTheLineIndicator() && !item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                item.setItemUnitPrice(new BigDecimal(item.getExtendedPrice().toString()));
            }
            // make restocking fee is negative
            else if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, item.getItemTypeCode())) {
                item.setExtendedPrice(item.getExtendedPrice().abs().negated());
                if (item.getItemUnitPrice() != null) {
                    item.setItemUnitPrice(item.getItemUnitPrice().abs().negate());
                }
            }
        }

        //proration
      //TODO (KULPURAP-1575: ckirschenman) move this to the accounts payable service and call, since it is essentially the easiest case from that
        if(cmDocument.isSourceVendor()) {
            //no proration on vendor
            return;
        }
        
        for (CreditMemoItem item : (List<CreditMemoItem>)cmDocument.getItems()) {
            
            //skip above the line
            if(item.getItemType().isItemTypeAboveTheLineIndicator()) {
                continue;
            }
            
            if((item.getSourceAccountingLines().isEmpty()) && (ObjectUtils.isNotNull(item.getExtendedPrice())) && 
                    (KualiDecimal.ZERO.compareTo(item.getExtendedPrice())!=0)) {

                KualiDecimal totalAmount = KualiDecimal.ZERO;
                List<PurApAccountingLine> distributedAccounts = null;
                List<SourceAccountingLine> summaryAccounts = null;

                totalAmount = cmDocument.getPurApSourceDocumentIfPossible().getTotalDollarAmount();
                //this should do nothing on preq which is fine
                SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(cmDocument.getPurApSourceDocumentIfPossible());
                summaryAccounts = SpringContext.getBean(PurapAccountingService.class).generateSummary(cmDocument.getPurApSourceDocumentIfPossible().getItems());
                distributedAccounts = SpringContext.getBean(PurapAccountingService.class).generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE,CreditMemoAccount.class); 

                if(CollectionUtils.isNotEmpty(distributedAccounts)&&
                        CollectionUtils.isEmpty(item.getSourceAccountingLines())) {
                    item.setSourceAccountingLines(distributedAccounts);
                }
            }
        }
        //end proration
    }

    private CreditMemoDocument getCreditMemoByDocumentNumber(String documentNumber) {
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                CreditMemoDocument doc = (CreditMemoDocument)documentService.getByDocumentHeaderId(documentNumber);
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting credit memo document from document service";
                LOG.error("getCreditMemoByDocumentNumber() " + errorMessage,e);
                throw new RuntimeException(errorMessage,e);
            }
        }
        return null;
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#getCreditMemoDocumentById(java.lang.Integer)
     */
    public CreditMemoDocument getCreditMemoDocumentById(Integer purchasingDocumentIdentifier) {
        return getCreditMemoByDocumentNumber(creditMemoDao.getDocumentNumberByCreditMemoId(purchasingDocumentIdentifier));
    }

    /**
     * Not used
     * 
     * @see org.kuali.module.purap.service.CreditMemoService#saveDocumentWithoutValidation(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void saveDocumentWithoutValidation(CreditMemoDocument document) {
        try {
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
//          documentService.saveDocumentWithoutRunningValidation(document);

        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage(); 
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
        catch( RuntimeException re){
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + re.getMessage(); 
            LOG.error(errorMsg, re);
            throw new RuntimeException(errorMsg, re);            
        }
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#saveDocument(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void populateAndSaveCreditMemo(CreditMemoDocument document) {
        try {
            document.setStatusCode(PurapConstants.CreditMemoStatuses.IN_PROCESS);            
            documentService.saveDocument(document, ContinueAccountsPayableEvent.class);
        }
        catch(ValidationException ve){
            document.setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);
        }        
        catch (WorkflowException we) {
            //set the status back to initiate
            document.setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage(); 
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /** 
     * This method reopens PO if closed
     * 
     * @param cmDocument
     */
    public void reopenClosedPO(CreditMemoDocument cmDocument){

        // TODO (KULPURAP-1576: dlemus) CHRIS/DELYEA - THIS SHOULD HAPPEN WITH GL AND PERCENT CONVERT AT 'route document'
        // reopen PO if closed
        Integer purchaseOrderDocumentId = cmDocument.getPurchaseOrderIdentifier();
        if (cmDocument.isSourceDocumentPaymentRequest() && 
            ObjectUtils.isNull(purchaseOrderDocumentId)) {
            PaymentRequestDocument paymentRequestDocument = paymentRequestService.getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
            purchaseOrderDocumentId = paymentRequestDocument.getPurchaseOrderIdentifier();
        }
        // if we found a valid po id number then check it for reopening
        if (ObjectUtils.isNotNull(purchaseOrderDocumentId)) {
            PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderDocumentId);
            // only reopen if the po is not null, it does not have a pending change already scheduled, and it is in closed status
            if (ObjectUtils.isNotNull(purchaseOrderDocument) &&
                (!purchaseOrderDocument.isPendingActionIndicator()) &&
                PurapConstants.PurchaseOrderStatuses.CLOSED.equals(purchaseOrderDocument.getStatusCode())) {
                // TODO (KULPURAP-1576: dlemus) call reopen purchasing order service method when avaliable
             }
        }
    }

    /**
     * Must be an AP user, cm not already on hold, extracted date is null, and cm status approved or complete
     * 
     * @see org.kuali.module.purap.service.CreditMemoService#canHoldPaymentRequest(org.kuali.module.purap.document.CreditMemoDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user) {
        boolean canHold = false;

        String accountsPayableGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        if ( (!cmDocument.isHoldIndicator()) && user.isMember(accountsPayableGroup) && ObjectUtils.isNull(cmDocument.getExtractedDate()) && 
             (!PurapConstants.CreditMemoStatuses.STATUSES_DISALLOWING_HOLD.contains(cmDocument.getStatusCode())) ) {
            canHold = true;
        }

        return canHold;
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#addHoldOnPaymentRequest(org.kuali.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void addHoldOnCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(cmDocument, note);
        documentService.addNoteToDocument(cmDocument, noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to true
        CreditMemoDocument cmDoc = getCreditMemoDocumentById(cmDocument.getPurapDocumentIdentifier());
        cmDoc.setHoldIndicator(true);
        cmDoc.setLastActionPerformedByUniversalUserId(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        saveDocumentWithoutValidation(cmDoc);

        // must also save it on the incoming document
        cmDocument.setHoldIndicator(true);
        cmDocument.setLastActionPerformedByUniversalUserId(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
    }

    /**
     * Must be person who put cm on hold or ap supervisor and cm must be on hold
     * 
     * @see org.kuali.module.purap.service.CreditMemoService#canRemoveHoldPaymentRequest(org.kuali.module.purap.document.CreditMemoDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canRemoveHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user) {
        boolean canRemoveHold = false;

        String accountsPayableSupervisorGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        if (cmDocument.isHoldIndicator() && (user.getPersonUniversalIdentifier().equals(cmDocument.getLastActionPerformedByUniversalUserId()) || user.isMember(accountsPayableSupervisorGroup))) {
            canRemoveHold = true;
        }

        return canRemoveHold;
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#removeHoldOnCreditMemo(org.kuali.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void removeHoldOnCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(cmDocument, note);
        documentService.addNoteToDocument(cmDocument, noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to false
        CreditMemoDocument cmDoc = getCreditMemoDocumentById(cmDocument.getPurapDocumentIdentifier());
        cmDoc.setHoldIndicator(false);
        cmDoc.setLastActionPerformedByUniversalUserId(null);
        saveDocumentWithoutValidation(cmDoc);

        // must also save it on the incoming document
        cmDocument.setHoldIndicator(false);
        cmDocument.setLastActionPerformedByUniversalUserId(null);
    }


    /**
     * Document can be canceled if not in canceled status already, extracted date is null, hold indicator is false, and user is
     * member of the ap workgroup.
     * 
     * @see org.kuali.module.purap.service.CreditMemoService#canCancelCreditMemo(org.kuali.module.purap.document.CreditMemoDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canCancelCreditMemo(CreditMemoDocument cmDocument, UniversalUser user) {
        boolean canCancel = false;

        String accountsPayableGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        if ( (!CreditMemoStatuses.CANCELLED_STATUSES.contains(cmDocument.getStatusCode())) && cmDocument.getExtractedDate() == null && !cmDocument.isHoldIndicator() && user.isMember(accountsPayableGroup)) {
            canCancel = true;
        }

        return canCancel;
    }

    public String updateStatusByNode(String currentNodeName, AccountsPayableDocument apDoc) {
        return updateStatusByNode(currentNodeName, (CreditMemoDocument)apDoc);
    }
    /**
     * This method updates the status of a cm document
     * @param cmDocument
     * @param currentNodeName
     * @param cmDoc
     */
    private String updateStatusByNode(String currentNodeName, CreditMemoDocument cmDoc) {
        // update the status on the document

        String cancelledStatusCode = "";
        if(StringUtils.isEmpty(currentNodeName)) {
            cancelledStatusCode = PurapConstants.CreditMemoStatuses.CANCELLED_POST_AP_APPROVE;
        } else {
            NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(currentNodeName);
            if(ObjectUtils.isNotNull(currentNode)) {
                cancelledStatusCode = currentNode.getDisapprovedStatusCode();
            }
        }

        if (StringUtils.isNotBlank(cancelledStatusCode)) {
            purapService.updateStatusAndStatusHistory(cmDoc, cancelledStatusCode);
            saveDocumentWithoutValidation(cmDoc);
            return cancelledStatusCode;
        } else {
            // TODO (KULPURAP-1579: ckirshenman/hjs) delyea - what to do in a cancel where no status to set exists?
            LOG.warn("No status found to set for document being disapproved in node '" + currentNodeName + "'");
        }
        return cancelledStatusCode;
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#cancelExtractedCreditMemo(org.kuali.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void cancelExtractedCreditMemo(CreditMemoDocument cmDocument, String note) {
        // TODO (KULPURAP-1444: ckirshenman) Auto-generated method stub

    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#resetExtractedCreditMemo(org.kuali.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void resetExtractedCreditMemo(CreditMemoDocument cmDocument, String note) {
        // TODO (KULPURAP-1444: ckirshenman) Auto-generated method stub
    }

    /**
     * Sets the creditMemoDao attribute value.
     * 
     * @param creditMemoDao The creditMemoDao to set.
     */
    public void setCreditMemoDao(CreditMemoDao creditMemoDao) {
        this.creditMemoDao = creditMemoDao;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the noteService attribute value.
     * 
     * @param noteService The noteService to set.
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Sets the purapService attribute value.
     * 
     * @param purapService The purapService to set.
     */
    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    /**
     * Sets the generalLedgerService attribute value.
     * 
     * @param generalLedgerService The generalLedgerService to set.
     */
    public void setPurapGeneralLedgerService(PurapGeneralLedgerService purapGeneralLedgerService) {
        this.purapGeneralLedgerService = purapGeneralLedgerService;
    }

    /**
     * Sets the paymentRequestService attribute value.
     * 
     * @param paymentRequestService The paymentRequestService to set.
     */
    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    /**
     * Sets the purchaseOrderService attribute value.
     * 
     * @param purchaseOrderService The purchaseOrderService to set.
     */
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public boolean shouldPurchaseOrderBeReversed(AccountsPayableDocument apDoc) {
        PurchaseOrderDocument po = apDoc.getPurchaseOrderDocument();
        if(ObjectUtils.isNull(po)) {
            return false;
        }
        //if past full entry and already closed return true
        if(purapService.isFullDocumentEntryCompleted(apDoc) &&
                StringUtils.equalsIgnoreCase(PurapConstants.PurchaseOrderStatuses.OPEN,po.getStatusCode())) {
            return true;
        }
        return false;
    }

    public UniversalUser getUniversalUserForCancel(AccountsPayableDocument apDoc) {
        // return null, since superuser is fine for CM
        return null;
    }

    public void takePurchaseOrderCancelAction(AccountsPayableDocument apDoc) {
        CreditMemoDocument cmDocument = (CreditMemoDocument)apDoc;
        if(cmDocument.isReopenPurchaseOrderIndicator()) {
            String docType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
            SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(cmDocument.getPurchaseOrderDocument().getDocumentNumber(), docType, "reopened by Payment Request "+apDoc.getPurapDocumentIdentifier()+ "cancel", new ArrayList());
        }
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#markPaid(org.kuali.module.purap.document.CreditMemoDocument, java.sql.Date)
     */
    public void markPaid(CreditMemoDocument cm,Date processDate) {
        LOG.debug("markPaid() started");

        cm.setCreditMemoPaidTimestamp(new Timestamp(processDate.getTime()));
        saveDocumentWithoutValidation(cm);
    }
}
