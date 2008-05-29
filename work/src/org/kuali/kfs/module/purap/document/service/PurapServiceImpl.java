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
package org.kuali.module.purap.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.UserSession;
import org.kuali.core.bo.Note;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.NoteService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.OrganizationParameter;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurapEnterableItem;
import org.kuali.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurapItemOperations;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.LogicContainer;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.exception.WorkflowException;

public class PurapServiceImpl implements PurapService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    private DocumentService documentService;
    private DataDictionaryService dataDictionaryService;
    private VendorService vendorService;
    private PersistenceService persistenceService;
    private PurchaseOrderService purchaseOrderService;
    private NoteService noteService;
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#updateStatus(org.kuali.module.purap.document.PurchasingAccountsPayableDocument, java.lang.String)
     */
    public boolean updateStatus(PurchasingAccountsPayableDocument document, String newStatus) {
        LOG.debug("updateStatus() started");

        if (ObjectUtils.isNotNull(document) || ObjectUtils.isNotNull(newStatus)) {
            String oldStatus = document.getStatusCode();
            document.setStatusCode(newStatus);
            LOG.debug("Status of document #" + document.getDocumentNumber() + " has been changed from " + oldStatus + " to " + newStatus);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#getRelatedViews(java.lang.Class, java.lang.Integer)
     */
    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        LOG.debug("getRelatedViews() started");

        Map criteria = new HashMap();
        criteria.put("accountsPayablePurchasingDocumentLinkIdentifier", accountsPayablePurchasingDocumentLinkIdentifier);
        List boList = (List) businessObjectService.findMatchingOrderBy(clazz, criteria, KFSPropertyConstants.DOCUMENT_NUMBER, true);
        return boList;
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#addBelowLineItems(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public void addBelowLineItems(PurchasingAccountsPayableDocument document) {
        LOG.debug("addBelowLineItems() started");

        String[] itemTypes = getBelowTheLineForDocument(document);

        List<PurApItem> existingItems = document.getItems();

        List<PurApItem> belowTheLine = new ArrayList<PurApItem>();
        // needed in case they get out of sync below won't work
        sortBelowTheLine(itemTypes, existingItems, belowTheLine);

        List<String> existingItemTypes = new ArrayList();
        for (PurApItem existingItem : existingItems) {
            existingItemTypes.add(existingItem.getItemTypeCode());
        }

        Class itemClass = document.getItemClass();

        for (int i = 0; i < itemTypes.length; i++) {
            int lastFound;
            if (!existingItemTypes.contains(itemTypes[i])) {
                try {
                    if (i > 0) {
                        lastFound = existingItemTypes.lastIndexOf(itemTypes[i - 1]) + 1;
                    }
                    else {
                        lastFound = existingItemTypes.size();
                    }
                    PurApItem newItem = (PurApItem) itemClass.newInstance();
                    newItem.setItemTypeCode(itemTypes[i]);
                    existingItems.add(lastFound, newItem);
                    existingItemTypes.add(itemTypes[i]);
                }
                catch (Exception e) {
                    // do something
                }
            }
        }
    }

    /**
     * Sorts the below the line elements
     * 
     * @param itemTypes
     * @param existingItems
     * @param belowTheLine
     */
    private void sortBelowTheLine(String[] itemTypes, List<PurApItem> existingItems, List<PurApItem> belowTheLine) {
        LOG.debug("sortBelowTheLine() started");

        // sort existing below the line if any
        for (int i = 0; i < existingItems.size(); i++) {
            PurApItem purApItem = existingItems.get(i);
            if (!purApItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                belowTheLine.add(existingItems.get(i));
            }
        }
        existingItems.removeAll(belowTheLine);
        for (int i = 0; i < itemTypes.length; i++) {
            for (PurApItem purApItem : belowTheLine) {
                if (StringUtils.equalsIgnoreCase(purApItem.getItemTypeCode(), itemTypes[i])) {
                    existingItems.add(purApItem);
                    break;
                }
            }
        }
        belowTheLine.removeAll(existingItems);
        if (belowTheLine.size() != 0) {
            throw new RuntimeException("below the line item sort didn't work: trying to remove an item without adding it back");
        }
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#sortBelowTheLine(java.lang.String[], java.util.List, java.util.List)
     */
    public void sortBelowTheLine(PurchasingAccountsPayableDocument document) {
        LOG.debug("sortBelowTheLine() started");

        String[] itemTypes = getBelowTheLineForDocument(document);

        List<PurApItem> existingItems = document.getItems();

        List<PurApItem> belowTheLine = new ArrayList<PurApItem>();
        // needed in case they get out of sync below won't work
        sortBelowTheLine(itemTypes, existingItems, belowTheLine);
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#getBelowTheLineForDocument(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public String[] getBelowTheLineForDocument(PurchasingAccountsPayableDocument document) {
        LOG.debug("getBelowTheLineForDocument() started");

        // Obtain a list of below the line items from system parameter
        String documentTypeClassName = document.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];

        //FIXME RELEASE 3 (hjs) why is this "if" here with no code in it?  is it supposed to be doing somethign?
        // If it's a credit memo, we'll have to append the source of the credit memo
        // whether it's created from a Vendor, a PO or a PREQ.
        if (documentType.equals("CreditMemoDocument")) {

        }
        try {
            return parameterService.getParameterValues(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.BELOW_THE_LINES_PARAMETER).toArray(new String[] {});
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("The getBelowTheLineForDocument method of PurapServiceImpl was unable to resolve the document class for type: " + PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType), e);
        }
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#getBelowTheLineByType(org.kuali.module.purap.document.PurchasingAccountsPayableDocument,
     *      org.kuali.module.purap.bo.ItemType)
     */
    public PurApItem getBelowTheLineByType(PurchasingAccountsPayableDocument document, ItemType iT) {
        LOG.debug("getBelowTheLineByType() started");

        String[] itemTypes = getBelowTheLineForDocument(document);
        boolean foundItemType = false;
        for (String itemType : itemTypes) {
            if (StringUtils.equals(iT.getItemTypeCode(), itemType)) {
                foundItemType = true;
                break;
            }
        }
        if (!foundItemType) {
            return null;
        }

        PurApItem belowTheLineItem = null;
        for (PurApItem item : (List<PurApItem>) document.getItems()) {
            if (!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                if (StringUtils.equals(iT.getItemTypeCode(), item.getItemType().getItemTypeCode())) {
                    belowTheLineItem = item;
                    break;
                }
            }
        }
        return belowTheLineItem;
    }
    
    /**
     * @see org.kuali.module.purap.service.PurapService#getDateFromOffsetFromToday(int)
     */
    public Date getDateFromOffsetFromToday(int offsetDays) {
        Calendar calendar = dateTimeService.getCurrentCalendar();
        calendar.add(Calendar.DATE, offsetDays);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#isDateInPast(java.sql.Date)
     */
    public boolean isDateInPast(Date compareDate) {
        LOG.debug("isDateInPast() started");

        Date today = dateTimeService.getCurrentSqlDate();
        int diffFromToday = dateTimeService.dateDiff(today, compareDate, false);
        return (diffFromToday < 0);
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#isDateMoreThanANumberOfDaysAway(java.sql.Date, int)
     */
    public boolean isDateMoreThanANumberOfDaysAway(Date compareDate, int daysAway) {
        LOG.debug("isDateMoreThanANumberOfDaysAway() started");

        Date todayAtMidnight = dateTimeService.getCurrentSqlDateMidnight();
        Calendar daysAwayCalendar = dateTimeService.getCalendar(todayAtMidnight);
        daysAwayCalendar.add(Calendar.DATE, daysAway);
        Timestamp daysAwayTime = new Timestamp(daysAwayCalendar.getTime().getTime());
        Calendar compareCalendar = dateTimeService.getCalendar(compareDate);
        compareCalendar.set(Calendar.HOUR, 0);
        compareCalendar.set(Calendar.MINUTE, 0);
        compareCalendar.set(Calendar.SECOND, 0);
        compareCalendar.set(Calendar.MILLISECOND, 0);
        compareCalendar.set(Calendar.AM_PM, Calendar.AM);
        Timestamp compareTime = new Timestamp(compareCalendar.getTime().getTime());
        return (compareTime.compareTo(daysAwayTime) > 0);
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#isDateAYearAfterToday(java.sql.Date)
     */
    public boolean isDateAYearBeforeToday(Date compareDate) {
        LOG.debug("isDateAYearBeforeToday() started");

        Calendar calendar = dateTimeService.getCurrentCalendar();
        calendar.add(Calendar.YEAR, -1);
        Date yearAgo = new Date(calendar.getTimeInMillis());
        int diffFromYearAgo = dateTimeService.dateDiff(compareDate, yearAgo, false);
        return (diffFromYearAgo > 0);
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#getApoLimit(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public KualiDecimal getApoLimit(Integer vendorContractGeneratedIdentifier, String chart, String org) {
        LOG.debug("getApoLimit() started");

        KualiDecimal purchaseOrderTotalLimit = vendorService.getApoLimitFromContract(vendorContractGeneratedIdentifier, chart, org);
        if (ObjectUtils.isNull(purchaseOrderTotalLimit)) {
            // We didn't find the limit on the vendor contract, get it from the org parameter table.
            if (ObjectUtils.isNull(chart) || ObjectUtils.isNull(org)) {
                return null;
            }
            OrganizationParameter organizationParameter = new OrganizationParameter();
            organizationParameter.setChartOfAccountsCode(chart);
            organizationParameter.setOrganizationCode(org);
            Map orgParamKeys = persistenceService.getPrimaryKeyFieldValues(organizationParameter);
            organizationParameter = (OrganizationParameter) businessObjectService.findByPrimaryKey(OrganizationParameter.class, orgParamKeys);
            purchaseOrderTotalLimit = (organizationParameter == null) ? null : organizationParameter.getOrganizationAutomaticPurchaseOrderLimit();
        }
        return purchaseOrderTotalLimit;
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#isFullDocumentEntryCompleted(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public boolean isFullDocumentEntryCompleted(PurchasingAccountsPayableDocument purapDocument) {
        LOG.debug("isFullDocumentEntryCompleted() started");

        // for now just return true if not in one of the first few states
        boolean value = false;
        if (purapDocument instanceof PaymentRequestDocument) {
            value = PurapConstants.PaymentRequestStatuses.STATUS_ORDER.isFullDocumentEntryCompleted(purapDocument.getStatusCode());
        }
        else if (purapDocument instanceof CreditMemoDocument) {
            value = PurapConstants.CreditMemoStatuses.STATUS_ORDER.isFullDocumentEntryCompleted(purapDocument.getStatusCode());
        }
        return value;
    }


    /**
     * Main hook point for close/Reopen PO.
     * 
     * @see org.kuali.module.purap.service.PurapService#performLogicForCloseReopenPO(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public void performLogicForCloseReopenPO(PurchasingAccountsPayableDocument purapDocument) {
        LOG.debug("performLogicForCloseReopenPO() started");

        if (purapDocument instanceof PaymentRequestDocument) {
            PaymentRequestDocument paymentRequest = (PaymentRequestDocument) purapDocument;

            if (paymentRequest.isClosePurchaseOrderIndicator() && PurapConstants.PurchaseOrderStatuses.OPEN.equals(paymentRequest.getPurchaseOrderDocument().getStatusCode())) {
                // get the po id and get the current po
                // check the current po: if status is not closed and there is no pending action... route close po as system user
                processCloseReopenPo((AccountsPayableDocumentBase) purapDocument, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT);
            }

        }
        else if (purapDocument instanceof CreditMemoDocument) {
            CreditMemoDocument creditMemo = (CreditMemoDocument) purapDocument;

            if (creditMemo.isReopenPurchaseOrderIndicator() && PurapConstants.PurchaseOrderStatuses.CLOSED.equals(creditMemo.getPurchaseOrderDocument().getStatusCode())) {
                // get the po id and get the current PO
                // route 'Re-Open PO Document' if PO criteria meets requirements from EPIC business rules
                processCloseReopenPo((AccountsPayableDocumentBase) purapDocument, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT);
            }

        }
        else {
            throw new RuntimeException("Attempted to perform full entry logic for unhandled document type '" + purapDocument.getClass().getName() + "'");
        }

    }

    /**
     * Remove items that have not been "entered" which means no data has been added to them so no more processing needs to continue
     * on these items.
     * 
     * @param apDocument  AccountsPayableDocument which contains list of items to be reviewed
     */
    public void deleteUnenteredItems(PurapItemOperations document) {
        LOG.debug("deleteUnenteredItems() started");
        
        List<PurapEnterableItem> deletionList = new ArrayList<PurapEnterableItem>();
        for (PurapEnterableItem item : (List<PurapEnterableItem>) document.getItems()) {
            if (!item.isConsideredEntered()) {
                deletionList.add(item);
            }
        }
        document.getItems().removeAll(deletionList);
    }

    /**
     * Actual method that will close or reopen a po.
     * 
     * @param apDocument  AccountsPayableDocument
     * @param docType
     */
    public void processCloseReopenPo(AccountsPayableDocumentBase apDocument, String docType) {
        LOG.debug("processCloseReopenPo() started");
        
        String action = null;
        String newStatus = null;
        // setup text for note that will be created, will either be closed or reopened
        if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT.equals(docType)) {
            action = "closed";
            newStatus = PurchaseOrderStatuses.PENDING_CLOSE;
        }
        else if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT.equals(docType)) {
            action = "reopened";
            newStatus = PurchaseOrderStatuses.PENDING_REOPEN;
        }
        else {
            String errorMessage = "Method processCloseReopenPo called using ID + '" + apDocument.getPurapDocumentIdentifier() + "' and invalid doc type '" + docType + "'";
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }


        Integer poId = apDocument.getPurchaseOrderIdentifier();
        PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(poId);
        if (!StringUtils.equalsIgnoreCase(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument().getDocumentType(), docType)) {
            // we are skipping the validation above because it would be too late to correct any errors (i.e. because in
            // post-processing)
            purchaseOrderService.createAndRoutePotentialChangeDocument(purchaseOrderDocument.getDocumentNumber(), docType, assemblePurchaseOrderNote(apDocument, docType, action), new ArrayList(), newStatus);
        }

        /*
         * if we made it here, route document has not errored out, so set appropriate indicator depending on what is being
         * requested.
         */
        if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT.equals(docType)) {
            apDocument.setClosePurchaseOrderIndicator(false);
            
            //add a note to the purchase order indicating it has been closed by a payment request document            
            String userName = apDocument.getLastActionPerformedByPersonName();
            StringBuffer poNote = new StringBuffer("");
            poNote.append("PO was closed manually by ");            
            poNote.append( userName );
            poNote.append(" in approving PREQ with ID ");
            poNote.append(apDocument.getDocumentNumber());
                        
            //save the note to the purchase order
            try{
                Note noteObj = documentService.createNoteFromDocument(apDocument.getPurchaseOrderDocument(), poNote.toString());
                documentService.addNoteToDocument(apDocument.getPurchaseOrderDocument(), noteObj);
                noteService.save(noteObj);
            }catch(Exception e){
                String errorMessage = "Error creating and saving close note for purchase order with document service";
                LOG.error("processCloseReopenPo() " + errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }                        
        }
        else if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT.equals(docType)) {
            apDocument.setReopenPurchaseOrderIndicator(false);
        }

    }

    /**
     * Generate a note for the close/reopen po method.
     * 
     * @param docType
     * @param preqId
     * @return Note to be saved
     */
    private String assemblePurchaseOrderNote(AccountsPayableDocumentBase apDocument, String docType, String action) {
        LOG.debug("assemblePurchaseOrderNote() started");

        String documentLabel = dataDictionaryService.getDocumentLabelByClass(getClass());
        StringBuffer closeReopenNote = new StringBuffer("");
        String userName = GlobalVariables.getUserSession().getUniversalUser().getPersonName();
        closeReopenNote.append(dataDictionaryService.getDocumentLabelByClass(PurchaseOrderDocument.class));
        closeReopenNote.append(" will be manually ");
        closeReopenNote.append(action);
        closeReopenNote.append(" by ");
        closeReopenNote.append(userName);
        closeReopenNote.append(" when approving ");
        closeReopenNote.append(documentLabel);
        closeReopenNote.append(" with ");
        closeReopenNote.append(dataDictionaryService.getAttributeLabel(getClass(), PurapPropertyConstants.PURAP_DOC_ID));
        closeReopenNote.append(" ");
        closeReopenNote.append(apDocument.getPurapDocumentIdentifier());

        return closeReopenNote.toString();
    }

    /**
     * @see org.kuali.module.purap.service.PurapService#performLogicWithFakedUserSession(java.lang.String, org.kuali.module.purap.service.LogicContainer, java.lang.Object[])
     */
    public Object performLogicWithFakedUserSession(String requiredUniversalUserPersonUserId, LogicContainer logicToRun, Object... objects) throws UserNotFoundException, WorkflowException, Exception {
        LOG.debug("performLogicWithFakedUserSession() started");

        if (StringUtils.isBlank(requiredUniversalUserPersonUserId)) {
            throw new RuntimeException("Attempted to perform logic with a fake user session with a blank user person id: '" + requiredUniversalUserPersonUserId + "'");
        }
        if (ObjectUtils.isNull(logicToRun)) {
            throw new RuntimeException("Attempted to perform logic with a fake user session with no logic to run");
        }
        UserSession actualUserSession = GlobalVariables.getUserSession();
        try {
            GlobalVariables.setUserSession(new UserSession(requiredUniversalUserPersonUserId));
            return logicToRun.runLogic(objects);
        }
        finally {
            GlobalVariables.setUserSession(actualUserSession);
        }
    }

    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#saveDocumentNoValidation(org.kuali.module.purap.document.PurchaseOrderDocument)
     */
    public void saveDocumentNoValidation(Document document) {
        try {
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }
    
    }
