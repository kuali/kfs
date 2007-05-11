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
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.util.collections.ManageableArrayList;
import org.kuali.core.bo.Note;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.bo.Status;
import org.kuali.module.purap.bo.StatusHistory;
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

    // COMMON ELEMENTS
    protected List statusHistories;

    // COLLECTIONS
    private List<PurchasingApItem> items;
    private List<RequisitionView> relatedRequisitionViews;
    private List<PurchaseOrderView> relatedPurchaseOrderViews;
    private List<PaymentRequestView> relatedPaymentRequestViews;
    private List<CreditMemoView> relatedCreditMemoViews;
    private List<SourceAccountingLine> summaryAccounts;

    // REFERENCE OBJECTS
    private Status status;
    private VendorDetail vendorDetail;

    // STATIC
    public transient String [] belowTheLineTypes;
    
    // CONSTRUCTORS
    public PurchasingAccountsPayableDocumentBase() {
        items = new TypedArrayList(getItemClass());
        this.statusHistories = new ManageableArrayList();
    }
    
    public KualiDecimal getTotalDollarAmount() {
        //TODO: the easy total dollar amount for now (i.e. not taking into account inactive etc
        //do more analysis and make better
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchasingApItem item : items) {
            KualiDecimal itemTotal = (item.getExtendedPrice()==null)?KualiDecimal.ZERO:item.getExtendedPrice();
            total = total.add(itemTotal);
        }
        return total;
    }

    /**
     * Retrieve all references common to purchasing and ap
     */
    public void refreshAllReferences() {
        this.refreshReferenceObject("status");
        this.refreshReferenceObject("vendorDetail");
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
    protected void addStatusHistoryNote( StatusHistory statusHistory, Note note ) {
        if( ObjectUtils.isNotNull( null ) ) {
            NoteService noteService = SpringServiceLocator.getNoteService();
            try {
                note = noteService.createNote( note, statusHistory );
                noteService.save( note );
            } catch( Exception e ) {
                LOG.error("Unable to create or save status history note " + e.getMessage());
                throw new RuntimeException("Unable to create or save status history note " + e.getMessage());
            }
        }
    }

    public String getVendorNumber() {
        if (ObjectUtils.isNotNull(vendorDetail)) {
            return vendorDetail.getVendorNumber();
        }
        return "";
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    // GETTERS AND SETTERS
    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorHeaderGeneratedIdentifier
     * 
     */
    public Integer getVendorHeaderGeneratedIdentifier() { 
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     * 
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the vendorDetailAssignedIdentifier
     * 
     */
    public Integer getVendorDetailAssignedIdentifier() { 
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute.
     * 
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     * 
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    /**
     * Gets the vendorCustomerNumber attribute.
     * 
     * @return Returns the vendorCustomerNumber
     * 
     */
    public String getVendorCustomerNumber() { 
        return vendorCustomerNumber;
    }

    /**
     * Sets the vendorCustomerNumber attribute.
     * 
     * @param vendorCustomerNumber The vendorCustomerNumber to set.
     * 
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
     * @return Returns the items.
     */
    public List getItems() {
        return items;
    }

    /**
     * Sets the items attribute value.
     * @param items The items to set.
     */
    public void setItems(List items) {
        this.items = items;
    }

    /*
    public void addItem(PurchasingApItem item) {
        int itemLinePosition = items.size();
        if(item.getItemLineNumber()!=null) {
            itemLinePosition = item.getItemLineNumber().intValue()-1;
        }
       
        //if the user entered something set line number to that
//        if(itemLinePosition>0&&itemLinePosition<items.size()) {
//            itemLinePosition = itemLinePosition - 1;
//        }
        else if(itemLinePosition>items.size()) {
            itemLinePosition=items.size();
        }
        
        items.add(itemLinePosition,item);
        renumberItems(itemLinePosition);
    }
    */
    
    public void addItem(PurchasingApItem item) {
        int itemLinePosition = getItemLinePosition();
        if(ObjectUtils.isNotNull(item.getItemLineNumber())) {
            itemLinePosition = item.getItemLineNumber().intValue()-1;
        }
        items.add(itemLinePosition,item);
        renumberItems(itemLinePosition);
    }
    
    public void deleteItem(int lineNum) {
        if(items.remove(lineNum)==null) {
            //throw error here
        }
        renumberItems(lineNum);
    }
    
    public void renumberItems(int start) {
        for (int i = start; i<items.size(); i++) {
            PurchasingApItem item = (PurchasingApItem)items.get(i);
            //only set the item line number for above the line items
            item.refreshNonUpdateableReferences();
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                item.setItemLineNumber(new Integer(i+1));
            }
        }
    }
    
    /** 
     * 
     * This method helps to determine the item line position if the user
     * did not specify the line number on an above the line items before
     * clicking on the add button. It subtracts the number of the below
     * the line items on the list with the total item list size.
     * 
     * @return int the item line position of the last(highest) line number of above
     *         the line items.
     */
    private int getItemLinePosition() {
        int belowTheLineCount = 0;
        for (PurchasingApItem item: items) {
            item.refreshNonUpdateableReferences();
            if (!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                belowTheLineCount++;
            }
        }
        return items.size() - belowTheLineCount;
    }
    
    public PurchasingApItem getItem(int pos) {
        //TODO: we probably don't need this because of the TypedArrayList
        while (getItems().size() <= pos) {
            
            try {
                getItems().add(getItemClass().newInstance());
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Unable to get class");
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to get class");
            }
            catch (NullPointerException e) {
                throw new RuntimeException("Can't instantiate Purchasing Item from base");
            }
        }
        return (PurchasingApItem)items.get(pos);
    }
    
    public abstract Class getItemClass();

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLines()
     */
    @Override
    public List getSourceAccountingLines() {
        //TODO: Chris loop through items and get accounts
        TypedArrayList accounts = null;
        if(items.size()>=1) {
            accounts = new TypedArrayList(getItem(0).getAccountingLineClass());
        }
        for (PurchasingApItem item : items) {
            accounts.addAll(item.getSourceAccountingLines());
        }
        return (accounts==null)?new ArrayList():accounts;
    }

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
    
    /**
     * Gets the accountsPayablePurchasingDocumentLinkIdentifier attribute. 
     * @return Returns the accountsPayablePurchasingDocumentLinkIdentifier.
     */
    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    /**
     * Sets the accountsPayablePurchasingDocumentLinkIdentifier attribute value.
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
     * @return Returns the belowTheLineTypes.
     */
    public String[] getBelowTheLineTypes() {
        if(this.belowTheLineTypes==null) {
            this.belowTheLineTypes = SpringServiceLocator.getPurapService().getBelowTheLineForDocument(this);
        }
        return belowTheLineTypes;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
//    @Override
//    public abstract Class getSourceAccountingLineClass();

    /**
     * Gets the summaryAccounts attribute this is used by the summary accounts method 
     * @return Returns the summaryAccounts.
     */
    public List<SourceAccountingLine> getSummaryAccounts() {
        return summaryAccounts;
    }

    /**
     * Sets the summaryAccounts attribute value.
     * @param summaryAccounts The summaryAccounts to set.
     */
    public void setSummaryAccounts(List<SourceAccountingLine> summaryAccounts) {
        this.summaryAccounts = summaryAccounts;
    }
        
}
