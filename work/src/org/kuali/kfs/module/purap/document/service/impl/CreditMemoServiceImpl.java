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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.CreditMemoDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccount;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.dataaccess.CreditMemoDao;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.ContinuePurapEvent;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.vnd.VendorUtils;
import org.springframework.transaction.annotation.Transactional;

import org.kuali.rice.kew.exception.WorkflowException;

/**
 * Provides services to support the creation of a Credit Memo Document.
 */
@Transactional
public class CreditMemoServiceImpl implements CreditMemoService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoServiceImpl.class);

    private CreditMemoDao creditMemoDao;
    private KualiConfigurationService kualiConfigurationService;
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private NoteService noteService;
    private PurapService purapService;
    private PurapGeneralLedgerService purapGeneralLedgerService;
    private PaymentRequestService paymentRequestService;
    private PurchaseOrderService purchaseOrderService;
    private PurapAccountingService purapAccountingService;
    private AccountsPayableService accountsPayableService;
    private WorkflowDocumentService workflowDocumentService;
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#getCreditMemosToExtract(java.lang.String)
     */
    public Iterator<CreditMemoDocument> getCreditMemosToExtract(String chartCode) {
        LOG.debug("getCreditMemosToExtract() started");

        return creditMemoDao.getCreditMemosToExtract(chartCode);
    }

    public Iterator<CreditMemoDocument> getCreditMemosToExtractByVendor(String chartCode, VendorGroupingHelper vendor ) {
        LOG.debug("getCreditMemosToExtractByVendor() started");

        return creditMemoDao.getCreditMemosToExtractByVendor(chartCode,vendor);
    }

    public Set<VendorGroupingHelper> getVendorsOnCreditMemosToExtract(String chartCode) {
        LOG.debug("getVendorsOnCreditMemosToExtract() started");
        HashSet<VendorGroupingHelper> vendors = new HashSet<VendorGroupingHelper>();
        
        Iterator<CreditMemoDocument> docs = getCreditMemosToExtract(chartCode);
        while ( docs.hasNext() ) {
            CreditMemoDocument doc = docs.next();
            vendors.add( new VendorGroupingHelper( doc ) );
        }
        return vendors;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#creditMemoDuplicateMessages(org.kuali.kfs.module.purap.document.CreditMemoDocument)
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
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#getPOInvoicedItems(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
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
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#calculateCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    public void calculateCreditMemo(CreditMemoDocument cmDocument) {

        cmDocument.updateExtendedPriceOnItems();

        for (CreditMemoItem item : (List<CreditMemoItem>) cmDocument.getItems()) {
            // update unit price for service items
            if (item.getItemType().isItemTypeAboveTheLineIndicator() && !item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                if(item.getExtendedPrice()!=null) {
                    item.setItemUnitPrice(new BigDecimal(item.getExtendedPrice().toString()));
                }
            }
            // make sure restocking fee is negative
            else if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, item.getItemTypeCode())) {
                if (item.getItemUnitPrice() != null) {
                    item.setExtendedPrice(item.getExtendedPrice().abs().negated());
                    item.setItemUnitPrice(item.getItemUnitPrice().abs().negate());
                }
            }
        }

        // proration
        if (cmDocument.isSourceVendor()) {
            // no proration on vendor
            return;
        }

        for (CreditMemoItem item : (List<CreditMemoItem>) cmDocument.getItems()) {

            // skip above the line
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                continue;
            }

            if ((item.getSourceAccountingLines().isEmpty()) && (ObjectUtils.isNotNull(item.getExtendedPrice())) && (KualiDecimal.ZERO.compareTo(item.getExtendedPrice()) != 0)) {

                KualiDecimal totalAmount = KualiDecimal.ZERO;
                List<PurApAccountingLine> distributedAccounts = null;
                List<SourceAccountingLine> summaryAccounts = null;

                totalAmount = cmDocument.getPurApSourceDocumentIfPossible().getTotalDollarAmount();
                // this should do nothing on preq which is fine
                purapAccountingService.updateAccountAmounts(cmDocument.getPurApSourceDocumentIfPossible());
                summaryAccounts = purapAccountingService.generateSummary(cmDocument.getPurApSourceDocumentIfPossible().getItems());
                distributedAccounts = purapAccountingService.generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, CreditMemoAccount.class);

                if (CollectionUtils.isNotEmpty(distributedAccounts) && CollectionUtils.isEmpty(item.getSourceAccountingLines())) {
                    item.setSourceAccountingLines(distributedAccounts);
                }
            }
        }
        // end proration
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#getCreditMemoByDocumentNumber(java.lang.String)
     */
    public CreditMemoDocument getCreditMemoByDocumentNumber(String documentNumber) {
        LOG.debug("getCreditMemoByDocumentNumber() started");

        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                CreditMemoDocument doc = (CreditMemoDocument) documentService.getByDocumentHeaderId(documentNumber);
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting credit memo document from document service";
                LOG.error("getCreditMemoByDocumentNumber() " + errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#getCreditMemoDocumentById(java.lang.Integer)
     */
    public CreditMemoDocument getCreditMemoDocumentById(Integer purchasingDocumentIdentifier) {
        return getCreditMemoByDocumentNumber(creditMemoDao.getDocumentNumberByCreditMemoId(purchasingDocumentIdentifier));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#saveDocumentWithoutValidation(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    public void saveDocumentWithoutValidation(AccountsPayableDocument document) {
        try {
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);

        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
        catch (RuntimeException re) {
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + re.getMessage();
            LOG.error(errorMsg, re);
            throw new RuntimeException(errorMsg, re);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#saveDocument(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    public void populateAndSaveCreditMemo(CreditMemoDocument document) {
        try {
            document.setStatusCode(PurapConstants.CreditMemoStatuses.IN_PROCESS);
            
            if (document.isSourceDocumentPaymentRequest()) {
                document.setBankCode(document.getPaymentRequestDocument().getBankCode());
                document.setBank(document.getPaymentRequestDocument().getBank());
            }
            else {
                // set bank code to default bank code in the system parameter
                Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(CreditMemoDocument.class);
                if (defaultBank != null) {
                    document.setBankCode(defaultBank.getBankCode());
                    document.setBank(defaultBank);
                }
            }
            
            documentService.saveDocument(document, ContinuePurapEvent.class);
        }
        catch (ValidationException ve) {
            document.setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);
        }
        catch (WorkflowException we) {
            // set the status back to initiate
            document.setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#reopenClosedPO(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    public void reopenClosedPO(CreditMemoDocument cmDocument) {
        // reopen PO if closed
        Integer purchaseOrderDocumentId = cmDocument.getPurchaseOrderIdentifier();
        if (cmDocument.isSourceDocumentPaymentRequest() && ObjectUtils.isNull(purchaseOrderDocumentId)) {
            PaymentRequestDocument paymentRequestDocument = paymentRequestService.getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
            purchaseOrderDocumentId = paymentRequestDocument.getPurchaseOrderIdentifier();
        }
        // if we found a valid po id number then check it for reopening
        if (ObjectUtils.isNotNull(purchaseOrderDocumentId)) {
            PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderDocumentId);
            // only reopen if the po is not null, it does not have a pending change already scheduled, and it is in closed status
            if (ObjectUtils.isNotNull(purchaseOrderDocument) && (!purchaseOrderDocument.isPendingActionIndicator()) && PurapConstants.PurchaseOrderStatuses.CLOSED.equals(purchaseOrderDocument.getStatusCode())) {

            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#canHoldPaymentRequest(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public boolean canHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user) {
        boolean canHold = false;

        String accountsPayableGroup = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        if ((!cmDocument.isHoldIndicator()) && user.isMember(accountsPayableGroup) && ObjectUtils.isNull(cmDocument.getExtractedDate()) && (!PurapConstants.CreditMemoStatuses.STATUSES_DISALLOWING_HOLD.contains(cmDocument.getStatusCode()))) {
            canHold = true;
        }

        return canHold;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#addHoldOnPaymentRequest(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public CreditMemoDocument addHoldOnCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(cmDocument, note);
        documentService.addNoteToDocument(cmDocument, noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to true
        CreditMemoDocument cmDoc = getCreditMemoDocumentById(cmDocument.getPurapDocumentIdentifier());
        cmDoc.setHoldIndicator(true);
        cmDoc.setLastActionPerformedByUniversalUserId(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier());
        saveDocumentWithoutValidation(cmDoc);

        // must also save it on the incoming document
        cmDocument.setHoldIndicator(true);
        cmDocument.setLastActionPerformedByUniversalUserId(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier());
        
        return cmDoc;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#canRemoveHoldPaymentRequest(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public boolean canRemoveHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user) {
        boolean canRemoveHold = false;

        String accountsPayableSupervisorGroup = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        if (cmDocument.isHoldIndicator() && (user.getPersonUniversalIdentifier().equals(cmDocument.getLastActionPerformedByUniversalUserId()) || user.isMember(accountsPayableSupervisorGroup))) {
            canRemoveHold = true;
        }

        return canRemoveHold;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#removeHoldOnCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public CreditMemoDocument removeHoldOnCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception {
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
        
        return cmDoc;
    }


    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#canCancelCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public boolean canCancelCreditMemo(CreditMemoDocument cmDocument, UniversalUser user) {
        boolean canCancel = false;

        String accountsPayableGroup = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        if ((!CreditMemoStatuses.CANCELLED_STATUSES.contains(cmDocument.getStatusCode())) && cmDocument.getExtractedDate() == null && !cmDocument.isHoldIndicator() && user.isMember(accountsPayableGroup)) {
            canCancel = true;
        }

        return canCancel;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#updateStatusByNode(java.lang.String, org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public String updateStatusByNode(String currentNodeName, AccountsPayableDocument apDoc) {
        return updateStatusByNode(currentNodeName, (CreditMemoDocument) apDoc);
    }

    /**
     * Updates the status of a credit memo document, currently this is used by the cancel action
     * 
     * @param currentNodeName  The string representing the current node to be used to obtain the canceled status code.
     * @param cmDoc            The credit memo document to be updated.
     * @return                 The string representing the canceledStatusCode, if empty it is assumed to be not from workflow. 
     */
    private String updateStatusByNode(String currentNodeName, CreditMemoDocument cmDoc) {
        // update the status on the document

        String cancelledStatusCode = "";
        if (StringUtils.isEmpty(currentNodeName)) {
            cancelledStatusCode = PurapConstants.CreditMemoStatuses.CANCELLED_POST_AP_APPROVE;
        }
        else {
            NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(currentNodeName);
            if (ObjectUtils.isNotNull(currentNode)) {
                cancelledStatusCode = currentNode.getDisapprovedStatusCode();
            }
        }

        if (StringUtils.isNotBlank(cancelledStatusCode)) {
            purapService.updateStatus(cmDoc, cancelledStatusCode);
            saveDocumentWithoutValidation(cmDoc);
            return cancelledStatusCode;
        }
        else {
            logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + currentNodeName + "'");
        }
        return cancelledStatusCode;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#cancelExtractedCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void cancelExtractedCreditMemo(CreditMemoDocument cmDocument, String note) {
        LOG.debug("cancelExtractedCreditMemo() started");
        if (CreditMemoStatuses.CANCELLED_STATUSES.contains(cmDocument.getStatusCode())) {
            LOG.debug("cancelExtractedCreditMemo() ended");
            return;
        }

        try {
            Note noteObj = documentService.createNoteFromDocument(cmDocument, note);
            documentService.addNoteToDocument(cmDocument, noteObj);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        accountsPayableService.cancelAccountsPayableDocument(cmDocument, "");
        LOG.debug("cancelExtractedCreditMemo() CM " + cmDocument.getPurapDocumentIdentifier() + " Cancelled Without Workflow");
        LOG.debug("cancelExtractedCreditMemo() ended");

    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#resetExtractedCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void resetExtractedCreditMemo(CreditMemoDocument cmDocument, String note) {
        LOG.debug("resetExtractedCreditMemo() started");
        if (CreditMemoStatuses.CANCELLED_STATUSES.contains(cmDocument.getStatusCode())) {
            LOG.debug("resetExtractedCreditMemo() ended");
            return;
        }
        cmDocument.setExtractedDate(null);
        cmDocument.setCreditMemoPaidTimestamp(null);

        Note noteObj;
        try {
            noteObj = documentService.createNoteFromDocument(cmDocument, note);
            documentService.addNoteToDocument(cmDocument, noteObj);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        saveDocumentWithoutValidation(cmDocument);

        LOG.debug("resetExtractedCreditMemo() CM " + cmDocument.getPurapDocumentIdentifier() + " Cancelled Without Workflow");
        LOG.debug("resetExtractedCreditMemo() ended");
    }


    public void setCreditMemoDao(CreditMemoDao creditMemoDao) {
        this.creditMemoDao = creditMemoDao;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setPurapGeneralLedgerService(PurapGeneralLedgerService purapGeneralLedgerService) {
        this.purapGeneralLedgerService = purapGeneralLedgerService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setAccountsPayableService(AccountsPayableService accountsPayableService) {
        this.accountsPayableService = accountsPayableService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService){
        this.workflowDocumentService = workflowDocumentService;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#shouldPurchaseOrderBeReversed(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public boolean shouldPurchaseOrderBeReversed(AccountsPayableDocument apDoc) {
        // always return false, never reverse
        return false;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#getUniversalUserForCancel(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public UniversalUser getUniversalUserForCancel(AccountsPayableDocument apDoc) {
        // return null, since superuser is fine for CM
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#takePurchaseOrderCancelAction(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public void takePurchaseOrderCancelAction(AccountsPayableDocument apDoc) {
        CreditMemoDocument cmDocument = (CreditMemoDocument) apDoc;
        if (cmDocument.isReopenPurchaseOrderIndicator()) {
            String docType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
            purchaseOrderService.createAndRoutePotentialChangeDocument(cmDocument.getPurchaseOrderDocument().getDocumentNumber(), docType, "reopened by Payment Request " + apDoc.getPurapDocumentIdentifier() + "cancel", new ArrayList(), PurapConstants.PurchaseOrderStatuses.PENDING_CLOSE);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#markPaid(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.sql.Date)
     */
    public void markPaid(CreditMemoDocument cm, Date processDate) {
        LOG.debug("markPaid() started");

        cm.setCreditMemoPaidTimestamp(new Timestamp(processDate.getTime()));
        saveDocumentWithoutValidation(cm);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#poItemEligibleForAp(org.kuali.kfs.module.purap.document.AccountsPayableDocument, org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem)
     */
    public boolean poItemEligibleForAp(AccountsPayableDocument apDoc, PurchaseOrderItem poItem) {
        // if the po item is not active... skip it
        if (!poItem.isItemActiveIndicator()) {
            return false;
        }

        if (poItem.getItemType().isQuantityBasedGeneralLedgerIndicator() && poItem.getItemInvoicedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)) {
            return true;
        }
        else {
            BigDecimal unitPrice = (poItem.getItemUnitPrice() == null ? new BigDecimal(0) : poItem.getItemUnitPrice());
            if (unitPrice.doubleValue() > poItem.getItemOutstandingEncumberedAmount().doubleValue()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * The given document here needs to be a Credit Memo.
     * 
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#generateGLEntriesCreateAccountsPayableDocument(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public void generateGLEntriesCreateAccountsPayableDocument(AccountsPayableDocument apDocument) {
        CreditMemoDocument creditMemo = (CreditMemoDocument)apDocument;
        purapGeneralLedgerService.generateEntriesCreateCreditMemo(creditMemo);
    }

    /**
     * Records the specified error message into the Log file and throws a runtime exception.
     * 
     * @param errorMessage the error message to be logged.
     */
    protected void logAndThrowRuntimeException(String errorMessage) {
        this.logAndThrowRuntimeException(errorMessage, null);
    }

    /**
     * Records the specified error message into the Log file and throws the specified runtime exception.
     * 
     * @param errorMessage the specified error message.
     * @param e the specified runtime exception.
     */
    protected void logAndThrowRuntimeException(String errorMessage, Exception e) {
        if (ObjectUtils.isNotNull(e)) {
            LOG.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
        else {
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#hasActiveCreditMemosForPurchaseOrder(java.lang.Integer)
     */
    public boolean hasActiveCreditMemosForPurchaseOrder(Integer purchaseOrderIdentifier){
        
        boolean hasActiveCreditMemos = false;
        List<String> docNumbers= null;
        KualiWorkflowDocument workflowDocument = null;
        
        docNumbers= creditMemoDao.getActiveCreditMemoDocumentNumbersForPurchaseOrder(purchaseOrderIdentifier);
        
        for (String docNumber : docNumbers) {
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getFinancialSystemUser());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            //if the document is not in a non-active status then return true and stop evaluation
            if(!(workflowDocument.stateIsCanceled() ||
                    workflowDocument.stateIsException() ||
                    workflowDocument.stateIsFinal()) ){
                hasActiveCreditMemos = true;
                break;
            }

        }
        
        return hasActiveCreditMemos;
    }
}
