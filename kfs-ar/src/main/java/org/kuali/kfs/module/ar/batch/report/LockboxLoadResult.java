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

/**
 * 
 * Represents one lockbox object from a batch file, and its results.
 * 
 */

public class LockboxLoadResult {
	public enum ResultCode { SUCCESS, FAILURE, ERROR, INCOMPLETE }
    public enum EntryType { INFO, ERROR } 

    private String filename;
    private String lockboxNumber;
    private ResultCode result;
    
    private List<String[]> messages;
    
    public LockboxLoadResult() {
        this.messages = new ArrayList<String[]>();
    }
    
    public LockboxLoadResult(String filename, String lockboxNumber) {
        this.filename = filename;
        this.lockboxNumber = lockboxNumber;
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
        
    public String getLockboxNumber() {
		return lockboxNumber;
	}

	public void setLockboxNumber(String lockboxNumber) {
		this.lockboxNumber = lockboxNumber;
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
