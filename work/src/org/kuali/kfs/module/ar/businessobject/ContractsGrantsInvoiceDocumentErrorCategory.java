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
 * Defines a Contracts & Grants Billing Invoice Document Error Category.
 */
public class ContractsGrantsInvoiceDocumentErrorCategory extends PersistableBusinessObjectBase {

    private Long proposalNumber;
    private Long errorLogIdentifier;
    private Long validationCategoryIdentifier;
    private String validationCategoryText;

    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public Long getErrorLogIdentifier() {
        return errorLogIdentifier;
    }

    public void setErrorLogIdentifier(Long errorLogIdentifier) {
        this.errorLogIdentifier = errorLogIdentifier;
    }

    public Long getValidationCategoryIdentifier() {
        return validationCategoryIdentifier;
    }

    public void setValidationCategoryIdentifier(Long validationCategoryIdentifier) {
        this.validationCategoryIdentifier = validationCategoryIdentifier;
    }

    public String getValidationCategoryText() {
        return validationCategoryText;
    }

    public void setValidationCategoryText(String validationCategoryText) {
        this.validationCategoryText = validationCategoryText;
    }

}