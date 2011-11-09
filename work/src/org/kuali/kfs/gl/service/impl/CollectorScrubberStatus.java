/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.service.impl;


/**
 * This class represents the status for origin entries through the different origin entry groups (i.e. input, valid, error, and expired) 
 */
public class CollectorScrubberStatus {
    private String inputFileName;
    private String validFileName;
    private String errorFileName;
    private String expiredFileName;

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
