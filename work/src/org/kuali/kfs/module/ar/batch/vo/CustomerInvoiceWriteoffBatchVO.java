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
package org.kuali.kfs.module.ar.batch.vo;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;

public class CustomerInvoiceWriteoffBatchVO {

    private transient DateTimeService dateTimeService;
    
    private String submittedByPrincipalId;
    private java.sql.Date submittedOn;
    private Set<String> invoiceNumbers;
    
    public CustomerInvoiceWriteoffBatchVO() {
        getDateTimeService();
        invoiceNumbers = new HashSet<String>();
        this.submittedOn = getDateTimeService().getCurrentSqlDate();
    }

    public CustomerInvoiceWriteoffBatchVO(String submittedByPrincipalId) {
        getDateTimeService();
        invoiceNumbers = new HashSet<String>();
        this.submittedByPrincipalId = submittedByPrincipalId;
        this.submittedOn = getDateTimeService().getCurrentSqlDate();
    }

    public CustomerInvoiceWriteoffBatchVO(String submittedByPrincipalId, java.sql.Date submittedOn) {
        getDateTimeService();
        invoiceNumbers = new HashSet<String>();
        this.submittedByPrincipalId = submittedByPrincipalId;
        this.submittedOn = submittedOn;
    }

    public void addInvoiceNumber(String invoiceNumber) {
        if (!invoiceNumbers.contains(invoiceNumber)) {
            invoiceNumbers.add(invoiceNumber);
        }
    }
    
    private DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }
    
    public String getSubmittedByPrincipalId() {
        return submittedByPrincipalId;
    }

    public void setSubmittedByPrincipalId(String submittedByPrincipalId) {
        this.submittedByPrincipalId = submittedByPrincipalId;
    }

    public java.sql.Date getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(java.sql.Date submittedOn) {
        this.submittedOn = submittedOn;
    }

    public Set<String> getInvoiceNumbers() {
        return invoiceNumbers;
    }

    public void setInvoiceNumbers(Set<String> invoiceNumbers) {
        this.invoiceNumbers = invoiceNumbers;
    }

}
