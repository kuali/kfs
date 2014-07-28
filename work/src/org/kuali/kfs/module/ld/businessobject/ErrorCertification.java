/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Error Certification business object for Salary Expense Transfer and Year End Salary Expense Transfer
 */
public class ErrorCertification extends PersistableBusinessObjectBase {
    private String documentNumber;
    private String expenditureDescription;
    private String expenditureProjectBenefit;
    private String errorDescription;
    private String errorCorrectionReason;

    /**
     * Gets the document number of the document this error certification is associated with.
     *
     * @return Returns the documentNumber.
     */

    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the document number of the document this error certification is associated with.
     *
     * @param documentNumber The document number of the document this error certification is associated with.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the expenditureDescription attribute.
     *
     * @return Returns the expenditureDescription.
     */
    public String getExpenditureDescription() {
        return expenditureDescription;
    }

    /**
     * Sets the expenditureDescription attribute value.
     *
     * @param expenditureDescription The expenditureDescription to set.
     */
    public void setExpenditureDescription(String expenditureDescription) {
        this.expenditureDescription = expenditureDescription;
    }

    /**
     * Gets the expenditureProjectBenefit attribute.
     *
     * @return Returns the expenditureProjectBenefit.
     */
    public String getExpenditureProjectBenefit() {
        return expenditureProjectBenefit;
    }

    /**
     * Sets the expenditureProjectBenefit attribute value.
     *
     * @param expenditureProjectBenefit The expenditureProjectBenefit to set.
     */
    public void setExpenditureProjectBenefit(String expenditureProjectBenefit) {
        this.expenditureProjectBenefit = expenditureProjectBenefit;
    }

    /**
     * Gets the errorDescription attribute.
     *
     * @return Returns the errorDescription.
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Sets the errorDescription attribute value.
     *
     * @param errorDescription The errorDescription to set.
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    /**
     * Gets the errorCorrectionReason attribute.
     *
     * @return Returns the errorCorrectionReason.
     */
    public String getErrorCorrectionReason() {
        return errorCorrectionReason;
    }

    /**
     * Sets the errorCorrectionReason attribute value.
     *
     * @param errorCorrectionReason The errorCorrectionReason to set.
     */
    public void setErrorCorrectionReason(String errorCorrectionReason) {
        this.errorCorrectionReason = errorCorrectionReason;
    }
}
