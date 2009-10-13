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
package org.kuali.kfs.module.cab.batch;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;

public class ExtractProcessLog {
    private Timestamp startTime;
    private Timestamp finishTime;
    private Timestamp lastExtractTime;
    private List<Entry> ignoredGLEntries;
    private List<Entry> duplicateGLEntries;
    private List<Entry> mismatchedGLEntries;
    private String errorMessage;
    private boolean success = true;
    private Integer totalGlCount = 0;
    private Integer nonPurApGlCount = 0;
    private Integer purApGlCount = 0;
    private String statusMessage;

    /**
     * Gets the ignoredGLEntries attribute.
     * 
     * @return Returns the ignoredGLEntries.
     */
    public List<Entry> getIgnoredGLEntries() {
        return ignoredGLEntries;
    }

    /**
     * Sets the ignoredGLEntries attribute value.
     * 
     * @param ignoredGLEntries The ignoredGLEntries to set.
     */
    public void setIgnoredGLEntries(List<Entry> ignoredGLEntries) {
        this.ignoredGLEntries = ignoredGLEntries;
    }

    /**
     * Gets the duplicateGLEntries attribute.
     * 
     * @return Returns the duplicateGLEntries.
     */
    public List<Entry> getDuplicateGLEntries() {
        return duplicateGLEntries;
    }

    /**
     * Sets the duplicateGLEntries attribute value.
     * 
     * @param duplicateGLEntries The duplicateGLEntries to set.
     */
    public void setDuplicateGLEntries(List<Entry> duplicateGLEntries) {
        this.duplicateGLEntries = duplicateGLEntries;
    }

    /**
     * Gets the mismatchedGLEntries attribute.
     * 
     * @return Returns the mismatchedGLEntries.
     */
    public List<Entry> getMismatchedGLEntries() {
        return mismatchedGLEntries;
    }

    /**
     * Sets the mismatchedGLEntries attribute value.
     * 
     * @param mismatchedGLEntries The mismatchedGLEntries to set.
     */
    public void setMismatchedGLEntries(List<Entry> mismatchedGLEntries) {
        this.mismatchedGLEntries = mismatchedGLEntries;
    }

    /**
     * Adds a collection of entries to ignoredGLEntries
     * 
     * @param add ignoredGLEntries
     */
    public void addIgnoredGLEntries(Collection<Entry> add) {
        if (this.ignoredGLEntries == null) {
            this.ignoredGLEntries = new ArrayList<Entry>();
        }
        this.ignoredGLEntries.addAll(add);
    }

    /**
     * Adds a collection of entries to duplicateGLEntries
     * 
     * @param add duplicateGLEntries
     */
    public void addDuplicateGLEntries(Collection<Entry> add) {
        if (this.duplicateGLEntries == null) {
            this.duplicateGLEntries = new ArrayList<Entry>();
        }
        this.duplicateGLEntries.addAll(add);
    }

    /**
     * Adds a collection of entries to mismatchedGLEntries
     * 
     * @param add mismatchedGLEntries
     */
    public void addMismatchedGLEntries(Collection<Entry> add) {
        if (this.mismatchedGLEntries == null) {
            this.mismatchedGLEntries = new ArrayList<Entry>();
        }
        this.mismatchedGLEntries.addAll(add);
    }

    /**
     * Add a GL entry to ignoredGLEntries
     * 
     * @param add Entry
     */
    public void addIgnoredGLEntry(Entry add) {
        if (this.ignoredGLEntries == null) {
            this.ignoredGLEntries = new ArrayList<Entry>();
        }
        this.ignoredGLEntries.add(add);
    }

    /**
     * Add a GL entry to duplicateGLEntries
     * 
     * @param add Entry
     */
    public void addDuplicateGLEntry(Entry add) {
        if (this.duplicateGLEntries == null) {
            this.duplicateGLEntries = new ArrayList<Entry>();
        }
        this.duplicateGLEntries.add(add);
    }

    /**
     * Add a GL entry to mismatchedGLEntries
     * 
     * @param add Entry
     */
    public void addMismatchedGLEntry(Entry add) {
        if (this.mismatchedGLEntries == null) {
            this.mismatchedGLEntries = new ArrayList<Entry>();
        }
        this.mismatchedGLEntries.add(add);
    }

    /**
     * Gets the startTime attribute.
     * 
     * @return Returns the startTime.
     */
    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * Sets the startTime attribute value.
     * 
     * @param startTime The startTime to set.
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the lastExtractTime attribute.
     * 
     * @return Returns the lastExtractTime.
     */
    public Timestamp getLastExtractTime() {
        return lastExtractTime;
    }

    /**
     * Sets the lastExtractTime attribute value.
     * 
     * @param lastExtractTime The lastExtractTime to set.
     */
    public void setLastExtractTime(Timestamp lastExtractTime) {
        this.lastExtractTime = lastExtractTime;
    }

    /**
     * Gets the success attribute.
     * 
     * @return Returns the success.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success attribute value.
     * 
     * @param success The success to set.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the finishTime attribute.
     * 
     * @return Returns the finishTime.
     */
    public Timestamp getFinishTime() {
        return finishTime;
    }

    /**
     * Sets the finishTime attribute value.
     * 
     * @param finishTime The finishTime to set.
     */
    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * Gets the errorMessage attribute.
     * 
     * @return Returns the errorMessage.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the errorMessage attribute value.
     * 
     * @param errorMessage The errorMessage to set.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the totalGlCount attribute.
     * 
     * @return Returns the totalGlCount.
     */
    public Integer getTotalGlCount() {
        return totalGlCount;
    }

    /**
     * Sets the totalGlCount attribute value.
     * 
     * @param totalGlCount The totalGlCount to set.
     */
    public void setTotalGlCount(Integer totalGlCount) {
        this.totalGlCount = totalGlCount;
    }

    /**
     * Gets the nonPurApGlCount attribute.
     * 
     * @return Returns the nonPurApGlCount.
     */
    public Integer getNonPurApGlCount() {
        return nonPurApGlCount;
    }

    /**
     * Sets the nonPurApGlCount attribute value.
     * 
     * @param nonPurApGlCount The nonPurApGlCount to set.
     */
    public void setNonPurApGlCount(Integer nonPurApGlCount) {
        this.nonPurApGlCount = nonPurApGlCount;
    }

    /**
     * Gets the purApGlCount attribute.
     * 
     * @return Returns the purApGlCount.
     */
    public Integer getPurApGlCount() {
        return purApGlCount;
    }

    /**
     * Sets the purApGlCount attribute value.
     * 
     * @param purApGlCount The purApGlCount to set.
     */
    public void setPurApGlCount(Integer purApGlCount) {
        this.purApGlCount = purApGlCount;
    }

    /**
     * Gets the statusMessage attribute.
     * 
     * @return Returns the statusMessage.
     */
    public String getStatusMessage() {
        if (this.statusMessage == null) {
            return success ? "Success" : this.errorMessage == null ? "" : this.errorMessage;
        }
        return statusMessage;
    }

    /**
     * Sets the statusMessage attribute value.
     * 
     * @param statusMessage The statusMessage to set.
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }


}
