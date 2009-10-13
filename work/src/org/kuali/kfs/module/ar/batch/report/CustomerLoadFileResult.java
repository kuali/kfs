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
