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
package org.kuali.kfs.module.ar.batch.vo;

import java.util.HashSet;
import java.util.Set;

public class CustomerInvoiceWriteoffBatchVO {

    private String submittedByPrincipalName;
    private String submittedOn;
    private String note;
    private Set<String> invoiceNumbers;
    
    public CustomerInvoiceWriteoffBatchVO() {
        this.invoiceNumbers = new HashSet<String>();
        this.submittedOn = "Unknown";
    }

    public CustomerInvoiceWriteoffBatchVO(String submittedByPrincipalName) {
        invoiceNumbers = new HashSet<String>();
        this.submittedByPrincipalName = submittedByPrincipalName;
        this.submittedOn = "Unknown";
    }

    public CustomerInvoiceWriteoffBatchVO(String submittedByPrincipalName, String submittedOn) {
        invoiceNumbers = new HashSet<String>();
        this.submittedByPrincipalName = submittedByPrincipalName;
        this.submittedOn = submittedOn;
    }

    public void addInvoiceNumber(String invoiceNumber) {
        if (!invoiceNumbers.contains(invoiceNumber)) {
            invoiceNumbers.add(invoiceNumber);
        }
    }
    
    public String getSubmittedByPrincipalName() {
        return submittedByPrincipalName;
    }

    public void setSubmittedByPrincipalName(String submittedByPrincipalName) {
        this.submittedByPrincipalName = submittedByPrincipalName;
    }

    public String getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(String submittedOn) {
        this.submittedOn = submittedOn;
    }

    public Set<String> getInvoiceNumbers() {
        return invoiceNumbers;
    }

    public void setInvoiceNumbers(Set<String> invoiceNumbers) {
        this.invoiceNumbers = invoiceNumbers;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
