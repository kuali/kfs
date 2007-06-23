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

import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.bo.Status;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;


/**
 * Purchasing-Accounts Payable Document Interface
 */
public interface PurchasingAccountsPayableDocument extends AccountingDocument {
    
    /**
     * Convenience method to set vendor address fields based on a given VendorAddress.
     * 
     * @param vendorAddress
     */
    public void templateVendorAddress(VendorAddress vendorAddress);


    public List getStatusHistories();
    
    /**
     * This method adds to the document's status history collection an object of the
     * appropriate child of StatusHistory.
     * 
     * @param oldStatus             A code for the old status in String form
     * @param newStatus             A code for the new status in String form
     * @param statusHistoryNote     An optional BO Note for the StatusHistory (can be null)
     */
    public void addToStatusHistories(String oldStatus, String newStatus, Note statusHistoryNote);

    public void setStatusHistories(List statusHistories);

    public List<RequisitionView> getRelatedRequisitionViews();
    public List<CreditMemoView> getRelatedCreditMemoViews();
    public List<PaymentRequestView> getRelatedPaymentRequestViews();
    public List<PurchaseOrderView> getRelatedPurchaseOrderViews();
    public List<PurchasingApItem> getItems();
    
    public void addItem(PurchasingApItem item);
    
    public void setItems(List items);
    
    public void deleteItem(int lineNum);

    public void itemSwap(int position1, int position2);
    
    public int getItemLinePosition();
    
    public PurchasingApItem getItem(int pos);
    
    public KualiDecimal getTotalDollarAmount();
    
    public abstract Class getItemClass();
    public void renumberItems(int start);
    
    public Country getVendorCountry();
    public Status getStatus();
    public VendorDetail getVendorDetail();

    public String getVendorNumber();
    public void setVendorNumber(String vendorNumber);
    public Integer getVendorHeaderGeneratedIdentifier();
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier);
    public Integer getVendorDetailAssignedIdentifier();
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier);
    public String getVendorCustomerNumber();
    public void setVendorCustomerNumber(String vendorCustomerNumber);
    public Integer getPurapDocumentIdentifier();
    public void setPurapDocumentIdentifier(Integer identifier);
    public String getStatusCode();
    public void setStatusCode(String statusCode);
    public String getVendorCityName();
    public void setVendorCityName(String vendorCityName);
    public String getVendorCountryCode();
    public void setVendorCountryCode(String vendorCountryCode);
    public String getVendorLine1Address();
    public void setVendorLine1Address(String vendorLine1Address);
    public String getVendorLine2Address();
    public void setVendorLine2Address(String vendorLine2Address);
    public String getVendorName();
    public void setVendorName(String vendorName);
    public String getVendorPostalCode();
    public void setVendorPostalCode(String vendorPostalCode);
    public String getVendorStateCode();
    public void setVendorStateCode(String vendorStateCode);
    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier();
    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier);
    public Integer getVendorAddressGeneratedIdentifier();
    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier);

    /**
     * Gets the belowTheLineTypes attribute. 
     * @return Returns the belowTheLineTypes.
     */
    public String[] getBelowTheLineTypes();


    /**
     * Gets the summaryAccounts attribute this is used by the summary accounts method 
     * @return Returns the summaryAccounts.
     */
    public List<SourceAccountingLine> getSummaryAccounts();

    /**
     * Sets the summaryAccounts attribute value.
     * @param summaryAccounts The summaryAccounts to set.
     */
    public void setSummaryAccounts(List<SourceAccountingLine> summaryAccounts);
        

}
