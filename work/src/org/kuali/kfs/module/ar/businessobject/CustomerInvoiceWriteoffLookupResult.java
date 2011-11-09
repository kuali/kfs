/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class CustomerInvoiceWriteoffLookupResult extends TransientBusinessObjectBase{
    
    private String customerName;
    private String customerNumber;
    private String customerTypeCode;
    private String collectionStatus;
    private String customerInvoiceNumber;
    private String customerNote;
    private Integer age;
    private KualiDecimal customerTotal;
    private List<CustomerInvoiceDocument> customerInvoiceDocuments;    
    
    public CustomerInvoiceWriteoffLookupResult(){
        customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
    }
    
    public String getCollectionStatus() {
        return collectionStatus;
    }
    public void setCollectionStatus(String collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

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
    
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    
    
    public String getCustomerNote() {
        return customerNote;
    }
    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }
    public List<String> getCustomerInvoiceDocumentAttributesForDisplay(){
        List<String> customerInvoiceDocumentAttributesForDisplay = new ArrayList<String>();
        customerInvoiceDocumentAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER);
        customerInvoiceDocumentAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.AGE);
        customerInvoiceDocumentAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE);
        customerInvoiceDocumentAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.SOURCE_TOTAL);
        customerInvoiceDocumentAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.OPEN_AMOUNT);
        
        return customerInvoiceDocumentAttributesForDisplay;
    }
    
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument(int index){
        return getCustomerInvoiceDocuments().get(index);
    }
}
