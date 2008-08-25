/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class CustomerInvoiceWriteoffLookupResult extends TransientBusinessObjectBase{
    
    private String customerName;
    private String customerNumber;
    private String customerTypeCode;
    private String customerInvoiceNumber;
    private KualiDecimal customerTotal;
    private List<CustomerInvoiceDocument> customerInvoiceDocuments;
    
    public KualiDecimal getCustomerTotal() {
        return customerTotal;
    }
    public void setCustomerTotal(KualiDecimal customerTotal) {
        this.customerTotal = customerTotal;
    }

    public List<CustomerInvoiceDocument> getCustomerInvoiceDocuments() {
        return customerInvoiceDocuments;
    }
    public void setCustomerInvoiceDocuments(List<CustomerInvoiceDocument> customerInvoiceDocuments) {
        this.customerInvoiceDocuments = customerInvoiceDocuments;
    }
    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerNumber() {
        return customerNumber;
    }
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
    public String getCustomerTypeCode() {
        return customerTypeCode;
    }
    public void setCustomerType(String customerType) {
        this.customerTypeCode = customerType;
    }

    public String getCustomerInvoiceNumber() {
        return customerInvoiceNumber;
    }
    public void setCustomerInvoiceNumber(String customerInvoiceNumber) {
        this.customerInvoiceNumber = customerInvoiceNumber;
    }
    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }
}
