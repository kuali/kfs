/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.util;

import java.util.Map;

import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;

/**
 * This class represents the status for origin entries through the different origin entry groups (i.e. input, valid, error, and expired)
 */
public class ScrubberStatus {
    private OriginEntryGroup inputGroup;
    private OriginEntryGroup validGroup;
    private OriginEntryGroup errorGroup;
    private OriginEntryGroup expiredGroup;
    private Map<OriginEntry, OriginEntry> unscrubbedToScrubbedEntries;

    /**
     * Gets the errorGroup attribute.
     * 
     * @return Returns the errorGroup.
     */
    public OriginEntryGroup getErrorGroup() {
        return errorGroup;
    }

    /**
     * Sets the errorGroup attribute value.
     * 
     * @param errorGroup The errorGroup to set.
     */
    public void setErrorGroup(OriginEntryGroup errorGroup) {
        this.errorGroup = errorGroup;
    }

    /**
     * Gets the expiredGroup attribute.
     * 
     * @return Returns the expiredGroup.
     */
    public OriginEntryGroup getExpiredGroup() {
        return expiredGroup;
    }

    /**
     * Sets the expiredGroup attribute value.
     * 
     * @param expiredGroup The expiredGroup to set.
     */
    public void setExpiredGroup(OriginEntryGroup expiredGroup) {
        this.expiredGroup = expiredGroup;
    }

    /**
     * Gets the inputGroup attribute.
     * 
     * @return Returns the inputGroup.
     */
    public OriginEntryGroup getInputGroup() {
        return inputGroup;
    }

    /**
     * Sets the inputGroup attribute value.
     * 
     * @param inputGroup The inputGroup to set.
     */
    public void setInputGroup(OriginEntryGroup inputGroup) {
        this.inputGroup = inputGroup;
    }

    /**
     * Gets the validGroup attribute.
     * 
     * @return Returns the validGroup.
     */
    public OriginEntryGroup getValidGroup() {
        return validGroup;
    }

    /**
     * Sets the validGroup attribute value.
     * 
     * @param validGroup The validGroup to set.
     */
    public void setValidGroup(OriginEntryGroup validGroup) {
        this.validGroup = validGroup;
    }

    /**
     * Gets the unscrubbedToScrubbedEntry attribute.
     * 
     * @return Returns the unscrubbedToScrubbedEntry.
     */
    public Map<OriginEntry, OriginEntry> getUnscrubbedToScrubbedEntries() {
        return unscrubbedToScrubbedEntries;
    }

    /**
     * Sets the unscrubbedToScrubbedEntry attribute value.
     * 
     * @param unscrubbedToScrubbedEntry The unscrubbedToScrubbedEntry to set.
     */
    public void setUnscrubbedToScrubbedEntries(Map<OriginEntry, OriginEntry> unscrubbedToScrubbedEntry) {
        this.unscrubbedToScrubbedEntries = unscrubbedToScrubbedEntry;
    }
}
