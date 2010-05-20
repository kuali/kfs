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
package org.kuali.kfs.module.external.kc.dto;

import java.io.Serializable;
import java.util.List;

public class AccountCreationStatus implements Serializable {
    
    private List<String> resultCodes;
    private String documentNumber;
    private String accountNumber;

    public AccountCreationStatus() {}

    /**
     * Gets the resultCodes attribute. 
     * @return Returns the resultCodes.
     */
    public List<String> getResultCodes() {
        return resultCodes;
    }

    /**
     * Sets the resultCodes attribute value.
     * @param resultCodes The resultCodes to set.
     */
    public void setResultCodes(List<String> resultCodes) {
        this.resultCodes = resultCodes;
    }

    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the accountNumber attribute. 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
}
