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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.util.collections.ManageableArrayList;
import org.kuali.core.bo.Note;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.bo.Status;
import org.kuali.module.purap.bo.StatusHistory;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;

/**
 * Purchasing-Accounts Payable Document Base
 */
public abstract class PurchasingAccountsPayableDocumentBase extends AccountingDocumentBase implements PurchasingAccountsPayableDocument, AmountTotaling {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableDocumentBase.class);

    // SHARED FIELDS BETWEEN REQUISITION, PURCHASE ORDER, PAYMENT REQUEST, AND CREDIT MEMO
    private Integer purapDocumentIdentifier;
    private String statusCode;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorCustomerNumber;
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorPostalCode;
    private String vendorCountryCode;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;

    // NOT PERSISTED IN DB
    private String vendorNumber;
    private Integer vendorAddressGeneratedIdentifier;

    // COMMON ELEMENTS
    protected List statusHistories;

    // COLLECTIONS
    private List<PurchasingApItem> items;
    private List<RequisitionView> relatedRequisitionViews;
    private List<PurchaseOrderView> relatedPurchaseOrderViews;
    private List<PaymentRequestView> relatedPaymentRequestViews;
    private List<CreditMemoView> relatedCreditMemoViews;
    private Map<SourceAccountingLine, List<PurchasingApItem>> summaryAccountsWithItems;
    private List<SourceAccountingLine> summaryAccountsWithItemsKey;
    private List<List<PurchasingApItem>> summaryAccountsWithItemsValue;
    
    private List<SourceAccountingLine> accountsForRouting;  //don't use me for anything else!!

    // REFERENCE OBJECTS
    private Status status;
    private VendorDetail vendorDetail;
    private Country vendorCountry;

    // STATIC
    public transient String[] belowTheLineTypes;

    // CONSTRUCTORS
    public PurchasingAccountsPayableDocumentBase() {
        items = new TypedArrayList(getItemClass());
        this.statusHistories = new ManageableArrayList();
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        refreshNonUpdateableReferences();
        SpringServiceLocator.getPurapAccountingService().updateAccountAmounts(this);
        super.prepareForSave(event);
    }

    /**
     * PURAP documents are all overriding this method to return false because sufficient funds
     * checking should not be performed on route of any PURAP documents.  Only the Purchase
     * Order performs a sufficient funds check and it is manually forced during routing.
     * 
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#documentPerformsSufficientFundsCheck()
     */
    @Override
    public boolean documentPerformsSufficientFundsCheck() {
        return false;
    }
    
    public void refreshAccountSummary() {
        //setSummaryAccounts(SpringServiceLocator.getPurapService().generateSummary(getItems()));
//        this.setSummaryAccountsWithItems(SpringServiceLocator.getPurapService().generateSummaryWithItems(getItems()));
    }

    /**
     * @see org.kuali.core.document.DocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        /* refreshNonUpdateableReferences needed below to update status reference object for 
         * document search results display of status description
         */
        refreshNonUpdateableReferences();
        SpringServiceLocator.getPurapAccountingService().updateAccountAmounts(this);
        refreshAccountSummary();
        
        //this is only temporary until we find out what's going on with refreshAccountSummary() (hjs)
        setAccountsForRouting(SpringServiceLocator.getPurapAccountingService().generateSummary(getItems()));
        
        super.populateDocumentForRouting();
    }

    protected void logAndThrowRuntimeException(String errorMessage) {
        this.logAndThrowRuntimeException(errorMessage, null);
    }
    
    protected void logAndThrowRuntimeException(String errorMessage, Exception e) {
        if (ObjectUtils.isNotNull(e)) {
            LOG.error(errorMessage,e);
            throw new RuntimeException(errorMessage,e);
        } else {
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
    
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getTotalDollarAmountAllItems();
    }

    public KualiDecimal getTotalDollarAmountAllItems() {
        return getTotalDollarAmountAllItems(null);
    }
    
    public KualiDecimal getTotalDollarAmountAllItems(String[] excludedTypes) {
        return getTotalDollarAmountWithExclusions(excludedTypes,true);
    }

    public KualiDecimal getTotalDollarAmountAboveLineItems() {
        return getTotalDollarAmountAboveLineItems(null);
    }
    
    public KualiDecimal getTotalDollarAmountAboveLineItems(String[] excludedTypes) {
        return getTotalDollarAmountWithExclusions(excludedTypes,false);
    }
    
    public KualiDecimal getTotalDollarAmountWithExclusions(String[] excludedTypes, boolean includeBelowTheLine) {
        if(excludedTypes==null) {
            excludedTypes=new String[]{};
        }

        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchasingApItem item : (List<PurchasingApItem>)getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            ItemType it = item.getItemType();
            if((includeBelowTheLine || it.isItemTypeAboveTheLineIndicator()) && 
                    !ArrayUtils.contains(excludedTypes,it.getItemTypeCode())) {
                KualiDecimal extendedPrice = item.getExtendedPrice();
                KualiDecimal itemTotal = (extendedPrice != null) ? extendedPrice : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#templateVendorAddress(org.kuali.module.vendor.bo.VendorAddress)
     */
    public void templateVendorAddress(VendorAddress vendorAddress) {
        if (vendorAddress == null) {
            return;
        }
        this.setVendorLine1Address(vendorAddress.getVendorLine1Address());
        this.setVendorLine2Address(vendorAddress.getVendorLine2Address());
        this.setVendorCityName(vendorAddress.getVendorCityName());
        this.setVendorStateCode(vendorAddress.getVendorStateCode());
        this.setVendorPostalCode(vendorAddress.getVendorZipCode());
        this.setVendorCountryCode(vendorAddress.getVendorCountryCode());
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("purapDocumentIdentifier", this.purapDocumentIdentifier);
        return m;
    }

    /**
     * This method is used to add a note to a Status History.
     * 
     * @param statusHistory
     * @param statusHistoryNote
     */
    protected void addStatusHistoryNote(StatusHistory statusHistory, Note note) {
        if (ObjectUtils.isNotNull(null)) {
            NoteService noteService = SpringServiceLocator.getNoteService();
            try {
                note = noteService.createNote(note, statusHistory);
                noteService.save(note);
            }
            catch (Exception e) {
                logAndThrowRuntimeException("Unable to create or save status history note " + e.getMessage(), e);
            }
        }
    }

    public String getVendorNumber() {
        if (StringUtils.isNotEmpty(vendorNumber)){
            return vendorNumber;
        }
        else if (ObjectUtils.isNotNull(vendorDetail)) {
            return vendorDetail.getVendorNumber();
        }else  return "";
        }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    // GETTERS AND SETTERS
    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorHeaderGeneratedIdentifier
     */
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the vendorDetailAssignedIdentifier
     */
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute.
     * 
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    /**
     * Gets the vendorCustomerNumber attribute.
     * 
     * @return Returns the vendorCustomerNumber
     */
    public String getVendorCustomerNumber() {
        return vendorCustomerNumber;
    }

    /**
     * Sets the vendorCustomerNumber attribute.
     * 
     * @param vendorCustomerNumber The vendorCustomerNumber to set.
     */
    public void setVendorCustomerNumber(String vendorCustomerNumber) {
        this.vendorCustomerNumber = vendorCustomerNumber;
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer identifier) {
        this.purapDocumentIdentifier = identifier;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List getStatusHistories() {
        return statusHistories;
    }

    public void setStatusHistories(List statusHistories) {
        this.statusHistories = statusHistories;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * Gets the items attribute.
     * 
     * @return Returns the items.
     */
    public List getItems() {
        return items;
    }

    /**
     * Sets the items attribute value.
     * 
     * @param items The items to set.
     */
    public void setItems(List items) {
        this.items = items;
    }

    /*
     * public void addItem(PurchasingApItem item) { int itemLinePosition = items.size(); if(item.getItemLineNumber()!=null) {
     * itemLinePosition = item.getItemLineNumber().intValue()-1; } //if the user entered something set line number to that //
     * if(itemLinePosition>0&&itemLinePosition<items.size()) { // itemLinePosition = itemLinePosition - 1; // } else
     * if(itemLinePosition>items.size()) { itemLinePosition=items.size(); } items.add(itemLinePosition,item);
     * renumberItems(itemLinePosition); }
     */

    public void addItem(PurchasingApItem item) {
        int itemLinePosition = getItemLinePosition();
        if (ObjectUtils.isNotNull(item.getItemLineNumber()) && (item.getItemLineNumber() > 0) && (item.getItemLineNumber() <= itemLinePosition)) {
            itemLinePosition = item.getItemLineNumber().intValue() - 1;
        }
        items.add(itemLinePosition, item);
        renumberItems(itemLinePosition);
    }

    public void deleteItem(int lineNum) {
        if (items.remove(lineNum) == null) {
            // throw error here
        }
        renumberItems(lineNum);
    }

    public void renumberItems(int start) {
        for (int i = start; i < items.size(); i++) {
            PurchasingApItem item = (PurchasingApItem) items.get(i);
            // only set the item line number for above the line items
            item.refreshNonUpdateableReferences();
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                item.setItemLineNumber(new Integer(i + 1));
            }
        }
    }
    /**
     *  swap two items based on item line numbers (which are one higher than item position in list
     * @param position1
     * @param position2
     */
    public void itemSwap(int positionFrom, int positionTo) {
        //if out of range do nothing
        if((positionTo <0) ||
           (positionTo>=getItemLinePosition())) {
            return;
        }
        PurchasingApItem item1 = this.getItem(positionFrom);
        PurchasingApItem item2 = this.getItem(positionTo);
        Integer oldFirstPos = item1.getItemLineNumber();
        //swap line numbers
        item1.setItemLineNumber(item2.getItemLineNumber());
        item2.setItemLineNumber(oldFirstPos);
        //fix ordering in list
        items.remove(positionFrom);
        items.add(positionTo,item1);
    }

    /**
     * This method helps to determine the item line position if the user did not specify the line number on an above the line items
     * before clicking on the add button. It subtracts the number of the below the line items on the list with the total item list
     * size.
     * 
     * @return int the item line position of the last(highest) line number of above the line items.
     */
    public int getItemLinePosition() {
        int belowTheLineCount = 0;
        for (PurchasingApItem item : items) {
            item.refreshNonUpdateableReferences();
            if (!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                belowTheLineCount++;
            }
        }
        return items.size() - belowTheLineCount;
    }

    public PurchasingApItem getItem(int pos) {
        // TODO: we probably don't need this because of the TypedArrayList
        while (getItems().size() <= pos) {

            try {
                getItems().add(getItemClass().newInstance());
            }
            catch (InstantiationException e) {
                logAndThrowRuntimeException("Unable to instantiate class", e);
            }
            catch (IllegalAccessException e) {
                logAndThrowRuntimeException("Unable to get class", e);
            }
            catch (NullPointerException e) {
                logAndThrowRuntimeException("Can't instantiate Purchasing Item from base", e);
            }
        }
        return (PurchasingApItem) items.get(pos);
    }
    
    /**
     * Iterates through the items of the document and returns the item with
     * the line number equal to the number given, or null if a match is not found.
     * 
     * @param lineNumber - line number to match on
     * @return PurchasingApItem - if a match was found, or null
     */
    public PurchasingApItem getItemByLineNumber(int lineNumber) {
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            PurchasingApItem item = (PurchasingApItem) iter.next();

            if (item.getItemLineNumber().intValue() == lineNumber) {
                return item;
            }
        }
        return null;
    }
    
    public abstract Class getItemClass();
    
    public abstract PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible();

    public abstract String getPurApSourceDocumentLabelIfPossible();

//    /**
//     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLines()
//     */
//    @Override
//    public List getSourceAccountingLines() {
//        
//        return SpringServiceLocator.getPurapAccountingService().generateSummary(this.getItems());
//
//        // TODO: Chris loop through items and get accounts
////        TypedArrayList accounts = null;
////        if (items.size() >= 1) {
////            accounts = new TypedArrayList(getItem(0).getAccountingLineClass());
////        }
////        for (PurchasingApItem item : items) {
////            accounts.addAll(item.getSourceAccountingLines());
////        }
////        return (accounts == null) ? new ArrayList() : accounts;
//    }

    public String getVendorCityName() {
        return vendorCityName;
    }

    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorPostalCode() {
        return vendorPostalCode;
    }

    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }

    public String getVendorStateCode() {
        return vendorStateCode;
    }

    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    /**
     * Gets the accountsPayablePurchasingDocumentLinkIdentifier attribute.
     * 
     * @return Returns the accountsPayablePurchasingDocumentLinkIdentifier.
     */
    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    /**
     * Sets the accountsPayablePurchasingDocumentLinkIdentifier attribute value.
     * 
     * @param accountsPayablePurchasingDocumentLinkIdentifier The accountsPayablePurchasingDocumentLinkIdentifier to set.
     */
    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public List<RequisitionView> getRelatedRequisitionViews() {
        if (!(this instanceof RequisitionDocument) && relatedRequisitionViews == null) {
            relatedRequisitionViews = new TypedArrayList(RequisitionView.class);
            List<RequisitionView> tmpViews = SpringServiceLocator.getPurapService().getRelatedViews(RequisitionView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (RequisitionView view : tmpViews) {
                relatedRequisitionViews.add(view);
            }
        }
        return relatedRequisitionViews;
    }

    public List<CreditMemoView> getRelatedCreditMemoViews() {
        if (!(this instanceof CreditMemoDocument) && relatedCreditMemoViews == null) {
            relatedCreditMemoViews = new TypedArrayList(CreditMemoView.class);
            List<CreditMemoView> tmpViews = SpringServiceLocator.getPurapService().getRelatedViews(CreditMemoView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (CreditMemoView view : tmpViews) {
                relatedCreditMemoViews.add(view);
            }
        }
        return relatedCreditMemoViews;
    }

    public List<PaymentRequestView> getRelatedPaymentRequestViews() {
        if (!(this instanceof PaymentRequestDocument) && relatedPaymentRequestViews == null) {
            relatedPaymentRequestViews = new TypedArrayList(PaymentRequestView.class);
            List<PaymentRequestView> tmpViews = SpringServiceLocator.getPurapService().getRelatedViews(PaymentRequestView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (PaymentRequestView view : tmpViews) {
                relatedPaymentRequestViews.add(view);
            }
        }
        return relatedPaymentRequestViews;
    }

    public List<PurchaseOrderView> getRelatedPurchaseOrderViews() {
        if (!(this instanceof PurchaseOrderDocument) && relatedPurchaseOrderViews == null) {
            relatedPurchaseOrderViews = new TypedArrayList(PurchaseOrderView.class);
            List<PurchaseOrderView> tmpViews = SpringServiceLocator.getPurapService().getRelatedViews(PurchaseOrderView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (PurchaseOrderView view : tmpViews) {
                relatedPurchaseOrderViews.add(view);
            }
        }
        return relatedPurchaseOrderViews;
    }

    /**
     * Gets the belowTheLineTypes attribute.
     * 
     * @return Returns the belowTheLineTypes.
     */
    public String[] getBelowTheLineTypes() {
        if (this.belowTheLineTypes == null) {
            this.belowTheLineTypes = SpringServiceLocator.getPurapService().getBelowTheLineForDocument(this);
        }
        return belowTheLineTypes;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */

    public Map<SourceAccountingLine, List<PurchasingApItem>> getSummaryAccountsWithItems() {
        return summaryAccountsWithItems;
    }

    public void setSummaryAccountsWithItems(Map<SourceAccountingLine, List<PurchasingApItem>> summaryAccountsWithItems) {
        this.summaryAccountsWithItems = summaryAccountsWithItems;
        getSummaryAccountsWithItemsKey();
        getSummaryAccountsWithItemsValue();
    }

    public List<SourceAccountingLine> getSummaryAccountsWithItemsKey() {
        if (this.summaryAccountsWithItemsKey == null || summaryAccountsWithItemsKey.size() == 0) {
            summaryAccountsWithItemsKey = new ArrayList();
            for (Iterator keyIter = summaryAccountsWithItems.keySet().iterator(); keyIter.hasNext();) {
                summaryAccountsWithItemsKey.add((SourceAccountingLine)keyIter.next());
            }
        }
        return summaryAccountsWithItemsKey;
    }

    public void setSummaryAccountsWithItemsKey(List<SourceAccountingLine> summaryAccountsWithItemsKey) {
        this.summaryAccountsWithItemsKey = summaryAccountsWithItemsKey;
    }

    public List<List<PurchasingApItem>> getSummaryAccountsWithItemsValue() {
        if (this.summaryAccountsWithItemsValue == null) {
            summaryAccountsWithItemsValue = new ArrayList();
            for (Iterator iter = summaryAccountsWithItems.values().iterator(); iter.hasNext();) {
                summaryAccountsWithItemsValue.add((List<PurchasingApItem>)iter.next());
            }
        }
        return summaryAccountsWithItemsValue;
    }

    public List<PurchasingApItem> getSummaryAccountsWithItemsValue(int index) {
        if (this.summaryAccountsWithItemsValue == null) {
            summaryAccountsWithItemsValue = getSummaryAccountsWithItemsValue();
        }
        if (summaryAccountsWithItemsValue.size() > index-1) {
            return summaryAccountsWithItemsValue.get(index);
        }
        else {
            return null;
        }
    }
    
    public void setSummaryAccountsWithItemsValue(List<List<PurchasingApItem>> summaryAccountsWithItemsValue) {
        this.summaryAccountsWithItemsValue = summaryAccountsWithItemsValue;
    }

    public Country getVendorCountry() {
        return vendorCountry;
    }

    protected boolean documentWillStopInRouteNode(String routeNodeName) {
        populateDocumentForRouting();
        return false;
        // return
        // SpringServiceLocator.getWorkflowInfoService().routeNodeHasApproverActionRequest(this.getDocumentHeader().getWorkflowDocument().getDocumentType(),
        // getDocumentHeader().getWorkflowDocument().getApplicationContent(), routeNodeName);
    }

    public List<SourceAccountingLine> getAccountsForRouting() {
        return accountsForRouting;
    }

    public void setAccountsForRouting(List<SourceAccountingLine> accountsForRouting) {
        this.accountsForRouting = accountsForRouting;
    }
}
