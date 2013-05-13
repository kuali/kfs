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
 * An encapsulator for validation information associated with a logical file/top level header
 */
public final class FlatFileTransactionInformation {
    public enum ResultCode { SUCCESS, FAILURE, ERROR, INCOMPLETE }
    public enum EntryType { INFO,WARN, ERROR  }

    private String flatFileDataIdentifier;
    private ResultCode result;
    private List<String[]> messages;

    /**
     * Constructs a FlatFileTransactionInformation
     */
    public FlatFileTransactionInformation() {
        this.messages = new ArrayList<String[]>();
    }

    /**
     * Constructs a FlatFileTransactionInformation
     * @param flatFileDataIdentifier the identifier for the file the business object this object holds messages for was parsed from
     */
    public FlatFileTransactionInformation(String flatFileDataIdentifier) {
        this.flatFileDataIdentifier = flatFileDataIdentifier;
        this.result = ResultCode.INCOMPLETE;
        this.messages = new ArrayList<String[]>();
    }

    /**
     * Creates a String representation for the given entry type
     * @param type the entry type to get a String representation for
     * @return the String representation
     */
    public static String getEntryTypeString(EntryType type) {
        if (type == null) {
            return "UNKNOWN";
        }
        return type.name();
    }

    /**
     * Creates a String representation for the given result code
     * @param type the resultCode to get a String representation for
     * @return the String representation
     */
    public static String getResultCodeString(ResultCode resultCode) {
        if (resultCode == null) {
            return "UNKNOWN";
        }
        return resultCode.name();
    }

    /**
     * @return returns the resultCode for this transaction information object
     */
    public ResultCode getResult() {
        return result;
    }

    /**
     * @return returns a String representation of the resultCode for this transaction information object
     */
    public String getResultString() {
        return getResultCodeString(result);
    }

    /**
     * Sets the resultCode for this transaction information object
     * @param result the result code to set
     */
    private void setResult(ResultCode result) {
        this.result = result;
    }

    /**
     * Declares the logical file a success!
     */
    public void setSuccessResult() {
        this.result = ResultCode.SUCCESS;
    }

    /**
     * Sets the result code to failure for the transaction
     */
    public void setFailureResult() {
        this.result = ResultCode.FAILURE;
    }

    /**
     * Sets the result code to error for the transaction
     */
    public void setErrorResult() {
        this.result = ResultCode.ERROR;
    }

    /**
     * @return the identifier for the flat file this is associated with
     */
    public String getFlatFileDataIdentifier() {
        return flatFileDataIdentifier;
    }

    /**
     * Sets the identifier for the flat file this is associated with
     * @param flatFileDataIdentifier the identifier for the flat file this is associated with
     */
    public void setFlatFileDataIdentifier(String flatFileDataIdentifier) {
        this.flatFileDataIdentifier = flatFileDataIdentifier;
    }

    /**
     * @return the full List of messages associated with the transaction this is associated with
     */
    public List<String[]> getMessages() {
        return messages;
    }

    /**
     * Adds a message to the List of messages for this transaction
     * @param entryType the kind of message being held
     * @param message the message to hold
     */
    private void addMessage(EntryType entryType, String message) {
        this.messages.add(new String[] { getEntryTypeString(entryType), message });
    }

    /**
     * Adds an error message for this transaction
     * @param message the message to explain the error
     */
    public void addErrorMessage(String message) {
        addMessage(EntryType.ERROR, message);
    }

    /**
     * Adds an informative message for this transaction
     * @param message the informative message, hopefully something more explanatory than this javadoc
     */
    public void addInfoMessage(String message) {
        addMessage(EntryType.INFO, message);
    }

    /**
     * Adds a warning message for this transaction
     * @param message the warning message
     */
    public void addWarnMessage(String message) {
        addMessage(EntryType.WARN, message);
    }
}
