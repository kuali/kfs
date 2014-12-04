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
