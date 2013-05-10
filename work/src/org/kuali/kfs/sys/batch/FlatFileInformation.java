/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A holder of error messages and informative messages associated with a single logical file within a larger physical file
 */
public class FlatFileInformation {
    private String fileName;
    private List<String[]> messages;
    private Map<String, FlatFileTransactionInformation> flatFileIdentifierToTransactionInfomationMap;

    /**
     * Constructs a new FlatFileInformation
     */
    public FlatFileInformation() {
        messages = new ArrayList<String[]>();
        flatFileIdentifierToTransactionInfomationMap = new HashMap<String, FlatFileTransactionInformation>();
    }

    /**
     * Constructs a new FlatFileInformation
     * @param fileName the file name of the physical file with the encapsulated logical file therein
     */
    public FlatFileInformation(String fileName) {
        this.fileName = fileName;
        messages = new ArrayList<String[]>();
        flatFileIdentifierToTransactionInfomationMap = new HashMap<String, FlatFileTransactionInformation>();
    }

    /**
     * Adds the given messages about a transaction to the map of transaction messages if necessary and returns the first set of messages about a transaction put into the map
     * @param flatFileDataIdentifier the identifier of the transaction
     * @param flatFileTransactionInformation the messages about that transaction
     * @return the messages about the transaction, either just added or previously added
     */
    public FlatFileTransactionInformation getOrAddFlatFileData(String flatFileDataIdentifier, FlatFileTransactionInformation flatFileTransactionInformation) {
        if (!flatFileIdentifierToTransactionInfomationMap.containsKey(flatFileDataIdentifier)) {
            flatFileIdentifierToTransactionInfomationMap.put(flatFileDataIdentifier, flatFileTransactionInformation);
        }
        return flatFileIdentifierToTransactionInfomationMap.get(flatFileDataIdentifier);
    }

    /**
     * Adds an error message for this logical file
     * @param message the error message
     */
    public void addFileErrorMessage(String message) {
        this.messages.add(new String[] { FlatFileTransactionInformation.getEntryTypeString(FlatFileTransactionInformation.EntryType.ERROR), message });
    }

    /**
     * Adds an informative message for this logical file
     * @param message the informative message
     */
    public void addFileInfoMessage(String message) {
        this.messages.add(new String[] { FlatFileTransactionInformation.getEntryTypeString(FlatFileTransactionInformation.EntryType.INFO), message });
    }

    /**
     * @return the name of the physical file the logical file this holds messages for is associated with
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the physical file the logical file this holds messages for is associated with
     * @param fileName the name of the physical file the logical file this holds messages for is associated with
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the full List of messages associated with this logical file
     */
    public List<String[]> getMessages() {
        return messages;
    }

    /**
     * Sets the full List of messages associated with this logical file
     * @param messages the List of messages
     */
    public void setMessages(List<String[]> messages) {
        this.messages = messages;
    }

    /**
     * @return the map that associates the transaction identifier to its associated messages
     */
    public Map<String, FlatFileTransactionInformation> getFlatFileIdentifierToTransactionInfomationMap() {
        return flatFileIdentifierToTransactionInfomationMap;
    }

    /**
     * Sets the map that associates the transaction identifier to its associated messages
     * @param flatFileIdentifierToTransactionInfomationMap the map that associates the transaction identifier to its associated messages
     */
    public void setFlatFileIdentifierToTransactionInfomationMap(Map<String, FlatFileTransactionInformation> flatFileIdentifierToTransactionInfomationMap) {
        this.flatFileIdentifierToTransactionInfomationMap = flatFileIdentifierToTransactionInfomationMap;
    }
}
