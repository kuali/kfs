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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class CustomerInvoiceLookup extends PersistableBusinessObjectBase {
    
    protected String invoiceNumber;
    protected String customerName;    
    protected Date invoiceDueDate;
    protected Date billingDate;
    protected String billByChartOfAccountCode;
    protected String billedByOrganizationCode;
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap<String,String> toStringMapper() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();      
        m.put("invoiceNumber", this.invoiceNumber);
        return m;
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

    /**
     * 
     */
    public Date getInvoiceDueDate() {
        return invoiceDueDate;
    }

    /**
     * 
     */
    public void setInvoiceDueDate(Date invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    /**
     * 
     */
    public Date getBillingDate() {
        return billingDate;
    }

    /**
     * 
     */
    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    /**
     * 
     */
    public String getBillByChartOfAccountCode() {
        return billByChartOfAccountCode;
    }

    /**
     * 
     */
    public void setBillByChartOfAccountCode(String billByChartOfAccountCode) {
        this.billByChartOfAccountCode = billByChartOfAccountCode;
    }

    /**
     * 
     */
    public String getBilledByOrganizationCode() {
        return billedByOrganizationCode;
    }

    /**
     * 
     */
    public void setBilledByOrganizationCode(String billedByOrganizationCode) {
        this.billedByOrganizationCode = billedByOrganizationCode;
    }
    
}
