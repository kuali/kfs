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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.CREDIT_MEMO_TYPES;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.CreditMemoDao;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.GeneralLedgerService;
import org.kuali.module.purap.service.PaymentRequestService;
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
    private GeneralLedgerService generalLedgerService;
    private PaymentRequestService paymentRequestService;
    private PurchaseOrderService purchaseOrderService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#creditMemoDuplicateMessages(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public String creditMemoDuplicateMessages(CreditMemoDocument cmDocument) {
        String duplicateMessage = null;

        String vendorNumber = cmDocument.getVendorNumber();
        if (StringUtils.isEmpty(vendorNumber)) {
            if (StringUtils.equals(cmDocument.getCreditMemoType(), CREDIT_MEMO_TYPES.TYPE_PREQ)) {
                PaymentRequestDocument paymentRequestDocument = paymentRequestService.getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
                vendorNumber = paymentRequestDocument.getVendorNumber();
            }
            else {
                PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
                vendorNumber = purchaseOrderDocument.getVendorNumber();
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
            
            if (poItem.getItemInvoicedTotalQuantity() != null && poItem.getItemInvoicedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)) {
                invoicedItems.add(poItem);
            }
            else {
                BigDecimal unitPrice = (poItem.getItemUnitPrice() == null ? new BigDecimal(0) : poItem.getItemUnitPrice());
                if (PurapConstants.ItemTypeCodes.ITEM_TYPE_SERVICE_CODE.equals(poItem.getItemType()) && (unitPrice.doubleValue() > poItem.getItemOutstandingEncumbranceAmount().doubleValue())) {
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
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#getCreditMemoDocumentById(java.lang.Integer)
     */
    public CreditMemoDocument getCreditMemoDocumentById(Integer purchasingDocumentIdentifier) {
        Map<String, Integer> criteria = new HashMap<String, Integer>();
        criteria.put(PurapPropertyConstants.PURAP_DOC_ID, purchasingDocumentIdentifier);

        return (CreditMemoDocument) businessObjectService.findByPrimaryKey(CreditMemoDocument.class, criteria);
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#save(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void save(CreditMemoDocument cmDocument) {
        cmDocument.prepareForSave();
        creditMemoDao.save(cmDocument);
        cmDocument.refreshReferenceObject(PurapPropertyConstants.STATUS);
    }

    /**
     * Recalculates the credit memo, calls document service to run rules and route the document, updates status and approval date.
     * Also reopens PO if closed.
     * 
     * @see org.kuali.module.purap.service.CreditMemoService#approve(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void approve(CreditMemoDocument cmDocument, String annotation, List adHocRecipients) throws WorkflowException {
        // recalculate
        cmDocument.updateExtendedPriceOnItems();
        calculateCreditMemo(cmDocument);

        // TODO: call method to update accounting line amounts

        // run rules and route, throws exception if errors were found
        documentService.routeDocument(cmDocument, annotation, adHocRecipients);

        purapService.updateStatusAndStatusHistory(cmDocument, PurapConstants.CreditMemoStatuses.AP_APPROVED);
        cmDocument.setAccountsPayableApprovalDate(dateTimeService.getCurrentSqlDate());
        save(cmDocument);

        // reopen PO if closed
        PurchaseOrderDocument purchaseOrderDocument = null;
        if (StringUtils.equals(cmDocument.getCreditMemoType(), CREDIT_MEMO_TYPES.TYPE_PREQ)) {
            PaymentRequestDocument paymentRequestDocument = paymentRequestService.getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
            purchaseOrderDocument = paymentRequestDocument.getPurchaseOrderDocument();
        }

        if (StringUtils.equals(cmDocument.getCreditMemoType(), CREDIT_MEMO_TYPES.TYPE_PO)) {
            purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
        }

        if (purchaseOrderDocument != null && PurapConstants.PurchaseOrderStatuses.CLOSED.equals(purchaseOrderDocument.getStatusCode())) {

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

        String accountsPayableGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        if (cmDocument.isHoldIndicator() == false && user.isMember(accountsPayableGroup) && cmDocument.getExtractedDate() == null && (PurapConstants.CreditMemoStatuses.AP_APPROVED.equals(cmDocument.getStatusCode()) || PurapConstants.CreditMemoStatuses.COMPLETE.equals(cmDocument.getStatusCode()))) {
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
        cmDoc.setAccountsPayableHoldIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        creditMemoDao.save(cmDoc);

        // must also save it on the incoming document
        cmDocument.setHoldIndicator(true);
        cmDocument.setAccountsPayableHoldIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
    }

    /**
     * Must be person who put cm on hold or ap supervisor and cm must be on hold
     * 
     * @see org.kuali.module.purap.service.CreditMemoService#canRemoveHoldPaymentRequest(org.kuali.module.purap.document.CreditMemoDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canRemoveHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user) {
        boolean canRemoveHold = false;

        String accountsPayableSupervisorGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        if (cmDocument.isHoldIndicator() && (user.getPersonUniversalIdentifier().equals(cmDocument.getAccountsPayableHoldIdentifier()) || user.isMember(accountsPayableSupervisorGroup))) {
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
        cmDoc.setAccountsPayableHoldIdentifier(null);
        creditMemoDao.save(cmDoc);

        // must also save it on the incoming document
        cmDocument.setHoldIndicator(false);
        cmDocument.setAccountsPayableHoldIdentifier(null);
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

        String accountsPayableGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        if (!StringUtils.equals(cmDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.CANCELLED) && cmDocument.getExtractedDate() == null && !cmDocument.isHoldIndicator() && user.isMember(accountsPayableGroup)) {
            canCancel = true;
        }

        return canCancel;
    }

    /**
     * Sets credit memo status to canceled. If gl entries have been created (ap_approve or complete status), cancel entries are
     * created.
     * 
     * @see org.kuali.module.purap.service.CreditMemoService#cancelCreditMemo(org.kuali.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void cancelCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(cmDocument, note);
        documentService.addNoteToDocument(cmDocument, noteObj);
        noteService.save(noteObj);

        // retrieve and save with canceled status, clear gl entries
        CreditMemoDocument cmDoc = getCreditMemoDocumentById(cmDocument.getPurapDocumentIdentifier());
        if (PurapConstants.CreditMemoStatuses.AP_APPROVED.equals(cmDoc.getStatusCode()) || PurapConstants.CreditMemoStatuses.COMPLETE.equals(cmDoc.getStatusCode())) {
            generalLedgerService.generateEntriesCancelCm(cmDoc);
            generalLedgerService.generateEntriesCancelCm(cmDocument);
        }

        purapService.updateStatusAndStatusHistory(cmDoc, PurapConstants.CreditMemoStatuses.CANCELLED, noteObj);
        save(cmDoc);

        // must also save it on the incoming document
        cmDocument.setStatusCode(PurapConstants.CreditMemoStatuses.CANCELLED);
        cmDocument.refreshReferenceObject(PurapPropertyConstants.STATUS);
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#cancelExtractedCreditMemo(org.kuali.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void cancelExtractedCreditMemo(CreditMemoDocument cmDocument, String note) {
        // TODO Auto-generated method stub

    }


    /**
     * @see org.kuali.module.purap.service.CreditMemoService#resetExtractedCreditMemo(org.kuali.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    public void resetExtractedCreditMemo(CreditMemoDocument cmDocument, String note) {
        // TODO Auto-generated method stub

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
    public void setGeneralLedgerService(GeneralLedgerService generalLedgerService) {
        this.generalLedgerService = generalLedgerService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
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
}
