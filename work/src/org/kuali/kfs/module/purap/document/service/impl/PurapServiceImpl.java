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
package org.kuali.kfs.module.purap.document.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants.TaxParameters;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.BulkReceivingView;
import org.kuali.kfs.module.purap.businessobject.CorrectionReceivingView;
import org.kuali.kfs.module.purap.businessobject.CreditMemoView;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingView;
import org.kuali.kfs.module.purap.businessobject.OrganizationParameter;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurapEnterableItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.businessobject.RequisitionView;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurapItemOperations;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.dataaccess.ParameterDao;
import org.kuali.kfs.module.purap.document.service.LogicContainer;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

@NonTransactional
public class PurapServiceImpl implements PurapService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private NoteService noteService;
    private ParameterService parameterService;
    private PersistenceService persistenceService;
    private PurchaseOrderService purchaseOrderService;
    private UniversityDateService universityDateService;
    private VendorService vendorService;
    private ParameterDao parameterDao;
    private TaxService taxService; 
    
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

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
    
    public void setParameterDao(ParameterDao parameterDao) {
        this.parameterDao = parameterDao;
    }
    
    public void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#updateStatus(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument, java.lang.String)
     */
  //TODO hjs: is this method really needed now that we don't have status history tables?
    public boolean updateStatus(PurchasingAccountsPayableDocument document, String newStatus) {
        LOG.debug("updateStatus() started");

        if (ObjectUtils.isNotNull(document) || ObjectUtils.isNotNull(newStatus)) {
            String oldStatus = document.getStatusCode();
            document.setStatusCode(newStatus);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Status of document #" + document.getDocumentNumber() + " has been changed from " + oldStatus + " to " + newStatus);
            }
            return true;
        }
        else {
            return false;
        }
    }

    public void saveRoutingDataForRelatedDocuments(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        
        try {
            //save requisition routing data
            List reqViews = getRelatedViews(RequisitionView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (Iterator iterator = reqViews.iterator(); iterator.hasNext();) {
                RequisitionView view = (RequisitionView) iterator.next();
                Document doc = documentService.getByDocumentHeaderId(view.getDocumentNumber());
                doc.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            }
            
            //save purchase order routing data
            List poViews = getRelatedViews(PurchaseOrderView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (Iterator iterator = poViews.iterator(); iterator.hasNext();) {
                PurchaseOrderView view = (PurchaseOrderView) iterator.next();
                Document doc = documentService.getByDocumentHeaderId(view.getDocumentNumber());
                doc.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            }
            
            //save payment request routing data
            List preqViews = getRelatedViews(PaymentRequestView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (Iterator iterator = preqViews.iterator(); iterator.hasNext();) {
                PaymentRequestView view = (PaymentRequestView) iterator.next();
                Document doc = documentService.getByDocumentHeaderId(view.getDocumentNumber());
                doc.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            }
            
            //save credit memo routing data
            List cmViews = getRelatedViews(CreditMemoView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (Iterator iterator = cmViews.iterator(); iterator.hasNext();) {
                CreditMemoView view = (CreditMemoView) iterator.next();
                Document doc = documentService.getByDocumentHeaderId(view.getDocumentNumber());
                doc.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            }
            
            //save line item receiving routing data
            List lineViews = getRelatedViews(LineItemReceivingView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (Iterator iterator = lineViews.iterator(); iterator.hasNext();) {
                LineItemReceivingView view = (LineItemReceivingView) iterator.next();
                Document doc = documentService.getByDocumentHeaderId(view.getDocumentNumber());
                doc.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            }

            //save correction receiving routing data
            List corrViews = getRelatedViews(CorrectionReceivingView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (Iterator iterator = corrViews.iterator(); iterator.hasNext();) {
                CorrectionReceivingView view = (CorrectionReceivingView) iterator.next();
                Document doc = documentService.getByDocumentHeaderId(view.getDocumentNumber());
                doc.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            }
            
            //save bulk receiving routing data
            List bulkViews = getRelatedViews(BulkReceivingView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (Iterator iterator = bulkViews.iterator(); iterator.hasNext();) {
                BulkReceivingView view = (BulkReceivingView) iterator.next();
                Document doc = documentService.getByDocumentHeaderId(view.getDocumentNumber());
                doc.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            }
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("unable to save routing data for related docs", e);
        }

    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#getRelatedDocumentIds(java.lang.Integer)
     */
    public List<String> getRelatedDocumentIds(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        LOG.debug("getRelatedDocumentIds() started");
        List documentIdList = new ArrayList();

        //get requisition views
        List reqViews = getRelatedViews(RequisitionView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        for (Iterator iterator = reqViews.iterator(); iterator.hasNext();) {
            RequisitionView view = (RequisitionView) iterator.next();
            documentIdList.add(view.getDocumentNumber());
        }
        
        //get purchase order views
        List poViews = getRelatedViews(PurchaseOrderView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        for (Iterator iterator = poViews.iterator(); iterator.hasNext();) {
            PurchaseOrderView view = (PurchaseOrderView) iterator.next();
            documentIdList.add(view.getDocumentNumber());
        }
        
        //get payment request views
        List preqViews = getRelatedViews(PaymentRequestView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        for (Iterator iterator = preqViews.iterator(); iterator.hasNext();) {
            PaymentRequestView view = (PaymentRequestView) iterator.next();
            documentIdList.add(view.getDocumentNumber());
        }
        
        //get credit memo views
        List cmViews = getRelatedViews(CreditMemoView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        for (Iterator iterator = cmViews.iterator(); iterator.hasNext();) {
            CreditMemoView view = (CreditMemoView) iterator.next();
            documentIdList.add(view.getDocumentNumber());
        }
        
        //get line item receiving views
        List lineViews = getRelatedViews(LineItemReceivingView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        for (Iterator iterator = lineViews.iterator(); iterator.hasNext();) {
            LineItemReceivingView view = (LineItemReceivingView) iterator.next();
            documentIdList.add(view.getDocumentNumber());
        }

        //get correction receiving views
        List corrViews = getRelatedViews(CorrectionReceivingView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        for (Iterator iterator = corrViews.iterator(); iterator.hasNext();) {
            CorrectionReceivingView view = (CorrectionReceivingView) iterator.next();
            documentIdList.add(view.getDocumentNumber());
        }
        
        //get bulk receiving views
        List bulkViews = getRelatedViews(BulkReceivingView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        for (Iterator iterator = bulkViews.iterator(); iterator.hasNext();) {
            BulkReceivingView view = (BulkReceivingView) iterator.next();
            documentIdList.add(view.getDocumentNumber());
        }
        
        //TODO (hjs)get electronic invoice reject views???
        
        return documentIdList;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#getRelatedViews(java.lang.Class, java.lang.Integer)
     */
    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        LOG.debug("getRelatedViews() started");

        Map criteria = new HashMap();
        criteria.put("accountsPayablePurchasingDocumentLinkIdentifier", accountsPayablePurchasingDocumentLinkIdentifier);
        
        // retrieve in descending order of document number so that newer documents are in the front
        List boList = (List) businessObjectService.findMatchingOrderBy(clazz, criteria, KFSPropertyConstants.DOCUMENT_NUMBER, false);
        return boList;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#addBelowLineItems(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
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
            if (purApItem.getItemType().isAdditionalChargeIndicator()) {
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
     * @see org.kuali.kfs.module.purap.document.service.PurapService#sortBelowTheLine(java.lang.String[], java.util.List, java.util.List)
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
     * @see org.kuali.kfs.module.purap.document.service.PurapService#getBelowTheLineForDocument(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public String[] getBelowTheLineForDocument(PurchasingAccountsPayableDocument document) {
        LOG.debug("getBelowTheLineForDocument() started");

        // Obtain a list of below the line items from system parameter
//        String documentTypeClassName = document.getClass().getName();
//        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
//        String documentType = documentTypeArray[documentTypeArray.length - 1];

        //FIXME RELEASE 3 (hjs) why is this "if" here with no code in it?  is it supposed to be doing somethign?
        // If it's a credit memo, we'll have to append the source of the credit memo
        // whether it's created from a Vendor, a PO or a PREQ.
//        if (documentType.equals("CreditMemoDocument")) {
//
//        }

        String documentType = dataDictionaryService.getDocumentTypeNameByClass(document.getClass());
        
        try {
            return parameterService.getParameterValues(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.BELOW_THE_LINES_PARAMETER).toArray(new String[] {});
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("The getBelowTheLineForDocument method of PurapServiceImpl was unable to resolve the document class for type: " + PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType), e);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#getBelowTheLineByType(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument,
     *      org.kuali.kfs.module.purap.businessobject.ItemType)
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
            if (item.getItemType().isAdditionalChargeIndicator()) {
                if (StringUtils.equals(iT.getItemTypeCode(), item.getItemType().getItemTypeCode())) {
                    belowTheLineItem = item;
                    break;
                }
            }
        }
        return belowTheLineItem;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#getDateFromOffsetFromToday(int)
     */
    public Date getDateFromOffsetFromToday(int offsetDays) {
        Calendar calendar = dateTimeService.getCurrentCalendar();
        calendar.add(Calendar.DATE, offsetDays);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#isDateInPast(java.sql.Date)
     */
    public boolean isDateInPast(Date compareDate) {
        LOG.debug("isDateInPast() started");

        Date today = dateTimeService.getCurrentSqlDate();
        int diffFromToday = dateTimeService.dateDiff(today, compareDate, false);
        return (diffFromToday < 0);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#isDateMoreThanANumberOfDaysAway(java.sql.Date, int)
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
     * @see org.kuali.kfs.module.purap.document.service.PurapService#isDateAYearAfterToday(java.sql.Date)
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
     * @see org.kuali.kfs.module.purap.document.service.PurapService#getApoLimit(java.lang.Integer, java.lang.String, java.lang.String)
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
            orgParamKeys.put(KNSPropertyConstants.ACTIVE_INDICATOR, true);
            organizationParameter = (OrganizationParameter) businessObjectService.findByPrimaryKey(OrganizationParameter.class, orgParamKeys);
            purchaseOrderTotalLimit = (organizationParameter == null) ? null : organizationParameter.getOrganizationAutomaticPurchaseOrderLimit();
        }
        
        String defaultLimit = parameterService.getParameterValue(RequisitionDocument.class, PurapParameterConstants.AUTOMATIC_PURCHASE_ORDER_DEFAULT_LIMIT_AMOUNT);
        if (ObjectUtils.isNull(purchaseOrderTotalLimit)) {
            purchaseOrderTotalLimit = new KualiDecimal(defaultLimit);
        }

        return purchaseOrderTotalLimit;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#isFullDocumentEntryCompleted(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public boolean isFullDocumentEntryCompleted(PurchasingAccountsPayableDocument purapDocument) {
        LOG.debug("isFullDocumentEntryCompleted() started");

        // for now just return true if not in one of the first few states
        boolean value = false;
        if (purapDocument instanceof PaymentRequestDocument) {
            value = PurapConstants.PaymentRequestStatuses.STATUS_ORDER.isFullDocumentEntryCompleted(purapDocument.getStatusCode());
        }
        else if (purapDocument instanceof VendorCreditMemoDocument) {
            value = PurapConstants.CreditMemoStatuses.STATUS_ORDER.isFullDocumentEntryCompleted(purapDocument.getStatusCode());
        }
        return value;
    }


    /**
     * Main hook point for close/Reopen PO.
     * 
     * @see org.kuali.kfs.module.purap.document.service.PurapService#performLogicForCloseReopenPO(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
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
        else if (purapDocument instanceof VendorCreditMemoDocument) {
            VendorCreditMemoDocument creditMemo = (VendorCreditMemoDocument) purapDocument;

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

        String documentLabel = dataDictionaryService.getDocumentLabelByClass(apDocument.getClass());
        StringBuffer closeReopenNote = new StringBuffer("");
        String userName = GlobalVariables.getUserSession().getPerson().getName();
        closeReopenNote.append(dataDictionaryService.getDocumentLabelByClass(PurchaseOrderDocument.class));
        closeReopenNote.append(" will be manually ");
        closeReopenNote.append(action);
        closeReopenNote.append(" by ");
        closeReopenNote.append(userName);
        closeReopenNote.append(" when approving ");
        closeReopenNote.append(documentLabel);
        closeReopenNote.append(" with ");
        closeReopenNote.append(dataDictionaryService.getAttributeLabel(apDocument.getClass(), PurapPropertyConstants.PURAP_DOC_ID));
        closeReopenNote.append(" ");
        closeReopenNote.append(apDocument.getPurapDocumentIdentifier());

        return closeReopenNote.toString();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#performLogicWithFakedUserSession(java.lang.String, org.kuali.kfs.module.purap.document.service.LogicContainer, java.lang.Object[])
     */
    public Object performLogicWithFakedUserSession(String requiredPersonPersonUserId, LogicContainer logicToRun, Object... objects) throws WorkflowException, Exception {
        LOG.debug("performLogicWithFakedUserSession() started");

        if (StringUtils.isBlank(requiredPersonPersonUserId)) {
            throw new RuntimeException("Attempted to perform logic with a fake user session with a blank user person id: '" + requiredPersonPersonUserId + "'");
        }
        if (ObjectUtils.isNull(logicToRun)) {
            throw new RuntimeException("Attempted to perform logic with a fake user session with no logic to run");
        }
        UserSession actualUserSession = GlobalVariables.getUserSession();
        try {
            GlobalVariables.setUserSession(new UserSession(requiredPersonPersonUserId));
            return logicToRun.runLogic(objects);
        }
        finally {
            GlobalVariables.setUserSession(actualUserSession);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#saveDocumentNoValidation(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
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
    
    public boolean isDocumentStoppedInRouteNode(PurchasingAccountsPayableDocument document, String nodeName) {
        List<String> currentRouteLevels = new ArrayList<String>();
        try {
            KualiWorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
            currentRouteLevels = Arrays.asList(document.getDocumentHeader().getWorkflowDocument().getNodeNames());
            if (currentRouteLevels.contains(nodeName) && workflowDoc.isApprovalRequested()) {
                return true;
            }
            return false;
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#allowEncumberNextFiscalYear()
     */
    public boolean allowEncumberNextFiscalYear() {
        LOG.debug("allowEncumberNextFiscalYear() started");

        java.util.Date today = dateTimeService.getCurrentDate();
        java.util.Date closingDate = universityDateService.getLastDateOfFiscalYear(universityDateService.getCurrentFiscalYear());
        int allowEncumberNext = (Integer.parseInt(parameterService.getParameterValue(RequisitionDocument.class, PurapRuleConstants.ALLOW_ENCUMBER_NEXT_YEAR_DAYS)));
        int diffTodayClosing = dateTimeService.dateDiff(today, closingDate, false);

        if (ObjectUtils.isNotNull(closingDate) && ObjectUtils.isNotNull(today) && ObjectUtils.isNotNull(allowEncumberNext)) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("allowEncumberNextFiscalYear() today = " + dateTimeService.toDateString(today) + "; encumber next FY range = " + allowEncumberNext + " - " + dateTimeService.toDateTimeString(today));
            }

            if (allowEncumberNext >= diffTodayClosing && diffTodayClosing >= KualiDecimal.ZERO.intValue()) {
                LOG.debug("allowEncumberNextFiscalYear() encumber next FY allowed; return true.");
                return true;
            }
        }
        LOG.debug("allowEncumberNextFiscalYear() encumber next FY not allowed; return false.");
        return false;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#getAllowedFiscalYears()
     */
    public List<Integer> getAllowedFiscalYears() {
        List allowedYears = new ArrayList();
        Integer currentFY = universityDateService.getCurrentFiscalYear();
        allowedYears.add(currentFY);
        if (allowEncumberNextFiscalYear()) {
            allowedYears.add(currentFY + 1);
        }
        return allowedYears;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#isTodayWithinApoAllowedRange()
     */
    public boolean isTodayWithinApoAllowedRange() {
        java.util.Date today = dateTimeService.getCurrentDate();
        Integer currentFY = universityDateService.getCurrentFiscalYear();
        java.util.Date closingDate = universityDateService.getLastDateOfFiscalYear(currentFY);
        int allowApoDate = (Integer.parseInt(parameterService.getParameterValue(RequisitionDocument.class, PurapRuleConstants.ALLOW_APO_NEXT_FY_DAYS)));
        int diffTodayClosing = dateTimeService.dateDiff(today, closingDate, true);

        return diffTodayClosing <= allowApoDate;
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ParameterService#getParametersGivenLikeCriteria(java.util.Map)
     */
    public List<Parameter> getParametersGivenLikeCriteria(Map<String, String> fieldValues) {
        return parameterDao.getParametersGivenLikeCriteria(fieldValues);
    }    
    
    /**
     * 
     * @see org.kuali.kfs.module.purap.document.service.PurapService#clearTax(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public void clearTax(PurchasingAccountsPayableDocument purapDocument, boolean useTax){
        for (PurApItem item : purapDocument.getItems()) {
            if(item.getItemType().isLineItemIndicator()) {
                //FIXME: only do above the line right now, if we tax below the line we need to change this

                if(useTax) {
                    item.getUseTaxItems().clear();
                } else {
                    item.setItemTaxAmount(null);
                }
                
            }
        }
    }
    
    public void updateUseTaxIndicator(PurchasingAccountsPayableDocument purapDocument, boolean newUseTaxIndicatorValue) {
        boolean currentUseTaxIndicator = purapDocument.isUseTaxIndicator();
        if(currentUseTaxIndicator!=newUseTaxIndicatorValue) {
            //i.e. if the indicator changed clear out the tax
            clearTax(purapDocument, currentUseTaxIndicator);
        }
        purapDocument.setUseTaxIndicator(newUseTaxIndicatorValue);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.PurapService#calculateTax(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public void calculateTax(PurchasingAccountsPayableDocument purapDocument){
        
          boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);
          boolean useTaxIndicator = purapDocument.isUseTaxIndicator();
          String deliveryState = getDeliveryState(purapDocument);
          String deliveryPostalCode = getDeliveryPostalCode(purapDocument);
          Date transactionTaxDate = purapDocument.getTransactionTaxDate();
          
          //calculate if sales tax enabled for purap
          if( salesTaxInd ){
              //iterate over items and calculate tax if taxable
              for(PurApItem item : purapDocument.getItems()){
                  if( isTaxable(useTaxIndicator, deliveryState, item) ){
                      calculateItemTax(useTaxIndicator, deliveryPostalCode, transactionTaxDate, item, item.getUseTaxClass());
                  }
              }
          }
      }
      
      private String getDeliveryState(PurchasingAccountsPayableDocument purapDocument){
          if (purapDocument instanceof PurchasingDocument){
              PurchasingDocument document = (PurchasingDocument)purapDocument; 
              return document.getDeliveryStateCode();
          }else if (purapDocument instanceof AccountsPayableDocument){
              AccountsPayableDocument document = (AccountsPayableDocument)purapDocument;
              if (document.getPurchaseOrderDocument() == null){
                  throw new RuntimeException("PurchaseOrder document does not exists");
              }
              return document.getPurchaseOrderDocument().getDeliveryStateCode();
          }
          return null;
      }
      
      private String getDeliveryPostalCode(PurchasingAccountsPayableDocument purapDocument){
          if (purapDocument instanceof PurchasingDocument){
              PurchasingDocument document = (PurchasingDocument)purapDocument; 
              return document.getDeliveryPostalCode();
          }else if (purapDocument instanceof AccountsPayableDocument){
              AccountsPayableDocument docBase = (AccountsPayableDocument)purapDocument;
              if (docBase.getPurchaseOrderDocument() == null){
                  throw new RuntimeException("PurchaseOrder document does not exists");
              }
              return docBase.getPurchaseOrderDocument().getDeliveryPostalCode();
          }
          return null;
      }
    
    /**
     * Determines if the item is taxable based on a decision tree.
     * 
     * @param useTaxIndicator
     * @param deliveryState
     * @param item
     * @return
     */
    private boolean isTaxable(boolean useTaxIndicator, String deliveryState, PurApItem item){
        
        boolean taxable = false;
        
        if(item.getItemType().isTaxableIndicator() &&
           ((ObjectUtils.isNull(item.getItemTaxAmount()) && useTaxIndicator == false) || useTaxIndicator) &&
           (doesCommodityAllowCallToTaxService(item)) &&                      
           (doesAccountAllowCallToTaxService(deliveryState, item)) ){
            
            taxable = true;
        }
        return taxable;
    }

    /**
     * Determines if the the tax service should be called due to the commodity code.
     * 
     * @param item
     * @return
     */
    private boolean doesCommodityAllowCallToTaxService(PurApItem item){
        
        boolean callService = true;
        
        if( item instanceof PurchasingItem){
            
            PurchasingItemBase purItem = (PurchasingItemBase)item;
            
            if(ObjectUtils.isNotNull(purItem.getCommodityCode())){                
                
                if(purItem.getCommodityCode().isSalesTaxIndicator() == false){
                    //not taxable, so don't call service
                    callService = false;
                }//if true we want to call service
                
            }//if null, return true
            
        }//if not a purchasing item, then we skip and still return true
        
        return callService;
    }

    /**
     * Determines if the delivery state is taxable or not. If parameter is Allow and delivery state in list, or parameter is Denied
     * and delivery state is not in list then state is taxable.
     * 
     * @param deliveryState
     * @return
     */
    private boolean isDeliveryStateTaxable(String deliveryState) {
        ParameterEvaluator parmEval = SpringContext.getBean(ParameterService.class).getParameterEvaluator(KfsParameterConstants.PURCHASING_DOCUMENT.class, TaxParameters.TAXABLE_DELIVERY_STATES, deliveryState);
        return parmEval.evaluationSucceeds();
    }
    
    /**
     * Checks if the account is taxable, based on the delivery state, fund/subfund groups, and object code level/consolidations.
     * 
     * @param deliveryState
     * @param item
     * @return
     */
    private boolean doesAccountAllowCallToTaxService(String deliveryState, PurApItem item) {
        boolean callService = false;
        boolean deliveryStateTaxable = isDeliveryStateTaxable(deliveryState);
        String parameterSuffix = null;

        for (PurApAccountingLine acctLine : item.getSourceAccountingLines()) {
            if (deliveryStateTaxable) {
                parameterSuffix = TaxParameters.FOR_TAXABLE_STATES_SUFFIX;
            }
            else {
                parameterSuffix = TaxParameters.FOR_NON_TAXABLE_STATES_SUFFIX;
            }

            // is account (fund/subfund) and object code (level/consolidation) taxable?
            if (isAccountTaxable(parameterSuffix, acctLine) && isObjectCodeTaxable(parameterSuffix, acctLine)) {
                callService = true;
                break;
            }
        }

        return callService;
    }
    
    /**
     * Checks if the account fund/subfund groups are in a set of parameters taking into account allowed/denied constraints and
     * ultimately determines if taxable.
     * 
     * @param parameterSuffix
     * @param acctLine
     * @return
     */
    private boolean isAccountTaxable(String parameterSuffix, PurApAccountingLine acctLine){
        
        boolean isAccountTaxable = false;
        String fundParam = TaxParameters.TAXABLE_FUND_GROUPS_PREFIX + parameterSuffix;
        String subFundParam = TaxParameters.TAXABLE_SUB_FUND_GROUPS_PREFIX + parameterSuffix;
        ParameterEvaluator fundParamEval = null;
        ParameterEvaluator subFundParamEval = null;
        
        if (ObjectUtils.isNull(acctLine.getAccount().getSubFundGroup())){
            acctLine.refreshNonUpdateableReferences();
        }
        
        fundParamEval = SpringContext.getBean(ParameterService.class).getParameterEvaluator(KfsParameterConstants.PURCHASING_DOCUMENT.class, fundParam, acctLine.getAccount().getSubFundGroup().getFundGroupCode());
        subFundParamEval = SpringContext.getBean(ParameterService.class).getParameterEvaluator(KfsParameterConstants.PURCHASING_DOCUMENT.class, subFundParam, acctLine.getAccount().getSubFundGroupCode());

        if( (isAllowedFound(fundParamEval) && (isAllowedFound(subFundParamEval) || isAllowedNotFound(subFundParamEval) || isDeniedNotFound(subFundParamEval))) ||
            (isAllowedNotFound(fundParamEval) && isAllowedFound(subFundParamEval)) ||
            (isDeniedFound(fundParamEval) && isAllowedFound(subFundParamEval)) ||
            (isDeniedNotFound(fundParamEval) && (isAllowedFound(subFundParamEval) || isAllowedNotFound(subFundParamEval) || isDeniedNotFound(subFundParamEval))) ){
            
            isAccountTaxable = true;
        }
        
        return isAccountTaxable;
    }

    /**
     * Checks if the object code level/consolidation groups are in a set of parameters taking into account allowed/denied constraints and
     * ultimately determines if taxable.
     * 
     * @param parameterSuffix
     * @param acctLine
     * @return
     */
    private boolean isObjectCodeTaxable(String parameterSuffix, PurApAccountingLine acctLine){
        
        boolean isObjectCodeTaxable = false;
        String levelParam = TaxParameters.TAXABLE_OBJECT_LEVELS_PREFIX + parameterSuffix;
        String consolidationParam = TaxParameters.TAXABLE_OBJECT_CONSOLIDATIONS_PREFIX + parameterSuffix;
        ParameterEvaluator levelParamEval = null;
        ParameterEvaluator consolidationParamEval = null;

        levelParamEval = SpringContext.getBean(ParameterService.class).getParameterEvaluator(KfsParameterConstants.PURCHASING_DOCUMENT.class, levelParam, acctLine.getObjectCode().getFinancialObjectLevelCode());
        consolidationParamEval = SpringContext.getBean(ParameterService.class).getParameterEvaluator(KfsParameterConstants.PURCHASING_DOCUMENT.class, consolidationParam, acctLine.getObjectCode().getFinancialObjectLevel().getFinancialConsolidationObjectCode());

        if( (isAllowedFound(levelParamEval) && (isAllowedFound(consolidationParamEval) || isAllowedNotFound(consolidationParamEval) || isDeniedNotFound(consolidationParamEval))) ||
            (isAllowedNotFound(levelParamEval) && isAllowedFound(consolidationParamEval)) ||
            (isDeniedFound(levelParamEval) && isAllowedFound(consolidationParamEval)) ||
            (isDeniedNotFound(levelParamEval) && (isAllowedFound(consolidationParamEval) || isAllowedNotFound(consolidationParamEval) || isDeniedNotFound(consolidationParamEval))) ){
                
            isObjectCodeTaxable = true;
        }

        return isObjectCodeTaxable;
    }

    /**
     * Helper method to work with parameter evaluator to find, allowed and found in parameter value.
     * 
     * @param eval
     * @return
     */
    private boolean isAllowedFound(ParameterEvaluator eval) {
        boolean exists = false;

        if (eval.evaluationSucceeds() && eval.constraintIsAllow()) {
            exists = true;
        }

        return exists;
    }

    /**
     * Helper method to work with parameter evaluator to find, allowed and not found in parameter value.
     * 
     * @param eval
     * @return
     */
    private boolean isAllowedNotFound(ParameterEvaluator eval) {
        boolean exists = false;

        if (eval.evaluationSucceeds() == false && eval.constraintIsAllow()) {
            exists = true;
        }

        return exists;
    }
    
    /**
     * Helper method to work with parameter evaluator to find, denied and found in parameter value.
     * 
     * @param eval
     * @return
     */
    private boolean isDeniedFound(ParameterEvaluator eval) {
        boolean exists = false;

        if (eval.evaluationSucceeds() == false && eval.constraintIsAllow() == false) {
            exists = true;
        }

        return exists;
    }

    /**
     * Helper method to work with parameter evaluator to find, denied and not found in parameter value.
     * 
     * @param eval
     * @return
     */
    private boolean isDeniedNotFound(ParameterEvaluator eval) {
        boolean exists = false;

        if (eval.evaluationSucceeds() && eval.constraintIsAllow() == false) {
            exists = true;
        }

        return exists;
    }

    /**
     * @param useTaxIndicator
     * @param deliveryPostalCode
     * @param transactionTaxDate
     * @param item
     * @param itemUseTaxClass
     */
    private void calculateItemTax(boolean useTaxIndicator, 
                                  String deliveryPostalCode, 
                                  Date transactionTaxDate, 
                                  PurApItem item,
                                  Class itemUseTaxClass){
        
        if (!useTaxIndicator){
            KualiDecimal taxAmount = taxService.getTotalSalesTaxAmount(transactionTaxDate, deliveryPostalCode, item.getExtendedPrice());
            item.setItemTaxAmount(taxAmount);
        } else {
            List<TaxDetail> taxDetails = taxService.getUseTaxDetails(transactionTaxDate, deliveryPostalCode, item.getExtendedPrice());
            List<PurApItemUseTax> newUseTaxItems = new ArrayList<PurApItemUseTax>(); 
            if (taxDetails != null){
                for (TaxDetail taxDetail : taxDetails) {
                    try {
                        PurApItemUseTax useTaxitem = (PurApItemUseTax)itemUseTaxClass.newInstance();
                        useTaxitem.setChartOfAccountsCode(taxDetail.getChartOfAccountsCode());
                        useTaxitem.setFinancialObjectCode(taxDetail.getFinancialObjectCode());
                        useTaxitem.setAccountNumber(taxDetail.getAccountNumber());
                        useTaxitem.setItemIdentifier(item.getItemIdentifier());
                        useTaxitem.setRateCode(taxDetail.getRateCode());
                        useTaxitem.setTaxAmount(taxDetail.getTaxAmount());
                        newUseTaxItems.add(useTaxitem);
                    }
                    catch (Exception e) {
                        /**
                         * Shallow.. This never happen - InstantiationException/IllegalAccessException
                         * To be safe, throw a runtime exception
                         */
                        throw new RuntimeException(e);
                    } 
                }
            }
            item.setUseTaxItems(newUseTaxItems);
        }
    }

}

