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
package org.kuali.kfs.module.ar.batch.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.kuali.kfs.module.ar.batch.report.CustomerLoadResult.EntryType;

public class CustomerLoadFileResult {

    private String filename;
    private List<String[]> messages;
    
    //  key=customerName, value=CustomerLoadResult
    private Map<String,CustomerLoadResult> customers;
    
    public CustomerLoadFileResult() {
        customers = new TreeMap<String,CustomerLoadResult>();
        messages = new ArrayList<String[]>();
    }

    public CustomerLoadFileResult(String filename) {
        this.filename = filename;
        customers = new TreeMap<String,CustomerLoadResult>();
        messages = new ArrayList<String[]>();
    }
    
    public void addCustomerInfoMessage(String customerName, String message) {
        CustomerLoadResult customer = getOrAddCustomer(customerName);
        customer.addInfoMessage(message);
    }
    
    public void addCustomerErrorMessage(String customerName, String message) {
        CustomerLoadResult customer = getOrAddCustomer(customerName);
        customer.addErrorMessage(message);
    }
    
    public void setCustomerSuccessResult(String customerName) {
        CustomerLoadResult customer = getOrAddCustomer(customerName);
        customer.setSuccessResult();
    }
    
    public void setCustomerFailureResult(String customerName) {
        CustomerLoadResult customer = getOrAddCustomer(customerName);
        customer.setFailureResult();
    }
    
    public void setCustomerErrorResult(String customerName) {
        CustomerLoadResult customer = getOrAddCustomer(customerName);
        customer.setErrorResult();
    }
    
    public void setCustomerWorkflowDocId(String customerName, String workflowDocId) {
        CustomerLoadResult customer = getOrAddCustomer(customerName);
        customer.setWorkflowDocId(workflowDocId);
    }
    
    private CustomerLoadResult getOrAddCustomer(String customerName) {
        if (!customers.containsKey(customerName)) {
            customers.put(customerName, new CustomerLoadResult(filename, customerName));
        }
        return customers.get(customerName);
    }
    
    public String getFilename() {
        return filename;
    }

    public List<String[]> getMessages() {
        return messages;
    }
    
    public void addFileErrorMessage(String message) {
        this.messages.add(new String[] { CustomerLoadResult.getEntryTypeString(EntryType.ERROR), message });
    }
    
    public void addFileInfoMessage(String message) {
        this.messages.add(new String[] { CustomerLoadResult.getEntryTypeString(EntryType.INFO), message });
    }
    
    public Set<String> getCustomerNames() {
        return this.customers.keySet();
    }
    
    public CustomerLoadResult getCustomer(String customerName) {
        return customers.get(customerName);
    }
    
}
