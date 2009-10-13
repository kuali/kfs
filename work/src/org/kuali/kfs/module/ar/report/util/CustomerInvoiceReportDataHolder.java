/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.report.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
/**
 * To group and hold the data presented to working reports of extract process
 */
public class CustomerInvoiceReportDataHolder {

    private Map<String, String> invoice;
    private Map<String, String> customer;
    private Map<String, String> sysinfo;
    private List<CustomerInvoiceDetail> details;
    
    
    private Map<String, Object> reportData;
    
    public final static String KEY_OF_INVOICE_ENTRY = "invoice";
    public final static String KEY_OF_CUSTOMER_ENTRY = "customer";
    public final static String KEY_OF_SYSINFO_ENTRY = "sysinfo";
    public final static String KEY_OF_DETAILS_ENTRY = "details";
    
    /**
     * Constructs a CustomerInvoiceReportDataHolder.java.
     */
    public CustomerInvoiceReportDataHolder() {
        
        this.invoice = new HashMap<String, String>();
        this.customer = new HashMap<String, String>();
        this.sysinfo = new HashMap<String, String>(); 
        this.details = new ArrayList<CustomerInvoiceDetail>();
 
        this.reportData = new HashMap<String, Object>();
    }


    /**
     * Gets the invoice attribute. 
     * @return Returns the invoice.
     */
    public Map<String, String> getInvoice() {
        return invoice;
    }

    /**
     * Sets the invoice attribute value.
     * @param invoice The invoice to set.
     */
    public void setInvoice(Map<String, String> invoice) {
        this.invoice = invoice;
    }

    /**
     * Gets the customer attribute. 
     * @return Returns the customer.
     */
    public Map<String, String> getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     * @param customer The customer to set.
     */
    public void setCustomer(Map<String, String> customer) {
        this.customer = customer;
    }

    /**
     * Gets the sysinfo attribute. 
     * @return Returns the sysinfo.
     */
    public Map<String, String> getSysinfo() {
        return sysinfo;
    }

    /**
     * Sets the sysinfo attribute value.
     * @param sysinfo The sysinfo to set.
     */
    public void setSysinfo(Map<String, String> sysinfo) {
        this.sysinfo = sysinfo;
    }

    /**
     * Gets the details attribute. 
     * @return Returns the details.
     */
    public List<CustomerInvoiceDetail> getDetails() {
        return details;
    }

    /**
     * Sets the details attribute value.
     * @param details The details to set.
     */
    public void setDetails(List<CustomerInvoiceDetail> details) {
        this.details = details;
    }

    
    
    /**
     * Gets the reportData attribute. 
     * @return Returns the reportData.
     */
    public Map<String, Object> getReportData() {        
        
        reportData.put(KEY_OF_INVOICE_ENTRY, invoice);
        reportData.put(KEY_OF_CUSTOMER_ENTRY, customer);
        reportData.put(KEY_OF_SYSINFO_ENTRY, sysinfo);
        reportData.put(KEY_OF_DETAILS_ENTRY, details);
        
        return reportData;
    }
    
   
    /**
     * Sets the reportData attribute value.
     * @param reportData The reportData to set.
     */
    public void setReportData(Map<String, Object> reportData) {
        this.reportData = reportData;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getReportData().toString();
    }
}
