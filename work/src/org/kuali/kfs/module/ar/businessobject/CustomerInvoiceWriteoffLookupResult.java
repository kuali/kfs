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
package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class CustomerInvoiceWriteoffLookupResult extends TransientBusinessObjectBase{

    private String customerName;
    private String customerNumber;
    private String customerTypeCode;
    private String customerInvoiceNumber;
    private String customerNote;
    private Integer age;
    private KualiDecimal customerTotal;
    private List<CustomerInvoiceDocument> customerInvoiceDocuments;

    private Customer customer;

    public CustomerInvoiceWriteoffLookupResult(){
        customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
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
        customerInvoiceDocumentAttributesForDisplay.add(KFSPropertyConstants.DOCUMENT_NUMBER);
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
