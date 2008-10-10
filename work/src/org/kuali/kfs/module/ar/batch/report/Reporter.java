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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * 
 * Contains, formats, and returns the reporting text and logging 
 * text for the Customer Load batch processing.
 * 
 */
public class Reporter {

    private static final String BATCH_FILENAME = "BATCHJOB";
    private static final String BATCH_RECORDNAME = "BATCHRECORD";
    
    // extra index of all customerNames/records, along with the file it came from as value
    // key = customerName/record, Value = fileName
    private Map<String,String> recordsIndex;
    
    // OUTER: key=fileName   INNER: key=customerName/record, value=List of CustomerLoadReportEntry's
    private Map<String, Map<String, List<ReportEntry>>> entries;
    
    private List<OutputStream> outStreams;
    
    private DateTimeService dateTimeService;
    private int status; // 0 = closed, 1 = open
    
    public Reporter() {
        entries = new TreeMap<String, Map<String, List<ReportEntry>>>();
        recordsIndex = new TreeMap<String,String>();
        outStreams = new ArrayList<OutputStream>();
        status = 1;
    }
    
    public void addInfoEntry(String filename, String customerName, String message) {
        addEntry(filename, customerName, ReportEntry.EntryTypes.INFO, message);
    }
    
    public void addBatchEntry(String filename, String customerName, String message) {
        addEntry(filename, customerName, ReportEntry.EntryTypes.BATCH, message);
    }
    
    public void addErrorEntry(String filename, String customerName, String message) {
        addEntry(filename, customerName, ReportEntry.EntryTypes.ERROR, message);
    }
    
    private void addEntry(String filename, String customerName, ReportEntry.EntryTypes type, String message) {
        addEntry(filename, customerName, type, nowTimestamp(), message);
    }
    
    private void addEntry(String filename, String customerName, ReportEntry.EntryTypes type, java.sql.Date timestamp, String message) {
        
        //  validations
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("The parameter [filename] must not be null or blank.");
        }
        if (StringUtils.isBlank(customerName)) {
            throw new IllegalArgumentException("The parameter [customerName] must not be null or blank.");
        }
        if (StringUtils.isBlank(message)) {
            throw new IllegalArgumentException("The parameter [message] must not be null or blank.");
        }
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] must not be null.");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("The parameter [timestamp] must not be null.");
        }
        
        //  disallow adding new entries once the reporter is closed
        if (status == 0) {
            throw new IllegalArgumentException("This reporter has been closed, and new entries cannot be added to it.");
        }
        
        //  create the new entry
        ReportEntry entry = new ReportEntry(filename, customerName, type, timestamp, message);
        
        //  add the filename entry if necessary
        if (!entries.containsKey(filename)) {
            entries.put(filename, new TreeMap<String, List<ReportEntry>>());
        }
        
        //  get a reference to the map of entries for this file
        Map<String,List<ReportEntry>> fileEntries = entries.get(filename);
        
        //  add the customerName record to this fileEntry if necessary
        if (!fileEntries.containsKey(customerName)) {
            fileEntries.put(customerName, new ArrayList<ReportEntry>());
        }
        
        //  get a reference to the list of entries for this customerName record
        List<ReportEntry> customerEntries = fileEntries.get(customerName);
        
        //  add the entry
        customerEntries.add(entry);
        
        //  add to the output stream
        byte[] bytes = entry.toString().getBytes();
        for (OutputStream outputStream : outStreams) {
            try {
                outputStream.write(bytes);
            }
            catch (IOException e) {
                throw new RuntimeException("IOException occurred while trying to write to the buffer.", e);
            }
        }
        
    }
    
    public void close() {
        addEndEntry();
        status = 0;
    }
    
    public void start() {
        addStartEntry();
    }

    /**
     * 
     * Adds a writer to the reporter for logging purposes.
     * @param writer Should be a buffered writer.
     */
    public void addOutputStream(OutputStream outputStream) {
        outStreams.add(outputStream);
    }
    
    /**
     * 
     * Adds a list of writers to the reporter for logging purposes.
     * @param writers Should be buffered writers.
     */
    public void addOutputStreams(List<OutputStream> outStreams) {
        this.outStreams.addAll(outStreams);
    }
    
    public void flush() {
        for (OutputStream outStream : outStreams) {
            try {
                outStream.flush();
            }
            catch (IOException e) {
                throw new RuntimeException("An IOException occurred while trying to flush this writer.", e);
            }
        }
    }
    
    private java.sql.Date nowTimestamp() {
        return dateTimeService.getCurrentSqlDate();
    }
    
    private void addStartEntry() {
        addEntry(BATCH_FILENAME, BATCH_RECORDNAME, ReportEntry.EntryTypes.BATCH, "Starting Customer Load Batch Process");
    }
    
    private void addEndEntry() {
        addEntry(BATCH_FILENAME, BATCH_RECORDNAME, ReportEntry.EntryTypes.BATCH, "Exiting Customer Load Batch Process");
    }
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
}
