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
package org.kuali.kfs.module.ar.batch.report;


public class ReportEntry {

    private String filename;
    private String customerName;
    private EntryTypes type;
    private java.sql.Date timestamp;
    private String message;
    
    public enum EntryTypes { INFO, ERROR, BATCH } 
    
    public ReportEntry(String filename, String customerName, EntryTypes type, java.sql.Date timestamp, String message) {
        this.filename = filename;
        this.customerName = customerName;
        this.type = type;
        this.timestamp = timestamp;
        this.message = message;
    }
    
    public String getFilename() { return filename; }
    public String getCustomerName() { return customerName; }
    public EntryTypes getType() { return type; }
    public java.sql.Date getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    
    public String entryTypeString(EntryTypes type) {
        String result = "UNKNOWN";
        switch (type) {
            case INFO: result = "INFO";
            case ERROR: result = "ERROR";
            case BATCH: result = "BATCH";
        }
        return result;
    }
    
    public String toString() {
        return "[" + filename + "] {" + customerName + "} " + timestamp + " - " + entryTypeString(type) + " - " + message;
    }
    
}
