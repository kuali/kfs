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

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;

/**
 * This class represents the status for origin entries through the different origin entry groups (i.e. input, valid, error, and expired) 
 */
public class CollectorScrubberStatus {
    private OriginEntryGroup inputGroup;
    private OriginEntryGroup validGroup;
    private OriginEntryGroup errorGroup;
    private OriginEntryGroup expiredGroup;

    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryService originEntryService;

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
     * Gets the originEntryGroupService attribute.
     * 
     * @return Returns the originEntryGroupService.
     */
    public OriginEntryGroupService getOriginEntryGroupService() {
        return originEntryGroupService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * Gets the originEntryService attribute.
     * 
     * @return Returns the originEntryService.
     */
    public OriginEntryService getOriginEntryService() {
        return originEntryService;
    }

    /**
     * Sets the originEntryService attribute value.
     * 
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
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
}
