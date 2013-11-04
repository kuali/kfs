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
import java.util.List;

/**
 * This class represents a full file which can be separated into a number of logical files, each logical file
 * holding its own set of errors, warnings, and informative messages for post-processing
 */
public class PhysicalFlatFileInformation {
    private String fileName;
    private List<String[]> messages;
    private List<FlatFileInformation> flatFileInfomationList;

    /**
     * Constructs a PhysicalFlatFileInformation
     * @param fileName the name of the file this encapsulates
     */
    public PhysicalFlatFileInformation(String fileName) {
        this.fileName = fileName;
        messages = new ArrayList<String[]>();
        flatFileInfomationList = new ArrayList<FlatFileInformation>();
    }

    public void addFileErrorMessages(List<String> messages) {
        for(String message : messages) {
            this.messages.add(new String[] { FlatFileTransactionInformation.getEntryTypeString(FlatFileTransactionInformation.EntryType.ERROR), message });
        }
    }

    /**
     * Adds an error message applicable to the entire file
     * @param message
     */
    public void addFileErrorMessage(String message) {
        this.messages.add(new String[] { FlatFileTransactionInformation.getEntryTypeString(FlatFileTransactionInformation.EntryType.ERROR), message });
    }

    public void addFileInfoMessage(List<String> messages) {
        for(String message : messages) {
            this.messages.add(new String[] { FlatFileTransactionInformation.getEntryTypeString(FlatFileTransactionInformation.EntryType.INFO), message });
        }
    }

    /**
     * Adds an informative message applicable to the entire file
     * @param message
     */
    public void addFileInfoMessage(String message) {
        this.messages.add(new String[] { FlatFileTransactionInformation.getEntryTypeString(FlatFileTransactionInformation.EntryType.INFO), message });
    }

    /**
     * @return the file name of the physical file encapsulated in this PhysicalFlatFileInformation object
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name of the physical file encapsulated in this PhysicalFlatFileInformation object
     * @param fileName the file name of the physical file encapsulated in this PhysicalFlatFileInformation object
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return a List of all messages associated with the physical file as a whole
     */
    public List<String[]> getMessages() {
        return messages;
    }

    public String getAllMessages() {
        StringBuffer message = new StringBuffer();
        for(String[] resultMessage : getMessages()) {
            message.append(resultMessage[1]);
            message.append("\n");
        }
        return message.toString();
    }

    /**
     * Sets a List of messages associated with the physical file as a whole
     * @param messages a List of messages
     */
    public void setMessages(List<String[]> messages) {
        this.messages = messages;
    }


    /**
     * @return a List of the FlatFileInformation objects, each representing a logical file within this physical file
     */
    public List<FlatFileInformation> getFlatFileInfomationList() {
        return flatFileInfomationList;
    }


    /**
     * Sets the List of FlatFileInformation objects, each representing a logical file within the encapsulated physical file
     * @param flatFileInfomationList
     */
    public void setFlatFileInfomationList(List<FlatFileInformation> flatFileInfomationList) {
        this.flatFileInfomationList = flatFileInfomationList;
    }
}
