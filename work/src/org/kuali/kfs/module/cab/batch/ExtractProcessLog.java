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
package org.kuali.kfs.module.cab.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;

public class ExtractProcessLog {
    private List<Entry> ignoredGLEntries = new ArrayList<Entry>();
    private List<Entry> duplicateGLEntries = new ArrayList<Entry>();
    private List<Entry> mismatchedGLEntries = new ArrayList<Entry>();

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
        this.ignoredGLEntries.addAll(add);
    }

    /**
     * Adds a collection of entries to duplicateGLEntries
     * 
     * @param add duplicateGLEntries
     */
    public void addDuplicateGLEntries(Collection<Entry> add) {
        this.duplicateGLEntries.addAll(add);
    }

    /**
     * Adds a collection of entries to mismatchedGLEntries
     * 
     * @param add mismatchedGLEntries
     */
    public void addMismatchedGLEntries(Collection<Entry> add) {
        this.mismatchedGLEntries.addAll(add);
    }

    /**
     * Add a GL entry to ignoredGLEntries
     * 
     * @param add Entry
     */
    public void addIgnoredGLEntry(Entry add) {
        this.ignoredGLEntries.add(add);
    }

    /**
     * Add a GL entry to duplicateGLEntries
     * 
     * @param add Entry
     */
    public void addDuplicateGLEntry(Entry add) {
        this.duplicateGLEntries.add(add);
    }

    /**
     * Add a GL entry to mismatchedGLEntries
     * 
     * @param add Entry
     */
    public void addMismatchedGLEntry(Entry add) {
        this.mismatchedGLEntries.add(add);
    }

    public void sendNotification() {
        // TODO - Here log content should be prepared and notified to user group configured
    }
}
