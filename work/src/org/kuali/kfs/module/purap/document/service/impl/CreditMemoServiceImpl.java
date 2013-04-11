/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccount;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.dataaccess.CreditMemoDao;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides services to support the creation of a Credit Memo Document.
 */
@Transactional
public class CreditMemoServiceImpl implements CreditMemoService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoServiceImpl.class);

    private AccountsPayableService accountsPayableService;
    private CreditMemoDao creditMemoDao;
    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    private ConfigurationService kualiConfigurationService;
    private NoteService noteService;
    private PaymentRequestService paymentRequestService;
    private PurapAccountingService purapAccountingService;
    private PurapGeneralLedgerService purapGeneralLedgerService;
    private PurapService purapService;
    private PurchaseOrderService purchaseOrderService;
    private VendorService vendorService;
    private WorkflowDocumentService workflowDocumentService;


    public void setAccountsPayableService(AccountsPayableService accountsPayableService) {
        this.accountsPayableService = accountsPayableService;
    }

    public void setCreditMemoDao(CreditMemoDao creditMemoDao) {
        this.creditMemoDao = creditMemoDao;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setPurapGeneralLedgerService(PurapGeneralLedgerService purapGeneralLedgerService) {
        this.purapGeneralLedgerService = purapGeneralLedgerService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService){
        this.workflowDocumentService = workflowDocumentService;
    }


    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#getCreditMemosToExtract(java.lang.String)
     */
    @Override
    public List<VendorCreditMemoDocument> getCreditMemosToExtract(String chartCode) {
        LOG.debug("getCreditMemosToExtract() started");

        List<VendorCreditMemoDocument> docs = creditMemoDao.getCreditMemosToExtract(chartCode);
        docs = (List<VendorCreditMemoDocument>) filterCreditMemoByAppDocStatus(docs, CreditMemoStatuses.STATUSES_ALLOWED_FOR_EXTRACTION);

        return docs;

    }

    @Override
    public Collection<VendorCreditMemoDocument> getCreditMemosToExtractByVendor(String chartCode, VendorGroupingHelper vendor ) {
        LOG.debug("getCreditMemosToExtractByVendor() started");

        Collection<VendorCreditMemoDocument> docs = creditMemoDao.getCreditMemosToExtractByVendor(chartCode,vendor);
        docs = filterCreditMemoByAppDocStatus(docs, CreditMemoStatuses.STATUSES_ALLOWED_FOR_EXTRACTION);
        return docs;

    }

    @Override
    public Set<VendorGroupingHelper> getVendorsOnCreditMemosToExtract(String chartCode) {
        LOG.debug("getVendorsOnCreditMemosToExtract() started");
        HashSet<VendorGroupingHelper> vendors = new HashSet<VendorGroupingHelper>();

        List<VendorCreditMemoDocument> docs = this.getCreditMemosToExtract(chartCode);

        for (VendorCreditMemoDocument doc: docs) {
            vendors.add( new VendorGroupingHelper( doc ) );
        }

        return vendors;
    }

    /**
     *  Since PaymentRequest does not have the app doc status, perform an additional lookup
     *  through doc search by using list of PaymentRequest Doc numbers.  Query appDocStatus
     *  from workflow document and filter against the provided status
     *
     *  DocumentSearch allows for multiple docNumber lookup by docId|docId|docId conversion
     *
     * @param lookupDocNumbers
     * @param appDocStatus
     * @return
     */
    private List<String> filterCreditMemoByAppDocStatus(List<String> lookupDocNumbers, String... appDocStatus) {
        boolean valid = false;

        final String DOC_NUM_DELIM = "|";
        StrBuilder routerHeaderIdBuilder = new StrBuilder().appendWithSeparators(lookupDocNumbers, DOC_NUM_DELIM);

        List<String> creditMemoDocNumbers = new ArrayList<String>();

        DocumentSearchCriteria.Builder documentSearchCriteriaDTO = DocumentSearchCriteria.Builder.create();
        documentSearchCriteriaDTO.setDocumentId(routerHeaderIdBuilder.toString());
        documentSearchCriteriaDTO.setDocumentTypeName(PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT);
        documentSearchCriteriaDTO.setApplicationDocumentStatuses(Arrays.asList(appDocStatus));

        DocumentSearchCriteria crit = documentSearchCriteriaDTO.build();

        int maxResults = SpringContext.getBean(FinancialSystemDocumentService.class).getMaxResultCap(crit);
        int iterations = SpringContext.getBean(FinancialSystemDocumentService.class).getFetchMoreIterationLimit();

        for (int i = 0; i < iterations; i++) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fetch Iteration: "+ i);
            }
            documentSearchCriteriaDTO.setStartAtIndex(maxResults * i);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Max Results: "+documentSearchCriteriaDTO.getStartAtIndex());
            }
            DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(
                    GlobalVariables.getUserSession().getPrincipalId(), crit);
            if (results.getSearchResults().isEmpty()) {
                break;
            }
            for (DocumentSearchResult resultRow: results.getSearchResults()) {
                creditMemoDocNumbers.add(resultRow.getDocument().getDocumentId());
            }
        }

        return creditMemoDocNumbers;
    }

    /**
     * Wrapper class to the filterPaymentRequestByAppDocStatus
     *
     * This class first extract the payment request document numbers from the Payment Request Collections,
     * then perform the filterPaymentRequestByAppDocStatus function.  Base on the filtered payment request
     * doc number, reconstruct the filtered Payment Request Collection
     *
     * @param paymentRequestDocuments
     * @param appDocStatus
     * @return
     */
    private Collection<VendorCreditMemoDocument> filterCreditMemoByAppDocStatus(Collection<VendorCreditMemoDocument> creditMemoDocuments, String... appDocStatus) {
        List<String> creditMemoDocNumbers = new ArrayList<String>();
        for (VendorCreditMemoDocument creditMemo : creditMemoDocuments){
            creditMemoDocNumbers.add(creditMemo.getDocumentNumber());
        }

        List<String> filteredCreditMemoDocNumbers = filterCreditMemoByAppDocStatus(creditMemoDocNumbers, appDocStatus);

        Collection<VendorCreditMemoDocument> filteredCreditMemoDocuments = new ArrayList<VendorCreditMemoDocument>();
        //add to filtered collection if it is in the filtered payment request doc number list
        for (VendorCreditMemoDocument creditMemo : creditMemoDocuments){
            if (filteredCreditMemoDocNumbers.contains(creditMemo.getDocumentNumber())){
                filteredCreditMemoDocuments.add(creditMemo);
            }
        }
        return filteredCreditMemoDocuments;
    }


    /**
     * Wrapper class to the filterPaymentRequestByAppDocStatus (Collection<PaymentRequestDocument>)
     *
     * This class first construct the Payment Request Collection from the iterator, and then process through
     * filterPaymentRequestByAppDocStatus
     *
     * @param paymentRequestDocuments
     * @param appDocStatus
     * @return
     */
    private Iterator<VendorCreditMemoDocument> filterCreditMemoByAppDocStatus(Iterator<VendorCreditMemoDocument> creditMemoIterator, String... appDocStatus) {
        Collection<VendorCreditMemoDocument> creditMemoDocuments = new ArrayList<VendorCreditMemoDocument>();
        for (;creditMemoIterator.hasNext();){
            creditMemoDocuments.add(creditMemoIterator.next());
        }

        return filterCreditMemoByAppDocStatus(creditMemoDocuments, appDocStatus).iterator();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#creditMemoDuplicateMessages(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    @Override
    public String creditMemoDuplicateMessages(VendorCreditMemoDocument cmDocument) {
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
                duplicateMessage = kualiConfigurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER);
            }

            // check for existence of another credit memo with the same vendor and credit memo date
            if (creditMemoDao.duplicateExists(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber), cmDocument.getCreditMemoDate(), cmDocument.getCreditMemoAmount())) {
                duplicateMessage = kualiConfigurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER_DATE_AMOUNT);
            }
        }

        return duplicateMessage;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#getPOInvoicedItems(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public List<PurchaseOrderItem> getPOInvoicedItems(PurchaseOrderDocument poDocument) {
        List<PurchaseOrderItem> invoicedItems = new ArrayList<PurchaseOrderItem>();

        for (Iterator iter = poDocument.getItems().iterator(); iter.hasNext();) {
            PurchaseOrderItem poItem = (PurchaseOrderItem) iter.next();

            poItem.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

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
    @Override
    public void calculateCreditMemo(VendorCreditMemoDocument cmDocument) {

        cmDocument.updateExtendedPriceOnItems();

        for (CreditMemoItem item : (List<CreditMemoItem>) cmDocument.getItems()) {
            // make sure restocking fee is negative
            if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, item.getItemTypeCode())) {
                if (item.getItemUnitPrice() != null) {
                    item.setExtendedPrice(item.getExtendedPrice().abs().negated());
                    item.setItemUnitPrice(item.getItemUnitPrice().abs().negate());
                }
            }
        }

        //calculate tax if cm not based on vendor
        if (cmDocument.isSourceVendor() == false) {
            purapService.calculateTax(cmDocument);
        }

        // proration
        if (cmDocument.isSourceVendor()) {
            // no proration on vendor
            return;
        }

        for (CreditMemoItem item : (List<CreditMemoItem>) cmDocument.getItems()) {

            // skip above the line
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (item.getItemType().isLineItemIndicator()) {
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
    @Override
    public VendorCreditMemoDocument getCreditMemoByDocumentNumber(String documentNumber) {
        LOG.debug("getCreditMemoByDocumentNumber() started");

        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                VendorCreditMemoDocument doc = (VendorCreditMemoDocument) documentService.getByDocumentHeaderId(documentNumber);
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
    @Override
    public VendorCreditMemoDocument getCreditMemoDocumentById(Integer purchasingDocumentIdentifier) {
        return getCreditMemoByDocumentNumber(creditMemoDao.getDocumentNumberByCreditMemoId(purchasingDocumentIdentifier));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#saveDocument(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    @Override
    public void populateAndSaveCreditMemo(VendorCreditMemoDocument document) {
        try {
         //   document.setApplicationDocumentStatus(PurapConstants.CreditMemoStatuses.APPDOC_IN_PROCESS);
         //   document.getDocumentHeader().getWorkflowDocument().setApplicationDocumentStatus(PurapConstants.CreditMemoStatuses.APPDOC_IN_PROCESS);
            document.updateAndSaveAppDocStatus(PurapConstants.CreditMemoStatuses.APPDOC_IN_PROCESS);

            if (document.isSourceDocumentPaymentRequest()) {
                document.setBankCode(document.getPaymentRequestDocument().getBankCode());
                document.setBank(document.getPaymentRequestDocument().getBank());
            }
            else {
                // set bank code to default bank code in the system parameter
                Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(document.getClass());
                if (defaultBank != null) {
                    document.setBankCode(defaultBank.getBankCode());
                    document.setBank(defaultBank);
                }
            }

            documentService.saveDocument(document, AttributedContinuePurapEvent.class);
        }
        catch (ValidationException ve) {
            // set the status back to initiate
            try {
                document.updateAndSaveAppDocStatus(PurapConstants.CreditMemoStatuses.APPDOC_INITIATE);
            }
            catch (WorkflowException workflowException) {

            }
        }
        catch (WorkflowException we) {
            // set the status back to initiate
            try {
                document.updateAndSaveAppDocStatus(PurapConstants.CreditMemoStatuses.APPDOC_INITIATE);
            }
            catch (WorkflowException workflowException) {

            }
            String errorMsg = "Error saving document # " + document.getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#reopenClosedPO(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    @Override
    public void reopenClosedPO(VendorCreditMemoDocument cmDocument) {
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
            if (ObjectUtils.isNotNull(purchaseOrderDocument) && (!purchaseOrderDocument.isPendingActionIndicator()) && PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED.equals(purchaseOrderDocument.getApplicationDocumentStatus())) {

            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#addHoldOnPaymentRequest(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    @Override
    public VendorCreditMemoDocument addHoldOnCreditMemo(VendorCreditMemoDocument cmDocument, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(cmDocument, note);
        cmDocument.addNote(noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to true
        VendorCreditMemoDocument cmDoc = getCreditMemoDocumentById(cmDocument.getPurapDocumentIdentifier());
        cmDoc.setHoldIndicator(true);
        cmDoc.setLastActionPerformedByPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        purapService.saveDocumentNoValidation(cmDoc);

        // must also save it on the incoming document
        cmDocument.setHoldIndicator(true);
        cmDocument.setLastActionPerformedByPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId());

        return cmDoc;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#removeHoldOnCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    @Override
    public VendorCreditMemoDocument removeHoldOnCreditMemo(VendorCreditMemoDocument cmDocument, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(cmDocument, note);
        cmDocument.addNote(noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to false
        VendorCreditMemoDocument cmDoc = getCreditMemoDocumentById(cmDocument.getPurapDocumentIdentifier());
        cmDoc.setHoldIndicator(false);
        cmDoc.setLastActionPerformedByPersonId(null);
        purapService.saveDocumentNoValidation(cmDoc);

        // must also save it on the incoming document
        cmDocument.setHoldIndicator(false);
        cmDocument.setLastActionPerformedByPersonId(null);

        return cmDoc;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#updateStatusByNode(java.lang.String, org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public String updateStatusByNode(String currentNodeName, AccountsPayableDocument apDoc) {
        return updateStatusByNode(currentNodeName, (VendorCreditMemoDocument) apDoc);
    }

    /**
     * Updates the status of a credit memo document, currently this is used by the cancel action
     *
     * @param currentNodeName  The string representing the current node to be used to obtain the canceled status code.
     * @param cmDoc            The credit memo document to be updated.
     * @return                 The string representing the canceledStatusCode, if empty it is assumed to be not from workflow.
     */
    protected String updateStatusByNode(String currentNodeName, VendorCreditMemoDocument cmDoc) {
        // update the status on the document

        String cancelledStatusCode = "";
        if (StringUtils.isEmpty(currentNodeName)) {
            cancelledStatusCode = PurapConstants.CreditMemoStatuses.APPDOC_CANCELLED_POST_AP_APPROVE;
        }
        else {
            cancelledStatusCode = CreditMemoStatuses.getCreditMemoAppDocDisapproveStatuses().get(currentNodeName);
        }

        if (StringUtils.isNotBlank(cancelledStatusCode)) {
            try {
                cmDoc.updateAndSaveAppDocStatus(cancelledStatusCode);
            }
            catch (WorkflowException we) {
                throw new RuntimeException("Unable to save the workflow document with document id: " + cmDoc.getDocumentNumber());
            }
            purapService.saveDocumentNoValidation(cmDoc);
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
    @Override
    public void cancelExtractedCreditMemo(VendorCreditMemoDocument cmDocument, String note) {
        LOG.debug("cancelExtractedCreditMemo() started");
        if (CreditMemoStatuses.CANCELLED_STATUSES.contains(cmDocument.getApplicationDocumentStatus())) {
            LOG.debug("cancelExtractedCreditMemo() ended");
            return;
        }

        try {
            Note noteObj = documentService.createNoteFromDocument(cmDocument, note);
            cmDocument.addNote(noteObj);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        accountsPayableService.cancelAccountsPayableDocument(cmDocument, "");
        if (LOG.isDebugEnabled()) {
            LOG.debug("cancelExtractedCreditMemo() CM " + cmDocument.getPurapDocumentIdentifier() + " Cancelled Without Workflow");
        }
        LOG.debug("cancelExtractedCreditMemo() ended");

    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#resetExtractedCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.lang.String)
     */
    @Override
    public void resetExtractedCreditMemo(VendorCreditMemoDocument cmDocument, String note) {
        LOG.debug("resetExtractedCreditMemo() started");
        if (CreditMemoStatuses.CANCELLED_STATUSES.contains(cmDocument.getApplicationDocumentStatus())) {
            LOG.debug("resetExtractedCreditMemo() ended");
            return;
        }
        cmDocument.setExtractedTimestamp(null);
        cmDocument.setCreditMemoPaidTimestamp(null);

        Note noteObj;
        try {
            noteObj = documentService.createNoteFromDocument(cmDocument, note);
            cmDocument.addNote(noteObj);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        purapService.saveDocumentNoValidation(cmDocument);

        if (LOG.isDebugEnabled()) {
            LOG.debug("resetExtractedCreditMemo() CM " + cmDocument.getPurapDocumentIdentifier() + " Cancelled Without Workflow");
        }
        LOG.debug("resetExtractedCreditMemo() ended");
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#shouldPurchaseOrderBeReversed(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public boolean shouldPurchaseOrderBeReversed(AccountsPayableDocument apDoc) {
        // always return false, never reverse
        return false;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#getPersonForCancel(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public Person getPersonForCancel(AccountsPayableDocument apDoc) {
        // return null, since superuser is fine for CM
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#takePurchaseOrderCancelAction(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public void takePurchaseOrderCancelAction(AccountsPayableDocument apDoc) {
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) apDoc;
        if (cmDocument.isReopenPurchaseOrderIndicator()) {
            String docType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
            purchaseOrderService.createAndRoutePotentialChangeDocument(cmDocument.getPurchaseOrderDocument().getDocumentNumber(), docType, "reopened by Payment Request " + apDoc.getPurapDocumentIdentifier() + "cancel", new ArrayList(), PurapConstants.PurchaseOrderStatuses.APPDOC_PENDING_CLOSE);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#markPaid(org.kuali.kfs.module.purap.document.CreditMemoDocument,
     *      java.sql.Date)
     */
    @Override
    public void markPaid(VendorCreditMemoDocument cm, Date processDate) {
        LOG.debug("markPaid() started");

        cm.setCreditMemoPaidTimestamp(new Timestamp(processDate.getTime()));
        purapService.saveDocumentNoValidation(cm);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#poItemEligibleForAp(org.kuali.kfs.module.purap.document.AccountsPayableDocument, org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem)
     */
    @Override
    public boolean poItemEligibleForAp(AccountsPayableDocument apDoc, PurchaseOrderItem poItem) {
        poItem.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

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
    @Override
    public void generateGLEntriesCreateAccountsPayableDocument(AccountsPayableDocument apDocument) {
        VendorCreditMemoDocument creditMemo = (VendorCreditMemoDocument)apDocument;
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
    @Override
    public boolean hasActiveCreditMemosForPurchaseOrder(Integer purchaseOrderIdentifier){

        boolean hasActiveCreditMemos = false;
        List<String> docNumbers= null;
        WorkflowDocument workflowDocument = null;

        docNumbers= creditMemoDao.getActiveCreditMemoDocumentNumbersForPurchaseOrder(purchaseOrderIdentifier);
        docNumbers = filterCreditMemoByAppDocStatus(docNumbers, CreditMemoStatuses.STATUSES_POTENTIALLY_ACTIVE);

        for (String docNumber : docNumbers) {
            try{
                workflowDocument = workflowDocumentService.loadWorkflowDocument(docNumber, GlobalVariables.getUserSession().getPerson());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }

            //if the document is not in a non-active status then return true and stop evaluation
            if(!(workflowDocument.isCanceled() ||
                    workflowDocument.isException() ||
                    workflowDocument.isFinal()) ){
                hasActiveCreditMemos = true;
                break;
            }

        }

        return hasActiveCreditMemos;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoCreateService#populateDocumentAfterInit(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    @Override
    public void populateDocumentAfterInit(VendorCreditMemoDocument cmDocument) {

        // make a call to search for expired/closed accounts
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = accountsPayableService.getExpiredOrClosedAccountList(cmDocument);

        if (cmDocument.isSourceDocumentPaymentRequest()) {
            populateDocumentFromPreq(cmDocument, expiredOrClosedAccountList);
        }
        else if (cmDocument.isSourceDocumentPurchaseOrder()) {
            populateDocumentFromPO(cmDocument, expiredOrClosedAccountList);
        }
        else {
            populateDocumentFromVendor(cmDocument);
        }

        populateDocumentDescription(cmDocument);

        // write a note for expired/closed accounts if any exist and add a message stating there were expired/closed accounts at the
        // top of the document
        accountsPayableService.generateExpiredOrClosedAccountNote(cmDocument, expiredOrClosedAccountList);

        // set indicator so a message is displayed for accounts that were replaced due to expired/closed status
        if (ObjectUtils.isNotNull(expiredOrClosedAccountList) && !expiredOrClosedAccountList.isEmpty()) {
            cmDocument.setContinuationAccountIndicator(true);
        }

    }

    /**
     * Populate Credit Memo of type Payment Request.
     *
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromPreq(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PaymentRequestDocument paymentRequestDocument = paymentRequestService.getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
        cmDocument.getDocumentHeader().setOrganizationDocumentNumber(paymentRequestDocument.getDocumentHeader().getOrganizationDocumentNumber());
        cmDocument.setPaymentRequestDocument(paymentRequestDocument);
        cmDocument.setPurchaseOrderDocument(paymentRequestDocument.getPurchaseOrderDocument());
        cmDocument.setUseTaxIndicator(paymentRequestDocument.isUseTaxIndicator());

        // credit memo address taken directly from payment request
        cmDocument.setVendorHeaderGeneratedIdentifier(paymentRequestDocument.getVendorHeaderGeneratedIdentifier());
        cmDocument.setVendorDetailAssignedIdentifier(paymentRequestDocument.getVendorDetailAssignedIdentifier());
        cmDocument.setVendorAddressGeneratedIdentifier(paymentRequestDocument.getVendorAddressGeneratedIdentifier());
        cmDocument.setVendorCustomerNumber(paymentRequestDocument.getVendorCustomerNumber());
        cmDocument.setVendorName(paymentRequestDocument.getVendorName());
        cmDocument.setVendorLine1Address(paymentRequestDocument.getVendorLine1Address());
        cmDocument.setVendorLine2Address(paymentRequestDocument.getVendorLine2Address());
        cmDocument.setVendorCityName(paymentRequestDocument.getVendorCityName());
        cmDocument.setVendorStateCode(paymentRequestDocument.getVendorStateCode());
        cmDocument.setVendorPostalCode(paymentRequestDocument.getVendorPostalCode());
        cmDocument.setVendorCountryCode(paymentRequestDocument.getVendorCountryCode());
        cmDocument.setVendorAttentionName(paymentRequestDocument.getVendorAttentionName());
        cmDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(paymentRequestDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());

        // prep the item lines (also collect warnings for later display) this is only done on paymentRequest
        purapAccountingService.convertMoneyToPercent(paymentRequestDocument);
        populateItemLinesFromPreq(cmDocument, expiredOrClosedAccountList);
    }

    /**
     * Populates the credit memo items from the payment request items.
     *
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateItemLinesFromPreq(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PaymentRequestDocument preqDocument = cmDocument.getPaymentRequestDocument();

        for (PaymentRequestItem preqItemToTemplate : (List<PaymentRequestItem>) preqDocument.getItems()) {
            preqItemToTemplate.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (preqItemToTemplate.getItemType().isLineItemIndicator() && ((preqItemToTemplate.getItemType().isQuantityBasedGeneralLedgerIndicator() && preqItemToTemplate.getItemQuantity().isNonZero())
                    || (preqItemToTemplate.getItemType().isAmountBasedGeneralLedgerIndicator() && preqItemToTemplate.getTotalAmount().isNonZero()))) {
                cmDocument.getItems().add(new CreditMemoItem(cmDocument, preqItemToTemplate, preqItemToTemplate.getPurchaseOrderItem(), expiredOrClosedAccountList));
            }
        }

        // add below the line items
        purapService.addBelowLineItems(cmDocument);

        cmDocument.fixItemReferences();
    }

    /**
     * Populate Credit Memo of type Purchase Order.
     *
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromPO(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
        cmDocument.setPurchaseOrderDocument(purchaseOrderDocument);
        cmDocument.getDocumentHeader().setOrganizationDocumentNumber(purchaseOrderDocument.getDocumentHeader().getOrganizationDocumentNumber());
        cmDocument.setUseTaxIndicator(cmDocument.isUseTaxIndicator());

        cmDocument.setVendorHeaderGeneratedIdentifier(purchaseOrderDocument.getVendorHeaderGeneratedIdentifier());
        cmDocument.setVendorDetailAssignedIdentifier(purchaseOrderDocument.getVendorDetailAssignedIdentifier());
        cmDocument.setVendorCustomerNumber(purchaseOrderDocument.getVendorCustomerNumber());
        cmDocument.setVendorName(purchaseOrderDocument.getVendorName());
        cmDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(purchaseOrderDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());

        // populate cm vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
        VendorAddress vendorAddress = vendorService.getVendorDefaultAddress(purchaseOrderDocument.getVendorHeaderGeneratedIdentifier(), purchaseOrderDocument.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus,false);
        if (vendorAddress != null) {
            cmDocument.templateVendorAddress(vendorAddress);
            cmDocument.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
            cmDocument.setVendorAttentionName(StringUtils.defaultString(vendorAddress.getVendorAttentionName()));
        }
        else {
            // set address from PO
            cmDocument.setVendorAddressGeneratedIdentifier(purchaseOrderDocument.getVendorAddressGeneratedIdentifier());
            cmDocument.setVendorLine1Address(purchaseOrderDocument.getVendorLine1Address());
            cmDocument.setVendorLine2Address(purchaseOrderDocument.getVendorLine2Address());
            cmDocument.setVendorCityName(purchaseOrderDocument.getVendorCityName());
            cmDocument.setVendorStateCode(purchaseOrderDocument.getVendorStateCode());
            cmDocument.setVendorPostalCode(purchaseOrderDocument.getVendorPostalCode());
            cmDocument.setVendorCountryCode(purchaseOrderDocument.getVendorCountryCode());

            boolean blankAttentionLine = StringUtils.equalsIgnoreCase("Y",SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.BLANK_ATTENTION_LINE_FOR_PO_TYPE_ADDRESS));
            if (blankAttentionLine){
                cmDocument.setVendorAttentionName(StringUtils.EMPTY);
            }else{
                cmDocument.setVendorAttentionName(StringUtils.defaultString(purchaseOrderDocument.getVendorAttentionName()));
            }
        }

        populateItemLinesFromPO(cmDocument, expiredOrClosedAccountList);
    }

    /**
     * Populates the credit memo items from the payment request items.
     *
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateItemLinesFromPO(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        List<PurchaseOrderItem> invoicedItems = getPOInvoicedItems(cmDocument.getPurchaseOrderDocument());
        for (PurchaseOrderItem poItem : invoicedItems) {
            poItem.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if ((poItem.getItemType().isQuantityBasedGeneralLedgerIndicator() && poItem.getItemInvoicedTotalQuantity().isNonZero())
                    || (poItem.getItemType().isAmountBasedGeneralLedgerIndicator() && poItem.getItemInvoicedTotalAmount().isNonZero())) {
                CreditMemoItem creditMemoItem = new CreditMemoItem(cmDocument, poItem, expiredOrClosedAccountList);
                cmDocument.getItems().add(creditMemoItem);
                PurchasingCapitalAssetItem purchasingCAMSItem = cmDocument.getPurchaseOrderDocument().getPurchasingCapitalAssetItemByItemIdentifier(poItem.getItemIdentifier());
                if (purchasingCAMSItem != null) {
                    creditMemoItem.setCapitalAssetTransactionTypeCode(purchasingCAMSItem.getCapitalAssetTransactionTypeCode());
                }
            }
        }

        // add below the line items
        purapService.addBelowLineItems(cmDocument);

        cmDocument.fixItemReferences();
    }

    /**
     * Populate Credit Memo of type Vendor.
     *
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromVendor(VendorCreditMemoDocument cmDocument) {
        Integer vendorHeaderId = VendorUtils.getVendorHeaderId(cmDocument.getVendorNumber());
        Integer vendorDetailId = VendorUtils.getVendorDetailId(cmDocument.getVendorNumber());

        VendorDetail vendorDetail = vendorService.getVendorDetail(vendorHeaderId, vendorDetailId);
        cmDocument.setVendorDetail(vendorDetail);

        cmDocument.setVendorHeaderGeneratedIdentifier(vendorDetail.getVendorHeaderGeneratedIdentifier());
        cmDocument.setVendorDetailAssignedIdentifier(vendorDetail.getVendorDetailAssignedIdentifier());
        cmDocument.setVendorCustomerNumber(vendorDetail.getVendorNumber());
        cmDocument.setVendorName(vendorDetail.getVendorName());


        // credit memo type vendor uses the default remit type address for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
        VendorAddress vendorAddress = vendorService.getVendorDefaultAddress(vendorHeaderId, vendorDetailId, VendorConstants.AddressTypes.REMIT, userCampus,false);
        if (vendorAddress == null) {
            // pick up the default vendor po address type
            vendorAddress = vendorService.getVendorDefaultAddress(vendorHeaderId, vendorDetailId, VendorConstants.AddressTypes.PURCHASE_ORDER, userCampus,false);
        }

        cmDocument.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
        cmDocument.templateVendorAddress(vendorAddress);

        // add below the line items
        purapService.addBelowLineItems(cmDocument);
    }

    /**
     * Defaults the document description based on the credit memo source type.
     *
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentDescription(VendorCreditMemoDocument cmDocument) {
        String description = "";
        if (cmDocument.isSourceVendor()) {
            description = "Vendor: " + cmDocument.getVendorName();
        }
        else {
            description = "PO: " + cmDocument.getPurchaseOrderDocument().getPurapDocumentIdentifier() + " Vendor: " + cmDocument.getVendorName();
        }

        // trim description if longer than whats specified in the data dictionary
        int noteTextMaxLength = dataDictionaryService.getAttributeMaxLength(DocumentHeader.class, KRADPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }

        cmDocument.getDocumentHeader().setDocumentDescription(description);
    }

}

