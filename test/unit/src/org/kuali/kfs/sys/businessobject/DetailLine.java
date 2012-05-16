/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class DetailLine extends TransientBusinessObjectBase {
    private String financialDocumentReferenceInvoiceNumber; //document number of the invoice being processed.
    private int invoiceSequenceNumber; //a unique number assigned to the invoice/payment processed.
    private String customerNumber; //customer number.
    private Date billingDate; //the date when the customer was billed.
    private String customerPaymentMediumCode; //Cash/Check/Credit. It will always be check for lockbox.
    
    /**
     * Gets the financialDocumentReferenceInvoiceNumber attribute.
     * 
     * @return Returns the financialDocumentReferenceInvoiceNumber
     * 
     */
    public String getFinancialDocumentReferenceInvoiceNumber() { 
        return financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Sets the financialDocumentReferenceInvoiceNumber attribute.
     * 
     * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
     * 
     */
    public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
        this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
    }


    /**
     * Gets the billingDate attribute.
     * 
     * @return Returns the billingDate
     * 
     */
    public Date getBillingDate() { 
        return billingDate;
    }

    /**
     * Sets the billingDate attribute.
     * 
     * @param billingDate The billingDate to set.
     * 
     */
    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }


    

    /**
     * Gets the customerPaymentMediumCode attribute.
     * 
     * @return Returns the customerPaymentMediumCode
     * 
     */
    public String getCustomerPaymentMediumCode() { 
        return customerPaymentMediumCode;
    }

    /**
     * Sets the customerPaymentMediumCode attribute.
     * 
     * @param customerPaymentMediumCode The customerPaymentMediumCode to set.
     * 
     */
    public void setCustomerPaymentMediumCode(String customerPaymentMediumCode) {
        if(!StringHelper.isNullOrEmpty(customerPaymentMediumCode) && customerPaymentMediumCode.equals("C")) {
             this.customerPaymentMediumCode = "CK";
         }
        
    }
    
    /**
     * Gets the invoiceSequenceNumber attribute.
     * 
     * @return Returns the invoiceSequenceNumber 
     * 
     */
    public int getInvoiceSequenceNumber() { 
        
        return invoiceSequenceNumber;
    }

    /**
     * Sets the invoiceSequenceNumber attribute.
     * 
     * @param invoiceSequenceNumber to set the invoice number  
     *  
     * 
     */
    public void setInvoiceSequenceNumber(int invoiceSequenceNumber) {
        this.invoiceSequenceNumber =  invoiceSequenceNumber;
    }
    
    
    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customerNumber
     * 
     */
    public String getCustomerNumber() { 
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     * 
     * @param customerNumber The customerNumber to set.
     * 
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    @SuppressWarnings("unchecked")
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pkMap = new LinkedHashMap();
        pkMap.put("InvoiceSequenceNumber", invoiceSequenceNumber);
        return pkMap;
    }


}
