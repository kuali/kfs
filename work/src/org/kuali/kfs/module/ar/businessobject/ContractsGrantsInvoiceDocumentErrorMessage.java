/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines a Contracts & Grants Billing Invoice Document Error Message.
 */
public class ContractsGrantsInvoiceDocumentErrorMessage extends PersistableBusinessObjectBase {

    private Long errorLogIdentifier;
    private Long errorMessageIdentifier;
    private String errorMessageText;

    public Long getErrorLogIdentifier() {
        return errorLogIdentifier;
    }

    public void setErrorLogIdentifier(Long errorLogIdentifier) {
        this.errorLogIdentifier = errorLogIdentifier;
    }

    public Long getErrorMessageIdentifier() {
        return errorMessageIdentifier;
    }

    public void setErrorMessageIdentifier(Long errorMessageIdentifier) {
        this.errorMessageIdentifier = errorMessageIdentifier;
    }

    public String getErrorMessageText() {
        return errorMessageText;
    }

    public void setErrorMessageText(String errorMessageText) {
        this.errorMessageText = errorMessageText;
    }

    /**
     * This can be displayed by ContractsGrantsInvoiceDocumentErrorLog lookup results.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return errorMessageText;
    }

}