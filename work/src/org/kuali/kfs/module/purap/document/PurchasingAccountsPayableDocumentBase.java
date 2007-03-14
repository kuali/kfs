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

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.purap.bo.PurApItemBase;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.SourceDocumentReference;
import org.kuali.module.purap.bo.Status;
import org.kuali.module.purap.bo.StatusHistory;
import org.kuali.module.purap.bo.VendorDetail;

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

    // COMMON ELEMENTS
    protected List<StatusHistory> statusHistories;
    protected List<SourceDocumentReference> sourceDocumentReferences;
    // COLLECTIONS
    private List<PurchasingApItem> items;
    
    // REFERENCE OBJECTS
    private Status status;
    private VendorDetail vendorDetail;

    // CONSTRUCTORS
    public PurchasingAccountsPayableDocumentBase() {
        items = new TypedArrayList(PurApItemBase.class);
        this.statusHistories = new TypedArrayList( StatusHistory.class );
    }
    
    public KualiDecimal getTotalDollarAmount() {
        //FIXME get real total
//        return Constants.ZERO;
        return new KualiDecimal(100);
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

    public List<StatusHistory> getStatusHistories() {
        return statusHistories;
    }

    public void setStatusHistories(List<StatusHistory> statusHistories) {
        this.statusHistories = statusHistories;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
}
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public List<SourceDocumentReference> getSourceDocumentReferences() {
        return sourceDocumentReferences;
}
    public void setSourceDocumentReferences(List<SourceDocumentReference> sourceDocumentReferences) {
        this.sourceDocumentReferences = sourceDocumentReferences;
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

    public void addItem(PurchasingApItem item) {
        int itemLinePosition = items.size();
        if(item.getItemLineNumber()!=null) {
            itemLinePosition = item.getItemLineNumber().intValue();
        }
       
        //if the user entered something set line number to that
        if(itemLinePosition>0&&itemLinePosition<items.size()) {
            itemLinePosition = item.getItemLineNumber() - 1;
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
            item.setItemLineNumber(new Integer(i+1));
        }
    }
    
    public PurchasingApItem getItem(int pos) {
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
    public KualiDecimal getTotal() {
        KualiDecimal total = new KualiDecimal("0");
        for (PurchasingApItem item : items) {
           total = total.add(item.getExtendedPrice());
       }
       return total;
    }

    public Class getItemClass() {
        //should we throw unimplemented method here
        return null;
    }
}
