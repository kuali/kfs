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
    
    protected List<String> errorCodes;
    protected String documentNumber;
    protected String accountNumber;
    protected String chartOfAccountsCode;
    protected boolean success;

    public AccountCreationStatus() {}

    /**
     * Gets the errorCodes attribute. 
     * @return Returns the errorCodes.
     */
    public List<String> getErrorCodes() {
        return errorCodes;
    }

    /**
     * Sets the errorCodes attribute value.
     * @param errorCodes The errorCodes to set.
     */
    public void setErrorCodes(List<String> errorCodes) {
        this.errorCodes = errorCodes;
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

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the success attribute. 
     * @return Returns the success.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success attribute value.
     * @param success The success to set.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

}
