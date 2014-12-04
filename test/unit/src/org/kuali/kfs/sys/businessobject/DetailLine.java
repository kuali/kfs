/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
