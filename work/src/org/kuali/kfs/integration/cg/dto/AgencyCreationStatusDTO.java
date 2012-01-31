/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.integration.cg.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Integration class for AgencyCreationStatusDTO
 */
public class AgencyCreationStatusDTO implements Serializable {

    private static final long serialVersionUID = -3058053637490790154L;

    protected List<String> errorMessages;
    protected String documentNumber;
    protected String status;

    public AgencyCreationStatusDTO() {
    }

    /**
     * Gets the errorCodes attribute.
     * 
     * @return Returns the errorCodes.
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Sets the errorCodes attribute value.
     * 
     * @param errorCodes The errorCodes to set.
     */
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the status attribute.
     * 
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status attribute value.
     * 
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }


}
