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

/**
 * 
 * Represents one customer object from a batch file, and its results.
 * 
 */
public class CustomerLoadResult {

    public enum ResultCode { SUCCESS, FAILURE, ERROR, INCOMPLETE }
    public enum EntryType { INFO, ERROR } 

    private String filename;
    private String customerName;
    private ResultCode result;
    private String workflowDocId;
    
    private List<String[]> messages;
    
    public CustomerLoadResult() {
        this.messages = new ArrayList<String[]>();
    }
    
    public CustomerLoadResult(String filename, String customerName) {
        this.filename = filename;
        this.customerName = customerName;
        this.result = ResultCode.INCOMPLETE;
        this.messages = new ArrayList<String[]>();
    }
    
    public static String getEntryTypeString(EntryType type) {
        String result = "UNKNOWN";
        switch (type) {
            case INFO: result = "INFO"; break;
            case ERROR: result = "ERROR"; break;
        }
        return result;
    }
    
    public static String getResultCodeString(ResultCode resultCode) {
        String result = "UNKNOWN";
        switch (resultCode) {
            case SUCCESS: result = "SUCCESS"; break;
            case FAILURE: result = "FAILURES"; break;
            case ERROR: result = "ERROR"; break;
            case INCOMPLETE: result = "INCOMPLETE"; break;
        }
        return result;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public ResultCode getResult() {
        return result;
    }
    
    public String getResultString() {
        return getResultCodeString(result);
    }
    
    private void setResult(ResultCode result) {
        this.result = result;
    }
    
    public void setSuccessResult() {
        this.result = ResultCode.SUCCESS;
    }
    
    public void setFailureResult() {
        this.result = ResultCode.FAILURE;
    }
    
    public void setErrorResult() {
        this.result = ResultCode.ERROR;
    }
    
    public String getWorkflowDocId() {
        return workflowDocId;
    }
    
    public void setWorkflowDocId(String workflowDocId) {
        this.workflowDocId = workflowDocId;
    }
    
    public List<String[]> getMessages() {
        return messages;
    }
    
    private void addMessage(EntryType entryType, String message) {
        this.messages.add(new String[] { getEntryTypeString(entryType), message });
    }
    
    public void addErrorMessage(String message) {
        addMessage(EntryType.ERROR, message);
    }
    
    public void addInfoMessage(String message) {
        addMessage(EntryType.INFO, message);
    }
    
}
