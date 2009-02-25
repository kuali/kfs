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
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;

/**
 * This class represents the status for origin entries through the different origin entry groups (i.e. input, valid, error, and expired) 
 */
public class CollectorScrubberStatus {
    private String inputFileName;
    private String validFileName;
    private String errorFileName;
    private String expiredFileName;

    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryService originEntryService;

    /**
     * Gets the errorFileName attribute.
     * 
     * @return Returns the errorFileName.
     */
    public String getErrorFileName() {
        return errorFileName;
    }

    /**
     * Sets the errorFileName attribute value.
     * 
     * @param errorFileName The errorFileName to set.
     */
    public void setErrorFileName(String errorFileName) {
        this.errorFileName = errorFileName;
    }

    /**
     * Gets the expiredFileName attribute.
     * 
     * @return Returns the expiredFileName.
     */
    public String getExpiredFileName() {
        return expiredFileName;
    }

    /**
     * Sets the expiredFileName attribute value.
     * 
     * @param expiredFileName The expiredFileName to set.
     */
    public void setExpiredFileName(String expiredFileName) {
        this.expiredFileName = expiredFileName;
    }

    /**
     * Gets the inputFileName attribute.
     * 
     * @return Returns the inputFileName.
     */
    public String getInputFileName() {
        return inputFileName;
    }

    /**
     * Sets the inputFileName attribute value.
     * 
     * @param inputFileName The inputFileName to set.
     */
    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    /**
     * Gets the StringService attribute.
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
     * Gets the validFileName attribute.
     * 
     * @return Returns the validFileName.
     */
    public String getValidFileName() {
        return validFileName;
    }

    /**
     * Sets the validFileName attribute value.
     * 
     * @param validFileName The validFileName to set.
     */
    public void setValidFileName(String validFileName) {
        this.validFileName = validFileName;
    }
}
