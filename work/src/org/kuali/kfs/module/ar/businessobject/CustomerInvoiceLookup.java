/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class CustomerInvoiceLookup extends PersistableBusinessObjectBase {
    
    protected String invoiceNumber;
    protected String customerName;
    
    /*
    protected FinancialSystemDocumentHeader documentHeader;
    
    protected String invoiceHeaderText;
    protected String invoiceAttentionLineText;
    protected Date invoiceDueDate;
    protected Date billingDate;
    protected Date closedDate;
    protected Date billingDateForDisplay;
    protected String invoiceTermsText;
    protected String organizationInvoiceNumber;
    protected String customerPurchaseOrderNumber;
    protected String printInvoiceIndicator;
    protected Date customerPurchaseOrderDate;
    protected String billByChartOfAccountCode;
    protected String billedByOrganizationCode;
    protected Integer customerShipToAddressIdentifier;
    protected Integer customerBillToAddressIdentifier;
    protected String customerSpecialProcessingCode;
    protected boolean customerRecordAttachmentIndicator;
    protected boolean openInvoiceIndicator;
    protected String paymentChartOfAccountsCode;
    protected String paymentAccountNumber;
    protected String paymentSubAccountNumber;
    protected String paymentFinancialObjectCode;
    protected String paymentFinancialSubObjectCode;
    protected String paymentProjectCode;
    protected String paymentOrganizationReferenceIdentifier;
    protected Date printDate;
    protected Integer age;
    
    protected String billingAddressName;
    protected String billingCityName;
    protected String billingStateCode;
    protected String billingZipCode;
    protected String billingCountryCode;
    protected String billingAddressInternationalProvinceName;
    protected String billingInternationalMailCode;
    protected String billingEmailAddress;
    protected String billingAddressTypeCode;
    protected String billingLine1StreetAddress;
    protected String billingLine2StreetAddress;
    protected String shippingLine1StreetAddress;
    protected String shippingLine2StreetAddress;
    protected String shippingAddressName;
    protected String shippingCityName;
    protected String shippingStateCode;
    protected String shippingZipCode;
    protected String shippingCountryCode;
    protected String shippingAddressInternationalProvinceName;
    protected String shippingInternationalMailCode;
    protected String shippingEmailAddress;
    protected String shippingAddressTypeCode;
    protected boolean recurredInvoiceIndicator;
    protected Date reportedDate;
    */
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String,String> toStringMapper() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();      
        m.put("invoiceNumber", this.invoiceNumber);
        return m;
    }
    
    public int hashCode() {
        int hashCode = 0;

        if (getInvoiceNumber() != null) {
            hashCode = getInvoiceNumber().hashCode();
        }

        return hashCode;
    }

    /**
     * 
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * 
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    
}
